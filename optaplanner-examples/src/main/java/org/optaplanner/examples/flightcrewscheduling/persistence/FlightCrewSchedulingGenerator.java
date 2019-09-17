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

import java.io.File;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.LoggingMain;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.generator.LocationDataGenerator;
import org.optaplanner.examples.common.persistence.generator.StringDataGenerator;
import org.optaplanner.examples.flightcrewscheduling.app.FlightCrewSchedulingApp;
import org.optaplanner.examples.flightcrewscheduling.domain.Airport;
import org.optaplanner.examples.flightcrewscheduling.domain.Employee;
import org.optaplanner.examples.flightcrewscheduling.domain.Flight;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightAssignment;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightCrewParametrization;
import org.optaplanner.examples.flightcrewscheduling.domain.FlightCrewSolution;
import org.optaplanner.examples.flightcrewscheduling.domain.Skill;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

import static java.time.temporal.ChronoUnit.*;
import static org.optaplanner.examples.common.persistence.generator.ProbabilisticDataGenerator.*;

public class FlightCrewSchedulingGenerator extends LoggingMain {

    private static final double TAXI_KM_THRESHOLD = 500.0;
    private static final double TAXI_SPEED_IN_KM_PER_MINUTE = 90.0 / 60.0;
    private static final double PLANE_SPEED_IN_KM_PER_MINUTE = 1000.0 / 60.0;
    private static final double PLANE_TAKE_OFF_AND_LANDING_MINUTES = 90.0;

    private static final int START_MINUTE_OF_DAY = 6 * 60;
    private static final int END_MINUTE_OF_DAY = 22 * 60;

    private static final int PILOT_COUNT_PER_FLIGHT = 2;
    private static final int FLIGHT_ATTENDANT_COUNT_PER_FLIGHT = 3;
    private static final int EMPLOYEE_COUNT_PER_FLIGHT = PILOT_COUNT_PER_FLIGHT + FLIGHT_ATTENDANT_COUNT_PER_FLIGHT;

    public static void main(String[] args) {
        FlightCrewSchedulingGenerator generator = new FlightCrewSchedulingGenerator();
        generator.writeFlightCrewSolution("Europe", LocationDataGenerator.EUROPE_BUSIEST_AIRPORTS, 10, 7);
        generator.writeFlightCrewSolution("Europe", LocationDataGenerator.EUROPE_BUSIEST_AIRPORTS, 10, 4 * 7);
        generator.writeFlightCrewSolution("Europe", LocationDataGenerator.EUROPE_BUSIEST_AIRPORTS, 50, 7);
        generator.writeFlightCrewSolution("US", LocationDataGenerator.US_MAINLAND_STATE_CAPITALS, 10, 7);
    }

    private final StringDataGenerator employeeNameGenerator = StringDataGenerator.buildFullNames();

    protected final SolutionFileIO<FlightCrewSolution> solutionFileIO;
    protected final File outputDir;

    protected Skill pilotSkill;
    protected Skill flightAttendantSkill;
    protected List<Airport> homeAirportList;

    protected Random random;

    public FlightCrewSchedulingGenerator() {
        solutionFileIO = new FlightCrewSchedulingXlsxFileIO();
        outputDir = new File(CommonApp.determineDataDir(FlightCrewSchedulingApp.DATA_DIR_NAME), "unsolved");
    }

    private void writeFlightCrewSolution(String locationDataName,
            LocationDataGenerator.LocationData[] locationDataArray, int flightRoundTripsPerDay, int dayCount) {
        int flightListSize = (flightRoundTripsPerDay * 5 / 2) * dayCount;
        String fileName = flightListSize + "flights-" + dayCount + "days-" + locationDataName;
        File outputFile = new File(outputDir, fileName + "." + solutionFileIO.getOutputFileExtension());
        FlightCrewSolution solution = createFlightCrewSolution(
                fileName, locationDataArray, flightRoundTripsPerDay, dayCount);
        solutionFileIO.write(solution, outputFile);
    }

    public FlightCrewSolution createFlightCrewSolution(String fileName,
            LocationDataGenerator.LocationData[] locationDataArray, int flightRoundTripsPerDay, int dayCount) {
        random = new Random(37);
        FlightCrewSolution solution = new FlightCrewSolution();
        solution.setId(0L);
        LocalDate firstDate = LocalDate.of(2018, 1, 1);
        solution.setScheduleFirstUTCDate(firstDate);
        solution.setScheduleLastUTCDate(firstDate.plusDays(dayCount - 1));
        FlightCrewParametrization parametrization = new FlightCrewParametrization();
        parametrization.setId(0L);
        solution.setParametrization(parametrization);

        createSkillList(solution);
        createAirportList(solution, locationDataArray);
        createFlightList(solution, flightRoundTripsPerDay, dayCount);
        createFlightAssignmentList(solution);
        createEmployeeList(solution, flightRoundTripsPerDay, dayCount);

        int employeeListSize = solution.getEmployeeList().size();
        int flightAssignmentListSize = solution.getFlightAssignmentList().size();
        BigInteger possibleSolutionSize = BigInteger.valueOf((long) employeeListSize).pow(flightAssignmentListSize);
        logger.info("FlightCrew {} has {} skills, {} airports, {} employees, {} flights and {} flight assignments with a search space of {}.",
                fileName,
                solution.getSkillList().size(),
                solution.getAirportList().size(),
                solution.getEmployeeList().size(),
                solution.getFlightList().size(),
                flightAssignmentListSize,
                AbstractSolutionImporter.getFlooredPossibleSolutionSize(possibleSolutionSize));
        return solution;
    }

