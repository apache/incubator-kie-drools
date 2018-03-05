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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;
import org.optaplanner.examples.conferencescheduling.app.ConferenceSchedulingApp;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceParametrization;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.examples.conferencescheduling.domain.Room;
import org.optaplanner.examples.conferencescheduling.domain.Speaker;
import org.optaplanner.examples.conferencescheduling.domain.Talk;
import org.optaplanner.examples.conferencescheduling.domain.TalkType;
import org.optaplanner.examples.conferencescheduling.domain.Timeslot;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;
import org.optaplanner.swing.impl.TangoColorFactory;

import static java.util.stream.Collectors.*;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceParametrization.*;

public class ConferenceSchedulingXslxFileIO implements SolutionFileIO<ConferenceSolution> {

    protected static final Pattern VALID_TAG_PATTERN = Pattern.compile("(?U)^[\\w&\\-\\.\\/\\(\\)\\'][\\w&\\-\\.\\/\\(\\)\\' ]*[\\w&\\-\\.\\/\\(\\)\\']?$");
    protected static final Pattern VALID_NAME_PATTERN = VALID_TAG_PATTERN;
    protected static final Pattern VALID_CODE_PATTERN = Pattern.compile("(?U)^[\\w\\-\\.\\/\\(\\)]+$");

    protected static final DateTimeFormatter DAY_FORMATTER
            = DateTimeFormatter.ofPattern("E yyyy-MM-dd", Locale.ENGLISH);
    protected static final DateTimeFormatter TIME_FORMATTER
            = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

    protected static final XSSFColor VIEW_TAB_COLOR = new XSSFColor(TangoColorFactory.BUTTER_1);

    protected static final XSSFColor UNAVAILABLE_COLOR = new XSSFColor(TangoColorFactory.ALUMINIUM_5);
    protected static final XSSFColor PINNED_COLOR = new XSSFColor(TangoColorFactory.PLUM_1);
    protected static final XSSFColor HARD_PENALTY_COLOR = new XSSFColor(TangoColorFactory.SCARLET_1);
    protected static final XSSFColor SOFT_PENALTY_COLOR = new XSSFColor(TangoColorFactory.ORANGE_1);

    @Override
    public String getInputFileExtension() {
        return "xlsx";
    }

    @Override
    public ConferenceSolution read(File inputSolutionFile) {
        try (InputStream in = new BufferedInputStream(new FileInputStream(inputSolutionFile))) {
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            return new ConferenceSchedulingXslxReader(workbook).read();
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Failed reading inputSolutionFile ("
                    + inputSolutionFile + ").", e);
        }
    }

    private static class ConferenceSchedulingXslxReader {

        protected final XSSFWorkbook workbook;

        protected ConferenceSolution solution;
        private Map<String, TalkType> totalTalkTypeMap;
        private Set<String> totalTimeslotTagSet;
        private Set<String> totalRoomTagSet;

        protected XSSFSheet currentSheet;
        protected Iterator<Row> currentRowIterator;
        protected XSSFRow currentRow;
        protected int currentRowNumber;
        protected int currentColumnNumber;

        public ConferenceSchedulingXslxReader(XSSFWorkbook workbook) {
            this.workbook = workbook;
        }

