/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.pmml.pmml_4_2.transformations;


import org.drools.pmml.pmml_4_2.PMML4Helper;
import org.drools.pmml.pmml_4_2.DroolsAbstractPMMLTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FunctionsTest extends DroolsAbstractPMMLTest {

    private static final boolean VERBOSE = false;
    private static final String source = "org/drools/pmml/pmml_4_2/test_functions.xml";
    private static final String packageName = "org.drools.pmml.pmml_4_2.test";



    @Before
    public void setUp() throws Exception {
        setKSession(getModelSession(source, VERBOSE));
        setKbase(getKSession().getKieBase());
    }

    @After
    public void tearDown() {
        getKSession().dispose();
    }


    @Test
    public void testFunctions() throws Exception {
        getKSession().getEntryPoint("in_Age").insert(30);
        getKSession().getEntryPoint("in_Age2").insert(2);
        getKSession().fireAllRules();

        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"MappedAge"),true,false, null,30);
        checkFirstDataFieldOfTypeStatus(getKbase().getFactType(packageName,"NestedAge"),true,false, null,932.0);

    }


    @Test
    public void testFunctionMapping() {
        PMML4Helper ctx = new PMML4Helper();

        assertEquals("(2 + 3 + 4)" , ctx.mapFunction("+","2","3","4"));
        assertEquals("(2 - 3)" , ctx.mapFunction("-","2","3"));
        assertEquals("(2 * 3 * 4)" , ctx.mapFunction("*","2","3","4"));
        assertEquals("(2 / 4)" , ctx.mapFunction("/","2","4"));

        assertEquals("(Math.min(2,Math.min(3,4)))" , ctx.mapFunction("min","2","3","4"));
        assertEquals("(Math.max(2,Math.max(3,4)))" , ctx.mapFunction("max","2","3","4"));
        assertEquals("(2 + 3 + 4)" , ctx.mapFunction("sum","2","3","4"));
        assertEquals("(2 * 3 * 4)" , ctx.mapFunction("product","2","3","4"));
        assertEquals("((2 + 3 + 4) / 3)" , ctx.mapFunction("avg","2","3","4"));
        assertEquals("(3)" , ctx.mapFunction("median","1","2","3","4","5"));
        assertEquals("( 0.5 * 3 + 0.5 * 4 )" , ctx.mapFunction("median","1","2","3","4","5","6"));

        assertEquals("(Math.log10(2))" , ctx.mapFunction("log10","2"));
        assertEquals("(Math.log(2))" , ctx.mapFunction("ln","2"));
        assertEquals("(Math.sqrt(2))" , ctx.mapFunction("sqrt","2"));
        assertEquals("(Math.abs(2))" , ctx.mapFunction("abs","2"));
        assertEquals("(Math.exp(2))" , ctx.mapFunction("exp","2"));
        assertEquals("(Math.pow(2,3))" , ctx.mapFunction("pow","2","3"));
        assertEquals("(1)" , ctx.mapFunction("pow","0","0"));
        assertEquals("(2 > 3 ? 1 : 0)" , ctx.mapFunction("threshold","2","3"));
        assertEquals("(Math.floor(2))" , ctx.mapFunction("floor","2"));
        assertEquals("(Math.ceil(2))" , ctx.mapFunction("ceil","2"));
        assertEquals("(Math.round(2))" , ctx.mapFunction("round","2"));

        assertEquals("(\"abc\".toString().toUpperCase())" , ctx.mapFunction("uppercase","\"abc\""));
        assertEquals("(\"testString\".toString().substring(2,6))" , ctx.mapFunction("substring","\"testString\"","3","4"));
        assertEquals("(new java.util.Formatter(new StringBuilder(),java.util.Locale.getDefault()).format(\"%3d\",3.0))" , ctx.mapFunction("formatNumber","\"%3d\"","3.0"));

        assertEquals("(new java.text.SimpleDateFormat(\"format\").format(new SimpleDateFormat().parse(\"date\", java.util.Locale.ENGLISH)))" , ctx.mapFunction("formatDatetime","\"date\"","\"format\""));

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
