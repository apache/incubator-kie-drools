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

package org.drools.verifier.components;

import org.drools.verifier.report.components.Cause;

public interface Consequence
    extends
    Cause {

    public static class ConsequenceType {
        public static final ConsequenceType TEXT = new ConsequenceType( "TEXT" );

        public String                       type;

        public ConsequenceType(String t) {
            type = t;
        }

        public String toString() {
            return type;
        }
    }

    public String getPath();

    public ConsequenceType getConsequenceType();

    public String getRulePath();
    
    public String getRuleName();
}
