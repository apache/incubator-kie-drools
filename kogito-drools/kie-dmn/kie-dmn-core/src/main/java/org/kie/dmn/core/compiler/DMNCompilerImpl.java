/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.compiler;

import org.kie.api.io.Resource;
import org.kie.dmn.backend.marshalling.v1_1.DMNMarshallerFactory;
import org.kie.dmn.core.api.DMNCompiler;
import org.kie.dmn.core.api.DMNMessage;
import org.kie.dmn.core.api.DMNModel;
import org.kie.dmn.core.api.DMNType;
import org.kie.dmn.core.ast.*;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.impl.FeelTypeImpl;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.model.v1_1.*;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.decisiontables.*;
import org.kie.dmn.feel.runtime.decisiontables.HitPolicy;
import org.kie.dmn.feel.runtime.functions.DTInvokerFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

public class DMNCompilerImpl implements DMNCompiler {

    private static final Logger logger = LoggerFactory.getLogger( DMNCompilerImpl.class );

    @Override
    public DMNModel compile(Resource resource) {
        try {
            return compile( resource.getReader() );
        } catch ( IOException e ) {
            logger.error( "Error retrieving reader for resource: "+resource.getSourcePath(), e );
        }
        return null;
    }

    @Override
    public DMNModel compile(Reader source) {
        try {
            Definitions dmndefs = DMNMarshallerFactory.newDefaultMarshaller().unmarshal( source );
            if ( dmndefs != null ) {
                DMNModelImpl model = new DMNModelImpl( dmndefs );

                processItemDefinitions( model, dmndefs );
                processDrgElements( model, dmndefs );
                return model;
            }
        } catch ( Exception e ) {
            logger.error( "Error compiling model from source.", e );
        }
        return null;
    }

    private void processItemDefinitions(DMNModelImpl model, Definitions dmndefs) {
        for( ItemDefinition id : dmndefs.getItemDefinition() ) {
            ItemDefNode idn = new ItemDefNode( id );
            DMNType type = buildTypeDef( model, idn, id );
            idn.setType( type );
            model.addItemDefinition( idn );
        }
    }

    private void processDrgElements(DMNModelImpl model, Definitions dmndefs) {
        for ( DRGElement e : dmndefs.getDrgElement() ) {
            if ( e instanceof InputData ) {
                InputData input = (InputData) e;
                String variableName = input.getVariable() != null ? input.getVariable().getName() : null;
                if( ! variableNameIsValid( variableName ) ) {
                    logger.error( "Invalid variable name '"+variableName+"' in input data '"+input.getId()+"'" );
                    model.addMessage( DMNMessage.Severity.ERROR, "Invalid variable name '"+variableName+"' in input data '"+input.getId()+"'", input.getId() );
                }
                InputDataNode idn = new InputDataNode( input );
                DMNType type = resolveSimpleTypeRef( model, idn, e, input.getVariable().getTypeRef() );
                idn.setDmnType( type );
                model.addInput( idn );
                model.getTypeRegistry().put( input.getVariable().getTypeRef(), type );
            } else if ( e instanceof Decision ) {
                Decision decision = (Decision) e;
                DecisionNode dn = new DecisionNode( decision );
                DMNType type = null;
                if( decision.getVariable() != null && decision.getVariable().getTypeRef() != null ) {
                    type = resolveSimpleTypeRef( model, dn, decision, decision.getVariable().getTypeRef() );
                } else {
                    type = resolveSimpleTypeRef( model, dn, decision, null );
                }
                dn.setResultType( type );
                model.addDecision( dn );
            } else if ( e instanceof BusinessKnowledgeModel ) {
                BusinessKnowledgeModel bkm = (BusinessKnowledgeModel) e;
                BusinessKnowledgeModelNode bkmn = new BusinessKnowledgeModelNode( bkm );
                DMNType type = null;
                if( bkm.getVariable() != null && bkm.getVariable().getTypeRef() != null ) {
                    type = resolveSimpleTypeRef( model, bkmn, bkm, bkm.getVariable().getTypeRef() );
                } else {
                    // TODO: need to handle cases where the variable is not defined or does not have a type;
                    // for now the call bellow will return type UNKNOWN
                    type = resolveSimpleTypeRef( model, bkmn, bkm, null );
                }
                bkmn.setResultType( type );
                model.addBusinessKnowledgeModel( bkmn );
            } else if ( e instanceof KnowledgeSource ) {
                // don't do anything as KnowledgeSource is a documentation element
                // without runtime semantics
            } else {
                model.addMessage( DMNMessage.Severity.ERROR, "Element "+e.getClass().getSimpleName()+" with id='"+e.getId()+"' not supported.", e.getId() );
            }
        }

        for ( BusinessKnowledgeModelNode bkm : model.getBusinessKnowledgeModels() ) {
            linkRequirements( model, bkm );
            FunctionDefinition funcDef = bkm.getBusinessKnowledModel().getEncapsulatedLogic();
            DMNExpressionEvaluator exprEvaluator = compileExpression( model, bkm, funcDef );
            bkm.setEvaluator( exprEvaluator );
        }
        for ( DecisionNode d : model.getDecisions() ) {
            linkRequirements( model, d );
            DMNExpressionEvaluator evaluator = compileExpression( model, d, d.getDecision().getExpression() );
            d.setEvaluator( evaluator );
        }
    }

