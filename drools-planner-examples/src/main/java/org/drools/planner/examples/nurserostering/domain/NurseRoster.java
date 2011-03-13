/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.examples.nurserostering.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.core.score.HardAndSoftScore;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.domain.AbstractPersistable;
import org.drools.planner.examples.nurserostering.domain.contract.Contract;
import org.drools.planner.examples.nurserostering.domain.contract.ContractLine;
import org.drools.planner.examples.nurserostering.domain.contract.PatternContractLine;
import org.drools.planner.examples.nurserostering.domain.request.DayOffRequest;
import org.drools.planner.examples.nurserostering.domain.request.DayOnRequest;
import org.drools.planner.examples.nurserostering.domain.request.ShiftOffRequest;
import org.drools.planner.examples.nurserostering.domain.request.ShiftOnRequest;

@XStreamAlias("NurseRoster")
public class NurseRoster extends AbstractPersistable implements Solution<HardAndSoftScore> {

    private String code;

    private List<Skill> skillList;
    private List<ShiftType> shiftTypeList;
    private List<ShiftTypeSkillRequirement> shiftTypeSkillRequirementList;
    private List<Pattern> patternList;
    private List<Contract> contractList;
    private List<ContractLine> contractLineList;
    private List<PatternContractLine> patternContractLineList;
    private List<Employee> employeeList;
    private List<SkillProficiency> skillProficiencyList;
    private List<ShiftDate> shiftDateList;
    private List<Shift> shiftList;
    private List<DayOffRequest> dayOffRequestList;
    private List<DayOnRequest> dayOnRequestList;
    private List<ShiftOffRequest> shiftOffRequestList;
    private List<ShiftOnRequest> shiftOnRequestList;

    private List<Assignment> assignmentList;

    private HardAndSoftScore score;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public List<Assignment> getAssignmentList() {
        return assignmentList;
    }

    public void setAssignmentList(List<Assignment> assignmentList) {
        this.assignmentList = assignmentList;
    }

    public HardAndSoftScore getScore() {
        return score;
    }

    public void setScore(HardAndSoftScore score) {
        this.score = score;
    }

    public boolean isInitialized() {
        return (assignmentList != null);
    }

    public Collection<? extends Object> getFacts() {
        List<Object> facts = new ArrayList<Object>();
        // TODO add RosterInfo as a property on NurseRoster
        facts.add(new RosterInfo(shiftDateList.get(0), shiftDateList.get(shiftDateList.size() - 1)));
        facts.addAll(skillList);
        facts.addAll(shiftTypeList);
        facts.addAll(shiftTypeSkillRequirementList);
        facts.addAll(patternList);
        facts.addAll(contractList);
        facts.addAll(contractLineList);
        facts.addAll(patternContractLineList);
        facts.addAll(employeeList);
        facts.addAll(skillProficiencyList);
        facts.addAll(shiftDateList);
        facts.addAll(shiftList);
        facts.addAll(dayOffRequestList);
        facts.addAll(dayOnRequestList);
        facts.addAll(shiftOffRequestList);
        facts.addAll(shiftOnRequestList);
        // TODO add more properties

        if (isInitialized()) {
            facts.addAll(assignmentList);
        }
        return facts;
    }

    /**
     * Clone will only deep copy the {@link #assignmentList}.
     */
    public NurseRoster cloneSolution() {
        NurseRoster clone = new NurseRoster();
        clone.id = id;
        clone.code = code;
        clone.skillList = skillList;
        clone.shiftTypeList = shiftTypeList;
        clone.shiftTypeSkillRequirementList = shiftTypeSkillRequirementList;
        clone.patternList = patternList;
        clone.contractList = contractList;
        clone.contractLineList = contractLineList;
        clone.patternContractLineList = patternContractLineList;
        clone.employeeList = employeeList;
        clone.skillProficiencyList = skillProficiencyList;
        clone.shiftDateList = shiftDateList;
        clone.shiftList = shiftList;
        clone.dayOffRequestList = dayOffRequestList;
        clone.dayOnRequestList = dayOnRequestList;
        clone.shiftOffRequestList = shiftOffRequestList;
        clone.shiftOnRequestList = shiftOnRequestList;
        List<Assignment> clonedAssignmentList = new ArrayList<Assignment>(
                assignmentList.size());
        for (Assignment assignment : assignmentList) {
            Assignment clonedAssignment = assignment.clone();
            clonedAssignmentList.add(clonedAssignment);
        }
        clone.assignmentList = clonedAssignmentList;
        clone.score = score;
        return clone;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (id == null || !(o instanceof NurseRoster)) {
            return false;
        } else {
            NurseRoster other = (NurseRoster) o;
            if (assignmentList.size() != other.assignmentList.size()) {
                return false;
            }
            for (Iterator<Assignment> it = assignmentList.iterator(), otherIt = other.assignmentList.iterator(); it.hasNext();) {
                Assignment assignment = it.next();
                Assignment otherAssignment = otherIt.next();
                // Notice: we don't use equals()
                if (!assignment.solutionEquals(otherAssignment)) {
                    return false;
                }
            }
            return true;
        }
    }

    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        for (Assignment assignment : assignmentList) {
            // Notice: we don't use hashCode()
            hashCodeBuilder.append(assignment.solutionHashCode());
        }
        return hashCodeBuilder.toHashCode();
    }

}
