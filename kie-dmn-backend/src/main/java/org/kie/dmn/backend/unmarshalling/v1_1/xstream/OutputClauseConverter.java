package org.kie.dmn.backend.unmarshalling.v1_1.xstream;

import org.kie.dmn.feel.model.v1_1.LiteralExpression;
import org.kie.dmn.feel.model.v1_1.OutputClause;
import org.kie.dmn.feel.model.v1_1.UnaryTests;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class OutputClauseConverter extends DMNElementConverter {
    public static final String DEFAULT_OUTPUT_ENTRY = "defaultOutputEntry";
    public static final String OUTPUT_VALUES = "outputValues";
    public static final String NAME = "name";
    public static final String TYPE_REF ="typeRef";
    
    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        OutputClause oc = (OutputClause) parent;
        
        if (OUTPUT_VALUES.equals(nodeName)) {
            oc.setOutputValues((UnaryTests) child);
        } else if (DEFAULT_OUTPUT_ENTRY.equals(nodeName)) {
            oc.setDefaultOutputEntry((LiteralExpression) child);
        } else {
            super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        OutputClause oc = (OutputClause) parent;
        
        String name = reader.getAttribute(NAME);
        String typeRefValue = reader.getAttribute(TYPE_REF);
        
        oc.setName(name);
        if (typeRefValue != null) oc.setTypeRef(MarshallingUtils.parseQNameString(typeRefValue));
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        OutputClause oc = (OutputClause) parent;
        
        if (oc.getOutputValues() != null) writeChildrenNode(writer, context, oc.getOutputValues(), OUTPUT_VALUES);
        if (oc.getDefaultOutputEntry() != null) writeChildrenNode(writer, context, oc.getDefaultOutputEntry(), DEFAULT_OUTPUT_ENTRY);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        OutputClause oc = (OutputClause) parent;
        
        if (oc.getName() != null) writer.addAttribute(NAME, oc.getName());
        if (oc.getTypeRef() != null) writer.addAttribute(TYPE_REF, MarshallingUtils.formatQName(oc.getTypeRef()));
    }

    public OutputClauseConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected Object createModelObject() {
        return new OutputClause();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( OutputClause.class );
    }

}
