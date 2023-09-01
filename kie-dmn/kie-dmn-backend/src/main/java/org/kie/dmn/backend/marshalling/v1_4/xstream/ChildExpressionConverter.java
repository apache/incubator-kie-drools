package org.kie.dmn.backend.marshalling.v1_4.xstream;

import org.kie.dmn.model.api.ChildExpression;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.v1_4.TChildExpression;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ChildExpressionConverter extends DMNModelInstrumentedBaseConverter {

    public static final String ID = "id";

    public ChildExpressionConverter(XStream xstream) {
        super( xstream );
    }
    
    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
    	ChildExpression i = (ChildExpression) parent;
        
        if (child instanceof Expression) {
            i.setExpression((Expression) child);
        } else {
        	super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes( reader, parent );
        String id = reader.getAttribute( ID );

        if (id != null) {
            ((ChildExpression) parent).setId(id);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        ChildExpression e = (ChildExpression) parent;
        
        if (e.getId() != null) {
            writer.addAttribute(ID, e.getId());
        }
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        ChildExpression i = (ChildExpression) parent;
        
        writeChildrenNode(writer, context, i.getExpression(), MarshallingUtils.defineExpressionNodeName(xstream, i.getExpression()));
    }

	@Override
	protected DMNModelInstrumentedBase createModelObject() {
		return new TChildExpression();
	}

	@Override
	public boolean canConvert(Class type) {
		return type.equals(TChildExpression.class);
	}

}
