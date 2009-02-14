package org.drools.process.command;

import org.drools.StatefulSession;

public class FireAllRulesCommand
    implements
    Command<Integer> {

    public FireAllRulesCommand() {
    }

    public Integer execute(StatefulSession session) {
        return session.fireAllRules();
    }

    public String toString() {
        return "session.fireAllRules();";
    }

}
