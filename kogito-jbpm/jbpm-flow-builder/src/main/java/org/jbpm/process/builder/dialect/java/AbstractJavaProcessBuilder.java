/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.builder.dialect.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.rule.builder.dialect.java.JavaAnalysisResult;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaLocalDeclarationDescr;
import org.drools.core.util.StringUtils;
import org.jbpm.process.builder.ProcessBuildContext;
import org.jbpm.process.core.ContextResolver;
import org.jbpm.process.core.context.variable.VariableScope;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;

public class AbstractJavaProcessBuilder {

    protected static final TemplateRegistry RULE_REGISTRY = new SimpleTemplateRegistry();
    protected static final TemplateRegistry INVOKER_REGISTRY = new SimpleTemplateRegistry();

    static {
        RULE_REGISTRY.addNamedTemplate("rules", TemplateCompiler.compileTemplate(AbstractJavaProcessBuilder.class.getResourceAsStream("javaRule.mvel")));
        INVOKER_REGISTRY.addNamedTemplate("invokers", TemplateCompiler.compileTemplate(AbstractJavaProcessBuilder.class.getResourceAsStream("javaInvokers.mvel")));

        /**
         * Process these templates
         */
        TemplateRuntime.execute(RULE_REGISTRY.getNamedTemplate("rules"), null, RULE_REGISTRY);
        TemplateRuntime.execute(INVOKER_REGISTRY.getNamedTemplate("invokers"), null, INVOKER_REGISTRY);
    }

    public TemplateRegistry getRuleTemplateRegistry() {
        return RULE_REGISTRY;
    }

    public TemplateRegistry getInvokerTemplateRegistry() {
        return INVOKER_REGISTRY;
    }

    public Map createVariableContext(final String className,
                                     final String text,
                                     final ProcessBuildContext context,
                                     final String[] globals) {
        final Map map = new HashMap();

        map.put("methodName",
                className);

        map.put("package",
                context.getPkg().getName());

        map.put("processClassName",
                StringUtils.ucFirst(context.getProcessDescr().getClassName()));

        map.put("invokerClassName",
                context.getProcessDescr().getClassName() + StringUtils.ucFirst(className) + "Invoker");

        if (text != null) {
            map.put("text",
                    text);

            map.put("hashCode",
                    new Integer(text.hashCode()));
        }

        final List globalTypes = new ArrayList(globals.length);
        for (int i = 0, length = globals.length; i < length; i++) {
            globalTypes.add(context.getPkg().getGlobals().get(globals[i]).replace('$',
                    '.'));
        }

        map.put("globals",
                globals);

        map.put("globalTypes",
                globalTypes);

        return map;
    }
    
    public Map createVariableContext(
    		final String className,
            final String text,
            final ProcessBuildContext context,
            final String[] globals,
            final Set<String> unboundIdentifiers,
            final ContextResolver contextResolver) {
    	Map map = createVariableContext(className, text, context, globals);
    	List<String> variables = new ArrayList<String>();
    	final List variableTypes = new ArrayList(globals.length);
        for (String variableName: unboundIdentifiers) {
        	VariableScope variableScope = (VariableScope) contextResolver.resolveContext(VariableScope.VARIABLE_SCOPE, variableName);
        	if (variableScope != null) {
        		variables.add(variableName);
        		variableTypes.add(variableScope.findVariable(variableName).getType().getStringType());
        	}
        }

        map.put("variables",
                variables);

        map.put("variableTypes",
                variableTypes);
    	return map;
    }

    public void generateTemplates(final String ruleTemplate,
                                 final String invokerTemplate,
                                 final ProcessBuildContext context,
                                 final String className,
                                 final Map vars,
                                 final Object invokerLookup,
                                 final BaseDescr descrLookup) {
        TemplateRegistry registry = getRuleTemplateRegistry();

        context.getMethods().add((String)
                TemplateRuntime.execute(registry.getNamedTemplate(ruleTemplate), null, new MapVariableResolverFactory(vars), registry)
        );

        registry = getInvokerTemplateRegistry();
        final String invokerClassName = context.getPkg().getName() + "." + context.getProcessDescr().getClassName() + StringUtils.ucFirst(className) + "Invoker";

        context.getInvokers().put(invokerClassName,
                (String)TemplateRuntime.execute(registry.getNamedTemplate(invokerTemplate), null, new MapVariableResolverFactory(vars), registry)
        );

        context.addInvokerLookup(invokerClassName, invokerLookup);
        context.addDescrLookups(invokerClassName, descrLookup);
    }
    
    protected void collectTypes(String key, AnalysisResult analysis, ProcessBuildContext context) {
        if (context.getProcess() != null) {
            Set<String> referencedTypes = new HashSet<String>();
            Set<String> unqualifiedClasses = new HashSet<String>();
            
            JavaAnalysisResult javaAnalysis = (JavaAnalysisResult) analysis;
            LOCAL_VAR: for( JavaLocalDeclarationDescr localDeclDescr : javaAnalysis.getLocalVariablesMap().values() ) { 
                String type = localDeclDescr.getRawType();
                 
                if( type.contains(".") ) { 
                    referencedTypes.add(type);
                } else { 
                    for( String alreadyRefdType : referencedTypes ) { 
                        String alreadyRefdSimpleName = alreadyRefdType.substring(alreadyRefdType.lastIndexOf(".") + 1);
                       if( type.equals(alreadyRefdSimpleName) ) { 
                           continue LOCAL_VAR;
                       }
                    }
                    unqualifiedClasses.add(type);
                }
            }
        
            context.getProcess().getMetaData().put(key + "ReferencedTypes", referencedTypes);
            context.getProcess().getMetaData().put(key + "UnqualifiedTypes", unqualifiedClasses);
        }
    }
}
