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

package org.optaplanner.examples.examination.persistence;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;
import org.optaplanner.examples.examination.app.ExaminationApp;
import org.optaplanner.examples.examination.domain.Exam;
import org.optaplanner.examples.examination.domain.Examination;
import org.optaplanner.examples.examination.domain.ExaminationConstraintConfiguration;
import org.optaplanner.examples.examination.domain.FollowingExam;
import org.optaplanner.examples.examination.domain.LeadingExam;
import org.optaplanner.examples.examination.domain.Period;
import org.optaplanner.examples.examination.domain.PeriodPenalty;
import org.optaplanner.examples.examination.domain.PeriodPenaltyType;
import org.optaplanner.examples.examination.domain.Room;
import org.optaplanner.examples.examination.domain.RoomPenalty;
import org.optaplanner.examples.examination.domain.RoomPenaltyType;
import org.optaplanner.examples.examination.domain.Student;
import org.optaplanner.examples.examination.domain.Topic;

public class ExaminationImporter extends AbstractTxtSolutionImporter<Examination> {

    private static final String INPUT_FILE_SUFFIX = "exam";
    private static final String SPLIT_REGEX = "\\,\\ ?";

    public static void main(String[] args) {
        SolutionConverter<Examination> converter = SolutionConverter.createImportConverter(
                ExaminationApp.DATA_DIR_NAME, new ExaminationImporter(), Examination.class);
        converter.convertAll();
    }

    @Override
    public String getInputFileSuffix() {
        return INPUT_FILE_SUFFIX;
    }

    @Override
    public TxtInputBuilder<Examination> createTxtInputBuilder() {
        return new ExaminationInputBuilder();
    }

    public static class ExaminationInputBuilder extends TxtInputBuilder<Examination> {

        private static final Comparator<Topic> COMPARATOR = Comparator.comparing(Topic::getStudentSize)
                .thenComparingLong(Topic::getId);
        private Examination examination;
        private Map<Topic, Set<Topic>> coincidenceMap;
        private Map<Topic, Set<Topic>> exclusionMap;
        private Map<Topic, Set<Topic>> afterMap;

        @Override
        public Examination readSolution() throws IOException {
            examination = new Examination();
            examination.setId(0L);

            readTopicListAndStudentList();
            readPeriodList();
            readRoomList();

            readPeriodPenaltyList();
            readRoomPenaltyList();
            readInstitutionalWeighting();
            tagFrontLoadLargeTopics();
            tagFrontLoadLastPeriods();

            createExamList();

            int possibleForOneExamSize = examination.getPeriodList().size() * examination.getRoomList().size();
            BigInteger possibleSolutionSize = BigInteger.valueOf(possibleForOneExamSize).pow(
                    examination.getExamList().size());
            logger.info("Examination {} has {} students, {} exams, {} periods, {} rooms, {} period constraints"
                            + " and {} room constraints with a search space of {}.",
                    getInputId(),
                    examination.getStudentList().size(),
                    examination.getExamList().size(),
                    examination.getPeriodList().size(),
                    examination.getRoomList().size(),
                    examination.getPeriodPenaltyList().size(),
                    examination.getRoomPenaltyList().size(),
                    getFlooredPossibleSolutionSize(possibleSolutionSize));
            return examination;
        }

        private void readTopicListAndStudentList() throws IOException {
            coincidenceMap = new LinkedHashMap<>();
            exclusionMap = new LinkedHashMap<>();
            afterMap = new LinkedHashMap<>();
            Map<Integer, Student> studentMap = new HashMap<>();
            int examSize = readHeaderWithNumber("Exams");
            List<Topic> topicList = new ArrayList<>(examSize);
            for (int i = 0; i < examSize; i++) {
                Topic topic = new Topic();
                topic.setId((long) i);
                String line = bufferedReader.readLine();
                String[] lineTokens = line.split(SPLIT_REGEX);
                topic.setDuration(Integer.parseInt(lineTokens[0]));
                List<Student> topicStudentList = new ArrayList<>(lineTokens.length - 1);
                for (int j = 1; j < lineTokens.length; j++) {
                    topicStudentList.add(findOrCreateStudent(studentMap, Integer.parseInt(lineTokens[j])));
                }
                topic.setStudentList(topicStudentList);
                topic.setFrontLoadLarge(false);
                topicList.add(topic);
                coincidenceMap.put(topic, new HashSet<>());
                exclusionMap.put(topic, new HashSet<>());
                afterMap.put(topic, new HashSet<>());
            }
            examination.setTopicList(topicList);
            List<Student> studentList = new ArrayList<>(studentMap.values());
            examination.setStudentList(studentList);
        }

