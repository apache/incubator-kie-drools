/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.rocktour.persistence;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeSet;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.optaplanner.examples.common.persistence.AbstractXslxSolutionFileIO;
import org.optaplanner.examples.rocktour.app.RockTourApp;
import org.optaplanner.examples.rocktour.domain.RockBus;
import org.optaplanner.examples.rocktour.domain.RockLocation;
import org.optaplanner.examples.rocktour.domain.RockShow;
import org.optaplanner.examples.rocktour.domain.RockTourParametrization;
import org.optaplanner.examples.rocktour.domain.RockTourSolution;

import static java.util.stream.Collectors.*;
import static org.optaplanner.examples.rocktour.domain.RockTourParametrization.*;

public class RockTourXslxFileIO extends AbstractXslxSolutionFileIO<RockTourSolution> {

    @Override
    public RockTourSolution read(File inputSolutionFile) {
        try (InputStream in = new BufferedInputStream(new FileInputStream(inputSolutionFile))) {
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            return new RockTourXslxReader(workbook).read();
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Failed reading inputSolutionFile ("
                    + inputSolutionFile + ").", e);
        }
    }

    private static class RockTourXslxReader extends AbstractXslxReader<RockTourSolution> {

        public RockTourXslxReader(XSSFWorkbook workbook) {
            super(workbook);
        }

        @Override
        public RockTourSolution read() {
            solution = new RockTourSolution();
            readConfiguration();
            readBus();
            readShowList();
            return solution;
        }

        private void readConfiguration() {
            nextSheet("Configuration");
            nextRow();
            readHeaderCell("Tour name");
            solution.setTourName(nextStringCell().getStringCellValue());
            if (!VALID_NAME_PATTERN.matcher(solution.getTourName()).matches()) {
                throw new IllegalStateException(currentPosition() + ": The tour name (" + solution.getTourName()
                        + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
            }
            nextRow(true);
            readHeaderCell("Constraint");
            readHeaderCell("Weight");
            readHeaderCell("Description");
            RockTourParametrization parametrization = new RockTourParametrization();
            parametrization.setId(0L);
            readConstraintLine(REVENUE_OPPORTUNITY, parametrization::setRevenueOpportunity,
                    "Soft reward per revenue opportunity");
            solution.setParametrization(parametrization);
        }

        private void readBus() {
            nextSheet("Bus");
            RockBus bus = new RockBus();
            bus.setId(0L);
            nextRow();
            readHeaderCell("");
            readHeaderCell("City name");
            readHeaderCell("Latitude");
            readHeaderCell("Longitude");
            readHeaderCell("Date");
            nextRow();
            readHeaderCell("Bus start");
            String startCityName = nextStringCell().getStringCellValue();
            double startLatitude = nextNumericCell().getNumericCellValue();
            double startLongitude = nextNumericCell().getNumericCellValue();
            bus.setStartLocation(new RockLocation(startCityName, startLatitude, startLongitude));
            bus.setStartDate(LocalDate.parse(nextStringCell().getStringCellValue(), DAY_FORMATTER));
            nextRow();
            readHeaderCell("Bus end");
            String endCityName = nextStringCell().getStringCellValue();
            double endLatitude = nextNumericCell().getNumericCellValue();
            double endLongitude = nextNumericCell().getNumericCellValue();
            bus.setEndLocation(new RockLocation(endCityName, endLatitude, endLongitude));
            bus.setEndDate(LocalDate.parse(nextStringCell().getStringCellValue(), DAY_FORMATTER));
            solution.setBus(bus);
        }

        private void readShowList() {
            nextSheet("Shows");
            LocalDate startDate = solution.getBus().getStartDate();
            LocalDate endDate = solution.getBus().getEndDate();
            nextRow(false);
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("Availability");
            nextRow(false);
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            for (LocalDate date = startDate; date.compareTo(endDate) < 0; date = date.plusDays(1)) {
                if (date.equals(startDate) || date.getDayOfMonth() == 1) {
                    readHeaderCell(MONTH_FORMATTER.format(date));
                } else {
                    readHeaderCell("");
                }
            }
            nextRow(false);
            readHeaderCell("Venue name");
            readHeaderCell("City name");
            readHeaderCell("Latitude");
            readHeaderCell("Longitude");
            readHeaderCell("Revenue opportunity");
            readHeaderCell("Required");
            for (LocalDate date = startDate; date.compareTo(endDate) < 0; date = date.plusDays(1)) {
                readHeaderCell(Integer.toString(date.getDayOfMonth()));
            }
            List<RockShow> showList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            long id = 0L;
            while (nextRow()) {
                RockShow show = new RockShow();
                show.setId(id);
                show.setVenueName(nextStringCell().getStringCellValue());
                String cityName = nextStringCell().getStringCellValue();
                double latitude = nextNumericCell().getNumericCellValue();
                double longitude = nextNumericCell().getNumericCellValue();
                show.setLocation(new RockLocation(cityName, latitude, longitude));
                double revenueOpportunityDouble = nextNumericCell().getNumericCellValue();
                if (revenueOpportunityDouble != (double) (int) revenueOpportunityDouble) {
                    throw new IllegalStateException(currentPosition() + ": The show (" + show.getVenueName()
                            + ")'s revenue opportunity (" + revenueOpportunityDouble + ") must be an integer number.");
                }
                show.setRevenueOpportunity((int) revenueOpportunityDouble);
                show.setRequired(nextBooleanCell().getBooleanCellValue());
                NavigableSet<LocalDate> availableDateSet = new TreeSet<>();
                for (LocalDate date = startDate; date.compareTo(endDate) < 0; date = date.plusDays(1)) {
                    XSSFCell cell = nextStringCell();
                    if (!Objects.equals(extractColor(cell, UNAVAILABLE_COLOR), UNAVAILABLE_COLOR)) {
                        availableDateSet.add(date);
                    }
                    if (!cell.getStringCellValue().isEmpty()) {
                        throw new IllegalStateException(currentPosition() + ": The cell (" + cell.getStringCellValue()
                                + ") should be empty.");
                    }
                }
                show.setAvailableDateSet(availableDateSet);
                id++;
                showList.add(show);
            }
            solution.setShowList(showList);
        }

    }


