package org.kie.dmn.backend.unmarshalling.v1_1.xstream;

import org.kie.dmn.feel.model.v1_1.ImportedValues;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ImportedValuesConverter extends ImportConverter {
    public static final String IMPORTED_ELEMENT = "importedElement";
    public static final String EXPRESSION_LANGUAGE = "expressionLanguage";

    public ImportedValuesConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        ImportedValues iv = (ImportedValues) parent;
        
        if (IMPORTED_ELEMENT.equals(nodeName)) {
            iv.setImportedElement((String) child);
        } else {
            super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        ImportedValues iv = (ImportedValues) parent;
        
        String expressionLanguage = reader.getAttribute(EXPRESSION_LANGUAGE);
        
        iv.setExpressionLanguage(expressionLanguage);
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        ImportedValues iv = (ImportedValues) parent;
        
        writeChildrenNode(writer, context, iv.getImportedElement(), IMPORTED_ELEMENT);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        ImportedValues iv = (ImportedValues) parent;
        
        if (iv.getExpressionLanguage() != null) writer.addAttribute(EXPRESSION_LANGUAGE, iv.getExpressionLanguage());
    }

    @Override
    protected Object createModelObject() {
        return new ImportedValues();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( ImportedValues.class );
    }
    
    
}
