/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.explainability.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.kie.kogito.explainability.model.DataDistribution;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureDistribution;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.PerturbationContext;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;

/**
 * Utility methods to handle and manipulate data.
 */
public class DataUtils {

    private DataUtils() {
    }

    /**
     * Generate a dataset of a certain size, sampled from a normal distribution, given mean and standard deviation.
     * Samples are generated from a normal distribution, multiplied by {@code stdDeviation} and summed to {@code mean},
     * actual mean {@code m} and standard deviation {@code d} are calculated.
     * Then all numbers are multiplied by the same number so that the standard deviation also gets
     * multiplied by the same number, hence we multiply each random number by {@code stdDeviation / d}.
     * The resultant set has standard deviation {@code stdDeviation} and mean {@code m1=m*stdDeviation/d}.
     * If a same number is added to all values the mean also changes by the same number so we add {@code mean - m1} to
     * all numbers.
     *
     * @param mean         desired mean
     * @param stdDeviation desired standard deviation
     * @param size         size of the array
     * @return the generated data
     */
    public static double[] generateData(double mean, double stdDeviation, int size, Random random) {

        // generate random data from a normal (gaussian) distribution
        double[] data = new double[size];
        for (int i = 0; i < size; i++) {
            data[i] = random.nextGaussian() * stdDeviation + mean;
        }

        double m = getMean(data);
        double d = getStdDev(data, m);

        // force desired standard deviation
        double d1 = stdDeviation / d;
        for (int i = 0; i < size; i++) {
            data[i] *= d1;
        }

        // get the new mean
        double m1 = m * stdDeviation / d;

        // force desired mean
        for (int i = 0; i < size; i++) {
            data[i] += mean - m1;
        }

        return data;
    }

    static double getMean(double[] data) {
        double m = 0;
        for (double datum : data) {
            m += datum;
        }
        m = m / data.length;
        return m;
    }

    static double getStdDev(double[] data, double mean) {
        double d = 0;
        for (double datum : data) {
            d += Math.pow(datum - mean, 2);
        }
        d /= data.length;
        d = Math.sqrt(d);
        return d;
    }

    /**
     * Generate equally {@code size} sampled values between {@code min} and {@code max}.
     *
     * @param min  minimum value
     * @param max  maximum value
     * @param size dataset size
     * @return the generated data
     */
    public static double[] generateSamples(double min, double max, int size) {
        double[] data = new double[size];
        double val = min;
        double sum = max / size;
        for (int i = 0; i < size; i++) {
            data[i] = val;
            val += sum;
        }
        return data;
    }

    /**
     * Transform an array of double into a list of numerical features.
     *
     * @param inputs an array of double numbers
     * @return a list of numerical features
     */
    public static List<Feature> doublesToFeatures(double[] inputs) {
        return DoubleStream.of(inputs).mapToObj(DataUtils::doubleToFeature).collect(Collectors.toList());
    }

    /**
     * Transform a double into a numerical feature.
     *
     * @param d the double value
     * @return a numerical feature
     */
    static Feature doubleToFeature(double d) {
        return FeatureFactory.newNumericalFeature(String.valueOf(d), d);
    }

    /**
     * Perform perturbations on a fixed number of features in the given input.
     * Which feature will be perturbed is non deterministic.
     *
     * @param input               the input whose features need to be perturbed
     * @param perturbationContext the perturbation context
     * @return a new input with perturbed features
     */
    public static PredictionInput perturbFeatures(PredictionInput input, PerturbationContext perturbationContext) {
        List<Feature> originalFeatures = input.getFeatures();
        List<Feature> newFeatures = new ArrayList<>(originalFeatures);
        PredictionInput perturbedInput = new PredictionInput(newFeatures);
        int perturbationSize = Math.min(perturbationContext.getNoOfPerturbations(), originalFeatures.size());
        int[] indexesToBePerturbed = perturbationContext.getRandom().ints(0, perturbedInput.getFeatures().size()).distinct().limit(perturbationSize).toArray();
        for (int index : indexesToBePerturbed) {
            Feature feature = perturbedInput.getFeatures().get(index);
            Feature perturbedFeature = FeatureFactory.copyOf(feature, feature.getType().perturb(feature.getValue(), perturbationContext));
            perturbedInput.getFeatures().set(index, perturbedFeature);
        }
        return perturbedInput;
    }

    /**
     * Drop a given feature by a list of existing feature.
     *
     * @param features the existing features
     * @param target   the feature to drop
     * @return a new list of features having the target feature dropped
     */
    public static List<Feature> dropFeature(List<Feature> features, Feature target) {
        String name = target.getName();
        Value<?> value = target.getValue();
        Feature droppedFeature = null;
        for (Feature feature : features) {
            if (name.equals(feature.getName())) {
                if (value.equals(feature.getValue())) {
                    droppedFeature = FeatureFactory.copyOf(feature, feature.getType().drop(value));
                } else {
                    List<Feature> linearizedFeatures = DataUtils.getLinearizedFeatures(List.of(feature));
                    int i = 0;
                    for (Feature linearizedFeature : linearizedFeatures) {
                        if (value.equals(linearizedFeature.getValue())) {
                            Feature e = linearizedFeatures.get(i);
                            linearizedFeatures.set(i, FeatureFactory.copyOf(e, e.getType().drop(value)));
                            droppedFeature = FeatureFactory.newCompositeFeature(name, linearizedFeatures);
                            break;
                        } else {
                            i++;
                        }
                    }
                }
                break;
            }
        }
        List<Feature> copy = List.copyOf(features);
        if (droppedFeature != null) {
            Feature finalDroppedFeature = droppedFeature;
            copy = copy.stream().map(f -> f.getName().equals(finalDroppedFeature.getName()) ? finalDroppedFeature : f).collect(Collectors.toList());
        }
        return copy;
    }

