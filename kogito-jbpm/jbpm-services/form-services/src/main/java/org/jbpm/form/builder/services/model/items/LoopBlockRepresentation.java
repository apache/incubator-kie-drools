/*
 * Copyright 2011 JBoss Inc 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.form.builder.services.model.items;

import java.util.Map;

import org.jbpm.form.builder.services.model.FormItemRepresentation;
import org.jbpm.form.builder.services.model.forms.FormEncodingException;
import org.jbpm.form.builder.services.model.forms.FormEncodingFactory;

public class LoopBlockRepresentation extends FormItemRepresentation {

    private String inputName;
    private String variableName;
    private FormItemRepresentation loopBlock;
    
    public LoopBlockRepresentation() {
        super("loopBlock");
    }

    public String getInputName() {
        return inputName;
    }

    public void setInputName(String inputName) {
        this.inputName = inputName;
    }

    public String getVariableName() {
        return variableName;
    }

    public void setVariableName(String variableName) {
        this.variableName = variableName;
    }

    public FormItemRepresentation getLoopBlock() {
        return loopBlock;
    }

    public void setLoopBlock(FormItemRepresentation loopBlock) {
        this.loopBlock = loopBlock;
    }
    
    @Override
    public Map<String, Object> getDataMap() {
        Map<String, Object> data = super.getDataMap();
        data.put("inputName", this.inputName);
        data.put("variableName", this.variableName);
        data.put("loopBlock", this.loopBlock == null ? null : this.loopBlock.getDataMap());
        return data;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void setDataMap(Map<String, Object> data) throws FormEncodingException {
        super.setDataMap(data);
        this.inputName = (String) data.get("inputName");
        this.variableName = (String) data.get("variableName");
        this.loopBlock = (FormItemRepresentation) FormEncodingFactory.getDecoder().decode((Map<String, Object>) data.get("loopBlock"));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof LoopBlockRepresentation)) return false;
        LoopBlockRepresentation other = (LoopBlockRepresentation) obj;
        boolean equals = (this.inputName == null && other.inputName == null) || 
            (this.inputName != null && this.inputName.equals(other.inputName));
        if (!equals) return equals;
        equals = (this.variableName == null && other.variableName == null) || 
            (this.variableName != null && this.variableName.equals(other.variableName));
        if (!equals) return equals;
        equals = (this.loopBlock == null && other.loopBlock == null) || 
            (this.loopBlock != null && this.loopBlock.equals(other.loopBlock));
        return equals;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = this.inputName == null ? 0 : this.inputName.hashCode();
        result = 37 * result + aux;
        aux = this.variableName == null ? 0 : this.variableName.hashCode();
        result = 37 * result + aux;
        aux = this.loopBlock == null ? 0 : this.loopBlock.hashCode();
        result = 37 * result + aux;
        return result;
    }
}
