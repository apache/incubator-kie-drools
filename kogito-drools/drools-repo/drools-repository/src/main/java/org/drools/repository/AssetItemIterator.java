/**
 * Copyright 2010 JBoss Inc
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

package org.drools.repository;

import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

/**
 * This iterates over nodes and produces RuleItem's.
 * Also allows "skipping" of results to jump to certain items,
 * as per JCRs "skip".
 *
 * JCR iterators are/can be lazy, so this makes the most of it for large
 * numbers of assets.
 *
 * @author Michael Neale
 */
public class AssetItemIterator
    implements
    Iterator<AssetItem> {

    private NodeIterator    it;
    private RulesRepository rulesRepository;

    public AssetItemIterator(NodeIterator nodes,
                            RulesRepository repo) {
        this.it = nodes;
        this.rulesRepository = repo;
    }

    public boolean hasNext() {
        return it.hasNext();
    }

    public AssetItem next() {
        return new AssetItem( rulesRepository,
                             (Node) it.next() );
    }

    public void remove() {
        throw new UnsupportedOperationException( "You can't remove a rule this way." );
    }

    /**
     * @param i The number of rules to skip.
     */
    public void skip(long i) {
        it.skip( i );
    }

    /**
     * @return the size of the underlying iterator's potential data set.
     * May be -1 if not known.
     */
    //NOTE this may return -1 as per JCR2.0 when precise count is not available due to performance reasons. 
     public long getSize() {
    	return it.getSize();
    }

    /**
     * Get the position in the result set.
     */
    public long getPosition() {
    	return it.getPosition();
    }


}
