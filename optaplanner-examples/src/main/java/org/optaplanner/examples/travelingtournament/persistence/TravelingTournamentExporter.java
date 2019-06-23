/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.travelingtournament.persistence;

import java.io.IOException;

import org.optaplanner.examples.common.persistence.AbstractTxtSolutionExporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;
import org.optaplanner.examples.travelingtournament.app.TravelingTournamentApp;
import org.optaplanner.examples.travelingtournament.domain.Day;
import org.optaplanner.examples.travelingtournament.domain.Match;
import org.optaplanner.examples.travelingtournament.domain.Team;
import org.optaplanner.examples.travelingtournament.domain.TravelingTournament;

public class TravelingTournamentExporter extends AbstractTxtSolutionExporter<TravelingTournament> {

    private static final String OUTPUT_FILE_SUFFIX = "trick.txt";

    public static void main(String[] args) {
        SolutionConverter<TravelingTournament> converter = SolutionConverter.createExportConverter(
                TravelingTournamentApp.DATA_DIR_NAME, TravelingTournament.class, new TravelingTournamentExporter());
        converter.convertAll();
    }

    @Override
    public String getOutputFileSuffix() {
        return OUTPUT_FILE_SUFFIX;
    }

    @Override
    public TxtOutputBuilder<TravelingTournament> createTxtOutputBuilder() {
        return new TravelingTournamentOutputBuilder();
    }

    public static class TravelingTournamentOutputBuilder extends TxtOutputBuilder<TravelingTournament> {

        @Override
        public void writeSolution() throws IOException {
            int maximumTeamNameLength = 0;
            for (Team team : solution.getTeamList()) {
                if (team.getName().length() > maximumTeamNameLength) {
                    maximumTeamNameLength = team.getName().length();
                }
            }
            for (Team team : solution.getTeamList()) {
                bufferedWriter.write(String.format("%-" + (maximumTeamNameLength + 3) + "s", team.getName()));
            }
            bufferedWriter.write("\n");
            for (Team team : solution.getTeamList()) {
                bufferedWriter.write(String.format("%-" + (maximumTeamNameLength + 3) + "s", team.getName().replaceAll("[\\w\\d]", "-")));
            }
            bufferedWriter.write("\n");
            for (Day day : solution.getDayList()) {
                for (Team team : solution.getTeamList()) {
                    // this could be put in a hashmap first for performance
                    boolean opponentIsHome = false;
                    Team opponentTeam = null;
                    for (Match match : solution.getMatchList()) {
                        if (match.getDay().equals(day)) {
                            if (match.getHomeTeam().equals(team)) {
                                opponentIsHome = false;
                                opponentTeam = match.getAwayTeam();
                            } else if (match.getAwayTeam().equals(team)) {
                                opponentIsHome = true;
                                opponentTeam = match.getHomeTeam();
                            }
                        }
                    }

                    if (opponentTeam != null) {
                        String opponentName = (opponentIsHome ? "@" : "") + opponentTeam.getName();
                        bufferedWriter.write(String.format("%-" + (maximumTeamNameLength + 3) + "s", opponentName));
                    }
                }
                bufferedWriter.write("\n");
            }
        }

    }

}
