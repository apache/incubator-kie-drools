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

package org.optaplanner.examples.flightcrewscheduling.persistence;

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
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.optaplanner.examples.common.persistence.AbstractXlsxSolutionFileIO;
import org.optaplanner.examples.flightcrewscheduling.app.FlightCrewSchedulingApp;
import org.optaplanner.examples.flightcrewscheduling.domain.Airport;
import org.optaplanner.examples.flightcrewscheduling.domain.Employee;
import org.optaplanner.examples.flightcrewscheduling.domain.Flight;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightAssignment;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightCrewParametrization;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightCrewSolution;
import org.optaplanner.examples.flightcrewscheduling.domain.Skill;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.optaplanner.examples.flightcrewscheduling.domain.FlightCrewParametrization.EMPLOYEE_UNAVAILABILITY;
import static org.optaplanner.examples.flightcrewscheduling.domain.FlightCrewParametrization.FLIGHT_CONFLICT;
import static org.optaplanner.examples.flightcrewscheduling.domain.FlightCrewParametrization.LOAD_BALANCE_FLIGHT_DURATION_TOTAL_PER_EMPLOYEE;
import static org.optaplanner.examples.flightcrewscheduling.domain.FlightCrewParametrization.REQUIRED_SKILL;
import static org.optaplanner.examples.flightcrewscheduling.domain.FlightCrewParametrization.TRANSFER_BETWEEN_TWO_FLIGHTS;

public class FlightCrewSchedulingXlsxFileIO extends AbstractXlsxSolutionFileIO<FlightCrewSolution> {

    public static final DateTimeFormatter MILITARY_TIME_FORMATTER
            = DateTimeFormatter.ofPattern("HHmm", Locale.ENGLISH);

    @Override
    public FlightCrewSolution read(File inputSolutionFile) {
        try (InputStream in = new BufferedInputStream(new FileInputStream(inputSolutionFile))) {
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            return new FlightCrewSchedulingXlsxReader(workbook).read();
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Failed reading inputSolutionFile ("
                    + inputSolutionFile + ").", e);
        }
    }

    @Override
    public void write(FlightCrewSolution solution, File outputSolutionFile) {
        try (FileOutputStream out = new FileOutputStream(outputSolutionFile)) {
            Workbook workbook = new FlightCrewSchedulingXlsxWriter(solution).write();
            workbook.write(out);
        } catch (IOException | RuntimeException e) {
            throw new IllegalStateException("Failed writing outputSolutionFile ("
                    + outputSolutionFile + ") for solution (" + solution + ").", e);
        }
    }

    private static class FlightCrewSchedulingXlsxReader extends AbstractXlsxReader<FlightCrewSolution> {

        private Map<String, Skill> skillMap;
        private Map<String, Employee> nameToEmployeeMap;
        private Map<String, Airport> airportMap;

        public FlightCrewSchedulingXlsxReader(XSSFWorkbook workbook) {
            super(workbook, FlightCrewSchedulingApp.SOLVER_CONFIG);
        }

        @Override
        public FlightCrewSolution read() {
            solution = new FlightCrewSolution();
            readConfiguration();
            readSkillList();
            readAirportList();
            readTaxiTimeMaps();
            readEmployeeList();
            readFlightListAndFlightAssignmentList();
            return solution;
        }

