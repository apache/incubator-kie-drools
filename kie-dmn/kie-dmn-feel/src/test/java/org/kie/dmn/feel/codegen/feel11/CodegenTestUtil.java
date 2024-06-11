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
package org.kie.dmn.feel.codegen.feel11;

import org.kie.dmn.feel.lang.EvaluationContext;
import org.kie.dmn.feel.lang.FEELDialect;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.lang.impl.FEELEventListenersManager;
import org.kie.dmn.feel.util.ClassLoaderUtil;

public class CodegenTestUtil {

    private CodegenTestUtil() {
        // only static methods for util class.
    }

    public static EvaluationContext newEmptyEvaluationContext(FEELEventListenersManager mgr) {
        // Defaulting FEELDialect to FEEL
        return new EvaluationContextImpl(ClassLoaderUtil.findDefaultClassLoader(), mgr, FEELDialect.FEEL);
    }

    public static EvaluationContext newEmptyEvaluationContext() {
        // Defaulting FEELDialect to FEEL
        return new EvaluationContextImpl(ClassLoaderUtil.findDefaultClassLoader(), null, FEELDialect.FEEL);
    }
}
