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
package org.kie.internal.runtime.conf;

import org.kie.api.conf.OptionKey;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.conf.DirectFiringOption;
import org.kie.api.runtime.conf.SingleValueRuleRuntimeOption;

/**
 * Option to force evaluation and then activation of rules annotated with @Eager.
 */
public class ForceEagerActivationOption implements SingleValueRuleRuntimeOption {

    private static final long serialVersionUID = 510l;

    public static final String PROPERTY_NAME = "drools.forceEagerActivation";

    public static OptionKey<ForceEagerActivationOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    public static final ForceEagerActivationOption YES = new ForceEagerActivationOption(new ForceEagerActivationFilter() {
        @Override
        public boolean accept(Rule rule) {
            return true;
        }
    });

    public static final ForceEagerActivationOption NO = new ForceEagerActivationOption(new ForceEagerActivationFilter() {
        @Override
        public boolean accept(Rule rule) {
            return false;
        }
    });

    private final ForceEagerActivationFilter filter;

    private ForceEagerActivationOption( final ForceEagerActivationFilter filter ) {
        this.filter = filter;
    }

    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public ForceEagerActivationFilter getFilter() {
        return filter;
    }

    public static class FILTERED extends ForceEagerActivationOption {
        public FILTERED(ForceEagerActivationFilter filter) {
            super(filter);
        }
    }

    public static ForceEagerActivationOption resolve(String value) {
        return Boolean.valueOf( value ) ? YES : NO;
    }
}
