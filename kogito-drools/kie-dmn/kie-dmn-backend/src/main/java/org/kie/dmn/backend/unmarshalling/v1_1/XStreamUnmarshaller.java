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

package org.kie.dmn.backend.unmarshalling.v1_1;

import com.sun.org.apache.xpath.internal.operations.Variable;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.AbstractCollectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.feel.model.v1_1.*;
import org.kie.dmn.unmarshalling.v1_1.Unmarshaller;

import java.io.Reader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class XStreamUnmarshaller
        implements Unmarshaller {

    private final static Pattern QNAME_PAT = Pattern.compile( "((\\{([^\\}]*)\\})?([^:]*):)?(.*)" );

    @Override
    public Definitions unmarshal(String xml) {
        return unmarshal( new StringReader( xml ) );
    }

    @Override
    public Definitions unmarshal(Reader isr) {
        try {
            XStream xStream = new XStream();
            xStream.alias( "definitions", Definitions.class );
            xStream.alias( "inputData", InputData.class );
            xStream.alias( "decision", Decision.class );
            xStream.alias( "variable", InformationItem.class );
            xStream.alias( "informationRequirement", InformationRequirement.class );
            xStream.alias( "requiredInput", DMNElementReference.class );
            xStream.alias( "literalExpression", LiteralExpression.class );
            xStream.alias( "text", String.class );

            xStream.registerConverter( new DefinitionsConverter( xStream ) );
            xStream.registerConverter( new DecisionConverter( xStream ) );
            xStream.registerConverter( new InputDataConverter( xStream ) );
            xStream.registerConverter( new InformationItemConverter( xStream ) );
            xStream.registerConverter( new InformationRequirementConverter( xStream ) );
            xStream.registerConverter( new DMNElementReferenceConverter( xStream ) );
            xStream.registerConverter( new LiteralExpressionConverter( xStream ) );

            Definitions def = (Definitions) xStream.fromXML( isr );

            return def;
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public static QName parseQNameString( String qns ) {
        if( qns != null ) {
            Matcher m = QNAME_PAT.matcher( qns );
            if( m.matches() ) {
                return new QName( m.group(3), m.group( 5 ), m.group( 4 ) );
            } else {
                return new QName( null, qns, null );
            }
        } else {
            return null;
        }
    }

    public static class DefinitionsConverter
            extends AbstractCollectionConverter
            implements Converter {
        public static final String ID                  = "id";
        public static final String LABEL               = "label";
        public static final String NAME                = "name";
        public static final String EXPRESSION_LANGUAGE = "expressionLanguage";
        public static final String TYPE_LANGUAGE       = "typeLanguage";
        public static final String NAMESPACE           = "namespace";
        public static final String EXPORTER            = "exporter";
        public static final String EXPORTER_VERSION    = "exporterVersion";

        public DefinitionsConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(
                Object object,
                HierarchicalStreamWriter writer,
                MarshallingContext context) {
        }

        public Object unmarshal(
                HierarchicalStreamReader reader,
                UnmarshallingContext context) {
            String id = reader.getAttribute( ID );
            String label = reader.getAttribute( LABEL );
            String name = reader.getAttribute( NAME );
            String exprLang = reader.getAttribute( EXPRESSION_LANGUAGE );
            String typeLang = reader.getAttribute( TYPE_LANGUAGE );
            String namespace = reader.getAttribute( NAMESPACE );
            String exporter = reader.getAttribute( EXPORTER );
            String exporterVersion = reader.getAttribute( EXPORTER_VERSION );

            Definitions def = new Definitions();
            def.setId( id );
            def.setLabel( label );
            def.setName( name );
            def.setExpressionLanguage( exprLang );
            def.setTypeLanguage( typeLang );
            def.setNamespace( namespace );
            def.setExporter( exporter );
            def.setExporterVersion( exporterVersion );

            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                Object object = readItem(
                        reader,
                        context,
                        null );
                reader.moveUp();
                if ( object instanceof InputData || object instanceof BusinessKnowledgeModel ||
                     object instanceof Decision || object instanceof KnowledgeSource ) {
                    def.getDrgElement().add( (DRGElement) object );
                }
            }
            return def;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( Definitions.class );
        }
    }

    public static class DecisionConverter
            extends AbstractCollectionConverter
            implements Converter {
        public static final String ID              = "id";
        public static final String LABEL           = "label";
        public static final String NAME            = "name";
        public static final String QUESTION        = "question";
        public static final String ALLOWED_ANSWERS = "allowedAnswers";

        public DecisionConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(
                Object object,
                HierarchicalStreamWriter writer,
                MarshallingContext context) {
        }

        public Object unmarshal(
                HierarchicalStreamReader reader,
                UnmarshallingContext context) {
            String id = reader.getAttribute( ID );
            String label = reader.getAttribute( LABEL );
            String name = reader.getAttribute( NAME );
            String question = reader.getAttribute( QUESTION );
            String allowedAnswers = reader.getAttribute( ALLOWED_ANSWERS );

            Decision dec = new Decision();
            dec.setId( id );
            dec.setLabel( label );
            dec.setName( name );
            dec.setQuestion( question );
            dec.setAllowedAnswers( allowedAnswers );

            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                Object object = readItem(
                        reader,
                        context,
                        null );
                reader.moveUp();
                if ( object instanceof InformationRequirement ) {
                    dec.getInformationRequirement().add( (InformationRequirement) object );
                } else if ( object instanceof InformationItem ) {
                    dec.setVariable( (InformationItem) object );
                } else if ( object instanceof Expression ) {
                    dec.setExpression( (Expression) object );
                }
            }
            return dec;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( Decision.class );
        }
    }

    public static class InputDataConverter
            extends AbstractCollectionConverter
            implements Converter {
        public static final String ID    = "id";
        public static final String LABEL = "label";
        public static final String NAME  = "name";

        public InputDataConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(
                Object object,
                HierarchicalStreamWriter writer,
                MarshallingContext context) {
        }

        public Object unmarshal(
                HierarchicalStreamReader reader,
                UnmarshallingContext context) {
            String id = reader.getAttribute( ID );
            String label = reader.getAttribute( LABEL );
            String name = reader.getAttribute( NAME );

            InputData idata = new InputData();
            idata.setId( id );
            idata.setLabel( label );
            idata.setName( name );

            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                Object object = readItem(
                        reader,
                        context,
                        null );
                reader.moveUp();
                if ( object instanceof InformationItem ) {
                    idata.setVariable( (InformationItem) object );
                }
            }
            return idata;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( InputData.class );
        }
    }

    public static class InformationItemConverter
            extends AbstractCollectionConverter
            implements Converter {
        public static final String ID       = "id";
        public static final String LABEL    = "label";
        public static final String NAME     = "name";
        public static final String TYPE_REF = "typeRef";


        public InformationItemConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(
                Object object,
                HierarchicalStreamWriter writer,
                MarshallingContext context) {
        }

        public Object unmarshal(
                HierarchicalStreamReader reader,
                UnmarshallingContext context) {
            String id = reader.getAttribute( ID );
            String label = reader.getAttribute( LABEL );
            String name = reader.getAttribute( NAME );
            String typeRef = reader.getAttribute( TYPE_REF );

            InformationItem var = new InformationItem();
            var.setId( id );
            var.setLabel( label );
            var.setName( name );
            var.setTypeRef( parseQNameString( typeRef ) );

            return var;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( InformationItem.class );
        }

    }

    public static class InformationRequirementConverter
            extends AbstractCollectionConverter
            implements Converter {

        public InformationRequirementConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(
                Object object,
                HierarchicalStreamWriter writer,
                MarshallingContext context) {
        }

        public Object unmarshal(
                HierarchicalStreamReader reader,
                UnmarshallingContext context) {
            InformationRequirement ir = new InformationRequirement();

            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                Object object = readItem(
                        reader,
                        context,
                        null );
                reader.moveUp();
                if ( object instanceof DMNElementReference ) {
                    ir.setRequiredInput( (DMNElementReference) object );
                }
            }
            return ir;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( InformationRequirement.class );
        }
    }

    public static class DMNElementReferenceConverter
            extends AbstractCollectionConverter
            implements Converter {

        public static final String HREF = "href";

        public DMNElementReferenceConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(
                Object object,
                HierarchicalStreamWriter writer,
                MarshallingContext context) {
        }

        public Object unmarshal(
                HierarchicalStreamReader reader,
                UnmarshallingContext context) {
            DMNElementReference er = new DMNElementReference();
            String href = reader.getAttribute( HREF );
            er.setHref( href );
            return er;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( DMNElementReference.class );
        }
    }

    public static class LiteralExpressionConverter
            extends AbstractCollectionConverter
            implements Converter {

        public static final String ID    = "id";
        public static final String LABEL = "label";
        public static final String TYPE_REF = "typeRef";
        public static final String EXPR_LANGUAGE = "expressionLanguage";

        public LiteralExpressionConverter(XStream xstream) {
            super( xstream.getMapper() );
        }

        public void marshal(
                Object object,
                HierarchicalStreamWriter writer,
                MarshallingContext context) {
        }

        public Object unmarshal(
                HierarchicalStreamReader reader,
                UnmarshallingContext context) {
            LiteralExpression le = new LiteralExpression();
            String id = reader.getAttribute( ID );
            String label = reader.getAttribute( LABEL );
            String typeRef = reader.getAttribute( TYPE_REF );
            String exprLanguage = reader.getAttribute( EXPR_LANGUAGE );
            le.setId( id );
            le.setLabel( label );
            le.setTypeRef( parseQNameString( typeRef) );
            le.setExpressionLanguage( exprLanguage );

            while ( reader.hasMoreChildren() ) {
                reader.moveDown();
                Object object = readItem(
                        reader,
                        context,
                        null );
                reader.moveUp();
                if ( object instanceof String ) {
                    le.setText( (String) object );
                }
            }

            return le;
        }

        public boolean canConvert(Class clazz) {
            return clazz.equals( LiteralExpression.class );
        }
    }
}
