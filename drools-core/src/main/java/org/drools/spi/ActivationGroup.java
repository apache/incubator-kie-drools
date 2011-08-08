/*
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

package org.drools.spi;

import java.util.Iterator;

import org.drools.core.util.Entry;
import org.drools.core.util.LinkedList;

public interface ActivationGroup
    extends
    org.drools.runtime.rule.ActivationGroup  {
    public String getName();

    public void addActivation(Activation activation);

    public void removeActivation(Activation activation);
    
    public LinkedList getList();

    public Iterator iterator();

    public boolean isEmpty();

    public int size();

    public void clear();
}
