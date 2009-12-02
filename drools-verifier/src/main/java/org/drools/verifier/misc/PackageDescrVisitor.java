package org.drools.verifier.misc;

import java.util.Collection;

import org.drools.base.evaluators.Operator;
import org.drools.lang.descr.AccessorDescr;
import org.drools.lang.descr.AccumulateDescr;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.CollectDescr;
import org.drools.lang.descr.ConditionalElementDescr;
import org.drools.lang.descr.DeclarativeInvokerDescr;
import org.drools.lang.descr.EntryPointDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.ExistsDescr;
import org.drools.lang.descr.FieldAccessDescr;
import org.drools.lang.descr.FieldBindingDescr;
import org.drools.lang.descr.FieldConstraintDescr;
import org.drools.lang.descr.ForallDescr;
import org.drools.lang.descr.FromDescr;
import org.drools.lang.descr.FunctionCallDescr;
import org.drools.lang.descr.LiteralRestrictionDescr;
import org.drools.lang.descr.MethodAccessDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.PatternSourceDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.lang.descr.QualifiedIdentifierRestrictionDescr;
import org.drools.lang.descr.RestrictionConnectiveDescr;
import org.drools.lang.descr.ReturnValueRestrictionDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.VariableRestrictionDescr;
import org.drools.verifier.components.Consequence;
import org.drools.verifier.components.Constraint;
import org.drools.verifier.components.EnumField;
import org.drools.verifier.components.EnumRestriction;
import org.drools.verifier.components.Field;
import org.drools.verifier.components.LiteralRestriction;
import org.drools.verifier.components.ObjectType;
import org.drools.verifier.components.OperatorDescr;
import org.drools.verifier.components.Pattern;
import org.drools.verifier.components.QualifiedIdentifierRestriction;
import org.drools.verifier.components.ReturnValueRestriction;
import org.drools.verifier.components.RulePackage;
import org.drools.verifier.components.Source;
import org.drools.verifier.components.SubPattern;
import org.drools.verifier.components.SubRule;
import org.drools.verifier.components.TextConsequence;
import org.drools.verifier.components.Variable;
import org.drools.verifier.components.VariableRestriction;
import org.drools.verifier.components.VerifierAccessorDescr;
import org.drools.verifier.components.VerifierAccumulateDescr;
import org.drools.verifier.components.VerifierCollectDescr;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.components.VerifierEntryPointDescr;
import org.drools.verifier.components.VerifierEvalDescr;
import org.drools.verifier.components.VerifierFieldAccessDescr;
import org.drools.verifier.components.VerifierFromDescr;
import org.drools.verifier.components.VerifierFunctionCallDescr;
import org.drools.verifier.components.VerifierMethodAccessDescr;
import org.drools.verifier.components.VerifierPredicateDescr;
import org.drools.verifier.components.VerifierRule;
import org.drools.verifier.components.WorkingMemory;
import org.drools.verifier.data.VerifierComponent;
import org.drools.verifier.data.VerifierData;
import org.drools.verifier.solver.Solvers;

/**
 * @author Toni Rikkola
 * 
 */
public class PackageDescrVisitor {

    private Solvers       solvers           = new Solvers();

    private VerifierData  data;

    private RulePackage   currentPackage    = null;
    private VerifierRule  currentRule       = null;
    private Pattern       currentPattern    = null;
    private Constraint    currentConstraint = null;
    private ObjectType    currentObjectType = null;
    private Field         currentField      = null;

    private WorkingMemory workingMemory     = null;

    /**
     * Adds packageDescr to given VerifierData object
     * 
     * @param packageDescr
     *            PackageDescr that will be visited.
     * @param data
     *            VerifierData where the visited objects are added.
     * @throws UnknownDescriptionException
     */
    public void addPackageDescrToData(PackageDescr packageDescr,
                                      VerifierData data) throws UnknownDescriptionException {

        this.data = data;

        visit( packageDescr );

        formPossibilities();
    }

