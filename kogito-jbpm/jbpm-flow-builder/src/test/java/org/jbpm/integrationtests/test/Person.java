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

package org.jbpm.integrationtests.test;

import java.io.Serializable;

import org.jbpm.integrationtests.test.Person;

public class Person
    implements
    Serializable {

    private static final long serialVersionUID = 510l;
    private String            name;
    private int               age;

    public Person() {

    }

    public Person(String name,
                  int age) {
        super();
        this.name = name;
        this.age = age;
    }

    public Person(final String name) {
        this( name, 0 );
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

    public String toString() {
        return "[Person name='" + this.name + " age='" + this.age + "']";
    }

    /**
     * @inheritDoc
     */
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + this.age;
        result = PRIME * result + ((this.name == null) ? 0 : this.name.hashCode());
        return result;
    }

    /**
     * @inheritDoc
     */
    public boolean equals(final Object obj) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        final Person other = (Person) obj;
        if ( this.age != other.age ) {
            return false;
        }
        if ( this.name == null ) {
            if ( other.name != null ) {
                return false;
            }
        } else if ( !this.name.equals( other.name ) ) {
            return false;
        }
        return true;
    }

}
