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
import org.kie.dmn.api.core.*;
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.api.marshalling.v1_1.DMNExtensionRegister;
import org.kie.dmn.backend.marshalling.v1_1.DMNMarshallerFactory;
import org.kie.dmn.core.ast.*;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.model.v1_1.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;


public class DMNCompilerImpl
        implements DMNCompiler {

    private static final Logger logger = LoggerFactory.getLogger( DMNCompilerImpl.class );
    private final DMNEvaluatorCompiler evaluatorCompiler;
    private final DMNFEELHelper feel;
    private DMNCompilerConfiguration dmnCompilerConfig;
    private Deque<DRGElementCompiler> drgCompilers = new LinkedList<>();
    {
        drgCompilers.add( new InputDataCompiler() );
        drgCompilers.add( new BusinessKnowledgeModelCompiler() );
        drgCompilers.add( new DecisionCompiler() );
        drgCompilers.add( new KnowledgeSourceCompiler() ); // keep last as it's a void compiler
    }

    public DMNCompilerImpl() {
        this.feel = new DMNFEELHelper();
        this.evaluatorCompiler = new DMNEvaluatorCompiler( this, feel );
    }

    public DMNCompilerImpl(DMNCompilerConfiguration dmnCompilerConfig) {
        this.feel = new DMNFEELHelper();
        this.evaluatorCompiler = new DMNEvaluatorCompiler( this, feel );
        this.dmnCompilerConfig = dmnCompilerConfig;
    }
    
    public void addDRGElementCompiler(DRGElementCompiler compiler) {
        drgCompilers.push(compiler);
    }
    public void addDRGElementCompilers(List<DRGElementCompiler> compilers) {
        ListIterator<DRGElementCompiler> listIterator = compilers.listIterator( compilers.size() );
        while ( listIterator.hasPrevious() ) {
            addDRGElementCompiler( listIterator.previous() );
        }
    }

    @Override
    public DMNModel compile(Resource resource) {
        try {
            DMNModel model = compile( resource.getReader() );
            ((DMNModelImpl)model).setResource( resource );
            return model;
        } catch ( IOException e ) {
            logger.error( "Error retrieving reader for resource: " + resource.getSourcePath(), e );
        }
        return null;
    }

    @Override
    public DMNModel compile(Reader source) {
        try {
            Definitions dmndefs = null;
            if(dmnCompilerConfig != null && !dmnCompilerConfig.getRegisteredExtensions().isEmpty()) {
                dmndefs = DMNMarshallerFactory.newMarshallerWithExtensions(getDmnCompilerConfig().getRegisteredExtensions()).unmarshal( source );
            } else {
                dmndefs = DMNMarshallerFactory.newDefaultMarshaller().unmarshal(source);
            }
            DMNModel model = compile( dmndefs );
            return model;
        } catch ( Exception e ) {
            logger.error( "Error compiling model from source.", e );
        }
        return null;
    }

    @Override
    public DMNModel compile(Definitions dmndefs) {
        DMNModelImpl model = null;
        if ( dmndefs != null ) {
            model = new DMNModelImpl( dmndefs );
            DMNCompilerContext ctx = new DMNCompilerContext();

            processItemDefinitions( ctx, feel, model, dmndefs );
            processDrgElements( ctx, feel, model, dmndefs );
            return model;
        }
        return model;
    }

    private void processItemDefinitions(DMNCompilerContext ctx, DMNFEELHelper feel, DMNModelImpl model, Definitions dmndefs) {
        dmndefs.getItemDefinition().stream().forEach(x->processItemDefQNameURIs(x));
        
        List<ItemDefinition> ordered = new ItemDefinitionDependenciesSorter(model.getNamespace()).sort(dmndefs.getItemDefinition());
        
        for ( ItemDefinition id : ordered ) {
            ItemDefNodeImpl idn = new ItemDefNodeImpl( id );
            DMNType type = buildTypeDef( ctx, feel, model, idn, id, true );
            idn.setType( type );
            model.addItemDefinition( idn );
        }
    }
    
    /**
     * Utility method to ensure any QName references contained inside the ItemDefinition have the namespace correctly valorized, also accordingly to the prefix.
     * (Even in the case of {@link XMLConstants.DEFAULT_NS_PREFIX} it will take the DMN model namespace for the no-prefix accordingly.)
     * @param id the ItemDefinition for which to ensure the QName references are valorized correctly.
     */
    private void processItemDefQNameURIs( ItemDefinition id ) {
        QName typeRef = id.getTypeRef();
        if ( typeRef != null && XMLConstants.NULL_NS_URI.equals( typeRef.getNamespaceURI() ) ) {
            String actualNS = id.getNamespaceURI( typeRef.getPrefix() );
            id.setTypeRef(new QName(actualNS, typeRef.getLocalPart(), typeRef.getPrefix()));
        }
        for ( ItemDefinition ic : id.getItemComponent() ) {
            processItemDefQNameURIs( ic );
        }
    }

    private void processDrgElements(DMNCompilerContext ctx, DMNFEELHelper feel, DMNModelImpl model, Definitions dmndefs) {
        for ( DRGElement e : dmndefs.getDrgElement() ) {
            boolean foundIt = false;
            for( DRGElementCompiler dc : drgCompilers ) {
                if ( dc.accept( e ) ) {
                    foundIt = true;
                    dc.compileNode(e, this, model);
                    continue;
                }
            }  
            if ( !foundIt ) {
                MsgUtil.reportMessage( logger,
                                       DMNMessage.Severity.ERROR,
                                       e,
                                       model,
                                       null,
                                       null,
                                       Msg.UNSUPPORTED_ELEMENT,
                                       e.getClass().getSimpleName(),
                                       e.getId() );
            }
        }

        for ( BusinessKnowledgeModelNode bkm : model.getBusinessKnowledgeModels() ) {
            BusinessKnowledgeModelNodeImpl bkmi = (BusinessKnowledgeModelNodeImpl) bkm;
            for( DRGElementCompiler dc : drgCompilers ) {
                if ( bkmi.getEvaluator() == null && dc.accept( bkm ) ) {
                    dc.compileEvaluator(bkm, this, ctx, model);
                }
            }
        }

        for ( DecisionNode d : model.getDecisions() ) {
            DecisionNodeImpl di = (DecisionNodeImpl) d;
            for( DRGElementCompiler dc : drgCompilers ) {
                if ( di.getEvaluator() == null && dc.accept( d ) ) {
                    dc.compileEvaluator(d, this, ctx, model);
                }
            }
        }
    }

    public void linkRequirements(DMNModelImpl model, DMNBaseNode node) {
        for ( InformationRequirement ir : node.getInformationRequirement() ) {
            if ( ir.getRequiredInput() != null ) {
                String id = getId( ir.getRequiredInput() );
                InputDataNode input = model.getInputById( id );
                if ( input != null ) {
                    node.addDependency( input.getName(), input );
                } else {
                    MsgUtil.reportMessage( logger,
                                           DMNMessage.Severity.ERROR,
                                           ir.getRequiredInput(),
                                           model,
                                           null,
                                           null,
                                           Msg.REQ_INPUT_NOT_FOUND_FOR_NODE,
                                           id,
                                           node.getName() );
                }
            } else if ( ir.getRequiredDecision() != null ) {
                String id = getId( ir.getRequiredDecision() );
                DecisionNode dn = model.getDecisionById( id );
                if ( dn != null ) {
                    node.addDependency( dn.getName(), dn );
                } else {
                    MsgUtil.reportMessage( logger,
                                           DMNMessage.Severity.ERROR,
                                           ir.getRequiredDecision(),
                                           model,
                                           null,
                                           null,
                                           Msg.REQ_DECISION_NOT_FOUND_FOR_NODE,
                                           id,
                                           node.getName() );
                }
            }
        }
        for ( KnowledgeRequirement kr : node.getKnowledgeRequirement() ) {
            if ( kr.getRequiredKnowledge() != null ) {
                String id = getId( kr.getRequiredKnowledge() );
                BusinessKnowledgeModelNode bkmn = model.getBusinessKnowledgeModelById( id );
                if ( bkmn != null ) {
                    node.addDependency( bkmn.getName(), bkmn );
                } else {
                    MsgUtil.reportMessage( logger,
                                           DMNMessage.Severity.ERROR,
                                           kr.getRequiredKnowledge(),
                                           model,
                                           null,
                                           null,
                                           Msg.REQ_BKM_NOT_FOUND_FOR_NODE,
                                           id,
                                           node.getName() );
                }
            }
        }
    }

    private String getId(DMNElementReference er) {
        String href = er.getHref();
        return href.contains( "#" ) ? href.substring( href.indexOf( '#' ) + 1 ) : href;
    }

    private DMNType buildTypeDef(DMNCompilerContext ctx, DMNFEELHelper feel, DMNModelImpl dmnModel, DMNNode node, ItemDefinition itemDef, boolean topLevel) {
        BaseDMNTypeImpl type = null;
        if ( itemDef.getTypeRef() != null ) {
            // this is a reference to an existing type, so resolve the reference
            type = (BaseDMNTypeImpl) resolveTypeRef( dmnModel, node, itemDef, itemDef, itemDef.getTypeRef() );
            if ( type != null ) {
                UnaryTests allowedValuesStr = itemDef.getAllowedValues();

                // we only want to clone the type definition if it is a top level type (not a field in a composite type)
                // or if it changes the metadata for the base type
                if( topLevel || allowedValuesStr != null || itemDef.isIsCollection() != type.isCollection() ) {

                    // we have to clone this type definition into a new one
                    BaseDMNTypeImpl baseType = type;
                    type = type.clone();

                    type.setBaseType( baseType );
                    type.setNamespace( dmnModel.getNamespace() );
                    type.setName( itemDef.getName() );
                    type.setAllowedValues(null);
                    if ( allowedValuesStr != null ) {
                        List<UnaryTest> av = feel.evaluateUnaryTests(
                                ctx,
                                allowedValuesStr.getText(),
                                dmnModel,
                                itemDef,
                                Msg.ERR_COMPILING_ALLOWED_VALUES_LIST_ON_ITEM_DEF,
                                allowedValuesStr.getText(),
                                node.getName()
                        );
                        type.setAllowedValues( av );
                    }
                    if ( itemDef.isIsCollection() ) {
                        type.setCollection( itemDef.isIsCollection() );
                    }
                }
                if( topLevel ) {
                    DMNType registered = dmnModel.getTypeRegistry().registerType( type );
                    if( registered != type ) {
                        MsgUtil.reportMessage( logger,
                                               DMNMessage.Severity.ERROR,
                                               itemDef,
                                               dmnModel,
                                               null,
                                               null,
                                               Msg.DUPLICATED_ITEM_DEFINITION,
                                               itemDef.getName() );
                    }
                }
            }
        } else {
            // this is a composite type
            DMNCompilerHelper.checkVariableName( dmnModel, itemDef, itemDef.getName() );
            CompositeTypeImpl compType = new CompositeTypeImpl( dmnModel.getNamespace(), itemDef.getName(), itemDef.getId(), itemDef.isIsCollection() );
            for ( ItemDefinition fieldDef : itemDef.getItemComponent() ) {
                DMNCompilerHelper.checkVariableName( dmnModel, fieldDef, fieldDef.getName() );
                DMNType fieldType = buildTypeDef( ctx, feel, dmnModel, node, fieldDef, false );
                fieldType = fieldType != null ? fieldType : DMNTypeRegistry.UNKNOWN;
                compType.addField( fieldDef.getName(), fieldType );
            }
            type = compType;
            if( topLevel ) {
                DMNType registered = dmnModel.getTypeRegistry().registerType( type );
                if( registered != type ) {
                    MsgUtil.reportMessage( logger,
                                           DMNMessage.Severity.ERROR,
                                           itemDef,
                                           dmnModel,
                                           null,
                                           null,
                                           Msg.DUPLICATED_ITEM_DEFINITION,
                                           itemDef.getName() );
                }
            }
        }
        return type;
    }

    public DMNType resolveTypeRef(DMNModelImpl dmnModel, DMNNode node, NamedElement model, DMNModelInstrumentedBase localElement, QName typeRef) {
        if ( typeRef != null ) {
            String namespace = getNamespace( localElement, typeRef );

            DMNType type = dmnModel.getTypeRegistry().resolveType( namespace, typeRef.getLocalPart() );
            if( type == null && DMNModelInstrumentedBase.URI_FEEL.equals( namespace ) ) {
                if ( model instanceof Decision && ((Decision) model).getExpression() instanceof DecisionTable ) {
                    DecisionTable dt = (DecisionTable) ((Decision) model).getExpression();
                    if ( dt.getOutput().size() > 1 ) {
                        // implicitly define a type for the decision table result
                        CompositeTypeImpl compType = new CompositeTypeImpl( dmnModel.getNamespace(), model.getName()+"_Type", model.getId(), dt.getHitPolicy().isMultiHit() );
                        for ( OutputClause oc : dt.getOutput() ) {
                            DMNType fieldType = resolveTypeRef( dmnModel, node, model, oc, oc.getTypeRef() );
                            compType.addField( oc.getName(), fieldType );
                        }
                        dmnModel.getTypeRegistry().registerType( compType );
                        return compType;
                    } else if ( dt.getOutput().size() == 1 ) {
                        return resolveTypeRef( dmnModel, node, model, dt.getOutput().get( 0 ), dt.getOutput().get( 0 ).getTypeRef() );
                    }
                }
            } else if( type == null ) {
                MsgUtil.reportMessage( logger,
                                       DMNMessage.Severity.ERROR,
                                       localElement,
                                       dmnModel,
                                       null,
                                       null,
                                       Msg.UNKNOWN_TYPE_REF_ON_NODE,
                                       typeRef.toString(),
                                       localElement.getParentDRDElement().getIdentifierString() );
            }
            return type;
        }
        return dmnModel.getTypeRegistry().resolveType( DMNModelInstrumentedBase.URI_FEEL, BuiltInType.UNKNOWN.getName() );
    }

    private String getNamespace(DMNModelInstrumentedBase localElement, QName typeRef) {
        String prefix = typeRef.getPrefix();
        return localElement.getNamespaceURI( prefix );
    }

    private DMNCompilerConfiguration getDmnCompilerConfig() {
        return this.dmnCompilerConfig;
    }

    public List<DMNExtensionRegister> getRegisteredExtensions() {
        if ( this.dmnCompilerConfig == null ) {
            return Collections.emptyList();
        } else {
            return this.dmnCompilerConfig.getRegisteredExtensions();
        }
    }
    
    public DMNEvaluatorCompiler getEvaluatorCompiler() {
        return evaluatorCompiler;
    }
    
}