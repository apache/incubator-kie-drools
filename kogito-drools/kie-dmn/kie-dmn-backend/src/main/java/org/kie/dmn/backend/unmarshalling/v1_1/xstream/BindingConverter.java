package org.kie.dmn.backend.unmarshalling.v1_1.xstream;

import org.kie.dmn.feel.model.v1_1.Binding;
import org.kie.dmn.feel.model.v1_1.Expression;
import org.kie.dmn.feel.model.v1_1.InformationItem;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class BindingConverter extends DMNModelInstrumentedBaseConverter {
    public static final String EXPRESSION = "expression";
    public static final String PARAMETER = "parameter";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        Binding b = (Binding) parent;
        
        if (PARAMETER.equals(nodeName)) {
            b.setParameter((InformationItem) child);
        } else if (EXPRESSION.equals(nodeName)) {
            b.setExpression((Expression) child);
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
        Binding b = (Binding) parent;
        
        writeChildrenNode(writer, context, b.getParameter(), PARAMETER);
        if (b.getExpression() != null) writeChildrenNode(writer, context, b.getExpression(), EXPRESSION);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        
        // no attributes.
    }

    public BindingConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected Object createModelObject() {
        return new Binding();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( Binding.class );
    }

}
