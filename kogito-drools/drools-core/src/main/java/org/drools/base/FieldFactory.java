package org.drools.base;

/*
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

import org.drools.spi.Evaluator;
import org.drools.spi.FieldValue;

public class FieldFactory {
    private static final FieldFactory INSTANCE = new FieldFactory();

    public static FieldFactory getInstance() {
        return FieldFactory.INSTANCE;
    }

    private FieldFactory() {

    }

    public static FieldValue getFieldValue(final String value,
                                           int valueType) {
        FieldValue field = null;
        if ( value == null ) {
            valueType = Evaluator.NULL_TYPE;
        }

        switch ( valueType ) {
            case Evaluator.NULL_TYPE :
                field = new FieldImpl( null );
                break;
            case Evaluator.CHAR_TYPE :
                field = new FieldImpl( new Character( value.charAt( 0 ) ) );
                break;
            case Evaluator.BYTE_TYPE :
                field = new FieldImpl( new Byte( value ) );
                break;
            case Evaluator.SHORT_TYPE :
                field = new FieldImpl( new Short( value ) );
                break;
            case Evaluator.INTEGER_TYPE :
                field = new FieldImpl( new Integer( value ) );
                break;
            case Evaluator.LONG_TYPE :
                field = new FieldImpl( new Long( value ) );
                break;
            case Evaluator.FLOAT_TYPE :
                field = new FieldImpl( new Float( value ) );
                break;
            case Evaluator.DOUBLE_TYPE :
                field = new FieldImpl( new Double( value ) );
                break;
            case Evaluator.BOOLEAN_TYPE :
                field = new FieldImpl( new Boolean( value ) );
                break;
            case Evaluator.STRING_TYPE :
                field = new FieldImpl( value.intern() );
                break;
            case Evaluator.DATE_TYPE :
                //MN: I think its fine like this, seems to work !
                field = new FieldImpl( value );
                break;
            case Evaluator.ARRAY_TYPE :
                //MN: I think its fine like this.
                field = new FieldImpl( value );
                break;
            case Evaluator.OBJECT_TYPE :
                field = new FieldImpl( value );
                break;
        }
        return field;
    }

}