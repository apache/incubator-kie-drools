package org.optaplanner.examples.travelingtournament.solver.move.factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.optaplanner.core.impl.heuristic.move.Move;
import org.optaplanner.core.impl.heuristic.selector.move.factory.MoveListFactory;
import org.optaplanner.examples.travelingtournament.domain.Day;
import org.optaplanner.examples.travelingtournament.domain.Match;
import org.optaplanner.examples.travelingtournament.domain.Team;
import org.optaplanner.examples.travelingtournament.domain.TravelingTournament;
import org.optaplanner.examples.travelingtournament.solver.move.MatchChainRotationsMove;

public class MatchChainRotationsMoveFactory implements MoveListFactory<TravelingTournament> {

    @Override
    public List<Move<TravelingTournament>> createMoveList(TravelingTournament travelingTournament) {
        List<Move<TravelingTournament>> moveList = new ArrayList<>();
        RotationMovesFactory rotationMovesFactory = new RotationMovesFactory(travelingTournament);
        rotationMovesFactory.addDayRotation(moveList);
        rotationMovesFactory.addTeamRotation(moveList);
        return moveList;
    }

    private static class RotationMovesFactory {

        private List<Day> dayList;
        private List<Team> teamList;
        private List<Match> matchList;
        private Map<Day, Map<Team, Match>> dayTeamMap;
        private Map<Team, Map<Day, Match>> teamDayMap;
        private Map<Team, Map<Team, Match>> homeTeamAwayTeamMap;

        public RotationMovesFactory(TravelingTournament travelingTournament) {
            dayList = travelingTournament.getDayList();
            teamList = travelingTournament.getTeamList();
            matchList = travelingTournament.getMatchList();
            createMaps();
        }

        private void createMaps() {
            dayTeamMap = new HashMap<>(dayList.size());
            for (Day day : dayList) {
                // This map should be ordered so the order of the matchRotationList is the same (when it's used as tabu)
                dayTeamMap.put(day, new LinkedHashMap<>(teamList.size()));
            }
            teamDayMap = new HashMap<>(teamList.size());
            homeTeamAwayTeamMap = new HashMap<>(teamList.size());
            for (Team team : teamList) {
                // This map should be ordered so the order of the matchRotationList is the same (when it's used as tabu)
                teamDayMap.put(team, new LinkedHashMap<>(dayList.size()));
                homeTeamAwayTeamMap.put(team, new LinkedHashMap<>(teamList.size() - 1));
            }
            for (Match match : matchList) {
                Map<Team, Match> subTeamMap = dayTeamMap.get(match.getDay());
                subTeamMap.put(match.getHomeTeam(), match);
                subTeamMap.put(match.getAwayTeam(), match);
                teamDayMap.get(match.getHomeTeam()).put(match.getDay(), match);
                teamDayMap.get(match.getAwayTeam()).put(match.getDay(), match);
                homeTeamAwayTeamMap.get(match.getHomeTeam()).put(match.getAwayTeam(), match);
            }
        }

        private Team getOtherTeam(Match match, Team team) {
            return match.getHomeTeam().equals(team) ? match.getAwayTeam() : match.getHomeTeam();
        }

        /**
         * @TODO clean up this code
         */
        private void addDayRotation(List<Move<TravelingTournament>> moveList) {
            for (ListIterator<Day> firstDayIt = dayList.listIterator(); firstDayIt.hasNext();) {
                Day firstDay = firstDayIt.next();
                Map<Team, Match> firstDayTeamMap = dayTeamMap.get(firstDay);
                for (ListIterator<Day> secondDayIt = dayList.listIterator(firstDayIt.nextIndex()); secondDayIt.hasNext();) {
                    Day secondDay = secondDayIt.next();
                    List<Match> clonedFirstDayMatchList = new ArrayList<>(firstDayTeamMap.values());
                    while (!clonedFirstDayMatchList.isEmpty()) {
                        List<Match> rotateList = new ArrayList<>(4);
                        Match startMatch = clonedFirstDayMatchList.remove(0);
                        boolean otherInFirst = false;
                        rotateList.add(startMatch);
                        Team startHomeTeam = startMatch.getHomeTeam();
                        Team nextTeamToFind = startMatch.getAwayTeam();
                        while (!startHomeTeam.equals(nextTeamToFind)) {
                            Map<Team, Match> subTeamMap = dayTeamMap.get(otherInFirst ? firstDay : secondDay);
                            Match repairMatch = subTeamMap.get(nextTeamToFind);
                            if (otherInFirst) {
                                clonedFirstDayMatchList.remove(repairMatch);
                            }
                            rotateList.add(repairMatch);
                            nextTeamToFind = getOtherTeam(repairMatch, nextTeamToFind);
                            otherInFirst = !otherInFirst;
                        }
                        // assert(rotateList.size() % 2 == 0);

                        // if size is 2 then addCachedHomeAwaySwapMoves will have done it
                        if (rotateList.size() > 2) {
                            List<Match> emptyList = Collections.emptyList();
                            MatchChainRotationsMove rotateMove = new MatchChainRotationsMove(rotateList, emptyList);
                            moveList.add(rotateMove);
                        }
                    }
                }
            }
        }

