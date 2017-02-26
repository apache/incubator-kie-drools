package org.kie.dmn.core.compiler;

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.core.ast.*;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.model.v1_1.*;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.decisiontables.*;
import org.kie.dmn.feel.runtime.functions.DTInvokerFunction;

import java.util.*;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class DMNEvaluatorCompiler {

    private final DMNFEELHelper feel;
    private DMNCompilerImpl compiler;

    public DMNEvaluatorCompiler( DMNCompilerImpl compiler, DMNFEELHelper feel ) {
        this.compiler = compiler;
        this.feel = feel;
    }

    public DMNExpressionEvaluator compileExpression(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String exprName, Expression expression) {
        if ( expression instanceof LiteralExpression ) {
            return compileLiteralExpression( ctx, model, node, exprName, (LiteralExpression) expression );
        } else if ( expression instanceof DecisionTable ) {
            return compileDecisionTable( ctx, model, node, exprName, (DecisionTable) expression );
        } else if ( expression instanceof FunctionDefinition ) {
            return compileFunctionDefinition( ctx, model, node, exprName, (FunctionDefinition) expression );
        } else if ( expression instanceof Context ) {
            return compileContext( ctx, model, node, exprName, (Context) expression );
        } else if ( expression instanceof org.kie.dmn.feel.model.v1_1.List ) {
            return compileList( ctx, model, node, exprName, (org.kie.dmn.feel.model.v1_1.List) expression );
        } else if ( expression instanceof Relation ) {
            return compileRelation( ctx, model, node, exprName, (Relation) expression );
        } else if ( expression instanceof Invocation ) {
            return compileInvocation( ctx, model, node, (Invocation) expression );
        } else {
            if ( expression != null ) {
                model.addMessage( DMNMessage.Severity.ERROR, "Expression type '" + expression.getClass().getSimpleName() + "' not supported in node '" + node.getId() + "'", node.getId() );
            } else {
                model.addMessage( DMNMessage.Severity.ERROR, "No expression defined for node '" + node.getId() + "'", node.getId() );
            }
        }
        return null;
    }

    private DMNExpressionEvaluator compileInvocation(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, Invocation expression) {
        Invocation invocation = expression;
        // expression must be a literal text with the name of the function
        String functionName = ((LiteralExpression) invocation.getExpression()).getText();
        DMNInvocationEvaluator invEval = new DMNInvocationEvaluator( node.getName(), node.getId(), functionName, invocation );
        for ( Binding binding : invocation.getBinding() ) {
            invEval.addParameter(
                    binding.getParameter().getName(),
                    compiler.resolveTypeRef( model, node, binding.getParameter(), binding.getParameter(), binding.getParameter().getTypeRef() ),
                    compileExpression( ctx, model, node, binding.getParameter().getName(), binding.getExpression() ) );
        }
        return invEval;
    }

    private DMNExpressionEvaluator compileRelation(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String relationName, Relation expression) {
        Relation relationDef = expression;
        DMNRelationEvaluator relationEval = new DMNRelationEvaluator( node.getName(), node.getId(), relationDef );
        for ( InformationItem col : relationDef.getColumn() ) {
            relationEval.addColumn( col.getName() );
        }
        for ( org.kie.dmn.feel.model.v1_1.List row : relationDef.getRow() ) {
            java.util.List<DMNExpressionEvaluator> values = new ArrayList<>();
            for ( Expression expr : row.getExpression() ) {
                values.add( compileExpression( ctx, model, node, relationName, expr ) );
            }
            relationEval.addRow( values );
        }
        return relationEval;
    }

    private DMNExpressionEvaluator compileList(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String listName, org.kie.dmn.feel.model.v1_1.List expression) {
        org.kie.dmn.feel.model.v1_1.List listDef = expression;
        DMNListEvaluator listEval = new DMNListEvaluator( node.getName(), node.getId(), listDef );
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
                    DMNType entryType = compiler.resolveTypeRef( model, node, ce.getVariable(), ce.getVariable(), ce.getVariable().getTypeRef() );
                    DMNExpressionEvaluator evaluator = compileExpression( ctx, model, node, entryName, ce.getExpression() );
                    ctxEval.addEntry(
                            entryName,
                            entryType,
                            evaluator );

                    // add context entry to the list of available variables for the following entries
                    ctx.setVariable( entryName, entryType );
                } else {
                    // if the variable is not defined, then it should be the last
                    // entry in the context and the result of this context evaluation is the
                    // result of this expression itself

                    // TODO: if it is not the last entry, raise error message
                    DMNType type = null;
                    if ( node instanceof BusinessKnowledgeModelNode ) {
                        type = ((BusinessKnowledgeModelNode) node).getResultType();
                    } else if ( node instanceof DecisionNode ) {
                        type = ((DecisionNode) node).getResultType();
                    }
                    ctxEval.addEntry(
                            DMNContextEvaluator.RESULT_ENTRY,
                            type,
                            compileExpression( ctx, model, node, contextName, ce.getExpression() ) );
                }
            }
        } finally {
            ctx.exitFrame();
        }
        return ctxEval;
    }

    private DMNExpressionEvaluator compileFunctionDefinition(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String functionName, FunctionDefinition expression) {
        FunctionDefinition funcDef = expression;

        ctx.enterFrame();
        try {
            DMNExpressionEvaluatorInvokerFunction func = new DMNExpressionEvaluatorInvokerFunction( node.getName(), funcDef );
            for ( InformationItem p : funcDef.getFormalParameter() ) {
                DMNType dmnType = compiler.resolveTypeRef( model, node, p, p, p.getTypeRef() );
                func.addParameter( p.getName(), dmnType );
                ctx.setVariable( p.getName(), dmnType );
            }
            DMNExpressionEvaluator eval = compileExpression( ctx, model, node, functionName, funcDef.getExpression() );
            func.setEvaluator( eval );
            return func;
        } finally {
            ctx.exitFrame();
        }
    }

    private DMNExpressionEvaluator compileDecisionTable(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String dtName, DecisionTable dt) {
        java.util.List<DTInputClause> inputs = new ArrayList<>();
        int index = 0;
        for ( InputClause ic : dt.getInput() ) {
            String inputExpressionText = ic.getInputExpression().getText();
            String inputValuesText =  Optional.ofNullable( ic.getInputValues() ).map( UnaryTests::getText).orElse( null);
            inputs.add( new DTInputClause(inputExpressionText, inputValuesText,
                                          textToUnaryTestList( ctx,
                                                               inputValuesText,
                                                               model,
                                                               ic,
                                                               createErrorMsg( node, node.getName(), ic, ++index, inputValuesText ) ) ) );
        }
        java.util.List<DTOutputClause> outputs = new ArrayList<>();
        index = 0;
        for ( OutputClause oc : dt.getOutput() ) {
            String outputName = oc.getName();
            String id = oc.getId();
            String outputValuesText = Optional.ofNullable( oc.getOutputValues() ).map( UnaryTests::getText ).orElse( null );
            String defaultValue = oc.getDefaultOutputEntry() != null ? oc.getDefaultOutputEntry().getText() : null;
            java.util.List<UnaryTest> outputValues = outputValuesText == null ? null : textToUnaryTestList( ctx,
                                                                                                            outputValuesText,
                                                                                                            model,
                                                                                                            oc,
                                                                                                            createErrorMsg( node, node.getName(), oc, ++index, outputValuesText ) );

            outputs.add( new DTOutputClause( outputName, id, outputValues, defaultValue ) );
        }
        java.util.List<DTDecisionRule> rules = new ArrayList<>();
        index = 0;
        for ( DecisionRule dr : dt.getRule() ) {
            DTDecisionRule rule = new DTDecisionRule( index );
            for( UnaryTests ut : dr.getInputEntry() ) {
                java.util.List<UnaryTest> tests = textToUnaryTestList( ctx,
                                                                       ut.getText(),
                                                                       model,
                                                                       dr,
                                                                       createErrorMsg( node, node.getName(), dr, index+1, ut.getText() ) );
                rule.getInputEntry().add( (c, x) -> tests.stream().anyMatch( t -> t.apply( c, x ) ) );
            }
            for ( LiteralExpression le : dr.getOutputEntry() ) {
                // we might want to compile and save the compiled expression here
                rule.getOutputEntry().add( le.getText() );
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

    private DMNExpressionEvaluator compileLiteralExpression(DMNCompilerContext ctx, DMNModelImpl model, DMNBaseNode node, String exprName, LiteralExpression expression) {
        DMNLiteralExpressionEvaluator evaluator = null;
        if ( expression.getExpressionLanguage() == null || expression.getExpressionLanguage().equals( DMNModelInstrumentedBase.URI_FEEL ) ) {
            String exprText = expression.getText();
            CompiledExpression compiledExpression = feel.compileFeelExpression( ctx, exprText, model, expression,
                                                                                createErrorMsg( node, exprName, expression, 0, exprText ) );
            evaluator = new DMNLiteralExpressionEvaluator( compiledExpression );
        }
        return evaluator;
    }

    private List<UnaryTest> textToUnaryTestList(DMNCompilerContext ctx, String text, DMNModelImpl model, DMNElement element, String errorMsg ) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        return feel.evaluateUnaryTests( ctx, text, model, element, errorMsg );
    }

    public String createErrorMsg(DMNNode node, String elementName, DMNElement element, int index, String expression) {
        String errorMsg;
        if ( element instanceof InputClause ) {
            errorMsg = "Error compiling FEEL expression '" + expression + "' on decision table '" + elementName + "', input clause #" + index;
        } else if ( element instanceof OutputClause ) {
            errorMsg = "Error compiling FEEL expression '" + expression + "' on decision table '" + elementName + "', output clause #" + index;
        } else if ( element instanceof ItemDefinition ) {
            errorMsg = "Error compiling allowed values list '" + expression + "' on item definition '" + elementName + "'";
        } else if ( element instanceof DecisionRule ) {
            errorMsg = "Error compiling FEEL expression '" + expression + "' on decision table '" + elementName + "', rule #" + index;
        } else if ( element instanceof LiteralExpression ) {
            errorMsg = "Error compiling FEEL expression '" + expression + "' for name '" + elementName +"' on node '"+node.getName()+"'";
        } else {
            errorMsg = "Error compiling FEEL expression '" + expression + "' on decision table '" + elementName + "'";
        }
        return errorMsg;
    }



}
