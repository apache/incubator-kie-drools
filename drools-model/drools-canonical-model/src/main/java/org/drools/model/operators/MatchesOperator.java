/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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

    // not final due to unit tests
    private static int MAX_SIZE_CACHE = getMaxSizeCache();

    // store Pattern for regular expressions using the regular expression as the key up to MAX_SIZE_CACHE entries.
    private static final Map<String, Pattern> patternMap = Collections.synchronizedMap(new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Pattern> eldest) {
            return size() > (MAX_SIZE_CACHE);
        }
    });

    // 0 default disables the Pattern map
    private static int getMaxSizeCache() {
        final String CACHE_MATCHES_COMPILED_MAX_PROPERTY = "drools.matches.compiled.cache.count";
        return Integer.parseInt(System.getProperty(CACHE_MATCHES_COMPILED_MAX_PROPERTY, "0"));
    }

    // package-private for unit testing
    void forceCacheSize(int size) {
        MAX_SIZE_CACHE = size;
        patternMap.clear();
    }

    // package-private for unit testing
    void reInitialize() {
        forceCacheSize(getMaxSizeCache());
    }

    // package-private for unit testing
    int mapSize() {
        return patternMap.size();
    }

    @Override
    public boolean eval(String input, String regex) {
        if (input == null) {
            return false;
        } else if (MAX_SIZE_CACHE == 0) {
            return input.matches(regex);
        } else {
            Pattern pattern = patternMap.get(regex);
            if (pattern == null) {
                //  Cache miss on regex, compile it, store it.
                //  Storing in patternMap may remove the oldest entry per MAX_SIZE_CACHE.
                pattern = Pattern.compile(regex);
                patternMap.put(regex, pattern);
            }
            Matcher matcher = pattern.matcher(input);
            return matcher.matches();
        }
    }

    @Override
    public String getOperatorName() {
        return "matches";
    }
}
