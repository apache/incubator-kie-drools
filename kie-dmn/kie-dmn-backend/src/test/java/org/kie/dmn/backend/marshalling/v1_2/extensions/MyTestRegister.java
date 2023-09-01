package org.kie.dmn.backend.marshalling.v1_2.extensions;

import javax.xml.namespace.QName;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.QNameMap;
import org.kie.dmn.api.marshalling.DMNExtensionRegister;

public class MyTestRegister implements DMNExtensionRegister {

    @Override
    public void registerExtensionConverters(XStream xStream) {
        xStream.processAnnotations(MyKieExt.class);
        xStream.processAnnotations(MyDroolsExt.class);
    }

    @Override
    public void beforeMarshal(Object o, QNameMap qmap) {
        qmap.registerMapping(new QName("https://github.com/kiegroup/drools", "mykieext", "kie"), "mykieext");
        qmap.registerMapping(new QName("http://drools.org", "mydroolsext", "drools"), "mydroolsext");
    }

}
