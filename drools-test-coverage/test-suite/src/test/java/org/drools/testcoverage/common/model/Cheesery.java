/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.testcoverage.common.model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Cheesery implements Serializable {

    private static final long serialVersionUID = 510L;

    public final static int MAKING_CHEESE = 0;
    public final static int SELLING_CHEESE = 1;

    private List<Cheese> cheeses = new ArrayList<>();

    private int status;
    private int totalAmount;
    private Maturity maturity;

    public List<Cheese> getCheeses() {
        return this.cheeses;
    }

    public void setCheeses(final List<Cheese> l) {
        this.cheeses = l;
    }

    public void addCheese(final Cheese cheese) {
        this.cheeses.add(cheese);
        this.totalAmount += cheese.getPrice();
    }

    public void removeCheese(final Cheese cheese) {
        this.cheeses.remove(cheese);
        recalculateTotalAmount();
    }

    private void recalculateTotalAmount() {
        this.totalAmount = 0;
        for (final Cheese cheese : this.cheeses) {
            this.totalAmount += cheese.getPrice();
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

        public static final Maturity YOUNG = new Maturity("young");
        public static final Maturity OLD = new Maturity("old");

        private String age;

        public Maturity() {
        }

        public Maturity(final String age) {
            this.age = age;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((age == null) ? 0 : age.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Maturity other = (Maturity) obj;
            if (age == null) {
                return other.age == null;
            } else {
                return age.equals(other.age);
            }
        }

        public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException {
            age = (String) in.readObject();
        }

        public void writeExternal(final ObjectOutput out) throws IOException {
            out.writeObject(age);
        }

        private Object readResolve() throws ObjectStreamException {
            if ("young".equals(this.age)) {
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
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Cheesery) {
            final Cheesery that = (Cheesery) obj;
            return cheeses.equals(that.cheeses) &&
                    status == that.status &&
                    totalAmount == that.totalAmount &&
                    maturity == that.maturity || maturity != null && maturity.equals(that.maturity);
        }
        return false;
    }
}
