package org.drools.decisiontable.parser.csv;
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