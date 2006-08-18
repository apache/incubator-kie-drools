package org.drools.brms.client.rulelist;

import org.drools.brms.client.rpc.RepositoryServiceAsync;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.TableConfig;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * A composite that displays a list of emails that can be selected.
 */
public class RuleListView extends Composite
    implements
    TableListener,
    ClickListener {

    private static final int       EDITOR_TAB         = 1;
    private int                     visibleItemCount = -1;

    private HTML                   countLabel         = new HTML();
    private HTML                   prevButton         = new HTML( "<a href='javascript:;'>&lt; prev</a>",
                                                                  true );
    private HTML                   nextButton         = new HTML( "<a href='javascript:;'>next &gt;</a>",
                                                                  true );
    private HTML                   editButton         = new HTML( "<a href='javascript:;'>edit</a>",
                                                                  true );

    private int                    startIndex, selectedRow = -1;
    private FlexTable              table              = new FlexTable();
    private HorizontalPanel        navBar             = new HorizontalPanel();
    private TabPanel               tabPanel;
    
    private final RepositoryServiceAsync service;
    private String[][] data;
    protected int numberOfColumns;

    public RuleListView(TabPanel tab) {

        service = RepositoryServiceFactory.getService();
        tabPanel = tab;

        // Setup the table.
        table.setCellSpacing( 0 );
        table.setCellPadding( 0 );
        table.setWidth( "100%" );

        // Hook up events.
        table.addTableListener( this );
        prevButton.addClickListener( this );
        nextButton.addClickListener( this );
        editButton.addClickListener( this );

        // Create the 'navigation' bar at the upper-right.
        HorizontalPanel innerNavBar = new HorizontalPanel();
        innerNavBar.setStyleName( "rule-ListNavBar" );
        innerNavBar.setSpacing( 8 );
        innerNavBar.add( prevButton );
        innerNavBar.add( countLabel );
        innerNavBar.add( nextButton );
        innerNavBar.add( editButton );
        navBar.setHorizontalAlignment( HorizontalPanel.ALIGN_RIGHT );
        navBar.add( innerNavBar );
        navBar.setWidth( "100%" );

        //needed for composite to work
        setWidget( table );

        setStyleName( "rule-List" );

        initTable();

    }

    public void onCellClicked(SourcesTableEvents sender,
                              int row,
                              int cell) {
        // Select the row that was clicked (-1 to account for header row).
        if ( row > 0 ) selectRow( row - 1 );
    }

    public void onClick(Widget sender) {
        if ( sender == nextButton ) {
            // Move forward a page.
            startIndex += visibleItemCount;
            if ( startIndex >= data.length ) startIndex -= visibleItemCount;
            else {
                styleRow( selectedRow,
                          false );
                selectedRow = -1;
                update();
            }
        } else if ( sender == prevButton ) {
            // Move back a page.
            startIndex -= visibleItemCount;
            if ( startIndex < 0 ) startIndex = 0;
            else {
                styleRow( selectedRow,
                          false );
                selectedRow = -1;
                update();
            }
        } else if ( sender == editButton ) {
            changeTabToEdit();
        }
    }

    private void changeTabToEdit() {
        tabPanel.selectTab( EDITOR_TAB );
    }

    /**
     * Initializes the table. Will load the header config, initialise etc.
     */
    private void initTable() {
        // Create the header row.

        table.setText( 0, 0, "Please wait..." );        
        service.loadTableConfig( "ruleList", new AsyncCallback() {

            public void onFailure(Throwable caught) {
                //TODO
            }

            public void onSuccess(Object result) {
                TableConfig config = (TableConfig) result;
                String[] header = config.headers;
                numberOfColumns = header.length;
                for ( int i = 0; i < numberOfColumns; i++ ) {
                    table.setText( 0, i, header[i]);
                }                
                table.setWidget( 0,
                                 numberOfColumns,
                                 navBar );      
                visibleItemCount = config.rowsPerPage;
                data = new String[1][numberOfColumns];
                update();      
                
            }
            
        });
        
        table.getRowFormatter().setStyleName( 0,
                                              "rule-ListHeader" );

        // Initialize the rest of the rows. MN: Not sure if I need to do this here or not...
//        for ( int i = 0; i < VISIBLE_ITEM_COUNT; ++i ) {
//            table.setText( i + 1,
//                           0,
//                           "" );
//            table.setText( i + 1,
//                           1,
//                           "" );
//            table.setText( i + 1,
//                           2,
//                           "" );
//            table.setText( i + 1,
//                           3,
//                           "" );
//
//            table.getCellFormatter().setWordWrap( i + 1,
//                                                  0,
//                                                  false );
//            table.getCellFormatter().setWordWrap( i + 1,
//                                                  1,
//                                                  false );
//            table.getCellFormatter().setWordWrap( i + 1,
//                                                  2,
//                                                  false );
//            table.getCellFormatter().setWordWrap( i + 1,
//                                                  3,
//                                                  false );
//
//            //table.getFlexCellFormatter().setColSpan(i + 1, 2, 2);
//        }
    }

    /**
     * Selects the given row (relative to the current page).
     * 
     * @param row the row to be selected
     */
    private void selectRow(int row) {

        //change the style flags
        styleRow( selectedRow,
                  false );
        styleRow( row,
                  true );

        //mark the selected row
        selectedRow = row;

        //TODO: also show "preview" view here of rule.
        System.out.println("[Preview rule now]");
    }

    private void styleRow(int row,
                          boolean selected) {
        if ( row != -1 ) {
            if ( selected ) table.getRowFormatter().addStyleName( row + 1,
                                                                  "rule-SelectedRow" );
            else table.getRowFormatter().removeStyleName( row + 1,
                                                          "rule-SelectedRow" );
        }
    }
    
    /**
     * This will inject the data into the table, and refresh it.
     * The data needs to match up with how this table was configured.
     * (the table will ask the server what cols there are, and how many to a page).
     * @param data A 2D array of tablular data.
     */
    public void setData(String[][] data) {
        this.data = data;
        update();
    }
    

    private void update() {
        if (this.numberOfColumns == -1) {
            //if it hasn't been setup, can't load data yet
            return;
        }
        // Update the older/newer buttons & label.
        int count = data.length;
        int max = startIndex + visibleItemCount;
        if ( max > count ) max = count;

        prevButton.setVisible( startIndex != 0 );
        nextButton.setVisible( startIndex + visibleItemCount < count );
        countLabel.setText( "" + (startIndex + 1) + " - " + max + " of " + count );

        // Show the selected emails.
        int i = 0;
        for ( ; i < visibleItemCount; ++i ) {
            // Don't read past the end.
            if ( startIndex + i >= count ) break;

            String[] rowData = data[startIndex + i];
            
            //RuleListItem item = data.getMailItem( startIndex + i );

            // Add a new row to the table, then set each of its columns value
            
            for ( int col = 0; col < rowData.length; col++ ) {
                table.setText( i + 1, col, rowData[col] );
            }
            
//            table.setText( i + 1,
//                           0,
//                           item.name );
//            table.setText( i + 1,
//                           1,
//                           item.status );
//            table.setText( i + 1,
//                           2,
//                           item.changedBy );
//            table.setText( i + 1,
//                           3,
//                           item.version );
        }

        // Clear any remaining slots.
        for ( ; i < visibleItemCount; ++i ) {
            
            for(int col = 0; col < numberOfColumns; col++) {
                table.setHTML( i + 1,
                               col,
                               "&nbsp;" );
            }
            
        }

        // Select the first row if none is selected.
        if ( selectedRow == -1 ) selectRow( 0 );
    }
}