        /**
         * @TODO clean up this code
         */
        private void addTeamRotation(List<Move<TravelingTournament>> moveList) {
            for (ListIterator<Team> firstTeamIt = teamList.listIterator(); firstTeamIt.hasNext();) {
                Team firstTeam = firstTeamIt.next();
                Map<Day, Match> firstTeamDayMap = teamDayMap.get(firstTeam);
                for (ListIterator<Team> secondTeamIt = teamList.listIterator(firstTeamIt.nextIndex()); secondTeamIt
                        .hasNext();) {
                    Team secondTeam = secondTeamIt.next();
                    List<Match> clonedFirstTeamMatchList = new ArrayList<>(firstTeamDayMap.values());
                    while (!clonedFirstTeamMatchList.isEmpty()) {
                        List<Match> firstRotateList = new ArrayList<>();
                        List<Match> secondRotateList = new ArrayList<>();

                        Match firstStartMatch = clonedFirstTeamMatchList.remove(0);
                        Team firstStartTeam = getOtherTeam(firstStartMatch, firstTeam);
                        Day startDay = firstStartMatch.getDay();
                        boolean firstTeamIsHomeTeam = firstStartMatch.getHomeTeam().equals(firstTeam);
                        Match secondStartMatch = teamDayMap.get(secondTeam).get(startDay);
                        if (firstStartMatch.equals(secondStartMatch)) {
                            break;
                        }
                        firstRotateList.add(0, firstStartMatch);
                        secondRotateList.add(secondStartMatch);
                        Map<Team, Match> visitedTeamMap = new HashMap<>();

                        Team teamToFind = getOtherTeam(secondStartMatch, secondTeam);

                        while (!teamToFind.equals(firstStartTeam)) {
                            //                            boolean shortcut = visitedTeamMap.containsKey(teamToFind);
                            //                            if (shortcut) {
                            Match firstRepairMatch = homeTeamAwayTeamMap
                                    .get(firstTeamIsHomeTeam ? firstTeam : teamToFind)
                                    .get(firstTeamIsHomeTeam ? teamToFind : firstTeam);
                            if (!clonedFirstTeamMatchList.contains(firstRepairMatch)) {
                                if (visitedTeamMap.containsKey(teamToFind)) {
                                    // shortcut splitoff is possible
                                    Match shortcutMatch = visitedTeamMap.get(teamToFind);
                                    int shortcutSize = firstRotateList.indexOf(shortcutMatch) + 1;
                                    int reverseShortcutSize = firstRotateList.size() - shortcutSize;
                                    List<Match> firstShortcutRotateList = new ArrayList<>(
                                            firstRotateList.subList(0, shortcutSize));
                                    for (Match match : firstShortcutRotateList) {
                                        visitedTeamMap.remove(getOtherTeam(match, firstTeam));
                                    }
                                    List<Match> secondShortcutRotateList = new ArrayList<>(
                                            secondRotateList.subList(reverseShortcutSize, secondRotateList.size()));
                                    firstRotateList = new ArrayList<>(
                                            firstRotateList.subList(shortcutSize, firstRotateList.size()));
                                    secondRotateList = new ArrayList<>(
                                            secondRotateList.subList(0, reverseShortcutSize));
                                    addTeamRotateMove(moveList, firstShortcutRotateList, secondShortcutRotateList);
                                }
                                firstTeamIsHomeTeam = !firstTeamIsHomeTeam;
                                //                            Team firstRepairHomeTeam = (firstTeamIsHomeTeam ^ shortcut) ? firstTeam : teamToFind;
                                //                            Team firstRepairAwayTeam = (firstTeamIsHomeTeam ^ shortcut) ? teamToFind : firstTeam;
                                //                            Match firstRepairMatch = homeTeamAwayTeamMap
                                //                                    .get(firstRepairHomeTeam).get(firstRepairAwayTeam);
                                firstRepairMatch = homeTeamAwayTeamMap
                                        .get(firstTeamIsHomeTeam ? firstTeam : teamToFind)
                                        .get(firstTeamIsHomeTeam ? teamToFind : firstTeam);
                            }

                            Day repairDay = firstRepairMatch.getDay();
                            Match secondRepairMatch = teamDayMap.get(secondTeam).get(repairDay);
                            clonedFirstTeamMatchList.remove(firstRepairMatch);
                            visitedTeamMap.put(teamToFind, firstRepairMatch);
                            firstRotateList.add(0, firstRepairMatch);
                            secondRotateList.add(secondRepairMatch);

                            teamToFind = getOtherTeam(secondRepairMatch, secondTeam);
                        }

                        addTeamRotateMove(moveList, firstRotateList, secondRotateList);
                    }
                }
            }

        }

        private void addTeamRotateMove(List<Move<TravelingTournament>> moveList,
                List<Match> firstRotateList, List<Match> secondRotateList) {
            assert (firstRotateList.size() == secondRotateList.size());
            // if size is 1 then addCachedHomeAwaySwapMoves will have done it
            // if size is 2 then addDayRotation will have done it by 1 list of size 4
            if (firstRotateList.size() > 2) {
                MatchChainRotationsMove rotateMove = new MatchChainRotationsMove(firstRotateList, secondRotateList);
                moveList.add(rotateMove);
            }
        }

    }
}
