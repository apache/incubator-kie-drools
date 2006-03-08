package org.drools.decisiontable.parser.xls;


/*
 * Copyright 2005 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a registered trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company.
 * (http://drools.werken.com/).
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */



import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.drools.decisiontable.parser.SheetListener;

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
    SheetListener
{

    private static final String EMPTY_STRING   = "";

    private Map                 _rowProperties = new HashMap( );

    private Properties          _properties    = new Properties( );

    /**
     * Return the key value pairs. If this is called before the sheet is
     * finished, then it will build the properties map with what is known.
     * Subsequent calls will update the properties map.
     * 
     * @return properties
     */
    public Properties getProperties()
    {
        finishSheet( ); // MN allows this to be called before the sheet is
        // finished, as
        // some properties are used whilst the sheet is being parsed.
        return _properties;
    }

    /*
     * (non-Javadoc)
     * 
     * @see my.hssf.util.SheetListener#startSheet(java.lang.String)
     */
    public void startSheet(String name)
    {
    }

    /*
     * (non-Javadoc)
     * 
     * @see my.hssf.util.SheetListener#finishSheet()
     */
    public void finishSheet()
    {
        for ( Iterator iter = _rowProperties.keySet( ).iterator( ); iter.hasNext( ); )
        {
            String[] keyValue = (String[]) _rowProperties.get( iter.next( ) );
            _properties.setProperty( keyValue[0],
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
    public void newRow(int rowNumber,
                       int columns)
    {
        // nothing to do.
    }

    /*
     * (non-Javadoc)
     * 
     * @see my.hssf.util.SheetListener#newCell(int, int, java.lang.String)
     */
    public void newCell(int row,
                        int column,
                        String value)
    {
        if (emptyCellValue( value )) {
            return;
        }
        Integer rowInt = new Integer( row );
        if ( _rowProperties.containsKey( rowInt ) )
        {
            String[] keyValue = (String[]) _rowProperties.get( rowInt );

            if ( keyValue[1] == EMPTY_STRING )
            {
                keyValue[1] = value;
            }
        }
        else
        {
            String[] keyValue = {value, EMPTY_STRING};
            _rowProperties.put( rowInt,
                                keyValue );
        }
    }

    private boolean emptyCellValue(String value)
    {
        return value == null || value.trim().equals("");
    }

}