        private Student findOrCreateStudent(Map<Integer, Student> studentMap, int id) {
            Student student = studentMap.get(id);
            if (student == null) {
                student = new Student();
                student.setId((long) id);
                studentMap.put(id, student);
            }
            return student;
        }

        private void readPeriodList() throws IOException {
            int periodSize = readHeaderWithNumber("Periods");
            List<Period> periodList = new ArrayList<>(periodSize);
            DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd:MM:yyyy HH:mm:ss", Locale.UK);
            LocalDateTime referenceDateTime = null;
            for (int i = 0; i < periodSize; i++) {
                Period period = new Period();
                period.setId((long) i);
                String line = bufferedReader.readLine();
                String[] lineTokens = line.split(SPLIT_REGEX);
                if (lineTokens.length != 4) {
                    throw new IllegalArgumentException("Read line (" + line + ") is expected to contain 4 tokens.");
                }
                String startDateTimeString = lineTokens[0] + " " + lineTokens[1];
                period.setStartDateTimeString(startDateTimeString);
                period.setPeriodIndex(i);
                LocalDateTime dateTime;
                try {
                    dateTime = LocalDateTime.parse(startDateTimeString, DATE_FORMAT);
                } catch (DateTimeParseException e) {
                    throw new IllegalArgumentException("Illegal startDateTimeString (" + startDateTimeString + ").", e);
                }
                if (referenceDateTime == null) {
                    referenceDateTime = dateTime;
                }
                int dayIndex = (int) ChronoUnit.DAYS.between(referenceDateTime, dateTime);
                if (dayIndex < 0) {
                    throw new IllegalStateException("The periods should be in ascending order.");
                }
                period.setDayIndex(dayIndex);
                period.setDuration(Integer.parseInt(lineTokens[2]));
                period.setPenalty(Integer.parseInt(lineTokens[3]));
                periodList.add(period);
            }
            examination.setPeriodList(periodList);
        }

        private void readRoomList() throws IOException {
            int roomSize = readHeaderWithNumber("Rooms");
            List<Room> roomList = new ArrayList<>(roomSize);
            for (int i = 0; i < roomSize; i++) {
                Room room = new Room();
                room.setId((long) i);
                String line = bufferedReader.readLine();
                String[] lineTokens = line.split(SPLIT_REGEX);
                if (lineTokens.length != 2) {
                    throw new IllegalArgumentException("Read line (" + line + ") is expected to contain 2 tokens.");
                }
                room.setCapacity(Integer.parseInt(lineTokens[0]));
                room.setPenalty(Integer.parseInt(lineTokens[1]));
                roomList.add(room);
            }
            examination.setRoomList(roomList);
        }

