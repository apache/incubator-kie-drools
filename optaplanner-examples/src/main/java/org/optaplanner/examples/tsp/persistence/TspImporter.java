package org.optaplanner.examples.tsp.persistence;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.optaplanner.examples.common.business.SolutionBusiness;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;
import org.optaplanner.examples.tsp.app.TspApp;
import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.Standstill;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.examples.tsp.domain.Visit;
import org.optaplanner.examples.tsp.domain.location.AirLocation;
import org.optaplanner.examples.tsp.domain.location.DistanceType;
import org.optaplanner.examples.tsp.domain.location.Location;
import org.optaplanner.examples.tsp.domain.location.RoadLocation;

public class TspImporter extends AbstractTxtSolutionImporter<TspSolution> {

    public static final String INPUT_FILE_SUFFIX = "tsp";

    public static void main(String[] args) {
        SolutionConverter<TspSolution> converter =
                SolutionConverter.createImportConverter(TspApp.DATA_DIR_NAME, new TspImporter(), new TspSolutionFileIO());
        converter.convert("other/air/europe40.tsp", "europe40.json");
        converter.convert("other/road-km/americanRoadTrip-road-km-n50.tsp", "americanRoadTrip-road-km-n50.json");
        converter.convert("cook/air/lu980.tsp", "lu980.json");
    }

    @Override
    public String getInputFileSuffix() {
        return INPUT_FILE_SUFFIX;
    }

    @Override
    public TxtInputBuilder<TspSolution> createTxtInputBuilder() {
        return new TspInputBuilder();
    }

    public static class TspInputBuilder extends TxtInputBuilder<TspSolution> {

        private TspSolution tspSolution;

        private int locationListSize;

        @Override
        public TspSolution readSolution() throws IOException {
            tspSolution = new TspSolution(0);
            String firstLine = readStringValue();
            if (firstLine.matches("\\s*NAME\\s*:.*")) {
                tspSolution.setName(removePrefixSuffixFromLine(firstLine, "\\s*NAME\\s*:", ""));
                readTspLibFormat();
            } else {
                tspSolution.setName(SolutionBusiness.getBaseFileName(inputFile));
                locationListSize = Integer.parseInt(firstLine.trim());
                readCourseraFormat();
            }
            BigInteger possibleSolutionSize = factorial(tspSolution.getLocationList().size() - 1);
            logger.info("TspSolution {} has {} locations with a search space of {}.",
                    getInputId(),
                    tspSolution.getLocationList().size(),
                    getFlooredPossibleSolutionSize(possibleSolutionSize));
            return tspSolution;
        }

        // ************************************************************************
        // TSP TSPLIB format. See http://www.math.uwaterloo.ca/tsp/
        // ************************************************************************

        private void readTspLibFormat() throws IOException {
            readTspLibHeaders();
            readTspLibCityList();
            createVisitList();
            readTspLibSolution();
            readConstantLine("EOF");
        }

        private void readTspLibHeaders() throws IOException {
            readUntilConstantLine("TYPE *: +TSP");
            locationListSize = readIntegerValue("DIMENSION *:");
            String edgeWeightType = readStringValue("EDGE_WEIGHT_TYPE *:");
            if (edgeWeightType.equalsIgnoreCase("EUC_2D")) {
                tspSolution.setDistanceType(DistanceType.AIR_DISTANCE);
            } else if (edgeWeightType.equalsIgnoreCase("EXPLICIT")) {
                tspSolution.setDistanceType(DistanceType.ROAD_DISTANCE);
                String edgeWeightFormat = readStringValue("EDGE_WEIGHT_FORMAT *:");
                if (!edgeWeightFormat.equalsIgnoreCase("FULL_MATRIX")) {
                    throw new IllegalArgumentException("The edgeWeightFormat (" + edgeWeightFormat + ") is not supported.");
                }
            } else {
                throw new IllegalArgumentException("The edgeWeightType (" + edgeWeightType + ") is not supported.");
            }
            tspSolution.setDistanceUnitOfMeasurement(readOptionalStringValue("EDGE_WEIGHT_UNIT_OF_MEASUREMENT *:", "distance"));
        }

