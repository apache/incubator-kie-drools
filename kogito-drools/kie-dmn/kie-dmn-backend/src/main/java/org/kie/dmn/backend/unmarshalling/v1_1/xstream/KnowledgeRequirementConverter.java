package org.kie.dmn.backend.unmarshalling.v1_1.xstream;

import org.kie.dmn.feel.model.v1_1.DMNElementReference;
import org.kie.dmn.feel.model.v1_1.KnowledgeRequirement;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class KnowledgeRequirementConverter extends DMNModelInstrumentedBaseConverter {
    public static final String REQUIRED_KNOWLEDGE = "requiredKnowledge";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        KnowledgeRequirement kr = (KnowledgeRequirement) parent;
        
        if (REQUIRED_KNOWLEDGE.equals(nodeName)) {
            kr.setRequiredKnowledge((DMNElementReference) child);
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
        KnowledgeRequirement kr = (KnowledgeRequirement) parent;
        
        writeChildrenNode(writer, context, kr.getRequiredKnowledge(), REQUIRED_KNOWLEDGE);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        
        // no attributes.
    }

    public KnowledgeRequirementConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected Object createModelObject() {
        return new KnowledgeRequirement();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( KnowledgeRequirement.class );
    }

}
