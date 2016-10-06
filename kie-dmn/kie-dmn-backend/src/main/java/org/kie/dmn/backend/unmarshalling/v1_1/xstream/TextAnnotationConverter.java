package org.kie.dmn.backend.unmarshalling.v1_1.xstream;

import org.kie.dmn.feel.model.v1_1.TextAnnotation;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class TextAnnotationConverter extends ArtifactConverter {
    public static final String TEXT = "text";
    public static final String TEXT_FORMAT = "textFormat";
    
    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        TextAnnotation ta = (TextAnnotation) parent;
        
        if (TEXT.equals(nodeName)) {
            ta.setText((String) child);
        } else {
            super.assignChildElement(parent, nodeName, child);
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes(reader, parent);
        TextAnnotation ta = (TextAnnotation) parent;
        
        String textFormat = reader.getAttribute(TEXT_FORMAT);
        
        ta.setTextFormat(textFormat);
    }

    @Override
    protected void writeChildren(HierarchicalStreamWriter writer, MarshallingContext context, Object parent) {
        super.writeChildren(writer, context, parent);
        TextAnnotation ta = (TextAnnotation) parent;
        
        if (ta.getText() != null) writeChildrenNode(writer, context, ta.getText(), TEXT);
    }

    @Override
    protected void writeAttributes(HierarchicalStreamWriter writer, Object parent) {
        super.writeAttributes(writer, parent);
        TextAnnotation ta = (TextAnnotation) parent;
        
        if (ta.getTextFormat() != null) writer.addAttribute(TEXT_FORMAT, ta.getTextFormat()); 
    }

    public TextAnnotationConverter(XStream xstream) {
        super(xstream);
    }

    @Override
    protected Object createModelObject() {
        return new TextAnnotation();
    }

    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals( TextAnnotation.class );
    }

}
