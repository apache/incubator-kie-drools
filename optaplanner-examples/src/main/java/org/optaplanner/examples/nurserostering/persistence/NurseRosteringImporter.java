/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.nurserostering.persistence;

import java.io.IOException;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.jdom.DataConversionException;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.optaplanner.examples.common.persistence.AbstractXmlSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;
import org.optaplanner.examples.nurserostering.app.NurseRosteringApp;
import org.optaplanner.examples.nurserostering.domain.Employee;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.domain.NurseRosterParametrization;
import org.optaplanner.examples.nurserostering.domain.Shift;
import org.optaplanner.examples.nurserostering.domain.ShiftAssignment;
import org.optaplanner.examples.nurserostering.domain.ShiftDate;
import org.optaplanner.examples.nurserostering.domain.ShiftType;
import org.optaplanner.examples.nurserostering.domain.ShiftTypeSkillRequirement;
import org.optaplanner.examples.nurserostering.domain.Skill;
import org.optaplanner.examples.nurserostering.domain.SkillProficiency;
import org.optaplanner.examples.nurserostering.domain.WeekendDefinition;
import org.optaplanner.examples.nurserostering.domain.contract.BooleanContractLine;
import org.optaplanner.examples.nurserostering.domain.contract.Contract;
import org.optaplanner.examples.nurserostering.domain.contract.ContractLine;
import org.optaplanner.examples.nurserostering.domain.contract.ContractLineType;
import org.optaplanner.examples.nurserostering.domain.contract.MinMaxContractLine;
import org.optaplanner.examples.nurserostering.domain.contract.PatternContractLine;
import org.optaplanner.examples.nurserostering.domain.pattern.FreeBefore2DaysWithAWorkDayPattern;
import org.optaplanner.examples.nurserostering.domain.pattern.Pattern;
import org.optaplanner.examples.nurserostering.domain.pattern.ShiftType2DaysPattern;
import org.optaplanner.examples.nurserostering.domain.pattern.ShiftType3DaysPattern;
import org.optaplanner.examples.nurserostering.domain.pattern.WorkBeforeFreeSequencePattern;
import org.optaplanner.examples.nurserostering.domain.request.DayOffRequest;
import org.optaplanner.examples.nurserostering.domain.request.DayOnRequest;
import org.optaplanner.examples.nurserostering.domain.request.ShiftOffRequest;
import org.optaplanner.examples.nurserostering.domain.request.ShiftOnRequest;

import static java.time.temporal.ChronoUnit.*;

public class NurseRosteringImporter extends AbstractXmlSolutionImporter<NurseRoster> {

    public static void main(String[] args) {
        SolutionConverter<NurseRoster> converter = SolutionConverter.createImportConverter(
                NurseRosteringApp.DATA_DIR_NAME, new NurseRosteringImporter(), NurseRoster.class);
        converter.convertAll();
    }

    @Override
    public XmlInputBuilder<NurseRoster> createXmlInputBuilder() {
        return new NurseRosteringInputBuilder();
    }

    public static class NurseRosteringInputBuilder extends XmlInputBuilder<NurseRoster> {

        protected Map<LocalDate, ShiftDate> shiftDateMap;
        protected Map<String, Skill> skillMap;
        protected Map<String, ShiftType> shiftTypeMap;
        protected Map<Pair<LocalDate, String>, Shift> dateAndShiftTypeToShiftMap;
        protected Map<Pair<DayOfWeek, ShiftType>, List<Shift>> dayOfWeekAndShiftTypeToShiftListMap;
        protected Map<String, Pattern> patternMap;
        protected Map<String, Contract> contractMap;
        protected Map<String, Employee> employeeMap;

        @Override
        public NurseRoster readSolution() throws IOException, JDOMException {
            // Note: javax.xml is terrible. JDom is much much easier.

            Element schedulingPeriodElement = document.getRootElement();
            assertElementName(schedulingPeriodElement, "SchedulingPeriod");
            NurseRoster nurseRoster = new NurseRoster();
            nurseRoster.setId(0L);
            nurseRoster.setCode(schedulingPeriodElement.getAttribute("ID").getValue());

            generateShiftDateList(nurseRoster,
                    schedulingPeriodElement.getChild("StartDate"),
                    schedulingPeriodElement.getChild("EndDate"));
            generateNurseRosterInfo(nurseRoster);
            readSkillList(nurseRoster, schedulingPeriodElement.getChild("Skills"));
            readShiftTypeList(nurseRoster, schedulingPeriodElement.getChild("ShiftTypes"));
            generateShiftList(nurseRoster);
            readPatternList(nurseRoster, schedulingPeriodElement.getChild("Patterns"));
            readContractList(nurseRoster, schedulingPeriodElement.getChild("Contracts"));
            readEmployeeList(nurseRoster, schedulingPeriodElement.getChild("Employees"));
            readRequiredEmployeeSizes(nurseRoster, schedulingPeriodElement.getChild("CoverRequirements"));
            readDayOffRequestList(nurseRoster, schedulingPeriodElement.getChild("DayOffRequests"));
            readDayOnRequestList(nurseRoster, schedulingPeriodElement.getChild("DayOnRequests"));
            readShiftOffRequestList(nurseRoster, schedulingPeriodElement.getChild("ShiftOffRequests"));
            readShiftOnRequestList(nurseRoster, schedulingPeriodElement.getChild("ShiftOnRequests"));
            createShiftAssignmentList(nurseRoster);

            BigInteger possibleSolutionSize = BigInteger.valueOf(nurseRoster.getEmployeeList().size()).pow(
                    nurseRoster.getShiftAssignmentList().size());
            logger.info("NurseRoster {} has {} skills, {} shiftTypes, {} patterns, {} contracts, {} employees," +
                    " {} shiftDates, {} shiftAssignments and {} requests with a search space of {}.",
                    getInputId(),
                    nurseRoster.getSkillList().size(),
                    nurseRoster.getShiftTypeList().size(),
                    nurseRoster.getPatternList().size(),
                    nurseRoster.getContractList().size(),
                    nurseRoster.getEmployeeList().size(),
                    nurseRoster.getShiftDateList().size(),
                    nurseRoster.getShiftAssignmentList().size(),
                    nurseRoster.getDayOffRequestList().size() + nurseRoster.getDayOnRequestList().size()
                            + nurseRoster.getShiftOffRequestList().size() + nurseRoster.getShiftOnRequestList().size(),
                    getFlooredPossibleSolutionSize(possibleSolutionSize));
            return nurseRoster;
        }

