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
import java.util.regex.PatternSyntaxException;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.util.XQueryImplUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatchesFunction
        extends BaseFEELFunction {
    private static final Logger log = LoggerFactory.getLogger(MatchesFunction.class);
    public static final MatchesFunction INSTANCE = new MatchesFunction();

    private MatchesFunction() {
        super( "matches" );
    }

    public FEELFnResult<Boolean> FEELFnResult(@ParameterName("input") String input, @ParameterName("pattern") String pattern) {
        return invoke( input, pattern, null );
    }

    public FEELFnResult<Boolean> invoke(@ParameterName("input") String input, @ParameterName("pattern") String pattern, @ParameterName("flags") String flags) {
        try {
            return matchFunctionWithFlags(input,pattern,flags);
        } catch (InvalidParameterException t ) {
            return FEELFnResult.ofError( new InvalidParametersEvent( Severity.ERROR, t.getMessage(), "cannot be null or is invalid", t ) );
        } catch (Throwable t) {
            String errorMessage;
            if (t.getMessage() != null && !t.getMessage().isEmpty()) {
                errorMessage = "Error: " + t.getMessage();
            } else {
                errorMessage = String.format("Some of the provided parameters might be invalid. Input: '%s', Pattern: '%s', Flags: '%s'",
                        input, pattern, flags);
            }
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, errorMessage, t));
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
        return FEELFnResult.ofResult(XQueryImplUtil.executeMatchesFunction(input, pattern, flags));
    }
}
