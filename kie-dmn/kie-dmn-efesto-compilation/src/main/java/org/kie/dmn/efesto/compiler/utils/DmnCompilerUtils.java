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
package org.kie.dmn.efesto.compiler.utils;

import org.kie.api.builder.Message;
import org.kie.api.io.Resource;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.internal.utils.DMNRuntimeBuilder;
import org.kie.dmn.efesto.compiler.model.EfestoCallableOutputDMN;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.internal.io.ResourceFactory;

import java.io.StringReader;
import java.util.Collections;
import java.util.List;

public class DmnCompilerUtils {

    public static boolean hasError(List<DMNMessage> dmnMessages) {
        return dmnMessages.stream().anyMatch(dmnMessage -> dmnMessage.getLevel().equals(Message.Level.ERROR));
    }

    public static EfestoCompilationOutput getDefaultEfestoCompilationOutput(String fileName, String modelName, String modelSource) {
        return new EfestoCallableOutputDMN(fileName, modelName, modelSource);
    }

    public static DMNModel getDMNModel(String modelSource) {
        Resource modelResource = ResourceFactory.newReaderResource(new StringReader(modelSource), "UTF-8");
        DMNRuntime dmnRuntime = DMNRuntimeBuilder.fromDefaults().buildConfiguration()
                .fromResources(Collections.singletonList(modelResource)).getOrElseThrow(RuntimeException::new);
        return dmnRuntime.getModels().get(0);
    }
}