    private void visit(Collection< ? > descrs,
                       VerifierComponent parent) throws UnknownDescriptionException {

        int orderNumber = 0;

        for ( Object o : descrs ) {
            BaseDescr descr = (BaseDescr) o;
            if ( descr instanceof PackageDescr ) {
                visit( (PackageDescr) descr );
            } else if ( descr instanceof RuleDescr ) {
                visit( (RuleDescr) descr,
                       parent );
            } else if ( descr instanceof PatternDescr ) {
                visit( (PatternDescr) descr,
                       parent,
                       orderNumber );
            } else if ( descr instanceof VariableRestrictionDescr ) {
                visit( (VariableRestrictionDescr) descr,
                       parent,
                       orderNumber );
            } else if ( descr instanceof FieldBindingDescr ) {
                visit( (FieldBindingDescr) descr,
                       parent,
                       orderNumber );
            } else if ( descr instanceof FieldConstraintDescr ) {
                visit( (FieldConstraintDescr) descr,
                       parent,
                       orderNumber );
            } else if ( descr instanceof RestrictionConnectiveDescr ) {
                visit( (RestrictionConnectiveDescr) descr,
                       parent,
                       orderNumber );
            } else if ( descr instanceof LiteralRestrictionDescr ) {
                visit( (LiteralRestrictionDescr) descr,
                       parent,
                       orderNumber );
            } else if ( descr instanceof ReturnValueRestrictionDescr ) {
                visit( (ReturnValueRestrictionDescr) descr,
                       parent,
                       orderNumber );
            } else if ( descr instanceof QualifiedIdentifierRestrictionDescr ) {
                visit( (QualifiedIdentifierRestrictionDescr) descr,
                       parent,
                       orderNumber );
            } else if ( descr instanceof FunctionCallDescr ) {
                visit( (FunctionCallDescr) descr,
                       parent,
                       orderNumber );
            } else if ( descr instanceof PredicateDescr ) {
                visit( (PredicateDescr) descr,
                       parent,
                       orderNumber );
            } else if ( descr instanceof AccessorDescr ) {
                visit( (AccessorDescr) descr,
                       parent,
                       orderNumber );
            } else if ( descr instanceof MethodAccessDescr ) {
                visit( (MethodAccessDescr) descr,
                       parent,
                       orderNumber );
            } else if ( descr instanceof FieldAccessDescr ) {
                visit( (FieldAccessDescr) descr,
                       parent,
                       orderNumber );
            } else if ( descr instanceof PatternSourceDescr ) {
                visit( (PatternSourceDescr) descr,
                       parent );
            } else if ( descr instanceof ConditionalElementDescr ) {
                visit( (ConditionalElementDescr) descr,
                       parent,
                       orderNumber );
            }

            orderNumber++;
        }
    }

    private Source visit(PatternSourceDescr descr,
                         VerifierComponent parent) throws UnknownDescriptionException {
        if ( descr instanceof AccumulateDescr ) {
            return visit( (AccumulateDescr) descr,
                          parent );
        } else if ( descr instanceof CollectDescr ) {
            return visit( (CollectDescr) descr,
                          parent );
        } else if ( descr instanceof EntryPointDescr ) {
            return visit( (EntryPointDescr) descr,
                          parent );
        } else if ( descr instanceof FromDescr ) {
            return visit( (FromDescr) descr,
                          parent );
        } else {
            throw new UnknownDescriptionException( descr );
        }
    }

    private VerifierComponent visit(DeclarativeInvokerDescr descr,
                                    VerifierComponent parent) throws UnknownDescriptionException {
        if ( descr instanceof AccessorDescr ) {
            return visit( (AccessorDescr) descr,
                          parent,
                          0 );
        } else if ( descr instanceof FieldAccessDescr ) {
            return visit( (FieldAccessDescr) descr,
                          parent,
                          0 );
        } else if ( descr instanceof FunctionCallDescr ) {
            return visit( (FunctionCallDescr) descr,
                          parent,
                          0 );
        } else if ( descr instanceof MethodAccessDescr ) {
            return visit( (MethodAccessDescr) descr,
                          parent,
                          0 );
        } else {
            throw new UnknownDescriptionException( descr );
        }
    }

