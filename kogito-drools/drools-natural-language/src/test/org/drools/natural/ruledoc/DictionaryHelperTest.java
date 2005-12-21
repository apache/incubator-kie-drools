package org.drools.natural.ruledoc;

import java.io.IOException;
import java.util.Properties;

import junit.framework.TestCase;

public class DictionaryHelperTest extends TestCase
{
    public void testLoad() throws IOException {
        
        Properties props = new Properties();
        props.load(getClass().getResourceAsStream("sample.dictionary.properties"));
        DictionaryHelper dic = new DictionaryHelper(props);
        assertEquals("convertToDate(\"${right}\")", dic.getItem("date of"));
    }
}
