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
package org.jbpm.formapi.shared.form;

public class FormDef {
    
    private String formUrl;
    private String jsonContent;
    
    public FormDef(String formUrl, String jsonContent) {
        this();
        this.formUrl = formUrl;
        this.jsonContent = jsonContent;
    }

    public FormDef() {
    }
    
    public String getFormUrl() {
        return formUrl;
    }
    
    public String getJsonContent() {
        return jsonContent;
    }
    
    public void setFormUrl(String formUrl) {
        this.formUrl = formUrl;
    }
    
    public void setJsonContent(String jsonContent) {
        this.jsonContent = jsonContent;
    }
}
