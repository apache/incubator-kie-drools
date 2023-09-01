package org.kie.dmn.backend.marshalling.v1_4.xstream;

import org.kie.dmn.model.api.ChildExpression;
import org.kie.dmn.model.api.Quantified;
import org.kie.dmn.model.v1_4.TChildExpression;
import org.kie.dmn.model.v1_4.TTypedChildExpression;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public abstract class QuantifiedConverter extends IteratorConverter {

	public static final String SATISFIES = "satisfies";

    public QuantifiedConverter(XStream xstream) {
        super( xstream );
    }
    
    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
    	Quantified i = (Quantified) parent;
        
        if (SATISFIES.equals(nodeName) && child instanceof ChildExpression) {
            i.setSatisfies((ChildExpression) child);
        } else {
        	super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        Quantified i = (Quantified) parent;
        
        writeChildrenNode(writer, context, i.getSatisfies(), SATISFIES);
    }

    protected void parseElements(HierarchicalStreamReader reader, UnmarshallingContext context, Object parent) {
        mvDownConvertAnotherMvUpAssignChildElement(reader, context, parent, IN, TTypedChildExpression.class);
        mvDownConvertAnotherMvUpAssignChildElement(reader, context, parent, SATISFIES, TChildExpression.class);
    }

}
