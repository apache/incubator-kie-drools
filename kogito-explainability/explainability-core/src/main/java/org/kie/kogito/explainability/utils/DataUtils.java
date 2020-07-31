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

import java.net.URI;
import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.kie.kogito.explainability.model.DataDistribution;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureDistribution;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.Type;

/**
 * Utility methods to handle and manipulate data.
 */
public class DataUtils {

    private DataUtils() {}

    private static final Random random = new Random();

    public static void setSeed(long seed) {
        random.setSeed(seed);
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
    public static double[] generateData(double mean, double stdDeviation, int size) {

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
     * @param input             the input whose features need to be perturbed
     * @param noOfPerturbations the no. of features to be perturbed
     * @return a new input with perturbed features
     */
    public static PredictionInput perturbFeatures(PredictionInput input, int noOfPerturbations) {
        List<Feature> originalFeatures = input.getFeatures();
        List<Feature> newFeatures = new ArrayList<>(originalFeatures);
        PredictionInput perturbedInput = new PredictionInput(newFeatures);
        int perturbationSize = Math.min(noOfPerturbations, originalFeatures.size());
        int[] indexesToBePerturbed = random.ints(0, perturbedInput.getFeatures().size()).distinct().limit(perturbationSize).toArray();
        // TODO : perturbing a composite / nested feature must be done by considering to only perturb #noOfPerturbations features
        for (int value : indexesToBePerturbed) {
            perturbedInput.getFeatures().set(value, perturbFeature(
                    perturbedInput.getFeatures().get(value)));
        }
        return perturbedInput;
    }

    private static Feature perturbFeature(Feature feature) {
        Type type = feature.getType();
        Feature f;
        String featureName = feature.getName();
        switch (type) {
            case COMPOSITE:
                List<Feature> composite = (List<Feature>) feature.getValue().getUnderlyingObject();
                Map<String, Object> featuresMap = new HashMap<>();
                for (Feature cf : composite) {
                    if (random.nextBoolean()) {
                        featuresMap.put(cf.getName(), perturbFeature(cf));
                    } else {
                        featuresMap.put(cf.getName(), cf);
                    }
                }
                f = FeatureFactory.newCompositeFeature(featureName, featuresMap);
                break;
            case TEXT:
                String newStringValue = "";
                // randomly drop entire string or parts of it
                if (random.nextBoolean()) {
                    String stringValue = feature.getValue().asString();
                    if (stringValue.indexOf(' ') != -1) {
                        List<String> words = new ArrayList<>(Arrays.asList(stringValue.split(" ")));
                        if (!words.isEmpty()) {
                            int featuresToDrop = random.nextInt(Math.min(2, words.size() / 2));
                            for (int i = 0; i < 1 + featuresToDrop; i++) {
                                int dropIdx = random.nextInt(words.size());
                                words.remove(dropIdx);
                            }
                        }
                        newStringValue = String.join(" ", words);
                    }
                }
                f = FeatureFactory.newTextFeature(featureName, newStringValue);
                break;
            case NUMBER:
                double originalFeatureValue = feature.getValue().asNumber();
                boolean intValue = originalFeatureValue % 1 == 0;

                // sample from a standard normal distribution and center around feature value
                double normalDistributionSample = random.nextGaussian();
                if (originalFeatureValue != 0d) {
                    normalDistributionSample = normalDistributionSample * originalFeatureValue + originalFeatureValue;
                }
                if (intValue) {
                    normalDistributionSample = (int) normalDistributionSample;
                    if (normalDistributionSample == originalFeatureValue) {
                        normalDistributionSample = (int) normalDistributionSample * 10d;
                    }
                }
                f = FeatureFactory.newNumericalFeature(featureName, normalDistributionSample);
                break;
            case BOOLEAN:
                // flip the boolean value
                f = FeatureFactory.newBooleanFeature(featureName, !Boolean.getBoolean(feature.getValue().asString()));
                break;
            case TIME:
                LocalTime featureValue = (LocalTime) feature.getValue().getUnderlyingObject();
                f = FeatureFactory.newTimeFeature(featureName, featureValue.minusHours(random.nextInt(24)));
                break;
            case DURATION:
                // set the duration to 0
                f = FeatureFactory.newDurationFeature(featureName, Duration.of(0, ChronoUnit.SECONDS));
                break;
            case CURRENCY:
                // set the currency to machine default locale
                f = FeatureFactory.newCurrencyFeature(featureName, Currency.getInstance(Locale.getDefault()));
                break;
            case CATEGORICAL:
                String category = feature.getValue().asString();
                if (!"0".equals(category)) {
                    category = "0";
                } else {
                    category = "1";
                }
                f = FeatureFactory.newCategoricalFeature(featureName, category);
                break;
            case BINARY:
                // set an empty buffer
                ByteBuffer byteBuffer = ByteBuffer.allocate(0);
                f = FeatureFactory.newBinaryFeature(featureName, byteBuffer);
                break;
            case URI:
                // set an empty URI
                f = FeatureFactory.newURIFeature(featureName, URI.create(""));
                break;
            case VECTOR:
                // randomly set a non zero value to zero (or decrease it by 1)
                double[] values = feature.getValue().asVector();
                if (values.length > 1) {
                    int idx = random.nextInt(values.length - 1);
                    if (values[idx] != 0) {
                        values[idx] = 0;
                    } else {
                        values[idx]--;
                    }
                }
                f = FeatureFactory.newVectorFeature(featureName, values);
                break;
            case UNDEFINED:
                if (feature.getValue().getUnderlyingObject() instanceof Feature) {
                    f = perturbFeature((Feature) feature.getValue().getUnderlyingObject());
                } else {
                    f = feature;
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        return f;
    }

    /**
     * Drop the value of a given feature if its name matches any of the given names.
     *
     * @param feature an input feature
     * @param names   a list of feature names
     * @return the same feature or its copy with the 'dropped' value
     */
    public static Feature dropFeature(Feature feature, List<String> names) {
        Type type = feature.getType();
        Feature f = feature;
        String featureName = feature.getName();
        switch (type) {
            case COMPOSITE:
                List<Feature> composite = (List<Feature>) feature.getValue().getUnderlyingObject();
                Map<String, Object> featuresMap = new HashMap<>();
                for (Feature cf : composite) {
                    featuresMap.put(cf.getName(), dropFeature(cf, names));
                }
                f = FeatureFactory.newCompositeFeature(featureName, featuresMap);
                break;
            case TEXT:
                if (names.contains(featureName)) {
                    f = FeatureFactory.newTextFeature(featureName, "");
                } else {
                    String stringValue = feature.getValue().asString();
                    if (stringValue.indexOf(' ') != -1) {
                        List<String> words = new ArrayList<>(Arrays.asList(stringValue.split(" ")));
                        List<String> matchingWords = names.stream().map(n -> n.contains(" (") ? n.substring(0, n.indexOf(" (")) : "").filter(words::contains).collect(Collectors.toList());
                        if (words.removeAll(matchingWords)) {
                            stringValue = String.join(" ", words);
                        }
                    }
                    f = FeatureFactory.newTextFeature(featureName, stringValue);
                }
                break;
            case NUMBER:
                if (names.contains(featureName)) {
                    if (feature.getValue().asNumber() == 0) {
                        f = FeatureFactory.newNumericalFeature(featureName, Double.NaN);
                    } else {
                        f = FeatureFactory.newNumericalFeature(featureName, 0);
                    }
                }
                break;
            case BOOLEAN:
                if (names.contains(featureName)) {
                    f = FeatureFactory.newBooleanFeature(featureName, null);
                }
                break;
            case TIME:
                if (names.contains(featureName)) {
                    f = FeatureFactory.newTimeFeature(featureName, null);
                }
                break;
            case DURATION:
                if (names.contains(featureName)) {
                    f = FeatureFactory.newDurationFeature(featureName, null);
                }
                break;
            case CURRENCY:
                if (names.contains(featureName)) {
                    f = FeatureFactory.newCurrencyFeature(featureName, null);
                }
                break;
            case CATEGORICAL:
                if (names.contains(featureName)) {
                    f = FeatureFactory.newCategoricalFeature(featureName, "");
                }
                break;
            case BINARY:
                if (names.contains(featureName)) {
                    // set an empty buffer
                    ByteBuffer byteBuffer = ByteBuffer.allocate(0);
                    f = FeatureFactory.newBinaryFeature(featureName, byteBuffer);
                }
                break;
            case URI:
                if (names.contains(featureName)) {
                    // set an empty URI
                    f = FeatureFactory.newURIFeature(featureName, URI.create(""));
                }
                break;
            case VECTOR:
                if (names.contains(featureName)) {
                    double[] values = feature.getValue().asVector();
                    if (values.length > 0) {
                        Arrays.fill(values, 0);
                    }
                    f = FeatureFactory.newVectorFeature(featureName, values);
                }
                break;
            case UNDEFINED:
                if (feature.getValue().getUnderlyingObject() instanceof Feature) {
                    f = dropFeature((Feature) feature.getValue().getUnderlyingObject(), names);
                } else {
                    f = FeatureFactory.newObjectFeature(featureName, null);
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + type);
        }
        return f;
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
    public static DataDistribution generateRandomDataDistribution(int noOfFeatures, int distributionSize) {
        List<FeatureDistribution> featureDistributions = new LinkedList<>();
        for (int i = 0; i < noOfFeatures; i++) {
            double[] doubles = generateData(random.nextDouble(), random.nextDouble(), distributionSize);
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
            if (Type.TEXT.equals(f.getType())) {
                for (String w : f.getValue().asString().split(" ")) {
                    Feature outputFeature = FeatureFactory.newTextFeature(w + " (" + f.getName() + ")", w);
                    flattenedFeatures.add(outputFeature);
                }
            } else {
                flattenedFeatures.add(f);
            }
        }
    }
}
