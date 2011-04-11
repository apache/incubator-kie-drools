package org.drools.pmml_4_0.transformations;


import org.drools.pmml_4_0.DroolsAbstractPMMLTest;
import org.drools.pmml_4_0.PMML4Wrapper;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestFunctions extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = true;
    private static final String source = "test_functions.xml";
    private static final String packageName = "org.drools.pmml_4_0.test";



    @Before
    public void setUp() throws Exception {
        setKSession(getModelSession(source, VERBOSE));
        setKbase(getKSession().getKnowledgeBase());
    }



    @Test
    public void testFunctions() throws Exception {
        getKSession().getWorkingMemoryEntryPoint("in_Age").insert(30);
        getKSession().getWorkingMemoryEntryPoint("in_Age2").insert(2);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"MappedAge"),true,false, null,30);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"NestedAge"),true,false, null,932.0);

    }


    @Test
    public void testFunctionMapping() {
        PMML4Wrapper ctx = new PMML4Wrapper();

        assertEquals("(2 + 3 + 4)" , ctx.mapFunction("+","2","3","4"));
        assertEquals("(2 - 3)" , ctx.mapFunction("-","2","3"));
        assertEquals("(2 * 3 * 4)" , ctx.mapFunction("*","2","3","4"));
        assertEquals("(2 / 4)" , ctx.mapFunction("/","2","4"));

        assertEquals("(Math.min(2,Math.min(3,4)))" , ctx.mapFunction("min","2","3","4"));
        assertEquals("(Math.max(2,Math.max(3,4)))" , ctx.mapFunction("max","2","3","4"));
        assertEquals("(2 + 3 + 4)" , ctx.mapFunction("sum","2","3","4"));
        assertEquals("((2 + 3 + 4) / 3)" , ctx.mapFunction("avg","2","3","4"));

        assertEquals("(Math.log10(2))" , ctx.mapFunction("log10","2"));
        assertEquals("(Math.log(2))" , ctx.mapFunction("ln","2"));
        assertEquals("(Math.sqrt(2))" , ctx.mapFunction("sqrt","2"));
        assertEquals("(Math.abs(2))" , ctx.mapFunction("abs","2"));
        assertEquals("(Math.exp(2))" , ctx.mapFunction("exp","2"));
        assertEquals("(Math.pow(2,3))" , ctx.mapFunction("pow","2","3"));
        assertEquals("(2 > 3 ? 1 : 0)" , ctx.mapFunction("threshold","2","3"));
        assertEquals("(Math.floor(2))" , ctx.mapFunction("floor","2"));
        assertEquals("(Math.ceil(2))" , ctx.mapFunction("ceil","2"));
        assertEquals("(Math.round(2))" , ctx.mapFunction("round","2"));

        assertEquals("(\"abc\".toString().toUpperCase())" , ctx.mapFunction("uppercase","\"abc\""));
        assertEquals("(\"testString\".toString().substring(2,6))" , ctx.mapFunction("substring","\"testString\"","3","4"));
        assertEquals("(new java.util.Formatter(new StringBuilder(),java.util.Locale.getDefault()).format(\"%3d\",3.0))" , ctx.mapFunction("formatNumber","\"%3d\"","3.0"));

        assertEquals("(new java.text.SimpleDateFormat(\"format\").format(new SimpleDateFormat().parse(\"date\")))" , ctx.mapFunction("formatDatetime","\"date\"","\"format\""));

        assertEquals("(( (new java.text.SimpleDateFormat()).parse(\"date\").getTime() - (new java.text.SimpleDateFormat()).parse(\"01/01/1956\").getTime() ) / (1000*60*60*24))", ctx.mapFunction("dateDaysSinceYear","\"date\"","1956"));
        assertEquals("(( (new java.text.SimpleDateFormat()).parse(\"date\").getTime() - (new java.text.SimpleDateFormat()).parse(\"01/01/1956\").getTime() ) / 1000)", ctx.mapFunction("dateSecondsSinceYear","\"date\"","1956"));
        assertEquals("((new java.text.SimpleDateFormat()).parse(\"date\").getTime() % 1000)", ctx.mapFunction("dateSecondsSinceMidnight","\"date\""));


        assertEquals("(a == b)" , ctx.mapFunction("equal","a","b"));
        assertEquals("(a != b)" , ctx.mapFunction("notEqual","a","b"));
        assertEquals("(a < b)" , ctx.mapFunction("lessThan","a","b"));
        assertEquals("(a <= b)" , ctx.mapFunction("lessOrEqual","a","b"));
        assertEquals("(a > b)" , ctx.mapFunction("greaterThan","a","b"));
        assertEquals("(a >= b)" , ctx.mapFunction("greaterOrEqual","a","b"));
        assertEquals("(a.contains(b))" , ctx.mapFunction("isIn","a","b"));
        assertEquals("((! a.contains(b)))" , ctx.mapFunction("isNotIn","a","b"));

        assertEquals("(( ! a ))" , ctx.mapFunction("not","a"));
        assertEquals("(a && b && c)" , ctx.mapFunction("and","a","b","c"));
        assertEquals("(a || b)" , ctx.mapFunction("or","a","b"));

        assertEquals("(a ? b : c)" , ctx.mapFunction("if","a","b","c"));
        assertEquals("(a ? b : null)" , ctx.mapFunction("if","a","b"));

    }




}
