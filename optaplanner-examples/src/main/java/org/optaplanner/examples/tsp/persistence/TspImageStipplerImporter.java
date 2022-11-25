package org.optaplanner.examples.tsp.persistence;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.optaplanner.examples.common.business.SolutionBusiness;
import org.optaplanner.examples.common.persistence.AbstractPngSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;
import org.optaplanner.examples.tsp.app.TspApp;
import org.optaplanner.examples.tsp.domain.Domicile;
import org.optaplanner.examples.tsp.domain.TspSolution;
import org.optaplanner.examples.tsp.domain.Visit;
import org.optaplanner.examples.tsp.domain.location.AirLocation;
import org.optaplanner.examples.tsp.domain.location.DistanceType;
import org.optaplanner.examples.tsp.domain.location.Location;

public class TspImageStipplerImporter extends AbstractPngSolutionImporter<TspSolution> {

    private static final double GRAY_MAXIMUM = 256.0 * 3.0;

    public static void main(String[] args) {
        SolutionConverter<TspSolution> converter = SolutionConverter.createImportConverter(TspApp.DATA_DIR_NAME,
                new TspImageStipplerImporter(), new TspSolutionFileIO());
        converter.convertAll();
    }

    @Override
    public PngInputBuilder<TspSolution> createPngInputBuilder() {
        return new TspImageStipplerInputBuilder();
    }

    public static class TspImageStipplerInputBuilder extends PngInputBuilder<TspSolution> {

        private TspSolution tspSolution;

        private int locationListSize;

        @Override
        public TspSolution readSolution() throws IOException {
            tspSolution = new TspSolution(0L);
            tspSolution.setName(SolutionBusiness.getBaseFileName(inputFile));
            floydSteinbergDithering();
            createVisitList();
            BigInteger possibleSolutionSize = factorial(tspSolution.getLocationList().size() - 1);
            logger.info("TspSolution {} has {} locations with a search space of {}.",
                    getInputId(),
                    tspSolution.getLocationList().size(),
                    getFlooredPossibleSolutionSize(possibleSolutionSize));
            return tspSolution;
        }

        /**
         * As described by <a href="https://en.wikipedia.org/wiki/Floyd%E2%80%93Steinberg_dithering">Floyd-Steinberg
         * dithering</a>.
         */
        private void floydSteinbergDithering() {
            tspSolution.setDistanceType(DistanceType.AIR_DISTANCE);
            tspSolution.setDistanceUnitOfMeasurement("distance");
            int width = image.getWidth();
            int height = image.getHeight();
            double[][] errorDiffusion = new double[width][height];
            List<Location> locationList = new ArrayList<>(1000);
            long id = 0L;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);
                    int r = (rgb) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = (rgb >> 16) & 0xFF;
                    double originalGray = (r + g + b) / GRAY_MAXIMUM;
                    double diffusedGray = originalGray + errorDiffusion[x][y];
                    double error;
                    if (diffusedGray <= 0.5) {
                        Location location = new AirLocation(id, -y, x);
                        id++;
                        locationList.add(location);
                        error = diffusedGray;
                    } else {
                        error = diffusedGray - 1.0;
                    }
                    if (x + 1 < width) {
                        errorDiffusion[x + 1][y] += error * 7.0 / 16.0;
                    }
                    if (y + 1 < height) {
                        if (x - 1 >= 0) {
                            errorDiffusion[x - 1][y + 1] += error * 3.0 / 16.0;
                        }
                        errorDiffusion[x][y + 1] += error * 5.0 / 16.0;
                        if (x + 1 < width) {
                            errorDiffusion[x + 1][y + 1] += error * 1.0 / 16.0;
                        }
                    }
                }
            }
            tspSolution.setLocationList(locationList);
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

    }

}
