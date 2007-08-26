package org.drools.examples;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

import org.drools.FactException;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.RuleBaseLoader;
import java.util.Iterator;

public class PetStore {

    public static void main(String[] args) {
        try {
            //            RuleSetLoader ruleSetLoader = new RuleSetLoader();
            //            ruleSetLoader.addFromUrl( PetStore.class.getResource( args[0] ) );
            //
            //            RuleBaseLoader ruleBaseLoader = new RuleBaseLoader();
            //            ruleBaseLoader.addFromRuleSetLoader( ruleSetLoader );
            //            RuleBase ruleBase = ruleBaseLoader.buildRuleBase();

            PackageBuilder builder = new PackageBuilder();
            builder.addPackageFromDrl( new InputStreamReader( PetStore.class.getResourceAsStream( "PetStore.drl" ) ) );
            RuleBase ruleBase = RuleBaseFactory.newRuleBase();
            ruleBase.addPackage( builder.getPackage() );

            //RuleB

            Vector stock = new Vector();
            stock.add( new Product( "Gold Fish",
                                    5 ) );
            stock.add( new Product( "Fish Tank",
                                    25 ) );
            stock.add( new Product( "Fish Food",
                                    2 ) );

            //The callback is responsible for populating working memory and
            // fireing all rules
            PetStoreUI ui = new PetStoreUI( stock,
                                            new CheckoutCallback( ruleBase ) );
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
        public PetStoreUI(Vector items,
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
                try {
                    //                    output.append( callback.checkout( (JFrame) button.getTopLevelAncestor(),
                    //                                                      tableModel.getItems() ) );
                    callback.checkout( (JFrame) button.getTopLevelAncestor(),
                                       tableModel.getItems() );
                } catch ( org.drools.FactException fe ) {
                    fe.printStackTrace();
                }
            }
        }

        /**
         * Resets the shopping cart, allowing the user to begin again.
         *  
         */
        private class ResetButtonHandler extends MouseAdapter {
            public void mouseReleased(MouseEvent e) {
                JButton button = (JButton) e.getComponent();
                output.setText( null );
                tableModel.clear();
                System.out.println( "------ Reset ------" );
            }
        }

        /**
         * Used to render the name column in the table
         */
        private class NameRenderer extends DefaultTableCellRenderer {
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
        private String[]  columnNames = {"Name", "Price"};

        private ArrayList items;

        public TableModel() {
            super();
            items = new ArrayList();
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

        public Class getColumnClass(int c) {
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

        public List getItems() {
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
        RuleBase  ruleBase;
        JTextArea output;

        public CheckoutCallback(RuleBase ruleBase) {
            this.ruleBase = ruleBase;
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
        public String checkout(JFrame frame,
                               List items) throws FactException {
            Order order = new Order();

            //Iterate through list and add to cart
            for ( int i = 0; i < items.size(); i++ ) {
                order.addItem( new Purchase( order, (Product) items.get( i ) ) );
            }

            //add the JFrame to the ApplicationData to allow for user interaction
            WorkingMemory workingMemory = ruleBase.newStatefulSession();
            workingMemory.setGlobal( "frame",
                                     frame );
            workingMemory.setGlobal( "textArea",
                                     this.output );
            workingMemory.insert( order );
            workingMemory.fireAllRules();

            //returns the state of the cart
            return order.toString();
        }
    }

    public static class Order {
        private List          items;

        private double        discount;

        private static String newline = System.getProperty( "line.separator" );

        public Order() {
            this.items = new ArrayList();
            this.discount = 0;
        }
        
        public void setDiscount(double discount) {
            this.discount = discount;
        }

        public double getDiscount() {
            return this.discount;
        }

        public void addItem(Purchase item) {
            this.items.add( item );
        }

        public List getItems() {
            return this.items;
        }

        /*
        public double getGrossCost() {
            Iterator itemIter = getItems().iterator();
            Product eachItem = null;

            double cost = 0.00;

            while ( itemIter.hasNext() ) {
                eachItem = (Product) itemIter.next();

                cost += eachItem.getPrice();
            }

            return cost;
        }

        public double getDiscountedCost() {
            double cost = getGrossCost();
            double discount = getDiscount();

            double discountedCost = cost * (1 - discount);

            return discountedCost;
        }
*/
        public String toString() {
            StringBuffer buf = new StringBuffer();

            buf.append( "ShoppingCart:" + newline );

            Iterator itemIter = getItems().iterator();

            while ( itemIter.hasNext() ) {
                buf.append( "\t" + itemIter.next() + newline );
            }

//            buf.append( "gross total=" + getGrossCost() + newline );
//            buf.append( "discounted total=" + getDiscountedCost() + newline );

            return buf.toString();
        }
    }
    
    public static class Purchase {
        private Order order;
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
    }



}
