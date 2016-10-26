/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.phreak;

import java.util.Iterator;

public interface PropagationList {
    void addEntry(PropagationEntry propagationEntry);

    PropagationEntry takeAll();

    void flush();
    void flush( PropagationEntry currentHead );

    void reset();

    boolean isEmpty();

    Iterator<PropagationEntry> iterator();

    void waitOnRest();

    void notifyWaitOnRest();

    void onEngineInactive();

    void dispose();
}
