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

package org.drools.compiler.phreak;

public class Pair {
    private Object o1;
    private Object o2;
    
    public Pair(Object o1,
                Object o2) {
        super();
        this.o1 = o1;
        this.o2 = o2;
    }
    
    public static Pair t(Object o1, Object o2) {
        return new Pair(o1, o2);
    }
            
    
    public Object getO1() {
        return o1;
    }
    
    public Object getO2() {
        return o2;
    }
    
}
