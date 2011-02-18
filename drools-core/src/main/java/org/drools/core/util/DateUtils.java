/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.util;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.drools.type.DateFormats;

/**
 * @author etirelli
 *
 */
public class DateUtils {

    private static final long serialVersionUID = 510l;
    private static final String DEFAULT_FORMAT_MASK = "dd-MMM-yyyy";
    private static final String DATE_FORMAT_MASK = getDateFormatMask();
    private static final String DEFAULT_COUNTRY = Locale.getDefault()
            .getCountry();
    private static final String DEFINE_COUNTRY = getDefaultContry();
    private static final String DEFAULT_LANGUAGE = Locale.getDefault()
            .getLanguage();
    private static final String DEFINE_LANGUAGE = getDefaultLanguage();

    private static ThreadLocal<SimpleDateFormat> df = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            DateFormatSymbols dateSymbol = new DateFormatSymbols(new Locale(
                    DEFINE_LANGUAGE, DEFINE_COUNTRY));
            SimpleDateFormat dateFormat = new SimpleDateFormat(
                    DATE_FORMAT_MASK, dateSymbol);
            return dateFormat;
        };
    };

    private static String getDefaultLanguage() {
        String fmt = System.getProperty("drools.defaultlanguage");
        if (fmt == null) {
            fmt = DEFAULT_LANGUAGE;
        }
        return fmt;
    }

    private static String getDefaultContry() {
        String fmt = System.getProperty("drools.defaultcountry");
        if (fmt == null) {
            fmt = DEFAULT_COUNTRY;
        }
        return fmt;
    }

    /** Use the simple date formatter to read the date from a string */
    public static Date parseDate(final String input, DateFormats dateFormats) {
        try {
//		    if (  dateFormat != null ) {
//		        return dateFormat.parse( input );
//		    } else {
//		        return df.get().parse(input);
//		    }
            return df.get().parse(input);
        } catch (final ParseException e) {
            throw new IllegalArgumentException("Invalid date input format: ["
                    + input + "] it should follow: [" + DATE_FORMAT_MASK + "]");
        }
    }

    /** Converts the right hand side date as appropriate */
    public static Date getRightDate(final Object object2, DateFormats dateFormats) {
        if (object2 == null) {
            return null;
        }
        if (object2 instanceof String) {
            return parseDate((String) object2, dateFormats);
        } else if (object2 instanceof Date) {
            return (Date) object2;
        } else {
            throw new IllegalArgumentException("Unable to convert "
                    + object2.getClass() + " to a Date.");
        }
    }

    /** Check for the system property override, if it exists */
    public static String getDateFormatMask() {
        String fmt = System.getProperty("drools.dateformat");
        if (fmt == null) {
            fmt = DEFAULT_FORMAT_MASK;
        }
        return fmt;
    }
}
