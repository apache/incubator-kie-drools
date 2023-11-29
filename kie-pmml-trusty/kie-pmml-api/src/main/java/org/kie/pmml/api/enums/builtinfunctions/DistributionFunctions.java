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
package org.kie.pmml.api.enums.builtinfunctions;

import java.util.Arrays;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.special.Erf;
import org.kie.pmml.api.exceptions.KieEnumException;
import org.kie.pmml.api.exceptions.KiePMMLException;

import static org.kie.pmml.api.enums.BUILTIN_FUNCTIONS.checkNumbers;

public enum DistributionFunctions {

    NORMAL_CDF("normalCDF"),
    NORMAL_PDF("normalPDF"),
    STD_NORMAL_CDF("stdNormalCDF"),
    STD_NORMAL_PDF("stdNormalPDF"),
    ERF("erf"),
    NORMAL_IDF("normalIDF"),
    STD_NORMAL_IDF("stdNormalIDF");

    private final String name;

    DistributionFunctions(String name) {
        this.name = name;
    }

    public static boolean isDistributionFunctions(String name) {
        return Arrays.stream(DistributionFunctions.values())
                .anyMatch(value -> name.equals(value.name));
    }

    public static DistributionFunctions byName(String name) {
        return Arrays.stream(DistributionFunctions.values())
                .filter(value -> name.equals(value.name))
                .findFirst()
                .orElseThrow(() -> new KieEnumException("Failed to find DistributionFunctions with name: " + name));
    }

    public String getName() {
        return name;
    }

    public Object getValue(final Object[] inputData) {
        switch (this) {
            case NORMAL_CDF:
                return normalCDF(inputData);
            case NORMAL_PDF:
                return normalPDF(inputData);
            case STD_NORMAL_CDF:
                return stdNormalCDF(inputData);
            case STD_NORMAL_PDF:
                return stdNormalPDF(inputData);
            case ERF:
                return erf(inputData);
            case NORMAL_IDF:
                return normalIDF(inputData);
            case STD_NORMAL_IDF:
                return stdNormalIDF(inputData);
            default:
                throw new KiePMMLException("Unmanaged DistributionFunctions " + this);
        }
    }

    private double normalCDF(final Object[] inputData) {
        checkNumbers(inputData, 3);
        double x = ((Number) inputData[0]).doubleValue();
        double mu = ((Number) inputData[1]).doubleValue();
        double sigma = ((Number) inputData[2]).doubleValue();
        NormalDistribution normalDistribution = new NormalDistribution(mu, sigma);
        return normalDistribution.cumulativeProbability(x);
    }

    private double normalPDF(final Object[] inputData) {
        checkNumbers(inputData, 3);
        double x = ((Number) inputData[0]).doubleValue();
        double mu = ((Number) inputData[1]).doubleValue();
        double sigma = ((Number) inputData[2]).doubleValue();
        NormalDistribution normalDistribution = new NormalDistribution(mu, sigma);
        return normalDistribution.density(x);
    }

    private double stdNormalCDF(final Object[] inputData) {
        checkNumbers(inputData, 1);
        double x = ((Number) inputData[0]).doubleValue();
        NormalDistribution normalDistribution = new NormalDistribution();
        return normalDistribution.cumulativeProbability(x);
    }

    private double stdNormalPDF(final Object[] inputData) {
        checkNumbers(inputData, 1);
        double x = ((Number) inputData[0]).doubleValue();
        NormalDistribution normalDistribution = new NormalDistribution();
        return normalDistribution.density(x);
    }

    private double erf(final Object[] inputData) {
        checkNumbers(inputData, 1);
        double x = ((Number) inputData[0]).doubleValue();
        return Erf.erf(x);
    }

    private double normalIDF(final Object[] inputData) {
        checkNumbers(inputData, 3);
        double p = ((Number) inputData[0]).doubleValue();
        double mu = ((Number) inputData[1]).doubleValue();
        double sigma = ((Number) inputData[2]).doubleValue();
        NormalDistribution normalDistribution = new NormalDistribution(mu, sigma);
        return normalDistribution.inverseCumulativeProbability(p);
    }

    private double stdNormalIDF(final Object[] inputData) {
        checkNumbers(inputData, 1);
        double x = ((Number) inputData[0]).doubleValue();
        NormalDistribution normalDistribution = new NormalDistribution();
        return normalDistribution.inverseCumulativeProbability(x);
    }

}
