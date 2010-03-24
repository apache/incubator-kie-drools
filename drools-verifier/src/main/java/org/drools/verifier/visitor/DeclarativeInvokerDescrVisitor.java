package org.drools.verifier.visitor;

import org.drools.lang.descr.AccessorDescr;
import org.drools.lang.descr.DeclarativeInvokerDescr;
import org.drools.lang.descr.FieldAccessDescr;
import org.drools.lang.descr.FunctionCallDescr;
import org.drools.lang.descr.MethodAccessDescr;
import org.drools.verifier.components.VerifierAccessorDescr;
import org.drools.verifier.components.VerifierFieldAccessDescr;
import org.drools.verifier.components.VerifierFunctionCallDescr;
import org.drools.verifier.components.VerifierMethodAccessDescr;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.data.VerifierComponent;
import org.drools.verifier.data.VerifierData;

public class DeclarativeInvokerDescrVisitor {

    private final VerifierData data;

    private final VerifierRule rule;

    private int                orderNumber = 0;

    public DeclarativeInvokerDescrVisitor(VerifierData data,
                                          VerifierRule rule) {
        this.data = data;
        this.rule = rule;
    }

    public VerifierComponent visit(DeclarativeInvokerDescr descr) throws UnknownDescriptionException {
        VerifierComponent ds;

        if ( descr instanceof AccessorDescr ) {
            ds = visit( (AccessorDescr) descr );
        } else if ( descr instanceof FieldAccessDescr ) {
            ds = visit( (FieldAccessDescr) descr );
        } else if ( descr instanceof FunctionCallDescr ) {
            ds = visit( (FunctionCallDescr) descr );
        } else if ( descr instanceof MethodAccessDescr ) {
            ds = visit( (MethodAccessDescr) descr );
        } else {
            throw new UnknownDescriptionException( descr );
        }

        return ds;
    }

    /**
     * End leaf
     * 
     * @param descr
     * @return
     */
    private VerifierFunctionCallDescr visit(FunctionCallDescr descr) {
        VerifierFunctionCallDescr functionCall = new VerifierFunctionCallDescr( rule );
        functionCall.setName( descr.getName() );
        functionCall.setArguments( descr.getArguments() );
        functionCall.setOrderNumber( orderNumber );
        functionCall.setParentPath( rule.getPath() );
        functionCall.setParentType( rule.getVerifierComponentType() );

        return functionCall;
    }

    /**
     * End leaf
     * 
     * @param descr
     */
    private VerifierFieldAccessDescr visit(FieldAccessDescr descr) {
        VerifierFieldAccessDescr accessor = new VerifierFieldAccessDescr( rule );
        accessor.setFieldName( descr.getFieldName() );
        accessor.setArgument( descr.getArgument() );
        accessor.setOrderNumber( orderNumber );
        accessor.setParentPath( rule.getPath() );
        accessor.setParentType( rule.getVerifierComponentType() );

        data.add( accessor );

        return accessor;
    }

    private VerifierAccessorDescr visit(AccessorDescr descr) {
        VerifierAccessorDescr accessor = new VerifierAccessorDescr( rule );
        accessor.setOrderNumber( orderNumber );
        accessor.setParentPath( rule.getPath() );
        accessor.setParentType( rule.getVerifierComponentType() );
        // TODO: I wonder what this descr does.

        data.add( accessor );

        return accessor;
    }

    /**
     * End leaf
     * 
     * @param descr
     */
    private VerifierMethodAccessDescr visit(MethodAccessDescr descr) {
        VerifierMethodAccessDescr accessor = new VerifierMethodAccessDescr( rule );
        accessor.setMethodName( descr.getMethodName() );
        accessor.setArguments( descr.getArguments() );
        accessor.setOrderNumber( orderNumber );
        accessor.setParentPath( rule.getPath() );
        accessor.setParentType( rule.getVerifierComponentType() );

        data.add( accessor );

        return accessor;
    }

}
