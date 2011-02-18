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

package org.drools.util.asm;

public class MethodCompareA {

    public boolean evaluate(final String foo) {
        if ( foo == null || foo.startsWith( "42" ) ) {
            return true;
        } else {
            return false;
        }
    }

    public boolean askew(final Integer a) {
        return false;
    }

    public boolean evaluate2(final String foo) {
        if ( foo == null || foo.startsWith( "43" ) ) {
            return true;
        } else {
            return false;
        }
    }

}
