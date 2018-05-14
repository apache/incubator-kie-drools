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
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.optaplanner.examples.common.persistence.AbstractXslxSolutionFileIO;
import org.optaplanner.examples.rocktour.app.RockTourApp;
import org.optaplanner.examples.rocktour.domain.RockBus;
import org.optaplanner.examples.rocktour.domain.RockLocation;
import org.optaplanner.examples.rocktour.domain.RockShow;
import org.optaplanner.examples.rocktour.domain.RockTourParametrization;
import org.optaplanner.examples.rocktour.domain.RockTourSolution;

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
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("City name");
            readHeaderCell("Latitude");
            readHeaderCell("Longitude");
            RockBus bus = new RockBus();
            bus.setId(0L);
            nextRow();
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("Bus start");
            String startCityName = nextStringCell().getStringCellValue();
            double startLatitude = nextNumericCell().getNumericCellValue();
            double startLongitude = nextNumericCell().getNumericCellValue();
            bus.setStartLocation(new RockLocation(startCityName, startLatitude, startLongitude));
            nextRow();
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("");
            readHeaderCell("Bus end");
            String endCityName = nextStringCell().getStringCellValue();
            double endLatitude = nextNumericCell().getNumericCellValue();
            double endLongitude = nextNumericCell().getNumericCellValue();
            bus.setEndLocation(new RockLocation(endCityName, endLatitude, endLongitude));
            solution.setBus(bus);

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

        private void readShowList() {
            nextSheet("Shows");
            nextRow(false);
            readHeaderCell("Venue name");
            readHeaderCell("City name");
            readHeaderCell("Latitude");
            readHeaderCell("Longitude");
            readHeaderCell("Revenue opportunity");
            readHeaderCell("Required");
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
            writeShowList();
            return workbook;
        }

        private void writeConfiguration() {
            nextSheet("Configuration", 1, 5, false);
            nextRow();
            nextHeaderCell("Tour name");
            nextCell().setCellValue(solution.getTourName());
            nextHeaderCell("");
            nextHeaderCell("");
            nextHeaderCell("City name");
            nextHeaderCell("Latitude");
            nextHeaderCell("Longitude");
            RockBus bus = solution.getBus();
            nextRow();
            nextHeaderCell("");
            nextHeaderCell("");
            nextHeaderCell("");
            nextHeaderCell("Bus start");
            nextCell().setCellValue(bus.getStartLocation().getCityName());
            nextCell().setCellValue(bus.getStartLocation().getLatitude());
            nextCell().setCellValue(bus.getStartLocation().getLongitude());
            nextRow();
            nextHeaderCell("");
            nextHeaderCell("");
            nextHeaderCell("");
            nextHeaderCell("Bus end");
            nextCell().setCellValue(bus.getEndLocation().getCityName());
            nextCell().setCellValue(bus.getEndLocation().getLatitude());
            nextCell().setCellValue(bus.getEndLocation().getLongitude());
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

        private void writeShowList() {
            nextSheet("Shows", 1, 1, false);
            nextRow();
            nextHeaderCell("Venue name");
            nextHeaderCell("City name");
            nextHeaderCell("Latitude");
            nextHeaderCell("Longitude");
            nextHeaderCell("Revenue opportunity");
            nextHeaderCell("Required");
            for (RockShow show : solution.getShowList()) {
                nextRow();
                nextCell().setCellValue(show.getVenueName());
                nextCell().setCellValue(show.getLocation().getCityName());
                nextCell().setCellValue(show.getLocation().getLatitude());
                nextCell().setCellValue(show.getLocation().getLongitude());
                nextCell().setCellValue(show.getRevenueOpportunity());
                nextCell().setCellValue(show.isRequired());
            }
            autoSizeColumnsWithHeader();
        }

    }

}
