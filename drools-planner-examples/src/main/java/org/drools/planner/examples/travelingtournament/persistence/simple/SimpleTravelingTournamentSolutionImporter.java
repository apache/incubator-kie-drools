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

package org.drools.planner.examples.travelingtournament.persistence.simple;

import java.util.List;

import org.drools.planner.examples.travelingtournament.domain.Day;
import org.drools.planner.examples.travelingtournament.domain.Match;
import org.drools.planner.examples.travelingtournament.domain.TravelingTournament;
import org.drools.planner.examples.travelingtournament.persistence.TravelingTournamentSolutionImporter;

public class SimpleTravelingTournamentSolutionImporter extends TravelingTournamentSolutionImporter {

    public static void main(String[] args) {
        new SimpleTravelingTournamentSolutionImporter().convertAll();
    }

    public SimpleTravelingTournamentSolutionImporter() {
        super(new SimpleTravelingTournamentDaoImpl());
    }

    public TxtInputBuilder createTxtInputBuilder() {
        return new SimpleTravelingTournament();
    }

    public class SimpleTravelingTournament extends TravelingTournamentInputBuilder {

        protected void initializeMatchDays(TravelingTournament travelingTournament) {
            List<Match> matchList = travelingTournament.getMatchList();
            List<Day> dayList = travelingTournament.getDayList();
            for (Match match : matchList) {
                match.setDay(dayList.get(0));
            }
        }

    }

}
