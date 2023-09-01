package org.drools.decisiontable.parser;

import java.io.IOException;
import java.io.InputStream;
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
        final Map<String, List<DataListener>> sheetListeners = new HashMap<>();
        final RuleSheetListener listener = new DefaultRuleSheetListener();
        final List<DataListener> listeners = List.of(listener);
        sheetListeners.put(ExcelParser.DEFAULT_RULESHEET_NAME, listeners);
        final ExcelParser parser = new ExcelParser(sheetListeners);
        try {
            parser.parseFile(stream);
        } finally {
            stream.close();
        }
        return listener;
    }
}
