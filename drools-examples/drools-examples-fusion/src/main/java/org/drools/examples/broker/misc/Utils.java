package org.drools.examples.broker.misc;

import java.text.DecimalFormat;
import java.util.Random;

import org.drools.examples.broker.model.Action;

public class Utils {
    private static final Random rand = new Random(System.currentTimeMillis()); 
    
    public static String percent( double number ) {
        return new DecimalFormat( "0.00%" ).format( number );
    }
    
    public static Action selectAction() {
        return Action.NOACTION;
//        int action = rand.nextInt( 3 );
//        switch (action) {
//            case 1 : return Action.BUY;
//            case 2 : return Action.SELL;
//            default : return Action.NOACTION;
//        }
    }
}
