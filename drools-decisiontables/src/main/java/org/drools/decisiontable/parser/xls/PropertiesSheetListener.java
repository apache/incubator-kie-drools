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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.decisiontable.parser.RuleSheetParserUtil;
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
public class PropertiesSheetListener implements DataListener {

    private static final String EMPTY_STRING   = "";

    private final Map<Integer, String[]> _rowProperties = new HashMap<Integer, String[]>();

    private final CaseInsensitiveMap _properties = new CaseInsensitiveMap();

    /**
     * Return the key value pairs. If this is called before the sheet is
     * finished, then it will build the properties map with what is known.
     * Subsequent calls will update the properties map.
     *
     * @return properties
     */
    public CaseInsensitiveMap getProperties() {
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
        	this._properties.addProperty( keyValue[0], new String[]{ keyValue[1], keyValue[2] } );
        }
        // Discard to avoid repeated addition of properties,
        // since finishSheet may be called more than once.
        _rowProperties.clear();
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
        		keyValue[2] = RuleSheetParserUtil.rc2name(row, column);
        	}
        } else {
        	final String[] keyValue = {value, PropertiesSheetListener.EMPTY_STRING, RuleSheetParserUtil.rc2name(row, column+1) };
        	this._rowProperties.put( rowInt, keyValue );
        }
    }

    private boolean emptyCellValue(final String value) {
        return value == null || value.trim().equals( "" );
    }

    @SuppressWarnings("serial")
    public static class CaseInsensitiveMap extends HashMap<String,List<String[]>> {

        private List<String[]> getPropertyCell(String key) {
        	return super.get( key.toLowerCase() );
        }

        public void addProperty( String key, String[] value ){
        	key = key.toLowerCase();
        	List<String[]> r  = getPropertyCell( key );
        	if( r == null ){
        		r = new ArrayList<String[]>();
        	}
        	r.add( value );
        	super.put( key, r );
        }

        private List<String> getList( String key, int index ) {
        	List<String[]> pcList  = getPropertyCell( key );
            if( pcList == null ) return null;
        	List<String> r = new ArrayList<String>();
        	for( String[] pc: pcList ){
        		r.add( pc[index] );
        	}
        	return r;
        }

        public List<String> getProperty(String key) {
        	return getList( key, 0 );
        }

        public List<String> getPropertyCells(String key) {
        	return getList( key, 1 );
        }

        private String getSingle( String key, int index ){
        	List<String[]> r  = getPropertyCell( key );
        	if( r == null || r.size() == 0 ) return null;
        	return r.get( 0 )[index];
        }

        public String getSingleProperty( String key ){
        	return getSingle( key, 0 );
        }

        public String getSinglePropertyCell( String key ){
        	return getSingle( key, 1 );
        }

        public String getSingleProperty( String key, String defaultValue ){
        	String r = getSingleProperty( key );
        	if( r == null || r == "" ) r = defaultValue;
        	return r;
        }

    }
}

