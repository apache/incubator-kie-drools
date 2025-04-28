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

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import org.kie.dmn.api.feel.runtime.events.FEELEvent.Severity;
import org.kie.dmn.feel.runtime.FEELCollectionFunction;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.util.NumberEvalHelper;

import static org.kie.dmn.feel.util.NumberEvalHelper.coerceIntegerNumber;

public class SublistFunction
        extends BaseFEELFunction implements FEELCollectionFunction {

    public static final SublistFunction INSTANCE = new SublistFunction();

    private SublistFunction() {
        super( "sublist" );
    }

    public FEELFnResult<List> invoke(@ParameterName("list") List list, @ParameterName("start position") BigDecimal start) {
        return invoke( list, start, null );
    }

    public FEELFnResult<List> invoke(@ParameterName("list") List list, @ParameterName("start position") BigDecimal start, @ParameterName("length") BigDecimal length) {
        try {
            if (list == null) {
                return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "list", "cannot be null"));
            }
            int coercedStart = coerceIntegerNumber(start).orElseThrow(() -> new NoSuchElementException("start"));
            if (coercedStart == 0) {
                return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "start", "cannot be zero"));
            }
            if (Math.abs(coercedStart) > list.size()) {
                return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "start", "is inconsistent with 'list' size"));
            }

            if (length != null && length.compareTo(BigDecimal.ZERO) <= 0) {
                return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "length", "must be a positive number when specified"));
            }
            int coercedLength = NumberEvalHelper.coerceIntegerNumber(length).orElse(0);
            if (coercedStart > 0) {
                int end = length != null ? coercedStart - 1 + coercedLength : list.size();
                if (end > list.size()) {
                    return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "attempting to create a sublist bigger than the original list"));
                }
                return FEELFnResult.ofResult(list.subList(coercedStart - 1, end));
            } else {
                int end = length != null ? list.size() + coercedStart + coercedLength : list.size();
                if (end > list.size()) {
                    return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, "attempting to create a sublist bigger than the original list"));
                }
                return FEELFnResult.ofResult(list.subList(list.size() + coercedStart, end));
            }
        } catch (NoSuchElementException e) {
            return FEELFnResult.ofError(new InvalidParametersEvent(Severity.ERROR, e.getMessage(), "could not be coerced to Integer: either null or not a valid Number."));
        }
    }
}
