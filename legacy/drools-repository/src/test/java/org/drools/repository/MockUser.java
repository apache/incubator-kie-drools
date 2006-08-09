package org.drools.repository;

import java.security.Principal;

public class MockUser
    implements
    Principal {

        private String name;
        
        public MockUser(String name) {
            this.name = name;
        }
        
        public String getName() {

            return name;
        }
        
    

}
