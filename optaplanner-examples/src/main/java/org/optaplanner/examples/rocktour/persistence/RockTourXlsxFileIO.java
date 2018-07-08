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
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.optaplanner.examples.common.persistence.AbstractXlsxSolutionFileIO;
import org.optaplanner.examples.rocktour.app.RockTourApp;
import org.optaplanner.examples.rocktour.domain.RockBus;
import org.optaplanner.examples.rocktour.domain.RockLocation;
import org.optaplanner.examples.rocktour.domain.RockShow;
import org.optaplanner.examples.rocktour.domain.RockTourParametrization;
import org.optaplanner.examples.rocktour.domain.RockTourSolution;

import static java.util.stream.Collectors.*;
import static org.optaplanner.examples.rocktour.domain.RockTourParametrization.*;

public class RockTourXlsxFileIO extends AbstractXlsxSolutionFileIO<RockTourSolution> {

    @Override
    public RockTourSolution read(File inputSolutionFile) {
        try (InputStream in = new BufferedInputStream(new FileInputStream(inputSolutionFile))) {
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            return new RockTourXlsxReader(workbook).read();
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Failed reading inputSolutionFile ("
                    + inputSolutionFile + ").", e);
        }
    }

    private static class RockTourXlsxReader extends AbstractXlsxReader<RockTourSolution> {

        public RockTourXlsxReader(XSSFWorkbook workbook) {
            super(workbook);
        }

        @Override
        public RockTourSolution read() {
            solution = new RockTourSolution();
            readConfiguration();
            readBus();
            readShowList();
            readDrivingTime();
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
            RockTourParametrization parametrization = new RockTourParametrization();
            readLongConstraintLine(EARLY_LATE_BREAK_DRIVING_SECONDS, parametrization::setEarlyLateBreakDrivingSecondsBudget,
                    "Maximum driving time in seconds between 2 shows on the same day.");
            readLongConstraintLine(NIGHT_DRIVING_SECONDS, parametrization::setNightDrivingSecondsBudget,
                    "Maximum driving time in seconds per night between 2 shows.");
            readLongConstraintLine(HOS_WEEK_DRIVING_SECONDS_BUDGET, parametrization::setHosWeekDrivingSecondsBudget,
                    "Maximum driving time in seconds since last weekend rest.");
            readIntConstraintLine(HOS_WEEK_CONSECUTIVE_DRIVING_DAYS_BUDGET, parametrization::setHosWeekConsecutiveDrivingDaysBudget,
                    "Maximum driving days since last weekend rest.");
            readIntConstraintLine(HOS_WEEK_REST_DAYS, parametrization::setHosWeekRestDays,
                    "Minimum weekend rest in days (actually in full night sleeps: 2 days guarantees only 32 hours).");
            nextRow(true);
            readHeaderCell("Constraint");
            readHeaderCell("Weight");
            readHeaderCell("Description");
            parametrization.setId(0L);
            readLongConstraintLine(MISSED_SHOW_PENALTY, parametrization::setMissedShowPenalty,
                    "Set this to 1 to prioritize visiting all shows (over the other constraints).");
            readLongConstraintLine(REVENUE_OPPORTUNITY, parametrization::setRevenueOpportunity,
                    "Reward per revenue opportunity.");
            readLongConstraintLine(DRIVING_TIME_COST_PER_SECOND, parametrization::setDrivingTimeCostPerSecond,
                    "Driving time cost per second.");
            readLongConstraintLine(DELAY_COST_PER_DAY, parametrization::setDelayCostPerDay,
                    "Cost per day for each day that a visit is later in the schedule.");
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
            readHeaderCell("");
            readHeaderCell("Availability");
            nextRow(false);
            readHeaderCell("");
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
            readHeaderCell("Duration (in days)");
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
                double duration = nextNumericCell().getNumericCellValue();
                int durationInHalfDay = (int) (duration * 2.0);
                if (((double) durationInHalfDay) != duration * 2.0) {
                    throw new IllegalStateException(currentPosition() + ": The duration (" + duration
                            + ") should be a multiple of 0.5.");
                }
                if (durationInHalfDay < 1) {
                    throw new IllegalStateException(currentPosition() + ": The duration (" + duration
                            + ") should be at least 0.5.");
                }
                show.setDurationInHalfDay(durationInHalfDay);
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
                if (availableDateSet.isEmpty()) {
                    throw new IllegalStateException(currentPosition() + ": The show (" + show.getVenueName()
                            + ")'s has no available date: all dates are unavailable.");
                }
                show.setAvailableDateSet(availableDateSet);
                id++;
                showList.add(show);
            }
            solution.setShowList(showList);
        }

