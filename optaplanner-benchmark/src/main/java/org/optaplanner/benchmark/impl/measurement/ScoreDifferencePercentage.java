/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.impl.measurement;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.apache.commons.lang3.LocaleUtils;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.score.ScoreUtils;

public class ScoreDifferencePercentage {

    public static <Score_ extends Score<Score_>> ScoreDifferencePercentage calculateScoreDifferencePercentage(
            Score_ baseScore, Score_ valueScore) {
        double[] baseLevels = ScoreUtils.extractLevelDoubles(baseScore);
        double[] valueLevels = ScoreUtils.extractLevelDoubles(valueScore);
        if (baseLevels.length != valueLevels.length) {
            throw new IllegalStateException("The baseScore (" + baseScore + ")'s levelsLength (" + baseLevels.length
                    + ") is different from the valueScore (" + valueScore + ")'s levelsLength (" + valueLevels.length
                    + ").");
        }
        double[] percentageLevels = new double[baseLevels.length];
        for (int i = 0; i < baseLevels.length; i++) {
            percentageLevels[i] = calculateDifferencePercentage(baseLevels[i], valueLevels[i]);
        }
        return new ScoreDifferencePercentage(percentageLevels);
    }

    public static double calculateDifferencePercentage(double base, double value) {
        double difference = value - base;
        if (base < 0.0) {
            return difference / -base;
        } else if (base == 0.0) {
            if (difference == 0.0) {
                return 0.0;
            } else {
                // percentageLevel will return Infinity or -Infinity
                return difference / base;
            }
        } else {
            return difference / base;
        }
    }

    private final double[] percentageLevels;

    public ScoreDifferencePercentage(double[] percentageLevels) {
        this.percentageLevels = percentageLevels;
    }

    public double[] getPercentageLevels() {
        return percentageLevels;
    }

    // ************************************************************************
    // Worker methods
    // ************************************************************************

    public ScoreDifferencePercentage add(ScoreDifferencePercentage addend) {
        if (percentageLevels.length != addend.getPercentageLevels().length) {
            throw new IllegalStateException("The addend (" + addend + ")'s levelsLength (" +
                    addend.getPercentageLevels().length + ") is different from the base (" +
                    this + ")'s levelsLength (" + percentageLevels.length + ").");
        }
        double[] newPercentageLevels = new double[percentageLevels.length];
        for (int i = 0; i < percentageLevels.length; i++) {
            newPercentageLevels[i] = percentageLevels[i] + addend.percentageLevels[i];
        }
        return new ScoreDifferencePercentage(newPercentageLevels);
    }

    public ScoreDifferencePercentage subtract(ScoreDifferencePercentage subtrahend) {
        if (percentageLevels.length != subtrahend.getPercentageLevels().length) {
            throw new IllegalStateException("The subtrahend (" + subtrahend + ")'s levelsLength (" +
                    subtrahend.getPercentageLevels().length + ") is different from the base (" +
                    this + ")'s levelsLength (" + percentageLevels.length + ").");
        }
        double[] newPercentageLevels = new double[percentageLevels.length];
        for (int i = 0; i < percentageLevels.length; i++) {
            newPercentageLevels[i] = percentageLevels[i] - subtrahend.percentageLevels[i];
        }
        return new ScoreDifferencePercentage(newPercentageLevels);
    }

    public ScoreDifferencePercentage multiply(double multiplicand) {
        double[] newPercentageLevels = new double[percentageLevels.length];
        for (int i = 0; i < percentageLevels.length; i++) {
            newPercentageLevels[i] = percentageLevels[i] * multiplicand;
        }
        return new ScoreDifferencePercentage(newPercentageLevels);
    }

    public ScoreDifferencePercentage divide(double divisor) {
        double[] newPercentageLevels = new double[percentageLevels.length];
        for (int i = 0; i < percentageLevels.length; i++) {
            newPercentageLevels[i] = percentageLevels[i] / divisor;
        }
        return new ScoreDifferencePercentage(newPercentageLevels);
    }

    @Override
    public String toString() {
        return toString(Locale.US);
    }

    public String toString(String locale) {
        return toString(LocaleUtils.toLocale(locale));
    }

    public String toString(Locale locale) {
        StringBuilder s = new StringBuilder(percentageLevels.length * 8);
        DecimalFormat decimalFormat = new DecimalFormat("0.00%", DecimalFormatSymbols.getInstance(locale));
        for (int i = 0; i < percentageLevels.length; i++) {
            if (i > 0) {
                s.append("/");
            }
            s.append(decimalFormat.format(percentageLevels[i]));
        }
        return s.toString();
    }

}