    private boolean variableNameIsValid(String variableName) {
        return FEELParser.isVariableNameValid( variableName );
    }

    private void linkRequirements(DMNModelImpl model, DMNBaseNode node ) {
        for ( InformationRequirement ir : node.getInformationRequirement() ) {
            if ( ir.getRequiredInput() != null ) {
                String id = getId( ir.getRequiredInput() );
                InputDataNode input = model.getInputById( id );
                if( input != null ) {
                    node.addDependency( input.getName(), input );
                } else {
                    String message = "Required input '"+id+"' not found for node '"+node.getName()+"'";
                    logger.error( message );
                    model.addMessage( DMNMessage.Severity.ERROR, message, node.getId() );
                }
            } else if ( ir.getRequiredDecision() != null ) {
                String id = getId( ir.getRequiredDecision() );
                DecisionNode dn = model.getDecisionById( id );
                if( dn != null ) {
                    node.addDependency( dn.getName(), dn );
                } else {
                    String message = "Required decision '"+id+"' not found for node '"+node.getName()+"'";
                    logger.error( message );
                    model.addMessage( DMNMessage.Severity.ERROR, message, node.getId() );
                }
            }
        }
        for ( KnowledgeRequirement kr : node.getKnowledgeRequirement() ) {
            if ( kr.getRequiredKnowledge() != null ) {
                String id = getId( kr.getRequiredKnowledge() );
                BusinessKnowledgeModelNode bkmn = model.getBusinessKnowledgeModelById( id );
                if( bkmn != null ) {
                    node.addDependency( bkmn.getName(), bkmn );
                } else {
                    String message = "Required Business Knowledge Model '"+id+"' not found for node '"+node.getName()+"'";
                    logger.error( message );
                    model.addMessage( DMNMessage.Severity.ERROR, message, node.getId() );
                }
            }
        }
    }

    private String getId(DMNElementReference er) {
        String href = er.getHref();
        return href.contains( "#" ) ? href.substring( href.indexOf( '#' ) + 1 ) : href;
    }

