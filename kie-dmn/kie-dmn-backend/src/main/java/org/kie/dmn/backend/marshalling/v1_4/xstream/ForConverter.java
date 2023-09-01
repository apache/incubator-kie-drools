package org.kie.dmn.backend.marshalling.v1_4.xstream;

import org.kie.dmn.model.api.ChildExpression;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.For;
import org.kie.dmn.model.v1_4.TChildExpression;
import org.kie.dmn.model.v1_4.TFor;
import org.kie.dmn.model.v1_4.TTypedChildExpression;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ForConverter extends IteratorConverter {

	public static final String RETURN = "return";

    public ForConverter(XStream xstream) {
        super( xstream );
    }
    
    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
    	For i = (For) parent;
        
        if (RETURN.equals(nodeName) && child instanceof ChildExpression) {
            i.setReturn((ChildExpression) child);
        } else {
        	super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        For i = (For) parent;
        
        writeChildrenNode(writer, context, i.getReturn(), RETURN);
    }

	@Override
	protected DMNModelInstrumentedBase createModelObject() {
		return new TFor();
	}

	@Override
	public boolean canConvert(Class type) {
		return type.equals(TFor.class);
	}
	
    protected void parseElements(HierarchicalStreamReader reader, UnmarshallingContext context, Object parent) {
        mvDownConvertAnotherMvUpAssignChildElement(reader, context, parent, IN, TTypedChildExpression.class);
        mvDownConvertAnotherMvUpAssignChildElement(reader, context, parent, RETURN, TChildExpression.class);
    }

}
