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
package org.drools.model.codegen.execmodel.util;

import java.util.Optional;

import org.drools.drl.ast.descr.ExprConstraintDescr;
import org.drools.drl.ast.descr.FromDescr;
import org.drools.drl.ast.descr.MVELExprDescr;
import org.drools.drl.ast.descr.PatternDescr;
import org.drools.drl.parser.lang.XpathAnalysis;
import org.drools.model.codegen.execmodel.generator.QueryParameter;
import org.drools.model.codegen.execmodel.generator.RuleContext;

public class PatternUtil {

    private PatternUtil() {}

    public static PatternDescr normalizeOOPathPattern(PatternDescr pattern, RuleContext context) {
        String oopathExpr = pattern.getDescrs().get(0).getText();
        XpathAnalysis xpathAnalysis = XpathAnalysis.analyze(oopathExpr);
        XpathAnalysis.XpathPart firstPart = xpathAnalysis.getPart(0);

        PatternDescr normalizedPattern = new PatternDescr();
        normalizedPattern.setObjectType(findPatternType(firstPart, context));
        firstPart.getConstraints().stream().map(ExprConstraintDescr::new).forEach(normalizedPattern::addConstraint);

        if (xpathAnalysis.getParts().size() == 1) {
            normalizedPattern.setIdentifier(pattern.getIdentifier());
        } else {
            StringBuilder sb = new StringBuilder();
            if (pattern.getIdentifier() != null) {
                sb.append(pattern.getIdentifier()).append(": ");
            }
            for (int i = 1; i < xpathAnalysis.getParts().size(); i++) {
                sb.append("/").append(xpathAnalysis.getPart(i));
            }
            normalizedPattern.addConstraint(new ExprConstraintDescr(sb.toString()));
        }

        FromDescr source = new FromDescr();
        source.setDataSource(new MVELExprDescr(firstPart.getField()));
        normalizedPattern.setSource(source);
        return normalizedPattern;
    }

    private static String findPatternType(XpathAnalysis.XpathPart firstPart, RuleContext context) {
        if (firstPart.getInlineCast() != null) {
            return firstPart.getInlineCast();
        }

        Optional<QueryParameter> queryParameter = context.getQueryParameterByName(firstPart.getField());
        if (queryParameter.isPresent()) {
            return queryParameter.get().getType().getCanonicalName();
        }

        Class<?> ruleUnitVarType = context.getTypeFromRuleUnitVarsAndScopedDeclarations(firstPart.getField());
        if (ruleUnitVarType == null) {
            throw new IllegalArgumentException("Unknown declaration: " + firstPart.getField());
        }
        return ruleUnitVarType.getCanonicalName();
    }
}
