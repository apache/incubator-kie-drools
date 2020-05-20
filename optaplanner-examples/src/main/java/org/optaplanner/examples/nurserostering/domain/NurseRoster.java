/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.nurserostering.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.nurserostering.domain.contract.Contract;
import org.optaplanner.examples.nurserostering.domain.contract.ContractLine;
import org.optaplanner.examples.nurserostering.domain.contract.PatternContractLine;
import org.optaplanner.examples.nurserostering.domain.pattern.Pattern;
import org.optaplanner.examples.nurserostering.domain.request.DayOffRequest;
import org.optaplanner.examples.nurserostering.domain.request.DayOnRequest;
import org.optaplanner.examples.nurserostering.domain.request.ShiftOffRequest;
import org.optaplanner.examples.nurserostering.domain.request.ShiftOnRequest;
import org.optaplanner.persistence.xstream.api.score.buildin.hardsoft.HardSoftScoreXStreamConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@PlanningSolution
@XStreamAlias("NurseRoster")
public class NurseRoster extends AbstractPersistable {

    private String code;

    @ProblemFactProperty
    private NurseRosterParametrization nurseRosterParametrization;
    @ProblemFactCollectionProperty
    private List<Skill> skillList;
    @ProblemFactCollectionProperty
    private List<ShiftType> shiftTypeList;
    @ProblemFactCollectionProperty
    private List<ShiftTypeSkillRequirement> shiftTypeSkillRequirementList;
    @ProblemFactCollectionProperty
    private List<Pattern> patternList;
    @ProblemFactCollectionProperty
    private List<Contract> contractList;
    @ProblemFactCollectionProperty
    private List<ContractLine> contractLineList;
    @ProblemFactCollectionProperty
    private List<PatternContractLine> patternContractLineList;
    @ValueRangeProvider(id = "employeeRange")
    @ProblemFactCollectionProperty
    private List<Employee> employeeList;
    @ProblemFactCollectionProperty
    private List<SkillProficiency> skillProficiencyList;
    @ProblemFactCollectionProperty
    private List<ShiftDate> shiftDateList;
    @ProblemFactCollectionProperty
    private List<Shift> shiftList;
    @ProblemFactCollectionProperty
    private List<DayOffRequest> dayOffRequestList;
    @ProblemFactCollectionProperty
    private List<DayOnRequest> dayOnRequestList;
    @ProblemFactCollectionProperty
    private List<ShiftOffRequest> shiftOffRequestList;
    @ProblemFactCollectionProperty
    private List<ShiftOnRequest> shiftOnRequestList;

    @PlanningEntityCollectionProperty
    private List<ShiftAssignment> shiftAssignmentList;

    @PlanningScore
    @XStreamConverter(HardSoftScoreXStreamConverter.class)
    private HardSoftScore score;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public NurseRosterParametrization getNurseRosterParametrization() {
        return nurseRosterParametrization;
    }

    public void setNurseRosterParametrization(NurseRosterParametrization nurseRosterParametrization) {
        this.nurseRosterParametrization = nurseRosterParametrization;
    }

    public List<Skill> getSkillList() {
        return skillList;
    }

    public void setSkillList(List<Skill> skillList) {
        this.skillList = skillList;
    }

    public List<ShiftType> getShiftTypeList() {
        return shiftTypeList;
    }

    public void setShiftTypeList(List<ShiftType> shiftTypeList) {
        this.shiftTypeList = shiftTypeList;
    }

    public List<ShiftTypeSkillRequirement> getShiftTypeSkillRequirementList() {
        return shiftTypeSkillRequirementList;
    }

    public void setShiftTypeSkillRequirementList(List<ShiftTypeSkillRequirement> shiftTypeSkillRequirementList) {
        this.shiftTypeSkillRequirementList = shiftTypeSkillRequirementList;
    }

    public List<Pattern> getPatternList() {
        return patternList;
    }

    public void setPatternList(List<Pattern> patternList) {
        this.patternList = patternList;
    }

    public List<Contract> getContractList() {
        return contractList;
    }

    public void setContractList(List<Contract> contractList) {
        this.contractList = contractList;
    }

    public List<ContractLine> getContractLineList() {
        return contractLineList;
    }

    public void setContractLineList(List<ContractLine> contractLineList) {
        this.contractLineList = contractLineList;
    }

    public List<PatternContractLine> getPatternContractLineList() {
        return patternContractLineList;
    }

    public void setPatternContractLineList(List<PatternContractLine> patternContractLineList) {
        this.patternContractLineList = patternContractLineList;
    }

    public List<Employee> getEmployeeList() {
        return employeeList;
    }

    public void setEmployeeList(List<Employee> employeeList) {
        this.employeeList = employeeList;
    }

    public List<SkillProficiency> getSkillProficiencyList() {
        return skillProficiencyList;
    }

    public void setSkillProficiencyList(List<SkillProficiency> skillProficiencyList) {
        this.skillProficiencyList = skillProficiencyList;
    }

    public List<ShiftDate> getShiftDateList() {
        return shiftDateList;
    }

    public void setShiftDateList(List<ShiftDate> shiftDateList) {
        this.shiftDateList = shiftDateList;
    }

    public List<Shift> getShiftList() {
        return shiftList;
    }

    public void setShiftList(List<Shift> shiftList) {
        this.shiftList = shiftList;
    }

    public List<DayOffRequest> getDayOffRequestList() {
        return dayOffRequestList;
    }

    public void setDayOffRequestList(List<DayOffRequest> dayOffRequestList) {
        this.dayOffRequestList = dayOffRequestList;
    }

    public List<DayOnRequest> getDayOnRequestList() {
        return dayOnRequestList;
    }

    public void setDayOnRequestList(List<DayOnRequest> dayOnRequestList) {
        this.dayOnRequestList = dayOnRequestList;
    }

    public List<ShiftOffRequest> getShiftOffRequestList() {
        return shiftOffRequestList;
    }

    public void setShiftOffRequestList(List<ShiftOffRequest> shiftOffRequestList) {
        this.shiftOffRequestList = shiftOffRequestList;
    }

    public List<ShiftOnRequest> getShiftOnRequestList() {
        return shiftOnRequestList;
    }

    public void setShiftOnRequestList(List<ShiftOnRequest> shiftOnRequestList) {
        this.shiftOnRequestList = shiftOnRequestList;
    }

    public List<ShiftAssignment> getShiftAssignmentList() {
        return shiftAssignmentList;
    }

    public void setShiftAssignmentList(List<ShiftAssignment> shiftAssignmentList) {
        this.shiftAssignmentList = shiftAssignmentList;
    }

    public HardSoftScore getScore() {
        return score;
    }

    public void setScore(HardSoftScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

}
