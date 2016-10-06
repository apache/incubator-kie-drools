package org.kie.dmn.backend.unmarshalling.v1_1.xstream;

import java.util.List;

import org.kie.dmn.feel.model.v1_1.Expression;
import org.kie.dmn.feel.model.v1_1.InformationItem;
import org.kie.dmn.feel.model.v1_1.Relation;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class RelationConverter extends ExpressionConverter {
    public static final String EXPRESSION = "expression";
    public static final String ROW = "row";
    public static final String COLUMN = "column";

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        Relation r = (Relation) parent;
        
        if (COLUMN.equals(nodeName)) {
            r.getColumn().add((InformationItem) child);
        }
        // TODO not sure what would happen here..
        else if (ROW.equals(nodeName)) {
            
        } else if (EXPRESSION.equals(nodeName)) {
            // TODO maybe?
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
        Relation r = (Relation) parent;
        
        for (InformationItem c : r.getColumn()) {
            writeChildrenNode(writer, context, c, COLUMN);
        }
        for (List<Expression> row : r.getRow()) {
            // TODO boh.
        }
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        
        // no attributes.
    }

    public RelationConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected Object createModelObject() {
        return new Relation();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( Relation.class );
    }

}
