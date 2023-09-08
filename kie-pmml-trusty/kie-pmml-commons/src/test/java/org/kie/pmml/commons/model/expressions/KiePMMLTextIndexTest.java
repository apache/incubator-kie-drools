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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.COUNT_HITS;
import org.kie.pmml.api.enums.LOCAL_TERM_WEIGHTS;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.api.enums.LOCAL_TERM_WEIGHTS.AUGMENTED_NORMALIZED_TERM_FREQUENCY;
import static org.kie.pmml.api.enums.LOCAL_TERM_WEIGHTS.BINARY;
import static org.kie.pmml.api.enums.LOCAL_TERM_WEIGHTS.LOGARITHMIC;
import static org.kie.pmml.api.enums.LOCAL_TERM_WEIGHTS.TERM_FREQUENCY;
import static org.kie.pmml.commons.CommonTestingUtility.getProcessingDTO;

public class KiePMMLTextIndexTest {

    private static final String TERM_0 = "brown fox";
    private static final String TEXT_0 = "The quick browny fox 234 -. jumps over the lazy dog with another Brown Fox." +
            " " +
            "The brown fox runs away and to be with " +
            "another  ; : brown-foxy.";
    private static final String NOT_NORMALIZED_TEXT_0 = "The quick blacky fox 234 -. jumps over the lazy dog with " +
            "trotother Brown Fox. " +
            "The brown fox runs away and to be with " +
            "another  ; : again.";

    private static final String TERM_1 = "ui_good";

    private static final String NOT_NORMALIZED_TEXT_1 = "Testing the app for a few days convinced me the interfaces " +
            "are excellent!";
    private static final String FIELD_NAME = "FIELD_NAME";

    @Test
    void evaluateNoTextIndex0Normalizations() {
        // <Constant>brown fox</Constant>
        final KiePMMLConstant kiePMMLConstant = new KiePMMLConstant("NAME-1", Collections.emptyList(), TERM_0, null);
        List<KiePMMLNameValue> kiePMMLNameValues = Collections.singletonList(new KiePMMLNameValue(FIELD_NAME, TEXT_0));
        ProcessingDTO processingDTO = getProcessingDTO(kiePMMLNameValues);

        double frequency = 3.0;
        double logarithmic = Math.log10(1 + frequency);
        int maxFrequency = 2;
        double augmentedNormalizedTermFrequency = 0.5 * (1 + (frequency / (double) maxFrequency)); // cast
        // for java:S2184
        Map<LOCAL_TERM_WEIGHTS, Double> expectedResults = new HashMap<>();
        expectedResults.put(TERM_FREQUENCY, frequency);
        expectedResults.put(BINARY, 1.0);
        expectedResults.put(LOGARITHMIC, logarithmic);
        expectedResults.put(AUGMENTED_NORMALIZED_TERM_FREQUENCY, augmentedNormalizedTermFrequency);
        expectedResults.forEach((localTermWeights, expected) -> {
            KiePMMLTextIndex kiePMMLTextIndex = KiePMMLTextIndex.builder(FIELD_NAME, Collections.emptyList(),
                                                                         kiePMMLConstant)
                    .withMaxLevenshteinDistance(2)
                    .withLocalTermWeights(localTermWeights)
                    .withIsCaseSensitive(true)
                    .withWordSeparatorCharacterRE("\\s+")
                    .build();
            assertThat(kiePMMLTextIndex.evaluate(processingDTO)).isEqualTo(expected);
        });
    }

