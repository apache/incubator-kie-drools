/*
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

package org.optaplanner.examples.examination.domain;

import org.optaplanner.examples.common.domain.AbstractPersistable;

public class PeriodPenalty extends AbstractPersistable {

    public PeriodPenalty() {
    }

    public PeriodPenalty(long id, Topic leftTopic, Topic rightTopic, PeriodPenaltyType periodPenaltyType) {
        super(id);
        this.leftTopic = leftTopic;
        this.rightTopic = rightTopic;
        this.periodPenaltyType = periodPenaltyType;
    }

    private PeriodPenaltyType periodPenaltyType;
    private Topic leftTopic;
    private Topic rightTopic;

    public PeriodPenaltyType getPeriodPenaltyType() {
        return periodPenaltyType;
    }

    public void setPeriodPenaltyType(PeriodPenaltyType periodPenaltyType) {
        this.periodPenaltyType = periodPenaltyType;
    }

    public Topic getLeftTopic() {
        return leftTopic;
    }

    public void setLeftTopic(Topic leftTopic) {
        this.leftTopic = leftTopic;
    }

    public Topic getRightTopic() {
        return rightTopic;
    }

    public void setRightTopic(Topic rightTopic) {
        this.rightTopic = rightTopic;
    }

    @Override
    public String toString() {
        return periodPenaltyType + "@" + leftTopic.getId() + "&" + rightTopic.getId();
    }

}
