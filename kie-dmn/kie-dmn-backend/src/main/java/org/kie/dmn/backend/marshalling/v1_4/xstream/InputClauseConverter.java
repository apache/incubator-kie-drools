package org.kie.dmn.backend.marshalling.v1_4.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.InputClause;
import org.kie.dmn.model.api.LiteralExpression;
import org.kie.dmn.model.api.UnaryTests;
import org.kie.dmn.model.v1_4.TInputClause;

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
    protected DMNModelInstrumentedBase createModelObject() {
        return new TInputClause();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(TInputClause.class);
    }

}
