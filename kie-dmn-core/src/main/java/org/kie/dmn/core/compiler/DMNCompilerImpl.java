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
import org.kie.dmn.core.api.DMNModel;
import org.kie.dmn.core.api.DMNType;
import org.kie.dmn.core.ast.DecisionNode;
import org.kie.dmn.core.ast.InputDataNode;
import org.kie.dmn.core.ast.ItemDefNode;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.impl.FeelTypeImpl;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.model.v1_1.DMNElementReference;
import org.kie.dmn.feel.model.v1_1.DMNModelInstrumentedBase;
import org.kie.dmn.feel.model.v1_1.DRGElement;
import org.kie.dmn.feel.model.v1_1.Decision;
import org.kie.dmn.feel.model.v1_1.DecisionRule;
import org.kie.dmn.feel.model.v1_1.DecisionTable;
import org.kie.dmn.feel.model.v1_1.Definitions;
import org.kie.dmn.feel.model.v1_1.Expression;
import org.kie.dmn.feel.model.v1_1.InformationRequirement;
import org.kie.dmn.feel.model.v1_1.InputClause;
import org.kie.dmn.feel.model.v1_1.InputData;
import org.kie.dmn.feel.model.v1_1.ItemDefinition;
import org.kie.dmn.feel.model.v1_1.LiteralExpression;
import org.kie.dmn.feel.model.v1_1.NamedElement;
import org.kie.dmn.feel.model.v1_1.OutputClause;
import org.kie.dmn.feel.model.v1_1.UnaryTests;
import org.kie.dmn.feel.runtime.Range;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.runtime.decisiontables.DTDecisionRule;
import org.kie.dmn.feel.runtime.decisiontables.DTInputClause;
import org.kie.dmn.feel.runtime.decisiontables.DTInvokerFunction;
import org.kie.dmn.feel.runtime.decisiontables.DTOutputClause;
import org.kie.dmn.feel.runtime.decisiontables.HitPolicy;
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
            DMNType type = buildTypeDef( model, id );
            ItemDefNode idn = new ItemDefNode( id, type );
            model.addItemDefinition( idn );
        }
    }

    private void processDrgElements(DMNModelImpl model, Definitions dmndefs) {
        for ( DRGElement e : dmndefs.getDrgElement() ) {
            if ( e instanceof InputData ) {
                InputData input = (InputData) e;
                DMNType type = resolveSimpleTypeRef( model, e, input.getVariable().getTypeRef() );
                InputDataNode idn = new InputDataNode( input, type );
                model.addInput( idn );
                model.getTypeRegistry().put( input.getVariable().getTypeRef(), type );
            } else if ( e instanceof Decision ) {
                Decision decision = (Decision) e;
                DMNType type = null;
                if( decision.getVariable() != null && decision.getVariable().getTypeRef() != null ) {
                    type = resolveSimpleTypeRef( model, decision, decision.getVariable().getTypeRef() );
                } else {
                    // TODO: need to handle cases where the variable is not defined or does not have a type;
                    // for now the call bellow will return type UNKNOWN
                    type = resolveSimpleTypeRef( model, decision, null );
                }
                DecisionNode dn = new DecisionNode( decision, type );
                model.addDecision( dn );
            }
        }

        for ( DecisionNode d : model.getDecisions() ) {
            linkDecisionRequirements( model, d );
            DecisionNode.DecisionEvaluator evaluator = compileDecision( d );
            d.setEvaluator( evaluator );
        }
    }

    private void linkDecisionRequirements(DMNModelImpl model, DecisionNode decision) {
        for ( InformationRequirement ir : decision.getDecision().getInformationRequirement() ) {
            if ( ir.getRequiredInput() != null ) {
                String id = getId( ir.getRequiredInput() );
                InputDataNode input = model.getInputById( id );
                decision.addDependency( input.getName(), input );
            } else if ( ir.getRequiredDecision() != null ) {
                String id = getId( ir.getRequiredDecision() );
                DecisionNode dn = model.getDecisionById( id );
                decision.addDependency( dn.getName(), dn );
            }
        }
    }

    private String getId(DMNElementReference er) {
        String href = er.getHref();
        return href.contains( "#" ) ? href.substring( href.indexOf( '#' ) + 1 ) : href;
    }

    private DMNType buildTypeDef( DMNModelImpl dmnModel, ItemDefinition itemDef ) {
        DMNType type = null;
        if( itemDef.getTypeRef() != null ) {
            // this is an "simple" type, so find the namespace
            type = resolveSimpleTypeRef( dmnModel, itemDef, itemDef.getTypeRef() );
            UnaryTests allowedValuesStr = itemDef.getAllowedValues();
            if( allowedValuesStr != null ) {
                Object av = FEEL.newInstance().evaluate( "[" + allowedValuesStr.getText() + "]" );
                java.util.List<?> allowedValues = av instanceof java.util.List ? (java.util.List) av : Collections.singletonList( av );
                ((FeelTypeImpl)type).setAllowedValues( allowedValues );
            }
        } else {
            // this is a composite type
            CompositeTypeImpl compType = new CompositeTypeImpl( itemDef.getName(), itemDef.getId() );
            for( ItemDefinition fieldDef : itemDef.getItemComponent() ) {
                DMNType field = buildTypeDef( dmnModel, fieldDef );
                compType.getFields().put( field.getName(), field );
            }
            type = compType;
        }
        return type;
    }

    private DMNType resolveSimpleTypeRef(DMNModelImpl dmnModel, NamedElement model, QName typeRef) {
        if( typeRef != null ) {
            String prefix = typeRef.getPrefix();
            String namespace = model.getNamespaceURI( prefix );
            if( namespace != null && DMNModelInstrumentedBase.URI_FEEL.equals( namespace ) ) {
                Type feelType = BuiltInType.determineTypeFromName( typeRef.getLocalPart() );
                return new FeelTypeImpl( model.getName(), model.getId(), feelType, null );
            } else if( dmnModel.getNamespace() != null && namespace != null && dmnModel.getNamespace().equals( namespace ) ) {
                // locally defined type
                List<ItemDefNode> itemDefs = dmnModel.getItemDefinitions().stream()
                        .filter( id -> id.getName() != null && id.getName().equals( typeRef.getLocalPart() ) )
                        .collect( toList() );
                if( itemDefs.size() == 1 ) {
                    return itemDefs.get( 0 ).getType();
                } else if( itemDefs.isEmpty() ) {
                    logger.error( "No '"+typeRef.toString()+"' type definition found.");
                } else {
                    logger.error( "Multiple types found for type reference '"+typeRef.toString()+"'.");
                }
            } else {
                logger.error( "Unknown namespace for type reference prefix: "+prefix );
            }
            return null;
        }
        return new FeelTypeImpl( model.getName(), model.getId(), BuiltInType.UNKNOWN, null );
    }

    private DecisionNode.DecisionEvaluator compileDecision(DecisionNode decisionNode) {
        Decision decision = decisionNode.getDecision();
        FEEL feel = FEEL.newInstance();
        Expression expression = decision.getExpression();
        if( expression instanceof LiteralExpression ) {
            CompilerContext ctx = feel.newCompilerContext();
            decisionNode.getDependencies().forEach( (name, node) -> {
                // TODO: need to properly resolve types here
                ctx.addInputVariableType( name, BuiltInType.UNKNOWN );
            } );
            CompiledExpression compiledExpression = feel.compile( ((LiteralExpression) expression).getText(), ctx );
            DecisionNode.LiteralExpressionFEELEvaluator evaluator = new DecisionNode.LiteralExpressionFEELEvaluator( compiledExpression );
            return evaluator;
        } else if( expression instanceof DecisionTable ) {
            DecisionTable dt = (DecisionTable) expression;
            List<DTInputClause> inputs = new ArrayList<>(  );
            for( InputClause ic : dt.getInput() ) {
                String inputExpressionText = ic.getInputExpression().getText();
                String inputValuesText =  Optional.ofNullable( ic.getInputValues() ).map(UnaryTests::getText).orElse(null);
                inputs.add( new DTInputClause(inputExpressionText, textToUnaryTestList(inputValuesText) ) );
            }
            List<DTOutputClause> outputs = new ArrayList<>(  );
            for( OutputClause oc : dt.getOutput() ) {
                String outputName = oc.getName();
                String id = oc.getId();
                String outputValuesText =  Optional.ofNullable( oc.getOutputValues() ).map(UnaryTests::getText).orElse(null);
                outputs.add( new DTOutputClause(outputName, id, (List<String>) feel.evaluate("["+outputValuesText+"]") ) );         // TODO another hack to be revised
            }
            List<DTDecisionRule> rules = new ArrayList<>(  );
            for( DecisionRule dr : dt.getRule() ) {
                DTDecisionRule rule = new DTDecisionRule();
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
            DTInvokerFunction dtf = new DTInvokerFunction( decision.getName()+"_DT", inputs, rules, outputs, HitPolicy.fromString( dt.getHitPolicy().value() ) );
            DecisionNode.DTExpressionEvaluator dtee = new DecisionNode.DTExpressionEvaluator( decision, dtf );
            return dtee;
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