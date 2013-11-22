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

public class PartialScore extends BaselineScore implements Serializable {
    protected String reasoncode;
    protected int position;
    protected double baselineScore;

    public PartialScore(String scorecardName, String characteristic, double score, String reasoncode, int position) {
        super(scorecardName, characteristic, score);
        this.reasoncode = reasoncode;
        this.position = position;
    }

    public PartialScore(String scorecardName, String characteristic, double score, String reasoncode, double baselineScore, int position) {
        super(scorecardName, characteristic, score);
        this.reasoncode = reasoncode;
        this.position = position;
        this.baselineScore = baselineScore;
    }

    public PartialScore(String scorecardName, String characteristic, double score) {
        super(scorecardName, characteristic, score);
        this.scorecardName = scorecardName;
        this.characteristic = characteristic;
        this.score = score;
    }

    public int getPosition() {
        return position;
    }

    public String getReasoncode() {
        return reasoncode;
    }

    public void setReasoncode(String reasoncode) {
        this.reasoncode = reasoncode;
    }

    public double getBaselineScore() {
        return baselineScore;
    }

    public void setBaselineScore(double baselineScore) {
        this.baselineScore = baselineScore;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
