package org.drools.base.base;


public interface ClassWireable {

    void wire( Class<?> klass );

    String getClassName();

    Class<?> getClassType();

}
