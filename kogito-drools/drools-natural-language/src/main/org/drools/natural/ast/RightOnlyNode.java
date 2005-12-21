package org.drools.natural.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.drools.natural.NaturalLanguageException;

/**
 * This represents an operation that only takes parameters to the right of it.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class RightOnlyNode extends ArgumentNode 
{

    private List args;
    private int numberOfArgs;    
    
    public RightOnlyNode(String originalVal, String valueFromDictionary, int numArgs) {
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
        AbstractSyntaxNode startArg = this.next;
        int numberOfArgsToGet = numberOfArgs - args.size();
        addToArguments( startArg,
                          numberOfArgsToGet, numberOfArgs, args, 
                          ArgumentNode.Direction.RIGHT );
    }


    
    public List getArguments() {
        return this.args;
    }


    public String render()
    {
        List vars = super.getVariableNameList();
        Properties variableVals = new Properties(); 
        for ( Iterator iter = vars.iterator( ); iter.hasNext( ); )
        {
            String varName = (String) iter.next( );
            if (varName.equals(ArgumentNode.RIGHT)) {
                variableVals.setProperty(varName, getArgForRightPositionVal("1", args));
            } else {                
                variableVals.setProperty(varName, getArgForRightPositionVal( varName, args ));
            }
        }
        
        return generateScript(variableVals);
    }
    
    

}
