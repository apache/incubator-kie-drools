/**
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

package org.drools.rule;

/**
 * Indicates an error regarding the semantic validity of a rule.
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 */
public class InvalidRuleException extends RuleConstructionException {
    /**
     * 
     */
    private static final long serialVersionUID = 510l;
    /** The invalid rule. */
    private Rule              rule;

    /**
     * @see java.lang.Exception#Exception()
     * 
     * @param rule
     *            The invalid <code>Rule</code>.
     */
    public InvalidRuleException(final Rule rule) {
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
                                final Rule rule) {
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
                                final Rule rule,
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
    public Rule getRule() {
        return this.rule;
    }
}
