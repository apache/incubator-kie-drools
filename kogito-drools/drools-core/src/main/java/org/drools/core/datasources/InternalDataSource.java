/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.datasources;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.spi.Activation;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.RuleUnit;

public interface InternalDataSource<T> extends DataSource<T> {

    void bind( RuleUnit unit, EntryPoint ep );
    void unbind( RuleUnit unit );

    void update( FactHandle fh, Object obj, BitMask mask, Class<?> modifiedClass, Activation activation );

    void setWorkingMemory( InternalWorkingMemory workingMemory );
}
