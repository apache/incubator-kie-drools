/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.compiler.canonical;

public class ModelMetaData {

    private String modelClassSimpleName;
    private String modelClassName;

    private String generatedClassModel;

    public ModelMetaData(String modelClassSimpleName, String modelClassName, String generatedClassModel) {
        this.modelClassSimpleName = modelClassSimpleName;
        this.modelClassName = modelClassName;
        this.generatedClassModel = generatedClassModel;
    }

    public String getModelClassSimpleName() {
        return modelClassSimpleName;
    }

    public void setModelClassSimpleName(String modelClassSimpleName) {
        this.modelClassSimpleName = modelClassSimpleName;
    }

    public String getModelClassName() {
        return modelClassName;
    }

    public void setModelClassName(String modelClassName) {
        this.modelClassName = modelClassName;
    }

    public String getGeneratedClassModel() {
        return generatedClassModel;
    }

    public void setGeneratedClassModel(String generatedClassModel) {
        this.generatedClassModel = generatedClassModel;
    }

    @Override
    public String toString() {
        return "ModelMetaData [modelClassName=" + modelClassName + "]";
    }

}
