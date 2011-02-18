/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.examples.broker.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A helper class to load and return the list of companies
 * 
 * @author etirelli
 *
 */
public class CompanyRegistry {

    private Map<String, Company> companies;

    public CompanyRegistry() {
        this.companies = new HashMap<String, Company>();
        this.companies.put( "RHT",
                            new Company( "Red Hat Inc",
                                         "RHT" ) );
        this.companies.put( "JAVA",
                            new Company( "Sun Microsystems",
                                         "JAVA" ) );
        this.companies.put( "MSFT",
                            new Company( "Microsoft Corp",
                                         "MSFT" ) );
        this.companies.put( "ORCL",
                            new Company( "Oracle Corp",
                                         "ORCL" ) );
        this.companies.put( "SAP",
                            new Company( "SAP",
                                         "SAP" ) );
        this.companies.put( "GOOG",
                            new Company( "Google Inc",
                                         "GOOG" ) );
        this.companies.put( "YHOO",
                            new Company( "Yahoo! Inc",
                                         "YHOO" ) );
        this.companies.put( "IBM",
                            new Company( "IBM Corp",
                                         "IBM" ) );
    }
    
    public Collection<Company> getCompanies() {
        return Collections.unmodifiableCollection( companies.values() );
    }
    
    public Company getCompany( String symbol ) {
        return this.companies.get( symbol );
    }
    
    
}