        private void generateShiftDateList(NurseRoster nurseRoster,
                Element startDateElement, Element endDateElement) throws JDOMException {
            LocalDate startDate;
            try {
                startDate = LocalDate.parse(startDateElement.getText(), DateTimeFormatter.ISO_DATE);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid startDate (" + startDateElement.getText() + ").", e);
            }
            int startDayOfYear = startDate.getDayOfYear();
            int startYear = startDate.getYear();
            LocalDate endDate;
            try {
                endDate = LocalDate.parse(endDateElement.getText(), DateTimeFormatter.ISO_DATE);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid endDate (" + endDateElement.getText() + ").", e);
            }
            int endDayOfYear = startDate.getDayOfYear();
            int endYear = startDate.getYear();
            if (startDate.compareTo(endDate) >= 0) {
                throw new IllegalStateException("The startDate (" + startDate
                        + " must be before endDate (" + endDate + ").");
            }
            int maxDayIndex = Math.toIntExact(DAYS.between(startDate, endDate));
            int shiftDateSize = maxDayIndex + 1;
            List<ShiftDate> shiftDateList = new ArrayList<>(shiftDateSize);
            shiftDateMap = new HashMap<>(shiftDateSize);
            long id = 0L;
            int dayIndex = 0;
            LocalDate date = startDate;
            for (int i = 0; i < shiftDateSize; i++) {
                ShiftDate shiftDate = new ShiftDate();
                shiftDate.setId(id);
                shiftDate.setDayIndex(dayIndex);
                shiftDate.setDate(date);
                shiftDate.setShiftList(new ArrayList<>());
                shiftDateList.add(shiftDate);
                shiftDateMap.put(date, shiftDate);
                id++;
                dayIndex++;
                date = date.plusDays(1);
            }
            nurseRoster.setShiftDateList(shiftDateList);
        }

        private void generateNurseRosterInfo(NurseRoster nurseRoster) {
            List<ShiftDate> shiftDateList = nurseRoster.getShiftDateList();
            NurseRosterParametrization nurseRosterParametrization = new NurseRosterParametrization();
            nurseRosterParametrization.setId(0L);
            nurseRosterParametrization.setFirstShiftDate(shiftDateList.get(0));
            nurseRosterParametrization.setLastShiftDate(shiftDateList.get(shiftDateList.size() - 1));
            nurseRosterParametrization.setPlanningWindowStart(shiftDateList.get(0));
            nurseRoster.setNurseRosterParametrization(nurseRosterParametrization);
        }

        private void readSkillList(NurseRoster nurseRoster, Element skillsElement) throws JDOMException {
            List<Skill> skillList;
            if (skillsElement == null) {
                skillList = Collections.emptyList();
            } else {
                List<Element> skillElementList = (List<Element>) skillsElement.getChildren();
                skillList = new ArrayList<>(skillElementList.size());
                skillMap = new HashMap<>(skillElementList.size());
                long id = 0L;
                for (Element element : skillElementList) {
                    assertElementName(element, "Skill");
                    Skill skill = new Skill();
                    skill.setId(id);
                    skill.setCode(element.getText());
                    skillList.add(skill);
                    if (skillMap.containsKey(skill.getCode())) {
                        throw new IllegalArgumentException("There are 2 skills with the same code ("
                                + skill.getCode() + ").");
                    }
                    skillMap.put(skill.getCode(), skill);
                    id++;
                }
            }
            nurseRoster.setSkillList(skillList);
        }

        private void readShiftTypeList(NurseRoster nurseRoster, Element shiftTypesElement) throws JDOMException {
            List<Element> shiftTypeElementList = (List<Element>) shiftTypesElement.getChildren();
            List<ShiftType> shiftTypeList = new ArrayList<>(shiftTypeElementList.size());
            shiftTypeMap = new HashMap<>(shiftTypeElementList.size());
            long id = 0L;
            int index = 0;
            List<ShiftTypeSkillRequirement> shiftTypeSkillRequirementList
                    = new ArrayList<>(shiftTypeElementList.size() * 2);
            long shiftTypeSkillRequirementId = 0L;
            for (Element element : shiftTypeElementList) {
                assertElementName(element, "Shift");
                ShiftType shiftType = new ShiftType();
                shiftType.setId(id);
                shiftType.setCode(element.getAttribute("ID").getValue());
                shiftType.setIndex(index);
                String startTimeString = element.getChild("StartTime").getText();
                shiftType.setStartTimeString(startTimeString);
                String endTimeString = element.getChild("EndTime").getText();
                shiftType.setEndTimeString(endTimeString);
                shiftType.setNight(startTimeString.compareTo(endTimeString) > 0);
                shiftType.setDescription(element.getChild("Description").getText());

                Element skillsElement = element.getChild("Skills");
                if (skillsElement != null) {
                    List<Element> skillElementList = (List<Element>) skillsElement.getChildren();
                    for (Element skillElement : skillElementList) {
                        assertElementName(skillElement, "Skill");
                        ShiftTypeSkillRequirement shiftTypeSkillRequirement = new ShiftTypeSkillRequirement();
                        shiftTypeSkillRequirement.setId(shiftTypeSkillRequirementId);
                        shiftTypeSkillRequirement.setShiftType(shiftType);
                        Skill skill = skillMap.get(skillElement.getText());
                        if (skill == null) {
                            throw new IllegalArgumentException("The skill (" + skillElement.getText()
                                    + ") of shiftType (" + shiftType.getCode() + ") does not exist.");
                        }
                        shiftTypeSkillRequirement.setSkill(skill);
                        shiftTypeSkillRequirementList.add(shiftTypeSkillRequirement);
                        shiftTypeSkillRequirementId++;
                    }
                }

                shiftTypeList.add(shiftType);
                if (shiftTypeMap.containsKey(shiftType.getCode())) {
                    throw new IllegalArgumentException("There are 2 shiftTypes with the same code ("
                            + shiftType.getCode() + ").");
                }
                shiftTypeMap.put(shiftType.getCode(), shiftType);
                id++;
                index++;
            }
            nurseRoster.setShiftTypeList(shiftTypeList);
            nurseRoster.setShiftTypeSkillRequirementList(shiftTypeSkillRequirementList);
        }

