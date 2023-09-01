package org.drools.mvel.builder;

import java.util.Map;

import org.drools.compiler.kie.util.BeanCreator;
import org.drools.base.base.CoreComponentsBuilder;
import org.kie.api.builder.model.QualifierModel;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;

public class MVELBeanCreator implements BeanCreator {

    private Map<String, Object> parameters;

    public MVELBeanCreator(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    @Override
    public <T> T createBean(ClassLoader cl, String type, QualifierModel qualifier ) throws Exception {
        if (qualifier != null) {
            throw new IllegalArgumentException("Cannot use a qualifier without a CDI container");
        }

        ParserConfiguration config = new ParserConfiguration();
        config.setClassLoader(cl);
        ParserContext ctx = new ParserContext( config);
        if (parameters != null) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                ctx.addVariable(entry.getKey(), entry.getValue().getClass());
            }
        }

        Object compiledExpression = MVEL.compileExpression( type, ctx );
        return (T) CoreComponentsBuilder.get().getMVELExecutor().executeExpression( compiledExpression, parameters );
    }
}
