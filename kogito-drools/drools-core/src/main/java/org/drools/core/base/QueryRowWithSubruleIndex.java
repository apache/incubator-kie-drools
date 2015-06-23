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

package org.drools.core.base;

import org.drools.core.common.InternalFactHandle;


public class QueryRowWithSubruleIndex {
    private InternalFactHandle[] handles;
    private int subruleIndex;
    
    public QueryRowWithSubruleIndex(InternalFactHandle[] handles,
                                    int subruleIndex) {
        this.handles = handles;
        this.subruleIndex = subruleIndex;
    }

    public InternalFactHandle[] getHandles() {
        return handles;
    }

    public int getSubruleIndex() {
        return subruleIndex;
    }                
}
