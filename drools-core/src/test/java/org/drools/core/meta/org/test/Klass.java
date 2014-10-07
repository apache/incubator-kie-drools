package org.drools.core.meta.org.test;

import org.drools.core.metadata.Metadatable;

import java.util.List;

public interface Klass extends Metadatable {

    public String getProp();
    public void setProp( String value );

    public AnotherKlass getAnother();
    public void setAnother( AnotherKlass another );

    public AnotherKlass getOneAnother();
    public void setOneAnother( AnotherKlass another );

    public List<AnotherKlass> getManyAnothers();
    public void setManyAnothers( List<AnotherKlass> anothers );

}
