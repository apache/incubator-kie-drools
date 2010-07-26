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

package org.drools;

import org.drools.rule.Rule;

/**
 * Indicates an error integrating a <code>Rule</code> or <code>Package</code>
 * into a <code>RuleBase</code>.
 * 
 * @see RuleBase#addRule
 * @see RuleBase#addPackage
 * 
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter </a>
 * 
 * @version $Id: RuleIntegrationException.java,v 1.6 2004/09/17 00:14:06
 *          mproctor Exp $
 */
public class RuleIntegrationException extends IntegrationException {
    /**
     * 
     */
    private static final long serialVersionUID = 400L;
    /** The rule. */
    private final Rule        rule;

    /**
     * @see java.lang.Exception#Exception()
     * 
     * @param rule
     *            The offending rule.
     */
    public RuleIntegrationException(final Rule rule) {
        super( createMessage( rule ) );
        this.rule = rule;
    }

    /**
     * @see java.lang.Exception#Exception(Throwable cause)
     * 
     * @param rule
     *            The offending rule.
     */
    public RuleIntegrationException(final Rule rule,
                                    final Throwable cause) {
        super( createMessage( rule ),
               cause );
        this.rule = rule;
    }

    /**
     * Retrieve the <code>Rule</code>.
     * 
     * @return The rule.
     */
    public Rule getRule() {
        return this.rule;
    }

    private static String createMessage(final Rule rule) {
        return rule.getName() + " cannot be integrated";
    }
}
