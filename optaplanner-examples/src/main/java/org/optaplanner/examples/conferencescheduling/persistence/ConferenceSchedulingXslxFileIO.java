/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.conferencescheduling.persistence;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceParametrization;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.examples.conferencescheduling.domain.Room;
import org.optaplanner.examples.conferencescheduling.domain.Speaker;
import org.optaplanner.examples.conferencescheduling.domain.Talk;
import org.optaplanner.examples.conferencescheduling.domain.Timeslot;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.optaplanner.swing.impl.TangoColorFactory;

import static java.util.stream.Collectors.*;

public class ConferenceSchedulingXslxFileIO implements SolutionFileIO<ConferenceSolution> {

    protected static final Pattern VALID_TAG_PATTERN = Pattern.compile("(?U)^[\\w\\d _&\\-\\.\\(\\)]+$");
    protected static final Pattern VALID_NAME_PATTERN = VALID_TAG_PATTERN;
    protected static final Pattern VALID_CODE_PATTERN = Pattern.compile("(?U)^[\\w\\d_\\-\\.\\(\\)]+$");

    protected static final DateTimeFormatter DAY_FORMATTER
            = DateTimeFormatter.ofPattern("E yyyy-MM-dd", Locale.ENGLISH);
    protected static final DateTimeFormatter TIME_FORMATTER
            = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

    protected static final XSSFColor UNAVAILABLE_COLOR = new XSSFColor(TangoColorFactory.ALUMINIUM_5);
    protected static final XSSFColor PINNED_COLOR = new XSSFColor(TangoColorFactory.PLUM_1);

    @Override
    public String getInputFileExtension() {
        return "xlsx";
    }

    @Override
    public ConferenceSolution read(File inputSolutionFile) {
        try (InputStream in = new BufferedInputStream(new FileInputStream(inputSolutionFile))) {
            Workbook workbook = new XSSFWorkbook(in);
            return new ConferenceSchedulingXslxReader(workbook).read();
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Failed reading inputSolutionFile ("
                    + inputSolutionFile + ").", e);
        }
    }

    private static class ConferenceSchedulingXslxReader {

        protected final Workbook workbook;

        protected ConferenceSolution solution;
        private Map<String, Pair<Timeslot, Room>> talkCodeToTimeslotRoomMap;
        private Map<String, List<Speaker>> talkCodeToSpeakerListMap;
        private Set<String> totalTalkTypeSet;
        private Set<String> totalTimeslotTagSet;
        private Set<String> totalRoomTagSet;

        protected Sheet currentSheet;
        protected Iterator<Row> currentRowIterator;
        protected Row currentRow;
        protected int currentRowNumber;
        protected int currentColumnNumber;

        public ConferenceSchedulingXslxReader(Workbook workbook) {
            this.workbook = workbook;
        }

        public ConferenceSolution read() {
            solution = new ConferenceSolution();
            talkCodeToTimeslotRoomMap = new HashMap<>();
            talkCodeToSpeakerListMap = new HashMap<>();
            totalTalkTypeSet = new HashSet<>();
            totalTimeslotTagSet = new HashSet<>();
            totalRoomTagSet = new HashSet<>();
            readConfiguration();
            readTimeslotList();
            readRoomList();
            readSpeakerList();
            readTalkList();
            return solution;
        }

