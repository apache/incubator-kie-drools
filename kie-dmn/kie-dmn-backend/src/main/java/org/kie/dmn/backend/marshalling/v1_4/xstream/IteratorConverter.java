package org.kie.dmn.backend.marshalling.v1_4.xstream;

import org.kie.dmn.model.api.Iterator;
import org.kie.dmn.model.api.TypedChildExpression;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public abstract class IteratorConverter extends ExpressionConverter {

	public static final String IN = "in";
    public static final String ITERATOR_VARIABLE = "iteratorVariable";

    public IteratorConverter(XStream xstream) {
        super( xstream );
    }
    
    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
    	Iterator i = (Iterator) parent;
        
        if (IN.equals(nodeName) && child instanceof TypedChildExpression) {
            i.setIn((TypedChildExpression) child);
        } else {
        	super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes( reader, parent );
        String iteratorVariable = reader.getAttribute( ITERATOR_VARIABLE );

        if (iteratorVariable != null) {
            ((Iterator) parent).setIteratorVariable(iteratorVariable);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        Iterator e = (Iterator) parent;
        
        if (e.getId() != null) {
            writer.addAttribute(ITERATOR_VARIABLE, e.getIteratorVariable());
        }
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        Iterator i = (Iterator) parent;
        
        writeChildrenNode(writer, context, i.getIn(), IN);
    }

}
