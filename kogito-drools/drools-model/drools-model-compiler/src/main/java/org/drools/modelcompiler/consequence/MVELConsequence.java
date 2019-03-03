/*
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

package org.drools.modelcompiler.consequence;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.compiler.rule.builder.dialect.mvel.MVELConsequenceBuilder;
import org.drools.core.WorkingMemory;
import org.drools.core.base.EvaluatorWrapper;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.Tuple;
import org.drools.core.util.ClassUtils;
import org.drools.model.Variable;
import org.drools.model.functions.ScriptBlock;
import org.drools.modelcompiler.RuleContext;
import org.kie.api.definition.KiePackage;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.compiler.ExecutableStatement;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.CachingMapVariableResolverFactory;

public class MVELConsequence implements Consequence {

    private final org.drools.model.Consequence consequence;
    private final RuleContext context;

    private MVELCompilationUnit cu;
    private ExecutableStatement compiledExpression;

    public MVELConsequence( org.drools.model.Consequence consequence, RuleContext context ) {
        this.consequence = consequence;
        this.context = context;
        init();
    }

    @Override
    public String getName() {
        return RuleImpl.DEFAULT_CONSEQUENCE_NAME;
    }

    @Override
    public void evaluate( KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory ) throws Exception {
        Map<String, Object> mvelContext = new HashMap<>();
        mvelContext.put("this", knowledgeHelper);
        mvelContext.put("drools", knowledgeHelper);
        mvelContext.put("kcontext", knowledgeHelper);
        mvelContext.put("rule", knowledgeHelper.getRule());

        Tuple tuple = knowledgeHelper.getTuple();
        Declaration[] declarations = ((RuleTerminalNode)knowledgeHelper.getMatch().getTuple().getTupleSink()).getRequiredDeclarations();

        int declrCounter = 0;
        for (Variable var : consequence.getVariables()) {
            if ( var.isFact() ) {
                Declaration declaration = declarations[declrCounter++];
                InternalFactHandle fh = tuple.get( declaration );
                mvelContext.put(var.getName(), declaration.getValue((InternalWorkingMemory) workingMemory, fh.getObject()));
            } else {
                mvelContext.put(var.getName(), workingMemory.getGlobal(var.getName()));
            }
        }

        CachingMapVariableResolverFactory cachingFactory = new CachingMapVariableResolverFactory(mvelContext);

        VariableResolverFactory factory = cu.getFactory( knowledgeHelper,  ((AgendaItem )knowledgeHelper.getMatch()).getTerminalNode().getRequiredDeclarations(),
                knowledgeHelper.getRule(), knowledgeHelper.getTuple(), null, (InternalWorkingMemory) workingMemory, workingMemory.getGlobalResolver()  );

        cachingFactory.setNextFactory(factory);

        MVEL.executeExpression(compiledExpression, knowledgeHelper, cachingFactory);
    }

    public void init() {
        Variable[] vars = consequence.getVariables();
        ScriptBlock scriptBlock = null;
        try {
            scriptBlock = (ScriptBlock) consequence.getBlock();
        } catch (ClassCastException e) {
            throw new RuntimeException("I tried to access a ScriptBlock but it was not. So something is thinking is a MVEL consequence but did not set the MVEL script textual representation", e);
        }
        String originalRHS = scriptBlock.getScript();

        String name = context.getRule().getPackageName() + "." + context.getRule().getName();
        String expression = MVELConsequenceBuilder.processMacros(originalRHS);
        String[] globalIdentifiers = new String[] {};
        String[] default_inputIdentifiers = new String[]{"this", "drools", "kcontext", "rule"};
        String[] inputIdentifiers = Stream.concat(Arrays.asList(default_inputIdentifiers).stream(), Stream.of(vars).map(Variable::getName)).collect(Collectors.toList()).toArray(new String[]{});
        String[] default_inputTypes = new String[]{"org.drools.core.spi.KnowledgeHelper", "org.drools.core.spi.KnowledgeHelper", "org.drools.core.spi.KnowledgeHelper", "org.kie.api.definition.rule.Rule"};
        String[] inputTypes = Stream.concat(Arrays.asList(default_inputTypes).stream(), Stream.of(vars).map(var -> var.getType().getName())).collect(Collectors.toList()).toArray(new String[]{});
        // ^^ please notice about inputTypes, it is to use the Class.getName(), because is later used by the Classloader internally in MVEL to load the class,
        //    do NOT replace with getCanonicalName() otherwise inner classes will not be loaded correctly.
        boolean strictMode = true;
        boolean readLocalsFromTuple = false;
        EvaluatorWrapper[] operators = new EvaluatorWrapper[] {};
        Declaration[] previousDeclarations = new Declaration[] {};
        Declaration[] localDeclarations = new Declaration[] {};
        String[] otherIdentifiers = new String[] {};

        expression = MVELConsequenceBuilder.rewriteUpdates( context::getDeclarationClass, ClassUtils::getAccessibleProperties, expression );

        cu = new MVELCompilationUnit(name,
                expression,
                globalIdentifiers,
                operators,
                previousDeclarations,
                localDeclarations,
                otherIdentifiers,
                inputIdentifiers,
                inputTypes,
                strictMode,
                readLocalsFromTuple );

        // TODO unfortunately the MVELDialectRuntimeData would be the one of compile time
        // the one from runtime is not helpful, in fact the dialect registry for runtime is empty:
        // MVELDialectRuntimeData runtimeData = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData("mvel");

        MVELDialectRuntimeData runtimeData = new MVELDialectRuntimeData();
        runtimeData.onAdd(null, Thread.currentThread().getContextClassLoader()); // this classloader will be used by the CompilationUnit to load the imports.
        runtimeData.addPackageImport(context.getPkg().getName());
        runtimeData.addPackageImport("java.lang");
        // sorry kind of a hack. runtimeData.getImports() does not hold information about imports, so we rely on all known builder's KBPackages
        // therefore we assume for the ScriptBlock all available KBPackages are the default available and imported for the scope of the Script itself.
        for (KiePackage kp : context.getKnowledgePackages()) {
            if (!kp.getName().equals(context.getPkg().getName())) {
                runtimeData.addPackageImport(kp.getName());
            }
        }

        ParserConfiguration parserConfiguration = runtimeData.getParserConfiguration();
        parserConfiguration.setClassLoader( context.getClassLoader() );

        Class<?> ruleClass = scriptBlock.getRuleClass();
        if (ruleClass != null) {
            for (Method m : ruleClass.getDeclaredMethods()) {
                if ( Modifier.isStatic( m.getModifiers() ) ) {
                    runtimeData.getParserConfiguration().addImport( m.getName(), m );
                }
            }
        }

        compiledExpression = (ExecutableStatement) cu.getCompiledExpression(runtimeData);
    }
}
