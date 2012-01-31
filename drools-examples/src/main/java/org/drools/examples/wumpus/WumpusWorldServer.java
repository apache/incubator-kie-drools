package org.drools.examples.wumpus;

import java.util.ArrayList;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.examples.wumpus.view.GameUI;
import org.drools.examples.wumpus.view.GameView;
import org.drools.examples.wumpus.view.SensorsView;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;

public class WumpusWorldServer {

    public static void main(String[] args) throws InterruptedException  {
        WumpusWorldServer ww = new WumpusWorldServer();
        ww.init();
    }

    public void init() throws InterruptedException {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        
        kbuilder.add( ResourceFactory.newClassPathResource( "init.drl", getClass() ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( "failed to build:\n" + kbuilder.getErrors().toString() );
        }        
        
        kbuilder.add( ResourceFactory.newClassPathResource( "commands.drl", getClass() ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( "failed to build:\n" + kbuilder.getErrors().toString() );
        }

        kbuilder.add( ResourceFactory.newClassPathResource( "sensors.drl", getClass() ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( "failed to build:\n" + kbuilder.getErrors().toString() );
        }

        kbuilder.add( ResourceFactory.newClassPathResource( "collision.drl", getClass() ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( "failed to build:\n" + kbuilder.getErrors().toString() );
        }
        
        kbuilder.add( ResourceFactory.newClassPathResource( "ui.drl", GameView.class ), ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( "failed to build:\n" + kbuilder.getErrors().toString() );
        }      
        

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ksession.setGlobal("randomInteger",new java.util.Random() );
        ksession.fireAllRules();
    }
    
    public void setData(GameView view) {
        KnowledgeBase kbase = view.getKbase();

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();        
        KnowledgeRuntimeLogger klogger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "log/wumpus");   
        view.setKlogger( klogger );

        List<Pit> pitts = new ArrayList<Pit>();
        java.util.Random gen = new java.util.Random();
        Cell[][] cells = new Cell[5][5];
        for ( int i = 0; i < 5; i++ ) {
            for ( int j = 0; j < 5; j++ ) {
                Cell cell = new Cell( i, j );
                cells[i][j] = cell;
                if ( i == 0 && j == 0 ) {
                    cell.setHidden( false );
                }
                ksession.insert( cell );
                if ( j != 0 && i != 0 && gen.nextInt( 99 ) <= 19 ) {
                    // 20% chance of pitt
                    Pit pitt = new Pit( i, j );
                    ksession.insert( pitt );
                    pitts.add( pitt );
                }
            }
        }

        int row = 0;
        int col = 0;
        while ( row == 0 && col == 0 ) {
            row = gen.nextInt( 4 );
            col = gen.nextInt( 4 );
        }
        Wumpus wumpus = new Wumpus( row, col );

        row = 0;
        col = 0;
        while ( row == 0 && col == 0 ) {
            row = gen.nextInt( 4 );
            col = gen.nextInt( 4 );
        }
        Gold gold = new Gold( row, col );
        Hero hero = new Hero( 0, 0 );

        SensorsView sensors = new SensorsView();
        
        ksession.insert( wumpus );
        ksession.insert( gold );
        ksession.insert( hero );
        ksession.insert( sensors );
        ksession.insert( view );
        
        ksession.fireAllRules();    
        view.init(cells, sensors, pitts, wumpus, gold, hero, 50, 50, 3, 20, 5, 5 );
        view.setKsession( ksession );
    }
}
