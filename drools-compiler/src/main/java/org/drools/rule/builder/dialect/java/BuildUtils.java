/*
 * Copyright 2006 JBoss Inc
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

package org.drools.rule.builder.dialect.java;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.AngleBracketTemplateLexer;
import org.codehaus.jfdi.interpreter.TypeResolver;
import org.drools.base.ClassFieldExtractorCache;
import org.drools.compiler.RuleError;
import org.drools.lang.descr.BaseDescr;
import org.drools.rule.Declaration;
import org.drools.rule.builder.BuildContext;
import org.drools.rule.builder.ConditionalElementBuilder;
import org.drools.rule.builder.RuleBuilder;

/**
 * A holder class for utility functions
 * 
 * @author etirelli
 */
public class BuildUtils {

    // the string template groups
    private final StringTemplateGroup      ruleGroup    = new StringTemplateGroup( new InputStreamReader( BuildUtils.class.getResourceAsStream( "javaRule.stg" ) ),
                                                                                   AngleBracketTemplateLexer.class );

    private final StringTemplateGroup      invokerGroup = new StringTemplateGroup( new InputStreamReader( BuildUtils.class.getResourceAsStream( "javaInvokers.stg" ) ),
                                                                                   AngleBracketTemplateLexer.class );

    private final KnowledgeHelperFixer     knowledgeHelperFixer;
  
    private final DeclarationTypeFixer     typeFixer;

    private final JavaExprAnalyzer         analyzer;

    private final TypeResolver             typeResolver;

    private final ClassFieldExtractorCache classFieldExtractorCache;
    
    private final Map                      builders;

    public BuildUtils(final KnowledgeHelperFixer knowledgeHelperFixer,
                      final DeclarationTypeFixer typeFixer,
                      final JavaExprAnalyzer analyzer,
                      final TypeResolver typeResolver,
                      final ClassFieldExtractorCache classFieldExtractorCache,
                      final Map builders ) {
        this.knowledgeHelperFixer = knowledgeHelperFixer;
        this.typeFixer = typeFixer;
        this.analyzer = analyzer;
        this.typeResolver = typeResolver;
        this.classFieldExtractorCache = classFieldExtractorCache;
        this.builders = builders;
    }

    public List[] getUsedIdentifiers(final BuildContext context,
                                     final BaseDescr descr,
                                     final Object content) {
        List[] usedIdentifiers = null;
        try {
            usedIdentifiers = this.analyzer.analyzeExpression( (String) content,
                                                               new Set[]{context.getDeclarationResolver().getDeclarations().keySet(), context.getPkg().getGlobals().keySet()} );
        } catch ( final Exception e ) {
            context.getErrors().add( new RuleError( context.getRule(),
                                                    descr,
                                                    null,
                                                    "Unable to determine the used declarations" ) );
        }
        return usedIdentifiers;
    }

    public List[] getUsedCIdentifiers(final BuildContext context,
                                      final BaseDescr descr,
                                      final String text) {
        List[] usedIdentifiers = null;
        try {
            usedIdentifiers = this.analyzer.analyzeBlock( text,
                                                          new Set[]{context.getDeclarationResolver().getDeclarations().keySet(), context.getPkg().getGlobals().keySet()} );
        } catch ( final Exception e ) {
            context.getErrors().add( new RuleError( context.getRule(),
                                                    descr,
                                                    null,
                                                    "Unable to determine the used declarations" ) );
        }
        return usedIdentifiers;
    }

    /**
     * Sets usual string template attributes:
     * 
     * <li> list of declarations and declaration types</li>
     * <li> list of globals and global types</li>
     *
     * @param context the current build context
     * @param st the string template whose attributes will be set 
     * @param declarations array of declarations to set
     * @param globals array of globals to set
     */
    public void setStringTemplateAttributes(final BuildContext context,
                                            final StringTemplate st,
                                            final Declaration[] declarations,
                                            final String[] globals) {
        final String[] declarationTypes = new String[declarations.length];
        for ( int i = 0, size = declarations.length; i < size; i++ ) {
            declarationTypes[i] = this.typeFixer.fix( declarations[i] ); 
        }

        final List globalTypes = new ArrayList( globals.length );
        for ( int i = 0, length = globals.length; i < length; i++ ) {
            globalTypes.add( ((Class) context.getPkg().getGlobals().get( globals[i] )).getName().replace( '$',
                                                                                                          '.' ) );
        }

        st.setAttribute( "declarations",
                         declarations );
        st.setAttribute( "declarationTypes",
                         declarationTypes );

        st.setAttribute( "globals",
                         globals );
        st.setAttribute( "globalTypes",
                         globalTypes );
    }

    /**
     * Upper case the first letter of "name"
     * 
     * @param name
     * @return
     */
    public String ucFirst(final String name) {
        return name.toUpperCase().charAt( 0 ) + name.substring( 1 );
    }

    /**
     * Returns the string template group of invokers
     * @return
     */
    public StringTemplateGroup getInvokerGroup() {
        return invokerGroup;
    }

    /**
     * Returns the string template group of actual rule templates
     * @return
     */
    public StringTemplateGroup getRuleGroup() {
        return ruleGroup;
    }

    /**
     * Returns the Knowledge Helper Fixer
     * @return
     */
    public KnowledgeHelperFixer getKnowledgeHelperFixer() {
        return knowledgeHelperFixer;
    }

    /**
     * Returns the current type resolver instance
     * @return
     */
    public TypeResolver getTypeResolver() {
        return this.typeResolver;
    }

    /**
     * Returns the cache of field extractors
     * @return
     */
    public ClassFieldExtractorCache getClassFieldExtractorCache() {
        return classFieldExtractorCache;
    }

    /**
     * Returns a map<Class, ConditionalElementBuilder> of builders
     * @return
     */
    public Map getBuilders() {
        return builders;
    }

    /**
     * Returns the builder for the given descriptor class
     * @param descr
     * @return
     */
    public ConditionalElementBuilder getBuilder(Class descr) {
        return (ConditionalElementBuilder) builders.get( descr );
    }

    /**
     * @return the typeFixer
     */
    public DeclarationTypeFixer getTypeFixer() {
        return typeFixer;
    }

}