        private void readConfiguration() {
            nextSheet("Configuration");
            nextRow(false);
            readHeaderCell("Schedule start UTC Date");
            solution.setScheduleFirstUTCDate(LocalDate.parse(nextStringCell().getStringCellValue(), DAY_FORMATTER));
            nextRow(false);
            readHeaderCell("Schedule end UTC Date");
            solution.setScheduleLastUTCDate(LocalDate.parse(nextStringCell().getStringCellValue(), DAY_FORMATTER));
            nextRow(false);
            nextRow(false);
            readHeaderCell("Constraint");
            readHeaderCell("Weight");
            readHeaderCell("Description");
            FlightCrewParametrization parametrization = new FlightCrewParametrization();
            parametrization.setId(0L);
            readLongConstraintParameterLine(LOAD_BALANCE_FLIGHT_DURATION_TOTAL_PER_EMPLOYEE,
                    parametrization::setLoadBalanceFlightDurationTotalPerEmployee,
                    "Soft penalty per 0.001 minute difference with the average flight duration total per employee.");
            readIntConstraintParameterLine(REQUIRED_SKILL, null,
                    "Hard penalty per missing required skill for a flight assignment");
            readIntConstraintParameterLine(FLIGHT_CONFLICT, null,
                    "Hard penalty per 2 flights of an employee that directly overlap");
            readIntConstraintParameterLine(TRANSFER_BETWEEN_TWO_FLIGHTS, null,
                    "Hard penalty per 2 sequential flights of an employee with no viable transfer from the arrival airport to the departure airport");
            readIntConstraintParameterLine(EMPLOYEE_UNAVAILABILITY, null,
                    "Hard penalty per flight assignment to an employee that is unavailable");
            solution.setParametrization(parametrization);
        }

        private void readSkillList() {
            nextSheet("Skills");
            nextRow(false);
            readHeaderCell("Name");
            List<Skill> skillList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            skillMap = new HashMap<>(currentSheet.getLastRowNum() - 1);
            long id = 0L;
            while (nextRow()) {
                Skill skill = new Skill();
                skill.setId(id++);
                skill.setName(nextStringCell().getStringCellValue());
                skillMap.put(skill.getName(), skill);
                skillList.add(skill);
            }
            solution.setSkillList(skillList);
        }

        private void readAirportList() {
            nextSheet("Airports");
            nextRow(false);
            readHeaderCell("Code");
            readHeaderCell("Name");
            readHeaderCell("Latitude");
            readHeaderCell("Longitude");
            List<Airport> airportList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            airportMap = new HashMap<>(currentSheet.getLastRowNum() - 1);
            long id = 0L;
            while (nextRow()) {
                Airport airport = new Airport();
                airport.setId(id++);
                airport.setCode(nextStringCell().getStringCellValue());
                airport.setName(nextStringCell().getStringCellValue());
                airport.setLatitude(nextNumericCell().getNumericCellValue());
                airport.setLongitude(nextNumericCell().getNumericCellValue());
                airportMap.put(airport.getCode(), airport);
                airportList.add(airport);
            }
            solution.setAirportList(airportList);
        }

        private void readTaxiTimeMaps() {
            nextSheet("Taxi time");
            nextRow();
            readHeaderCell("Driving time in minutes by taxi between two nearby airports to allow employees to start from a different airport.");
            List<Airport> airportList = solution.getAirportList();
            nextRow();
            readHeaderCell("Airport code");
            for (Airport airport : airportList) {
                readHeaderCell(airport.getCode());
            }
            for (Airport a : airportList) {
                a.setTaxiTimeInMinutesMap(new LinkedHashMap<>(airportList.size()));
                nextRow();
                readHeaderCell(a.getCode());
                for (Airport b : airportList) {
                    XSSFCell taxiTimeCell = nextNumericCellOrBlank();
                    if (taxiTimeCell != null) {
                        a.getTaxiTimeInMinutesMap().put(b, (long) taxiTimeCell.getNumericCellValue());
                    }
                }
            }
        }

