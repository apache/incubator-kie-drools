package org.drools.lang;

import java.io.*;
import java.lang.reflect.*;
import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.drools.base.evaluators.EvaluatorRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestTree2TestDRL {
	String stdout;
	String stderr;

    @Before
	public void setUp() throws Exception {

		// initializes pluggable operators
		new EvaluatorRegistry();
	}

    @Test
    public void testPackage_statement_walks_Package_statement1() throws Exception {
		// test input: "package foo"
		Object retval = execTreeParser("package_statement", "package_statement", "package foo", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"package_statement", expecting, actual);
	}

    @Test
    public void testPackage_statement_walks_Package_statement2() throws Exception {
		// test input: "package foo.bar.baz;"
		Object retval = execTreeParser("package_statement", "package_statement", "package foo.bar.baz;", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"package_statement", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit1() throws Exception {
		// test input: ""
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit2() throws Exception {
		// test input: "package foo; import com.foo.Bar; import com.foo.Baz;"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package foo; import com.foo.Bar; import com.foo.Baz;", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit3() throws Exception {
		// test input: "rule empty \n\nthen\n  \nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule empty \n\nthen\n  \nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit4() throws Exception {
		// test input: "#the purpose of this is to see what happens when we have some partially damaged syntax\n#as the IDE uses the parsers AST to work out completion suggestions.\npackage test\n\n\nrule simple_rule \n  when\n    foo3 : Bar(\n"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "#the purpose of this is to see what happens when we have some partially damaged syntax\n#as the IDE uses the parsers AST to work out completion suggestions.\npackage test\n\n\nrule simple_rule \n  when\n    foo3 : Bar(\n", false);
		Object actual = examineParserExecResult(28, retval);
		Object expecting = "FAIL";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit5() throws Exception {
		// test input: "package test.templates\n\ntemplate Cheese\n\tString \tname\n\tInteger age\nend\n\ntemplate \"Wine\"\n\tString \t\tname\n\tString \t\tyear\n\tString[] \taccolades\nend\n\n\nrule \"a rule\"\n  when\n\tCheese(name == \"Stilton\", age==2001)\n\tWine(name == \"Grange\", age == \"1978\", accolades contains \"world champion\")\n  then\n  \tbaz();\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package test.templates\n\ntemplate Cheese\n\tString \tname\n\tInteger age\nend\n\ntemplate \"Wine\"\n\tString \t\tname\n\tString \t\tyear\n\tString[] \taccolades\nend\n\n\nrule \"a rule\"\n  when\n\tCheese(name == \"Stilton\", age==2001)\n\tWine(name == \"Grange\", age == \"1978\", accolades contains \"world champion\")\n  then\n  \tbaz();\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit6() throws Exception {
		// test input: "package foo\n\nrule rule_one \n  when\n  \tFoo()\n  then\n  \t if (speed > speedLimit ? true : false;)\n     pullEmOver();\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package foo\n\nrule rule_one \n  when\n  \tFoo()\n  then\n  \t if (speed > speedLimit ? true : false;)\n     pullEmOver();\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit7() throws Exception {
		// test input: "package foo\n\nfunction String[] yourFunction(String args[]) {\n     baz();\n}\n\nrule \"new rule\"\n\n\twhen\n\t\tSomething()\n\tthen\n\t\tyourFunction(new String[3] {\"a\",\"b\",\"c\"});\n\t\t\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package foo\n\nfunction String[] yourFunction(String args[]) {\n     baz();\n}\n\nrule \"new rule\"\n\n\twhen\n\t\tSomething()\n\tthen\n\t\tyourFunction(new String[3] {\"a\",\"b\",\"c\"});\n\t\t\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit8() throws Exception {
		// test input: "rule almost_empty \n  when\n  then\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule almost_empty \n  when\n  then\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit9() throws Exception {
		// test input: "rule \"quoted string name\"\n  when\n  then\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule \"quoted string name\"\n  when\n  then\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit10() throws Exception {
		// test input: "rule rule1 \n  no-loop false\n  when\n  \tnot Cheese(type == \"stilton\")\n  then\n\tfunky();\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule rule1 \n  no-loop false\n  when\n  \tnot Cheese(type == \"stilton\")\n  then\n\tfunky();\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit11() throws Exception {
		// test input: "rule rule1 \n  auto-focus true\n  when\n  \tnot Cheese(type == \"stilton\")\n  then\n\tfunky();\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule rule1 \n  auto-focus true\n  when\n  \tnot Cheese(type == \"stilton\")\n  then\n\tfunky();\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit12() throws Exception {
		// test input: "rule rule1 \n  ruleflow-group \"a group\"\n  when\n  \tnot Cheese(type == \"stilton\")\n  then\n\tfunky();\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule rule1 \n  ruleflow-group \"a group\"\n  when\n  \tnot Cheese(type == \"stilton\")\n  then\n\tfunky();\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit13() throws Exception {
		// test input: "\nrule myrule \n  when\n  then\n  \tint i = 0;\n\ti = 1;\n\ti / 1;\n\ti == 1;\n\ti(i);\n\ti = 'i';\n\ti.i.i;\n\ti\\i;\n\ti<i;\n\ti>i;\n\ti=\"i\";\t\n\t++i;\n\ti++;\n\t--i;\n\ti--;\n\ti += i;\n\ti -= i;\n\ti *= i;\n\ti /= i;\n\tint i = 5;\n\tfor(int j; j<i; ++j) {\n\t  System.out.println(j);\n\t}\t\n\tObject o = new String(\"Hello\");\n\tString s = (String) o;\t\n\t\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "\nrule myrule \n  when\n  then\n  \tint i = 0;\n\ti = 1;\n\ti / 1;\n\ti == 1;\n\ti(i);\n\ti = 'i';\n\ti.i.i;\n\ti\\i;\n\ti<i;\n\ti>i;\n\ti=\"i\";\t\n\t++i;\n\ti++;\n\t--i;\n\ti--;\n\ti += i;\n\ti -= i;\n\ti *= i;\n\ti /= i;\n\tint i = 5;\n\tfor(int j; j<i; ++j) {\n\t  System.out.println(j);\n\t}\t\n\tObject o = new String(\"Hello\");\n\tString s = (String) o;\t\n\t\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit14() throws Exception {
		// test input: "#check that it can handle true/false literals, and \n#negative numbers\nrule simple_rule \n  when\n\tFoo(bar == false)\n\tFoo(boo > -42)\n\tFoo(boo > -42.42)\n  then\n\tcons();\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "#check that it can handle true/false literals, and \n#negative numbers\nrule simple_rule \n  when\n\tFoo(bar == false)\n\tFoo(boo > -42)\n\tFoo(boo > -42.42)\n  then\n\tcons();\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit15() throws Exception {
		// test input: "package org.drools.test;\n \nimport org.drools.Cheese;\n \nrule \"simple rule\"\n    when\n        Cheese( )\n    then\nend "
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package org.drools.test;\n \nimport org.drools.Cheese;\n \nrule \"simple rule\"\n    when\n        Cheese( )\n    then\nend ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit16() throws Exception {
		// test input: "rule blah\n\n when \n\n\tCol1() from something.doIt( foo,bar,42,\"hello\",{ a => \"b\", \"something\" => 42, \"a\" => foo, x => {x=>y}},\"end\", [a, \"b\", 42] )\n\tCol2()\n then\n\tpartay();\nend\t"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule blah\n\n when \n\n\tCol1() from something.doIt( foo,bar,42,\"hello\",{ a => \"b\", \"something\" => 42, \"a\" => foo, x => {x=>y}},\"end\", [a, \"b\", 42] )\n\tCol2()\n then\n\tpartay();\nend\t", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit17() throws Exception {
		// test input: "rule blah\n\n when \n\n\tCol1() from doIt( foo,bar,42,\"hello\",{ a => \"b\", \"something\" => 42, \"a\" => foo, x => {x=>y}},\"end\", [a, \"b\", 42] )\n\tCol2()\n then\n\tpartay();\nend\t"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule blah\n\n when \n\n\tCol1() from doIt( foo,bar,42,\"hello\",{ a => \"b\", \"something\" => 42, \"a\" => foo, x => {x=>y}},\"end\", [a, \"b\", 42] )\n\tCol2()\n then\n\tpartay();\nend\t", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit18() throws Exception {
		// test input: "rule blah\n\n when \n\n\tCol1() from something.doIt\n\tCol2()\n then\n\tpartay();\nend\t"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule blah\n\n when \n\n\tCol1() from something.doIt\n\tCol2()\n then\n\tpartay();\nend\t", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit19() throws Exception {
		// test input: "rule blah\n\n when \n\n\tCol1() from something.doIt[\"key\"]\n\tCol2()\n then\n\tpartay();\nend\t"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule blah\n\n when \n\n\tCol1() from something.doIt[\"key\"]\n\tCol2()\n then\n\tpartay();\nend\t", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit20() throws Exception {
		// test input: "rule blah\n when \n\tCol1() from doIt1( foo,bar,42,\"hello\",{ a => \"b\"}, [a, \"b\", 42] )\n\t            .doIt2(bar, [a, \"b\", 42]).field[\"key\"]\n\tCol2()\n then\n\tpartay();\nend\t"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule blah\n when \n\tCol1() from doIt1( foo,bar,42,\"hello\",{ a => \"b\"}, [a, \"b\", 42] )\n\t            .doIt2(bar, [a, \"b\", 42]).field[\"key\"]\n\tCol2()\n then\n\tpartay();\nend\t", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit21() throws Exception {
		// test input: "rule simple_rule \n  when\n    foo3 : Bar(a==3)\n    foo4 : Bar(a4:a==4)\n    Baz()\n  then\n  if ( a == b ) {\n    assert( foo3 );\n  } else {\n    retract( foo4 );\n  } \n  System.out.println( a4 );\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule simple_rule \n  when\n    foo3 : Bar(a==3)\n    foo4 : Bar(a4:a==4)\n    Baz()\n  then\n  if ( a == b ) {\n    assert( foo3 );\n  } else {\n    retract( foo4 );\n  } \n  System.out.println( a4 );\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit22() throws Exception {
		// test input: "#this is for showing off all the new multi restriction stuff\n\n\n\n\nrule simple_rule \n  when\n  \tPerson(age > 30 && < 40)\n  \tVehicle(type == \"sedan\" || == \"wagon\", age < 3)\n  then\n\tconsequence();\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "#this is for showing off all the new multi restriction stuff\n\n\n\n\nrule simple_rule \n  when\n  \tPerson(age > 30 && < 40)\n  \tVehicle(type == \"sedan\" || == \"wagon\", age < 3)\n  then\n\tconsequence();\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit23() throws Exception {
		// test input: "package la\n\n\nrule simple_rule \n  when\n  \tBaz()\n  then\n  \t//woot\n  \tfirst\n  \t\n  \t#\n  \t\n  \t/* lala\n  \t\n  \t*/\n  \tsecond  \nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package la\n\n\nrule simple_rule \n  when\n  \tBaz()\n  then\n  \t//woot\n  \tfirst\n  \t\n  \t#\n  \t\n  \t/* lala\n  \t\n  \t*/\n  \tsecond  \nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit24() throws Exception {
		// test input: "rule simple_rule \n  when\n    foo3 : Bar(a==3) ; foo4 : Bar(a4:a==4) ; Baz()\n  then\n  if ( a == b ) {\n    assert( foo3 );\n  } else {\n    retract( foo4 );\n  } \n  System.out.println( a4 );\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule simple_rule \n  when\n    foo3 : Bar(a==3) ; foo4 : Bar(a4:a==4) ; Baz()\n  then\n  if ( a == b ) {\n    assert( foo3 );\n  } else {\n    retract( foo4 );\n  } \n  System.out.println( a4 );\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit25() throws Exception {
		// test input: "rule simple_rule \n  when\n  \tnot Cheese(type == \"stilton\")\n  then\n\tfunky();\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule simple_rule \n  when\n  \tnot Cheese(type == \"stilton\")\n  then\n\tfunky();\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit26() throws Exception {
		// test input: "package HR1\n\nimport function abd.def.x\nimport function qed.wah.*\n\nrule simple_rule \n  when  \t\t  \t\n  \tnot ( Cheese(type == \"stilton\") )\n  \texists ( Foo() )\n  then\n\tfunky();\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package HR1\n\nimport function abd.def.x\nimport function qed.wah.*\n\nrule simple_rule \n  when  \t\t  \t\n  \tnot ( Cheese(type == \"stilton\") )\n  \texists ( Foo() )\n  then\n\tfunky();\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit27() throws Exception {
		// test input: "package HR1\n\nrule simple_rule \n  when  \t\t  \t\n  \tnot ( Cheese(type == \"stilton\") )\n  \texists ( Foo() )\n  then\n\tfunky();\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package HR1\n\nrule simple_rule \n  when  \t\t  \t\n  \tnot ( Cheese(type == \"stilton\") )\n  \texists ( Foo() )\n  then\n\tfunky();\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit28() throws Exception {
		// test input: "package HR2\n\nrule simple_rule \n  when  \t\t  \t\n  \ta : (not ( Cheese(type == \"stilton\") ))\n  \texists ( Foo() )\n  then\n\tfunky();\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package HR2\n\nrule simple_rule \n  when  \t\t  \t\n  \ta : (not ( Cheese(type == \"stilton\") ))\n  \texists ( Foo() )\n  then\n\tfunky();\nend", false);
		Object actual = examineParserExecResult(28, retval);
		Object expecting = "FAIL";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit29() throws Exception {
		// test input: "\nquery \"simple_query\" \n    foo3 : Bar(a==3)\n    foo4 : Bar(a4:a==4)\n    Baz()\n\t\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "\nquery \"simple_query\" \n    foo3 : Bar(a==3)\n    foo4 : Bar(a4:a==4)\n    Baz()\n\t\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit30() throws Exception {
		// test input: "package foo\n\nrule bar\n\twhen\n\t\tBaz()\n\tthen\n\t\tBoo()\nend\n\nquery \"simple_query\" \n    foo3 : Bar(a==3)\n    foo4 : Bar(a4:a==4)\n    Baz()\n\t\nend\n\nrule bar2\n\twhen\n\t\tBaz()\n\tthen\n\t\tBoo()\nend\n\nquery \"simple_query2\" \n    foo3 : Bar(a==3)\n    foo4 : Bar(a4:a==4)\n    Baz()\n\t\nend\n\t"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package foo\n\nrule bar\n\twhen\n\t\tBaz()\n\tthen\n\t\tBoo()\nend\n\nquery \"simple_query\" \n    foo3 : Bar(a==3)\n    foo4 : Bar(a4:a==4)\n    Baz()\n\t\nend\n\nrule bar2\n\twhen\n\t\tBaz()\n\tthen\n\t\tBoo()\nend\n\nquery \"simple_query2\" \n    foo3 : Bar(a==3)\n    foo4 : Bar(a4:a==4)\n    Baz()\n\t\nend\n\t", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit31() throws Exception {
		// test input: "package org.drools.test;\n\nimport org.drools.integrationtests.Cheese;\n\nrule \"Like Stilton\"\n    when\n        Cheese( t:type == \"stilton\" )\n    then\n        System.out.println(\"I like \" + t);\nend    \n\nrule \"Like Cheddar\"\n    when\n        Cheese( t:type == \"cheddar\" )\n    then\n        System.out.println(\"I like \" + t );\nend    "
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package org.drools.test;\n\nimport org.drools.integrationtests.Cheese;\n\nrule \"Like Stilton\"\n    when\n        Cheese( t:type == \"stilton\" )\n    then\n        System.out.println(\"I like \" + t);\nend    \n\nrule \"Like Cheddar\"\n    when\n        Cheese( t:type == \"cheddar\" )\n    then\n        System.out.println(\"I like \" + t );\nend    ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit32() throws Exception {
		// test input: "package org.drools.test;\n\nimport org.drools.Cheese;\n\nrule \"like cheddar\"\n    when\n        Cheese( $type:type )\n    then\n        System.out.println(\"I like \" + $type);\nend    "
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package org.drools.test;\n\nimport org.drools.Cheese;\n\nrule \"like cheddar\"\n    when\n        Cheese( $type:type )\n    then\n        System.out.println(\"I like \" + $type);\nend    ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit33() throws Exception {
		// test input: "package org.drools.test;\n\nimport org.drools.Cheese;\nimport org.drools.Person;\n\nrule \"Who likes Stilton\"\n    when\n        Cheese($type : type == \"stilton\")\n        $person : Person($name : name == \"bob\", likes == $type)        \n    then\n        System.out.println( $name + \" likes \" + $type);\nend    "
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package org.drools.test;\n\nimport org.drools.Cheese;\nimport org.drools.Person;\n\nrule \"Who likes Stilton\"\n    when\n        Cheese($type : type == \"stilton\")\n        $person : Person($name : name == \"bob\", likes == $type)        \n    then\n        System.out.println( $name + \" likes \" + $type);\nend    ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit34() throws Exception {
		// test input: "import org.drools.Person\n\nrule simple_rule \n  when\n\tPerson(name == \"mark\") or \n\t( Person(type == \"fan\") and Cheese(type == \"green\") )\n  then\n\tSystem.out.println( \"Mark and Michael\" + bar );\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "import org.drools.Person\n\nrule simple_rule \n  when\n\tPerson(name == \"mark\") or \n\t( Person(type == \"fan\") and Cheese(type == \"green\") )\n  then\n\tSystem.out.println( \"Mark and Michael\" + bar );\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit35() throws Exception {
		// test input: "import org.drools.Person\n\nrule simple_rule \n  when\n    Person(name == \"mark\") && Cheese(type == \"stilton\")\n    Person(name == \"mark\") || Cheese(type == \"stilton\")\n  then\n\tSystem.out.println( \"Mark and Michael\" );\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "import org.drools.Person\n\nrule simple_rule \n  when\n    Person(name == \"mark\") && Cheese(type == \"stilton\")\n    Person(name == \"mark\") || Cheese(type == \"stilton\")\n  then\n\tSystem.out.println( \"Mark and Michael\" );\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit36() throws Exception {
		// test input: "import org.drools.Person\n\nrule simple_rule \n  when\n\tfoo :  ( Person(name == \"mark\") or Person(type == \"fan\") ) \n\tCheese(type == \"green\")\n  then\n\tSystem.out.println( \"Mark and Michael\" + bar );\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "import org.drools.Person\n\nrule simple_rule \n  when\n\tfoo :  ( Person(name == \"mark\") or Person(type == \"fan\") ) \n\tCheese(type == \"green\")\n  then\n\tSystem.out.println( \"Mark and Michael\" + bar );\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit37() throws Exception {
		// test input: "\nrule simple_rule \n  when\n\tfoo : ( Person(name == \"mark\") \n\t\tor \n\t\tPerson(type == \"fan\") )\n  then\n\tSystem.out.println( \"Mark and Michael\" + bar );\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "\nrule simple_rule \n  when\n\tfoo : ( Person(name == \"mark\") \n\t\tor \n\t\tPerson(type == \"fan\") )\n  then\n\tSystem.out.println( \"Mark and Michael\" + bar );\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit38() throws Exception {
		// test input: "rule simple_rule \n  when\n\tfoo : ( \n\t\tPerson(name == \"mark\") or Person(type == \"fan\") \n\t\t)\n  then\n\tSystem.out.println( \"Mark and Michael\" + bar );\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule simple_rule \n  when\n\tfoo : ( \n\t\tPerson(name == \"mark\") or Person(type == \"fan\") \n\t\t)\n  then\n\tSystem.out.println( \"Mark and Michael\" + bar );\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit39() throws Exception {
		// test input: "rule simple_rule \n  when\n\t ( (not Foo(x==\"a\") or Foo(x==\"y\") ) and ( Shoes() or Butt() ) )\n  then\n\tgo wild\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule simple_rule \n  when\n\t ( (not Foo(x==\"a\") or Foo(x==\"y\") ) and ( Shoes() or Butt() ) )\n  then\n\tgo wild\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit40() throws Exception {
		// test input: "rule simple_rule \n  when\n\teval(abc(\"foo\") + 5)\n\tFoo()\n\teval(qed())\n\tBar()\n  then\n\tKapow\n\tPoof\n\t\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule simple_rule \n  when\n\teval(abc(\"foo\") + 5)\n\tFoo()\n\teval(qed())\n\tBar()\n  then\n\tKapow\n\tPoof\n\t\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit41() throws Exception {
		// test input: "rule simple_rule \n  when\n\tFoo()\n\tBar()\n\teval(abc(\"foo\"))\n  then\n\tKapow\n\t\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule simple_rule \n  when\n\tFoo()\n\tBar()\n\teval(abc(\"foo\"))\n  then\n\tKapow\n\t\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit42() throws Exception {
		// test input: "rule simple_rule \n  when\n\tFoo(name== (a + b))\n  then\n\tKapow\n\t\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule simple_rule \n  when\n\tFoo(name== (a + b))\n  then\n\tKapow\n\t\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit43() throws Exception {
		// test input: "rule simple_rule \n  when\n  \tPerson( $age2:age -> ($age2 == $age1+2 ) ) \n  then\n\tfoo bar\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule simple_rule \n  when\n  \tPerson( $age2:age -> ($age2 == $age1+2 ) ) \n  then\n\tfoo bar\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit44() throws Exception {
		// test input: "package org.drools.test;\n\nimport org.drools.Cheese;\n\nglobal java.util.List list;\nglobal java.lang.Integer five;\n\nrule \"not rule test\"\n    when\n        $person : Person( $likes:like )\n        not Cheese( type == $likes )\n    then\n\t\tlist.add( $person );\nend    "
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package org.drools.test;\n\nimport org.drools.Cheese;\n\nglobal java.util.List list;\nglobal java.lang.Integer five;\n\nrule \"not rule test\"\n    when\n        $person : Person( $likes:like )\n        not Cheese( type == $likes )\n    then\n\t\tlist.add( $person );\nend    ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit45() throws Exception {
		// test input: "package org.drools.test;\n\nimport org.drools.Cheese;\n\nglobal java.lang.String foo\nglobal java.lang.Integer bar;\n\nrule baz\n    when\n        Cheese( )\n    then\n\nend    "
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package org.drools.test;\n\nimport org.drools.Cheese;\n\nglobal java.lang.String foo\nglobal java.lang.Integer bar;\n\nrule baz\n    when\n        Cheese( )\n    then\n\nend    ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit46() throws Exception {
		// test input: "import java.lang.String\n\nfunction String functionA(String s, Integer i) {\n\t\n\tfoo();\n\n}\n\nfunction void functionB() {\n\tbar();\t\n}\n\n\nrule something \n\twhen\n\tthen\nend\n\nrule \"one more thing\"\n\twhen\n\tthen\nend\n\n\n\n\t"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "import java.lang.String\n\nfunction String functionA(String s, Integer i) {\n\t\n\tfoo();\n\n}\n\nfunction void functionB() {\n\tbar();\t\n}\n\n\nrule something \n\twhen\n\tthen\nend\n\nrule \"one more thing\"\n\twhen\n\tthen\nend\n\n\n\n\t", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit47() throws Exception {
		// test input: "#this starts with a comment\npackage foo.bar\n\n//and another comment\n\n/*\nyet\n\t   another\n   \t\t\t\tstyle\n*/\n\nrule \"test\"\n  when\n  then\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "#this starts with a comment\npackage foo.bar\n\n//and another comment\n\n/*\nyet\n\t   another\n   \t\t\t\tstyle\n*/\n\nrule \"test\"\n  when\n  then\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit48() throws Exception {
		// test input: "\n\nrule simple_rule \n\t\t#attributes keywork (and colon) is totally optional\n\t\tsalience 42\n\t\tagenda-group \"my_group\"\n\t\tno-loop \n\t\tduration 42\n\t\tactivation-group \"my_activation_group\"\n\t\tlock-on-active true\n\twhen\n\t\tFoo()\n\tthen\n\t\tbar();\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "\n\nrule simple_rule \n\t\t#attributes keywork (and colon) is totally optional\n\t\tsalience 42\n\t\tagenda-group \"my_group\"\n\t\tno-loop \n\t\tduration 42\n\t\tactivation-group \"my_activation_group\"\n\t\tlock-on-active true\n\twhen\n\t\tFoo()\n\tthen\n\t\tbar();\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit49() throws Exception {
		// test input: "\n\nrule simple_rule \n\tattributes: \n\t\tsalience 42, agenda-group \"my_group\", no-loop,  lock-on-active, duration 42, activation-group \"my_activation_group\"\n\twhen\n\t\tFoo()\n\tthen\n\t\tbar();\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "\n\nrule simple_rule \n\tattributes: \n\t\tsalience 42, agenda-group \"my_group\", no-loop,  lock-on-active, duration 42, activation-group \"my_activation_group\"\n\twhen\n\t\tFoo()\n\tthen\n\t\tbar();\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit50() throws Exception {
		// test input: "rule simple_rule \n  when\n  \tFoo(bar == Foo.BAR)\n  then\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule simple_rule \n  when\n  \tFoo(bar == Foo.BAR)\n  then\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit51() throws Exception {
		// test input: "rule one\n  when\n    exists Foo()\n    exits Bar()\n  then\nend\n\nrule two \n  when\n    ford = ford = ford\n  then\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule one\n  when\n    exists Foo()\n    exits Bar()\n  then\nend\n\nrule two \n  when\n    ford = ford = ford\n  then\nend", false);
		Object actual = examineParserExecResult(28, retval);
		Object expecting = "FAIL";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit52() throws Exception {
		// test input: "rule \"another test\"\n    when\n        s : String()\n        eval(s.equals(\"foo\") && s.startsWith(\"f\"))\n        \n        \n    then\n        list.add( s );\nend "
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule \"another test\"\n    when\n        s : String()\n        eval(s.equals(\"foo\") && s.startsWith(\"f\"))\n        \n        \n    then\n        list.add( s );\nend ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit53() throws Exception {
		// test input: "package nesting;\n\n\n\n\nrule \"test something\"\n\n\twhen\n\t\tp: Person( name soundslike \"Michael\" )\n\tthen\n\t\tp.name = \"goober\"\n\t\tSystem.out.println(p.name)\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package nesting;\n\n\n\n\nrule \"test something\"\n\n\twhen\n\t\tp: Person( name soundslike \"Michael\" )\n\tthen\n\t\tp.name = \"goober\"\n\t\tSystem.out.println(p.name)\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit54() throws Exception {
		// test input: "package com.foo;\n\nagenda-group \"x\"\n\nimport goo.ber\nimport wee.waa\n\n\ndialect \"java\"\n\n\n\n\nrule bar\n  when\n  then\nend\n\nrule baz\n  dialect \"mvel\"\n  when\n  then\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package com.foo;\n\nagenda-group \"x\"\n\nimport goo.ber\nimport wee.waa\n\n\ndialect \"java\"\n\n\n\n\nrule bar\n  when\n  then\nend\n\nrule baz\n  dialect \"mvel\"\n  when\n  then\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit55() throws Exception {
		// test input: "package com.foo;\n\nimport im.one\n\nimport im.two\n\nrule foo\n  when\n  then\nend\n\nfunction cheeseIt() {\n\n}\n\nimport im.three;\n\nrule bar\n  when\n  then\nend\n\nfunction uncheeseIt() {\n\n}\n\nimport im.four;"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package com.foo;\n\nimport im.one\n\nimport im.two\n\nrule foo\n  when\n  then\nend\n\nfunction cheeseIt() {\n\n}\n\nimport im.three;\n\nrule bar\n  when\n  then\nend\n\nfunction uncheeseIt() {\n\n}\n\nimport im.four;", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit56() throws Exception {
		// test input: "rule \"1. Do Stuff!\"\n  when\n  then\nend\n\nrule \"2. Do More Stuff!\"\n  when\n  then\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule \"1. Do Stuff!\"\n  when\n  then\nend\n\nrule \"2. Do More Stuff!\"\n  when\n  then\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit57() throws Exception {
		// test input: "rule simple_rule \n  when\n\tFoo()\n\tBar()\n\teval(\n\t\n\t\n\t\n\t       abc(\n\t       \n\t       \"foo\") + \n\t       5\n\t       \n\t       \n\t       \n\t        \n\t       )\n  then\n\tKapow\n\tPoof\n\t\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule simple_rule \n  when\n\tFoo()\n\tBar()\n\teval(\n\t\n\t\n\t\n\t       abc(\n\t       \n\t       \"foo\") + \n\t       5\n\t       \n\t       \n\t       \n\t        \n\t       )\n  then\n\tKapow\n\tPoof\n\t\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit58() throws Exception {
		// test input: "rule simple_rule \n  when\n\teval(abc();)\n  then\n\tKapow\n\tPoof\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule simple_rule \n  when\n\teval(abc();)\n  then\n\tKapow\n\tPoof\nend", false);
		Object actual = examineParserExecResult(28, retval);
		Object expecting = "FAIL";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit59() throws Exception {
		// test input: "\n\nrule simple_rule \n  when\n\tFoo(\n\t  bar == baz, la==laz\n\t  )\n  then\n\tKapow\n\tPoof\nend\n\t"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "\n\nrule simple_rule \n  when\n\tFoo(\n\t  bar == baz, la==laz\n\t  )\n  then\n\tKapow\n\tPoof\nend\n\t", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit60() throws Exception {
		// test input: "package org.drools.test;\n\nrule \"Who likes Stilton\"\n    when\n        com.cheeseco.Cheese($type : type == \"stilton\")\n    then\n        System.out.println( $name + \" likes \" + $type);\nend    "
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package org.drools.test;\n\nrule \"Who likes Stilton\"\n    when\n        com.cheeseco.Cheese($type : type == \"stilton\")\n    then\n        System.out.println( $name + \" likes \" + $type);\nend    ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit61() throws Exception {
		// test input: "rule \"AccumulateParserTest\"\nwhen\n     Integer() from accumulate( Person( age > 21 ),\n                                init( int x = 0; ),\n                                action( x++; ),\n                                result( new Integer(x) ) );\nthen\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule \"AccumulateParserTest\"\nwhen\n     Integer() from accumulate( Person( age > 21 ),\n                                init( int x = 0; ),\n                                action( x++; ),\n                                result( new Integer(x) ) );\nthen\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit62() throws Exception {
		// test input: "rule \"AccumulateParserTest\"\nwhen\n     $counter:Integer() from accumulate( $person : Person( age > 21 ),\n                                         init( int x = 0; ),\n                                         action( x++; ),\n                                         result( new Integer(x) ) );\nthen\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule \"AccumulateParserTest\"\nwhen\n     $counter:Integer() from accumulate( $person : Person( age > 21 ),\n                                         init( int x = 0; ),\n                                         action( x++; ),\n                                         result( new Integer(x) ) );\nthen\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit63() throws Exception {
		// test input: "rule \"CollectParserTest\"\nwhen\n     $personList : ArrayList() from collect( Person( age > 21 ) );\nthen\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule \"CollectParserTest\"\nwhen\n     $personList : ArrayList() from collect( Person( age > 21 ) );\nthen\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit64() throws Exception {
		// test input: "rule \"test_Quotes\"\n   when\n      InitialFact()\n   then\n      String s = \"\\\"\\n\\t\\\\\";\nend "
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule \"test_Quotes\"\n   when\n      InitialFact()\n   then\n      String s = \"\\\"\\n\\t\\\\\";\nend ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit65() throws Exception {
		// test input: "rule \"test nested CEs\"\t\n\twhen\n\t    not ( State( $state : state ) and\n\t          not( Person( status == $state, $likes : likes ) and\n\t               Cheese( type == $likes ) ) )\n\t    Person( name == \"Bob\" )\n\t    ( Cheese( price == 10 ) or Cheese( type == \"brie\" ) )\n\tthen \n\t\tresults.add(\"OK\");\t\t\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule \"test nested CEs\"\t\n\twhen\n\t    not ( State( $state : state ) and\n\t          not( Person( status == $state, $likes : likes ) and\n\t               Cheese( type == $likes ) ) )\n\t    Person( name == \"Bob\" )\n\t    ( Cheese( price == 10 ) or Cheese( type == \"brie\" ) )\n\tthen \n\t\tresults.add(\"OK\");\t\t\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit66() throws Exception {
		// test input: "rule \"ForallParserTest\"\nwhen\n     forall( Person( age > 21, $likes : likes )\n             Cheese( type == $likes ) );\nthen\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule \"ForallParserTest\"\nwhen\n     forall( Person( age > 21, $likes : likes )\n             Cheese( type == $likes ) );\nthen\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit67() throws Exception {
		// test input: "#testing 'in' operator\n\nrule simple_rule \n  when\n  \tPerson(age > 30 && < 40)\n  \tVehicle(type in ( \"sedan\", \"wagon\" ), age < 3)\n  then\n\tconsequence();\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "#testing 'in' operator\n\nrule simple_rule \n  when\n  \tPerson(age > 30 && < 40)\n  \tVehicle(type in ( \"sedan\", \"wagon\" ), age < 3)\n  then\n\tconsequence();\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit68() throws Exception {
		// test input: "#testing not 'in' operator\n\nrule simple_rule \n  when\n  \tPerson(age > 30 && < 40)\n  \tVehicle(type not in ( \"sedan\", \"wagon\" ), age < 3)\n  then\n\tconsequence();\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "#testing not 'in' operator\n\nrule simple_rule \n  when\n  \tPerson(age > 30 && < 40)\n  \tVehicle(type not in ( \"sedan\", \"wagon\" ), age < 3)\n  then\n\tconsequence();\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit69() throws Exception {
		// test input: "package org.drools;\n\nglobal java.util.List list;\n\nrule \"rule1\"\nwhen\n    Pattern1();\n    Pattern2() from x.y.z;\nthen\n    System.out.println(\"Test\");\nend;\n\nquery \"query1\"\n\tPattern5();\n\tPattern6();\n\tPattern7();\nend;\n\nrule \"rule2\"\nwhen\n    Pattern3();\n    Pattern4() from collect( Pattern5() );\nthen\n    System.out.println(\"Test\");\nend;\n\n\t"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package org.drools;\n\nglobal java.util.List list;\n\nrule \"rule1\"\nwhen\n    Pattern1();\n    Pattern2() from x.y.z;\nthen\n    System.out.println(\"Test\");\nend;\n\nquery \"query1\"\n\tPattern5();\n\tPattern6();\n\tPattern7();\nend;\n\nrule \"rule2\"\nwhen\n    Pattern3();\n    Pattern4() from collect( Pattern5() );\nthen\n    System.out.println(\"Test\");\nend;\n\n\t", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit70() throws Exception {
		// test input: "package org.drools\n\nrule \"Test Parse\"\n\nwhen\n    eval( 3==3 )\nthen\n    System.out.println(\"OK\");\nend "
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package org.drools\n\nrule \"Test Parse\"\n\nwhen\n    eval( 3==3 )\nthen\n    System.out.println(\"OK\");\nend ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit71() throws Exception {
		// test input: "rule \"AccumulateReverseParserTest\"\nwhen\n     Integer() from accumulate( Person( age > 21 ),\n                                init( int x = 0; ),\n                                action( x++; ),\n                                reverse( x--; ),\n                                result( new Integer(x) ) );\nthen\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule \"AccumulateReverseParserTest\"\nwhen\n     Integer() from accumulate( Person( age > 21 ),\n                                init( int x = 0; ),\n                                action( x++; ),\n                                reverse( x--; ),\n                                result( new Integer(x) ) );\nthen\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit72() throws Exception {
		// test input: "rule \"AccumulateReverseParserTest\"\nwhen\n     Number() from accumulate( Person( $age : age > 21 ),\n                               average( $age ) );\nthen\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule \"AccumulateReverseParserTest\"\nwhen\n     Number() from accumulate( Person( $age : age > 21 ),\n                               average( $age ) );\nthen\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit73() throws Exception {
		// test input: "rule \"CollectParserTest\"\nwhen\n     #bellow statement makes no sense, but is useful to test parsing recursiveness\n     $personList : ArrayList() from collect( $p : Person( age > 21 || age < 10 ) from collect( People() from $town.getPeople() ) );\nthen\nend\n\n\t"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule \"CollectParserTest\"\nwhen\n     #bellow statement makes no sense, but is useful to test parsing recursiveness\n     $personList : ArrayList() from collect( $p : Person( age > 21 || age < 10 ) from collect( People() from $town.getPeople() ) );\nthen\nend\n\n\t", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit74() throws Exception {
		// test input: "rule \"AccumulateParserTest\"\nwhen\n     #bellow statement makes no sense, but is useful to test parsing recursiveness\n     $personList : ArrayList() from accumulate( Person( $age : age > 21 || < 10 ) from collect( People() from $town.getPeople() ),\n                                                max( $age ) );\nthen\nend\n\n\t"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule \"AccumulateParserTest\"\nwhen\n     #bellow statement makes no sense, but is useful to test parsing recursiveness\n     $personList : ArrayList() from accumulate( Person( $age : age > 21 || < 10 ) from collect( People() from $town.getPeople() ),\n                                                max( $age ) );\nthen\nend\n\n\t", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit75() throws Exception {
		// test input: "package org.drools;\n\nrule \"testing OR CE\"\nwhen\n    $p : Person( name == \"bob\" )\n    $c : Cheese( type == $p.likes ) || Cheese( price == 10 )\nthen\n    // do something\nend "
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package org.drools;\n\nrule \"testing OR CE\"\nwhen\n    $p : Person( name == \"bob\" )\n    $c : Cheese( type == $p.likes ) || Cheese( price == 10 )\nthen\n    // do something\nend ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit76() throws Exception {
		// test input: "rule \"another test\" salience 10 when eval( true ) then System.out.println(1); end"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule \"another test\" salience 10 when eval( true ) then System.out.println(1); end", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit77() throws Exception {
		// test input: "rule \"another test\" salience 10 when eval( true ) then System.out.println(1);\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule \"another test\" salience 10 when eval( true ) then System.out.println(1);\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit78() throws Exception {
		// test input: "rule \"AccumulateMultiPatternParserTest\"\nwhen\n     $counter:Integer() from accumulate( $person : Person( age > 21 ) and Cheese( type == $person.likes ),\n                                         init( int x = 0; ),\n                                         action( x++; ),\n                                         result( new Integer(x) ) );\nthen\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule \"AccumulateMultiPatternParserTest\"\nwhen\n     $counter:Integer() from accumulate( $person : Person( age > 21 ) and Cheese( type == $person.likes ),\n                                         init( int x = 0; ),\n                                         action( x++; ),\n                                         result( new Integer(x) ) );\nthen\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit79() throws Exception {
		// test input: "package org.drools;\n\nrule \"test rule\"\n\tsalience 10\n\twhen\n\t\t$c: WorkerPerformanceContext(eval)$c.getBalanceMonth() != null))\n\tthen\n\t\tretract($p);\nend\n\t"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package org.drools;\n\nrule \"test rule\"\n\tsalience 10\n\twhen\n\t\t$c: WorkerPerformanceContext(eval)$c.getBalanceMonth() != null))\n\tthen\n\t\tretract($p);\nend\n\t", false);
		Object actual = examineParserExecResult(28, retval);
		Object expecting = "FAIL";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit80() throws Exception {
		// test input: "package org.drools;\n\nrule \"Avoid NPE on wrong syntax\"\nwhen\n    not( Cheese( ( type == \"stilton\", price == 10 ) || ( type == \"brie\", price == 15 ) ) from $cheeseList )\nthen\n    System.out.println(\"OK\");\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package org.drools;\n\nrule \"Avoid NPE on wrong syntax\"\nwhen\n    not( Cheese( ( type == \"stilton\", price == 10 ) || ( type == \"brie\", price == 15 ) ) from $cheeseList )\nthen\n    System.out.println(\"OK\");\nend", false);
		Object actual = examineParserExecResult(28, retval);
		Object expecting = "FAIL";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit81() throws Exception {
		// test input: "package org.drools;\n\nrule \"test pluggable operators\"\nwhen\n    $a : EventA()\n    $b : EventB( this after[1,10] $a )\n    $c : EventC( this finishes $b )\n    $d : EventD( this not starts $a )\n    $e : EventE( this not before [1, 10] $b )\nthen\nend"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "package org.drools;\n\nrule \"test pluggable operators\"\nwhen\n    $a : EventA()\n    $b : EventB( this after[1,10] $a )\n    $c : EventC( this finishes $b )\n    $d : EventD( this not starts $a )\n    $e : EventE( this not before [1, 10] $b )\nthen\nend", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit82() throws Exception {
		// test input: "rule \"Test\"\nwhen\n( $r :LiteralRestriction( operator == Operator.EQUAL ) )\n        then\n    end"
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule \"Test\"\nwhen\n( $r :LiteralRestriction( operator == Operator.EQUAL ) )\n        then\n    end", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testCompilation_unit_walks_Compilation_unit83() throws Exception {
		// test input: "rule \"Test2\"\nwhen\n( not $r :LiteralRestriction( operator == Operator.EQUAL ) )\n        then\n    end "
		Object retval = execTreeParser("compilation_unit", "compilation_unit", "rule \"Test2\"\nwhen\n( not $r :LiteralRestriction( operator == Operator.EQUAL ) )\n        then\n    end ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"compilation_unit", expecting, actual);
	}

    @Test
    public void testLhs_walks_Pattern_source1() throws Exception {
		// test input: "StockTick( symbol==\"ACME\") from entry-point StreamA"
		Object retval = execTreeParser("lhs", "pattern_source", "StockTick( symbol==\"ACME\") from entry-point StreamA", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block1() throws Exception {
		// test input: ""
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block2() throws Exception {
		// test input: "     Country( $cities : city )\n     Person( city memberOf $cities )\n    "
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "     Country( $cities : city )\n     Person( city memberOf $cities )\n    ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block3() throws Exception {
		// test input: "     Country( $cities : city )\n     Person( city not memberOf $cities )\n    "
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "     Country( $cities : city )\n     Person( city not memberOf $cities )\n    ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block4() throws Exception {
		// test input: " Person( age < 42 && location==\"atlanta\") "
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", " Person( age < 42 && location==\"atlanta\") ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block5() throws Exception {
		// test input: " Person( age < 42 || location==\"atlanta\") "
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", " Person( age < 42 || location==\"atlanta\") ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block6() throws Exception {
		// test input: "Person( age < 42 && location==\"atlanta\" || age > 20 && location==\"Seatle\" || location == \"Chicago\")"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "Person( age < 42 && location==\"atlanta\" || age > 20 && location==\"Seatle\" || location == \"Chicago\")", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block7() throws Exception {
		// test input: "Person( age < 42 && ( location==\"atlanta\" || age > 20 && location==\"Seatle\") || location == \"Chicago\")"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "Person( age < 42 && ( location==\"atlanta\" || age > 20 && location==\"Seatle\") || location == \"Chicago\")", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block8() throws Exception {
		// test input: " Person( ( age == 70 && hair == \"black\" ) || ( age == 40 && hair == \"pink\" ) || ( age == 12 && ( hair == \"yellow\" || hair == \"blue\" ) ) ) "
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", " Person( ( age == 70 && hair == \"black\" ) || ( age == 40 && hair == \"pink\" ) || ( age == 12 && ( hair == \"yellow\" || hair == \"blue\" ) ) ) ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block9() throws Exception {
		// test input: " Person( name matches \"mark\" || matches \"bob\" ) "
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", " Person( name matches \"mark\" || matches \"bob\" ) ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block10() throws Exception {
		// test input: "\tCity( $city : city )\n\tCountry( cities not contains $city )\n\t"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "\tCity( $city : city )\n\tCountry( cities not contains $city )\n\t", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block11() throws Exception {
		// test input: " Message( text not matches '[abc]*' ) "
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", " Message( text not matches '[abc]*' ) ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block12() throws Exception {
		// test input: "Foo( bar > 1 || == 1 )"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "Foo( bar > 1 || == 1 )", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block13() throws Exception {
		// test input: "\t(or\n\tnot Person()\n\t\t(and Cheese()\n\t\t\tMeat()\n\t\t\tWine()))\n\t"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "\t(or\n\tnot Person()\n\t\t(and Cheese()\n\t\t\tMeat()\n\t\t\tWine()))\n\t", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block14() throws Exception {
		// test input: "Person( ( age ( > 60 && < 70 ) || ( > 50 && < 55 ) && hair == \"black\" ) || ( age == 40 && hair == \"pink\" ) || ( age == 12 && ( hair == \"yellow\" || hair == \"blue\" ) ))"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "Person( ( age ( > 60 && < 70 ) || ( > 50 && < 55 ) && hair == \"black\" ) || ( age == 40 && hair == \"pink\" ) || ( age == 12 && ( hair == \"yellow\" || hair == \"blue\" ) ))", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block15() throws Exception {
		// test input: "org   .   drools/*comment*/\t  .Message( text not matches $c#comment\n. property )"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "org   .   drools/*comment*/\t  .Message( text not matches $c#comment\n. property )", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block16() throws Exception {
		// test input: " Test( ( text == null || text matches \"\" ) )  "
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", " Test( ( text == null || text matches \"\" ) )  ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block17() throws Exception {
		// test input: " $id : Something( duration == \"foo\") "
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", " $id : Something( duration == \"foo\") ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block18() throws Exception {
		// test input: "foo3 : Bar("
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "foo3 : Bar(", false);
		Object actual = examineParserExecResult(28, retval);
		Object expecting = "FAIL";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block19() throws Exception {
		// test input: "Cheese(name == \"Stilton\", age==2001)\nWine(name == \"Grange\", age == \"1978\", accolades contains \"world champion\")"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "Cheese(name == \"Stilton\", age==2001)\nWine(name == \"Grange\", age == \"1978\", accolades contains \"world champion\")", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block20() throws Exception {
		// test input: "Foo()"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "Foo()", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block21() throws Exception {
		// test input: "not Cheese(type == \"stilton\")"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "not Cheese(type == \"stilton\")", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block22() throws Exception {
		// test input: "Person(age < 42, location==\"atlanta\") \nor\nPerson(name==\"bob\")"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "Person(age < 42, location==\"atlanta\") \nor\nPerson(name==\"bob\")", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block23() throws Exception {
		// test input: "Foo(bar == false)\nFoo(boo > -42)\nFoo(boo > -42.42)"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "Foo(bar == false)\nFoo(boo > -42)\nFoo(boo > -42.42)", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block24() throws Exception {
		// test input: "Cheese( )"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "Cheese( )", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block25() throws Exception {
		// test input: "Col1() from something.doIt( foo,bar,42,\"hello\",{ a => \"b\", \"something\" => 42, \"a\" => foo, x => {x=>y}},\"end\", [a, \"b\", 42] )\nCol2()"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "Col1() from something.doIt( foo,bar,42,\"hello\",{ a => \"b\", \"something\" => 42, \"a\" => foo, x => {x=>y}},\"end\", [a, \"b\", 42] )\nCol2()", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block26() throws Exception {
		// test input: "Col1() from doIt( foo,bar,42,\"hello\",{ a => \"b\", \"something\" => 42, \"a\" => foo, x => {x=>y}},\"end\", [a, \"b\", 42] )\nCol2()"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "Col1() from doIt( foo,bar,42,\"hello\",{ a => \"b\", \"something\" => 42, \"a\" => foo, x => {x=>y}},\"end\", [a, \"b\", 42] )\nCol2()", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block27() throws Exception {
		// test input: "Col1() from something.doIt\nCol2()"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "Col1() from something.doIt\nCol2()", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block28() throws Exception {
		// test input: "Col1() from something.doIt[\"key\"]\nCol2()"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "Col1() from something.doIt[\"key\"]\nCol2()", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block29() throws Exception {
		// test input: "Col1() from doIt1( foo,bar,42,\"hello\",{ a => \"b\"}, [a, \"b\", 42] )\n            .doIt2(bar, [a, \"b\", 42]).field[\"key\"]\nCol2()"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "Col1() from doIt1( foo,bar,42,\"hello\",{ a => \"b\"}, [a, \"b\", 42] )\n            .doIt2(bar, [a, \"b\", 42]).field[\"key\"]\nCol2()", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block30() throws Exception {
		// test input: "foo3 : Bar(a==3)\nfoo4 : Bar(a4:a==4)\nBaz()"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "foo3 : Bar(a==3)\nfoo4 : Bar(a4:a==4)\nBaz()", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block31() throws Exception {
		// test input: "Person(age > 30 && < 40)\nVehicle(type == \"sedan\" || == \"wagon\", age < 3)"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "Person(age > 30 && < 40)\nVehicle(type == \"sedan\" || == \"wagon\", age < 3)", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block32() throws Exception {
		// test input: "    foo3 : Bar(a==3) ; foo4 : Bar(a4:a==4) ; Baz()"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "    foo3 : Bar(a==3) ; foo4 : Bar(a4:a==4) ; Baz()", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block33() throws Exception {
		// test input: "not ( Cheese(type == \"stilton\") )\nexists ( Foo() )"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "not ( Cheese(type == \"stilton\") )\nexists ( Foo() )", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block34() throws Exception {
		// test input: "not ( Cheese(type == \"stilton\") )\nexists ( Foo() )"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "not ( Cheese(type == \"stilton\") )\nexists ( Foo() )", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block35() throws Exception {
		// test input: "a : (not ( Cheese(type == \"stilton\") ))\nexists ( Foo() )"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "a : (not ( Cheese(type == \"stilton\") ))\nexists ( Foo() )", false);
		Object actual = examineParserExecResult(28, retval);
		Object expecting = "FAIL";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block36() throws Exception {
		// test input: " Cheese( t:type == \"cheddar\" ) "
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", " Cheese( t:type == \"cheddar\" ) ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block37() throws Exception {
		// test input: "Cheese( $type:type )"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "Cheese( $type:type )", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block38() throws Exception {
		// test input: "    Cheese($type : type == \"stilton\")\n    $person : Person($name : name == \"bob\", likes == $type)        "
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "    Cheese($type : type == \"stilton\")\n    $person : Person($name : name == \"bob\", likes == $type)        ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block39() throws Exception {
		// test input: "Person(name == \"mark\") or \n( Person(type == \"fan\") and Cheese(type == \"green\") )"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "Person(name == \"mark\") or \n( Person(type == \"fan\") and Cheese(type == \"green\") )", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block40() throws Exception {
		// test input: "Person(name == \"mark\") && Cheese(type == \"stilton\")\nPerson(name == \"mark\") || Cheese(type == \"stilton\")"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "Person(name == \"mark\") && Cheese(type == \"stilton\")\nPerson(name == \"mark\") || Cheese(type == \"stilton\")", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block41() throws Exception {
		// test input: "foo :  ( Person(name == \"mark\") or Person(type == \"fan\") ) \nCheese(type == \"green\")"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "foo :  ( Person(name == \"mark\") or Person(type == \"fan\") ) \nCheese(type == \"green\")", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block42() throws Exception {
		// test input: "foo : ( Person(name == \"mark\") \n\tor \n\tPerson(type == \"fan\") )"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "foo : ( Person(name == \"mark\") \n\tor \n\tPerson(type == \"fan\") )", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block43() throws Exception {
		// test input: "foo : ( \n\tPerson(name == \"mark\") or Person(type == \"fan\") \n\t)"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "foo : ( \n\tPerson(name == \"mark\") or Person(type == \"fan\") \n\t)", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block44() throws Exception {
		// test input: " ( (not Foo(x==\"a\") or Foo(x==\"y\") ) and ( Shoes() or Butt() ) )"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", " ( (not Foo(x==\"a\") or Foo(x==\"y\") ) and ( Shoes() or Butt() ) )", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block45() throws Exception {
		// test input: "eval(abc(\"foo\") + 5)\nFoo()\neval(qed())\nBar()"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "eval(abc(\"foo\") + 5)\nFoo()\neval(qed())\nBar()", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block46() throws Exception {
		// test input: "Foo()\nBar()\neval(abc(\"foo\"))"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "Foo()\nBar()\neval(abc(\"foo\"))", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block47() throws Exception {
		// test input: "Foo(name== (a + b))"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "Foo(name== (a + b))", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block48() throws Exception {
		// test input: "Person( $age2:age -> ($age2 == $age1+2 ) )"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "Person( $age2:age -> ($age2 == $age1+2 ) )", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block49() throws Exception {
		// test input: "Foo(bar == Foo.BAR)"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "Foo(bar == Foo.BAR)", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block50() throws Exception {
		// test input: "p: Person( name soundslike \"Michael\" )"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "p: Person( name soundslike \"Michael\" )", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block51() throws Exception {
		// test input: "Foo()\nBar()\neval(\n\n\n\n       abc(\n       \n       \"foo\") + \n       5\n       \n       \n       \n        \n       )"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "Foo()\nBar()\neval(\n\n\n\n       abc(\n       \n       \"foo\") + \n       5\n       \n       \n       \n        \n       )", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block52() throws Exception {
		// test input: "eval(abc();)"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "eval(abc();)", false);
		Object actual = examineParserExecResult(28, retval);
		Object expecting = "FAIL";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block53() throws Exception {
		// test input: "Foo(\n  bar == baz, la==laz\n  )\n "
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "Foo(\n  bar == baz, la==laz\n  )\n ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block54() throws Exception {
		// test input: "com.cheeseco.Cheese($type : type == \"stilton\")"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "com.cheeseco.Cheese($type : type == \"stilton\")", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block55() throws Exception {
		// test input: "     Integer() from accumulate( Person( age > 21 ),\n                            init( int x = 0; ),\n                            action( x++; ),\n                            result( new Integer(x) ) );"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "     Integer() from accumulate( Person( age > 21 ),\n                            init( int x = 0; ),\n                            action( x++; ),\n                            result( new Integer(x) ) );", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block56() throws Exception {
		// test input: " $counter:Integer() from accumulate( $person : Person( age > 21 ),\n                                     init( int x = 0; ),\n                                     action( x++; ),\n                                     result( new Integer(x) ) );"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", " $counter:Integer() from accumulate( $person : Person( age > 21 ),\n                                     init( int x = 0; ),\n                                     action( x++; ),\n                                     result( new Integer(x) ) );", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block57() throws Exception {
		// test input: "$personList : ArrayList() from collect( Person( age > 21 ) );"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "$personList : ArrayList() from collect( Person( age > 21 ) );", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block58() throws Exception {
		// test input: "\tnot ( State( $state : state ) and\n          not( Person( status == $state, $likes : likes ) and\n               Cheese( type == $likes ) ) )\n    Person( name == \"Bob\" )\n    ( Cheese( price == 10 ) or Cheese( type == \"brie\" ) )"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "\tnot ( State( $state : state ) and\n          not( Person( status == $state, $likes : likes ) and\n               Cheese( type == $likes ) ) )\n    Person( name == \"Bob\" )\n    ( Cheese( price == 10 ) or Cheese( type == \"brie\" ) )", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block59() throws Exception {
		// test input: " forall( Person( age > 21, $likes : likes )\n         Cheese( type == $likes ) );"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", " forall( Person( age > 21, $likes : likes )\n         Cheese( type == $likes ) );", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block60() throws Exception {
		// test input: "  \tPerson(age > 30 && < 40)\n  \tVehicle(type in ( \"sedan\", \"wagon\" ), age < 3)\n\t"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "  \tPerson(age > 30 && < 40)\n  \tVehicle(type in ( \"sedan\", \"wagon\" ), age < 3)\n\t", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block61() throws Exception {
		// test input: "  \tPerson(age > 30 && < 40)\n  \tVehicle(type not in ( \"sedan\", \"wagon\" ), age < 3)\n\t"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "  \tPerson(age > 30 && < 40)\n  \tVehicle(type not in ( \"sedan\", \"wagon\" ), age < 3)\n\t", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block62() throws Exception {
		// test input: "\t\tPattern1();\n\t\tPattern2() from x.y.z;\n\t\tPattern5();\n\t\tPattern6();\n\t\tPattern7();\n\t\tPattern3();\n\t\tPattern4() from collect( Pattern5() );\n\t\t"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "\t\tPattern1();\n\t\tPattern2() from x.y.z;\n\t\tPattern5();\n\t\tPattern6();\n\t\tPattern7();\n\t\tPattern3();\n\t\tPattern4() from collect( Pattern5() );\n\t\t", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block63() throws Exception {
		// test input: " eval( 3==3 ) "
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", " eval( 3==3 ) ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block64() throws Exception {
		// test input: "\t\tInteger() from accumulate( Person( age > 21 ),\n\t\t                           init( int x = 0; ),\n\t\t                           action( x++; ),\n\t\t                           reverse( x--; ),\n\t\t                           result( new Integer(x) ) );\n\t\t"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "\t\tInteger() from accumulate( Person( age > 21 ),\n\t\t                           init( int x = 0; ),\n\t\t                           action( x++; ),\n\t\t                           reverse( x--; ),\n\t\t                           result( new Integer(x) ) );\n\t\t", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block65() throws Exception {
		// test input: "\t     Number() from accumulate( Person( $age : age > 21 ),\n\t                               average( $age ) );\n\t\t"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "\t     Number() from accumulate( Person( $age : age > 21 ),\n\t                               average( $age ) );\n\t\t", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block66() throws Exception {
		// test input: "\t     #bellow statement makes no sense, but is useful to test parsing recursiveness\n\t     $personList : ArrayList() from collect( $p : Person( age > 21 || age < 10 ) from collect( People() from $town.getPeople() ) );\n\t\t"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "\t     #bellow statement makes no sense, but is useful to test parsing recursiveness\n\t     $personList : ArrayList() from collect( $p : Person( age > 21 || age < 10 ) from collect( People() from $town.getPeople() ) );\n\t\t", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block67() throws Exception {
		// test input: "\t     $personList : ArrayList() from accumulate( Person( $age : age > 21 || < 10 ) from collect( People() from $town.getPeople() ),\n\t                                                max( $age ) );\n\t\t"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "\t     $personList : ArrayList() from accumulate( Person( $age : age > 21 || < 10 ) from collect( People() from $town.getPeople() ),\n\t                                                max( $age ) );\n\t\t", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block68() throws Exception {
		// test input: "\t    $p : Person( name == \"bob\" )\n\t    $c : Cheese( type == $p.likes ) || Cheese( price == 10 )\n\t    "
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "\t    $p : Person( name == \"bob\" )\n\t    $c : Cheese( type == $p.likes ) || Cheese( price == 10 )\n\t    ", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block69() throws Exception {
		// test input: "\t\t     $counter:Integer() from accumulate( $person : Person( age > 21 ) and Cheese( type == $person.likes ),\n\t\t                                         init( int x = 0; ),\n\t\t                                         action( x++; ),\n\t\t                                         result( new Integer(x) ) );\n\t\t\t"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "\t\t     $counter:Integer() from accumulate( $person : Person( age > 21 ) and Cheese( type == $person.likes ),\n\t\t                                         init( int x = 0; ),\n\t\t                                         action( x++; ),\n\t\t                                         result( new Integer(x) ) );\n\t\t\t", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block70() throws Exception {
		// test input: "\t\t    $a : EventA()\n\t\t    $b : EventB( this after[1,10] $a )\n\t\t    $c : EventC( this finishes $b )\n\t\t    $d : EventD( this not starts $a )\n\t\t    $e : EventE( this not before [1, 10] $b )\n\t\t\t"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "\t\t    $a : EventA()\n\t\t    $b : EventB( this after[1,10] $a )\n\t\t    $c : EventC( this finishes $b )\n\t\t    $d : EventD( this not starts $a )\n\t\t    $e : EventE( this not before [1, 10] $b )\n\t\t\t", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testLhs_block_walks_Normal_lhs_block71() throws Exception {
		// test input: "StockTick( symbol==\"ACME\") from entry-point StreamA"
		Object retval = execTreeParser("lhs_block", "normal_lhs_block", "StockTick( symbol==\"ACME\") from entry-point StreamA", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"lhs_block", expecting, actual);
	}

    @Test
    public void testFact_expression_walks_Constraints1() throws Exception {
		// test input: "eval( $var.equals(\"xyz\") )"
		Object retval = execTreeParser("fact_expression", "constraints", "eval( $var.equals(\"xyz\") )", false);
		Object actual = examineParserExecResult(27, retval);
		Object expecting = "OK";
		
		assertEquals("testing rule "+"fact_expression", expecting, actual);
	}

	// Invoke target parser.rule
	public Object execTreeParser(String testTreeRuleName, String testRuleName, String testInput, boolean isFile) throws Exception {
		CharStream input;
		if ( isFile==true ) {
			input = new ANTLRFileStream(testInput);
		}
		else {
			input = new ANTLRStringStream(testInput);
		}
		try {
			DRLLexer lexer = new DRLLexer(input);
			CommonTokenStream tokens = new CommonTokenStream(lexer);
			DRLParser parser = new DRLParser(tokens);
			parser.setTreeAdaptor(new DroolsTreeAdaptor());
			/** Use Reflection to get rule method from parser */
			Method ruleName = Class.forName("org.drools.lang.DRLParser").getMethod(testRuleName);

			/** Start of I/O Redirecting */
			PipedInputStream pipedIn = new PipedInputStream();
			PipedOutputStream pipedOut = new PipedOutputStream();
			PipedInputStream pipedErrIn = new PipedInputStream();
			PipedOutputStream pipedErrOut = new PipedOutputStream();
			try {
				pipedOut.connect(pipedIn);
				pipedErrOut.connect(pipedErrIn);
			}
			catch(IOException e) {
				System.err.println("connection failed...");
				System.exit(1);
			}
			PrintStream console = System.out;
			PrintStream consoleErr = System.err;
			PrintStream ps = new PrintStream(pipedOut);
			PrintStream ps2 = new PrintStream(pipedErrOut);
			System.setOut(ps);
			System.setErr(ps2);
			/** End of redirecting */

			/** Invoke grammar rule, and get the return value */
			Object ruleReturn = ruleName.invoke(parser);
			
			Class _return = Class.forName("org.drools.lang.DRLParser"+"$"+testRuleName+"_return");            	
        	Method returnName = _return.getMethod("getTree");
        	CommonTree tree = (CommonTree) returnName.invoke(ruleReturn);
			
			// Walk resulting tree; create tree nodes stream first
        	CommonTreeNodeStream nodes = new CommonTreeNodeStream(tree);
        	// AST nodes have payload that point into token stream
        	nodes.setTokenStream(tokens);
        	// Create a tree walker attached to the nodes stream
        	Tree2TestDRL treeParser = new Tree2TestDRL(nodes);
        	/** Invoke the tree rule, and store the return value if there is */
            Method treeRuleName = Class.forName("org.drools.lang.Tree2TestDRL").getMethod(testTreeRuleName);
            Object treeRuleReturn = treeRuleName.invoke(treeParser);
            
            String astString = null;
            /** If tree rule has return value, determine if it's an AST */
            if ( treeRuleReturn!=null ) {
            	/** If return object is instanceof AST, get the toStringTree */
                if ( treeRuleReturn.toString().indexOf(testTreeRuleName+"_return")>0 ) {
                	try {	// NullPointerException may happen here...
                		Class _treeReturn = Class.forName("org.drools.lang.Tree2TestDRL"+"$"+testTreeRuleName+"_return");
                		Method[] methods = _treeReturn.getDeclaredMethods();
			            for(Method method : methods) {
			                if ( method.getName().equals("getTree") ) {
			                	Method treeReturnName = _treeReturn.getMethod("getTree");
		                    	CommonTree returnTree = (CommonTree) treeReturnName.invoke(treeRuleReturn);
		                        astString = returnTree.toStringTree();
			                }
			            }
                	}
                	catch(Exception e) {
                		System.err.println(e);
                	}
                }
            }
            
            // @FIXME etirelli !!!
//			org.antlr.gunit.gUnitExecuter.StreamVacuum stdoutVacuum = new org.antlr.gunit.gUnitExecuter.StreamVacuum(pipedIn);
//			org.antlr.gunit.gUnitExecuter.StreamVacuum stderrVacuum = new org.antlr.gunit.gUnitExecuter.StreamVacuum(pipedErrIn);
//			ps.close();
//			ps2.close();
//			System.setOut(console);			// Reset standard output
//			System.setErr(consoleErr);		// Reset standard err out
//			this.stdout = null;
//			this.stderr = null;
//			stdoutVacuum.start();
//			stderrVacuum.start();			
//			stdoutVacuum.join();
//			stderrVacuum.join();
//			// retVal could be actual return object from rule, stderr or stdout
//			if ( stderrVacuum.toString().length()>0 ) {
//				this.stderr = stderrVacuum.toString();
//				return this.stderr;
//			}
//            if ( parser.hasErrors() ) {
//                this.stderr = parser.getErrors().toString();
//                return this.stderr;
//            }
//			if ( stdoutVacuum.toString().length()>0 ) {
//				this.stdout = stdoutVacuum.toString();
//			}
//			if ( astString!=null ) {	// Return toStringTree of AST
//				return astString;
//			}
//			if ( treeRuleReturn!=null ) {
//				return treeRuleReturn;
//			}
//			if ( stderrVacuum.toString().length()==0 && stdoutVacuum.toString().length()==0 ) {
//				return null;
//			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace(); System.exit(1);
		} catch (SecurityException e) {
			e.printStackTrace(); System.exit(1);
		} catch (NoSuchMethodException e) {
			e.printStackTrace(); System.exit(1);
		} catch (IllegalAccessException e) {
			e.printStackTrace(); System.exit(1);
		} catch (InvocationTargetException e) {
//			this.stderr = "error";
			return e.getCause().toString();
//		} catch (InterruptedException e) {
//			e.printStackTrace(); System.exit(1);
		} catch (Exception e) {
			e.printStackTrace(); System.exit(1);
		}
		return stdout;
	}

	// Modify the return value if the expected token type is OK or FAIL
	public Object examineParserExecResult(int tokenType, Object retVal) {	
		if ( tokenType==27 ) {	// expected Token: OK
			if ( this.stderr==null ) {
				return "OK";
			}
			else {
				return "FAIL";
			}
		}
		else if ( tokenType==28 ) {	// expected Token: FAIL
			if ( this.stderr!=null ) {
				return "FAIL";
			}
			else {
				return "OK";
			}
		}
		else {	// return the same object for the other token types
			return retVal;
		}		
	}

}
