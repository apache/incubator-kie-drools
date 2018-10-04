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
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.examples.common.persistence.AbstractXlsxSolutionFileIO;
import org.optaplanner.examples.conferencescheduling.app.ConferenceSchedulingApp;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceParametrization;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.examples.conferencescheduling.domain.Room;
import org.optaplanner.examples.conferencescheduling.domain.Speaker;
import org.optaplanner.examples.conferencescheduling.domain.Talk;
import org.optaplanner.examples.conferencescheduling.domain.TalkType;
import org.optaplanner.examples.conferencescheduling.domain.Timeslot;
import org.optaplanner.swing.impl.TangoColorFactory;

import static java.util.stream.Collectors.*;
import static org.optaplanner.examples.conferencescheduling.domain.ConferenceParametrization.*;

public class ConferenceSchedulingXlsxFileIO extends AbstractXlsxSolutionFileIO<ConferenceSolution> {

    private boolean strict;

    public ConferenceSchedulingXlsxFileIO() {
        this(true);
    }

    public ConferenceSchedulingXlsxFileIO(boolean strict) {
        super();
        this.strict = strict;
    }

    @Override
    public ConferenceSolution read(File inputSolutionFile) {
        try (InputStream in = new BufferedInputStream(new FileInputStream(inputSolutionFile))) {
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            return new ConferenceSchedulingXlsxReader(workbook).read();
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Failed reading inputSolutionFile ("
                    + inputSolutionFile + ").", e);
        }
    }

    private class ConferenceSchedulingXlsxReader extends AbstractXlsxReader<ConferenceSolution> {

        private Map<String, TalkType> totalTalkTypeMap;
        private Set<String> totalTimeslotTagSet;
        private Set<String> totalRoomTagSet;
        private Map<String, Talk> totalTalkCodeMap;

        public ConferenceSchedulingXlsxReader(XSSFWorkbook workbook) {
            super(workbook);
        }

        @Override
        public ConferenceSolution read() {
            solution = new ConferenceSolution();
            totalTalkTypeMap = new HashMap<>();
            totalTimeslotTagSet = new HashSet<>();
            totalRoomTagSet = new HashSet<>();
            totalTalkCodeMap = new HashMap<>();
            readConfiguration();
            readTimeslotList();
            readRoomList();
            readSpeakerList();
            readTalkList();
            // Needed for merging in the sheet Rooms views
            solution.getTimeslotList().sort(Comparator.comparing(Timeslot::getStartDateTime)
                    .thenComparing(Comparator.comparing(Timeslot::getEndDateTime).reversed()));
            return solution;
        }

