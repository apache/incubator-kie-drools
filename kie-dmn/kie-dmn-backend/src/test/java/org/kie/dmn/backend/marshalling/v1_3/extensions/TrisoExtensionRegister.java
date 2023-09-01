package org.kie.dmn.backend.marshalling.v1_3.extensions;

import javax.xml.namespace.QName;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.QNameMap;
import org.kie.dmn.api.marshalling.DMNExtensionRegister;

public class TrisoExtensionRegister implements DMNExtensionRegister {

    @Override
    public void registerExtensionConverters(XStream xStream) {
        xStream.processAnnotations(ProjectCharter.class);
    }

    @Override
    public void beforeMarshal(Object o, QNameMap qmap) {
        qmap.registerMapping(new QName("http://www.trisotech.com/2015/triso/modeling", "ProjectCharter", "triso"), "ProjectCharter");
        qmap.registerMapping(new QName("http://www.trisotech.com/2015/triso/modeling", "projectGoals", "triso"), "projectGoals");
        qmap.registerMapping(new QName("http://www.trisotech.com/2015/triso/modeling", "projectChallenges", "triso"), "projectChallenges");
        qmap.registerMapping(new QName("http://www.trisotech.com/2015/triso/modeling", "projectStakeholders", "triso"), "projectStakeholders");
    }

}
