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

package org.drools.examples;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

public class PetStore {

    public static void main(String[] args) {
        try {
            KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

            kbuilder.add( ResourceFactory.newClassPathResource( "PetStore.drl",
                                                                        PetStore.class ),
                                  ResourceType.DRL );
            KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
            kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

            //RuleB
            Vector<Product> stock = new Vector<Product>();
            stock.add( new Product( "Gold Fish",
                                    5 ) );
            stock.add( new Product( "Fish Tank",
                                    25 ) );
            stock.add( new Product( "Fish Food",
                                    2 ) );

            //The callback is responsible for populating working memory and
            // fireing all rules
            PetStoreUI ui = new PetStoreUI( stock,
                                            new CheckoutCallback( kbase ) );
            ui.createAndShowGUI();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * This swing UI is used to create a simple shopping cart to allow a user to add
     * and remove items from a shopping cart before doign a checkout upon doing a
     * checkout a callback is used to allow drools interaction with the shopping
     * cart ui.
     */
    public static class PetStoreUI extends JPanel {

		private static final long serialVersionUID = 1L;

		private JTextArea        output;

        private TableModel       tableModel;

        private CheckoutCallback callback;

        /**
         * Build UI using specified items and using the given callback to pass the
         * items and jframe reference to the drools application
         * 
         * @param listData
         * @param callback
         */
        public PetStoreUI(Vector<Product> items,
                          CheckoutCallback callback) {
            super( new BorderLayout() );
            this.callback = callback;

            //Create main vertical split panel
            JSplitPane splitPane = new JSplitPane( JSplitPane.VERTICAL_SPLIT );
            add( splitPane,
                 BorderLayout.CENTER );

            //create top half of split panel and add to parent
            JPanel topHalf = new JPanel();
            topHalf.setLayout( new BoxLayout( topHalf,
                                              BoxLayout.X_AXIS ) );
            topHalf.setBorder( BorderFactory.createEmptyBorder( 5,
                                                                5,
                                                                0,
                                                                5 ) );
            topHalf.setMinimumSize( new Dimension( 400,
                                                   50 ) );
            topHalf.setPreferredSize( new Dimension( 450,
                                                     250 ) );
            splitPane.add( topHalf );

            //create bottom top half of split panel and add to parent
            JPanel bottomHalf = new JPanel( new BorderLayout() );
            bottomHalf.setMinimumSize( new Dimension( 400,
                                                      50 ) );
            bottomHalf.setPreferredSize( new Dimension( 450,
                                                        300 ) );
            splitPane.add( bottomHalf );

            //Container that list container that shows available store items
            JPanel listContainer = new JPanel( new GridLayout( 1,
                                                               1 ) );
            listContainer.setBorder( BorderFactory.createTitledBorder( "List" ) );
            topHalf.add( listContainer );

            //Create JList for items, add to scroll pane and then add to parent
            // container
            JList list = new JList( items );
            ListSelectionModel listSelectionModel = list.getSelectionModel();
            listSelectionModel.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
            //handler adds item to shopping cart
            list.addMouseListener( new ListSelectionHandler() );
            JScrollPane listPane = new JScrollPane( list );
            listContainer.add( listPane );

            JPanel tableContainer = new JPanel( new GridLayout( 1,
                                                                1 ) );
            tableContainer.setBorder( BorderFactory.createTitledBorder( "Table" ) );
            topHalf.add( tableContainer );

            //Container that displays table showing items in cart
            tableModel = new TableModel();
            JTable table = new JTable( tableModel );
            //handler removes item to shopping cart
            table.addMouseListener( new TableSelectionHandler() );
            ListSelectionModel tableSelectionModel = table.getSelectionModel();
            tableSelectionModel.setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
            TableColumnModel tableColumnModel = table.getColumnModel();
            //notice we have a custom renderer for each column as both columns
            // point to the same underlying object
            tableColumnModel.getColumn( 0 ).setCellRenderer( new NameRenderer() );
            tableColumnModel.getColumn( 1 ).setCellRenderer( new PriceRenderer() );
            tableColumnModel.getColumn( 1 ).setMaxWidth( 50 );

            JScrollPane tablePane = new JScrollPane( table );
            tablePane.setPreferredSize( new Dimension( 150,
                                                       100 ) );
            tableContainer.add( tablePane );

            //Create panel for checkout button and add to bottomHalf parent
            JPanel checkoutPane = new JPanel();
            JButton button = new JButton( "Checkout" );
            button.setVerticalTextPosition( AbstractButton.CENTER );
            button.setHorizontalTextPosition( AbstractButton.LEADING );
            //attach handler to assert items into working memory
            button.addMouseListener( new CheckoutButtonHandler() );
            button.setActionCommand( "checkout" );
            checkoutPane.add( button );
            bottomHalf.add( checkoutPane,
                            BorderLayout.NORTH );

            button = new JButton( "Reset" );
            button.setVerticalTextPosition( AbstractButton.CENTER );
            button.setHorizontalTextPosition( AbstractButton.TRAILING );
            //attach handler to assert items into working memory
            button.addMouseListener( new ResetButtonHandler() );
            button.setActionCommand( "reset" );
            checkoutPane.add( button );
            bottomHalf.add( checkoutPane,
                            BorderLayout.NORTH );

            //Create output area, imbed in scroll area an add to bottomHalf parent
            //Scope is at instance level so it can be easily referenced from other
            // methods
            output = new JTextArea( 1,
                                    10 );
            output.setEditable( false );
            JScrollPane outputPane = new JScrollPane( output,
                                                      ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                                                      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
            bottomHalf.add( outputPane,
                            BorderLayout.CENTER );

            this.callback.setOutput( this.output );
        }

        /**
         * Create and show the GUI
         *  
         */
        public void createAndShowGUI() {
            //Create and set up the window.
            JFrame frame = new JFrame( "Pet Store Demo" );
            frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

            setOpaque( true );
            frame.setContentPane( this );

            //Display the window.
            frame.pack();
            frame.setVisible( true );
        }

        /**
         * Adds the selected item to the table
         */
        private class ListSelectionHandler extends MouseAdapter {
            public void mouseReleased(MouseEvent e) {
                JList jlist = (JList) e.getSource();
                tableModel.addItem( (Product) jlist.getSelectedValue() );
            }
        }

        /**
         * Removes the selected item from the table
         */
        private class TableSelectionHandler extends MouseAdapter {
            public void mouseReleased(MouseEvent e) {
                JTable jtable = (JTable) e.getSource();
                TableModel tableModel = (TableModel) jtable.getModel();
                tableModel.removeItem( jtable.getSelectedRow() );
            }
        }

        /**
         * Calls the referenced callback, passing a the jrame and selected items.
         *  
         */
        private class CheckoutButtonHandler extends MouseAdapter {
            public void mouseReleased(MouseEvent e) {
                JButton button = (JButton) e.getComponent();
                callback.checkout( (JFrame) button.getTopLevelAncestor(),
                                   tableModel.getItems() );
            }
        }

        /**
         * Resets the shopping cart, allowing the user to begin again.
         *  
         */
        private class ResetButtonHandler extends MouseAdapter {
            public void mouseReleased(MouseEvent e) {
                output.setText( null );
                tableModel.clear();
                System.out.println( "------ Reset ------" );
            }
        }

        /**
         * Used to render the name column in the table
         */
        private class NameRenderer extends DefaultTableCellRenderer {

			private static final long serialVersionUID = 1L;

			public NameRenderer() {
                super();
            }

            public void setValue(Object object) {
                Product item = (Product) object;
                setText( item.getName() );
            }
        }

        /**
         * Used to render the price column in the table
         */
        private class PriceRenderer extends DefaultTableCellRenderer {

			private static final long serialVersionUID = 1L;

			public PriceRenderer() {
                super();
            }

            public void setValue(Object object) {
                Product item = (Product) object;
                setText( Double.toString( item.getPrice() ) );
            }
        }
    }

    /**
     * This is the table model used to represent the users shopping cart While
     * we have two colums, both columns point to the same object. We user a
     * different renderer to display the different information abou the object -
     * name and price.
     */
    private static class TableModel extends AbstractTableModel {

    	private static final long serialVersionUID = 1L;

		private String[]  columnNames = {"Name", "Price"};

        private ArrayList<Product> items;

        public TableModel() {
            super();
            items = new ArrayList<Product>();
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return items.size();
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row,
                                 int col) {
            return items.get( row );
        }

        public Class<?> getColumnClass(int c) {
            return Product.class;
        }

        public void addItem(Product item) {
            items.add( item );
            fireTableRowsInserted( items.size(),
                                   items.size() );
        }

        public void removeItem(int row) {
            items.remove( row );
            fireTableRowsDeleted( row,
                                  row );
        }

        public List<Product> getItems() {
            return items;
        }

        public void clear() {
            int lastRow = items.size();
            items.clear();
            fireTableRowsDeleted( 0,
                                  lastRow );
        }
    }

    /**
     * 
     * This callback is called when the user pressed the checkout button. It is
     * responsible for adding the items to the shopping cart, asserting the shopping
     * cart and then firing all rules.
     * 
     * A reference to the JFrame is also passed so the rules can launch dialog boxes
     * for user interaction. It uses the ApplicationData feature for this.
     *  
     */
    public static class CheckoutCallback {
        KnowledgeBase kbase;
        JTextArea     output;

        public CheckoutCallback(KnowledgeBase kbase) {
            this.kbase = kbase;
        }

        public void setOutput(JTextArea output) {
            this.output = output;
        }

        /**
         * Populate the cart and assert into working memory Pass Jframe reference
         * for user interaction
         * 
         * @param frame
         * @param items
         * @return cart.toString();
         */
        public String checkout(JFrame frame, List<Product> items) {
            Order order = new Order();

            //Iterate through list and add to cart
            for ( Product p: items ) {
                order.addItem( new Purchase( order, p ) );
            }

            //add the JFrame to the ApplicationData to allow for user interaction

            StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
            ksession.setGlobal( "frame",
                                frame );
            ksession.setGlobal( "textArea",
                                this.output );

            ksession.insert( new Product( "Gold Fish",
                                          5 ) );
            ksession.insert( new Product( "Fish Tank",
                                          25 ) );
            ksession.insert( new Product( "Fish Food",
                                          2 ) );

            ksession.insert( new Product( "Fish Food Sample",
                                          0 ) );

            ksession.insert( order );
            ksession.fireAllRules();

            //returns the state of the cart
            return order.toString();
        }
    }

    public static class Order {
        private List<Purchase>          items;

        private double        grossTotal      = -1;
        private double        discountedTotal = -1;

        private static String newline         = System.getProperty( "line.separator" );

        public Order() {
            this.items = new ArrayList<Purchase>();
        }

        public void addItem(Purchase item) {
            this.items.add( item );
        }

        public List<Purchase> getItems() {
            return this.items;
        }

        public void setGrossTotal(double grossCost) {
            this.grossTotal = grossCost;
        }

        public double getGrossTotal() {
            return this.grossTotal;
        }

        public void setDiscountedTotal(double discountedCost) {
            this.discountedTotal = discountedCost;
        }

        public double getDiscountedTotal() {
            return this.discountedTotal;
        }

        public String toString() {
            StringBuffer buf = new StringBuffer();

            buf.append( "ShoppingCart:" + newline );

            Iterator<Purchase> itemIter = getItems().iterator();

            while ( itemIter.hasNext() ) {
                buf.append( "\t" + itemIter.next() + newline );
            }

            //            buf.append( "gross total=" + getGrossCost() + newline );
            //            buf.append( "discounted total=" + getDiscountedCost() + newline );

            return buf.toString();
        }
    }

    public static class Purchase {
        private Order   order;
        private Product product;

        public Purchase(Order order,
                        Product product) {
            super();
            this.order = order;
            this.product = product;
        }

        public Order getOrder() {
            return order;
        }

        public Product getProduct() {
            return product;
        }

        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((order == null) ? 0 : order.hashCode());
            result = PRIME * result + ((product == null) ? 0 : product.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            final Purchase other = (Purchase) obj;
            if ( order == null ) {
                if ( other.order != null ) return false;
            } else if ( !order.equals( other.order ) ) return false;
            if ( product == null ) {
                if ( other.product != null ) return false;
            } else if ( !product.equals( other.product ) ) return false;
            return true;
        }

    }

    public static class Product {
        private String name;

        private double price;

        public Product(String name,
                       double cost) {
            this.name = name;
            this.price = cost;
        }

        public String getName() {
            return this.name;
        }

        public double getPrice() {
            return this.price;
        }

        public String toString() {
            return name + " " + this.price;
        }

        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + ((name == null) ? 0 : name.hashCode());
            long temp;
            temp = Double.doubleToLongBits( price );
            result = PRIME * result + (int) (temp ^ (temp >>> 32));
            return result;
        }

        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            final Product other = (Product) obj;
            if ( name == null ) {
                if ( other.name != null ) return false;
            } else if ( !name.equals( other.name ) ) return false;
            if ( Double.doubleToLongBits( price ) != Double.doubleToLongBits( other.price ) ) return false;
            return true;
        }

    }

}
