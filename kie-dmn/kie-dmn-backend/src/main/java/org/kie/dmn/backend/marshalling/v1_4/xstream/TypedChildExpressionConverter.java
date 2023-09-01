package org.kie.dmn.backend.marshalling.v1_4.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import org.kie.dmn.model.api.DMNModelInstrumentedBase;
import org.kie.dmn.model.api.TypedChildExpression;
import org.kie.dmn.model.v1_4.TTypedChildExpression;

public class TypedChildExpressionConverter extends ChildExpressionConverter {

    public static final String TYPE_REF = "typeRef";

    public TypedChildExpressionConverter(XStream xstream) {
        super( xstream );
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes( reader, parent );
        String typeRef = reader.getAttribute( TYPE_REF );

        if (typeRef != null) {
            ((TypedChildExpression) parent).setTypeRef(typeRef);
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        TypedChildExpression e = (TypedChildExpression) parent;
        
        if (e.getTypeRef() != null) {
            writer.addAttribute(TYPE_REF, e.getTypeRef());
        }
    }

	@Override
	protected DMNModelInstrumentedBase createModelObject() {
		return new TTypedChildExpression();
	}

	@Override
	public boolean canConvert(Class type) {
		return type.equals(TTypedChildExpression.class);
	}
}
