package org.kie.dmn.backend.unmarshalling.v1_1.xstream;

import org.kie.dmn.feel.model.v1_1.Expression;
import org.kie.dmn.feel.model.v1_1.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class DMNListConverter extends ExpressionConverter {

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        List list = (List) parent;
        
        if (child instanceof Expression) {
            list.getExpression().add((Expression) child);
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
        List list = (List) parent;
        
        for (Expression e : list.getExpression()) {
            writeChildrenNode(writer, context, e, MarshallingUtils.defineExpressionNodeName(e));
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        
        // no attributes.
    }

    public DMNListConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected Object createModelObject() {
        return new List();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( List.class );
    }

}
