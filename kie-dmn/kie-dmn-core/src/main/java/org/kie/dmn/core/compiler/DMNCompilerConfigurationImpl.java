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
package org.kie.dmn.core.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.api.conf.Option;
import org.kie.dmn.api.core.AfterGeneratingSourcesListener;
import org.kie.dmn.api.core.DMNCompilerConfiguration;
import org.kie.dmn.api.marshalling.DMNExtensionRegister;
import org.kie.dmn.core.compiler.DMNCompilerConfigurationImpl.KieDefaultDMNDecisionLogicCompilerFactory;
import org.kie.dmn.feel.lang.FEELProfile;
import org.kie.dmn.feel.util.ClassLoaderUtil;

public class DMNCompilerConfigurationImpl implements DMNCompilerConfiguration {

    private List<DMNExtensionRegister> registeredExtensions = new ArrayList<>();
    private Map<String, String> properties = new HashMap<>();
    private List<DRGElementCompiler> drgElementCompilers = new ArrayList<>();
    private List<FEELProfile> feelProfiles = new ArrayList<>();
    private ClassLoader rootClassLoader = ClassLoaderUtil.findDefaultClassLoader();
    private List<AfterGeneratingSourcesListener> listeners = new ArrayList<>();
    private Boolean deferredCompilation = false;
    private DMNDecisionLogicCompilerFactory decisionLogicCompilerFactory = new KieDefaultDMNDecisionLogicCompilerFactory();

    public void addExtensions(List<DMNExtensionRegister> extensionRegisters) {
        this.registeredExtensions.addAll(extensionRegisters);
    }

    public void addExtension(DMNExtensionRegister extensionRegister) {
        this.registeredExtensions.add(extensionRegister);
    }

    @Override
    public void addListener(AfterGeneratingSourcesListener listener) {
        listeners.add(listener);
    }

    @Override
    public List<AfterGeneratingSourcesListener> getAfterGeneratingSourcesListeners() {
        return listeners;
    }

    public List<DMNExtensionRegister> getRegisteredExtensions() {
        return this.registeredExtensions;
    }

    public void setProperties(Map<String, String> dmnPrefs) {
        this.properties.putAll(dmnPrefs);
    }

    public void setProperty(String name, String value) {
        this.properties.put(name, value);
    }

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(this.properties);
    }

    public final <T extends Option> T getOption(Class<T> option) {
        if (RuntimeTypeCheckOption.class.equals(option)) {
            return (T) new RuntimeTypeCheckOption(properties.get(RuntimeTypeCheckOption.PROPERTY_NAME));
        } else if (CoerceDecisionServiceSingletonOutputOption.class.equals(option)) {
            return (T) new CoerceDecisionServiceSingletonOutputOption(properties.get(CoerceDecisionServiceSingletonOutputOption.PROPERTY_NAME));
        } else if (ExecModelCompilerOption.class.equals(option)) {
            return (T) new ExecModelCompilerOption(properties.get(ExecModelCompilerOption.PROPERTY_NAME));
        } else if (AlphaNetworkOption.class.equals(option)) {
            return (T) new AlphaNetworkOption(properties.get(AlphaNetworkOption.PROPERTY_NAME));
        }
        throw new RuntimeException("Unknown option: " + option.toString());
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

    public ClassLoader getRootClassLoader() {
        return this.rootClassLoader;
    }

    public void setRootClassLoader(ClassLoader classLoader) {
        this.rootClassLoader = classLoader;
    }

    public boolean isUseExecModelCompiler() {
        return getOption(ExecModelCompilerOption.class).isUseExecModelCompiler();
    }

    public boolean isUseAlphaNetwork() {
        return getOption(AlphaNetworkOption.class).isUseAlphaNetwork();
    }

    public boolean isDeferredCompilation() {
        return deferredCompilation;
    }

    public void setDeferredCompilation(Boolean deferredCompilation) {
        this.deferredCompilation = deferredCompilation;
    }


    public DMNDecisionLogicCompilerFactory getDecisionLogicCompilerFactory() {
        return decisionLogicCompilerFactory;
    }

    public void setDecisionLogicCompilerFactory(DMNDecisionLogicCompilerFactory decisionLogicCompilerFactory) {
        this.decisionLogicCompilerFactory = decisionLogicCompilerFactory;
    }

    /**
     * this is the standard, kie-dmn-core, default.
     */
    public static class KieDefaultDMNDecisionLogicCompilerFactory implements DMNDecisionLogicCompilerFactory {

        @Override
        public DMNDecisionLogicCompiler newDMNDecisionLogicCompiler(DMNCompilerImpl dmnCompiler, DMNCompilerConfigurationImpl dmnCompilerConfig) {
            return DMNEvaluatorCompiler.dmnEvaluatorCompilerFactory(dmnCompiler, dmnCompilerConfig);
        }

    }
}
