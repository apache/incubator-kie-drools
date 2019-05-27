/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen.rules.config;

import org.drools.core.config.DefaultRuleEventListenerConfig;
import org.drools.core.config.StaticRuleConfig;

import com.github.javaparser.ast.expr.ObjectCreationExpr;

public class RuleConfigGenerator {
    private String ruleEventListenersConfigClass = DefaultRuleEventListenerConfig.class.getCanonicalName();
    
    public RuleConfigGenerator ruleEventListenersConfig(String cfg) {
        if (cfg == null) {
            throw new IllegalArgumentException("Specified rule listeners config class is undefined (null)!");
        }
        this.ruleEventListenersConfigClass = cfg;
        return this;
    }


    public ObjectCreationExpr newInstance() {
        return new ObjectCreationExpr()
                .setType(StaticRuleConfig.class.getCanonicalName())
                .addArgument(new ObjectCreationExpr().setType(ruleEventListenersConfigClass));
    }
}