        private void readDrivingTime() {
            Map<Pair<Double, Double>, List<RockLocation>> latLongToLocationMap = Stream.concat(
                    Stream.of(solution.getBus().getStartLocation(), solution.getBus().getEndLocation()),
                    solution.getShowList().stream().map(RockShow::getLocation))
                    .distinct()
                    .sorted(Comparator.comparing(RockLocation::getLatitude).thenComparing(RockLocation::getLongitude).thenComparing(RockLocation::getCityName))
                    .collect(groupingBy(location -> Pair.of(location.getLatitude(), location.getLongitude()),
                            LinkedHashMap::new, toList()));
            if (!hasSheet("Driving time")) {
                latLongToLocationMap.forEach((fromLatLong, fromLocationList) -> {
                    for (RockLocation fromLocation : fromLocationList) {
                        fromLocation.setDrivingSecondsMap(new LinkedHashMap<>(fromLocationList.size()));
                    }
                    latLongToLocationMap.forEach((toLatLong, toLocationList) -> {
                        long drivingTime = 0L;
                        for (RockLocation fromLocation : fromLocationList) {
                            for (RockLocation toLocation : toLocationList) {
                                // TODO use haversine air distance and convert to average seconds for truck
                                drivingTime = fromLocation.getAirDistanceTo(toLocation);
                                fromLocation.getDrivingSecondsMap().put(toLocation, drivingTime);
                            }
                        }
                    });
                });
                return;
            }
            nextSheet("Driving time");
            nextRow();
            readHeaderCell("Driving time in seconds. Delete this sheet to generate it from air distances.");
            nextRow();
            readHeaderCell("Latitude");
            readHeaderCell("");
            for (Pair<Double, Double> latLong : latLongToLocationMap.keySet()) {
                readHeaderCell(latLong.getLeft());
            }
            nextRow();
            readHeaderCell("");
            readHeaderCell("Longitude");
            for (Pair<Double, Double> latLong : latLongToLocationMap.keySet()) {
                readHeaderCell(latLong.getRight());
            }
            latLongToLocationMap.forEach((fromLatLong, fromLocationList) -> {
                nextRow();
                readHeaderCell(fromLatLong.getLeft());
                readHeaderCell(fromLatLong.getRight());
                for (RockLocation fromLocation : fromLocationList) {
                    fromLocation.setDrivingSecondsMap(new LinkedHashMap<>(fromLocationList.size()));
                }
                latLongToLocationMap.forEach((toLatLong, toLocationList) -> {
                    double drivingTimeDouble = nextNumericCell().getNumericCellValue();
                    long drivingTime = (long) drivingTimeDouble;
                    if (drivingTimeDouble != (double) drivingTime) {
                        throw new IllegalStateException(currentPosition() + ": The driving time (" + drivingTimeDouble
                                + ") should be an integer number.");
                    }
                    for (RockLocation fromLocation : fromLocationList) {
                        for (RockLocation toLocation : toLocationList) {
                            fromLocation.getDrivingSecondsMap().put(toLocation, drivingTime);
                        }
                    }
                });
            });
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
            writeDrivingTime();
            writeStopsView();
            return workbook;
        }

