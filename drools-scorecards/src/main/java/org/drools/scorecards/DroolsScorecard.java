/*
 * Copyright 2012 JBoss Inc
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

package org.drools.scorecards;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DroolsScorecard implements Serializable {
    double calculatedScore;
    List<String> reasonCodes = new ArrayList<String>();
    Map<String, Double> baselineScoreMap = new HashMap<String, Double>();
    private int reasonCodeAlgorithm;
    public static int REASON_CODE_ALGORITHM_POINTSABOVE = 1;
    public static int REASON_CODE_ALGORITHM_POINTSBELOW = -1;

    public int getReasonCodeAlgorithm() {
        return reasonCodeAlgorithm;
    }

    public void setReasonCodeAlgorithm(int reasonCodeAlgorithm) {
        this.reasonCodeAlgorithm = reasonCodeAlgorithm;
    }

    public void setBaselineScore(String characteristic, int baselineScore){
        baselineScoreMap.put(characteristic, (double)baselineScore);
    }

    public void setBaselineScore(String characteristic, double baselineScore){
        baselineScoreMap.put(characteristic, baselineScore);
    }

    public double getCalculatedScore() {
        return calculatedScore;
    }

    public void setCalculatedScore(double calculatedScore) {
        this.calculatedScore = calculatedScore;
    }

    public void setInitialScore(double initialScore) {
        this.calculatedScore = initialScore;
    }

    public List<String> getReasonCodes() {
        return Collections.unmodifiableList(reasonCodes);
    }

    public void setReasonCodes(List<String> reasonCodes) {
        this.reasonCodes = reasonCodes;
    }

    public void sortAndSetReasonCodes(List<PartialScore> partialScores) {
        sortAndSetReasonCodes(reasonCodeAlgorithm, partialScores);
    }

    public void sortAndSetReasonCodes(int reasonCodeAlgorithm, List<PartialScore> partialScores) {
        setReasonCodeAlgorithm(reasonCodeAlgorithm);
        TreeMap<Double, String> distanceMap = new TreeMap<Double, String>();
        for (PartialScore partialScore : partialScores ){
            double baseline = partialScore.getBaselineScore();
            double distance = 0;
            if (getReasonCodeAlgorithm() == REASON_CODE_ALGORITHM_POINTSABOVE) {
                distance = (baseline - partialScore.getScore())+partialScore.getPosition();
                if( distance >= baseline) {
                    distanceMap.put(distance, partialScore.getReasoncode());
                }
            } else if (getReasonCodeAlgorithm() == REASON_CODE_ALGORITHM_POINTSBELOW){
                distance = (partialScore.getScore()-baseline)+partialScore.getPosition();
                if( distance <= baseline) {
                    distanceMap.put(distance, partialScore.getReasoncode());
                }
            }
        }

        List<String> reasonCodes = new ArrayList<String>();
        for ( Double distance : distanceMap.descendingKeySet()) {
            reasonCodes.add(distanceMap.get(distance));
        }
        while (reasonCodes.size() < partialScores.size()){
            reasonCodes.add(reasonCodes.get(reasonCodes.size()-1));
        }
        setReasonCodes(reasonCodes);
    }


    public DroolsScorecard() {
    }
}