    private DMNType buildTypeDef( DMNModelImpl dmnModel, DMNNode node, ItemDefinition itemDef ) {
        DMNType type = null;
        if( itemDef.getTypeRef() != null ) {
            // this is an "simple" type, so find the namespace
            type = resolveSimpleTypeRef( dmnModel, node, itemDef, itemDef.getTypeRef() );
            UnaryTests allowedValuesStr = itemDef.getAllowedValues();
            if( allowedValuesStr != null ) {
                Object av = FEEL.newInstance().evaluate( "[" + allowedValuesStr.getText() + "]" );
                java.util.List<?> allowedValues = av instanceof java.util.List ? (java.util.List) av : Collections.singletonList( av );
                ((FeelTypeImpl)type).setAllowedValues( allowedValues );
            }
            if( itemDef.isIsCollection() ) {
                ((FeelTypeImpl)type).setCollection( itemDef.isIsCollection() );
            }
        } else {
            // this is a composite type
            CompositeTypeImpl compType = new CompositeTypeImpl( itemDef.getName(), itemDef.getId(), itemDef.isIsCollection() );
            for( ItemDefinition fieldDef : itemDef.getItemComponent() ) {
                DMNType field = buildTypeDef( dmnModel, node, fieldDef );
                compType.getFields().put( field.getName(), field );
            }
            type = compType;
        }
        return type;
    }

    private DMNType resolveSimpleTypeRef(DMNModelImpl dmnModel, DMNNode node, NamedElement model, QName typeRef) {
        if( typeRef != null ) {
            String prefix = typeRef.getPrefix();
            String namespace = model.getNamespaceURI( prefix );
            if( namespace != null && DMNModelInstrumentedBase.URI_FEEL.equals( namespace ) ) {
                Type feelType = BuiltInType.determineTypeFromName( typeRef.getLocalPart() );
                if( feelType == BuiltInType.CONTEXT || feelType == BuiltInType.UNKNOWN ) {
                    if( model instanceof Decision && ((Decision)model).getExpression() instanceof DecisionTable ) {
                        DecisionTable dt = (DecisionTable) ((Decision)model).getExpression();
                        if( dt.getOutput().size() > 1 ) {
                            CompositeTypeImpl compType = new CompositeTypeImpl( "__t"+model.getName(), model.getId() );
                            for( OutputClause oc : dt.getOutput() ) {
                                DMNType fieldType = resolveSimpleTypeRef( dmnModel, node, model, oc.getTypeRef() );
                                compType.getFields().put( oc.getName(), fieldType );
                            }
                            return compType;
                        } else if( dt.getOutput().size() == 1 ) {
                            return resolveSimpleTypeRef( dmnModel, node, model, dt.getOutput().get( 0 ).getTypeRef() );
                        }
                    }
                }
                return new FeelTypeImpl( model.getName(), model.getId(), feelType, false, null );
            } else if( dmnModel.getNamespace() != null && namespace != null && dmnModel.getNamespace().equals( namespace ) ) {
                // locally defined type
                List<ItemDefNode> itemDefs = dmnModel.getItemDefinitions().stream()
                        .filter( id -> id.getName() != null && id.getName().equals( typeRef.getLocalPart() ) )
                        .collect( toList() );
                if( itemDefs.size() == 1 ) {
                    return itemDefs.get( 0 ).getType();
                } else if( itemDefs.isEmpty() ) {
                    logger.error( "No '"+typeRef.toString()+"' type definition found for element '"+ model.getName()+"' on node '"+node.getName()+"'");
                } else {
                    logger.error( "Multiple types found for type reference '"+typeRef.toString()+"' on element '"+ model.getName()+"' on node '"+node.getName()+"'");
                }
            } else {
                logger.error( "Unknown namespace for type reference prefix '"+prefix+"' on element '"+ model.getName()+"' on node '"+node.getName()+"'" );
            }
            return null;
        }
        return new FeelTypeImpl( model.getName(), model.getId(), BuiltInType.UNKNOWN, false, null );
    }