        private void generateShiftList(NurseRoster nurseRoster) throws JDOMException {
            List<ShiftType> shiftTypeList = nurseRoster.getShiftTypeList();
            int shiftListSize = shiftDateMap.size() * shiftTypeList.size();
            List<Shift> shiftList = new ArrayList<>(shiftListSize);
            dateAndShiftTypeToShiftMap = new HashMap<>(shiftListSize);
            dayOfWeekAndShiftTypeToShiftListMap = new HashMap<>(7 * shiftTypeList.size());
            long id = 0L;
            int index = 0;
            for (ShiftDate shiftDate : nurseRoster.getShiftDateList()) {
                for (ShiftType shiftType : shiftTypeList) {
                    Shift shift = new Shift();
                    shift.setId(id);
                    shift.setShiftDate(shiftDate);
                    shiftDate.getShiftList().add(shift);
                    shift.setShiftType(shiftType);
                    shift.setIndex(index);
                    shift.setRequiredEmployeeSize(0); // Filled in later
                    shiftList.add(shift);
                    dateAndShiftTypeToShiftMap.put(Pair.of(shiftDate.getDate(), shiftType.getCode()), shift);
                    addShiftToDayOfWeekAndShiftTypeToShiftListMap(shiftDate, shiftType, shift);
                    id++;
                    index++;
                }
            }
            nurseRoster.setShiftList(shiftList);
        }

        private void addShiftToDayOfWeekAndShiftTypeToShiftListMap(ShiftDate shiftDate, ShiftType shiftType,
                Shift shift) {
            Pair<DayOfWeek, ShiftType> key = Pair.of(shiftDate.getDayOfWeek(), shiftType);
            List<Shift> dayOfWeekAndShiftTypeToShiftList = dayOfWeekAndShiftTypeToShiftListMap.computeIfAbsent(key,
                    k -> new ArrayList<>((shiftDateMap.size() + 6) / 7));
            dayOfWeekAndShiftTypeToShiftList.add(shift);
        }

