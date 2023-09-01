package org.kie.dmn.backend.marshalling.v1_3.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.api.List;
import org.kie.dmn.model.v1_3.TList;

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
            writeChildrenNode(writer, context, e, MarshallingUtils.defineExpressionNodeName(xstream, e));
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
    protected DMNModelInstrumentedBase createModelObject() {
        return new TList();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(TList.class);
    }

}
