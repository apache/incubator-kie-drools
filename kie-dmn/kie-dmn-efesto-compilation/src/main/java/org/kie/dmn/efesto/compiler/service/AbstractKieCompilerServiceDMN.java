/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.efesto.compiler.service;

import java.util.Arrays;
import org.kie.dmn.api.identifiers.DmnIdFactory;
import org.kie.dmn.api.identifiers.KieDmnComponentRoot;
import org.kie.dmn.api.identifiers.LocalCompilationSourceIdDmn;
import org.kie.dmn.core.compiler.profiles.ExtendedDMNProfile;
import org.kie.dmn.validation.DMNValidator;
import org.kie.dmn.validation.DMNValidatorFactory;
import org.kie.efesto.common.api.identifiers.EfestoAppRoot;
import org.kie.efesto.common.api.model.EfestoCompilationContext;
import org.kie.efesto.common.core.storage.ContextStorage;
import org.kie.efesto.compilationmanager.api.model.EfestoCompilationOutput;
import org.kie.efesto.compilationmanager.api.service.KieCompilerService;


public abstract class AbstractKieCompilerServiceDMN implements KieCompilerService<EfestoCompilationOutput, EfestoCompilationContext> {

    static final DMNValidator validator = DMNValidatorFactory.newValidator(Arrays.asList(new ExtendedDMNProfile()));

    @Override
    public boolean hasCompilationSource(String fileName) {
        LocalCompilationSourceIdDmn localCompilationSourceIdDmn = new EfestoAppRoot()
                .get(KieDmnComponentRoot.class)
                .get(DmnIdFactory.class)
                .get(fileName);
        return ContextStorage.getEfestoCompilationSource(localCompilationSourceIdDmn) != null;
    }

    @Override
    public String getCompilationSource(String fileName) {
        LocalCompilationSourceIdDmn localCompilationSourceIdDmn = new EfestoAppRoot()
                .get(KieDmnComponentRoot.class)
                .get(DmnIdFactory.class)
                .get(fileName);
        return ContextStorage.getEfestoCompilationSource(localCompilationSourceIdDmn);
    }

    @Override
    public String getModelType() {
        return "dmn";
    }

}
