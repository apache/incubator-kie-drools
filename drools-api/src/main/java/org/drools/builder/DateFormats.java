package org.drools.builder;

import java.text.DateFormat;
import java.util.Date;

public interface DateFormats {
    DateFormat get(String identifier);

    void set(String identifier,
             DateFormat value);
    
    Date parse(String identifer, String date);
}
