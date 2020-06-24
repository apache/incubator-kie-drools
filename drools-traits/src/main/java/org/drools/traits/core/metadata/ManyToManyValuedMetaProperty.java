/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.traits.core.metadata;


import java.util.Collection;

public interface ManyToManyValuedMetaProperty<T,R,C extends Collection<R>,D extends Collection<T>>
        extends ManyValuedMetaProperty<T,R,C>, InverseManyValuedMetaProperty<T,R,D> {

    @Override
    public ManyValuedMetaProperty<R,T,D> getInverse();
}
