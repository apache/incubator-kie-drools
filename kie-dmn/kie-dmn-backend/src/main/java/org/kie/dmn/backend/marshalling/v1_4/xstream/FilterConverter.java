package org.kie.dmn.backend.marshalling.v1_4.xstream;

import org.kie.dmn.model.api.ChildExpression;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Filter;
import org.kie.dmn.model.v1_4.TChildExpression;
import org.kie.dmn.model.v1_4.TFilter;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class FilterConverter extends ExpressionConverter  {

    public static final String IN = "in";
    public static final String MATCH = "match";

    public FilterConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        Filter filter = (Filter) parent;

        if (IN.equals(nodeName)) {
            filter.setIn((ChildExpression) child);
        } else if (MATCH.equals(nodeName)) {
            filter.setMatch((ChildExpression) child);
        } else {
            super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        Filter filter = (Filter) parent;
        writeChildrenNode(writer, context, filter.getIn(), IN);
        writeChildrenNode(writer, context, filter.getMatch(), MATCH);

    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TFilter();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(TFilter.class);
    }
    
    protected void parseElements(HierarchicalStreamReader reader, UnmarshallingContext context, Object parent) {
        mvDownConvertAnotherMvUpAssignChildElement(reader, context, parent, IN, TChildExpression.class);
        mvDownConvertAnotherMvUpAssignChildElement(reader, context, parent, MATCH, TChildExpression.class);
    }

}
