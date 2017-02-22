/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.util.debug;

import java.util.ArrayList;
import java.util.function.Consumer;

public class DebugList<T> extends ArrayList<T> {
    public Consumer<DebugList<T>> onItemAdded;

    @Override
    public synchronized boolean add( T t ) {
        System.out.println( Thread.currentThread() + " adding " + t );
        boolean result = super.add( t );
        if (onItemAdded != null) {
            onItemAdded.accept( this );
        }
        return result;
    }
}
