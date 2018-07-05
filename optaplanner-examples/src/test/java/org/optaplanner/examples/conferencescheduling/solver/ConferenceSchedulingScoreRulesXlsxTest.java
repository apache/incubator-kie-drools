/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.conferencescheduling.solver;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Before;
import org.junit.Test;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.impl.domain.solution.cloner.FieldAccessingSolutionCloner;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.examples.common.persistence.AbstractXlsxSolutionFileIO;
import org.optaplanner.examples.conferencescheduling.app.ConferenceSchedulingApp;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.examples.conferencescheduling.domain.Room;
import org.optaplanner.examples.conferencescheduling.domain.Talk;
import org.optaplanner.examples.conferencescheduling.domain.Timeslot;
import org.optaplanner.examples.conferencescheduling.persistence.ConferenceSchedulingXlsxFileIO;
import org.optaplanner.test.impl.score.buildin.hardsoft.HardSoftScoreVerifier;

public class ConferenceSchedulingScoreRulesXlsxTest {

    private ConferenceSolution currentSolution;
    private String currentPackage;
    private String currentConstraint;
    private String currentSheetName;
    private int expectedHardScore, expectedSoftScore;

    private final String testFileName = "testConferenceSchedulingScoreRules.xlsx";
    private HardSoftScoreVerifier<ConferenceSolution> scoreVerifier = new HardSoftScoreVerifier<>(
            SolverFactory.createFromXmlResource(ConferenceSchedulingApp.SOLVER_CONFIG));
    private testConferenceSchedulingScoreRulesReader testFileReader;

    @Before
    public void setup() {
        File testFile = new File(getClass().getResource(testFileName).getFile());
        try (InputStream in = new BufferedInputStream(new FileInputStream(testFile))) {
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            this.currentSolution = new ConferenceSchedulingXlsxFileIO().read(testFile);
            this.testFileReader = new testConferenceSchedulingScoreRulesReader(workbook);
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Failed reading inputSolutionFile ("
                                                    + testFileName + ").", e);
        }
    }

    @Test
    public void testRules() {
        while ((currentSolution = testFileReader.nextSolution()) != null) {
            //TODO: give a clue which test sheet is failing
            System.out.println(currentSheetName);
            scoreVerifier.assertHardWeight(currentPackage, currentConstraint, expectedHardScore, currentSolution);
            scoreVerifier.assertSoftWeight(currentPackage, currentConstraint, expectedSoftScore, currentSolution);
        }
    }

    private class testConferenceSchedulingScoreRulesReader extends AbstractXlsxSolutionFileIO.AbstractXslxReader<ConferenceSolution> {

        private int numberOfSheets, currentTestSheetIndex;
        private Map<String, Room> roomMap;
        private Map<String, Talk> talkMap;
        private Map<Pair<LocalDateTime, LocalDateTime>, Timeslot> timeslotMap;
        private Map<Integer, LocalDate> columnIndexToDateMap;
        private Map<Integer, LocalTime> columnIndexToStartTimeMap;
        private Map<Integer, LocalTime> columnIndexToEndTimeMap;

        private final FieldAccessingSolutionCloner<ConferenceSolution> solutionCloner = new FieldAccessingSolutionCloner<>(new SolutionDescriptor<>(ConferenceSolution.class));
        private final Pattern VALID_SCORE_PATTERN = Pattern.compile("-?\\d+ hard/-?\\d+ soft");
        private final String ONLY_DIGITS_AND_SPACES_REGEX = "[^\\d\\s-]";

        final DateTimeFormatter DAY_FORMATTER
                = DateTimeFormatter.ofPattern("E yyyy-MM-dd", Locale.ENGLISH);
        final DateTimeFormatter TIME_FORMATTER
                = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

        public testConferenceSchedulingScoreRulesReader(XSSFWorkbook workbook) {
            super(workbook);
            this.numberOfSheets = workbook.getNumberOfSheets();
            this.currentTestSheetIndex = workbook.getSheetIndex("Talks") + 1;
            roomMap = currentSolution.getRoomList().stream().collect(
                    Collectors.toMap(Room::getName, Function.identity()));
            talkMap = currentSolution.getTalkList().stream().collect(
                    Collectors.toMap(Talk::getCode, Function.identity()));
            timeslotMap = currentSolution.getTimeslotList().stream().collect(
                    Collectors.toMap(timeslot -> Pair.of(timeslot.getStartDateTime(), timeslot.getEndDateTime()),
                                     Function.identity()));
            this.columnIndexToDateMap = new HashMap<>(timeslotMap.size());
            this.columnIndexToStartTimeMap = new HashMap<>(timeslotMap.size());
            this.columnIndexToEndTimeMap = new HashMap<>(timeslotMap.size());
        }

        @Override
        public ConferenceSolution read() {
            return currentSolution;
        }

