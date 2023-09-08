/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
