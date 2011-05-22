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

package org.drools.planner.examples.trailerrouting.persistence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.drools.planner.examples.trailerrouting.domain.TrailerRoutingLocation;
import org.drools.planner.examples.trailerrouting.domain.TrailerRoutingSchedule;

public class TrailerRoutingSolutionImporter extends AbstractTxtSolutionImporter {

    public static void main(String[] args) {
        new TrailerRoutingSolutionImporter().convertAll();
    }

    public TrailerRoutingSolutionImporter() {
        super(new TrailerRoutingDaoImpl());
    }

    public TxtInputBuilder createTxtInputBuilder() {
        return new TrailerRoutingInputBuilder();
    }

    public class TrailerRoutingInputBuilder extends TxtInputBuilder {

        public Solution readSolution() throws IOException {
            TrailerRoutingSchedule schedule = new TrailerRoutingSchedule();
            schedule.setId(0L);

            // TODO read the data file and set all lists of TrailerRoutingSchedule except for orderAssignmentList
            // Take a look at the other *SolutionImporter implementations in drools-planner-examples

            // TODO a single problem should have a single data set. Multiple CSV files for 1 problem is not allowed.
            // TODO Easiest solution is probably to to concat the CSV files in a txt file with headers between it
            // TODO note: the inherited getInputFileSuffix() method is currently excepting a single *.txt file as input

            readLocationList(schedule);
            // TODO readDriverList, readTruckList, ... (except orderAssignmentList, leave that null)

            logger.info("TrailerRoutingSchedule with {} locations, {} drivers, {} trucks, {} trailers, {} orders" +
                    " and {} unavailable period constraints.",
                    new Object[]{schedule.getLocationList().size(),
                            schedule.getDriverList().size(),
                            schedule.getTruckList().size(),
                            schedule.getTrailerList().size(),
                            schedule.getOrderList().size()});

            // Note: orderAssignmentList stays null, that's work for the StartingSolutionInitializer
            return schedule;
        }

        private void readLocationList(TrailerRoutingSchedule schedule)
                throws IOException {
            int locationListSize = readIntegerValue("Locations:");
            List<TrailerRoutingLocation> locationList = new ArrayList<TrailerRoutingLocation>();
            for (int i = 0; i < locationListSize; i++) {
                TrailerRoutingLocation location = new TrailerRoutingLocation();
                location.setId((long) i);
                String line = readStringValue();
                // TODO set all properties on location: name, locationType, openingTimeInMinutes, ...
                // TODO setting routeMap must be done too, but can be done in a later read method

                // TODO the domain model (see class diagram) can be adjusted as needed, but
                // - it should not become less structured, less readable or less valid, for example changing an enum into an int is not allowed
                // - it should contain everything needed to do the planning, as normalized as possible
                // - Driver, Truck and Trailer must not be generalized as "Resource"
                locationList.add(location);
            }
            schedule.setLocationList(locationList);
        }

        // TODO other methods: readDriverList, readTruckList, ...

    }

}
