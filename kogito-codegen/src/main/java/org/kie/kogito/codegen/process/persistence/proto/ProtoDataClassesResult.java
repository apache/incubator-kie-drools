/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.codegen.process.persistence.proto;

import java.util.Collection;

import org.kie.kogito.codegen.GeneratedFile;

public class ProtoDataClassesResult<T> {
    private Collection<T> dataModelClasses;
    private Collection<GeneratedFile> generatedFiles;

    public ProtoDataClassesResult(Collection dataModelClasses, Collection<GeneratedFile> generatedFiles){
        this.dataModelClasses = dataModelClasses;
        this.generatedFiles = generatedFiles;
    }

    public Collection getDataModelClasses(){
        return this.dataModelClasses;
    }

    public Collection<GeneratedFile> getGeneratedFiles(){
        return this.generatedFiles;
    }
}