        private void readPatternList(NurseRoster nurseRoster, Element patternsElement) throws JDOMException {
            List<Pattern> patternList;
            if (patternsElement == null) {
                patternList = Collections.emptyList();
            } else {
                List<Element> patternElementList = (List<Element>) patternsElement.getChildren();
                patternList = new ArrayList<>(patternElementList.size());
                patternMap = new HashMap<>(patternElementList.size());
                long id = 0L;
                long patternEntryId = 0L;
                for (Element element : patternElementList) {
                    assertElementName(element, "Pattern");
                    String code = element.getAttribute("ID").getValue();
                    int weight = element.getAttribute("weight").getIntValue();

                    List<Element> patternEntryElementList = (List<Element>) element.getChild("PatternEntries")
                            .getChildren();
                    if (patternEntryElementList.size() < 2) {
                        throw new IllegalArgumentException("The size of PatternEntries ("
                                + patternEntryElementList.size() + ") of pattern (" + code + ") should be at least 2.");
                    }
                    Pattern pattern;
                    if (patternEntryElementList.get(0).getChild("ShiftType").getText().equals("None")) {
                        pattern = new FreeBefore2DaysWithAWorkDayPattern();
                        if (patternEntryElementList.size() != 3) {
                            throw new IllegalStateException("boe");
                        }
                    } else if (patternEntryElementList.get(1).getChild("ShiftType").getText().equals("None")) {
                        pattern = new WorkBeforeFreeSequencePattern();
                        // TODO support this too (not needed for competition)
                        throw new UnsupportedOperationException("The pattern (" + code + ") is not supported."
                                + " None of the test data exhibits such a pattern.");
                    } else {
                        switch (patternEntryElementList.size()) {
                            case 2:
                                pattern = new ShiftType2DaysPattern();
                                break;
                            case 3:
                                pattern = new ShiftType3DaysPattern();
                                break;
                            default:
                                throw new IllegalArgumentException("A size of PatternEntries ("
                                        + patternEntryElementList.size() + ") of pattern (" + code
                                        + ") above 3 is not supported.");
                        }
                    }
                    pattern.setId(id);
                    pattern.setCode(code);
                    pattern.setWeight(weight);
                    int patternEntryIndex = 0;
                    DayOfWeek firstDayOfWeek = null;
                    for (Element patternEntryElement : patternEntryElementList) {
                        assertElementName(patternEntryElement, "PatternEntry");
                        Element shiftTypeElement = patternEntryElement.getChild("ShiftType");
                        boolean shiftTypeIsNone;
                        ShiftType shiftType;
                        if (shiftTypeElement.getText().equals("Any")) {
                            shiftTypeIsNone = false;
                            shiftType = null;
                        } else if (shiftTypeElement.getText().equals("None")) {
                            shiftTypeIsNone = true;
                            shiftType = null;
                        } else {
                            shiftTypeIsNone = false;
                            shiftType = shiftTypeMap.get(shiftTypeElement.getText());
                            if (shiftType == null) {
                                throw new IllegalArgumentException("The shiftType (" + shiftTypeElement.getText()
                                        + ") of pattern (" + pattern.getCode() + ") does not exist.");
                            }
                        }
                        Element dayElement = patternEntryElement.getChild("Day");
                        DayOfWeek dayOfWeek;
                        if (dayElement.getText().equals("Any")) {
                            dayOfWeek = null;
                        } else {
                            dayOfWeek = null;
                            for (DayOfWeek possibleDayOfWeek : DayOfWeek.values()) {
                                if (possibleDayOfWeek.name().equalsIgnoreCase(dayElement.getText())) {
                                    dayOfWeek = possibleDayOfWeek;
                                    break;
                                }
                            }
                            if (dayOfWeek == null) {
                                throw new IllegalArgumentException("The dayOfWeek (" + dayElement.getText()
                                        + ") of pattern (" + pattern.getCode() + ") does not exist.");
                            }
                        }
                        if (patternEntryIndex == 0) {
                            firstDayOfWeek = dayOfWeek;
                        } else {
                            if (firstDayOfWeek != null) {
                                int distance = dayOfWeek.getValue() - firstDayOfWeek.getValue();
                                if (distance < 0) {
                                    distance += 7;
                                }
                                if (distance != patternEntryIndex) {
                                    throw new IllegalArgumentException("On patternEntryIndex (" + patternEntryIndex
                                            + ") of pattern (" + pattern.getCode()
                                            + ") the dayOfWeek (" + dayOfWeek
                                            + ") is not valid with previous entries.");
                                }
                            } else {
                                if (dayOfWeek != null) {
                                    throw new IllegalArgumentException("On patternEntryIndex (" + patternEntryIndex
                                            + ") of pattern (" + pattern.getCode()
                                            + ") the dayOfWeek should be (Any), in line with previous entries.");
                                }
                            }
                        }
                        if (pattern instanceof FreeBefore2DaysWithAWorkDayPattern) {
                            FreeBefore2DaysWithAWorkDayPattern castedPattern = (FreeBefore2DaysWithAWorkDayPattern) pattern;
                            if (patternEntryIndex == 0) {
                                if (dayOfWeek == null) {
                                    // TODO Support an any dayOfWeek too (not needed for competition)
                                    throw new UnsupportedOperationException("On patternEntryIndex (" + patternEntryIndex
                                            + ") of FreeBeforeWorkSequence pattern (" + pattern.getCode()
                                            + ") the dayOfWeek should not be (Any)."
                                            + "\n None of the test data exhibits such a pattern.");
                                }
                                castedPattern.setFreeDayOfWeek(dayOfWeek);
                            }
                            if (patternEntryIndex == 1) {
                                if (shiftType != null) {
                                    // TODO Support a specific shiftType too (not needed for competition)
                                    throw new UnsupportedOperationException("On patternEntryIndex (" + patternEntryIndex
                                            + ") of FreeBeforeWorkSequence pattern (" + pattern.getCode()
                                            + ") the shiftType should be (Any)."
                                            + "\n None of the test data exhibits such a pattern.");
                                }
                                // castedPattern.setWorkShiftType(shiftType);
                                // castedPattern.setWorkDayLength(patternEntryElementList.size() - 1);
                            }
                            // if (patternEntryIndex > 1 && shiftType != castedPattern.getWorkShiftType()) {
                            //     throw new IllegalArgumentException("On patternEntryIndex (" + patternEntryIndex
                            //             + ") of FreeBeforeWorkSequence pattern (" + pattern.getCode()
                            //             + ") the shiftType (" + shiftType + ") should be ("
                            //             + castedPattern.getWorkShiftType() + ").");
                            // }
                            if (patternEntryIndex != 0 && shiftTypeIsNone) {
                                throw new IllegalArgumentException("On patternEntryIndex (" + patternEntryIndex
                                        + ") of FreeBeforeWorkSequence pattern (" + pattern.getCode()
                                        + ") the shiftType can not be (None).");
                            }
                        } else if (pattern instanceof WorkBeforeFreeSequencePattern) {
                            WorkBeforeFreeSequencePattern castedPattern = (WorkBeforeFreeSequencePattern) pattern;
                            if (patternEntryIndex == 0) {
                                castedPattern.setWorkDayOfWeek(dayOfWeek);
                                castedPattern.setWorkShiftType(shiftType);
                                castedPattern.setFreeDayLength(patternEntryElementList.size() - 1);
                            }
                            if (patternEntryIndex != 0 && !shiftTypeIsNone) {
                                throw new IllegalArgumentException("On patternEntryIndex (" + patternEntryIndex
                                        + ") of WorkBeforeFreeSequence pattern (" + pattern.getCode()
                                        + ") the shiftType should be (None).");
                            }
                        } else if (pattern instanceof ShiftType2DaysPattern) {
                            ShiftType2DaysPattern castedPattern = (ShiftType2DaysPattern) pattern;
                            if (patternEntryIndex == 0) {
                                if (dayOfWeek != null) {
                                    // TODO Support a specific dayOfWeek too (not needed for competition)
                                    throw new UnsupportedOperationException("On patternEntryIndex (" + patternEntryIndex
                                            + ") of FreeBeforeWorkSequence pattern (" + pattern.getCode()
                                            + ") the dayOfWeek should be (Any)."
                                            + "\n None of the test data exhibits such a pattern.");
                                }
                                // castedPattern.setStartDayOfWeek(dayOfWeek);
                            }
                            if (shiftType == null) {
                                // TODO Support any shiftType too (not needed for competition)
                                throw new UnsupportedOperationException("On patternEntryIndex (" + patternEntryIndex
                                        + ") of FreeBeforeWorkSequence pattern (" + pattern.getCode()
                                        + ") the shiftType should not be (Any)."
                                        + "\n None of the test data exhibits such a pattern.");
                            }
                            switch (patternEntryIndex) {
                                case 0:
                                    castedPattern.setDayIndex0ShiftType(shiftType);
                                    break;
                                case 1:
                                    castedPattern.setDayIndex1ShiftType(shiftType);
                                    break;
                                default:
                                    throw new IllegalArgumentException("The patternEntryIndex ("
                                            + patternEntryIndex + ") is not supported.");
                            }
                        } else if (pattern instanceof ShiftType3DaysPattern) {
                            ShiftType3DaysPattern castedPattern = (ShiftType3DaysPattern) pattern;
                            if (patternEntryIndex == 0) {
                                if (dayOfWeek != null) {
                                    // TODO Support a specific dayOfWeek too (not needed for competition)
                                    throw new UnsupportedOperationException("On patternEntryIndex (" + patternEntryIndex
                                            + ") of FreeBeforeWorkSequence pattern (" + pattern.getCode()
                                            + ") the dayOfWeek should be (Any)."
                                            + "\n None of the test data exhibits such a pattern.");
                                }
                                // castedPattern.setStartDayOfWeek(dayOfWeek);
                            }
                            if (shiftType == null) {
                                // TODO Support any shiftType too
                                throw new UnsupportedOperationException("On patternEntryIndex (" + patternEntryIndex
                                        + ") of FreeBeforeWorkSequence pattern (" + pattern.getCode()
                                        + ") the shiftType should not be (Any)."
                                        + "\n None of the test data exhibits such a pattern.");
                            }
                            switch (patternEntryIndex) {
                                case 0:
                                    castedPattern.setDayIndex0ShiftType(shiftType);
                                    break;
                                case 1:
                                    castedPattern.setDayIndex1ShiftType(shiftType);
                                    break;
                                case 2:
                                    castedPattern.setDayIndex2ShiftType(shiftType);
                                    break;
                                default:
                                    throw new IllegalArgumentException("The patternEntryIndex ("
                                            + patternEntryIndex + ") is not supported.");
                            }
                        } else {
                            throw new IllegalStateException("Unsupported patternClass (" + pattern.getClass() + ").");
                        }
                        patternEntryIndex++;
                    }
                    patternList.add(pattern);
                    if (patternMap.containsKey(pattern.getCode())) {
                        throw new IllegalArgumentException("There are 2 patterns with the same code ("
                                + pattern.getCode() + ").");
                    }
                    patternMap.put(pattern.getCode(), pattern);
                    id++;
                }
            }
            nurseRoster.setPatternList(patternList);
        }

