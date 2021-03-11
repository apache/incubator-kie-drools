/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.constraints;

import org.drools.core.common.GroupByFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;

public class GroupByDeclaration extends Declaration {

    public GroupByDeclaration(String name, Pattern pattern) {
        super(name, new LambdaReadAccessor( Object.class, x -> x ), pattern);
    }

    @Override
    public Object getValue( InternalWorkingMemory workingMemory, InternalFactHandle fh) {
        return (( GroupByFactHandle ) fh).getGroupKey();
    }

    @Override
    public Declaration cloneWithPattern(Pattern pattern) {
        return new GroupByDeclaration( getIdentifier(), pattern );
    }
}
