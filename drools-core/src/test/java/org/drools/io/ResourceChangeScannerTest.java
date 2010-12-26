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

package org.drools.io;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author esteban
 */
public class ResourceChangeScannerTest {

    @Test
    public void testValidPollIntervals(){
        //the default configuration should be valid!!
        ResourceFactory.getResourceChangeScannerService();

        //using a default configuration object should also work
        ResourceChangeScannerConfiguration conf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
        ResourceFactory.getResourceChangeScannerService().configure(conf);

        //a positive value should be valid
        conf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
        conf.setProperty("drools.resource.scanner.interval", "10");
        ResourceFactory.getResourceChangeScannerService().configure(conf);

        ResourceFactory.getResourceChangeScannerService().setInterval(5);

    }

    @Test
    public void testInvalidPollIntervals(){

        //0 is not allowed because of performance reasons
        try{
            ResourceChangeScannerConfiguration conf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
            conf.setProperty("drools.resource.scanner.interval", "0");
            ResourceFactory.getResourceChangeScannerService().configure(conf);
            fail("0 should not be allowed because of performance reasons.");
        } catch (IllegalArgumentException ex){
            
        }

        //a negative interval value is not allowed
        try{
            ResourceChangeScannerConfiguration conf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
            conf.setProperty("drools.resource.scanner.interval", "-10");
            ResourceFactory.getResourceChangeScannerService().configure(conf);
            fail("Negative values should not be allowed.");
        } catch (IllegalArgumentException ex){

        }

        //0 is not allowed even when setting after Scanner configuration
        try{
            ResourceChangeScannerConfiguration conf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
            conf.setProperty("drools.resource.scanner.interval", "60");
            ResourceFactory.getResourceChangeScannerService().configure(conf);

            ResourceFactory.getResourceChangeScannerService().setInterval(0);

            fail("0 should not be allowed because of performance reasons.");
        } catch (IllegalArgumentException ex){

        }


        //a negative interval value is not allowed even when setting after
        //Scanner configuration
        try{
            ResourceChangeScannerConfiguration conf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
            conf.setProperty("drools.resource.scanner.interval", "60");
            ResourceFactory.getResourceChangeScannerService().configure(conf);

            ResourceFactory.getResourceChangeScannerService().setInterval(-10);

            fail("Negative values should not be allowed.");
        } catch (IllegalArgumentException ex){

        }

    }

}
