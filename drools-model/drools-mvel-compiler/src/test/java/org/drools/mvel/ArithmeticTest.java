/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Borrowed from MVEL, under the ASL2.0 license.
 * 
 */

package org.drools.mvel;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.mvel.MVEL.executeExpressionWithDefaultVariables;
import static org.drools.mvel.MVEL.executeExpression;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;



/**
 * Porting of ArithmeticTests from MVEL project.
 * 
 * Disabled tests are failing for various causes
 * 
 */

public class ArithmeticTest {
	
  @Ignore("DROOLS-6572 - Generates wrong code for promotion")
  @Test
  public void testMath() {
    String expression = "pi * hour";
    int result = 188;

    assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Test
  public void testMath2() {
    assertEquals(3, executeExpressionWithDefaultVariables("foo.number-1"));
  }

  @Ignore("DROOLS-6572 - Rounding error")
  @Test
  public void testMath3() {
    String expression = "(10 * 5) * 2 / 3";
	double result = (10d * 5d) * 2d / 3d;
	
	assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Ignore("DROOLS-6572 - Rounding error")
  @Test
  public void testMath4() {
	String expression = "(100 % 3) * 2 - 1 / 1 + 8 + (5 * 2)";
    double result = ((100d % 3d) * 2d - 1d / 1d + 8d + (5d * 2d));
    
	assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Ignore("DROOLS-6572 - Rounding error")
  @Test
  public void testMath4a() {
    String expression = "(100 % 90) * 20 - 15 / 16 + 80 + (50 * 21)";
    double result = (100d % 90d) * 20d - 15d / 16d + 80d + (50d * 21d);
    
	assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Test
  public void testMath5() {
    String expression = "300.5 / 5.3 / 2.1 / 1.5";
	double result = 300.5 / 5.3 / 2.1 / 1.5;
	
	assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Test
  public void testMath5a() {
    String expression = "300.5 / 5.3 / 2.1 / 1.5";
    double result = 300.5 / 5.3 / 2.1 / 1.5;
    
	assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Ignore("DROOLS-6572 - Rounding error")
  @Test
  public void testMath6() {
    String expression = "(300 * five + 1) + (100 / 2 * 2)";
    double result = (300 * 5 + 1) + 100 / 2 * 2;

    assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Ignore("DROOLS-6572 - Rounding error")
  @Test
  public void testMath7() {
	String expression = "(100 % 3) * 2 - 1 / 1 + 8 + (5 * 2)";
    double result = ((100d % 3d) * 2d - 1d / 1d + 8d + (5d * 2d));

	assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Test
  public void testMath8() {
	String expression = "5 * (100.56 * 30.1)";
    double result = 5d * (100.56d * 30.1d);
	
    assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Ignore("DROOLS-6572 - Unable to parse **")
  @Test
  public void testPowerOf() {
    assertEquals(25, executeExpressionWithDefaultVariables("5 ** 2"));
  }

  @Ignore("DROOLS-6572 - Unable to parse")
  @Test
  public void testSignOperator() {
    String expr = "int x = 15; -x";
    Map<String, Object> vars = new HashMap<>();

    assertEquals(-15, executeExpression(expr, vars));
  }

  @Test
  public void testMath14() {
	String expression = "10-5*2 + 5*8-4";
    int result = 10 - 5 * 2 + 5 * 8 - 4;
	
    assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Test
  public void testMath15() {
    String expression = "100-500*200 + 500*800-400";
    int result = 100 - 500 * 200 + 500 * 800 - 400;
    
	assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Test
  public void testMath16() {
    String expression = "100-500*200*150 + 500*800-400";
    int result = 100 - 500 * 200 * 150 + 500 * 800 - 400;
    
	assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Test
  public void testMath17() {
    String expression = "(100d * 50d) * 20d / 30d * 2d";
    double result = (100d * 50d) * 20d / 30d * 2d;
    
	assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Ignore("DROOLS-6572 - Unable to parse multiple assignements")
  @Test
  public void testMath18() {
    String expression = "a = 100d; b = 50d; c = 20d; d = 30d; e = 2d; (a * b) * c / d * e";
    double result = (100d * 50d) * 20d / 30d * 2d;

    assertEquals(result, executeExpression(expression, Collections.emptyMap()));
  }

  @Ignore("DROOLS-6572 - Unable to parse multiple assignments")
  @Test
  public void testMath19() {
    String expression = "a = 100; b = 500; c = 200; d = 150; e = 500; f = 800; g = 400; a-b*c*d + e*f-g";
    int result = 100 - 500 * 200 * 150 + 500 * 800 - 400;
    assertEquals(result, executeExpression(expression, Collections.emptyMap()));
  }

  @Ignore("DROOLS-6572 - Unable to parse")
  @Test
  public void testMath32() {
    String expression = "x = 20; y = 10; z = 5; x-y-z";
    int result = 20 - 10 - 5;
    
	assertEquals(result, executeExpression(expression, Collections.emptyMap()));
  }

  @Ignore("DROOLS-6572 - Unable to parse")
  @Test
  public void testMath33() {
    String expression = "x = 20; y = 2; z = 2; x/y/z";
    int result = 20 / 2 / 2;
    
	assertEquals(result, executeExpression(expression, Collections.emptyMap()));
  }

  @Test
  public void testMath20() {
    String expression = "10-5*7-3*8-6";
    int result = 10 - 5 * 7 - 3 * 8 - 6;
    
	assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Test
  public void testMath21() {
    String expression = "100-50*70-30*80-60";
    int expected = 100 - 50 * 70 - 30 * 80 - 60;
    
	assertEquals(expected, executeExpressionWithDefaultVariables(expression));
  }

  @Ignore("DROOLS-6572 - Unable to parse **")
  @Test
  public void testMath22() {
    String expression = "(100-50)*70-30*(20-9)**3";
    int result = (int) ((100 - 50) * 70 - 30 * Math.pow(20 - 9, 3));
    
	assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Ignore("DROOLS-6572 - Unable to parse **")
  @Test
  public void testMath22b() {
    String expression = "a = 100; b = 50; c = 70; d = 30; e = 20; f = 9; g = 3; (a-b)*c-d*(e-f)**g";
    int result = (int) ((100 - 50) * 70 - 30 * Math.pow(20 - 9, 3));
    
	assertEquals(result, executeExpression(expression, Collections.emptyMap()));
  }

  @Ignore("DROOLS-6572 - Unable to parse **")
  @Test
  public void testMath23() {
    String expression = "10 ** (3)*10**3";
    int result = (int) (Math.pow(10, 3) * Math.pow(10, 3));
    
	assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Ignore("DROOLS-6572 - Rounding error")
  @Test
  public void testMath24() {
    String expression = "51 * 52 * 33 / 24 / 15 + 45 * 66 * 47 * 28 + 19";
    double result = 51d * 52d * 33d / 24d / 15d + 45d * 66d * 47d * 28d + 19d;

    assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Ignore("DROOLS-6572 - Calculation error")
  @Test
  public void testMath25() {
    String expression = "51 * (40 - 1000 * 50) + 100 + 50 * 20 / 10 + 11 + 12 - 80";
    double result = 51 * (40 - 1000 * 50) + 100 + 50 * 20 / 10 + 11 + 12 - 80;
    
    assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Ignore("DROOLS-6572 - Unable to parse **")
  @Test
  public void testMath26() {
    String expression = "5 + 3 * 8 * 2 ** 2";
    int result = (int) (5d + 3d * 8d * Math.pow(2, 2));
    
    assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Ignore("DROOLS-6572 - Unable to parse **")
  @Test
  public void testMath27() {
    String expression = "50 + 30 * 80 * 20 ** 3 * 51";
    double result = 50 + 30 * 80 * Math.pow(20, 3) * 51;
    
    assertEquals((int) result, executeExpressionWithDefaultVariables(expression));
  }

  @Ignore("DROOLS-6572 - Unable to parse **")
  @Test
  public void testMath28() {
    String expression = "50 + 30 + 80 + 11 ** 2 ** 2 * 51";
    double result = 50 + 30 + 80 + Math.pow(Math.pow(11, 2), 2) * 51;
    
    assertEquals((int) result, executeExpressionWithDefaultVariables(expression));
  }

  @Ignore("DROOLS-6572 - Rounding error")
  @Test
  public void testMath29() {
    String expression = "10 + 20 / 4 / 4";
    double result = 10d + 20d / 4d / 4d;

    assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Ignore("DROOLS-6572 - Rounding error")
  @Test
  public void testMath30() {
    String expression = "40 / 20 + 10 + 60 / 21";
    double result = 40d / 20d + 10d + 60d / 21d;
    
    assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Ignore("DROOLS-6572 - Unable to parse **")
  @Test
  public void testMath31() {
    String expression = "40 / 20 + 5 - 4 + 8 / 2 * 2 * 6 ** 2 + 6 - 8";
    double result = 40f / 20f + 5f - 4f + 8f / 2f * 2f * Math.pow(6, 2) + 6f - 8f;
    
    assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Ignore("DROOLS-6572 - Rounding error")
  @Test
  public void testMath34() {
    String expression = "a+b-c*d*x/y-z+10";
    double result = (double) 200 + 100 - 150 * 2 * 400 / 300 - 75 + 10;

    Map<String, Object> map = new HashMap<>();
    map.put("a", 200);
    map.put("b", 100);
    map.put("c", 150);
    map.put("d", 2);
    map.put("x", 400);
    map.put("y", 300);
    map.put("z", 75);

	assertEquals(result, executeExpression(expression, map));
  }

  @Ignore("DROOLS-6572 - Rounding error")
  @Test
  public void testMath34_Interpreted() {
    String expression = "a+b-c*x/y-z";
    double result = (double) 200 + 100 - 150 * 400 / 300 - 75;

    Map<String, Object> map = new HashMap<>();
    map.put("a", 200);
    map.put("b", 100);
    map.put("c", 150);
    map.put("x", 400);
    map.put("y", 300);
    map.put("z", 75);

	assertEquals(result, executeExpression(expression, map));
  }

  @Ignore("DROOLS-6572 - Rounding error")
  @Test
  public void testMath35() {
    String expression = "b/x/b/b*y+a";
    double result = 20d / 40d / 20d / 20d * 50d + 10d;

    Map<String, Object> map = new HashMap<>();
    map.put("a", 10);
    map.put("b", 20);
    map.put("c", 30);
    map.put("x", 40);
    map.put("y", 50);
    map.put("z", 60);

	assertEquals((Object) result, executeExpression(expression, map));
  }


  @Ignore("DROOLS-6572 - Rounding error")
  @Test
  public void testMath35_Interpreted() {
    String expression = "b/x/b/b*y+a";
    double result = 20d / 40d / 20d / 20d * 50d + 10d;

    Map<String, Object> map = new HashMap<>();
    map.put("a", 10);
    map.put("b", 20);
    map.put("c", 30);
    map.put("x", 40);
    map.put("y", 50);
    map.put("z", 60);

	assertEquals((Object) result, executeExpression(expression, map));
  }

  @Ignore("DROOLS-6572 - Rounding error")
  @Test
  public void testMath36() {
    String expression = "b/x*z/a+x-b+x-b/z+y";
    double result = 20d / 40d * 60d / 10d + 40d - 20d + 40d - 20d / 60d + 50d;

    Map<String, Object> map = new HashMap<>();
    map.put("a", 10);
    map.put("b", 20);
    map.put("c", 30);
    map.put("x", 40);
    map.put("y", 50);
    map.put("z", 60);

	assertEquals((Object) result, executeExpression(expression, map));
  }

  @Ignore("DROOLS-6572 - Rounding error")
  @Test
  public void testMath37() {
    String expression = "x+a*a*c/x*b*z+x/y-b";
    double result = 2d + 10d * 10d * 30d / 2d * 20d * 60d + 2d / 2d - 20d;

    Map<String, Object> map = new HashMap<>();
    map.put("a", 10);
    map.put("b", 20);
    map.put("c", 30);
    map.put("x", 2);
    map.put("y", 2);
    map.put("z", 60);

	assertEquals((Object) result, executeExpression(expression, map));
  }

  @Ignore("DROOLS-6572 - Rounding error")
  @Test
  public void testMath38() {
    String expression = "100 + 200 - 300 + 400 - 500 + 105 / 205 - 405 + 305 * 206";
    double result = 100d + 200d - 300d + 400d - 500d + 105d / 205d - 405d + 305d * 206d;

    assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Ignore("DROOLS-6572 - Rounding error")
  @Test
  public void testMath39() {
    String expression = "147 + 60 / 167 % 448 + 36 * 23 / 166";
    double result = 147d + 60d / 167d % 448d + 36d * 23d / 166d;

    assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Ignore("DROOLS-6572 - Rounding error")
  @Test
  public void testMath40() {
    String expression = "228 - 338 % 375 - 103 + 260 + 412 * 177 + 121";
    double result = 228d - 338d % 375d - 103d + 260d + 412d * 177d + 121d;

    assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Test
  public void testMath41() {
    String expression = "304d - 246d / 242d % 235d / 425d - 326d + 355d * 264d % 308d";
    double result = 304d - 246d / 242d % 235d / 425d - 326d + 355d * 264d % 308d;

    assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Test
  public void testMath42() {
    String expression = "11d - 7d / 3d * 18d % 14d * 8d * 11d - 2d - 11d / 13d + 14d";
    double result = 11d - 7d / 3d * 18d % 14d * 8d * 11d - 2d - 11d / 13d + 14d;

    assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Test
  public void testMath43() {
    String expression = "4d/3d*6d%8d*5d*8d+7d+9d*1d";
    double result = 4d / 3d * 6d % 8d * 5d * 8d + 7d + 9d * 1d;

    assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Test
  public void testMath44() {
    String expression = "6d+8d/9d*1d*9d*10d%4d*4d-4d*6d*3d";
    double result = 6d + 8d / 9d * 1d * 9d * 10d % 4d * 4d - 4d * 6d * 3d;

    assertEquals(result, executeExpressionWithDefaultVariables(expression));
  }

  @Test
  public void testMath44b() {
    String expression = "a+b/c*d*e*f%g*h-i*j*k";
    double result = 6d + 8d / 9d * 1d * 9d * 10d % 4d * 4d - 4d * 6d * 3d;

    Map<String, Object> vars = new HashMap<>();
    vars.put("a", 6d);
    vars.put("b", 8d);
    vars.put("c", 9d);
    vars.put("d", 1d);
    vars.put("e", 9d);
    vars.put("f", 10d);
    vars.put("g", 4d);
    vars.put("h", 4d);
    vars.put("i", 4d);
    vars.put("j", 6d);
    vars.put("k", 3d);

    assertEquals(result, executeExpression(expression, vars));
  }

  @Ignore("DROOLS-6572 - Unable to parse")
  @Test
  public void testOperatorPrecedence() {
    String expression = "_x_001 = 500.2; _x_002 = 200.8; _r_001 = 701; _r_001 == _x_001 + _x_002 || _x_001 == 500 + 0.1";
    assertEquals(true, executeExpressionWithDefaultVariables(expression));
  }

  @Ignore("DROOLS-6572 - Unable to parse")
  @Test
  public void testOperatorPrecedence2() {
    String expression = "_x_001 = 500.2; _x_002 = 200.8; _r_001 = 701; _r_001 == _x_001 + _x_002 && _x_001 == 500 + 0.2";
    assertEquals(true, executeExpressionWithDefaultVariables(expression));
  }

  @Ignore("DROOLS-6572 - Unable to parse")
  @Test
  public void testOperatorPrecedence3() {
    String expression = "_x_001 = 500.2; _x_002 = 200.9; _r_001 = 701; _r_001 == _x_001 + _x_002 && _x_001 == 500 + 0.2";

    assertEquals(false, executeExpressionWithDefaultVariables(expression));
  }

  @Ignore("DROOLS-6572 - Unable to parse")
  @Test
  public void testOperatorPrecedence4() {
    String expression = "_x_001 = 500.2; _x_002 = 200.9; _r_001 = 701; _r_001 == _x_001 + _x_002 || _x_001 == 500 + 0.2";
    assertEquals(true, executeExpressionWithDefaultVariables(expression));
  }

  @Test
  public void testOperatorPrecedence5() {
    String expression = "_x_001 == _x_001 / 2 - _x_001 + _x_001 + _x_001 / 2 && _x_002 / 2 == _x_002 / 2";

    Map<String, Object> vars = new HashMap<>();
    vars.put("_x_001", 500.2);
    vars.put("_x_002", 200.9);
    vars.put("_r_001", 701);

    assertEquals(true, executeExpression(expression, vars));
  }

  @Test
  public void testModulus() {
    assertEquals(38392 % 2, executeExpressionWithDefaultVariables("38392 % 2"));
  }

  @Test
  public void testBitwiseOr1() {
    assertEquals(6, executeExpressionWithDefaultVariables("2|4"));
  }

  @Ignore("DROOLS-6572 - Considers second element a boolean")
  @Test
  public void testBitwiseOr2() {
    assertEquals(true, executeExpressionWithDefaultVariables("(2 | 1) > 0"));
  }

  @Ignore("DROOLS-6572 - Considers second element a boolean")
  @Test
  public void testBitwiseOr3() {
    assertEquals(true, executeExpressionWithDefaultVariables("(2|1) == 3"));
  }

  @Test
  public void testBitwiseOr4() {
    assertEquals(2 | 5, executeExpressionWithDefaultVariables("2|five"));
  }

  @Test
  public void testBitwiseAnd1() {
    assertEquals(2, executeExpressionWithDefaultVariables("2 & 3"));
  }

  @Test
  public void testBitwiseAnd2() {
    assertEquals(5 & 3, executeExpressionWithDefaultVariables("five & 3"));
  }

  @Test
  public void testShiftLeft() {
    assertEquals(4, executeExpressionWithDefaultVariables("2 << 1"));
  }

  @Test
  public void testShiftLeft2() {
    assertEquals(5 << 1, executeExpressionWithDefaultVariables("five << 1"));
  }

  @Ignore("DROOLS-6572 - Generates wrong code - unable to parse <<<")
  @Test
  public void testUnsignedShiftLeft() {
    assertEquals(2, executeExpressionWithDefaultVariables("-2 <<< 0"));
  }

  @Test
  public void testShiftRight() {
    assertEquals(128, executeExpressionWithDefaultVariables("256 >> 1"));
  }

  @Test
  public void testShiftRight2() {
    assertEquals(5 >> 1, executeExpressionWithDefaultVariables("five >> 1"));
  }

  @Test
  public void testUnsignedShiftRight() {
    assertEquals(-5 >>> 1, executeExpressionWithDefaultVariables("-5 >>> 1"));
  }

  @Test
  public void testUnsignedShiftRight2() {
    assertEquals(-5 >>> 1, executeExpressionWithDefaultVariables("(five - 10) >>> 1"));
  }

  @Test
  public void testShiftRightAssign() {
    assertEquals(5 >> 2, executeExpressionWithDefaultVariables("_zZz = 5; _zZz >>= 2"));
  }

  @Test
  public void testShiftLeftAssign() {
    assertEquals(10 << 2, executeExpressionWithDefaultVariables("_yYy = 10; _yYy <<= 2"));
  }

  @Ignore("DROOLS-6572 - Unable to parse")
  @Test
  public void testUnsignedShiftRightAssign() {
    String expression = "_xXx = -5; _xXx >>>= 2";
    
    assertEquals(-5 >>> 2, executeExpression(expression, Collections.emptyMap()));
  }

  @Test
  public void testXOR() {
    assertEquals(3, executeExpressionWithDefaultVariables("1 ^ 2"));
  }

  @Test
  public void testXOR2() {
    assertEquals(5 ^ 2, executeExpressionWithDefaultVariables("five ^ 2"));
  }

  @Test
  public void testInvert() {
    assertEquals(~10, executeExpressionWithDefaultVariables("~10"));
  }

  @Test
  public void testInvert2() {
    assertEquals(~(10 + 1), executeExpressionWithDefaultVariables("~(10 + 1)"));
  }

  @Test
  public void testInvert3() {
    assertEquals(~10 + (1 + ~50), executeExpressionWithDefaultVariables("~10 + (1 + ~50)"));
  }

  @Test
  public void testDeepPropertyAdd() {
    assertEquals(10, executeExpressionWithDefaultVariables("foo.countTest+ 10"));
  }

  @Ignore("DROOLS-6572 - Generates wrong code")
  @Test
  public void testDeepAssignmentIncrement() {
    String expression = "foo.countTest += 5; if (foo.countTest == 5) { foo.countTest = 0; return true; }" +
        " else { foo.countTest = 0; return false; }";

    assertEquals(true, executeExpressionWithDefaultVariables(expression));

    assertEquals(true,
        executeExpressionWithDefaultVariables("foo.countTest += 5; if (foo.countTest == 5) { foo.countTest = 0; return true; }" +
            " else { foo.countTest = 0; return false; }"));
  }

  @Ignore("DROOLS-6572 - Generates wrong code - check for statements")
  @Test
  public void testDeepAssignmentWithBlock() {
    String expression = "with (foo) { countTest += 5 }; if (foo.countTest == 5) { foo.countTest = 0; return true; }" +
        " else { foo.countTest = 0; return false; }";

    assertEquals(true, executeExpressionWithDefaultVariables(expression));

    assertEquals(true,
        executeExpressionWithDefaultVariables("with (foo) { countTest += 5 }; if (foo.countTest == 5) { foo.countTest = 0; return true; }" +
            " else { foo.countTest = 0; return false; }"));
  }

  @Test
  public void testOperativeAssignMod() {
    int val = 5;
    assertEquals(val %= 2, executeExpressionWithDefaultVariables("int val = 5; val %= 2; val"));
  }

  @Test
  public void testOperativeAssignDiv() {
    int val = 10;
    assertEquals(val /= 2, executeExpressionWithDefaultVariables("int val = 10; val /= 2; val"));
  }

  @Test
  public void testOperativeAssignShift1() {
    int val = 5;
    assertEquals(val <<= 2, executeExpressionWithDefaultVariables("int val = 5; val <<= 2; val"));
  }

  @Test
  public void testOperativeAssignShift2() {
    int val = 5;
    assertEquals(val >>= 2, executeExpressionWithDefaultVariables("int val = 5; val >>= 2; val"));
  }

  @Test
  public void testOperativeAssignShift3() {
    int val = -5;
    assertEquals(val >>>= 2, executeExpressionWithDefaultVariables("int val = -5; val >>>= 2; val"));
  }

  @Ignore("DROOLS-6572 - Unable to parse")
  @Test
  public void testAssignPlus() {
    assertEquals(10, executeExpressionWithDefaultVariables("xx0 = 5; xx0 += 4; xx0 + 1"));
  }

  @Ignore("DROOLS-6572 - Unable to parse")
  @Test
  public void testAssignPlus2() {
    assertEquals(10, executeExpressionWithDefaultVariables("xx0 = 5; xx0 =+ 4; xx0 + 1"));
  }

  @Ignore("DROOLS-6572 - Rounding error")
  @Test
  public void testAssignDiv() {
    assertEquals(2.0, executeExpressionWithDefaultVariables("xx0 = 20; xx0 /= 10; xx0"));
  }

  public void testAssignMult() {
    assertEquals(36, executeExpressionWithDefaultVariables("xx0 = 6; xx0 *= 6; xx0"));
  }

  @Test
  public void testAssignSub() {
    assertEquals(11, executeExpressionWithDefaultVariables("xx0 = 15; xx0 -= 4; xx0"));
  }

  @Ignore("DROOLS-6572 - Calculation error")
  @Test
  public void testAssignSub2() {
    assertEquals(-95, executeExpressionWithDefaultVariables("xx0 = 5; xx0 =- 100"));
  }

  @Test
  public void testBooleanStrAppend() {
    assertEquals("footrue", executeExpressionWithDefaultVariables("\"foo\" + true"));
  }

  
  @Ignore("DROOLS-6572 - Unable to parse")
  @Test
  public void testStringAppend() {
    String expression = "c + 'bar'";

    assertEquals("catbar", executeExpressionWithDefaultVariables(expression));
  }

  @Test
  public void testNegation() {
    assertEquals(1, executeExpressionWithDefaultVariables("-(-1)"));
  }

  @Test
  public void testStrongTypingModeComparison() {
    Map<String, Object> variables = new HashMap<>();
    variables.put("a", 0l);
    
    executeExpression("a==0", variables);
  }

  
  @Ignore("DROOLS-6572 - Rounding error")
  @Test
  public void testJIRA158() {
    String expression = "(float) (4/2 + Math.sin(1))";

    assertEquals((float) (4 / 2 + Math.sin(1)), executeExpressionWithDefaultVariables(expression));
  }

  @Test
  public void testJIRA162() {
    String expression = "1d - 2d + (3d * var1) * var1";
    double result = 1 - 2 + (3 * 1d) * 1;
    
    Map<String, Object> vars = new HashMap<>();
    vars.put("var1", 1d);

	assertEquals(result, executeExpression(expression, vars));
  }

  @Test
  public void testJIRA161() {
    String expression = "1==-(-1)";

    assertEquals(1 == -(-1), executeExpressionWithDefaultVariables(expression));
  }

  
  @Test
  public void testJIRA163() {
    String expression = "1d - 2d + (3d * 4d) * var1";
    double result = 1 - 2 + (3 * 4) * 1d;

    Map<String, Object> vars = new HashMap<>();
    vars.put("var1", 1d);

	assertEquals(result, executeExpression(expression, vars));
  }

  @Ignore("DROOLS-6572 - Wrong value calculated")
  @Test
  public void testJIRA164() {
    String expression = "1 / (var1 + var1) * var1";

    double var1 = 1d;
    double result = 1 / (var1 + var1) * var1;

    Map<String, Object> vars = new HashMap<>();
    vars.put("var1", var1);

	assertEquals(result, executeExpression(expression, vars));

  }

  @Ignore("DROOLS-6572 - Wrong value calculated")
  @Test
  public void testJIRA164b() {
    String expression = "1 + 1 / (var1 + var1) * var1";

    double var1 = 1d;
    double result = 1 + 1 / (var1 + var1) * var1;
    Map<String, Object> vars = new HashMap<>();
    vars.put("var1", var1);

    assertEquals(result, executeExpression(expression, vars));
  }

  @Ignore("DROOLS-6572 - Wrong value calculated")
  @Test
  public void testJIRA164c() {
	double var1 = 1d;
    String expression = "1 + 1 / (var1 + var1 + 2 + 3) * var1";

    double result = 1 + 1 / (var1 + var1 + 2 + 3) * var1;
   
    Map<String, Object> vars = new HashMap<>();
    vars.put("var1", var1);

    assertEquals(result, executeExpression(expression, vars));
  }

  @Ignore("DROOLS-6572 - Wrong value calculated")
  @Test
  public void testJIRA164d() {
	double var1 = 1d;
    String expression = "1 + 1 + 1 / (var1 + var1) * var1";
    double result = 1 + 1 + 1 / (var1 + var1) * var1;
    Map<String, Object> vars = new HashMap<>();

    vars.put("var1", var1);

    assertEquals(result, executeExpression(expression, vars));
  }

  @Ignore("DROOLS-6572 - Wrong value calculated")
  @Test
  public void testJIRA164e() {
    String expression = "10 + 11 + 12 / (var1 + var1 + 51 + 71) * var1 + 13 + 14";
    Map<String, Object> vars = new HashMap<>();

    double var1 = 1d;
    vars.put("var1", var1);

    assertEquals((float) (10 + 11 + 12 / (var1 + var1 + 51 + 71) * var1 + 13 + 14),
        ((Double) executeExpression(expression, vars)).floatValue(), 0.01);
  }

  @Ignore("DROOLS-6572 - Wrong value calculated")
  @Test
  public void testJIRA164f() {
    String expression = "10 + 11 + 12 / (var1 + 1 + var1 + 51 + 71) * var1 + 13 + 14";
    double var1 = 1d;
    float result = (float) (10 + 11 + 12 / (var1 + 1 + var1 + 51 + 71) * var1 + 13 + 14);

    Map<String, Object> vars = new HashMap<>();

    vars.put("var1", var1);

	assertEquals(result,
        ((Double) executeExpression(expression, vars)).floatValue(), 0.01);
  }


  @Test
  public void testJIRA164g() {
    String expression = "1 - 2 + (3 * var1) * var1";
    double var1 = 1d;
    float result = (float) (1 - 2 + (3 * var1) * var1);

    Map<String, Object> vars = new HashMap<>();
    vars.put("var1", var1);

    assertEquals(result, ((Double) executeExpression(expression, vars)).floatValue(), 0.01);
  }

  @Test
  public void testJIRA164h() {
    String expression = "1 - var1 * (var1 * var1 * (var1 * var1) * var1) * var1";

    Map<String, Object> vars = new HashMap<>();

    double var1 = 2d;
    vars.put("var1", var1);

    assertEquals((float) (1 - var1 * (var1 * var1 * (var1 * var1) * var1) * var1),
        ((Double) executeExpression(expression, vars)).floatValue(), 0.01);
  }

  @Test
  public void testJIRA180() {
    executeExpressionWithDefaultVariables("-Math.sin(0)");
  }

  @Test
  public void testJIRA208() {
    Map<String, Object> vars = new LinkedHashMap<>();
    vars.put("bal", 999);

    String[] testCases = {"bal - 80 - 90 - 30", "bal-80-90-30", "100 + 80 == 180", "100+80==180"};

    Object val1, val2;
    for (String expr : testCases) {
      val1 = executeExpression(expr, vars);
      assertThat(val1).isNotNull();
      val2 = executeExpression(expr, vars);
      assertThat(val2).isNotNull();
      assertEquals("expression did not evaluate correctly: " + expr, val1, val2);
    }
  }

  @Test
  public void testJIRA1208a() {
    Map<String, Object> bal = new HashMap<>();
    bal.put("bal", 999);
    assertEquals(799, executeExpression("bal - 80 - 90 - 30", bal));
  }

  @Test
  public void testJIRA208b() {
    Map<String, Object> vars = new LinkedHashMap<>();
    vars.put("bal", 999);

    String[] testCases = {
                "bal + 80 - 80",
               "bal - 80 + 80", "bal * 80 / 80",
        "bal / 80 * 80"
    };

    
    for (String expr : testCases) {
      Object val1 = executeExpression(expr, vars);
      assertThat(val1).isNotNull();
      Object val2 = executeExpression(expr, vars);
      assertThat(val2).isNotNull();
      assertEquals("expression did not evaluate correctly: " + expr, val1, val2);
    }
  }

  @Test
  public void testJIRA210() {
    Map<String, Object> vars = new LinkedHashMap<>();
    vars.put("bal", new BigDecimal("999.99"));

    String[] testCases = {"bal - 1 + \"abc\"",};


    for (String expr : testCases) {
      Object val1 = executeExpression(expr, vars);
      assertThat(val1).isNotNull();
      Object val2 = executeExpression(expr, vars);
      assertThat(val2).isNotNull();
      assertEquals("expression did not evaluate correctly: " + expr, val1, val2);
    }
  }

  @Ignore("DROOLS-6572 - Generates wrong code - missing symbol")
  @Test
  public void testMathDec30() {
    Map<String, Object> params = new HashMap<>();
    params.put("value", 10);
    Map<String, Object> vars = new HashMap<>();
    vars.put("param", params);
    vars.put("param2", 10);
    
    assertEquals(1 + 2 * 10, executeExpression("1 + 2 * param.value", vars));
  }

  @Test
  public void testJIRA99_Interpreted() {
    Map<String, Object> map = new HashMap<>();
    map.put("x", 20);
    map.put("y", 10);
    map.put("z", 5);

    assertEquals(20 - 10 - 5, executeExpression("x - y - z", map));
  }

  @Test
  public void testJIRA99_Compiled() {
    Map<String, Object> map = new HashMap<>();
    map.put("x", 20);
    map.put("y", 10);
    map.put("z", 5);

    assertEquals(20 - 10 - 5, executeExpression("x - y - z", map));
  }

  @Ignore("DROOLS-6572 - Too many iterations - why")
  @Test
  public void testModExpr() {
    String str = "$y % 4 == 0 && $y % 100 != 0 || $y % 400 == 0 ";
    Map<String, Object> vars = new HashMap<>();

    for (int i = 0; i < 500; i++) {
      int y = i;
      boolean expected = y % 4 == 0 && y % 100 != 0 || y % 400 == 0;
      vars.put("$y", y);

      assertEquals(expected, executeExpression(str, vars));

    }
  }

  @Test
  public void testIntsWithDivision() {
    String expression = "0 == x - (y/2)";

    Map<String, Object> vars = new HashMap<>();
    vars.put("x", 50);
    vars.put("y", 100);
    Boolean result = (Boolean) executeExpression(expression, vars);
    assertTrue(result);
  }

  @Ignore("DROOLS-6572 - Wrong result")
  @Test
  public void testMathCeil() {
    String expression = "Math.ceil( x/3 ) == 2";

    Map<String, Object> vars = new HashMap<>();
    vars.put("x", 4);
    Boolean result = (Boolean) executeExpression(expression, vars);
    assertTrue(result);
  }
  
  @Ignore("DROOLS-6572 - Generates wrong code")
  @Test
  public void testStaticMathCeil() {      
    int x = 4;
    int m = (int) Math.ceil( x/3 ); // demonstrating it's perfectly valid java

    String expression = "int m = (int) java.lang.Math.ceil( x/3 ); return m;";

    Map<String, Object> vars = new HashMap<>();
    vars.put("x", 4);
    assertEquals(Integer.valueOf(2), executeExpression(expression, vars));
  }  

  @Ignore("DROOLS-6572 - Calculates wrong result")
  @Test
  public void testStaticMathCeilWithJavaClassStyleLiterals() {            
	String expression = "java.lang.Math.ceil( x/3 )";
	double result = Math.ceil((double) 4 / 3);
	
	Map<String, Object> vars = new HashMap<>();
	vars.put("x", 4);
	assertEquals(result, executeExpression(expression, vars));
  }
  
  @Test
  public void testMathCeilWithDoubleCast() {
    String expression = "Math.ceil( (double) x / 3 )";
    double result = Math.ceil((double) 4 / 3);

    Map<String, Object> vars = new HashMap<>();
    vars.put("x", 4);
	assertEquals(result, executeExpression(expression, vars));
  }
  
  @Test
  public void testBigDecimalAssignmentIncrement() {
    String expression = "s1=0B;s1+=1;s1+=1;s1";
    BigDecimal result = new BigDecimal(2);

    assertEquals(result, executeExpression(expression, new HashMap<>()));
  }
  
  /* https://github.com/mvel/mvel/issues/249
   * The following caused a ClassCastException because the compiler optimized for integers
   */
  @Test
  public void testIssue249() {
    String expression = "70 + 30 *  x1";
    Map<String, Object> expressionVars = new HashMap<>();
    expressionVars.put("x1", 128.33);
    
    assertEquals(3919.9, ((Number)executeExpression(expression, expressionVars)).doubleValue(), 0.01);
  }

}