    @Test
    void evaluateTextIndex0Normalizations() {
        // <Constant>brown fox</Constant>
        final KiePMMLConstant kiePMMLConstant = new KiePMMLConstant("NAME-1", Collections.emptyList(), TERM_0, null);
        List<KiePMMLNameValue> kiePMMLNameValues = Collections.singletonList(new KiePMMLNameValue(FIELD_NAME,
                                                                                                  NOT_NORMALIZED_TEXT_0));
        ProcessingDTO processingDTO = getProcessingDTO(kiePMMLNameValues);

        double frequency = 3.0;
        double logarithmic = Math.log10(1 + frequency);
        int maxFrequency = 2;
        double augmentedNormalizedTermFrequency = 0.5 * (1 + (frequency / (double) maxFrequency)); // cast
        // for java:S2184
        Map<LOCAL_TERM_WEIGHTS, Double> expectedResults = new HashMap<>();
        expectedResults.put(TERM_FREQUENCY, frequency);
        expectedResults.put(BINARY, 1.0);
        expectedResults.put(LOGARITHMIC, logarithmic);
        expectedResults.put(AUGMENTED_NORMALIZED_TERM_FREQUENCY, augmentedNormalizedTermFrequency);
        expectedResults.forEach((localTermWeights, expected) -> {
            KiePMMLTextIndex kiePMMLTextIndex = KiePMMLTextIndex.builder(FIELD_NAME, Collections.emptyList(),
                                                                         kiePMMLConstant)
                    .withMaxLevenshteinDistance(2)
                    .withLocalTermWeights(localTermWeights)
                    .withIsCaseSensitive(true)
                    .withTextIndexNormalizations(getKiePMMLTextIndexNormalizations())
                    .build();
            assertThat(kiePMMLTextIndex.evaluate(processingDTO)).isEqualTo(expected);
        });
    }

    @Test
    void evaluateTextIndex1Normalizations() {
        // <TextIndexNormalization inField="string" outField="stem" regexField="regex">
        //        <InlineTable>
        //          <row>
        //            <string>interfaces?</string>
        //            <stem>interface</stem>
        //            <regex>true</regex>
        //          </row>
        //          <row>
        //            <string>is|are|seem(ed|s?)|were</string>
        //            <stem>be</stem>
        //            <regex>true</regex>
        //          </row>
        //          <row>
        //            <string>user friendl(y|iness)</string>
        //            <stem>user_friendly</stem>
        //            <regex>true</regex>
        //          </row>
        //        </InlineTable>
        //      </TextIndexNormalization>
        Map<String, Object> columnValues = new HashMap<>();
        columnValues.put("string", "interfaces?");
        columnValues.put("stem", "interface");
        columnValues.put("regex", "true");
        KiePMMLRow row0_0 = new KiePMMLRow(columnValues);
        columnValues = new HashMap<>();
        columnValues.put("string", "is|are|seem(ed|s?)|were");
        columnValues.put("stem", "be");
        columnValues.put("regex", "true");
        KiePMMLRow row0_1 = new KiePMMLRow(columnValues);
        columnValues = new HashMap<>();
        columnValues.put("string", "user friendl(y|iness)");
        columnValues.put("stem", "user_friendly");
        columnValues.put("regex", "true");
        KiePMMLRow row0_2 = new KiePMMLRow(columnValues);
        KiePMMLInlineTable inlineTable0 = new KiePMMLInlineTable("inlineTable0", Collections.emptyList(),
                                                                 Arrays.asList(row0_0, row0_1, row0_2));
        KiePMMLTextIndexNormalization indexNormalization0 = KiePMMLTextIndexNormalization.builder(
                        "indexNormalization0", Collections.emptyList())
                .withInField("string")
                .withOutField("stem")
                .withRegexField("regex")
                .withKiePMMLInlineTable(inlineTable0)
                .build();

        // <TextIndexNormalization inField="re" outField="feature" regexField="regex">
        //        <InlineTable>
        //          <row>
        //            <re>interface be (user_friendly|well designed|excellent)</re>
        //            <feature>ui_good</feature>
        //            <regex>true</regex>
        //          </row>
        //        </InlineTable>
        //      </TextIndexNormalization>
        columnValues = new HashMap<>();
        columnValues.put("re", "interface be (user_friendly|well designed|excellent)");
        columnValues.put("feature", "ui_good");
        columnValues.put("regex", "true");
        KiePMMLRow row1_0 = new KiePMMLRow(columnValues);
        KiePMMLInlineTable inlineTable1 = new KiePMMLInlineTable("inlineTable1", Collections.emptyList(),
                                                                 Collections.singletonList(row1_0));
        KiePMMLTextIndexNormalization indexNormalization1 = KiePMMLTextIndexNormalization.builder(
                        "indexNormalization1", Collections.emptyList())
                .withInField("re")
                .withOutField("feature")
                .withRegexField("regex")
                .withKiePMMLInlineTable(inlineTable1)
                .build();

        // <FieldRef field="term"/>
        KiePMMLFieldRef kiePMMLFieldRef = new KiePMMLFieldRef("term", Collections.emptyList(), null);

        KiePMMLTextIndex kiePMMLTextIndex = KiePMMLTextIndex.builder("reviewText", Collections.emptyList(),
                                                                     kiePMMLFieldRef)
                .withMaxLevenshteinDistance(2)
                .withLocalTermWeights(BINARY)
                .withIsCaseSensitive(false)
                .withTextIndexNormalizations(Arrays.asList(indexNormalization0, indexNormalization1))
                .build();

        List<KiePMMLNameValue> kiePMMLNameValues = Arrays.asList(new KiePMMLNameValue("term",
                                                                                      TERM_1),
                                                                 new KiePMMLNameValue("reviewText",
                                                                                      NOT_NORMALIZED_TEXT_1));
        ProcessingDTO processingDTO = getProcessingDTO(kiePMMLNameValues);
        assertThat(kiePMMLTextIndex.evaluate(processingDTO)).isEqualTo(1.0);
    }

