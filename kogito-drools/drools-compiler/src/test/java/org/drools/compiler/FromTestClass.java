/*
 * Copyright 2015 JBoss Inc
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

package org.drools.compiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FromTestClass {
    public List toList(final Object object1,
                       final Object object2,
                       final String object3,
                       final int integer,
                       final Map map,
                       final List inputList) {
        final List list = new ArrayList();
        list.add( object1 );
        list.add( object2 );
        list.add( object3 );
        list.add( new Integer( integer ) );
        list.add( map );
        list.add( inputList );
        return list;
    }
}
