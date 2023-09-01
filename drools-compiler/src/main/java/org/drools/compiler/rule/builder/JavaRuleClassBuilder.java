package org.drools.compiler.rule.builder;

import org.drools.drl.ast.descr.RuleDescr;
import org.drools.core.rule.consequence.KnowledgeHelper;
import org.drools.util.StringUtils;

public class JavaRuleClassBuilder implements RuleClassBuilder {

    /* (non-Javadoc)
     * @see org.kie.rule.builder.dialect.java.RuleClassBuilder#buildRule(org.kie.rule.builder.BuildContext, org.kie.rule.builder.dialect.java.BuildUtils, org.kie.lang.descr.RuleDescr)
     */
    public String  buildRule(final RuleBuildContext context) {
        // If there is no compiled code, return
        if ( context.getMethods().isEmpty() ) {
            return null;
        }
        
        final String lineSeparator = System.getProperty( "line.separator" );

        final StringBuilder buffer = new StringBuilder();
        buffer.append("package ").append(context.getPkg().getName()).append(";").append(lineSeparator);

        for (String s : context.getPkg().getImports().keySet()) {
            buffer.append("import ").append(s).append(";");
        }
        buffer.append("import ").append(KnowledgeHelper.class.getName()).append(";");

        for (String s : context.getPkg().getStaticImports()) {
            buffer.append("import static ").append(s).append(";");
        }
        
        buffer.append( lineSeparator );

        final RuleDescr ruleDescr = context.getRuleDescr();
        
        buffer.append("public class ").append(StringUtils.ucFirst(ruleDescr.getClassName())).append(" {").append(lineSeparator);
        buffer.append("    private static final long serialVersionUID = 510l;").append(lineSeparator);

        for ( int i = 0, size = context.getMethods().size() - 1; i < size; i++ ) {
            buffer.append(context.getMethods().get(i)).append(lineSeparator);
        }

        final String[] lines = buffer.toString().split( lineSeparator, -1 );

        ruleDescr.setConsequenceOffset( lines.length );

        buffer.append(context.getMethods().get(context.getMethods().size() - 1)).append(lineSeparator);
        buffer.append( "}" );

        return buffer.toString();
    }
}
