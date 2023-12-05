/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.pmml.commons.model.expressions;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.kie.pmml.api.enums.COUNT_HITS;
import org.kie.pmml.api.enums.LOCAL_TERM_WEIGHTS;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLExtension;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.abstracts.AbstractKiePMMLComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.util.stream.Collectors.counting;
import static org.kie.pmml.commons.model.expressions.ExpressionsUtils.getFromPossibleSources;

public class KiePMMLTextIndex extends AbstractKiePMMLComponent implements KiePMMLExpression {

    private static final long serialVersionUID = -1946996874918753317L;
    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTextIndex.class);
    public static final String DEFAULT_TOKENIZER = "\\s+";
    private final KiePMMLExpression expression;
    private LOCAL_TERM_WEIGHTS localTermWeights = LOCAL_TERM_WEIGHTS.TERM_FREQUENCY;
    private boolean isCaseSensitive = false;
    private int maxLevenshteinDistance = 0;
    private COUNT_HITS countHits = COUNT_HITS.ALL_HITS;
    private String wordSeparatorCharacterRE = DEFAULT_TOKENIZER;
    private boolean tokenize = true;
    private LevenshteinDistance levenshteinDistance;
    private List<KiePMMLTextIndexNormalization> textIndexNormalizations;

    private KiePMMLTextIndex(String name, List<KiePMMLExtension> extensions, KiePMMLExpression expression) {
        super(name, extensions);
        this.expression = expression;
        this.levenshteinDistance = new LevenshteinDistance(maxLevenshteinDistance);
    }

    public static Builder builder(String name, List<KiePMMLExtension> extensions, KiePMMLExpression expression) {
        return new Builder(name, extensions, expression);
    }

    static double evaluateRaw(boolean isCaseSensitive,
                              boolean tokenize,
                              String term,
                              String text,
                              String wordSeparatorCharacterRE,
                              LOCAL_TERM_WEIGHTS localTermWeights,
                              COUNT_HITS countHits,
                              LevenshteinDistance levenshteinDistance) {
        if (!isCaseSensitive) {
            term = term.toLowerCase();
            text = text.toLowerCase();
        }
        Pattern pattern = tokenize ? Pattern.compile(wordSeparatorCharacterRE) : Pattern.compile(DEFAULT_TOKENIZER);
        List<String> terms = splitText(term, pattern);
        List<String> texts = splitText(text, pattern);
        int calculatedLevenshteinDistance;
        switch (countHits) {
            case ALL_HITS:
                calculatedLevenshteinDistance = evaluateLevenshteinDistanceAllHits(levenshteinDistance, terms, texts);
                break;
            case BEST_HITS:
                calculatedLevenshteinDistance = evaluateLevenshteinDistanceBestHits(levenshteinDistance, terms, texts);
                break;
            default:
                throw new IllegalArgumentException("Unknown COUNT_HITS " + countHits);
        }
        switch (localTermWeights) {
            case TERM_FREQUENCY:
                return calculatedLevenshteinDistance;
            case BINARY:
                return evaluateBinary(calculatedLevenshteinDistance);
            case LOGARITHMIC:
                return evaluateLogarithmic(calculatedLevenshteinDistance);
            case AUGMENTED_NORMALIZED_TERM_FREQUENCY:
                return evaluateAugmentedNormalizedTermFrequency(calculatedLevenshteinDistance, texts);
            default:
                throw new IllegalArgumentException("Unknown LOCAL_TERM_WEIGHTS " + localTermWeights);
        }
    }

    static int evaluateBinary(int calculatedLevenshteinDistance) {
        return calculatedLevenshteinDistance >= 0 ? 1 : 0;
    }

    static double evaluateLogarithmic(int calculatedLevenshteinDistance) {
        return Math.log10(1 + (double) calculatedLevenshteinDistance); // cast for java:S2184
    }

    static double evaluateAugmentedNormalizedTermFrequency(int calculatedLevenshteinDistance, List<String> texts) {
        Map<String, Long> wordFrequencies =
                texts.stream().collect(Collectors.groupingBy(Function.identity(), counting()));
        int maxFrequency = wordFrequencies.values().stream()
                .max(Comparator.comparingLong(f -> f))
                .map(Long::intValue)
                .orElseThrow(() -> new KiePMMLException("Failed to find most frequent word!"));
        int binaryEvaluation = evaluateBinary(calculatedLevenshteinDistance);
        return 0.5 * (binaryEvaluation + (calculatedLevenshteinDistance / (double) maxFrequency)); // cast for
        // java:S2184
    }

    static int evaluateLevenshteinDistanceAllHits(LevenshteinDistance levenshteinDistance, List<String> terms,
                                                  List<String> texts) {
        logger.debug("evaluateLevenshteinDistanceAllHits {} {}", terms, texts);
        int batchSize = terms.size();
        int limit = texts.size() - batchSize + 1;
        String toSearch = String.join(" ", terms);
        int toReturn = 0;
        for (int i = 0; i < limit; i++) {
            String subText = String.join(" ", texts.subList(i, i + batchSize));
            int distance = evaluateLevenshteinDistance(levenshteinDistance, toSearch, subText);
            if (distance > -1) {
                toReturn++;
            }
        }
        return toReturn;
    }

    static int evaluateLevenshteinDistanceBestHits(LevenshteinDistance levenshteinDistance, List<String> terms,
                                                   List<String> texts) {
        logger.debug("evaluateLevenshteinDistanceBestHits {} {}", terms, texts);
        int batchSize = terms.size();
        int limit = texts.size() - batchSize + 1;
        String toSearch = String.join(" ", terms);
        SortedMap<Integer, AtomicInteger> distancesMap = new TreeMap<>();
        for (int i = 0; i < limit; i++) {
            String subText = String.join(" ", texts.subList(i, i + batchSize));
            int distance = evaluateLevenshteinDistance(levenshteinDistance, toSearch, subText);
            if (distance > -1) {
                if (distancesMap.containsKey(distance)) {
                    distancesMap.get(distance).addAndGet(1);
                } else {
                    distancesMap.put(distance, new AtomicInteger(1));
                }
            }
        }
        return distancesMap.get(distancesMap.firstKey()).get();
    }

    static int evaluateLevenshteinDistance(LevenshteinDistance levenshteinDistance, String term, String text) {
        logger.debug("evaluateLevenshteinDistance {} {}", term, text);
        return levenshteinDistance.apply(term, text);
    }

    static List<String> splitText(String toSplit, Pattern pattern) {
        return pattern.splitAsStream(toSplit)
                .map(trm -> trm.replaceAll("[^a-zA-Z0-9 ]", ""))
                .filter(trm -> !trm.isEmpty())
                .collect(Collectors.toList());
    }

    public KiePMMLExpression getExpression() {
        return expression;
    }

    public LOCAL_TERM_WEIGHTS getLocalTermWeights() {
        return localTermWeights;
    }

    public boolean isCaseSensitive() {
        return isCaseSensitive;
    }

    public int getMaxLevenshteinDistance() {
        return maxLevenshteinDistance;
    }

    public COUNT_HITS getCountHits() {
        return countHits;
    }

    public String getWordSeparatorCharacterRE() {
        return wordSeparatorCharacterRE;
    }

    public boolean isTokenize() {
        return tokenize;
    }

    public LevenshteinDistance getLevenshteinDistance() {
        return levenshteinDistance;
    }

    public List<KiePMMLTextIndexNormalization> getTextIndexNormalizations() {
        return Collections.unmodifiableList(textIndexNormalizations);
    }

    @Override
    public Object evaluate(final ProcessingDTO processingDTO) {
        String term = (String) expression.evaluate(processingDTO);
        String text = (String) getFromPossibleSources(name, processingDTO).orElseThrow(() -> new KiePMMLException("No text to scan in " + this));
        if (textIndexNormalizations != null) {
            for (KiePMMLTextIndexNormalization textIndexNormalization : textIndexNormalizations) {
                text = textIndexNormalization.replace(text, isCaseSensitive, maxLevenshteinDistance, false,
                                                      DEFAULT_TOKENIZER);
            }
        }
        return evaluateRaw(isCaseSensitive,
                           tokenize,
                           term,
                           text,
                           wordSeparatorCharacterRE,
                           localTermWeights,
                           countHits,
                           levenshteinDistance);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", KiePMMLTextIndex.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("localTermWeights=" + localTermWeights)
                .add("isCaseSensitive=" + isCaseSensitive)
                .add("maxLevenshteinDistance=" + maxLevenshteinDistance)
                .add("countHits=" + countHits)
                .add("wordSeparatorCharacterRE='" + wordSeparatorCharacterRE + "'")
                .add("tokenize=" + tokenize)
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        KiePMMLTextIndex that = (KiePMMLTextIndex) o;
        return isCaseSensitive == that.isCaseSensitive && maxLevenshteinDistance == that.maxLevenshteinDistance && tokenize == that.tokenize && localTermWeights == that.localTermWeights && countHits == that.countHits && wordSeparatorCharacterRE.equals(that.wordSeparatorCharacterRE);
    }

    @Override
    public int hashCode() {
        return Objects.hash(localTermWeights, isCaseSensitive, maxLevenshteinDistance, countHits,
                            wordSeparatorCharacterRE, tokenize);
    }

    public static class Builder extends AbstractKiePMMLComponent.Builder<KiePMMLTextIndex> {

        private Builder(String name, List<KiePMMLExtension> extensions, KiePMMLExpression expression) {
            super("TextIndex-", () -> new KiePMMLTextIndex(name, extensions, expression));
        }

        public Builder withLocalTermWeights(LOCAL_TERM_WEIGHTS localTermWeights) {
            if (localTermWeights != null) {
                toBuild.localTermWeights = localTermWeights;
            }
            return this;
        }

        public Builder withIsCaseSensitive(boolean isCaseSensitive) {
            toBuild.isCaseSensitive = isCaseSensitive;
            return this;
        }

        public Builder withMaxLevenshteinDistance(int maxLevenshteinDistance) {
            toBuild.maxLevenshteinDistance = maxLevenshteinDistance;
            toBuild.levenshteinDistance = new LevenshteinDistance(maxLevenshteinDistance);
            return this;
        }

        public Builder withCountHits(COUNT_HITS countHits) {
            if (countHits != null) {
                toBuild.countHits = countHits;
            }
            return this;
        }

        public Builder withWordSeparatorCharacterRE(String wordSeparatorCharacterRE) {
            if (wordSeparatorCharacterRE != null) {
                toBuild.wordSeparatorCharacterRE = wordSeparatorCharacterRE;
            }
            return this;
        }

        public Builder withTokenize(boolean tokenize) {
            toBuild.tokenize = tokenize;
            return this;
        }

        public Builder withTextIndexNormalizations(List<KiePMMLTextIndexNormalization> textIndexNormalizations) {
            if (textIndexNormalizations != null) {
                toBuild.textIndexNormalizations = textIndexNormalizations;
            }
            return this;
        }
    }
}
