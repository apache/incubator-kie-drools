/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.drools.core.meta.org.test;

import org.drools.core.metadata.Metadatable;

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