        public ConferenceSolution read() {
            solution = new ConferenceSolution();
            totalTalkTypeMap = new HashMap<>();
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
            solution.setConferenceName(nextStringCell().getStringCellValue());
            if (!VALID_NAME_PATTERN.matcher(solution.getConferenceName()).matches()) {
                throw new IllegalStateException(currentPosition() + ": The conference name (" + solution.getConferenceName()
                        + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
            }
            nextRow(true);
            readHeaderCell("Constraint");
            readHeaderCell("Weight");
            readHeaderCell("Description");
            ConferenceParametrization parametrization = new ConferenceParametrization();
            parametrization.setId(0L);
            readConstraintLine(THEME_TRACK_CONFLICT, parametrization::setThemeTrackConflict,
                    "Soft penalty per common theme track of 2 talks that have an overlapping timeslot");
            readConstraintLine(SECTOR_CONFLICT, parametrization::setSectorConflict,
                    "Soft penalty per common sector of 2 talks that have an overlapping timeslot");
            readConstraintLine(AUDIENCE_TYPE_DIVERSITY, parametrization::setAudienceTypeDiversity,
                    "Soft reward per 2 talks that have the same timeslot and a different audience type");
            readConstraintLine(AUDIENCE_TYPE_THEME_TRACK_CONFLICT, parametrization::setAudienceTypeThemeTrackConflict,
                    "Soft penalty per 2 talks that have a common audience type, have a common theme track and have an overlapping timeslot");
            readConstraintLine(AUDIENCE_LEVEL_DIVERSITY, parametrization::setAudienceLevelDiversity,
                    "Soft reward per 2 talks that have the same timeslot and a different audience level");
            readConstraintLine(AUDIENCE_LEVEL_FLOW_PER_CONTENT_VIOLATION, parametrization::setAudienceLevelFlowPerContentViolation,
                    "Soft penalty per common content of 2 talks with a different audience level for which the easier talk isn't scheduled earlier than the other talk");
            readConstraintLine(CONTENT_CONFLICT, parametrization::setContentConflict,
                    "Soft penalty per common content of 2 talks that have an overlapping timeslot");
            readConstraintLine(LANGUAGE_DIVERSITY, parametrization::setLanguageDiversity,
                    "Soft reward per 2 talks that have the same timeslot and a different language");
            readConstraintLine(SPEAKER_PREFERRED_TIMESLOT_TAG, parametrization::setSpeakerPreferredTimeslotTag,
                    "Soft penalty per missing preferred tag in a talk's timeslot");
            readConstraintLine(SPEAKER_UNDESIRED_TIMESLOT_TAG, parametrization::setSpeakerUndesiredTimeslotTag,
                    "Soft penalty per undesired tag in a talk's timeslot");
            readConstraintLine(TALK_PREFERRED_TIMESLOT_TAG, parametrization::setTalkPreferredTimeslotTag,
                    "Soft penalty per missing preferred tag in a talk's timeslot");
            readConstraintLine(TALK_UNDESIRED_TIMESLOT_TAG, parametrization::setTalkUndesiredTimeslotTag,
                    "Soft penalty per undesired tag in a talk's timeslot");
            readConstraintLine(SPEAKER_PREFERRED_ROOM_TAG, parametrization::setSpeakerPreferredRoomTag,
                    "Soft penalty per missing preferred tag in a talk's room");
            readConstraintLine(SPEAKER_UNDESIRED_ROOM_TAG, parametrization::setSpeakerUndesiredRoomTag,
                    "Soft penalty per undesired tag in a talk's room");
            readConstraintLine(TALK_PREFERRED_ROOM_TAG, parametrization::setTalkPreferredRoomTag,
                    "Soft penalty per missing preferred tag in a talk's room");
            readConstraintLine(TALK_UNDESIRED_ROOM_TAG, parametrization::setTalkUndesiredRoomTag,
                    "Soft penalty per undesired tag in a talk's room");
            readConstraintLine(TALK_TYPE_OF_TIMESLOT, null,
                    "Hard penalty per talk in a timeslot with an other talk type");
            readConstraintLine(ROOM_UNAVAILABLE_TIMESLOT, null,
                    "Hard penalty per talk with an unavailable room at its timeslot");
            readConstraintLine(ROOM_CONFLICT, null,
                    "Hard penalty per pair of talks in the same room in overlapping timeslots");
            readConstraintLine(SPEAKER_UNAVAILABLE_TIMESLOT, null,
                    "Hard penalty per talk with an unavailable speaker at its timeslot");
            readConstraintLine(SPEAKER_CONFLICT, null,
                    "Hard penalty per pair of talks with the same speaker in overlapping timeslots");
            readConstraintLine(SPEAKER_REQUIRED_TIMESLOT_TAG, null,
                    "Hard penalty per missing required tag in a talk's timeslot");
            readConstraintLine(SPEAKER_PROHIBITED_TIMESLOT_TAG, null,
                    "Hard penalty per prohibited tag in a talk's timeslot");
            readConstraintLine(TALK_REQUIRED_TIMESLOT_TAG, null,
                    "Hard penalty per missing required tag in a talk's timeslot");
            readConstraintLine(TALK_PROHIBITED_TIMESLOT_TAG, null,
                    "Hard penalty per prohibited tag in a talk's timeslot");
            readConstraintLine(SPEAKER_REQUIRED_ROOM_TAG, null,
                    "Hard penalty per missing required tag in a talk's room");
            readConstraintLine(SPEAKER_PROHIBITED_ROOM_TAG, null,
                    "Hard penalty per prohibited tag in a talk's room");
            readConstraintLine(TALK_REQUIRED_ROOM_TAG, null,
                    "Hard penalty per missing required tag in a talk's room");
            readConstraintLine(TALK_PROHIBITED_ROOM_TAG, null,
                    "Hard penalty per prohibited tag in a talk's room");
            solution.setParametrization(parametrization);
        }

        private void readConstraintLine(String name, Consumer<Integer> consumer, String constraintdescription) {
            nextRow();
            readHeaderCell(name);
            XSSFCell weightCell = nextCell();
            if (consumer != null) {
                if (weightCell.getCellTypeEnum() != CellType.NUMERIC) {
                    throw new IllegalArgumentException(currentPosition() + ": The value ("
                            + weightCell.getStringCellValue()
                            + ") for constraint (" + name + ") must be a number and the cell type must be numeric.");
                }
                double value = weightCell.getNumericCellValue();
                if (((double) ((int) value)) != value) {
                    throw new IllegalArgumentException(currentPosition() + ": The value (" + value
                            + ") for constraint (" + name + ") must be an integer.");
                }
                consumer.accept((int) value);
            } else {
                if (weightCell.getCellTypeEnum() == CellType.NUMERIC
                        || !weightCell.getStringCellValue().equals("n/a")) {
                    throw new IllegalArgumentException(currentPosition() + ": The value ("
                            + weightCell.getStringCellValue()
                            + ") for constraint (" + name + ") must be an n/a.");
                }
            }
            readHeaderCell(constraintdescription);
        }

        private void readTimeslotList() {
            nextSheet("Timeslots");
            nextRow(false);
            readHeaderCell("Day");
            readHeaderCell("Start");
            readHeaderCell("End");
            readHeaderCell("Talk types");
            readHeaderCell("Tags");
            List<TalkType> talkTypeList = new ArrayList<>();
            List<Timeslot> timeslotList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            long id = 0L;
            long talkTypeId = 0L;
            while (nextRow()) {
                Timeslot timeslot = new Timeslot();
                timeslot.setId(id++);
                LocalDate day = LocalDate.parse(nextStringCell().getStringCellValue(), DAY_FORMATTER);
                LocalTime startTime = LocalTime.parse(nextStringCell().getStringCellValue(), TIME_FORMATTER);
                LocalTime endTime = LocalTime.parse(nextStringCell().getStringCellValue(), TIME_FORMATTER);
                if (startTime.compareTo(endTime) >= 0) {
                    throw new IllegalStateException(currentPosition() + ": The startTime (" + startTime
                            + ") must be less than the endTime (" + endTime + ").");
                }
                timeslot.setStartDateTime(LocalDateTime.of(day, startTime));
                timeslot.setEndDateTime(LocalDateTime.of(day, endTime));
                String[] talkTypeNames = nextStringCell().getStringCellValue().split(", ");
                Set<TalkType> talkTypeSet = new LinkedHashSet<>(talkTypeNames.length);
                for (String talkTypeName : talkTypeNames) {
                    TalkType talkType = totalTalkTypeMap.get(talkTypeName);
                    if (talkType == null) {
                        talkType = new TalkType(talkTypeId);
                        talkTypeId++;
                        if (!VALID_TAG_PATTERN.matcher(talkTypeName).matches()) {
                            throw new IllegalStateException(currentPosition()
                                    + ": The timeslot (" + timeslot + ")'s talkType (" + talkTypeName
                                    + ") must match to the regular expression (" + VALID_TAG_PATTERN + ").");
                        }
                        talkType.setName(talkTypeName);
                        talkType.setCompatibleTimeslotSet(new LinkedHashSet<>());
                        talkType.setCompatibleRoomSet(new LinkedHashSet<>());
                        totalTalkTypeMap.put(talkTypeName, talkType);
                        talkTypeList.add(talkType);
                    }
                    talkTypeSet.add(talkType);
                    talkType.getCompatibleTimeslotSet().add(timeslot);
                }
                if (talkTypeSet.isEmpty()) {
                    throw new IllegalStateException(currentPosition()
                            + ": The timeslot (" + timeslot + ")'s talk types (" + timeslot.getTalkTypeSet()
                            + ") must not be empty.");
                }
                timeslot.setTalkTypeSet(talkTypeSet);
                timeslot.setTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
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
            solution.setTalkTypeList(talkTypeList);
        }

        private void readRoomList() {
            nextSheet("Rooms");
            nextRow(false);
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readTimeslotDaysHeaders();
            nextRow(false);
            readHeaderCell("Name");
            readHeaderCell("Talk types");
            readHeaderCell("Tags");
            readTimeslotHoursHeaders();
            List<Room> roomList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            long id = 0L;
            while (nextRow()) {
                Room room = new Room();
                room.setId(id++);
                room.setName(nextStringCell().getStringCellValue());
                if (!VALID_NAME_PATTERN.matcher(room.getName()).matches()) {
                    throw new IllegalStateException(currentPosition() + ": The room name (" + room.getName()
                            + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
                }

                String[] talkTypeNames = nextStringCell().getStringCellValue().split(", ");
                Set<TalkType> talkTypeSet;
                if (talkTypeNames.length == 0  || (talkTypeNames.length == 1 && talkTypeNames[0].isEmpty())) {
                    talkTypeSet = new LinkedHashSet<>(totalTalkTypeMap.values());
                    for (TalkType talkType : talkTypeSet) {
                        talkType.getCompatibleRoomSet().add(room);
                    }
                } else {
                    talkTypeSet = new LinkedHashSet<>(talkTypeNames.length);
                    for (String talkTypeName : talkTypeNames) {
                        TalkType talkType = totalTalkTypeMap.get(talkTypeName);
                        if (talkType == null) {
                            throw new IllegalStateException(currentPosition()
                                    + ": The room (" + room + ")'s talkType (" + talkTypeName
                                    + ") does not exist in the talk types (" + totalTalkTypeMap.keySet()
                                    + ") of the other sheet (Timeslots).");
                        }
                        talkTypeSet.add(talkType);
                        talkType.getCompatibleRoomSet().add(room);
                    }
                }
                room.setTalkTypeSet(talkTypeSet);
                room.setTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
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
                    XSSFCell cell = nextStringCell();
                    if (Objects.equals(extractColor(cell, UNAVAILABLE_COLOR), UNAVAILABLE_COLOR)) {
                        unavailableTimeslotSet.add(timeslot);
                    }
                    if (!cell.getStringCellValue().isEmpty()) {
                        throw new IllegalStateException(currentPosition() + ": The cell (" + cell.getStringCellValue()
                                + ") should be empty. Use the talks sheet pre-assign rooms and timeslots.");
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
                speaker.setName(nextStringCell().getStringCellValue());
                if (!VALID_NAME_PATTERN.matcher(speaker.getName()).matches()) {
                    throw new IllegalStateException(currentPosition() + ": The speaker name (" + speaker.getName()
                            + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
                }
                speaker.setRequiredTimeslotTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyTimeslotTags(speaker.getRequiredTimeslotTagSet());
                speaker.setPreferredTimeslotTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyTimeslotTags(speaker.getPreferredTimeslotTagSet());
                speaker.setProhibitedTimeslotTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyTimeslotTags(speaker.getProhibitedTimeslotTagSet());
                speaker.setUndesiredTimeslotTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyTimeslotTags(speaker.getUndesiredTimeslotTagSet());
                speaker.setRequiredRoomTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyRoomTags(speaker.getRequiredRoomTagSet());
                speaker.setPreferredRoomTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyRoomTags(speaker.getPreferredRoomTagSet());
                speaker.setProhibitedRoomTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyRoomTags(speaker.getProhibitedRoomTagSet());
                speaker.setUndesiredRoomTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyRoomTags(speaker.getUndesiredRoomTagSet());
                Set<Timeslot> unavailableTimeslotSet = new LinkedHashSet<>();
                for (Timeslot timeslot : solution.getTimeslotList()) {
                    XSSFCell cell = nextStringCell();
                    if (Objects.equals(extractColor(cell, UNAVAILABLE_COLOR), UNAVAILABLE_COLOR)) {
                        unavailableTimeslotSet.add(timeslot);
                    }
                    if (!cell.getStringCellValue().isEmpty()) {
                        throw new IllegalStateException(currentPosition() + ": The cell (" + cell.getStringCellValue()
                                + ") should be empty. Use the talks sheet pre-assign rooms and timeslots.");
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
            readHeaderCell("Speakers");
            readHeaderCell("Theme track tags");
            readHeaderCell("Sector tags");
            readHeaderCell("Audience types");
            readHeaderCell("Audience level");
            readHeaderCell("Content tags");
            readHeaderCell("Language");
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
            Map<Pair<LocalDateTime, LocalDateTime>, Timeslot> timeslotMap = solution.getTimeslotList().stream().collect(
                    Collectors.toMap(timeslot -> Pair.of(timeslot.getStartDateTime(), timeslot.getEndDateTime()),
                            Function.identity()));
            Map<String, Room> roomMap = solution.getRoomList().stream().collect(
                    Collectors.toMap(Room::getName, Function.identity()));
            while (nextRow()) {
                Talk talk = new Talk();
                talk.setId(id++);
                talk.setCode(nextStringCell().getStringCellValue());
                if (!VALID_CODE_PATTERN.matcher(talk.getCode()).matches()) {
                    throw new IllegalStateException(currentPosition() + ": The talk code (" + talk.getCode()
                            + ") must match to the regular expression (" + VALID_CODE_PATTERN + ").");
                }
                talk.setTitle(nextStringCell().getStringCellValue());
                String talkTypeName = nextStringCell().getStringCellValue();
                TalkType talkType = totalTalkTypeMap.get(talkTypeName);
                if (talkType == null) {
                    throw new IllegalStateException(currentPosition()
                            + ": The talk (" + talk + ")'s talkType (" + talkType
                            + ") does not exist in the talk types (" + totalTalkTypeMap.keySet()
                            + ") of the other sheet (Timeslots).");
                }
                talk.setTalkType(talkType);
                talk.setSpeakerList(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).map(speakerName -> {
                            Speaker speaker = speakerMap.get(speakerName);
                            if (speaker == null) {
                                throw new IllegalStateException(currentPosition() + ": The talk with code (" + talk.getCode()
                                        + ") has a speaker (" + speakerName + ") that doesn't exist in the speaker list.");
                            }
                            return speaker;
                        }).collect(toList()));
                talk.setThemeTrackTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                for (String tag : talk.getThemeTrackTagSet()) {
                    if (!VALID_TAG_PATTERN.matcher(tag).matches()) {
                        throw new IllegalStateException(currentPosition() + ": The talk (" + talk + ")'s theme tag (" + tag
                                + ") must match to the regular expression (" + VALID_TAG_PATTERN + ").");
                    }
                }
                talk.setSectorTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                for (String tag : talk.getSectorTagSet()) {
                    if (!VALID_TAG_PATTERN.matcher(tag).matches()) {
                        throw new IllegalStateException(currentPosition() + ": The talk (" + talk + ")'s sector tag (" + tag
                                + ") must match to the regular expression (" + VALID_TAG_PATTERN + ").");
                    }
                }
                talk.setAudienceTypeSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                for (String audienceType : talk.getAudienceTypeSet()) {
                    if (!VALID_TAG_PATTERN.matcher(audienceType).matches()) {
                        throw new IllegalStateException(currentPosition() + ": The talk (" + talk + ")'s audience type (" + audienceType
                                + ") must match to the regular expression (" + VALID_TAG_PATTERN + ").");
                    }
                }
                double audienceLevelDouble = nextNumericCell().getNumericCellValue();
                if (audienceLevelDouble <= 0 || audienceLevelDouble != Math.floor(audienceLevelDouble)) {
                    throw new IllegalStateException(currentPosition() + ": The talk with code (" + talk.getCode()
                            + ")'s has an audience level (" + audienceLevelDouble + ") that isn't a strictly positive integer number.");
                }
                talk.setAudienceLevel((int) audienceLevelDouble);
                talk.setContentTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                for (String tag : talk.getContentTagSet()) {
                    if (!VALID_TAG_PATTERN.matcher(tag).matches()) {
                        throw new IllegalStateException(currentPosition() + ": The talk (" + talk + ")'s content tag (" + tag
                                + ") must match to the regular expression (" + VALID_TAG_PATTERN + ").");
                    }
                }
                talk.setLanguage(nextStringCell().getStringCellValue());
                talk.setRequiredTimeslotTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyTimeslotTags(talk.getRequiredTimeslotTagSet());
                talk.setPreferredTimeslotTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyTimeslotTags(talk.getPreferredTimeslotTagSet());
                talk.setProhibitedTimeslotTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyTimeslotTags(talk.getProhibitedTimeslotTagSet());
                talk.setUndesiredTimeslotTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyTimeslotTags(talk.getUndesiredTimeslotTagSet());
                talk.setRequiredRoomTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyRoomTags(talk.getRequiredRoomTagSet());
                talk.setPreferredRoomTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyRoomTags(talk.getPreferredRoomTagSet());
                talk.setProhibitedRoomTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyRoomTags(talk.getProhibitedRoomTagSet());
                talk.setUndesiredRoomTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                verifyRoomTags(talk.getUndesiredRoomTagSet());
                talk.setPinnedByUser(nextBooleanCell().getBooleanCellValue());
                String dateString = nextStringCell().getStringCellValue();
                String startTimeString = nextStringCell().getStringCellValue();
                String endTimeString = nextStringCell().getStringCellValue();
                if (!dateString.isEmpty() || !startTimeString.isEmpty() || !endTimeString.isEmpty()) {
                    LocalDateTime startDateTime;
                    LocalDateTime endDateTime;
                    try {
                        startDateTime = LocalDateTime.of(LocalDate.parse(dateString, DAY_FORMATTER),
                                LocalTime.parse(startTimeString, TIME_FORMATTER));
                        endDateTime = LocalDateTime.of(LocalDate.parse(dateString, DAY_FORMATTER),
                                LocalTime.parse(endTimeString, TIME_FORMATTER));
                    } catch (DateTimeParseException e) {
                        throw new IllegalStateException(currentPosition() + ": The talk with code (" + talk.getCode()
                                + ") has a timeslot date (" + dateString
                                + "), startTime (" + startTimeString + ") and endTime (" + endTimeString
                                + ") that doesn't parse as a date or time.", e);
                    }
                    Timeslot timeslot = timeslotMap.get(Pair.of(startDateTime, endDateTime));
                    if (timeslot == null) {
                        throw new IllegalStateException(currentPosition() + ": The talk with code (" + talk.getCode()
                                + ") has a timeslot date (" + dateString
                                + "), startTime (" + startTimeString + ") and endTime (" + endTimeString
                                + ") that doesn't exist in the other sheet (Timeslots).");
                    }
                    talk.setTimeslot(timeslot);
                }
                String roomName = nextStringCell().getStringCellValue();
                if (!roomName.isEmpty()) {
                    Room room = roomMap.get(roomName);
                    if (room == null) {
                        throw new IllegalStateException(currentPosition() + ": The talk with code (" + talk.getCode()
                                + ") has a roomName (" + roomName
                                + ") that doesn't exist in the other sheet (Rooms).");
                    }
                    talk.setRoom(room);
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
            currentRow = (XSSFRow) currentRowIterator.next();
            while (skipEmptyRows && currentRowIsEmpty()) {
                if (!currentRowIterator.hasNext()) {
                    currentRow = null;
                    return false;
                }
                currentRow = (XSSFRow) currentRowIterator.next();
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

        protected boolean currentRowIsEmpty() {
            if (currentRow.getPhysicalNumberOfCells() == 0) {
                return true;
            }
            for (Cell cell : currentRow) {
                if (cell.getCellTypeEnum() == CellType.STRING) {
                    if (!cell.getStringCellValue().isEmpty()) {
                        return false;
                    }
                } else if (cell.getCellTypeEnum() != CellType.BLANK) {
                    return false;
                }
            }
            return true;
        }

        protected void readHeaderCell(String value) {
            XSSFCell cell = currentRow == null ? null : nextStringCell();
            if (cell == null || !cell.getStringCellValue().equals(value)) {
                throw new IllegalStateException(currentPosition() + ": The cell does not contain the expected value ("
                        + value + ").");
            }
        }

        protected XSSFCell nextStringCell() {
            XSSFCell cell = nextCell();
            if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                throw new IllegalStateException(currentPosition() + ": The cell with value ("
                        + cell.getNumericCellValue() + ") has a numeric type but should be a string.");
            }
            return cell;
        }

        protected XSSFCell nextNumericCell() {
            XSSFCell cell = nextCell();
            if (cell.getCellTypeEnum() == CellType.STRING) {
                throw new IllegalStateException(currentPosition() + ": The cell with value ("
                        + cell.getStringCellValue() + ") has a string type but should be numeric.");
            }
            return cell;
        }

        protected XSSFCell nextBooleanCell() {
            XSSFCell cell = nextCell();
            if (cell.getCellTypeEnum() == CellType.STRING) {
                throw new IllegalStateException(currentPosition() + ": The cell with value ("
                        + cell.getStringCellValue() + ") has a string type but should be boolean.");
            }
            if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                throw new IllegalStateException(currentPosition() + ": The cell with value ("
                        + cell.getNumericCellValue() + ") has a numeric type but should be a boolean.");
            }
            return cell;
        }

        protected XSSFCell nextCell() {
            currentColumnNumber++;
            XSSFCell cell = currentRow.getCell(currentColumnNumber);
            // TODO HACK to workaround the fact that LibreOffice and Excel automatically remove empty trailing cells
            if (cell == null) {
                // Return dummy cell
                return currentRow.createCell(currentColumnNumber);
            }
            return cell;
        }

        protected XSSFColor extractColor(XSSFCell cell, XSSFColor... acceptableColors) {
            XSSFCellStyle cellStyle = cell.getCellStyle();
            FillPatternType fillPattern = cellStyle.getFillPatternEnum();
            if (fillPattern == null || fillPattern == FillPatternType.NO_FILL) {
                return null;
            }
            if (fillPattern != FillPatternType.SOLID_FOREGROUND) {
                throw new IllegalStateException(currentPosition() + ": The fill pattern (" + fillPattern
                        + ") should be either " + FillPatternType.NO_FILL
                        + " or " + FillPatternType.SOLID_FOREGROUND + ".");
            }
            XSSFColor color = cellStyle.getFillForegroundColorColor();
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
        protected final Map<Object, Indictment> indictmentMap;

        protected XSSFWorkbook workbook;
        protected CreationHelper creationHelper;

        protected XSSFCellStyle headerStyle;
        protected XSSFCellStyle defaultStyle;
        protected XSSFCellStyle unavailableStyle;
        protected XSSFCellStyle pinnedStyle;
        protected XSSFCellStyle hardPenaltyStyle;
        protected XSSFCellStyle softPenaltyStyle;
        protected XSSFCellStyle wrappedStyle;

        protected XSSFSheet currentSheet;
        protected Drawing currentDrawing;
        protected XSSFRow currentRow;
        protected int currentRowNumber;
        protected int currentColumnNumber;
        protected int headerCellCount;

        public ConferenceSchedulingXlsxWriter(ConferenceSolution solution) {
            this.solution = solution;
            ScoreDirectorFactory<ConferenceSolution> scoreDirectorFactory
                    = SolverFactory.<ConferenceSolution>createFromXmlResource(ConferenceSchedulingApp.SOLVER_CONFIG)
                    .buildSolver().getScoreDirectorFactory();
            try (ScoreDirector<ConferenceSolution> scoreDirector = scoreDirectorFactory.buildScoreDirector()) {
                scoreDirector.setWorkingSolution(solution);
                scoreDirector.calculateScore();
                indictmentMap = scoreDirector.getIndictmentMap();
            }
        }

        public Workbook write() {
            workbook = new XSSFWorkbook();
            creationHelper = workbook.getCreationHelper();
            createStyles();
            writeConfiguration();
            writeTimeslotList();
            writeRoomList();
            writeSpeakerList();
            writeTalkList();
            writeScoreView();
            writeRoomsView();
            writeSpeakersView();
            writeThemeTracksView();
            writeSectorsView();
            writeAudienceTypeView();
            writeAudienceLevelView();
            writeContentsView();
            return workbook;
        }

        public void createStyles() {
            headerStyle = createStyle(null);
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            defaultStyle = createStyle(null);
            unavailableStyle = createStyle(UNAVAILABLE_COLOR);
            pinnedStyle = createStyle(PINNED_COLOR);
            hardPenaltyStyle = createStyle(HARD_PENALTY_COLOR);
            softPenaltyStyle = createStyle(SOFT_PENALTY_COLOR);
            wrappedStyle = createStyle(null);
        }

        private XSSFCellStyle createStyle(XSSFColor color) {
            XSSFCellStyle style = workbook.createCellStyle();
            if (color != null) {
                style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                style.setFillForegroundColor(color);
            }
            style.setWrapText(true);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            return style;
        }

        private void writeConfiguration() {
            nextSheet("Configuration", 1, 3, false);
            nextRow();
            nextHeaderCell("Conference name");
            nextCell().setCellValue(solution.getConferenceName());
            nextRow();
            nextRow();
            nextHeaderCell("Constraint");
            nextHeaderCell("Weight");
            nextHeaderCell("Description");
            ConferenceParametrization parametrization = solution.getParametrization();

            writeConstraintLine(THEME_TRACK_CONFLICT, parametrization::getThemeTrackConflict,
                    "Soft penalty per common theme track of 2 talks that have an overlapping timeslot");
            writeConstraintLine(SECTOR_CONFLICT, parametrization::getSectorConflict,
                    "Soft penalty per common sector of 2 talks that have an overlapping timeslot");
            writeConstraintLine(AUDIENCE_TYPE_DIVERSITY, parametrization::getAudienceTypeDiversity,
                    "Soft reward per 2 talks that have the same timeslot and a different audience type");
            writeConstraintLine(AUDIENCE_TYPE_THEME_TRACK_CONFLICT, parametrization::getAudienceTypeThemeTrackConflict,
                    "Soft penalty per 2 talks that have a common audience type, have a common theme track and have an overlapping timeslot");
            writeConstraintLine(AUDIENCE_LEVEL_DIVERSITY, parametrization::getAudienceLevelDiversity,
                    "Soft reward per 2 talks that have the same timeslot and a different audience level");
            writeConstraintLine(AUDIENCE_LEVEL_FLOW_PER_CONTENT_VIOLATION, parametrization::getAudienceLevelFlowPerContentViolation,
                    "Soft penalty per common content of 2 talks with a different audience level for which the easier talk isn't scheduled earlier than the other talk");
            writeConstraintLine(CONTENT_CONFLICT, parametrization::getContentConflict,
                    "Soft penalty per common content of 2 talks that have an overlapping timeslot");
            writeConstraintLine(LANGUAGE_DIVERSITY, parametrization::getLanguageDiversity,
                    "Soft reward per 2 talks that have the same timeslot and a different language");
            writeConstraintLine(SPEAKER_PREFERRED_TIMESLOT_TAG, parametrization::getSpeakerPreferredTimeslotTag,
                    "Soft penalty per missing preferred tag in a talk's timeslot");
            writeConstraintLine(SPEAKER_UNDESIRED_TIMESLOT_TAG, parametrization::getSpeakerUndesiredTimeslotTag,
                    "Soft penalty per undesired tag in a talk's timeslot");
            writeConstraintLine(TALK_PREFERRED_TIMESLOT_TAG, parametrization::getTalkPreferredTimeslotTag,
                    "Soft penalty per missing preferred tag in a talk's timeslot");
            writeConstraintLine(TALK_UNDESIRED_TIMESLOT_TAG, parametrization::getTalkUndesiredTimeslotTag,
                    "Soft penalty per undesired tag in a talk's timeslot");
            writeConstraintLine(SPEAKER_PREFERRED_ROOM_TAG, parametrization::getSpeakerPreferredRoomTag,
                    "Soft penalty per missing preferred tag in a talk's room");
            writeConstraintLine(SPEAKER_UNDESIRED_ROOM_TAG, parametrization::getSpeakerUndesiredRoomTag,
                    "Soft penalty per undesired tag in a talk's room");
            writeConstraintLine(TALK_PREFERRED_ROOM_TAG, parametrization::getTalkPreferredRoomTag,
                    "Soft penalty per missing preferred tag in a talk's room");
            writeConstraintLine(TALK_UNDESIRED_ROOM_TAG, parametrization::getTalkUndesiredRoomTag,
                    "Soft penalty per undesired tag in a talk's room");
            nextRow();
            writeConstraintLine(TALK_TYPE_OF_TIMESLOT, null,
                    "Hard penalty per talk in a timeslot with an other talk type");
            writeConstraintLine(ROOM_UNAVAILABLE_TIMESLOT, null,
                    "Hard penalty per talk with an unavailable room at its timeslot");
            writeConstraintLine(ROOM_CONFLICT, null,
                    "Hard penalty per pair of talks in the same room in overlapping timeslots");
            writeConstraintLine(SPEAKER_UNAVAILABLE_TIMESLOT, null,
                    "Hard penalty per talk with an unavailable speaker at its timeslot");
            writeConstraintLine(SPEAKER_CONFLICT, null,
                    "Hard penalty per pair of talks with the same speaker in overlapping timeslots");
            writeConstraintLine(SPEAKER_REQUIRED_TIMESLOT_TAG, null,
                    "Hard penalty per missing required tag in a talk's timeslot");
            writeConstraintLine(SPEAKER_PROHIBITED_TIMESLOT_TAG, null,
                    "Hard penalty per prohibited tag in a talk's timeslot");
            writeConstraintLine(TALK_REQUIRED_TIMESLOT_TAG, null,
                    "Hard penalty per missing required tag in a talk's timeslot");
            writeConstraintLine(TALK_PROHIBITED_TIMESLOT_TAG, null,
                    "Hard penalty per prohibited tag in a talk's timeslot");
            writeConstraintLine(SPEAKER_REQUIRED_ROOM_TAG, null,
                    "Hard penalty per missing required tag in a talk's room");
            writeConstraintLine(SPEAKER_PROHIBITED_ROOM_TAG, null,
                    "Hard penalty per prohibited tag in a talk's room");
            writeConstraintLine(TALK_REQUIRED_ROOM_TAG, null,
                    "Hard penalty per missing required tag in a talk's room");
            writeConstraintLine(TALK_PROHIBITED_ROOM_TAG, null,
                    "Hard penalty per prohibited tag in a talk's room");
            autoSizeColumnsWithHeader();
        }

        private void writeConstraintLine(String name, Supplier<Integer> supplier, String constraintdescription) {
            nextRow();
            nextHeaderCell(name);
            XSSFCell weightCell = nextCell();
            if (supplier != null) {
                weightCell.setCellValue(supplier.get());
            } else {
                weightCell.setCellValue("n/a");
            }
            nextHeaderCell(constraintdescription);
        }

        private void writeTimeslotList() {
            nextSheet("Timeslots", 3, 1, false);
            nextRow();
            nextHeaderCell("Day");
            nextHeaderCell("Start");
            nextHeaderCell("End");
            nextHeaderCell("Talk types");
            nextHeaderCell("Tags");
            for (Timeslot timeslot : solution.getTimeslotList()) {
                nextRow();
                nextCell().setCellValue(DAY_FORMATTER.format(timeslot.getDate()));
                nextCell().setCellValue(TIME_FORMATTER.format(timeslot.getStartDateTime()));
                nextCell().setCellValue(TIME_FORMATTER.format(timeslot.getEndDateTime()));
                nextCell().setCellValue(String.join(", ", timeslot.getTalkTypeSet().stream().map(TalkType::getName).collect(toList())));
                nextCell().setCellValue(String.join(", ", timeslot.getTagSet()));
            }
            autoSizeColumnsWithHeader();
        }

        private void writeRoomList() {
            nextSheet("Rooms", 1, 2, false);
            nextRow();
            nextHeaderCell("");
            nextHeaderCell("");
            nextHeaderCell("");
            writeTimeslotDaysHeaders();
            nextRow();
            nextHeaderCell("Name");
            nextHeaderCell("Talk types");
            nextHeaderCell("Tags");
            writeTimeslotHoursHeaders();
            for (Room room : solution.getRoomList()) {
                nextRow();
                nextCell().setCellValue(room.getName());
                nextCell().setCellValue(String.join(", ", room.getTalkTypeSet().stream().map(TalkType::getName).collect(toList())));
                nextCell().setCellValue(String.join(", ", room.getTagSet()));
                for (Timeslot timeslot : solution.getTimeslotList()) {
                    nextCell(room.getUnavailableTimeslotSet().contains(timeslot) ? unavailableStyle : defaultStyle)
                            .setCellValue("");
                }
            }
            autoSizeColumnsWithHeader();
        }

        private void writeSpeakerList() {
            nextSheet("Speakers", 1, 2, false);
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
                for (Timeslot timeslot : solution.getTimeslotList()) {
                    nextCell(speaker.getUnavailableTimeslotSet().contains(timeslot) ? unavailableStyle : defaultStyle)
                            .setCellValue("");

                }
            }
            autoSizeColumnsWithHeader();
        }

        private void writeTalkList() {
            nextSheet("Talks", 2, 1, false);
            nextRow();
            nextHeaderCell("Code");
            nextHeaderCell("Title");
            nextHeaderCell("Talk type");
            nextHeaderCell("Speakers");
            nextHeaderCell("Theme track tags");
            nextHeaderCell("Sector tags");
            nextHeaderCell("Audience types");
            nextHeaderCell("Audience level");
            nextHeaderCell("Content tags");
            nextHeaderCell("Language");
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
                nextCell().setCellValue(talk.getTalkType().getName());
                nextCell().setCellValue(talk.getSpeakerList()
                        .stream().map(Speaker::getName).collect(joining(", ")));
                nextCell().setCellValue(String.join(", ", talk.getThemeTrackTagSet()));
                nextCell().setCellValue(String.join(", ", talk.getSectorTagSet()));
                nextCell().setCellValue(String.join(", ", talk.getAudienceTypeSet()));
                nextCell().setCellValue(talk.getAudienceLevel());
                nextCell().setCellValue(String.join(", ", talk.getContentTagSet()));
                nextCell().setCellValue(talk.getLanguage());
                nextCell().setCellValue(String.join(", ", talk.getRequiredTimeslotTagSet()));
                nextCell().setCellValue(String.join(", ", talk.getPreferredTimeslotTagSet()));
                nextCell().setCellValue(String.join(", ", talk.getProhibitedTimeslotTagSet()));
                nextCell().setCellValue(String.join(", ", talk.getUndesiredTimeslotTagSet()));
                nextCell().setCellValue(String.join(", ", talk.getRequiredRoomTagSet()));
                nextCell().setCellValue(String.join(", ", talk.getPreferredRoomTagSet()));
                nextCell().setCellValue(String.join(", ", talk.getProhibitedRoomTagSet()));
                nextCell().setCellValue(String.join(", ", talk.getUndesiredRoomTagSet()));
                nextCell(talk.isPinnedByUser() ? pinnedStyle : defaultStyle).setCellValue(talk.isPinnedByUser());
                nextCell().setCellValue(talk.getTimeslot() == null ? "" : DAY_FORMATTER.format(talk.getTimeslot().getDate()));
                nextCell().setCellValue(talk.getTimeslot() == null ? "" : TIME_FORMATTER.format(talk.getTimeslot().getStartDateTime()));
                nextCell().setCellValue(talk.getTimeslot() == null ? "" : TIME_FORMATTER.format(talk.getTimeslot().getEndDateTime()));
                nextCell().setCellValue(talk.getRoom() == null ? "" : talk.getRoom().getName());
            }
            autoSizeColumnsWithHeader();
        }

        private void writeScoreView() {
            nextSheet("Score view", 1, 1, true);
            nextRow();
            nextHeaderCell("Score");
            nextCell().setCellValue(solution.getScore() == null ? "Not yet solved" : solution.getScore().toShortString());
            nextRow();
            nextRow();
            nextHeaderCell("Talk type");
            nextHeaderCell("Count");
            nextHeaderCell("Usable timeslots");
            nextHeaderCell("Usable rooms");
            nextHeaderCell("Usable sessions");

            Map<TalkType, Long> talkTypeToCountMap = solution.getTalkList().stream()
                    .collect(groupingBy(Talk::getTalkType, LinkedHashMap::new, counting()));
            for (Map.Entry<TalkType, Long> entry : talkTypeToCountMap.entrySet()) {
                TalkType talkType = entry.getKey();
                long count = entry.getValue();
                nextRow();
                nextHeaderCell(talkType.getName());
                nextCell().setCellValue(count);
                int timeslotListSize = talkType.getCompatibleTimeslotSet().size();
                nextCell().setCellValue(timeslotListSize);
                int roomListSize = talkType.getCompatibleRoomSet().size();
                nextCell().setCellValue(roomListSize);
                int sessionCount = timeslotListSize * roomListSize;
                nextCell(sessionCount < count ? hardPenaltyStyle : defaultStyle).setCellValue(sessionCount);
            }
            nextRow();
            nextRow();
            nextHeaderCell("Total");
            int talkListSize = solution.getTalkList().size();
            nextCell().setCellValue(talkListSize);
            int timeslotListSize = solution.getTimeslotList().size();
            nextCell().setCellValue(timeslotListSize);
            int roomListSize = solution.getRoomList().size();
            nextCell().setCellValue(roomListSize);
            int sessionCount = timeslotListSize * roomListSize;
            nextCell(sessionCount < talkListSize ? hardPenaltyStyle : defaultStyle).setCellValue(sessionCount);
            autoSizeColumnsWithHeader();
        }

        private void writeRoomsView() {
            nextSheet("Rooms view", 1, 2, true);
            nextRow();
            nextHeaderCell("");
            writeTimeslotDaysHeaders();
            nextRow();
            nextHeaderCell("Room");
            writeTimeslotHoursHeaders();
            for (Room room : solution.getRoomList()) {
                nextRow();
                currentRow.setHeightInPoints(4 * currentSheet.getDefaultRowHeightInPoints());
                nextCell().setCellValue(room.getName());
                List<Talk> roomTalkList = solution.getTalkList().stream()
                        .filter(talk -> talk.getRoom() == room)
                        .collect(toList());
                for (Timeslot timeslot : solution.getTimeslotList()) {
                    List<Talk> talkList = roomTalkList.stream()
                            .filter(talk -> talk.getTimeslot() == timeslot).collect(toList());
                    boolean unavailable = room.getUnavailableTimeslotSet().contains(timeslot)
                            || Collections.disjoint(room.getTalkTypeSet(), timeslot.getTalkTypeSet());
                    nextTalkListCell(unavailable, talkList, talk -> talk.getCode() + ": " + talk.getTitle() + "\n  "
                            + talk.getSpeakerList().stream().map(Speaker::getName).collect(joining(", ")));
                }
            }
            currentSheet.autoSizeColumn(0);
            for (int i = 1; i < headerCellCount; i++) {
                currentSheet.setColumnWidth(i, 20 * 256);
            }
        }

        private void writeSpeakersView() {
            nextSheet("Speakers view", 1, 2, true);
            nextRow();
            nextHeaderCell("");
            writeTimeslotDaysHeaders();
            nextRow();
            nextHeaderCell("Speaker");
            writeTimeslotHoursHeaders();
            for (Speaker speaker : solution.getSpeakerList()) {
                nextRow();
                nextCell().setCellValue(speaker.getName());
                List<Talk> timeslotTalkList = solution.getTalkList().stream()
                        .filter(talk -> talk.getSpeakerList().contains(speaker))
                        .collect(toList());
                for (Timeslot timeslot : solution.getTimeslotList()) {
                    List<Talk> talkList = timeslotTalkList.stream()
                            .filter(talk -> talk.getTimeslot() == timeslot).collect(toList());
                    boolean unavailable = speaker.getUnavailableTimeslotSet().contains(timeslot);
                    nextTalkListCell(unavailable, talkList);
                }
            }
            autoSizeColumnsWithHeader();
        }

        private void writeThemeTracksView() {
            nextSheet("Theme tracks view", 1, 2, true);
            nextRow();
            nextHeaderCell("");
            writeTimeslotDaysHeaders();
            nextRow();
            nextHeaderCell("Theme track tag");
            writeTimeslotHoursHeaders();

            Map<String, Map<Timeslot, List<Talk>>> tagToTimeslotToTalkListMap = solution.getTalkList().stream()
                    .filter(talk -> talk.getTimeslot() != null)
                    .flatMap(talk -> talk.getThemeTrackTagSet().stream()
                            .map(tag -> Pair.of(tag, Pair.of(talk.getTimeslot(), talk))))
                    .collect(groupingBy(Pair::getLeft, groupingBy(o -> o.getRight().getLeft(), mapping(o -> o.getRight().getRight(), toList()))));
            for (Map.Entry<String, Map<Timeslot, List<Talk>>> entry : tagToTimeslotToTalkListMap.entrySet()) {
                nextRow();
                nextHeaderCell(entry.getKey());
                Map<Timeslot, List<Talk>> timeslotToTalkListMap = entry.getValue();
                for (Timeslot timeslot : solution.getTimeslotList()) {
                    List<Talk> talkList = timeslotToTalkListMap.get(timeslot);
                    nextTalkListCell(talkList);
                }
            }
            autoSizeColumnsWithHeader();
        }

        private void writeSectorsView() {
            nextSheet("Sectors view", 1, 2, true);
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
                    nextTalkListCell(talkList);
                }
            }
            autoSizeColumnsWithHeader();
        }

        private void writeAudienceTypeView() {
            nextSheet("Audience type view", 1, 2, true);
            nextRow();
            nextHeaderCell("");
            writeTimeslotDaysHeaders();
            nextRow();
            nextHeaderCell("Audience type");
            writeTimeslotHoursHeaders();

            Map<String, Map<Timeslot, List<Talk>>> audienceTypeToTimeslotToTalkListMap = solution.getTalkList().stream()
                    .filter(talk -> talk.getTimeslot() != null)
                    .flatMap(talk -> talk.getAudienceTypeSet().stream()
                            .map(audienceType -> Pair.of(audienceType, Pair.of(talk.getTimeslot(), talk))))
                    .collect(groupingBy(Pair::getLeft, groupingBy(o -> o.getRight().getLeft(), mapping(o -> o.getRight().getRight(), toList()))));
            for (Map.Entry<String, Map<Timeslot, List<Talk>>> entry : audienceTypeToTimeslotToTalkListMap.entrySet()) {
                nextRow();
                nextHeaderCell(entry.getKey());
                Map<Timeslot, List<Talk>> timeslotToTalkListMap = entry.getValue();
                for (Timeslot timeslot : solution.getTimeslotList()) {
                    List<Talk> talkList = timeslotToTalkListMap.get(timeslot);
                    nextTalkListCell(talkList);
                }
            }
            autoSizeColumnsWithHeader();
        }

        private void writeAudienceLevelView() {
            nextSheet("Audience level view", 1, 2, true);
            nextRow();
            nextHeaderCell("");
            writeTimeslotDaysHeaders();
            nextRow();
            nextHeaderCell("Audience level");
            writeTimeslotHoursHeaders();

            Map<Integer, Map<Timeslot, List<Talk>>> tagToTimeslotToTalkListMap = solution.getTalkList().stream()
                    .filter(talk -> talk.getTimeslot() != null)
                    .map(talk -> Pair.of(talk.getAudienceLevel(), Pair.of(talk.getTimeslot(), talk)))
                    .collect(groupingBy(Pair::getLeft, groupingBy(o -> o.getRight().getLeft(), mapping(o -> o.getRight().getRight(), toList()))));
            for (Map.Entry<Integer, Map<Timeslot, List<Talk>>> entry : tagToTimeslotToTalkListMap.entrySet()) {
                nextRow();
                nextHeaderCell(Integer.toString(entry.getKey()));
                Map<Timeslot, List<Talk>> timeslotToTalkListMap = entry.getValue();
                for (Timeslot timeslot : solution.getTimeslotList()) {
                    List<Talk> talkList = timeslotToTalkListMap.get(timeslot);
                    nextTalkListCell(talkList);
                }
            }
            autoSizeColumnsWithHeader();
        }

        private void writeContentsView() {
            nextSheet("Contents view", 1, 2, true);
            nextRow();
            nextHeaderCell("");
            writeTimeslotDaysHeaders();
            nextRow();
            nextHeaderCell("Content tag");
            writeTimeslotHoursHeaders();

            Map<String, Map<Timeslot, List<Talk>>> tagToTimeslotToTalkListMap = solution.getTalkList().stream()
                    .filter(talk -> talk.getTimeslot() != null)
                    .flatMap(talk -> talk.getContentTagSet().stream()
                            .map(tag -> Pair.of(tag, Pair.of(talk.getTimeslot(), talk))))
                    .collect(groupingBy(Pair::getLeft, groupingBy(o -> o.getRight().getLeft(), mapping(o -> o.getRight().getRight(), toList()))));
            for (Map.Entry<String, Map<Timeslot, List<Talk>>> entry : tagToTimeslotToTalkListMap.entrySet()) {
                nextRow();
                nextHeaderCell(entry.getKey());
                Map<Timeslot, List<Talk>> timeslotToTalkListMap = entry.getValue();
                for (Timeslot timeslot : solution.getTimeslotList()) {
                    List<Talk> talkList = timeslotToTalkListMap.get(timeslot);
                    nextTalkListCell(talkList,
                            talk -> talk.getCode() + " (level " + talk.getAudienceLevel() + ")");
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

        protected void nextSheet(String sheetName, int colSplit, int rowSplit, boolean view) {
            currentSheet = workbook.createSheet(sheetName);
            currentDrawing = currentSheet.createDrawingPatriarch();
            currentSheet.createFreezePane(colSplit, rowSplit);
            currentRowNumber = -1;
            headerCellCount = 0;
            if (view) {
                currentSheet.setTabColor(VIEW_TAB_COLOR);
            }
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

        protected void nextTalkListCell(List<Talk> talkList) {
            nextTalkListCell(false, talkList);
        }

        protected void nextTalkListCell(boolean unavailable, List<Talk> talkList) {
            nextTalkListCell(unavailable, talkList,
                    talk -> talk.getCode() + " @ " + (talk.getRoom() == null ? "No room" : talk.getRoom().getName()));
        }

        protected void nextTalkListCell(List<Talk> talkList, Function<Talk, String> stringFunction) {
            nextTalkListCell(false, talkList, stringFunction);
        }

        protected void nextTalkListCell(boolean unavailable, List<Talk> talkList, Function<Talk, String> stringFunction) {
            if (talkList == null) {
                talkList = Collections.emptyList();
            }
            List<Indictment> indictmentList = talkList.stream()
                    .map(indictmentMap::get).filter(Objects::nonNull).collect(Collectors.toList());
            HardSoftScore score = indictmentList.stream()
                    .flatMap(indictment -> indictment.getConstraintMatchSet().stream())
                    .map(tConstraintMatch -> (HardSoftScore) tConstraintMatch.getScore())
                    // Filter out positive constraints
                    .filter(indictmentScore -> !(indictmentScore.getHardScore() >= 0 && indictmentScore.getSoftScore() >= 0))
                    .reduce(Score::add).orElse(HardSoftScore.ZERO);
            XSSFCell cell;
            if (talkList.stream().anyMatch(Talk::isPinnedByUser)) {
                cell = nextCell(pinnedStyle);
            } else if (!score.isFeasible()) {
                cell = nextCell(hardPenaltyStyle);
            } else if (unavailable) {
                cell = nextCell(unavailableStyle);
            } else if (score.getSoftScore() < 0) {
                cell = nextCell(softPenaltyStyle);
            } else {
                cell = nextCell(wrappedStyle);
            }
            if (!talkList.isEmpty()) {
                ClientAnchor anchor = creationHelper.createClientAnchor();
                anchor.setCol1(cell.getColumnIndex());
                anchor.setCol2(cell.getColumnIndex() + 5);
                anchor.setRow1(currentRow.getRowNum());
                anchor.setRow2(currentRow.getRowNum() + 5);
                Comment comment = currentDrawing.createCellComment(anchor);
                String commentString = talkList.stream()
                        .map(talk -> talk.getCode() + ": "
                                + talk.getTitle() + "\n  "
                                + talk.getSpeakerList().stream().map(Speaker::getName).collect(joining(", "))
                                + (talk.isPinnedByUser() ? "\n  PINNED BY USER" : "")
                        ).collect(joining("\n\n"));
                if (!indictmentList.isEmpty()) {
                    commentString += "\n\nConstraint matches:\n  "
                            + indictmentList.stream().flatMap(indictment -> indictment.getConstraintMatchSet().stream())
                            .map(constraintMatch -> constraintMatch.getConstraintName() + " ("
                                    + constraintMatch.getJustificationList().stream()
                                    .filter(o -> o instanceof Talk).map(o -> ((Talk) o).getCode())
                                    .collect(joining(", "))
                                    + "): " + constraintMatch.getScore().toShortString())
                            .collect(joining("\n  "));
                }
                comment.setString(creationHelper.createRichTextString(commentString));
                cell.setCellComment(comment);
            }
            cell.setCellValue(talkList.stream().map(stringFunction).collect(joining("\n")));
            currentRow.setHeightInPoints(Math.max(currentRow.getHeightInPoints(), talkList.size() * currentSheet.getDefaultRowHeightInPoints()));
        }

        protected XSSFCell nextCell() {
            return nextCell(defaultStyle);
        }

        protected XSSFCell nextCell(XSSFCellStyle cellStyle) {
            currentColumnNumber++;
            XSSFCell cell = currentRow.createCell(currentColumnNumber);
            cell.setCellStyle(cellStyle);
            return cell;
        }

        protected void autoSizeColumnsWithHeader() {
            for (int i = 0; i < headerCellCount; i++) {
                currentSheet.autoSizeColumn(i);
            }
        }

    }

}
