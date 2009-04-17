package org.drools.examples.broker.ui;

import java.text.DecimalFormat;

public class Utils {
    
    public static String percent( double number ) {
        return new DecimalFormat( "%0.00" ).format( number );
    }
}
