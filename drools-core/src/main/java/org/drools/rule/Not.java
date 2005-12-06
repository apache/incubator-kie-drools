package org.drools.rule;

import java.util.ArrayList;
import java.util.List;

public class Not  extends ConditionalElement
{
    public Object getChild()
    {
        return getChildren().get( 0 );
    }
    
}
