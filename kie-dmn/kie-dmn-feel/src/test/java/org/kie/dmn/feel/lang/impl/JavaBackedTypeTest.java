package org.kie.dmn.feel.lang.impl;

import java.util.Set;

import org.junit.Test;
import org.kie.dmn.feel.lang.CompositeType;
import org.kie.dmn.feel.lang.FEELType;
import org.kie.dmn.feel.model.Person;

import static org.assertj.core.api.Assertions.assertThat;

public class JavaBackedTypeTest {

    @Test
    public void testPerson() {
        CompositeType personType = (CompositeType) JavaBackedType.of( Person.class );
        Set<String> personProperties = personType.getFields().keySet();
        
        assertThat(personProperties).contains("home address");
        assertThat(personProperties).contains("address");
        
        // for consistency and to fix possible hierarchy resolution problem, we add the property also as per standard JavaBean specs.
        assertThat(personProperties).contains("homeAddress");
    }
    
    @FEELType
    public static class MyPojoNoMethodAnn {
        private String a;
        private String b;
        public MyPojoNoMethodAnn(String a, String b) {
            super();
            this.a = a;
            this.b = b;
        }
        
        public String getA() {
            return a;
        }
        
        public void setA(String a) {
            this.a = a;
        }
        
        public String getB() {
            return b;
        }
        
        public void setB(String b) {
            this.b = b;
        }
        
        @SuppressWarnings("unused") // used for tests.
        private String getBPrivate() {
            return b;
        }
    }
    
    @Test
    public void testMyPojoNoMethodAnn() {
        CompositeType personType = (CompositeType) JavaBackedType.of( MyPojoNoMethodAnn.class );
        Set<String> personProperties = personType.getFields().keySet();
        
        assertThat(personProperties).contains("a");
        assertThat(personProperties).contains("b");
        
        // should not include private methods
        assertThat(personProperties).doesNotContain("bPrivate");
        
        // should not include methods which are actually Object methods.
        assertThat(personProperties).doesNotContain("equals");
        assertThat(personProperties).doesNotContain("wait");
    }
}