    private void createSkillList(FlightCrewSolution solution) {
        List<Skill> skillList = new ArrayList<>(2);
        pilotSkill = new Skill();
        pilotSkill.setId(0L);
        pilotSkill.setName("Pilot");
        skillList.add(pilotSkill);
        flightAttendantSkill = new Skill();
        flightAttendantSkill.setId(1L);
        flightAttendantSkill.setName("Flight attendant");
        skillList.add(flightAttendantSkill);
        solution.setSkillList(skillList);
    }

    private void createAirportList(FlightCrewSolution solution, LocationDataGenerator.LocationData[] locationDataArray) {
        List<Airport> airportList = new ArrayList<>(locationDataArray.length);
        long id = 0L;
        for (LocationDataGenerator.LocationData locationData : locationDataArray) {
            Airport airport = new Airport();
            airport.setId(id);
            id++;
            airport.setCode(locationData.getName().replaceAll("\\,.*", ""));
            airport.setName(locationData.getName());
            airport.setLatitude(locationData.getLatitude());
            airport.setLongitude(locationData.getLongitude());
            logger.trace("Created airport ({}).", airport);
            airportList.add(airport);
        }
        for (Airport a : airportList) {
            Map<Airport, Long> taxiTimeInMinutesMap = new LinkedHashMap<>(airportList.size());
            for (Airport b : airportList) {
                double distanceInKm = a.getHaversineDistanceInKmTo(b);
                if (distanceInKm < TAXI_KM_THRESHOLD) {
                    taxiTimeInMinutesMap.put(b, (long) (distanceInKm / TAXI_SPEED_IN_KM_PER_MINUTE));
                }
            }
            a.setTaxiTimeInMinutesMap(taxiTimeInMinutesMap);
        }
        solution.setAirportList(airportList);
    }

    private void createFlightList(FlightCrewSolution solution, int flightRoundTripsPerDay, int dayCount) {
        int flightListSize = flightRoundTripsPerDay * dayCount;
        List<Flight> flightList = new ArrayList<>(flightListSize);
        List<Airport> airportList = solution.getAirportList();
        Airport centerAirport = airportList.get(0);
        int homeAirportListSize = Math.min(airportList.size() / 10, flightRoundTripsPerDay / 5);
        homeAirportList = airportList.stream()
                .sorted(Comparator.comparingDouble(centerAirport::getHaversineDistanceInKmTo))
                .limit(homeAirportListSize)
                .collect(Collectors.toList());
        LocalDate firstDate = solution.getScheduleFirstUTCDate();
        LocalDate lastDate = solution.getScheduleLastUTCDate();
        int flightNumberSuffix = 1;
        long flightId = 0L;
        for (int i = 0; i < flightRoundTripsPerDay; i++) {
            int flightCount = i % 2 == 0 ? 2 : 3;
            ArrayList<Airport> selectedAirports = new ArrayList<>(flightCount);
            Airport firstAirport = extractRandomElement(random, homeAirportList);
            selectedAirports.add(firstAirport);
            ArrayList<Airport> nonFirstAirports = new ArrayList<>(airportList);
            nonFirstAirports.remove(firstAirport);
            Collections.shuffle(nonFirstAirports, random);
            selectedAirports.addAll(nonFirstAirports.subList(0, flightCount - 1));
            for (int j = 0; j < flightCount; j++) {
                String flightNumber = "AB" + String.format("%03d", flightNumberSuffix);
                flightNumberSuffix++;
                Airport departureAirport = selectedAirports.get(j);
                Airport arrivalAirport = selectedAirports.get((j + 1) % flightCount);
                int flyingTime = (int) ((departureAirport.getHaversineDistanceInKmTo(arrivalAirport)
                        / PLANE_SPEED_IN_KM_PER_MINUTE) + PLANE_TAKE_OFF_AND_LANDING_MINUTES);
                int departureMinute = START_MINUTE_OF_DAY + random.nextInt(
                        END_MINUTE_OF_DAY - flyingTime - START_MINUTE_OF_DAY + 1);
                int arrivalMinute = departureMinute + flyingTime;
                for (LocalDate date = firstDate; date.compareTo(lastDate) <= 0; date = date.plusDays(1)) {
                    Flight flight = new Flight();
                    flight.setId((long) flightId);
                    flightId++;
                    flight.setFlightNumber(flightNumber);
                    flight.setDepartureAirport(departureAirport);
                    flight.setDepartureUTCDateTime(date.atTime(departureMinute / 60, departureMinute % 60));
                    flight.setArrivalAirport(arrivalAirport);
                    flight.setArrivalUTCDateTime(date.atTime(arrivalMinute / 60, arrivalMinute % 60));
                    logger.trace("Created flight ({}).", flight);
                    flightList.add(flight);
                }
            }
        }
        flightList.sort(Flight::compareTo);
        solution.setFlightList(flightList);
    }

