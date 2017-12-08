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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.optaplanner.examples.conferencescheduling.domain.ConferenceSolution;
import org.optaplanner.examples.conferencescheduling.domain.Room;
import org.optaplanner.examples.conferencescheduling.domain.Speaker;
import org.optaplanner.examples.conferencescheduling.domain.Talk;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class ConferenceSchedulingXslxFileIO implements SolutionFileIO<ConferenceSolution> {

    @Override
    public String getInputFileExtension() {
        return "xlsx";
    }

    @Override
    public ConferenceSolution read(File inputSolutionFile) {
        try (InputStream in = new BufferedInputStream(new FileInputStream(inputSolutionFile))) {
            Workbook workbook = new XSSFWorkbook(in);
            return new ConferfenceSchedulingXslxReader(workbook).read();
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Failed reading inputSolutionFile ("
                    + inputSolutionFile + ").", e);
        }
    }

    private static class ConferfenceSchedulingXslxReader {

        protected final Workbook workbook;

        protected ConferenceSolution solution;

        protected Sheet currentSheet;
        protected Row currentRow;
        protected int currentRowNumber;
        protected int currentColumnNumber;

        public ConferfenceSchedulingXslxReader(Workbook workbook) {
            this.workbook = workbook;
        }

        public ConferenceSolution read() {
            solution = new ConferenceSolution();
            readRoomList();
            readSpeakerList();
            readTalkList();
            return solution;
        }

        private void readRoomList() {
            nextSheet("Rooms");
            nextRow();
            readHeaderCell("Name");
            readHeaderCell("Tags");
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
            readHeaderCell("Name");
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
            nextSheet("Talks");
            nextRow();
            readHeaderCell("Title");
            List<Talk> talkList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            long id = 0L;
            while (hasNextRow()) {
                nextRow();
                Talk talk = new Talk();
                talk.setId(id++);
                talk.setTitle(nextCell().getStringCellValue());
                talkList.add(talk);
            }
            solution.setTalkList(talkList);
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
            if (cell == null) {
                throw new IllegalStateException("The sheet (" + currentSheet.getSheetName()
                        + ") does not contain the value (" + value
                        + ") at cell (" + currentRowNumber + "," + currentColumnNumber + ").");
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

        private void writeRoomList() {
            nextSheet("Rooms", 1, 1);
            nextRow();
            addHeaderCell("Name");
            addHeaderCell("Tags");
            for (Room room : solution.getRoomList()) {
                nextRow();
                nextCell().setCellValue(room.getName());
                nextCell().setCellValue(String.join(", ", room.getRoomTagSet()));
            }
            autoSizeColumnsWithHeader();
        }

        private void writeSpeakerList() {
            nextSheet("Speakers", 1, 1);
            nextRow();
            addHeaderCell("Name");
//            addHeaderCell("Tags");
            for (Speaker speaker : solution.getSpeakerList()) {
                nextRow();
                nextCell().setCellValue(speaker.getName());
//                nextCell().setCellValue(String.join(", ", speaker.getRoomTagSet()));
            }
            autoSizeColumnsWithHeader();
        }

        private void writeTalkList() {
            nextSheet("Talks", 1, 1);
            nextRow();
            addHeaderCell("Title");
            for (Talk talk : solution.getTalkList()) {
                nextRow();
                nextCell().setCellValue(talk.getTitle());
            }
            autoSizeColumnsWithHeader();
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