    private void visit(ConditionalElementDescr descr,
                       VerifierComponent parent,
                       int orderNumber) throws UnknownDescriptionException {

        if ( descr instanceof AndDescr ) {
            visit( (AndDescr) descr,
                   parent,
                   orderNumber );
        } else if ( descr instanceof CollectDescr ) {
            visit( (CollectDescr) descr,
                   parent,
                   orderNumber );
        } else if ( descr instanceof EvalDescr ) {
            visit( (EvalDescr) descr,
                   parent,
                   orderNumber );
        } else if ( descr instanceof ExistsDescr ) {
            visit( (ExistsDescr) descr,
                   parent );
        } else if ( descr instanceof ForallDescr ) {
            visit( (ForallDescr) descr,
                   parent );
        } else if ( descr instanceof FromDescr ) {
            visit( (FromDescr) descr,
                   parent );
        } else if ( descr instanceof NotDescr ) {
            visit( (NotDescr) descr,
                   parent );
        } else if ( descr instanceof OrDescr ) {
            visit( (OrDescr) descr,
                   parent,
                   orderNumber );
        }
    }

    private void visit(ForallDescr descr,
                       VerifierComponent parent) throws UnknownDescriptionException {
        solvers.startForall();
        visit( descr.getDescrs(),
               parent );
        solvers.endForall();
    }

    private void visit(ExistsDescr descr,
                       VerifierComponent parent) throws UnknownDescriptionException {
        solvers.startExists();
        visit( descr.getDescrs(),
               parent );
        solvers.endExists();
    }

    private void visit(NotDescr descr,
                       VerifierComponent parent) throws UnknownDescriptionException {
        solvers.startNot();
        visit( descr.getDescrs(),
               parent );
        solvers.endNot();
    }

    /**
     * End leaf
     * 
     * @param descr
     * @return
     */
    private VerifierFunctionCallDescr visit(FunctionCallDescr descr,
                                            VerifierComponent parent,
                                            int orderNumber) {
        VerifierFunctionCallDescr functionCall = new VerifierFunctionCallDescr();
        functionCall.setName( descr.getName() );
        functionCall.setArguments( descr.getArguments() );
        functionCall.setOrderNumber( orderNumber );
        functionCall.setParentGuid( parent.getGuid() );
        functionCall.setParentType( parent.getVerifierComponentType() );

        return functionCall;
    }

    /**
     * End leaf
     * 
     * @param descr
     * @return
     */
    private VerifierPredicateDescr visit(PredicateDescr descr,
                                         VerifierComponent parent,
                                         int orderNumber) {

        VerifierPredicateDescr predicate = new VerifierPredicateDescr();
        predicate.setRuleName( currentRule.getRuleName() );
        predicate.setRuleGuid( currentRule.getGuid() );
        predicate.setContent( descr.getContent().toString() );
        predicate.setClassMethodName( descr.getClassMethodName() );
        predicate.setOrderNumber( orderNumber );
        predicate.setParentGuid( parent.getGuid() );
        predicate.setParentType( parent.getVerifierComponentType() );

        data.add( predicate );

        return predicate;
    }

    /**
     * End leaf
     * 
     * @param descr
     * @return
     */
    private VerifierEvalDescr visit(EvalDescr descr,
                                    VerifierComponent parent,
                                    int orderNumber) {

        VerifierEvalDescr eval = new VerifierEvalDescr();
        eval.setRuleGuid( currentRule.getGuid() );
        eval.setRuleName( currentRule.getRuleName() );
        eval.setContent( descr.getContent().toString() );
        eval.setClassMethodName( descr.getClassMethodName() );
        eval.setOrderNumber( orderNumber );
        eval.setParentGuid( parent.getGuid() );
        eval.setParentType( parent.getVerifierComponentType() );

        data.add( eval );

        return eval;
    }

