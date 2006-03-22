package org.drools.jsr94.benchmark;

/*
 * $Id: MannersDat.java,v 1.3 2004/11/17 03:09:44 dbarnett Exp $
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
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Miss Manners Data Generator
 *
 * The purpose of this program is to generate a file of make statements that can
 * be used as an input data set for the Miss Manners OPS5c program.
 *
 * All input to this program will be interactively obtained from the user. The
 * file of make statements will be written to file manners.dat. The user
 * specifies how many guests there will be. Each guest's name will be a unique
 * integer. Each guest is assigned a sex at random. The user can specify the
 * total number of hobbies it is possible for a guest to have, and a lower limit
 * of the number of hobbies for a guest.
 *
 * For instance, if the user chooses 10 hobbies and a lower limit of 3 hobbies,
 * each guest will have between 3 and 10 hobbies. The hobbies will be designated
 * with an integer. Finally, the user can specify the number of seats available.
 *
 * The sex of the guests is assigned so that approximately half of the guests
 * are male and half are female.
 *
 * This is based on the work of Tim Grose.
 *
 * @author <a href="mailto:thomas.diesler@softcon-itec.de">thomas diesler </a>
 */
public class MannersDat
{

    private int numGuests = 64;

    private int numSeats = 64;

    private int minHobbies = 2;

    private int maxHobbies = 3;

    public static void main( String[] args ) throws Exception
    {
        MannersDat md = new MannersDat( );
        md.initTestMetrics( );
        md.generateData( );
    }

    /**
     * Generates the manners.dat file for the test metrics.
     */
    private void generateData( ) throws IOException
    {

        File file = new File( "manners" + numGuests + ".dat" );
        PrintWriter pw = new PrintWriter( new FileWriter( file ) );

        int maxMale = numGuests / 2;
        int maxFemale = numGuests / 2;

        int maleCount = 0;
        int femaleCount = 0;

        // init hobbies
        List hobbyList = new ArrayList( );
        for ( int i = 1; i <= maxHobbies; i++ )
        {
            hobbyList.add( "h" + i );
        }

        Random rnd = new Random( );
        for ( int i = 1; i <= numGuests; i++ )
        {

            char sex = rnd.nextBoolean( ) ? 'm' : 'f';
            if ( sex == 'm' && maleCount == maxMale ) sex = 'f';
            if ( sex == 'f' && femaleCount == maxFemale ) sex = 'm';
            if ( sex == 'm' ) maleCount++;
            if ( sex == 'f' ) femaleCount++;

            List guestHobbies = new ArrayList( hobbyList );

            int numHobbies =
                minHobbies + rnd.nextInt( maxHobbies - minHobbies + 1 );
            for ( int j = 0; j < numHobbies; j++ )
            {
                int hobbyIndex = rnd.nextInt( guestHobbies.size( ) );
                String hobby = ( String ) guestHobbies.get( hobbyIndex );
                pw.println( "(guest (name n" + i + ") (sex " + sex
                            + ") (hobby " + hobby + "))" );
                guestHobbies.remove( hobbyIndex );
            }
        }

        pw.println( "(last_seat (seat " + numSeats + "))" );

        pw.println( );
        pw.println( "(context (state start))" );

        System.out.println( "generated: " + file.getAbsoluteFile( ) );

        pw.close( );
    }

    /**
     * Get the test metrics interactively
     */
    private void initTestMetrics( ) throws IOException
    {
        String instr = readInput( "number of guests [64]: " );
        if ( instr.length( ) > 0 ) {
            numGuests = new Integer( instr ).intValue( );
        }

        instr = readInput( "number of seats [64]: " );
        if ( instr.length( ) > 0 ) {
            numSeats = new Integer( instr ).intValue( );
        }

        instr = readInput( "min hobbies [2]: " );
        if ( instr.length( ) > 0 ) {
            minHobbies = new Integer( instr ).intValue( );
        }

        instr = readInput( "max hobbies [3]: " );
        if ( instr.length( ) > 0 ) {
            maxHobbies = new Integer( instr ).intValue( );
        }
    }

    /**
     * Read a line of user input, for the given message.
     */
    private String readInput( String msg ) throws IOException
    {
        System.out.print( msg );
        BufferedReader br =
            new BufferedReader( new InputStreamReader( System.in ) );
        return br.readLine( );
    }
}
