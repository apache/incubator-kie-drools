/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.model.operators;

import org.drools.model.functions.Operator;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public enum MatchesOperator implements Operator.SingleValue<String, String> {

    INSTANCE;

    private static final String CACHE_MATCHES_COMPILED_MAX_PROPERTY = "drools.matches.compiled.cache.count";

    // default to 0 for no cache
    private final static int MAX_SIZE_CACHE = Integer.parseInt(System.getProperty(CACHE_MATCHES_COMPILED_MAX_PROPERTY, "0"));

    // store Pattern for regular expressions using the regular expression as the key up to MAX_SIZE_CACHE entries.
    public final Map<String, Pattern> patternMap = Collections.synchronizedMap(new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry( Map.Entry<String, Pattern> eldest ) {
            return size() > (MAX_SIZE_CACHE);
        }
    });



    // S1 is the candidate string
    // S2 is the regular expression
    @Override
    public boolean eval( String s1, String s2 ) {
        if (s1 == null) {
            return false;
        } else if (MAX_SIZE_CACHE ==0 ) {
            return s1.matches( s2 );
        } else {
            Pattern pattern = patternMap.get( s2 );
            if (pattern == null) {
                //  cache miss on s2, compile it, then store it
                pattern = Pattern.compile(s2);
                patternMap.put(s2, pattern);
            }
            Matcher matcher = pattern.matcher(s1);
            return matcher.matches();
        }
    }

    @Override
    public String getOperatorName() {
        return "matches";
    }
}
