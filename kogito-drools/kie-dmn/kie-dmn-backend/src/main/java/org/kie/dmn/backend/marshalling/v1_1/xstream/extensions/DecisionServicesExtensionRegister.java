package org.kie.dmn.backend.marshalling.v1_1.xstream.extensions;

import javax.xml.namespace.QName;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.QNameMap;
import org.kie.dmn.api.marshalling.v1_1.DMNExtensionRegister;
import org.kie.dmn.model.v1_1.DMNModelInstrumentedBase;
import org.kie.dmn.model.v1_1.extensions.DecisionServices;

public class DecisionServicesExtensionRegister implements DMNExtensionRegister {

    @Override
    public void registerExtensionConverters(XStream xstream) {
        xstream.alias("decisionServices", DecisionServices.class);
        xstream.registerConverter(new DecisionServicesConverter(xstream));
    }

    @Override
    public void beforeMarshal(Object o, QNameMap qmap) {
        qmap.registerMapping(new QName(DMNModelInstrumentedBase.URI_KIE, "decisionServices", "drools"), "decisionServices");
    }

}
