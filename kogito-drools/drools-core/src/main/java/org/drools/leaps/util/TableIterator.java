package org.drools.leaps.util;

/*
 * Copyright 2005 Alexander Bagerman
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

import java.util.Iterator;

/**
 * Leaps specific iterator for leaps tables. relies on leaps table double link
 * list structure for navigating
 * 
 * @author Alexander Bagerman
 * 
 */
public interface TableIterator extends Iterator {
    /**
     * single object iterator
     * 
     * @return table iterator
     */
    public boolean isEmpty();

    public void reset();

    public boolean hasNext();

    public Object next();

    public Object peekNext();

    public void remove();
}
