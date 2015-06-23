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

package org.drools.core.metadata;

import java.net.URI;
import java.util.Collection;

public interface MetaProperty<T,R,C> extends Comparable<MetaProperty<T,R,C>>, Identifiable {

    public int getIndex();

    public String getName();

    public URI getKey();

    public boolean isManyValued();

    public OneValuedMetaProperty<T,C> asFunctionalProperty();

    public <X extends Collection<R>> ManyValuedMetaProperty<T,R,X> asManyValuedProperty();

    public C get( T o );

    public boolean isDatatype();

}
