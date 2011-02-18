/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.event;

import java.util.EventObject;

import org.drools.RuleBase;
import org.drools.rule.Package;
import org.drools.rule.Rule;

public class RuleBaseEvent extends EventObject {

    private static final long serialVersionUID = 510l;
    private final RuleBase    ruleBase;
    private final Package     pkg;
    private final Rule        rule;
    private final String      function;

    public RuleBaseEvent(final RuleBase ruleBase) {
        super( ruleBase );
        this.ruleBase = ruleBase;
        this.pkg = null;
        this.rule = null;
        this.function = null;
    }

    public RuleBaseEvent(final RuleBase ruleBase,
                         final Package pkg) {
        super( ruleBase );
        this.ruleBase = ruleBase;
        this.pkg = pkg;
        this.rule = null;
        this.function = null;
    }

    public RuleBaseEvent(final RuleBase ruleBase,
                         final Package pkg,
                         final Rule rule) {
        super( ruleBase );
        this.ruleBase = ruleBase;
        this.pkg = pkg;
        this.rule = rule;
        this.function = null;
    }

    public RuleBaseEvent(final RuleBase ruleBase,
                         final Package pkg,
                         final String function) {
        super( ruleBase );
        this.ruleBase = ruleBase;
        this.pkg = pkg;
        this.rule = null;
        this.function = function;
    }

    public RuleBase getRuleBase() {
        return this.ruleBase;
    }

    public Package getPackage() {
        return this.pkg;
    }

    public Rule getRule() {
        return this.rule;
    }

    public String getFunction() {
        return this.function;
    }

}
