package org.kie.dmn.backend.marshalling.v1_2.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.RuleAnnotationClause;
import org.kie.dmn.model.v1_2.TRuleAnnotationClause;

public class RuleAnnotationClauseConverter extends DMNModelInstrumentedBaseConverter {

    public static final String NAME = "name";

    public RuleAnnotationClauseConverter(XStream xstream) {
        super( xstream );
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        super.assignChildElement(parent, nodeName, child);
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes( reader, parent );

        ((RuleAnnotationClause) parent).setName(reader.getAttribute(NAME));
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);

        RuleAnnotationClause e = (RuleAnnotationClause) parent;

        if (e.getName() != null) {
            writer.addAttribute(NAME, e.getName());
        }
    }

    @Override
    protected DMNModelInstrumentedBase createModelObject() {
        return new TRuleAnnotationClause();
    }

    @Override
    public boolean canConvert(Class type) {
        return type.equals(TRuleAnnotationClause.class);
    }
}
