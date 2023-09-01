package org.kie.dmn.backend.marshalling.v1_3.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.FunctionItem;
import org.kie.dmn.model.api.InformationItem;
import org.kie.dmn.model.v1_3.TFunctionItem;

public class FunctionItemConverter extends DMNElementConverter {

    private static final String OUTPUT_TYPE_REF = "outputTypeRef";
    private static final String PARAMETERS = "parameters";

    public FunctionItemConverter(XStream xstream) {
        super( xstream );
    }

    public boolean canConvert(Class clazz) {
        return clazz.equals(TFunctionItem.class);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        FunctionItem ii = (FunctionItem) parent;

        if (PARAMETERS.equals(nodeName)) {
            ii.getParameters().add((InformationItem) child);
        } else {
            super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes( reader, parent );
        FunctionItem ii = (FunctionItem) parent;

        String typeRef = reader.getAttribute(OUTPUT_TYPE_REF);
        ii.setOutputTypeRef(MarshallingUtils.parseQNameString(typeRef));
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TFunctionItem();
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);

        FunctionItem ii = (FunctionItem) parent;

        for (InformationItem ic : ii.getParameters()) {
            writeChildrenNode(writer, context, ic, PARAMETERS);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        FunctionItem ii = (FunctionItem) parent;
        
        if (ii.getOutputTypeRef() != null) {
            writer.addAttribute(OUTPUT_TYPE_REF, MarshallingUtils.formatQName(ii.getOutputTypeRef(), ii));
        }
    }

}