        private void readEmployeeList() {
            nextSheet("Employees");
            nextRow(false);
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("Unavailability");
            nextRow(false);
            readHeaderCell("Name");
            readHeaderCell("Home airport");
            readHeaderCell("Skills");
            LocalDate firstDate = solution.getScheduleFirstUTCDate();
            LocalDate lastDate = solution.getScheduleLastUTCDate();
            for (LocalDate date = firstDate; date.compareTo(lastDate) <= 0; date = date.plusDays(1)) {
                readHeaderCell(DAY_FORMATTER.format(date));
            }
            List<Employee> employeeList = new ArrayList<>(currentSheet.getLastRowNum() - 2);
            nameToEmployeeMap = new HashMap<>(currentSheet.getLastRowNum() - 2);
            long id = 0L;
            while (nextRow()) {
                Employee employee = new Employee();
                employee.setId(id++);
                employee.setName(nextStringCell().getStringCellValue());
                if (!VALID_NAME_PATTERN.matcher(employee.getName()).matches()) {
                    throw new IllegalStateException(currentPosition() + ": The employee name (" + employee.getName()
                            + ") must match to the regular expression (" + VALID_NAME_PATTERN + ").");
                }
                String homeAirportCode = nextStringCell().getStringCellValue();
                Airport homeAirport = airportMap.get(homeAirportCode);
                if (homeAirport == null) {
                    throw new IllegalStateException(currentPosition()
                            + ": The employee (" + employee.getName()
                            + ")'s homeAirport (" + homeAirportCode
                            + ") does not exist in the airports (" + airportMap.keySet()
                            + ") of the other sheet (Airports).");
                }
                employee.setHomeAirport(homeAirport);
                String[] skillNames = nextStringCell().getStringCellValue().split(", ");
                Set<Skill> skillSet = new LinkedHashSet<>(skillNames.length);
                for (String skillName : skillNames) {
                    Skill skill = skillMap.get(skillName);
                    if (skill == null) {
                        throw new IllegalStateException(currentPosition()
                                + ": The employee (" + employee + ")'s skill (" + skillName
                                + ") does not exist in the skills (" + skillMap.keySet()
                                + ") of the other sheet (Skills).");
                    }
                    skillSet.add(skill);
                }
                employee.setSkillSet(skillSet);
                Set<LocalDate> unavailableDaySet = new LinkedHashSet<>();
                for (LocalDate date = firstDate; date.compareTo(lastDate) <= 0; date = date.plusDays(1)) {
                    XSSFCell cell = nextStringCell();
                    if (Objects.equals(extractColor(cell, UNAVAILABLE_COLOR), UNAVAILABLE_COLOR)) {
                        unavailableDaySet.add(date);
                    }
                    if (!cell.getStringCellValue().isEmpty()) {
                        throw new IllegalStateException(currentPosition() + ": The cell (" + cell.getStringCellValue()
                                + ") should be empty.");
                    }
                }
                employee.setUnavailableDaySet(unavailableDaySet);
                employee.setFlightAssignmentSet(new TreeSet<>());
                nameToEmployeeMap.put(employee.getName(), employee);
                employeeList.add(employee);
            }
            solution.setEmployeeList(employeeList);
        }