    @Test
    void evaluateRawTokenize() {
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance(2);
        double frequency = 3.0;
        double logarithmic = Math.log10(1 + frequency);
        int maxFrequency = 2;
        double augmentedNormalizedTermFrequency = 0.5 * (1 + (frequency / (double) maxFrequency)); // cast
        // for java:S2184
        Map<LOCAL_TERM_WEIGHTS, Double> expectedResults = new HashMap<>();
        expectedResults.put(TERM_FREQUENCY, frequency);
        expectedResults.put(BINARY, 1.0);
        expectedResults.put(LOGARITHMIC, logarithmic);
        expectedResults.put(AUGMENTED_NORMALIZED_TERM_FREQUENCY, augmentedNormalizedTermFrequency);
        expectedResults.forEach((localTermWeights, expected) -> {
            double evaluateRaw = KiePMMLTextIndex.evaluateRaw(true,
                                                              true,
                                                              TERM_0,
                                                              TEXT_0,
                                                              "\\s+",
                                                              localTermWeights,
                                                              COUNT_HITS.ALL_HITS,
                                                              levenshteinDistance);
            assertThat(evaluateRaw).isCloseTo(expected, Offset.offset(0.0000001));
        });
        //---
        maxFrequency = 3;
        augmentedNormalizedTermFrequency = 0.5 * (1 + (frequency / (double) maxFrequency)); // cast
        // for java:S2184
        expectedResults = new HashMap<>();
        expectedResults.put(TERM_FREQUENCY, frequency);
        expectedResults.put(BINARY, 1.0);
        expectedResults.put(LOGARITHMIC, logarithmic);
        expectedResults.put(AUGMENTED_NORMALIZED_TERM_FREQUENCY, augmentedNormalizedTermFrequency);
        expectedResults.forEach((localTermWeights, expected) -> {
            double evaluateRaw = KiePMMLTextIndex.evaluateRaw(false,
                                                              true,
                                                              TERM_0,
                                                              TEXT_0,
                                                              "\\s+",
                                                              localTermWeights,
                                                              COUNT_HITS.ALL_HITS,
                                                              levenshteinDistance);
            assertThat(evaluateRaw).isCloseTo(expected, Offset.offset(0.0000001));
        });
        //---
        frequency = 4.0;
        logarithmic = Math.log10(1 + frequency);
        augmentedNormalizedTermFrequency = 0.5 * (1 + (frequency / (double) maxFrequency)); // cast
        // for java:S2184
        expectedResults = new HashMap<>();
        expectedResults.put(TERM_FREQUENCY, frequency);
        expectedResults.put(BINARY, 1.0);
        expectedResults.put(LOGARITHMIC, logarithmic);
        expectedResults.put(AUGMENTED_NORMALIZED_TERM_FREQUENCY, augmentedNormalizedTermFrequency);
        expectedResults.forEach((localTermWeights, expected) -> {
            double evaluateRaw = KiePMMLTextIndex.evaluateRaw(false,
                                                              true,
                                                              TERM_0,
                                                              TEXT_0,
                                                              "[\\s\\-]",
                                                              localTermWeights,
                                                              COUNT_HITS.ALL_HITS,
                                                              levenshteinDistance);
            assertThat(evaluateRaw).isCloseTo(expected, Offset.offset(0.0000001));
        });
    }

