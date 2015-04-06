/*
 * Copyright 2015 JBoss Inc
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

package org.optaplanner.examples.coachshuttlegathering.domain.solver;

import org.apache.commons.lang.ObjectUtils;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.coachshuttlegathering.domain.Bus;
import org.optaplanner.examples.coachshuttlegathering.domain.BusOrStop;
import org.optaplanner.examples.coachshuttlegathering.domain.BusStop;
import org.optaplanner.examples.coachshuttlegathering.domain.StopOrHub;

public class TransportTimeToHubUpdatingVariableListener implements VariableListener<BusOrStop> {

    public void beforeEntityAdded(ScoreDirector scoreDirector, BusOrStop busOrStop) {
        // Do nothing
    }

    public void afterEntityAdded(ScoreDirector scoreDirector, BusOrStop busOrStop) {
        if (busOrStop instanceof BusStop) {
            updateTransportTimeToHub(scoreDirector, (BusStop) busOrStop);
        }
    }

    public void beforeVariableChanged(ScoreDirector scoreDirector, BusOrStop busOrStop) {
        // Do nothing
    }

    public void afterVariableChanged(ScoreDirector scoreDirector, BusOrStop busOrStop) {
        if (busOrStop instanceof BusStop) {
            updateTransportTimeToHub(scoreDirector, (BusStop) busOrStop);
        }
    }

    public void beforeEntityRemoved(ScoreDirector scoreDirector, BusOrStop busOrStop) {
        // Do nothing
    }

    public void afterEntityRemoved(ScoreDirector scoreDirector, BusOrStop busOrStop) {
        // Do nothing
    }

    protected void updateTransportTimeToHub(ScoreDirector scoreDirector, BusStop sourceStop) {
        Bus bus = sourceStop.getBus();
        Integer transportTimeToHub;
        if (bus == null) {
            transportTimeToHub = null;
        } else {
            StopOrHub next = sourceStop.getNextStop();
            if (next == null) {
                next = bus.getDestination();
            }
            transportTimeToHub = (next == null) ? null : next.getTransportTimeToHub();
            if (transportTimeToHub != null) {
                transportTimeToHub += bus.getDurationFromTo(sourceStop.getLocation(), next.getLocation());
            }
        }
        scoreDirector.beforeVariableChanged(sourceStop, "transportTimeToHub");
        sourceStop.setTransportTimeToHub(transportTimeToHub);
        scoreDirector.afterVariableChanged(sourceStop, "transportTimeToHub");
        BusStop toStop = sourceStop;
        for (BusOrStop busOrStop = sourceStop.getPreviousBusOrStop();
                busOrStop instanceof BusStop;) {
            BusStop stop = (BusStop) busOrStop;
            if (transportTimeToHub != null) {
                transportTimeToHub += bus.getDurationFromTo(stop.getLocation(), toStop.getLocation());
            }
            scoreDirector.beforeVariableChanged(stop, "transportTimeToHub");
            stop.setTransportTimeToHub(transportTimeToHub);
            scoreDirector.afterVariableChanged(stop, "transportTimeToHub");
            toStop = stop;
            busOrStop = stop.getPreviousBusOrStop();
        }
    }

}
