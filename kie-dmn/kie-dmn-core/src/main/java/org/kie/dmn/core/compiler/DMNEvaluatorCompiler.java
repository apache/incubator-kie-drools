package org.kie.dmn.core.compiler;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.api.EvaluatorResult;
import org.kie.dmn.core.ast.*;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.runtime.FEELFunction;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.decisiontables.*;
import org.kie.dmn.feel.runtime.functions.DTInvokerFunction;
import org.kie.dmn.model.v1_1.*;
import org.kie.dmn.model.v1_1.HitPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.namespace.QName;

import static java.util.stream.Collectors.toList;

public class DMNEvaluatorCompiler {

    private static final Logger logger = LoggerFactory.getLogger( DMNEvaluatorCompiler.class );

    private final DMNFEELHelper feel;
    private DMNCompilerImpl compiler;

    public DMNEvaluatorCompiler( DMNCompilerImpl compiler, DMNFEELHelper feel ) {
        this.compiler = compiler;
        this.feel = feel;
    }

    public DMNExpressionEvaluator compileExpression(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String exprName, Expression expression) {
        if ( expression == null ) {
            if( node instanceof DecisionNode ) {
                MsgUtil.reportMessage( logger,
                                       DMNMessage.Severity.ERROR,
                                       node.getSource(),
                                       model,
                                       null,
                                       null,
                                       Msg.MISSING_EXPRESSION_FOR_DECISION,
                                       node.getIdentifierString() );
            } else if( node instanceof BusinessKnowledgeModelNode ) {
                MsgUtil.reportMessage( logger,
                                       DMNMessage.Severity.ERROR,
                                       node.getSource(),
                                       model,
                                       null,
                                       null,
                                       Msg.MISSING_EXPRESSION_FOR_BKM,
                                       node.getIdentifierString() );
            } else {
                MsgUtil.reportMessage( logger,
                                       DMNMessage.Severity.ERROR,
                                       node.getSource(),
                                       model,
                                       null,
                                       null,
                                       Msg.MISSING_EXPRESSION_FOR_NODE,
                                       node.getIdentifierString() );
            }

        } else if ( expression instanceof LiteralExpression ) {
            return compileLiteralExpression( ctx, model, node, exprName, (LiteralExpression) expression );
        } else if ( expression instanceof DecisionTable ) {
            return compileDecisionTable( ctx, model, node, exprName, (DecisionTable) expression );
        } else if ( expression instanceof FunctionDefinition ) {
            return compileFunctionDefinition( ctx, model, node, exprName, (FunctionDefinition) expression );
        } else if ( expression instanceof Context ) {
            return compileContext( ctx, model, node, exprName, (Context) expression );
        } else if ( expression instanceof org.kie.dmn.model.v1_1.List ) {
            return compileList( ctx, model, node, exprName, (org.kie.dmn.model.v1_1.List) expression );
        } else if ( expression instanceof Relation ) {
            return compileRelation( ctx, model, node, exprName, (Relation) expression );
        } else if ( expression instanceof Invocation ) {
            return compileInvocation( ctx, model, node, (Invocation) expression );
        } else {
            MsgUtil.reportMessage( logger,
                                   DMNMessage.Severity.ERROR,
                                   node.getSource(),
                                   model,
                                   null,
                                   null,
                                   Msg.EXPR_TYPE_NOT_SUPPORTED_IN_NODE,
                                   expression.getClass().getSimpleName(),
                                   node.getIdentifierString() );
        }
        return null;
    }

    private DMNExpressionEvaluator compileInvocation(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, Invocation expression) {
        Invocation invocation = expression;
        // expression must be a literal text with the name of the function
        String functionName = ((LiteralExpression) invocation.getExpression()).getText();
        DMNInvocationEvaluator invEval = new DMNInvocationEvaluator( node.getName(), node.getSource(), functionName, invocation, null );
        for ( Binding binding : invocation.getBinding() ) {
            if( binding.getParameter() == null ) {
                // error, missing binding parameter
                MsgUtil.reportMessage( logger,
                                       DMNMessage.Severity.ERROR,
                                       binding,
                                       model,
                                       null,
                                       null,
                                       Msg.MISSING_PARAMETER_FOR_INVOCATION,
                                       node.getIdentifierString() );
                return null;
            }
            if( binding.getExpression() == null ) {
                MsgUtil.reportMessage( logger,
                                       DMNMessage.Severity.ERROR,
                                       binding,
                                       model,
                                       null,
                                       null,
                                       Msg.MISSING_PARAMETER_FOR_INVOCATION,
                                       node.getIdentifierString() );
                return null;
            }
            invEval.addParameter(
                    binding.getParameter().getName(),
                    compiler.resolveTypeRef( model, node, binding.getParameter(), binding.getParameter(), binding.getParameter().getTypeRef() ),
                    compileExpression( ctx, model, node, binding.getParameter().getName(), binding.getExpression() ) );
        }
        return invEval;
    }