    @Test
    void evaluateRawNoTokenize() {
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance(2);
        Map<LOCAL_TERM_WEIGHTS, Double> expectedResults = new HashMap<>();
        double frequency = 3.0;
        double logarithmic = Math.log10(1 + frequency);
        int maxFrequency = 2;
        double augmentedNormalizedTermFrequency = 0.5 * (1 + (frequency / (double) maxFrequency)); // cast
        // for java:S2184
        expectedResults.put(TERM_FREQUENCY, frequency);
        expectedResults.put(BINARY, 1.0);
        expectedResults.put(LOGARITHMIC, logarithmic);
        expectedResults.put(AUGMENTED_NORMALIZED_TERM_FREQUENCY, augmentedNormalizedTermFrequency);
        expectedResults.forEach((localTermWeights, expected) -> {
            double evaluateRaw = KiePMMLTextIndex.evaluateRaw(true,
                                                              false,
                                                              TERM_0,
                                                              TEXT_0,
                                                              "\\s+",
                                                              localTermWeights,
                                                              COUNT_HITS.ALL_HITS,
                                                              levenshteinDistance);
            assertThat(evaluateRaw).isCloseTo(expected, Offset.offset(0.0000001));
        });
        //---
        maxFrequency = 3;
        augmentedNormalizedTermFrequency = 0.5 * (1 + (frequency / (double) maxFrequency)); // cast
        // for java:S2184
        expectedResults = new HashMap<>();
        expectedResults.put(TERM_FREQUENCY, frequency);
        expectedResults.put(BINARY, 1.0);
        expectedResults.put(LOGARITHMIC, logarithmic);
        expectedResults.put(AUGMENTED_NORMALIZED_TERM_FREQUENCY, augmentedNormalizedTermFrequency);
        expectedResults.forEach((localTermWeights, expected) -> {
            double evaluateRaw = KiePMMLTextIndex.evaluateRaw(false,
                                                              false,
                                                              TERM_0,
                                                              TEXT_0,
                                                              "\\s+",
                                                              localTermWeights,
                                                              COUNT_HITS.ALL_HITS,
                                                              levenshteinDistance);
            assertThat(evaluateRaw).isCloseTo(expected, Offset.offset(0.0000001));
        });
        //---
        frequency = 3.0;
        logarithmic = Math.log10(1 + frequency);
        augmentedNormalizedTermFrequency = 0.5 * (1 + (frequency / (double) maxFrequency)); // cast
        // for java:S2184
        expectedResults = new HashMap<>();
        expectedResults.put(TERM_FREQUENCY, frequency);
        expectedResults.put(BINARY, 1.0);
        expectedResults.put(LOGARITHMIC, logarithmic);
        expectedResults.put(AUGMENTED_NORMALIZED_TERM_FREQUENCY, augmentedNormalizedTermFrequency);
        expectedResults.forEach((localTermWeights, expected) -> {
            double evaluateRaw = KiePMMLTextIndex.evaluateRaw(false,
                                                              false,
                                                              TERM_0,
                                                              TEXT_0,
                                                              "[\\s\\-]",
                                                              localTermWeights,
                                                              COUNT_HITS.ALL_HITS,
                                                              levenshteinDistance);
            assertThat(evaluateRaw).isCloseTo(expected, Offset.offset(0.0000001));
        });
    }

    @Test
    void evaluateAugmentedNormalizedTermFrequency() {
        Map<Integer, String> source = new HashMap<>();
        int maxFrequency = 23;
        source.put(maxFrequency, "aword");
        source.put(19, "anotherword");
        source.put(5, "adifferentword");
        source.put(3, "lastword");
        List<String> texts = new ArrayList<>();
        source.forEach((integer, s) -> IntStream.range(0, integer).forEach(i -> texts.add(s)));
        Collections.shuffle(texts);
        int calculatedLevenshteinDistance = 4;
        int binaryEvaluation = 1;
        double expected = 0.5 * (binaryEvaluation + (calculatedLevenshteinDistance / (double) maxFrequency)); // cast
        // for java:S2184
        assertThat(KiePMMLTextIndex.evaluateAugmentedNormalizedTermFrequency(calculatedLevenshteinDistance, texts)).isCloseTo(expected, Offset.offset(0.0));
    }

