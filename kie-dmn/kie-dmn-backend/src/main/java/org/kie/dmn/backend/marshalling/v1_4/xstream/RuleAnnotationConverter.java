package org.kie.dmn.backend.marshalling.v1_4.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.RuleAnnotation;
import org.kie.dmn.model.v1_4.TRuleAnnotation;

public class RuleAnnotationConverter extends DMNModelInstrumentedBaseConverter {

    public static final String TEXT = "text";

    public RuleAnnotationConverter(XStream xstream) {
        super( xstream );
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        super.assignChildElement(parent, nodeName, child);
        RuleAnnotation e = (RuleAnnotation) parent;

        if (TEXT.equals(nodeName)) {
            e.setText((String) child);
        } else {
            super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes( reader, parent );

        // no attributes.
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        RuleAnnotation r = (RuleAnnotation) parent;

        writeChildrenNode(writer, context, r.getText(), TEXT);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);

        // no attributes.
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TRuleAnnotation();
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(TRuleAnnotation.class);
    }
}
