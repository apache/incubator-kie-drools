package org.drools;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface PersonInterface {

    public abstract String getStatus();

    public abstract void setStatus(String status);

    public abstract String getLikes();

    public abstract String getName();

    public abstract int getAge();

    public abstract boolean isAlive();

    public abstract void setAlive(boolean alive);

    public abstract char getSex();

    public abstract void setSex(char sex);

    public abstract BigDecimal getBigDecimal();

    public abstract void setBigDecimal(BigDecimal bigDecimal);

    public abstract BigInteger getBigInteger();

    public abstract void setBigInteger(BigInteger bigInteger);

}