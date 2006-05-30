package org.drools.decisiontable;

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

import org.drools.decisiontable.parser.DecisionTableParser;
import org.drools.decisiontable.parser.SheetListener;
import org.drools.decisiontable.parser.csv.CsvLineParser;
import org.drools.decisiontable.parser.csv.CsvParser;
import org.drools.decisiontable.parser.xls.ExcelParser;

/**
 * Provides valid input types for decision tables.
 * (which also serve as parser factories).
 * 
 * @author Michael Neale
 */
public abstract class InputType {
    public static final InputType XLS = new XlsInput();
    public static final InputType CSV = new CsvInput();

    protected InputType() {

    }

    /**
     * @param listener
     * @return The appropriate Parser. 
     */
    public abstract DecisionTableParser createParser(SheetListener listener);

}

class XlsInput extends InputType {

    public DecisionTableParser createParser(final SheetListener listener) {
        return new ExcelParser( listener );
    }

}

class CsvInput extends InputType {

    public DecisionTableParser createParser(final SheetListener listener) {
        return new CsvParser( listener,
                              new CsvLineParser() );
    }

}