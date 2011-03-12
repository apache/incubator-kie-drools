/*
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

package org.drools.examples;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.examples.templates.ActivityType;
import org.drools.examples.templates.FeeScheduleRule;
import org.drools.examples.templates.FeeScheduleType;
import org.drools.examples.templates.FeeType;
import org.drools.examples.templates.ProductType;
import org.drools.template.DataProvider;
import org.drools.template.DataProviderCompiler;

/**
 * This example shows how to use Data-driven rule templates. It assumes that the FeeScheduleRule
 * objects have been retrieved from a database using some form of Object-Relational Mapper (such
 * as Hibernate or Toplink).
 *
 * An alternative is to use the ResultSetGenerator from the org.drools.templates.jdbc package, where you can use
 * vanilla JDBC to generate rules from (you just select the table names as the field named from the template) - this means no ORM
 * is required. 
 * 
 * Some things to note:
 *   - at the moment the templates require all parameters to come in a Strings
 *   - any row that references a parameter that is empty will not be generated.
 */
public class DataDrivenTemplateExample {

    private class TestDataProvider
        implements
        DataProvider {

        private Iterator<FeeScheduleRule> iterator;

        TestDataProvider(List<FeeScheduleRule> rows) {
            this.iterator = rows.iterator();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public String[] next() {
            FeeScheduleRule nextRule = iterator.next();
            String[] row = new String[]{ String.valueOf( nextRule.getFeeEventId() ),
                                         nextRule.getType().getCode(), 
                                         nextRule.getEntityBranch(), 
                                         nextRule.getProductType().getCode(), 
                                         nextRule.getActivityType().getName(), 
                                         nextRule.getFeeType().getCode(), 
                                         nextRule.getOwningParty(),
                                         nextRule.getCurrency(), 
                                         nextRule.getComparator(), 
                                         String.valueOf( nextRule.getCompareAmount() ), 
                                         String.valueOf( nextRule.getAmount() ),
                                         String.valueOf( nextRule.isLogEvent() )};
            return row;
        }

    }

    public static void main(String[] args) throws Exception {
        DataDrivenTemplateExample example = new DataDrivenTemplateExample();
        example.testCompiler();
    }

    public void testCompiler() throws Exception {
        ArrayList<FeeScheduleRule> rules = new ArrayList<FeeScheduleRule>();
        FeeScheduleType standard = new FeeScheduleType( "STANDARD" );
        FeeScheduleType flat = new FeeScheduleType( "FLAT" );
        ProductType sblc = new ProductType( "SBLC" );
        ProductType rrc = new ProductType( "RRC" );
        ActivityType iss = new ActivityType( "ISS" );
        ActivityType osx = new ActivityType( "OSX" );
        FeeType commission = new FeeType( "Commission" );
        FeeType postage = new FeeType( "Postage" );
        FeeType telex = new FeeType( "Telex" );

        rules.add( createRule( 1,
                               flat,
                               "",
                               sblc,
                               iss,
                               commission,
                               "Party 1",
                               "USD",
                               "",
                               0,
                               750,
                               true ) );
        rules.add( createRule( 2,
                               standard,
                               "Entity Branch 1",
                               rrc,
                               iss,
                               commission,
                               "",
                               "YEN",
                               "",
                               0,
                               1600,
                               false ) );
        rules.add( createRule( 3,
                               standard,
                               "",
                               sblc,
                               iss,
                               postage,
                               "",
                               "YEN",
                               "",
                               0,
                               40,
                               true ) );
        rules.add( createRule( 4,
                               flat,
                               "",
                               sblc,
                               osx,
                               telex,
                               "",
                               "YEN",
                               "<",
                               30000,
                               45,
                               false ) );
        TestDataProvider tdp = new TestDataProvider( rules );
        final DataProviderCompiler converter = new DataProviderCompiler();
        final String drl = converter.compile( tdp,
                                              getTemplate() );
        System.out.println( drl );

    }

    private InputStream getTemplate() {
        return DataDrivenTemplateExample.class.getResourceAsStream( "FeeScheduleRules.drt" );
    }

    private FeeScheduleRule createRule(long feeEventId,
                                       FeeScheduleType type,
                                       String entityBranch,
                                       ProductType productType,
                                       ActivityType activityType,
                                       FeeType feeType,
                                       String owningParty,
                                       String currency,
                                       String comparator,
                                       long compareAmount,
                                       long amount,
                                       boolean logEvent) {
        FeeScheduleRule rule = new FeeScheduleRule( feeEventId,
                                                    activityType,
                                                    productType,
                                                    type,
                                                    feeType,
                                                    owningParty,
                                                    entityBranch,
                                                    comparator,
                                                    compareAmount,
                                                    amount,
                                                    currency,
                                                    logEvent );
        return rule;
    }

}