        private void readFlightListAndFlightAssignmentList() {
            nextSheet("Flights");
            nextRow(false);
            readHeaderCell("Flight number");
            readHeaderCell("Departure airport code");
            readHeaderCell("Departure UTC date time");
            readHeaderCell("Arrival airport code");
            readHeaderCell("Arrival UTC date time");
            readHeaderCell("Employee skill requirements");
            readHeaderCell("Employee assignments");
            List<Flight> flightList = new ArrayList<>(currentSheet.getLastRowNum() - 1);
            List<FlightAssignment> flightAssignmentList = new ArrayList<>((currentSheet.getLastRowNum() - 1) * 5);
            long id = 0L;
            long flightAssignmentId = 0L;
            while (nextRow()) {
                Flight flight = new Flight();
                flight.setId(id++);
                flight.setFlightNumber(nextStringCell().getStringCellValue());
                String departureAirportCode = nextStringCell().getStringCellValue();
                Airport departureAirport = airportMap.get(departureAirportCode);
                if (departureAirport == null) {
                    throw new IllegalStateException(currentPosition()
                            + ": The flight (" + flight.getFlightNumber()
                            + ")'s departureAirport (" + departureAirportCode
                            + ") does not exist in the airports (" + airportMap.keySet()
                            + ") of the other sheet (Airports).");
                }
                flight.setDepartureAirport(departureAirport);
                flight.setDepartureUTCDateTime(LocalDateTime.parse(nextStringCell().getStringCellValue(), DATE_TIME_FORMATTER));
                String arrivalAirportCode = nextStringCell().getStringCellValue();
                Airport arrivalAirport = airportMap.get(arrivalAirportCode);
                if (arrivalAirport == null) {
                    throw new IllegalStateException(currentPosition()
                            + ": The flight (" + flight.getFlightNumber()
                            + ")'s arrivalAirport (" + arrivalAirportCode
                            + ") does not exist in the airports (" + airportMap.keySet()
                            + ") of the other sheet (Airports).");
                }
                flight.setArrivalAirport(arrivalAirport);
                flight.setArrivalUTCDateTime(LocalDateTime.parse(nextStringCell().getStringCellValue(), DATE_TIME_FORMATTER));

                String[] skillNames = nextStringCell().getStringCellValue().split(", ");
                String[] employeeNames = nextStringCell().getStringCellValue().split(", ");
                for (int i = 0; i < skillNames.length; i++) {
                    FlightAssignment flightAssignment = new FlightAssignment();
                    flightAssignment.setId(flightAssignmentId++);
                    flightAssignment.setFlight(flight);
                    flightAssignment.setIndexInFlight(i);
                    Skill requiredSkill = skillMap.get(skillNames[i]);
                    if (requiredSkill == null) {
                        throw new IllegalStateException(currentPosition()
                                + ": The flight (" + flight.getFlightNumber()
                                + ")'s requiredSkill (" + requiredSkill
                                + ") does not exist in the skills (" + skillMap.keySet()
                                + ") of the other sheet (Skills).");
                    }
                    flightAssignment.setRequiredSkill(requiredSkill);
                    if (employeeNames.length > i && !employeeNames[i].isEmpty()) {
                        Employee employee = nameToEmployeeMap.get(employeeNames[i]);
                        if (employee == null) {
                            throw new IllegalStateException(currentPosition()
                                    + ": The flight (" + flight.getFlightNumber()
                                    + ")'s employeeAssignment's name (" + employeeNames[i]
                                    + ") does not exist in the employees (" + nameToEmployeeMap.keySet()
                                    + ") of the other sheet (Employees).");
                        }
                        flightAssignment.setEmployee(employee);
                    }
                    flightAssignmentList.add(flightAssignment);
                }
                flightList.add(flight);
            }
            solution.setFlightList(flightList);
            solution.setFlightAssignmentList(flightAssignmentList);
        }
    }

    private static class FlightCrewSchedulingXlsxWriter extends AbstractXlsxWriter<FlightCrewSolution> {

        private static final Comparator<FlightAssignment> COMPARATOR =
                Comparator.<FlightAssignment, LocalDateTime>comparing(a -> a.getFlight().getDepartureUTCDateTime())
                        .thenComparing(a -> a.getFlight().getArrivalUTCDateTime())
                        .thenComparingLong(FlightAssignment::getId);

        public FlightCrewSchedulingXlsxWriter(FlightCrewSolution solution) {
            super(solution, FlightCrewSchedulingApp.SOLVER_CONFIG);
        }

        @Override
        public Workbook write() {
            writeSetup();
            writeConfiguration();
            writeSkillList();
            writeAirportList();
            writeTaxiTimeMaps();
            writeEmployeeList();
            writeFlightListAndFlightAssignmentList();
            writeEmployeesView();
            writeScoreView(justificationList -> justificationList.stream()
                    .filter(o -> o instanceof FlightAssignment).map(o -> ((FlightAssignment) o).getFlight().toString())
                    .collect(joining(", ")));
            return workbook;
        }

