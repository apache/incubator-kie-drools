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

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.optaplanner.examples.rocktour.domain.RockTourConstraintConfiguration.DELAY_SHOW_COST_PER_DAY;
import static org.optaplanner.examples.rocktour.domain.RockTourConstraintConfiguration.DRIVING_TIME_TO_BUS_ARRIVAL_PER_SECOND;
import static org.optaplanner.examples.rocktour.domain.RockTourConstraintConfiguration.DRIVING_TIME_TO_SHOW_PER_SECOND;
import static org.optaplanner.examples.rocktour.domain.RockTourConstraintConfiguration.EARLY_LATE_BREAK_DRIVING_SECONDS;
import static org.optaplanner.examples.rocktour.domain.RockTourConstraintConfiguration.HOS_WEEK_CONSECUTIVE_DRIVING_DAYS_BUDGET;
import static org.optaplanner.examples.rocktour.domain.RockTourConstraintConfiguration.HOS_WEEK_DRIVING_SECONDS_BUDGET;
import static org.optaplanner.examples.rocktour.domain.RockTourConstraintConfiguration.HOS_WEEK_REST_DAYS;
import static org.optaplanner.examples.rocktour.domain.RockTourConstraintConfiguration.NIGHT_DRIVING_SECONDS;
import static org.optaplanner.examples.rocktour.domain.RockTourConstraintConfiguration.REQUIRED_SHOW;
import static org.optaplanner.examples.rocktour.domain.RockTourConstraintConfiguration.REVENUE_OPPORTUNITY;
import static org.optaplanner.examples.rocktour.domain.RockTourConstraintConfiguration.SHORTEN_DRIVING_TIME_PER_MILLISECOND_SQUARED;
import static org.optaplanner.examples.rocktour.domain.RockTourConstraintConfiguration.UNASSIGNED_SHOW;

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
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.examples.common.persistence.AbstractXlsxSolutionFileIO;
import org.optaplanner.examples.rocktour.app.RockTourApp;
import org.optaplanner.examples.rocktour.domain.RockBus;
import org.optaplanner.examples.rocktour.domain.RockLocation;
import org.optaplanner.examples.rocktour.domain.RockShow;
import org.optaplanner.examples.rocktour.domain.RockTourConstraintConfiguration;
import org.optaplanner.examples.rocktour.domain.RockTourSolution;

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

    private static class RockTourXlsxReader extends AbstractXlsxReader<RockTourSolution, HardMediumSoftLongScore> {

        public RockTourXlsxReader(XSSFWorkbook workbook) {
            super(workbook, RockTourApp.SOLVER_CONFIG);
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
            RockTourConstraintConfiguration constraintConfiguration = new RockTourConstraintConfiguration();
            readLongConstraintParameterLine(EARLY_LATE_BREAK_DRIVING_SECONDS,
                    constraintConfiguration::setEarlyLateBreakDrivingSecondsBudget,
                    "Maximum driving time in seconds between 2 shows on the same day.");
            readLongConstraintParameterLine(NIGHT_DRIVING_SECONDS, constraintConfiguration::setNightDrivingSecondsBudget,
                    "Maximum driving time in seconds per night between 2 shows.");
            readLongConstraintParameterLine(HOS_WEEK_DRIVING_SECONDS_BUDGET,
                    constraintConfiguration::setHosWeekDrivingSecondsBudget,
                    "Maximum driving time in seconds since last weekend rest.");
            readIntConstraintParameterLine(HOS_WEEK_CONSECUTIVE_DRIVING_DAYS_BUDGET,
                    constraintConfiguration::setHosWeekConsecutiveDrivingDaysBudget,
                    "Maximum driving days since last weekend rest.");
            readIntConstraintParameterLine(HOS_WEEK_REST_DAYS, constraintConfiguration::setHosWeekRestDays,
                    "Minimum weekend rest in days (actually in full night sleeps: 2 days guarantees only 32 hours).");
            readScoreConstraintHeaders();
            constraintConfiguration.setId(0L);
            constraintConfiguration.setRequiredShow(readScoreConstraintLine(REQUIRED_SHOW,
                    "Penalty per required show that isn't assigned."));
            constraintConfiguration.setUnassignedShow(readScoreConstraintLine(UNASSIGNED_SHOW,
                    "Penalty per show that isn't assigned."));
            constraintConfiguration.setRevenueOpportunity(readScoreConstraintLine(REVENUE_OPPORTUNITY,
                    "Reward per revenue opportunity."));
            constraintConfiguration.setDrivingTimeToShowPerSecond(readScoreConstraintLine(DRIVING_TIME_TO_SHOW_PER_SECOND,
                    "Driving time cost per second, excluding after the last show."));
            constraintConfiguration
                    .setDrivingTimeToBusArrivalPerSecond(readScoreConstraintLine(DRIVING_TIME_TO_BUS_ARRIVAL_PER_SECOND,
                            "Driving time cost per second from the last show to the bus arrival location."));
            constraintConfiguration.setDelayShowCostPerDay(readScoreConstraintLine(DELAY_SHOW_COST_PER_DAY,
                    "Cost per day for each day that a show is assigned later in the schedule."));
            constraintConfiguration.setShortenDrivingTimePerMillisecondSquared(
                    readScoreConstraintLine(SHORTEN_DRIVING_TIME_PER_MILLISECOND_SQUARED,
                            "Avoid long driving times: Penalty per millisecond of continuous driving time squared."));
            solution.setConstraintConfiguration(constraintConfiguration);
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
                    .sorted(Comparator.comparing(RockLocation::getLatitude).thenComparing(RockLocation::getLongitude)
                            .thenComparing(RockLocation::getCityName))
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

    private static class RockTourXlsxWriter extends AbstractXlsxWriter<RockTourSolution, HardMediumSoftLongScore> {

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
            writeScoreView(justificationList -> justificationList.stream()
                    .filter(o -> o instanceof RockShow).map(o -> ((RockShow) o).getVenueName())
                    .collect(joining(", ")));
            return workbook;
        }

        private void writeConfiguration() {
            nextSheet("Configuration", 1, 8, false);
            nextRow();
            nextHeaderCell("Tour name");
            nextCell().setCellValue(solution.getTourName());
            RockTourConstraintConfiguration constraintConfiguration = solution.getConstraintConfiguration();
            writeLongConstraintParameterLine(EARLY_LATE_BREAK_DRIVING_SECONDS,
                    constraintConfiguration::getEarlyLateBreakDrivingSecondsBudget,
                    "Maximum driving time in seconds between 2 shows on the same day.");
            writeLongConstraintParameterLine(NIGHT_DRIVING_SECONDS, constraintConfiguration::getNightDrivingSecondsBudget,
                    "Maximum driving time in seconds per night between 2 shows.");
            writeLongConstraintParameterLine(HOS_WEEK_DRIVING_SECONDS_BUDGET,
                    constraintConfiguration::getHosWeekDrivingSecondsBudget,
                    "Maximum driving time in seconds since last weekend rest.");
            writeIntConstraintParameterLine(HOS_WEEK_CONSECUTIVE_DRIVING_DAYS_BUDGET,
                    constraintConfiguration::getHosWeekConsecutiveDrivingDaysBudget,
                    "Maximum driving days since last weekend rest.");
            writeIntConstraintParameterLine(HOS_WEEK_REST_DAYS, constraintConfiguration::getHosWeekRestDays,
                    "Minimum weekend rest in days (actually in full night sleeps: 2 days guarantees only 32 hours).");
            nextRow();
            writeScoreConstraintHeaders();
            writeScoreConstraintLine(REQUIRED_SHOW, constraintConfiguration.getRequiredShow(),
                    "Penalty per required show that isn't assigned.");
            writeScoreConstraintLine(UNASSIGNED_SHOW, constraintConfiguration.getUnassignedShow(),
                    "Penalty per show that isn't assigned.");
            writeScoreConstraintLine(REVENUE_OPPORTUNITY, constraintConfiguration.getRevenueOpportunity(),
                    "Reward per revenue opportunity.");
            writeScoreConstraintLine(DRIVING_TIME_TO_SHOW_PER_SECOND, constraintConfiguration.getDrivingTimeToShowPerSecond(),
                    "Driving time cost per second, excluding after the last show.");
            writeScoreConstraintLine(DRIVING_TIME_TO_BUS_ARRIVAL_PER_SECOND,
                    constraintConfiguration.getDrivingTimeToBusArrivalPerSecond(),
                    "Driving time cost per second from the last show to the bus arrival location.");
            writeScoreConstraintLine(DELAY_SHOW_COST_PER_DAY, constraintConfiguration.getDelayShowCostPerDay(),
                    "Cost per day for each day that a show is assigned later in the schedule.");
            writeScoreConstraintLine(SHORTEN_DRIVING_TIME_PER_MILLISECOND_SQUARED,
                    constraintConfiguration.getShortenDrivingTimePerMillisecondSquared(),
                    "Avoid long driving times: Penalty per millisecond of continuous driving time squared.");
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
                            currentColumnNumber,
                            currentColumnNumber + (int) ChronoUnit.DAYS.between(date, startNextMonthDate) - 1));
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
                    .sorted(Comparator.comparing(RockLocation::getLatitude).thenComparing(RockLocation::getLongitude)
                            .thenComparing(RockLocation::getCityName))
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
                                        + ") from (" + fromLocation + ") to (" + toLocation + ").");
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
