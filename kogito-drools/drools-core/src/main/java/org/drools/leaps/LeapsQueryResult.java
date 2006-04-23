package org.drools.leaps;
/*
 * Copyright 2005 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.drools.FactHandle;
import org.drools.QueryResult;
import org.drools.QueryResults;
import org.drools.WorkingMemory;
import org.drools.spi.Tuple;

/** 
 *  
 * @author Alexander Bagerman
 * 
 */

public class LeapsQueryResult extends QueryResult {
    public LeapsQueryResult(Tuple tuple, WorkingMemory workingMemory,
            QueryResults queryResults) {
        super( tuple, workingMemory, queryResults );
    }

    public Object get( int i ) {
        // adjust for the DroolsQuery object
        return super.get(i - 1);
    }

    public FactHandle[] getFactHandles() {
        // Strip the DroolsQuery fact
        FactHandle[] src = super.tuple.getFactHandles( );
        FactHandle[] dst = new FactHandle[src.length - 1];
        System.arraycopy( src, 0, dst, 0, dst.length );
        return dst;
    }

    public int size() {
        // Adjust for the DroolsQuery object
        return super.size() + 1;
    }

}
