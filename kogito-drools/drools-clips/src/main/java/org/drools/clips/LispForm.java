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

package org.drools.clips;

public class LispForm
 implements SExpression {
    private SExpression[] sExpressions;
    
    public LispForm(SExpression[] sExpressions) {
        this.sExpressions = sExpressions;
    }

    public SExpression[] getSExpressions() {
        return sExpressions;
    }

    public void setSExpressions(SExpression[] sExpressions) {
        this.sExpressions = sExpressions;
    }
    
//    public String toString() {
//        StringBuilder builder = new StringBuilder();
//        
//        builder.append("(");
//        for ( int i = 0, length = sExpressions.length; i < length; i++) {
//            builder.append(" " + sExpressions[i] + " ");
//        }
//        builder.append(")");
//        return builder.toString();
//    }
    public int size() {
        return this.sExpressions.length;
    }
    public String toString() {
        StringBuilder builder = new StringBuilder();
        
        builder.append("(");
        for ( int i = 0, length = sExpressions.length; i < length; i++ ) {
            builder.append(sExpressions[i]);
            if ( i < length - 1 ) {
                builder.append( " " );
            }
        }
        builder.append(")");
        return builder.toString();
    }
    
}
