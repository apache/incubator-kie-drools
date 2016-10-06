package org.kie.dmn.backend.unmarshalling.v1_1.xstream;

import org.kie.dmn.feel.model.v1_1.InputClause;
import org.kie.dmn.feel.model.v1_1.LiteralExpression;
import org.kie.dmn.feel.model.v1_1.UnaryTests;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class InputClauseConverter extends DMNElementConverter {
    public static final String INPUT_VALUES = "inputValues";
    public static final String INPUT_EXPRESSION = "inputExpression";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        InputClause ic = (InputClause) parent;
        
        if (INPUT_EXPRESSION.equals(nodeName)) {
            ic.setInputExpression((LiteralExpression) child);
        } else if (INPUT_VALUES.equals(nodeName)) {
            ic.setInputValues((UnaryTests) child);
        } else {
            super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        InputClause ic = (InputClause) parent;
        
        writeChildrenNode(writer, context, ic.getInputExpression(), INPUT_EXPRESSION);
        if (ic.getInputValues() != null) writeChildrenNode(writer, context, ic.getInputValues(), INPUT_VALUES); 
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
    }

    public InputClauseConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected Object createModelObject() {
        return new InputClause();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( InputClause.class );
    }

}
