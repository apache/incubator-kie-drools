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
package org.kie.maven.plugin.helpers;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.maven.plugin.logging.Log;
import org.drools.compiler.kie.builder.impl.FileKieModule;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieMetaInfoBuilder;
import org.drools.base.rule.KieModuleMetaInfo;
import org.drools.base.rule.TypeMetaInfo;

public class CompilerHelper {

    public void share(Map<String, Object> kieMap, InternalKieModule kModule, Log log) {
        String compilationID = getCompilationID(kieMap, log);
        shareKieObjectsWithMap(kModule, compilationID, kieMap, log);
        shareTypesMetaInfoWithMap(getTypeMetaInfo(kModule),
                                  kieMap,
                                  compilationID, log);
    }

    public String getCompilationID(Map<String, Object> kieMap, Log log) {
        Object compilationIDObj = kieMap.get("compilation.ID");
        if (compilationIDObj != null) {
            return compilationIDObj.toString();
        } else {
            log.error("compilation.ID key not present in the shared map using thread name:"
                              + Thread.currentThread().getName());
            return Thread.currentThread().getName();
        }
    }

    public void shareKieObjectsWithMap(InternalKieModule kModule, String compilationID, Map<String, Object> kieMap, Log log) {
        if (kModule != null && compilationID != null) {
            KieMetaInfoBuilder builder = new KieMetaInfoBuilder(kModule);
            KieModuleMetaInfo modelMetaInfo = builder.getKieModuleMetaInfo();
            if (modelMetaInfo != null) {
                /*Standard for the kieMap keys -> compilationID + dot + class name */
                StringBuilder sbModelMetaInfo = new StringBuilder(compilationID).append(".").append(KieModuleMetaInfo.class.getName());
                kieMap.put(sbModelMetaInfo.toString(), modelMetaInfo);
                log.info("KieModelMetaInfo available in the map shared with the Maven Embedder with key:" + sbModelMetaInfo.toString());
            }
            if (kModule != null) {
                /*Standard for the kieMap keys -> compilationID + dot + class name */
                StringBuilder sbkModule = new StringBuilder(compilationID).append(".").append(FileKieModule.class.getName());
                kieMap.put(sbkModule.toString(), kModule);
                log.info("KieModule available in the map shared with the Maven Embedder with key:" + sbkModule.toString());
            }
        }
    }

    public void shareTypesMetaInfoWithMap(Map<String, TypeMetaInfo> typesMetaInfo,
                                          Map<String, Object> kieMap,
                                          String compilationID, Log log) {
        if (typesMetaInfo != null) {
            StringBuilder sbTypes = new StringBuilder(compilationID).append(".").append(TypeMetaInfo.class.getName());
            Set<String> eventClasses = new HashSet<>();
            for (Map.Entry<String, TypeMetaInfo> item : typesMetaInfo.entrySet()) {
                if (item.getValue().isEvent()) {
                    eventClasses.add(item.getKey());
                }
            }
            if (!eventClasses.isEmpty()) {
                kieMap.put(sbTypes.toString(),
                           eventClasses);
                log.info("TypesMetaInfo keys available in the map shared with the Maven Embedder");
            }
        }
    }

    //for test
    public Map<String, TypeMetaInfo> getTypeMetaInfo(InternalKieModule kModule) {
        KieMetaInfoBuilder kb = new KieMetaInfoBuilder(kModule);
        KieModuleMetaInfo info = kb.generateKieModuleMetaInfo(null);
        Map<String, TypeMetaInfo> typesMetaInfo = info.getTypeMetaInfos();
        return typesMetaInfo;
    }
}
