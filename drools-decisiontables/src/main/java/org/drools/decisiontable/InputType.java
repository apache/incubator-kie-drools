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

package org.drools.decisiontable;

import java.util.List;

import org.drools.decisiontable.parser.DecisionTableParser;
import org.drools.decisiontable.parser.csv.CsvLineParser;
import org.drools.decisiontable.parser.csv.CsvParser;
import org.drools.decisiontable.parser.xls.ExcelParser;
import org.drools.template.parser.DataListener;

/**
 * Provides valid input types for decision tables.
 * (which also serve as parser factories).
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
    public abstract DecisionTableParser createParser(DataListener listener);
    public abstract DecisionTableParser createParser(List<DataListener> listeners);

}

class XlsInput extends InputType {

    public DecisionTableParser createParser(final DataListener listener) {
        return new ExcelParser( listener );
    }
    public DecisionTableParser createParser(final List<DataListener> listeners) {
        return new ExcelParser( listeners );
    }

}

class CsvInput extends InputType {

    public DecisionTableParser createParser(final DataListener listener) {
        return new CsvParser( listener,
                              new CsvLineParser() );
    }

    public DecisionTableParser createParser(final List<DataListener> listeners) {
        return new CsvParser( listeners,
                new CsvLineParser() );
    }
    
}
