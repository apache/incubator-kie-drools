/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.examples.travelingtournament.persistence;

import java.io.File;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.optaplanner.examples.common.persistence.AbstractJsonSolutionFileIO;
import org.optaplanner.examples.travelingtournament.domain.Team;
import org.optaplanner.examples.travelingtournament.domain.TravelingTournament;

public class TravelingTournamentSolutionFileIO extends AbstractJsonSolutionFileIO<TravelingTournament> {

    public TravelingTournamentSolutionFileIO() {
        super(TravelingTournament.class);
    }

    @Override
    public TravelingTournament read(File inputSolutionFile) {
        TravelingTournament travelingTournament = super.read(inputSolutionFile);

        var teamsById = travelingTournament.getTeamList().stream()
                .collect(Collectors.toMap(Team::getId, Function.identity()));
        /*
         * Replace the duplicate team instances in the distanceToTeamMap by references to instances from
         * the teamList.
         */
        for (Team team : travelingTournament.getTeamList()) {
            var newTravelDistanceMap = deduplicateMap(team.getDistanceToTeamMap(),
                    teamsById, Team::getId);
            team.setDistanceToTeamMap(newTravelDistanceMap);
        }
        return travelingTournament;
    }

}
