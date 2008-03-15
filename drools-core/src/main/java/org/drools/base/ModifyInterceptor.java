package org.drools.base;

import java.io.Serializable;
import java.io.Externalizable;
import java.io.ObjectOutput;
import java.io.IOException;
import java.io.ObjectInput;

import org.drools.base.mvel.DroolsMVELKnowledgeHelper;
import org.drools.spi.KnowledgeHelper;
import org.mvel.ast.ASTNode;
import org.mvel.ast.WithNode;
import org.mvel.integration.Interceptor;
import org.mvel.integration.VariableResolverFactory;

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
        Object object = ((WithNode) node).getNestedStatement().getValue( null,
                                                                         factory );

        DroolsMVELKnowledgeHelper resolver = (DroolsMVELKnowledgeHelper) factory.getVariableResolver( "drools" );
        KnowledgeHelper helper = (KnowledgeHelper) resolver.getValue();
        helper.modifyRetract( object );
        return 0;
    }

    public int doAfter(Object value,
                       ASTNode node,
                       VariableResolverFactory factory) {
        DroolsMVELKnowledgeHelper resolver = (DroolsMVELKnowledgeHelper) factory.getVariableResolver( "drools" );
        KnowledgeHelper helper = (KnowledgeHelper) resolver.getValue();
        helper.modifyInsert( value );
        return 0;
    }
}
