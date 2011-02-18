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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.MessageFormat;
import java.text.ParseException;

import org.drools.examples.broker.model.StockTick;

/**
 * A helper to save and load StockTicks
 * 
 * @author etirelli
 */
public class StockTickPersister implements EventSource {
    private MessageFormat format = new MessageFormat("{0,number,0};{1};{2,number,currency}");
    private BufferedWriter out;
    private BufferedReader in;
    private long baseTimestamp;
    private Event<StockTick> next;
    
    public void openForSave( Writer writer ) throws IOException {
        out = new BufferedWriter( writer );
    }
    
    public void openForRead( Reader reader, long baseTimestamp ) throws FileNotFoundException {
        in = new BufferedReader( reader );
        this.baseTimestamp = baseTimestamp;
    }
    
    public void save( StockTick tick ) throws IOException {
        Object[] args = new Object[] {
                                      tick.getTimestamp(),
                                      tick.getSymbol(),
                                      tick.getPrice()
        };
        out.append( format.format( args ) + "\n" );
    }
    
    public StockTick load() throws ParseException, IOException {
        Object[] results = format.parse( in.readLine() );
        StockTick tick = new StockTick( (String)results[1],
                                        ((Number)results[2]).doubleValue(),
                                        ((Number)results[0]).longValue()+baseTimestamp );
        return tick;
    }
    
    public void close() throws IOException {
        if( out != null ) {
            out.close();
        }
        if( in != null ) {
            in.close();
        }
    }

    public Event<StockTick> getNext() {
        return next;
    }

    public boolean hasNext() {
        if( in != null ) {
            try {
                StockTick tick = load();
                next = new EventImpl<StockTick>( tick.getTimestamp(), tick );
                return true;
            } catch ( Exception e ) {
                // nothing to do, return false
                e.printStackTrace();
            }
        }
        return false;
    }

}
