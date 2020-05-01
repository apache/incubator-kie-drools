/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import static org.optaplanner.examples.common.persistence.generator.ProbabilisticDataGenerator.extractRandomElement;
import static org.optaplanner.examples.common.persistence.generator.ProbabilisticDataGenerator.generateRandomIntFromThresholds;

import java.io.File;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Random;
import java.util.TreeSet;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.LoggingMain;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.generator.LocationDataGenerator;
import org.optaplanner.examples.rocktour.app.RockTourApp;
import org.optaplanner.examples.rocktour.domain.RockBus;
import org.optaplanner.examples.rocktour.domain.RockLocation;
import org.optaplanner.examples.rocktour.domain.RockShow;
import org.optaplanner.examples.rocktour.domain.RockTourConstraintConfiguration;
import org.optaplanner.examples.rocktour.domain.RockTourSolution;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class RockTourGenerator extends LoggingMain {

    public static void main(String[] args) {
        RockTourGenerator generator = new RockTourGenerator();
        generator.writeSolution(LocationDataGenerator.US_MAINLAND_STATE_CAPITALS);
    }

    protected final SolutionFileIO<RockTourSolution> solutionFileIO;
    protected final File outputDir;
    protected Random random;

    protected static final LocalDate START_DATE = LocalDate.of(2018, 2, 1);
    protected static final LocalDate END_DATE = LocalDate.of(2018, 12, 1);

    public RockTourGenerator() {
        solutionFileIO = new RockTourXlsxFileIO();
        outputDir = new File(CommonApp.determineDataDir(RockTourApp.DATA_DIR_NAME), "unsolved");
    }

    private void writeSolution(List<LocationDataGenerator.LocationData> locationDataList) {
        String fileName = (locationDataList.size() - 1) + "shows";
        File outputFile = new File(outputDir, fileName + "." + solutionFileIO.getOutputFileExtension());
        RockTourSolution solution = createRockTourSolution(fileName, locationDataList);
        solutionFileIO.write(solution, outputFile);
    }

    public RockTourSolution createRockTourSolution(String fileName,
            List<LocationDataGenerator.LocationData> locationDataList) {
        random = new Random(37);
        RockTourSolution solution = new RockTourSolution();
        solution.setId(0L);
        solution.setTourName(fileName);
        RockTourConstraintConfiguration constraintConfiguration = new RockTourConstraintConfiguration();
        constraintConfiguration.setId(0L);
        solution.setConstraintConfiguration(constraintConfiguration);

        createShowList(solution, locationDataList);

        BigInteger possibleSolutionSize = AbstractSolutionImporter.factorial(solution.getShowList().size());
        logger.info("Rock tour {} has {} shows with a search space of {}.",
                fileName,
                solution.getShowList().size(),
                AbstractSolutionImporter.getFlooredPossibleSolutionSize(possibleSolutionSize));
        return solution;
    }

    private void createShowList(RockTourSolution solution, List<LocationDataGenerator.LocationData> locationDataList) {
        List<LocalDate> globalAvailableDayList = new ArrayList<>();
        for (LocalDate date = START_DATE; date.compareTo(END_DATE) < 0; date = date.plusDays(1)) {
            if (date.getDayOfWeek() != DayOfWeek.SUNDAY) {
                globalAvailableDayList.add(date);
            }
        }
        int locationDataListSize = locationDataList.size();
        List<RockShow> showList = new ArrayList<>(locationDataListSize);
        long showId = 0L;
        List<RockLocation> locationList = new ArrayList<>(locationDataListSize);
        for (int i = 0; i < locationDataListSize; i++) {
            LocationDataGenerator.LocationData locationData = locationDataList.get(i);
            RockLocation location = new RockLocation(locationData.getName(), locationData.getLatitude(),
                    locationData.getLongitude());
            locationList.add(location);
            if (i == 0) {
                RockBus bus = new RockBus();
                bus.setId((long) i);
                bus.setStartLocation(location);
                bus.setStartDate(START_DATE);
                bus.setEndLocation(location);
                bus.setEndDate(END_DATE);
                solution.setBus(bus);
            } else {
                RockShow show = new RockShow();
                show.setId(showId++);
                show.setVenueName(locationData.getName());
                show.setLocation(location);
                show.setDurationInHalfDay(generateRandomIntFromThresholds(random, 0.0, 0.5, 0.9, 0.9));
                show.setRevenueOpportunity((random.nextInt(30) + 1) * 100_000);
                show.setRequired(i <= 3);
                NavigableSet<LocalDate> availableDaySet;
                if (i <= 8) {
                    availableDaySet = new TreeSet<>();
                    availableDaySet.add(extractRandomElement(random, globalAvailableDayList));
                } else {
                    int fromIndex = globalAvailableDayList.size() * 4 / 5;
                    Collections.shuffle(globalAvailableDayList, random);
                    availableDaySet = new TreeSet<>(globalAvailableDayList.subList(
                            0, fromIndex + random.nextInt(globalAvailableDayList.size() - fromIndex)));
                }
                show.setAvailableDateSet(availableDaySet);
                showList.add(show);
            }
        }
        for (int i = 0; i < locationList.size(); i++) {
            RockLocation fromLocation = locationList.get(i);
            Map<RockLocation, Long> drivingSecondsMap = new LinkedHashMap<>(locationList.size());
            for (int j = 0; j < locationList.size(); j++) {
                RockLocation toLocation = locationList.get(j);
                long divingSeconds = fromLocation == toLocation ? 0L : random.nextInt(1000); // TODO not random
                drivingSecondsMap.put(toLocation, divingSeconds);
            }
            fromLocation.setDrivingSecondsMap(drivingSecondsMap);
        }
        solution.setShowList(showList);
    }

}