    /**
     * End leaf
     * 
     * @param descr
     * @return
     * @throws UnknownDescriptionException
     */
    private VerifierFromDescr visit(FromDescr descr,
                                    VerifierComponent parent) throws UnknownDescriptionException {
        VerifierFromDescr from = new VerifierFromDescr();

        VerifierComponent ds = null;

        if ( descr.getDataSource() instanceof AccessorDescr ) {
            ds = visit( (AccessorDescr) descr.getDataSource(),
                        parent );
        } else if ( descr.getDataSource() instanceof FieldAccessDescr ) {
            ds = visit( (FieldAccessDescr) descr.getDataSource(),
                        parent );
        } else if ( descr.getDataSource() instanceof FunctionCallDescr ) {
            ds = visit( (FunctionCallDescr) descr.getDataSource(),
                        parent );
        } else if ( descr.getDataSource() instanceof MethodAccessDescr ) {
            ds = visit( (MethodAccessDescr) descr.getDataSource(),
                        parent );
        }

        from.setDataSourceGuid( ds.getGuid() );
        from.setDataSourceType( ds.getVerifierComponentType() );
        from.setParentGuid( parent.getGuid() );
        from.setParentType( parent.getVerifierComponentType() );

        data.add( from );

        return from;
    }

    private VerifierEntryPointDescr visit(EntryPointDescr descr,
                                          VerifierComponent parent) throws UnknownDescriptionException {
        // Check if already exists
        VerifierEntryPointDescr entryPoint = data.getEntryPointByEntryId( descr.getEntryId() );

        if ( entryPoint == null ) {
            entryPoint = new VerifierEntryPointDescr();

            entryPoint.setEntryId( descr.getEntryId() );

            data.add( entryPoint );
        }

        return entryPoint;
    }

    private VerifierAccumulateDescr visit(AccumulateDescr descr,
                                          VerifierComponent parent) throws UnknownDescriptionException {
        VerifierAccumulateDescr accumulate = new VerifierAccumulateDescr();

        accumulate.setInputPatternGuid( visit( descr.getInputPattern(),
                                               accumulate,
                                               0 ) );
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
        accumulate.setParentGuid( parent.getGuid() );
        accumulate.setParentType( parent.getVerifierComponentType() );

        data.add( accumulate );

        return accumulate;
    }

    private VerifierCollectDescr visit(CollectDescr descr,
                                       VerifierComponent parent) throws UnknownDescriptionException {
        VerifierCollectDescr collect = new VerifierCollectDescr();
        collect.setClassMethodName( descr.getClassMethodName() );
        collect.setInsidePatternGuid( visit( descr.getInputPattern(),
                                             collect,
                                             0 ) );
        collect.setParentGuid( parent.getGuid() );
        collect.setParentType( parent.getVerifierComponentType() );

        data.add( collect );

        return collect;
    }

    private VerifierAccessorDescr visit(AccessorDescr descr,
                                        VerifierComponent parent,
                                        int orderNumber) {
        VerifierAccessorDescr accessor = new VerifierAccessorDescr();
        accessor.setOrderNumber( orderNumber );
        accessor.setParentGuid( parent.getGuid() );
        accessor.setParentType( parent.getVerifierComponentType() );
        // TODO: I wonder what this descr does.

        data.add( accessor );

        return accessor;
    }

    /**
     * End leaf
     * 
     * @param descr
     */
    private VerifierMethodAccessDescr visit(MethodAccessDescr descr,
                                            VerifierComponent parent,
                                            int orderNumber) {
        VerifierMethodAccessDescr accessor = new VerifierMethodAccessDescr();
        accessor.setMethodName( descr.getMethodName() );
        accessor.setArguments( descr.getArguments() );
        accessor.setOrderNumber( orderNumber );
        accessor.setParentGuid( parent.getGuid() );
        accessor.setParentType( parent.getVerifierComponentType() );

        data.add( accessor );

        return accessor;
    }

    /**
     * End leaf
     * 
     * @param descr
     */
    private VerifierFieldAccessDescr visit(FieldAccessDescr descr,
                                           VerifierComponent parent,
                                           int orderNumber) {
        VerifierFieldAccessDescr accessor = new VerifierFieldAccessDescr();
        accessor.setFieldName( descr.getFieldName() );
        accessor.setArgument( descr.getArgument() );
        accessor.setOrderNumber( orderNumber );
        accessor.setParentGuid( parent.getGuid() );
        accessor.setParentType( parent.getVerifierComponentType() );

        data.add( accessor );

        return accessor;
    }

