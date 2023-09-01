package org.kie.dmn.backend.marshalling.v1_3.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Import;
import org.kie.dmn.model.v1_3.TImport;

public class ImportConverter extends NamedElementConverter {
    public static final String NAMESPACE = "namespace";
    public static final String LOCATION_URI = "locationURI"; 
    public static final String IMPORT_TYPE = "importType";  
    
    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        super.assignChildElement(parent, nodeName, child);
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        Import i = (Import) parent;
        
        String namespace = reader.getAttribute(NAMESPACE);
        String locationUri = reader.getAttribute(LOCATION_URI);
        String importType = reader.getAttribute(IMPORT_TYPE);
        
        i.setNamespace(namespace);
        i.setLocationURI(locationUri);
        i.setImportType(importType);
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        Import i = (Import) parent;
        
        if (i.getNamespace() != null) writer.addAttribute(NAMESPACE, i.getNamespace());
        if (i.getLocationURI() != null) writer.addAttribute(LOCATION_URI, i.getLocationURI());
        if (i.getImportType() != null) writer.addAttribute(IMPORT_TYPE, i.getImportType());
    }

    public ImportConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TImport();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(TImport.class);
    }

}
