/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.kie.kogito.explainability.local.LocalExplainer;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.ShapPrediction;
import org.kie.kogito.explainability.utils.MatrixUtils;
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
public class ShapKernelExplainer implements LocalExplainer<double[][]> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShapKernelExplainer.class);

    public ShapKernelExplainer() {
        // nothing to set
    }

    private ShapDataCarrier initialize(ShapPrediction prediction, PredictionProvider model) {
        ShapConfig config = prediction.getConfig();

        // get shapes of input and output data
        int[] shape = MatrixUtils.getShape(config.getBackgroundMatrix());
        int rows = shape[0];
        int cols = shape[1];

        if (rows > 100) {
            LOGGER.debug("Warning: Background data sets larger than 100 samples might be slow!");
        }

        // establish background data
        CompletableFuture<double[][]> modelNull = model.predictAsync(config.getBackground())
                .thenApply(MatrixUtils::matrixFromPredictionOutput);
        CompletableFuture<Integer> outputSize = modelNull.thenApply(mn -> MatrixUtils.getShape(mn)[1]);

        //compute the mean of each column
        CompletableFuture<double[]> fnull = modelNull.thenApply(mn -> MatrixUtils.sum(
                MatrixUtils.matrixMultiply(mn, 1. / rows),
                MatrixUtils.Axis.ROW));
        CompletableFuture<double[][]> linkNull =
                fnull.thenApply(fn -> MatrixUtils.rowVector(this.link(fn, config.getLink())));

        // track number of samples
        int numSamples = config.getNSamples().orElseGet(() -> 2048 + (2 * cols));

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
        sdc.setConfig(config);

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
    private double link(double x, ShapConfig.LinkType l) {
        if (l.equals(ShapConfig.LinkType.IDENTITY)) {
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
    private double[] link(double[] v, ShapConfig.LinkType l) {
        return Arrays.stream(v)
                .map(x -> this.link(x, l))
                .toArray();
    }

    /**
     * Determine which features vary across the background data and this particular input.
     * If the feature has one value across all background data points and the input, it does not vary.
     *
     * @param input: The PredictionInput to look for variance with, in conjunction with the background data
     */
    private void setVaryingFeatureGroups(PredictionInput input, ShapDataCarrier sdc) {
        List<Integer> varyingFeatureGroups = new ArrayList<>();
        double[] inputVector = MatrixUtils.matrixFromPredictionInput(input)[0];
        double[] columnFeatures = new double[sdc.getRows() + 1];
        for (int col = 0; col < sdc.getCols(); col++) {
            System.arraycopy(MatrixUtils.getCol(sdc.getConfig().getBackgroundMatrix(), col),
                    0, columnFeatures, 0, sdc.getRows());
            columnFeatures[sdc.getRows()] = inputVector[col];
            long uniques = Arrays.stream(columnFeatures).distinct().count();
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
    private double[] normalizeWeightVector(double[] v) {
        double[][] expanded = MatrixUtils.rowVector(v);
        double sum = MatrixUtils.sum(expanded, MatrixUtils.Axis.COLUMN)[0];
        if (sum == 0) {
            return v;
        } else {
            return MatrixUtils.matrixMultiply(expanded, 1 / sum)[0];
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
     */
    private void addSample(PredictionInput pi, List<Integer> combination, double weight,
            boolean inverse, boolean fixed, ShapDataCarrier sdc) {
        boolean[] mask = new boolean[sdc.getCols()];
        if (inverse) {
            for (int i = 0; i < sdc.getNumVarying(); i++) {
                mask[sdc.getVaryingFeatureGroups(i)] = true;
            }
        }

        for (int i = 0; i < combination.size(); i++) {
            mask[sdc.getVaryingFeatureGroups(combination.get(i))] = !inverse;
        }
        int maskHash = this.hashMask(mask);
        if (sdc.getMasksUsed().containsKey(maskHash)) {
            ShapSyntheticDataSample previousSample = sdc.getSamplesAdded(sdc.getMasksUsed(maskHash));
            previousSample.incrementWeight();
        } else {
            ShapSyntheticDataSample sample = new ShapSyntheticDataSample(pi, mask, sdc.getConfig().getBackgroundMatrix(), weight, fixed);
            // map index in the samplesAdded list to the unique hash of this mask
            sdc.addMask(maskHash, sdc.getSamplesAddedSize());
            sdc.addSample(sample);
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
     * Compute the shap values for a specific prediction
     *
     * @param prediction: The ShapPrediction to be explained.
     * @param model: The PredictionProvider we are explaining.
     *
     * @return the shap values for this prediction, of shape [n_model_outputs x n_features]
     */
    private CompletableFuture<double[][]> explain(ShapPrediction prediction, PredictionProvider model) {
        ShapDataCarrier sdc = this.initialize(prediction, model);
        sdc.setSamplesAdded(new ArrayList<>());
        PredictionInput pi = prediction.getInput();
        PredictionOutput po = prediction.getOutput();

        if (pi.getFeatures().size() != sdc.getCols()) {
            throw new IllegalArgumentException(String.format(
                    "Prediction input feature count (%d) does not match background data feature count (%d)",
                    pi.getFeatures().size(), sdc.getCols()));
        }

        int cols = sdc.getCols();
        CompletableFuture<double[][]> output = sdc.getOutputSize().thenApply(os -> {
            if (po.getOutputs().size() != os) {
                throw new IllegalArgumentException(String.format(
                        "Prediction output size (%d) does not match background data output size (%d)",
                        po.getOutputs().size(), os));
            }
            return new double[os][cols];
        });

        double[][] poMatrix = MatrixUtils.matrixFromPredictionOutput(po);

        //first find varying features
        this.setVaryingFeatureGroups(pi, sdc);

        // if no features vary, then the features do not effect output, and all shap values are zero.
        if (sdc.getNumVarying() == 0) {
            return output;
        } else if (sdc.getNumVarying() == 1)
        // if 1 feature varies, this feature has all the effect
        {
            CompletableFuture<double[]> diff = sdc.getLinkNull()
                    .thenApply(ln -> MatrixUtils.matrixDifference(poMatrix, ln)[0]);
            return output.thenCompose(o -> diff.thenCombine(sdc.getOutputSize(), (df, os) -> {
                double[][] out = new double[os][cols];
                for (int i = 0; i < os; i++) {
                    out[i][sdc.getVaryingFeatureGroups(0)] = df[i];
                }
                return out;
            }));
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
            CompletableFuture<double[][]> expectations = this.runSyntheticData(sdc);

            // run the wlr model over the synthetic data results
            return output.thenCompose(o -> this.solveSystem(expectations, poMatrix[0], sdc));
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
        double[] weightOfSubsetSize = normalizeWeightVector(rawWeights);
        shapStats.setWeightOfSubsetSize(weightOfSubsetSize);
        shapStats.setRemainingWeights(Arrays.copyOf(weightOfSubsetSize, weightOfSubsetSize.length));
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
            double samplingWeight = shapStats.getRemainingWeights()[subsetSize];
            // if we have enough samples for the entirety of this subset:
            if (shapStats.getNumSamplesRemaining() * samplingWeight >= numSubsets) {
                shapStats.incrementNumFullSubsets();
                shapStats.decreaseNumSamplesRemainingBy(numSubsets);
                double[] remainingWeights = shapStats.getRemainingWeights();
                remainingWeights[subsetSize] = 0;
                shapStats.setRemainingWeights(normalizeWeightVector(remainingWeights));

                Iterator<int[]> combinations = CombinatoricsUtils.combinationsIterator(sdc.getNumVarying(), subsetSize);
                double individualWeight = shapStats.getWeightOfSubsetSize()[subsetSize] / numSubsets;
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
        double[] weightOfSubsetSize = shapStats.getWeightOfSubsetSize();
        double[] remainingWeights = Arrays.copyOf(weightOfSubsetSize, weightOfSubsetSize.length);
        for (int i = 0; i < remainingWeights.length; i++) {
            if (i < shapStats.getLargestPairedSubsetSize()) {
                remainingWeights[i] /= 2;
            }
        }
        double[] nonFullRemainingWeights = Arrays.copyOfRange(remainingWeights, shapStats.getNumFullSubsets() + 1,
                shapStats.getNumSubsetSizes() + 1);
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

            List<Double> subsetSizeWeights = Arrays.stream(shapStats.getFinalRemainingWeights())
                    .boxed()
                    .collect(Collectors.toList());

            RandomChoice<Integer> subsetSampler = new RandomChoice<>(subsetSizesRemaining, subsetSizeWeights);
            List<Integer> sizeSamples = subsetSampler.sample(shapStats.getNumSamplesRemaining() * 4,
                    sdc.getConfig().getRN());
            List<Integer> maskSizes = IntStream.range(0, sdc.getNumVarying()).boxed().collect(Collectors.toList());

            int sampleIdx = 0;
            while (shapStats.getNumSamplesRemaining() > 0) {
                int subsetSize = sizeSamples.get(sampleIdx);
                sampleIdx += 1;
                Collections.shuffle(maskSizes);
                List<Integer> maskIdxs = maskSizes.subList(0, subsetSize);
                this.addSample(pi, maskIdxs, 1., false, false, sdc);
                shapStats.decreaseNumSamplesRemainingBy(1);

                // add compliment if possible
                if (shapStats.getNumSamplesRemaining() > 0 && subsetSize <= shapStats.getLargestPairedSubsetSize()) {
                    this.addSample(pi, maskIdxs, 1., true, false, sdc);
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
        double nonFullWeight = 0;
        for (int i = shapStats.getNumFullSubsets() + 1; i < shapStats.getNumSubsetSizes() + 1; i++) {
            nonFullWeight += shapStats.getWeightOfSubsetSize()[i];
        }

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

    private CompletableFuture<double[][]> runSyntheticData(ShapDataCarrier sdc) {
        return sdc.getLinkNull().thenCompose(ln -> sdc.getOutputSize().thenCompose(os -> {
            HashMap<Integer, CompletableFuture<double[]>> expectationSlices = new HashMap<>();

            //in theory all of these can happen in parallel
            for (int i = 0; i < sdc.getSamplesAddedSize(); i++) {
                List<PredictionInput> pis = sdc.getSamplesAdded(i).getSyntheticData();
                expectationSlices.put(i,
                        sdc.getModel().predictAsync(pis)
                                .thenApply(MatrixUtils::matrixFromPredictionOutput)
                                .thenApply(posMatrix -> MatrixUtils.sum(
                                        MatrixUtils.matrixMultiply(posMatrix, 1. / sdc.getRows()),
                                        MatrixUtils.Axis.ROW))
                                .thenApply(x -> this.link(x, sdc.getConfig().getLink()))
                                .thenApply(x -> MatrixUtils.matrixDifference(MatrixUtils.rowVector(x), ln)[0]));
            }

            // reduce parallel operations into single array
            final CompletableFuture<double[][]>[] expectations = new CompletableFuture[] {
                    CompletableFuture.supplyAsync(() -> new double[sdc.getSamplesAddedSize()][os],
                            sdc.getConfig().getExecutor()) };
            expectationSlices.forEach((idx, slice) -> expectations[0] = expectations[0].thenCompose(e -> slice.thenApply(s -> {
                e[idx] = s;
                return e;
            })));
            return expectations[0];
        }));
    }

    /**
     * Create a WLR model to retrieve the SHAP values for a particular output of the model
     *
     * @param expectations: The expectations of each sample
     * @param output: The index of the particular output
     * @param poMatrix: The predictionOutputs for this explanation's prediction
     * @param fnull: The value stored in the CompletableFuture this.fnull
     * @param dropIdx: The regularization feature to use in the wlr model
     *
     * @return the shap values as found by the WLR
     */
    private double[] solve(double[][] expectations, int output, double[] poMatrix, double[] fnull,
            int dropIdx, ShapDataCarrier sdc) {
        double[][] xs = new double[sdc.getSamplesAddedSize()][sdc.getCols()];
        double[] ws = new double[sdc.getSamplesAddedSize()];
        double[] ys = new double[sdc.getSamplesAddedSize()];

        for (int i = 0; i < sdc.getSamplesAddedSize(); i++) {
            for (int j = 0; j < sdc.getCols(); j++) {
                xs[i][j] = sdc.getSamplesAdded(i).getMask()[j] ? 1. : 0.;
            }
            ys[i] = expectations[i][output];
            ws[i] = sdc.getSamplesAdded(i).getWeight();
        }

        ShapConfig.LinkType l = sdc.getConfig().getLink();
        double outputChange = this.link(poMatrix[output], l) - this.link(fnull[output], l);
        double[][] dropMask = MatrixUtils.rowVector(MatrixUtils.getCol(xs, dropIdx));
        double[][] dropEffect = MatrixUtils.matrixMultiply(dropMask, outputChange);
        double[] adjY = MatrixUtils.matrixDifference(MatrixUtils.rowVector(ys), dropEffect)[0];
        List<Integer> included = new ArrayList<>();
        sdc.getVaryingFeatureGroups().forEach(v -> {
            if (v != dropIdx) {
                included.add(v);
            }
        });
        double[][] includeMask = MatrixUtils.transpose(MatrixUtils.getCols(xs, included));
        double[][] maskDiff = MatrixUtils.transpose(MatrixUtils.matrixRowDifference(includeMask, dropMask[0]));
        return this.runWLRR(maskDiff, adjY, ws, outputChange, dropIdx, sdc);
    }

    /**
     * Run WLRRs in parallel, with each parallel thread computing the shap values for a particular output of the model
     *
     * @param expectations: The expectations of each sample
     * @param poMatrix: The predictionOutputs for this explanation's prediction
     *
     * @return the shap values as found by the WLR
     */
    private CompletableFuture<double[][]> solveSystem(CompletableFuture<double[][]> expectations, double[] poMatrix,
            ShapDataCarrier sdc) {
        int dropIdx = sdc.getVaryingFeatureGroups(sdc.getVaryingFeatureGroups().size() - 1);

        return expectations.thenCompose(exps -> sdc.getFnull().thenCompose(fn -> sdc.getOutputSize().thenCompose(os -> {
            HashMap<Integer, CompletableFuture<double[]>> shapSlices = new HashMap<>();
            for (int output = 0; output < os; output++) {
                int finalOutput = output;
                shapSlices.put(output, CompletableFuture.supplyAsync(
                        () -> solve(exps, finalOutput, poMatrix, fn, dropIdx, sdc),
                        sdc.getConfig().getExecutor()));
            }

            // reduce parallel operations into single array
            final CompletableFuture<double[][]>[] shapVals = new CompletableFuture[] {
                    CompletableFuture.supplyAsync(() -> new double[os][sdc.getCols()], sdc.getConfig().getExecutor()) };
            shapSlices.forEach((idx, slice) -> shapVals[0] = shapVals[0].thenCompose(e -> slice.thenApply(s -> {
                e[idx] = s;
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
     * @return the shap values as found by the WLR
     */
    // run the WLRR for a single output
    private double[] runWLRR(double[][] maskDiff, double[] adjY, double[] ws, double outputChange,
            int dropIdx, ShapDataCarrier sdc) {
        WeightedLinearRegressionResults wlrr = WeightedLinearRegression.fit(maskDiff, adjY,
                ws, false, sdc.getConfig().getRN());
        double[] coeffs = wlrr.getCoefficients();
        int usedCoefs = 0;
        double[] shapSlice = new double[sdc.getCols()];
        for (int i = 0; i < sdc.getVaryingFeatureGroups().size(); i++) {
            int idx = sdc.getVaryingFeatureGroups(i);
            if (idx != dropIdx) {
                shapSlice[idx] = coeffs[usedCoefs];
                usedCoefs += 1;
            }
        }
        shapSlice[dropIdx] = outputChange - Arrays.stream(coeffs).sum();
        return shapSlice;
    }

    /**
     * Perform SHAP from a model and predictions.
     *
     * @param prediction: A ShapPrediction to be explained
     * @param model: The model to be explained
     * @return shap values, double[][] of shape [n_outputs x n_features]
     */
    @Override
    public CompletableFuture<double[][]> explainAsync(Prediction prediction, PredictionProvider model) {
        return explainAsync(prediction, model, null);
    }

    /**
     * Perform SHAP from a model and predictions.
     *
     * @param prediction: A ShapPrediction to be explained
     * @param model: The model to be explained
     * @param intermediateResultsConsumer: Unused
     *
     * @return shap values, double[][] of shape [n_outputs x n_features]
     */
    @Override
    public CompletableFuture<double[][]> explainAsync(Prediction prediction, PredictionProvider model, Consumer<double[][]> intermediateResultsConsumer) {
        ShapPrediction sPrediction = (ShapPrediction) prediction;
        return this.explain(sPrediction, model);
    }
}
