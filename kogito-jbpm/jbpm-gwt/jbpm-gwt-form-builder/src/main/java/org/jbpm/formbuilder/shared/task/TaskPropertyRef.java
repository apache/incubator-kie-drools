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
package org.jbpm.formbuilder.shared.task;

public class TaskPropertyRef {

    private String name;
    private String sourceExpresion;

    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getSourceExpresion() {
        return sourceExpresion;
    }
    
    public void setSourceExpresion(String sourceExpresion) {
        this.sourceExpresion = sourceExpresion;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (!(obj instanceof TaskPropertyRef)) return false;
        TaskPropertyRef other = (TaskPropertyRef) obj;
        boolean equals = (this.name == null && other.name == null) || 
            (this.name != null && this.name.equals(other.name));
        if (!equals) return equals;
        equals = (this.sourceExpresion == null && other.sourceExpresion == null) || 
            (this.sourceExpresion != null && this.sourceExpresion.equals(other.sourceExpresion));
        return equals;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = this.name == null ? 0 : this.name.hashCode();
        result = 37 * result + aux;
        aux = this.sourceExpresion == null ? 0 : this.sourceExpresion.hashCode();
        result = 37 * result + aux;
        return result;
    }
    
    @Override
    public String toString() {
        return new StringBuilder("TaskPropertyRef[name=").append(this.name).
            append(";sourceExpresion=").append(this.sourceExpresion).append("]").toString();
    }
}
