/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.openapi.impl;

import org.eclipse.microprofile.openapi.models.media.Schema;
import org.kie.dmn.feel.lang.ast.FunctionInvocationNode;
import org.kie.dmn.feel.lang.ast.InfixOpNode;
import org.kie.dmn.feel.lang.ast.InfixOperator;
import org.kie.dmn.feel.lang.ast.NumberNode;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.functions.BuiltInFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfixOpNodeSchemaMapper {

    private static final Logger LOG = LoggerFactory.getLogger(InfixOpNodeSchemaMapper.class);

    static void populateSchemaFromFunctionInvocationNode(Schema toPopulate, InfixOpNode infixOpNode) {
        LOG.debug("populateSchemaFromFunctionInvocationNode {} {}", toPopulate, infixOpNode);
        String functionString = ((FunctionInvocationNode) infixOpNode.getLeft()).getName().getText();
        FEELFunction function = BuiltInFunctions.getFunction(functionString);
        InfixOperator operator = infixOpNode.getOperator();
        Object rightValue = infixOpNode.getRight();
        if (rightValue instanceof NumberNode numberNode) {
            rightValue = numberNode.getValue();
        }
        FEELFunctionSchemaMapper.populateSchemaFromFEELFunction(function, operator, rightValue, toPopulate);
    }

    private InfixOpNodeSchemaMapper() {
        // deliberate intention not to allow instantiation of this class.
    }
}