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
package org.kie.kogito.explainability.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import org.apache.commons.math3.linear.CholeskyDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class LarsPathDataCarrier {
    // integers
    private final int nSamples;
    private final int nFeatures;
    private final int maxIterations;
    private final int maxFeatures;
    private int cIdx;
    private int nIter;
    private int nActive;

    // integer collections
    private Set<Integer> idx;
    private int[] indices;
    private List<Integer> active;

    // doubles
    private static final double EQUALITY_TOLERANCE = Math.ulp((float) 1.0);
    private static final double TINY = 1e-12;
    private double c;
    private double c_;
    private double normalizationFactor;
    private double gamma;
    private double zPos;

    //booleans
    private boolean drop;
    private final boolean lasso;
    private boolean degenerateRegressor;

    // real vectors
    private final RealVector y;
    private RealVector signActive;
    private RealVector alphas;
    private RealVector cov;
    private RealVector covNotShortened;

    private RealVector leastSquares;
    private RealVector corrEqDir;
    private RealVector z;

    // real matrices
    private RealMatrix X;
    private RealMatrix coefs;
    private final RealMatrix XT;

    //other
    CholeskyDecomposition decomp;

    // this carries all intermedate results from the LarsPath algorithm, and is purely a container class
    // that performs no calculations
    public LarsPathDataCarrier(RealMatrix X, RealVector y, int maxIterations, boolean lasso) {
        this.X = X;
        this.y = y;

        this.nSamples = X.getRowDimension();
        this.nFeatures = X.getColumnDimension();
        this.maxIterations = maxIterations;
        this.lasso = lasso;

        // initialize the values we know ahead of runtime
        this.XT = X.transpose();
        this.cov = XT.operate(y);
        this.maxFeatures = Math.min(nFeatures, maxIterations);
        this.signActive = MatrixUtils.createRealVector(new double[nFeatures]);
        this.indices = IntStream.range(0, nFeatures).toArray();
        this.active = new ArrayList<>();

        // initialize algorithm counters
        this.nIter = 0;
        this.nActive = 0;
        this.drop = false;
        this.idx = new HashSet<>();

        // initialize return objects
        this.coefs = MatrixUtils.createRealMatrix(maxFeatures + 1, nFeatures);
        this.alphas = MatrixUtils.createRealVector(new double[maxFeatures + 1]);
    }

    // getters and setters for everything
    public int getnSamples() {
        return nSamples;
    }

    public int getnFeatures() {
        return nFeatures;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public int getMaxFeatures() {
        return maxFeatures;
    }

    public int getcIdx() {
        return cIdx;
    }

    public void setcIdx(int cIdx) {
        this.cIdx = cIdx;
    }

    public int getnIter() {
        return nIter;
    }

    public void incrementnIter() {
        this.nIter += 1;
    }

    public int getnActive() {
        return nActive;
    }

    public void setnActive(int nActive) {
        this.nActive = nActive;
    }

    public Set<Integer> getIdx() {
        return idx;
    }

    public void setIdx(Set<Integer> idx) {
        this.idx = idx;
    }

    public int[] getIndices() {
        return indices;
    }

    public List<Integer> getActive() {
        return active;
    }

    public void setActive(List<Integer> active) {
        this.active = active;
    }

    public double getEqualityTolerance() {
        return EQUALITY_TOLERANCE;
    }

    public double getTiny() {
        return TINY;
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }

    public double getC_() {
        return c_;
    }

    public void setC_(double c_) {
        this.c_ = c_;
    }

    public double getNormalizationFactor() {
        return normalizationFactor;
    }

    public void setNormalizationFactor(double normalizationFactor) {
        this.normalizationFactor = normalizationFactor;
    }

    public double getGamma() {
        return gamma;
    }

    public void setGamma(double gamma) {
        this.gamma = gamma;
    }

    public double getzPos() {
        return zPos;
    }

    public void setzPos(double zPos) {
        this.zPos = zPos;
    }

    public boolean isDrop() {
        return drop;
    }

    public void setDrop(boolean drop) {
        this.drop = drop;
    }

    public boolean isDegenerateRegressor() {
        return degenerateRegressor;
    }

    public void setDegenerateRegressor(boolean degenerateRegressor) {
        this.degenerateRegressor = degenerateRegressor;
    }

    public boolean isLasso() {
        return lasso;
    }

    public RealVector getY() {
        return y;
    }

    public RealVector getSignActive() {
        return signActive;
    }

    public void setSignActive(RealVector signActive) {
        this.signActive = signActive;
    }

    public RealVector getAlphas() {
        return alphas;
    }

    public void setAlphas(RealVector alphas) {
        this.alphas = alphas;
    }

    public RealVector getCov() {
        return cov;
    }

    public void setCov(RealVector cov) {
        this.cov = cov;
    }

    public RealVector getCovNotShortened() {
        return covNotShortened;
    }

    public void setCovNotShortened(RealVector covNotShortened) {
        this.covNotShortened = covNotShortened;
    }

    public RealVector getLeastSquares() {
        return leastSquares;
    }

    public void setLeastSquares(RealVector leastSquares) {
        this.leastSquares = leastSquares;
    }

    public RealVector getCorrEqDir() {
        return corrEqDir;
    }

    public void setCorrEqDir(RealVector corrEqDir) {
        this.corrEqDir = corrEqDir;
    }

    public RealVector getZ() {
        return z;
    }

    public void setZ(RealVector z) {
        this.z = z;
    }

    public RealMatrix getX() {
        return X;
    }

    public void setX(RealMatrix x) {
        X = x;
    }

    public RealMatrix getCoefs() {
        return coefs;
    }

    public void setCoefs(RealMatrix coefs) {
        this.coefs = coefs;
    }

    public RealMatrix getXT() {
        return XT;
    }

    public CholeskyDecomposition getDecomp() {
        return decomp;
    }

    public void setDecomp(CholeskyDecomposition decomp) {
        this.decomp = decomp;
    }
};
