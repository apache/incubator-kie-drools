package org.optaplanner.examples.tennis.persistence;

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.LoggingMain;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.tennis.app.TennisApp;
import org.optaplanner.examples.tennis.domain.Day;
import org.optaplanner.examples.tennis.domain.Team;
import org.optaplanner.examples.tennis.domain.TeamAssignment;
import org.optaplanner.examples.tennis.domain.TennisSolution;
import org.optaplanner.examples.tennis.domain.UnavailabilityPenalty;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class TennisGenerator extends LoggingMain {

    public static void main(String[] args) {
        new TennisGenerator().generate();
    }

    protected final SolutionFileIO<TennisSolution> solutionFileIO = new TennisSolutionFileIO();
    protected final File outputDir;

    public TennisGenerator() {
        this.outputDir = new File(CommonApp.determineDataDir(TennisApp.DATA_DIR_NAME), "unsolved");
    }

    public void generate() {
        File outputFile = new File(outputDir, "munich-7teams.json");
        TennisSolution tennisSolution = createTennisSolution();
        solutionFileIO.write(tennisSolution, outputFile);
        logger.info("Saved: {}", outputFile);
    }

    public TennisSolution createTennisSolution() {
        TennisSolution tennisSolution = new TennisSolution(0L);

        List<Team> teamList = new ArrayList<>();
        teamList.add(new Team(0L, "Micha"));
        teamList.add(new Team(1L, "Angelika"));
        teamList.add(new Team(2L, "Katrin"));
        teamList.add(new Team(3L, "Susi"));
        teamList.add(new Team(4L, "Irene"));
        teamList.add(new Team(5L, "Kristina"));
        teamList.add(new Team(6L, "Tobias"));
        tennisSolution.setTeamList(teamList);

        List<Day> dayList = new ArrayList<>();
        for (int i = 0; i < 18; i++) {
            dayList.add(new Day(i, i));
        }
        tennisSolution.setDayList(dayList);

        List<UnavailabilityPenalty> unavailabilityPenaltyList = new ArrayList<>();
        unavailabilityPenaltyList.add(new UnavailabilityPenalty(0L, teamList.get(4), dayList.get(0)));
        unavailabilityPenaltyList.add(new UnavailabilityPenalty(1L, teamList.get(6), dayList.get(1)));
        unavailabilityPenaltyList.add(new UnavailabilityPenalty(2L, teamList.get(2), dayList.get(2)));
        unavailabilityPenaltyList.add(new UnavailabilityPenalty(3L, teamList.get(4), dayList.get(3)));
        unavailabilityPenaltyList.add(new UnavailabilityPenalty(4L, teamList.get(4), dayList.get(5)));
        unavailabilityPenaltyList.add(new UnavailabilityPenalty(5L, teamList.get(2), dayList.get(6)));
        unavailabilityPenaltyList.add(new UnavailabilityPenalty(6L, teamList.get(1), dayList.get(8)));
        unavailabilityPenaltyList.add(new UnavailabilityPenalty(7L, teamList.get(2), dayList.get(9)));
        unavailabilityPenaltyList.add(new UnavailabilityPenalty(8L, teamList.get(4), dayList.get(10)));
        unavailabilityPenaltyList.add(new UnavailabilityPenalty(9L, teamList.get(4), dayList.get(11)));
        unavailabilityPenaltyList.add(new UnavailabilityPenalty(10L, teamList.get(6), dayList.get(12)));
        unavailabilityPenaltyList.add(new UnavailabilityPenalty(11L, teamList.get(5), dayList.get(15)));
        tennisSolution.setUnavailabilityPenaltyList(unavailabilityPenaltyList);

        List<TeamAssignment> teamAssignmentList = new ArrayList<>();
        long id = 0L;
        for (Day day : dayList) {
            for (int i = 0; i < 4; i++) {
                teamAssignmentList.add(new TeamAssignment(id, day, i));
                id++;
            }
        }
        tennisSolution.setTeamAssignmentList(teamAssignmentList);

        BigInteger possibleSolutionSize = BigInteger.valueOf(teamList.size()).pow(
                teamAssignmentList.size());
        logger.info("Tennis {} has {} teams, {} days, {} unavailabilityPenalties and {} teamAssignments"
                + " with a search space of {}.",
                "munich-7teams", teamList.size(), dayList.size(), unavailabilityPenaltyList.size(), teamAssignmentList.size(),
                AbstractSolutionImporter.getFlooredPossibleSolutionSize(possibleSolutionSize));
        return tennisSolution;
    }

}
