package org.drools.rule;

import java.util.ArrayList;
import java.util.List;

public class Exists  extends ConditionalElement
{
    public Object getChild()
    {
        return getChildren().get( 0 );
    }   
}
