package org.drools.natural.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/**
 * This represents an expression that only takes arguments to the left of it.
 * 
 * It is important to note that the arguments are added to the list in order from CLOSEST
 * to the expression to furthest. Users number these with ${-1}, ${-2} style...
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class LeftInfix extends InfixNode
{

    private List args;
    private int numberOfArgs;
     
    
    public LeftInfix(String originalVal, String valueFromDictionary, int numArgs) {
        super.originalValue = originalVal;
        args = new ArrayList();
        numberOfArgs = numArgs;
        expressionFromDictionary = valueFromDictionary;
    }    
    
    public boolean isSatisfied()
    {
        return args.size() == numberOfArgs;        
    }

    protected void process()
    {                
        BaseSyntaxNode startArg = this.prev;
        int numberOfArgsToGet = numberOfArgs - args.size();
        addToArguments( startArg,
                          numberOfArgsToGet, numberOfArgs, args, 
                          InfixNode.Direction.LEFT );
    }


    
    List getArguments() {
        return this.args;
    }
    
    public String render()
    {
        List vars = super.getVariableNameList();
        Properties variableVals = new Properties(); 
        for ( Iterator iter = vars.iterator( ); iter.hasNext( ); )
        {
            String varName = (String) iter.next( );
            if (varName.equals(InfixNode.LEFT)) {
                variableVals.setProperty(varName, getArgForLeftPositionVal( "-1", args ));
            } else {                
                variableVals.setProperty(varName, getArgForLeftPositionVal( varName, args ));
            }
        }
        return generateScript(variableVals);
    }


    

}
