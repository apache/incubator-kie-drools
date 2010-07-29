/**
 * Copyright 2005 JBoss Inc
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

package org.drools.lang.descr;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

public class LiteralDescr extends BaseDescr {
    public static final int   TYPE_NULL        = 1;
    public static final int   TYPE_NUMBER      = 2;
    public static final int   TYPE_STRING      = 3;
    public static final int   TYPE_BOOLEAN     = 4;

    private static final long serialVersionUID = 510l;
    private int               type;
    
    private String            text;

    public LiteralDescr(){
    }

    public LiteralDescr(final String text,
                             final int type) {
        this.text = text;
        this.type = type;
    }

    public String toString() {
        return "[LiteralValue: " + getValue().getClass() + " " + getValue() + "]";
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
    
    public String getText() {
        return this.text;
    }

    public Object getValue() {
        switch ( this.type ) {
            case TYPE_NUMBER :
                try {
                    // in the DRL, we always use US number formatting 
                    return DecimalFormat.getInstance(Locale.US).parse( this.getText() );
                } catch ( ParseException e ) {
                    // return String anyway
                    return this.getText();
                }
            case TYPE_BOOLEAN :
                return Boolean.valueOf( this.getText() );
            default :
                return this.getText();
        }
    }
}
