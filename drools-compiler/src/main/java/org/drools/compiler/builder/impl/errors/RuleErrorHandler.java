/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.builder.impl.errors;

import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.compiler.RuleBuildError;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.core.definitions.rule.impl.RuleImpl;

public class RuleErrorHandler extends ErrorHandler {

    private BaseDescr descr;

    private RuleImpl rule;

    public RuleErrorHandler(final BaseDescr ruleDescr,
                            final RuleImpl rule,
                            final String message) {
        this.descr = ruleDescr;
        this.rule = rule;
        this.message = message;
    }

    public DroolsError getError() {
        return new RuleBuildError(this.rule,
                                  this.descr,
                                  collectCompilerProblems(),
                                  this.message);
    }

}