    @Test
    void evaluateLevenshteinDistanceAllHits() {
        String wordSeparatorCharacterRE = "\\s+"; // brown-foxy does not match
        Pattern pattern = Pattern.compile(wordSeparatorCharacterRE);
        List<String> terms = KiePMMLTextIndex.splitText(TERM_0, pattern);
        List<String> texts = KiePMMLTextIndex.splitText(TEXT_0, pattern);
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance(0);
        assertThat(KiePMMLTextIndex.evaluateLevenshteinDistanceAllHits(levenshteinDistance, terms, texts)).isEqualTo(1);
        levenshteinDistance = new LevenshteinDistance(1);
        assertThat(KiePMMLTextIndex.evaluateLevenshteinDistanceAllHits(levenshteinDistance, terms, texts)).isEqualTo(2);
        levenshteinDistance = new LevenshteinDistance(2);
        assertThat(KiePMMLTextIndex.evaluateLevenshteinDistanceAllHits(levenshteinDistance, terms, texts)).isEqualTo(3);
        //---
        wordSeparatorCharacterRE = "[\\s\\-]"; // brown-foxy match
        pattern = Pattern.compile(wordSeparatorCharacterRE);
        terms = KiePMMLTextIndex.splitText(TERM_0, pattern);
        texts = KiePMMLTextIndex.splitText(TEXT_0, pattern);
        levenshteinDistance = new LevenshteinDistance(0);
        assertThat(KiePMMLTextIndex.evaluateLevenshteinDistanceAllHits(levenshteinDistance, terms, texts)).isEqualTo(1);
        levenshteinDistance = new LevenshteinDistance(1);
        assertThat(KiePMMLTextIndex.evaluateLevenshteinDistanceAllHits(levenshteinDistance, terms, texts)).isEqualTo(3);
        levenshteinDistance = new LevenshteinDistance(2);
        assertThat(KiePMMLTextIndex.evaluateLevenshteinDistanceAllHits(levenshteinDistance, terms, texts)).isEqualTo(4);
    }

    @Test
    void evaluateLevenshteinDistanceBestHits() {
        String wordSeparatorCharacterRE = "\\s+"; // brown-foxy does not match
        Pattern pattern = Pattern.compile(wordSeparatorCharacterRE);
        List<String> terms = KiePMMLTextIndex.splitText("The", pattern);
        List<String> texts = KiePMMLTextIndex.splitText(TEXT_0, pattern);
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance(0);
        assertThat(KiePMMLTextIndex.evaluateLevenshteinDistanceBestHits(levenshteinDistance, terms, texts)).isEqualTo(2);
        levenshteinDistance = new LevenshteinDistance(1);
        assertThat(KiePMMLTextIndex.evaluateLevenshteinDistanceBestHits(levenshteinDistance, terms, texts)).isEqualTo(2);
        levenshteinDistance = new LevenshteinDistance(2);
        assertThat(KiePMMLTextIndex.evaluateLevenshteinDistanceBestHits(levenshteinDistance, terms, texts)).isEqualTo(2);
        //---
        wordSeparatorCharacterRE = "[\\s\\-]"; // brown-foxy match
        pattern = Pattern.compile(wordSeparatorCharacterRE);
        terms = KiePMMLTextIndex.splitText("The", pattern);
        texts = KiePMMLTextIndex.splitText(TEXT_0, pattern);
        levenshteinDistance = new LevenshteinDistance(0);
        assertThat(KiePMMLTextIndex.evaluateLevenshteinDistanceBestHits(levenshteinDistance, terms, texts)).isEqualTo(2);
        levenshteinDistance = new LevenshteinDistance(1);
        assertThat(KiePMMLTextIndex.evaluateLevenshteinDistanceBestHits(levenshteinDistance, terms, texts)).isEqualTo(2);
        levenshteinDistance = new LevenshteinDistance(2);
        assertThat(KiePMMLTextIndex.evaluateLevenshteinDistanceBestHits(levenshteinDistance, terms, texts)).isEqualTo(2);
    }

