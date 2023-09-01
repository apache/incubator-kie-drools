package org.kie.dmn.backend.marshalling.v1_1.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.InformationItem;
import org.kie.dmn.model.api.InputData;
import org.kie.dmn.model.v1_1.TInputData;

public class InputDataConverter
        extends DRGElementConverter {

    private static final String VARIABLE = "variable";

    public InputDataConverter(XStream xstream) {
        super( xstream );
    }

    public boolean canConvert(Class clazz) {
        return clazz.equals(TInputData.class);
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        super.assignChildElement(parent, nodeName, child);
        InputData id = (InputData) parent;
        
        if ( VARIABLE.equals( nodeName ) ) {
            id.setVariable( (InformationItem) child );
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
        return new TInputData();
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        InputData id = (InputData) parent;
        
        if ( id.getVariable() != null ) {
            writeChildrenNode(writer, context, id.getVariable(), VARIABLE);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);

        // no attributes.
    }

    
}
