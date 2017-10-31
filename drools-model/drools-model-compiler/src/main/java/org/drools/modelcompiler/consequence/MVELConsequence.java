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

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.compiler.rule.builder.dialect.mvel.MVELConsequenceBuilder;
import org.drools.core.WorkingMemory;
import org.drools.core.base.EvaluatorWrapper;
import org.drools.core.base.mvel.MVELCompilationUnit;
import org.drools.core.base.mvel.MVELCompilationUnit.DroolsVarFactory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.ImportDeclaration;
import org.drools.core.rule.MVELDialectRuntimeData;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.KnowledgeHelper;
import org.drools.core.spi.Tuple;
import org.drools.model.Variable;
import org.drools.model.functions.ScriptBlock;
import org.drools.modelcompiler.RuleContext;
import org.mvel2.MVEL;
import org.mvel2.compiler.ExecutableStatement;
import org.mvel2.integration.impl.CachingMapVariableResolverFactory;

public class MVELConsequence implements Consequence {

    private final org.drools.model.Consequence consequence;
    private final RuleContext context;

    public MVELConsequence( org.drools.model.Consequence consequence, RuleContext context ) {
        this.consequence = consequence;
        this.context = context;
    }

    @Override
    public String getName() {
        return RuleImpl.DEFAULT_CONSEQUENCE_NAME;
    }

    @Override
    public void evaluate( KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory ) throws Exception {
        // same as lambda consequence... 
        Tuple tuple = knowledgeHelper.getTuple();
        Declaration[] declarations = ((RuleTerminalNode)knowledgeHelper.getMatch().getTuple().getTupleSink()).getRequiredDeclarations();

        Variable[] vars = consequence.getVariables();
        Map<Variable, Object> facts = new LinkedHashMap<>(); // ...but the facts are association of Variable and its value, preserving order.

        int declrCounter = 0;
        for (Variable var : vars) {
            if ( var.isFact() ) {
                Declaration declaration = declarations[declrCounter++];
                InternalFactHandle fh = tuple.get( declaration );
                facts.put(var, declaration.getValue((InternalWorkingMemory) workingMemory, fh.getObject()));
            } else {
                facts.put(var, workingMemory.getGlobal(var.getName()));
            }
        }
        
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
        String[] inputIdentifiers = Stream.concat(Arrays.asList(default_inputIdentifiers).stream(), facts.entrySet().stream().map(kv -> kv.getKey().getName())).collect(Collectors.toList()).toArray(new String[]{});
        String[] default_inputTypes = new String[]{"org.drools.core.spi.KnowledgeHelper", "org.drools.core.spi.KnowledgeHelper", "org.drools.core.spi.KnowledgeHelper", "org.kie.api.definition.rule.Rule"};
        String[] inputTypes = Stream.concat(Arrays.asList(default_inputTypes).stream(), facts.entrySet().stream().map(kv -> kv.getKey().getType().asClass().getCanonicalName())).collect(Collectors.toList()).toArray(new String[]{});
        int languageLevel = 4;
        boolean strictMode = true;
        boolean readLocalsFromTuple = false;
        EvaluatorWrapper[] operators = new EvaluatorWrapper[] {};
        Declaration[] previousDeclarations = new Declaration[] {};
        Declaration[] localDeclarations = new Declaration[] {};
        String[] otherIdentifiers = new String[] {};

        MVELCompilationUnit cu = new MVELCompilationUnit(name,
                                                         expression,
                                                         globalIdentifiers,
                                                         operators,
                                                         previousDeclarations,
                                                         localDeclarations,
                                                         otherIdentifiers,
                                                         inputIdentifiers,
                                                         inputTypes,
                                                         languageLevel,
                                                         strictMode,
                                                         readLocalsFromTuple );

        // TODO unfortunately the MVELDialectRuntimeData would be the one of compile time
        // the one from runtime is not helpful, in fact the dialect registry for runtime is empty:
        // MVELDialectRuntimeData runtimeData = (MVELDialectRuntimeData) context.getPkg().getDialectRuntimeRegistry().getDialectData("mvel");

        MVELDialectRuntimeData runtimeData = new MVELDialectRuntimeData();
        runtimeData.onAdd(null, Thread.currentThread().getContextClassLoader()); // this classloader will be used by the CompilationUnit to load the imports.
        runtimeData.addPackageImport(context.getPkg().getName());
        runtimeData.addPackageImport("java.lang");
        Map<String, Object> imports = runtimeData.getImports(); // sorry a hack.
        for (ImportDeclaration id : context.getPkg().getImports().values()) {
            imports.put(id.getTarget(), id.getTarget());
        }

        Serializable cuResult = cu.getCompiledExpression(runtimeData); // sometimes here it was passed as a 2nd argument a String?? similar to `rule R in file file.drl`
        ExecutableStatement compiledExpression = (ExecutableStatement) cuResult;

        // TODO the part above up to the ExecutableStatement compiledExpression should be cached.

        Map<String, Object> mvelContext = new HashMap<>();
        mvelContext.put("this", knowledgeHelper);
        mvelContext.put("drools", knowledgeHelper);
        mvelContext.put("kcontext", knowledgeHelper);
        mvelContext.put("rule", null);
        for (Entry<Variable, Object> kv : facts.entrySet()) {
            mvelContext.put(kv.getKey().getName(), kv.getValue());
        }

        DroolsVarFactory droolsVarFactory = new DroolsVarFactory();
        droolsVarFactory.setKnowledgeHelper(knowledgeHelper);

        CachingMapVariableResolverFactory factory = new CachingMapVariableResolverFactory(mvelContext);
        factory.setNextFactory(droolsVarFactory);

        MVEL.executeExpression(compiledExpression, factory);
    }
}
