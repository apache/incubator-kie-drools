/*
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

package org.optaplanner.core.impl.score;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.function.Predicate;

import org.optaplanner.core.api.score.IBendableScore;
import org.optaplanner.core.api.score.Score;

public final class ScoreUtil {

    public static final String INIT_LABEL = "init";
    public static final String HARD_LABEL = "hard";
    public static final String MEDIUM_LABEL = "medium";
    public static final String SOFT_LABEL = "soft";
    public static final String[] LEVEL_SUFFIXES = new String[] { HARD_LABEL, SOFT_LABEL };

    public static String[] parseScoreTokens(Class<? extends Score<?>> scoreClass, String scoreString, String... levelSuffixes) {
        String[] scoreTokens = new String[levelSuffixes.length + 1];
        String[] suffixedScoreTokens = scoreString.split("/");
        int startIndex;
        if (suffixedScoreTokens.length == levelSuffixes.length + 1) {
            String suffixedScoreToken = suffixedScoreTokens[0];
            if (!suffixedScoreToken.endsWith(INIT_LABEL)) {
                throw new IllegalArgumentException("The scoreString (" + scoreString
                        + ") for the scoreClass (" + scoreClass.getSimpleName()
                        + ") doesn't follow the correct pattern (" + buildScorePattern(false, levelSuffixes) + "):"
                        + " the suffixedScoreToken (" + suffixedScoreToken
                        + ") does not end with levelSuffix (" + INIT_LABEL + ").");
            }
            scoreTokens[0] = suffixedScoreToken.substring(0, suffixedScoreToken.length() - INIT_LABEL.length());
            startIndex = 1;
        } else if (suffixedScoreTokens.length == levelSuffixes.length) {
            scoreTokens[0] = "0";
            startIndex = 0;
        } else {
            throw new IllegalArgumentException("The scoreString (" + scoreString
                    + ") for the scoreClass (" + scoreClass.getSimpleName()
                    + ") doesn't follow the correct pattern (" + buildScorePattern(false, levelSuffixes) + "):"
                    + " the suffixedScoreTokens length (" + suffixedScoreTokens.length
                    + ") differs from the levelSuffixes length ("
                    + levelSuffixes.length + " or " + (levelSuffixes.length + 1) + ").");
        }
        for (int i = 0; i < levelSuffixes.length; i++) {
            String suffixedScoreToken = suffixedScoreTokens[startIndex + i];
            String levelSuffix = levelSuffixes[i];
            if (!suffixedScoreToken.endsWith(levelSuffix)) {
                throw new IllegalArgumentException("The scoreString (" + scoreString
                        + ") for the scoreClass (" + scoreClass.getSimpleName()
                        + ") doesn't follow the correct pattern (" + buildScorePattern(false, levelSuffixes) + "):"
                        + " the suffixedScoreToken (" + suffixedScoreToken
                        + ") does not end with levelSuffix (" + levelSuffix + ").");
            }
            scoreTokens[1 + i] = suffixedScoreToken.substring(0, suffixedScoreToken.length() - levelSuffix.length());
        }
        return scoreTokens;
    }

    public static int parseInitScore(Class<? extends Score<?>> scoreClass, String scoreString, String initScoreString) {
        try {
            return Integer.parseInt(initScoreString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The scoreString (" + scoreString
                    + ") for the scoreClass (" + scoreClass.getSimpleName() + ") has a initScoreString ("
                    + initScoreString + ") which is not a valid integer.", e);
        }
    }

    public static int parseLevelAsInt(Class<? extends Score<?>> scoreClass, String scoreString, String levelString) {
        if (levelString.equals("*")) {
            return Integer.MIN_VALUE;
        }
        try {
            return Integer.parseInt(levelString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The scoreString (" + scoreString
                    + ") for the scoreClass (" + scoreClass.getSimpleName() + ") has a levelString (" + levelString
                    + ") which is not a valid integer.", e);
        }
    }

    public static long parseLevelAsLong(Class<? extends Score<?>> scoreClass, String scoreString, String levelString) {
        if (levelString.equals("*")) {
            return Long.MIN_VALUE;
        }
        try {
            return Long.parseLong(levelString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The scoreString (" + scoreString
                    + ") for the scoreClass (" + scoreClass.getSimpleName() + ") has a levelString (" + levelString
                    + ") which is not a valid long.", e);
        }
    }

    public static BigDecimal parseLevelAsBigDecimal(Class<? extends Score<?>> scoreClass, String scoreString,
            String levelString) {
        if (levelString.equals("*")) {
            throw new IllegalArgumentException("The scoreString (" + scoreString
                    + ") for the scoreClass (" + scoreClass.getSimpleName()
                    + ") has a wildcard (*) as levelString (" + levelString
                    + ") which is not supported for BigDecimal score values," +
                    " because there is no general MIN_VALUE for BigDecimal.");
        }
        try {
            return new BigDecimal(levelString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("The scoreString (" + scoreString
                    + ") for the scoreClass (" + scoreClass.getSimpleName() + ") has a levelString (" + levelString
                    + ") which is not a valid BigDecimal.", e);
        }
    }

    public static String buildScorePattern(boolean bendable, String... levelSuffixes) {
        StringBuilder scorePattern = new StringBuilder(levelSuffixes.length * 10);
        boolean first = true;
        for (String levelSuffix : levelSuffixes) {
            if (first) {
                first = false;
            } else {
                scorePattern.append("/");
            }
            if (bendable) {
                scorePattern.append("[999/.../999]");
            } else {
                scorePattern.append("999");
            }
            scorePattern.append(levelSuffix);
        }
        return scorePattern.toString();
    }

    public static String getInitPrefix(int initScore) {
        if (initScore == 0) {
            return "";
        }
        return initScore + INIT_LABEL + "/";
    }

    public static <Score_ extends Score<Score_>> String buildShortString(Score<Score_> score, Predicate<Number> notZero,
            String... levelLabels) {
        int initScore = score.initScore();
        StringBuilder shortString = new StringBuilder();
        if (initScore != 0) {
            shortString.append(initScore).append(INIT_LABEL);
        }
        int i = 0;
        for (Number levelNumber : score.toLevelNumbers()) {
            if (notZero.test(levelNumber)) {
                if (shortString.length() > 0) {
                    shortString.append("/");
                }
                shortString.append(levelNumber).append(levelLabels[i]);
            }
            i++;
        }
        if (shortString.length() == 0) {
            // Even for BigDecimals we use "0" over "0.0" because different levels can have different scales
            return "0";
        }
        return shortString.toString();
    }

    public static String[][] parseBendableScoreTokens(Class<? extends IBendableScore<?>> scoreClass,
            String scoreString) {
        String[][] scoreTokens = new String[3][];
        scoreTokens[0] = new String[1];
        int startIndex = 0;
        int initEndIndex = scoreString.indexOf(INIT_LABEL, startIndex);
        if (initEndIndex >= 0) {
            scoreTokens[0][0] = scoreString.substring(startIndex, initEndIndex);
            startIndex = initEndIndex + INIT_LABEL.length() + "/".length();
        } else {
            scoreTokens[0][0] = "0";
        }
        for (int i = 0; i < LEVEL_SUFFIXES.length; i++) {
            String levelSuffix = LEVEL_SUFFIXES[i];
            int endIndex = scoreString.indexOf(levelSuffix, startIndex);
            if (endIndex < 0) {
                throw new IllegalArgumentException("The scoreString (" + scoreString
                        + ") for the scoreClass (" + scoreClass.getSimpleName()
                        + ") doesn't follow the correct pattern (" + buildScorePattern(true, LEVEL_SUFFIXES) + "):"
                        + " the levelSuffix (" + levelSuffix
                        + ") isn't in the scoreSubstring (" + scoreString.substring(startIndex) + ").");
            }
            String scoreSubString = scoreString.substring(startIndex, endIndex);
            if (!scoreSubString.startsWith("[") || !scoreSubString.endsWith("]")) {
                throw new IllegalArgumentException("The scoreString (" + scoreString
                        + ") for the scoreClass (" + scoreClass.getSimpleName()
                        + ") doesn't follow the correct pattern (" + buildScorePattern(true, LEVEL_SUFFIXES) + "):"
                        + " the scoreSubString (" + scoreSubString
                        + ") does not start and end with \"[\" and \"]\".");
            }
            if (scoreSubString.equals("[]")) {
                scoreTokens[1 + i] = new String[0];
            } else {
                scoreTokens[1 + i] = scoreSubString.substring(1, scoreSubString.length() - 1).split("/");
            }
            startIndex = endIndex + levelSuffix.length() + "/".length();
        }
        if (startIndex != scoreString.length() + "/".length()) {
            throw new IllegalArgumentException("The scoreString (" + scoreString
                    + ") for the scoreClass (" + scoreClass.getSimpleName()
                    + ") doesn't follow the correct pattern (" + buildScorePattern(true, LEVEL_SUFFIXES) + "):"
                    + " the suffix (" + scoreString.substring(startIndex - 1) + ") is unsupported.");
        }
        return scoreTokens;
    }

    public static <Score_ extends IBendableScore<Score_>> String buildBendableShortString(IBendableScore<Score_> score,
            Predicate<Number> notZero) {
        int initScore = score.initScore();
        StringBuilder shortString = new StringBuilder();
        if (initScore != 0) {
            shortString.append(initScore).append(INIT_LABEL);
        }
        Number[] levelNumbers = score.toLevelNumbers();
        int hardLevelsSize = score.hardLevelsSize();
        if (Arrays.stream(levelNumbers).limit(hardLevelsSize).anyMatch(notZero)) {
            if (shortString.length() > 0) {
                shortString.append("/");
            }
            shortString.append("[");
            boolean first = true;
            for (int i = 0; i < hardLevelsSize; i++) {
                if (first) {
                    first = false;
                } else {
                    shortString.append("/");
                }
                shortString.append(levelNumbers[i]);
            }
            shortString.append("]").append(HARD_LABEL);
        }
        int softLevelsSize = score.softLevelsSize();
        if (Arrays.stream(levelNumbers).skip(hardLevelsSize).anyMatch(notZero)) {
            if (shortString.length() > 0) {
                shortString.append("/");
            }
            shortString.append("[");
            boolean first = true;
            for (int i = 0; i < softLevelsSize; i++) {
                if (first) {
                    first = false;
                } else {
                    shortString.append("/");
                }
                shortString.append(levelNumbers[hardLevelsSize + i]);
            }
            shortString.append("]").append(SOFT_LABEL);
        }
        if (shortString.length() == 0) {
            // Even for BigDecimals we use "0" over "0.0" because different levels can have different scales
            return "0";
        }
        return shortString.toString();
    }

    private ScoreUtil() {
        // No external instances.
    }

}