        private void readPeriodPenaltyList() throws IOException {
            readConstantLine("\\[PeriodHardConstraints\\]");
            List<Topic> topicList = examination.getTopicList();
            List<PeriodPenalty> periodPenaltyList = new ArrayList<>();
            String line = bufferedReader.readLine();
            int id = 0;
            while (!line.equals("[RoomHardConstraints]")) {
                String[] lineTokens = line.split(SPLIT_REGEX);
                if (lineTokens.length != 3) {
                    throw new IllegalArgumentException("Read line (" + line + ") is expected to contain 3 tokens.");
                }
                PeriodPenalty periodPenalty = new PeriodPenalty();
                periodPenalty.setId((long) id);
                id++;
                Topic leftTopic = topicList.get(Integer.parseInt(lineTokens[0]));
                periodPenalty.setLeftTopic(leftTopic);
                PeriodPenaltyType periodPenaltyType = PeriodPenaltyType.valueOf(lineTokens[1]);
                periodPenalty.setPeriodPenaltyType(periodPenaltyType);
                Topic rightTopic = topicList.get(Integer.parseInt(lineTokens[2]));
                periodPenalty.setRightTopic(rightTopic);
                boolean ignorePenalty = false;

                switch (periodPenaltyType) {
                    case EXAM_COINCIDENCE:
                        if (leftTopic.getId().equals(rightTopic.getId())) {
                            logger.warn("  Filtering out periodPenalty (" + periodPenalty
                                    + ") because the left and right topic are the same.");
                            ignorePenalty = true;
                        } else if (!Collections.disjoint(leftTopic.getStudentList(), rightTopic.getStudentList())) {
                            throw new IllegalStateException("PeriodPenalty (" + periodPenalty
                                    + ") for leftTopic (" + leftTopic + ") and rightTopic (" + rightTopic
                                    + ")'s left and right topic share students.");
                        } else if (coincidenceMap.get(leftTopic).contains(rightTopic)) {
                            logger.trace("  Filtering out periodPenalty (" + periodPenalty
                                    + ") for leftTopic (" + leftTopic + ") and rightTopic (" + rightTopic
                                    + ") because it is mentioned twice.");
                            ignorePenalty = true;
                        } else {
                            boolean added = coincidenceMap.get(leftTopic).add(rightTopic)
                                    && coincidenceMap.get(rightTopic).add(leftTopic);
                            if (!added) {
                                throw new IllegalStateException("The periodPenaltyType (" + periodPenaltyType
                                        + ") for leftTopic (" + leftTopic + ") and rightTopic (" + rightTopic
                                        + ") was not successfully added twice.");
                            }
                        }
                        break;
                    case EXCLUSION:
                        if (leftTopic.getId().equals(rightTopic.getId())) {
                            logger.warn("  Filtering out periodPenalty (" + periodPenalty
                                    + ") for leftTopic (" + leftTopic + ") and rightTopic (" + rightTopic
                                    + ") because the left and right topic are the same.");
                            ignorePenalty = true;
                        } else if (exclusionMap.get(leftTopic).contains(rightTopic)) {
                            logger.trace("  Filtering out periodPenalty (" + periodPenalty
                                    + ") for leftTopic (" + leftTopic + ") and rightTopic (" + rightTopic
                                    + ") because it is mentioned twice.");
                            ignorePenalty = true;
                        } else {
                            boolean added = exclusionMap.get(leftTopic).add(rightTopic)
                                    && exclusionMap.get(rightTopic).add(leftTopic);
                            if (!added) {
                                throw new IllegalStateException("The periodPenaltyType (" + periodPenaltyType
                                        + ") for leftTopic (" + leftTopic + ") and rightTopic (" + rightTopic
                                        + ") was not successfully added twice.");
                            }
                        }
                        break;
                    case AFTER:
                        if (afterMap.get(leftTopic).contains(rightTopic)) {
                            ignorePenalty = true;
                        } else {
                            boolean added = afterMap.get(leftTopic).add(rightTopic);
                            if (!added) {
                                throw new IllegalStateException("The periodPenaltyType (" + periodPenaltyType
                                        + ") for leftTopic (" + leftTopic + ") and rightTopic (" + rightTopic
                                        + ") was not successfully added.");
                            }
                        }
                        break;
                    default:
                        throw new IllegalStateException("The periodPenaltyType ("
                                + periodPenalty.getPeriodPenaltyType() + ") is not implemented.");
                }
                if (!ignorePenalty) {
                    periodPenaltyList.add(periodPenalty);
                }
                line = bufferedReader.readLine();
            }
            // createIndirectPeriodPenalties of type EXAM_COINCIDENCE
            for (Map.Entry<Topic, Set<Topic>> entry : coincidenceMap.entrySet()) {
                Topic leftTopic = entry.getKey();
                Set<Topic> middleTopicSet = entry.getValue();
                for (Topic middleTopic : new ArrayList<>(middleTopicSet)) {
                    for (Topic rightTopic : new ArrayList<>(coincidenceMap.get(middleTopic))) {
                        if (rightTopic != leftTopic
                                && !middleTopicSet.contains(rightTopic)) {
                            PeriodPenalty indirectPeriodPenalty = new PeriodPenalty();
                            indirectPeriodPenalty.setId((long) id);
                            id++;
                            indirectPeriodPenalty.setPeriodPenaltyType(PeriodPenaltyType.EXAM_COINCIDENCE);
                            indirectPeriodPenalty.setLeftTopic(leftTopic);
                            indirectPeriodPenalty.setRightTopic(rightTopic);
                            periodPenaltyList.add(indirectPeriodPenalty);
                            boolean added = coincidenceMap.get(leftTopic).add(rightTopic)
                                    && coincidenceMap.get(rightTopic).add(leftTopic);
                            if (!added) {
                                throw new IllegalStateException("The periodPenalty (" + indirectPeriodPenalty
                                        + ") for leftTopic (" + leftTopic + ") and rightTopic (" + rightTopic
                                        + ") was not successfully added twice.");
                            }
                        }
                    }
                }
            }
            // createIndirectPeriodPenalties of type AFTER
            for (Map.Entry<Topic, Set<Topic>> entry : afterMap.entrySet()) {
                Topic leftTopic = entry.getKey();
                Set<Topic> afterLeftSet = entry.getValue();
                Queue<Topic> queue = new ArrayDeque<>();
                for (Topic topic : afterMap.get(leftTopic)) {
                    queue.add(topic);
                    queue.addAll(coincidenceMap.get(topic));
                }
                while (!queue.isEmpty()) {
                    Topic rightTopic = queue.poll();
                    if (!afterLeftSet.contains(rightTopic)) {
                        PeriodPenalty indirectPeriodPenalty = new PeriodPenalty();
                        indirectPeriodPenalty.setId((long) id);
                        id++;
                        indirectPeriodPenalty.setPeriodPenaltyType(PeriodPenaltyType.AFTER);
                        indirectPeriodPenalty.setLeftTopic(leftTopic);
                        indirectPeriodPenalty.setRightTopic(rightTopic);
                        periodPenaltyList.add(indirectPeriodPenalty);
                        boolean added = afterMap.get(leftTopic).add(rightTopic);
                        if (!added) {
                            throw new IllegalStateException("The periodPenalty (" + indirectPeriodPenalty
                                    + ") for leftTopic (" + leftTopic + ") and rightTopic (" + rightTopic
                                    + ") was not successfully added.");
                        }
                    }
                    for (Topic topic : afterMap.get(rightTopic)) {
                        queue.add(topic);
                        queue.addAll(coincidenceMap.get(topic));
                    }
                }
            }
            examination.setPeriodPenaltyList(periodPenaltyList);
        }

