package org.codehaus.jfdi.interpreter;

import java.math.BigDecimal;


public class BigDecimalOverloader
    implements
    OperatorOverloader {

    public Object divide(Object left,
                         Object right) {
        BigDecimal l = (BigDecimal) left;        
        return l.divide( (BigDecimal) right, BigDecimal.ROUND_HALF_UP );        
    }

    public Class getApplicableType() {        
        return BigDecimal.class;
    }

    public Object minus(Object left,
                        Object right) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object multiply(Object left,
                           Object right) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object plus(Object left,
                       Object right) {
        BigDecimal l = (BigDecimal) left;
        BigDecimal r = (BigDecimal) right;
        
        //hmmm... should the right allow a string or something else? 
        return l.add( r );
    }

}