        private void readContractList(NurseRoster nurseRoster, Element contractsElement) throws JDOMException {
            int contractLineTypeListSize = ContractLineType.values().length;
            List<Element> contractElementList = (List<Element>) contractsElement.getChildren();
            List<Contract> contractList = new ArrayList<>(contractElementList.size());
            contractMap = new HashMap<>(contractElementList.size());
            long id = 0L;
            List<ContractLine> contractLineList = new ArrayList<>(
                    contractElementList.size() * contractLineTypeListSize);
            long contractLineId = 0L;
            List<PatternContractLine> patternContractLineList = new ArrayList<>(
                    contractElementList.size() * 3);
            long patternContractLineId = 0L;
            for (Element element : contractElementList) {
                assertElementName(element, "Contract");
                Contract contract = new Contract();
                contract.setId(id);
                contract.setCode(element.getAttribute("ID").getValue());
                contract.setDescription(element.getChild("Description").getText());

                List<ContractLine> contractLineListOfContract = new ArrayList<>(contractLineTypeListSize);
                contractLineId = readBooleanContractLine(contract, contractLineList, contractLineListOfContract,
                        contractLineId, element.getChild("SingleAssignmentPerDay"),
                        ContractLineType.SINGLE_ASSIGNMENT_PER_DAY);
                contractLineId = readMinMaxContractLine(contract, contractLineList, contractLineListOfContract,
                        contractLineId, element.getChild("MinNumAssignments"),
                        element.getChild("MaxNumAssignments"),
                        ContractLineType.TOTAL_ASSIGNMENTS);
                contractLineId = readMinMaxContractLine(contract, contractLineList, contractLineListOfContract,
                        contractLineId, element.getChild("MinConsecutiveWorkingDays"),
                        element.getChild("MaxConsecutiveWorkingDays"),
                        ContractLineType.CONSECUTIVE_WORKING_DAYS);
                contractLineId = readMinMaxContractLine(contract, contractLineList, contractLineListOfContract,
                        contractLineId, element.getChild("MinConsecutiveFreeDays"),
                        element.getChild("MaxConsecutiveFreeDays"),
                        ContractLineType.CONSECUTIVE_FREE_DAYS);
                contractLineId = readMinMaxContractLine(contract, contractLineList, contractLineListOfContract,
                        contractLineId, element.getChild("MinConsecutiveWorkingWeekends"),
                        element.getChild("MaxConsecutiveWorkingWeekends"),
                        ContractLineType.CONSECUTIVE_WORKING_WEEKENDS);
                contractLineId = readMinMaxContractLine(contract, contractLineList, contractLineListOfContract,
                        contractLineId, null,
                        element.getChild("MaxWorkingWeekendsInFourWeeks"),
                        ContractLineType.TOTAL_WORKING_WEEKENDS_IN_FOUR_WEEKS);
                WeekendDefinition weekendDefinition = WeekendDefinition.valueOfCode(
                        element.getChild("WeekendDefinition").getText());
                contract.setWeekendDefinition(weekendDefinition);
                contractLineId = readBooleanContractLine(contract, contractLineList, contractLineListOfContract,
                        contractLineId, element.getChild("CompleteWeekends"),
                        ContractLineType.COMPLETE_WEEKENDS);
                contractLineId = readBooleanContractLine(contract, contractLineList, contractLineListOfContract,
                        contractLineId, element.getChild("IdenticalShiftTypesDuringWeekend"),
                        ContractLineType.IDENTICAL_SHIFT_TYPES_DURING_WEEKEND);
                contractLineId = readBooleanContractLine(contract, contractLineList, contractLineListOfContract,
                        contractLineId, element.getChild("NoNightShiftBeforeFreeWeekend"),
                        ContractLineType.NO_NIGHT_SHIFT_BEFORE_FREE_WEEKEND);
                contractLineId = readBooleanContractLine(contract, contractLineList, contractLineListOfContract,
                        contractLineId, element.getChild("AlternativeSkillCategory"),
                        ContractLineType.ALTERNATIVE_SKILL_CATEGORY);
                contract.setContractLineList(contractLineListOfContract);

                List<Element> unwantedPatternElementList = (List<Element>) element.getChild("UnwantedPatterns")
                        .getChildren();
                for (Element patternElement : unwantedPatternElementList) {
                    assertElementName(patternElement, "Pattern");
                    Pattern pattern = patternMap.get(patternElement.getText());
                    if (pattern == null) {
                        throw new IllegalArgumentException("The pattern (" + patternElement.getText()
                                + ") of contract (" + contract.getCode() + ") does not exist.");
                    }
                    PatternContractLine patternContractLine = new PatternContractLine();
                    patternContractLine.setId(patternContractLineId);
                    patternContractLine.setContract(contract);
                    patternContractLine.setPattern(pattern);
                    patternContractLineList.add(patternContractLine);
                    patternContractLineId++;
                }

                contractList.add(contract);
                if (contractMap.containsKey(contract.getCode())) {
                    throw new IllegalArgumentException("There are 2 contracts with the same code ("
                            + contract.getCode() + ").");
                }
                contractMap.put(contract.getCode(), contract);
                id++;
            }
            nurseRoster.setContractList(contractList);
            nurseRoster.setContractLineList(contractLineList);
            nurseRoster.setPatternContractLineList(patternContractLineList);
        }

