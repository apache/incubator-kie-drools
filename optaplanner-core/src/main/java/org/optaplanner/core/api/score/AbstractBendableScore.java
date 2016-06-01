/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.core.api.score.buildin.bendable.BendableScore;

/**
 * Abstract superclass for bendable {@link Score} types.
 * <p>
 * Subclasses must be immutable.
 * @see BendableScore
 */
public abstract class AbstractBendableScore<S extends FeasibilityScore<S>> extends AbstractScore<S> {

    protected static final String HARD_LABEL = "hard";
    protected static final String SOFT_LABEL = "soft";
    protected static final String[] LEVEL_SUFFIXES = new String[]{HARD_LABEL, SOFT_LABEL};

    protected static String[][] parseBendableScoreTokens(Class<? extends Score> scoreClass, String scoreString) {
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

    public abstract int getHardLevelsSize();

    public abstract int getSoftLevelsSize();

    /**
     * @return {@link #getHardLevelsSize()} + {@link #getSoftLevelsSize()}
     */
    public abstract int getLevelsSize();

    protected AbstractBendableScore(int initScore) {
        super(initScore);
    }

}
