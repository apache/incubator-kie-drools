package org.kie.dmn.backend.marshalling.v1_4.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.Association;
import org.kie.dmn.model.api.AssociationDirection;
import org.kie.dmn.model.api.DMNElementReference;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_4.TAssociation;

public class AssociationConverter extends ArtifactConverter {
    public static final String TARGET_REF = "targetRef";
    public static final String SOURCE_REF = "sourceRef";
    public static final String ASSOCIATION_DIRECTION = "associationDirection";

    public AssociationConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TAssociation();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(TAssociation.class);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        Association a = (Association) parent;
        
        if( SOURCE_REF.equals( nodeName ) ) {
            a.setSourceRef( (DMNElementReference) child );
        } else if( TARGET_REF.equals( nodeName ) ) {
            a.setTargetRef( (DMNElementReference) child );
        } else {
            super.assignChildElement( parent, nodeName, child );
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        Association a = (Association) parent;
        
        String associationDirectionValue = reader.getAttribute(ASSOCIATION_DIRECTION);
        
        if (associationDirectionValue != null) a.setAssociationDirection(AssociationDirection.fromValue(associationDirectionValue));
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        Association a = (Association) parent;
        
        writeChildrenNode(writer, context, a.getSourceRef(), SOURCE_REF);
        writeChildrenNode(writer, context, a.getTargetRef(), TARGET_REF);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        Association a = (Association) parent;
        
        if (a.getAssociationDirection() != null) writer.addAttribute(ASSOCIATION_DIRECTION, a.getAssociationDirection().value());
    }
}
