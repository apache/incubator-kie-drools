package org.drools.parser;

import java.util.Properties;

public class Demo {

    /**
     * @param args
     */
    public static void main(String[] args) {
        Properties grammar = new Properties();
        grammar.setProperty("is located in", "${left}.getLocation().equals(\"${right}\")");
        grammar.setProperty("before", "${left}.compareTo(${right}) < 0");
        grammar.setProperty("name is", "${left}( name=='${right}' )");
        grammar.setProperty("with attributes", "${left}( ${right} )");
        grammar.setProperty("is", "==");
        grammar.setProperty("and", "&&");
        grammar.setProperty("or", "||");
        
        ExpressionExpander expander = new PseudoNaturalExpander(grammar);
        
        
        System.out.println(expander.expandExpression("bob is located in atlanta"));
        System.out.println(expander.expandExpression("event before Today"));
        System.out.println(expander.expandExpression("Persons name is Michael"));
        System.out.println(expander.expandExpression("Person with attributes [age < 21]"));
        System.out.println(expander.expandExpression("Person ( age is 21 and city is Atlanta or height is 160 )"));
        

    }

}
