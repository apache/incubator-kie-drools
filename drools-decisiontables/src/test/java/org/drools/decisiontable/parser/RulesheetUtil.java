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

package org.drools.decisiontable.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.decisiontable.parser.xls.ExcelParser;
import org.drools.template.parser.DataListener;

public class RulesheetUtil {

    /**
     * Utility method showing how to get a rule sheet listener from a stream.
     */
    public static RuleSheetListener getRuleSheetListener(final InputStream stream) throws IOException {
        final Map<String, List<DataListener>> sheetListeners = new HashMap<String, List<DataListener>>();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        final RuleSheetListener listener = new DefaultRuleSheetListener();
        listeners.add(listener);
        sheetListeners.put( ExcelParser.DEFAULT_RULESHEET_NAME,
                      listeners );
        final ExcelParser parser = new ExcelParser( sheetListeners );
        try {
            parser.parseFile( stream );
        } finally {
            stream.close();
        }
        stream.close();
        return listener;
    }
}
