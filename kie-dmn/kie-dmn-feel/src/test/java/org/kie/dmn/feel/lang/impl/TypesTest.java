package org.kie.dmn.feel.lang.impl;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.FEELProperty;
import org.kie.dmn.feel.lang.FEELType;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TypesTest {
    private static final Logger LOG = LoggerFactory.getLogger(TypesTest.class);
    private static final FEEL feel = FEEL.newInstance();
    static {
        feel.addListener(new FEELEventListener() {
            @Override
            public void onEvent(FEELEvent event) {
                LOG.info(event.toString());
            }
        });
    }
    
    @FEELType
    public static class PersonPojo {
        private String fullName;
        private String fullAddress;
        public PersonPojo(String fullName, String fullAddress) {
            this.fullName = fullName;
            this.fullAddress = fullAddress;
        }
        @FEELProperty("Full Name")
        public String getFullName() {
            return fullName;
        }
        public void setFullName(String fullName) {
            this.fullName = fullName;
        }
        @FEELProperty("Full Address")
        public String getFullAddress() {
            return fullAddress;
        }
        public void setFullAddress(String fullAddress) {
            this.fullAddress = fullAddress;
        }
    }

    @Test
    public void test1() {
        CompilerContext ctx = feel.newCompilerContext();
        ctx.addInputVariableType( "Input Person", JavaBackedType.of(PersonPojo.class) );
        CompiledExpression compiledExpression = feel.compile( "Input Person.Full Name = \"Matteo Mortari\"", ctx );
        
        System.out.println("COMPILATION DONE.");
        
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("Input Person", new PersonPojo("Matteo Mortari", "100 E. Davie Street"));
        
        Object result = feel.evaluate(compiledExpression, inputs);
        
        System.out.println(result);
    }
    
    @Test
    public void test2() {
        CompilerContext ctx = feel.newCompilerContext();
        ctx.addInputVariableType( "Person List", BuiltInType.LIST );
        CompiledExpression compiledExpression = feel.compile( "count(Person List)", ctx );
        
        Map<String, Object> inputs = new HashMap<>();
        List<PersonPojo> pList = new ArrayList<>();
        inputs.put("Person List", pList);
        pList.add(new PersonPojo("Edson Tirelli", "100 E. Davie Street"));
        pList.add(new PersonPojo("Matteo Mortari", "100 E. Davie Street"));
        
        Object result = feel.evaluate(compiledExpression, inputs);
        
        System.out.println(result);
    }
    
//    @Test
//    public void test3() {
//        CompilerContext ctx = feel.newCompilerContext();
//        ctx.addInputVariableType( "Person List", new GenericType(BuiltInType.LIST, JavaBackedType.of(PersonPojo.class)));
//        CompiledExpression compiledExpression = feel.compile( "Person List[Full Name = \"Edson Tirelli\"]", ctx );
//        
//        System.out.println("COMPILATION DONE.");
//        
//        Map<String, Object> inputs = new HashMap<>();
//        List<PersonPojo> pList = new ArrayList<>();
//        inputs.put("Person List", pList);
//        pList.add(new PersonPojo("Edson Tirelli", "100 E. Davie Street"));
//        pList.add(new PersonPojo("Matteo Mortari", "100 E. Davie Street"));
//        
//        Object result = feel.evaluate(compiledExpression, inputs);
//        
//        System.out.println(result);
//    }
    
    @Test
    public void test3b() {
        CompilerContext ctx = feel.newCompilerContext();
        ctx.addInputVariableType( "PersonList", BuiltInType.LIST);
        CompiledExpression compiledExpression = feel.compile( "PersonList[fullName = \"Edson Tirelli\"]", ctx );
        
        System.out.println("COMPILATION DONE.");
        
        Map<String, Object> inputs = new HashMap<>();
        List<PersonPojo> pList = new ArrayList<>();
        inputs.put("PersonList", pList);
        pList.add(new PersonPojo("Edson Tirelli", "100 E. Davie Street"));
        pList.add(new PersonPojo("Matteo Mortari", "100 E. Davie Street"));
        
        Object result = feel.evaluate(compiledExpression, inputs);
        
        System.out.println(result);
    }
    
    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    public static <K, U> Collector<Map.Entry<K, U>, ?, Map<K, U>> toMap() {
        return Collectors.toMap(x -> x.getKey(), x -> x.getValue());
    }
    
    @SafeVarargs
    public static <K, V> Map<K, V> prototype(Map.Entry<K, V>... attributes) {
        return Stream.of(attributes).collect(toMap());
    }
    
    @Test
    public void test4() {
        CompilerContext ctx = feel.newCompilerContext();
        ctx.addInputVariableType( "Person List", BuiltInType.LIST);
        CompiledExpression compiledExpression = feel.compile( "Person List[Full Name = \"Edson Tirelli\"]", ctx );
        
        System.out.println("COMPILATION DONE.");
        
        Map<String, Object> inputs = new HashMap<>();
        List<Map<String, ?>> pList = new ArrayList<>();
        inputs.put("Person List", pList);
        pList.add(prototype(entry("Full Name",   "Edson Tirelli"),
                            entry("Full Address","100 E. Davie Street"))
                );
        pList.add(prototype(entry("Full Name",   "Matteo Mortari"),
                            entry("Full Address","100 E. Davie Street"))
                );
        
        Object result = feel.evaluate(compiledExpression, inputs);
        
        System.out.println(result);
    }
    
    @Test
    public void test4easy() {
        CompilerContext ctx = feel.newCompilerContext();
        ctx.addInputVariableType( "PersonList", BuiltInType.LIST);
        CompiledExpression compiledExpression = feel.compile( "PersonList[FullName = \"Edson Tirelli\"]", ctx );
        
        System.out.println("COMPILATION DONE.");
        
        Map<String, Object> inputs = new HashMap<>();
        List<Map<String, ?>> pList = new ArrayList<>();
        inputs.put("PersonList", pList);
        pList.add(prototype(entry("FullName",   "Edson Tirelli"),
                            entry("FullAddress","100 E. Davie Street"))
                );
        pList.add(prototype(entry("FullName",   "Matteo Mortari"),
                            entry("FullAddress","100 E. Davie Street"))
                );
        
        Object result = feel.evaluate(compiledExpression, inputs);
        
        System.out.println(result);
    }
    
    @Test
    public void test5() {
        CompilerContext ctx = feel.newCompilerContext();
        ctx.addInputVariableType( "Person List", BuiltInType.LIST);
        CompiledExpression compiledExpression = feel.compile( "{ print my list: function(p1) \"A list of \" + string(count(p1)) , result: if count( Person List ) > 0 then print my list(Person List) else \"empty.\" }", ctx );
        
        System.out.println("COMPILATION DONE.");
        
        Map<String, Object> inputs = new HashMap<>();
        List<Map<String, ?>> pList = new ArrayList<>();
        inputs.put("Person List", pList);
        pList.add(prototype(entry("Full Name",   "Edson Tirelli"),
                            entry("Full Address","100 E. Davie Street"))
                );
        pList.add(prototype(entry("Full Name",   "Matteo Mortari"),
                            entry("Full Address","100 E. Davie Street"))
                );
        
        Object result = feel.evaluate(compiledExpression, inputs);
        
        System.out.println(result);
    }
    
    @Test
    public void mario() {
        System.out.println(null instanceof Object);
//        Object.class.isAssignableFrom(null.getClass());
    }
}