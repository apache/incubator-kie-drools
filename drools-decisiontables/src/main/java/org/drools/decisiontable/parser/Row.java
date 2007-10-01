package org.drools.decisiontable.parser;

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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * @author <a href="mailto:stevearoonie@gmail.com">Steven Williams</a>
 *  
 * Represents a row in a decision table.
 */
public class Row {
    int  rowNum;
    List cells = new ArrayList();

    public Row() {
    	
    }
    
    Row(int r, Column[] columns) {
        rowNum = r;
        for (int i = 0; i < columns.length; i++) {
			cells.add(columns[i].createCell(this));
		}
    }
    
    public int getRowNumber() {
        return rowNum;
    }

    Cell getCell(int columnIndex)
    {
    	return (Cell) cells.get(columnIndex);
    }
    
    boolean isEmpty() {
    	for (Iterator it = cells.iterator(); it.hasNext();) {
			Cell cell = (Cell) it.next();
			if (!cell.isEmpty())
			{
				return false;
			}
		}
    	return true;
    }
    
    public List getCells() {
    	return cells;
    }

    public String toString() {
        return "Row " + rowNum + cells + "\n";
    }


}