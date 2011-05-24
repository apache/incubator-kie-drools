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

package org.drools;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private Map<Object, Address> namedAddresses = new HashMap<Object, Address>();
    

    public Object object;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

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
     * @see org.drools.PersonInterface#getStatus()
     */
    public String getStatus() {
        return this.status;
    }

    /* (non-Javadoc)
     * @see org.drools.PersonInterface#setStatus(java.lang.String)
     */
    public void setStatus(final String status) {
        this.status = status;
    }

    /* (non-Javadoc)
     * @see org.drools.PersonInterface#getLikes()
     */
    public String getLikes() {
        return this.likes;
    }

    /* (non-Javadoc)
     * @see org.drools.PersonInterface#getName()
     */
    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.drools.PersonInterface#getAge()
     */
    public int getAge() {
        return this.age;
    }

    public void setAge(final int age) {
        this.age = age;
    }

    /* (non-Javadoc)
     * @see org.drools.PersonInterface#isAlive()
     */
    public boolean isAlive() {
        return this.alive;
    }

    /* (non-Javadoc)
     * @see org.drools.PersonInterface#setAlive(boolean)
     */
    public void setAlive(final boolean alive) {
        this.alive = alive;
    }

    /* (non-Javadoc)
     * @see org.drools.PersonInterface#getSex()
     */
    public char getSex() {
        return this.sex;
    }

    /* (non-Javadoc)
     * @see org.drools.PersonInterface#setSex(char)
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
        if ( getClass() != obj.getClass() ) return false;
        Person other = (Person) obj;
        if ( address == null ) {
            if ( other.address != null ) return false;
        } else if ( !address.equals( other.address ) ) return false;
        if ( addresses == null ) {
            if ( other.addresses != null ) return false;
        } else if ( !addresses.equals( other.addresses ) ) return false;
        if ( age != other.age ) return false;
        if ( alive != other.alive ) return false;
        if ( bigDecimal == null ) {
            if ( other.bigDecimal != null ) return false;
        } else if ( !bigDecimal.equals( other.bigDecimal ) ) return false;
        if ( bigInteger == null ) {
            if ( other.bigInteger != null ) return false;
        } else if ( !bigInteger.equals( other.bigInteger ) ) return false;
        if ( cheese == null ) {
            if ( other.cheese != null ) return false;
        } else if ( !cheese.equals( other.cheese ) ) return false;
        if ( hair == null ) {
            if ( other.hair != null ) return false;
        } else if ( !hair.equals( other.hair ) ) return false;
        if ( happy != other.happy ) return false;
        if ( likes == null ) {
            if ( other.likes != null ) return false;
        } else if ( !likes.equals( other.likes ) ) return false;
        if ( name == null ) {
            if ( other.name != null ) return false;
        } else if ( !name.equals( other.name ) ) return false;
        if ( object == null ) {
            if ( other.object != null ) return false;
        } else if ( !object.equals( other.object ) ) return false;
        if ( pet == null ) {
            if ( other.pet != null ) return false;
        } else if ( !pet.equals( other.pet ) ) return false;
        if ( sex != other.sex ) return false;
        return true;
    }

    /* (non-Javadoc)
     * @see org.drools.PersonInterface#getBigDecimal()
     */
    public BigDecimal getBigDecimal() {
        return this.bigDecimal;
    }

    /* (non-Javadoc)
     * @see org.drools.PersonInterface#setBigDecimal(java.math.BigDecimal)
     */
    public void setBigDecimal(final BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    /* (non-Javadoc)
     * @see org.drools.PersonInterface#getBigInteger()
     */
    public BigInteger getBigInteger() {
        return this.bigInteger;
    }

    /* (non-Javadoc)
     * @see org.drools.PersonInterface#setBigInteger(java.math.BigInteger)
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