    private void visit(PackageDescr descr) throws UnknownDescriptionException {
        RulePackage rulePackage = data.getPackageByName( descr.getName() );

        if ( rulePackage == null ) {
            rulePackage = new RulePackage();

            rulePackage.setName( descr.getName() );
            data.add( rulePackage );
        }

        currentPackage = rulePackage;

        visit( descr.getRules(),
               rulePackage );
    }

    private void visit(RuleDescr descr,
                       VerifierComponent parent) throws UnknownDescriptionException {

        VerifierRule rule = new VerifierRule();
        currentRule = rule;

        rule.setRuleName( descr.getName() );

        for ( AttributeDescr attribute : descr.getAttributes().values() ) {
            rule.getAttributes().put( attribute.getName(),
                                      attribute.getValue() );
        }

        Consequence consequence = visitConsequence( rule,
                                                    descr.getConsequence() );
        rule.setConsequenceGuid( consequence.getGuid() );
        rule.setConsequenceType( consequence.getConsequenceType() );
        rule.setLineNumber( descr.getLine() );
        rule.setPackageGuid( currentPackage.getGuid() );
        rule.setParentGuid( parent.getGuid() );
        rule.setParentType( parent.getVerifierComponentType() );

        data.add( rule );

        currentPackage.getRules().add( rule );

        solvers.startRuleSolver( rule );
        visit( descr.getLhs(),
               rule,
               0 );
        solvers.endRuleSolver();
    }

    /**
     * Creates verifier object from rule consequence. Currently only supports
     * text based consequences.
     * 
     * @param o
     *            Consequence object.
     * @return Verifier object that implements the Consequence interface.
     */
    private Consequence visitConsequence(VerifierComponent parent,
                                         Object o) {

        TextConsequence consequence = new TextConsequence();

        String text = o.toString();

        /*
         * Strip all comments out of the code.
         */
        StringBuffer buffer = new StringBuffer( text );
        int commentIndex = buffer.indexOf( "//" );

        while ( commentIndex != -1 ) {
            buffer = buffer.delete( commentIndex,
                                    buffer.indexOf( "\n",
                                                    commentIndex ) );
            commentIndex = buffer.indexOf( "//" );
        }

        text = buffer.toString();

        /*
         * Strip all useless characters out of the code.
         */
        text = text.replaceAll( "\n",
                                "" );
        text = text.replaceAll( "\r",
                                "" );
        text = text.replaceAll( "\t",
                                "" );
        text = text.replaceAll( " ",
                                "" );

        consequence.setText( text );
        consequence.setRuleGuid( currentRule.getGuid() );
        consequence.setRuleName( currentRule.getRuleName() );
        consequence.setParentGuid( parent.getGuid() );
        consequence.setParentType( parent.getVerifierComponentType() );

        data.add( consequence );

        return consequence;
    }

    private void visit(OrDescr descr,
                       VerifierComponent parent,
                       int orderNumber) throws UnknownDescriptionException {
        OperatorDescr operatorDescr = new OperatorDescr( OperatorDescr.Type.OR );
        operatorDescr.setOrderNumber( orderNumber );
        operatorDescr.setParentGuid( parent.getGuid() );
        operatorDescr.setParentType( parent.getVerifierComponentType() );

        data.add( operatorDescr );

        solvers.startOperator( OperatorDescr.Type.OR );
        visit( descr.getDescrs(),
               operatorDescr );
        solvers.endOperator();
    }

    private void visit(AndDescr descr,
                       VerifierComponent parent,
                       int orderNumber) throws UnknownDescriptionException {
        OperatorDescr operatorDescr = new OperatorDescr( OperatorDescr.Type.AND );
        operatorDescr.setOrderNumber( orderNumber );
        operatorDescr.setParentGuid( parent.getGuid() );
        operatorDescr.setParentType( parent.getVerifierComponentType() );

        data.add( operatorDescr );

        solvers.startOperator( OperatorDescr.Type.AND );
        visit( descr.getDescrs(),
               operatorDescr );
        solvers.endOperator();
    }

