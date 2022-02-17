/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.explainability.local.shap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.math3.exception.MathArithmeticException;
import org.apache.commons.math3.linear.AnyMatrix;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.kie.kogito.explainability.local.LocalExplainer;
import org.kie.kogito.explainability.model.FeatureImportance;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Saliency;
import org.kie.kogito.explainability.utils.LarsPath;
import org.kie.kogito.explainability.utils.LassoLarsIC;
import org.kie.kogito.explainability.utils.MatrixUtilsExtensions;
import org.kie.kogito.explainability.utils.RandomChoice;
import org.kie.kogito.explainability.utils.WeightedLinearRegression;
import org.kie.kogito.explainability.utils.WeightedLinearRegressionResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the SHAP kernel explainer as per
 * https://proceedings.neurips.cc/paper/2017/file/8a20a8621978632d76c43dfd28b67767-Paper.pdf
 * see also https://github.com/slundberg/shap/blob/master/shap/explainers/_kernel.py
 */
public class ShapKernelExplainer implements LocalExplainer<ShapResults> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShapKernelExplainer.class);
    private ShapConfig config;

    public ShapKernelExplainer(ShapConfig shapConfig) {
        this.config = shapConfig;
    }

    /**
     * Update the configuration of SHAP
     *
     * @param shapConfig: The new ShapConfig for SHAP
     */
    public void setConfig(ShapConfig shapConfig) {
        this.config = shapConfig;
    }

    private ShapDataCarrier initialize(PredictionProvider model) {
        // get shapes of input and output data
        int rows = this.config.getBackgroundMatrix().getRowDimension();
        int cols = this.config.getBackgroundMatrix().getColumnDimension();

        if (rows > 100) {
            LOGGER.debug("Warning: Background data sets larger than 100 samples might be slow!");
        }

        // establish background data
        CompletableFuture<RealMatrix> modelNull = model.predictAsync(config.getBackground())
                .thenApply(MatrixUtilsExtensions::matrixFromPredictionOutput);
        CompletableFuture<Integer> outputSize = modelNull.thenApply(AnyMatrix::getColumnDimension);

        //compute the mean of each column
        CompletableFuture<RealVector> fnull = modelNull.thenApply(mn -> MatrixUtilsExtensions.rowSum(mn).mapDivide(rows));
        CompletableFuture<RealVector> linkNull = fnull.thenApply(this::link);

        // track number of samples
        int numSamples = this.config.getNSamples().orElseGet(() -> 2048 + (2 * cols));

        // lower number of samples if it's greater than total feature permutation size
        if (cols <= 30) {
            int maxSamples = (int) Math.pow(2, cols) - 2;
            if (maxSamples < numSamples) {
                numSamples = maxSamples;
            }
        }

        ShapDataCarrier sdc = new ShapDataCarrier();

        // add data statistics to data carrier
        sdc.setRows(rows);
        sdc.setCols(cols);
        sdc.setOutputSize(outputSize);

        // add model data
        sdc.setModel(model);
        sdc.setFnull(fnull);
        sdc.setLinkNull(linkNull);

        // add shap run configuration data
        sdc.setNumSamples(numSamples);

        return sdc;
    }

    /**
     * The link function calculation for the explanation.
     * If link is IDENTITY: link: f(x) = x
     * else: link: f(x) = logit(x)
     *
     * @param x: the input to the link function
     *
     * @return link(x)
     */
    private double link(double x) {
        if (this.config.getLink().equals(ShapConfig.LinkType.IDENTITY)) {
            return x;
        } else {
            return Math.log(x / (1 - x));
        }
    }

    /**
     * Vector version of the link function
     *
     * @param v: the input to the link function
     *
     * @return link(v)
     */
    private RealVector link(RealVector v) {
        return v.map(this::link);
    }

    /**
     * Determine which features vary across the background data and this particular input.
     * If the feature has one value across all background data points and the input, it does not vary.
     *
     * @param input: The PredictionInput to look for variance with, in conjunction with the background data
     */
    private void setVaryingFeatureGroups(PredictionInput input, ShapDataCarrier sdc) {
        List<Integer> varyingFeatureGroups = new ArrayList<>();
        RealVector inputVector = MatrixUtilsExtensions.vectorFromPredictionInput(input);
        RealVector columnFeatures = MatrixUtils.createRealVector(new double[sdc.getRows() + 1]);
        for (int col = 0; col < sdc.getCols(); col++) {
            columnFeatures.setSubVector(0, this.config.getBackgroundMatrix().getColumnVector(col));
            columnFeatures.setEntry(sdc.getRows(), inputVector.getEntry(col));
            long uniques = Arrays.stream(columnFeatures.toArray()).distinct().count();
            if (uniques > 1) {
                varyingFeatureGroups.add(col);
            }
        }
        sdc.setVaryingFeatureGroups(varyingFeatureGroups);
        sdc.setNumVarying(varyingFeatureGroups.size());
    }

    /**
     * Normalize the weight vector to sum to 1.
     *
     * @param v: The vector to be normalized.
     *
     * @return The normalized vector
     */
    private RealVector normalizeWeightVector(RealVector v) {
        try {
            return v.mapDivide(MatrixUtilsExtensions.sum(v));
        } catch (MathArithmeticException e) {
            return v;
        }
    }

    /**
     * Add a sample to the WLRR computation
     *
     * @param pi: The specific input to generated the synthetic data sample
     * @param combination: The combination of features to include from the input
     * @param weight: The weight of this sample
     * @param inverse: Flag to add the complement of the specified sample instead of the specified sample.
     * @param fixed: Is this sample from a fully enumerated subset? If so, the final weight for the
     *        sample is known at creation time, and does not need to be changed. Otherwise, we'll
     *        need to readjust the given weight after we randomly choose samples.
     * @return boolean, whether a sample was succesfully added or whether it collided with existing sample hashes
     */
    private boolean addSample(PredictionInput pi, List<Integer> combination, double weight,
            boolean inverse, boolean fixed, ShapDataCarrier sdc) {
        boolean[] mask = new boolean[sdc.getCols()];
        if (inverse) {
            for (int i = 0; i < sdc.getNumVarying(); i++) {
                mask[sdc.getVaryingFeatureGroups(i)] = true;
            }
        }

        for (Integer i : combination) {
            mask[sdc.getVaryingFeatureGroups(i)] = !inverse;
        }
        int maskHash = this.hashMask(mask);
        if (sdc.getMasksUsed().containsKey(maskHash)) {
            ShapSyntheticDataSample previousSample = sdc.getSamplesAdded(sdc.getMasksUsed(maskHash));
            previousSample.incrementWeight();
            return false;
        } else {
            ShapSyntheticDataSample sample = new ShapSyntheticDataSample(pi, mask, this.config.getBackgroundMatrix(), weight, fixed);
            // map index in the samplesAdded list to the unique hash of this mask
            sdc.addMask(maskHash, sdc.getSamplesAddedSize());
            sdc.addSample(sample);
            return true;
        }
    }

    /**
     * Hash a boolean mask array. Hashing is done by treating the boolean array of size n
     * as an n-digit binary number, and then computing the base-10 integer represented by this binary.
     *
     * @param mask: The boolean mask to be hashed.
     *
     * @return the mask hash
     */
    private int hashMask(boolean[] mask) {
        int maskSize = mask.length;
        int hash = 0;
        for (int i = 0; i < maskSize; i++) {
            hash += mask[i] ? Math.pow(2, (maskSize - i - 1)) : 0;
        }
        return hash;
    }

    /**
     * Given an n x m matrix of n outputs and m feature importances, return an array of Saliencies
     *
     * @param m: The n x m matrix
     * @param pi: The prediction input
     * @param po: The prediction output
     *
     * @return an array of n saliencies, one for each output of the model. Each Saliency lists the feature
     *         importances of each input feature to that particular output
     */
    public static Saliency[] saliencyFromMatrix(RealMatrix m, PredictionInput pi, PredictionOutput po) {
        Saliency[] saliencies = new Saliency[m.getRowDimension()];
        for (int i = 0; i < m.getRowDimension(); i++) {
            List<FeatureImportance> fis = new ArrayList<>();
            for (int j = 0; j < m.getColumnDimension(); j++) {
                fis.add(new FeatureImportance(pi.getFeatures().get(j), m.getEntry(i, j)));
            }
            saliencies[i] = new Saliency(po.getOutputs().get(i), fis);
        }
        return saliencies;
    }

    /**
     * Given an n x m matrix of feature importances and an nxm matrix of confidences, return an array of Saliencies
     *
     * @param m: The n x m matrix
     * @param bounds: The n x m matrix of confidences
     * @param pi: The prediction input
     * @param po: The prediction output
     *
     * @return an array of n saliencies, one for each output of the model. Each Saliency lists the feature
     *         importances and confidences of each input feature to that particular output
     */
    public static Saliency[] saliencyFromMatrix(RealMatrix m, RealMatrix bounds, PredictionInput pi, PredictionOutput po) {
        Saliency[] saliencies = new Saliency[m.getRowDimension()];
        for (int i = 0; i < m.getRowDimension(); i++) {
            List<FeatureImportance> fis = new ArrayList<>();
            for (int j = 0; j < m.getColumnDimension(); j++) {
                fis.add(new FeatureImportance(pi.getFeatures().get(j), m.getEntry(i, j), bounds.getEntry(i, j)));
            }
            saliencies[i] = new Saliency(po.getOutputs().get(i), fis);
        }
        return saliencies;
    }

    /**
     * Compute the shap values for a specific prediction
     *
     * @param prediction: The ShapPrediction to be explained.
     * @param model: The PredictionProvider we are explaining.
     *
     * @return the shap values for this prediction, of shape [n_model_outputs x n_features]
     */
    private CompletableFuture<ShapResults> explain(Prediction prediction, PredictionProvider model) {
        ShapDataCarrier sdc = this.initialize(model);
        sdc.setSamplesAdded(new ArrayList<>());
        PredictionInput pi = prediction.getInput();
        PredictionOutput po = prediction.getOutput();

        if (pi.getFeatures().size() != sdc.getCols()) {
            throw new IllegalArgumentException(String.format(
                    "Prediction input feature count (%d) does not match background data feature count (%d)",
                    pi.getFeatures().size(), sdc.getCols()));
        }

        int cols = sdc.getCols();
        CompletableFuture<RealMatrix> output = sdc.getOutputSize().thenApply(os -> {
            if (po.getOutputs().size() != os) {
                throw new IllegalArgumentException(String.format(
                        "Prediction output size (%d) does not match background data output size (%d)",
                        po.getOutputs().size(), os));
            }
            return MatrixUtils.createRealMatrix(new double[os][cols]);
        });

        RealVector poVector = MatrixUtilsExtensions.vectorFromPredictionOutput(po);

        //first find varying features
        this.setVaryingFeatureGroups(pi, sdc);

        // if no features vary, then the features do not effect output, and all shap values are zero.
        if (sdc.getNumVarying() == 0) {
            return output.thenApply(o -> saliencyFromMatrix(o, pi, po)).thenCombine(sdc.getFnull(), ShapResults::new);
        } else if (sdc.getNumVarying() == 1)
        // if 1 feature varies, this feature has all the effect
        {
            CompletableFuture<RealVector> diff = sdc.getLinkNull().thenApply(poVector::subtract);
            return output.thenCompose(o -> diff.thenCombine(sdc.getOutputSize(), (df, os) -> {
                RealMatrix out = MatrixUtils.createRealMatrix(new double[os][cols]);
                for (int i = 0; i < os; i++) {
                    out.setEntry(i, sdc.getVaryingFeatureGroups(0), df.getEntry(i));
                }
                return saliencyFromMatrix(out, pi, po);
            })).thenCombine(sdc.getFnull(), ShapResults::new);
        } else
        // if more than 1 feature varies, we need to perform WLR
        {
            // establish sizes of feature permutations (called subsets)
            ShapStatistics shapStats = this.computeSubsetStatistics(sdc);

            // weight each subset by number of features
            this.initializeWeights(shapStats, sdc);

            // add all fully enumerated subsets
            this.addCompleteSubsets(shapStats, pi, sdc);

            // renormalize weights after full subsets have been added
            this.renormalizeWeights(shapStats);

            // sample non-fully enumerated subsets
            this.addNonCompleteSubsets(shapStats, pi, sdc);

            // run the synthetic data generated through the model
            CompletableFuture<RealMatrix> expectations = this.runSyntheticData(sdc);

            // run the wlr model over the synthetic data results
            return output.thenCompose(o -> this.solveSystem(expectations, poVector, sdc)
                    .thenApply(wo -> saliencyFromMatrix(wo[0], wo[1], pi, po)))
                    .thenCombine(sdc.getFnull(), ShapResults::new);
        }
    }

    /**
     * Create a shap statistics object for this explanation, given the configuration for this explainer.
     *
     * @return ShapStatics object
     */
    private ShapStatistics computeSubsetStatistics(ShapDataCarrier sdc) {
        // the size of the range (0, numVarying//2) is the number of possible feature permutations
        // above numVarying//2 all sets are complements of earlier sets
        int numSubsetSizes = (int) Math.ceil((sdc.getNumVarying() - 1) / 2.);

        // how many of those subsets have a complement set? If numVarying is even, all. If odd, all but the "middle" set
        int largestPairedSubsetSize = sdc.getNumVarying() % 2 == 1 ? numSubsetSizes : numSubsetSizes - 1;

        // compute the number of subsets at each size
        // this can be a potentially colossal number.
        // If we get an arithmetic error, truncate the result to numSamples^2
        int[] numSubsetsAtSize = new int[numSubsetSizes + 1];
        for (int i = 1; i < numSubsetSizes + 1; i++) {
            try {
                numSubsetsAtSize[i] = (int) CombinatoricsUtils.binomialCoefficient(sdc.getNumVarying(), i);
            } catch (MathArithmeticException e) {
                numSubsetsAtSize[i] = sdc.getNumSamples() * sdc.getNumSamples();
            }
        }

        int numSamplesRemaining = sdc.getNumSamples();
        return new ShapStatistics(numSubsetSizes, largestPairedSubsetSize, numSubsetsAtSize, numSamplesRemaining);
    }

    /**
     * Set the weights for each subset size. This is computed based on the total number of varying features,
     * the number of permutations with $n features, and whether there is a complement set to this subset
     *
     * @param shapStats: The ShapStatistics object for this explanation
     */
    private void initializeWeights(ShapStatistics shapStats, ShapDataCarrier sdc) {
        // compute the weighting for a subset of a particular size
        double[] rawWeights = new double[shapStats.getNumSubsetSizes() + 1];
        for (int subsetSize = 1; subsetSize <= shapStats.getNumSubsetSizes(); subsetSize++) {
            double weight = (sdc.getNumVarying() - 1.) / (subsetSize * (sdc.getNumVarying() - subsetSize));
            // the inverse subset has the same weight
            if (subsetSize <= shapStats.getLargestPairedSubsetSize()) {
                weight *= 2;
            }
            rawWeights[subsetSize] = weight;
        }
        RealVector weightOfSubsetSize = normalizeWeightVector(MatrixUtils.createRealVector(rawWeights));
        shapStats.setWeightOfSubsetSize(weightOfSubsetSize);
        shapStats.setRemainingWeights(weightOfSubsetSize.copy());
    }

    /**
     * For every subset that we can fully evaluate (ie, subsetSize <= samplesRemaining), add all samples
     * from the subset
     *
     * @param shapStats: The ShapStatistics object for this explanation
     * @param pi: The PredictionInput for this explanation
     */
    private void addCompleteSubsets(ShapStatistics shapStats, PredictionInput pi, ShapDataCarrier sdc) {
        sdc.setMasksUsed(new HashMap<>());

        // fill out all subsets that can be completely filled
        for (int subsetSize = 1; subsetSize < shapStats.getNumSubsetSizes() + 1; subsetSize++) {
            // get n subsets at particular size
            int numSubsets = shapStats.getNumSubsetsAtSize()[subsetSize];

            // if inverse exists, double size
            numSubsets *= subsetSize <= shapStats.getLargestPairedSubsetSize() ? 2 : 1;
            double samplingWeight = shapStats.getRemainingWeights().getEntry(subsetSize);
            // if we have enough samples for the entirety of this subset:
            if (shapStats.getNumSamplesRemaining() * samplingWeight >= numSubsets) {
                shapStats.incrementNumFullSubsets();
                shapStats.decreaseNumSamplesRemainingBy(numSubsets);
                RealVector remainingWeights = shapStats.getRemainingWeights();
                remainingWeights.setEntry(subsetSize, 0);
                shapStats.setRemainingWeights(normalizeWeightVector(remainingWeights));

                Iterator<int[]> combinations = CombinatoricsUtils.combinationsIterator(sdc.getNumVarying(), subsetSize);
                double individualWeight = shapStats.getWeightOfSubsetSize().getEntry(subsetSize) / numSubsets;
                while (combinations.hasNext()) {
                    List<Integer> combination = Arrays.stream(combinations.next()).boxed().collect(Collectors.toList());
                    addSample(pi, combination, individualWeight, false, true, sdc);
                    if (subsetSize <= shapStats.getLargestPairedSubsetSize()) {
                        addSample(pi, combination, individualWeight, true, true, sdc);
                    }
                }
            } else {
                break;
            }
        }
    }

    /**
     * Renormalize the remaining weights such that the weight vector for all non-enumerated subsets
     * sums to 1.
     *
     * @param shapStats: The ShapStatistics object for this explanation
     */
    private void renormalizeWeights(ShapStatistics shapStats) {
        //grab another copy of the normalized weights
        RealVector weightOfSubsetSize = shapStats.getWeightOfSubsetSize();
        RealVector remainingWeights = weightOfSubsetSize.copy();
        RealVector divisor = MatrixUtils.createRealVector(
                IntStream.range(0, remainingWeights.getDimension())
                        .mapToDouble(i -> i < shapStats.getLargestPairedSubsetSize() ? 2. : 1.)
                        .toArray());
        remainingWeights.ebeDivide(divisor);
        int nToGrab = shapStats.getNumSubsetSizes() - shapStats.getNumFullSubsets();
        RealVector nonFullRemainingWeights = remainingWeights.getSubVector(shapStats.getNumFullSubsets() + 1, nToGrab);
        shapStats.setFinalRemainingWeights(this.normalizeWeightVector(nonFullRemainingWeights));
    }

    /**
     * For every subset that we cannot fully evaluate (ie, subsetSize > samplesRemaining), grab a
     * random amount of subset samples, weighted by the subset weight
     *
     * @param shapStats: The ShapStatistics object for this explanation
     * @param pi: The PredictionInput for this explanation
     */
    private void addNonCompleteSubsets(ShapStatistics shapStats, PredictionInput pi, ShapDataCarrier sdc) {
        if (shapStats.getNumFullSubsets() < shapStats.getNumSubsetSizes()) {
            // draw a bunch of random samples from remaining subsets
            List<Integer> subsetSizesRemaining = IntStream
                    .range(shapStats.getNumFullSubsets() + 1, shapStats.getNumSubsetSizes() + 1)
                    .boxed()
                    .collect(Collectors.toList());

            List<Double> subsetSizeWeights = Arrays.stream(shapStats.getFinalRemainingWeights().toArray())
                    .boxed()
                    .collect(Collectors.toList());

            RandomChoice<Integer> subsetSampler = new RandomChoice<>(subsetSizesRemaining, subsetSizeWeights);
            List<Integer> sizeSamples = subsetSampler.sample(shapStats.getNumSamplesRemaining() * 4,
                    this.config.getPC().getRandom());
            List<Integer> maskSizes = IntStream.range(0, sdc.getNumVarying()).boxed().collect(Collectors.toList());

            int sampleIdx = 0;
            while (shapStats.getNumSamplesRemaining() > 0) {
                if (sampleIdx >= sizeSamples.size()) {
                    sizeSamples = subsetSampler.sample(shapStats.getNumSamplesRemaining() * 4,
                            this.config.getPC().getRandom());
                    sampleIdx = 0;
                }
                int subsetSize = sizeSamples.get(sampleIdx);
                sampleIdx += 1;
                Collections.shuffle(maskSizes);
                List<Integer> maskIdxs = maskSizes.subList(0, subsetSize);
                if (this.addSample(pi, maskIdxs, 1., false, false, sdc)) {
                    shapStats.decreaseNumSamplesRemainingBy(1);
                }

                // add compliment if possible
                if ((shapStats.getNumSamplesRemaining() > 0 && subsetSize <= shapStats.getLargestPairedSubsetSize()) &&
                        (this.addSample(pi, maskIdxs, 1., true, false, sdc))) {
                    shapStats.decreaseNumSamplesRemainingBy(1);
                }
            }
            this.normalizeSampleWeights(shapStats, sdc);
        }
    }

    /**
     * For the non-fully-enumerated subsets, readjust the sample weighting based on the number
     * of randomly chosen samples at each subset size. This is necessary because the sample weighting
     * depends on the number of samples at a particular subset size, which can only be known after
     * we have randomly chosen these samples.
     *
     * @param shapStats: The ShapStatistics object for this explanation
     */
    private void normalizeSampleWeights(ShapStatistics shapStats, ShapDataCarrier sdc) {
        double nonFullWeight = MatrixUtilsExtensions.sum(
                shapStats.getWeightOfSubsetSize()
                        .getSubVector(shapStats.getNumFullSubsets(), shapStats.getNumSubsetSizes() - shapStats.getNumFullSubsets()));

        double nonFixedWeight = 0.;
        for (int i = 0; i < sdc.getSamplesAddedSize(); i++) {
            if (!sdc.getSamplesAdded(i).isFixed()) {
                nonFixedWeight += sdc.getSamplesAdded(i).getWeight();
            }
        }

        for (int i = 0; i < sdc.getSamplesAddedSize(); i++) {
            ShapSyntheticDataSample sample = sdc.getSamplesAdded(i);
            if (!sample.isFixed() && nonFixedWeight != 0) {
                sample.setWeight(sample.getWeight() * nonFullWeight / nonFixedWeight);
            }
        }
    }

    /**
     * Pass the synthetic data samples through the model. For each sample, the expectation is
     * the mean output of the model over the entirely of that sample's synthetic data, subtracted
     * from the mean output of the model over the background data.
     *
     * @return the expectations of the model over the synthetic data, of shape [nsamples x modelOutputSize]
     */

    private CompletableFuture<RealMatrix> runSyntheticData(ShapDataCarrier sdc) {
        return sdc.getLinkNull().thenCompose(ln -> sdc.getOutputSize().thenCompose(os -> {
            HashMap<Integer, CompletableFuture<RealVector>> expectationSlices = new HashMap<>();

            //in theory all of these can happen in parallel
            for (int i = 0; i < sdc.getSamplesAddedSize(); i++) {
                List<PredictionInput> pis = sdc.getSamplesAdded(i).getSyntheticData();
                expectationSlices.put(i,
                        sdc.getModel().predictAsync(pis)
                                .thenApply(MatrixUtilsExtensions::matrixFromPredictionOutput)
                                .thenApply(posMatrix -> MatrixUtilsExtensions.rowSum(posMatrix).mapDivide(posMatrix.getRowDimension()))
                                .thenApply(this::link)
                                .thenApply(x -> x.subtract(ln)));
            }

            // reduce parallel operations into single array
            final CompletableFuture<RealMatrix>[] expectations = new CompletableFuture[] {
                    CompletableFuture.supplyAsync(() -> MatrixUtils.createRealMatrix(
                            new double[sdc.getSamplesAddedSize()][os]),
                            this.config.getExecutor()) };
            expectationSlices.forEach((idx, slice) -> expectations[0] = expectations[0].thenCompose(
                    e -> slice.thenApply(s -> {
                        e.setRowVector(idx, s);
                        return e;
                    })));
            return expectations[0];
        }));
    }

    /**
     * Apply the specified regularization technique
     *
     * @param augX: The augmented mask matrix used as input to the regularizers
     * @param augY: The augmented observation vector used as input to regularizer
     *
     * @return A List of features selected by the regularizer to be used in the regression
     */
    private List<Integer> getRegularizationIndexes(RealMatrix augX, RealVector augY) {
        List<Integer> nonzeros = List.of();
        switch (this.config.getRegularizerType()) {
            case AUTO:
            case AIC:
                nonzeros = MatrixUtilsExtensions.nonzero(
                        LassoLarsIC.fit(augX, augY, LassoLarsIC.Criterion.AIC).getCoefs());
                break;
            case BIC:
                nonzeros = MatrixUtilsExtensions.nonzero(
                        LassoLarsIC.fit(augX, augY, LassoLarsIC.Criterion.BIC).getCoefs());
                break;
            case TOP_N_FEATURES:
                nonzeros = LarsPath.fit(augX, augY, this.config.getNRegularizationFeatures(), false)
                        .getActive();
                break;
            case NONE:
                throw new IllegalArgumentException("RegularizerType=NONE will never be able enter the switch statement");
        }
        return nonzeros;
    }

    /**
     * Create a WLR model to retrieve the SHAP values for a particular output of the model
     *
     * @param expectations: The expectations of each sample
     * @param output: The index of the particular output
     * @param poVector: The predictionOutputs for this explanation's prediction
     * @param fnull: The value stored in the CompletableFuture this.fnull
     *
     * @return the shap values as found by the WLR
     */
    private RealVector[] solve(RealMatrix expectations, int output, RealVector poVector, RealVector fnull,
            ShapDataCarrier sdc) {
        RealMatrix xs = MatrixUtils.createRealMatrix(new double[sdc.getSamplesAddedSize()][sdc.getCols()]);
        RealVector ws = MatrixUtils.createRealVector(new double[sdc.getSamplesAddedSize()]);
        RealVector ys = MatrixUtils.createRealVector(new double[sdc.getSamplesAddedSize()]);

        for (int i = 0; i < sdc.getSamplesAddedSize(); i++) {
            for (int j = 0; j < sdc.getCols(); j++) {
                xs.setEntry(i, j, sdc.getSamplesAdded(i).getMask()[j] ? 1. : 0.);
            }
            ys.setEntry(i, expectations.getEntry(i, output));
            ws.setEntry(i, sdc.getSamplesAdded(i).getWeight());
        }

        double sampleFraction = sdc.getSamplesAddedSize() / Math.pow(2, sdc.getCols());
        double outputChange = this.link(poVector.getEntry(output)) - this.link(fnull.getEntry(output));

        List<Integer> nonzeros;
        boolean autoRegularize = sampleFraction < .2 && config.getRegularizerType() == ShapConfig.RegularizerType.AUTO;
        boolean specificRegularize = config.getRegularizerType() != ShapConfig.RegularizerType.NONE && config.getRegularizerType() != ShapConfig.RegularizerType.AUTO;
        if (autoRegularize || specificRegularize) {
            // perform augmentation
            RealVector maskSum = MatrixUtilsExtensions.colSum(xs);

            // augment weights
            RealVector augWeights = MatrixUtils.createRealVector(new double[ws.getDimension() * 2]);
            augWeights.setSubVector(0, ws.ebeMultiply(maskSum.map(x -> sdc.getNumVarying() - x)));
            augWeights.setSubVector(ws.getDimension(), ws.ebeMultiply(maskSum));
            RealVector sqrtAugWeights = augWeights.map(Math::sqrt);

            // augment ys
            RealVector augYs = MatrixUtils.createRealVector(new double[ys.getDimension() * 2]);
            augYs.setSubVector(0, ys);
            augYs.setSubVector(ys.getDimension(), ys.mapSubtract(outputChange));
            augYs = augYs.ebeMultiply(sqrtAugWeights);

            // augment xs
            RealMatrix augXsRaw = MatrixUtils.createRealMatrix(xs.getRowDimension() * 2, xs.getColumnDimension());
            augXsRaw.setSubMatrix(xs.getData(), 0, 0);
            augXsRaw.setSubMatrix(MatrixUtilsExtensions.map(xs, x -> x - 1).getData(), xs.getRowDimension(), 0);
            RealMatrix augXs = MatrixUtilsExtensions.vectorRowProduct(augXsRaw.transpose(), sqrtAugWeights).transpose();

            nonzeros = getRegularizationIndexes(augXs, augYs);
        } else {
            nonzeros = sdc.getVaryingFeatureGroups();
        }

        // select features for regularization as specified by nonzeros
        int dropIdx = nonzeros.get(nonzeros.size() - 1);
        RealVector dropMask = xs.getColumnVector(dropIdx);
        RealVector dropEffect = dropMask.mapMultiply(outputChange);
        RealVector adjY = ys.subtract(dropEffect);
        List<Integer> allNZButLast = nonzeros.subList(0, nonzeros.size() - 1);
        RealMatrix xsAdj = MatrixUtilsExtensions.vectorDifference(
                MatrixUtilsExtensions.getCols(xs, allNZButLast),
                dropMask,
                MatrixUtilsExtensions.Axis.COLUMN);

        // run the regression
        return this.runWLRR(xsAdj, adjY, ws, outputChange, dropIdx, nonzeros, sdc);
    }

    /**
     * Run WLRRs in parallel, with each parallel thread computing the shap values for a particular output of the model
     *
     * @param expectations: The expectations of each sample
     * @param poVector: The predictionOutputs for this explanation's prediction
     *
     * @return the shap values as found by the WLR
     */
    private CompletableFuture<RealMatrix[]> solveSystem(CompletableFuture<RealMatrix> expectations, RealVector poVector,
            ShapDataCarrier sdc) {
        return expectations.thenCompose(exps -> sdc.getFnull().thenCompose(fn -> sdc.getOutputSize().thenCompose(os -> {
            HashMap<Integer, CompletableFuture<RealVector[]>> shapSlices = new HashMap<>();
            for (int output = 0; output < os; output++) {
                int finalOutput = output;
                shapSlices.put(output, CompletableFuture.supplyAsync(
                        () -> solve(exps, finalOutput, poVector, fn, sdc),
                        this.config.getExecutor()));
            }

            // reduce parallel operations into single array
            RealMatrix outputMatrix = MatrixUtils.createRealMatrix(new double[os][sdc.getCols()]);
            final CompletableFuture<RealMatrix[]>[] shapVals = new CompletableFuture[] {
                    CompletableFuture.supplyAsync(
                            () -> new RealMatrix[] { outputMatrix.copy(), outputMatrix.copy() },
                            this.config.getExecutor()) };
            shapSlices.forEach((idx, slice) -> shapVals[0] = shapVals[0].thenCompose(e -> slice.thenApply(s -> {
                // store shap values in first matrix
                e[0].setRowVector(idx, s[0]);
                // shap value confidences go in the second
                e[1].setRowVector(idx, s[1]);
                return e;
            })));
            return shapVals[0];
        })));
    }

    /**
     * Run the WLR model over the expectations.
     *
     * @param maskDiff: The mask matrix, not including the regularization feature
     * @param adjY: The expected model outputs, adjusted for dropping the regularization feature
     * @param ws: The weights of each sample
     * @param outputChange: The raw difference between the model output and the null output
     * @param dropIdx: The regularization feature index
     *
     * @return a 2xnFeatures array, containing the shap values as found by the WLR in the first row and the
     *         confidences of those values in the second row.
     */
    // run the WLRR for a single output
    private RealVector[] runWLRR(RealMatrix maskDiff, RealVector adjY, RealVector ws, double outputChange,
            int dropIdx, List<Integer> nonzeros, ShapDataCarrier sdc) {

        // temporary conversion to and from MAtrixUtils data structures; these will be used throughout after FAI-661
        WeightedLinearRegressionResults wlrr = WeightedLinearRegression.fit(maskDiff, adjY, ws, false);
        RealVector coeffs = wlrr.getCoefficients();
        RealVector bounds = wlrr.getConf(1 - this.config.getConfidence());

        int usedCoefs = 0;
        RealVector shapSlice = MatrixUtils.createRealVector(new double[sdc.getCols()]);
        RealVector boundsReg = shapSlice.copy();
        for (int idx : nonzeros) {
            if (idx != dropIdx) {
                shapSlice.setEntry(idx, coeffs.getEntry(usedCoefs));
                boundsReg.setEntry(idx, bounds.getEntry(usedCoefs));
                usedCoefs += 1;
            }
        }
        shapSlice.setEntry(dropIdx, outputChange - MatrixUtilsExtensions.sum(coeffs));
        //propagate the error of sum
        boundsReg.setEntry(dropIdx, Math.sqrt(MatrixUtilsExtensions.sum(bounds.map(x -> x * x))));

        // bundle error and shap values together
        RealVector[] wlrrOutput = new RealVector[2];
        wlrrOutput[0] = shapSlice;
        wlrrOutput[1] = boundsReg;
        return wlrrOutput;
    }

    /**
     * Perform SHAP from a model and predictions.
     *
     * @param prediction: A ShapPrediction to be explained
     * @param model: The model to be explained
     * @return shap values, Saliency[] of shape [n_outputs], with each saliency reporting m feature importances+confidences
     */
    @Override
    public CompletableFuture<ShapResults> explainAsync(Prediction prediction, PredictionProvider model) {
        return explainAsync(prediction, model, null);
    }

    /**
     * Perform SHAP from a model and predictions.
     *
     * @param prediction: A ShapPrediction to be explained
     * @param model: The model to be explained
     * @param intermediateResultsConsumer: Unused
     *
     * @return shap values, Saliency[] of shape [n_outputs], with each saliency reporting m feature importances+confidences
     */
    @Override
    public CompletableFuture<ShapResults> explainAsync(Prediction prediction, PredictionProvider model, Consumer<ShapResults> intermediateResultsConsumer) {
        return this.explain(prediction, model);
    }
}
