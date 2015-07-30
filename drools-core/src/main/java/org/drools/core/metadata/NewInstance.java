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

import org.drools.core.factmodel.traits.InstantiatorFactory;

public interface NewInstance<T> extends WorkingMemoryTask<T> {

    public boolean isInterface();

    public Object callUntyped();

    public InstantiatorFactory getInstantiatorFactory();

    public NewInstance<T> setInstantiatorFactory( InstantiatorFactory factory );

    public Modify getInitArgs();

    public Class<T> getInstanceClass();
}
