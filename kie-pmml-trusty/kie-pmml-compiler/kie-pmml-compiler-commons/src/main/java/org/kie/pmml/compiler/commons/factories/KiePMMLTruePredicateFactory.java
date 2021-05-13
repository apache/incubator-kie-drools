/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.compiler.commons.factories;

import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KiePMMLTruePredicateFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTruePredicateFactory.class.getName());

    private KiePMMLTruePredicateFactory() {
    }

    public static BlockStmt getTruePredicateBody() {
        final BlockStmt toReturn = new BlockStmt();
        final ReturnStmt returnStmt = new ReturnStmt();
        returnStmt.setExpression(new NameExpr("true"));
        toReturn.addStatement(returnStmt);
        return toReturn;
    }
}
