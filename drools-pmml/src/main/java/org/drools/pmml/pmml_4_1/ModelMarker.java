/*
 * Copyright 2011 JBoss Inc
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

package org.drools.pmml.pmml_4_1;


import org.kie.api.definition.type.Position;
import org.kie.api.definition.type.PropertyReactive;

@PropertyReactive
public class ModelMarker {

    @Position(0)
    private String modelName;

    @Position(1)
    private String modelClass;

    @Position(2)
    private boolean enabled         = true;

    @Position(3)
    private String modelUrl;

    private Object boundInput;

    public ModelMarker() { }

    public ModelMarker(String modelName, String modelClass) {
        this.modelName = modelName;
        this.modelClass = modelClass;
    }

    public String getModelClass() {
        return modelClass;
    }

    public void setModelClass(String modelClass) {
        this.modelClass = modelClass;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelUrl() {
        return modelUrl;
    }

    public void setModelUrl(String modelUrl) {
        this.modelUrl = modelUrl;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "ModelMarker{" +
                "modelName='" + modelName + '\'' +
                ", modelClass='" + modelClass + '\'' +
                ", enabled=" + enabled +
                ", modelUrl='" + modelUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ModelMarker that = (ModelMarker) o;

        if (modelName != null ? !modelName.equals(that.modelName) : that.modelName != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return modelName != null ? modelName.hashCode() : 0;
    }

    public Object getBoundInput() {
        return boundInput;
    }

    public void setBoundInput( Object boundInput ) {
        this.boundInput = boundInput;
    }
}
