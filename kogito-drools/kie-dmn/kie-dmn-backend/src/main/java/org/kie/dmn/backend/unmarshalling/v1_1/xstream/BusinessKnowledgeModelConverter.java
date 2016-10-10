package org.kie.dmn.backend.unmarshalling.v1_1.xstream;

import org.kie.dmn.feel.model.v1_1.AuthorityRequirement;
import org.kie.dmn.feel.model.v1_1.BusinessKnowledgeModel;
import org.kie.dmn.feel.model.v1_1.FunctionDefinition;
import org.kie.dmn.feel.model.v1_1.InformationItem;
import org.kie.dmn.feel.model.v1_1.KnowledgeRequirement;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class BusinessKnowledgeModelConverter extends DRGElementConverter {
    public static final String ENCAPSULATED_LOGIC = "encapsulatedLogic";
    public static final String VARIABLE = "variable";
    public static final String KNOWLEDGE_REQUIREMENT = "knowledgeRequirement";
    public static final String AUTHORITY_REQUIREMENT = "authorityRequirement";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        BusinessKnowledgeModel bkm = (BusinessKnowledgeModel) parent;
        
        if (ENCAPSULATED_LOGIC.equals(nodeName)) {
            bkm.setEncapsulatedLogic((FunctionDefinition) child);
        } else if (VARIABLE.equals(nodeName)) {
            bkm.setVariable((InformationItem) child);
        } else if (KNOWLEDGE_REQUIREMENT.equals(nodeName)) {
            bkm.getKnowledgeRequirement().add((KnowledgeRequirement) child);
        } else if (AUTHORITY_REQUIREMENT.equals(nodeName)) {
            bkm.getAuthorityRequirement().add((AuthorityRequirement) child);
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
        BusinessKnowledgeModel bkm = (BusinessKnowledgeModel) parent;
        
        if (bkm.getEncapsulatedLogic() != null) writeChildrenNode(writer, context, bkm.getEncapsulatedLogic(), ENCAPSULATED_LOGIC);
        if (bkm.getVariable() != null) writeChildrenNode(writer, context, bkm.getVariable(), VARIABLE);
        for (KnowledgeRequirement i : bkm.getKnowledgeRequirement()) {
            writeChildrenNode(writer, context, i, KNOWLEDGE_REQUIREMENT);
        }
        for (AuthorityRequirement a : bkm.getAuthorityRequirement()) {
            writeChildrenNode(writer, context, a, AUTHORITY_REQUIREMENT);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        
        // no attributes.
    }

    public BusinessKnowledgeModelConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected Object createModelObject() {
        return new BusinessKnowledgeModel();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( BusinessKnowledgeModel.class );
    }

}
