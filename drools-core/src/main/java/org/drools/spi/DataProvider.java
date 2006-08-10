package org.drools.spi;

import java.util.Iterator;
import java.util.List;

import org.drools.rule.Declaration;

public interface DataProvider {
    
    public Declaration[] getRequiredDeclarations();
    
   public Iterator getResults(Tuple tuple); 
}