    private DMNExpressionEvaluator compileRelation(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String relationName, Relation expression) {
        Relation relationDef = expression;
        DMNRelationEvaluator relationEval = new DMNRelationEvaluator( node.getName(), node.getSource(), relationDef );
        for ( InformationItem col : relationDef.getColumn() ) {
            DMNCompilerHelper.checkVariableName( model, col, col.getName() );
            relationEval.addColumn( col.getName() );
        }
        for ( org.kie.dmn.model.v1_1.List row : relationDef.getRow() ) {
            java.util.List<DMNExpressionEvaluator> values = new ArrayList<>();
            for ( Expression expr : row.getExpression() ) {
                values.add( compileExpression( ctx, model, node, relationName, expr ) );
            }
            relationEval.addRow( values );
        }
        return relationEval;
    }

    private DMNExpressionEvaluator compileList(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String listName, org.kie.dmn.model.v1_1.List expression) {
        org.kie.dmn.model.v1_1.List listDef = expression;
        DMNListEvaluator listEval = new DMNListEvaluator( node.getName(), node.getSource(), listDef );
        for ( Expression expr : listDef.getExpression() ) {
            listEval.addElement( compileExpression( ctx, model, node, listName, expr ) );
        }
        return listEval;
    }

    private DMNExpressionEvaluator compileContext(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String contextName, Context expression) {
        Context ctxDef = expression;
        DMNContextEvaluator ctxEval = new DMNContextEvaluator( node.getName(), ctxDef );
        ctx.enterFrame();
        try {
            for ( ContextEntry ce : ctxDef.getContextEntry() ) {
                if ( ce.getVariable() != null ) {
                    String entryName = ce.getVariable().getName();
                    DMNCompilerHelper.checkVariableName( model, node.getSource(), entryName );
                    DMNType entryType = compiler.resolveTypeRef( model, node, ce.getVariable(), ce.getVariable(), ce.getVariable().getTypeRef() );
                    DMNExpressionEvaluator evaluator = compileExpression( ctx, model, node, entryName, ce.getExpression() );
                    ctxEval.addEntry(
                            entryName,
                            entryType,
                            evaluator,
                            ce );

                    // add context entry to the list of available variables for the following entries
                    ctx.setVariable( entryName, entryType );
                } else {
                    // if the variable is not defined, then it should be the last
                    // entry in the context and the result of this context evaluation is the
                    // result of this expression itself

                    // TODO: if it is not the last entry, raise error message
                    DMNType type = null;
                    if ( ctxDef.getParent() instanceof ContextEntry && ((ContextEntry)ctxDef.getParent()).getVariable() != null ) {
                        ContextEntry parentEntry = (ContextEntry) ctxDef.getParent();
                        type = compiler.resolveTypeRef( model, node, parentEntry.getVariable(), parentEntry.getVariable(), parentEntry.getVariable().getTypeRef() );
                    } else if ( node instanceof BusinessKnowledgeModelNode ) {
                        type = ((BusinessKnowledgeModelNode) node).getResultType();
                    } else if ( node instanceof DecisionNode ) {
                        type = ((DecisionNode) node).getResultType();
                    }
                    ctxEval.addEntry(
                            DMNContextEvaluator.RESULT_ENTRY,
                            type,
                            compileExpression( ctx, model, node, contextName, ce.getExpression() ),
                            ce );
                }
            }
        } finally {
            ctx.exitFrame();
        }
        return ctxEval;
    }

