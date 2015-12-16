/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.integrationtests.eventgenerator.example;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Tools {

    // Utitlity functions for console output
    public static String formattedDate (Calendar date){
        return
            ((date.get(Calendar.HOUR_OF_DAY) < 10)?  "0" : "") + date.get(Calendar.HOUR_OF_DAY) + ":" +
            ((date.get(Calendar.MINUTE) < 10)?       "0" : "") + date.get(Calendar.MINUTE)      + ":" +
            ((date.get(Calendar.SECOND) < 10)?       "0" : "") + date.get(Calendar.SECOND) + "." +
            ((date.get(Calendar.MILLISECOND) < 10)? "0" : "") + ((date.get(Calendar.MILLISECOND) < 100)? "0" : "") + date.get(Calendar.MILLISECOND);
    }

    public static String formattedDate (long dateInMillis){
        Calendar date = new GregorianCalendar();
        date.setTimeInMillis(dateInMillis);
        return formattedDate(date);
    }

    public static String formattedInterval(Calendar start, Calendar end){
        return "["+Tools.formattedDate(start)+".."+Tools.formattedDate(end)+"]";
    }

    public static String formattedInterval(long start, long end){
        return "["+Tools.formattedDate(start)+".."+Tools.formattedDate(end)+"]";
    }

    public static void drawLine(){
        System.out.println("----------------------------------------------------------------------------------------------------------------------------------");
    }
}