        private long readBooleanContractLine(Contract contract, List<ContractLine> contractLineList,
                List<ContractLine> contractLineListOfContract, long contractLineId, Element element,
                ContractLineType contractLineType) throws DataConversionException {
            boolean enabled = Boolean.valueOf(element.getText());
            int weight;
            if (enabled) {
                weight = element.getAttribute("weight").getIntValue();
                if (weight < 0) {
                    throw new IllegalArgumentException("The weight (" + weight
                            + ") of contract (" + contract.getCode() + ") and contractLineType (" + contractLineType
                            + ") should be 0 or at least 1.");
                } else if (weight == 0) {
                    // If the weight is zero, the constraint should not be considered.
                    enabled = false;
                    logger.warn("In contract ({}), the contractLineType ({}) is enabled with weight 0.",
                            contract.getCode(), contractLineType);
                }
            } else {
                weight = 0;
            }
            if (enabled) {
                BooleanContractLine contractLine = new BooleanContractLine();
                contractLine.setId(contractLineId);
                contractLine.setContract(contract);
                contractLine.setContractLineType(contractLineType);
                contractLine.setEnabled(enabled);
                contractLine.setWeight(weight);
                contractLineList.add(contractLine);
                contractLineListOfContract.add(contractLine);
                contractLineId++;
            }
            return contractLineId;
        }

        private long readMinMaxContractLine(Contract contract, List<ContractLine> contractLineList,
                List<ContractLine> contractLineListOfContract, long contractLineId,
                Element minElement, Element maxElement,
                ContractLineType contractLineType) throws DataConversionException {
            boolean minimumEnabled = minElement == null ? false : minElement.getAttribute("on").getBooleanValue();
            int minimumWeight;
            if (minimumEnabled) {
                minimumWeight = minElement.getAttribute("weight").getIntValue();
                if (minimumWeight < 0) {
                    throw new IllegalArgumentException("The minimumWeight (" + minimumWeight
                            + ") of contract (" + contract.getCode() + ") and contractLineType (" + contractLineType
                            + ") should be 0 or at least 1.");
                } else if (minimumWeight == 0) {
                    // If the weight is zero, the constraint should not be considered.
                    minimumEnabled = false;
                    logger.warn("In contract ({}), the contractLineType ({}) minimum is enabled with weight 0.",
                            contract.getCode(), contractLineType);
                }
            } else {
                minimumWeight = 0;
            }
            boolean maximumEnabled = maxElement == null ? false : maxElement.getAttribute("on").getBooleanValue();
            int maximumWeight;
            if (maximumEnabled) {
                maximumWeight = maxElement.getAttribute("weight").getIntValue();
                if (maximumWeight < 0) {
                    throw new IllegalArgumentException("The maximumWeight (" + maximumWeight
                            + ") of contract (" + contract.getCode() + ") and contractLineType (" + contractLineType
                            + ") should be 0 or at least 1.");
                } else if (maximumWeight == 0) {
                    // If the weight is zero, the constraint should not be considered.
                    maximumEnabled = false;
                    logger.warn("In contract ({}), the contractLineType ({}) maximum is enabled with weight 0.",
                            contract.getCode(), contractLineType);
                }
            } else {
                maximumWeight = 0;
            }
            if (minimumEnabled || maximumEnabled) {
                MinMaxContractLine contractLine = new MinMaxContractLine();
                contractLine.setId(contractLineId);
                contractLine.setContract(contract);
                contractLine.setContractLineType(contractLineType);
                contractLine.setMinimumEnabled(minimumEnabled);
                if (minimumEnabled) {
                    int minimumValue = Integer.parseInt(minElement.getText());
                    if (minimumValue < 1) {
                        throw new IllegalArgumentException("The minimumValue (" + minimumValue
                                + ") of contract (" + contract.getCode() + ") and contractLineType ("
                                + contractLineType + ") should be at least 1.");
                    }
                    contractLine.setMinimumValue(minimumValue);
                    contractLine.setMinimumWeight(minimumWeight);
                }
                contractLine.setMaximumEnabled(maximumEnabled);
                if (maximumEnabled) {
                    int maximumValue = Integer.parseInt(maxElement.getText());
                    if (maximumValue < 0) {
                        throw new IllegalArgumentException("The maximumValue (" + maximumValue
                                + ") of contract (" + contract.getCode() + ") and contractLineType ("
                                + contractLineType + ") should be at least 0.");
                    }
                    contractLine.setMaximumValue(maximumValue);
                    contractLine.setMaximumWeight(maximumWeight);
                }
                contractLineList.add(contractLine);
                contractLineListOfContract.add(contractLine);
                contractLineId++;
            }
            return contractLineId;
        }

        private void readEmployeeList(NurseRoster nurseRoster, Element employeesElement) throws JDOMException {
            List<Element> employeeElementList = (List<Element>) employeesElement.getChildren();
            List<Employee> employeeList = new ArrayList<>(employeeElementList.size());
            employeeMap = new HashMap<>(employeeElementList.size());
            long id = 0L;
            List<SkillProficiency> skillProficiencyList
                    = new ArrayList<>(employeeElementList.size() * 2);
            long skillProficiencyId = 0L;
            for (Element element : employeeElementList) {
                assertElementName(element, "Employee");
                Employee employee = new Employee();
                employee.setId(id);
                employee.setCode(element.getAttribute("ID").getValue());
                employee.setName(element.getChild("Name").getText());
                Element contractElement = element.getChild("ContractID");
                Contract contract = contractMap.get(contractElement.getText());
                if (contract == null) {
                    throw new IllegalArgumentException("The contract (" + contractElement.getText()
                            + ") of employee (" + employee.getCode() + ") does not exist.");
                }
                employee.setContract(contract);
                int estimatedRequestSize = (shiftDateMap.size() / employeeElementList.size()) + 1;
                employee.setDayOffRequestMap(new HashMap<>(estimatedRequestSize));
                employee.setDayOnRequestMap(new HashMap<>(estimatedRequestSize));
                employee.setShiftOffRequestMap(new HashMap<>(estimatedRequestSize));
                employee.setShiftOnRequestMap(new HashMap<>(estimatedRequestSize));

                Element skillsElement = element.getChild("Skills");
                if (skillsElement != null) {
                    List<Element> skillElementList = (List<Element>) skillsElement.getChildren();
                    for (Element skillElement : skillElementList) {
                        assertElementName(skillElement, "Skill");
                        Skill skill = skillMap.get(skillElement.getText());
                        if (skill == null) {
                            throw new IllegalArgumentException("The skill (" + skillElement.getText()
                                    + ") of employee (" + employee.getCode() + ") does not exist.");
                        }
                        SkillProficiency skillProficiency = new SkillProficiency();
                        skillProficiency.setId(skillProficiencyId);
                        skillProficiency.setEmployee(employee);
                        skillProficiency.setSkill(skill);
                        skillProficiencyList.add(skillProficiency);
                        skillProficiencyId++;
                    }
                }

                employeeList.add(employee);
                if (employeeMap.containsKey(employee.getCode())) {
                    throw new IllegalArgumentException("There are 2 employees with the same code ("
                            + employee.getCode() + ").");
                }
                employeeMap.put(employee.getCode(), employee);
                id++;
            }
            nurseRoster.setEmployeeList(employeeList);
            nurseRoster.setSkillProficiencyList(skillProficiencyList);
        }

