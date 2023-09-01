package org.drools.decisiontable.parser;

import org.drools.decisiontable.parser.xls.PropertiesSheetListener.CaseInsensitiveMap;
import org.drools.template.model.Package;
import org.drools.template.parser.DataListener;

/**
 *
 * SheetListener used for creating rules
 */
public interface RuleSheetListener extends DataListener {

    /**
     * Return the rule sheet properties
     */
    public abstract CaseInsensitiveMap getProperties();

    /**
     * Build the final ruleset as parsed.
     */
    public abstract Package getRuleSet();

}
