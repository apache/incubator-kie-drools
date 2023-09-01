package org.kie.dmn.backend.marshalling.v1_4.xstream;

import org.kie.dmn.model.api.ChildExpression;
import org.kie.dmn.model.api.Conditional;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_4.TChildExpression;
import org.kie.dmn.model.v1_4.TConditional;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ConditionalConverter extends ExpressionConverter {

    public static final String IF = "if";
    public static final String THEN = "then";
    public static final String ELSE = "else";
    
    public ConditionalConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        Conditional cond = (Conditional) parent;

        if (IF.equals(nodeName)) {
            cond.setIf((ChildExpression) child);
        } else if (THEN.equals(nodeName)) {
            cond.setThen((ChildExpression) child);
        } else if (ELSE.equals(nodeName)) {
            cond.setElse((ChildExpression) child);
        } else {
            super.assignChildElement(parent, nodeName, child);
        }

    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        Conditional cond = (Conditional) parent;
        writeChildrenNode(writer, context, cond.getIf(), IF);
        writeChildrenNode(writer, context, cond.getThen(), THEN);
        writeChildrenNode(writer, context, cond.getElse(), ELSE);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TConditional();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(TConditional.class);
    }
    
    protected void parseElements(HierarchicalStreamReader reader, UnmarshallingContext context, Object parent) {
        mvDownConvertAnotherMvUpAssignChildElement(reader, context, parent, IF, TChildExpression.class);
        mvDownConvertAnotherMvUpAssignChildElement(reader, context, parent, THEN, TChildExpression.class);
        mvDownConvertAnotherMvUpAssignChildElement(reader, context, parent, ELSE, TChildExpression.class);
    }

}
