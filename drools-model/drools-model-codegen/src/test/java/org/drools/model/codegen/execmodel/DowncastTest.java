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
package org.drools.model.codegen.execmodel;

import java.math.BigDecimal;

import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class DowncastTest extends BaseModelTest {

    public DowncastTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testDowncast() {
        // DROOLS-6520
        String str =
                "import " + Product.class.getCanonicalName() + ";" +
                "import " + CashProduct.class.getCanonicalName() + ";" +
                "import " + LoanProduct.class.getCanonicalName() + ";" +
                "import " + Proposal.class.getCanonicalName() + ";" +
                "rule R no-loop when\n" +
                "  $cp : Proposal(product.type == Product.Type.Cash)\n" +
                "  $lp : Proposal(product.type == Product.Type.Loan," +
                        "Math.abs((((LoanProduct)product).loanValue.subtract(((CashProduct)$cp.product).cashValue)).divide(((CashProduct)$cp.product).cashValue)) > 3)\n" +
                "then\n" +
                "    $lp.setOrderType(\"FULL_PROPOSAL\");\n" +
                "    update($lp);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Proposal cashProposal = new Proposal(new CashProduct(new BigDecimal(2)));
        Proposal loanProposal = new Proposal(new LoanProduct(new BigDecimal(12)));

        ksession.insert(cashProposal);
        ksession.insert(loanProposal);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(loanProposal.getOrderType()).isEqualTo("FULL_PROPOSAL");
    }

    public static class Product {
        public enum Type { Cash, Loan }

        private Type type;

        public Product() { }

        public Product(Type type) {
            this.type = type;
        }

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }
    }

    public static class CashProduct extends Product {

        private BigDecimal cashValue;

        public CashProduct() {
            super(Type.Cash);
        }

        public CashProduct(BigDecimal cashValue) {
            super(Type.Cash);
            this.cashValue = cashValue;
        }

        public BigDecimal getCashValue() {
            return cashValue;
        }

        public void setCashValue(BigDecimal cashValue) {
            this.cashValue = cashValue;
        }
    }

    public static class LoanProduct extends Product {

        private BigDecimal loanValue;

        public LoanProduct() {
            super(Type.Loan);
        }

        public LoanProduct(BigDecimal loanValue) {
            super(Type.Loan);
            this.loanValue = loanValue;
        }

        public void setLoanValue(BigDecimal loanValue) {
            this.loanValue = loanValue;
        }

        public BigDecimal getLoanValue() {
            return loanValue;
        }
    }

    public static class Proposal {
        private Product product;

        private String orderType;

        public Proposal() { }

        public Proposal(Product product) {
            this.product = product;
        }

        public Product getProduct() {
            return product;
        }

        public void setProduct(Product product) {
            this.product = product;
        }

        public String getOrderType() {
            return orderType;
        }

        public void setOrderType(String orderType) {
            this.orderType = orderType;
        }
    }
}
