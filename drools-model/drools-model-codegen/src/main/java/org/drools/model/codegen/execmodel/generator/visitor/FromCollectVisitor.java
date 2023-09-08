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
package org.drools.model.codegen.execmodel.generator.visitor;

import org.drools.drl.ast.descr.AccumulateDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.CollectDescr;
import org.drools.drl.ast.descr.PatternDescr;

public class FromCollectVisitor {

    public static final String GENERIC_COLLECT = "genericCollect";

    private final ModelGeneratorVisitor parentVisitor;

    public FromCollectVisitor(ModelGeneratorVisitor parentVisitor) {
        this.parentVisitor = parentVisitor;
    }

    public void transformFromCollectToCollectList(PatternDescr pattern, CollectDescr collectDescr) {
        // The inner pattern of the "from collect" needs to be processed to have the binding
        final PatternDescr collectDescrInputPattern = collectDescr.getInputPattern();
        if (!parentVisitor.initPattern( collectDescrInputPattern )) {
            return;
        }

        String collectTarget = pattern.getObjectType();

        final AccumulateDescr accumulateDescr = new AccumulateDescr();
        accumulateDescr.setInputPattern(collectDescrInputPattern);
        accumulateDescr.addFunction(getCollectFunction(collectTarget), null, false, new String[]{collectDescrInputPattern.getIdentifier()});

        final PatternDescr transformedPatternDescr = new PatternDescr(collectTarget, pattern.getIdentifier());
        for (BaseDescr o : pattern.getConstraint().getDescrs()) {
            transformedPatternDescr.addConstraint(o);
        }
        transformedPatternDescr.setSource(accumulateDescr);
        transformedPatternDescr.accept(parentVisitor);
    }

    private static String getCollectFunction(String collectTarget) {
        switch (collectTarget) {
            case "Collection":
            case "java.util.Collection":
            case "List":
            case "java.util.List":
                return "collectList";
            case "Set":
            case "java.util.Set":
                return "collectSet";
            default: return GENERIC_COLLECT;
        }
    }
}
