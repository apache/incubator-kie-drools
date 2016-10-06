package org.kie.dmn.backend.unmarshalling.v1_1.xstream;

import org.kie.dmn.feel.model.v1_1.Expression;
import org.kie.dmn.feel.model.v1_1.FunctionDefinition;
import org.kie.dmn.feel.model.v1_1.InformationItem;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class FunctionDefinitionConverter extends ExpressionConverter {
    public static final String EXPRESSION = "expression";
    public static final String FORMAL_PARAMETER = "formalParameter";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        FunctionDefinition fd = (FunctionDefinition) parent;
        
        if (FORMAL_PARAMETER.equals(nodeName)) {
            fd.getFormalParameter().add((InformationItem) child);
        } else if (EXPRESSION.equals(nodeName)) {
            fd.setExpression((Expression) child);
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
        FunctionDefinition fd = (FunctionDefinition) parent;
        
        for (InformationItem fparam : fd.getFormalParameter()) {
            writeChildrenNode(writer, context, fparam, FORMAL_PARAMETER);
        }
        if (fd.getExpression() != null) writeChildrenNode(writer, context, fd.getExpression(), EXPRESSION);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        
        // no attributes.
    }

    public FunctionDefinitionConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected Object createModelObject() {
        return new FunctionDefinition();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( FunctionDefinition.class );
    }

}
