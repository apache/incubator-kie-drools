/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package org.kie.dmn.core.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.conf.Option;
import org.kie.dmn.api.core.DMNCompilerConfiguration;
import org.kie.dmn.api.marshalling.v1_1.DMNExtensionRegister;
import org.kie.dmn.feel.lang.FEELProfile;

public class DMNCompilerConfigurationImpl implements DMNCompilerConfiguration {

    private List<DMNExtensionRegister> registeredExtensions = new ArrayList<>();
    private Map<String, String> properties = new HashMap<>();
    private List<DRGElementCompiler> drgElementCompilers = new ArrayList<>();
    private List<FEELProfile> feelProfiles = new ArrayList<>();

    public void addExtensions(List<DMNExtensionRegister> extensionRegisters) {
        this.registeredExtensions.addAll(extensionRegisters);
    }

    public void addExtension(DMNExtensionRegister extensionRegister) {
        this.registeredExtensions.add(extensionRegister);
    }

    public List<DMNExtensionRegister> getRegisteredExtensions() {
        return this.registeredExtensions;
    }

    public void setProperties(Map<String, String> dmnPrefs) {
        this.properties.putAll(dmnPrefs);
    }

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(this.properties);
    }

    public final <T extends Option> T getOption(Class<T> option) {
        if (RuntimeTypeCheckOption.class.equals(option)) {
            return (T) new RuntimeTypeCheckOption(properties.get(RuntimeTypeCheckOption.PROPERTY_NAME));
        }
        return null;
    }

    public void addDRGElementCompilers(List<DRGElementCompiler> drgElementCompilers) {
        this.drgElementCompilers.addAll(drgElementCompilers);
    }

    public List<DRGElementCompiler> getDRGElementCompilers() {
        return drgElementCompilers;
    }

    public List<FEELProfile> getFeelProfiles() {
        return feelProfiles;
    }

    public void addFEELProfile(FEELProfile dmnProfile) {
        this.feelProfiles.add(dmnProfile);
    }
}
