/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.query;

import static org.jbpm.persistence.util.PersistenceUtil.cleanUp;
import static org.jbpm.persistence.util.PersistenceUtil.setupWithPoolingDataSource;
import static org.junit.Assert.fail;
import static org.kie.api.runtime.EnvironmentName.ENTITY_MANAGER_FACTORY;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManagerFactory;

//import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.kie.internal.query.ExtendedParametrizedQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

public abstract class QueryBuilderCoverageTestUtil {

    private static HashMap<String, Object> context;
    private static EntityManagerFactory emf;

    private static final Logger logger = LoggerFactory.getLogger(QueryBuilderCoverageTestUtil.class);

    public static EntityManagerFactory beforeClass(String persistenceUnit) {
        hackTheDatabaseMetadataLoggerBecauseTheresALogbackXmlInTheClasspath();
        context = setupWithPoolingDataSource(persistenceUnit);
        emf = (EntityManagerFactory) context.get(ENTITY_MANAGER_FACTORY);
        return emf;
    }

    public static void afterClass() {
        cleanUp(context);
    }

    public static void hackTheDatabaseMetadataLoggerBecauseTheresALogbackXmlInTheClasspath() {
        String [] fieldName = { "LOG", "log", "logger" };
        try {
            Object loggerObj = null;
            for( int i = 0; i < fieldName.length; ++i ) {
                Field loggerField;
                Class objClass = null;
                if( loggerObj == null ) {
                    objClass = DatabaseMetaData.class;
                } else {
                   objClass = loggerObj.getClass();
                }
                loggerField = objClass.getDeclaredField(fieldName[i]);
                loggerField.setAccessible(true);
                loggerObj = loggerField.get(loggerObj);
            }
            ((ch.qos.logback.classic.Logger) loggerObj).setLevel(Level.OFF);
        } catch( Exception e ) {
            e.printStackTrace();
            // do nothing
        }
    }

    public static void queryBuilderCoverageTest(
            ExtendedParametrizedQueryBuilder queryBuilder,
            Class builderClass,
            ModuleSpecificInputFiller inputFiller,
            String... skipMethodName) {
       Set<Method> queryMethodSet = new HashSet<Method>();
       while( builderClass != null && ! builderClass.equals(ExtendedParametrizedQueryBuilder.class) ) {
           queryMethodSet.addAll(Arrays.asList(builderClass.getMethods()));
           builderClass = builderClass.getInterfaces()[0];
       }
       List<Method> queryMethods = new ArrayList<Method>(queryMethodSet);

       String [] specialMethodsArr = {
               "newGroup", "endGroup",
               "equals", "identity",
               "intersect", "union",
               "and", "or",
               "like", "regex", "equal",
               "build", "clear",
               "notify", "notifyAll", "wait"
       };
       Set<String> specialMethods = new HashSet<String>(Arrays.asList(specialMethodsArr));
       specialMethods.addAll(Arrays.asList(skipMethodName));

       Iterator<Method> iter = queryMethods.iterator();
       while( iter.hasNext() ) {
          Method method = iter.next();
          if( specialMethods.contains(method.getName()) ) {
              iter.remove();
          }
       }

       Collections.sort(queryMethods, new Comparator<Method>() {

        @Override
        public int compare( Method m1, Method m2 ) {
            if( m1 == m2 ) {
               return 0;
            } else if( m1 == null ) {
                return -1;
            } else if( m2 == null ) {
                return 1;
            } else {
                return m1.getName().compareTo(m2.getName());
            }
        }
       });

       for( Method methodA : queryMethods ) {
           for( Method methodB : queryMethods ) {
               Object [] inputA = fillInput(methodA.getParameterTypes(), inputFiller);
               Object [] inputB = fillInput(methodB.getParameterTypes(), inputFiller);

               try {
                   // build query
                   StringBuffer testName = new StringBuffer(methodA.getName());
                   callMethod(methodA, queryBuilder, inputA);

                   testName.append(" | ");
                   queryBuilder.union();

                   testName.append(methodB.getName());
                   callMethod(methodB, queryBuilder, inputB);

                   logger.debug(testName.toString());

                   // try queryT
                   queryBuilder.build().getResultList();
                   queryBuilder.clear();
               } catch( Throwable t) {
                   t.printStackTrace();
                   String msg = createTestName(methodA, inputA, methodB, inputB, queryBuilder.getClass());
                   fail(msg);
               }
           }
       }
    }

