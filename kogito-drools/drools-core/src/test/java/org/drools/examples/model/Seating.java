package org.drools.examples.model;

/*
 * $Id: Seating.java,v 1.1 2005/07/26 01:06:32 mproctor Exp $
 *
 * Copyright 2002 (C) The Werken Company. All Rights Reserved.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Seating
    implements
    Serializable {
    private int     seat1, seat2;

    private Guest   guest1, guest2;

    private List    tabooList = new ArrayList();

    private Seating prevSeat;

    public Seating(int seat1,
                   Guest guest1,
                   Seating prevSeat){
        this.seat1 = seat1;
        this.guest1 = guest1;
        this.prevSeat = prevSeat;
        this.seat2 = seat1 + 1;

        if ( prevSeat != null ) {
            this.tabooList.addAll( prevSeat.tabooList );
        }

        this.tabooList.add( guest1 );
    }

    public int getSeat1(){
        return this.seat1;
    }

    public int getSeat2(){
        return this.seat2;
    }

    public Guest getGuest1(){
        return this.guest1;
    }

    public Guest getGuest2(){
        return this.guest2;
    }

    public void setGuest2(Guest guest2){
        this.guest2 = guest2;
    }

    public Seating getPrevSeat(){
        return this.prevSeat;
    }

    public List getTabooList(){
        return this.tabooList;
    }

    public String toString(){
        return "{seat1=" + this.seat1 + ",guest1=" + this.guest1 + ",seat2=" + this.seat2 + ",guest2=" + this.guest2 + "}";
    }
}