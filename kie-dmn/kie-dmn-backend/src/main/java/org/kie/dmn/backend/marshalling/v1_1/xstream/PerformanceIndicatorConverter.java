package org.kie.dmn.backend.marshalling.v1_1.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.PerformanceIndicator;
import org.kie.dmn.model.v1_1.TPerformanceIndicator;

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
    protected DMNModelInstrumentedBase createModelObject() {
        return new TPerformanceIndicator();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(TPerformanceIndicator.class);
    }

}
