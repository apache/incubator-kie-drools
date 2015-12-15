/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.api.score;

import java.io.Serializable;
import java.math.BigDecimal;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;

/**
 * Abstract superclass for {@link Score}.
 * <p>
 * Subclasses must be immutable.
 * @see Score
 * @see HardSoftScore
 */
public abstract class AbstractScore<S extends Score> implements Score<S>, Serializable {

    protected static String[] parseLevelStrings(Class<? extends Score> scoreClass,
            String scoreString, int levelsSize) {
        String[] scoreTokens = scoreString.split("/");
        if (scoreTokens.length != levelsSize) {
            throw new IllegalArgumentException("The scoreString (" + scoreString
                    + ") for the scoreClass (" + scoreClass.getSimpleName()
                    + ") doesn't follow the correct pattern (" + buildScorePattern(levelsSize) + "):"
                    + " the scoreTokens length (" + scoreTokens.length
                    + ") differs from the levelsSize (" + levelsSize + "). Check the <scoreDefinitionType> too.");
        }
        return scoreTokens;
    }

    protected static String[] parseLevelStrings(Class<? extends Score> scoreClass,
            String scoreString, String... levelSuffixes) {
        String[] scoreTokens = scoreString.split("/");
        if (scoreTokens.length != levelSuffixes.length) {
            throw new IllegalArgumentException("The scoreString (" + scoreString
                    + ") for the scoreClass (" + scoreClass.getSimpleName()
                    + ") doesn't follow the correct pattern (" + buildScorePattern(levelSuffixes) + "):"
                    + " the scoreTokens length (" + scoreTokens.length
                    + ") differs from the levelSuffixes length (" + levelSuffixes.length + ").");
        }
        String[] levelStrings = new String[levelSuffixes.length];
        for (int i = 0; i < levelSuffixes.length; i++) {
            if (!scoreTokens[i].endsWith(levelSuffixes[i])) {
                throw new IllegalArgumentException("The scoreString (" + scoreString
                        + ") for the scoreClass (" + scoreClass.getSimpleName()
                        + ") doesn't follow the correct pattern (" + buildScorePattern(levelSuffixes) + "):"
                        + " the scoreToken (" + scoreTokens[i]
                        + ") does not end with levelSuffix (" + levelSuffixes[i] + ").");
            }
            levelStrings[i] = scoreTokens[i].substring(0, scoreTokens[i].length() - levelSuffixes[i].length());
        }
        return levelStrings;
    }

    protected static int parseLevelAsInt(Class<? extends Score> scoreClass,
            String scoreString, String levelString) {
        try {
            return Integer.parseInt(levelString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The scoreString (" + scoreString
                    + ") for the scoreClass (" + scoreClass.getSimpleName() + ") has a levelString (" + levelString
                    + ") which is not a valid integer.", e);
        }
    }

    protected static long parseLevelAsLong(Class<? extends Score> scoreClass,
            String scoreString, String levelString) {
        try {
            return Long.parseLong(levelString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The scoreString (" + scoreString
                    + ") for the scoreClass (" + scoreClass.getSimpleName() + ") has a levelString (" + levelString
                    + ") which is not a valid long.", e);
        }
    }

    protected static double parseLevelAsDouble(Class<? extends Score> scoreClass,
            String scoreString, String levelString) {
        try {
            return Double.parseDouble(levelString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The scoreString (" + scoreString
                    + ") for the scoreClass (" + scoreClass.getSimpleName() + ") has a levelString (" + levelString
                    + ") which is not a valid double.", e);
        }
    }

    protected static BigDecimal parseLevelAsBigDecimal(Class<? extends Score> scoreClass,
            String scoreString, String levelString) {
        try {
            return new BigDecimal(levelString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The scoreString (" + scoreString
                    + ") for the scoreClass (" + scoreClass.getSimpleName() + ") has a levelString (" + levelString
                    + ") which is not a valid BigDecimal.", e);
        }
    }

    protected static String buildScorePattern(int levelsSize) {
        StringBuilder scorePattern = new StringBuilder(levelsSize * 4);
        boolean first = true;
        for (int i = 0; i < levelsSize; i++) {
            if (first) {
                first = false;
            } else {
                scorePattern.append("/");
            }
            scorePattern.append("999");
        }
        return scorePattern.toString();
    }

    protected static String buildScorePattern(String... levelSuffixes) {
        StringBuilder scorePattern = new StringBuilder(levelSuffixes.length * 10);
        boolean first = true;
        for (String levelSuffix : levelSuffixes) {
            if (first) {
                first = false;
            } else {
                scorePattern.append("/");
            }
            scorePattern.append("999");
            scorePattern.append(levelSuffix);
        }
        return scorePattern.toString();
    }

    @Override
    public boolean isCompatibleArithmeticArgument(Score otherScore) {
        return getClass().isInstance(otherScore);
    }

}
