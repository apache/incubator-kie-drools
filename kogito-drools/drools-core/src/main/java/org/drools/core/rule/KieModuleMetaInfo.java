package org.drools.core.rule;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.util.List;
import java.util.Map;

public class KieModuleMetaInfo {
    private static final XStream xStream = new XStream(new DomDriver());

    private final Map<String, TypeMetaInfo> typeMetaInfos;
    private final Map<String, List<String>> rulesByKieBase;

    public KieModuleMetaInfo(Map<String, TypeMetaInfo> typeMetaInfoMap, Map<String, List<String>> rulesByKieBase) {
        this.typeMetaInfos = typeMetaInfoMap;
        this.rulesByKieBase = rulesByKieBase;
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

    public Map<String, List<String>> getRulesByKieBase() {
        return rulesByKieBase;
    }
}
