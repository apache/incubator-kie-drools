package org.drools.leaps.util;

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

import java.util.EmptyStackException;

import org.drools.leaps.Token;
import org.drools.util.PrimitiveLongMap;

/**
 * This class follows java.util.Stack interface but accounts 
 * for remove( object ) functionallity needed by leaps
 * 
 * @author Alexander Bagerman
 * 
 */

public class TokenStack {

    protected TableRecord tailRecord = null;

    private PrimitiveLongMap map = new PrimitiveLongMap();
    
    public TokenStack() {
        
    }

    public boolean empty() {
        return this.tailRecord == null;
    }
    
    public Object peek() {
        if(this.tailRecord != null){
            return this.tailRecord.object;
        }
        else {
            throw new EmptyStackException();
        }
    }
    
    public Object pop()  {
        if(this.tailRecord != null){
            Object ret = this.tailRecord.object;
            TableRecord buf = this.tailRecord; 
            this.tailRecord = buf.left;
            if (buf.left != null) {
                this.tailRecord.right = null;
            }
            buf.left = null;

            map.remove(((Token) ret).getDominantFactHandle().getId());
            return ret;
        }
        else {
            throw new EmptyStackException();
        }
    }
    

    /**
     * Removes object from the table
     * 
     * @param object
     *            to remove from the table
     */
    public void remove(long factId) {
        if (this.tailRecord != null) {
            TableRecord record = (TableRecord) this.map.remove(factId);

            if (record != null) {
                if (record == this.tailRecord) {
                    this.tailRecord = record.left;
                }
                if (record.left != null) {
                    record.left.right = record.right;
                }
                if (record.right != null) {
                    record.right.left = record.left;
                }
                record.left = null;
                record.right = null;
            }
        }
    }
    public Object push(Object item) {
        TableRecord record = new TableRecord(item);
        if (this.tailRecord != null) {
            this.tailRecord.right = record;
            record.left = this.tailRecord;
        }
        this.tailRecord = record;
        
        this.map.put(((Token)item).getDominantFactHandle().getId(), record);
        return item;
    }
}