    private void createFlightAssignmentList(FlightCrewSolution solution) {
        List<Flight> flightList = solution.getFlightList();
        List<FlightAssignment> flightAssignmentList = new ArrayList<>(flightList.size() * EMPLOYEE_COUNT_PER_FLIGHT);
        long flightAssignmentId = 0L;
        for (Flight flight : flightList) {
            for (int indexInFlight = 0; indexInFlight < EMPLOYEE_COUNT_PER_FLIGHT; indexInFlight++) {
                FlightAssignment flightAssignment = new FlightAssignment();
                flightAssignment.setId((long) flightAssignmentId);
                flightAssignmentId++;
                flightAssignment.setFlight(flight);
                flightAssignment.setIndexInFlight(indexInFlight);
                Skill requiredSkill = indexInFlight < PILOT_COUNT_PER_FLIGHT ? pilotSkill : flightAttendantSkill;
                flightAssignment.setRequiredSkill(requiredSkill);
                flightAssignmentList.add(flightAssignment);
            }
        }
        solution.setFlightAssignmentList(flightAssignmentList);
    }


    private void createEmployeeList(FlightCrewSolution solution, int flightRoundTripsPerDay, int dayCount) {
        int employeeListSize = flightRoundTripsPerDay * EMPLOYEE_COUNT_PER_FLIGHT * 3;
        List<Employee> employeeList = new ArrayList<>(employeeListSize);
        employeeNameGenerator.predictMaximumSizeAndReset(employeeListSize);
        LocalDate firstDate = solution.getScheduleFirstUTCDate();
        LocalDate lastDate = solution.getScheduleLastUTCDate();
        List<LocalDate> allDateList = new ArrayList<>((int) DAYS.between(firstDate, lastDate) + 1);
        for (LocalDate date = firstDate; date.compareTo(lastDate) <= 0; date = date.plusDays(1)) {
            allDateList.add(date);
        }
        List<LocalDate> unavailableDayPool = new ArrayList<>(allDateList);
        Collections.shuffle(unavailableDayPool, random);
        long id = 0L;
        for (int i = 0; i < employeeListSize; i++) {
            Employee employee = new Employee();
            employee.setId(id);
            id++;
            employee.setName(employeeNameGenerator.generateNextValue());
            employee.setHomeAirport(extractRandomElement(random, homeAirportList));
            employee.setSkillSet(Collections.singleton((i % 5) < 2 ? pilotSkill : flightAttendantSkill));
            int unavailableDayCount = 0;
            for (int j = 0; j < dayCount && unavailableDayCount < dayCount; j++) {
                if (random.nextDouble() < (20.0 / 365.0)) {
                    unavailableDayCount++;
                }
            }
            employee.setUnavailableDaySet(generateUnavailableDaySet(unavailableDayCount, allDateList, unavailableDayPool));
            employee.setFlightAssignmentSet(new TreeSet<>());
            logger.trace("Created employee ({}).", employee);
            employeeList.add(employee);
        }
        solution.setEmployeeList(employeeList);

    }

    private Set<LocalDate> generateUnavailableDaySet(int size, List<LocalDate> allDateList, List<LocalDate> pool) {
        Set<LocalDate> unavailableDaySet = new LinkedHashSet<>(size);
        if (pool.size() < size) {
            // There are not enough left in the pool, so provision more
            unavailableDaySet.addAll(pool);
            pool.clear();
            pool.addAll(allDateList);
            Collections.shuffle(pool, random);
            // Add those that haven't been added yet
            for (Iterator<LocalDate> it = pool.iterator(); it.hasNext() && unavailableDaySet.size() < size; ) {
                LocalDate date = it.next();
                if (unavailableDaySet.add(date)) {
                    it.remove();
                }
            }
        } else {
            // There are enough left in the pool
            List<LocalDate> selection = pool.subList(0, size);
            unavailableDaySet.addAll(selection);
            selection.clear();
        }
        return unavailableDaySet;
    }
}
