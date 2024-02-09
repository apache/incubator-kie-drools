/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static java.lang.System.currentTimeMillis;

public class MVEL {

    public static class MiscTestClass {
	    int exec = 0;
	
	    @SuppressWarnings({"unchecked", "UnnecessaryBoxing"})
	    public List toList(Object object1, String string, int integer, Map map, List list) {
	      exec++;
	      List l = new ArrayList();
	      l.add(object1);
	      l.add(string);
	      l.add(Integer.valueOf(integer));
	      l.add(map);
	      l.add(list);
	      return l;
	    }
	
	
	    public int getExec() {
	      return exec;
	    }
	  }

    public static class Order {
        private int number = 20;


        public int getNumber() {
          return number;
        }

        public void setNumber(int number) {
          this.number = number;
        }
      }

	public static Serializable executeExpression(final Object compiledExpression, final Map<String, Object> vars) {
        Evaluator evaluator = new Evaluator();
        return evaluator.compileEvaluateWithDroolsMvelCompiler(compiledExpression, vars, evaluator.getClass().getClassLoader());
    }

    public static Serializable executeExpressionWithDefaultVariables(final Object compiledExpression) {
        return executeExpression(compiledExpression, createTestMap());
    }


    protected static Map<String, Object> createTestMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("foo", new Foo());
        map.put("a", null);
        map.put("b", null);
        map.put("c", "cat");
        map.put("BWAH", "");

        map.put("misc", new MiscTestClass());

        map.put("pi", "3.14");
        map.put("hour", 60);
        map.put("zero", 0);

        map.put("array", new String[]{"", "blip"});

        map.put("order", new Order());
        map.put("$id", 20);

        map.put("five", 5);

        map.put("testImpl",
                new TestInterface() {

                    public String getName() {
                        return "FOOBAR!";
                    }

                    public boolean isFoo() {
                        return true;
                    }
                });


        map.put("ipaddr", "10.1.1.2");

        map.put("dt1", new Date(currentTimeMillis() - 100000));
        map.put("dt2", new Date(currentTimeMillis()));
        return map;
    }
}
