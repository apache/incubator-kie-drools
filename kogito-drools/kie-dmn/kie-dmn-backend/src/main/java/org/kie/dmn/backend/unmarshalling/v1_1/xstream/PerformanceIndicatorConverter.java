package org.kie.dmn.backend.unmarshalling.v1_1.xstream;

import org.kie.dmn.feel.model.v1_1.DMNElementReference;
import org.kie.dmn.feel.model.v1_1.PerformanceIndicator;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class PerformanceIndicatorConverter extends BusinessContextElementConverter {
    public static final String IMPACTING_DECISION = "impactingDecision";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        PerformanceIndicator pi = (PerformanceIndicator) parent;
        
        if (IMPACTING_DECISION.equals(nodeName)) {
            pi.getImpactingDecision().add((DMNElementReference) child);
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
        PerformanceIndicator pi = (PerformanceIndicator) parent;
        
        for ( DMNElementReference id : pi.getImpactingDecision() ) {
            writeChildrenNode(writer, context, id, IMPACTING_DECISION);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        
        // no attributes.
    }

    public PerformanceIndicatorConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected Object createModelObject() {
        return new PerformanceIndicator();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( PerformanceIndicator.class );
    }

}
