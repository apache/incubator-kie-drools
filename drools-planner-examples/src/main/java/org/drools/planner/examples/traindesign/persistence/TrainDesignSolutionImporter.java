/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.examples.traindesign.persistence;

import java.io.IOException;
import java.math.BigDecimal;
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
import org.drools.planner.examples.pas.domain.BedDesignation;
import org.drools.planner.examples.pas.domain.Department;
import org.drools.planner.examples.pas.domain.DepartmentSpecialism;
import org.drools.planner.examples.pas.domain.Equipment;
import org.drools.planner.examples.pas.domain.Gender;
import org.drools.planner.examples.pas.domain.GenderLimitation;
import org.drools.planner.examples.pas.domain.Night;
import org.drools.planner.examples.pas.domain.Patient;
import org.drools.planner.examples.pas.domain.PreferredPatientEquipment;
import org.drools.planner.examples.pas.domain.RequiredPatientEquipment;
import org.drools.planner.examples.pas.domain.Room;
import org.drools.planner.examples.pas.domain.RoomEquipment;
import org.drools.planner.examples.pas.domain.RoomSpecialism;
import org.drools.planner.examples.pas.domain.Specialism;
import org.drools.planner.examples.traindesign.domain.RailArc;
import org.drools.planner.examples.traindesign.domain.RailNode;
import org.drools.planner.examples.traindesign.domain.TrainDesign;

public class TrainDesignSolutionImporter extends AbstractTxtSolutionImporter {

    private static final String INPUT_FILE_SUFFIX = ".csv";
    private static final BigDecimal DISTANCE_MULTIPLICANT = new BigDecimal(1000);

    public static void main(String[] args) {
        new TrainDesignSolutionImporter().convertAll();
    }

    public TrainDesignSolutionImporter() {
        super(new TrainDesignDaoImpl());
    }

    @Override
    protected String getInputFileSuffix() {
        return INPUT_FILE_SUFFIX;
    }

    public TxtInputBuilder createTxtInputBuilder() {
        return new TrainDesignInputBuilder();
    }

    public class TrainDesignInputBuilder extends TxtInputBuilder {

        private TrainDesign trainDesign;

        private Map<String, RailNode> nameToRailNodeMap = null;

        public Solution readSolution() throws IOException {
            trainDesign = new TrainDesign();
            trainDesign.setId(0L);
            readRailNodeList();
            readCarBlockList();
            readRailArcList();

//            createBedDesignationList();
            logger.info("TrainDesign with {} rail nodes and {} rail arcs.",
                    new Object[]{trainDesign.getRailNodeList().size(),
                            trainDesign.getRailArcList().size()});
//            BigInteger possibleSolutionSize = BigInteger.valueOf(trainDesign.getBedList().size()).pow(
//                    trainDesign.getAdmissionPartList().size());
//            String flooredPossibleSolutionSize = "10^" + (possibleSolutionSize.toString().length() - 1);
//            logger.info("TrainDesign with flooredPossibleSolutionSize ({}) and possibleSolutionSize({}).",
//                    flooredPossibleSolutionSize, possibleSolutionSize);
            return trainDesign;
        }

        private void readRailNodeList() throws IOException {
            readConstantLine("\"Network Nodes\";;;;;;");
            readConstantLine("\"Node\";\"BlockSwap Cost\";;;;;");
            List<RailNode> railNodeList = new ArrayList<RailNode>();
            nameToRailNodeMap = new HashMap<String, RailNode>();
            String line = bufferedReader.readLine();
            long id = 0L;
            while (!line.equals(";;;;;;")) {
                String[] lineTokens = splitBySemicolonSeparatedValue(line, 2);
                RailNode railNode = new RailNode();
                railNode.setId(id);
                id++;
                railNode.setName(lineTokens[0]);
                railNode.setBlockSwapCost(Integer.parseInt(lineTokens[1]));
                railNodeList.add(railNode);
                nameToRailNodeMap.put(railNode.getName(), railNode);
                line = bufferedReader.readLine();
            }
            trainDesign.setRailNodeList(railNodeList);
        }

