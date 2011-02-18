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

package org.drools.clips.functions;

import org.drools.clips.Appendable;
import org.drools.clips.Function;
import org.drools.clips.FunctionHandlers;
import org.drools.clips.LispAtom;
import org.drools.clips.LispForm;
import org.drools.clips.SExpression;

public class IfFunction implements Function {
    private static final String name = "if";

    public String getName() {
        return name;
    }
    
    public void dump(LispForm lispForm, Appendable appendable) {
        SExpression[] sExpressions = lispForm.getSExpressions();

        appendable.append( "if " );
        
        FunctionHandlers.dump( sExpressions[1], appendable );
        
        appendable.append( "{" );
        int i = 3;
        for ( int length = sExpressions.length; i < length; i++ ) {
            SExpression sExpr = ( SExpression ) sExpressions[i];
            if ( ( sExpr instanceof LispAtom ) && "\"else\"".equals( ((LispAtom)sExpr).getValue() ) ) {
                i++;
                break;
            }
            FunctionHandlers.dump( sExpressions[i], appendable, true );
        }
        appendable.append( "}" );
        
        
        while ( i < sExpressions.length ) {
            appendable.append( " else {" );
            for ( int length = sExpressions.length; i < length; i++ ) {
                SExpression sExpr = ( SExpression ) sExpressions[i];
                if ( ( sExpr instanceof LispAtom ) && "\"else\"".equals( ((LispAtom)sExpr).getValue() ) ) {
                    i++;
                    break;
                }
                FunctionHandlers.dump( sExpressions[i], appendable, true );
            }
            appendable.append( "}" );
        }
    }
}
