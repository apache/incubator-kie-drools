package org.drools.base;

import java.io.Serializable;

import org.drools.base.mvel.DroolsMVELKnowledgeHelper;
import org.drools.spi.KnowledgeHelper;
import org.mvel.ASTNode;
import org.mvel.ast.WithNode;
import org.mvel.integration.Interceptor;
import org.mvel.integration.VariableResolverFactory;

public class ModifyInterceptor
    implements
    Interceptor,
    Serializable {
    private static final long serialVersionUID = 400L;

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