        private void writeConfiguration() {
            nextSheet("Configuration", 1, 4, false);
            nextRow();
            nextHeaderCell("Schedule start UTC Date");
            nextCell().setCellValue(DAY_FORMATTER.format(solution.getScheduleFirstUTCDate()));
            nextRow();
            nextHeaderCell("Schedule end UTC Date");
            nextCell().setCellValue(DAY_FORMATTER.format(solution.getScheduleLastUTCDate()));
            nextRow();
            nextRow();
            nextHeaderCell("Constraint");
            nextHeaderCell("Weight");
            nextHeaderCell("Description");
            FlightCrewParametrization parametrization = solution.getParametrization();

            writeLongConstraintParameterLine(LOAD_BALANCE_FLIGHT_DURATION_TOTAL_PER_EMPLOYEE,
                    parametrization::getLoadBalanceFlightDurationTotalPerEmployee,
                    "Soft penalty per 0.001 minute difference with the average flight duration total per employee.");
            nextRow();
            writeIntConstraintParameterLine(REQUIRED_SKILL, null,
                    "Hard penalty per missing required skill for a flight assignment");
            writeIntConstraintParameterLine(FLIGHT_CONFLICT, null,
                    "Hard penalty per 2 flights of an employee that directly overlap");
            writeIntConstraintParameterLine(TRANSFER_BETWEEN_TWO_FLIGHTS, null,
                    "Hard penalty per 2 sequential flights of an employee with no viable transfer from the arrival airport to the departure airport");
            writeIntConstraintParameterLine(EMPLOYEE_UNAVAILABILITY, null,
                    "Hard penalty per flight assignment to an employee that is unavailable");
            autoSizeColumnsWithHeader();
        }

        private void writeSkillList() {
            nextSheet("Skills", 1, 1, false);
            nextRow();
            nextHeaderCell("Name");
            for (Skill skill : solution.getSkillList()) {
                nextRow();
                nextCell().setCellValue(skill.getName());
            }
            autoSizeColumnsWithHeader();
        }

        private void writeAirportList() {
            nextSheet("Airports", 1, 1, false);
            nextRow();
            nextHeaderCell("Code");
            nextHeaderCell("Name");
            nextHeaderCell("Latitude");
            nextHeaderCell("Longitude");
            for (Airport airport : solution.getAirportList()) {
                nextRow();
                nextCell().setCellValue(airport.getCode());
                nextCell().setCellValue(airport.getName());
                nextCell().setCellValue(airport.getLatitude());
                nextCell().setCellValue(airport.getLongitude());
            }
            autoSizeColumnsWithHeader();
        }

        private void writeTaxiTimeMaps() {
            nextSheet("Taxi time", 1, 1, false);
            nextRow();
            nextHeaderCell("Driving time in minutes by taxi between two nearby airports to allow employees to start from a different airport.");
            currentSheet.addMergedRegion(new CellRangeAddress(currentRowNumber, currentRowNumber,
                    currentColumnNumber, currentColumnNumber + 20));
            List<Airport> airportList = solution.getAirportList();
            nextRow();
            nextHeaderCell("Airport code");
            for (Airport airport : airportList) {
                nextHeaderCell(airport.getCode());
            }
            for (Airport a : airportList) {
                nextRow();
                nextHeaderCell(a.getCode());
                for (Airport b : airportList) {
                    Long taxiTime = a.getTaxiTimeInMinutesTo(b);
                    if (taxiTime == null) {
                        nextCell();
                    } else {
                        nextCell().setCellValue(taxiTime);
                    }
                }
            }
            autoSizeColumnsWithHeader();
        }

