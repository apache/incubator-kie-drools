package org.kie.dmn.backend.marshalling.v1_1.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.ImportedValues;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.v1_1.TLiteralExpression;

public class LiteralExpressionConverter
        extends ExpressionConverter {

    public static final String IMPORTED_VALUES = "importedValues";
    public static final String TEXT = "text";
    public static final String EXPR_LANGUAGE = "expressionLanguage";

    public LiteralExpressionConverter(XStream xstream) {
        super( xstream );
    }

    public boolean canConvert(Class clazz) {
        return clazz.equals(TLiteralExpression.class);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        LiteralExpression le = (LiteralExpression)parent;
        
        if( TEXT.equals( nodeName ) ) {
            le.setText( (String) child );
        } else if( IMPORTED_VALUES.equals( nodeName ) ) {
            le.setImportedValues( (ImportedValues) child );
        } else {
            super.assignChildElement( parent, nodeName, child );
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes( reader, parent );
        LiteralExpression le = (LiteralExpression) parent;
        
        String exprLanguage = reader.getAttribute( EXPR_LANGUAGE );

        le.setExpressionLanguage( exprLanguage );
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TLiteralExpression();
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        LiteralExpression le = (LiteralExpression) parent;
        
        if ( le.getText() != null ) writeChildrenNodeAsValue(writer, context, le.getText(), TEXT);
        if ( le.getImportedValues() != null ) writeChildrenNode(writer, context, le.getImportedValues(), IMPORTED_VALUES);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        LiteralExpression le = (LiteralExpression) parent;
        
        if ( le.getExpressionLanguage() != null ) writer.addAttribute(EXPR_LANGUAGE, le.getExpressionLanguage());
    }

    
}
