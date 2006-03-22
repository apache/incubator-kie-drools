package org.drools.jsr94.benchmark;

/*
 * $Id: BenchmarkTestBase.java,v 1.5 2004/11/17 03:09:44 dbarnett Exp $
 *
 * Copyright 2002-2004 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a registered trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company.
 * (http://drools.werken.com/).
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.rules.RuleServiceProvider;
import javax.rules.StatelessRuleSession;
import javax.rules.admin.RuleAdministrator;

import junit.framework.TestCase;

/**
 * The base class for JSR94 benchmark tests.
 *
 * @author <a href="mailto:thomas.diesler@softcon-itec.de">thomas diesler </a>
 */
public abstract class BenchmarkTestBase extends TestCase
{
    protected RuleServiceProvider ruleServiceProvider;

    protected RuleAdministrator ruleAdministrator;

    protected StatelessRuleSession statelessRuleSession;

    private long start, end;

    /** setup the timer. */
    protected void setUp( ) throws Exception
    {
        super.setUp( );
        start = System.currentTimeMillis( );
    }

    /** stop the timer. */
    protected void tearDown( ) throws Exception
    {
        super.tearDown( );
        end = System.currentTimeMillis( );
        System.out.println( "Elapsed time: " + ( end - start ) + "ms" );
    }

    /**
     * Convert the facts from the <code>InputStream</code> to a list of
     * objects.
     */
    protected List getInputObjects( InputStream inputStream ) throws IOException
    {
        List list = new ArrayList( );

        BufferedReader br =
            new BufferedReader( new InputStreamReader( inputStream ) );

        Map guests = new HashMap( );

        String line = null;
        while ( ( line = br.readLine( ) ) != null )
        {
            if ( line.trim( ).length( ) == 0 || line.trim( ).startsWith( ";" ) )
            {
                continue;
            }
            StringTokenizer st = new StringTokenizer( line, "() " );
            String type = st.nextToken( );

            if ( "guest".equals( type ) )
            {
                if ( !"name".equals( st.nextToken( ) ) )
                {
                    throw new IOException( "expected 'name' in: " + line );
                }
                String name = st.nextToken( );
                if ( !"sex".equals( st.nextToken( ) ) )
                {
                    throw new IOException( "expected 'sex' in: " + line );
                }
                String sex = st.nextToken( );
                if ( !"hobby".equals( st.nextToken( ) ) )
                {
                    throw new IOException( "expected 'hobby' in: " + line );
                }
                String hobby = st.nextToken( );

                Guest guest = ( Guest ) guests.get( name );
                if ( guest == null )
                {
                    guest = new Guest( name, sex.charAt( 0 ) );
                    guests.put( name, guest );
                    list.add( guest );
                }
                guest.addHobby( hobby );
            }

            if ( "last_seat".equals( type ) )
            {
                if ( !"seat".equals( st.nextToken( ) ) )
                {
                    throw new IOException( "expected 'seat' in: " + line );
                }
                list.add( new LastSeat(
                    new Integer( st.nextToken( ) ).intValue( ) ) );
            }

            if ( "context".equals( type ) )
            {
                if ( !"state".equals( st.nextToken( ) ) )
                {
                    throw new IOException( "expected 'state' in: " + line );
                }
                list.add( new Context( st.nextToken( ) ) );
            }
        }
        inputStream.close( );

        return list;
    }

    /**
     * Verify that each guest has at least one common hobby with the one before
     * him/her.
     */
    protected int validateResults( List inList, List outList )
    {
        int seatCount = 0;
        Guest lastGuest = null;
        Iterator it = outList.iterator( );
        while ( it.hasNext( ) )
        {
            Object obj = it.next( );
            if ( !( obj instanceof Seat ) )
            {
                continue;
            }

            Seat seat = ( Seat ) obj;
            if ( lastGuest == null )
            {
                lastGuest = guest4Seat( inList, seat );
            }

            Guest guest = guest4Seat( inList, seat );

            boolean hobbyFound = false;
            for ( int i = 0; !hobbyFound && i < lastGuest.getHobbies( ).size( );
                  i++ )
            {
                String hobby = ( String ) lastGuest.getHobbies( ).get( i );
                if ( guest.getHobbies( ).contains( hobby ) )
                {
                    hobbyFound = true;
                }
            }

            if ( !hobbyFound )
            {
                fail( "seat: " + seat.getSeat( )
                     + " no common hobby " + lastGuest + " -> " + guest );
            }
            seatCount++;
        }

        return seatCount;
    }

    /** Gets the Guest object from the inList base on the guest name of the seat. */
    private Guest guest4Seat(List inList, Seat seat)
    {
        Iterator it = inList.iterator( );
        while ( it.hasNext( ) )
        {
            Object obj = it.next( );
            if ( !( obj instanceof Guest ) )
            {
                continue;
            }
            Guest guest = ( Guest ) obj;
            if ( guest.getName( ).equals( seat.getName( ) ) )
            {
                return guest;
            }
        }

        return null;
    }
}