    @Override
    public void write(RockTourSolution solution, File outputSolutionFile) {
        try (FileOutputStream out = new FileOutputStream(outputSolutionFile)) {
            Workbook workbook = new RockTourXlsxWriter(solution).write();
            workbook.write(out);
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Failed writing outputSolutionFile ("
                    + outputSolutionFile + ") for solution (" + solution + ").", e);
        }
    }

    private static class RockTourXlsxWriter extends AbstractXlsxWriter<RockTourSolution> {

        public RockTourXlsxWriter(RockTourSolution solution) {
            super(solution, RockTourApp.SOLVER_CONFIG);
        }

        @Override
        public Workbook write() {
            writeSetup();
            writeConfiguration();
            writeBus();
            writeShowList();
            writeStopsView();
            return workbook;
        }

        private void writeConfiguration() {
            nextSheet("Configuration", 1, 3, false);
            nextRow();
            nextHeaderCell("Tour name");
            nextCell().setCellValue(solution.getTourName());
            nextRow();
            nextRow();
            nextHeaderCell("Constraint");
            nextHeaderCell("Weight");
            nextHeaderCell("Description");
            RockTourParametrization parametrization = solution.getParametrization();

            writeConstraintLine(REVENUE_OPPORTUNITY, parametrization::getRevenueOpportunity,
                    "Soft reward per revenue opportunity");
            nextRow();
            autoSizeColumnsWithHeader();
        }

        private void writeBus() {
            nextSheet("Bus", 1, 0, false);
            nextRow();
            nextHeaderCell("");
            nextHeaderCell("City name");
            nextHeaderCell("Latitude");
            nextHeaderCell("Longitude");
            nextHeaderCell("Date");
            RockBus bus = solution.getBus();
            nextRow();
            nextHeaderCell("Bus start");
            nextCell().setCellValue(bus.getStartLocation().getCityName());
            nextCell().setCellValue(bus.getStartLocation().getLatitude());
            nextCell().setCellValue(bus.getStartLocation().getLongitude());
            nextCell().setCellValue(DAY_FORMATTER.format(bus.getStartDate()));
            nextRow();
            nextHeaderCell("Bus end");
            nextCell().setCellValue(bus.getEndLocation().getCityName());
            nextCell().setCellValue(bus.getEndLocation().getLatitude());
            nextCell().setCellValue(bus.getEndLocation().getLongitude());
            nextCell().setCellValue(DAY_FORMATTER.format(bus.getEndDate()));
            autoSizeColumnsWithHeader();
        }

