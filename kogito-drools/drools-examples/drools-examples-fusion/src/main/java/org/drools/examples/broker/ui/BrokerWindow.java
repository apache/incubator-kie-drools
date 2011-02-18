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

package org.drools.examples.broker.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.drools.examples.broker.model.Company;
import org.drools.examples.broker.model.StockTick;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * Main window implementation for the Broker example
 * 
 * @author etirelli
 */
public class BrokerWindow {

    private final JFrame                    frame;
    private final Map<String, CompanyPanel> companies;
    private final LogPanel                  log;
    private final ScrollingBanner           banner;

    public BrokerWindow(final Collection<Company> companies) {
        this.log = new LogPanel();
        this.banner = new ScrollingBanner();
        this.companies = new HashMap<String, CompanyPanel>();
        this.frame = buildFrame( companies );
    }

    private JFrame buildFrame(final Collection<Company> companies) {
        FormLayout layout = new FormLayout( "10dlu, fill:max(pref;80dlu), 10dlu, fill:max(pref;80dlu), 10dlu, fill:max(pref;200dlu), 10dlu", // columns
                                            "10dlu, fill:pref, 10dlu, fill:pref, 10dlu, fill:pref, 10dlu, fill:pref, 10dlu, fill:14dlu, 3dlu" ); // rows

        PanelBuilder builder = new PanelBuilder( layout );
        CellConstraints cc = new CellConstraints();

        int x = 2;
        int y = 2;
        for ( Company company : companies ) {
            CompanyPanel panel = new CompanyPanel( company );
            this.companies.put( company.getSymbol(), panel );
            builder.add( panel.getPanel(),
                         cc.xy( x,
                                y ) );
            y = (x == 2) ? y : y + 2;
            x = (x == 2) ? 4 : 2;
        }
        builder.add( log.getPanel(), cc.xywh( 6, 2, 1, 7 ) );
        builder.add( banner, cc.xywh( 2, 10, 5, 1 ) );
        JFrame frame = new JFrame();
        frame.getRootPane().setLayout( new BorderLayout() );
        frame.getRootPane().add( builder.getPanel(), BorderLayout.CENTER );
        
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setTitle( "Drools Fusion Example: Simple Broker" );
        frame.setResizable( true );
        frame.setSize(800, 350);
        
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation( (screen.width-frame.getWidth())/2, (screen.height-frame.getHeight())/2 );
        
        Thread bannerThread = new Thread( banner );
        bannerThread.setPriority( bannerThread.getPriority()-1 );
        bannerThread.start();
        
        return frame;
    }
    
    public void show() {
        this.frame.setVisible( true );
    }
    
    public void updateCompany( String symbol ) {
        this.companies.get( symbol ).updatePanel();
    }

    public void log( String message ) {
        this.log.log( message );
    }

    public void updateTick(StockTick tick) {
        this.banner.addTick( tick );
    }
}
