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

package org.drools.examples.broker;

import java.io.InputStreamReader;
import java.util.Locale;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.drools.examples.broker.events.EventFeeder;
import org.drools.examples.broker.events.StockTickPersister;
import org.drools.examples.broker.model.CompanyRegistry;
import org.drools.examples.broker.ui.BrokerWindow;
import org.drools.time.TimerService;
import org.drools.time.impl.JDKTimerService;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;

/**
 * This is the main class for the broker example.
 */
public class BrokerMain {

    /**
     * @param args
     * @throws UnsupportedLookAndFeelException 
     */
    public static void main(String[] args) throws Exception {
        // set up and show main window
        UIManager.setLookAndFeel( new Plastic3DLookAndFeel() );
        Locale.setDefault( Locale.US );
        CompanyRegistry registry = new CompanyRegistry();
        BrokerWindow window = new BrokerWindow( registry.getCompanies() );
        window.show();
        //Thread.sleep( 10000 );
        Broker broker = new Broker( window, registry );
        
        TimerService clock = new JDKTimerService(1);
        StockTickPersister source = new StockTickPersister();
        source.openForRead( new InputStreamReader( BrokerMain.class.getResourceAsStream( "/stocktickstream.dat" ) ),
                            System.currentTimeMillis() );
        
        EventFeeder feeder = new EventFeeder(clock, source, broker );
        feeder.feed();
        
    }
}
