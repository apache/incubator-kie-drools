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

package org.drools.compiler.lang.descr;

import org.drools.core.util.StringUtils;

public class QualifiedName {

    private String name;
    private String namespace;

    public QualifiedName(String name) {
        setName( name );
    }

    public QualifiedName(String name, String namespace) {
        this.name = name;
        this.namespace = namespace;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        int pos = name.lastIndexOf( '.' );
        if ( pos < 0 ) {
            this.name = name;
        } else {
            this.name = name.substring( pos + 1 );
            this.namespace = name.substring( 0, pos );
        }
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QualifiedName that = (QualifiedName) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return !(namespace != null ? !namespace.equals(that.namespace) : that.namespace != null);
    }

    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
        return result;
    }

    public String getFullName() {
        if ( StringUtils.isEmpty(namespace) ) {
            return name;
        } else {
            return namespace + "." + name;
        }
    }

    public String toString() {
        return getFullName();
    }

    public boolean isFullyQualified() {
        return ! StringUtils.isEmpty( namespace );
    }
}