    private DMNExpressionEvaluator compileFunctionDefinition(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String functionName, FunctionDefinition expression) {
        FunctionDefinition funcDef = expression;

        String kindStr = funcDef.getOtherAttributes().get( FunctionDefinition.KIND_QNAME );
        FunctionDefinition.Kind kind = kindStr != null ? FunctionDefinition.Kind.determineFromString( kindStr ) :FunctionDefinition.Kind.FEEL;

        if( kind == null ) {
            // unknown function kind
            MsgUtil.reportMessage( logger,
                                   DMNMessage.Severity.ERROR,
                                   funcDef,
                                   model,
                                   null,
                                   null,
                                   Msg.FUNC_DEF_INVALID_KIND,
                                   kindStr,
                                   node.getIdentifierString() );
            return new DMNFunctionDefinitionEvaluator( node.getName(), funcDef );
        } else if( kind.equals( FunctionDefinition.Kind.FEEL ) ) {
            ctx.enterFrame();
            try {
                DMNFunctionDefinitionEvaluator func = new DMNFunctionDefinitionEvaluator( node.getName(), funcDef );
                for ( InformationItem p : funcDef.getFormalParameter() ) {
                    DMNCompilerHelper.checkVariableName( model, p, p.getName() );
                    DMNType dmnType = compiler.resolveTypeRef( model, node, p, p, p.getTypeRef() );
                    func.addParameter( p.getName(), dmnType );
                    ctx.setVariable( p.getName(), dmnType );
                }

                DMNExpressionEvaluator eval = compileExpression( ctx, model, node, functionName, funcDef.getExpression() );
                if( eval instanceof DMNLiteralExpressionEvaluator && ((DMNLiteralExpressionEvaluator)eval).isFunctionDefinition() ) {
                    // we need to resolve the function and eliminate the indirection
                    CompiledExpression fexpr = ((DMNLiteralExpressionEvaluator) eval).getExpression();
                    FEELFunction feelFunction = feel.evaluateFunctionDef( ctx, fexpr, model, funcDef,
                                                                          Msg.FUNC_DEF_COMPILATION_ERR,
                                                                          functionName,
                                                                          node.getIdentifierString() );
                    DMNInvocationEvaluator invoker = new DMNInvocationEvaluator( node.getName(), node.getSource(), functionName, new Invocation(),
                                                                                 ( fctx, fname ) -> feelFunction );

                    for( InformationItem p : funcDef.getFormalParameter() ) {
                        invoker.addParameter( p.getName(), func.getParameterType( p.getName() ), (em, dr) -> new EvaluatorResultImpl( dr.getContext().get( p.getName() ), EvaluatorResult.ResultType.SUCCESS ) );
                    }
                    eval = invoker;
                }

                func.setEvaluator( eval );
                return func;
            } finally {
                ctx.exitFrame();
            }
        } else if( kind.equals( FunctionDefinition.Kind.JAVA ) ) {
            if( funcDef.getExpression() instanceof Context ) {
                // proceed
                Context context = (Context) funcDef.getExpression();
                String clazz = null;
                String method = null;
                for( ContextEntry ce : context.getContextEntry() ) {
                    if( ce.getVariable() != null && ce.getVariable().getName() != null && ce.getExpression() != null && ce.getExpression() instanceof LiteralExpression ) {
                        if( ce.getVariable().getName().equals( "class" )  ) {
                            clazz = stripQuotes( ((LiteralExpression) ce.getExpression()).getText().trim() );
                        } else if( ce.getVariable().getName().equals( "method signature" ) ) {
                            method = stripQuotes( ((LiteralExpression) ce.getExpression()).getText().trim() );
                        }
                    }
                }
                if( clazz != null && method != null ) {
                    String params = funcDef.getFormalParameter().stream().map( p -> p.getName() ).collect( Collectors.joining(",") );
                    String expr = String.format( "function(%s) external { java: { class: \"%s\", method signature: \"%s\" }}", params, clazz, method );

                    try {
                        FEELFunction feelFunction = feel.evaluateFunctionDef( ctx, expr, model, funcDef,
                                                                              Msg.FUNC_DEF_COMPILATION_ERR,
                                                                              functionName,
                                                                              node.getIdentifierString() );

                        DMNInvocationEvaluator invoker = new DMNInvocationEvaluator( node.getName(), node.getSource(), functionName, new Invocation(),
                                                                                     ( fctx, fname ) -> feelFunction );

                        DMNFunctionDefinitionEvaluator func = new DMNFunctionDefinitionEvaluator( node.getName(), funcDef );
                        for ( InformationItem p : funcDef.getFormalParameter() ) {
                            DMNCompilerHelper.checkVariableName( model, p, p.getName() );
                            DMNType dmnType = compiler.resolveTypeRef( model, node, p, p, p.getTypeRef() );
                            func.addParameter( p.getName(), dmnType );
                            invoker.addParameter( p.getName(), dmnType, (em, dr) -> new EvaluatorResultImpl( dr.getContext().get( p.getName() ), EvaluatorResult.ResultType.SUCCESS ) );
                        }
                        func.setEvaluator( invoker );
                        return func;
                    } catch ( Throwable e ) {
                        MsgUtil.reportMessage( logger,
                                               DMNMessage.Severity.ERROR,
                                               expression,
                                               model,
                                               e,
                                               null,
                                               Msg.FUNC_DEF_COMPILATION_ERR,
                                               functionName,
                                               node.getIdentifierString(),
                                               "Exception raised: "+e.getClass().getSimpleName());
                    }
                } else {
                    MsgUtil.reportMessage( logger,
                                           DMNMessage.Severity.ERROR,
                                           expression,
                                           model,
                                           null,
                                           null,
                                           Msg.FUNC_DEF_MISSING_ENTRY,
                                           functionName,
                                           node.getIdentifierString());
                }
            } else {
                // error, java function definitions require a context
                MsgUtil.reportMessage( logger,
                                       DMNMessage.Severity.ERROR,
                                       funcDef,
                                       model,
                                       null,
                                       null,
                                       Msg.FUNC_DEF_BODY_NOT_CONTEXT,
                                       node.getIdentifierString() );
            }
        } else if( kind.equals( FunctionDefinition.Kind.PMML ) ) {
            MsgUtil.reportMessage( logger,
                                   DMNMessage.Severity.WARN,
                                   funcDef,
                                   model,
                                   null,
                                   null,
                                   Msg.FUNC_DEF_PMML_NOT_SUPPORTED,
                                   node.getIdentifierString() );
        } else {
            MsgUtil.reportMessage( logger,
                                   DMNMessage.Severity.ERROR,
                                   funcDef,
                                   model,
                                   null,
                                   null,
                                   Msg.FUNC_DEF_INVALID_KIND,
                                   kindStr,
                                   node.getIdentifierString() );
        }
        return new DMNFunctionDefinitionEvaluator( node.getName(), funcDef );
    }

