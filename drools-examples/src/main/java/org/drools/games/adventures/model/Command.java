package org.drools.games.adventures.model;

import org.drools.games.adventures.UserSession;
import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public abstract class Command {

    @Position(0)
    private UserSession session;

    public Command() {
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

}
