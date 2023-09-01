package org.kie.api.pmml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.api.definition.type.Role;
import org.kie.api.definition.type.Role.Type;

@XmlRootElement(name="baseOutputField")
@XmlAccessorType(XmlAccessType.FIELD)
@Role(Type.EVENT)
public class PMML4OutputField extends PMML4DataField {
    @XmlElement(name="warning")
    private String warning = "No warning";
    @XmlAttribute(name="target")
    private boolean target = false;
    
    public String getWarning() {
        return warning;
    }
    public void setWarning(String warning) {
        this.warning = warning;
    }
    public boolean isTarget() {
        return target;
    }
    public void setTarget(boolean target) {
        this.target = target;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (target ? 1231 : 1237);
        result = prime * result + ((warning == null) ? 0 : warning.hashCode());
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PMML4OutputField other = (PMML4OutputField) obj;
        if (target != other.target) {
            return false;
        }
        if (warning == null) {
            if (other.warning != null) {
                return false;
            }
        } else if (!warning.equals(other.warning)) {
            return false;
        }
        return true;
    }
    
    @Override
    public String toString() {
        StringBuilder bldr = new StringBuilder("PMML4OutputField - [");
        bldr.append(super.toString())
            .append(", warning=").append(warning)
            .append(", target=").append(target)
            .append("]");
        return bldr.toString();
    }
    
}
