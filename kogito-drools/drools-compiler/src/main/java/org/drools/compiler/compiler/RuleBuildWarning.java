/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.compiler;

import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.core.definitions.rule.impl.RuleImpl;

public class RuleBuildWarning extends DescrBuildWarning {
    private final RuleImpl rule;

    public RuleBuildWarning( final RuleImpl rule,
                             final BaseDescr descr,
                             final Object object,
                             final String message ) {
        super( descr, descr, object, message );
        this.rule = rule;
    }

    public RuleImpl getRule() {
        return this.rule;
    }

}
