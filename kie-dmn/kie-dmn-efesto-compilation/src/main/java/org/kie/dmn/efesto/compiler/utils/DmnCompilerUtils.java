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
import java.util.Arrays;
import java.util.Collection;
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
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.internal.io.ResourceFactory;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;

public class DmnCompilerUtils {

    static final List<String> CLEANABLE_PATTERNS = Arrays.asList("src/main/resources",
                                                                 "src/main/java",
                                                                 "src/test/resources",
                                                                 "src/test/java",
                                                                 "target/generated-resources",
                                                                 "target/classes",
                                                                 "target/test-classes");

    private DmnCompilerUtils() {
        // avoid instantiation
    }

    public static boolean hasError(List<DMNMessage> dmnMessages) {
        return dmnMessages.stream().anyMatch(dmnMessage -> dmnMessage.getLevel().equals(Message.Level.ERROR));
    }

    public static EfestoCompilationOutput getDefaultEfestoCompilationOutput(String fileName, String modelName, String modelSource, DMNModel dmnModel) {
        return new EfestoCallableOutputDMN(fileName, modelName, modelSource, dmnModel);
    }

    public static EfestoCompilationOutput getDefaultEfestoCompilationOutput(ModelLocalUriId modelLocalUriId, String modelSource, DMNModel dmnModel) {
        return new EfestoCallableOutputDMN(modelLocalUriId, modelSource, dmnModel);
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

    public static List<DMNModel> getDMNModelsFromResources(Collection<Resource> dmnResources,
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

    /**
     * This method remove unwanted preceding paths from given <code>File</code>, and always returns a <b>/</b>-separated path,
     * without leading <b>/</b>
     * @param fileToClean
     * @return
     */
    public static String getCleanedFilenameForURI(File fileToClean) {
        return getCleanedFilenameForURI(fileToClean.getPath());
    }

    /**
     * This method remove unwanted preceding paths from given <code>String</code>, and always returns a <b>/</b>-separated path,
     * without leading <b>/</b>
     * @param filenameToClean
     * @return
     */
    public static String getCleanedFilenameForURI(String filenameToClean) {
        if (filenameToClean == null) {
            return filenameToClean;
        }
        String toReturn = filenameToClean;
        for (String patternToClean : CLEANABLE_PATTERNS) {
            toReturn = getCleanedFileNameForURIByPattern(toReturn, patternToClean);
        }
        if (toReturn != null && toReturn.startsWith("/")) {
            toReturn = toReturn.substring(1);
        }
        return toReturn;
    }

    /**
     * This method remove unwanted preceding paths from given <code>String</code> and always returns a <b>/</b>-separated path,
     * @param filenameToClean
     *  @param patternToClean
     * @return
     */
    static String getCleanedFileNameForURIByPattern(String filenameToClean, String patternToClean) {
        if (filenameToClean == null) {
            return filenameToClean;
        }
        String toReturn = filenameToClean.replace(File.separator, "/"); // needed because the path could come from a file (e.g. scesim) built on a different OS
        if (toReturn.endsWith(".dmn")) {
            toReturn = toReturn.substring(0, toReturn.lastIndexOf(".dmn"));
        }
        if (toReturn.contains(patternToClean)) {
            toReturn = toReturn.substring(toReturn.indexOf(patternToClean) + patternToClean.length());
        }
        return toReturn;
    }
}
