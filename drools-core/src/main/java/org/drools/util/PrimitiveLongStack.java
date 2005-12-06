package org.drools.util;
/*
 * $Id: PrimitiveLongStack.java,v 1.1 2005/07/26 01:06:32 mproctor Exp $
 *
 * Copyright 2001-2003 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company. (http://werken.com/)
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

import java.io.Serializable;

public class PrimitiveLongStack
    implements
    Serializable
{
    private final int tableSize;
    private int currentPageId;
    private Page currentPage;

    public PrimitiveLongStack()
    {
        this( 256 );
    }

    public PrimitiveLongStack(int tableSize)
    {
        this.tableSize = tableSize;
        this.currentPageId = 0;

        // instantiate the first node
        // previous sibling of first node is null
        // next sibling of last node is null
        this.currentPage = new Page( null,
                                     this.currentPageId,
                                     this.tableSize );
    }

    public void push(long value)
    {
        if ( this.currentPage.getPosition( ) == this.tableSize - 1 )
        {

            Page node = new Page( this.currentPage,
                                  ++this.currentPageId,
                                  this.tableSize );
            this.currentPage = node;
        }

        this.currentPage.push( value );
    }

    public long pop()
    {
        if ( this.currentPage.getPosition( ) == -1 )
        {
            if ( this.currentPageId == 0 )
            {
                throw new RuntimeException( "Unable to pop" );
            }

            Page node = this.currentPage;
            this.currentPage = node.getPreviousSibling( );
            this.currentPageId--;
            node.remove( );

        }

        return this.currentPage.pop( );
    }

    public boolean isEmpty()
    {
        return this.currentPageId == 0 && this.currentPage.getPosition( ) == -1;
    }

    private static final class Page
        implements
        Serializable
    {
        private final int pageId;
        private Page nextSibling;
        private Page previousSibling;
        private long[] table;
        private int lastKey;

        Page(Page previousSibling,
             int nodeId,
             int tableSize)
        {
            // create bi-directional link
            this.previousSibling = previousSibling;
            if ( this.previousSibling != null )
            {
                this.previousSibling.setNextSibling( this );
            }
            this.pageId = nodeId;
            lastKey = -1;

            // initiate tree;
            this.table = new long[tableSize];
        }

        public int getNodeId()
        {
            return this.pageId;
        }

        void setNextSibling(Page nextSibling)
        {
            this.nextSibling = nextSibling;
        }

        public Page getNextSibling()
        {
            return this.nextSibling;
        }

        public Page getPreviousSibling()
        {
            return this.previousSibling;
        }

        public long pop()
        {
            return this.table[this.lastKey--];
        }

        public void push(long value)
        {
            this.table[++this.lastKey] = value;
        }

        public int getPosition()
        {
            return this.lastKey;
        }

        void remove()
        {
            previousSibling.setNextSibling( null );
            this.previousSibling = null;
            this.table = null;
        }
    }
}
