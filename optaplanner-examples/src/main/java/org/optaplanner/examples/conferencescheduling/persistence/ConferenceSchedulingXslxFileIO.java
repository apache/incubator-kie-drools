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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.examples.conferencescheduling.domain.Room;
import org.optaplanner.examples.conferencescheduling.domain.Speaker;
import org.optaplanner.examples.conferencescheduling.domain.Talk;
import org.optaplanner.examples.conferencescheduling.domain.Timeslot;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class ConferenceSchedulingXslxFileIO implements SolutionFileIO<ConferenceSolution> {

    public static final DateTimeFormatter DAY_FORMATTER
            = DateTimeFormatter.ofPattern("E yyyy-MM-dd", Locale.ENGLISH);

    public static final DateTimeFormatter TIME_FORMATTER
            = DateTimeFormatter.ofPattern("HH:mm", Locale.ENGLISH);

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

        protected Sheet currentSheet;
        protected Row currentRow;
        protected int currentRowNumber;
        protected int currentColumnNumber;

        public ConferenceSchedulingXslxReader(Workbook workbook) {
            this.workbook = workbook;
        }

        public ConferenceSolution read() {
            solution = new ConferenceSolution();
            readTimeslotList();
            readRoomList();
            readSpeakerList();
            readTalkList();
            return solution;
        }

        private void readTimeslotList() {
            nextSheet("Timeslots");
            nextRow();
            readHeaderCell("Day");
            readHeaderCell("Start");
            readHeaderCell("End");
            readHeaderCell("Tags");
            List<Timeslot> timeslotList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            long id = 0L;
            while (hasNextRow()) {
                nextRow();
                Timeslot timeslot = new Timeslot();
                timeslot.setId(id++);
                LocalDate day = LocalDate.parse(nextCell().getStringCellValue(), DAY_FORMATTER);
                LocalTime startTime = LocalTime.parse(nextCell().getStringCellValue(), TIME_FORMATTER);
                LocalTime endTime = LocalTime.parse(nextCell().getStringCellValue(), TIME_FORMATTER);
                if (startTime.compareTo(endTime) >= 0) {
                    throw new IllegalStateException(currentPosition() + ": The startTime (" + startTime
                            + ") must be less than the endTime (" + endTime + ")");
                }
                timeslot.setStartDateTime(LocalDateTime.of(day, startTime));
                timeslot.setEndDateTime(LocalDateTime.of(day, endTime));
                timeslot.setTimeslotTagSet(new LinkedHashSet<>(Arrays.asList(nextCell().getStringCellValue().split(", "))));
                timeslotList.add(timeslot);
            }
            solution.setTimeslotList(timeslotList);
        }

        private void readRoomList() {
            nextSheet("Rooms");
            nextRow();
            readHeaderCell("");
            readHeaderCell("");
            readTimeslotDaysHeaders();
            nextRow();
            readHeaderCell("Name");
            readHeaderCell("Tags");
            readTimeslotHoursHeaders();
            List<Room> roomList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            long id = 0L;
            while (hasNextRow()) {
                nextRow();
                Room room = new Room();
                room.setId(id++);
                room.setName(nextCell().getStringCellValue());
                room.setRoomTagSet(new LinkedHashSet<>(Arrays.asList(nextCell().getStringCellValue().split(", "))));
                roomList.add(room);
            }
            solution.setRoomList(roomList);
        }

        private void readSpeakerList() {
            nextSheet("Speakers");
            nextRow();
            readHeaderCell("");
            readTimeslotDaysHeaders();
            nextRow();
            readHeaderCell("Name");
            readTimeslotHoursHeaders();
            List<Speaker> roomList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            long id = 0L;
            while (hasNextRow()) {
                nextRow();
                Speaker speaker = new Speaker();
                speaker.setId(id++);
                speaker.setName(nextCell().getStringCellValue());
                roomList.add(speaker);
            }
            solution.setSpeakerList(roomList);
        }

        private void readTalkList() {
            Map<String, Speaker> speakerMap = solution.getSpeakerList().stream().collect(
                    Collectors.toMap(Speaker::getName, speaker -> speaker));
            nextSheet("Talks");
            nextRow();
            readHeaderCell("Code");
            readHeaderCell("Title");
            List<Talk> talkList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            long id = 0L;
            while (hasNextRow()) {
                nextRow();
                Talk talk = new Talk();
                talk.setId(id++);
                talk.setCode(nextCell().getStringCellValue());
                talk.setTitle(nextCell().getStringCellValue());
                talk.setSpeakerList(Arrays.stream(nextCell().getStringCellValue().split(", ")).map(speakerName -> {
                    Speaker speaker = speakerMap.get(speakerName);
                    if (speaker == null) {
                        throw new IllegalStateException(currentPosition() + ": The talk with code (" + talk.getCode()
                                + ") has a speaker (" + speakerName + ") that doesn't exist in the speaker list.");
                    }
                    return speaker;
                }).collect(Collectors.toList()));
                talkList.add(talk);
            }
            solution.setTalkList(talkList);
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
            currentRowNumber = -1;
        }

        protected void nextRow() {
            currentRowNumber++;
            currentRow = currentSheet.getRow(currentRowNumber);
            currentColumnNumber = -1;
        }

        protected boolean hasNextRow() {
            return currentRowNumber < currentSheet.getLastRowNum();
        }

        protected void readHeaderCell(String value) {
            Cell cell = nextCell();
            if (cell == null || !cell.getStringCellValue().equals(value)) {
                throw new IllegalStateException(currentPosition() + ": The cell does not contain the expected value ("
                        + value + ").");
            }
        }

        protected Cell nextCell() {
            currentColumnNumber++;
            return currentRow.getCell(currentColumnNumber);
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

        protected Workbook workbook;

        protected CellStyle headerStyle;

        protected Sheet currentSheet;
        protected Row currentRow;
        protected int currentRowNumber;
        protected int currentColumnNumber;
        protected int headerCellCount;

        public ConferenceSchedulingXlsxWriter(ConferenceSolution solution) {
            this.solution = solution;
        }

        public Workbook write() {
            workbook = new XSSFWorkbook();
            createStyles();
            writeTimeslotList();
            writeRoomList();
            writeSpeakerList();
            writeTalkList();
            return workbook;
        }

        public void createStyles() {
            headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
        }

        private void writeTimeslotList() {
            nextSheet("Timeslots", 3, 1);
            nextRow();
            addHeaderCell("Day");
            addHeaderCell("Start");
            addHeaderCell("End");
            addHeaderCell("Tags");
            for (Timeslot timeslot : solution.getTimeslotList()) {
                nextRow();
                nextCell().setCellValue(DAY_FORMATTER.format(timeslot.getDate()));
                nextCell().setCellValue(TIME_FORMATTER.format(timeslot.getStartDateTime()));
                nextCell().setCellValue(TIME_FORMATTER.format(timeslot.getEndDateTime()));
                nextCell().setCellValue(String.join(", ", timeslot.getTimeslotTagSet()));
            }
            autoSizeColumnsWithHeader();
        }

        private void writeRoomList() {
            nextSheet("Rooms", 1, 2);
            nextRow();
            addHeaderCell("");
            addHeaderCell("");
            writeTimeslotDaysHeaders();
            nextRow();
            addHeaderCell("Name");
            addHeaderCell("Tags");
            writeTimeslotHoursHeaders();
            for (Room room : solution.getRoomList()) {
                nextRow();
                nextCell().setCellValue(room.getName());
                nextCell().setCellValue(String.join(", ", room.getRoomTagSet()));
            }
            autoSizeColumnsWithHeader();
        }

        private void writeSpeakerList() {
            nextSheet("Speakers", 1, 2);
            nextRow();
            addHeaderCell("");
            writeTimeslotDaysHeaders();
            nextRow();
            addHeaderCell("Name");
            writeTimeslotHoursHeaders();
            for (Speaker speaker : solution.getSpeakerList()) {
                nextRow();
                nextCell().setCellValue(speaker.getName());
            }
            autoSizeColumnsWithHeader();
        }

        private void writeTalkList() {
            nextSheet("Talks", 2, 1);
            nextRow();
            addHeaderCell("Code");
            addHeaderCell("Title");
            addHeaderCell("Speakers");
            for (Talk talk : solution.getTalkList()) {
                nextRow();
                nextCell().setCellValue(talk.getCode());
                nextCell().setCellValue(talk.getTitle());
                nextCell().setCellValue(talk.getSpeakerList()
                        .stream().map(Speaker::getName).collect(Collectors.joining(", ")));
            }
            autoSizeColumnsWithHeader();
        }

        private void writeTimeslotDaysHeaders() {
            LocalDate previousTimeslotDay = null;
            for (Timeslot timeslot : solution.getTimeslotList()) {
                LocalDate timeslotDay = timeslot.getDate();
                if (timeslotDay.equals(previousTimeslotDay)) {
                    addHeaderCell("");
                } else {
                    addHeaderCell(DAY_FORMATTER.format(timeslotDay));
                    previousTimeslotDay = timeslotDay;
                }
            }
        }

        private void writeTimeslotHoursHeaders() {
            for (Timeslot timeslot : solution.getTimeslotList()) {
                addHeaderCell(TIME_FORMATTER.format(timeslot.getStartDateTime())
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

        protected void addHeaderCell(String value) {
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
