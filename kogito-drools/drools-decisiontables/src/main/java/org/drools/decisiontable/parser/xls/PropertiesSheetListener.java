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

package org.drools.decisiontable.parser.xls;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.drools.template.parser.DataListener;

/**
 * Reads an Excel sheet as key-value properties.
 *
 * Treats the first non-empty cell on a row as a key and any subsequent
 * non-empty cell as a value. Any cells defined after the second cell are
 * ignored as comments.
 *
 * Could be easily adapted to accept multiple values per key but the semantics
 * were kept in line with Properties.
 *
 * @author <a href="mailto:shaun.addison@gmail.com"> Shaun Addison </a>
 *
 */
public class PropertiesSheetListener
    implements
    DataListener {

    private static final String EMPTY_STRING   = "";

    private final Map<Integer, String[]> _rowProperties = new HashMap<Integer, String[]>();

    private final Properties  _properties = new CaseInsensitiveMap();

    /**
     * Return the key value pairs. If this is called before the sheet is
     * finished, then it will build the properties map with what is known.
     * Subsequent calls will update the properties map.
     *
     * @return properties
     */
    public Properties getProperties() {
        finishSheet(); // MN allows this to be called before the sheet is
        // finished, as
        // some properties are used whilst the sheet is being parsed.
        return this._properties;
    }

    /*
     * (non-Javadoc)
     *
     * @see my.hssf.util.SheetListener#startSheet(java.lang.String)
     */
    public void startSheet(final String name) {
    }

    /*
     * (non-Javadoc)
     *
     * @see my.hssf.util.SheetListener#finishSheet()
     */
    public void finishSheet() {
        for ( String[] keyValue : _rowProperties.values() ) {
            this._properties.put( keyValue[0],
                                     keyValue[1] );
        }
    }

    /**
     * Enter a new row. This is ignored.
     *
     * @param rowNumber
     *            The row number.
     * @param columns
     *            The Colum number.
     */
    public void newRow(final int rowNumber,
                       final int columns) {
        // nothing to do.
    }

    /*
     * (non-Javadoc)
     *
     * @see my.hssf.util.SheetListener#newCell(int, int, java.lang.String)
     */
    public void newCell(final int row,
                        final int column,
                        final String value,
                        final int mergedColStart) {
        if ( emptyCellValue( value ) ) {
            return;
        }
        final Integer rowInt = new Integer( row );
        if ( this._rowProperties.containsKey( rowInt ) ) {
            final String[] keyValue = (String[]) this._rowProperties.get( rowInt );

            if ( keyValue[1] == PropertiesSheetListener.EMPTY_STRING ) {
                keyValue[1] = value;
            }
        } else {
            final String[] keyValue = {value, PropertiesSheetListener.EMPTY_STRING};
            this._rowProperties.put( rowInt,
                                keyValue );
        }
    }

    private boolean emptyCellValue(final String value) {
        return value == null || value.trim().equals( "" );
    }

    @SuppressWarnings("serial")
    public static class CaseInsensitiveMap extends Properties {


    	@Override
    	public String getProperty(String key) {
    		if (this.containsKey(key)) {
    			return super.getProperty(key);
    		}
    		return get(key);
    	}

    	@Override
    	public String getProperty(String key, String defaultValue) {
    		String r  = getProperty(key);
    		return (r != null) ? r : defaultValue;
    	}

    	private String get(String key) {
    	    Enumeration< ? > keyNames = this.propertyNames();
            while ( keyNames.hasMoreElements() ) {
                String k = (String) keyNames.nextElement();
                if (key.equalsIgnoreCase( k )) {
                    return super.getProperty( k );
                }
                    
            }
    		return null;
    	}

    }

}

