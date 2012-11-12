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
import org.jbpm.form.builder.services.model.forms.FormRepresentationDecoder;

public class ConditionalBlockRepresentation extends FormItemRepresentation {

    private FormItemRepresentation ifBlock;
    private FormItemRepresentation elseBlock;
    private String condition;
    
    public ConditionalBlockRepresentation() {
        super("conditionalBlock");
    }

    public FormItemRepresentation getIfBlock() {
        return ifBlock;
    }

    public void setIfBlock(FormItemRepresentation ifBlock) {
        this.ifBlock = ifBlock;
    }

    public FormItemRepresentation getElseBlock() {
        return elseBlock;
    }

    public void setElseBlock(FormItemRepresentation elseBlock) {
        this.elseBlock = elseBlock;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
    
    @Override
    public Map<String, Object> getDataMap() {
        Map<String, Object> data = super.getDataMap();
        data.put("condition", this.condition);
        data.put("ifBlock", this.ifBlock == null ? null : this.ifBlock.getDataMap());
        data.put("elseBlock", this.elseBlock == null ? null : this.elseBlock.getDataMap());
        return data;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void setDataMap(Map<String, Object> data) throws FormEncodingException {
        super.setDataMap(data);
        this.condition = (String) data.get("condition");
        FormRepresentationDecoder decoder = FormEncodingFactory.getDecoder();
        this.ifBlock = (FormItemRepresentation) decoder.decode((Map<String, Object>) data.get("ifBlock"));
        this.elseBlock = (FormItemRepresentation) decoder.decode((Map<String, Object>) data.get("elseBlock"));
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof ConditionalBlockRepresentation)) return false;
        ConditionalBlockRepresentation other = (ConditionalBlockRepresentation) obj;
        boolean equals = (this.condition == null && other.condition == null) || 
            (this.condition != null && this.condition.equals(other.condition));
        if (!equals) return equals;
        equals = (this.ifBlock == null && other.ifBlock == null) || 
            (this.ifBlock != null && this.ifBlock.equals(other.ifBlock));
        if (!equals) return equals;
        equals = (this.elseBlock == null && other.elseBlock == null) || 
            (this.elseBlock != null && this.elseBlock.equals(other.elseBlock));
        return equals;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = this.condition == null ? 0 : this.condition.hashCode();
        result = 37 * result + aux;
        aux = this.ifBlock == null ? 0 : this.ifBlock.hashCode();
        result = 37 * result + aux;
        aux = this.elseBlock == null ? 0 : this.elseBlock.hashCode();
        result = 37 * result + aux;
        return result;
    }
}
