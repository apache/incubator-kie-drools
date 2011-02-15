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

package org.drools.planner.examples.travelingtournament.persistence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import org.drools.planner.examples.common.persistence.AbstractTxtSolutionImporter;
import org.drools.planner.examples.common.persistence.SolutionDao;
import org.drools.planner.examples.travelingtournament.domain.Day;
import org.drools.planner.examples.travelingtournament.domain.Match;
import org.drools.planner.examples.travelingtournament.domain.Team;
import org.drools.planner.examples.travelingtournament.domain.TravelingTournament;
import org.drools.planner.core.solution.Solution;

public abstract class TravelingTournamentSolutionImporter extends AbstractTxtSolutionImporter {

    protected TravelingTournamentSolutionImporter(SolutionDao solutionDao) {
        super(solutionDao);
    }

    public abstract class TravelingTournamentInputBuilder extends TxtInputBuilder {

        public Solution readSolution() throws IOException {
            TravelingTournament travelingTournament = new TravelingTournament();
            travelingTournament.setId(0L);
            int n = readN();
            List<Team> teamList = readTeamList(n);
            travelingTournament.setTeamList(teamList);
            List<Day> dayList = constructDayList(n);
            travelingTournament.setDayList(dayList);
            List<List<Integer>> outerDistanceList = readOuterDistanceList();
            List<Match> matchList = constructMatchListAndSetDistancesInTeamList(teamList, outerDistanceList);
            travelingTournament.setMatchList(matchList);
            initializeMatchDays(travelingTournament);
            return travelingTournament;
        }

        private int readN() throws IOException {
            return Integer.parseInt(bufferedReader.readLine());
        }

        private List<Team> readTeamList(int n) throws IOException {
            List<Team> teamList = new ArrayList<Team>();
            for (int i = 0; i < n; i++) {
                Team team = new Team();
                team.setId((long) i);
                team.setName(bufferedReader.readLine());
                team.setDistanceToTeamMap(new HashMap<Team, Integer>());
                teamList.add(team);
            }
            return teamList;
        }

        private List<List<Integer>> readOuterDistanceList() throws IOException {
            List<List<Integer>> outerDistanceList = new ArrayList<List<Integer>>();
            String line = bufferedReader.readLine();
            while (line != null && !line.replaceAll("\\s+", "").equals("")) {
                StringTokenizer tokenizer = new StringTokenizer(line.replaceAll("\\s+", " ").trim());
                List<Integer> innerDistanceList = new ArrayList<Integer>();
                while (tokenizer.hasMoreTokens()) {
                    int distance = Integer.parseInt(tokenizer.nextToken());
                    innerDistanceList.add(distance);
                }
                outerDistanceList.add(innerDistanceList);
                line = bufferedReader.readLine();
            }
            return outerDistanceList;
        }

        private List<Day> constructDayList(int n) {
            List<Day> dayList = new ArrayList<Day>();
            int daySize = (n - 1) * 2; // Play vs each team (except itself) twice (home and away)
            Day previousDay = null;
            for (int i = 0; i < daySize; i++) {
                Day day = new Day();
                day.setId((long) i);
                day.setIndex(i);
                dayList.add(day);
                if (previousDay != null) {
                    previousDay.setNextDay(day);
                }
                previousDay = day;
            }
            return dayList;
        }

        private List<Match> constructMatchListAndSetDistancesInTeamList(List<Team> teamList, List<List<Integer>> outerDistanceList) {
            List<Match> matchList = new ArrayList<Match>();
            int i = 0;
            long matchId = 0;
            for (Team homeTeam : teamList) {
                int j = 0;
                for (Team awayTeam : teamList) {
                    int distance = outerDistanceList.get(i).get(j);
                    homeTeam.getDistanceToTeamMap().put(awayTeam, distance);
                    if (i != j) {
                        Match match = new Match();
                        match.setId(matchId);
                        matchId++;
                        match.setHomeTeam(homeTeam);
                        match.setAwayTeam(awayTeam);
                        matchList.add(match);
                    }
                    j++;
                }
                i++;
            }
            return matchList;
        }

        protected abstract void initializeMatchDays(TravelingTournament travelingTournament);

    }

}
