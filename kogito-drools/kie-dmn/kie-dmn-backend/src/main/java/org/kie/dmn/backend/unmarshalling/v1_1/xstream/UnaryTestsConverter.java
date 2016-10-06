package org.kie.dmn.backend.unmarshalling.v1_1.xstream;

import org.kie.dmn.feel.model.v1_1.UnaryTests;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class UnaryTestsConverter extends DMNElementConverter {
    public static final String TEXT = "text";
    public static final String EXPRESSION_LANGUAGE = "expressionLanguage";
    
    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        UnaryTests ut = (UnaryTests) parent;
        
        if (TEXT.equals(nodeName)) {
            ut.setText((String) child);
        } else {
            super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        UnaryTests ut = (UnaryTests) parent;

        String expressionLanguage = reader.getAttribute(EXPRESSION_LANGUAGE);
        
        ut.setExpressionLanguage(expressionLanguage);
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        UnaryTests ut = (UnaryTests) parent;

        writeChildrenNode(writer, context, ut.getText(), TEXT);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        UnaryTests ut = (UnaryTests) parent;

        if (ut.getExpressionLanguage() != null) writer.addAttribute(EXPRESSION_LANGUAGE, ut.getExpressionLanguage());
    }

    public UnaryTestsConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected Object createModelObject() {
        return new UnaryTests();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( UnaryTests.class );
    }
}
