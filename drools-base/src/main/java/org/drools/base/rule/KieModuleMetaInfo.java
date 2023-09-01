package org.drools.base.rule;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import org.drools.base.base.XMLSupport;

public class KieModuleMetaInfo implements Serializable {

    private Map<String, TypeMetaInfo> typeMetaInfos;
    private Map<String, Set<String>> rulesByPackage;

    public KieModuleMetaInfo() { }

    public KieModuleMetaInfo(Map<String, TypeMetaInfo> typeMetaInfoMap, Map<String, Set<String>> rulesByPackage) {
        this.typeMetaInfos = typeMetaInfoMap;
        this.rulesByPackage = rulesByPackage;
    }

    public String marshallMetaInfos() {
        return XMLSupport.get().toXml(XMLSupport.options().withClassLoader(KieModuleMetaInfo.class.getClassLoader()), this);
    }

    public static KieModuleMetaInfo unmarshallMetaInfos(String s) {
        return XMLSupport.get().fromXml(XMLSupport.options().withClassLoader(KieModuleMetaInfo.class.getClassLoader()), s);
    }

    public Map<String, TypeMetaInfo> getTypeMetaInfos() {
        return typeMetaInfos;
    }

    public Map<String, Set<String>> getRulesByPackage() {
        return rulesByPackage;
    }
}