        private void writeEmployeeList() {
            nextSheet("Employees", 1, 2, false);
            nextRow();
            nextHeaderCell("");
            nextHeaderCell("");
            nextHeaderCell("");
            nextHeaderCell("Unavailability");
            nextRow();
            nextHeaderCell("Name");
            nextHeaderCell("Home airport");
            nextHeaderCell("Skills");
            LocalDate firstDate = solution.getScheduleFirstUTCDate();
            LocalDate lastDate = solution.getScheduleLastUTCDate();
            for (LocalDate date = firstDate; date.compareTo(lastDate) <= 0; date = date.plusDays(1)) {
                nextHeaderCell(DAY_FORMATTER.format(date));
            }
            for (Employee employee : solution.getEmployeeList()) {
                nextRow();
                nextCell().setCellValue(employee.getName());
                nextCell().setCellValue(employee.getHomeAirport().getCode());
                nextCell().setCellValue(String.join(", ", employee.getSkillSet().stream().map(Skill::getName).collect(toList())));
                for (LocalDate date = firstDate; date.compareTo(lastDate) <= 0; date = date.plusDays(1)) {
                    nextCell(employee.getUnavailableDaySet().contains(date) ? unavailableStyle : defaultStyle)
                            .setCellValue("");
                }
            }
            autoSizeColumnsWithHeader();
        }

        private void writeFlightListAndFlightAssignmentList() {
            nextSheet("Flights", 1, 1, false);
            nextRow();
            nextHeaderCell("Flight number");
            nextHeaderCell("Departure airport code");
            nextHeaderCell("Departure UTC date time");
            nextHeaderCell("Arrival airport code");
            nextHeaderCell("Arrival UTC date time");
            nextHeaderCell("Employee skill requirements");
            nextHeaderCell("Employee assignments");
            Map<Flight, List<FlightAssignment>> flightToFlightAssignmentMap = solution.getFlightAssignmentList()
                    .stream().collect(groupingBy(FlightAssignment::getFlight, toList()));
            for (Flight flight : solution.getFlightList()) {
                nextRow();
                nextCell().setCellValue(flight.getFlightNumber());
                nextCell().setCellValue(flight.getDepartureAirport().getCode());
                nextCell().setCellValue(DATE_TIME_FORMATTER.format(flight.getDepartureUTCDateTime()));
                nextCell().setCellValue(flight.getArrivalAirport().getCode());
                nextCell().setCellValue(DATE_TIME_FORMATTER.format(flight.getArrivalUTCDateTime()));
                List<FlightAssignment> flightAssignmentList = flightToFlightAssignmentMap.get(flight);
                nextCell().setCellValue(flightAssignmentList.stream()
                        .map(FlightAssignment::getRequiredSkill).map(Skill::getName)
                        .collect(joining(", ")));
                nextCell().setCellValue(flightAssignmentList.stream()
                        .map(FlightAssignment::getEmployee)
                        .map(employee -> employee == null ? "" : employee.getName())
                        .collect(joining(", ")));
            }
            autoSizeColumnsWithHeader();
        }

