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
package org.kie.dmn.signavio.feel.runtime.functions;

import java.util.Arrays;
import java.util.List;

import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.feel.runtime.events.InvalidParametersEvent;
import org.kie.dmn.feel.runtime.functions.BaseFEELFunction;
import org.kie.dmn.feel.runtime.functions.FEELFnResult;
import org.kie.dmn.feel.runtime.functions.ParameterName;

public class ConcatFunction
		extends BaseFEELFunction {
	
	public ConcatFunction() {
		super("concat");
	}
	
	
	public FEELFnResult<String> invoke(@ParameterName("values") List<?> list) {
		if (list == null) {
			return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "list", "cannot be null"));
		}
		if (list.contains(null)) {
			return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "list", "cannot contain null values"));
		}
		
		StringBuilder sb = new StringBuilder();
		for (Object element : list) {
			if (!(element instanceof String)) {
				return FEELFnResult.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "list", "contains an element that is not a string"));
			}
			sb.append(element);
		}
		return FEELFnResult.ofResult(sb.toString());
	}
	
	
	public FEELFnResult<String> invoke(@ParameterName("values") Object[] list) {
		return invoke(Arrays.asList(list));
	}
	
	
	public FEELFnResult<String> invoke() {
		return FEELFnResult
				.ofError(new InvalidParametersEvent(FEELEvent.Severity.ERROR, "list", "is missing"));
	}
}
