package org.optaplanner.examples.nurserostering.persistence;

import static java.time.temporal.ChronoUnit.DAYS;

import java.io.IOException;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jdom2.DataConversionException;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.optaplanner.examples.common.persistence.AbstractXmlSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;
import org.optaplanner.examples.common.util.Pair;
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

public class NurseRosteringImporter extends AbstractXmlSolutionImporter<NurseRoster> {

    public static void main(String[] args) {
        SolutionConverter<NurseRoster> converter = SolutionConverter.createImportConverter(NurseRosteringApp.DATA_DIR_NAME,
                new NurseRosteringImporter(), new NurseRosterSolutionFileIO());
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
            // Note: javax.xml is terrible. JDom is much, much easier.

            Element schedulingPeriodElement = document.getRootElement();
            assertElementName(schedulingPeriodElement, "SchedulingPeriod");
            NurseRoster nurseRoster = new NurseRoster(0L);
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
            readRequiredEmployeeSizes(schedulingPeriodElement.getChild("CoverRequirements"));
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

        private void generateShiftDateList(NurseRoster nurseRoster, Element startDateElement, Element endDateElement) {
            LocalDate startDate;
            try {
                startDate = LocalDate.parse(startDateElement.getText(), DateTimeFormatter.ISO_DATE);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid startDate (" + startDateElement.getText() + ").", e);
            }
            LocalDate endDate;
            try {
                endDate = LocalDate.parse(endDateElement.getText(), DateTimeFormatter.ISO_DATE);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid endDate (" + endDateElement.getText() + ").", e);
            }
            if (startDate.compareTo(endDate) >= 0) {
                throw new IllegalStateException("The startDate (" + startDate + " must be before endDate (" + endDate + ").");
            }
            int maxDayIndex = Math.toIntExact(DAYS.between(startDate, endDate));
            int shiftDateSize = maxDayIndex + 1;
            List<ShiftDate> shiftDateList = new ArrayList<>(shiftDateSize);
            shiftDateMap = new LinkedHashMap<>(shiftDateSize);
            long id = 0L;
            int dayIndex = 0;
            LocalDate date = startDate;
            for (int i = 0; i < shiftDateSize; i++) {
                ShiftDate shiftDate = new ShiftDate(id, dayIndex, date);
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
            NurseRosterParametrization nurseRosterParametrization = new NurseRosterParametrization(0L,
                    shiftDateList.get(0), shiftDateList.get(shiftDateList.size() - 1), shiftDateList.get(0));
            nurseRoster.setNurseRosterParametrization(nurseRosterParametrization);
        }

        private void readSkillList(NurseRoster nurseRoster, Element skillsElement) {
            List<Skill> skillList;
            if (skillsElement == null) {
                skillList = Collections.emptyList();
            } else {
                List<Element> skillElementList = skillsElement.getChildren();
                skillList = new ArrayList<>(skillElementList.size());
                skillMap = new LinkedHashMap<>(skillElementList.size());
                long id = 0L;
                for (Element element : skillElementList) {
                    assertElementName(element, "Skill");
                    Skill skill = new Skill(id, element.getText());
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

        private void readShiftTypeList(NurseRoster nurseRoster, Element shiftTypesElement) {
            List<Element> shiftTypeElementList = shiftTypesElement.getChildren();
            List<ShiftType> shiftTypeList = new ArrayList<>(shiftTypeElementList.size());
            shiftTypeMap = new LinkedHashMap<>(shiftTypeElementList.size());
            long id = 0L;
            int index = 0;
            List<ShiftTypeSkillRequirement> shiftTypeSkillRequirementList = new ArrayList<>(shiftTypeElementList.size() * 2);
            long shiftTypeSkillRequirementId = 0L;
            for (Element element : shiftTypeElementList) {
                assertElementName(element, "Shift");
                String startTimeString = element.getChild("StartTime").getText();
                String endTimeString = element.getChild("EndTime").getText();
                ShiftType shiftType = new ShiftType(id, element.getAttribute("ID").getValue(), index,
                        startTimeString, endTimeString, startTimeString.compareTo(endTimeString) > 0,
                        element.getChild("Description").getText());

                Element skillsElement = element.getChild("Skills");
                if (skillsElement != null) {
                    List<Element> skillElementList = skillsElement.getChildren();
                    for (Element skillElement : skillElementList) {
                        assertElementName(skillElement, "Skill");
                        Skill skill = skillMap.get(skillElement.getText());
                        if (skill == null) {
                            throw new IllegalArgumentException("The skill (" + skillElement.getText()
                                    + ") of shiftType (" + shiftType.getCode() + ") does not exist.");
                        }
                        ShiftTypeSkillRequirement shiftTypeSkillRequirement =
                                new ShiftTypeSkillRequirement(shiftTypeSkillRequirementId, shiftType, skill);
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

        private void generateShiftList(NurseRoster nurseRoster) {
            List<ShiftType> shiftTypeList = nurseRoster.getShiftTypeList();
            int shiftListSize = shiftDateMap.size() * shiftTypeList.size();
            List<Shift> shiftList = new ArrayList<>(shiftListSize);
            dateAndShiftTypeToShiftMap = new LinkedHashMap<>(shiftListSize);
            dayOfWeekAndShiftTypeToShiftListMap = new LinkedHashMap<>(7 * shiftTypeList.size());
            long id = 0L;
            int index = 0;
            for (ShiftDate shiftDate : nurseRoster.getShiftDateList()) {
                for (ShiftType shiftType : shiftTypeList) {
                    // Required employee size filled in later.
                    Shift shift = new Shift(id, shiftDate, shiftType, index, 0);
                    shiftDate.getShiftList().add(shift);
                    shiftList.add(shift);
                    dateAndShiftTypeToShiftMap.put(Pair.of(shiftDate.getDate(), shiftType.getCode()), shift);
                    addShiftToDayOfWeekAndShiftTypeToShiftListMap(shiftDate, shiftType, shift);
                    id++;
                    index++;
                }
            }
            nurseRoster.setShiftList(shiftList);
        }

        private void addShiftToDayOfWeekAndShiftTypeToShiftListMap(ShiftDate shiftDate, ShiftType shiftType, Shift shift) {
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
                List<Element> patternElementList = patternsElement.getChildren();
                patternList = new ArrayList<>(patternElementList.size());
                patternMap = new LinkedHashMap<>(patternElementList.size());
                long id = 0L;
                for (Element element : patternElementList) {
                    assertElementName(element, "Pattern");
                    String code = element.getAttribute("ID").getValue();
                    int weight = element.getAttribute("weight").getIntValue();

                    List<Element> patternEntryElementList = element.getChild("PatternEntries")
                            .getChildren();
                    if (patternEntryElementList.size() < 2) {
                        throw new IllegalArgumentException("The size of PatternEntries ("
                                + patternEntryElementList.size() + ") of pattern (" + code + ") should be at least 2.");
                    }
                    Pattern pattern;
                    if (patternEntryElementList.get(0).getChild("ShiftType").getText().equals("None")) {
                        pattern = new FreeBefore2DaysWithAWorkDayPattern(id, code);
                        if (patternEntryElementList.size() != 3) {
                            throw new IllegalStateException("boe");
                        }
                    } else if (patternEntryElementList.get(1).getChild("ShiftType").getText().equals("None")) {
                        throw new UnsupportedOperationException("The pattern (" + code + ") is not supported."
                                + " None of the test data exhibits such a pattern.");
                    } else {
                        switch (patternEntryElementList.size()) {
                            case 2:
                                pattern = new ShiftType2DaysPattern(id, code);
                                break;
                            case 3:
                                pattern = new ShiftType3DaysPattern(id, code);
                                break;
                            default:
                                throw new IllegalArgumentException("A size of PatternEntries ("
                                        + patternEntryElementList.size() + ") of pattern (" + code
                                        + ") above 3 is not supported.");
                        }
                    }
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
                                    throw new UnsupportedOperationException("On patternEntryIndex (" + patternEntryIndex
                                            + ") of FreeBeforeWorkSequence pattern (" + pattern.getCode()
                                            + ") the dayOfWeek should not be (Any)."
                                            + "\n None of the test data exhibits such a pattern.");
                                }
                                castedPattern.setFreeDayOfWeek(dayOfWeek);
                            }
                            if (patternEntryIndex == 1) {
                                if (shiftType != null) {
                                    throw new UnsupportedOperationException("On patternEntryIndex (" + patternEntryIndex
                                            + ") of FreeBeforeWorkSequence pattern (" + pattern.getCode()
                                            + ") the shiftType should be (Any)."
                                            + "\n None of the test data exhibits such a pattern.");
                                }
                            }
                            if (patternEntryIndex != 0 && shiftTypeIsNone) {
                                throw new IllegalArgumentException("On patternEntryIndex (" + patternEntryIndex
                                        + ") of FreeBeforeWorkSequence pattern (" + pattern.getCode()
                                        + ") the shiftType cannot be (None).");
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
                                    throw new UnsupportedOperationException("On patternEntryIndex (" + patternEntryIndex
                                            + ") of FreeBeforeWorkSequence pattern (" + pattern.getCode()
                                            + ") the dayOfWeek should be (Any)."
                                            + "\n None of the test data exhibits such a pattern.");
                                }
                            }
                            if (shiftType == null) {
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
                                    throw new UnsupportedOperationException("On patternEntryIndex (" + patternEntryIndex
                                            + ") of FreeBeforeWorkSequence pattern (" + pattern.getCode()
                                            + ") the dayOfWeek should be (Any)."
                                            + "\n None of the test data exhibits such a pattern.");
                                }
                            }
                            if (shiftType == null) {
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
            List<Element> contractElementList = contractsElement.getChildren();
            List<Contract> contractList = new ArrayList<>(contractElementList.size());
            contractMap = new LinkedHashMap<>(contractElementList.size());
            long id = 0L;
            List<ContractLine> contractLineList = new ArrayList<>(
                    contractElementList.size() * contractLineTypeListSize);
            long contractLineId = 0L;
            List<PatternContractLine> patternContractLineList = new ArrayList<>(
                    contractElementList.size() * 3);
            long patternContractLineId = 0L;
            for (Element element : contractElementList) {
                assertElementName(element, "Contract");
                Contract contract = new Contract(id, element.getAttribute("ID").getValue(),
                        element.getChild("Description").getText());

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

                List<Element> unwantedPatternElementList = element.getChild("UnwantedPatterns")
                        .getChildren();
                for (Element patternElement : unwantedPatternElementList) {
                    assertElementName(patternElement, "Pattern");
                    Pattern pattern = patternMap.get(patternElement.getText());
                    if (pattern == null) {
                        throw new IllegalArgumentException("The pattern (" + patternElement.getText()
                                + ") of contract (" + contract.getCode() + ") does not exist.");
                    }
                    PatternContractLine patternContractLine = new PatternContractLine(patternContractLineId, contract, pattern);
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
            boolean enabled = Boolean.parseBoolean(element.getText());
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
                BooleanContractLine contractLine =
                        new BooleanContractLine(contractLineId, contract, contractLineType, enabled, weight);
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
            boolean minimumEnabled = minElement != null && minElement.getAttribute("on").getBooleanValue();
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
            boolean maximumEnabled = maxElement != null && maxElement.getAttribute("on").getBooleanValue();
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
                MinMaxContractLine contractLine =
                        new MinMaxContractLine(contractLineId, contract, contractLineType, minimumEnabled, maximumEnabled);
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

        private void readEmployeeList(NurseRoster nurseRoster, Element employeesElement) {
            List<Element> employeeElementList = employeesElement.getChildren();
            List<Employee> employeeList = new ArrayList<>(employeeElementList.size());
            employeeMap = new LinkedHashMap<>(employeeElementList.size());
            long id = 0L;
            List<SkillProficiency> skillProficiencyList = new ArrayList<>(employeeElementList.size() * 2);
            long skillProficiencyId = 0L;
            for (Element element : employeeElementList) {
                assertElementName(element, "Employee");
                String code = element.getAttribute("ID").getValue();
                Element contractElement = element.getChild("ContractID");
                Contract contract = contractMap.get(contractElement.getText());
                if (contract == null) {
                    throw new IllegalArgumentException("The contract (" + contractElement.getText()
                            + ") of employee (" + code + ") does not exist.");
                }
                Employee employee = new Employee(id, code, element.getChild("Name").getText(), contract);
                int estimatedRequestSize = (shiftDateMap.size() / employeeElementList.size()) + 1;
                employee.setDayOffRequestMap(new LinkedHashMap<>(estimatedRequestSize));
                employee.setDayOnRequestMap(new LinkedHashMap<>(estimatedRequestSize));
                employee.setShiftOffRequestMap(new LinkedHashMap<>(estimatedRequestSize));
                employee.setShiftOnRequestMap(new LinkedHashMap<>(estimatedRequestSize));

                Element skillsElement = element.getChild("Skills");
                if (skillsElement != null) {
                    List<Element> skillElementList = skillsElement.getChildren();
                    for (Element skillElement : skillElementList) {
                        assertElementName(skillElement, "Skill");
                        Skill skill = skillMap.get(skillElement.getText());
                        if (skill == null) {
                            throw new IllegalArgumentException("The skill (" + skillElement.getText()
                                    + ") of employee (" + employee.getCode() + ") does not exist.");
                        }
                        SkillProficiency skillProficiency = new SkillProficiency(skillProficiencyId, employee, skill);
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

        private void readRequiredEmployeeSizes(Element coverRequirementsElement) {
            List<Element> coverRequirementElementList = coverRequirementsElement.getChildren();
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

                    List<Element> coverElementList = element.getChildren("Cover");
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
                    List<Element> coverElementList = element.getChildren("Cover");
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
                List<Element> dayOffElementList = dayOffRequestsElement.getChildren();
                dayOffRequestList = new ArrayList<>(dayOffElementList.size());
                long id = 0L;
                for (Element element : dayOffElementList) {
                    assertElementName(element, "DayOff");

                    Element employeeElement = element.getChild("EmployeeID");
                    Employee employee = employeeMap.get(employeeElement.getText());
                    if (employee == null) {
                        throw new IllegalArgumentException("The shiftDate (" + employeeElement.getText()
                                + ") of dayOffRequest (" + id + ") does not exist.");
                    }

                    Element dateElement = element.getChild("Date");
                    ShiftDate shiftDate = shiftDateMap.get(LocalDate.parse(dateElement.getText(), DateTimeFormatter.ISO_DATE));
                    if (shiftDate == null) {
                        throw new IllegalArgumentException("The date (" + dateElement.getText()
                                + ") of dayOffRequest (" + id + ") does not exist.");
                    }

                    DayOffRequest dayOffRequest =
                            new DayOffRequest(id, employee, shiftDate, element.getAttribute("weight").getIntValue());
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
                List<Element> dayOnElementList = dayOnRequestsElement.getChildren();
                dayOnRequestList = new ArrayList<>(dayOnElementList.size());
                long id = 0L;
                for (Element element : dayOnElementList) {
                    assertElementName(element, "DayOn");

                    Element employeeElement = element.getChild("EmployeeID");
                    Employee employee = employeeMap.get(employeeElement.getText());
                    if (employee == null) {
                        throw new IllegalArgumentException("The shiftDate (" + employeeElement.getText()
                                + ") of dayOnRequest (" + id + ") does not exist.");
                    }

                    Element dateElement = element.getChild("Date");
                    ShiftDate shiftDate = shiftDateMap.get(LocalDate.parse(dateElement.getText(), DateTimeFormatter.ISO_DATE));
                    if (shiftDate == null) {
                        throw new IllegalArgumentException("The date (" + dateElement.getText()
                                + ") of dayOnRequest (" + id + ") does not exist.");
                    }

                    DayOnRequest dayOnRequest =
                            new DayOnRequest(id, employee, shiftDate, element.getAttribute("weight").getIntValue());
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
                List<Element> shiftOffElementList = shiftOffRequestsElement.getChildren();
                shiftOffRequestList = new ArrayList<>(shiftOffElementList.size());
                long id = 0L;
                for (Element element : shiftOffElementList) {
                    assertElementName(element, "ShiftOff");

                    Element employeeElement = element.getChild("EmployeeID");
                    Employee employee = employeeMap.get(employeeElement.getText());
                    if (employee == null) {
                        throw new IllegalArgumentException("The shift (" + employeeElement.getText()
                                + ") of shiftOffRequest (" + id + ") does not exist.");
                    }

                    Element dateElement = element.getChild("Date");
                    Element shiftTypeElement = element.getChild("ShiftTypeID");
                    LocalDate date = LocalDate.parse(dateElement.getText(), DateTimeFormatter.ISO_DATE);
                    Shift shift = dateAndShiftTypeToShiftMap.get(Pair.of(date, shiftTypeElement.getText()));
                    if (shift == null) {
                        throw new IllegalArgumentException("The date (" + dateElement.getText()
                                + ") or the shiftType (" + shiftTypeElement.getText()
                                + ") of shiftOffRequest (" + id + ") does not exist.");
                    }

                    ShiftOffRequest shiftOffRequest =
                            new ShiftOffRequest(id, employee, shift, element.getAttribute("weight").getIntValue());
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
                List<Element> shiftOnElementList = shiftOnRequestsElement.getChildren();
                shiftOnRequestList = new ArrayList<>(shiftOnElementList.size());
                long id = 0L;
                for (Element element : shiftOnElementList) {
                    assertElementName(element, "ShiftOn");

                    Element employeeElement = element.getChild("EmployeeID");
                    Employee employee = employeeMap.get(employeeElement.getText());
                    if (employee == null) {
                        throw new IllegalArgumentException("The shift (" + employeeElement.getText()
                                + ") of shiftOnRequest (" + id + ") does not exist.");
                    }

                    Element dateElement = element.getChild("Date");
                    Element shiftTypeElement = element.getChild("ShiftTypeID");
                    LocalDate date = LocalDate.parse(dateElement.getText(), DateTimeFormatter.ISO_DATE);
                    Shift shift = dateAndShiftTypeToShiftMap.get(Pair.of(date, shiftTypeElement.getText()));
                    if (shift == null) {
                        throw new IllegalArgumentException("The date (" + dateElement.getText()
                                + ") or the shiftType (" + shiftTypeElement.getText()
                                + ") of shiftOnRequest (" + id + ") does not exist.");
                    }

                    ShiftOnRequest shiftOnRequest =
                            new ShiftOnRequest(id, employee, shift, element.getAttribute("weight").getIntValue());
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
                    ShiftAssignment shiftAssignment = new ShiftAssignment(id, shift, i);
                    id++;
                    // Notice that we leave the PlanningVariable properties on null
                    shiftAssignmentList.add(shiftAssignment);
                }
            }
            nurseRoster.setShiftAssignmentList(shiftAssignmentList);
        }

    }

}