        private void readRoomPenaltyList() throws IOException {
            List<Topic> topicList = examination.getTopicList();
            List<RoomPenalty> roomPenaltyList = new ArrayList<>();
            String line = bufferedReader.readLine();
            int id = 0;
            while (!line.equals("[InstitutionalWeightings]")) {
                String[] lineTokens = line.split(SPLIT_REGEX);
                if (lineTokens.length != 2) {
                    throw new IllegalArgumentException("Read line (" + line + ") is expected to contain 2 tokens.");
                }
                RoomPenalty roomPenalty = new RoomPenalty();
                roomPenalty.setId((long) id);
                roomPenalty.setTopic(topicList.get(Integer.parseInt(lineTokens[0])));
                roomPenalty.setRoomPenaltyType(RoomPenaltyType.valueOf(lineTokens[1]));
                roomPenaltyList.add(roomPenalty);
                line = bufferedReader.readLine();
                id++;
            }
            examination.setRoomPenaltyList(roomPenaltyList);
        }

        private int readHeaderWithNumber(String header) throws IOException {
            String line = bufferedReader.readLine();
            if (!line.startsWith("[" + header + ":") || !line.endsWith("]")) {
                throw new IllegalStateException("Read line (" + line + " is not the expected header (["
                        + header + ":number])");
            }
            return Integer.parseInt(line.substring(header.length() + 2, line.length() - 1));
        }

        private void readInstitutionalWeighting() throws IOException {
            ExaminationConstraintConfiguration constraintConfiguration = new ExaminationConstraintConfiguration();
            constraintConfiguration.setId(0L);
            String[] lineTokens;
            lineTokens = readInstitutionalWeightingProperty("TWOINAROW", 2);
            constraintConfiguration.setTwoInARowPenalty(Integer.parseInt(lineTokens[1]));
            lineTokens = readInstitutionalWeightingProperty("TWOINADAY", 2);
            constraintConfiguration.setTwoInADayPenalty(Integer.parseInt(lineTokens[1]));
            lineTokens = readInstitutionalWeightingProperty("PERIODSPREAD", 2);
            constraintConfiguration.setPeriodSpreadLength(Integer.parseInt(lineTokens[1]));
            constraintConfiguration.setPeriodSpreadPenalty(1); // constant
            lineTokens = readInstitutionalWeightingProperty("NONMIXEDDURATIONS", 2);
            constraintConfiguration.setMixedDurationPenalty(Integer.parseInt(lineTokens[1]));
            lineTokens = readInstitutionalWeightingProperty("FRONTLOAD", 4);
            constraintConfiguration.setFrontLoadLargeTopicSize(Integer.parseInt(lineTokens[1]));
            constraintConfiguration.setFrontLoadLastPeriodSize(Integer.parseInt(lineTokens[2]));
            constraintConfiguration.setFrontLoadPenalty(Integer.parseInt(lineTokens[3]));
            examination.setConstraintConfiguration(constraintConfiguration);
        }