        private void writeShowList() {
            nextSheet("Shows", 1, 3, false);
            LocalDate startDate = solution.getBus().getStartDate();
            LocalDate endDate = solution.getBus().getEndDate();
            nextRow();
            nextHeaderCell("");
            nextHeaderCell("");
            nextHeaderCell("");
            nextHeaderCell("");
            nextHeaderCell("");
            nextHeaderCell("");
            nextHeaderCell("Availability");
            currentSheet.addMergedRegion(new CellRangeAddress(currentRowNumber, currentRowNumber,
                    currentColumnNumber, currentColumnNumber + (int) ChronoUnit.DAYS.between(startDate, endDate) - 1));
            nextRow();
            nextHeaderCell("");
            nextHeaderCell("");
            nextHeaderCell("");
            nextHeaderCell("");
            nextHeaderCell("");
            nextHeaderCell("");
            for (LocalDate date = startDate; date.compareTo(endDate) < 0; date = date.plusDays(1)) {
                if (date.equals(startDate) || date.getDayOfMonth() == 1) {
                    nextHeaderCell(MONTH_FORMATTER.format(date));
                    LocalDate startNextMonthDate = date.with(TemporalAdjusters.firstDayOfNextMonth());
                    if (endDate.compareTo(startNextMonthDate) < 0) {
                        startNextMonthDate = endDate;
                    }
                    currentSheet.addMergedRegion(new CellRangeAddress(currentRowNumber, currentRowNumber,
                            currentColumnNumber, currentColumnNumber + (int) ChronoUnit.DAYS.between(date, startNextMonthDate) - 1));
                } else {
                    nextCell();
                }
            }
            nextRow();
            nextHeaderCell("Venue name");
            nextHeaderCell("City name");
            nextHeaderCell("Latitude");
            nextHeaderCell("Longitude");
            nextHeaderCell("Revenue opportunity");
            nextHeaderCell("Required");
            for (LocalDate date = startDate; date.compareTo(endDate) < 0; date = date.plusDays(1)) {
                nextHeaderCell(Integer.toString(date.getDayOfMonth()));
            }
            for (RockShow show : solution.getShowList()) {
                nextRow();
                nextCell().setCellValue(show.getVenueName());
                nextCell().setCellValue(show.getLocation().getCityName());
                nextCell().setCellValue(show.getLocation().getLatitude());
                nextCell().setCellValue(show.getLocation().getLongitude());
                nextCell().setCellValue(show.getRevenueOpportunity());
                nextCell().setCellValue(show.isRequired());
                for (LocalDate date = startDate; date.compareTo(endDate) < 0; date = date.plusDays(1)) {
                    if (show.getAvailableDateSet().contains(date)) {
                        nextCell();
                    } else {
                        nextCell(unavailableStyle);
                    }
                }
            }
            autoSizeColumnsWithHeader();
        }

        private void writeStopsView() {
            nextSheet("Stops", 1, 1, true);
            nextRow();
            nextHeaderCell("Date");
            nextHeaderCell("Venue names");
            nextHeaderCell("City names");
            LocalDate startDate = solution.getBus().getStartDate();
            LocalDate endDate = solution.getBus().getEndDate();
            for (LocalDate date = startDate; date.compareTo(endDate) < 0; date = date.plusDays(1)) {
                nextRow();
                nextHeaderCell(DAY_FORMATTER.format(date));
                final LocalDate finalDate = date;
                List<RockShow> dateShowList = solution.getShowList().stream().filter(show -> finalDate.equals(show.getDate())).collect(toList());
                nextCell().setCellValue(dateShowList.stream().map(RockShow::getVenueName).collect(joining(", ")));
                nextCell().setCellValue(dateShowList.stream().map(show -> show.getLocation().getCityName()).collect(joining(", ")));
            }
            nextRow();
            nextHeaderCell("Unassigned");
            List<RockShow> unassignedShowList = solution.getShowList().stream().filter(show -> show.getDate() == null).collect(toList());
            nextCell().setCellValue(unassignedShowList.stream().map(RockShow::getVenueName).collect(joining(", ")));
            nextCell().setCellValue(unassignedShowList.stream().map(show -> show.getLocation().getCityName()).collect(joining(", ")));
            autoSizeColumnsWithHeader();
        }

    }

}
