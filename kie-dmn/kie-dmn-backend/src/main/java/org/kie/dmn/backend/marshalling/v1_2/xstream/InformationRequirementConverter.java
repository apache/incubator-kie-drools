package org.kie.dmn.backend.marshalling.v1_2.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.InformationRequirement;
import org.kie.dmn.model.v1_2.TInformationRequirement;

public class InformationRequirementConverter extends DMNElementConverter {

    private static final String REQUIRED_INPUT    = "requiredInput";
    private static final String REQUIRED_DECISION = "requiredDecision";

    public InformationRequirementConverter(XStream xstream) {
        super( xstream );
    }

    public boolean canConvert(Class clazz) {
        return clazz.equals(TInformationRequirement.class);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        InformationRequirement ir = (InformationRequirement) parent;
        
        if ( REQUIRED_INPUT.equals( nodeName ) ) {
            ir.setRequiredInput( (DMNElementReference) child );
        } else if ( REQUIRED_DECISION.equals( nodeName ) ) {
            ir.setRequiredDecision( (DMNElementReference) child );
        } else {
            super.assignChildElement( parent, nodeName, child );
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes( reader, parent );
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TInformationRequirement();
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        InformationRequirement ir = (InformationRequirement) parent;
        
        if ( ir.getRequiredDecision() != null ) {
            writeChildrenNode(writer, context, ir.getRequiredDecision(), REQUIRED_DECISION);
        }
        if ( ir.getRequiredInput() != null ) {
            writeChildrenNode(writer, context, ir.getRequiredInput(), REQUIRED_INPUT);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        
        // no attributes.
    }

    

}