    /**
     * Calculate the Hamming distance between two points.
     * <p>
     * see https://en.wikipedia.org/wiki/Hamming_distance
     *
     * @param x first point
     * @param y second point
     * @return the Hamming distance
     */
    public static double hammingDistance(double[] x, double[] y) {
        if (x.length != y.length) {
            return Double.NaN;
        } else {
            double h = 0d;
            for (int i = 0; i < x.length; i++) {
                if (x[i] != y[i]) {
                    h++;
                }
            }
            return h;
        }
    }

    /**
     * Calculate the Hamming distance between two text strings.
     * <p>
     * see https://en.wikipedia.org/wiki/Hamming_distance
     *
     * @param x first string
     * @param y second string
     * @return the Hamming distance
     */
    public static double hammingDistance(String x, String y) {
        if (x.length() != y.length()) {
            return Double.NaN;
        } else {
            double h = 0;
            for (int i = 0; i < x.length(); i++) {
                if (x.charAt(i) != y.charAt(i)) {
                    h++;
                }
            }
            return h;
        }
    }

    /**
     * Calculate the Euclidean distance between two points.
     *
     * @param x first point
     * @param y second point
     * @return the Euclidean distance
     */
    public static double euclideanDistance(double[] x, double[] y) {
        if (x.length != y.length) {
            return Double.NaN;
        } else {
            double e = 0;
            for (int i = 0; i < x.length; i++) {
                e += Math.pow(x[i] - y[i], 2);
            }
            return Math.sqrt(e);
        }
    }

    /**
     * Calculate the Gaussian kernel of a given value.
     *
     * @param x     Gaussian kernel input value
     * @param mu    mean
     * @param sigma variance
     * @return the Gaussian filtered value
     */
    public static double gaussianKernel(double x, double mu, double sigma) {
        return Math.exp(-Math.pow((x - mu) / sigma, 2) / 2) / (sigma * Math.sqrt(2d * Math.PI));
    }

    /**
     * Calculate exponentially smoothed kernel of a given value (e.g. distance between two points).
     *
     * @param x     value to smooth
     * @param width kernel width
     * @return the exponentially smoothed value
     */
    public static double exponentialSmoothingKernel(double x, double width) {
        return Math.sqrt(Math.exp(-(Math.pow(x, 2)) / Math.pow(width, 2)));
    }

    /**
     * Calculate distribution statistics for an array of numbers.
     *
     * @param doubles an array of numbers
     * @return feature distribution statistics
     */
    public static FeatureDistribution getFeatureDistribution(double[] doubles) {
        double min = DoubleStream.of(doubles).min().orElse(0);
        double max = DoubleStream.of(doubles).max().orElse(0);
        double mean = getMean(doubles);
        double stdDev = getStdDev(doubles, mean);
        return new FeatureDistribution(min, max, mean, stdDev);
    }

    /**
     * Generate a random data distribution.
     *
     * @param noOfFeatures     number of features
     * @param distributionSize number of samples for each feature
     * @return a data distribution
     */
    public static DataDistribution generateRandomDataDistribution(int noOfFeatures, int distributionSize, Random random) {
        List<FeatureDistribution> featureDistributions = new LinkedList<>();
        for (int i = 0; i < noOfFeatures; i++) {
            double[] doubles = generateData(random.nextDouble(), random.nextDouble(), distributionSize, random);
            FeatureDistribution featureDistribution = DataUtils.getFeatureDistribution(doubles);
            featureDistributions.add(featureDistribution);
        }
        return new DataDistribution(featureDistributions);
    }

    /**
     * Transform a list of prediction inputs into another list of the same prediction inputs but having linearized features.
     *
     * @param predictionInputs a list of prediction inputs
     * @return a list of prediction inputs with linearized features
     */
    public static List<PredictionInput> linearizeInputs(List<PredictionInput> predictionInputs) {
        List<PredictionInput> newInputs = new LinkedList<>();
        for (PredictionInput predictionInput : predictionInputs) {
            List<Feature> originalFeatures = predictionInput.getFeatures();
            List<Feature> flattenedFeatures = getLinearizedFeatures(originalFeatures);
            newInputs.add(new PredictionInput(flattenedFeatures));
        }
        return newInputs;
    }

    /**
     * Transform a list of eventually composite / nested features into a flat list of non composite / non nested features.
     *
     * @param originalFeatures a list of features
     * @return a flat list of features
     */
    public static List<Feature> getLinearizedFeatures(List<Feature> originalFeatures) {
        List<Feature> flattenedFeatures = new LinkedList<>();
        for (Feature f : originalFeatures) {
            linearizeFeature(flattenedFeatures, f);
        }
        return flattenedFeatures;
    }

    private static void linearizeFeature(List<Feature> flattenedFeatures, Feature f) {
        if (Type.UNDEFINED.equals(f.getType())) {
            if (f.getValue().getUnderlyingObject() instanceof Feature) {
                linearizeFeature(flattenedFeatures, (Feature) f.getValue().getUnderlyingObject());
            } else {
                flattenedFeatures.add(f);
            }
        } else if (Type.COMPOSITE.equals(f.getType())) {
            List<Feature> features = (List<Feature>) f.getValue().getUnderlyingObject();
            for (Feature feature : features) {
                linearizeFeature(flattenedFeatures, feature);
            }
        } else {
            flattenedFeatures.add(f);
        }
    }
}
