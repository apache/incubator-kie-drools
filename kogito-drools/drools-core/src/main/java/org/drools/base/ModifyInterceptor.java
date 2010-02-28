package org.drools.base;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.base.mvel.DroolsMVELKnowledgeHelper;
import org.drools.spi.KnowledgeHelper;
import org.mvel2.ast.ASTNode;
import org.mvel2.ast.WithNode;
import org.mvel2.integration.Interceptor;
import org.mvel2.integration.VariableResolverFactory;

public class ModifyInterceptor
    implements
    Interceptor,
    Externalizable {
    private static final long serialVersionUID = 400L;

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    }

    public void writeExternal(ObjectOutput out) throws IOException {
    }

    public int doBefore(ASTNode node,
                        VariableResolverFactory factory) {
        return 0;
    }

    public int doAfter(Object value,
                       ASTNode node,
                       VariableResolverFactory factory) {
        DroolsMVELKnowledgeHelper resolver = (DroolsMVELKnowledgeHelper) factory.getVariableResolver( "drools" );
        KnowledgeHelper helper = (KnowledgeHelper) resolver.getValue();
        helper.update( value );
        return 0;
    }
}