    private String visit(PatternDescr descr,
                         VerifierComponent parent,
                         int orderNumber) throws UnknownDescriptionException {

        ObjectType objectType = findOrCreateNewObjectType( descr.getObjectType() );
        currentObjectType = objectType;

        Pattern pattern = new Pattern();
        pattern.setRuleGuid( currentRule.getGuid() );
        pattern.setRuleName( currentRule.getRuleName() );
        pattern.setObjectTypeGuid( objectType.getGuid() );
        pattern.setName( objectType.getName() );
        pattern.setPatternNot( solvers.getRuleSolver().isChildNot() );
        pattern.setPatternExists( solvers.getRuleSolver().isExists() );
        pattern.setPatternForall( solvers.getRuleSolver().isForall() );
        pattern.setOrderNumber( orderNumber );
        pattern.setParentGuid( parent.getGuid() );
        pattern.setParentType( parent.getVerifierComponentType() );

        data.add( pattern );
        currentPattern = pattern;

        if ( descr.getIdentifier() != null ) {
            Variable variable = new Variable();
            variable.setRuleGuid( currentRule.getGuid() );
            variable.setRuleName( currentRule.getRuleName() );
            variable.setName( descr.getIdentifier() );

            variable.setObjectTypeType( VerifierComponentType.OBJECT_TYPE.getType() );
            variable.setObjectTypeGuid( objectType.getGuid() );
            variable.setObjectTypeType( descr.getObjectType() );

            data.add( variable );
        }

        Source source;
        // visit source.
        if ( descr.getSource() != null ) {
            source = visit( descr.getSource(),
                            pattern );
        } else {
            if ( workingMemory == null ) {
                workingMemory = new WorkingMemory();
                data.add( workingMemory );
            }
            source = workingMemory;
        }
        pattern.setSourceGuid( source.getGuid() );
        pattern.setSourceType( source.getVerifierComponentType() );

        solvers.startPatternSolver( pattern );
        visit( descr.getConstraint(),
               pattern,
               0 );
        solvers.endPatternSolver();

        return pattern.getGuid();
    }

    private void visit(FieldConstraintDescr descr,
                       VerifierComponent parent,
                       int orderNumber) throws UnknownDescriptionException {

        Field field = data.getFieldByObjectTypeAndFieldName( currentObjectType.getName(),
                                                             descr.getFieldName() );
        if ( field == null ) {
            field = createField( descr.getFieldName(),
                                 currentObjectType.getGuid(),
                                 currentObjectType.getName(),
                                 parent );
            data.add( field );
        }
        currentField = field;

        Constraint constraint = new Constraint();

        constraint.setRuleGuid( currentRule.getGuid() );
        constraint.setFieldGuid( currentField.getGuid() );
        constraint.setFieldName( currentField.getName() );
        constraint.setPatternGuid( currentPattern.getGuid() );
        constraint.setPatternIsNot( currentPattern.isPatternNot() );
        constraint.setFieldGuid( field.getGuid() );
        constraint.setOrderNumber( orderNumber );
        constraint.setParentGuid( parent.getGuid() );
        constraint.setParentType( parent.getVerifierComponentType() );

        data.add( constraint );

        currentConstraint = constraint;

        visit( descr.getRestriction(),
               constraint,
               0 );
    }

    private void visit(RestrictionConnectiveDescr descr,
                       VerifierComponent parent,
                       int orderNumber) throws UnknownDescriptionException {

        if ( descr.getConnective() == RestrictionConnectiveDescr.AND ) {

            solvers.startOperator( OperatorDescr.Type.AND );
            visit( descr.getRestrictions(),
                   parent );
            solvers.endOperator();

        } else if ( descr.getConnective() == RestrictionConnectiveDescr.OR ) {

            solvers.startOperator( OperatorDescr.Type.OR );
            visit( descr.getRestrictions(),
                   parent );
            solvers.endOperator();

        } else {
            throw new UnknownDescriptionException( descr );
        }
    }

    /**
     * End
     * 
     * @param descr
     */
    private void visit(FieldBindingDescr descr,
                       VerifierComponent parent,
                       int orderNumber) {

        Variable variable = new Variable();
        variable.setRuleGuid( currentRule.getGuid() );
        variable.setRuleName( currentRule.getRuleName() );
        variable.setName( descr.getIdentifier() );
        variable.setOrderNumber( orderNumber );
        variable.setParentGuid( parent.getGuid() );
        variable.setParentType( parent.getVerifierComponentType() );

        variable.setObjectTypeType( VerifierComponentType.FIELD.getType() );

        data.add( variable );
    }