    private String stripQuotes(String trim) {
        return trim.startsWith( "\"" ) && trim.endsWith( "\"" ) && trim.length() >= 2 ? trim.substring( 1, trim.length()-1 ) : trim;
    }

    private String resolveNamespaceForTypeRef(QName typeRef, DMNElement fromElement) {
        if ( typeRef.getNamespaceURI() == null || typeRef.getNamespaceURI().isEmpty() ) {
            // TODO this could have actually been populated during unmarshalling, but requires throughout customization of the Stax reader.
            String namespaceURI = fromElement.getNamespaceURI(typeRef.getPrefix());
            return namespaceURI;
        } else {
            return typeRef.getNamespaceURI();
        }
    }

    private DMNExpressionEvaluator compileDecisionTable(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String dtName, DecisionTable dt) {
        java.util.List<DTInputClause> inputs = new ArrayList<>();
        int index = 0;
        for ( InputClause ic : dt.getInput() ) {
            index++;
            String inputExpressionText = ic.getInputExpression().getText();
            String inputValuesText =  Optional.ofNullable( ic.getInputValues() ).map( UnaryTests::getText).orElse( null);
            java.util.List<UnaryTest> inputValues = null;
            if ( inputValuesText != null ) {
                inputValues = textToUnaryTestList( ctx,
                                                   inputValuesText,
                                                   model,
                                                   ic,
                                                   Msg.ERR_COMPILING_FEEL_EXPR_ON_DT_INPUT_CLAUSE_IDX,
                                                   inputValuesText,
                                                   node.getIdentifierString(),
                                                   index );
            } else if ( ic.getInputExpression().getTypeRef() != null ) {
                QName inputExpressionTypeRef = ic.getInputExpression().getTypeRef();
                BaseDMNTypeImpl typeRef = (BaseDMNTypeImpl) model.getTypeRegistry().resolveType(resolveNamespaceForTypeRef(inputExpressionTypeRef, ic.getInputExpression()), inputExpressionTypeRef.getLocalPart());
                inputValues = typeRef.getAllowedValuesFEEL();
            }
            CompiledExpression compiledInput = feel.compileFeelExpression(
                    ctx,
                    inputExpressionText,
                    model,
                    dt,
                    Msg.ERR_COMPILING_FEEL_EXPR_ON_DT_INPUT_CLAUSE_IDX,
                    inputExpressionText,
                    dtName,
                    index );
            inputs.add( new DTInputClause(inputExpressionText, inputValuesText, inputValues, compiledInput ) );
        }
        java.util.List<DTOutputClause> outputs = new ArrayList<>();
        index = 0;
        boolean hasOutputValues = false;
        for ( OutputClause oc : dt.getOutput() ) {
            String outputName = oc.getName();
            if( outputName != null ) {
                DMNCompilerHelper.checkVariableName( model, node.getSource(), outputName );
            }
            String id = oc.getId();
            String outputValuesText = Optional.ofNullable( oc.getOutputValues() ).map( UnaryTests::getText ).orElse( null );
            String defaultValue = oc.getDefaultOutputEntry() != null ? oc.getDefaultOutputEntry().getText() : null;
            BaseDMNTypeImpl typeRef = (BaseDMNTypeImpl) DMNTypeRegistry.UNKNOWN;
            java.util.List<UnaryTest> outputValues = null;
            if ( oc.getTypeRef() != null ) {
                QName outputExpressionTypeRef = oc.getTypeRef();
                typeRef = (BaseDMNTypeImpl) model.getTypeRegistry().resolveType( resolveNamespaceForTypeRef( outputExpressionTypeRef, oc ), outputExpressionTypeRef.getLocalPart() );
                if( typeRef == null ) {
                    typeRef = (BaseDMNTypeImpl) DMNTypeRegistry.UNKNOWN;
                }
            }

            if ( outputValuesText != null ) {
                outputValues = textToUnaryTestList( ctx,
                                                    outputValuesText,
                                                    model,
                                                    oc,
                                                    Msg.ERR_COMPILING_FEEL_EXPR_ON_DT_OUTPUT_CLAUSE_IDX,
                                                    outputValuesText,
                                                    node.getIdentifierString(),
                                                    ++index );
            } else if ( typeRef != DMNTypeRegistry.UNKNOWN ) {
                outputValues = typeRef.getAllowedValuesFEEL();
            } else if ( dt.getOutput().size() == 1
                        && ( dt.getParent() instanceof Decision || dt.getParent() instanceof BusinessKnowledgeModel || dt.getParent() instanceof ContextEntry ) ) {
                QName inferredTypeRef = recurseUpToInferTypeRef(model, oc, dt);
                // if inferredTypeRef is null, a std err will have been reported
                if ( inferredTypeRef != null ) {
                    typeRef = (BaseDMNTypeImpl) model.getTypeRegistry().resolveType(resolveNamespaceForTypeRef(inferredTypeRef, oc), inferredTypeRef.getLocalPart());
                    outputValues = typeRef.getAllowedValuesFEEL();
                }
            }
            if ( outputValues != null && !outputValues.isEmpty() ) {
                hasOutputValues = true;
            }
            outputs.add( new DTOutputClause( outputName, id, outputValues, defaultValue, typeRef.getFeelType() ) );
        }
        if ( dt.getHitPolicy().equals(HitPolicy.PRIORITY) && !hasOutputValues ) {
            MsgUtil.reportMessage( logger,
            DMNMessage.Severity.ERROR,
            dt.getParent(),
            model,
            null,
            null,
            Msg.MISSING_OUTPUT_VALUES,
            dt.getParent() );
        }
        java.util.List<DTDecisionRule> rules = new ArrayList<>();
        index = 0;
        for ( DecisionRule dr : dt.getRule() ) {
            DTDecisionRule rule = new DTDecisionRule( index );
            for( UnaryTests ut : dr.getInputEntry() ) {
                final java.util.List<UnaryTest> tests;
                if( ut == null || ut.getText() == null || ut.getText().isEmpty() ) {
                    tests = Collections.emptyList();
                    MsgUtil.reportMessage( logger,
                                           DMNMessage.Severity.ERROR,
                                           ut,
                                           model,
                                           null,
                                           null,
                                           Msg.DTABLE_EMPTY_ENTRY,
                                           dt.getRule().indexOf( dr ) + 1,
                                           dr.getInputEntry().indexOf( ut ) + 1,
                                           dt.getParentDRDElement().getIdentifierString() );
                } else {
                    tests = textToUnaryTestList( ctx,
                            ut.getText(),
                            model,
                            dr,
                            Msg.ERR_COMPILING_FEEL_EXPR_ON_DT_RULE_IDX,
                            ut.getText(),
                            node.getIdentifierString(),
                            index+1 );
                }
                rule.getInputEntry().add( (c, x) -> tests.stream().anyMatch( t -> {
                    Boolean result = t.apply( c, x );
                    return result != null && result == true;
                } ) );
            }
            for ( LiteralExpression le : dr.getOutputEntry() ) {
                String expressionText = le.getText();
                CompiledExpression compiledExpression = feel.compileFeelExpression(
                        ctx,
                        expressionText,
                        model,
                        dr,
                        Msg.ERR_COMPILING_FEEL_EXPR_ON_DT_RULE_IDX,
                        expressionText,
                        dtName,
                        index+1 );
                rule.getOutputEntry().add( compiledExpression );
            }
            rules.add( rule );
            index++;
        }
        String policy = dt.getHitPolicy().value() + (dt.getAggregation() != null ? " " + dt.getAggregation().value() : "");
        org.kie.dmn.feel.runtime.decisiontables.HitPolicy hp = org.kie.dmn.feel.runtime.decisiontables.HitPolicy.fromString( policy );
        java.util.List<String> parameterNames = new ArrayList<>();
        if ( node instanceof BusinessKnowledgeModelNode ) {
            // need to break this statement down and check for nulls
            parameterNames.addAll( ((BusinessKnowledgeModelNode) node).getBusinessKnowledModel().getEncapsulatedLogic().getFormalParameter().stream().map( f -> f.getName() ).collect( toList() ) );
        } else {
            parameterNames.addAll( node.getDependencies().keySet() );
        }

        DecisionTableImpl dti = new DecisionTableImpl( dtName, parameterNames, inputs, outputs, rules, hp );
        DTInvokerFunction dtf = new DTInvokerFunction( dti );
        DMNDTExpressionEvaluator dtee = new DMNDTExpressionEvaluator( node, dtf );
        return dtee;
    }
    
