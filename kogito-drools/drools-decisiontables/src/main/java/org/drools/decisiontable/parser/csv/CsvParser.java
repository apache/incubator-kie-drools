package org.drools.decisiontable.parser.csv;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.drools.decisiontable.parser.DecisionTableParseException;
import org.drools.decisiontable.parser.DecisionTableParser;
import org.drools.decisiontable.parser.SheetListener;

/**
 * Csv implementation.
 * This implementation removes empty "cells" at the end of each line.
 * Different CSV tools may or may not put heaps of empty cells in.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class CsvParser implements DecisionTableParser
{

    private SheetListener _listener;
    private CsvLineParser _lineParser;

    public CsvParser(SheetListener listener, CsvLineParser lineParser) {
        _listener = listener;
        _lineParser = lineParser;
    }
        
    public void parseFile(InputStream inStream) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inStream));
        try {
        	_listener.startSheet("csv");
        	processRows(reader);
        	_listener.finishSheet();
        } catch (IOException e) {
            throw new DecisionTableParseException("An error occurred reading the CSV data.", e);
        }
    }

	private void processRows(BufferedReader reader) throws IOException {
		String line = reader.readLine();
		
		int row = 0;
		while (line != null) {
			                        
		    List cells = _lineParser.parse(line);
            //remove the trailing empty "cells" which some tools smatter around
            //trimCells(cells);
		    _listener.newRow(row, cells.size());
		    
		    for (int col = 0; col < cells.size(); col++) {
		        String cell = (String) cells.get(col);
                
                _listener.newCell(row, col, cell);
		    }
		    row++;
			line = reader.readLine();
		}
	}

    /** remove the trailing empty cells */
    private void trimCells(List cells)
    {
        for (int i = cells.size() - 1; i > 0; i--) {
            String cell = (String) cells.get(i);
            if (!cell.trim().equals("")) {
                return;
            } else {
                cells.remove(i);
            }
        }
        
    }

}
