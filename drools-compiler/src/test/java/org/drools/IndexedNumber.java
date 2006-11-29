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

package org.drools;

/**
 * IndexedNumber
 *
 * Created: 22/06/2006
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a> 
 *
 * @version $Id$
 */

public class IndexedNumber {
    
    private int number = 0;
    
    private int index = 0;
    
    private boolean printed;
    
    public IndexedNumber() {
    }

    /**
     * @param pNb
     * @param pIndex
     */
    public IndexedNumber(int pNb, int pIndex) {
        number = pNb;
        index = pIndex;
    }

    /**
     * @return le/la/les index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * @param pIndex le/la/les index à sauvegarder.
     */
    public void setIndex(int pIndex) {
        index = pIndex;
    }

    /**
     * @return le/la/les nb.
     */
    public int getNumber() {
        return number;
    }

    /**
     * @param pNb le/la/les nb à sauvegarder.
     */
    public void setNumber(int pNb) {
        number = pNb;
    }
    
    /**
     * {@inheritDoc}
     */
    public String toString() {
        return "IndexedNumber[ " + number + ", " + index + " ]";
    }

    public boolean isPrinted() {
        return printed;
    }

    public void setPrinted(boolean printed) {
        this.printed = printed;
    }
    

}
