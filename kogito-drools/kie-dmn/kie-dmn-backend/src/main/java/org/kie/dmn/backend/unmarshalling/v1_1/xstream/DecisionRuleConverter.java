package org.kie.dmn.backend.unmarshalling.v1_1.xstream;

import org.kie.dmn.feel.model.v1_1.DecisionRule;
import org.kie.dmn.feel.model.v1_1.LiteralExpression;
import org.kie.dmn.feel.model.v1_1.UnaryTests;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class DecisionRuleConverter extends DMNElementConverter {
    public static final String OUTPUT_ENTRY = "outputEntry";
    public static final String INPUT_ENTRY = "inputEntry";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        DecisionRule dr = (DecisionRule) parent;

        if (INPUT_ENTRY.equals(nodeName)) {
            dr.getInputEntry().add((UnaryTests) child);
        } else if (OUTPUT_ENTRY.equals(nodeName)) {
            dr.getOutputEntry().add((LiteralExpression) child);
        } else {
            super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        
        // no attributes.
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        DecisionRule dr = (DecisionRule) parent;
        
        for (UnaryTests ie : dr.getInputEntry()) {
            writeChildrenNode(writer, context, ie, INPUT_ENTRY);
        }
        for (LiteralExpression oe : dr.getOutputEntry()) {
            writeChildrenNode(writer, context, oe, OUTPUT_ENTRY);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        
        // no attributes.
    }

    public DecisionRuleConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected Object createModelObject() {
        return new DecisionRule();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( DecisionRule.class );
    }

}
