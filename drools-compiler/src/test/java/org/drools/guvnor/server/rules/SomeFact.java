package org.drools.guvnor.server.rules;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

import org.drools.Cheese;

public class SomeFact {
    private static final long serialVersionUID = 400L;
    private String            name;
    private String            likes;
    private int               age;
    private BigDecimal        bigDecimal;
    private BigInteger        bigInteger;
    private String            hair;

    private char              sex;

    private boolean           alive;

    private String            status;

    private Cheese            cheese;

    private Date              date;

    private Boolean           dead;

    private List<SomeFact> factList = new ArrayList<SomeFact>();

    private List<String> factListString = new ArrayList<String>();



    private EnumClass anEnum;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    public BigInteger getBigInteger() {
        return bigInteger;
    }

    public void setBigInteger(BigInteger bigInteger) {
        this.bigInteger = bigInteger;
    }

    public Cheese getCheese() {
        return cheese;
    }

    public void setCheese(Cheese cheese) {
        this.cheese = cheese;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Boolean getDead() {
        return dead;
    }

    public void setDead(Boolean dead) {
        this.dead = dead;
    }

    public String getHair() {
        return hair;
    }

    public void setHair(String hair) {
        this.hair = hair;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public char getSex() {
        return sex;
    }

    public void setSex(char sex) {
        this.sex = sex;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public List<SomeFact> getFactList() {
        return factList;
    }

    public List<String> getFactListString() {
        return factListString;
    }

    public void setFactListString(List<String> factListString) {
        this.factListString = factListString;
    }

    public void setFactList(List<SomeFact> factList) {
        this.factList = factList;
    }
    public EnumClass getAnEnum() {
        return anEnum;
    }

    public void setAnEnum(EnumClass anEnum) {
        this.anEnum = anEnum;
    }
    
    public List<SomeFact> aMethod(int anInt) {
    	return null;
    }
}
