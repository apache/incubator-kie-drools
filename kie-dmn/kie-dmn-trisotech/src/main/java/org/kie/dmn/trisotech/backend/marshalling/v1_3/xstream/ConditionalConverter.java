package org.kie.dmn.trisotech.backend.marshalling.v1_3.xstream;

import org.kie.dmn.backend.marshalling.v1_3.xstream.ExpressionConverter;
import org.kie.dmn.backend.marshalling.v1x.ConverterDefinesExpressionNodeName;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.trisotech.model.api.Conditional;
import org.kie.dmn.trisotech.model.api.NamedExpression;
import org.kie.dmn.trisotech.model.v1_3.TConditional;
import org.kie.dmn.trisotech.model.v1_3.TNamedExpression;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ConditionalConverter extends ExpressionConverter implements ConverterDefinesExpressionNodeName {

    public static final String IF = "if";
    public static final String THEN = "then";
    public static final String ELSE = "else";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        Conditional cond = (Conditional) parent;

        if (IF.equals(nodeName)) {
            cond.setIf(((NamedExpression) child).getExpression());
        } else if (THEN.equals(nodeName)) {
            cond.setThen(((NamedExpression) child).getExpression());
        } else if (ELSE.equals(nodeName)) {
            cond.setElse(((NamedExpression) child).getExpression());
        } else {
            super.assignChildElement(parent, nodeName, child);
        }

    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        Conditional cond = (Conditional) parent;
        writeChildrenNode(writer, context,  new TNamedExpression(IF,cond.getIf()), IF);
        writeChildrenNode(writer, context, new TNamedExpression(THEN,cond.getThen()), THEN);
        writeChildrenNode(writer, context,new TNamedExpression(ELSE,cond.getElse()), ELSE);

    }

    public ConditionalConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TConditional();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(TConditional.class);
    }

    @Override
    public String defineExpressionNodeName(Expression e) {
        return "conditional";
    }

}
