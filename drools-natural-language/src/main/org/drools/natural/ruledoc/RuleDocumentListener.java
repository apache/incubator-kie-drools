package org.drools.natural.ruledoc;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a simple rule listener implementation. Doesn't do anything clever, just extracts the rules.
 * Leaves everything else alone.
 * No special treatment of tables.
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 */
public class RuleDocumentListener 
{

    private boolean inComment;
    private List    rules;
    private StringBuffer ruleBuffer;
    private boolean inRule;
    
    public RuleDocumentListener() {
        rules = new ArrayList();
    }

    public void handleText(String text)
    {
        if (text.trim().startsWith(Keywords.getKeyword("rule.start"))) {
            startNewRule(text);
        } else if (text.trim().endsWith(Keywords.getKeyword("rule.end"))) {
            finishCurrentRule(text);
        } else if (inComment) { 
            return;
        } else if (inRule) {
            ruleBuffer.append(text);
        }
        
    }

    private void finishCurrentRule(String text)
    {
        ruleBuffer.append(text);
        rules.add(ruleBuffer.toString());    
        inRule = false;
    }

    private void startNewRule(String text)
    {
        ruleBuffer = new StringBuffer();
        ruleBuffer.append(text);
        inRule = true;
    }

    public void startTable()
    {
        // TODO Auto-generated method stub
        
    }

    public void startColumn()
    {
        // TODO Auto-generated method stub
        
    }

    public void startRow()
    {
        // TODO Auto-generated method stub
        
    }

    public void endTable()
    {
        // TODO Auto-generated method stub
        
    }

    public void startComment()
    {
        this.inComment = true;                
    }

    public void endComment()
    {
        this.inComment = false;              
    }
    
    public List getRules() {
        return rules;
    }
    

}
