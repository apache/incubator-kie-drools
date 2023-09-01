package org.kie.dmn.api.marshalling;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.QNameMap;

public interface DMNExtensionRegister {

    public void registerExtensionConverters(XStream xstream);

    default void beforeMarshal(Object o, QNameMap qmap) {
        // do nothing.
    }
}
