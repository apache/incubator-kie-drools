/*
 * Copyright 2015 JBoss Inc
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

package org.jbpm.bpmn2.objects;

@javax.xml.bind.annotation.XmlRootElement
public class NotAvailableGoodsReport implements java.io.Serializable {

    static final long serialVersionUID = 1L;

    @org.kie.api.definition.type.Position(value = 0)
    private java.lang.String type;

    public NotAvailableGoodsReport() {
    }

    public NotAvailableGoodsReport(java.lang.String type) {
        this.type = type;
    }

    public java.lang.String getType() {
        return this.type;
    }

    public void setType(java.lang.String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "NotAvailableGoodsReport{type:" + type + "}";
    }

}
