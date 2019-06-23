/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
import java.util.Objects;

import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.coachshuttlegathering.domain.Bus;
import org.optaplanner.examples.coachshuttlegathering.domain.BusOrStop;
import org.optaplanner.examples.coachshuttlegathering.domain.BusStop;
import org.optaplanner.examples.coachshuttlegathering.domain.Shuttle;
import org.optaplanner.examples.coachshuttlegathering.domain.StopOrHub;

public class TransportTimeToHubUpdatingVariableListener implements VariableListener<BusOrStop> {

    @Override
    public void beforeEntityAdded(ScoreDirector scoreDirector, BusOrStop busOrStop) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector scoreDirector, BusOrStop busOrStop) {
        if (busOrStop instanceof BusStop) {
            updateTransportTimeToHub(scoreDirector, (BusStop) busOrStop);
        } else if (busOrStop instanceof Shuttle) {
            updateTransportTimeToHubOfShuttle(scoreDirector, (Shuttle) busOrStop);
        }
    }

    @Override
    public void beforeVariableChanged(ScoreDirector scoreDirector, BusOrStop busOrStop) {
        // Do nothing
    }

    @Override
    public void afterVariableChanged(ScoreDirector scoreDirector, BusOrStop busOrStop) {
        if (busOrStop instanceof BusStop) {
            updateTransportTimeToHub(scoreDirector, (BusStop) busOrStop);
        } else if (busOrStop instanceof Shuttle) {
            updateTransportTimeToHubOfShuttle(scoreDirector, (Shuttle) busOrStop);
        }
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector scoreDirector, BusOrStop busOrStop) {
        // Do nothing
    }

    @Override
    public void afterEntityRemoved(ScoreDirector scoreDirector, BusOrStop busOrStop) {
        // Do nothing
    }

    protected void updateTransportTimeToHub(ScoreDirector scoreDirector, BusStop sourceStop) {
        Bus bus = sourceStop.getBus();
        Integer transportTimeToHub;
        if (bus == null) {
            transportTimeToHub = null;
        } else {
            StopOrHub destination = bus.getDestination();
            if (destination instanceof BusStop // Also implies bus is a Shuttle because a Coach destination is a Hub
                    && ((BusStop) destination).getBus() instanceof Shuttle) {
                // A shuttle that follows a shuttle should have only transportTimeToHub null
                transportTimeToHub = null;
            } else {
                StopOrHub next = sourceStop.getNextStop();
                if (next != null) {
                    transportTimeToHub = next.getTransportTimeToHub();
                } else if (destination != null) {
                    transportTimeToHub = destination.getTransportTimeToHub();
                    next = destination;
                } else {
                    transportTimeToHub = null;
                }
                transportTimeToHub = addTransportTime(transportTimeToHub, sourceStop, next);
            }
        }
        updateTransportTime(scoreDirector, sourceStop, bus, transportTimeToHub);
    }

    private void updateTransportTime(ScoreDirector scoreDirector, BusStop sourceStop, Bus bus, Integer transportTimeToHub) {
        if (sourceStop == null) {
            throw new IllegalArgumentException("The sourceStop (" + sourceStop + ") cannot be null.");
        }
        if (Objects.equals(sourceStop.getTransportTimeToHub(), transportTimeToHub)) {
            return;
        }
        scoreDirector.beforeVariableChanged(sourceStop, "transportTimeToHub");
        sourceStop.setTransportTimeToHub(transportTimeToHub);
        scoreDirector.afterVariableChanged(sourceStop, "transportTimeToHub");
        updateTransportTimeForTransferShuttleList(scoreDirector, sourceStop, bus);
        BusStop toStop = sourceStop;
        for (BusOrStop busOrStop = sourceStop.getPreviousBusOrStop(); busOrStop instanceof BusStop;) {
            BusStop stop = (BusStop) busOrStop;
            transportTimeToHub = addTransportTime(transportTimeToHub, stop, toStop);
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
            // Avoid stack overflow if 2 shuttles bite each others tail or if 1 shuttle bites its own tail
            parentTransportTimeToHub = null;
        }
        for (Shuttle shuttle : transferShuttleList) {
            updateTransportTimeToHubOfShuttle(scoreDirector, parentStop, parentTransportTimeToHub, shuttle);
        }
    }

    private void updateTransportTimeToHubOfShuttle(ScoreDirector scoreDirector, Shuttle shuttle) {
        StopOrHub destination = shuttle.getDestination();
        Integer destinationTransportTimeToHub;
        if (destination != null) {
            if (destination instanceof BusStop
                    && ((BusStop) destination).getBus() instanceof Shuttle) {
                // A shuttle that follows a shuttle should have only transportTimeToHub null
                destinationTransportTimeToHub = null;
            } else {
                destinationTransportTimeToHub = destination.getTransportTimeToHub();
            }
        } else {
            destinationTransportTimeToHub = null;
        }
        updateTransportTimeToHubOfShuttle(scoreDirector, destination, destinationTransportTimeToHub, shuttle);
    }

    private void updateTransportTimeToHubOfShuttle(ScoreDirector scoreDirector, StopOrHub parentStop, Integer parentTransportTimeToHub, Shuttle shuttle) {
        if (shuttle.getNextStop() == null) {
            return;
        }
        BusStop lastStop = null;
        for (BusStop stop = shuttle.getNextStop(); stop != null; stop = stop.getNextStop()) {
            lastStop = stop;
        }
        Integer transportTimeToHub = parentTransportTimeToHub;
        transportTimeToHub = addTransportTime(transportTimeToHub, lastStop, parentStop);
        updateTransportTime(scoreDirector, lastStop, shuttle, transportTimeToHub);
    }

    private static Integer addTransportTime(Integer transportTimeToHub, BusStop fromStop, StopOrHub toStop) {
        if (transportTimeToHub == null) {
            return null;
        }
        return transportTimeToHub + fromStop.getBus().getDurationFromTo(fromStop.getLocation(), toStop.getLocation());
    }

}
