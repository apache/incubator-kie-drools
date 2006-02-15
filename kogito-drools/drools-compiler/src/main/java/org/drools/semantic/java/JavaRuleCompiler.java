package org.drools.semantic.java;

import java.util.Iterator;

import org.apache.commons.jci.readers.ResourceReader;
import org.drools.lang.descr.AndDescr;
import org.drools.lang.descr.ColumnDescr;
import org.drools.lang.descr.ConsequenceDescr;
import org.drools.lang.descr.EvalDescr;
import org.drools.lang.descr.NotDescr;
import org.drools.lang.descr.OrDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.PredicateDescr;
import org.drools.rule.ConditionalElement;
import org.drools.rule.Rule;

public class JavaRuleCompiler {    
    private final RuleSetBundle ruleSetBundle;
    
    public JavaRuleCompiler(RuleSetBundle ruleSetBundle) {
        this.ruleSetBundle = ruleSetBundle;
    }
    
    public void configure(Rule rule, AndDescr lhs, ConsequenceDescr rhs) {
        for (Iterator it = lhs.getDescrs().iterator(); it.hasNext; ) {
            
        }
    }
    
    private void configure(ConditionalElement ce, AndDescr and) {
        
    }
    
    private void configure(ConditionalElement ce, OrDescr and) {
        
    }
    
    private void configure(ConditionalElement ce, EvalDescr and) {
        // generate method
        // generate invoker
    }    
    
    private void configure(ConditionalElement ce, ConsequenceDescr and) {
        // generate method
        // generate invoker
    }    
    
    private void configure(ConditionalElement ce, PredicateDescr and) {
        // generate method
        // generate invoker
    }     
    
    /**
     * Takes a given name and makes sure that its legal and doesn't already exist. If the file exists it increases counter appender untill it is unique.
     * 
     * @param packageName
     * @param name
     * @param ext
     * @return
     */
    private String generateUniqueLegalName(String packageName,
                                           String name,
                                           String ext)
    {
        // replaces the first char if its a number and after that all non
        // alphanumeric or $ chars with _
        String newName = name.replaceAll( "(^[0-9]|[^\\w$])",
                                          "_" );

        // make sure the class name does not exist, if it does increase the counter
        int counter = -1;
        boolean exists = true;
        while ( exists )
        {
            counter++;
            String fileName = packageName.replaceAll( "\\.",
                                                      "/" ) + newName + "_" + counter + ext;
            this.ruleSetBundle.getMemoryResourceReader().isAvailable(  fileName );
        }
        // we have duplicate file names so append counter
        if ( counter >= 0 )
        {
            newName = newName + "_" + counter;
        }

        return newName;
    }    
}
