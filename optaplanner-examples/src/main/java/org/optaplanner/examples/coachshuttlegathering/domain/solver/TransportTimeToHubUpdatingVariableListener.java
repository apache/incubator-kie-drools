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

import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.coachshuttlegathering.domain.Bus;
import org.optaplanner.examples.coachshuttlegathering.domain.BusOrStop;
import org.optaplanner.examples.coachshuttlegathering.domain.BusStop;
import org.optaplanner.examples.coachshuttlegathering.domain.Shuttle;
import org.optaplanner.examples.coachshuttlegathering.domain.StopOrHub;

public class TransportTimeToHubUpdatingVariableListener implements VariableListener<BusOrStop> {

    public void beforeEntityAdded(ScoreDirector scoreDirector, BusOrStop busOrStop) {
        // Do nothing
    }

    public void afterEntityAdded(ScoreDirector scoreDirector, BusOrStop busOrStop) {
        if (busOrStop instanceof BusStop) {
            updateTransportTimeToHub(scoreDirector, (BusStop) busOrStop);
        } else if (busOrStop instanceof Shuttle) {
            updateTransportTimeToHubOfShuttle(scoreDirector, (Shuttle) busOrStop);
        }
    }

    public void beforeVariableChanged(ScoreDirector scoreDirector, BusOrStop busOrStop) {
        // Do nothing
    }

    public void afterVariableChanged(ScoreDirector scoreDirector, BusOrStop busOrStop) {
        if (busOrStop instanceof BusStop) {
            updateTransportTimeToHub(scoreDirector, (BusStop) busOrStop);
        } else if (busOrStop instanceof Shuttle) {
            updateTransportTimeToHubOfShuttle(scoreDirector, (Shuttle) busOrStop);
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
        updateTransportTime(scoreDirector, sourceStop, bus, transportTimeToHub);
    }

    private void updateTransportTime(ScoreDirector scoreDirector, BusStop sourceStop, Bus bus, Integer transportTimeToHub) {
        if (ObjectUtils.equals(sourceStop.getTransportTimeToHub(), transportTimeToHub)) {
            return;
        }
        scoreDirector.beforeVariableChanged(sourceStop, "transportTimeToHub");
        sourceStop.setTransportTimeToHub(transportTimeToHub);
        scoreDirector.afterVariableChanged(sourceStop, "transportTimeToHub");
        updateTransportTimeForTransferShuttleList(scoreDirector, sourceStop, bus);
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
            updateTransportTimeForTransferShuttleList(scoreDirector, stop, bus);
            toStop = stop;
            busOrStop = stop.getPreviousBusOrStop();
        }
    }

    private void updateTransportTimeForTransferShuttleList(ScoreDirector scoreDirector, BusStop parentStop,
            Bus parentBus) {
        List<Shuttle> transferShuttleList = parentStop.getTransferShuttleList();
        if (transferShuttleList.isEmpty()) {
            return;
        }
        Integer parentTransportTimeToHub = parentStop.getTransportTimeToHub();
        if (parentBus instanceof Shuttle) {
            // Avoid stack overflow if 2 shuttles bite each others tail
            parentTransportTimeToHub = null;
        }
        for (Shuttle shuttle : transferShuttleList) {
            updateTransportTimeToHubOfShuttle(scoreDirector, parentStop, parentTransportTimeToHub, shuttle);
        }
    }

    private void updateTransportTimeToHubOfShuttle(ScoreDirector scoreDirector, Shuttle shuttle) {
        StopOrHub destination = shuttle.getDestination();
        Integer destinationTransportTimeToHub = (destination == null) ? null : destination.getTransportTimeToHub();
        updateTransportTimeToHubOfShuttle(scoreDirector, destination, destinationTransportTimeToHub, shuttle);
    }

    private void updateTransportTimeToHubOfShuttle(ScoreDirector scoreDirector, StopOrHub parentStop, Integer parentTransportTimeToHub, Shuttle shuttle) {
        BusStop lastStop = null;
        for (BusStop stop = shuttle.getNextStop(); stop != null; stop = stop.getNextStop()) {
            lastStop = stop;
        }
        if (lastStop == null) {
            return;
        }
        Integer transportTimeToHub = parentTransportTimeToHub;
        if (transportTimeToHub != null) {
            transportTimeToHub += shuttle.getDurationFromTo(lastStop.getLocation(), parentStop.getLocation());
        }
        updateTransportTime(scoreDirector, lastStop, shuttle, transportTimeToHub);
    }

}
