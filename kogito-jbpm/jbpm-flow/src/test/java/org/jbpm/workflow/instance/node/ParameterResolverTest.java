package org.jbpm.workflow.instance.node;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;

import org.jbpm.test.util.AbstractBaseTest;
import org.junit.Test;
import org.slf4j.LoggerFactory;

public class ParameterResolverTest extends AbstractBaseTest {

    public void addLogger() { 
        logger = LoggerFactory.getLogger(this.getClass());
    }
    
    @Test
    public void testSingleVariable() {
        
        String[] expected = new String[]{"var1"};
        String s = "#{var1}";
        
        List<String> foundVariables = new ArrayList<String>();
        
        Matcher matcher = StateBasedNodeInstance.PARAMETER_MATCHER.matcher(s);
        while (matcher.find()) {
            String paramName = matcher.group(1);

            foundVariables.add(paramName);
        }
        
        assertEquals(1, foundVariables.size());
        assertEquals(Arrays.asList(expected), foundVariables);
    }
    
    @Test
    public void testSingleVariableEnclosedWithText() {
        
        String[] expected = new String[]{"var1"};
        String s = "this is my #{var1} variable";
        
        List<String> foundVariables = new ArrayList<String>();
        
        Matcher matcher = StateBasedNodeInstance.PARAMETER_MATCHER.matcher(s);
        while (matcher.find()) {
            String paramName = matcher.group(1);

            foundVariables.add(paramName);
        }
        
        assertEquals(1, foundVariables.size());
        assertEquals(Arrays.asList(expected), foundVariables);
    }
    
    @Test
    public void testMultiVariableWithoutWhitespace() {
        
        String[] expected = new String[]{"var1", "var2"};
        String s = "#{var1}=#{var2}";
        
        List<String> foundVariables = new ArrayList<String>();
        
        Matcher matcher = StateBasedNodeInstance.PARAMETER_MATCHER.matcher(s);
        while (matcher.find()) {
            String paramName = matcher.group(1);

            foundVariables.add(paramName);
        }
        
        assertEquals(2, foundVariables.size());
        assertEquals(Arrays.asList(expected), foundVariables);
    }
    
    @Test
    public void testMultiVariableSeparatedWithComma() {
        
        String[] expected = new String[]{"var1", "var2"};
        String s = "#{var1},#{var2}";
        
        List<String> foundVariables = new ArrayList<String>();
        
        Matcher matcher = StateBasedNodeInstance.PARAMETER_MATCHER.matcher(s);
        while (matcher.find()) {
            String paramName = matcher.group(1);

            foundVariables.add(paramName);
        }
        
        assertEquals(2, foundVariables.size());
        assertEquals(Arrays.asList(expected), foundVariables);
    }
    
    @Test
    public void testMultiVariableEnclosedWithText() {
        
        String[] expected = new String[]{"var1", "var2"};
        String s = "Here are my two #{var1},#{var2} variables";
        
        List<String> foundVariables = new ArrayList<String>();
        
        Matcher matcher = StateBasedNodeInstance.PARAMETER_MATCHER.matcher(s);
        while (matcher.find()) {
            String paramName = matcher.group(1);

            foundVariables.add(paramName);
        }
        
        assertEquals(2, foundVariables.size());
        assertEquals(Arrays.asList(expected), foundVariables);
    }
    
    @Test
    public void testMultiVariableNextToEachOther() {
        
        String[] expected = new String[]{"var1", "var2"};
        String s = "#{var1}#{var2}";
        
        List<String> foundVariables = new ArrayList<String>();
        
        Matcher matcher = StateBasedNodeInstance.PARAMETER_MATCHER.matcher(s);
        while (matcher.find()) {
            String paramName = matcher.group(1);

            foundVariables.add(paramName);
        }
        
        assertEquals(2, foundVariables.size());
        assertEquals(Arrays.asList(expected), foundVariables);
    }
}