        public ConferenceSolution nextSolution() {
            if (currentTestSheetIndex >= numberOfSheets) {
                return null;
            }
            nextSheet(workbook.getSheetName(currentTestSheetIndex++));
            currentSheetName = currentSheet.getSheetName();

            ConferenceSolution nextSheetSolution = solutionCloner.cloneSolution(currentSolution);
            talkMap = nextSheetSolution.getTalkList().stream().collect(
                    Collectors.toMap(Talk::getCode, Function.identity()));

            nextRow(false);
            readHeaderCell("Constraint package");
            currentPackage = nextStringCell().getStringCellValue();
            nextRow(false);
            readHeaderCell("Constraint name");
            currentConstraint = nextStringCell().getStringCellValue();
            nextRow(false);
            readHeaderCell("Score");
            String scoreAsString = nextStringCell().getStringCellValue();
            if (!VALID_SCORE_PATTERN.matcher(scoreAsString).matches()) {
                throw new IllegalStateException(currentPosition() + ": The score (" + scoreAsString
                                                        + ") must match to the regular expression (" + VALID_SCORE_PATTERN + ").");
            }
            String[] scoresStringArray = scoreAsString.replaceAll(ONLY_DIGITS_AND_SPACES_REGEX, "").split(" +");
            expectedHardScore = Integer.parseInt(scoresStringArray[0]);
            expectedSoftScore = Integer.parseInt(scoresStringArray[1]);

            nextRow();
            readTimeslotDays();
            nextRow(false);
            readHeaderCell("Room");
            readTiemslotHours();
            while (nextRow()) {
                String roomName = nextStringCell().getStringCellValue();
                Room room = roomMap.get(roomName);
                if (room == null) {
                    throw new IllegalStateException(currentPosition() + ": The room (" + roomName
                                                            + ") does not exist in the room list.");
                }
                for (int i = 0; i < columnIndexToStartTimeMap.size(); i++) {
                    String talkCode = nextCell().getStringCellValue();
                    if (!talkCode.isEmpty()) {
                        Talk talk = talkMap.get(talkCode);
                        if (talk == null) {
                            throw new IllegalStateException(currentPosition() + ": Talk (" + talkCode
                                                                    + ") does not exist in the talk list.");
                        }

                        LocalDateTime startDateTime = LocalDateTime.of(columnIndexToDateMap.get(currentColumnNumber), columnIndexToStartTimeMap.get(currentColumnNumber));
                        LocalDateTime endDateTime = LocalDateTime.of(columnIndexToDateMap.get(currentColumnNumber), columnIndexToEndTimeMap.get(currentColumnNumber));

                        Timeslot timeslot = timeslotMap.get(Pair.of(startDateTime, endDateTime));
                        if (timeslot == null) {
                            throw new IllegalStateException(currentPosition() + ": The timeslot with date (" + startDateTime.toLocalDate().toString()
                                                                    + "), startTime (" + startDateTime.toLocalTime().toString() + ") and endTime (" + endDateTime.toLocalTime().toString()
                                                                    + ") that doesn't exist in the other sheet (Timeslots).");
                        }
                        talk.setRoom(room);
                        talk.setTimeslot(timeslot);
                    }
                }
            }

            return nextSheetSolution;
        }

        private void readTimeslotDays() {
            columnIndexToDateMap.clear();
            String previousDateString = null;
            for (int i = 0; i < currentRow.getLastCellNum(); i++) {
                XSSFCell cell = currentRow.getCell(i);
                if (cell.getStringCellValue().isEmpty() && previousDateString == null) {
                    continue;
                } else {
                    if ((!cell.getStringCellValue().isEmpty())) {
                        previousDateString = cell.getStringCellValue();
                    }
                    try {
                        columnIndexToDateMap.put(i, LocalDate.parse(previousDateString, DAY_FORMATTER));
                    } catch (DateTimeParseException e) {
                        throw new IllegalStateException(currentPosition() + ": The date (" + cell.getStringCellValue()
                                                                + "does not parse as a date.");
                    }
                }
            }
        }

        private void readTiemslotHours() {
            columnIndexToStartTimeMap.clear();
            columnIndexToEndTimeMap.clear();
            StreamSupport.stream(currentRow.spliterator(), false)
                    .forEach(cell -> {
                        if (!cell.getStringCellValue().isEmpty() && !cell.getStringCellValue().equals("Room")) {
                            String[] startAndEndTimeStringArray = cell.getStringCellValue().split("-");
                            try {
                                columnIndexToStartTimeMap.put(cell.getColumnIndex(), LocalTime.parse(startAndEndTimeStringArray[0], TIME_FORMATTER));
                                columnIndexToEndTimeMap.put(cell.getColumnIndex(), LocalTime.parse(startAndEndTimeStringArray[1], TIME_FORMATTER));
                            } catch (DateTimeParseException e) {
                                throw new IllegalStateException(currentPosition() + ": The startTime (" + startAndEndTimeStringArray[0]
                                                                        + ") or endTime (" + startAndEndTimeStringArray[1]
                                                                        + ") doesn't parse as a  time.", e);
                            }
                        }
                    });
        }
    }
}
