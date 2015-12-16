/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
package org.kie.internal.builder.conf;

import org.kie.api.conf.SingleValueKieBaseOption;

/**
 * <p>
 * An Enum for switching between Phreak and Reteoo algorithms.
 * </p>
 * 
 * <pre>
 * drools.ruleEngine = &lt;phreak|reteoo&gt;
 * </pre>
 * 
 * <b>DEFAULT = phreak.</b>
 * 
 */
public enum RuleEngineOption implements SingleValueKieBaseOption {

    PHREAK("phreak"), RETEOO("reteoo");

    /**
     * The property name for the L and R Unlinking option
     */
    public static final String PROPERTY_NAME = "drools.ruleEngine";

    private String value;

    RuleEngineOption(final String value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public boolean isLRUnlinkingEnabled() {
        return this == PHREAK;
    }
    
    public static RuleEngineOption determineOption( String val ) {
    	return RETEOO.value.equalsIgnoreCase(val) ? RETEOO : PHREAK;
    }

    @Override
    public String toString() {
        return value;
    }
}