    private DMNExpressionEvaluator compileExpression(DMNModelImpl model, DMNBaseNode node, Expression expression) {
        FEEL feel = FEEL.newInstance();
        if( expression instanceof LiteralExpression ) {
            CompilerContext ctx = feel.newCompilerContext();
            node.getDependencies().forEach( (name, depNode) -> {
                // TODO: need to properly resolve types here
                ctx.addInputVariableType( name, BuiltInType.UNKNOWN );
            } );
            CompiledExpression compiledExpression = feel.compile( ((LiteralExpression) expression).getText(), ctx );
            DMNLiteralExpressionEvaluator evaluator = new DMNLiteralExpressionEvaluator( compiledExpression );
            return evaluator;
        } else if( expression instanceof DecisionTable ) {
            DecisionTable dt = (DecisionTable) expression;
            List<DTInputClause> inputs = new ArrayList<>(  );
            for( InputClause ic : dt.getInput() ) {
                String inputExpressionText = ic.getInputExpression().getText();
                String inputValuesText =  Optional.ofNullable( ic.getInputValues() ).map(UnaryTests::getText).orElse(null);
                inputs.add( new DTInputClause(inputExpressionText, inputValuesText, textToUnaryTestList(inputValuesText) ) );
            }
            List<DTOutputClause> outputs = new ArrayList<>(  );
            for( OutputClause oc : dt.getOutput() ) {
                String outputName = oc.getName();
                String id = oc.getId();
                String outputValuesText =  Optional.ofNullable( oc.getOutputValues() ).map(UnaryTests::getText).orElse(null);
                outputs.add( new DTOutputClause(outputName, id, (List<String>) feel.evaluate("["+outputValuesText+"]") ) );         // TODO another hack to be revised
            }
            List<DTDecisionRule> rules = new ArrayList<>(  );
            int index = 0;
            for( DecisionRule dr : dt.getRule() ) {
                DTDecisionRule rule = new DTDecisionRule( index++ );
                for( UnaryTests ut : dr.getInputEntry() ) {
                    List<UnaryTest> tests = textToUnaryTestList( ut.getText() );
                    rule.getInputEntry().add( x -> tests.stream().anyMatch( t -> t.apply( x ) ) );
                }
                for( LiteralExpression le : dr.getOutputEntry() ) {
                    // we might want to compile and save the compiled expression here
                    rule.getOutputEntry().add( le.getText() );
                }
                rules.add( rule );
            }
            String policy = dt.getHitPolicy().value() + (dt.getAggregation() != null ? " " + dt.getAggregation().value() : "");
            HitPolicy hp = HitPolicy.fromString( policy );
            List<String> parameterNames = new ArrayList<>( );
            if( node instanceof BusinessKnowledgeModelNode ) {
                // need to break this statement down and check for nulls
                parameterNames.addAll( ((BusinessKnowledgeModelNode) node).getBusinessKnowledModel().getEncapsulatedLogic().getFormalParameter().stream().map( f -> f.getName() ).collect(toList()) );
            } else {
                parameterNames.addAll( node.getDependencies().keySet() );
            }

            DecisionTableImpl dti = new DecisionTableImpl( node.getName(), parameterNames, inputs, outputs, rules, hp );
            DTInvokerFunction dtf = new DTInvokerFunction( dti );
            DMNDTExpressionEvaluator dtee = new DMNDTExpressionEvaluator( node, dtf );
            return dtee;
        } else if( expression instanceof FunctionDefinition ) {
            FunctionDefinition funcDef = (FunctionDefinition) expression;
            DMNExpressionEvaluatorInvokerFunction func = new DMNExpressionEvaluatorInvokerFunction( node.getName(), funcDef );
            for( InformationItem p : funcDef.getFormalParameter() ) {
                func.addParameter( p.getName(), resolveSimpleTypeRef( model, node, p, p.getTypeRef() ) );
            }
            DMNExpressionEvaluator eval = compileExpression( model, node, funcDef.getExpression() );
            func.setEvaluator( eval );
            return func;
        } else if( expression instanceof Context ) {
            Context ctxDef = (Context) expression;
            DMNContextEvaluator ctxEval = new DMNContextEvaluator( node.getName(), ctxDef );
            for( ContextEntry ce : ctxDef.getContextEntry() ) {
                if( ce.getVariable() != null ) {
                    ctxEval.addEntry( ce.getVariable().getName(),
                                      resolveSimpleTypeRef( model, node, ce.getVariable(), ce.getVariable().getTypeRef() ),
                                      compileExpression( model, node, ce.getExpression() ) );
                } else {
                    // if the variable is not defined, then it should be the last
                    // entry in the context and the result of this context evaluation is the
                    // result of this expression itself
                    DMNType type = null;
                    if( node instanceof BusinessKnowledgeModelNode ) {
                        type = ((BusinessKnowledgeModelNode) node).getResultType();
                    } else if( node instanceof DecisionNode ) {
                        type = ((DecisionNode) node).getResultType();
                    }
                    ctxEval.addEntry( DMNContextEvaluator.RESULT_ENTRY,
                                      type,
                                      compileExpression( model, node, ce.getExpression() ) );
                }
            }
            return ctxEval;
        } else if( expression instanceof org.kie.dmn.feel.model.v1_1.List ) {
            org.kie.dmn.feel.model.v1_1.List listDef = (org.kie.dmn.feel.model.v1_1.List) expression;
            DMNListEvaluator listEval = new DMNListEvaluator( node.getName(), node.getId(), listDef );
            for( Expression expr : listDef.getExpression() ) {
                listEval.addElement( compileExpression( model, node, expr ) );
            }
            return listEval;
        } else if( expression instanceof Relation ) {
            Relation relationDef = (Relation) expression;
            DMNRelationEvaluator relationEval = new DMNRelationEvaluator( node.getName(), node.getId(), relationDef );
            for( InformationItem col : relationDef.getColumn() ) {
                relationEval.addColumn( col.getName() );
            }
            for( org.kie.dmn.feel.model.v1_1.List row : relationDef.getRow() ) {
                List<DMNExpressionEvaluator> values = new ArrayList<>(  );
                for( Expression expr : row.getExpression() ) {
                    values.add( compileExpression( model, node, expr ) );
                }
                relationEval.addRow( values );
            }
            return relationEval;
        } else if( expression instanceof Invocation ) {
            Invocation invocation = (Invocation) expression;
            // expression must be a literal text with the name of the function
            String functionName = ((LiteralExpression) invocation.getExpression()).getText();
            DMNInvocationEvaluator invEval = new DMNInvocationEvaluator( node.getName(), node.getId(), functionName, invocation );
            for( Binding binding : invocation.getBinding() ) {
                invEval.addParameter( binding.getParameter().getName(),
                                      resolveSimpleTypeRef( model, node, binding.getParameter(), binding.getParameter().getTypeRef() ),
                                      compileExpression( model, node, binding.getExpression() ) );
            }
            return invEval;
        } else {
            if( expression != null ) {
                model.addMessage( DMNMessage.Severity.ERROR, "Expression type '"+expression.getClass().getSimpleName()+"' not supported in node '"+node.getId()+"'", node.getId() );
            } else {
                model.addMessage( DMNMessage.Severity.ERROR, "No expression defined for node '"+node.getId()+"'", node.getId() );
            }
        }
        return null;
    }
    
    /**
     * TODO quick hack to parse values, in case they are a list
     * @param text
     * @return
     */
    protected static List<UnaryTest> textToUnaryTestList(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        List<Object> ie = (List<Object>) FEEL.newInstance().evaluate( "[ " + text + " ]" );
        List<UnaryTest> tests = new ArrayList<>(  );
        for( Object o : ie ) {
            if ( o instanceof UnaryTest ) {
                tests.add( (UnaryTest) o );
            } else if ( o instanceof Range ) {
                tests.add( x -> x != null && ((Range) o).includes( (Comparable<?>) x ) );
            } else {
                tests.add( x -> x != null && x.equals( o ) );
            }
        }
        return tests;
    }


}