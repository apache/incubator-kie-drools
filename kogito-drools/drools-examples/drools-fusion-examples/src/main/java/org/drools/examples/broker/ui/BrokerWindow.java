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
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.drools.examples.broker.model.Company;
import org.drools.examples.broker.model.StockTick;

/**
 * Main window implementation for the Broker example
 */
public class BrokerWindow {

    private final JFrame                    frame;
    private final Map<String, CompanyPanel> companies;
    private final LogPanel logPanel;
    private final ScrollingBanner           banner;

    public BrokerWindow(final Collection<Company> companies) {
        this.logPanel = new LogPanel();
        this.banner = new ScrollingBanner();
        this.companies = new HashMap<String, CompanyPanel>();
        this.frame = buildFrame( companies );
    }

    private JFrame buildFrame(final Collection<Company> companies) {
        JPanel contentPanel = new JPanel(new BorderLayout());

        JPanel companyListPanel = new JPanel(new GridLayout(0, 2));

        for ( Company company : companies ) {
            CompanyPanel panel = new CompanyPanel( company );
            this.companies.put( company.getSymbol(), panel );
            companyListPanel.add(panel);
        }
        contentPanel.add( companyListPanel, BorderLayout.WEST );
        contentPanel.add( logPanel, BorderLayout.CENTER );
        contentPanel.add( banner, BorderLayout.SOUTH );

        JFrame frame = new JFrame();
        frame.setContentPane(contentPanel);
        
        frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        frame.setTitle( "Drools Fusion Example: Simple Broker" );
        frame.setResizable( true );
        frame.pack();
        
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
        this.companies.get( symbol ).updateCompany();
    }

    public void log( String message ) {
        this.logPanel.log( message );
    }

    public void updateTick(StockTick tick) {
        this.banner.addTick( tick );
    }
}
