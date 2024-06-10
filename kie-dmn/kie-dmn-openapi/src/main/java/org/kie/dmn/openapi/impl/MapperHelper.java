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
package org.kie.dmn.openapi.impl;

import org.kie.dmn.feel.lang.FEELDialect;
import org.kie.dmn.feel.lang.ast.AtLiteralNode;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.impl.EvaluationContextImpl;
import org.kie.dmn.feel.util.ClassLoaderUtil;

/**
 * Class meant to provide evaluation of nodes when there is not a viable alternative to manually parse it
 */
public class MapperHelper {

    static Object evaluateInfixOpNode(InfixOpNode toEvaluate) {
        // Defaulting FEELDialect to FEEL
        return toEvaluate.evaluate(new EvaluationContextImpl(ClassLoaderUtil.findDefaultClassLoader(), null, FEELDialect.FEEL));
    }

    static Object evaluateAtLiteralNode(AtLiteralNode toEvaluate) {
        // Defaulting FEELDialect to FEEL
        return toEvaluate.evaluate(new EvaluationContextImpl(ClassLoaderUtil.findDefaultClassLoader(), null, FEELDialect.FEEL));
    }

    private MapperHelper() {
        // deliberate intention not to allow instantiation of this class.
    }
}