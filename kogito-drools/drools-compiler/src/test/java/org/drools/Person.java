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
import java.util.List;

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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    private List<Address> addresses = new ArrayList<Address>();

    public Object object;

    //    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    //        name    = (String)in.readObject();
    //        likes    = (String)in.readObject();
    //        age     = in.readInt();
    //        bigDecimal    = (BigDecimal)in.readObject();
    //        bigInteger    = (BigInteger)in.readObject();
    //        hair    = (String)in.readObject();
    //        sex     = in.readChar();
    //        alive   = in.readBoolean();
    //        status    = (String)in.readObject();
    //        cheese    = (Cheese)in.readObject();
    //        addresses    = (List)in.readObject();
    //    }
    //
    //    public void writeExternal(ObjectOutput out) throws IOException {
    //        out.writeObject(name);
    //        out.writeObject(likes);
    //        out.writeObject(bigDecimal);
    //        out.writeObject(bigInteger);
    //        out.writeObject(hair);
    //        out.writeChar(sex);
    //        out.writeBoolean(alive);
    //        out.writeObject(status);
    //        out.writeObject(cheese);
    //        out.writeObject(addresses);
    //    }

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

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public void addAddress(Address address) {
        this.addresses.add(address);
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

    /**
     * @inheritDoc
     */
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + this.age;
        result = PRIME * result + (this.alive ? 1231 : 1237);
        result = PRIME * result + ((this.name == null) ? 0 : this.name.hashCode());
        result = PRIME * result + this.sex;
        return result;
    }

    /**
     * @inheritDoc
     */
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Person other = (Person) obj;
        if (this.age != other.age) {
            return false;
        }
        if (this.alive != other.alive) {
            return false;
        }
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        if (this.sex != other.sex) {
            return false;
        }
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
