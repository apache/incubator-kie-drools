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

package org.acme.maintenancescheduling.bootstrap;

import io.quarkus.runtime.StartupEvent;
import org.acme.maintenancescheduling.domain.MaintainableUnit;
import org.acme.maintenancescheduling.domain.MaintenanceCrew;
import org.acme.maintenancescheduling.domain.MaintenanceJob;
import org.acme.maintenancescheduling.domain.MutuallyExclusiveJobs;
import org.acme.maintenancescheduling.domain.TimeGrain;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DemoDataGenerator {

    @ConfigProperty(name = "schedule.demoData", defaultValue = "SMALLEST")
    public DemoData demoData;

    public enum DemoData {
        NONE,
        SMALL,
        SMALLEST
    }

    @Transactional
    public void generateDemoData(@Observes StartupEvent startupEvent) {
        if (demoData == DemoData.NONE) {
            return;
        }

        List<MaintainableUnit> maintainableUnitList = new ArrayList<>();
        maintainableUnitList.add(new MaintainableUnit("Track 1"));
        maintainableUnitList.add(new MaintainableUnit("Track 2"));
        maintainableUnitList.add(new MaintainableUnit("Track 3"));
        maintainableUnitList.add(new MaintainableUnit("Switch 1"));
        maintainableUnitList.add(new MaintainableUnit("Switch 2"));
        maintainableUnitList.add(new MaintainableUnit("Switch 3"));
        maintainableUnitList.add(new MaintainableUnit("Yard 1"));
        maintainableUnitList.add(new MaintainableUnit("Yard 2"));
        maintainableUnitList.add(new MaintainableUnit("Yard 3"));
        if (demoData == DemoData.SMALL) {
            maintainableUnitList.add(new MaintainableUnit("Track 4"));
            maintainableUnitList.add(new MaintainableUnit("Track 5"));
            maintainableUnitList.add(new MaintainableUnit("Track 6"));
            maintainableUnitList.add(new MaintainableUnit("Switch 4"));
            maintainableUnitList.add(new MaintainableUnit("Switch 5"));
            maintainableUnitList.add(new MaintainableUnit("Switch 6"));
            maintainableUnitList.add(new MaintainableUnit("Yard 4"));
            maintainableUnitList.add(new MaintainableUnit("Yard 5"));
            maintainableUnitList.add(new MaintainableUnit("Yard 6"));
        }
        MaintainableUnit.persist(maintainableUnitList);

        List<MaintenanceCrew> maintenanceCrewList = new ArrayList<>();
        maintenanceCrewList.add(new MaintenanceCrew("Crew 1"));
        maintenanceCrewList.add(new MaintenanceCrew("Crew 2"));
        maintenanceCrewList.add(new MaintenanceCrew("Crew 3"));
        if (demoData == DemoData.SMALL) {
            maintenanceCrewList.add(new MaintenanceCrew("Crew 4"));
            maintenanceCrewList.add(new MaintenanceCrew("Crew 5"));
            maintenanceCrewList.add(new MaintenanceCrew("Crew 6"));
        }
        MaintenanceCrew.persist(maintenanceCrewList);

        List<TimeGrain> timeGrainList = new ArrayList<>();
        for (int i = 0; i <= 24; i++) {
            timeGrainList.add(new TimeGrain(i));
        }
        if (demoData == DemoData.SMALL) {
            for (int i = 25; i <= 48; i++) {
                timeGrainList.add(new TimeGrain(i));
            }
        }
        TimeGrain.persist(timeGrainList);

        List<MaintenanceJob> maintenanceJobList = new ArrayList<>();
        maintenanceJobList.add(new MaintenanceJob("Bolt tightening 1", maintainableUnitList.get(0), 0, 24, 1, true));
        maintenanceJobList.add(new MaintenanceJob("Bolt tightening 2", maintainableUnitList.get(1), 0, 24, 1, true));
        maintenanceJobList.add(new MaintenanceJob("Bolt tightening 3", maintainableUnitList.get(2), 0, 24, 1, true));
        maintenanceJobList.add(new MaintenanceJob("Switch replacement 1", maintainableUnitList.get(3), 8, 24, 2, true));
        maintenanceJobList.add(new MaintenanceJob("Switch replacement 2", maintainableUnitList.get(4), 8, 24, 2, true));
        maintenanceJobList.add(new MaintenanceJob("Switch replacement 3", maintainableUnitList.get(5), 8, 24, 2, true));
        maintenanceJobList.add(new MaintenanceJob("Yard repair 1", maintainableUnitList.get(6), 0, 24, 4, true));
        maintenanceJobList.add(new MaintenanceJob("Yard repair 2", maintainableUnitList.get(7), 0, 24, 4, true));
        maintenanceJobList.add(new MaintenanceJob("Yard repair 3", maintainableUnitList.get(8), 0, 24, 4, true));
        maintenanceJobList.add(new MaintenanceJob("Track replacement 1", maintainableUnitList.get(0), 0, 24, 8, true));
        if (demoData == DemoData.SMALL) {
            maintenanceJobList.add(new MaintenanceJob("Bolt tightening 4", maintainableUnitList.get(9), 24, 48, 1, true));
            maintenanceJobList.add(new MaintenanceJob("Bolt tightening 5", maintainableUnitList.get(10), 24, 48, 1, true));
            maintenanceJobList.add(new MaintenanceJob("Bolt tightening 6", maintainableUnitList.get(11), 24, 48, 1, true));
            maintenanceJobList.add(new MaintenanceJob("Switch replacement 4", maintainableUnitList.get(12), 32, 48, 2, true));
            maintenanceJobList.add(new MaintenanceJob("Switch replacement 5", maintainableUnitList.get(13), 32, 48, 2, true));
            maintenanceJobList.add(new MaintenanceJob("Switch replacement 6", maintainableUnitList.get(14), 32, 48, 2, true));
            maintenanceJobList.add(new MaintenanceJob("Yard repair 4", maintainableUnitList.get(15), 24, 48, 4, true));
            maintenanceJobList.add(new MaintenanceJob("Yard repair 5", maintainableUnitList.get(16), 24, 48, 4, true));
            maintenanceJobList.add(new MaintenanceJob("Yard repair 6", maintainableUnitList.get(17), 24, 48, 4, true));
            maintenanceJobList.add(new MaintenanceJob("Track replacement 2", maintainableUnitList.get(9), 24, 48, 8, true));

            maintenanceJobList.add(new MaintenanceJob("Track cleaning 1", maintainableUnitList.get(0), 0, 48, 1, false));
            maintenanceJobList.add(new MaintenanceJob("Track cleaning 2", maintainableUnitList.get(1), 0, 48, 1, false));
            maintenanceJobList.add(new MaintenanceJob("Track cleaning 3", maintainableUnitList.get(2), 0, 48, 1, false));
            maintenanceJobList.add(new MaintenanceJob("Switch tightening 1", maintainableUnitList.get(3), 8, 48, 2, false));
            maintenanceJobList.add(new MaintenanceJob("Switch tightening 2", maintainableUnitList.get(4), 8, 48, 2, false));
            maintenanceJobList.add(new MaintenanceJob("Switch tightening 3", maintainableUnitList.get(5), 8, 48, 2, false));
            maintenanceJobList.add(new MaintenanceJob("Yard sanding 1", maintainableUnitList.get(6), 0, 48, 4, false));
            maintenanceJobList.add(new MaintenanceJob("Yard sanding 2", maintainableUnitList.get(7), 0, 48, 4, false));
            maintenanceJobList.add(new MaintenanceJob("Yard sanding 3", maintainableUnitList.get(8), 0, 48, 4, false));
            maintenanceJobList.add(new MaintenanceJob("Track sanding 1", maintainableUnitList.get(0), 0, 48, 8, false));
        }
        MaintenanceJob.persist(maintenanceJobList);

        List<MutuallyExclusiveJobs> mutuallyExclusiveJobsList = new ArrayList<>();
        mutuallyExclusiveJobsList.add(
                new MutuallyExclusiveJobs(maintenanceJobList.get(0), maintenanceJobList.get(1), maintenanceJobList.get(2)));
        mutuallyExclusiveJobsList.add(
                new MutuallyExclusiveJobs(maintenanceJobList.get(3), maintenanceJobList.get(4), maintenanceJobList.get(5)));
        mutuallyExclusiveJobsList.add(
                new MutuallyExclusiveJobs(maintenanceJobList.get(6), maintenanceJobList.get(7), maintenanceJobList.get(8)));
        if (demoData == DemoData.SMALL) {
            mutuallyExclusiveJobsList.add(new MutuallyExclusiveJobs(maintenanceJobList.get(10), maintenanceJobList.get(11),
                    maintenanceJobList.get(12)));
            mutuallyExclusiveJobsList.add(new MutuallyExclusiveJobs(maintenanceJobList.get(13), maintenanceJobList.get(14),
                    maintenanceJobList.get(15)));
            mutuallyExclusiveJobsList.add(new MutuallyExclusiveJobs(maintenanceJobList.get(16), maintenanceJobList.get(17),
                    maintenanceJobList.get(18)));
        }
        MutuallyExclusiveJobs.persist(mutuallyExclusiveJobsList);
    }
}
