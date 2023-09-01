package org.kie.dmn.backend.marshalling.v1_3.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.ImportedValues;
import org.kie.dmn.model.v1_3.TImportedValues;

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
    protected DMNModelInstrumentedBase createModelObject() {
        return new TImportedValues();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(TImportedValues.class);
    }
    
    
}
