package org.drools.examples;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

public class ShoppingExample {

    public static final void main(String[] args) throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "Shopping.drl", ShoppingExample.class ),
                              ResourceType.DRL );

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Customer mark = new Customer( "mark",
                                      0 );
        ksession.insert( mark );

        Product shoes = new Product( "shoes",
                                     60 );
        ksession.insert( shoes );

        Product hat = new Product( "hat",
                                   60 );
        ksession.insert( hat );

        ksession.insert( new Purchase( mark,
                                       shoes ) );

        FactHandle hatPurchaseHandle = ksession.insert( new Purchase( mark,
                                                                      hat ) );

        ksession.fireAllRules();

        ksession.retract( hatPurchaseHandle );
        System.out.println( "Customer mark has returned the hat" );
        ksession.fireAllRules();
    }

    public static class Customer {
        private String name;

        private int    discount;

        public Customer(String name,
                        int discount) {
            this.name = name;
            this.discount = discount;
        }

        public String getName() {
            return name;
        }

        public int getDiscount() {
            return discount;
        }

        public void setDiscount(int discount) {
            this.discount = discount;
        }

    }

    public static class Discount {
        private Customer customer;
        private int      amount;

        public Discount(Customer customer,
                        int amount) {
            this.customer = customer;
            this.amount = amount;
        }

        public Customer getCustomer() {
            return customer;
        }

        public int getAmount() {
            return amount;
        }

    }

    public static class Product {
        private String name;
        private float  price;

        public Product(String name,
                       float price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public float getPrice() {
            return price;
        }

    }

    public static class Purchase {
        private Customer customer;
        private Product  product;

        public Purchase(Customer customer,
                        Product product) {
            this.customer = customer;
            this.product = product;
        }

        public Customer getCustomer() {
            return customer;
        }

        public Product getProduct() {
            return product;
        }
    }
}
