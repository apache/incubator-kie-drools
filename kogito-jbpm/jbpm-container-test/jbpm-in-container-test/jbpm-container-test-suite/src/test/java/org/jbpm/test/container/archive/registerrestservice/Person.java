/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.container.archive.registerrestservice;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "person")
@XmlAccessorType(XmlAccessType.FIELD)
public class Person implements Serializable {

    /**
     * Default ID.
     */
    private static final long serialVersionUID = 1L;

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "middlename", defaultValue = "")
    private String middlename;

    @XmlElement(name = "surname")
    private String surname;

    
    public Person() {
    }

    /**
     * 
     * @param name
     * @param middlename
     * @param surname
     */
    public Person(String name, String middlename, String surname) {
        this.name = name;
        this.middlename = middlename;
        this.surname = surname;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the middlename
     */
    public String getMiddlename() {
        return middlename;
    }

    /**
     * @param middlename
     *            the middlename to set
     */
    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    /**
     * @return the surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * @param surname
     *            the surname to set
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder(name);
        b.append(" - ");
        if (middlename != null && !middlename.isEmpty()) {
            b.append(middlename);
            b.append(" - ");
        }
        b.append(surname);
        return b.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((middlename == null) ? 1 : middlename.hashCode());
        result = prime * result + ((name == null) ? 1 : name.hashCode());
        result = prime * result + ((surname == null) ? 1 : surname.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof Person) {
            Person p = (Person) obj;
            return p.toString().equals(toString());
        }

        return false;
    }

}
