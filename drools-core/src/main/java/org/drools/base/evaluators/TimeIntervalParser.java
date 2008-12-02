/*
 * Copyright 2008 Red Hat
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
 *
 */
package org.drools.base.evaluators;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.drools.RuntimeDroolsException;

/**
 * A parameters parser that uses JodaTime for time units parsing.
 *  
 * @author etirelli
 */
public class TimeIntervalParser
    implements
    EvaluatorParametersParser {

    // Simple syntax
    private static final Pattern SIMPLE  = Pattern.compile( "([+-])?((\\d+)[Dd])?\\s*((\\d+)[Hh])?\\s*((\\d+)[Mm])?\\s*((\\d+)[Ss])?\\s*((\\d+)([Mm][Ss])?)?" );
    private static final int     SIM_SGN = 1;
    private static final int     SIM_DAY = 3;
    private static final int     SIM_HOU = 5;
    private static final int     SIM_MIN = 7;
    private static final int     SIM_SEC = 9;
    private static final int     SIM_MS  = 11;

    // ISO 8601 compliant
    //    private static final Pattern ISO8601   = Pattern.compile( "(P((\\d+)[Yy])?((\\d+)[Mm])?((\\d+)[Dd])?)?(T((\\d+)[Hh])?((\\d+)[Mm])?((\\d+)[Ss])?((\\d+)([Mm][Ss])?)?)?" );

    private static final long    SEC_MS  = 1000;
    private static final long    MIN_MS  = 60 * SEC_MS;
    private static final long    HOU_MS  = 60 * MIN_MS;
    private static final long    DAY_MS  = 24 * HOU_MS;

    //    private static final long    MON_MS = 30 * DAY_MS;
    //    private static final long    YEA_MS = 365 * DAY_MS;

    /**
     * @inheritDoc
     * 
     * @see org.drools.base.evaluators.EvaluatorParametersParser#parse(java.lang.String)
     */
    public Long[] parse(String paramText) {
        if ( paramText == null || paramText.trim().length() == 0 ) {
            return new Long[0];
        }
        String[] params = paramText.split( "," );
        Long[] result = new Long[params.length];
        int index = 0;
        for ( String param : params ) {
            String trimmed = param.trim();
            if ( trimmed.length() > 0 ) {
                Matcher mat = SIMPLE.matcher( trimmed );
                if ( mat.matches() ) {
                    int days = (mat.group( SIM_DAY ) != null) ? Integer.parseInt( mat.group( SIM_DAY ) ) : 0;
                    int hours = (mat.group( SIM_HOU ) != null) ? Integer.parseInt( mat.group( SIM_HOU ) ) : 0;
                    int min = (mat.group( SIM_MIN ) != null) ? Integer.parseInt( mat.group( SIM_MIN ) ) : 0;
                    int sec = (mat.group( SIM_SEC ) != null) ? Integer.parseInt( mat.group( SIM_SEC ) ) : 0;
                    int ms = (mat.group( SIM_MS ) != null) ? Integer.parseInt( mat.group( SIM_MS ) ) : 0;
                    long r = days * DAY_MS + hours * HOU_MS + min * MIN_MS + sec * SEC_MS + ms;
                    if( mat.group(SIM_SGN) != null && mat.group( SIM_SGN ).equals( "-" ) ) {
                        r = -r;
                    }
                    result[index] = new Long( r );
                } else if( "*".equals( trimmed ) || "+*".equals( trimmed ) ) {
                    // positive infinity
                    result[index] = Long.MAX_VALUE;
                } else if( "-*".equals( trimmed ) ) {
                    // negative infinity
                    result[index] = Long.MIN_VALUE;
                } else {
                    throw new RuntimeDroolsException( "Error parsing interval value: " + param );
                }
            } else {
                throw new RuntimeDroolsException( "Empty parameters not allowed in: [" + paramText + "]" );
            }
            index++;
        }
        return result;
    }

}
