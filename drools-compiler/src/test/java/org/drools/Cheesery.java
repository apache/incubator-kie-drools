/*
 * Copyright 2005 JBoss Inc
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

package org.drools;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Cheesery
    implements
    Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 510l;
    public final static int   MAKING_CHEESE    = 0;
    public final static int   SELLING_CHEESE   = 1;

    private List        cheeses          = new ArrayList();

    private int               status;
    private int               totalAmount;
    private Maturity          maturity;

    public List<Object> getCheeses() {
        return this.cheeses;
    }
    public void setCheeses(List l) {
        this.cheeses=l;
    }

    public void addCheese(final Cheese cheese) {
        this.cheeses.add( cheese );
        this.totalAmount += cheese.getPrice();
    }

    public void removeCheese(final Cheese cheese) {
        this.cheeses.remove( cheese );
        recalculateTotalAmount();
    }

    /**
     * Used to check inline evals.
     */
    public boolean hasSomeFlavour(String flavour) {
        return "zesty".equals(flavour);
    }

    private void recalculateTotalAmount() {
        this.totalAmount = 0;
        for( Iterator it = this.cheeses.iterator(); it.hasNext(); ) {
            this.totalAmount += ((Cheese) it.next()).getPrice();
        }
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public int getStatus() {
        return this.status;
    }

    public Maturity getMaturity() {
        return this.maturity;
    }

    public void setMaturity(final Maturity maturity) {
        this.maturity = maturity;
    }

    public int getTotalAmount() {
        return this.totalAmount;
    }

    public void setTotalAmount(final int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public static class Maturity implements Externalizable {
        public static final Maturity YOUNG = new Maturity( "young" );
        public static final Maturity OLD   = new Maturity( "old" );

        private String               age;

        public Maturity() {
        }       

        public Maturity(final String age) {
            this.age = age;
        }

        public boolean equals(Object obj) {
            if (obj instanceof Maturity) {
                return age == ((Maturity)obj).age || age != null && age.equals(((Maturity)obj).age);
            }
            return false;
        }

        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            age = (String)in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(age);
        }

        private Object readResolve() throws ObjectStreamException {
            if ( "young".equals( this.age) ) {
                return Maturity.YOUNG;
            } else {
                return Maturity.OLD;
            }
        }  
        
        public String toString() {
            return "[Maturity age='" + this.age + "']";
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cheeses == null) ? 0 : cheeses.hashCode());
        result = prime * result + ((maturity == null) ? 0 : maturity.hashCode());
        result = prime * result + status;
        result = prime * result + totalAmount;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        else if (obj instanceof Cheesery) {
            Cheesery    that    = (Cheesery)obj;
            return cheeses.equals(that.cheeses) &&
                   status == that.status &&
                   totalAmount == that.totalAmount &&
                   maturity == that.maturity || maturity != null && maturity.equals(that.maturity);
        }
        return false;
    }
}