        private void readRequiredEmployeeSizes(NurseRoster nurseRoster, Element coverRequirementsElement) {
            List<Element> coverRequirementElementList = (List<Element>) coverRequirementsElement.getChildren();
            for (Element element : coverRequirementElementList) {
                if (element.getName().equals("DayOfWeekCover")) {
                    Element dayOfWeekElement = element.getChild("Day");
                    DayOfWeek dayOfWeek = null;
                    for (DayOfWeek possibleDayOfWeek : DayOfWeek.values()) {
                        if (possibleDayOfWeek.name().equalsIgnoreCase(dayOfWeekElement.getText())) {
                            dayOfWeek = possibleDayOfWeek;
                            break;
                        }
                    }
                    if (dayOfWeek == null) {
                        throw new IllegalArgumentException("The dayOfWeek (" + dayOfWeekElement.getText()
                                + ") of an entity DayOfWeekCover does not exist.");
                    }

                    List<Element> coverElementList = (List<Element>) element.getChildren("Cover");
                    for (Element coverElement : coverElementList) {
                        Element shiftTypeElement = coverElement.getChild("Shift");
                        ShiftType shiftType = shiftTypeMap.get(shiftTypeElement.getText());
                        if (shiftType == null) {
                            if (shiftTypeElement.getText().equals("Any")) {
                                throw new IllegalStateException("The shiftType Any is not supported on DayOfWeekCover.");
                            } else if (shiftTypeElement.getText().equals("None")) {
                                throw new IllegalStateException("The shiftType None is not supported on DayOfWeekCover.");
                            } else {
                                throw new IllegalArgumentException("The shiftType (" + shiftTypeElement.getText()
                                        + ") of an entity DayOfWeekCover does not exist.");
                            }
                        }
                        Pair<DayOfWeek, ShiftType> key = Pair.of(dayOfWeek, shiftType);
                        List<Shift> shiftList = dayOfWeekAndShiftTypeToShiftListMap.get(key);
                        if (shiftList == null) {
                            throw new IllegalArgumentException("The dayOfWeek (" + dayOfWeekElement.getText()
                                    + ") with the shiftType (" + shiftTypeElement.getText()
                                    + ") of an entity DayOfWeekCover does not have any shifts.");
                        }
                        int requiredEmployeeSize = Integer.parseInt(coverElement.getChild("Preferred").getText());
                        for (Shift shift : shiftList) {
                            shift.setRequiredEmployeeSize(shift.getRequiredEmployeeSize() + requiredEmployeeSize);
                        }
                    }
                } else if (element.getName().equals("DateSpecificCover")) {
                    Element dateElement = element.getChild("Date");
                    List<Element> coverElementList = (List<Element>) element.getChildren("Cover");
                    for (Element coverElement : coverElementList) {
                        Element shiftTypeElement = coverElement.getChild("Shift");
                        LocalDate date = LocalDate.parse(dateElement.getText(), DateTimeFormatter.ISO_DATE);
                        Shift shift = dateAndShiftTypeToShiftMap.get(Pair.of(date, shiftTypeElement.getText()));
                        if (shift == null) {
                            throw new IllegalArgumentException("The date (" + dateElement.getText()
                                    + ") with the shiftType (" + shiftTypeElement.getText()
                                    + ") of an entity DateSpecificCover does not have a shift.");
                        }
                        int requiredEmployeeSize = Integer.parseInt(coverElement.getChild("Preferred").getText());
                        shift.setRequiredEmployeeSize(shift.getRequiredEmployeeSize() + requiredEmployeeSize);
                    }
                } else {
                    throw new IllegalArgumentException("Unknown cover entity (" + element.getName() + ").");
                }
            }
        }

        private void readDayOffRequestList(NurseRoster nurseRoster, Element dayOffRequestsElement) throws JDOMException {
            List<DayOffRequest> dayOffRequestList;
            if (dayOffRequestsElement == null) {
                dayOffRequestList = Collections.emptyList();
            } else {
                List<Element> dayOffElementList = (List<Element>) dayOffRequestsElement.getChildren();
                dayOffRequestList = new ArrayList<>(dayOffElementList.size());
                long id = 0L;
                for (Element element : dayOffElementList) {
                    assertElementName(element, "DayOff");
                    DayOffRequest dayOffRequest = new DayOffRequest();
                    dayOffRequest.setId(id);

                    Element employeeElement = element.getChild("EmployeeID");
                    Employee employee = employeeMap.get(employeeElement.getText());
                    if (employee == null) {
                        throw new IllegalArgumentException("The shiftDate (" + employeeElement.getText()
                                + ") of dayOffRequest (" + dayOffRequest + ") does not exist.");
                    }
                    dayOffRequest.setEmployee(employee);

                    Element dateElement = element.getChild("Date");
                    ShiftDate shiftDate = shiftDateMap.get(LocalDate.parse(dateElement.getText(), DateTimeFormatter.ISO_DATE));
                    if (shiftDate == null) {
                        throw new IllegalArgumentException("The date (" + dateElement.getText()
                                + ") of dayOffRequest (" + dayOffRequest + ") does not exist.");
                    }
                    dayOffRequest.setShiftDate(shiftDate);

                    dayOffRequest.setWeight(element.getAttribute("weight").getIntValue());

                    dayOffRequestList.add(dayOffRequest);
                    employee.getDayOffRequestMap().put(shiftDate, dayOffRequest);
                    id++;
                }
            }
            nurseRoster.setDayOffRequestList(dayOffRequestList);
        }

