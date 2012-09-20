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

package org.drools.planner.examples.examination.persistence;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.drools.planner.examples.examination.domain.Exam;
import org.drools.planner.examples.examination.domain.Examination;
import org.drools.planner.examples.examination.domain.InstitutionParametrization;
import org.drools.planner.examples.examination.domain.Period;
import org.drools.planner.examples.examination.domain.PeriodPenalty;
import org.drools.planner.examples.examination.domain.PeriodPenaltyType;
import org.drools.planner.examples.examination.domain.Room;
import org.drools.planner.examples.examination.domain.RoomPenalty;
import org.drools.planner.examples.examination.domain.RoomPenaltyType;
import org.drools.planner.examples.examination.domain.Student;
import org.drools.planner.examples.examination.domain.Topic;
import org.drools.planner.examples.examination.domain.solver.ExamBefore;
import org.drools.planner.examples.examination.domain.solver.ExamCoincidence;

public class ExaminationSolutionImporter extends AbstractTxtSolutionImporter {

    private static final String INPUT_FILE_SUFFIX = ".exam";
    private static final String SPLIT_REGEX = "\\,\\ ?";

    public static void main(String[] args) {
        new ExaminationSolutionImporter().convertAll();
    }

    public ExaminationSolutionImporter() {
        super(new ExaminationDaoImpl());
    }

    @Override
    public String getInputFileSuffix() {
        return INPUT_FILE_SUFFIX;
    }

    public TxtInputBuilder createTxtInputBuilder() {
        return new ExaminationInputBuilder();
    }

    public class ExaminationInputBuilder extends TxtInputBuilder {

        public Solution readSolution() throws IOException {
            Examination examination = new Examination();
            examination.setId(0L);

            readTopicListAndStudentList(examination);
            readPeriodList(examination);
            readRoomList(examination);

            String line = bufferedReader.readLine();
            if (!line.equals("[PeriodHardConstraints]")) {
                throw new IllegalStateException("Read line (" + line
                        + " is not the expected header ([PeriodHardConstraints])");
            }
            readPeriodPenaltyList(examination);
            readRoomPenaltyList(examination);
            readInstitutionalWeighting(examination);
            tagFrontLoadLargeTopics(examination);
            tagFrontLoadLastPeriods(examination);

            createExamList(examination);

            logger.info("Examination with {} students, {} topics/exams, {} periods, {} rooms, {} period constraints" +
                    " and {} room constraints.",
                    new Object[]{examination.getStudentList().size(), examination.getTopicList().size(),
                            examination.getPeriodList().size(), examination.getRoomList().size(),
                            examination.getPeriodPenaltyList().size(),
                            examination.getRoomPenaltyList().size()});
            int possibleForOneExamSize = examination.getPeriodList().size() * examination.getRoomList().size();
            BigInteger possibleSolutionSize = BigInteger.valueOf(possibleForOneExamSize).pow(
                    examination.getTopicList().size());
            String flooredPossibleSolutionSize = "10^" + (possibleSolutionSize.toString().length() - 1);
            logger.info("Examination with flooredPossibleSolutionSize ({}) and possibleSolutionSize ({}).",
                    flooredPossibleSolutionSize, possibleSolutionSize);
            return examination;
        }

