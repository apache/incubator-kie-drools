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
 * Indicates an attempt to add a <code>Rule</code> to a <code>Package</code>
 * that already contains a <code>Rule</code> with the same name.
 * 
 * @see Rule
 * @see Package
 * 
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter </a>
 */
public class DuplicateRuleNameException extends RuleConstructionException {
    /**
     * 
     */
    private static final long serialVersionUID = 510l;

    /** The rule-set. */
    private Package           pkg;

    /** The member rule. */
    private Rule              originalRule;

    /** The conflicting rule. */
    private Rule              conflictingRule;

    /**
     * @see java.lang.Exception#Exception()
     * 
     * @param pkg
     *            The <code>Package</code>.
     * @param originalRule
     *            The <code>Rule</code> already in the <code>Package</code>.
     * @param conflictingRule
     *            The new, conflicting <code>Rule</code>.
     */
    public DuplicateRuleNameException(final Package pkg,
                                      final Rule originalRule,
                                      final Rule conflictingRule) {
        super( createMessage( pkg,
                              conflictingRule ) );
        this.pkg = pkg;
        this.originalRule = originalRule;
        this.conflictingRule = conflictingRule;
    }

    /**
     * @see java.lang.Exception#Exception(Throwable cause)
     * 
     * @param pkg
     *            The <code>Package</code>.
     * @param originalRule
     *            The <code>Rule</code> already in the <code>Package</code>.
     * @param conflictingRule
     *            The new, conflicting <code>Rule</code>.
     */
    public DuplicateRuleNameException(final Package pkg,
                                      final Rule originalRule,
                                      final Rule conflictingRule,
                                      final Throwable cause) {
        super( createMessage( pkg,
                              conflictingRule ),
               cause );
        this.pkg = pkg;
        this.originalRule = originalRule;
        this.conflictingRule = conflictingRule;
    }

    /**
     * Retrieve the <code>Package</code>.
     * 
     * @return The <code>Package</code>.
     */
    public Package getPackage() {
        return this.pkg;
    }

    /**
     * Retrieve the original <code>Rule</code> in the <code>Package</code>.
     * 
     * @return The <code>Rule</code>.
     */
    public Rule getOriginalRule() {
        return this.originalRule;
    }

    /**
     * Retrieve the new conflicting <code>Rule</code>.
     * 
     * @return The <code>Rule</code>.
     */
    public Rule getConflictingRule() {
        return this.conflictingRule;
    }

    private static String createMessage(final Package pkg,
                                        final Rule rule) {
        return "Package " + ((pkg.getName() != null) ? pkg.getName() : "<no-name>") + " already contains rule with name " + rule.getName();
    }
}