    /**
     * Utility method for DecisionTable with only 1 output, to infer typeRef from parent
     * @param model used for reporting errors
     * @param originalElement the original OutputClause[0] single output for which the DecisionTable parameter recursionIdx is being processed for inferring the typeRef
     * @param recursionIdx start of the recursion is the DecisionTable model node itself
     * @return the inferred `typeRef` or null in case of errors. Errors are reported with standard notification mechanism via MsgUtil.reportMessage
     */
    private QName recurseUpToInferTypeRef(DMNModelImpl model, OutputClause originalElement, DMNElement recursionIdx) {
        if ( recursionIdx.getParent() instanceof Decision ) {
            InformationItem parentVariable = ((Decision) recursionIdx.getParent()).getVariable();
            if ( parentVariable != null ) {
                return variableTypeRefOrErrIfNull(model, parentVariable);
            } else {
                return null; // simply to avoid NPE, the proper error is already managed in compilation
            }
        } else if ( recursionIdx.getParent() instanceof BusinessKnowledgeModel ) {
            InformationItem parentVariable = ((BusinessKnowledgeModel) recursionIdx.getParent()).getVariable();
            if ( parentVariable != null ) {
                return variableTypeRefOrErrIfNull(model, parentVariable);
            } else {
                return null; // simply to avoid NPE, the proper error is already managed in compilation
            }
        } else if ( recursionIdx.getParent() instanceof ContextEntry ) {
            ContextEntry parentCtxEntry = (ContextEntry) recursionIdx.getParent();
            if ( parentCtxEntry.getVariable() != null ) {
                return variableTypeRefOrErrIfNull(model, parentCtxEntry.getVariable());
            } else {
                Context parentCtx = (Context) parentCtxEntry.getParent();
                if ( parentCtx.getContextEntry().get(parentCtx.getContextEntry().size()-1).equals(parentCtxEntry) ) {
                    // the ContextEntry is the last one in the Context, so I can recurse up-ward in the DMN model tree
                    // please notice the recursion would be considering the parentCtxEntry's parent, which is the `parentCtx` so is effectively a 2x jump upward in the model tree 
                    return recurseUpToInferTypeRef(model, originalElement, parentCtx);
                } else {
                    // error not last ContextEntry in context
                    MsgUtil.reportMessage( logger,
                            DMNMessage.Severity.ERROR,
                            parentCtxEntry,
                            model,
                            null,
                            null,
                            Msg.MISSING_VARIABLE_ON_CONTEXT,
                            parentCtxEntry );
                    return null;
                }
            }
        } else {
            // this is only for safety in case the recursion is escaping the allowed path for a broken model.
            MsgUtil.reportMessage( logger,
                    DMNMessage.Severity.ERROR,
                    originalElement,
                    model,
                    null,
                    null,
                    Msg.UNKNOWN_OUTPUT_TYPE_FOR_DT_ON_NODE,
                    originalElement.getParentDRDElement().getIdentifierString() );
            return null;
        }
    }
    
