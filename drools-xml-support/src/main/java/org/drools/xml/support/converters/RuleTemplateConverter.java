package org.drools.xml.support.converters;

import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.drools.compiler.kproject.models.RuleTemplateModelImpl;

public class RuleTemplateConverter extends AbstractXStreamConverter {

    public RuleTemplateConverter() {
        super(RuleTemplateModelImpl.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        RuleTemplateModelImpl rtm = (RuleTemplateModelImpl) value;
        writer.addAttribute( "dtable", rtm.getDtable() );
        writer.addAttribute( "template", rtm.getTemplate() );
        writer.addAttribute( "row", "" + rtm.getRow() );
        writer.addAttribute( "col", "" + rtm.getCol() );
    }

    public Object unmarshal(HierarchicalStreamReader reader, final UnmarshallingContext context) {
        RuleTemplateModelImpl rtm = new RuleTemplateModelImpl();
        rtm.setDtable(reader.getAttribute("dtable"));
        rtm.setTemplate(reader.getAttribute("template"));
        rtm.setRow(Integer.parseInt( reader.getAttribute("row")) );
        rtm.setCol(Integer.parseInt( reader.getAttribute( "col" ) ) );
        return rtm;
    }
}