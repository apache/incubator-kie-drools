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
package org.kie.dmn.feel.runtime.functions;

import java.security.InvalidParameterException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatchesFunction
        extends BaseFEELFunction {
    private static final Logger log = LoggerFactory.getLogger(MatchesFunction.class);
    public static final MatchesFunction INSTANCE = new MatchesFunction();

    private MatchesFunction() {
        super( "matches" );
    }

    public FEELFnResult<Boolean> invoke(@ParameterName("input") String input, @ParameterName("pattern") String pattern) {
        return invoke( input, pattern, null );
    }

    public FEELFnResult<Boolean> invoke(@ParameterName("input") String input, @ParameterName("pattern") String pattern, @ParameterName("flags") String flags) {
        try {
            return matchFunctionWithFlags(input,pattern,flags);
        } catch ( PatternSyntaxException t ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "pattern", "is invalid and can not be compiled", t ) );
        } catch (InvalidParameterException t ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, t.getMessage(), "cannot be null", t ) );
        } catch (IllegalArgumentException t ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "flags", "contains unknown flags", t ) );
        } catch (Throwable t) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, "pattern", "is invalid and can not be compiled", t ) );
        }
    }

    static FEELFnResult<Boolean> matchFunctionWithFlags(String input, String pattern, String flags) {
        log.debug("Input:  {} , Pattern: {}, Flags: {}", input, pattern, flags);
        if ( input == null ) {
            throw new InvalidParameterException("input");
        }
        if ( pattern == null ) {
            throw new InvalidParameterException("pattern");
        }
            final String flagsString;
            if (flags != null && !flags.isEmpty()) {
                checkFlags(flags);
                if(!flags.contains("U")){
                    flags += "U";
                }
                flagsString = String.format("(?%s)", flags);
            } else {
                flagsString = "";
            }
            log.debug("flagsString: {}", flagsString);
            String stringToBeMatched = flagsString + pattern;
            log.debug("stringToBeMatched: {}", stringToBeMatched);
            Pattern p=Pattern.compile(stringToBeMatched);
            Matcher m = p.matcher( input );
            boolean matchFound=m.find();
            log.debug("matchFound: {}", matchFound);
            return FEELFnResult.ofResult(matchFound);
    }

   static void checkFlags(String flags) {
        Set<Character> allowedChars = Set.of('s','i','x','m');
         boolean isValidFlag= flags.chars()
                .mapToObj(c -> (char) c)
                .allMatch(allowedChars::contains)
                && flags.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.toSet())
                .size() == flags.length();
         if(!isValidFlag){
             throw new IllegalArgumentException("Not a valid flag parameter " +flags);
         }
    }
}
