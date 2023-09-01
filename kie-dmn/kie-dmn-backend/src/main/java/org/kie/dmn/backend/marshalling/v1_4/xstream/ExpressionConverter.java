package org.kie.dmn.backend.marshalling.v1_4.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.kie.dmn.model.api.Expression;
import org.kie.dmn.model.api.UnaryTests;

public abstract class ExpressionConverter
        extends DMNElementConverter {

    public static final String TYPE_REF = "typeRef";

    public ExpressionConverter(XStream xstream) {
        super( xstream );
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes( reader, parent );
        String typeRef = reader.getAttribute( TYPE_REF );

        if (typeRef != null) {
            ((Expression) parent).setTypeRef(MarshallingUtils.parseQNameString(typeRef));
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        Expression e = (Expression) parent;
        
        if (!(e instanceof UnaryTests) && e.getTypeRef() != null) {
            writer.addAttribute(TYPE_REF, MarshallingUtils.formatQName(e.getTypeRef(), e));
        }
    }
}
