package org.drools.agent;

import java.util.Properties;
public  class MockProvider extends PackageProvider {

        public MockProvider() {
        }
        
        public void configure(Properties config) {
        }

        PackageChangeInfo loadPackageChanges() {
            return null;
        }


        
    }