    @Test
    void evaluateLevenshteinDistanceSplitText() {
        String toSearch = "brown fox";
        String toScan = "brown fox";
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance(0);
        assertThat(KiePMMLTextIndex.evaluateLevenshteinDistance(levenshteinDistance, toSearch, toScan)).isEqualTo(0);
        levenshteinDistance = new LevenshteinDistance(1);
        assertThat(KiePMMLTextIndex.evaluateLevenshteinDistance(levenshteinDistance, toSearch, toScan)).isEqualTo(0);
        levenshteinDistance = new LevenshteinDistance(2);
        assertThat(KiePMMLTextIndex.evaluateLevenshteinDistance(levenshteinDistance, toSearch, toScan)).isEqualTo(0);
        toScan = "brown foxy";
        levenshteinDistance = new LevenshteinDistance(0);
        assertThat(KiePMMLTextIndex.evaluateLevenshteinDistance(levenshteinDistance, toSearch, toScan)).isEqualTo(-1);
        levenshteinDistance = new LevenshteinDistance(1);
        assertThat(KiePMMLTextIndex.evaluateLevenshteinDistance(levenshteinDistance, toSearch, toScan)).isEqualTo(1);
        levenshteinDistance = new LevenshteinDistance(2);
        assertThat(KiePMMLTextIndex.evaluateLevenshteinDistance(levenshteinDistance, toSearch, toScan)).isEqualTo(1);
        toScan = "browny foxy";
        levenshteinDistance = new LevenshteinDistance(0);
        assertThat(KiePMMLTextIndex.evaluateLevenshteinDistance(levenshteinDistance, toSearch, toScan)).isEqualTo(-1);
        levenshteinDistance = new LevenshteinDistance(1);
        assertThat(KiePMMLTextIndex.evaluateLevenshteinDistance(levenshteinDistance, toSearch, toScan)).isEqualTo(-1);
        levenshteinDistance = new LevenshteinDistance(2);
        assertThat(KiePMMLTextIndex.evaluateLevenshteinDistance(levenshteinDistance, toSearch, toScan)).isEqualTo(2);
    }

    @Test
    void splitText() {
        final Pattern unwantedPattern = Pattern.compile("[^a-zA-Z0-9 ]");
        final Pattern wantedPattern = Pattern.compile("[a-zA-Z0-9]");
        Pattern pattern = Pattern.compile("\\s+");
        List<String> retrieved = KiePMMLTextIndex.splitText(TEXT_0, pattern);
        assertThat(retrieved).hasSize(25);
        retrieved.forEach(txt -> {
            assertThat(unwantedPattern.matcher(txt).find()).isFalse();
            assertThat(wantedPattern.matcher(txt).find()).isTrue();
        });
        pattern = Pattern.compile("[\\s\\-]");
        retrieved = KiePMMLTextIndex.splitText(TEXT_0, pattern);
        assertThat(retrieved).hasSize(26);
        retrieved.forEach(txt -> {
            assertThat(unwantedPattern.matcher(txt).find()).isFalse();
            assertThat(wantedPattern.matcher(txt).find()).isTrue();
        });
    }

    private List<KiePMMLTextIndexNormalization> getKiePMMLTextIndexNormalizations() {
        Map<String, Object> columnValues0 = new HashMap<>();
        columnValues0.put("string", "blacky");
        columnValues0.put("stem", "browny");
        columnValues0.put("regex", "false");
        KiePMMLRow row0 = new KiePMMLRow(columnValues0);
        Map<String, Object> columnValues1 = new HashMap<>();
        columnValues1.put("string", "trot?");
        columnValues1.put("stem", "an");
        columnValues1.put("regex", "true");
        KiePMMLRow row1 = new KiePMMLRow(columnValues1);
        KiePMMLInlineTable inlineTable1 = new KiePMMLInlineTable("inlineTable1", Collections.emptyList(),
                                                                 Arrays.asList(row0, row1));
        KiePMMLTextIndexNormalization indexNormalization1 = KiePMMLTextIndexNormalization.builder(
                        "indexNormalization1", Collections.emptyList())
                .withKiePMMLInlineTable(inlineTable1)
                .withRecursive(true)
                .build();
        Map<String, Object> columnValues2 = new HashMap<>();
        columnValues2.put("string", "again|is|are|seem(ed|s?)|were?");
        columnValues2.put("stem", "brown-foxy");
        columnValues2.put("regex", "true");
        KiePMMLRow row2 = new KiePMMLRow(columnValues2);
        KiePMMLInlineTable inlineTable2 = new KiePMMLInlineTable("inlineTable2", Collections.emptyList(),
                                                                 Collections.singletonList(row2));
        KiePMMLTextIndexNormalization indexNormalization2 = KiePMMLTextIndexNormalization.builder(
                        "indexNormalization2", Collections.emptyList())
                .withKiePMMLInlineTable(inlineTable2)
                .withRecursive(true)
                .build();
        return Arrays.asList(indexNormalization1, indexNormalization2);
    }
}