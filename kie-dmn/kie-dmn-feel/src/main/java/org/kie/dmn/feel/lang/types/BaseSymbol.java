/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.feel.lang.types;

import org.kie.dmn.feel.lang.Scope;
import org.kie.dmn.feel.lang.Symbol;
import org.kie.dmn.feel.lang.Type;

public class BaseSymbol implements Symbol {
    private String id;
    private Type type;
    private Scope scope;

    public BaseSymbol() {
    }

    public BaseSymbol(String id) {
        this.id = id;
    }

    public BaseSymbol(String id, Type type) {
        this.id = id;
        this.type = type;
    }

    public BaseSymbol(String id, Scope scope) {
        this.id = id;
        this.scope = scope;
    }

    public BaseSymbol(String id, Type type, Scope scope) {
        this.id = id;
        this.type = type;
        this.scope = scope;
    }

    public String getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public Scope getScope() {
        return scope;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o ) return true;
        if ( !(o instanceof BaseSymbol) ) return false;

        BaseSymbol that = (BaseSymbol) o;

        if ( id != null ? !id.equals( that.id ) : that.id != null ) return false;
        if ( type != null ? !type.equals( that.type ) : that.type != null ) return false;
        return !(scope != null ? !scope.equals( that.scope ) : that.scope != null);

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (scope != null ? scope.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Symbol{" +
               " id='" + id + '\'' +
               ", type=" + ( type != null ? type.getName() : "<null>" ) +
               ", scope=" + ( scope != null ? scope.getName() : "<null>" ) +
               " }";
    }
}
