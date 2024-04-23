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
package org.drools.compiler.kproject.models;

import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.base.base.XMLSupport;
import org.drools.util.PortablePath;
import org.drools.util.StringUtils;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;

public class KieModuleModelImpl implements KieModuleModel {

    public static final String KMODULE_FILE_NAME = "kmodule.xml";
    public static final PortablePath KMODULE_INFO_JAR_PATH = PortablePath.of("META-INF/kmodule.info");
    public static final PortablePath KMODULE_JAR_PATH = PortablePath.of("META-INF").resolve(KMODULE_FILE_NAME);
    public static final PortablePath KMODULE_SPRING_JAR_PATH = PortablePath.of("META-INF/kmodule-spring.xml");
    public static final PortablePath KMODULE_SRC_PATH = PortablePath.of("src/main/resources").resolve(KMODULE_JAR_PATH);

    private Map<String, String> confProps = new HashMap<>();
    private Map<String, KieBaseModel> kBases = new HashMap<>();

    public KieModuleModelImpl() { }

    public KieModuleModel setConfigurationProperty(String key, String value) {
        confProps.put(key, value);
        return this;
    }

    public String getConfigurationProperty(String key) {
        return confProps.get(key);
    }

    public Map<String, String> getConfigurationProperties() {
        return confProps;
    }

    public KieBaseModel newKieBaseModel() {
        return newKieBaseModel( StringUtils.uuid() );
    }

    /* (non-Javadoc)
     * @see org.kie.kModule.KieProject#addKBase(org.kie.kModule.KieBaseModelImpl)
     */
    public KieBaseModel newKieBaseModel(String name) {
        KieBaseModel kbase = new KieBaseModelImpl(this, name);
        Map<String, KieBaseModel> newMap = new HashMap<>();
        newMap.putAll( this.kBases );
        newMap.put( kbase.getName(), kbase );
        setKBases( newMap );

        return kbase;
    }

    /* (non-Javadoc)
     * @see org.kie.kModule.KieProject#removeKieBaseModel(org.kie.kModule.KieBaseModel)
     */
    public void removeKieBaseModel(String qName) {
        Map<String, KieBaseModel> newMap = new HashMap<>();
        newMap.putAll( this.kBases );
        newMap.remove( qName );
        setKBases( newMap );
    }

    public Map<String, KieBaseModel> getKieBaseModels() {
        return Collections.unmodifiableMap( kBases );
    }

    public Map<String, KieBaseModel> getRawKieBaseModels() {
        return kBases;
    }

    /* (non-Javadoc)
     * @see org.kie.kModule.KieProject#setKBases(java.util.Map)
     */
    private void setKBases(Map<String, KieBaseModel> kBases) {
        this.kBases = kBases;
    }

    void changeKBaseName(KieBaseModel kieBase, String oldName, String newName) {
        kBases.remove(oldName);
        kBases.put(newName, kieBase);
    }

    public Map<String, String> getConfProps() {
        return confProps;
    }

    public void setConfProps( Map<String, String> confProps ) {
        this.confProps = confProps;
    }

    /* (non-Javadoc)
     * @see org.kie.kModule.KieProject#toString()
     */
    @Override
    public String toString() {
        return "KieModuleModel [kbases=" + kBases + "]";
    }

    private static final String KMODULE_XSD =
            "<kmodule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" +
                    "         xmlns=\"http://www.drools.org/xsd/kmodule\""; // missed end >, so we can cater for />

    public String toXML() {
        String xml = XMLSupport.get().kieModuleMarshaller().toXML(this);
        return KMODULE_XSD + xml.substring("<kmodule".length());  // missed end >, so we can cater for />
    }

    public static KieModuleModel fromXML(InputStream kModuleStream) {
        return (KieModuleModel) XMLSupport.get().kieModuleMarshaller().fromXML(kModuleStream);
    }

    public static KieModuleModel fromXML(java.io.File kModuleFile) {
        return (KieModuleModel) XMLSupport.get().kieModuleMarshaller().fromXML(kModuleFile);
    }

    public static KieModuleModel fromXML(URL kModuleUrl) {
        return (KieModuleModel) XMLSupport.get().kieModuleMarshaller().fromXML(kModuleUrl);
    }

    public static KieModuleModel fromXML(String kModuleString) {
        return (KieModuleModel) XMLSupport.get().kieModuleMarshaller().fromXML(kModuleString);
    }
}