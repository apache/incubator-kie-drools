/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.rule;

import org.drools.core.definitions.rule.impl.RuleImpl;

/**
 * Indicates an error regarding the semantic validity of a rule.
 */
public class InvalidRuleException extends RuleConstructionException {
    private static final long serialVersionUID = 510l;
    /** The invalid rule. */
    private RuleImpl              rule;

    /**
     * @see java.lang.Exception#Exception()
     * 
     * @param rule
     *            The invalid <code>Rule</code>.
     */
    public InvalidRuleException(final RuleImpl rule) {
        super();
        this.rule = rule;
    }

    /**
     * @see java.lang.Exception#Exception(String message)
     * 
     * @param message
     * @param rule
     */
    public InvalidRuleException(final String message,
                                final RuleImpl rule) {
        super( message );
        this.rule = rule;
    }

    /**
     * @see java.lang.Exception#Exception(String message, Throwable cause)
     * 
     * @param message
     * @param rule
     */
    public InvalidRuleException(final String message,
                                final RuleImpl rule,
                                final Throwable cause) {
        super( message,
               cause );
        this.rule = rule;
    }

    /**
     * Retrieve the invalid <code>Rule</code>.
     * 
     * @return The invalid <code>Rule</code>.
     */
    public RuleImpl getRule() {
        return this.rule;
    }
}
