package org.kie.dmn.core.compiler.extensions;

import com.thoughtworks.xstream.XStream;
import org.kie.dmn.api.marshalling.DMNExtensionRegister;

public class FirstNameDescriptionRegister implements DMNExtensionRegister {


    @Override
    public void registerExtensionConverters(XStream xStream) {
        xStream.processAnnotations(FirstNameDescription.class);
    }
}
