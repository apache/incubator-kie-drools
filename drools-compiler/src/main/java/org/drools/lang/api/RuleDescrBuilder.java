/*
 * Copyright 2011 JBoss Inc
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

package org.drools.lang.api;

import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.RuleDescr;

/**
 *  A descriptor builder for rules
 */
public interface RuleDescrBuilder
    extends
    AnnotatedDescrBuilder<RuleDescrBuilder>,
    AttributeSupportBuilder<RuleDescrBuilder>,
    DescrBuilder<PackageDescrBuilder, RuleDescr> {

    /**
     * The name of the rule. Best practice is to keep rule names relatively short,
     * i.e. under 60 characters.
     * 
     * @param name name of the rule
     * 
     * @return itself
     */
    public RuleDescrBuilder name( String name );

    /**
     * Defines the name of the rule this rule extends. It will cause the rule
     * to inherit the LHS from the parent rule.
     * 
     * @param name name of the parent rule
     * 
     * @return itself
     */
    public RuleDescrBuilder extendsRule( String name );

    /**
     * The right hand side (consequence) of the rule. This is a code block
     * that must be valid according to the used dialect (java or MVEL). In particular,
     * the deprecated '#' character, that was used for one line comments is not supported.
     * For one line comments, please use standard '//'.
     * 
     * @param rhs the code block 
     * 
     * @return itself
     */
    public RuleDescrBuilder rhs( String rhs );

    /**
     * Defines the LHS (condition) of the rule.
     * 
     * @return a Conditional Element descriptor builder with the AND CE semantic.
     */
    public CEDescrBuilder<RuleDescrBuilder, AndDescr> lhs();

}
