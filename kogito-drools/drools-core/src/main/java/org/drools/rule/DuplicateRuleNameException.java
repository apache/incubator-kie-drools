package org.drools.rule;

/*
 * $Id: DuplicateRuleNameException.java,v 1.1 2005/07/26 01:06:31 mproctor Exp $
 *
 * Copyright 2001-2003 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company. (http://werken.com/)
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

/**
 * Indicates an attempt to add a <code>Rule</code> to a <code>RuleSet</code>
 * that already contains a <code>Rule</code> with the same name.
 * 
 * @see Rule
 * @see RuleSet
 * 
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter </a>
 */
public class DuplicateRuleNameException extends RuleConstructionException {
    /** The rule-set. */
    private RuleSet ruleSet;

    /** The member rule. */
    private Rule    originalRule;

    /** The conflicting rule. */
    private Rule    conflictingRule;

    /**
     * @see java.lang.Exception#Exception()
     * 
     * @param ruleSet
     *            The <code>RuleSet</code>.
     * @param originalRule
     *            The <code>Rule</code> already in the <code>RuleSet</code>.
     * @param conflictingRule
     *            The new, conflicting <code>Rule</code>.
     */
    public DuplicateRuleNameException(RuleSet ruleSet,
                                      Rule originalRule,
                                      Rule conflictingRule){
        super( createMessage( ruleSet,
                              conflictingRule ) );
        this.ruleSet = ruleSet;
        this.originalRule = originalRule;
        this.conflictingRule = conflictingRule;
    }

    /**
     * @see java.lang.Exception#Exception(Throwable cause)
     * 
     * @param ruleSet
     *            The <code>RuleSet</code>.
     * @param originalRule
     *            The <code>Rule</code> already in the <code>RuleSet</code>.
     * @param conflictingRule
     *            The new, conflicting <code>Rule</code>.
     */
    public DuplicateRuleNameException(RuleSet ruleSet,
                                      Rule originalRule,
                                      Rule conflictingRule,
                                      Throwable cause){
        super( createMessage( ruleSet,
                              conflictingRule ),
               cause );
        this.ruleSet = ruleSet;
        this.originalRule = originalRule;
        this.conflictingRule = conflictingRule;
    }

    /**
     * Retrieve the <code>RuleSet</code>.
     * 
     * @return The <code>RuleSet</code>.
     */
    public RuleSet getRuleSet(){
        return this.ruleSet;
    }

    /**
     * Retrieve the original <code>Rule</code> in the <code>RuleSet</code>.
     * 
     * @return The <code>Rule</code>.
     */
    public Rule getOriginalRule(){
        return this.originalRule;
    }

    /**
     * Retrieve the new conflicting <code>Rule</code>.
     * 
     * @return The <code>Rule</code>.
     */
    public Rule getConflictingRule(){
        return this.conflictingRule;
    }

    private static String createMessage(RuleSet ruleSet,
                                        Rule rule){
        return "Rule-set " + ((ruleSet.getName() != null) ? ruleSet.getName() : "<no-name>") + " already contains rule with name " + rule.getName();
    }
}
