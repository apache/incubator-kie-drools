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

package org.kie.api.runtime.conf;

import org.kie.api.definition.rule.Rule;

public class TimedRuleExecutionOption implements SingleValueKieSessionOption {

    private static final long serialVersionUID = 510l;

    public static final String PROPERTY_NAME = "drools.timedRuleExecution";

    public static final TimedRuleExecutionOption YES = new TimedRuleExecutionOption(new TimedRuleExecutionFilter() {
        @Override
        public boolean accept(Rule[] rules) {
            return true;
        }
    });

    public static final TimedRuleExecutionOption NO = new TimedRuleExecutionOption(null);

    private final TimedRuleExecutionFilter filter;

    private TimedRuleExecutionOption( final TimedRuleExecutionFilter filter ) {
        this.filter = filter;
    }

    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public TimedRuleExecutionFilter getFilter() {
        return filter;
    }

    public static class FILTERED extends TimedRuleExecutionOption {
        public FILTERED(TimedRuleExecutionFilter filter) {
            super(filter);
        }
    }

    public static TimedRuleExecutionOption resolve(String value) {
        return Boolean.valueOf( value ) ? YES : NO;
    }
}