        private void readTopicListAndStudentList(Examination examination) throws IOException {
            Map<Integer, Student> studentMap = new HashMap<Integer, Student>();
            int examSize = readHeaderWithNumber("Exams");
            List<Topic> topicList = new ArrayList<Topic>(examSize);
            for (int i = 0; i < examSize; i++) {
                Topic topic = new Topic();
                topic.setId((long) i);
                String line = bufferedReader.readLine();
                String[] lineTokens = line.split(SPLIT_REGEX);
                topic.setDuration(Integer.parseInt(lineTokens[0]));
                List<Student> topicStudentList = new ArrayList<Student>(lineTokens.length - 1);
                for (int j = 1; j < lineTokens.length; j++) {
                    topicStudentList.add(findOrCreateStudent(studentMap, Integer.parseInt(lineTokens[j])));
                }
                topic.setStudentList(topicStudentList);
                topic.setFrontLoadLarge(false);
                topicList.add(topic);
            }
            examination.setTopicList(topicList);
            List<Student> studentList = new ArrayList<Student>(studentMap.values());
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

        private void readPeriodList(Examination examination) throws IOException {
            int periodSize = readHeaderWithNumber("Periods");
            List<Period> periodList = new ArrayList<Period>(periodSize);
            // Everything is in the default timezone and the default locale.
            Calendar calendar = Calendar.getInstance();
            final DateFormat DATE_FORMAT = new SimpleDateFormat("dd:MM:yyyy HH:mm:ss");
            int referenceDayOfYear = -1;
            int referenceYear = -1;
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
                int dayOfYear;
                int year;
                try {
                    calendar.setTime(DATE_FORMAT.parse(startDateTimeString));
                    calendar.get(Calendar.DAY_OF_YEAR);
                    dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
                    year = calendar.get(Calendar.YEAR);
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Illegal startDateTimeString (" + startDateTimeString + ").", e);
                }
                if (referenceDayOfYear < 0) {
                    referenceDayOfYear = dayOfYear;
                    referenceYear = year;
                }
                if (year != referenceYear) {
                    // Because the Calendar API in JSE sucks... (java 7 will fix that FINALLY)
                    throw new IllegalStateException("Not yet implemented to handle periods spread over different years...");
                }
                int dayIndex = dayOfYear - referenceDayOfYear;
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

        private void readRoomList(Examination examination) throws IOException {
            int roomSize = readHeaderWithNumber("Rooms");
            List<Room> roomList = new ArrayList<Room>(roomSize);
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

        private void readPeriodPenaltyList(Examination examination)
                throws IOException {
            List<Topic> topicList = examination.getTopicList();
            List<PeriodPenalty> periodPenaltyList = new ArrayList<PeriodPenalty>();
            String line = bufferedReader.readLine();
            int id = 0;
            while (!line.equals("[RoomHardConstraints]")) {
                String[] lineTokens = line.split(SPLIT_REGEX);
                PeriodPenalty periodPenalty = new PeriodPenalty();
                periodPenalty.setId((long) id);
                if (lineTokens.length != 3) {
                    throw new IllegalArgumentException("Read line (" + line + ") is expected to contain 3 tokens.");
                }
                Topic leftTopic = topicList.get(Integer.parseInt(lineTokens[0]));
                periodPenalty.setLeftSideTopic(leftTopic);
                PeriodPenaltyType periodPenaltyType = PeriodPenaltyType.valueOf(lineTokens[1]);
                periodPenalty.setPeriodPenaltyType(periodPenaltyType);
                Topic rightTopic = topicList.get(Integer.parseInt(lineTokens[2]));
                periodPenalty.setRightSideTopic(rightTopic);
                if (periodPenaltyType == PeriodPenaltyType.EXAM_COINCIDENCE) {
                    // It's not specified what happens
                    // when A coincidences with B and B coincidences with C
                    // and when A and C share students (but don't directly coincidence)
                    if (!Collections.disjoint(leftTopic.getStudentList(), rightTopic.getStudentList())) {
                        logger.warn("Filtering out periodPenalty (" + periodPenalty
                                + ") because the left and right topic share students.");
                    } else {
                        periodPenaltyList.add(periodPenalty);
                    }
                } else {
                    periodPenaltyList.add(periodPenalty);
                }
                line = bufferedReader.readLine();
                id++;
            }
            examination.setPeriodPenaltyList(periodPenaltyList);
        }

        private void readRoomPenaltyList(Examination examination)
                throws IOException {
            List<Topic> topicList = examination.getTopicList();
            List<RoomPenalty> roomPenaltyList = new ArrayList<RoomPenalty>();
            String line = bufferedReader.readLine();
            int id = 0;
            while (!line.equals("[InstitutionalWeightings]")) {
                String[] lineTokens = line.split(SPLIT_REGEX);
                RoomPenalty roomPenalty = new RoomPenalty();
                roomPenalty.setId((long) id);
                if (lineTokens.length != 2) {
                    throw new IllegalArgumentException("Read line (" + line + ") is expected to contain 3 tokens.");
                }
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

        private void readInstitutionalWeighting(Examination examination) throws IOException {
            InstitutionParametrization institutionParametrization = new InstitutionParametrization();
            institutionParametrization.setId(0L);
            String[] lineTokens;
            lineTokens = readInstitutionalWeightingProperty("TWOINAROW", 2);
            institutionParametrization.setTwoInARowPenalty(Integer.parseInt(lineTokens[1]));
            lineTokens = readInstitutionalWeightingProperty("TWOINADAY", 2);
            institutionParametrization.setTwoInADayPenalty(Integer.parseInt(lineTokens[1]));
            lineTokens = readInstitutionalWeightingProperty("PERIODSPREAD", 2);
            institutionParametrization.setPeriodSpreadLength(Integer.parseInt(lineTokens[1]));
            institutionParametrization.setPeriodSpreadPenalty(1); // constant
            lineTokens = readInstitutionalWeightingProperty("NONMIXEDDURATIONS", 2);
            institutionParametrization.setMixedDurationPenalty(Integer.parseInt(lineTokens[1]));
            lineTokens = readInstitutionalWeightingProperty("FRONTLOAD", 4);
            institutionParametrization.setFrontLoadLargeTopicSize(Integer.parseInt(lineTokens[1]));
            institutionParametrization.setFrontLoadLastPeriodSize(Integer.parseInt(lineTokens[2]));
            institutionParametrization.setFrontLoadPenalty(Integer.parseInt(lineTokens[3]));
            examination.setInstitutionParametrization(institutionParametrization);
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

        private void tagFrontLoadLargeTopics(Examination examination) {
            List<Topic> sortedTopicList = new ArrayList<Topic>(examination.getTopicList());
            Collections.sort(sortedTopicList, new Comparator<Topic>() {
                public int compare(Topic a, Topic b) {
                    return new CompareToBuilder()
                            .append(a.getStudentSize(), b.getStudentSize()) // Ascending
                            .append(b.getId(), a.getId()) // Descending (according to spec)
                            .toComparison();
                }
            });
            int frontLoadLargeTopicSize = examination.getInstitutionParametrization().getFrontLoadLargeTopicSize();
            if (frontLoadLargeTopicSize == 0) {
                return;
            }
            int minimumTopicId = sortedTopicList.size() - frontLoadLargeTopicSize;
            if (minimumTopicId < 0) {
                logger.warn("The frontLoadLargeTopicSize (" + frontLoadLargeTopicSize + ") is bigger than topicListSize ("
                        + sortedTopicList.size() + "). Tagging all topic as frontLoadLarge...");
                minimumTopicId = 0;
            }
            for (Topic topic : sortedTopicList.subList(minimumTopicId, sortedTopicList.size())) {
                topic.setFrontLoadLarge(true);
            }
        }

        private void tagFrontLoadLastPeriods(Examination examination) {
            List<Period> periodList = examination.getPeriodList();
            int frontLoadLastPeriodSize = examination.getInstitutionParametrization().getFrontLoadLastPeriodSize();
            if (frontLoadLastPeriodSize == 0) {
                return;
            }
            int minimumPeriodId = periodList.size() - frontLoadLastPeriodSize;
            if (minimumPeriodId < 0) {
                logger.warn("The frontLoadLastPeriodSize (" + frontLoadLastPeriodSize + ") is bigger than periodListSize ("
                        + periodList.size() + "). Tagging all periods as frontLoadLast...");
                minimumPeriodId = 0;
            }
            for (Period period : periodList.subList(minimumPeriodId, periodList.size())) {
                period.setFrontLoadLast(true);
            }
        }

        private void createExamList(Examination examination) {
            List<Topic> topicList = examination.getTopicList();
            List<Exam> examList = new ArrayList<Exam>(topicList.size());
            Map<Topic, Exam> topicToExamMap = new HashMap<Topic, Exam>(topicList.size());
            for (Topic topic : topicList) {
                Exam exam = new Exam();
                exam.setId(topic.getId());
                exam.setTopic(topic);
                // Notice that we leave the PlanningVariable properties on null
                examList.add(exam);
                topicToExamMap.put(topic, exam);
            }
            for (PeriodPenalty periodPenalty : examination.getPeriodPenaltyList()) {
                if (periodPenalty.getPeriodPenaltyType() == PeriodPenaltyType.EXAM_COINCIDENCE) {
                    Exam leftExam = topicToExamMap.get(periodPenalty.getLeftSideTopic());
                    Exam rightExam = topicToExamMap.get(periodPenalty.getRightSideTopic());

                    Set<Exam> newCoincidenceExamSet = new LinkedHashSet<Exam>(4);
                    ExamCoincidence leftExamCoincidence = leftExam.getExamCoincidence();
                    if (leftExamCoincidence != null) {
                        newCoincidenceExamSet.addAll(leftExamCoincidence.getCoincidenceExamSet());
                    } else {
                        newCoincidenceExamSet.add(leftExam);
                    }
                    ExamCoincidence rightExamCoincidence = rightExam.getExamCoincidence();
                    if (rightExamCoincidence != null) {
                        newCoincidenceExamSet.addAll(rightExamCoincidence.getCoincidenceExamSet());
                    } else {
                        newCoincidenceExamSet.add(rightExam);
                    }
                    ExamCoincidence newExamCoincidence = new ExamCoincidence(newCoincidenceExamSet);
                    for (Exam exam : newCoincidenceExamSet) {
                        exam.setExamCoincidence(newExamCoincidence);
                    }
                } else if (periodPenalty.getPeriodPenaltyType() == PeriodPenaltyType.AFTER) {
                    Exam afterExam = topicToExamMap.get(periodPenalty.getLeftSideTopic());
                    Exam beforeExam = topicToExamMap.get(periodPenalty.getRightSideTopic());
                    ExamBefore examBefore = beforeExam.getExamBefore();
                    if (examBefore == null) {
                        examBefore = new ExamBefore(new LinkedHashSet<Exam>(2));
                        beforeExam.setExamBefore(examBefore);
                    }
                    examBefore.getAfterExamSet().add(afterExam);
                }
            }
            examination.setExamList(examList);
        }

    }

}
