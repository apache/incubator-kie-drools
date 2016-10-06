package org.kie.dmn.backend.unmarshalling.v1_1.xstream;

import org.kie.dmn.feel.model.v1_1.AuthorityRequirement;
import org.kie.dmn.feel.model.v1_1.DMNElementReference;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class AuthorityRequirementConverter extends DMNModelInstrumentedBaseConverter {
    public static final String REQUIRED_AUTHORITY = "requiredAuthority";
    public static final String REQUIRED_INPUT = "requiredInput";
    public static final String REQUIRED_DECISION = "requiredDecision";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        AuthorityRequirement ar = (AuthorityRequirement) parent;
        
        if (REQUIRED_DECISION.equals(nodeName)) {
            ar.setRequiredDecision( (DMNElementReference) child );
        } else if (REQUIRED_INPUT.equals(nodeName)) {
            ar.setRequiredInput( (DMNElementReference) child );
        } else if (REQUIRED_AUTHORITY.equals(nodeName)) {
            ar.setRequiredAuthority( (DMNElementReference) child );
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
        AuthorityRequirement ar = (AuthorityRequirement) parent;
        
        if (ar.getRequiredDecision() != null) writeChildrenNode(writer, context, ar.getRequiredDecision(), REQUIRED_DECISION); 
        // TODO or if else?
        if (ar.getRequiredInput() != null) writeChildrenNode(writer, context, ar.getRequiredInput(), REQUIRED_INPUT);
        if (ar.getRequiredAuthority() != null) writeChildrenNode(writer, context, ar.getRequiredAuthority(), REQUIRED_AUTHORITY);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);

        // no attributes.
    }

    public AuthorityRequirementConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected Object createModelObject() {
        return new AuthorityRequirement();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( AuthorityRequirement.class );
    }
}
