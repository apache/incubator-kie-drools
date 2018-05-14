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

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.LoggingMain;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.generator.LocationDataGenerator;
import org.optaplanner.examples.rocktour.app.RockTourApp;
import org.optaplanner.examples.rocktour.domain.RockBus;
import org.optaplanner.examples.rocktour.domain.RockLocation;
import org.optaplanner.examples.rocktour.domain.RockShow;
import org.optaplanner.examples.rocktour.domain.RockTourParametrization;
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

    public RockTourGenerator() {
        solutionFileIO = new RockTourXslxFileIO();
        outputDir = new File(CommonApp.determineDataDir(RockTourApp.DATA_DIR_NAME), "unsolved");
    }

    private void writeSolution(LocationDataGenerator.LocationData[] locationDataArray) {
        String fileName = locationDataArray.length + "shows";
        File outputFile = new File(outputDir, fileName + "." + solutionFileIO.getOutputFileExtension());
        RockTourSolution solution = createRockTourSolution(fileName, locationDataArray);
        solutionFileIO.write(solution, outputFile);
    }

    public RockTourSolution createRockTourSolution(String fileName, LocationDataGenerator.LocationData[] locationDataArray) {
        random = new Random(37);
        RockTourSolution solution = new RockTourSolution();
        solution.setId(0L);
        solution.setTourName(fileName);
        RockTourParametrization parametrization = new RockTourParametrization();
        parametrization.setId(0L);
        solution.setParametrization(parametrization);

        createShowList(solution, locationDataArray);

        BigInteger possibleSolutionSize = AbstractSolutionImporter.factorial(solution.getShowList().size());
        logger.info("Rock tour {} has {} shows with a search space of {}.",
                fileName,
                solution.getShowList().size(),
                AbstractSolutionImporter.getFlooredPossibleSolutionSize(possibleSolutionSize));
        return solution;
    }

    private void createShowList(RockTourSolution solution, LocationDataGenerator.LocationData[] locationDataArray) {
        List<RockShow> showList = new ArrayList<>(locationDataArray.length);
        long showId = 0L;
        for (int i = 0; i < locationDataArray.length; i++) {
            LocationDataGenerator.LocationData locationData = locationDataArray[i];
            RockLocation location = new RockLocation(locationData.getName(), locationData.getLatitude(), locationData.getLongitude());
            if (i == 0) {
                RockBus bus = new RockBus();
                bus.setId((long) i);
                bus.setStartLocation(location);
                bus.setEndLocation(location);
                solution.setBus(bus);
            } else {
                RockShow show = new RockShow();
                show.setId(showId++);
                show.setVenueName(locationData.getName());
                show.setLocation(location);
                show.setRevenueOpportunity((random.nextInt(30) + 1) * 100_000);
                show.setRequired(i <= 3);
                showList.add(show);
            }
        }
        solution.setShowList(showList);
    }

}