    private static <T,S> String createTestName(Method methodA, T [] inputA, Method methodB, S [] inputB, Class builderClass) {
        StringBuffer msg = new StringBuffer(getMethodName(methodA, inputA));
        msg.append(" OR ");
        msg.append(getMethodName(methodB, inputB));
        msg.append(": " + builderClass.getSimpleName());

        return msg.toString();
    }

    private static void callMethod(Method method, Object obj, Object input) {
        boolean noArgs = false;
        Object [] arrInput = null;
        if( input.getClass().isArray() ) {
            int length = Array.getLength(input);
            if( length == 1 ) {
                input = Array.get(input, 0);
            } else if( length == 0 ) {
                noArgs = true;
            } else {
                arrInput = (Object[]) input;
            }
        }
        try {
            if( noArgs ) {
                method.invoke(obj);
            } else {
                if( arrInput != null ) {
                    method.invoke(obj, arrInput);
                } else {
                    method.invoke(obj, input);
                }
            }
        } catch( Exception e ) {
            e.printStackTrace();
            fail( "Method [" + method.getName() + ".(" + getInputAsString(input) + ")]");
        }
    }

    private static <T> String getMethodName(Method method, T [] input) {
        StringBuilder msg = new StringBuilder(method.getName());
        msg.append("(");
        if( input.length > 0 ) {
            msg.append(getInputAsString(input[0]));
            for( int j = 1; j < input.length; ++j ) {
                msg.append(",").append(getInputAsString(input[j]));
            }
        }
        msg.append(")");
        return msg.toString();
    }

    private static <T> String getInputAsString(Object input) {
       if( input.getClass().isArray()) {
           if( Array.getLength(input) == 0 ) {
               return "";
           }
           Class compType = input.getClass().getComponentType();
           if( int.class.equals(compType) ) {
               return Arrays.toString((int []) input);
           } else if( long.class.equals(compType) ) {
               return Arrays.toString((long []) input);
           } else {
               return Arrays.toString((Object []) input);
           }
       } else {
           return input.toString();
       }
    }

    public static interface ModuleSpecificInputFiller {
        public Object fillInput(Class type);
    }

    private static Object [] fillInput(Type [] types, ModuleSpecificInputFiller inputFiller) {
        Object [] result = new Object[types.length];
       for( int i = 0; i< types.length; ++i ) {
           Class type = (Class) types[i];
          if( type.equals(int.class) ) {
             result[i] = 23;
          } else if( type.equals(boolean.class) ) {
             result[i] = false;
          } else if( type.equals(Date.class) ) {
             result[i] = new Date();
          } else if( type.equals(long.class) ) {
             result[i] = 46l;
          } else if( type.equals(Long.class) ) {
             result[i] = 96l;
          } else if( type.equals(String.class) ) {
              result[i] = "that";
          } else if( type.isArray() ) {
             Class arrayType = type.getComponentType();
             if( arrayType.equals(int.class) ) {
                 int [] intArr = { 1,3,5 };
                 result[i] = intArr;
             } else if( arrayType.equals(long.class) ) {
                 long [] longArr = { 1,9,25 };
                 result[i] = longArr;
             } else if( arrayType.equals(String.class) ) {
                 String [] strArr = { "blu", "red", "gro"};
                 result[i] = strArr;
             } else if( arrayType.equals(Date.class) ) {
                 Date [] strArr = { new Date(), new Date() };
                 result[i] = strArr;
             }
          }

          if( result[i] == null ) {
             result[i] = inputFiller.fillInput(type);
          }

          if( result[i] == null ) {
              throw new IllegalStateException("Add logic for type: " + types[i].toString());
          }
       }
       return result;
    }

}