    /**
     * End
     * 
     * Foo( bar == $bar )<br>
     * $bar is a VariableRestrictionDescr
     * 
     * @param descr
     */
    private void visit(VariableRestrictionDescr descr,
                       VerifierComponent parent,
                       int orderNumber) {

        Variable variable = data.getVariableByRuleAndVariableName( currentRule.getRuleName(),
                                                                   descr.getIdentifier() );
        VariableRestriction restriction = new VariableRestriction();

        restriction.setRuleGuid( currentRule.getGuid() );
        restriction.setRuleName( currentRule.getRuleName() );
        restriction.setPatternGuid( currentPattern.getGuid() );
        restriction.setPatternIsNot( currentPattern.isPatternNot() );
        restriction.setConstraintGuid( currentConstraint.getGuid() );
        restriction.setFieldGuid( currentConstraint.getFieldGuid() );
        restriction.setOperator( Operator.determineOperator( descr.getEvaluator(),
                                                             descr.isNegated() ) );
        restriction.setVariable( variable );
        restriction.setOrderNumber( orderNumber );
        restriction.setParentGuid( parent.getGuid() );
        restriction.setParentType( parent.getVerifierComponentType() );

        // Set field value, if it is unset.
        currentField.setFieldType( Field.FieldType.VARIABLE );

        data.add( restriction );
        solvers.addRestriction( restriction );
    }

    /**
     * End
     * 
     * @param descr
     */
    private void visit(ReturnValueRestrictionDescr descr,
                       VerifierComponent parent,
                       int orderNumber) {

        ReturnValueRestriction restriction = new ReturnValueRestriction();

        restriction.setRuleGuid( currentRule.getGuid() );
        restriction.setRuleName( currentRule.getRuleName() );
        restriction.setPatternGuid( currentPattern.getGuid() );
        restriction.setPatternIsNot( currentPattern.isPatternNot() );
        restriction.setConstraintGuid( currentConstraint.getGuid() );
        restriction.setFieldGuid( currentConstraint.getFieldGuid() );
        restriction.setOperator( Operator.determineOperator( descr.getEvaluator(),
                                                             descr.isNegated() ) );
        restriction.setClassMethodName( descr.getClassMethodName() );
        restriction.setContent( descr.getContent() );
        restriction.setDeclarations( descr.getDeclarations() );
        restriction.setOrderNumber( orderNumber );
        restriction.setParentGuid( parent.getGuid() );
        restriction.setParentType( parent.getVerifierComponentType() );

        data.add( restriction );
        solvers.addRestriction( restriction );

    }

    /**
     * End
     * 
     * @param descr
     */
    private void visit(LiteralRestrictionDescr descr,
                       VerifierComponent parent,
                       int orderNumber) {

        LiteralRestriction restriction = new LiteralRestriction();

        restriction.setRuleGuid( currentRule.getGuid() );
        restriction.setRuleName( currentRule.getRuleName() );
        restriction.setRuleGuid( currentRule.getGuid() );
        restriction.setPatternGuid( currentPattern.getGuid() );
        restriction.setPatternIsNot( currentPattern.isPatternNot() );
        restriction.setConstraintGuid( currentConstraint.getGuid() );
        restriction.setFieldGuid( currentConstraint.getFieldGuid() );
        restriction.setOperator( Operator.determineOperator( descr.getEvaluator(),
                                                             descr.isNegated() ) );
        restriction.setValue( descr.getText() );
        restriction.setOrderNumber( orderNumber );
        restriction.setParentGuid( parent.getGuid() );
        restriction.setParentType( parent.getVerifierComponentType() );

        // Set field value, if it is unset.
        currentField.setFieldType( restriction.getValueType() );

        data.add( restriction );
        solvers.addRestriction( restriction );
    }

