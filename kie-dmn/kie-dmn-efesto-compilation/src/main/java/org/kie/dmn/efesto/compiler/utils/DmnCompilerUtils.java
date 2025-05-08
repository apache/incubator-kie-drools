/*
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
package org.kie.dmn.efesto.compiler.utils;

import java.io.File;
import java.util.Set;
import org.kie.api.builder.Message;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.compiler.DMNProfile;
import org.kie.dmn.core.compiler.RuntimeTypeCheckOption;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.efesto.compiler.model.EfestoCallableOutputDMN;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.internal.io.ResourceFactory;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;

public class DmnCompilerUtils {

    private DmnCompilerUtils() {
        // avoid instantiation
    }

    public static boolean hasError(List<DMNMessage> dmnMessages) {
        return dmnMessages.stream().anyMatch(dmnMessage -> dmnMessage.getLevel().equals(Message.Level.ERROR));
    }

    public static EfestoCompilationOutput getDefaultEfestoCompilationOutput(String nameSpace, String modelName, String modelSource, DMNModel dmnModel) {
        return new EfestoCallableOutputDMN(nameSpace, modelName, modelSource, dmnModel);
    }

    public static DMNModel getDMNModel(String modelSource) {
        Resource modelResource = ResourceFactory.newReaderResource(new StringReader(modelSource), "UTF-8");
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults().buildConfiguration()
                .fromResources(Collections.singletonList(modelResource)).getOrElseThrow(RuntimeException::new);
        return dmnRuntime.getModels().get(0);
    }

    public static List<DMNModel> getDMNModelsFromFiles(Set<File> modelFiles,
                                                       Set<DMNProfile> customDMNProfiles,
                                                       RuntimeTypeCheckOption runtimeTypeCheckOption,
                                                       ClassLoader classLoader) {
        List<Resource> modelResources = modelFiles.stream().map(ResourceFactory::newFileResource)
                .toList();
        DMNRuntimeBuilder dmnRuntimeBuilder = DMNRuntimeBuilder.fromDefaults();
        if (runtimeTypeCheckOption != null) {
            dmnRuntimeBuilder.setOption(runtimeTypeCheckOption);
        }
        if (customDMNProfiles != null) {
            customDMNProfiles.forEach(dmnRuntimeBuilder::addProfile);
        }
        DMNRuntime dmnRuntime = dmnRuntimeBuilder
                .setRootClassLoader(classLoader)
                .buildConfiguration()
                .fromResources(modelResources)
                .getOrElseThrow(RuntimeException::new);
        return dmnRuntime.getModels();
    }

    public static List<DMNModel> getDMNModelsFromResources(Set<Resource> dmnResources,
                                              Set<DMNProfile> customDMNProfiles,
                                              RuntimeTypeCheckOption runtimeTypeCheckOption,
                                              ClassLoader classLoader) {
        DMNRuntimeBuilder dmnRuntimeBuilder = DMNRuntimeBuilder.fromDefaults();
        if (runtimeTypeCheckOption != null) {
            dmnRuntimeBuilder.setOption(runtimeTypeCheckOption);
        }
        if (customDMNProfiles != null) {
            customDMNProfiles.forEach(dmnRuntimeBuilder::addProfile);
        }
        DMNRuntime dmnRuntime = dmnRuntimeBuilder
                .setRootClassLoader(classLoader)
                .buildConfiguration()
                .fromResources(dmnResources)
                .getOrElseThrow(RuntimeException::new);
        return dmnRuntime.getModels();
    }
}
