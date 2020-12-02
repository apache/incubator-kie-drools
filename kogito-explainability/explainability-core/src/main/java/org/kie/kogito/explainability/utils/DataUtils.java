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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.kie.kogito.explainability.model.DataDistribution;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureDistribution;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.IndependentFeaturesDataDistribution;
import org.kie.kogito.explainability.model.NumericFeatureDistribution;
import org.kie.kogito.explainability.model.PerturbationContext;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
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

        double generatedDataMean = getMean(data);
        double generatedDataStdDev = getStdDev(data, generatedDataMean);

        // force desired standard deviation
        double newStdDeviation = generatedDataStdDev != 0 ? stdDeviation / generatedDataStdDev : stdDeviation; // avoid division by zero
        for (int i = 0; i < size; i++) {
            data[i] *= newStdDeviation;
        }

        // get the new mean
        double newMean = generatedDataStdDev != 0 ? generatedDataMean * stdDeviation / generatedDataStdDev :
                generatedDataMean * stdDeviation;

        // force desired mean
        for (int i = 0; i < size; i++) {
            data[i] += mean - newMean;
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
     * @param originalFeatures    the input features that need to be perturbed
     * @param perturbationContext the perturbation context
     * @return a perturbed copy of the input features
     */
    public static List<Feature> perturbFeatures(List<Feature> originalFeatures, PerturbationContext perturbationContext) {
        List<Feature> newFeatures = new ArrayList<>(originalFeatures);
        if (!newFeatures.isEmpty()) {
            // perturb at most in the range [|features|/2), noOfPerturbations]
            int lowerBound = (int) Math.min(perturbationContext.getNoOfPerturbations(), 0.5d * newFeatures.size());
            int upperBound = (int) Math.max(perturbationContext.getNoOfPerturbations(), 0.5d * newFeatures.size());
            upperBound = Math.min(upperBound, newFeatures.size());
            lowerBound = Math.max(1, lowerBound); // lower bound should always be greater than zero (not ok to not perturb)
            int perturbationSize = 0;
            if (lowerBound == upperBound) {
                perturbationSize = lowerBound;
            }
            else if (upperBound > lowerBound) {
                perturbationSize = perturbationContext.getRandom().ints(1, lowerBound, upperBound).findFirst().orElse(1);
            }
            if (perturbationSize > 0) {
                int[] indexesToBePerturbed = perturbationContext.getRandom().ints(0, newFeatures.size())
                        .distinct().limit(perturbationSize).toArray();
                for (int index : indexesToBePerturbed) {
                    Feature feature = newFeatures.get(index);
                    Feature perturbedFeature = FeatureFactory.copyOf(feature, feature.getType().perturb(feature.getValue(), perturbationContext));
                    newFeatures.set(index, perturbedFeature);
                }
            }
        }
        return newFeatures;
    }

    /**
     * Drop a given feature from a list of existing features.
     *
     * @param features the existing features
     * @param target   the feature to drop
     * @return a new list of features having the target feature dropped
     */
    public static List<Feature> dropFeature(List<Feature> features, Feature target) {
        List<Feature> newList = new ArrayList<>(features.size());
        for (Feature sourceFeature : features) {
            String sourceFeatureName = sourceFeature.getName();
            Type sourceFeatureType = sourceFeature.getType();
            Value<?> sourceFeatureValue = sourceFeature.getValue();
            Feature f;
            if (target.getName().equals(sourceFeatureName)) {
                if (target.getType().equals(sourceFeatureType) && target.getValue().equals(sourceFeatureValue)) {
                    Value<?> droppedValue = sourceFeatureType.drop(sourceFeatureValue);
                    f = FeatureFactory.copyOf(sourceFeature, droppedValue);
                } else {
                    f = dropOnLinearizedFeatures(target, sourceFeature);
                }
            } else if (Type.COMPOSITE.equals(sourceFeatureType)) {
                List<Feature> nestedFeatures = (List<Feature>) sourceFeatureValue.getUnderlyingObject();
                f = FeatureFactory.newCompositeFeature(sourceFeatureName, dropFeature(nestedFeatures, target));
            } else {
                // not found
                f = FeatureFactory.copyOf(sourceFeature, sourceFeatureValue);
            }
            newList.add(f);
        }

        return newList;
    }

    /**
     * Drop a target feature from a "linearized" version of a source feature.
     * Any of such linearized features are eventually dropped if they match on associated name, type and value.
     *
     * @param target        the target feature
     * @param sourceFeature the source feature
     * @return the source feature having one of its underlying "linearized" values eventually dropped
     */
    protected static Feature dropOnLinearizedFeatures(Feature target, Feature sourceFeature) {
        Feature f = null;
        List<Feature> linearizedFeatures = DataUtils.getLinearizedFeatures(List.of(sourceFeature));
        int i = 0;
        for (Feature linearizedFeature : linearizedFeatures) {
            if (target.getValue().equals(linearizedFeature.getValue())) {
                linearizedFeatures.set(i, FeatureFactory.copyOf(linearizedFeature, linearizedFeature.getType().drop(target.getValue())));
                f = FeatureFactory.newCompositeFeature(target.getName(), linearizedFeatures);
                break;
            } else {
                i++;
            }
        }
        // not found
        if (f == null) {
            f = FeatureFactory.copyOf(sourceFeature, sourceFeature.getValue());
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
            Feature feature = FeatureFactory.newNumericalFeature("f_" + i, Double.NaN);
            FeatureDistribution featureDistribution = new NumericFeatureDistribution(feature, doubles);
            featureDistributions.add(featureDistribution);
        }
        return new IndependentFeaturesDataDistribution(featureDistributions);
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
            if (f.getValue().getUnderlyingObject() instanceof List) {
                List<Feature> features = (List<Feature>) f.getValue().getUnderlyingObject();
                for (Feature feature : features) {
                    linearizeFeature(flattenedFeatures, feature);
                }
            } else {
                flattenedFeatures.add(f);
            }
        } else {
            flattenedFeatures.add(f);
        }
    }

    /**
     * Build Predictions from PredictionInputs and PredictionOutputs.
     *
     * @param inputs prediction inputs
     * @param os     prediction outputs
     * @return a list of predictions
     */
    public static List<Prediction> getPredictions(List<PredictionInput> inputs, List<PredictionOutput> os) {
        return IntStream.range(0, os.size())
                .mapToObj(i -> new Prediction(inputs.get(i), os.get(i))).collect(Collectors.toList());
    }

    /**
     * Sample (with replacement) from a list of values.
     *
     * @param values     the list to sample from
     * @param sampleSize the no. of samples to draw
     * @param random     a random instance
     * @param <T>        the type of values to sample
     * @return a list of sampled values
     */
    public static <T> List<T> sampleWithReplacement(List<T> values, int sampleSize, Random random) {
        if (sampleSize <= 0 || values.isEmpty()) {
            return Collections.emptyList();
        } else {
            return random
                    .ints(sampleSize, 0, values.size())
                    .mapToObj(values::get)
                    .collect(Collectors.toList());
        }
    }
}
