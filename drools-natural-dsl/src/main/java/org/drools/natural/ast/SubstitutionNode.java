package org.drools.natural.ast;

/**
 * This node type simply swaps out the users value with the one from the dictionary.
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class SubstitutionNode extends BaseSyntaxNode
{
    
    public String expressionFromDictionary;
    

    public SubstitutionNode(String originalVal, String valFromDictionary) {
        super.originalValue = originalVal;
        this.expressionFromDictionary = valFromDictionary;
    }
    
    public boolean isSatisfied()
    {
        return true;
    }

    protected void process()
    {
        
    }

    public String render()
    {
        
        if (this.expressionFromDictionary.startsWith("<<")) {
            return expressionFromDictionary.substring(2);
        } else {
            if (prev == null) {
                return expressionFromDictionary;
            } else 
            {
                return SPACE + expressionFromDictionary;
            }
        }        
    }
}
