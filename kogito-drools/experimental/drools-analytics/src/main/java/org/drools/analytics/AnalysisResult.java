package org.drools.analytics;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.drools.spi.KnowledgeHelper;

/**
 * This is a class which contains lists of warnings and errors to be reported to the user via a user interface or static report of
 * some form. 
 * 
 * It uses the ReportItem class to contains rules/warnings as pertains to them. Display them or ignore them as you will.
 * 
 * @author Michael Neale
 *
 */
public class AnalysisResult implements Serializable {

    
    private static final long serialVersionUID = -6207688526236713721L;
    private List warnings = new ArrayList();
    private List errors = new ArrayList();
    
    public void addWarning(KnowledgeHelper k, String message) {
        warnings.add( new ReportItem(k.getRule().getName(), message) );
    }
    
    public void addError(KnowledgeHelper k, String message) {
        errors.add( new ReportItem(k.getRule().getName(), message) );
    }
    
    public List getWarnings() {
        return warnings;
    }
    
    static class ReportItem implements Serializable {
        private static final long serialVersionUID = 400L;
        public String rule;
        public String message;
        
        public ReportItem(String rule, String message) {
            this.rule = rule;
            this.message = message;
        }
        
        public String toString() {
            return rule + ": " + message;
        }
    }

    public List getErrors() {
        return errors;
    }
    
}
