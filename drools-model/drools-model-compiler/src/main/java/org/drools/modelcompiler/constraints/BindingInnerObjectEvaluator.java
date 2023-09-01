package org.drools.modelcompiler.constraints;

import org.drools.base.base.ValueResolver;
import org.drools.base.reteoo.BaseTuple;
import org.drools.base.rule.Declaration;
import org.drools.model.Binding;
import org.drools.model.view.BindViewItem1;
import org.kie.api.runtime.rule.FactHandle;

public class BindingInnerObjectEvaluator extends BindingEvaluator {

    public BindingInnerObjectEvaluator( Binding binding ) {
        super( null, binding );
    }

    @Override
    public Object evaluate(FactHandle handle, BaseTuple tuple, ValueResolver reteEvaluator, Declaration[] declarations, Declaration[] innerDeclarations) {
        return (( BindViewItem1 ) binding).eval( handle.getObject() );
    }
}
