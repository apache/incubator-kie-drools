package org.kie.dmn.backend.unmarshalling.v1_1.xstream;

import org.kie.dmn.feel.model.v1_1.DMNElementReference;
import org.kie.dmn.feel.model.v1_1.OrganizationUnit;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class OrganizationUnitConverter extends BusinessContextElementConverter {
    public static final String DECISION_OWNED = "decisionOwned";
    public static final String DECISION_MADE = "decisionMade";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        OrganizationUnit ou = (OrganizationUnit) parent;
        
        if (DECISION_MADE.equals(nodeName)) {
            ou.getDecisionMade().add((DMNElementReference) child);
        } else if (DECISION_OWNED.equals(nodeName)) {
            ou.getDecisionOwned().add((DMNElementReference) child);
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
        OrganizationUnit ou = (OrganizationUnit) parent;
        
        for (DMNElementReference dm : ou.getDecisionMade()) {
            writeChildrenNode(writer, context, dm, DECISION_MADE);
        }
        for (DMNElementReference downed : ou.getDecisionOwned()) {
            writeChildrenNode(writer, context, downed, DECISION_OWNED);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        
        // no attributes.
    }

    public OrganizationUnitConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected Object createModelObject() {
        return new OrganizationUnit();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( OrganizationUnit.class );
    }

}
