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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.drools.core.io.impl.FileSystemResource;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNCompiler;
import org.kie.dmn.api.core.DMNCompilerConfiguration;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.api.core.ast.BusinessKnowledgeModelNode;
import org.kie.dmn.api.core.ast.DMNNode;
import org.kie.dmn.api.core.ast.DecisionNode;
import org.kie.dmn.api.core.ast.DecisionServiceNode;
import org.kie.dmn.api.core.ast.InputDataNode;
import org.kie.dmn.api.core.ast.ItemDefNode;
import org.kie.dmn.api.marshalling.DMNExtensionRegister;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.backend.marshalling.v1x.DMNMarshallerFactory;
import org.kie.dmn.core.api.DMNFactory;
import org.kie.dmn.core.ast.BusinessKnowledgeModelNodeImpl;
import org.kie.dmn.core.ast.DMNBaseNode;
import org.kie.dmn.core.ast.DecisionNodeImpl;
import org.kie.dmn.core.ast.DecisionServiceNodeImpl;
import org.kie.dmn.core.ast.ItemDefNodeImpl;
import org.kie.dmn.core.compiler.ImportDMNResolverUtil.ImportType;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.pmml.DMNImportPMMLInfo;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.AliasFEELType;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.runtime.UnaryTest;
import org.kie.dmn.feel.util.Either;
import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.Decision;
import org.kie.dmn.model.api.DecisionService;
import org.kie.dmn.model.api.DecisionTable;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.Import;
import org.kie.dmn.model.api.InformationItem;
import org.kie.dmn.model.api.InformationRequirement;
import org.kie.dmn.model.api.ItemDefinition;
import org.kie.dmn.model.api.KnowledgeRequirement;
import org.kie.dmn.model.api.NamedElement;
import org.kie.dmn.model.api.OutputClause;
import org.kie.dmn.model.api.UnaryTests;
import org.kie.dmn.model.v1_1.KieDMNModelInstrumentedBase;
import org.kie.dmn.model.v1_1.TInformationItem;
import org.kie.dmn.model.v1_1.extensions.DecisionServices;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DMNCompilerImpl implements DMNCompiler {

    private static final Logger logger = LoggerFactory.getLogger( DMNCompilerImpl.class );

    private final DMNEvaluatorCompiler evaluatorCompiler;
    private DMNCompilerConfiguration dmnCompilerConfig;
    private Deque<DRGElementCompiler> drgCompilers = new LinkedList<>();
    {
        drgCompilers.add( new InputDataCompiler() );
        drgCompilers.add( new BusinessKnowledgeModelCompiler() );
        drgCompilers.add( new DecisionCompiler() );
        drgCompilers.add( new DecisionServiceCompiler() );
        drgCompilers.add( new KnowledgeSourceCompiler() ); // keep last as it's a void compiler
    }

    public DMNCompilerImpl() {
        this(DMNFactory.newCompilerConfiguration());
    }

    public DMNCompilerImpl(DMNCompilerConfiguration dmnCompilerConfig) {
        this.dmnCompilerConfig = dmnCompilerConfig;
        DMNCompilerConfigurationImpl cc = (DMNCompilerConfigurationImpl) dmnCompilerConfig;
        addDRGElementCompilers(cc.getDRGElementCompilers());
        this.evaluatorCompiler = DMNEvaluatorCompiler.dmnEvaluatorCompilerFactory(this, cc);
    }

    private void addDRGElementCompiler(DRGElementCompiler compiler) {
        drgCompilers.push(compiler);
    }

    private void addDRGElementCompilers(List<DRGElementCompiler> compilers) {
        ListIterator<DRGElementCompiler> listIterator = compilers.listIterator( compilers.size() );
        while ( listIterator.hasPrevious() ) {
            addDRGElementCompiler( listIterator.previous() );
        }
    }

    @Override
    public DMNModel compile(Resource resource, Collection<DMNModel> dmnModels) {
        try {
            DMNModel model = compile(resource.getReader(), dmnModels, resource);
            return model;
        } catch ( IOException e ) {
            logger.error( "Error retrieving reader for resource: " + resource.getSourcePath(), e );
        }
        return null;
    }

    @Override
    public DMNModel compile(Reader source, Collection<DMNModel> dmnModels) {
        return compile(source, dmnModels, null);
    }

    public DMNModel compile(Reader source, Collection<DMNModel> dmnModels, Resource resource) {
        try {
            Definitions dmndefs = getMarshaller().unmarshal(source);
            DMNModel model = compile(dmndefs, dmnModels, resource, null);
            return model;
        } catch ( Exception e ) {
            logger.error( "Error compiling model from source.", e );
        }
        return null;
    }

    public DMNMarshaller getMarshaller() {
        if (dmnCompilerConfig != null && !dmnCompilerConfig.getRegisteredExtensions().isEmpty()) {
            return DMNMarshallerFactory.newMarshallerWithExtensions(getDmnCompilerConfig().getRegisteredExtensions());
        } else {
            return DMNMarshallerFactory.newDefaultMarshaller();
        }
    }

    @Override
    public DMNModel compile(Definitions dmndefs, Collection<DMNModel> dmnModels) {
        return compile(dmndefs, dmnModels, null, null);
    }

    public DMNModel compile(Definitions dmndefs, Collection<DMNModel> dmnModels, Resource resource, Function<String, Reader> relativeResolver) {
        if (dmndefs == null) {
            return null;
        }
        DMNModelImpl model = new DMNModelImpl(dmndefs, resource);
        model.setRuntimeTypeCheck(((DMNCompilerConfigurationImpl) dmnCompilerConfig).getOption(RuntimeTypeCheckOption.class).isRuntimeTypeCheck());
        DMNCompilerConfigurationImpl cc = (DMNCompilerConfigurationImpl) dmnCompilerConfig;
        List<FEELProfile> helperFEELProfiles = cc.getFeelProfiles();
        DMNFEELHelper feel = new DMNFEELHelper(cc.getRootClassLoader(), helperFEELProfiles);
        DMNCompilerContext ctx = new DMNCompilerContext(feel);
        ctx.setRelativeResolver(relativeResolver);

        if (!dmndefs.getImport().isEmpty()) {
            for (Import i : dmndefs.getImport()) {
                if (ImportDMNResolverUtil.whichImportType(i) == ImportType.DMN) {
                    Either<String, DMNModel> resolvedResult = ImportDMNResolverUtil.resolveImportDMN(i, dmnModels, (DMNModel m) -> new QName(m.getNamespace(), m.getName()));
                    DMNModel located = resolvedResult.cata(msg -> {
                        MsgUtil.reportMessage(logger,
                                              DMNMessage.Severity.ERROR,
                                              i,
                                              model,
                                              null,
                                              null,
                                              Msg.IMPORT_NOT_FOUND_FOR_NODE,
                                              msg,
                                              i);
                        return null;
                    }, Function.identity());
                    if (located != null) {
                        String iAlias = Optional.ofNullable(i.getName()).orElse(located.getName());
                        model.setImportAliasForNS(iAlias, located.getNamespace(), located.getName());
                        importFromModel(model, located, iAlias);
                    }
                } else if (ImportDMNResolverUtil.whichImportType(i) == ImportType.PMML) {
                    processPMMLImport(model, i, relativeResolver);
                    model.setImportAliasForNS(i.getName(), i.getNamespace(), i.getName());
                } else {
                    MsgUtil.reportMessage(logger,
                                          DMNMessage.Severity.ERROR,
                                          null,
                                          model,
                                          null,
                                          null,
                                          Msg.IMPORT_TYPE_UNKNOWN,
                                          i.getImportType());
                }
            }
        }

        processItemDefinitions(ctx, model, dmndefs);
        processDrgElements(ctx, model, dmndefs);
        return model;
    }

    private void processPMMLImport(DMNModelImpl model, Import i, Function<String, Reader> relativeResolver) {
        ClassLoader rootClassLoader = ((DMNCompilerConfigurationImpl) dmnCompilerConfig).getRootClassLoader();
        Resource relativeResource = resolveRelativeResource(rootClassLoader, model, i, i, relativeResolver);
        try (InputStream pmmlIS = relativeResource.getInputStream()) {
            DMNImportPMMLInfo.from(pmmlIS, (DMNCompilerConfigurationImpl) dmnCompilerConfig, model, i).consume(new PMMLImportErrConsumer(model, i),
                                                                                                               model::addPMMLImportInfo);
        } catch (IOException e) {
            new PMMLImportErrConsumer(model, i).accept(e);
        }
    }

    public static class PMMLImportErrConsumer implements Consumer<Exception> {

        private final DMNModelImpl model;
        private final Import i;
        private final DMNModelInstrumentedBase node;

        public PMMLImportErrConsumer(DMNModelImpl model, Import i) {
            this(model, i, i);
        }

        public PMMLImportErrConsumer(DMNModelImpl model, Import i, DMNModelInstrumentedBase node) {
            this.model = model;
            this.i = i;
            this.node = node;
        }

        @Override
        public void accept(Exception t) {
            logger.error("Unable to locate pmml model from locationURI {}.", i.getLocationURI(), t);
            MsgUtil.reportMessage(logger,
                                  DMNMessage.Severity.ERROR,
                                  i,
                                  model,
                                  null,
                                  null,
                                  Msg.FUNC_DEF_PMML_ERR_LOCATIONURI,
                                  i.getLocationURI());
        }

    }

    protected static Resource resolveRelativeResource(ClassLoader classLoader, DMNModelImpl model, Import i, DMNModelInstrumentedBase node, Function<String, Reader> relativeResolver) {
        if (model.getResource() != null) {
            URL pmmlURL = pmmlImportURL(classLoader, model, i, node);
            return ResourceFactory.newUrlResource(pmmlURL);
        } else if (relativeResolver != null) {
            Reader reader = relativeResolver.apply(i.getLocationURI());
            return ResourceFactory.newReaderResource(reader);
        }
        throw new UnsupportedOperationException("Unable to determine relative Resource for import named: " + i.getName());
    }

    protected static URL pmmlImportURL(ClassLoader classLoader, DMNModelImpl model, Import i, DMNModelInstrumentedBase node) {
        String locationURI = i.getLocationURI();
        logger.trace("locationURI: {}", locationURI);
        URL pmmlURL = null;
        try {
            URI resolveRelativeURI = DMNCompilerImpl.resolveRelativeURI(model, locationURI);
            pmmlURL = resolveRelativeURI.isAbsolute() ? resolveRelativeURI.toURL() : classLoader.getResource(resolveRelativeURI.getPath());
        } catch (URISyntaxException | IOException e) {
            new PMMLImportErrConsumer(model, i, node).accept(e);
        }
        logger.trace("pmmlURL: {}", pmmlURL);
        return pmmlURL;
    }

    protected static URI resolveRelativeURI(DMNModelImpl model, String relative) throws URISyntaxException, IOException {
        URI relativeAsURI = new URI(null, null, relative, null);
        if (model.getResource() instanceof FileSystemResource) {
            FileSystemResource fsr = (FileSystemResource) model.getResource();
            logger.trace("fsr: {}", fsr.getURL());
            URI resolve = fsr.getURL().toURI().resolve(relativeAsURI);
            return resolve;
        } else {
            URI dmnModelURI = new URI(null, null, model.getResource().getSourcePath(), null);
            logger.trace("dmnModelURI: {}", dmnModelURI);
            URI relativeURI = dmnModelURI.resolve(relativeAsURI);
            return relativeURI;
        }
    }

    private void importFromModel(DMNModelImpl model, DMNModel m, String iAlias) {
        model.addImportChainChild(((DMNModelImpl) m).getImportChain(), iAlias);
        for (ItemDefNode idn : m.getItemDefinitions()) {
            model.getTypeRegistry().registerType(idn.getType());
        }
        for (InputDataNode idn : m.getInputs()) {
            model.addInput(idn);
        }
        for (BusinessKnowledgeModelNode bkm : m.getBusinessKnowledgeModels()) {
            model.addBusinessKnowledgeModel(bkm);
        }
        for (DecisionNode dn : m.getDecisions()) {
            model.addDecision(dn);
        }
        for (DecisionServiceNode dsn : m.getDecisionServices()) {
            model.addDecisionService(dsn);
        }
    }

    private void processItemDefinitions(DMNCompilerContext ctx, DMNModelImpl model, Definitions dmndefs) {
        dmndefs.normalize();
        
        List<ItemDefinition> ordered = new ItemDefinitionDependenciesSorter(model.getNamespace()).sort(dmndefs.getItemDefinition());
        
        for ( ItemDefinition id : ordered ) {
            ItemDefNodeImpl idn = new ItemDefNodeImpl( id );
            DMNType type = buildTypeDef(ctx, model, idn, id, true);
            idn.setType( type );
            model.addItemDefinition( idn );
        }
    }

    private void processDrgElements(DMNCompilerContext ctx, DMNModelImpl model, Definitions dmndefs) {
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

        // in DMN v1.1 the DecisionService is not on the DRGElement but as an extension
        if (dmndefs.getExtensionElements() != null) {
            List<DecisionServices> decisionServices = dmndefs.getExtensionElements().getAny().stream().filter(DecisionServices.class::isInstance).map(DecisionServices.class::cast).collect(Collectors.toList());
            for (DecisionServices dss : decisionServices) {
                for (DecisionService ds : dss.getDecisionService()) {
                    // compatibility with DMN v1.1, create Decision Service's variable:
                    if (ds.getVariable() == null) {
                        InformationItem variable = new TInformationItem();
                        variable.setId(UUID.randomUUID().toString());
                        variable.setName(ds.getName());
                        variable.setParent(ds);
                        // the introduction of an on-the-fly ItemDefinition has been removed. The variable type will be evaluated as feel:any, or in v1.2 will receive the (user-defined, explicit) ItemDefinition type.
                        ds.setVariable(variable);
                    }
                    // continuing with normal compilation of Decision Service:
                    boolean foundIt = false;
                    for (DRGElementCompiler dc : drgCompilers) {
                        if (dc.accept(ds)) {
                            foundIt = true;
                            dc.compileNode(ds, this, model);
                            continue;
                        }
                    }
                }
            }
        }
        for (DecisionServiceNode ds : model.getDecisionServices()) {
            DecisionServiceNodeImpl dsi = (DecisionServiceNodeImpl) ds;
            dsi.addModelImportAliases(model.getImportAliasesForNS());
            for (DRGElementCompiler dc : drgCompilers) {
                if (dsi.getEvaluator() == null && dc.accept(dsi)) { // will compile in fact all DS belonging to this model (not the imported ones).
                    dc.compileEvaluator(dsi, this, ctx, model);
                }
            }
        }

        for ( BusinessKnowledgeModelNode bkm : model.getBusinessKnowledgeModels() ) {
            BusinessKnowledgeModelNodeImpl bkmi = (BusinessKnowledgeModelNodeImpl) bkm;
            bkmi.addModelImportAliases(model.getImportAliasesForNS());
            for( DRGElementCompiler dc : drgCompilers ) {
                if ( bkmi.getEvaluator() == null && dc.accept( bkm ) ) {
                    dc.compileEvaluator(bkm, this, ctx, model);
                }
            }
        }

        for ( DecisionNode d : model.getDecisions() ) {
            DecisionNodeImpl di = (DecisionNodeImpl) d;
            di.addModelImportAliases(model.getImportAliasesForNS());
            for( DRGElementCompiler dc : drgCompilers ) {
                if ( di.getEvaluator() == null && dc.accept( d ) ) {
                    dc.compileEvaluator(d, this, ctx, model);
                }
            }
        }
        detectCycles( model );


    }

    private void detectCycles( DMNModelImpl model ) {
        /*
        Boolean.TRUE = node is either safe or already reported for having a cyclic dependency
        Boolean.FALSE = node is being checked at the moment
         */
        final Map<DecisionNodeImpl, Boolean> registry = new HashMap<>();
        for ( DecisionNode decision : model.getDecisions() ) {
            final DecisionNodeImpl decisionNode = (DecisionNodeImpl) decision;
            detectCycles( decisionNode, registry, model );
        }
    }

    private void detectCycles( DecisionNodeImpl node, Map<DecisionNodeImpl, Boolean> registry, DMNModelImpl model ) {
        if ( Boolean.TRUE.equals(registry.get( node ) ) ) return;
        if ( Boolean.FALSE.equals( registry.put( node, Boolean.FALSE ) ) ) {
            MsgUtil.reportMessage( logger,
                                   DMNMessage.Severity.ERROR,
                                   node.getSource(),
                                   model,
                                   null,
                                   null,
                                   Msg.CYCLIC_DEP_FOR_NODE,
                                   node.getName() );
            registry.put( node, Boolean.TRUE );
        }
        for ( DMNNode dependency : node.getDependencies().values() ) {
            if ( dependency instanceof DecisionNodeImpl ) {
                detectCycles( (DecisionNodeImpl) dependency, registry, model );
            }
        }
        registry.put( node, Boolean.TRUE );
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
                DecisionServiceNode dsn = model.getDecisionServiceById(id);
                if ( bkmn != null ) {
                    node.addDependency( bkmn.getName(), bkmn );
                } else if (dsn != null) {
                    node.addDependency(dsn.getName(), dsn);
                } else {
                    MsgUtil.reportMessage( logger,
                                           DMNMessage.Severity.ERROR,
                                           kr.getRequiredKnowledge(),
                                           model,
                                           null,
                                           null,
                                          Msg.REQ_BKM_NOT_FOUND_FOR_NODE, // TODO or a DS ?
                                           id,
                                           node.getName() );
                }
            }
        }
    }

    /**
     * For the purpose of Compilation, in the DMNModel the DRGElements are stored with their full ID, so an ElementReference might reference in two forms:
     *  - #id (a local to the model ID)
     *  - namespace#id (an imported DRGElement ID)
     * This method now returns in the first case the proper ID, while leave unchanged in the latter case, in order for the ID to be reconciliable on the DMNModel. 
     */
    public static String getId(DMNElementReference er) {
        String href = er.getHref();
        return href.startsWith("#") ? href.substring(1) : href;
    }

    private DMNType buildTypeDef(DMNCompilerContext ctx, DMNModelImpl dmnModel, DMNNode node, ItemDefinition itemDef, boolean topLevel) {
        BaseDMNTypeImpl type = null;
        if ( itemDef.getTypeRef() != null ) {
            // this is a reference to an existing type, so resolve the reference
            type = (BaseDMNTypeImpl) resolveTypeRef(dmnModel, itemDef, itemDef, itemDef.getTypeRef());
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

                    Type baseFEELType = type.getFeelType();
                    if (baseFEELType instanceof BuiltInType) { // Then it is an ItemDefinition in place for "aliasing" a base FEEL type, for having type(itemDefname) I need to define its SimpleType.
                        type.setFeelType(new AliasFEELType(itemDef.getName(), (BuiltInType) baseFEELType));
                    }

                    type.setAllowedValues(null);
                    if ( allowedValuesStr != null ) {
                        List<UnaryTest> av = ctx.getFeelHelper().evaluateUnaryTests(
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
            for (ItemDefinition fieldDef : itemDef.getItemComponent()) {
                DMNCompilerHelper.checkVariableName(dmnModel, fieldDef, fieldDef.getName());
                DMNType fieldType = buildTypeDef(ctx, dmnModel, node, fieldDef, false);
                fieldType = fieldType != null ? fieldType : dmnModel.getTypeRegistry().unknown();
                compType.addField(fieldDef.getName(), fieldType);
            }
        }
        return type;
    }

    /**
     * Resolve the QName typeRef accordingly to definition of builtin (FEEL) types, local model ItemDef or imported definitions.
     * If the typeRef cannot be resolved, (FEEL) UNKNOWN is returned and an error logged using standard DMN message logging. 
     */
    public DMNType resolveTypeRef(DMNModelImpl dmnModel, NamedElement model, DMNModelInstrumentedBase localElement, QName typeRef) {
        if ( typeRef != null ) {
            QName nsAndName = getNamespaceAndName(localElement, dmnModel.getImportAliasesForNS(), typeRef, dmnModel.getNamespace());

            DMNType type = dmnModel.getTypeRegistry().resolveType(nsAndName.getNamespaceURI(), nsAndName.getLocalPart());
            if (type == null && localElement.getURIFEEL().equals(nsAndName.getNamespaceURI())) {
                if ( model instanceof Decision && ((Decision) model).getExpression() instanceof DecisionTable ) {
                    DecisionTable dt = (DecisionTable) ((Decision) model).getExpression();
                    if ( dt.getOutput().size() > 1 ) {
                        // implicitly define a type for the decision table result
                        CompositeTypeImpl compType = new CompositeTypeImpl( dmnModel.getNamespace(), model.getName()+"_Type", model.getId(), dt.getHitPolicy().isMultiHit() );
                        for ( OutputClause oc : dt.getOutput() ) {
                            DMNType fieldType = resolveTypeRef(dmnModel, model, oc, oc.getTypeRef());
                            compType.addField( oc.getName(), fieldType );
                        }
                        dmnModel.getTypeRegistry().registerType( compType );
                        return compType;
                    } else if ( dt.getOutput().size() == 1 ) {
                        return resolveTypeRef(dmnModel, model, dt.getOutput().get(0), dt.getOutput().get(0).getTypeRef());
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
                type = dmnModel.getTypeRegistry().unknown();
            }
            return type;
        }
        return dmnModel.getTypeRegistry().unknown();
    }

    /**
     * Given a typeRef in the form of prefix:localname or importalias.localname, resolves namespace and localname appropriately.
     * <br>Example: <code>feel:string</code> would be resolved as <code>http://www.omg.org/spec/FEEL/20140401, string</code>.
     * <br>Example: <code>myimport.tPerson</code> assuming an external model namespace as "http://drools.org" would be resolved as <code>http://drools.org, tPerson</code>.
     * @param localElement the local element is used to determine the namespace from the prefix if present, as in the form prefix:localname
     * @param importAliases the map of import aliases is used to determine the namespace, as in the form importalias.localname
     * @param typeRef the typeRef to be resolved.
     * @return
     */
    public static QName getNamespaceAndName(DMNModelInstrumentedBase localElement, Map<String, QName> importAliases, QName typeRef, String modelNamespace) {
        if (localElement instanceof KieDMNModelInstrumentedBase) {
            if (!typeRef.getPrefix().equals(XMLConstants.DEFAULT_NS_PREFIX)) {
                return new QName(localElement.getNamespaceURI(typeRef.getPrefix()), typeRef.getLocalPart());
            } else {
                for (Entry<String, QName> alias : importAliases.entrySet()) {
                    String prefix = alias.getKey() + ".";
                    if (typeRef.getLocalPart().startsWith(prefix)) {
                        return new QName(alias.getValue().getNamespaceURI(), typeRef.getLocalPart().replace(prefix, ""));
                    }
                }
                return new QName(localElement.getNamespaceURI(typeRef.getPrefix()), typeRef.getLocalPart());
            }
        } else {
            for (BuiltInType bi : DMNTypeRegistryV12.ITEMDEF_TYPEREF_FEEL_BUILTIN) {
                for (String biName : bi.getNames()) {
                    if (biName.equals(typeRef.getLocalPart())) {
                        return new QName(localElement.getURIFEEL(), typeRef.getLocalPart());
                    }
                }
            }
            for (Entry<String, QName> alias : importAliases.entrySet()) {
                String prefix = alias.getKey() + ".";
                if (typeRef.getLocalPart().startsWith(prefix)) {
                    return new QName(alias.getValue().getNamespaceURI(), typeRef.getLocalPart().replace(prefix, ""));
                }
            }
            return new QName(modelNamespace, typeRef.getLocalPart());
        }
    }

    public DMNCompilerConfiguration getDmnCompilerConfig() {
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