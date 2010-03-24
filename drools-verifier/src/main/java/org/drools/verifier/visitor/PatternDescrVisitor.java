package org.drools.verifier.visitor;

import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.CollectDescr;
import org.drools.lang.descr.DeclarativeInvokerDescr;
import org.drools.lang.descr.EntryPointDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.verifier.components.Import;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.OperatorDescrType;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.PatternOperatorDescr;
import org.drools.verifier.components.Variable;
import org.drools.verifier.components.VerifierAccumulateDescr;
import org.drools.verifier.components.VerifierCollectDescr;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.components.EntryPoint;
import org.drools.verifier.components.VerifierFromDescr;
import org.drools.verifier.components.PatternEval;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.components.WorkingMemory;
import org.drools.verifier.data.VerifierComponent;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.solver.Solvers;

public class PatternDescrVisitor extends ConditionalElementDescrVisitor {

    private ObjectType    objectType;
    private Pattern       pattern;
    private WorkingMemory workingMemory = null;

    public PatternDescrVisitor(VerifierData data,
                               VerifierRule rule,
                               Solvers solvers) {
        super( data,
               solvers );
        this.rule = rule;
    }

    public void visitPatternDescr(PatternDescr descr,
                                  VerifierComponent parent,
                                  int orderNumber) throws UnknownDescriptionException {
        Pattern pattern = visitPatternDescr( descr,
                                             orderNumber );

        pattern.setParentPath( parent.getPath() );
        pattern.setParentType( parent.getVerifierComponentType() );
    }

    public Pattern visitPatternDescr(PatternDescr descr,
                                     int orderNumber) throws UnknownDescriptionException {

        objectType = data.getObjectTypeByFullName( descr.getObjectType() );

        if ( objectType == null ) {
            Import objectImport = data.getImportByName( descr.getObjectType() );

            if ( objectImport != null ) {
                objectType = ObjectTypeFactory.createObjectType( objectImport );
            } else {
                objectType = ObjectTypeFactory.createObjectType( descr.getObjectType() );
            }

            data.add( objectType );
        }

        pattern = new Pattern( rule );
        pattern.setObjectTypePath( objectType.getPath() );
        pattern.setName( objectType.getName() );
        pattern.setPatternNot( solvers.getRuleSolver().isChildNot() );
        pattern.setPatternExists( solvers.getRuleSolver().isExists() );
        pattern.setPatternForall( solvers.getRuleSolver().isForall() );
        pattern.setOrderNumber( orderNumber );

        data.add( pattern );

        if ( descr.getIdentifier() != null ) {
            Variable variable = new Variable( rule );
            variable.setName( descr.getIdentifier() );

            variable.setObjectTypeType( VerifierComponentType.OBJECT_TYPE.getType() );
            variable.setObjectTypePath( objectType.getPath() );
            variable.setObjectTypeType( descr.getObjectType() );

            data.add( variable );
        }

        // visit source.
        if ( descr.getSource() != null ) {
            visit( descr.getSource() );
        } else {
            if ( workingMemory == null ) {
                workingMemory = new WorkingMemory();
                data.add( workingMemory );
            }
            pattern.setSourcePath( workingMemory.getPath() );
            pattern.setSourceType( workingMemory.getVerifierComponentType() );
        }

        solvers.startPatternSolver( pattern );

        visit( descr.getConstraint() );

        solvers.endPatternSolver();

        return pattern;
    }

    @Override
    protected VerifierComponent getParent() {
        return pattern;
    }

    @Override
    protected void visit(Object descr) throws UnknownDescriptionException {
        if ( descr instanceof AccumulateDescr ) {
            visitAccumulateDescr( (AccumulateDescr) descr );
        } else if ( descr instanceof CollectDescr ) {
            visitCollectDescr( (CollectDescr) descr );
        } else if ( descr instanceof EntryPointDescr ) {
            visitEntryPointDescr( (EntryPointDescr) descr );
        } else if ( descr instanceof FromDescr ) {
            visitFromDescr( (FromDescr) descr );
        } else if ( descr instanceof AndDescr ) {
            visitAndDescr( (AndDescr) descr );
        } else if ( descr instanceof OrDescr ) {
            visitOrDescr( (OrDescr) descr );
        } else if ( descr instanceof FieldConstraintDescr ) {
            visitFieldConstraintDescr( (FieldConstraintDescr) descr );
        } else if ( descr instanceof FieldBindingDescr ) {
            visitFieldBindingDescr( (FieldBindingDescr) descr );
        } else if ( descr instanceof PredicateDescr ) {
            visitPredicateDescr( (PredicateDescr) descr );
        } else {
            throw new UnknownDescriptionException( (BaseDescr) descr );
        }
    }

    /**
     * End leaf
     * 
     * @param descr
     * @return
     */
    private PatternEval visitPredicateDescr(PredicateDescr descr) {

        PatternEval predicate = new PatternEval( pattern );
        predicate.setContent( descr.getContent().toString() );
        predicate.setClassMethodName( descr.getClassMethodName() );
        predicate.setOrderNumber( orderNumber.next() );
        predicate.setParentPath( pattern.getPath() );
        predicate.setParentType( pattern.getVerifierComponentType() );

        data.add( predicate );

        solvers.addPatternComponent( predicate );

        return predicate;
    }

