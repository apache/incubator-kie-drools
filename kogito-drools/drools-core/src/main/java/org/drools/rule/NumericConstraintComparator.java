package org.drools.rule;

import org.drools.spi.ConstraintComparator;

public class NumericConstraintComparator
    implements
    ConstraintComparator
{
    public final int type;

    public NumericConstraintComparator(int type)
    {
        this.type = type;
    }

    public boolean compare(Object object1,
                           Object object2)
    {
        boolean value = false;
        if ( object1 instanceof Integer )
        {
            value = compare( ( (Integer) object1).intValue(),
                             ( (Integer) object2).intValue() );
        }
        else if (object1 instanceof Float )
        {
            value = compare( ( (Float) object1).intValue(),
                             ( (Float) object2).intValue() );            
        }
        else if (object1 instanceof Double )
        {
            value = compare( ( (Double) object1).intValue(),
                             ( (Double) object2).intValue() );
        }     
        
        return value;
    }
    
    public boolean compare( int n1, int n2)
    {
        boolean value = false;
        switch ( this.type )
        {
        case ConstraintComparator.EQUAL :
            value = ( n1 == n2 );
            break;
        case ConstraintComparator.NOT_EQUAL :
            value = ( n1 != n2 );
            break;
        case ConstraintComparator.LESS :
            value = ( n1 < n2 );
            break;
        case ConstraintComparator.LESS_OR_EQUAL:
            value = ( n1 <= n2 );
            break;
        case ConstraintComparator.GREATER :
            value = ( n1 > n2 );
            break; 
        case ConstraintComparator.GREATER_OR_EQUAL:
            value = ( n1 >= n2 );
            break;             
        }
        return value;
    }
    
    public boolean compare( float n1, float n2)
    {
        boolean value = false;
        switch ( this.type )
        {
        case ConstraintComparator.EQUAL :
            value = ( n1 == n2 );
            break;
        case ConstraintComparator.NOT_EQUAL :
            value = ( n1 != n2 );
            break;
        case ConstraintComparator.LESS :
            value = ( n1 < n2 );
            break;
        case ConstraintComparator.LESS_OR_EQUAL:
            value = ( n1 <= n2 );
            break;
        case ConstraintComparator.GREATER :
            value = ( n1 > n2 );
            break; 
        case ConstraintComparator.GREATER_OR_EQUAL:
            value = ( n1 >= n2 );
            break;             
        }
        return value;
    }    
    
    public boolean compare( double n1, double n2)
    {
        boolean value = false;
        switch ( this.type )
        {
        case ConstraintComparator.EQUAL :
            value = ( n1 == n2 );
            break;
        case ConstraintComparator.NOT_EQUAL :
            value = ( n1 != n2 );
            break;
        case ConstraintComparator.LESS :
            value = ( n1 < n2 );
            break;
        case ConstraintComparator.LESS_OR_EQUAL:
            value = ( n1 <= n2 );
            break;
        case ConstraintComparator.GREATER :
            value = ( n1 > n2 );
            break; 
        case ConstraintComparator.GREATER_OR_EQUAL:
            value = ( n1 >= n2 );
            break;             
        }
        return value;
    }     

}
