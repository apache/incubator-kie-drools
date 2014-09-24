/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.drools.core.meta.org.test;

import org.drools.core.metadata.Metadatable;

public interface AnotherKlass extends Metadatable {

    public int getNum();
    public void setNum( int value );

}
