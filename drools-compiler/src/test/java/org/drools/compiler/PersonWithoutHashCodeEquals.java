/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler;

import org.drools.core.factmodel.traits.Traitable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Traitable
@XmlAccessorType(XmlAccessType.FIELD)
public class PersonWithoutHashCodeEquals
        implements
        Serializable,
        PersonInterface {
    private static final long serialVersionUID = 510l;
    private String name;
    private String likes;
    private int age;
    private BigDecimal bigDecimal;
    private BigInteger bigInteger;
    private String hair;

    private char sex;

    private boolean alive;
    private boolean happy;

    private String status;

    private Cheese cheese;

    private AddressWithoutHashCodeEquals address;
    private Pet pet;

    private List<AddressWithoutHashCodeEquals> addresses = new ArrayList<AddressWithoutHashCodeEquals>();
    private Map<Object, AddressWithoutHashCodeEquals> namedAddresses = new HashMap<Object, AddressWithoutHashCodeEquals>(0);


    public Object object;

    public Object notInEqualTestObject;

    public PersonWithoutHashCodeEquals() {

    }

    public PersonWithoutHashCodeEquals(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public PersonWithoutHashCodeEquals(String name, int age, boolean happy) {
        this.name = name;
        this.age = age;
        this.happy = happy;
    }

    public PersonWithoutHashCodeEquals(final String name) {
        this(name,
                "",
                0);
    }

    public AddressWithoutHashCodeEquals getAddress() {
        return address;
    }

    public void setAddress(AddressWithoutHashCodeEquals address) {
        this.address = address;
    }

    public Option<AddressWithoutHashCodeEquals> getAddressOption() {
        return new Option<AddressWithoutHashCodeEquals>(address);
    }

    public List<AddressWithoutHashCodeEquals> getAddresses() {
        return addresses;
    }

    public List getAddressesNoGenerics() {
        return addresses;
    }

    public void setAddresses(List<AddressWithoutHashCodeEquals> addresses) {
        this.addresses = addresses;
    }

    public void addAddress(AddressWithoutHashCodeEquals address) {
        this.addresses.add(address);
    }

    public Map<Object, AddressWithoutHashCodeEquals> getNamedAddresses() {
        return namedAddresses;
    }

    public void setNamedAddresses(Map<Object, AddressWithoutHashCodeEquals> namedAddresses) {
        this.namedAddresses = namedAddresses;
    }

    public PersonWithoutHashCodeEquals(final String name,
                  final String likes) {
        this(name,
                likes,
                0);
    }

    public PersonWithoutHashCodeEquals(final String name,
                  final String likes,
                  final int age) {
        this.name = name;
        this.likes = likes;
        this.age = age;
    }

    /* (non-Javadoc)
     * @see org.drools.compiler.PersonInterface#getStatus()
     */
    public String getStatus() {
        return this.status;
    }

    /* (non-Javadoc)
     * @see org.drools.compiler.PersonInterface#setStatus(java.lang.String)
     */
    public void setStatus(final String status) {
        this.status = status;
    }

    /* (non-Javadoc)
     * @see org.drools.compiler.PersonInterface#getLikes()
     */
    public String getLikes() {
        return this.likes;
    }

    /* (non-Javadoc)
     * @see org.drools.compiler.PersonInterface#getName()
     */
    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.drools.compiler.PersonInterface#getAge()
     */
    public int getAge() {
        return this.age;
    }

    public int getDoubleAge() {
        return this.age * 2;
    }

    public void setAge(final int age) {
        this.age = age;
    }

    public Integer getAgeAsInteger() {
        return this.age;
    }

    /* (non-Javadoc)
     * @see org.drools.compiler.PersonInterface#isAlive()
     */
    public boolean isAlive() {
        return this.alive;
    }

    /* (non-Javadoc)
     * @see org.drools.compiler.PersonInterface#setAlive(boolean)
     */
    public void setAlive(final boolean alive) {
        this.alive = alive;
    }

    /* (non-Javadoc)
     * @see org.drools.compiler.PersonInterface#getSex()
     */
    public char getSex() {
        return this.sex;
    }

    /* (non-Javadoc)
     * @see org.drools.compiler.PersonInterface#setSex(char)
     */
    public void setSex(final char sex) {
        this.sex = sex;
    }

    public String getHair() {
        return this.hair;
    }

    public void setHair(final String hair) {
        this.hair = hair;
    }

    public Object getNotInEqualTestObject() {
        return notInEqualTestObject;
    }

    public void setNotInEqualTestObject(Object notInEqualTestObject) {
        this.notInEqualTestObject = notInEqualTestObject;
    }

    public String toString() {
        return "[Person name='" + this.name + " age='" + this.age + "' likes='" + likes + "']";
    }

    /* (non-Javadoc)
         * @see org.drools.compiler.PersonInterface#getBigDecimal()
         */
    public BigDecimal getBigDecimal() {
        return this.bigDecimal;
    }

    /* (non-Javadoc)
     * @see org.drools.compiler.PersonInterface#setBigDecimal(java.math.BigDecimal)
     */
    public void setBigDecimal(final BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    /* (non-Javadoc)
     * @see org.drools.compiler.PersonInterface#getBigInteger()
     */
    public BigInteger getBigInteger() {
        return this.bigInteger;
    }

    /* (non-Javadoc)
     * @see org.drools.compiler.PersonInterface#setBigInteger(java.math.BigInteger)
     */
    public void setBigInteger(final BigInteger bigInteger) {
        this.bigInteger = bigInteger;
    }

    public void setLikes(final String likes) {
        this.likes = likes;
    }

    public Cheese getCheese() {
        return this.cheese;
    }


    public Pet getPet() {
        return pet;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public void setCheese(final Cheese cheese) {
        this.cheese = cheese;
    }

    public boolean isHappy() {
        return happy;
    }

    public void setHappy(boolean happy) {
        this.happy = happy;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

}
