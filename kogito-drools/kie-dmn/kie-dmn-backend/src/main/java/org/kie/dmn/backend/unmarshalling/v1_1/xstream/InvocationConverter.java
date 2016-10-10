package org.kie.dmn.backend.unmarshalling.v1_1.xstream;

import org.kie.dmn.feel.model.v1_1.Binding;
import org.kie.dmn.feel.model.v1_1.Expression;
import org.kie.dmn.feel.model.v1_1.Invocation;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class InvocationConverter extends ExpressionConverter {
    public static final String BINDING = "binding";
    public static final String EXPRESSION = "expression";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        Invocation i = (Invocation) parent;
        
        if (child instanceof Expression) {
            i.setExpression((Expression) child);
        } else if (BINDING.equals(nodeName)) {
            i.getBinding().add((Binding) child);
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
        Invocation i = (Invocation) parent;
        
        if (i.getExpression() != null) writeChildrenNode(writer, context, i.getExpression(), MarshallingUtils.defineExpressionNodeName(i.getExpression()));
        for (Binding b : i.getBinding()) {
            writeChildrenNode(writer, context, b, BINDING);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
    }

    public InvocationConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected Object createModelObject() {
        return new Invocation();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( Invocation.class );
    }

}
