package org.kie.dmn.feel.lang.impl;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;

import java.util.Set;

import org.junit.Test;
import org.kie.dmn.feel.lang.CustomType;
import org.kie.dmn.feel.lang.FEELType;

import test.Person;

public class JavaBackedTypeTest {

    @Test
    public void testPerson() {
        CustomType personType = (CustomType) JavaBackedType.of( Person.class );
        Set<String> personProperties = personType.getProperties().keySet();
        
        assertThat( personProperties,  hasItem("home address") );
        assertThat( personProperties,  hasItem("address") );
        
        // for consistency and to fix possible hierarchy resolution problem, we add the property also as per standard JavaBean specs.
        assertThat( personProperties,  hasItem("homeAddress") );
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
        CustomType personType = (CustomType) JavaBackedType.of( MyPojoNoMethodAnn.class );
        Set<String> personProperties = personType.getProperties().keySet();
        
        assertThat( personProperties,  hasItem("a") );
        assertThat( personProperties,  hasItem("b") );
        
        // should not include private methods
        assertThat( personProperties,  not( hasItem("bPrivate") ) );
        
        // should not include methods which are actually Object methods.
        assertThat( personProperties,  not( hasItem("equals") ) );
        assertThat( personProperties,  not( hasItem("wait") ) );
    }
}
