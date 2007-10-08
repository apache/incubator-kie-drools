package org.drools.tutorials.banking;

import java.text.SimpleDateFormat;

import java.util.Date;


public class SimpleDate extends Date {
    private static final SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
    
    public SimpleDate(String datestr) throws Exception {             
        setTime(format.parse(datestr).getTime());
    }

}