    /**
     * End
     * 
     * @param descr
     */
    private void visitFieldBindingDescr(FieldBindingDescr descr) {

        Variable variable = new Variable( rule );
        variable.setName( descr.getIdentifier() );
        variable.setOrderNumber( orderNumber.next() );
        variable.setParentPath( rule.getPath() );
        variable.setParentType( rule.getVerifierComponentType() );

        variable.setObjectTypeType( VerifierComponentType.FIELD.getType() );

        data.add( variable );
    }

    private void visitFieldConstraintDescr(FieldConstraintDescr descr) throws UnknownDescriptionException {
        FieldConstraintDescrVisitor visitor = new FieldConstraintDescrVisitor( data,
                                                                               pattern,
                                                                               solvers,
                                                                               orderNumber.next() );
        visitor.visitFieldConstraintDescr( descr );
    }

    private void visitEntryPointDescr(EntryPointDescr descr) throws UnknownDescriptionException {
        // Check if already exists
        EntryPoint entryPoint = data.getEntryPointByEntryId( descr.getEntryId() );

        if ( entryPoint == null ) {
            entryPoint = new EntryPoint();

            entryPoint.setEntryPointName( descr.getEntryId() );

            data.add( entryPoint );
        }

        pattern.setSourcePath( entryPoint.getPath() );
        pattern.setSourceType( entryPoint.getVerifierComponentType() );
    }

    /**
     * End leaf
     * 
     * @param descr
     * @return
     * @throws UnknownDescriptionException
     */
    private VerifierFromDescr visitFromDescr(FromDescr descr) throws UnknownDescriptionException {
        VerifierFromDescr from = new VerifierFromDescr( pattern );

        VerifierComponent ds = visit( descr.getDataSource() );

        from.setDataSourcePath( ds.getPath() );
        from.setDataSourceType( ds.getVerifierComponentType() );
        from.setParentPath( rule.getPath() );
        from.setParentType( rule.getVerifierComponentType() );

        data.add( from );

        return from;
    }

    private VerifierCollectDescr visitCollectDescr(CollectDescr descr) throws UnknownDescriptionException {
        VerifierCollectDescr collect = new VerifierCollectDescr( pattern );
        collect.setClassMethodName( descr.getClassMethodName() );

        PatternDescrVisitor visitor = new PatternDescrVisitor( data,
                                                               rule,
                                                               solvers );

        visitor.visitPatternDescr( descr.getInputPattern(),
                                   collect,
                                   0 );
        collect.setParentPath( rule.getPath() );
        collect.setParentType( rule.getVerifierComponentType() );

        data.add( collect );

        return collect;
    }

    private VerifierAccumulateDescr visitAccumulateDescr(AccumulateDescr descr) throws UnknownDescriptionException {
        VerifierAccumulateDescr accumulate = new VerifierAccumulateDescr( pattern );

        PatternDescrVisitor visitor = new PatternDescrVisitor( data,
                                                               rule,
                                                               solvers );
        Pattern parentPattern = visitor.visitPatternDescr( descr.getInputPattern(),
                                                           0 );

        accumulate.setInitCode( descr.getInitCode() );
        accumulate.setActionCode( descr.getActionCode() );
        accumulate.setReverseCode( descr.getReverseCode() );
        accumulate.setResultCode( descr.getResultCode() );

        // XXX: Array seems to be always null.
        // accumulate.setDeclarations(descr.getDeclarations());

        accumulate.setClassName( descr.getClassName() );
        accumulate.setExternalFunction( descr.isExternalFunction() );
        accumulate.setFunctionIdentifier( descr.getFunctionIdentifier() );
        accumulate.setExpression( descr.getExpression() );
        accumulate.setParentPath( parentPattern.getPath() );
        accumulate.setParentType( parentPattern.getVerifierComponentType() );

        data.add( accumulate );

        return accumulate;
    }

    protected VerifierComponent visit(DeclarativeInvokerDescr descr) throws UnknownDescriptionException {

        DeclarativeInvokerDescrVisitor visitor = new DeclarativeInvokerDescrVisitor( data,
                                                                                     rule );

        return visitor.visit( descr );
    }

    @Override
    protected void visitAndDescr(AndDescr descr) throws UnknownDescriptionException {
        PatternOperatorDescr operatorDescr = new PatternOperatorDescr( pattern,
                                                                       OperatorDescrType.AND );
        operatorDescr.setOrderNumber( orderNumber.next() );
        operatorDescr.setParentPath( getParent().getPath() );
        operatorDescr.setParentType( getParent().getVerifierComponentType() );

        data.add( operatorDescr );

        solvers.startOperator( OperatorDescrType.AND );
        visit( descr.getDescrs() );
        solvers.endOperator();
    }

    @Override
    protected void visitOrDescr(OrDescr descr) throws UnknownDescriptionException {
        PatternOperatorDescr operatorDescr = new PatternOperatorDescr( pattern,
                                                                       OperatorDescrType.OR );
        operatorDescr.setOrderNumber( orderNumber.next() );

        data.add( operatorDescr );

        solvers.startOperator( OperatorDescrType.OR );
        visit( descr.getDescrs() );
        solvers.endOperator();
    }

}
