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
package org.drools.compiler.rule.builder;

import org.drools.base.rule.Pattern;
import org.drools.base.rule.RuleConditionElement;
import org.drools.base.rule.WindowReference;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.WindowReferenceDescr;

/**
 * A class capable of building window source references
 */
public class WindowReferenceBuilder
    implements
    RuleConditionBuilder {

    public RuleConditionElement build(RuleBuildContext context,
                                      BaseDescr descr) {
        return build( context,
                      descr,
                      null );
    }

    public RuleConditionElement build(RuleBuildContext context,
                                      BaseDescr descr,
                                      Pattern prefixPattern) {
        final WindowReferenceDescr window = (WindowReferenceDescr) descr;

        if ( !context.getPkg().getWindowDeclarations().containsKey( window.getName() ) ) {
            context.addError(new DescrBuildError(context.getParentDescr(),
                                                 descr,
                                                 null,
                                                 "Unknown window " + window.getName()));
        }

        return new WindowReference( window.getName() );
    }

}
