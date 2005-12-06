package org.drools.rule;

import java.util.ArrayList;
import java.util.List;

public class Exist  extends ConditionalElement
{
    public Object getChild()
    {
        return getChildren().get( 0 );
    }   
}
