/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.examples.tsp.persistence;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.drools.planner.examples.pas.domain.AdmissionPart;
import org.drools.planner.examples.pas.domain.Bed;
import org.drools.planner.examples.pas.domain.Department;
import org.drools.planner.examples.pas.domain.DepartmentSpecialism;
import org.drools.planner.examples.pas.domain.Equipment;
import org.drools.planner.examples.pas.domain.Gender;
import org.drools.planner.examples.pas.domain.GenderLimitation;
import org.drools.planner.examples.pas.domain.Night;
import org.drools.planner.examples.pas.domain.Patient;
import org.drools.planner.examples.pas.domain.PatientAdmissionSchedule;
import org.drools.planner.examples.pas.domain.PreferredPatientEquipment;
import org.drools.planner.examples.pas.domain.RequiredPatientEquipment;
import org.drools.planner.examples.pas.domain.Room;
import org.drools.planner.examples.pas.domain.RoomEquipment;
import org.drools.planner.examples.pas.domain.RoomSpecialism;
import org.drools.planner.examples.pas.domain.Specialism;
import org.drools.planner.examples.tsp.domain.City;
import org.drools.planner.examples.tsp.domain.TravelingSalesmanSchedule;

public class TspSolutionImporter extends AbstractTxtSolutionImporter {

    public static void main(String[] args) {
        new TspSolutionImporter().convertAll();
    }

    public TspSolutionImporter() {
        super(new TspDaoImpl());
    }

    public TxtInputBuilder createTxtInputBuilder() {
        return new TravelingSalesmanScheduleInputBuilder();
    }

    public class TravelingSalesmanScheduleInputBuilder extends TxtInputBuilder {

        private TravelingSalesmanSchedule travelingSalesmanSchedule;

        private int cityListSize;

        public Solution readSolution() throws IOException {
            travelingSalesmanSchedule = new TravelingSalesmanSchedule();
            travelingSalesmanSchedule.setId(0L);
            readHeaders();
            readCityList();
            readConstantLine("EOF");
            logger.info("TravelingSalesmanSchedule with {} cities.",
                    travelingSalesmanSchedule.getCityList().size());
            BigInteger possibleSolutionSize = factorial(travelingSalesmanSchedule.getCityList().size() - 1);
            String flooredPossibleSolutionSize = "10^" + (possibleSolutionSize.toString().length() - 1);
            logger.info("TravelingSalesmanSchedule with flooredPossibleSolutionSize ({}) and possibleSolutionSize({}).",
                    flooredPossibleSolutionSize, possibleSolutionSize);
            return travelingSalesmanSchedule;
        }

        private void readHeaders() throws IOException {
            travelingSalesmanSchedule.setName(readStringValue("NAME :"));
            readUntilConstantLine("TYPE : TSP");
            cityListSize = readIntegerValue("DIMENSION :");
            readConstantLine("EDGE_WEIGHT_TYPE : EUC_2D");
        }

        private void readCityList() throws IOException {
            readConstantLine("NODE_COORD_SECTION");
            List<City> cityList = new ArrayList<City>(cityListSize);
            for (int i = 0; i < cityListSize; i++) {
                String line = bufferedReader.readLine();
                String[] lineTokens = splitBySpace(line, 3);
                City city = new City();
                city.setId(Long.parseLong(lineTokens[0]));
                city.setX(Double.parseDouble(lineTokens[1]));
                city.setY(Double.parseDouble(lineTokens[2]));
                cityList.add(city);
            }
            travelingSalesmanSchedule.setCityList(cityList);
            travelingSalesmanSchedule.setStartCity(cityList.get(0));
        }

        private BigInteger factorial(int base) {
            BigInteger value = BigInteger.ONE;
            for (int i = 1; i <= base; i++) {
                value.multiply(BigInteger.valueOf(base));
            }
            return value;
        }

    }

}
