package org.drools.agent;

import java.util.Properties;

import org.drools.rule.Package;
public  class MockProvider extends PackageProvider {

        public MockProvider() {
        }
        
        public void configure(Properties config) {
        }

        PackageChangeInfo loadPackageChanges() {
            return null;
        }


        
    }