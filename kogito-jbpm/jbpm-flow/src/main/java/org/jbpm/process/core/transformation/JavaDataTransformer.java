/*
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
package org.jbpm.process.core.transformation;

import java.util.Map;
import java.util.function.Function;

import org.kie.api.runtime.process.DataTransformer;

public class JavaDataTransformer implements DataTransformer {

    @Override
    public Object compile(String expression, Map<String, Object> parameters) {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object transform(Object expression, Map<String, Object> parameters) {
        return ((Function<Map<String, Object>, Object>) expression).apply(parameters);
    }

}
