/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.kie.internal.runtime.conf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
/**
 * Object model that defines how and of what type the object instance should be created.<br>
 * The how is actually delegated to resolved implementation <code>ObjectModelResolver</code>
 * that default to Java Reflection based resolver but might utilize others (such as MVEL, Spring, CDI, etc).
 * <br>
 * The what type is derived from identifier (which should be FQCN in case of reflection) that is then used
 * to create instance of that object using constructor. Which constructor is taken depends on defined parameters
 * which might be again an ObjectModel for complex types.
 * String types are supported directly, all other should be represented as ObjectModel.
 * <br>
 * There are some key words acceptable that directly will refer to available instances:
 * <ul>
 *  <li>runtimeManager - to get RuntimeManager instance injected</li>
 *  <li>runtimeEngine - to get RuntimeEngine instance injected</li>
 *  <li>ksession - to get KieSession instance injected</li>
 *  <li>taskService - to get TaskService instance injected</li>
 * </ul>
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class ObjectModel implements Serializable {

    private static final long serialVersionUID = 6079949171686382208L;

    @XmlElement(name="resolver")
    @XmlSchemaType(name="string")
    private String resolver = "reflection";

    @XmlElement(name="identifier")
    @XmlSchemaType(name="string")
    private String identifier;
    @XmlElement(name="parameter")
    @XmlElementWrapper(name="parameters")
    private List<Object> parameters = new ArrayList<Object>();

    public ObjectModel() {
        // fox jaxb only
    }

    public ObjectModel(String identifier, Object... parameters) {
        this.identifier = identifier;
        if (parameters != null) {
            this.parameters = new ArrayList<Object>(Arrays.asList(parameters));
        }
    }

    public ObjectModel(String resolver, String identifier, Object... parameters) {
        this.resolver = resolver;
        this.identifier = identifier;
        if (parameters != null) {
            this.parameters = new ArrayList<Object>(Arrays.asList(parameters));
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String classname) {
        this.identifier = classname;
    }

    public List<Object> getParameters() {
        return parameters;
    }

    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }

    public void addParameter(Object parameter) {
        this.parameters.add(parameter);
    }

    public String getResolver() {
        return resolver;
    }

    public void setResolver(String resolver) {
        this.resolver = resolver;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((identifier == null) ? 0 : identifier.hashCode());
        result = prime * result
                + ((parameters == null) ? 0 : parameters.hashCode());
        result = prime * result
                + ((resolver == null) ? 0 : resolver.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ObjectModel other = (ObjectModel) obj;
        if (identifier == null) {
            if (other.identifier != null) {
                return false;
            }
        } else if (!identifier.equals(other.identifier)) {
            return false;
        }
        if (parameters == null) {
            if (other.parameters != null) {
                return false;
            }
        } else if (!parameters.equals(other.parameters)) {
            return false;
        }
        if (resolver == null) {
            if (other.resolver != null) {
                return false;
            }
        } else if (!resolver.equals(other.resolver)) {
            return false;
        }
        return true;
    }
}
