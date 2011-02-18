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

package org.drools.examples.broker.events;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import org.drools.examples.broker.model.Company;
import org.drools.examples.broker.model.CompanyRegistry;
import org.drools.examples.broker.model.StockTick;


/**
 * A class to generate stock tick events
 * 
 * @author etirelli
 *
 */
public class EventGenerator {
    private static final String DATA_FILE = "src/main/resources/stocktickstream.dat";
    
    // creating 3 random objects to avoid interference among them on the distribution of values
    private static Random steps = new Random( System.currentTimeMillis() );
    private static Random symbols = new Random( System.currentTimeMillis() );
    private static Random prices = new Random( System.currentTimeMillis() );
    
    public static void main(String args[]) throws IOException {
        // 20 minutes
        long timespam = 20 * 60 * 1000;
        // interval between events: [200ms,2s]
        long[] interval = new long[]{ 200, 2000 };
        // price changes: +- 10%
        double[] priceChanges = new double[] { -0.1, 0.1 };
        // starting price range
        double[] startingPrices = new double[]{ 50, 120 };
        // companies
        Company[] companies = (new CompanyRegistry()).getCompanies().toArray( new Company[0] );
        // persister helper
        StockTickPersister persister = new StockTickPersister();
        persister.openForSave( new FileWriter( DATA_FILE ) );
        
        System.out.print("Generating data for 20 min...");

        // initializing starting prices
        for( Company company : companies ) {
            company.setCurrentPrice( nextStartingPrice( startingPrices ) );
            StockTick tick = new StockTick( company.getSymbol(),
                                            company.getCurrentPrice(),
                                            0 );
            persister.save( tick );
        }
        
        
        for( long offset = 0; offset < timespam; offset += nextStep( interval ) ) {
            int company = symbols.nextInt( companies.length );
            double price = companies[company].getCurrentPrice() * (1 + nextPriceChange( priceChanges ));
            StockTick tick = new StockTick(companies[company].getSymbol(),
                                           price,
                                           offset );
            persister.save( tick );
            companies[company].setCurrentPrice( price );
        }
        persister.close();
        
        System.out.println("done.");
    }

    private static double nextStartingPrice(double[] startingPrices) {
        double range = startingPrices[1]-startingPrices[0];
        return ( prices.nextDouble() * range ) + startingPrices[0];
    }

    private static long nextStep(long[] interval) {
        long range = interval[1]-interval[0];
        return (long) ( ( steps.nextFloat() * range ) + interval[0] );
    }

    private static double nextPriceChange(double[] interval) {
        double range = interval[1]-interval[0];
        return ( prices.nextDouble() * range ) + interval[0];
    }

}