        private void readConfiguration() {
            nextSheet("Configuration");
            nextRow();
            readHeaderCell("Conference name");
            solution.setConferenceName(nextStringCell().getStringCellValue());
            if (strict && !VALID_NAME_PATTERN.matcher(solution.getConferenceName()).matches()) {
                throw new IllegalStateException(currentPosition() + ": The conference name (" + solution.getConferenceName()
                        + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
            }
            nextRow(true);
            readHeaderCell("Constraint");
            readHeaderCell("Weight");
            readHeaderCell("Description");
            ConferenceParametrization parametrization = new ConferenceParametrization();
            parametrization.setId(0L);
            readIntConstraintLine(THEME_TRACK_CONFLICT, parametrization::setThemeTrackConflict,
                    "Soft penalty per common theme track of 2 talks that have an overlapping timeslot");
            readIntConstraintLine(SECTOR_CONFLICT, parametrization::setSectorConflict,
                    "Soft penalty per common sector of 2 talks that have an overlapping timeslot");
            readIntConstraintLine(AUDIENCE_TYPE_DIVERSITY, parametrization::setAudienceTypeDiversity,
                    "Soft reward per 2 talks that have the same timeslot and a different audience type");
            readIntConstraintLine(AUDIENCE_TYPE_THEME_TRACK_CONFLICT, parametrization::setAudienceTypeThemeTrackConflict,
                    "Soft penalty per 2 talks that have a common audience type, have a common theme track and have an overlapping timeslot");
            readIntConstraintLine(AUDIENCE_LEVEL_DIVERSITY, parametrization::setAudienceLevelDiversity,
                    "Soft reward per 2 talks that have the same timeslot and a different audience level");
            readIntConstraintLine(AUDIENCE_LEVEL_FLOW_PER_CONTENT_VIOLATION, parametrization::setAudienceLevelFlowPerContentViolation,
                    "Soft penalty per common content of 2 talks with a different audience level for which the easier talk isn't scheduled earlier than the other talk");
            readIntConstraintLine(CONTENT_CONFLICT, parametrization::setContentConflict,
                    "Soft penalty per common content of 2 talks that have an overlapping timeslot");
            readIntConstraintLine(LANGUAGE_DIVERSITY, parametrization::setLanguageDiversity,
                    "Soft reward per 2 talks that have the same timeslot and a different language");
            readIntConstraintLine(SPEAKER_PREFERRED_TIMESLOT_TAGS, parametrization::setSpeakerPreferredTimeslotTags,
                    "Soft penalty per missing preferred tag in a talk's timeslot");
            readIntConstraintLine(SPEAKER_UNDESIRED_TIMESLOT_TAGS, parametrization::setSpeakerUndesiredTimeslotTags,
                    "Soft penalty per undesired tag in a talk's timeslot");
            readIntConstraintLine(TALK_PREFERRED_TIMESLOT_TAGS, parametrization::setTalkPreferredTimeslotTags,
                    "Soft penalty per missing preferred tag in a talk's timeslot");
            readIntConstraintLine(TALK_UNDESIRED_TIMESLOT_TAGS, parametrization::setTalkUndesiredTimeslotTags,
                    "Soft penalty per undesired tag in a talk's timeslot");
            readIntConstraintLine(SPEAKER_PREFERRED_ROOM_TAGS, parametrization::setSpeakerPreferredRoomTags,
                    "Soft penalty per missing preferred tag in a talk's room");
            readIntConstraintLine(SPEAKER_UNDESIRED_ROOM_TAGS, parametrization::setSpeakerUndesiredRoomTags,
                    "Soft penalty per undesired tag in a talk's room");
            readIntConstraintLine(TALK_PREFERRED_ROOM_TAGS, parametrization::setTalkPreferredRoomTags,
                    "Soft penalty per missing preferred tag in a talk's room");
            readIntConstraintLine(TALK_UNDESIRED_ROOM_TAGS, parametrization::setTalkUndesiredRoomTags,
                    "Soft penalty per undesired tag in a talk's room");
            readIntConstraintLine(SAME_DAY_TALKS, parametrization::setSameDayTalks,
                    "Soft penalty per common content/theme of 2 talks that are scheduled on different days");
            readIntConstraintLine(POPULAR_TALKS, parametrization::setPopularTalks,
                    "Soft penalty per 2 talks where the less popular one (has lower favorite count) is assigned a larger room than the more popular talk");
            readIntConstraintLine(CROWD_CONTROL, parametrization::setCrowdControl,
                    "Soft penalty per talks with crowd control risk greater than zero that are not in pairs");
            readIntConstraintLine(PUBLISHED_ROOM, parametrization::setPublishedRoom,
                    "Soft penalty per talk scheduled at a different room than its published one");
            readIntConstraintLine(ROOM_STABILITY, parametrization::setRoomStability,
                    "Soft penalty per two talks with the same track scheduled in the same day but at different rooms");

            readIntConstraintLine(TALK_MUTUALLY_EXCLUSIVE_TALKS_TAGS, parametrization::setTalkMutuallyExclusiveTalksTags,
                    "Medium penalty per two talks that share the same Mutually exclusive talks tag that are scheduled in overlapping timeslots");
            readIntConstraintLine(PUBLISHED_TIMESLOT, parametrization::setPublishedTimeslot,
                    "Medium penalty per talk scheduled at a different timeslot than its published one");

            readIntConstraintLine(TALK_TYPE_OF_TIMESLOT, parametrization::setTalkTypeOfTimeslot,
                    "Hard penalty per talk in a timeslot with another talk type");
            readIntConstraintLine(TALK_TYPE_OF_ROOM, parametrization::setTalkTypeOfRoom,
                    "Hard penalty per talk in a room with another talk type");
            readIntConstraintLine(ROOM_UNAVAILABLE_TIMESLOT, parametrization::setRoomUnavailableTimeslot,
                    "Hard penalty per talk with an unavailable room at its timeslot");
            readIntConstraintLine(ROOM_CONFLICT, parametrization::setRoomConflict,
                    "Hard penalty per pair of talks in the same room in overlapping timeslots");
            readIntConstraintLine(SPEAKER_UNAVAILABLE_TIMESLOT, parametrization::setSpeakerUnavailableTimeslot,
                    "Hard penalty per talk with an unavailable speaker at its timeslot");
            readIntConstraintLine(SPEAKER_CONFLICT, parametrization::setSpeakerConflict,
                    "Hard penalty per pair of talks with the same speaker in overlapping timeslots");
            readIntConstraintLine(SPEAKER_REQUIRED_TIMESLOT_TAGS, parametrization::setSpeakerRequiredTimeslotTags,
                    "Hard penalty per missing required tag in a talk's timeslot");
            readIntConstraintLine(SPEAKER_PROHIBITED_TIMESLOT_TAGs, parametrization::setSpeakerProhibitedTimeslotTags,
                    "Hard penalty per prohibited tag in a talk's timeslot");
            readIntConstraintLine(TALK_REQUIRED_TIMESLOT_TAGS, parametrization::setTalkRequiredTimeslotTags,
                    "Hard penalty per missing required tag in a talk's timeslot");
            readIntConstraintLine(TALK_PROHIBITED_TIMESLOT_TAGS, parametrization::setTalkProhibitedTimeslotTags,
                    "Hard penalty per prohibited tag in a talk's timeslot");
            readIntConstraintLine(SPEAKER_REQUIRED_ROOM_TAGS, parametrization::setSpeakerRequiredRoomTags,
                    "Hard penalty per missing required tag in a talk's room");
            readIntConstraintLine(SPEAKER_PROHIBITED_ROOM_TAGS, parametrization::setSpeakerProhibitedRoomTags,
                    "Hard penalty per prohibited tag in a talk's room");
            readIntConstraintLine(TALK_REQUIRED_ROOM_TAGS, parametrization::setTalkRequiredRoomTags,
                    "Hard penalty per missing required tag in a talk's room");
            readIntConstraintLine(TALK_PROHIBITED_ROOM_TAGS, parametrization::setTalkProhibitedRoomTags,
                    "Hard penalty per prohibited tag in a talk's room");
            readIntConstraintLine(TALK_PREREQUISITE_TALKS, parametrization::setTalkPrerequisiteTalks,
                    "Hard penalty per talk that is scheduled before any of its prerequisite talks");
            readIntConstraintLine(CONSECUTIVE_TALKS_PAUSE, parametrization::setConsecutiveTalksPause,
                    "Hard penalty per two consecutive talks for the same speaker with a pause less than minimum pause");

            solution.setParametrization(parametrization);
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
                        if (strict && !VALID_TAG_PATTERN.matcher(talkTypeName).matches()) {
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
                    if (strict && !VALID_TAG_PATTERN.matcher(tag).matches()) {
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
            readHeaderCell("");
            readTimeslotDaysHeaders();
            nextRow(false);
            readHeaderCell("Name");
            readHeaderCell("Capacity");
            readHeaderCell("Talk types");
            readHeaderCell("Tags");
            readTimeslotHoursHeaders();
            List<Room> roomList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            long id = 0L;
            while (nextRow()) {
                Room room = new Room();
                room.setId(id++);
                room.setName(nextStringCell().getStringCellValue());
                if (strict && !VALID_NAME_PATTERN.matcher(room.getName()).matches()) {
                    throw new IllegalStateException(currentPosition() + ": The room name (" + room.getName()
                            + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
                }
                room.setCapacity(getNextStrictlyPositiveIntegerCell("room name (" + room.getName(), "capacity"));
                String[] talkTypeNames = nextStringCell().getStringCellValue().split(", ");
                Set<TalkType> talkTypeSet;
                if (talkTypeNames.length == 0 || (talkTypeNames.length == 1 && talkTypeNames[0].isEmpty())) {
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
                    if (strict && !VALID_TAG_PATTERN.matcher(tag).matches()) {
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
                if (strict && !VALID_NAME_PATTERN.matcher(speaker.getName()).matches()) {
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
                                + ") should be empty. Use the other sheet (Talks) to pre-assign rooms and timeslots.");
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
            readHeaderCell("Mutually exclusive talks tags");
            readHeaderCell("Prerequisite talks codes");
            readHeaderCell("Favorite count");
            readHeaderCell("Crowd control risk");
            readHeaderCell("Pinned by user");
            readHeaderCell("Timeslot day");
            readHeaderCell("Start");
            readHeaderCell("End");
            readHeaderCell("Room");
            readHeaderCell("Published Timeslot");
            readHeaderCell("Published Start");
            readHeaderCell("Published End");
            readHeaderCell("Published Room");
            List<Talk> talkList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            long id = 0L;
            Map<Pair<LocalDateTime, LocalDateTime>, Timeslot> timeslotMap = solution.getTimeslotList().stream().collect(
                    Collectors.toMap(timeslot -> Pair.of(timeslot.getStartDateTime(), timeslot.getEndDateTime()),
                            Function.identity()));
            Map<String, Room> roomMap = solution.getRoomList().stream().collect(
                    Collectors.toMap(Room::getName, Function.identity()));
            Map<Talk, Set<String>> talkToPrerequisiteTalkSetMap = new HashMap<>();
            while (nextRow()) {
                Talk talk = new Talk();
                talk.setId(id++);
                talk.setCode(nextStringCell().getStringCellValue());
                totalTalkCodeMap.put(talk.getCode(), talk);
                if (strict && !VALID_CODE_PATTERN.matcher(talk.getCode()).matches()) {
                    throw new IllegalStateException(currentPosition() + ": The talk code (" + talk.getCode()
                            + ") must match to the regular expression (" + VALID_CODE_PATTERN + ").");
                }
                talk.setTitle(nextStringCell().getStringCellValue());
                String talkTypeName = nextStringCell().getStringCellValue();
                TalkType talkType = totalTalkTypeMap.get(talkTypeName);
                if (talkType == null) {
                    throw new IllegalStateException(currentPosition()
                            + ": The talk (" + talk + ")'s talkType (" + talkTypeName
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
                    if (strict && !VALID_TAG_PATTERN.matcher(tag).matches()) {
                        throw new IllegalStateException(currentPosition() + ": The talk (" + talk + ")'s theme tag (" + tag
                                + ") must match to the regular expression (" + VALID_TAG_PATTERN + ").");
                    }
                }
                talk.setSectorTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                for (String tag : talk.getSectorTagSet()) {
                    if (strict && !VALID_TAG_PATTERN.matcher(tag).matches()) {
                        throw new IllegalStateException(currentPosition() + ": The talk (" + talk + ")'s sector tag (" + tag
                                + ") must match to the regular expression (" + VALID_TAG_PATTERN + ").");
                    }
                }
                talk.setAudienceTypeSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                for (String audienceType : talk.getAudienceTypeSet()) {
                    if (strict && !VALID_TAG_PATTERN.matcher(audienceType).matches()) {
                        throw new IllegalStateException(currentPosition() + ": The talk (" + talk + ")'s audience type (" + audienceType
                                + ") must match to the regular expression (" + VALID_TAG_PATTERN + ").");
                    }
                }
                talk.setAudienceLevel(getNextStrictlyPositiveIntegerCell("talk with code (" + talk.getCode(), "an audience level"));
                talk.setContentTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(toCollection(LinkedHashSet::new)));
                for (String tag : talk.getContentTagSet()) {
                    if (strict && !VALID_TAG_PATTERN.matcher(tag).matches()) {
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
                talk.setMutuallyExclusiveTalksTagSet(Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(Collectors.toCollection(LinkedHashSet::new)));
                talkToPrerequisiteTalkSetMap.put(talk, Arrays.stream(nextStringCell().getStringCellValue().split(", "))
                        .filter(tag -> !tag.isEmpty()).collect(Collectors.toCollection(LinkedHashSet::new)));
                talk.setFavoriteCount(getNextPositiveIntegerCell("talk with code (" + talk.getCode(), "a Favorite count"));
                talk.setCrowdControlRisk(getNextPositiveIntegerCell("talk with code (" + talk.getCode(), "a crowd control risk"));
                talk.setPinnedByUser(nextBooleanCell().getBooleanCellValue());
                talk.setTimeslot(extractTimeslot(timeslotMap, talk));
                talk.setRoom(extractRoom(roomMap, talk));
                talk.setPublishedTimeslot(extractTimeslot(timeslotMap, talk));
                talk.setPublishedRoom(extractRoom(roomMap, talk));

                talkList.add(talk);
            }

            setPrerequisiteTalkSets(talkToPrerequisiteTalkSetMap);
            solution.setTalkList(talkList);
        }

        private Timeslot extractTimeslot(Map<Pair<LocalDateTime, LocalDateTime>, Timeslot> timeslotMap, Talk talk) {
            Timeslot assignedTimeslot;
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

                assignedTimeslot = timeslotMap.get(Pair.of(startDateTime, endDateTime));
                if (assignedTimeslot == null) {
                    throw new IllegalStateException(currentPosition() + ": The talk with code (" + talk.getCode()
                            + ") has a timeslot date (" + dateString
                            + "), startTime (" + startTimeString + ") and endTime (" + endTimeString
                            + ") that doesn't exist in the other sheet (Timeslots).");
                }

                return assignedTimeslot;
            }

            return null;
        }

        private Room extractRoom(Map<String, Room> roomMap, Talk talk) {
            String roomName = nextStringCell().getStringCellValue();
            if (!roomName.isEmpty()) {
                Room room = roomMap.get(roomName);
                if (room == null) {
                    throw new IllegalStateException(currentPosition() + ": The talk with code (" + talk.getCode()
                            + ") has a roomName (" + roomName
                            + ") that doesn't exist in the other sheet (Rooms).");
                }
                return room;
            }

            return null;
        }

        private int getNextStrictlyPositiveIntegerCell(String classSpecifier, String columnName) {
            double cellValueDouble = nextNumericCell().getNumericCellValue();
            if (strict && (cellValueDouble <= 0 || cellValueDouble != Math.floor(cellValueDouble))) {
                throw new IllegalStateException(currentPosition() + ": The" + classSpecifier
                        + ")'s has " + columnName + " (" + cellValueDouble + ") that isn't a strictly positive integer number.");
            }
            return (int) cellValueDouble;
        }

        private int getNextPositiveIntegerCell(String classSpecifier, String columnName) {
            double cellValueDouble = nextNumericCell().getNumericCellValue();
            if (strict && (cellValueDouble < 0 || cellValueDouble != Math.floor(cellValueDouble))) {
                throw new IllegalStateException(currentPosition() + ": The " + classSpecifier
                        + ")'s has " + columnName + " (" + cellValueDouble + ") that isn't a positive integer number.");
            }
            return (int) cellValueDouble;
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

        private void setPrerequisiteTalkSets(Map<Talk, Set<String>> talkToPrerequisiteTalkSetMap) {
            for (Map.Entry<Talk, Set<String>> entry : talkToPrerequisiteTalkSetMap.entrySet()) {
                Talk currentTalk = entry.getKey();
                Set<Talk> prerequisiteTalkSet = new HashSet<>();
                for (String prerequisiteTalkCode : entry.getValue()) {
                    Talk prerequisiteTalk = totalTalkCodeMap.get(prerequisiteTalkCode);
                    if (prerequisiteTalk == null) {
                        throw new IllegalStateException("The talk (" + currentTalk.getCode()
                                + ") has a prerequisite talk (" + prerequisiteTalkCode + ") that doesn't exist in the talk list.");
                    }
                    prerequisiteTalkSet.add(prerequisiteTalk);
                }
                currentTalk.setPrerequisiteTalkSet(prerequisiteTalkSet);
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

    private class ConferenceSchedulingXlsxWriter extends AbstractXlsxWriter<ConferenceSolution> {

        private Map<String, XSSFCellStyle> themeTrackToStyleMap;

        public ConferenceSchedulingXlsxWriter(ConferenceSolution solution) {
            super(solution, ConferenceSchedulingApp.SOLVER_CONFIG);
        }

        @Override
        public Workbook write() {
            writeSetup();
            initializeThemeTrackToStyleMap();
            writeConfiguration();
            writeTimeslotList();
            writeRoomList();
            writeSpeakerList();
            writeTalkList();
            writeInfeasibleView();
            writeRoomsView();
            writeSpeakersView();
            writeThemeTracksView();
            writeSectorsView();
            writeAudienceTypesView();
            writeAudienceLevelsView();
            writeContentsView();
            writeLanguagesView();
            writeScoreView();
            writeDaysSheets();
            return workbook;
        }

        private void initializeThemeTrackToStyleMap() {
            this.themeTrackToStyleMap = new HashMap<>();
            TangoColorFactory tangoColorFactory = new TangoColorFactory();
            List<String> themeTrackList = solution.getTalkList().stream()
                    .flatMap(talk -> talk.getThemeTrackTagSet().stream())
                    .distinct().collect(toList());
            for (String themeTrack : themeTrackList) {
                XSSFCellStyle style = createStyle(new XSSFColor(tangoColorFactory.pickColor(themeTrack)));
                themeTrackToStyleMap.put(themeTrack, style);
            }
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

            writeIntConstraintLine(THEME_TRACK_CONFLICT, parametrization::getThemeTrackConflict,
                    "Soft penalty per common theme track of 2 talks that have an overlapping timeslot");
            writeIntConstraintLine(SECTOR_CONFLICT, parametrization::getSectorConflict,
                    "Soft penalty per common sector of 2 talks that have an overlapping timeslot");
            writeIntConstraintLine(AUDIENCE_TYPE_DIVERSITY, parametrization::getAudienceTypeDiversity,
                    "Soft reward per 2 talks that have the same timeslot and a different audience type");
            writeIntConstraintLine(AUDIENCE_TYPE_THEME_TRACK_CONFLICT, parametrization::getAudienceTypeThemeTrackConflict,
                    "Soft penalty per 2 talks that have a common audience type, have a common theme track and have an overlapping timeslot");
            writeIntConstraintLine(AUDIENCE_LEVEL_DIVERSITY, parametrization::getAudienceLevelDiversity,
                    "Soft reward per 2 talks that have the same timeslot and a different audience level");
            writeIntConstraintLine(AUDIENCE_LEVEL_FLOW_PER_CONTENT_VIOLATION, parametrization::getAudienceLevelFlowPerContentViolation,
                    "Soft penalty per common content of 2 talks with a different audience level for which the easier talk isn't scheduled earlier than the other talk");
            writeIntConstraintLine(CONTENT_CONFLICT, parametrization::getContentConflict,
                    "Soft penalty per common content of 2 talks that have an overlapping timeslot");
            writeIntConstraintLine(LANGUAGE_DIVERSITY, parametrization::getLanguageDiversity,
                    "Soft reward per 2 talks that have the same timeslot and a different language");
            writeIntConstraintLine(SPEAKER_PREFERRED_TIMESLOT_TAGS, parametrization::getSpeakerPreferredTimeslotTags,
                    "Soft penalty per missing preferred tag in a talk's timeslot");
            writeIntConstraintLine(SPEAKER_UNDESIRED_TIMESLOT_TAGS, parametrization::getSpeakerUndesiredTimeslotTags,
                    "Soft penalty per undesired tag in a talk's timeslot");
            writeIntConstraintLine(TALK_PREFERRED_TIMESLOT_TAGS, parametrization::getTalkPreferredTimeslotTags,
                    "Soft penalty per missing preferred tag in a talk's timeslot");
            writeIntConstraintLine(TALK_UNDESIRED_TIMESLOT_TAGS, parametrization::getTalkUndesiredTimeslotTags,
                    "Soft penalty per undesired tag in a talk's timeslot");
            writeIntConstraintLine(SPEAKER_PREFERRED_ROOM_TAGS, parametrization::getSpeakerPreferredRoomTags,
                    "Soft penalty per missing preferred tag in a talk's room");
            writeIntConstraintLine(SPEAKER_UNDESIRED_ROOM_TAGS, parametrization::getSpeakerUndesiredRoomTags,
                    "Soft penalty per undesired tag in a talk's room");
            writeIntConstraintLine(TALK_PREFERRED_ROOM_TAGS, parametrization::getTalkPreferredRoomTags,
                    "Soft penalty per missing preferred tag in a talk's room");
            writeIntConstraintLine(TALK_UNDESIRED_ROOM_TAGS, parametrization::getTalkUndesiredRoomTags,
                    "Soft penalty per undesired tag in a talk's room");
            writeIntConstraintLine(SAME_DAY_TALKS, parametrization::getSameDayTalks,
                    "Soft penalty per common content/theme of 2 talks that are scheduled on different days");
            writeIntConstraintLine(POPULAR_TALKS, parametrization::getPopularTalks,
                    "Soft penalty per 2 talks where the less popular one (has lower favorite count) is assigned a larger room than the more popular talk");
            writeIntConstraintLine(CROWD_CONTROL, parametrization::getCrowdControl,
                    "Soft penalty per talks with crowd control risk greater than zero that are not in pairs");
            writeIntConstraintLine(PUBLISHED_ROOM, parametrization::getPublishedRoom,
                    "Soft penalty per talk scheduled at a different room than its published one");
            writeIntConstraintLine(ROOM_STABILITY, parametrization::getRoomStability,
                    "Soft penalty per two talks with the same track scheduled in the same day but at different rooms");

            nextRow();
            writeIntConstraintLine(TALK_MUTUALLY_EXCLUSIVE_TALKS_TAGS, parametrization::getTalkMutuallyExclusiveTalksTags,
                    "Medium penalty per two talks that share the same Mutually exclusive talks tag that are scheduled in overlapping timeslots");
            writeIntConstraintLine(PUBLISHED_TIMESLOT, parametrization::getPublishedTimeslot,
                    "Medium penalty per talk scheduled at a different timeslot than its published one");

            nextRow();
            writeIntConstraintLine(TALK_TYPE_OF_TIMESLOT, parametrization::getTalkTypeOfTimeslot,
                    "Hard penalty per talk in a timeslot with another talk type");
            writeIntConstraintLine(TALK_TYPE_OF_ROOM, parametrization::getTalkTypeOfRoom,
                    "Hard penalty per talk in a room with another talk type");
            writeIntConstraintLine(ROOM_UNAVAILABLE_TIMESLOT, parametrization::getRoomUnavailableTimeslot,
                    "Hard penalty per talk with an unavailable room at its timeslot");
            writeIntConstraintLine(ROOM_CONFLICT, parametrization::getRoomConflict,
                    "Hard penalty per pair of talks in the same room in overlapping timeslots");
            writeIntConstraintLine(SPEAKER_UNAVAILABLE_TIMESLOT, parametrization::getSpeakerUnavailableTimeslot,
                    "Hard penalty per talk with an unavailable speaker at its timeslot");
            writeIntConstraintLine(SPEAKER_CONFLICT, parametrization::getSpeakerConflict,
                    "Hard penalty per pair of talks with the same speaker in overlapping timeslots");
            writeIntConstraintLine(SPEAKER_REQUIRED_TIMESLOT_TAGS, parametrization::getSpeakerRequiredTimeslotTags,
                    "Hard penalty per missing required tag in a talk's timeslot");
            writeIntConstraintLine(SPEAKER_PROHIBITED_TIMESLOT_TAGs, parametrization::getSpeakerProhibitedTimeslotTags,
                    "Hard penalty per prohibited tag in a talk's timeslot");
            writeIntConstraintLine(TALK_REQUIRED_TIMESLOT_TAGS, parametrization::getTalkRequiredTimeslotTags,
                    "Hard penalty per missing required tag in a talk's timeslot");
            writeIntConstraintLine(TALK_PROHIBITED_TIMESLOT_TAGS, parametrization::getTalkProhibitedTimeslotTags,
                    "Hard penalty per prohibited tag in a talk's timeslot");
            writeIntConstraintLine(SPEAKER_REQUIRED_ROOM_TAGS, parametrization::getSpeakerRequiredRoomTags,
                    "Hard penalty per missing required tag in a talk's room");
            writeIntConstraintLine(SPEAKER_PROHIBITED_ROOM_TAGS, parametrization::getSpeakerProhibitedRoomTags,
                    "Hard penalty per prohibited tag in a talk's room");
            writeIntConstraintLine(TALK_REQUIRED_ROOM_TAGS, parametrization::getTalkRequiredRoomTags,
                    "Hard penalty per missing required tag in a talk's room");
            writeIntConstraintLine(TALK_PROHIBITED_ROOM_TAGS, parametrization::getTalkProhibitedRoomTags,
                    "Hard penalty per prohibited tag in a talk's room");
            writeIntConstraintLine(TALK_PREREQUISITE_TALKS, parametrization::getTalkPrerequisiteTalks,
                    "Hard penalty per talk that is scheduled before any of its prerequisite talks");
            writeIntConstraintLine(CONSECUTIVE_TALKS_PAUSE, parametrization::getConsecutiveTalksPause,
                    "Hard penalty per two consecutive talks for the same speaker with a pause less than minimum pause");
            autoSizeColumnsWithHeader();
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
            nextHeaderCell("");
            writeTimeslotDaysHeaders();
            nextRow();
            nextHeaderCell("Name");
            nextHeaderCell("Capacity");
            nextHeaderCell("Talk types");
            nextHeaderCell("Tags");
            writeTimeslotHoursHeaders();
            for (Room room : solution.getRoomList()) {
                nextRow();
                nextCell().setCellValue(room.getName());
                nextCell().setCellValue(room.getCapacity());
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
            nextHeaderCell("Mutually exclusive talks tags");
            nextHeaderCell("Prerequisite talks codes");
            nextHeaderCell("Favorite count");
            nextHeaderCell("Crowd control risk");
            nextHeaderCell("Pinned by user");
            nextHeaderCell("Timeslot day");
            nextHeaderCell("Start");
            nextHeaderCell("End");
            nextHeaderCell("Room");
            nextHeaderCell("Published Timeslot");
            nextHeaderCell("Published Start");
            nextHeaderCell("Published End");
            nextHeaderCell("Published Room");

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
                nextCell().setCellValue(String.join(", ", talk.getMutuallyExclusiveTalksTagSet()));
                nextCell().setCellValue(String.join(", ", talk.getPrerequisiteTalkSet().stream().map(Talk::getCode).collect(toList())));
                nextCell().setCellValue(talk.getFavoriteCount());
                nextCell().setCellValue(talk.getCrowdControlRisk());
                nextCell(talk.isPinnedByUser() ? pinnedStyle : defaultStyle).setCellValue(talk.isPinnedByUser());
                nextCell(talk.getTimeslot().equals(talk.getPublishedTimeslot()) ? defaultStyle : republishedStyle)
                        .setCellValue(talk.getTimeslot() == null ? "" : DAY_FORMATTER.format(talk.getTimeslot().getDate()));
                nextCell(talk.getTimeslot().equals(talk.getPublishedTimeslot()) ? defaultStyle : republishedStyle)
                        .setCellValue(talk.getTimeslot() == null ? "" : TIME_FORMATTER.format(talk.getTimeslot().getStartDateTime()));
                nextCell(talk.getTimeslot().equals(talk.getPublishedTimeslot()) ? defaultStyle : republishedStyle)
                        .setCellValue(talk.getTimeslot() == null ? "" : TIME_FORMATTER.format(talk.getTimeslot().getEndDateTime()));
                nextCell(talk.getRoom().equals(talk.getPublishedRoom()) ? defaultStyle : republishedStyle)
                        .setCellValue(talk.getRoom() == null ? "" : talk.getRoom().getName());
                nextCell().setCellValue(talk.getPublishedTimeslot() == null ? "" : DAY_FORMATTER.format(talk.getPublishedTimeslot().getDate()));
                nextCell().setCellValue(talk.getPublishedTimeslot() == null ? "" : TIME_FORMATTER.format(talk.getPublishedTimeslot().getStartDateTime()));
                nextCell().setCellValue(talk.getPublishedTimeslot() == null ? "" : TIME_FORMATTER.format(talk.getPublishedTimeslot().getEndDateTime()));
                nextCell().setCellValue(talk.getPublishedRoom() == null ? "" : talk.getPublishedRoom().getName());
            }
            autoSizeColumnsWithHeader();
        }

        private void writeInfeasibleView() {
            if (solution.getScore() == null || solution.getScore().isFeasible()) {
                return;
            }
            nextSheet("Infeasible view", 1, 1, true);
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
            int sessionCount = 0;
            for (Timeslot timeslot : solution.getTimeslotList()) {
                for (Room room : solution.getRoomList()) {
                    if (!Collections.disjoint(timeslot.getTalkTypeSet(), room.getTalkTypeSet())
                            && !room.getUnavailableTimeslotSet().contains(timeslot)) {
                        sessionCount++;
                    }
                }
            }
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
                currentRow.setHeightInPoints(3 * currentSheet.getDefaultRowHeightInPoints());
                nextCell().setCellValue(room.getName());
                List<Talk> roomTalkList = solution.getTalkList().stream()
                        .filter(talk -> talk.getRoom() == room)
                        .collect(toList());

                Timeslot mergePreviousTimeslot = null;
                int mergeStart = -1;
                for (Timeslot timeslot : solution.getTimeslotList()) {
                    List<Talk> talkList = roomTalkList.stream()
                            .filter(talk -> talk.getTimeslot() == timeslot).collect(toList());
                    if (talkList.isEmpty() && mergePreviousTimeslot != null
                            && timeslot.getStartDateTime().compareTo(mergePreviousTimeslot.getEndDateTime()) < 0) {
                        nextCell();
                    } else {
                        if (mergePreviousTimeslot != null && mergeStart < currentColumnNumber) {
                            currentSheet.addMergedRegion(new CellRangeAddress(currentRowNumber, currentRowNumber, mergeStart, currentColumnNumber));
                        }
                        boolean unavailable = room.getUnavailableTimeslotSet().contains(timeslot)
                                || Collections.disjoint(room.getTalkTypeSet(), timeslot.getTalkTypeSet());
                        nextTalkListCell(unavailable, talkList, talk -> talk.getCode() + ": " + talk.getTitle() + "\n  "
                                + talk.getSpeakerList().stream().map(Speaker::getName).collect(joining(", ")));
                        mergePreviousTimeslot = talkList.isEmpty() ? null : timeslot;
                        mergeStart = currentColumnNumber;
                    }
                }
                if (mergePreviousTimeslot != null && mergeStart < currentColumnNumber) {
                    currentSheet.addMergedRegion(new CellRangeAddress(currentRowNumber, currentRowNumber, mergeStart, currentColumnNumber));
                }
            }
            currentSheet.autoSizeColumn(0);
            for (int i = 1; i < headerCellCount; i++) {
                currentSheet.setColumnWidth(i, 20 * 256);
            }
        }

        private void writeSpeakersView() {
            nextSheet("Speakers view", 1, 2, true);
            String[] filteredConstraintNames = {
                    SPEAKER_UNAVAILABLE_TIMESLOT, SPEAKER_CONFLICT,
                    SPEAKER_REQUIRED_TIMESLOT_TAGS, SPEAKER_PROHIBITED_TIMESLOT_TAGs,
                    SPEAKER_PREFERRED_TIMESLOT_TAGS, SPEAKER_UNDESIRED_TIMESLOT_TAGS,
                    SPEAKER_REQUIRED_ROOM_TAGS, SPEAKER_PROHIBITED_ROOM_TAGS,
                    SPEAKER_PREFERRED_ROOM_TAGS, SPEAKER_UNDESIRED_ROOM_TAGS};
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

                Timeslot mergePreviousTimeslot = null;
                int mergeStart = -1;
                for (Timeslot timeslot : solution.getTimeslotList()) {
                    List<Talk> talkList = timeslotTalkList.stream()
                            .filter(talk -> talk.getTimeslot() == timeslot).collect(toList());
                    if (talkList.isEmpty() && mergePreviousTimeslot != null
                            && timeslot.getStartDateTime().compareTo(mergePreviousTimeslot.getEndDateTime()) < 0) {
                        nextCell();
                    } else {
                        if (mergePreviousTimeslot != null && mergeStart < currentColumnNumber) {
                            currentSheet.addMergedRegion(new CellRangeAddress(currentRowNumber, currentRowNumber, mergeStart, currentColumnNumber));
                        }
                        boolean unavailable = speaker.getUnavailableTimeslotSet().contains(timeslot);
                        nextTalkListCell(unavailable, talkList, filteredConstraintNames);
                        mergePreviousTimeslot = talkList.isEmpty() ? null : timeslot;
                        mergeStart = currentColumnNumber;
                    }
                }
                if (mergePreviousTimeslot != null && mergeStart < currentColumnNumber) {
                    currentSheet.addMergedRegion(new CellRangeAddress(currentRowNumber, currentRowNumber, mergeStart, currentColumnNumber));
                }
            }
            currentSheet.autoSizeColumn(0);
            for (int i = 1; i < headerCellCount; i++) {
                currentSheet.setColumnWidth(i, 20 * 256);
            }
        }

        private void writeThemeTracksView() {
            nextSheet("Theme tracks view", 1, 2, true);
            String[] filteredConstraintNames = {THEME_TRACK_CONFLICT, AUDIENCE_TYPE_THEME_TRACK_CONFLICT, SAME_DAY_TALKS};
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
                    nextTalkListCell(talkList, filteredConstraintNames);
                }
            }
            autoSizeColumnsWithHeader();
        }

        private void writeSectorsView() {
            nextSheet("Sectors view", 1, 2, true);
            String[] filteredConstraintNames = {SECTOR_CONFLICT};
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
                    nextTalkListCell(talkList, filteredConstraintNames);
                }
            }
            autoSizeColumnsWithHeader();
        }

        private void writeAudienceTypesView() {
            nextSheet("Audience types view", 1, 2, true);
            String[] filteredConstraintNames = {AUDIENCE_TYPE_DIVERSITY, AUDIENCE_TYPE_THEME_TRACK_CONFLICT};
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
                    nextTalkListCell(talkList, filteredConstraintNames);
                }
            }
            autoSizeColumnsWithHeader();
        }

        private void writeAudienceLevelsView() {
            nextSheet("Audience levels view", 1, 2, true);
            String[] filteredConstraintNames = {AUDIENCE_LEVEL_DIVERSITY, AUDIENCE_LEVEL_FLOW_PER_CONTENT_VIOLATION};
            nextRow();
            nextHeaderCell("");
            writeTimeslotDaysHeaders();
            nextRow();
            nextHeaderCell("Audience level");
            writeTimeslotHoursHeaders();

            Map<Integer, Map<Timeslot, List<Talk>>> levelToTimeslotToTalkListMap = solution.getTalkList().stream()
                    .filter(talk -> talk.getTimeslot() != null)
                    .map(talk -> Pair.of(talk.getAudienceLevel(), Pair.of(talk.getTimeslot(), talk)))
                    .collect(groupingBy(Pair::getLeft, groupingBy(o -> o.getRight().getLeft(), mapping(o -> o.getRight().getRight(), toList()))));
            for (Map.Entry<Integer, Map<Timeslot, List<Talk>>> entry : levelToTimeslotToTalkListMap.entrySet()) {
                nextRow();
                nextHeaderCell(Integer.toString(entry.getKey()));
                Map<Timeslot, List<Talk>> timeslotToTalkListMap = entry.getValue();
                for (Timeslot timeslot : solution.getTimeslotList()) {
                    List<Talk> talkList = timeslotToTalkListMap.get(timeslot);
                    nextTalkListCell(talkList, filteredConstraintNames);
                }
            }
            autoSizeColumnsWithHeader();
        }

        private void writeContentsView() {
            nextSheet("Contents view", 1, 2, true);
            String[] filteredConstraintNames = {AUDIENCE_LEVEL_FLOW_PER_CONTENT_VIOLATION, CONTENT_CONFLICT};
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
                            talk -> talk.getCode() + " (level " + talk.getAudienceLevel() + ")",
                            filteredConstraintNames,
                            justificationList -> justificationList.stream().allMatch(justification -> !(justification instanceof Talk)
                                    || ((Talk) justification).getContentTagSet().contains(entry.getKey())
                            ));
                }
            }
            autoSizeColumnsWithHeader();
        }

        private void writeLanguagesView() {
            nextSheet("Languages view", 1, 2, true);
            String[] filteredConstraintNames = {LANGUAGE_DIVERSITY};
            nextRow();
            nextHeaderCell("");
            writeTimeslotDaysHeaders();
            nextRow();
            nextHeaderCell("Language");
            writeTimeslotHoursHeaders();

            Map<String, Map<Timeslot, List<Talk>>> languageToTimeslotToTalkListMap = solution.getTalkList().stream()
                    .filter(talk -> talk.getTimeslot() != null)
                    .map(talk -> Pair.of(talk.getLanguage(), Pair.of(talk.getTimeslot(), talk)))
                    .collect(groupingBy(Pair::getLeft, groupingBy(o -> o.getRight().getLeft(), mapping(o -> o.getRight().getRight(), toList()))));
            for (Map.Entry<String, Map<Timeslot, List<Talk>>> entry : languageToTimeslotToTalkListMap.entrySet()) {
                nextRow();
                nextHeaderCell(entry.getKey());
                Map<Timeslot, List<Talk>> timeslotToTalkListMap = entry.getValue();
                for (Timeslot timeslot : solution.getTimeslotList()) {
                    List<Talk> talkList = timeslotToTalkListMap.get(timeslot);
                    nextTalkListCell(talkList, filteredConstraintNames);
                }
            }
            autoSizeColumnsWithHeader();
        }

        private void writeScoreView() {
            nextSheet("Score view", 1, 3, true);
            nextRow();
            nextHeaderCell("Score");
            nextCell().setCellValue(solution.getScore() == null ? "Not yet solved" : solution.getScore().toShortString());
            nextRow();
            nextRow();
            nextHeaderCell("Constraint match");
            nextHeaderCell("Match score");
            nextHeaderCell("Total score");
            for (ConstraintMatchTotal constraintMatchTotal : constraintMatchTotalList) {
                nextRow();
                nextHeaderCell(constraintMatchTotal.getConstraintName());
                nextCell();
                nextCell().setCellValue(constraintMatchTotal.getScore().toShortString());
                List<ConstraintMatch> constraintMatchList = new ArrayList<>(constraintMatchTotal.getConstraintMatchSet());
                constraintMatchList.sort(Comparator.comparing(ConstraintMatch::getScore));
                for (ConstraintMatch constraintMatch : constraintMatchList) {
                    nextRow();
                    nextCell().setCellValue("    " + constraintMatch.getJustificationList().stream()
                            .filter(o -> o instanceof Talk).map(o -> ((Talk) o).getCode())
                            .collect(joining(", ")));
                    nextCell().setCellValue(constraintMatch.getScore().toShortString());
                    nextCell();
                    nextCell();
                }
            }
            autoSizeColumnsWithHeader();
        }

        private void writeDaysSheets() {
            List<LocalDate> dayList = solution.getTimeslotList().stream().map(Timeslot::getDate).distinct().collect(toList());

            for (LocalDate day : dayList) {
                List<Timeslot> dayTimeslotList = solution.getTimeslotList().stream().filter(timeslot -> timeslot.getDate().equals(day)).collect(toList());
                List<Talk> dayTalkList = solution.getTalkList().stream().filter(talk ->
                        talk.getTimeslot() != null && talk.getTimeslot().getDate().equals(day)).collect(toList());
                writeDaySheet(day, dayTimeslotList, dayTalkList);
            }
        }

        private void writeDaySheet(LocalDate day, List<Timeslot> timeslotList, List<Talk> talkList) {
            nextSheet(DAY_FORMATTER.format(day), 1, 1, true);
            nextRow();
            nextHeaderCell(DAY_FORMATTER.format(day));
            writeTimeslotHoursVertically(timeslotList);
            List<Room> dayRoomList = talkList.stream().map(Talk::getRoom).distinct().collect(toList());
            dayRoomList.sort(Comparator.comparing(Room::getName));
            for (Room room : dayRoomList) {
                currentColumnNumber++;
                currentRowNumber = -1;
                nextCellVertically().setCellValue(room.getName());
                List<Talk> roomTalkList = talkList.stream()
                        .filter(talk -> talk.getRoom() == room)
                        .collect(toList());
                writeRoomTalks(timeslotList, room, roomTalkList);
            }
            currentSheet.autoSizeColumn(0);
            for (int i = 1; i < currentSheet.getRow(0).getPhysicalNumberOfCells(); i++) {
                currentSheet.setColumnWidth(i, 15 * 256);
            }
        }

        private void writeRoomTalks(List<Timeslot> dayTimeslotList, Room room, List<Talk> roomTalkList) {
            Timeslot mergePreviousTimeslot = null;
            int mergeStart = -1;
            for (Timeslot timeslot : dayTimeslotList) {
                List<Talk> talkList = roomTalkList.stream()
                        .filter(talk -> talk.getTimeslot() == timeslot).collect(toList());
                if (talkList.isEmpty() && mergePreviousTimeslot != null
                        && timeslot.getStartDateTime().compareTo(mergePreviousTimeslot.getEndDateTime()) < 0) {
                    nextCellVertically();
                } else {
                    if (mergePreviousTimeslot != null && mergeStart < currentRowNumber) {
                        currentSheet.addMergedRegion(new CellRangeAddress(mergeStart, currentRowNumber, currentColumnNumber, currentColumnNumber));
                    }
                    boolean unavailable = room.getUnavailableTimeslotSet().contains(timeslot)
                            || Collections.disjoint(room.getTalkTypeSet(), timeslot.getTalkTypeSet());
                    nextTalkListCell(unavailable, talkList, talk -> StringUtils.abbreviate(talk.getTitle(), 50) + "\n"
                            + StringUtils.abbreviate(talk.getSpeakerList().stream().map(Speaker::getName).collect(joining(", ")), 30), true);
                    mergePreviousTimeslot = talkList.isEmpty() ? null : timeslot;
                    mergeStart = currentRowNumber;
                }
            }
            if (mergePreviousTimeslot != null && mergeStart < currentRowNumber) {
                currentSheet.addMergedRegion(new CellRangeAddress(mergeStart, currentRowNumber, currentColumnNumber, currentColumnNumber));
            }
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

        private void writeTimeslotHoursVertically(List<Timeslot> dayTimeslotList) {
            for (Timeslot timeslot : dayTimeslotList) {
                nextRow();
                nextCell().setCellValue(TIME_FORMATTER.format(timeslot.getStartDateTime())
                        + "-" + TIME_FORMATTER.format(timeslot.getEndDateTime()));
                currentRow.setHeightInPoints(3 * currentSheet.getDefaultRowHeightInPoints());
            }
        }

        protected void nextTalkListCell(List<Talk> talkList, String[] filteredConstraintNames) {
            nextTalkListCell(false, talkList, filteredConstraintNames);
        }

        protected void nextTalkListCell(boolean unavailable, List<Talk> talkList, String[] filteredConstraintNames) {
            nextTalkListCell(unavailable, talkList,
                    talk -> talk.getCode() + " @ " + (talk.getRoom() == null ? "No room" : talk.getRoom().getName()),
                    filteredConstraintNames, false, null);
        }

        protected void nextTalkListCell(List<Talk> talkList, Function<Talk, String> stringFunction,
                                        String[] filteredConstraintNames, Predicate<List<Object>> isValidJustificationList) {
            nextTalkListCell(false, talkList, stringFunction, filteredConstraintNames, false, isValidJustificationList);
        }

        protected void nextTalkListCell(boolean unavailable, List<Talk> talkList, Function<Talk, String> stringFunction) {
            nextTalkListCell(unavailable, talkList, stringFunction, null, false, null);
        }

        protected void nextTalkListCell(boolean unavailable, List<Talk> talkList, Function<Talk, String> stringFunction, boolean isVerticalView) {
            nextTalkListCell(unavailable, talkList, stringFunction, null, isVerticalView, null);
        }

        protected void nextTalkListCell(boolean unavailable, List<Talk> talkList, Function<Talk, String> stringFunction,
                                        String[] filteredConstraintNames, boolean isPrintedView, Predicate<List<Object>> isValidJustificationList) {
            List<String> filteredConstraintNameList = (filteredConstraintNames == null) ? null
                    : Arrays.asList(filteredConstraintNames);
            if (talkList == null) {
                talkList = Collections.emptyList();
            }
            HardMediumSoftScore score = talkList.stream()
                    .map(indictmentMap::get).filter(Objects::nonNull)
                    .flatMap(indictment -> indictment.getConstraintMatchSet().stream())
                    // Filter out filtered constraints
                    .filter(constraintMatch -> filteredConstraintNameList == null
                            || filteredConstraintNameList.contains(constraintMatch.getConstraintName()))
                    .filter(constraintMatch -> isValidJustificationList == null
                            || isValidJustificationList.test(constraintMatch.getJustificationList()))
                    .map(constraintMatch -> (HardMediumSoftScore) constraintMatch.getScore())
                    // Filter out positive constraints
                    .filter(indictmentScore -> !(indictmentScore.getHardScore() >= 0 && indictmentScore.getMediumScore() >= 0 && indictmentScore.getSoftScore() >= 0))
                    .reduce(Score::add).orElse(HardMediumSoftScore.ZERO);
            XSSFCell cell;
            if (isPrintedView) {
                cell = nextCellVertically(talkList.isEmpty() || talkList.get(0).getThemeTrackTagSet().isEmpty() ? wrappedStyle :
                        themeTrackToStyleMap.get(talkList.get(0).getThemeTrackTagSet().iterator().next()));
            } else if (talkList.stream().anyMatch(Talk::isPinnedByUser)) {
                cell = nextCell(pinnedStyle);
            } else if (!score.isFeasible()) {
                cell = nextCell(hardPenaltyStyle);
            } else if (unavailable) {
                cell = nextCell(unavailableStyle);
            } else if (score.getMediumScore() < 0) {
                cell = nextCell(mediumPenaltyStyle);
            } else if (score.getSoftScore() < 0) {
                cell = nextCell(softPenaltyStyle);
            } else {
                cell = nextCell(wrappedStyle);
            }
            if (!talkList.isEmpty()) {
                ClientAnchor anchor = creationHelper.createClientAnchor();
                anchor.setCol1(cell.getColumnIndex());
                anchor.setCol2(cell.getColumnIndex() + 4);
                anchor.setRow1(currentRow.getRowNum());
                anchor.setRow2(currentRow.getRowNum() + 4);
                Comment comment = currentDrawing.createCellComment(anchor);
                StringBuilder commentString = new StringBuilder(talkList.size() * 200);
                for (Talk talk : talkList) {
                    commentString.append(talk.getCode()).append("-").append(String.join(", ", talk.getThemeTrackTagSet()))
                            .append(": ").append(talk.getTitle()).append("\n    ")
                            .append(talk.getSpeakerList().stream().map(Speaker::getName).collect(joining(", ")))
                            .append(talk.isPinnedByUser() ? "\nPINNED BY USER" : "");
                    Indictment indictment = indictmentMap.get(talk);
                    if (indictment != null) {
                        commentString.append("\n").append(indictment.getScore().toShortString())
                                .append(" total");
                        Set<ConstraintMatch> constraintMatchSet = indictment.getConstraintMatchSet().stream()
                                .filter(constraintMatch -> filteredConstraintNameList == null
                                        || filteredConstraintNameList.contains(constraintMatch.getConstraintName()))
                                .collect(toSet());
                        List<String> constraintNameList = constraintMatchSet.stream()
                                .map(ConstraintMatch::getConstraintName).distinct().collect(toList());
                        for (String constraintName : constraintNameList) {
                            List<ConstraintMatch> filteredConstraintMatchList = constraintMatchSet.stream()
                                    .filter(constraintMatch -> constraintMatch.getConstraintName().equals(constraintName)
                                            && (isValidJustificationList == null || isValidJustificationList.test(constraintMatch.getJustificationList())))
                                    .collect(toList());
                            Score sum = filteredConstraintMatchList.stream()
                                    .map(ConstraintMatch::getScore)
                                    .reduce(Score::add).orElse(HardMediumSoftScore.ZERO);
                            String justificationTalkCodes = filteredConstraintMatchList.stream()
                                    .flatMap(constraintMatch -> constraintMatch.getJustificationList().stream())
                                    .filter(justification -> justification instanceof Talk && justification != talk)
                                    .distinct().map(o -> ((Talk) o).getCode()).collect(joining(", "));
                            commentString.append("\n    ").append(sum.toShortString())
                                    .append(" for ").append(filteredConstraintMatchList.size())
                                    .append(" ").append(constraintName).append("s")
                                    .append("\n        ").append(justificationTalkCodes);
                        }
                    }
                    commentString.append("\n\n");
                }
                comment.setString(creationHelper.createRichTextString(commentString.toString()));
                cell.setCellComment(comment);
            }
            cell.setCellValue(talkList.stream().map(stringFunction).collect(joining("\n")));
            currentRow.setHeightInPoints(Math.max(currentRow.getHeightInPoints(), talkList.size() * currentSheet.getDefaultRowHeightInPoints()));
        }
    }
}
