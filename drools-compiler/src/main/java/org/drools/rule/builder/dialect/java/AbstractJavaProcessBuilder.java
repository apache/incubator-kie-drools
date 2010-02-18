package org.drools.rule.builder.dialect.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.DescrBuildError;
import org.drools.core.util.StringUtils;
import org.drools.lang.descr.BaseDescr;
import org.drools.process.core.ContextResolver;
import org.drools.process.core.context.variable.Variable;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.rule.builder.ProcessBuildContext;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.templates.SimpleTemplateRegistry;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRegistry;
import org.mvel2.templates.TemplateRuntime;

public class AbstractJavaProcessBuilder {

    protected static final TemplateRegistry RULE_REGISTRY = new SimpleTemplateRegistry();
    protected static final TemplateRegistry INVOKER_REGISTRY = new SimpleTemplateRegistry();

    static {
        RULE_REGISTRY.addNamedTemplate("rules", TemplateCompiler.compileTemplate(AbstractJavaProcessBuilder.class.getResourceAsStream("javaRule.mvel"), null));
        INVOKER_REGISTRY.addNamedTemplate("invokers", TemplateCompiler.compileTemplate(AbstractJavaProcessBuilder.class.getResourceAsStream("javaInvokers.mvel"), null));

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
            final List<String> unboundIdentifiers,
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

    public void generatTemplates(final String ruleTemplate,
                                 final String invokerTemplate,
                                 final ProcessBuildContext context,
                                 final String className,
                                 final Map vars,
                                 final Object invokerLookup,
                                 final BaseDescr descrLookup) {
        TemplateRegistry registry = getRuleTemplateRegistry();

        context.getMethods().add(
                TemplateRuntime.execute(registry.getNamedTemplate(ruleTemplate), null, new MapVariableResolverFactory(vars), registry)
        );

        registry = getInvokerTemplateRegistry();
        final String invokerClassName = context.getPkg().getName() + "." + context.getProcessDescr().getClassName() + StringUtils.ucFirst(className) + "Invoker";

        context.getInvokers().put(invokerClassName,
                TemplateRuntime.execute(registry.getNamedTemplate(invokerTemplate), null, new MapVariableResolverFactory(vars), registry)
        );

        context.getInvokerLookups().put(invokerClassName,
                invokerLookup);
        context.getDescrLookups().put(invokerClassName,
                descrLookup);
    }
}
