package org.drools.drl.ast.dsl;

import org.drools.drl.ast.descr.AndDescr;
import org.drools.drl.ast.descr.RuleDescr;

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
    RuleDescrBuilder name( String name );

    /**
     * Defines the name of the rule this rule extends. It will cause the rule
     * to inherit the LHS from the parent rule.
     * 
     * @param name name of the parent rule
     * 
     * @return itself
     */
    RuleDescrBuilder extendsRule( String name );

    /**
     * The default right hand side (consequence) of the rule. This is a code block
     * that must be valid according to the used dialect (java or MVEL). In particular,
     * the deprecated '#' character, that was used for one line comments is not supported.
     * For one line comments, please use standard '//'.
     * 
     * @param rhs the code block 
     * 
     * @return itself
     */
    RuleDescrBuilder rhs( String rhs );

    /**
     * An additional named right hand side (consequence) of the rule. This is a code block
     * that must be valid according to the used dialect (java or MVEL). In particular,
     * the deprecated '#' character, that was used for one line comments is not supported.
     * For one line comments, please use standard '//'.
     *
     * @param name the name of the consequence
     * @param rhs the code block
     *
     * @return itself
     */
    RuleDescrBuilder namedRhs( String name, String rhs );

    /**
     * Defines the LHS (condition) of the rule.
     * 
     * @return a Conditional Element descriptor builder with the AND CE semantic.
     */
    CEDescrBuilder<RuleDescrBuilder, AndDescr> lhs();

}
