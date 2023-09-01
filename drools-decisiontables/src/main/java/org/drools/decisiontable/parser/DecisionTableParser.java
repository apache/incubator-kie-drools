package org.drools.decisiontable.parser;

import java.io.File;
import java.io.InputStream;

/**
 * Generic interface for all input parsers.
 */
public interface DecisionTableParser {
    /**
     * Parse an input stream, store the resulting rulebase.
     */
    void parseFile(InputStream inStream);

    /**
     * Parse a file, store the resulting rulebase.
     */
    void parseFile(File file);
}
