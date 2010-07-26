/**
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

package org.drools.base;

public class SimpleValueType {
    public static final int UNKNOWN  = 0;
    public static final int NULL     = 1;
    public static final int BOOLEAN  = 2;
    public static final int NUMBER   = 3;
    public static final int INTEGER  = 4;
    public static final int DECIMAL  = 5;
    public static final int CHAR     = 6;
    public static final int STRING   = 7;
    public static final int DATE     = 8;
    public static final int LIST     = 9;
    public static final int OBJECT   = 10;
    public static final int FUNCTION = 11; //This one is for LISP
}
