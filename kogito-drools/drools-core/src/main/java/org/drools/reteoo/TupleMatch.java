package org.drools.reteoo;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.util.AbstractBaseLinkedListNode;

/**
 * <code>TupleMatch</code> maintains a reference to the parent <code>ReteTuple</code> and a <code>List</code> of all resulting joins. 
 * This is a List rather than a single instance reference because we need to create a join for each TupleSink branches.
 * A reference is also maintained to the <code>ObjectMatches</code> instance; this is so the <code>FactHandleImpl</code> that 
 * is used in the join can be referenced, and also any other <code>TupleMatch</code>es the <code>FactHandleImpl</code> is joined with.
 * 
 * @see TupleMatch
 * @see ObjectMatches
 * @see ReteTuple
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 *
 */
public class TupleMatch extends AbstractBaseLinkedListNode {
    private ReteTuple     tuple;

    private List          joined = Collections.EMPTY_LIST;

    private ObjectMatches objectMatches;

    /**
     * Construct a <code>TupleMatch</code> with references to the parent <code>ReteTuple</code> and 
     * <code>FactHandleImpl</code>, via ObjecMatches.
     * 
     * @param tuple
     * @param objectMatches
     */
    public TupleMatch(ReteTuple tuple,
                      ObjectMatches objectMatches) {
        this.tuple = tuple;
        this.objectMatches = objectMatches;
    }

    /**
     * Return the parent <code>ReteTuple</code>
     * 
     * @return the <code>ReteTuple</code>
     */
    public ReteTuple getTuple() {
        return this.tuple;
    }

    /**
     * Returns the referenced <code>ObjectMatches</code> which provides the 
     * <code>FactHandleImpl</code> the <code>ReteTuple</code> is joined with.
     * 
     * @return the <code>ObjectMatches</code>
     */
    public ObjectMatches getObjectMatches() {
        return this.objectMatches;
    }

    /**
     * Adds a resulting join to the <code>List</code>. A join is made for each <code>TupleSink</code>.
     * 
     * @param tuple
     */
    public void addJoinedTuple(ReteTuple tuple) {
        if ( this.joined == Collections.EMPTY_LIST ) {
            this.joined = new ArrayList( 1 );
        }
        this.joined.add( tuple );
    }

    /**
     * Return the <code>List</code> of joined <code>ReteTuple</code>s.
     * 
     * @return the <code>List<code>.
     */
    public List getJoinedTuples() {
        return this.joined;
    }

}