        private void readCarBlockList() throws IOException {
            readConstantLine("\"Blocks\";;;;;;");
            readConstantLine("\"BlockID\";\"Origin\";\"Destination\";\"# of Cars\";\"Total Length (Feet)\";\"Total Tonnage (Tons)\";\"Shortest Distance (Miles)\"");
//            List<RailNode> railNodeList = new ArrayList<RailNode>();
//            nameToRailNodeMap = new HashMap<String, RailNode>();
            String line = bufferedReader.readLine();
            long id = 0L;
            while (!line.equals(";;;;;;")) {
                String[] lineTokens = splitBySemicolonSeparatedValue(line, 7);
//                RailNode railNode = new RailNode();
//                railNode.setId(id);
//                id++;
//                railNode.setName(lineTokens[0]);
//                railNode.setBlockSwapCost(Integer.parseInt(lineTokens[2]));
//                railNodeList.add(railNode);
//                nameToRailNodeMap.put(railNode.getName(), railNode);
                line = bufferedReader.readLine();
            }
//            trainDesign.setRailNodeList(railNodeList);
        }

        private void readRailArcList() throws IOException {
            readConstantLine("\"Network\";;;;;;");
            readConstantLine("\"Origin\";\"Destination\";\"Distance\";\"Max Train Length(Feet)\";\"Max Tonnage (Tons)\";\"Max # of Trains\";");
            List<RailArc> railArcListList = new ArrayList<RailArc>();
            String line = bufferedReader.readLine();
            long id = 0L;
            while (!line.equals(";;;;;;")) {
                String[] lineTokens = splitBySemicolonSeparatedValue(line, 6);
                RailArc railArc = new RailArc();
                railArc.setId(id);
                id++;
                RailNode origin = nameToRailNodeMap.get(lineTokens[0]);
                if (origin == null) {
                    throw new IllegalArgumentException("Read line (" + line
                            + ") has a non existing origin (" + lineTokens[0] + ").");
                }
                railArc.setOrigin(origin);
                RailNode destination = nameToRailNodeMap.get(lineTokens[1]);
                if (destination == null) {
                    throw new IllegalArgumentException("Read line (" + line
                            + ") has a non existing destination (" + lineTokens[1] + ").");
                }
                railArc.setDestination(destination);
                railArc.setDistance(readDistance(lineTokens[2]));
                railArc.setMaximumTrainLength(Integer.parseInt(lineTokens[3]));
                railArc.setMaximumTonnage(Integer.parseInt(lineTokens[4]));
                railArc.setMaximumNumberOfTrains(Integer.parseInt(lineTokens[5]));
                railArcListList.add(railArc);
                line = bufferedReader.readLine();
            }
            trainDesign.setRailArcList(railArcListList);
        }

        private int readDistance(String lineToken) {
            BigDecimal distanceBigDecimal = new BigDecimal(lineToken).multiply(DISTANCE_MULTIPLICANT);
            if (distanceBigDecimal.remainder(BigDecimal.ONE).compareTo(BigDecimal.ZERO) != 0) {
                throw new IllegalArgumentException("The distance (" + lineToken + ") is too detailed.");
            }
            return distanceBigDecimal.intValue();
        }

//        private void createBedDesignationList() {
//            List<AdmissionPart> admissionPartList = trainDesign.getAdmissionPartList();
//            List<BedDesignation> bedDesignationList = new ArrayList<BedDesignation>(admissionPartList.size());
//            long id = 0L;
//            for (AdmissionPart admissionPart : admissionPartList) {
//                BedDesignation bedDesignation = new BedDesignation();
//                bedDesignation.setId(id);
//                id++;
//                bedDesignation.setAdmissionPart(admissionPart);
//                // Notice that we leave the PlanningVariable properties on null
//                bedDesignationList.add(bedDesignation);
//            }
//            trainDesign.setBedDesignationList(bedDesignationList);
//        }

    }

}
