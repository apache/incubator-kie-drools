package org.optaplanner.examples.travelingtournament.persistence;

import org.optaplanner.examples.travelingtournament.domain.TravelingTournament;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class TravelingTournamentXmlSolutionFileIO extends XStreamSolutionFileIO<TravelingTournament> {

    public TravelingTournamentXmlSolutionFileIO() {
        super(TravelingTournament.class);
    }
}
