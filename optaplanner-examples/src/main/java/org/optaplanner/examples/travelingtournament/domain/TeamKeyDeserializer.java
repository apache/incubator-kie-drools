package org.optaplanner.examples.travelingtournament.domain;

import org.optaplanner.examples.common.persistence.jackson.AbstractKeyDeserializer;

final class TeamKeyDeserializer extends AbstractKeyDeserializer<Team> {

    public TeamKeyDeserializer() {
        super(Team.class);
    }

    @Override
    protected Team createInstance(long id) {
        return new Team(id);
    }
}
