package org.drools.examples.conway;

public class ConwayAgendaGroupRun extends AbstractRunConway {
    public static void main(final String[] args) {
        ConwayAgendaGroupRun app = new ConwayAgendaGroupRun( );
        app.start( AbstractRunConway.AGENDAGROUP );
    }
}