    /**
     * End
     * 
     * @param descr
     */
    private void visit(QualifiedIdentifierRestrictionDescr descr,
                       VerifierComponent parent,
                       int orderNumber) {

        String text = descr.getText();

        String base = text.substring( 0,
                                      text.indexOf( "." ) );
        String fieldName = text.substring( text.indexOf( "." ) );

        Variable variable = data.getVariableByRuleAndVariableName( currentRule.getRuleName(),
                                                                   base );

        if ( variable != null ) {

            QualifiedIdentifierRestriction restriction = new QualifiedIdentifierRestriction();

            restriction.setRuleGuid( currentRule.getGuid() );
            restriction.setPatternGuid( currentPattern.getGuid() );
            restriction.setPatternIsNot( currentPattern.isPatternNot() );
            restriction.setConstraintGuid( currentConstraint.getGuid() );
            restriction.setFieldGuid( currentConstraint.getFieldGuid() );
            restriction.setOperator( Operator.determineOperator( descr.getEvaluator(),
                                                                 descr.isNegated() ) );
            restriction.setVariableGuid( variable.getGuid() );
            restriction.setVariableName( base );
            restriction.setVariablePath( fieldName );
            restriction.setOrderNumber( orderNumber );
            restriction.setParentGuid( parent.getGuid() );
            restriction.setParentType( parent.getVerifierComponentType() );

            // Set field value, if it is not set.
            currentField.setFieldType( Field.FieldType.VARIABLE );

            variable.setObjectTypeType( VerifierComponentType.FIELD.getType() );

            data.add( restriction );
            solvers.addRestriction( restriction );
        } else {

            EnumField enumField = (EnumField) data.getFieldByObjectTypeAndFieldName( base,
                                                                                     fieldName );
            if ( enumField == null ) {
                ObjectType objectType = findOrCreateNewObjectType( base );

                enumField = new EnumField();

                enumField.setObjectTypeGuid( objectType.getGuid() );
                enumField.setObjectTypeName( objectType.getName() );
                enumField.setName( fieldName );

                objectType.getFields().add( enumField );

                data.add( enumField );
            }

            EnumRestriction restriction = new EnumRestriction();

            restriction.setRuleGuid( currentRule.getGuid() );
            restriction.setPatternGuid( currentPattern.getGuid() );
            restriction.setPatternIsNot( currentPattern.isPatternNot() );
            restriction.setConstraintGuid( currentConstraint.getGuid() );
            restriction.setFieldGuid( currentConstraint.getFieldGuid() );
            restriction.setOperator( Operator.determineOperator( descr.getEvaluator(),
                                                                 descr.isNegated() ) );
            restriction.setEnumBaseGuid( enumField.getGuid() );
            restriction.setEnumBase( base );
            restriction.setEnumName( fieldName );
            restriction.setOrderNumber( orderNumber );
            restriction.setParentGuid( parent.getGuid() );
            restriction.setParentType( parent.getVerifierComponentType() );

            // Set field value, if it is not set.
            currentField.setFieldType( Field.FieldType.ENUM );

            data.add( restriction );
            solvers.addRestriction( restriction );
        }
    }

    private ObjectType findOrCreateNewObjectType(String name) {
        ObjectType objectType = data.getObjectTypeByName( name );
        if ( objectType == null ) {
            objectType = new ObjectType();
            objectType.setName( name );
            data.add( objectType );
        }
        return objectType;
    }

    private Field createField(String fieldName,
                              String classGuid,
                              String className,
                              VerifierComponent parent) {
        Field field = new Field();
        field.setObjectTypeGuid( classGuid );
        field.setObjectTypeName( className );
        field.setName( fieldName );
        field.setParentGuid( parent.getGuid() );
        field.setParentType( parent.getVerifierComponentType() );

        currentObjectType.getFields().add( field );
        return field;
    }

    private void formPossibilities() {

        for ( SubPattern subPattern : solvers.getPatternPossibilities() ) {
            Pattern pattern = data.getVerifierObject( VerifierComponentType.PATTERN,
                                                      subPattern.getPatternGuid() );

            subPattern.setPattern( pattern );

            data.add( subPattern );
        }

        for ( SubRule subRule : solvers.getRulePossibilities() ) {
            VerifierRule rule = data.getVerifierObject( VerifierComponentType.RULE,
                                                        subRule.getRuleGuid() );

            subRule.setRule( rule );

            data.add( subRule );
        }
    }
}
