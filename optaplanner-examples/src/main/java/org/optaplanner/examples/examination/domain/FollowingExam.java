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

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.ShadowVariable;
import org.optaplanner.examples.examination.domain.solver.PeriodUpdatingVariableListener;

@PlanningEntity
public class FollowingExam extends Exam {

    protected LeadingExam leadingExam;

    // Shadow variables
    protected Period period;

    public LeadingExam getLeadingExam() {
        return leadingExam;
    }

    public void setLeadingExam(LeadingExam leadingExam) {
        this.leadingExam = leadingExam;
    }

    @Override
    @ShadowVariable(variableListenerClass = PeriodUpdatingVariableListener.class,
            sourceEntityClass = LeadingExam.class, sourceVariableName = "period")
    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    // ************************************************************************
    // With methods
    // ************************************************************************

    public FollowingExam withId(long id) {
        this.setId(id);
        return this;
    }

    public FollowingExam withTopic(Topic topic) {
        this.setTopic(topic);
        return this;
    }

    public FollowingExam withRoom(Room room) {
        this.setRoom(room);
        return this;
    }

    public FollowingExam withPeriod(Period period) {
        this.setPeriod(period);
        return this;
    }

    public FollowingExam withLeadingExam(LeadingExam leadingExam) {
        this.setLeadingExam(leadingExam);
        return this;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
