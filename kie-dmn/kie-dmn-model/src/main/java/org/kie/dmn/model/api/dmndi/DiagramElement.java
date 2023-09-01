package org.kie.dmn.model.api.dmndi;

import java.util.List;

import org.kie.dmn.model.api.DMNModelInstrumentedBase;

public interface DiagramElement extends DMNModelInstrumentedBase {

    public DiagramElement.Extension getExtension();

    public void setExtension(DiagramElement.Extension value);

    public Style getStyle();

    public void setStyle(Style value);

    public Style getSharedStyle();

    public void setSharedStyle(Style value);

    public String getId();

    public void setId(String value);

    public static interface Extension {

        public List<Object> getAny();
    }

}
