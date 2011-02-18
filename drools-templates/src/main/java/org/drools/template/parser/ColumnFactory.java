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

package org.drools.template.parser;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:stevearoonie@gmail.com">Steven Williams</a>
 * 
 * Factory to produce a column of the correct type based on its declaration.
 * [] indicates a column that represents an array (comma-delimited) of values.
 */
public class ColumnFactory {
    private final static Pattern PATTERN = Pattern.compile( "([a-zA-Z0-9_]*)(\\[\\])?(:\\s*([a-zA-Z]*)(\\[\\])?)?" );

    public Column getColumn(String value) {
        Matcher m = PATTERN.matcher( value );
        if ( !m.matches() ) throw new IllegalArgumentException( "value " + value + " is not a valid column definition" );
        String name = m.group( 1 );
        String type = m.group( 4 );
        type = type == null ? "String" : type;
        boolean array = (m.group( 2 ) != null) || (m.group( 5 ) != null);
        if ( array ) {
            return new ArrayColumn( name,
                                    createColumn( name,
                                                  type ) );
        }
        return createColumn( name,
                             type );
    }

    @SuppressWarnings("unchecked")
    private Column createColumn(String name,
                                String type) {
        try {
            Class<Column> klass = (Class<Column>) Class.forName( this.getClass().getPackage().getName() + "." + type + "Column" );
            Constructor<Column> constructor = klass.getConstructor( new Class[]{String.class} );
            return constructor.newInstance( new Object[]{name} );
        } catch ( SecurityException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        } catch ( NoSuchMethodException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        } catch ( IllegalArgumentException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        } catch ( InstantiationException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        } catch ( IllegalAccessException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        } catch ( InvocationTargetException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }
}
