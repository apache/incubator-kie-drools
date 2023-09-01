package org.kie.dmn.model.api.dmndi;

import java.util.List;

import org.kie.dmn.model.api.DMNModelInstrumentedBase;

public interface Style extends DMNModelInstrumentedBase {

    public Style.Extension getExtension();

    public void setExtension(Style.Extension value);

    public String getId();

    public void setId(String value);

    public static interface Extension {

        public List<Object> getAny();

    }

}
