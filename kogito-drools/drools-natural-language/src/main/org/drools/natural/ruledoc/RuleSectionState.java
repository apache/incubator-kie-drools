package org.drools.natural.ruledoc;

import org.apache.commons.lang.StringUtils;

public class RuleSectionState extends ParseState
{
    
    private StringBuffer buf = new StringBuffer();
    private String name;
    
    
    public RuleSectionState(String name)  {
        this.buf.append(name + " ");
    }
    
    void parseChunk(String text)
    {
        buf.append(text);
    }

    public static boolean isStart(String text)
    {
        return StringUtils.contains(text, Keywords.getKeyword("rule.start"));
    }
    
    public static boolean isEnd(String text)
    {
        return StringUtils.contains(text, Keywords.getKeyword("rule.end"));
    }    

}
