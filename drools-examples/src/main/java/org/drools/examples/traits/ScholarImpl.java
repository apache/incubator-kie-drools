package org.drools.examples.traits;


import org.drools.factmodel.traits.Thing;

public class ScholarImpl<K> implements Scholar<K> {

    private Thing<K> core;

    public ScholarImpl() {

    }

    public ScholarImpl( Thing<K> arg ) {
        this.core = arg;
    }

    public void learn( String subject ) {
        System.out.println( "I " + core.getFields().get( "name" ) + ", now know everything about " + subject );
    }

}
