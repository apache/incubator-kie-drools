package org.kie.internal.runtime.conf;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;

/**
 * Extension of <code>ObjectModel</code> that provides unique name for the object model
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class NamedObjectModel extends ObjectModel {

    private static final long serialVersionUID = -72398619245628956L;

    @XmlElement(name="name")
    @XmlSchemaType(name="string")
    private String name;

    public NamedObjectModel() {
        //for jaxb only
    }

    public NamedObjectModel(String resolver, String name, String classname, Object... parameters) {
        super(resolver, classname, parameters);
        this.name = name;
    }

    public NamedObjectModel(String name, String classname, Object... parameters) {
        super(classname, parameters);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (this == obj) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NamedObjectModel other = (NamedObjectModel) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }

        return true;
    }

}
