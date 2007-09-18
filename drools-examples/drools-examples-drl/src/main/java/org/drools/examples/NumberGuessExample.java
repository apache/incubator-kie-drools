package org.drools.examples;

import java.io.InputStreamReader;
import java.util.Random;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.compiler.PackageBuilder;
import org.drools.event.DefaultRuleFlowEventListener;
import org.drools.event.RuleFlowEventListener;
import org.drools.event.RuleFlowGroupDeactivatedEvent;

public class NumberGuessExample {

    public static final void main(String[] args) throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( ShoppingExample.class.getResourceAsStream( "NumberGuess.drl" ) ) );

        builder.addRuleFlow( new InputStreamReader( ShoppingExample.class.getResourceAsStream( "NumberGuess.rfm" ) ) );

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( builder.getPackage() );

        final StatefulSession session = ruleBase.newStatefulSession();
        
        final WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( session );
        logger.setFileName( "log/numberguess" );        

        session.insert( new GameRules( 100,
                                       5 ) );
        session.insert( new RandomNumber() );
        session.insert( new Game() );

        session.startProcess( "Number Guess" );
        session.fireAllRules();
        
        logger.writeToDisk();        

        session.dispose();
    }

    public static class RandomNumber {
        private int randomNumber;

        public RandomNumber() {
            this.randomNumber = new Random().nextInt( 100 );
        }

        public int getValue() {
            return this.randomNumber;
        }
    }

    public static class Guess {
        private int value;

        public Guess(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public String toString() {
            return "Guess " + this.value;
        }
    }

    public static class GameRules {
        private int maxRange;
        private int allowedGuesses;

        public GameRules(int maxRange,
                         int allowedGuesses) {
            this.maxRange = maxRange;
            this.allowedGuesses = allowedGuesses;
        }

        public int getAllowedGuesses() {
            return allowedGuesses;
        }

        public int getMaxRange() {
            return maxRange;
        }

    }

    public static class Game {
        private int biggest;
        private int smallest;
        private int guessCount;

        public void begin() {
            this.guessCount = 0;
            this.biggest = 0;
            this.smallest = 100;
        }

        public void incrementGuessCount() {
            guessCount++;
        }

        public int getBiggest() {
            return this.biggest;
        }

        public int getSmallest() {
            return this.smallest;
        }

        public int getGuessCount() {
            return this.guessCount;
        }

        public void setGuessCount(int guessCount) {
            this.guessCount = guessCount;
        }

        public void setBiggest(int biggest) {
            this.biggest = biggest;
        }

        public void setSmallest(int smallest) {
            this.smallest = smallest;
        }
    }
}
