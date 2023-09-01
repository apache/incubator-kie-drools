package org.kie.dmn.backend.marshalling.v1_3.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.Group;
import org.kie.dmn.model.v1_3.TGroup;

public class GroupConverter extends ArtifactConverter {

    public static final String NAME = "name";

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        Group grp = (Group) parent;
        
        String name = reader.getAttribute(NAME);
        
        grp.setName(name);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        Group grp = (Group) parent;
        
        if (grp.getName() != null) {
            writer.addAttribute(NAME, grp.getName());
        }
    }

    public GroupConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TGroup();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(TGroup.class);
    }

}
