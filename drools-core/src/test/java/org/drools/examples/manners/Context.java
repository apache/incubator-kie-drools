package org.drools.examples.manners;

/*
 * $Id: Context.java,v 1.1 2005/07/26 01:06:32 mproctor Exp $
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
 *
 */

import java.io.Serializable;

public class Context
    implements
    Serializable {

    public static final int START_UP        = 0;
    public static final int ASSIGN_SEATS    = 1;
    public static final int MAKE_PATH       = 2;
    public static final int CHECK_DONE      = 3;
    public static final int PRINT_RESULTS   = 4;   
    
    private int             state;
    
    public Context(String state) {
        if ("start".equals( state ) ) {
            this.state = Context.START_UP;
        } else {
            throw new RuntimeException("Context '" + state + "' does not exist for Context Enum" );
        }
    }

    public Context(int state) {
        this.state = state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public boolean isState(int state) {
        return this.state == state;
    }

    public int getState() {
        return this.state;
    }        
    
    public String getStringValue() {
        switch (this.state) {
            case 0:
                return "START_UP";
            case 1:
                return "ASSIGN_SEATS";
            case 2:
                return "MAKE_PATH";
            case 3:
                return "CHECK_DONE";
            case 4:
                return "PRINT_RESULTS";                
        }
        return "";
    }

    public String toString() {
        return "[Context state=" + getStringValue() + "]";
    }
}