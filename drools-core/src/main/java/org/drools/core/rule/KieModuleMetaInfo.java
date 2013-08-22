package org.drools.core.rule;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class KieModuleMetaInfo {
    private static final XStream xStream = new XStream(new DomDriver());

    private Map<String, TypeMetaInfo> typeMetaInfos;
    private Map<String, Set<String>> rulesByPackage;

    public KieModuleMetaInfo() { }

    public KieModuleMetaInfo(Map<String, TypeMetaInfo> typeMetaInfoMap, Map<String, Set<String>> rulesByPackage) {
        this.typeMetaInfos = typeMetaInfoMap;
        this.rulesByPackage = rulesByPackage;
    }

    public String marshallMetaInfos() {
        return xStream.toXML(this);
    }

    public static KieModuleMetaInfo unmarshallMetaInfos(String s) {
        return (KieModuleMetaInfo)xStream.fromXML(s);
    }

    public Map<String, TypeMetaInfo> getTypeMetaInfos() {
        return typeMetaInfos;
    }

    public Map<String, Set<String>> getRulesByPackage() {
        return rulesByPackage;
    }
}
