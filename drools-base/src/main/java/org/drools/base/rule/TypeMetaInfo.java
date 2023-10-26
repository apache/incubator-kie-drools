/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.base.rule;

import java.io.Serializable;

import org.kie.api.definition.type.Role;

public class TypeMetaInfo implements Serializable{
    private TypeDeclaration.Kind kind;
    private Role.Type role;
    private boolean isDeclaredType;

    public TypeMetaInfo() { }

    public TypeMetaInfo(TypeDeclaration typeDeclaration) {
        this.kind = typeDeclaration.getKind();
        this.role = typeDeclaration.getRole();
        this.isDeclaredType = !typeDeclaration.isJavaBased();
    }

    public TypeMetaInfo(Class<?> clazz) {
        this.kind = TypeDeclaration.Kind.CLASS;
        Role role = clazz.getAnnotation(Role.class);
        this.role = role == null ? Role.Type.FACT : role.value();
        this.isDeclaredType = false;
    }

    public boolean isEvent() {
        return role == Role.Type.EVENT;
    }

    public boolean isDeclaredType() {
        return isDeclaredType;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("kind=").append(kind.toString().toLowerCase());
        sb.append(",");
        sb.append("role=").append(role.toString().toLowerCase());
        sb.append(",");
        sb.append("isDeclaredType=").append(isDeclaredType);
        return sb.toString();
    }
}
