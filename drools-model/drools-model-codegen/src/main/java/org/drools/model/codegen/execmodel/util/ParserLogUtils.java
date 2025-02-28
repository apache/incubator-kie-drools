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
package org.drools.model.codegen.execmodel.util;

import java.util.Optional;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import org.drools.drl.parser.lang.ParserHelper;
import org.drools.model.codegen.execmodel.generator.RuleContext;
import org.drools.mvel.parser.printer.PrintUtil;

/*
 * This class is used to log parser warnings
 * in case that org.drools.drl.parser.lang.ParserHelper's log methods cannot handle,
 * because executable-model delegates constraint parsing to drools-mvel-parser.
 */
public class ParserLogUtils {

    public static void logHalfConstraintWarn(Expression expression, Optional<RuleContext> ruleContextOpt) {
        String halfConstraintStr = PrintUtil.printNode(expression);
        String parentOperatorStr = expression.getParentNode()
                .filter(BinaryExpr.class::isInstance)
                .map(BinaryExpr.class::cast)
                .map(binary -> binary.getOperator().asString())
                .orElse("");
        String ruleName = ruleContextOpt.map(RuleContext::getRuleDescr)
                .map(descr -> descr.getName())
                .orElse("");
        ParserHelper.logHalfConstraintWarn("The use of a half constraint '" + parentOperatorStr + " " + halfConstraintStr + "'" +
                                                   " is deprecated and will be removed in the future version (LanguageLevel.DRL10)." +
                                                   " Please add a left operand in rule '" + ruleName + "'.");
    }
}