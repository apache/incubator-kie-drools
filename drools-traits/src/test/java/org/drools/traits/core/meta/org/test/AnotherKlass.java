package org.drools.traits.core.meta.org.test;

import org.drools.traits.core.metadata.Metadatable;

import java.util.List;

public interface AnotherKlass extends Metadatable {

    public int getNum();
    public void setNum( int value );

    public Klass getTheKlass();
    public void setTheKlass( Klass klass );

    public List<Klass> getManyKlasses();
    public void setManyKlasses( List<Klass> klasses );

    public List<Klass> getManyMoreKlasses();
    public void setManyMoreKlasses( List<Klass> klasses );

}
