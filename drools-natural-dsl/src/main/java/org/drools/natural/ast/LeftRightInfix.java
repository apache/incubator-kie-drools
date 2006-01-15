package org.drools.natural.ast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

public class LeftRightInfix extends InfixNode
{

    private int  numArgsToLeft;
    private int  numArgsToRight;

    private List argsLeft;
    private List argsRight;

    public LeftRightInfix(String originalVal,
                         String valFromDictionary,
                         int numArgsToLeft,
                         int numArgsToRight)
    {
        this.numArgsToRight = numArgsToRight;
        this.numArgsToLeft = numArgsToLeft;
        super.originalValue = originalVal;
        this.expressionFromDictionary = valFromDictionary;

        this.argsLeft = new ArrayList( );
        this.argsRight = new ArrayList( );
    }

    public boolean isSatisfied()
    {
        return (argsLeft.size( ) == numArgsToLeft && argsRight.size( ) == numArgsToRight);
    }

    protected void process()
    {
        processRight( );
        processLeft( );
    }

    private void processLeft()
    {
        int numberOfArgsToGet = numArgsToLeft - argsLeft.size( );
        super.addToArguments( this.prev,
                              numberOfArgsToGet,
                              numArgsToLeft,
                              argsLeft,
                              InfixNode.Direction.LEFT );
    }

    private void processRight()
    {
        int numberOfArgsToGet = numArgsToRight - argsRight.size( );
        super.addToArguments( this.next,
                              numberOfArgsToGet,
                              numArgsToRight,
                              argsRight,
                              InfixNode.Direction.RIGHT );
    }

    List getArgumentsLeft()
    {
        return this.argsLeft;
    }

    List getArgumentsRight()
    {
        return this.argsRight;
    }
    

    public String render()
    {
        List vars = super.getVariableNameList( );
        Properties variableVals = new Properties( );
        for ( Iterator iter = vars.iterator( ); iter.hasNext( ); )
        {
            String varName = (String) iter.next( );
            if ( varName.equals( InfixNode.LEFT ) )
            {
                variableVals.setProperty( varName,
                                          getArgForLeftPositionVal( "-1",
                                                                    this.argsLeft ) );
            }
            else if ( varName.equals( InfixNode.RIGHT ) )
            {
                variableVals.setProperty( varName,
                                          getArgForRightPositionVal( "1",
                                                                     this.argsRight ) );
            }
            else if ( varName.startsWith( "-" ) )
            {
                variableVals.setProperty( varName,
                                          getArgForLeftPositionVal( varName,
                                                                    this.argsLeft ) );

            }
            else
            {
                variableVals.setProperty( varName,
                                          getArgForRightPositionVal( varName,
                                                                     this.argsRight ) );
            }
        }
        return generateScript( variableVals );
        
    }

}