        private void readConfiguration() {
            nextSheet("Configuration");
            nextRow();
            readHeaderCell("Conference name");
            solution.setConferenceName(nextCell().getStringCellValue());
            if (!VALID_NAME_PATTERN.matcher(solution.getConferenceName()).matches()) {
                throw new IllegalStateException(currentPosition() + ": The conference name (" + solution.getConferenceName()
                        + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
            }
            nextRow(false);
            nextRow(false);
            readHeaderCell("Constraint");
            readHeaderCell("Weight");
            readHeaderCell("Description");
            ConferenceParametrization parametrization = new ConferenceParametrization();
            parametrization.setId(0L);
            readConstraintLine("Theme conflict", parametrization::setThemeConflict,
                    "Soft penalty per common theme of 2 talks that have an overlapping timeslot");
            readConstraintLine("Sector conflict", parametrization::setSectorConflict,
                    "Soft penalty per common sector of 2 talks that have an overlapping timeslot");
            readConstraintLine("Language diversity", parametrization::setLanguageDiversity,
                    "Soft reward per 2 talks that have the same timeslot and a different language");
            readConstraintLine("Speaker preferred timeslot tag", parametrization::setSpeakerPreferredTimeslotTag,
                    "Soft penalty per missing preferred tag in a talk's timeslot");
            readConstraintLine("Speaker undesired timeslot tag", parametrization::setSpeakerUndesiredTimeslotTag,
                    "Soft penalty per undesired tag in a talk's timeslot");
            readConstraintLine("Talk preferred timeslot tag", parametrization::setTalkPreferredTimeslotTag,
                    "Soft penalty per missing preferred tag in a talk's timeslot");
            readConstraintLine("Talk undesired timeslot tag", parametrization::setTalkUndesiredTimeslotTag,
                    "Soft penalty per undesired tag in a talk's timeslot");
            readConstraintLine("Speaker preferred room tag", parametrization::setSpeakerPreferredRoomTag,
                    "Soft penalty per missing preferred tag in a talk's room");
            readConstraintLine("Speaker undesired room tag", parametrization::setSpeakerUndesiredRoomTag,
                    "Soft penalty per undesired tag in a talk's room");
            readConstraintLine("Talk preferred room tag", parametrization::setTalkPreferredRoomTag,
                    "Soft penalty per missing preferred tag in a talk's room");
            readConstraintLine("Talk undesired room tag", parametrization::setTalkUndesiredRoomTag,
                    "Soft penalty per undesired tag in a talk's room");
            solution.setParametrization(parametrization);
        }

        private void readConstraintLine(String name, Consumer<Integer> consumer, String constraintdescription) {
            nextRow();
            readHeaderCell(name);
            double value = nextCell().getNumericCellValue();
            if (((double) ((int) value)) != value) {
                throw new IllegalArgumentException(currentPosition() + ": The value (" + value
                        + ") for constraint (" + name + ") must be an integer.");
            }
            consumer.accept((int) value);
            readHeaderCell(constraintdescription);
        }

        private void readTimeslotList() {
            nextSheet("Timeslots");
            nextRow(false);
            readHeaderCell("Day");
            readHeaderCell("Start");
            readHeaderCell("End");
            readHeaderCell("Talk type");
            readHeaderCell("Tags");
            List<Timeslot> timeslotList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            long id = 0L;
            while (nextRow()) {
                Timeslot timeslot = new Timeslot();
                timeslot.setId(id++);
                LocalDate day = LocalDate.parse(nextCell().getStringCellValue(), DAY_FORMATTER);
                LocalTime startTime = LocalTime.parse(nextCell().getStringCellValue(), TIME_FORMATTER);
                LocalTime endTime = LocalTime.parse(nextCell().getStringCellValue(), TIME_FORMATTER);
                if (startTime.compareTo(endTime) >= 0) {
                    throw new IllegalStateException(currentPosition() + ": The startTime (" + startTime
                            + ") must be less than the endTime (" + endTime + ").");
                }
                timeslot.setStartDateTime(LocalDateTime.of(day, startTime));
                timeslot.setEndDateTime(LocalDateTime.of(day, endTime));
                timeslot.setTalkType(nextCell().getStringCellValue());
                if (timeslot.getTalkType().isEmpty()) {
                    throw new IllegalStateException(currentPosition() + ": The talk type (" + timeslot.getTalkType()
                            + ") must not be empty.");
                }
                totalTalkTypeSet.add(timeslot.getTalkType());
                timeslot.setTagSet(Arrays.stream(nextCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                for (String tag : timeslot.getTagSet()) {
                    if (!VALID_TAG_PATTERN.matcher(tag).matches()) {
                        throw new IllegalStateException(currentPosition()
                                + ": The timeslot (" + timeslot + ")'s tag (" + tag
                                + ") must match to the regular expression (" + VALID_TAG_PATTERN + ").");
                    }
                }
                totalTimeslotTagSet.addAll(timeslot.getTagSet());
                timeslotList.add(timeslot);
            }
            solution.setTimeslotList(timeslotList);
        }

        private void readRoomList() {
            nextSheet("Rooms");
            nextRow(false);
            readHeaderCell("");
            readHeaderCell("");
            readTimeslotDaysHeaders();
            nextRow(false);
            readHeaderCell("Name");
            readHeaderCell("Tags");
            readTimeslotHoursHeaders();
            List<Room> roomList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            long id = 0L;
            while (nextRow()) {
                Room room = new Room();
                room.setId(id++);
                room.setName(nextCell().getStringCellValue());
                if (!VALID_NAME_PATTERN.matcher(room.getName()).matches()) {
                    throw new IllegalStateException(currentPosition() + ": The room name (" + room.getName()
                            + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
                }
                room.setTagSet(Arrays.stream(nextCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                for (String tag : room.getTagSet()) {
                    if (!VALID_TAG_PATTERN.matcher(tag).matches()) {
                        throw new IllegalStateException(currentPosition() + ": The room (" + room + ")'s tag (" + tag
                                + ") must match to the regular expression (" + VALID_TAG_PATTERN + ").");
                    }
                }
                totalRoomTagSet.addAll(room.getTagSet());
                Set<Timeslot> unavailableTimeslotSet = new LinkedHashSet<>();
                for (Timeslot timeslot : solution.getTimeslotList()) {
                    Cell cell = nextCell();
                    if (Objects.equals(extractColor(cell, UNAVAILABLE_COLOR, PINNED_COLOR), UNAVAILABLE_COLOR)) {
                        unavailableTimeslotSet.add(timeslot);
                    }
                    String[] talkCodes = cell.getStringCellValue().split(", ");
                    Pair<Timeslot, Room> pair = Pair.of(timeslot, room);
                    for (String talkCode : talkCodes) {
                        if (!talkCode.isEmpty()) {
                            Pair<Timeslot, Room> old = talkCodeToTimeslotRoomMap.put(talkCode, pair);
                            if (old != null) {
                                throw new IllegalStateException(currentPosition() + ": The talk (" + talkCode
                                        + ") occurs both in room (" + room + ") on timeslot (" + timeslot
                                        + ") and in room (" + old.getRight() + ") on timeslot (" + old.getLeft() + ").");
                            }
                        }
                    }
                }
                room.setUnavailableTimeslotSet(unavailableTimeslotSet);
                roomList.add(room);
            }
            solution.setRoomList(roomList);
        }

        private void readSpeakerList() {
            nextSheet("Speakers");
            nextRow(false);
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readTimeslotDaysHeaders();
            nextRow(false);
            readHeaderCell("Name");
            readHeaderCell("Required timeslot tags");
            readHeaderCell("Preferred timeslot tags");
            readHeaderCell("Prohibited timeslot tags");
            readHeaderCell("Undesired timeslot tags");
            readHeaderCell("Required room tags");
            readHeaderCell("Preferred room tags");
            readHeaderCell("Prohibited room tags");
            readHeaderCell("Undesired room tags");
            readTimeslotHoursHeaders();
            List<Speaker> speakerList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            long id = 0L;
            while (nextRow()) {
                Speaker speaker = new Speaker();
                speaker.setId(id++);
                speaker.setName(nextCell().getStringCellValue());
                if (!VALID_NAME_PATTERN.matcher(speaker.getName()).matches()) {
                    throw new IllegalStateException(currentPosition() + ": The speaker name (" + speaker.getName()
                            + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
                }
                speaker.setRequiredTimeslotTagSet(Arrays.stream(nextCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyTimeslotTags(speaker.getRequiredTimeslotTagSet());
                speaker.setPreferredTimeslotTagSet(Arrays.stream(nextCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyTimeslotTags(speaker.getPreferredTimeslotTagSet());
                speaker.setProhibitedTimeslotTagSet(Arrays.stream(nextCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyTimeslotTags(speaker.getProhibitedTimeslotTagSet());
                speaker.setUndesiredTimeslotTagSet(Arrays.stream(nextCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyTimeslotTags(speaker.getUndesiredTimeslotTagSet());
                speaker.setRequiredRoomTagSet(Arrays.stream(nextCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyRoomTags(speaker.getRequiredRoomTagSet());
                speaker.setPreferredRoomTagSet(Arrays.stream(nextCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyRoomTags(speaker.getPreferredRoomTagSet());
                speaker.setProhibitedRoomTagSet(Arrays.stream(nextCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyRoomTags(speaker.getProhibitedRoomTagSet());
                speaker.setUndesiredRoomTagSet(Arrays.stream(nextCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyRoomTags(speaker.getUndesiredRoomTagSet());
                Set<Timeslot> unavailableTimeslotSet = new LinkedHashSet<>();
                for (Timeslot timeslot : solution.getTimeslotList()) {
                    Cell cell = nextCell();
                    if (Objects.equals(extractColor(cell, UNAVAILABLE_COLOR), UNAVAILABLE_COLOR)) {
                        unavailableTimeslotSet.add(timeslot);
                    }
                    String[] talkCodes = cell.getStringCellValue().split(", ");
                    for (String talkCode : talkCodes) {
                        if (!talkCode.isEmpty()) {
                            talkCodeToSpeakerListMap.computeIfAbsent(talkCode, o -> new ArrayList<>()).add(speaker);
                            Pair<Timeslot, Room> pair = talkCodeToTimeslotRoomMap.get(talkCode);
                            Timeslot otherTimeslot = pair == null ? null : pair.getLeft();
                            if (timeslot != otherTimeslot) {
                                throw new IllegalStateException(currentPosition() + ": The talk with code (" + talkCode
                                        + ")'s is on timeslot (" + timeslot
                                        + ") in the Speakers sheet, but on a different timeslot (" + otherTimeslot
                                        + ") in the Rooms sheet.");
                            }
                        }
                    }
                }
                speaker.setUnavailableTimeslotSet(unavailableTimeslotSet);
                speakerList.add(speaker);
            }
            solution.setSpeakerList(speakerList);
        }

        private void readTalkList() {
            Map<String, Speaker> speakerMap = solution.getSpeakerList().stream().collect(
                    toMap(Speaker::getName, speaker -> speaker));
            nextSheet("Talks");
            nextRow(false);
            readHeaderCell("Code");
            readHeaderCell("Title");
            readHeaderCell("Talk type");
            readHeaderCell("Theme tags");
            readHeaderCell("Sector tags");
            readHeaderCell("Language");
            readHeaderCell("Speakers");
            readHeaderCell("Required timeslot tags");
            readHeaderCell("Preferred timeslot tags");
            readHeaderCell("Prohibited timeslot tags");
            readHeaderCell("Undesired timeslot tags");
            readHeaderCell("Required room tags");
            readHeaderCell("Preferred room tags");
            readHeaderCell("Prohibited room tags");
            readHeaderCell("Undesired room tags");
            readHeaderCell("Pinned by user");
            readHeaderCell("Timeslot day");
            readHeaderCell("Start");
            readHeaderCell("End");
            readHeaderCell("Room");
            List<Talk> talkList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            long id = 0L;
            while (nextRow()) {
                Talk talk = new Talk();
                talk.setId(id++);
                talk.setCode(nextCell().getStringCellValue());
                if (!VALID_CODE_PATTERN.matcher(talk.getCode()).matches()) {
                    throw new IllegalStateException(currentPosition() + ": The talk code (" + talk.getCode()
                            + ") must match to the regular expression (" + VALID_CODE_PATTERN + ").");
                }
                talk.setTitle(nextCell().getStringCellValue());
                talk.setTalkType(nextCell().getStringCellValue());
                if (!totalTalkTypeSet.contains(talk.getTalkType())) {
                    throw new IllegalStateException(currentPosition() + ": The talk type (" + talk.getTalkType()
                            + ") does not exist in the talk types (" + totalTalkTypeSet
                            + ") of the other sheet (Timeslots).");
                }
                talk.setThemeTagSet(Arrays.stream(nextCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                for (String tag : talk.getThemeTagSet()) {
                    if (!VALID_TAG_PATTERN.matcher(tag).matches()) {
                        throw new IllegalStateException(currentPosition() + ": The talk (" + talk + ")'s theme tag (" + tag
                                + ") must match to the regular expression (" + VALID_TAG_PATTERN + ").");
                    }
                }
                talk.setSectorTagSet(Arrays.stream(nextCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                for (String tag : talk.getSectorTagSet()) {
                    if (!VALID_TAG_PATTERN.matcher(tag).matches()) {
                        throw new IllegalStateException(currentPosition() + ": The talk (" + talk + ")'s sector tag (" + tag
                                + ") must match to the regular expression (" + VALID_TAG_PATTERN + ").");
                    }
                }
                talk.setLanguage(nextCell().getStringCellValue());
                talk.setSpeakerList(Arrays.stream(nextCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).map(speakerName -> {
                    Speaker speaker = speakerMap.get(speakerName);
                    if (speaker == null) {
                        throw new IllegalStateException(currentPosition() + ": The talk with code (" + talk.getCode()
                                + ") has a speaker (" + speakerName + ") that doesn't exist in the speaker list.");
                    }
                    return speaker;
                }).collect(toList()));
                talk.setRequiredTimeslotTagSet(Arrays.stream(nextCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyTimeslotTags(talk.getRequiredTimeslotTagSet());
                talk.setPreferredTimeslotTagSet(Arrays.stream(nextCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyTimeslotTags(talk.getPreferredTimeslotTagSet());
                talk.setProhibitedTimeslotTagSet(Arrays.stream(nextCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyTimeslotTags(talk.getProhibitedTimeslotTagSet());
                talk.setUndesiredTimeslotTagSet(Arrays.stream(nextCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyTimeslotTags(talk.getUndesiredTimeslotTagSet());
                talk.setRequiredRoomTagSet(Arrays.stream(nextCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyRoomTags(talk.getRequiredRoomTagSet());
                talk.setPreferredRoomTagSet(Arrays.stream(nextCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyRoomTags(talk.getPreferredRoomTagSet());
                talk.setProhibitedRoomTagSet(Arrays.stream(nextCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyRoomTags(talk.getProhibitedRoomTagSet());
                talk.setUndesiredRoomTagSet(Arrays.stream(nextCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyRoomTags(talk.getUndesiredRoomTagSet());
                talk.setPinnedByUser(nextCell().getBooleanCellValue());
                Pair<Timeslot, Room> timeslotRoomPair = talkCodeToTimeslotRoomMap.get(talk.getCode());
                if (timeslotRoomPair != null) {
                    talk.setTimeslot(timeslotRoomPair.getLeft());
                    talk.setRoom(timeslotRoomPair.getRight());
                }
                List<Speaker> otherSpeakerList = talkCodeToSpeakerListMap.get(talk.getCode());
                if (otherSpeakerList != null) {
                    for (Speaker otherSpeaker : otherSpeakerList) {
                        if (!talk.getSpeakerList().contains(otherSpeaker)) {
                            throw new IllegalStateException(currentPosition() + ": The talk with code (" + talk.getCode()
                                    + ")'s speakerList (" + talk.getSpeakerList() + ") does contain the speaker (" + otherSpeaker
                                    + ") despite that the Speaker sheet does have that talk for that speaker.");
                        }
                    }
                }
                String dateString = nextCell().getStringCellValue();
                String otherDateString = talk.getTimeslot() == null ? "" : talk.getTimeslot().getDate().format(DAY_FORMATTER);
                if (!dateString.equals(otherDateString)) {
                    throw new IllegalStateException(currentPosition() + ": The talk with code (" + talk.getCode()
                            + ") has a timeslot day (" + dateString
                            + ") that is inconsistent with the grid in the Rooms sheet's timeslot day (" + otherDateString + ").\n"
                            + "Maybe a talk was manually assigned, but not in every required sheet.");
                }
                String startTimeString = nextCell().getStringCellValue();
                String otherStartTimeString = talk.getTimeslot() == null ? "" : talk.getTimeslot().getStartDateTime().format(TIME_FORMATTER);
                if (!startTimeString.equals(otherStartTimeString)) {
                    throw new IllegalStateException(currentPosition() + ": The talk with code (" + talk.getCode()
                            + ") has a timeslot start (" + startTimeString
                            + ") that is inconsistent with the grid in the Rooms sheet's timeslot start (" + otherStartTimeString + ").\n"
                            + "Maybe a talk was manually assigned, but not in every required sheet.");
                }
                String endTimeString = nextCell().getStringCellValue();
                String otherEndTimeString = talk.getTimeslot() == null ? "" : talk.getTimeslot().getEndDateTime().format(TIME_FORMATTER);
                if (!endTimeString.equals(otherEndTimeString)) {
                    throw new IllegalStateException(currentPosition() + ": The talk with code (" + talk.getCode()
                            + ") has a timeslot end (" + endTimeString
                            + ") that is inconsistent with the grid in the Rooms sheet's timeslot end (" + otherEndTimeString + ").\n"
                            + "Maybe a talk was manually assigned, but not in every required sheet.");
                }
                String roomName = nextCell().getStringCellValue();
                String otherRoomName = talk.getRoom() == null ? "" : talk.getRoom().getName();
                if (!roomName.equals(otherRoomName)) {
                    throw new IllegalStateException(currentPosition() + ": The talk with code (" + talk.getCode()
                            + ") has a roomName (" + roomName
                            + ") that is inconsistent with the grid in the Rooms sheet's room (" + otherRoomName + ").\n"
                            + "Maybe a talk was manually assigned, but not in every required sheet.");
                }

                talkList.add(talk);
            }
            solution.setTalkList(talkList);
        }

        private void verifyTimeslotTags(Set<String> timeslotTagSet) {
            for (String tag : timeslotTagSet) {
                if (!totalTimeslotTagSet.contains(tag)) {
                    throw new IllegalStateException(currentPosition() + ": The timeslot tag (" + tag
                            + ") does not exist in the tags (" + totalTimeslotTagSet
                            + ") of the other sheet (Timeslots).");
                }
            }
        }

        private void verifyRoomTags(Set<String> roomTagSet) {
            for (String tag : roomTagSet) {
                if (!totalRoomTagSet.contains(tag)) {
                    throw new IllegalStateException(currentPosition() + ": The room tag (" + tag
                            + ") does not exist in the tags (" + totalRoomTagSet + ") of the other sheet (Rooms).");
                }
            }
        }

        private void readTimeslotDaysHeaders() {
            LocalDate previousTimeslotDay = null;
            for (Timeslot timeslot : solution.getTimeslotList()) {
                LocalDate timeslotDay = timeslot.getDate();
                if (timeslotDay.equals(previousTimeslotDay)) {
                    readHeaderCell("");
                } else {
                    readHeaderCell(DAY_FORMATTER.format(timeslotDay));
                    previousTimeslotDay = timeslotDay;
                }
            }
        }

        private void readTimeslotHoursHeaders() {
            for (Timeslot timeslot : solution.getTimeslotList()) {
                readHeaderCell(TIME_FORMATTER.format(timeslot.getStartDateTime())
                        + "-" + TIME_FORMATTER.format(timeslot.getEndDateTime()));
            }
        }

        protected String currentPosition() {
            return "Sheet (" + currentSheet.getSheetName() + ") cell ("
                    + (currentRowNumber + 1) + CellReference.convertNumToColString(currentColumnNumber) + ")";
        }

        protected void nextSheet(String sheetName) {
            currentSheet = workbook.getSheet(sheetName);
            if (currentSheet == null) {
                throw new IllegalStateException("The workbook does not contain a sheet with name ("
                        + sheetName + ").");
            }
            currentRowIterator = currentSheet.rowIterator();
            if (currentRowIterator == null) {
                throw new IllegalStateException(currentPosition() + ": The sheet has no rows.");
            }
            currentRowNumber = -1;
        }

        protected boolean nextRow() {
            return nextRow(true);
        }

        protected boolean nextRow(boolean skipEmptyRows) {
            currentRowNumber++;
            currentColumnNumber = -1;
            if (!currentRowIterator.hasNext()) {
                currentRow = null;
                return false;
            }
            currentRow = currentRowIterator.next();
            while (skipEmptyRows && currentRow.getPhysicalNumberOfCells() == 0) {
                if (!currentRowIterator.hasNext()) {
                    currentRow = null;
                    return false;
                }
                currentRow = currentRowIterator.next();
            }
            if (currentRow.getRowNum() != currentRowNumber) {
                if (currentRow.getRowNum() == currentRowNumber + 1) {
                    currentRowNumber++;
                } else {
                    throw new IllegalStateException(currentPosition() + ": The next row (" + currentRow.getRowNum()
                            + ") has a gap of more than 1 empty line with the previous.");
                }
            }
            return true;
        }

        protected void readHeaderCell(String value) {
            Cell cell = currentRow == null ? null : nextCell();
            if (cell == null || !cell.getStringCellValue().equals(value)) {
                throw new IllegalStateException(currentPosition() + ": The cell does not contain the expected value ("
                        + value + ").");
            }
        }

        protected Cell nextCell() {
            currentColumnNumber++;
            Cell cell = currentRow.getCell(currentColumnNumber);
            // TODO HACK to workaround the fact that LibreOffice and Excel automatically remove empty trailing cells
            if (cell == null) {
                // Return dummy cell
                return currentRow.createCell(currentColumnNumber);
            }
            return cell;
        }

        protected XSSFColor extractColor(Cell cell, XSSFColor... acceptableColors) {
            CellStyle cellStyle = cell.getCellStyle();
            FillPatternType fillPattern = cellStyle.getFillPatternEnum();
            if (fillPattern == null || fillPattern == FillPatternType.NO_FILL) {
                return null;
            }
            if (fillPattern != FillPatternType.SOLID_FOREGROUND) {
                throw new IllegalStateException(currentPosition() + ": The fill pattern (" + fillPattern
                        + ") should be either " + FillPatternType.NO_FILL
                        + " or " + FillPatternType.SOLID_FOREGROUND + ".");
            }
            XSSFColor color = (XSSFColor) cellStyle.getFillForegroundColorColor();
            for (XSSFColor acceptableColor : acceptableColors) {
                if (acceptableColor.equals(color)) {
                    return acceptableColor;
                }
            }
            throw new IllegalStateException(currentPosition() + ": The fill color (" + color
                    + ") is not one of the acceptableColors (" + Arrays.toString(acceptableColors) + ").");
        }

    }


    @Override
    public void write(ConferenceSolution solution, File outputSolutionFile) {
        try (FileOutputStream out = new FileOutputStream(outputSolutionFile)) {
            Workbook workbook = new ConferenceSchedulingXlsxWriter(solution).write();
            workbook.write(out);
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Failed writing outputSolutionFile ("
                    + outputSolutionFile + ") for solution (" + solution + ").", e);
        }
    }

    private static class ConferenceSchedulingXlsxWriter {

        protected final ConferenceSolution solution;
        private Map<Pair<Timeslot, Room>, List<Talk>> timeslotRoomToTalkMap;

        protected Workbook workbook;

        protected CellStyle headerStyle;
        protected CellStyle unavailableStyle;
        protected CellStyle pinnedStyle;

        protected Sheet currentSheet;
        protected Row currentRow;
        protected int currentRowNumber;
        protected int currentColumnNumber;
        protected int headerCellCount;

        public ConferenceSchedulingXlsxWriter(ConferenceSolution solution) {
            this.solution = solution;
            timeslotRoomToTalkMap = solution.getTalkList().stream().collect(groupingBy(
                    talk -> Pair.of(talk.getTimeslot(), talk.getRoom())));
        }

        public Workbook write() {
            workbook = new XSSFWorkbook();
            createStyles();
            writeConfiguration();
            writeTimeslotList();
            writeRoomList();
            writeSpeakerList();
            writeTalkList();
            writeThemeView();
            writeSectorView();
            workbook.setActiveSheet(2);
            return workbook;
        }

        public void createStyles() {
            headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            unavailableStyle = workbook.createCellStyle();
            unavailableStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            ((XSSFCellStyle) unavailableStyle).setFillForegroundColor(UNAVAILABLE_COLOR);
            pinnedStyle = workbook.createCellStyle();
            pinnedStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            ((XSSFCellStyle) pinnedStyle).setFillForegroundColor(PINNED_COLOR);
        }

        private void writeConfiguration() {
            nextSheet("Configuration", 1, 3);
            nextRow();
            nextHeaderCell("Conference name");
            nextCell().setCellValue(solution.getConferenceName());
            nextRow();
            nextRow();
            nextHeaderCell("Constraint");
            nextHeaderCell("Weight");
            nextHeaderCell("Description");
            ConferenceParametrization parametrization = solution.getParametrization();

            writeConstraintLine("Theme conflict", parametrization::getThemeConflict,
                    "Soft penalty per common theme of 2 talks that have an overlapping timeslot");
            writeConstraintLine("Sector conflict", parametrization::getSectorConflict,
                    "Soft penalty per common sector of 2 talks that have an overlapping timeslot");
            writeConstraintLine("Language diversity", parametrization::getLanguageDiversity,
                    "Soft reward per 2 talks that have the same timeslot and a different language");
            writeConstraintLine("Speaker preferred timeslot tag", parametrization::getSpeakerPreferredTimeslotTag,
                    "Soft penalty per missing preferred tag in a talk's timeslot");
            writeConstraintLine("Speaker undesired timeslot tag", parametrization::getSpeakerUndesiredTimeslotTag,
                    "Soft penalty per undesired tag in a talk's timeslot");
            writeConstraintLine("Talk preferred timeslot tag", parametrization::getTalkPreferredTimeslotTag,
                    "Soft penalty per missing preferred tag in a talk's timeslot");
            writeConstraintLine("Talk undesired timeslot tag", parametrization::getTalkUndesiredTimeslotTag,
                    "Soft penalty per undesired tag in a talk's timeslot");
            writeConstraintLine("Speaker preferred room tag", parametrization::getSpeakerPreferredRoomTag,
                    "Soft penalty per missing preferred tag in a talk's room");
            writeConstraintLine("Speaker undesired room tag", parametrization::getSpeakerUndesiredRoomTag,
                    "Soft penalty per undesired tag in a talk's room");
            writeConstraintLine("Talk preferred room tag", parametrization::getTalkPreferredRoomTag,
                    "Soft penalty per missing preferred tag in a talk's room");
            writeConstraintLine("Talk undesired room tag", parametrization::getTalkUndesiredRoomTag,
                    "Soft penalty per undesired tag in a talk's room");
            autoSizeColumnsWithHeader();
        }

        private void writeConstraintLine(String name, Supplier<Integer> supplier, String constraintdescription) {
            nextRow();
            nextHeaderCell(name);
            nextCell().setCellValue(supplier.get());
            nextHeaderCell(constraintdescription);
        }

        private void writeTimeslotList() {
            nextSheet("Timeslots", 3, 1);
            nextRow();
            nextHeaderCell("Day");
            nextHeaderCell("Start");
            nextHeaderCell("End");
            nextHeaderCell("Talk type");
            nextHeaderCell("Tags");
            for (Timeslot timeslot : solution.getTimeslotList()) {
                nextRow();
                nextCell().setCellValue(DAY_FORMATTER.format(timeslot.getDate()));
                nextCell().setCellValue(TIME_FORMATTER.format(timeslot.getStartDateTime()));
                nextCell().setCellValue(TIME_FORMATTER.format(timeslot.getEndDateTime()));
                nextCell().setCellValue(timeslot.getTalkType());
                nextCell().setCellValue(String.join(", ", timeslot.getTagSet()));
            }
            autoSizeColumnsWithHeader();
        }

        private void writeRoomList() {
            nextSheet("Rooms", 1, 2);
            nextRow();
            nextHeaderCell("");
            nextHeaderCell("");
            writeTimeslotDaysHeaders();
            nextRow();
            nextHeaderCell("Name");
            nextHeaderCell("Tags");
            writeTimeslotHoursHeaders();
            for (Room room : solution.getRoomList()) {
                nextRow();
                nextCell().setCellValue(room.getName());
                nextCell().setCellValue(String.join(", ", room.getTagSet()));
                for (Timeslot timeslot : solution.getTimeslotList()) {
                    List<Talk> talkList = timeslotRoomToTalkMap.get(Pair.of(timeslot, room));
                    nextCell(room.getUnavailableTimeslotSet().contains(timeslot) ? unavailableStyle
                            : (talkList != null && talkList.stream().anyMatch(Talk::isPinnedByUser)) ? pinnedStyle : null)
                            .setCellValue(talkList == null ? ""
                            : talkList.stream().map(Talk::getCode).collect(joining(", ")));
                }
            }
            autoSizeColumnsWithHeader();
        }

        private void writeSpeakerList() {
            nextSheet("Speakers", 1, 2);
            nextRow();
            nextHeaderCell("");
            nextHeaderCell("");
            nextHeaderCell("");
            nextHeaderCell("");
            nextHeaderCell("");
            nextHeaderCell("");
            nextHeaderCell("");
            nextHeaderCell("");
            nextHeaderCell("");
            writeTimeslotDaysHeaders();
            nextRow();
            nextHeaderCell("Name");
            nextHeaderCell("Required timeslot tags");
            nextHeaderCell("Preferred timeslot tags");
            nextHeaderCell("Prohibited timeslot tags");
            nextHeaderCell("Undesired timeslot tags");
            nextHeaderCell("Required room tags");
            nextHeaderCell("Preferred room tags");
            nextHeaderCell("Prohibited room tags");
            nextHeaderCell("Undesired room tags");
            writeTimeslotHoursHeaders();
            for (Speaker speaker : solution.getSpeakerList()) {
                nextRow();
                nextCell().setCellValue(speaker.getName());
                nextCell().setCellValue(String.join(", ", speaker.getRequiredTimeslotTagSet()));
                nextCell().setCellValue(String.join(", ", speaker.getPreferredTimeslotTagSet()));
                nextCell().setCellValue(String.join(", ", speaker.getProhibitedTimeslotTagSet()));
                nextCell().setCellValue(String.join(", ", speaker.getUndesiredTimeslotTagSet()));
                nextCell().setCellValue(String.join(", ", speaker.getRequiredRoomTagSet()));
                nextCell().setCellValue(String.join(", ", speaker.getPreferredRoomTagSet()));
                nextCell().setCellValue(String.join(", ", speaker.getProhibitedRoomTagSet()));
                nextCell().setCellValue(String.join(", ", speaker.getUndesiredRoomTagSet()));
                List<Talk> talkList = solution.getTalkList().stream()
                        .filter(talk -> talk.getSpeakerList().contains(speaker))
                        .collect(toList());
                for (Timeslot timeslot : solution.getTimeslotList()) {
                    nextCell(speaker.getUnavailableTimeslotSet().contains(timeslot) ? unavailableStyle : null)
                            .setCellValue(talkList.stream()
                            .filter(talk -> talk.getTimeslot() == timeslot)
                            .map(Talk::getCode).collect(joining(", ")));

                }
            }
            autoSizeColumnsWithHeader();
        }

        private void writeTalkList() {
            nextSheet("Talks", 2, 1);
            nextRow();
            nextHeaderCell("Code");
            nextHeaderCell("Title");
            nextHeaderCell("Talk type");
            nextHeaderCell("Theme tags");
            nextHeaderCell("Sector tags");
            nextHeaderCell("Language");
            nextHeaderCell("Speakers");
            nextHeaderCell("Required timeslot tags");
            nextHeaderCell("Preferred timeslot tags");
            nextHeaderCell("Prohibited timeslot tags");
            nextHeaderCell("Undesired timeslot tags");
            nextHeaderCell("Required room tags");
            nextHeaderCell("Preferred room tags");
            nextHeaderCell("Prohibited room tags");
            nextHeaderCell("Undesired room tags");
            nextHeaderCell("Pinned by user");
            nextHeaderCell("Timeslot day");
            nextHeaderCell("Start");
            nextHeaderCell("End");
            nextHeaderCell("Room");
            for (Talk talk : solution.getTalkList()) {
                nextRow();
                nextCell().setCellValue(talk.getCode());
                nextCell().setCellValue(talk.getTitle());
                nextCell().setCellValue(talk.getTalkType());
                nextCell().setCellValue(String.join(", ", talk.getThemeTagSet()));
                nextCell().setCellValue(String.join(", ", talk.getSectorTagSet()));
                nextCell().setCellValue(talk.getLanguage());
                nextCell().setCellValue(talk.getSpeakerList()
                        .stream().map(Speaker::getName).collect(joining(", ")));
                nextCell().setCellValue(String.join(", ", talk.getRequiredTimeslotTagSet()));
                nextCell().setCellValue(String.join(", ", talk.getPreferredTimeslotTagSet()));
                nextCell().setCellValue(String.join(", ", talk.getProhibitedTimeslotTagSet()));
                nextCell().setCellValue(String.join(", ", talk.getUndesiredTimeslotTagSet()));
                nextCell().setCellValue(String.join(", ", talk.getRequiredRoomTagSet()));
                nextCell().setCellValue(String.join(", ", talk.getPreferredRoomTagSet()));
                nextCell().setCellValue(String.join(", ", talk.getProhibitedRoomTagSet()));
                nextCell().setCellValue(String.join(", ", talk.getUndesiredRoomTagSet()));
                nextCell(talk.isPinnedByUser() ? pinnedStyle : null).setCellValue(talk.isPinnedByUser());
                nextCell().setCellValue(talk.getTimeslot() == null ? "" : DAY_FORMATTER.format(talk.getTimeslot().getDate()));
                nextCell().setCellValue(talk.getTimeslot() == null ? "" : TIME_FORMATTER.format(talk.getTimeslot().getStartDateTime()));
                nextCell().setCellValue(talk.getTimeslot() == null ? "" : TIME_FORMATTER.format(talk.getTimeslot().getEndDateTime()));
                nextCell().setCellValue(talk.getRoom() == null ? "" : talk.getRoom().getName());
            }
            autoSizeColumnsWithHeader();
        }

        private void writeThemeView() {
            nextSheet("Theme view", 1, 1);
            currentSheet.protectSheet("ThisDataIsIgnoredOnInput");
            nextRow();
            nextHeaderCell("");
            writeTimeslotDaysHeaders();
            nextRow();
            nextHeaderCell("Theme tag");
            writeTimeslotHoursHeaders();

            Map<String, Map<Timeslot, List<Talk>>> tagToTimeslotToTalkListMap = solution.getTalkList().stream()
                    .filter(talk -> talk.getTimeslot() != null)
                    .flatMap(talk -> talk.getThemeTagSet().stream()
                            .map(tag -> Pair.of(tag, Pair.of(talk.getTimeslot(), talk))))
                    .collect(groupingBy(Pair::getLeft, groupingBy(o -> o.getRight().getLeft(), mapping(o -> o.getRight().getRight(), toList()))));
            for (Map.Entry<String, Map<Timeslot, List<Talk>>> entry : tagToTimeslotToTalkListMap.entrySet()) {
                nextRow();
                nextHeaderCell(entry.getKey());
                Map<Timeslot, List<Talk>> timeslotToTalkListMap = entry.getValue();
                for (Timeslot timeslot : solution.getTimeslotList()) {
                    List<Talk> talkList = timeslotToTalkListMap.get(timeslot);
                    nextCell().setCellValue(talkList == null ? ""
                            : talkList.stream().map(Talk::getCode).collect(joining(", ")));
                }
            }
            autoSizeColumnsWithHeader();
        }

        private void writeSectorView() {
            nextSheet("Sector view", 1, 1);
            currentSheet.protectSheet("ThisDataIsIgnoredOnInput");
            nextRow();
            nextHeaderCell("");
            writeTimeslotDaysHeaders();
            nextRow();
            nextHeaderCell("Sector tag");
            writeTimeslotHoursHeaders();

            Map<String, Map<Timeslot, List<Talk>>> tagToTimeslotToTalkListMap = solution.getTalkList().stream()
                    .filter(talk -> talk.getTimeslot() != null)
                    .flatMap(talk -> talk.getSectorTagSet().stream()
                            .map(tag -> Pair.of(tag, Pair.of(talk.getTimeslot(), talk))))
                    .collect(groupingBy(Pair::getLeft, groupingBy(o -> o.getRight().getLeft(), mapping(o -> o.getRight().getRight(), toList()))));
            for (Map.Entry<String, Map<Timeslot, List<Talk>>> entry : tagToTimeslotToTalkListMap.entrySet()) {
                nextRow();
                nextHeaderCell(entry.getKey());
                Map<Timeslot, List<Talk>> timeslotToTalkListMap = entry.getValue();
                for (Timeslot timeslot : solution.getTimeslotList()) {
                    List<Talk> talkList = timeslotToTalkListMap.get(timeslot);
                    nextCell().setCellValue(talkList == null ? ""
                            : talkList.stream().map(Talk::getCode).collect(joining(", ")));
                }
            }
            autoSizeColumnsWithHeader();
        }

        private void writeTimeslotDaysHeaders() {
            LocalDate previousTimeslotDay = null;
            int mergeStart = -1;
            for (Timeslot timeslot : solution.getTimeslotList()) {
                LocalDate timeslotDay = timeslot.getDate();
                if (timeslotDay.equals(previousTimeslotDay)) {
                    nextHeaderCell("");
                } else {
                    if (previousTimeslotDay != null) {
                        currentSheet.addMergedRegion(new CellRangeAddress(currentRowNumber, currentRowNumber, mergeStart, currentColumnNumber));
                    }
                    nextHeaderCell(DAY_FORMATTER.format(timeslotDay));
                    previousTimeslotDay = timeslotDay;
                    mergeStart = currentColumnNumber;
                }
            }
            if (previousTimeslotDay != null) {
                currentSheet.addMergedRegion(new CellRangeAddress(currentRowNumber, currentRowNumber, mergeStart, currentColumnNumber));
            }
        }

        private void writeTimeslotHoursHeaders() {
            for (Timeslot timeslot : solution.getTimeslotList()) {
                nextHeaderCell(TIME_FORMATTER.format(timeslot.getStartDateTime())
                        + "-" + TIME_FORMATTER.format(timeslot.getEndDateTime()));
            }
        }

        protected void nextSheet(String sheetName, int colSplit, int rowSplit) {
            currentSheet = workbook.createSheet(sheetName);
            currentSheet.createFreezePane(colSplit, rowSplit);
            currentRowNumber = -1;
            headerCellCount = 0;
        }

        protected void nextRow() {
            currentRowNumber++;
            currentRow = currentSheet.createRow(currentRowNumber);
            currentColumnNumber = -1;
        }

        protected void nextHeaderCell(String value) {
            nextCell(headerStyle).setCellValue(value);
            headerCellCount++;
        }

        protected Cell nextCell() {
            return nextCell(null);
        }

        protected Cell nextCell(CellStyle cellStyle) {
            currentColumnNumber++;
            Cell cell = currentRow.createCell(currentColumnNumber);
            if (cellStyle != null) {
                cell.setCellStyle(cellStyle);
            }
            return cell;
        }

        protected void autoSizeColumnsWithHeader() {
            for (int i = 0; i < headerCellCount; i++) {
                currentSheet.autoSizeColumn(i);
            }
        }

    }

}
