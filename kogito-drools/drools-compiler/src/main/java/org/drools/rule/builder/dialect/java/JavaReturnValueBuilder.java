package org.drools.rule.builder.dialect.java;

import java.util.List;

import org.antlr.stringtemplate.StringTemplate;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.rule.Declaration;
import org.drools.rule.ReturnValueRestriction;
import org.drools.rule.builder.BuildContext;
import org.drools.rule.builder.ReturnValueBuilder;

public class JavaReturnValueBuilder
    implements
    ReturnValueBuilder {
    public void build(final BuildContext context,
                      final BuildUtils utils,
                      final List[] usedIdentifiers,
                      final Declaration[] previousDeclarations,
                      final Declaration[] localDeclarations,
                      final ReturnValueRestriction returnValueRestriction,
                      final ReturnValueRestrictionDescr returnValueRestrictionDescr) {
        final String className = "returnValue" + context.getNextId();
        returnValueRestrictionDescr.setClassMethodName( className );

        StringTemplate st = utils.getRuleGroup().getInstanceOf( "returnValueMethod" );

        utils.setStringTemplateAttributes( context,
                                           st,
                                           previousDeclarations,
                                           (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ) );

        final String[] localDeclarationTypes = new String[localDeclarations.length];
        for ( int i = 0, size = localDeclarations.length; i < size; i++ ) {
            localDeclarationTypes[i] = utils.getTypeFixer().fix( localDeclarations[i] );
        }

        st.setAttribute( "localDeclarations",
                         localDeclarations );
        st.setAttribute( "localDeclarationTypes",
                         localDeclarationTypes );

        st.setAttribute( "methodName",
                         className );

        final String returnValueText = (String) returnValueRestrictionDescr.getContent();
        st.setAttribute( "text",
                         returnValueText );

        context.getMethods().add( st.toString() );

        st = utils.getInvokerGroup().getInstanceOf( "returnValueInvoker" );

        st.setAttribute( "package",
                         context.getPkg().getName() );
        st.setAttribute( "ruleClassName",
                         utils.ucFirst( context.getRuleDescr().getClassName() ) );
        st.setAttribute( "invokerClassName",
                         context.getRuleDescr().getClassName() + utils.ucFirst( className ) + "Invoker" );
        st.setAttribute( "methodName",
                         className );

        utils.setStringTemplateAttributes( context,
                                           st,
                                           previousDeclarations,
                                           (String[]) usedIdentifiers[1].toArray( new String[usedIdentifiers[1].size()] ) );

        st.setAttribute( "localDeclarations",
                         localDeclarations );
        st.setAttribute( "localDeclarationTypes",
                         localDeclarationTypes );

        st.setAttribute( "hashCode",
                         returnValueText.hashCode() );

        final String invokerClassName = context.getPkg().getName() + "." + context.getRuleDescr().getClassName() + utils.ucFirst( className ) + "Invoker";
        context.getInvokers().put( invokerClassName,
                                   st.toString() );
        context.getInvokerLookups().put( invokerClassName,
                                         returnValueRestriction );
        context.getDescrLookups().put( invokerClassName,
                                       returnValueRestrictionDescr );
    }
}
