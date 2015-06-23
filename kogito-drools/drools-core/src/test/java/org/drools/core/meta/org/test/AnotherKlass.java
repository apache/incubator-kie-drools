/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