    /**
     * Utility method to have a error message is reported if a DMN Variable is missing typeRef. 
     * @param model used for reporting errors 
     * @param variable the variable to extract typeRef
     * @return the `variable.typeRef` or null in case of errors. Errors are reported with standard notification mechanism via MsgUtil.reportMessage
     */
    private QName variableTypeRefOrErrIfNull(DMNModelImpl model, InformationItem variable) {
        if ( variable.getTypeRef() != null ) {
            return variable.getTypeRef();
        } else {
            MsgUtil.reportMessage( logger,
                    DMNMessage.Severity.ERROR,
                    variable,
                    model,
                    null,
                    null,
                    Msg.MISSING_TYPEREF_FOR_VARIABLE,
                    variable.getName(),
                    variable.getParentDRDElement().getIdentifierString() );
            return null;
        }
    }

    private DMNExpressionEvaluator compileLiteralExpression(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String exprName, LiteralExpression expression) {
        DMNLiteralExpressionEvaluator evaluator = null;
        if ( expression.getExpressionLanguage() == null || expression.getExpressionLanguage().equals( DMNModelInstrumentedBase.URI_FEEL ) ) {
            String exprText = expression.getText();
            if( exprText != null ) {
                try {
                    CompiledExpression compiledExpression = feel.compileFeelExpression( ctx,
                                                                                        exprText,
                                                                                        model,
                                                                                        expression,
                                                                                        Msg.ERR_COMPILING_FEEL_EXPR_FOR_NAME_ON_NODE,
                                                                                        exprText,
                                                                                        exprName,
                                                                                        node.getIdentifierString() );
                    evaluator = new DMNLiteralExpressionEvaluator( compiledExpression );
                } catch ( Throwable e ) {
                    MsgUtil.reportMessage( logger,
                                           DMNMessage.Severity.ERROR,
                                           expression,
                                           model,
                                           e,
                                           null,
                                           Msg.ERR_COMPILING_FEEL_EXPR_FOR_NAME_ON_NODE,
                                           exprText,
                                           exprName,
                                           node.getIdentifierString(),
                                           "Exception raised: "+e.getClass().getSimpleName());
                }
            } else {
                MsgUtil.reportMessage( logger,
                                       DMNMessage.Severity.ERROR,
                                       expression,
                                       model,
                                       null,
                                       null,
                                       Msg.MISSING_EXPRESSION_FOR_NAME,
                                       exprName,
                                       node.getIdentifierString() );
            }
        }
        return evaluator;
    }

    private List<UnaryTest> textToUnaryTestList(DMNCompilerContext ctx, String text, DMNModelImpl model, DMNElement element, Msg.Message errorMsg, Object... msgParams ) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        return feel.evaluateUnaryTests( ctx, text, model, element, errorMsg, msgParams );
    }

}
