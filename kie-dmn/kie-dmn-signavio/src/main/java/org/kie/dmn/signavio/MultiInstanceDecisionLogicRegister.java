package org.kie.dmn.signavio;

import com.thoughtworks.xstream.XStream;
import org.kie.dmn.api.marshalling.DMNExtensionRegister;

public class MultiInstanceDecisionLogicRegister implements DMNExtensionRegister {
    @Override
    public void registerExtensionConverters(XStream xStream) {
        xStream.processAnnotations(MultiInstanceDecisionLogic.class);
    }
}
