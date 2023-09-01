package org.kie.dmn.trisotech.backend.marshalling.v1_3.xstream;

import org.kie.dmn.backend.marshalling.v1_3.xstream.DMNModelInstrumentedBaseConverter;
import org.kie.dmn.backend.marshalling.v1_3.xstream.MarshallingUtils;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.trisotech.model.api.NamedExpression;
import org.kie.dmn.trisotech.model.v1_3.TNamedExpression;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class NamedExpressionConverter extends DMNModelInstrumentedBaseConverter {

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        NamedExpression namedExp = (NamedExpression) parent;

        if (child instanceof Expression) {
            namedExp.setExpression((Expression) child);
            namedExp.setName(nodeName);
        } else
            super.assignChildElement(parent, nodeName, child);
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        NamedExpression exp = (NamedExpression) parent;
        String typeRef = reader.getAttribute("typeRef");
        exp.setTypeRef(MarshallingUtils.parseQNameString(typeRef));
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        NamedExpression namedExp = (NamedExpression) parent;
        writeChildrenNode(writer, context, namedExp.getExpression(), MarshallingUtils.defineExpressionNodeName(xstream, namedExp.getExpression()));
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        NamedExpression exp = (NamedExpression) parent;
        if (exp.getTypeRef() != null) {
            writer.addAttribute("typeRef", MarshallingUtils.formatQName(exp.getTypeRef(), exp));
        }

    }

    public NamedExpressionConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TNamedExpression();
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(TNamedExpression.class);
    }

}