        private void readDayOnRequestList(NurseRoster nurseRoster, Element dayOnRequestsElement) throws JDOMException {
            List<DayOnRequest> dayOnRequestList;
            if (dayOnRequestsElement == null) {
                dayOnRequestList = Collections.emptyList();
            } else {
                List<Element> dayOnElementList = (List<Element>) dayOnRequestsElement.getChildren();
                dayOnRequestList = new ArrayList<>(dayOnElementList.size());
                long id = 0L;
                for (Element element : dayOnElementList) {
                    assertElementName(element, "DayOn");
                    DayOnRequest dayOnRequest = new DayOnRequest();
                    dayOnRequest.setId(id);

                    Element employeeElement = element.getChild("EmployeeID");
                    Employee employee = employeeMap.get(employeeElement.getText());
                    if (employee == null) {
                        throw new IllegalArgumentException("The shiftDate (" + employeeElement.getText()
                                + ") of dayOnRequest (" + dayOnRequest + ") does not exist.");
                    }
                    dayOnRequest.setEmployee(employee);

                    Element dateElement = element.getChild("Date");
                    ShiftDate shiftDate = shiftDateMap.get(LocalDate.parse(dateElement.getText(), DateTimeFormatter.ISO_DATE));
                    if (shiftDate == null) {
                        throw new IllegalArgumentException("The date (" + dateElement.getText()
                                + ") of dayOnRequest (" + dayOnRequest + ") does not exist.");
                    }
                    dayOnRequest.setShiftDate(shiftDate);

                    dayOnRequest.setWeight(element.getAttribute("weight").getIntValue());

                    dayOnRequestList.add(dayOnRequest);
                    employee.getDayOnRequestMap().put(shiftDate, dayOnRequest);
                    id++;
                }
            }
            nurseRoster.setDayOnRequestList(dayOnRequestList);
        }

        private void readShiftOffRequestList(NurseRoster nurseRoster, Element shiftOffRequestsElement) throws JDOMException {
            List<ShiftOffRequest> shiftOffRequestList;
            if (shiftOffRequestsElement == null) {
                shiftOffRequestList = Collections.emptyList();
            } else {
                List<Element> shiftOffElementList = (List<Element>) shiftOffRequestsElement.getChildren();
                shiftOffRequestList = new ArrayList<>(shiftOffElementList.size());
                long id = 0L;
                for (Element element : shiftOffElementList) {
                    assertElementName(element, "ShiftOff");
                    ShiftOffRequest shiftOffRequest = new ShiftOffRequest();
                    shiftOffRequest.setId(id);

                    Element employeeElement = element.getChild("EmployeeID");
                    Employee employee = employeeMap.get(employeeElement.getText());
                    if (employee == null) {
                        throw new IllegalArgumentException("The shift (" + employeeElement.getText()
                                + ") of shiftOffRequest (" + shiftOffRequest + ") does not exist.");
                    }
                    shiftOffRequest.setEmployee(employee);

                    Element dateElement = element.getChild("Date");
                    Element shiftTypeElement = element.getChild("ShiftTypeID");
                    LocalDate date = LocalDate.parse(dateElement.getText(), DateTimeFormatter.ISO_DATE);
                    Shift shift = dateAndShiftTypeToShiftMap.get(Pair.of(date, shiftTypeElement.getText()));
                    if (shift == null) {
                        throw new IllegalArgumentException("The date (" + dateElement.getText()
                                + ") or the shiftType (" + shiftTypeElement.getText()
                                + ") of shiftOffRequest (" + shiftOffRequest + ") does not exist.");
                    }
                    shiftOffRequest.setShift(shift);

                    shiftOffRequest.setWeight(element.getAttribute("weight").getIntValue());

                    shiftOffRequestList.add(shiftOffRequest);
                    employee.getShiftOffRequestMap().put(shift, shiftOffRequest);
                    id++;
                }
            }
            nurseRoster.setShiftOffRequestList(shiftOffRequestList);
        }

        private void readShiftOnRequestList(NurseRoster nurseRoster, Element shiftOnRequestsElement) throws JDOMException {
            List<ShiftOnRequest> shiftOnRequestList;
            if (shiftOnRequestsElement == null) {
                shiftOnRequestList = Collections.emptyList();
            } else {
                List<Element> shiftOnElementList = (List<Element>) shiftOnRequestsElement.getChildren();
                shiftOnRequestList = new ArrayList<>(shiftOnElementList.size());
                long id = 0L;
                for (Element element : shiftOnElementList) {
                    assertElementName(element, "ShiftOn");
                    ShiftOnRequest shiftOnRequest = new ShiftOnRequest();
                    shiftOnRequest.setId(id);

                    Element employeeElement = element.getChild("EmployeeID");
                    Employee employee = employeeMap.get(employeeElement.getText());
                    if (employee == null) {
                        throw new IllegalArgumentException("The shift (" + employeeElement.getText()
                                + ") of shiftOnRequest (" + shiftOnRequest + ") does not exist.");
                    }
                    shiftOnRequest.setEmployee(employee);

                    Element dateElement = element.getChild("Date");
                    Element shiftTypeElement = element.getChild("ShiftTypeID");
                    LocalDate date = LocalDate.parse(dateElement.getText(), DateTimeFormatter.ISO_DATE);
                    Shift shift = dateAndShiftTypeToShiftMap.get(Pair.of(date, shiftTypeElement.getText()));
                    if (shift == null) {
                        throw new IllegalArgumentException("The date (" + dateElement.getText()
                                + ") or the shiftType (" + shiftTypeElement.getText()
                                + ") of shiftOnRequest (" + shiftOnRequest + ") does not exist.");
                    }
                    shiftOnRequest.setShift(shift);

                    shiftOnRequest.setWeight(element.getAttribute("weight").getIntValue());

                    shiftOnRequestList.add(shiftOnRequest);
                    employee.getShiftOnRequestMap().put(shift, shiftOnRequest);
                    id++;
                }
            }
            nurseRoster.setShiftOnRequestList(shiftOnRequestList);
        }

        private void createShiftAssignmentList(NurseRoster nurseRoster) {
            List<Shift> shiftList = nurseRoster.getShiftList();
            List<ShiftAssignment> shiftAssignmentList = new ArrayList<>(shiftList.size());
            long id = 0L;
            for (Shift shift : shiftList) {
                for (int i = 0; i < shift.getRequiredEmployeeSize(); i++) {
                    ShiftAssignment shiftAssignment = new ShiftAssignment();
                    shiftAssignment.setId(id);
                    id++;
                    shiftAssignment.setShift(shift);
                    shiftAssignment.setIndexInShift(i);
                    // Notice that we leave the PlanningVariable properties on null
                    shiftAssignmentList.add(shiftAssignment);
                }
            }
            nurseRoster.setShiftAssignmentList(shiftAssignmentList);
        }

    }

}
