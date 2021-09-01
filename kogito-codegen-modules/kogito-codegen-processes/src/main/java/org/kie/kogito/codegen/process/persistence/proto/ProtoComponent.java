/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.codegen.process.persistence.proto;

public abstract class ProtoComponent {

    protected String name;
    protected String javaPackageOption;
    protected String comment;

    public ProtoComponent(String name, String javaPackageOption) {
        this.name = name;
        this.javaPackageOption = javaPackageOption;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJavaPackageOption() {
        return javaPackageOption;
    }

    public void setJavaPackageOption(String javaPackageOption) {
        this.javaPackageOption = javaPackageOption;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public abstract String serialize();

    @Override
    public String toString() {
        return serialize();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ProtoComponent other = (ProtoComponent) obj;
        if (name == null) {
            return other.name == null;
        } else {
            return name.equals(other.name);
        }
    }
}