        private String[] readInstitutionalWeightingProperty(String property,
                int propertySize) throws IOException {
            String[] lineTokens;
            lineTokens = bufferedReader.readLine().split(SPLIT_REGEX);
            if (!lineTokens[0].equals(property) || lineTokens.length != propertySize) {
                throw new IllegalArgumentException("Read line (" + Arrays.toString(lineTokens)
                        + ") is expected to contain " + propertySize + " tokens and start with " + property + ".");
            }
            return lineTokens;
        }

        private void tagFrontLoadLargeTopics() {
            List<Topic> sortedTopicList = new ArrayList<>(examination.getTopicList());
            Collections.sort(sortedTopicList, COMPARATOR);
            int frontLoadLargeTopicSize = examination.getConstraintConfiguration().getFrontLoadLargeTopicSize();
            if (frontLoadLargeTopicSize == 0) {
                return;
            }
            int minimumTopicId = sortedTopicList.size() - frontLoadLargeTopicSize;
            if (minimumTopicId < 0) {
                logger.warn("The frontLoadLargeTopicSize (" + frontLoadLargeTopicSize
                        + ") is bigger than topicListSize (" + sortedTopicList.size()
                        + "). Tagging all topic as frontLoadLarge...");
                minimumTopicId = 0;
            }
            for (Topic topic : sortedTopicList.subList(minimumTopicId, sortedTopicList.size())) {
                topic.setFrontLoadLarge(true);
            }
        }

        private void tagFrontLoadLastPeriods() {
            List<Period> periodList = examination.getPeriodList();
            int frontLoadLastPeriodSize = examination.getConstraintConfiguration().getFrontLoadLastPeriodSize();
            if (frontLoadLastPeriodSize == 0) {
                return;
            }
            int minimumPeriodId = periodList.size() - frontLoadLastPeriodSize;
            if (minimumPeriodId < 0) {
                logger.warn("The frontLoadLastPeriodSize (" + frontLoadLastPeriodSize
                        + ") is bigger than periodListSize (" + periodList.size()
                        + "). Tagging all periods as frontLoadLast...");
                minimumPeriodId = 0;
            }
            for (Period period : periodList.subList(minimumPeriodId, periodList.size())) {
                period.setFrontLoadLast(true);
            }
        }

        private void createExamList() {
            List<Topic> topicList = examination.getTopicList();
            List<Exam> examList = new ArrayList<>(topicList.size());
            Map<Topic, LeadingExam> leadingTopicToExamMap = new HashMap<>(topicList.size());
            for (Topic topic : topicList) {
                Exam exam;
                Topic leadingTopic = topic;
                for (Topic coincidenceTopic : coincidenceMap.get(topic)) {
                    if (coincidenceTopic.getId() < leadingTopic.getId()) {
                        leadingTopic = coincidenceTopic;
                    }
                }
                if (leadingTopic == topic) {
                    LeadingExam leadingExam = new LeadingExam();
                    leadingExam.setFollowingExamList(new ArrayList<>(10));
                    leadingTopicToExamMap.put(topic, leadingExam);
                    exam = leadingExam;
                } else {
                    FollowingExam followingExam = new FollowingExam();
                    LeadingExam leadingExam = leadingTopicToExamMap.get(leadingTopic);
                    if (leadingExam == null) {
                        throw new IllegalStateException("The followingExam (" + topic.getId()
                                + ")'s leadingExam (" + leadingExam + ") cannot be null.");
                    }
                    followingExam.setLeadingExam(leadingExam);
                    leadingExam.getFollowingExamList().add(followingExam);
                    exam = followingExam;
                }
                exam.setId(topic.getId());
                exam.setTopic(topic);
                // Notice that we leave the PlanningVariable properties on null
                examList.add(exam);
            }
            examination.setExamList(examList);
        }
    }
}