        private void writeConfiguration() {
            nextSheet("Configuration", 1, 8, false);
            nextRow();
            nextHeaderCell("Tour name");
            nextCell().setCellValue(solution.getTourName());
            RockTourParametrization parametrization = solution.getParametrization();
            writeLongConstraintLine(EARLY_LATE_BREAK_DRIVING_SECONDS, parametrization::getEarlyLateBreakDrivingSecondsBudget,
                    "Maximum driving time in seconds between 2 shows on the same day.");
            writeLongConstraintLine(NIGHT_DRIVING_SECONDS, parametrization::getNightDrivingSecondsBudget,
                    "Maximum driving time in seconds per night between 2 shows.");
            writeLongConstraintLine(HOS_WEEK_DRIVING_SECONDS_BUDGET, parametrization::getHosWeekDrivingSecondsBudget,
                    "Maximum driving time in seconds since last weekend rest.");
            writeIntConstraintLine(HOS_WEEK_CONSECUTIVE_DRIVING_DAYS_BUDGET, parametrization::getHosWeekConsecutiveDrivingDaysBudget,
                    "Maximum driving days since last weekend rest.");
            writeIntConstraintLine(HOS_WEEK_REST_DAYS, parametrization::getHosWeekRestDays,
                    "Minimum weekend rest in days (actually in full night sleeps: 2 days guarantees only 32 hours).");
            nextRow();
            nextRow();
            nextHeaderCell("Constraint");
            nextHeaderCell("Weight");
            nextHeaderCell("Description");
            writeLongConstraintLine(MISSED_SHOW_PENALTY, parametrization::getMissedShowPenalty,
                    "Set this to 1 to prioritize visiting all shows (over the other constraints).");
            writeLongConstraintLine(REVENUE_OPPORTUNITY, parametrization::getRevenueOpportunity,
                    "Reward per revenue opportunity.");
            writeLongConstraintLine(DRIVING_TIME_COST_PER_SECOND, parametrization::getDrivingTimeCostPerSecond,
                    "Driving time cost per second.");
            writeLongConstraintLine(DELAY_COST_PER_DAY, parametrization::getDelayCostPerDay,
                    "Cost per day for each day that a visit is later in the schedule.");
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
            nextHeaderCell("Duration (in days)");
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
                nextCell().setCellValue(show.getDurationInHalfDay() * 0.5);
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

        private void writeDrivingTime() {
            nextSheet("Driving time", 2, 3, false);
            nextRow();
            nextHeaderCell("Driving time in seconds. Delete this sheet to generate it from air distances.");
            currentSheet.addMergedRegion(new CellRangeAddress(currentRowNumber, currentRowNumber,
                    currentColumnNumber, currentColumnNumber + 10));
            Map<Pair<Double, Double>, List<RockLocation>> latLongToLocationMap = Stream.concat(
                    Stream.of(solution.getBus().getStartLocation(), solution.getBus().getEndLocation()),
                    solution.getShowList().stream().map(RockShow::getLocation))
                    .distinct()
                    .sorted(Comparator.comparing(RockLocation::getLatitude).thenComparing(RockLocation::getLongitude).thenComparing(RockLocation::getCityName))
                    .collect(groupingBy(location -> Pair.of(location.getLatitude(), location.getLongitude()),
                            LinkedHashMap::new, toList()));
            nextRow();
            nextHeaderCell("Latitude");
            nextHeaderCell("");
            for (Pair<Double, Double> latLong : latLongToLocationMap.keySet()) {
                nextHeaderCell(latLong.getLeft());
            }
            nextRow();
            nextHeaderCell("");
            nextHeaderCell("Longitude");
            for (Pair<Double, Double> latLong : latLongToLocationMap.keySet()) {
                nextHeaderCell(latLong.getRight());
            }
            latLongToLocationMap.forEach((fromLatLong, fromLocationList) -> {
                nextRow();
                nextHeaderCell(fromLatLong.getLeft());
                nextHeaderCell(fromLatLong.getRight());
                latLongToLocationMap.forEach((toLatLong, toLocationList) -> {
                    long drivingTime = fromLocationList.get(0).getDrivingTimeTo(toLocationList.get(0));
                    for (RockLocation fromLocation : fromLocationList) {
                        for (RockLocation toLocation : toLocationList) {
                            if (fromLocation.getDrivingTimeTo(toLocation) != drivingTime) {
                                throw new IllegalStateException("The driving time (" + drivingTime
                                        + ") from (" + fromLocationList.get(0) + ") to (" + toLocationList.get(0)
                                        + ") is not the driving time (" + fromLocation.getDrivingTimeTo(toLocation)
                                        +  ") from (" + fromLocation + ") to (" + toLocation + ").");
                            }
                        }
                    }
                    nextCell().setCellValue(drivingTime);
                });
            });
            autoSizeColumnsWithHeader();
        }

        private void writeStopsView() {
            nextSheet("Stops", 2, 1, true);
            nextRow();
            nextHeaderCell("Date");
            nextHeaderCell("Venue name");
            nextHeaderCell("City name");
            nextHeaderCell("Driving time");
            nextHeaderCell("Driving time per week");
            nextHeaderCell("Latitude");
            nextHeaderCell("Longitude");
            nextHeaderCell("Duration (in days)");
            nextHeaderCell("Revenue opportunity");
            nextHeaderCell("Required");
            nextHeaderCell("Available dates size");
            LocalDate startDate = solution.getBus().getStartDate();
            LocalDate endDate = solution.getBus().getEndDate();
            Map<LocalDate, List<RockShow>> dateToShowListMap = solution.getShowList().stream()
                    .filter(show -> show.getDate() != null)
                    .collect(groupingBy(RockShow::getDate));
            long drivingTimeWeekTotal = 0L;
            for (LocalDate date = startDate; date.compareTo(endDate) < 0; date = date.plusDays(1)) {
                List<RockShow> showList = dateToShowListMap.computeIfAbsent(date, k -> Collections.emptyList());
                showList.sort(Comparator.comparing(RockShow::getTimeOfDay).thenComparing(RockShow::getVenueName));
                nextRow();
                if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                    nextCell(unavailableStyle).setCellValue(DAY_FORMATTER.format(date));
                } else {
                    nextHeaderCell(DAY_FORMATTER.format(date));
                }
                if (!showList.isEmpty()) {
                    boolean first = true;
                    for (RockShow show : showList) {
                        if (!first) {
                            nextRow();
                            nextCell();
                        }
                        nextCell().setCellValue(show.getVenueName());
                        nextCell().setCellValue(show.getLocation().getCityName());
                        long drivingTime = show.getDrivingTimeFromPreviousStandstill();
                        drivingTimeWeekTotal += drivingTime;
                        nextCell().setCellValue(toHoursAndMinutes(drivingTime));
                        if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                            nextCell().setCellValue(toHoursAndMinutes(drivingTimeWeekTotal));
                            drivingTimeWeekTotal = 0;
                        } else {
                            nextCell();
                        }
                        nextCell().setCellValue(show.getLocation().getLatitude());
                        nextCell().setCellValue(show.getLocation().getLongitude());
                        nextCell().setCellValue(show.getDurationInHalfDay() * 0.5);
                        nextCell().setCellValue(show.getRevenueOpportunity());
                        nextCell().setCellValue(show.isRequired());
                        nextCell().setCellValue(show.getAvailableDateSet().size());
                        first = false;
                    }
                } else {
                    nextCell();
                    nextCell();
                    nextCell();
                    if (date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                        nextCell().setCellValue(toHoursAndMinutes(drivingTimeWeekTotal));
                        drivingTimeWeekTotal = 0;
                    }
                }
            }
            nextRow();
            nextRow();
            long revenueOpportunityLoss = 0L;
            for (RockShow show : solution.getShowList().stream()
                    .filter(show -> show.getDate() == null)
                    .sorted(Comparator.comparing(RockShow::getVenueName))
                    .collect(toList())) {
                nextRow();
                nextHeaderCell("Unassigned");
                nextCell().setCellValue(show.getVenueName());
                nextCell().setCellValue(show.getLocation().getCityName());
                nextCell().setCellValue("0");
                nextCell().setCellValue(show.getLocation().getLatitude());
                nextCell().setCellValue(show.getLocation().getLongitude());
                nextCell().setCellValue(show.getDurationInHalfDay() * 0.5);
                nextCell().setCellValue(show.getRevenueOpportunity());
                revenueOpportunityLoss += show.getRevenueOpportunity();
                nextCell().setCellValue(show.isRequired());
                nextCell().setCellValue(show.getAvailableDateSet().size());
            }
            nextRow();
            nextHeaderCell("Total revenue opportunity loss");
            nextCell();
            nextCell();
            nextCell();
            nextCell();
            nextCell();
            nextCell();
            nextCell().setCellValue(revenueOpportunityLoss);
            autoSizeColumnsWithHeader();
        }

        private static String toHoursAndMinutes(long drivingTimeWeekTotal) {
            return (drivingTimeWeekTotal / 3600L) + " hours " + (drivingTimeWeekTotal % 3600 / 60) + " minutes";
        }

    }

}