        private void writeEmployeesView() {
            nextSheet("Employees view", 2, 2, true);
            int minimumHour = solution.getFlightList().stream()
                    .map(Flight::getDepartureUTCTime).map(LocalTime::getHour)
                    .min(Comparator.naturalOrder()).orElse(9);
            int maximumHour = solution.getFlightList().stream()
                    .map(Flight::getArrivalUTCTime).map(LocalTime::getHour)
                    .max(Comparator.naturalOrder()).orElse(17);
            nextRow();
            nextHeaderCell("");
            nextHeaderCell("");
            LocalDate firstDate = solution.getScheduleFirstUTCDate();
            LocalDate lastDate = solution.getScheduleLastUTCDate();
            for (LocalDate date = firstDate; date.compareTo(lastDate) <= 0; date = date.plusDays(1)) {
                nextHeaderCell(DAY_FORMATTER.format(date));
                currentSheet.addMergedRegion(new CellRangeAddress(currentRowNumber, currentRowNumber,
                        currentColumnNumber, currentColumnNumber + (maximumHour - minimumHour)));
                currentColumnNumber += (maximumHour - minimumHour);
            }
            nextRow();
            nextHeaderCell("Employee name");
            nextHeaderCell("Home airport");
            for (LocalDate date = firstDate; date.compareTo(lastDate) <= 0; date = date.plusDays(1)) {
                for (int hour = minimumHour; hour <= maximumHour; hour++) {
                    nextHeaderCell(TIME_FORMATTER.format(LocalTime.of(hour, 0)));
                }
            }
            Map<Employee, List<FlightAssignment>> employeeToFlightAssignmentMap = solution.getFlightAssignmentList()
                    .stream().filter(flightAssignment -> flightAssignment.getEmployee() != null)
                    .collect(groupingBy(FlightAssignment::getEmployee, toList()));
            for (Employee employee : solution.getEmployeeList()) {
                nextRow();
                nextHeaderCell(employee.getName());
                nextHeaderCell(employee.getHomeAirport().getCode());
                List<FlightAssignment> employeeAssignmentList = employeeToFlightAssignmentMap.get(employee);
                if (employeeAssignmentList != null) {
                    employeeAssignmentList.sort(COMPARATOR);
                    for (LocalDate date = firstDate; date.compareTo(lastDate) <= 0; date = date.plusDays(1)) {
                        boolean unavailable = employee.getUnavailableDaySet().contains(date);
                        Map<Integer, List<FlightAssignment>> hourToAssignmentListMap = extractHourToAssignmentListMap(employeeAssignmentList, date);
                        for (int departureHour = minimumHour; departureHour <= maximumHour; departureHour++) {
                            List<FlightAssignment> flightAssignmentList = hourToAssignmentListMap.get(departureHour);
                            if (flightAssignmentList != null && !flightAssignmentList.isEmpty()) {
                                nextCell(unavailable ? unavailableStyle : defaultStyle).setCellValue(flightAssignmentList.stream()
                                        .map(FlightAssignment::getFlight)
                                        .map(flight -> flight.getDepartureAirport().getCode()
                                                + MILITARY_TIME_FORMATTER.format(flight.getDepartureUTCTime())
                                                + "→"
                                                + flight.getArrivalAirport().getCode()
                                                + MILITARY_TIME_FORMATTER.format(flight.getArrivalUTCTime()))
                                        .collect(joining(", ")));
                                int maxArrivalHour = flightAssignmentList.stream().map(a -> a.getFlight().getArrivalUTCTime().getHour())
                                        .max(Comparator.naturalOrder()).get();
                                int stretch = maxArrivalHour - departureHour;
                                currentSheet.addMergedRegion(new CellRangeAddress(currentRowNumber, currentRowNumber,
                                        currentColumnNumber, currentColumnNumber + stretch));
                                currentColumnNumber += stretch;
                                departureHour += stretch;
                            } else {
                                nextCell(unavailable ? unavailableStyle : defaultStyle);
                            }
                        }
                    }
                }
            }
            setSizeColumnsWithHeader(1500);
            currentSheet.autoSizeColumn(0);
            currentSheet.autoSizeColumn(1);
        }

        private Map<Integer, List<FlightAssignment>> extractHourToAssignmentListMap(
                List<FlightAssignment> employeeAssignmentList, LocalDate date) {
            Map<Integer, List<FlightAssignment>> hourToAssignmentListMap = new HashMap<>(
                    employeeAssignmentList.size());
            int previousArrivalHour = -1;
            List<FlightAssignment> previousFlightAssignmentList = null;
            for (FlightAssignment flightAssignment : employeeAssignmentList) {
                Flight flight = flightAssignment.getFlight();
                if (flight.getDepartureUTCDate().equals(date)) {
                    int departureHour = flight.getDepartureUTCTime().getHour();
                    int arrivalHour = flight.getArrivalUTCTime().getHour();
                    if (previousArrivalHour < departureHour) {
                        previousFlightAssignmentList = new ArrayList<>(24);
                        hourToAssignmentListMap.put(departureHour, previousFlightAssignmentList);
                        previousArrivalHour = arrivalHour;
                    } else {
                        previousArrivalHour = Math.max(previousArrivalHour, arrivalHour);
                    }
                    // The list is never null; gets initialized in the first loop due to previousArrivalHour = -1
                    previousFlightAssignmentList.add(flightAssignment);
                }
            }
            return hourToAssignmentListMap;
        }
    }
}