        private void readTspLibCityList() throws IOException {
            readConstantLine("NODE_COORD_SECTION");
            DistanceType distanceType = tspSolution.getDistanceType();
            List<Location> locationList = new ArrayList<>(locationListSize);
            for (int i = 0; i < locationListSize; i++) {
                String line = bufferedReader.readLine();
                String[] lineTokens = splitBySpace(line, 3, 4, false, true);
                Location location;
                long id = Long.parseLong(lineTokens[0]);
                double latitude = Double.parseDouble(lineTokens[1]);
                double longitude = Double.parseDouble(lineTokens[2]);
                switch (distanceType) {
                    case AIR_DISTANCE:
                        location = new AirLocation(id, latitude, longitude);
                        break;
                    case ROAD_DISTANCE:
                        location = new RoadLocation(id, latitude, longitude);
                        break;
                    default:
                        throw new IllegalStateException("The distanceType (" + distanceType
                                + ") is not implemented.");

                }
                if (lineTokens.length >= 4) {
                    location.setName(lineTokens[3]);
                }
                locationList.add(location);
            }
            tspSolution.setLocationList(locationList);
            if (distanceType == DistanceType.ROAD_DISTANCE) {
                readConstantLine("EDGE_WEIGHT_SECTION");
                for (int i = 0; i < locationListSize; i++) {
                    RoadLocation location = (RoadLocation) locationList.get(i);
                    Map<RoadLocation, Double> travelDistanceMap = new LinkedHashMap<>(locationListSize);
                    String line = bufferedReader.readLine();
                    String[] lineTokens = splitBySpacesOrTabs(line.trim(), locationListSize);
                    for (int j = 0; j < locationListSize; j++) {
                        double travelDistance = Double.parseDouble(lineTokens[j]);
                        if (i == j) {
                            if (travelDistance != 0.0) {
                                throw new IllegalStateException("The travelDistance (" + travelDistance
                                        + ") should be zero.");
                            }
                        } else {
                            RoadLocation otherLocation = (RoadLocation) locationList.get(j);
                            travelDistanceMap.put(otherLocation, travelDistance);
                        }
                    }
                    location.setTravelDistanceMap(travelDistanceMap);
                }
            }
        }

        private void createVisitList() {
            List<Location> locationList = tspSolution.getLocationList();
            List<Visit> visitList = new ArrayList<>(locationList.size() - 1);
            int count = 0;
            for (Location location : locationList) {
                if (count < 1) {
                    Domicile domicile = new Domicile(location.getId(), location);
                    tspSolution.setDomicile(domicile);
                } else {
                    Visit visit = new Visit(location.getId(), location);
                    // Notice that we leave the PlanningVariable properties on null
                    visitList.add(visit);
                }
                count++;
            }
            tspSolution.setVisitList(visitList);
        }

        private void readTspLibSolution() throws IOException {
            boolean enabled = readOptionalConstantLine("TOUR_SECTION");
            if (!enabled) {
                return;
            }
            long domicileId = readLongValue();
            Domicile domicile = tspSolution.getDomicile();
            if (domicile.getId() != domicileId) {
                throw new IllegalStateException("The domicileId (" + domicileId
                        + ") is not the domicile's id (" + domicile.getId() + ").");
            }
            int visitListSize = tspSolution.getVisitList().size();
            Map<Long, Visit> idToVisitMap = new HashMap<>(visitListSize);
            for (Visit visit : tspSolution.getVisitList()) {
                idToVisitMap.put(visit.getId(), visit);
            }
            Standstill previousStandstill = domicile;
            for (int i = 0; i < visitListSize; i++) {
                long visitId = readLongValue();
                Visit visit = idToVisitMap.get(visitId);
                if (visit == null) {
                    throw new IllegalStateException("The visitId (" + visitId
                            + ") is does not exist.");
                }
                visit.setPreviousStandstill(previousStandstill);
                previousStandstill = visit;
            }
        }

        // ************************************************************************
        // TSP coursera format. See https://class.coursera.org/optimization-001/
        // ************************************************************************

        private void readCourseraFormat() throws IOException {
            List<Location> locationList = new ArrayList<>(locationListSize);
            long id = 0;
            for (int i = 0; i < locationListSize; i++) {
                String line = bufferedReader.readLine();
                String[] lineTokens = splitBySpace(line, 2);
                Location location = new AirLocation(id, Double.parseDouble(lineTokens[0]), Double.parseDouble(lineTokens[1]));
                locationList.add(location);
            }
            tspSolution.setLocationList(locationList);
            createVisitList();
        }

    }

}
