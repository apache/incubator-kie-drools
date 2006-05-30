package org.drools.util;

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

import java.io.Serializable;

public class PrimitiveLongStack
    implements
    Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1121303319795490425L;
    private final int         tableSize;
    private int               currentPageId;
    private Page              currentPage;

    public PrimitiveLongStack() {
        this( 256 );
    }

    public PrimitiveLongStack(final int tableSize) {
        this.tableSize = tableSize;
        this.currentPageId = 0;

        // instantiate the first node
        // previous sibling of first node is null
        // next sibling of last node is null
        this.currentPage = new Page( null,
                                     this.currentPageId,
                                     this.tableSize );
    }

    public void push(final long value) {
        if ( this.currentPage.getPosition() == this.tableSize - 1 ) {

            final Page node = new Page( this.currentPage,
                                        ++this.currentPageId,
                                        this.tableSize );
            this.currentPage = node;
        }

        this.currentPage.push( value );
    }

    public long pop() {
        if ( this.currentPage.getPosition() == -1 ) {
            if ( this.currentPageId == 0 ) {
                throw new RuntimeException( "Unable to pop" );
            }

            final Page node = this.currentPage;
            this.currentPage = node.getPreviousSibling();
            this.currentPageId--;
            node.remove();

        }

        return this.currentPage.pop();
    }

    public boolean isEmpty() {
        return this.currentPageId == 0 && this.currentPage.getPosition() == -1;
    }

    private static final class Page
        implements
        Serializable {
        /**
         * 
         */
        private static final long serialVersionUID = 3287090599941407636L;
        private final int         pageId;
        private Page              nextSibling;
        private Page              previousSibling;
        private long[]            table;
        private int               lastKey;

        Page(final Page previousSibling,
             final int nodeId,
             final int tableSize) {
            // create bi-directional link
            this.previousSibling = previousSibling;
            if ( this.previousSibling != null ) {
                this.previousSibling.setNextSibling( this );
            }
            this.pageId = nodeId;
            this.lastKey = -1;

            // initiate tree;
            this.table = new long[tableSize];
        }

        public int getNodeId() {
            return this.pageId;
        }

        void setNextSibling(final Page nextSibling) {
            this.nextSibling = nextSibling;
        }

        public Page getNextSibling() {
            return this.nextSibling;
        }

        public Page getPreviousSibling() {
            return this.previousSibling;
        }

        public long pop() {
            return this.table[this.lastKey--];
        }

        public void push(final long value) {
            this.table[++this.lastKey] = value;
        }

        public int getPosition() {
            return this.lastKey;
        }

        void remove() {
            this.previousSibling.setNextSibling( null );
            this.previousSibling = null;
            this.table = null;
        }
    }
}