package org.drools.drl.ast.descr;

import java.io.Serializable;

import org.drools.drl.ast.util.AstUtil;

public class QualifiedName implements Serializable {

    private static final long serialVersionUID = 500964956132811301L;
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
        if ( AstUtil.isEmpty(namespace) ) {
            return name;
        } else {
            return namespace + "." + name;
        }
    }

    public String toString() {
        return getFullName();
    }

    public boolean isFullyQualified() {
        return ! AstUtil.isEmpty( namespace );
    }
}
