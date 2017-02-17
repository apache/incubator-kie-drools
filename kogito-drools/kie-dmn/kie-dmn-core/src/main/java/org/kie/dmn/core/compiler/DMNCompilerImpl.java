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
import org.kie.dmn.api.core.DMNCompiler;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.core.api.DMNExpressionEvaluator;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.api.core.ast.ItemDefNode;
import org.kie.dmn.backend.marshalling.v1_1.DMNMarshallerFactory;
import org.kie.dmn.core.ast.*;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
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
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.decisiontables.*;
import org.kie.dmn.feel.runtime.decisiontables.HitPolicy;
import org.kie.dmn.feel.runtime.functions.DTInvokerFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.List;

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
            ItemDefNodeImpl idn = new ItemDefNodeImpl( id );
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
                InputDataNodeImpl idn = new InputDataNodeImpl( input );
                DMNType type = resolveTypeRef( model, idn, e, input.getVariable(), input.getVariable().getTypeRef() );
                idn.setDmnType( type );
                model.addInput( idn );
                model.getTypeRegistry().put( input.getVariable().getTypeRef(), type );
            } else if ( e instanceof Decision ) {
                Decision decision = (Decision) e;
                DecisionNodeImpl dn = new DecisionNodeImpl( decision );
                DMNType type = null;
                if( decision.getVariable() != null && decision.getVariable().getTypeRef() != null ) {
                    type = resolveTypeRef( model, dn, decision, decision.getVariable(), decision.getVariable().getTypeRef() );
                } else {
                    type = resolveTypeRef( model, dn, decision, decision, null );
                }
                dn.setResultType( type );
                model.addDecision( dn );
            } else if ( e instanceof BusinessKnowledgeModel ) {
                BusinessKnowledgeModel bkm = (BusinessKnowledgeModel) e;
                BusinessKnowledgeModelNodeImpl bkmn = new BusinessKnowledgeModelNodeImpl( bkm );
                DMNType type = null;
                if( bkm.getVariable() != null && bkm.getVariable().getTypeRef() != null ) {
                    type = resolveTypeRef( model, bkmn, bkm, bkm.getVariable(), bkm.getVariable().getTypeRef() );
                } else {
                    // TODO: need to handle cases where the variable is not defined or does not have a type;
                    // for now the call bellow will return type UNKNOWN
                    type = resolveTypeRef( model, bkmn, bkm, bkm,null );
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
            DMNExpressionEvaluator exprEvaluator = compileExpression( model, bkm, bkm.getName(), funcDef );
            ((BusinessKnowledgeModelNodeImpl) bkm).setEvaluator( exprEvaluator );
        }
        for ( DecisionNode d : model.getDecisions() ) {
            linkRequirements( model, d );
            DMNExpressionEvaluator evaluator = compileExpression( model, d, d.getName(), d.getDecision().getExpression() );
            ((DecisionNodeImpl) d).setEvaluator( evaluator );
        }
    }

    private boolean variableNameIsValid(String variableName) {
        return FEELParser.isVariableNameValid( variableName );
    }

    private void linkRequirements(DMNModelImpl model, DMNNode n ) {
        DMNBaseNode node = (DMNBaseNode) n;
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
        BaseDMNTypeImpl type = null;
        if( itemDef.getTypeRef() != null ) {
            // this is a reference to an existing type, so resolve the reference
            type = (BaseDMNTypeImpl) resolveTypeRef( dmnModel, node, itemDef, itemDef, itemDef.getTypeRef() );
            if( type != null ) {
                // we have to clone this type definition into a new one
                type = type.clone();

                UnaryTests allowedValuesStr = itemDef.getAllowedValues();
                if( allowedValuesStr != null ) {
                    Object av = FEEL.newInstance().evaluate( "[" + allowedValuesStr.getText() + "]" );
                    java.util.List<?> allowedValues = av instanceof java.util.List ? (java.util.List) av : Collections.singletonList( av );
                    type.setAllowedValues( allowedValues );
                }
                if( itemDef.isIsCollection() ) {
                    type.setCollection( itemDef.isIsCollection() );
                }
            } else {
                String message = "Unknown type reference '"+itemDef.getTypeRef()+"' on node '"+node.getName()+"'";
                logger.error( message );
                dmnModel.addMessage( DMNMessage.Severity.ERROR, message, node.getId() );
            }
        } else {
            // this is a composite type
            CompositeTypeImpl compType = new CompositeTypeImpl( itemDef.getName(), itemDef.getId(), itemDef.isIsCollection() );
            for( ItemDefinition fieldDef : itemDef.getItemComponent() ) {
                DMNType fieldType = buildTypeDef( dmnModel, node, fieldDef );
                compType.getFields().put( fieldDef.getName(), fieldType );
            }
            type = compType;
        }
        return type;
    }

    private DMNType resolveTypeRef(DMNModelImpl dmnModel, DMNNode node, NamedElement model, DMNModelInstrumentedBase localElement, QName typeRef) {
        if( typeRef != null ) {
            String prefix = typeRef.getPrefix();
            String namespace = localElement.getNamespaceURI( prefix );
            if( namespace != null && DMNModelInstrumentedBase.URI_FEEL.equals( namespace ) ) {
                Type feelType = BuiltInType.determineTypeFromName( typeRef.getLocalPart() );
                if( feelType == BuiltInType.CONTEXT || feelType == BuiltInType.UNKNOWN ) {
                    if( model instanceof Decision && ((Decision)model).getExpression() instanceof DecisionTable ) {
                        DecisionTable dt = (DecisionTable) ((Decision)model).getExpression();
                        if( dt.getOutput().size() > 1 ) {
                            CompositeTypeImpl compType = new CompositeTypeImpl( "__t"+model.getName(), model.getId() );
                            for( OutputClause oc : dt.getOutput() ) {
                                DMNType fieldType = resolveTypeRef( dmnModel, node, model, oc, oc.getTypeRef() );
                                compType.getFields().put( oc.getName(), fieldType );
                            }
                            return compType;
                        } else if( dt.getOutput().size() == 1 ) {
                            return resolveTypeRef( dmnModel, node, model, dt.getOutput().get( 0 ), dt.getOutput().get( 0 ).getTypeRef() );
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
                    if( model.getName() != null && node.getName() != null && model.getName().equals( node.getName() ) ) {
                        logger.error( "No '"+typeRef.toString()+"' type definition found for node '"+node.getName()+"'");
                    } else {
                        logger.error( "No '"+typeRef.toString()+"' type definition found for element '"+ model.getName()+"' on node '"+node.getName()+"'");
                    }
                } else {
                    if( model.getName() != null && node.getName() != null && model.getName().equals( node.getName() ) ) {
                        logger.error( "Multiple types found for type reference '" + typeRef.toString() + "' on node '" + node.getName() + "'" );
                    } else {
                        logger.error( "Multiple types found for type reference '" + typeRef.toString() + "' on element '" + model.getName() + "' on node '" + node.getName() + "'" );
                    }
                }
            } else {
                if( model.getName() != null && node.getName() != null && model.getName().equals( node.getName() ) ) {
                    logger.error( "Unknown namespace for type reference prefix '" + prefix + "' on node '" + node.getName() + "'" );
                } else {
                    logger.error( "Unknown namespace for type reference prefix '" + prefix + "' on element '" + model.getName() + "' on node '" + node.getName() + "'" );
                }
            }
            return null;
        }
        return new FeelTypeImpl( model.getName(), model.getId(), BuiltInType.UNKNOWN, false, null );
    }

    private DMNExpressionEvaluator compileExpression(DMNModelImpl model, DMNNode node, String exprName, Expression expression) {
        FEEL feel = FEEL.newInstance();
        if( expression instanceof LiteralExpression ) {
            return compileLiteralExpression( node, (LiteralExpression) expression, feel );
        } else if( expression instanceof DecisionTable ) {
            return compileDecisionTable( node, exprName, (DecisionTable) expression, feel );
        } else if( expression instanceof FunctionDefinition ) {
            return compileFunctionDefinition( model, node, exprName, (FunctionDefinition) expression );
        } else if( expression instanceof Context ) {
            return compileContext( model, node, exprName, (Context) expression );
        } else if( expression instanceof org.kie.dmn.feel.model.v1_1.List ) {
            return compileList( model, node, exprName, (org.kie.dmn.feel.model.v1_1.List) expression );
        } else if( expression instanceof Relation ) {
            return compileRelation( model, node, exprName, (Relation) expression );
        } else if( expression instanceof Invocation ) {
            return compileInvocation( model, node, (Invocation) expression );
        } else {
            if( expression != null ) {
                model.addMessage( DMNMessage.Severity.ERROR, "Expression type '"+expression.getClass().getSimpleName()+"' not supported in node '"+node.getId()+"'", node.getId() );
            } else {
                model.addMessage( DMNMessage.Severity.ERROR, "No expression defined for node '"+node.getId()+"'", node.getId() );
            }
        }
        return null;
    }

    private DMNExpressionEvaluator compileInvocation(DMNModelImpl model, DMNNode node, Invocation expression) {
        Invocation invocation = expression;
        // expression must be a literal text with the name of the function
        String functionName = ((LiteralExpression) invocation.getExpression()).getText();
        DMNInvocationEvaluator invEval = new DMNInvocationEvaluator( node.getName(), node.getId(), functionName, invocation );
        for( Binding binding : invocation.getBinding() ) {
            invEval.addParameter( binding.getParameter().getName(),
                                  resolveTypeRef( model, node, binding.getParameter(), binding.getParameter(), binding.getParameter().getTypeRef() ),
                                  compileExpression( model, node, binding.getParameter().getName(), binding.getExpression() ) );
        }
        return invEval;
    }

    private DMNExpressionEvaluator compileRelation(DMNModelImpl model, DMNNode node, String relationName, Relation expression) {
        Relation relationDef = expression;
        DMNRelationEvaluator relationEval = new DMNRelationEvaluator( node.getName(), node.getId(), relationDef );
        for( InformationItem col : relationDef.getColumn() ) {
            relationEval.addColumn( col.getName() );
        }
        for( org.kie.dmn.feel.model.v1_1.List row : relationDef.getRow() ) {
            List<DMNExpressionEvaluator> values = new ArrayList<>(  );
            for( Expression expr : row.getExpression() ) {
                values.add( compileExpression( model, node, relationName, expr ) );
            }
            relationEval.addRow( values );
        }
        return relationEval;
    }

    private DMNExpressionEvaluator compileList(DMNModelImpl model, DMNNode node, String listName, org.kie.dmn.feel.model.v1_1.List expression) {
        org.kie.dmn.feel.model.v1_1.List listDef = expression;
        DMNListEvaluator listEval = new DMNListEvaluator( node.getName(), node.getId(), listDef );
        for( Expression expr : listDef.getExpression() ) {
            listEval.addElement( compileExpression( model, node, listName, expr ) );
        }
        return listEval;
    }

    private DMNExpressionEvaluator compileContext(DMNModelImpl model, DMNNode node, String contextName, Context expression) {
        Context ctxDef = expression;
        DMNContextEvaluator ctxEval = new DMNContextEvaluator( node.getName(), ctxDef );
        for( ContextEntry ce : ctxDef.getContextEntry() ) {
            if( ce.getVariable() != null ) {
                ctxEval.addEntry( ce.getVariable().getName(),
                                  resolveTypeRef( model, node, ce.getVariable(), ce.getVariable(), ce.getVariable().getTypeRef() ),
                                  compileExpression( model, node, ce.getVariable().getName(), ce.getExpression() ) );
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
                                  compileExpression( model, node, contextName, ce.getExpression() ) );
            }
        }
        return ctxEval;
    }

    private DMNExpressionEvaluator compileFunctionDefinition(DMNModelImpl model, DMNNode node, String functionName, FunctionDefinition expression) {
        FunctionDefinition funcDef = expression;
        DMNExpressionEvaluatorInvokerFunction func = new DMNExpressionEvaluatorInvokerFunction( node.getName(), funcDef );
        for( InformationItem p : funcDef.getFormalParameter() ) {
            func.addParameter( p.getName(), resolveTypeRef( model, node, p, p, p.getTypeRef() ) );
        }
        DMNExpressionEvaluator eval = compileExpression( model, node, functionName, funcDef.getExpression() );
        func.setEvaluator( eval );
        return func;
    }

    private DMNExpressionEvaluator compileDecisionTable(DMNNode node, String dtName, DecisionTable expression, FEEL feel) {
        DecisionTable dt = expression;
        List<DTInputClause> inputs = new ArrayList<>(  );
        for( InputClause ic : dt.getInput() ) {
            String inputExpressionText = ic.getInputExpression().getText();
            String inputValuesText =  Optional.ofNullable( ic.getInputValues() ).map( UnaryTests::getText).orElse( null);
            inputs.add( new DTInputClause(inputExpressionText, inputValuesText, textToUnaryTestList( feel, inputValuesText) ) );
        }
        List<DTOutputClause> outputs = new ArrayList<>(  );
        for( OutputClause oc : dt.getOutput() ) {
            String outputName = oc.getName();
            String id = oc.getId();
            String outputValuesText =  Optional.ofNullable( oc.getOutputValues() ).map(UnaryTests::getText).orElse(null);
            String defaultValue = oc.getDefaultOutputEntry() != null ? oc.getDefaultOutputEntry().getText() : null;
            outputs.add( new DTOutputClause(outputName, id, (List<String>) feel.evaluate("["+outputValuesText+"]"), defaultValue ) );         // TODO another hack to be revised
        }
        List<DTDecisionRule> rules = new ArrayList<>(  );
        int index = 0;
        for( DecisionRule dr : dt.getRule() ) {
            DTDecisionRule rule = new DTDecisionRule( index++ );
            for( UnaryTests ut : dr.getInputEntry() ) {
                List<UnaryTest> tests = textToUnaryTestList( feel, ut.getText() );
                rule.getInputEntry().add( (c, x) -> tests.stream().anyMatch( t -> t.apply( c, x ) ) );
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
            parameterNames.addAll( ((DMNBaseNode) node).getDependencies().keySet() );
        }

        DecisionTableImpl dti = new DecisionTableImpl( dtName, parameterNames, inputs, outputs, rules, hp );
        DTInvokerFunction dtf = new DTInvokerFunction( dti );
        DMNDTExpressionEvaluator dtee = new DMNDTExpressionEvaluator( node, dtf );
        return dtee;
    }

    private DMNExpressionEvaluator compileLiteralExpression(DMNNode node, LiteralExpression expression, FEEL feel) {
        CompilerContext ctx = feel.newCompilerContext();
        ((DMNBaseNode) node).getDependencies().forEach( (name, depNode) -> {
            // TODO: need to properly resolve types here
            ctx.addInputVariableType( name, BuiltInType.UNKNOWN );
        } );
        DMNLiteralExpressionEvaluator evaluator = null;
        if( expression.getExpressionLanguage() == null || expression.getExpressionLanguage().equals( DMNModelInstrumentedBase.URI_FEEL ) ) {
            CompiledExpression compiledExpression = feel.compile( expression.getText(), ctx );
            evaluator = new DMNLiteralExpressionEvaluator( compiledExpression );
        }
        return evaluator;
    }

    protected static List<UnaryTest> textToUnaryTestList(FEEL feel, String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptyList();
        }
        return feel.evaluateUnaryTests( text );
    }


}