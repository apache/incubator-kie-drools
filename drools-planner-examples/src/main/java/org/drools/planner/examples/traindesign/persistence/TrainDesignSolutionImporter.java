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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.drools.planner.examples.traindesign.domain.CarBlock;
import org.drools.planner.examples.traindesign.domain.CrewSegment;
import org.drools.planner.examples.traindesign.domain.RailArc;
import org.drools.planner.examples.traindesign.domain.RailNode;
import org.drools.planner.examples.traindesign.domain.TrainDesign;
import org.drools.planner.examples.traindesign.domain.TrainDesignParametrization;

public class TrainDesignSolutionImporter extends AbstractTxtSolutionImporter {

    private static final String INPUT_FILE_SUFFIX = ".csv"; // TODO undo me
    private static final BigDecimal DISTANCE_MULTIPLICAND = new BigDecimal(1000);

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
            readCrewSegmentList();
            readTrainDesignParametrization();
            buildTodo();

//            createBedDesignationList();
            logger.info("TrainDesign with {} rail nodes, {} rail arcs, {} car blocks, {} train crews.",
                    new Object[]{trainDesign.getRailNodeList().size(),
                            trainDesign.getRailArcList().size(),
                            trainDesign.getCarBlockList().size(),
                            trainDesign.getCrewSegmentList().size()});
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
                railNode.setCode(lineTokens[0]);
                railNode.setBlockSwapCost(Integer.parseInt(lineTokens[1]));
                railNode.setOriginatingRailArcList(new ArrayList<RailArc>());
                railNodeList.add(railNode);
                nameToRailNodeMap.put(railNode.getCode(), railNode);
                line = bufferedReader.readLine();
            }
            trainDesign.setRailNodeList(railNodeList);
        }

        private void readCarBlockList() throws IOException {
            readConstantLine("\"Blocks\";;;;;;");
            readConstantLine("\"BlockID\";\"Origin\";\"Destination\";\"# of Cars\";\"Total Length (Feet)\";\"Total Tonnage (Tons)\";\"Shortest Distance (Miles)\"");
            List<CarBlock> carBlockList = new ArrayList<CarBlock>();
            String line = bufferedReader.readLine();
            long id = 0L;
            while (!line.equals(";;;;;;")) {
                String[] lineTokens = splitBySemicolonSeparatedValue(line, 7);
                CarBlock carBlock = new CarBlock();
                carBlock.setId(id);
                id++;
                carBlock.setCode(lineTokens[0]);
                RailNode origin = nameToRailNodeMap.get(lineTokens[1]);
                if (origin == null) {
                    throw new IllegalArgumentException("Read line (" + line
                            + ") has a non existing origin (" + lineTokens[1] + ").");
                }
                carBlock.setOrigin(origin);
                RailNode destination = nameToRailNodeMap.get(lineTokens[2]);
                if (destination == null) {
                    throw new IllegalArgumentException("Read line (" + line
                            + ") has a non existing destination (" + lineTokens[2] + ").");
                }
                carBlock.setDestination(destination);
                carBlock.setNumberOfCars(Integer.parseInt(lineTokens[3]));
                carBlock.setLength(Integer.parseInt(lineTokens[4]));
                carBlock.setTonnage(Integer.parseInt(lineTokens[5]));
                carBlock.setShortestDistance(readDistance(lineTokens[6]));
                carBlockList.add(carBlock);
                line = bufferedReader.readLine();
            }
            trainDesign.setCarBlockList(carBlockList);
        }

        private void readRailArcList() throws IOException {
            readConstantLine("\"Network\";;;;;;");
            readConstantLine("\"Origin\";\"Destination\";\"Distance\";\"Max Train Length(Feet)\";\"Max Tonnage (Tons)\";\"Max # of Trains\";");
            List<RailArc> railArcList = new ArrayList<RailArc>();
            String line = bufferedReader.readLine();
            long id = 0L;
            while (!line.equals(";;;;;;")) {
                String[] lineTokens = splitBySemicolonSeparatedValue(line, 6);
                RailArc railArc = new RailArc();
                RailArc reverseRailArc = new RailArc();
                railArc.setId(id);
                id++;
                reverseRailArc.setId(id);
                id++;
                RailNode origin = nameToRailNodeMap.get(lineTokens[0]);
                if (origin == null) {
                    throw new IllegalArgumentException("Read line (" + line
                            + ") has a non existing origin (" + lineTokens[0] + ").");
                }
                railArc.setOrigin(origin);
                origin.getOriginatingRailArcList().add(railArc);
                reverseRailArc.setDestination(origin);
                RailNode destination = nameToRailNodeMap.get(lineTokens[1]);
                if (destination == null) {
                    throw new IllegalArgumentException("Read line (" + line
                            + ") has a non existing destination (" + lineTokens[1] + ").");
                }
                railArc.setDestination(destination);
                reverseRailArc.setOrigin(destination);
                destination.getOriginatingRailArcList().add(reverseRailArc);
                int distance = readDistance(lineTokens[2]);
                railArc.setDistance(distance);
                reverseRailArc.setDistance(distance);
                int maximumTrainLength = Integer.parseInt(lineTokens[3]);
                railArc.setMaximumTrainLength(maximumTrainLength);
                reverseRailArc.setMaximumTrainLength(maximumTrainLength);
                int maximumTonnage = Integer.parseInt(lineTokens[4]);
                railArc.setMaximumTonnage(maximumTonnage);
                reverseRailArc.setMaximumTonnage(maximumTonnage);
                int maximumNumberOfTrains = Integer.parseInt(lineTokens[5]);
                railArc.setMaximumNumberOfTrains(maximumNumberOfTrains);
                reverseRailArc.setMaximumNumberOfTrains(maximumNumberOfTrains);
                railArcList.add(railArc);
                railArcList.add(reverseRailArc);
                line = bufferedReader.readLine();
            }
            trainDesign.setRailArcList(railArcList);
        }

        private void readCrewSegmentList() throws IOException {
            readConstantLine("\"Crew Segments\";;;;;;");
            readConstantLine("\"Node1\";\"Node2\";;;;;");
            List<CrewSegment> crewSegmentList = new ArrayList<CrewSegment>();
            String line = bufferedReader.readLine();
            long id = 0L;
            while (!line.equals(";;;;;;")) {
                String[] lineTokens = splitBySemicolonSeparatedValue(line, 2);
                CrewSegment crewSegment = new CrewSegment();
                crewSegment.setId(id);
                id++;
                RailNode home = nameToRailNodeMap.get(lineTokens[0]);
                if (home == null) {
                    throw new IllegalArgumentException("Read line (" + line
                            + ") has a non existing crew home (" + lineTokens[0] + ").");
                }
                crewSegment.setHome(home);
                RailNode away = nameToRailNodeMap.get(lineTokens[1]);
                if (away == null) {
                    throw new IllegalArgumentException("Read line (" + line
                            + ") has a non existing crew away (" + lineTokens[1] + ").");
                }
                crewSegment.setAway(away);
                crewSegmentList.add(crewSegment);
                line = bufferedReader.readLine();
            }
            trainDesign.setCrewSegmentList(crewSegmentList);
        }

        private void buildTodo() {
            for (CrewSegment crewSegment : trainDesign.getCrewSegmentList()) {

                Dijkstra shortestDijkstra = findShortestDijkstra(crewSegment);

                // TODO do something with shortestDijkstra

                // TODO reverse: away to home
            }
        }

        private Dijkstra findShortestDijkstra(CrewSegment crewSegment) {
            int railNodeSize = trainDesign.getRailNodeList().size();
            RailNode start = crewSegment.getHome();
            RailNode finish = crewSegment.getAway();
            Map<RailNode, Dijkstra> dijkstraMap = new HashMap<RailNode, Dijkstra>(railNodeSize);
            List<Dijkstra> unvisitedDijkstraList = new ArrayList<Dijkstra>(railNodeSize);
            Dijkstra startDijkstra = new Dijkstra(start);
            startDijkstra.setShortestDistance(0);
            startDijkstra.resetRailPath();
            startDijkstra.addRailPath(new ArrayList<RailArc>(0));
            dijkstraMap.put(start, startDijkstra);
            unvisitedDijkstraList.add(startDijkstra);
            while (!unvisitedDijkstraList.isEmpty()) {
                Dijkstra campingDijkstra = unvisitedDijkstraList.remove(0);
                if (campingDijkstra.isVisited()) {
                    throw new IllegalStateException("Bug in Dijkstra algorithm.");
                }
                campingDijkstra.setVisited(true);
                if (campingDijkstra.getRailNode().equals(finish)) {
                    return campingDijkstra;
                }
                for (RailArc nextRailArc : campingDijkstra.getRailNode().getOriginatingRailArcList()) {
                    RailNode nextNode = nextRailArc.getDestination();
                    int nextDistance = campingDijkstra.getShortestDistance() + nextRailArc.getDistance();

                    Dijkstra dijkstra = dijkstraMap.get(nextNode);
                    if (dijkstra == null) {
                        dijkstra = new Dijkstra(nextNode);
                        dijkstra.setShortestDistance(Integer.MAX_VALUE);
                        dijkstraMap.put(nextNode, dijkstra);
                        unvisitedDijkstraList.add(dijkstra);
                    }
                    if (nextDistance <= dijkstra.getShortestDistance()) {
                        if (dijkstra.isVisited()) {
                            throw new IllegalStateException("Bug in Dijkstra algorithm.");
                        }
                        if (nextDistance < dijkstra.getShortestDistance()) {
                            dijkstra.setShortestDistance(nextDistance);
                            dijkstra.resetRailPath();
                        }
                        for (List<RailArc> campingRailPath : campingDijkstra.getRailPathList()) {
                            List<RailArc> nextRailPath = new ArrayList<RailArc>(campingRailPath);
                            nextRailPath.add(nextRailArc);
                            dijkstra.addRailPath(nextRailPath);
                        }
                    }
                }
                Collections.sort(unvisitedDijkstraList);
            }
            throw new IllegalArgumentException("The CrewSegment (" + crewSegment + ") has no valid railPath.");
        }

        private class Dijkstra implements Comparable<Dijkstra> {

            private RailNode railNode;
            private boolean visited = false;

            private int shortestDistance;
            private List<List<RailArc>> railPathList;

            private Dijkstra(RailNode railNode) {
                this.railNode = railNode;
            }

            public RailNode getRailNode() {
                return railNode;
            }

            public boolean isVisited() {
                return visited;
            }

            public void setVisited(boolean visited) {
                this.visited = visited;
            }

            public int getShortestDistance() {
                return shortestDistance;
            }

            public void setShortestDistance(int shortestDistance) {
                this.shortestDistance = shortestDistance;
            }

            public List<List<RailArc>> getRailPathList() {
                return railPathList;
            }

            public void resetRailPath() {
                railPathList = new ArrayList<List<RailArc>>(2);
            }

            public void addRailPath(List<RailArc> railPath) {
                railPathList.add(railPath);
            }

            public int compareTo(Dijkstra other) {
                return new CompareToBuilder()
                        .append(shortestDistance, other.shortestDistance)
                        .append(railPathList.size(), other.railPathList.size())
                        .append(railNode.getId(), other.railNode.getId())
                        .toComparison();
            }
        }

        private void readTrainDesignParametrization() throws IOException {
            readConstantLine("\"Parameters\";;;;;;");
            readConstantLine("\"Parameters\";\"Values\";;;;;");
            TrainDesignParametrization trainDesignParametrization = new TrainDesignParametrization();

            trainDesignParametrization.setCrewImbalancePenalty(
                    readIntegerValue("\"Crew Imbalance Penalty per imbalance\";", ";;;;;"));
            trainDesignParametrization.setTrainImbalancePenalty(
                    readIntegerValue("\"Train Imbalance Penalty per imbalance\";", ";;;;;"));
            trainDesignParametrization.setTrainTravelCostPerDistance(
                    readDistance(readStringValue("\"Train travel cost per mile\";", ";;;;;")));
            trainDesignParametrization.setCarTravelCostPerDistance(
                    readDistance(readStringValue("\"Car travel cost per mile\";", ";;;;;")));
            trainDesignParametrization.setWorkEventCost(
                    readIntegerValue("\"Cost per work event\";", ";;;;;"));
            trainDesignParametrization.setMaximumBlocksPerTrain(
                    readIntegerValue("\"Maximum Blocks per train\";", ";;;;;"));
            trainDesignParametrization.setMaximumBlockSwapsPerBlock(
                    readIntegerValue("\"Maximum Block swaps per block\";", ";;;;;"));
            trainDesignParametrization.setMaximumIntermediateWorkEventsPerTrain(
                    readIntegerValue("\"Maximum intermediate work events per train\";", ";;;;;"));
            trainDesignParametrization.setTrainStartCost(
                    readIntegerValue("\"Train start Cost\";", ";;;;;"));
            trainDesignParametrization.setMissedCarCost(
                    readIntegerValue("\"Missed cost per railcar\";", ";;;;;"));

            trainDesign.setTrainDesignParametrization(trainDesignParametrization);
        }

        private int readDistance(String lineToken) {
            BigDecimal distanceBigDecimal = new BigDecimal(lineToken).multiply(DISTANCE_MULTIPLICAND);
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
