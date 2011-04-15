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

package org.drools.benchmark.manners;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.definition.KnowledgePackage;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

public class MannersBenchmark {

    /** Number of guests at the dinner (default: 16). */
    private static int numGuests  = 16;

    /** Number of seats at the table (default: 16). */
    private static int numSeats   = 16;

    /** Minimum number of hobbies each guest should have (default: 2). */
    private static int minHobbies = 2;

    /** Maximun number of hobbies each guest should have (default: 3). */
    private static int maxHobbies = 3;

    public static void main(final String[] args) {
        KnowledgeBuilderConfiguration kbuilderConfig = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder( kbuilderConfig );
        kbuilder.add( ResourceFactory.newClassPathResource( "manners.drl",
                                                                    MannersBenchmark.class ),
                              ResourceType.DRL );
        Collection<KnowledgePackage> pkgs = kbuilder.getKnowledgePackages();

        // add the package to a rulebase
        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( pkgs );

        long totalTime = 0;
        for ( int i = 0; i < 5; i++ ) {
            StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
    
            String filename;
            if ( args.length != 0 ) {
                String arg = args[0];
                filename = arg;
            } else {
                filename = "manners128.dat";
            }
    
            InputStream is = MannersBenchmark.class.getResourceAsStream( "data/" + filename );
            List list = getInputObjects( is );
            for ( Iterator it = list.iterator(); it.hasNext(); ) {
                Object object = it.next();
                ksession.insert( object );
            }
    
            ksession.insert( new Count( 1 ) );
    
            long start = System.currentTimeMillis();
            ksession.fireAllRules();
            long time = System.currentTimeMillis() - start;
            System.err.println( time );
            totalTime += time;
            ksession.dispose();
        }
        
        System.out.println( "average : " + totalTime / 5 );
    }

    /**
     * Convert the facts from the <code>InputStream</code> to a list of
     * objects.
     */
    protected static List<Object> getInputObjects(InputStream inputStream) {
        List<Object> list = new ArrayList<Object>();

        try {
            BufferedReader br = new BufferedReader( new InputStreamReader( inputStream ) );

            String line;
            while ( (line = br.readLine()) != null ) {
                if ( line.trim().length() == 0 || line.trim().startsWith( ";" ) ) {
                    continue;
                }
                StringTokenizer st = new StringTokenizer( line,
                                                          "() " );
                String type = st.nextToken();

                if ( "guest".equals( type ) ) {
                    if ( !"name".equals( st.nextToken() ) ) {
                        throw new IOException( "expected 'name' in: " + line );
                    }
                    String name = st.nextToken();
                    if ( !"sex".equals( st.nextToken() ) ) {
                        throw new IOException( "expected 'sex' in: " + line );
                    }
                    String sex = st.nextToken();
                    if ( !"hobby".equals( st.nextToken() ) ) {
                        throw new IOException( "expected 'hobby' in: " + line );
                    }
                    String hobby = st.nextToken();

                    Guest guest = new Guest( name,
                                             Sex.resolve( sex ),
                                             Hobby.resolve( hobby ) );

                    list.add( guest );
                }

                if ( "last_seat".equals( type ) ) {
                    if ( !"seat".equals( st.nextToken() ) ) {
                        throw new IOException( "expected 'seat' in: " + line );
                    }
                    list.add( new LastSeat( new Integer( st.nextToken() ).intValue() ) );
                }

                if ( "context".equals( type ) ) {
                    if ( !"state".equals( st.nextToken() ) ) {
                        throw new IOException( "expected 'state' in: " + line );
                    }
                    list.add( new Context( st.nextToken() ) );
                }
            }
            inputStream.close();
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read inputstream properly.", e);
        }

        return list;
    }

    public static InputStream generateData() {
        final String LINE_SEPARATOR = System.getProperty( "line.separator" );

        StringWriter writer = new StringWriter();

        int maxMale = numGuests / 2;
        int maxFemale = numGuests / 2;

        int maleCount = 0;
        int femaleCount = 0;

        // init hobbies
        List<String> hobbyList = new ArrayList<String>();
        for ( int i = 1; i <= maxHobbies; i++ ) {
            hobbyList.add( "h" + i );
        }

        Random rnd = new Random();
        for ( int i = 1; i <= numGuests; i++ ) {
            char sex = rnd.nextBoolean() ? 'm' : 'f';
            if ( sex == 'm' && maleCount == maxMale ) {
                sex = 'f';
            }
            if ( sex == 'f' && femaleCount == maxFemale ) {
                sex = 'm';
            }
            if ( sex == 'm' ) {
                maleCount++;
            }
            if ( sex == 'f' ) {
                femaleCount++;
            }

            List<String> guestHobbies = new ArrayList<String>( hobbyList );

            int numHobbies = minHobbies + rnd.nextInt( maxHobbies - minHobbies + 1 );
            for ( int j = 0; j < numHobbies; j++ ) {
                int hobbyIndex = rnd.nextInt( guestHobbies.size() );
                String hobby = (String) guestHobbies.get( hobbyIndex );
                writer.write( "(guest (name n" + i + ") (sex " + sex + ") (hobby " + hobby + "))" + LINE_SEPARATOR );
                guestHobbies.remove( hobbyIndex );
            }
        }
        writer.write( "(last_seat (seat " + numSeats + "))" + LINE_SEPARATOR );

        writer.write( LINE_SEPARATOR );
        writer.write( "(context (state start))" + LINE_SEPARATOR );

        return new ByteArrayInputStream( writer.getBuffer().toString().getBytes() );
    }

}
