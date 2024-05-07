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
package org.kie.dmn.feel.runtime;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.impl.JavaBackedType;
import org.kie.dmn.feel.lang.impl.MapBackedType;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.model.Address;
import org.kie.dmn.feel.model.Person;

public class FEELStaticTypeTest
        extends BaseFEELCompilerTest {

    @ParameterizedTest
    @MethodSource("data")
    protected void instanceTest(String expression, Map<String, Type> inputTypes, Map<String, Object> inputValues, Object result, BaseFEELTest.FEEL_TARGET testFEELTarget) {
        expression(expression, inputTypes, inputValues,  result, testFEELTarget);
    }

    private static Collection<Object[]> data() {
        final Object[][] cases = new Object[][] {
            
                { "{ name : first name + last name }",
                    new HashMap<String, Type>() {{
                        put( "first name", BuiltInType.STRING );
                        put( "last name", BuiltInType.STRING );
                    }},
                    new HashMap<String, Object>() {{
                        put( "first name", "John " );
                        put( "last name", "Doe" );
                    }},
                    new HashMap<String,Object>() {{
                        put( "name", "John Doe" );
                    }} },
                
                { "{ name : person.first name + person.last name }",
                  new HashMap<String, Type>() {{
                      put( "person", new MapBackedType()
                                      .addField( "first name", BuiltInType.STRING )
                                      .addField( "last name", BuiltInType.STRING )
                      );
                  }},
                  new HashMap<String, Object>() {{
                      Map<String, String> person = new HashMap<>();
                      person.put("first name", "John ");
                      person.put("last name", "Doe");
                      put( "person", person ); 
                  }},
                  new HashMap<String,Object>() {{
                      put( "name", "John Doe" );
                  }} },
            
                { "{ myFeelVar : person.first name + person.last name }",
                  new HashMap<String, Type>() {{
                      put( "person", JavaBackedType.of(Person.class) );
                  }},
                  new HashMap<String, Object>() {{
                      put( "person", new Person("John ", "Doe") ); 
                  }},
                  new HashMap<String,Object>() {{
                      put( "myFeelVar", "John Doe" );
                  }} },
                
                { "{ myFeelVar : person.first name + person.last name + \" resides in \" + person.home address.street name }",
                  new HashMap<String, Type>() {{
                      put( "person", JavaBackedType.of(Person.class) );
                  }},
                  new HashMap<String, Object>() {{
                      put( "person", new Person("John ", "Doe", new Address("Lumbard St.")) ); 
                  }},
                  new HashMap<String,Object>() {{
                      put( "myFeelVar", "John Doe resides in Lumbard St." );
                  }} },
                
                { "{ myFeelVar : person.age }",
                  new HashMap<String, Type>() {{
                      put( "person", JavaBackedType.of(Person.class) );
                  }},
                  new HashMap<String, Object>() {{
                      put( "person", new Person("John ", "Doe", 47) ); 
                  }},
                  new HashMap<String,Object>() {{
                      put( "myFeelVar", new BigDecimal(47) );
                  }} },
                
                { "{ myFeelVar : person.first name + \"zip code is: \" + person.home address.zip }",
                  new HashMap<String, Type>() {{
                      put( "person", JavaBackedType.of(Person.class) );
                  }},
                  new HashMap<String, Object>() {{
                      put( "person", new Person("John ", "Doe", new Address("Lumbard St.", "12345")) ); 
                  }},
                  new HashMap<String,Object>() {{
                      put( "myFeelVar", "John zip code is: 12345" );
                  }} },
                
                { "{ myFeelVar : person.first name + \"home street name is: \" + person.address.street name }",
                  new HashMap<String, Type>() {{
                      put( "person", JavaBackedType.of(Person.class) );
                  }},
                  new HashMap<String, Object>() {{
                      put( "person", new Person("John ", "Doe", new Address("Lumbard St.", "12345")) ); 
                  }},
                  new HashMap<String,Object>() {{
                      put( "myFeelVar", "John home street name is: Lumbard St." );
                  }} }
                
                
        };
        return enrichWith5thParameter(cases);
    }
}
