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

package org.kie.dmn.backend.marshalling.v1_1.xstream;

import org.kie.dmn.model.v1_1.*;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class DefinitionsConverter
        extends NamedElementConverter {
    private static final String EXPRESSION_LANGUAGE = "expressionLanguage";
    private static final String TYPE_LANGUAGE       = "typeLanguage";
    private static final String NAMESPACE           = "namespace";
    private static final String EXPORTER            = "exporter";
    private static final String EXPORTER_VERSION    = "exporterVersion";
    
    public static final String IMPORT = "import";
    public static final String ITEM_DEFINITION = "itemDefinition";
    public static final String DRG_ELEMENT = "drgElement";
    public static final String ARTIFACT = "artifact";
    public static final String ELEMENT_COLLECTION = "elementCollection";
    public static final String BUSINESS_CONTEXT_ELEMENT = "businessContextElement";

    public DefinitionsConverter(XStream xstream) {
        super( xstream );
    }

    public boolean canConvert(Class clazz) {
        return clazz.equals( Definitions.class );
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        Definitions def = (Definitions) parent;
        if ( IMPORT.equals(nodeName) ) {
            def.getImport().add((Import) child);
        } else if (ITEM_DEFINITION.equals(nodeName)) {
            def.getItemDefinition().add((ItemDefinition) child);
        } else if (child instanceof DRGElement) {
            def.getDrgElement().add( (DRGElement) child );
        } else if (child instanceof Artifact) {
            def.getArtifact().add((Artifact) child);
        } else if (ELEMENT_COLLECTION.equals(nodeName)) {
            def.getElementCollection().add((ElementCollection) child);
        } else if (child instanceof BusinessContextElement ) {
            def.getBusinessContextElement().add((BusinessContextElement) child);
        } else {
            super.assignChildElement( def, nodeName, child );
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes( reader, parent );
        Definitions def = (Definitions) parent;

        String exprLang = reader.getAttribute( EXPRESSION_LANGUAGE );
        String typeLang = reader.getAttribute( TYPE_LANGUAGE ); 
        String namespace = reader.getAttribute( NAMESPACE );
        String exporter = reader.getAttribute( EXPORTER );
        String exporterVersion = reader.getAttribute( EXPORTER_VERSION );

        def.setExpressionLanguage( exprLang );
        def.setTypeLanguage( typeLang );
        def.setNamespace( namespace );
        def.setExporter( exporter );
        def.setExporterVersion( exporterVersion );
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new Definitions();
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        Definitions def = (Definitions) parent;
        
        for ( Import i : def.getImport() ) {
            writeChildrenNode(writer, context, i, IMPORT);
        }
        for ( ItemDefinition id : def.getItemDefinition() ) {
            writeChildrenNode(writer, context, id, ITEM_DEFINITION);
        }
        for ( DRGElement e : def.getDrgElement() ) {
            String nodeName = DRG_ELEMENT;
            if (e instanceof BusinessKnowledgeModel) {
                nodeName = "businessKnowledgeModel";
            } else if (e instanceof Decision) {
                nodeName = "decision";
            } else if (e instanceof InputData) {
                nodeName = "inputData";
            } else if (e instanceof KnowledgeSource) {
                nodeName = "knowledgeSource";
            }
            writeChildrenNode(writer, context, e, nodeName);
        }
        for ( Artifact a : def.getArtifact() ) {
            String nodeName = ARTIFACT;
            if (a instanceof Association) {
                nodeName = "association";
            } else if (a instanceof TextAnnotation) {
                nodeName = "textAnnotation";
            }
            writeChildrenNode(writer, context, a, nodeName);
        }
        for ( ElementCollection ec : def.getElementCollection() ) {
            writeChildrenNode(writer, context, ec, ELEMENT_COLLECTION);
        }
        for ( BusinessContextElement bce : def.getBusinessContextElement() ) {
            String nodeName = BUSINESS_CONTEXT_ELEMENT;
            if (bce instanceof OrganizationUnit) {
                nodeName = "organizationUnit";
            } else if (bce instanceof PerformanceIndicator) {
                nodeName = "performanceIndicator";
            }
            writeChildrenNode(writer, context, bce, nodeName);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        Definitions def = (Definitions) parent;
        
        if (def.getExpressionLanguage() != null) writer.addAttribute( EXPRESSION_LANGUAGE , def.getExpressionLanguage() );
        if (def.getTypeLanguage() != null) writer.addAttribute( TYPE_LANGUAGE, def.getTypeLanguage() );
        if (def.getNamespace() != null) writer.addAttribute( NAMESPACE, def.getNamespace());
        if (def.getExporter() != null) writer.addAttribute( EXPORTER, def.getExporter() );
        if (def.getExporterVersion() != null) writer.addAttribute( EXPORTER_VERSION, def.getExporterVersion());
    }
}
