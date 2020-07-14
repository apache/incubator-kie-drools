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

package org.kie.dmn.backend.marshalling.v1_2.xstream;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.AbstractPullReader;
import com.thoughtworks.xstream.io.xml.QNameMap;
import com.thoughtworks.xstream.io.xml.StaxDriver;
import com.thoughtworks.xstream.io.xml.StaxWriter;
import com.thoughtworks.xstream.security.TypeHierarchyPermission;
import org.kie.dmn.api.marshalling.DMNExtensionRegister;
import org.kie.dmn.api.marshalling.DMNMarshaller;
import org.kie.dmn.backend.marshalling.CustomStaxReader;
import org.kie.dmn.backend.marshalling.CustomStaxWriter;
import org.kie.dmn.backend.marshalling.v1x.DMNXStream;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase;
import org.kie.dmn.model.v1_2.TArtifact;
import org.kie.dmn.model.v1_2.TAssociation;
import org.kie.dmn.model.v1_2.TAuthorityRequirement;
import org.kie.dmn.model.v1_2.TBinding;
import org.kie.dmn.model.v1_2.TBusinessContextElement;
import org.kie.dmn.model.v1_2.TBusinessKnowledgeModel;
import org.kie.dmn.model.v1_2.TContext;
import org.kie.dmn.model.v1_2.TContextEntry;
import org.kie.dmn.model.v1_2.TDMNElement;
import org.kie.dmn.model.v1_2.TDMNElementReference;
import org.kie.dmn.model.v1_2.TDecision;
import org.kie.dmn.model.v1_2.TDecisionRule;
import org.kie.dmn.model.v1_2.TDecisionService;
import org.kie.dmn.model.v1_2.TDecisionTable;
import org.kie.dmn.model.v1_2.TDefinitions;
import org.kie.dmn.model.v1_2.TElementCollection;
import org.kie.dmn.model.v1_2.TExpression;
import org.kie.dmn.model.v1_2.TFunctionDefinition;
import org.kie.dmn.model.v1_2.TImport;
import org.kie.dmn.model.v1_2.TImportedValues;
import org.kie.dmn.model.v1_2.TInformationItem;
import org.kie.dmn.model.v1_2.TInformationRequirement;
import org.kie.dmn.model.v1_2.TInputClause;
import org.kie.dmn.model.v1_2.TInputData;
import org.kie.dmn.model.v1_2.TInvocation;
import org.kie.dmn.model.v1_2.TItemDefinition;
import org.kie.dmn.model.v1_2.TKnowledgeRequirement;
import org.kie.dmn.model.v1_2.TKnowledgeSource;
import org.kie.dmn.model.v1_2.TLiteralExpression;
import org.kie.dmn.model.v1_2.TNamedElement;
import org.kie.dmn.model.v1_2.TOrganizationUnit;
import org.kie.dmn.model.v1_2.TOutputClause;
import org.kie.dmn.model.v1_2.TPerformanceIndicator;
import org.kie.dmn.model.v1_2.TRelation;
import org.kie.dmn.model.v1_2.TRuleAnnotation;
import org.kie.dmn.model.v1_2.TRuleAnnotationClause;
import org.kie.dmn.model.v1_2.TTextAnnotation;
import org.kie.dmn.model.v1_2.TUnaryTests;
import org.kie.dmn.model.v1_2.dmndi.Bounds;
import org.kie.dmn.model.v1_2.dmndi.Color;
import org.kie.dmn.model.v1_2.dmndi.DMNDI;
import org.kie.dmn.model.v1_2.dmndi.DMNDecisionServiceDividerLine;
import org.kie.dmn.model.v1_2.dmndi.DMNDiagram;
import org.kie.dmn.model.v1_2.dmndi.DMNEdge;
import org.kie.dmn.model.v1_2.dmndi.DMNLabel;
import org.kie.dmn.model.v1_2.dmndi.DMNShape;
import org.kie.dmn.model.v1_2.dmndi.DMNStyle;
import org.kie.dmn.model.v1_2.dmndi.DiagramElement;
import org.kie.dmn.model.v1_2.dmndi.Dimension;
import org.kie.dmn.model.v1_2.dmndi.Point;
import org.kie.soup.xstream.XStreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XStreamMarshaller
        implements DMNMarshaller {

    private static Logger logger = LoggerFactory.getLogger( XStreamMarshaller.class );
    private List<DMNExtensionRegister> extensionRegisters = new ArrayList<>();


    private static StaxDriver staxDriver;
    static {
        staxDriver = new StaxDriver() {
            public AbstractPullReader createStaxReader(XMLStreamReader in) {
                return new CustomStaxReader(getQnameMap(), in);
            }
            
            public StaxWriter createStaxWriter(XMLStreamWriter out, boolean writeStartEndDocument) throws XMLStreamException {
                return new CustomStaxWriter(newQNameMap(), out, writeStartEndDocument, isRepairingNamespace(), getNameCoder());
            }

            public QNameMap newQNameMap() {
                QNameMap qmap = new QNameMap();
                configureQNameMap( qmap );
                return qmap;
            }
        };
        QNameMap qmap = new QNameMap();
        configureQNameMap( qmap );
        staxDriver.setQnameMap(qmap);
        staxDriver.setRepairingNamespace(false);
    }
    
    public static void configureQNameMap( QNameMap qmap ) {
        qmap.setDefaultNamespace(KieDMNModelInstrumentedBase.URI_DMN);
    }

    public XStreamMarshaller() {

    }

    public XStreamMarshaller (List<DMNExtensionRegister> extensionRegisters) {
        this.extensionRegisters.addAll(extensionRegisters);
    }


    @Override
    public Definitions unmarshal(String xml) {
        return unmarshal( new StringReader( xml ) );
    }

    @Override
    public Definitions unmarshal(Reader isr) {
        try {
            XStream xStream = newXStream();

            Definitions def = (Definitions) xStream.fromXML( isr );

            return def;
        } catch ( Exception e ) {
            logger.error( "Error unmarshalling DMN model from reader.", e );
        }
        return null;
    }

    @Override
    public String marshal(Object o) {
        try ( Writer writer = new StringWriter();
              CustomStaxWriter hsWriter = (CustomStaxWriter) staxDriver.createWriter(writer); ) {
            XStream xStream = newXStream();
            if ( o instanceof DMNModelInstrumentedBase ) {
                KieDMNModelInstrumentedBase base = (KieDMNModelInstrumentedBase) o;
                String dmnPrefix = base.getNsContext().entrySet().stream().filter(kv -> KieDMNModelInstrumentedBase.URI_DMN.equals(kv.getValue())).findFirst().map(Map.Entry::getKey).orElse("");
                hsWriter.getQNameMap().setDefaultPrefix( dmnPrefix );
            }
            extensionRegisters.forEach( r -> r.beforeMarshal(o, hsWriter.getQNameMap()) );
            xStream.marshal(o, hsWriter);
            hsWriter.flush();
            return writer.toString();
        } catch ( Exception e ) {
            logger.error( "Error marshalling DMN model to XML.", e );
        }
        return null;
    }

    @Override
    public void marshal(Object o, Writer out) {
        try {
            out.write( marshal( o ) );
        } catch ( Exception e ) {
            logger.error( "Error marshalling DMN model to XML.", e );
        }
    }

    private XStream newXStream() {
        XStream xStream = XStreamUtils.createNonTrustingXStream(staxDriver, Definitions.class.getClassLoader(), DMNXStream::from);
        xStream.addPermission(new TypeHierarchyPermission(QName.class));
        xStream.addPermission(new TypeHierarchyPermission(KieDMNModelInstrumentedBase.class));
        
        xStream.alias("artifact", TArtifact.class);
        xStream.alias("definitions", TDefinitions.class);
        xStream.alias("inputData", TInputData.class);
        xStream.alias("decision", TDecision.class);
        xStream.alias("variable", TInformationItem.class);
        xStream.alias("informationRequirement", TInformationRequirement.class);
        xStream.alias("requiredInput", TDMNElementReference.class);
        xStream.alias("literalExpression", TLiteralExpression.class);
        
        xStream.alias("DMNElement", TDMNElement.class);
        xStream.alias("allowedValues", TUnaryTests.class);
        xStream.alias("artifact", TArtifact.class);
        xStream.alias("association", TAssociation.class);
        xStream.alias("authorityRequirement", TAuthorityRequirement.class);
        xStream.alias("binding", TBinding.class);
        xStream.alias("businessContextElement", TBusinessContextElement.class);
        xStream.alias("businessKnowledgeModel", TBusinessKnowledgeModel.class);
        xStream.alias("column", TInformationItem.class);
        xStream.alias("context", TContext.class);
        xStream.alias("contextEntry", TContextEntry.class);
        xStream.alias("decision", TDecision.class);
        xStream.alias("decisionMade", TDMNElementReference.class);
        xStream.alias("decisionMaker", TDMNElementReference.class);
        xStream.alias("decisionOwned", TDMNElementReference.class);
        xStream.alias("decisionOwner", TDMNElementReference.class);
        xStream.alias("decisionService", TDecisionService.class);
        xStream.alias("decisionTable", TDecisionTable.class);
        xStream.alias("defaultOutputEntry", TLiteralExpression.class);
        xStream.alias("definitions", TDefinitions.class);
        xStream.alias("drgElement", TDMNElementReference.class);
        xStream.alias("elementCollection", TElementCollection.class);
        xStream.alias("encapsulatedDecision", TDMNElementReference.class);
        xStream.alias("encapsulatedLogic", TFunctionDefinition.class);
        xStream.alias("expression", TExpression.class);
        xStream.alias("formalParameter", TInformationItem.class);
        xStream.alias("functionDefinition", TFunctionDefinition.class);
        xStream.alias("impactedPerformanceIndicator", TDMNElementReference.class);
        xStream.alias("impactingDecision", TDMNElementReference.class);
        xStream.alias("import", TImport.class);
        xStream.alias("import", TImport.class);
        xStream.alias("importedElement", String.class ); // TODO where?
        xStream.alias("importedValues", TImportedValues.class);
        xStream.alias("informationItem", TInformationItem.class);
        xStream.alias("informationRequirement", TInformationRequirement.class);
        xStream.alias("input", TInputClause.class);
        xStream.alias("inputData", TInputData.class);
        xStream.alias("inputDecision", TDMNElementReference.class);
        xStream.alias("inputEntry", TUnaryTests.class);
        xStream.alias("inputExpression", TLiteralExpression.class);
        xStream.alias("inputValues", TUnaryTests.class);
        xStream.alias("invocation", TInvocation.class);
        xStream.alias("itemComponent", TItemDefinition.class);
        xStream.alias("itemDefinition", TItemDefinition.class);
        xStream.alias("knowledgeRequirement", TKnowledgeRequirement.class);
        xStream.alias("knowledgeSource", TKnowledgeSource.class);
        xStream.alias("literalExpression", TLiteralExpression.class);
        xStream.alias("namedElement", TNamedElement.class);
        xStream.alias("organizationUnit", TOrganizationUnit.class);
        xStream.alias("output", TOutputClause.class);
        xStream.alias("outputDecision", TDMNElementReference.class);
        xStream.alias("outputEntry", TLiteralExpression.class);
        xStream.alias("outputValues", TUnaryTests.class);
        xStream.alias("owner", TDMNElementReference.class);
        xStream.alias("parameter", TInformationItem.class);
        xStream.alias("performanceIndicator", TPerformanceIndicator.class);
        xStream.alias("relation", TRelation.class);
        xStream.alias("requiredAuthority", TDMNElementReference.class);
        xStream.alias("requiredDecision", TDMNElementReference.class);
        xStream.alias("requiredInput", TDMNElementReference.class);
        xStream.alias("requiredKnowledge", TDMNElementReference.class);
        xStream.alias("rule", TDecisionRule.class);
        xStream.alias("sourceRef", TDMNElementReference.class);
        xStream.alias("supportedObjective", TDMNElementReference.class);
        xStream.alias("targetRef", TDMNElementReference.class);
        xStream.alias("textAnnotation", TTextAnnotation.class);
        xStream.alias("type", String.class ); // TODO where?
        xStream.alias("typeRef", QName.class );
        xStream.alias("usingProcess", TDMNElementReference.class);
        xStream.alias("usingTask", TDMNElementReference.class);
        xStream.alias("variable", TInformationItem.class);
        xStream.alias("row", org.kie.dmn.model.v1_2.TList.class);
        xStream.alias("list", org.kie.dmn.model.v1_2.TList.class);
        xStream.alias("extensionElements", TDMNElement.TExtensionElements.class);

        // Manually imported TEXT = String
        xStream.alias( LiteralExpressionConverter.TEXT, String.class );
        // unnecessary 'text' key repetition:        xStream.alias( TextAnnotationConverter.TEXT, String.class );
        // unnecessary 'text' key repetition:        xStream.alias( UnaryTestsConverter.TEXT, String.class );
        xStream.alias( DecisionConverter.QUESTION, String.class );
        xStream.alias( DecisionConverter.ALLOWED_ANSWERS, String.class );
        xStream.alias( DMNElementConverter.DESCRIPTION, String.class );
        // unnecessary 'text' key repetition:      xStream.alias("text", xsd:string.class );
        // unnecessary 'text' key repetition:      xStream.alias("text", xsd:string.class );
        // unnecessary 'text' key repetition:      xStream.alias("text", xsd:string.class );
        //      xStream.alias("question", xsd:string.class );
        //      xStream.alias("allowedAnswers", xsd:string.class );
        //      xStream.alias("description", xsd:string.class );

        // DMN v1.2:
        // Note, to comply with NS for XStream need also to adjust entries inside DMNModelInstrumentedBaseConverter
        xStream.alias("annotation", TRuleAnnotationClause.class);
        xStream.alias("annotationEntry", TRuleAnnotation.class);
        xStream.registerConverter(new RuleAnnotationClauseConverter(xStream));
        xStream.registerConverter(new RuleAnnotationConverter(xStream));
        xStream.alias("DMNDI", DMNDI.class);
        xStream.registerConverter(new DMNDIConverter(xStream));
        xStream.alias("DMNDiagram", DMNDiagram.class);
        xStream.registerConverter(new DMNDiagramConverter(xStream));
        xStream.alias("DMNStyle", DMNStyle.class);
        xStream.registerConverter(new DMNStyleConverter(xStream));
        xStream.alias("Size", Dimension.class);
        xStream.registerConverter(new DimensionConverter(xStream));
        xStream.alias("DMNShape", DMNShape.class);
        xStream.registerConverter(new DMNShapeConverter(xStream));
        xStream.alias("FillColor", Color.class);
        xStream.alias("StrokeColor", Color.class);
        xStream.alias("FontColor", Color.class);
        xStream.registerConverter(new ColorConverter(xStream));
        xStream.alias("Bounds", Bounds.class);
        xStream.registerConverter(new BoundsConverter(xStream));
        xStream.alias("DMNLabel", DMNLabel.class);
        xStream.registerConverter(new DMNLabelConverter(xStream));
        xStream.alias("DMNEdge", DMNEdge.class);
        xStream.registerConverter(new DMNEdgeConverter(xStream));
        xStream.alias("DMNDecisionServiceDividerLine", DMNDecisionServiceDividerLine.class);
        xStream.registerConverter(new DMNDecisionServiceDividerLineConverter(xStream));
        xStream.alias("waypoint", Point.class);
        xStream.registerConverter(new PointConverter(xStream));
        xStream.alias("extension", DiagramElement.Extension.class);
        xStream.alias(DMNLabelConverter.TEXT, String.class);

        xStream.registerConverter(new AssociationConverter( xStream ) );
        xStream.registerConverter(new AuthorityRequirementConverter( xStream ) );
        xStream.registerConverter(new BindingConverter( xStream ) );
        xStream.registerConverter(new BusinessKnowledgeModelConverter( xStream ) );
        xStream.registerConverter(new ContextConverter( xStream ) );
        xStream.registerConverter(new ContextEntryConverter( xStream ) );
        xStream.registerConverter(new DecisionConverter( xStream ) );
        xStream.registerConverter(new DecisionRuleConverter( xStream ) );
        xStream.registerConverter(new DecisionServiceConverter(xStream));
        xStream.registerConverter(new DecisionTableConverter( xStream ) );
        xStream.registerConverter(new DefinitionsConverter( xStream ) );
        xStream.registerConverter(new DMNElementReferenceConverter( xStream ) );
        xStream.registerConverter(new FunctionDefinitionConverter( xStream ) );
        xStream.registerConverter(new ImportConverter( xStream ) );
        xStream.registerConverter(new ImportedValuesConverter( xStream ) );
        xStream.registerConverter(new InformationItemConverter( xStream ) );
        xStream.registerConverter(new InformationRequirementConverter( xStream ) );
        xStream.registerConverter(new InputClauseConverter( xStream ) );
        xStream.registerConverter(new InputDataConverter( xStream ) );
        xStream.registerConverter(new InvocationConverter( xStream ) );
        xStream.registerConverter(new ItemDefinitionConverter( xStream ) );
        xStream.registerConverter(new KnowledgeRequirementConverter( xStream ) );
        xStream.registerConverter(new KnowledgeSourceConverter( xStream ) );
        xStream.registerConverter(new LiteralExpressionConverter( xStream ) );
        xStream.registerConverter(new OrganizationUnitConverter( xStream ) );
        xStream.registerConverter(new OutputClauseConverter( xStream ) );
        xStream.registerConverter(new PerformanceIndicatorConverter( xStream ) );
        xStream.registerConverter(new RelationConverter( xStream ) );
        xStream.registerConverter(new TextAnnotationConverter( xStream ) );
        xStream.registerConverter(new UnaryTestsConverter( xStream ) );
        
        xStream.registerConverter(new QNameConverter());
        xStream.registerConverter(new DMNListConverter( xStream ) );
        xStream.registerConverter(new ElementCollectionConverter( xStream ) );
        xStream.registerConverter(new ExtensionElementsConverter( xStream, extensionRegisters ) );
        xStream.registerConverter(new DiagramElementExtensionConverter(xStream, extensionRegisters));
        
        xStream.ignoreUnknownElements();

        for(DMNExtensionRegister extensionRegister : extensionRegisters) {
            extensionRegister.registerExtensionConverters(xStream);
        }

        return xStream;
    }

}
