/*
 * Copyright 2010 JBoss Inc
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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@Traitable
@XmlAccessorType(XmlAccessType.FIELD)
public class Person
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

    private Address address;
    private Pet pet;

    private List<Address> addresses = new ArrayList<Address>();
    private Map<Object, Address> namedAddresses = new HashMap<Object, Address>(0);


    public Object object;

    public Object notInEqualTestObject;

    public Person() {

    }

    public Person(String name,
                  int age) {
        super();
        this.name = name;
        this.age = age;
    }

    public Person(final String name) {
        this(name,
                "",
                0);
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Option<Address> getAddressOption() {
        return new Option<Address>(address);
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public List getAddressesNoGenerics() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public void addAddress(Address address) {
        this.addresses.add(address);
    }

    public Map<Object, Address> getNamedAddresses() {
        return namedAddresses;
    }

    public void setNamedAddresses(Map<Object, Address> namedAddresses) {
        this.namedAddresses = namedAddresses;
    }

    public Person(final String name,
                  final String likes) {
        this(name,
                likes,
                0);
    }

    public Person(final String name,
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((address == null) ? 0 : address.hashCode());
        result = prime * result + ((addresses == null) ? 0 : addresses.hashCode());
        result = prime * result + age;
        result = prime * result + (alive ? 1231 : 1237);
        result = prime * result + ((bigDecimal == null) ? 0 : bigDecimal.hashCode());
        result = prime * result + ((bigInteger == null) ? 0 : bigInteger.hashCode());
        result = prime * result + ((cheese == null) ? 0 : cheese.hashCode());
        result = prime * result + ((hair == null) ? 0 : hair.hashCode());
        result = prime * result + (happy ? 1231 : 1237);
        result = prime * result + ((likes == null) ? 0 : likes.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((object == null) ? 0 : object.hashCode());
        result = prime * result + ((pet == null) ? 0 : pet.hashCode());
        result = prime * result + sex;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( ! ( obj instanceof Person ) ) return false;
        Person other = (Person) obj;
        if ( address == null ) {
            if ( other.getAddress() != null ) return false;
        } else if ( !address.equals( other.getAddress() ) ) return false;
        if ( addresses == null ) {
            if ( other.getAddresses() != null ) return false;
        } else if ( !addresses.equals( other.getAddresses() ) ) return false;
        if ( age != other.getAge() ) return false;
        if ( alive != other.isAlive() ) return false;
        if ( bigDecimal == null ) {
            if ( other.getBigDecimal() != null ) return false;
        } else if ( !bigDecimal.equals( other.getBigDecimal() ) ) return false;
        if ( bigInteger == null ) {
            if ( other.getBigInteger() != null ) return false;
        } else if ( !bigInteger.equals( other.getBigInteger() ) ) return false;
        if ( cheese == null ) {
            if ( other.getCheese() != null ) return false;
        } else if ( !cheese.equals( other.getCheese() ) ) return false;
        if ( hair == null ) {
            if ( other.getHair() != null ) return false;
        } else if ( !hair.equals( other.getHair() ) ) return false;
        if ( happy != other.isHappy() ) return false;
        if ( likes == null ) {
            if ( other.getLikes() != null ) return false;
        } else if ( !likes.equals( other.getLikes() ) ) return false;
        if ( name == null ) {
            if ( other.getName() != null ) return false;
        } else if ( !name.equals( other.getName() ) ) return false;
        if ( object == null ) {
            if ( other.getObject() != null ) return false;
        } else if ( !object.equals( other.getObject() ) ) return false;
        if ( pet == null ) {
            if ( other.getPet() != null ) return false;
        } else if ( !pet.equals( other.getPet() ) ) return false;
        if ( sex != other.getSex() ) return false;
        return true;
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
