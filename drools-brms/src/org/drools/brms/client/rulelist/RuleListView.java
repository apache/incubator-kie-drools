package org.drools.brms.client.rulelist;

import org.drools.brms.client.rpc.RepositoryServiceAsync;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.TableConfig;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * A composite that displays an explorer and a list view.
 */
public class RuleListView extends Composite
    implements
    TableListener,
    ClickListener {

    
    private int                    visibleItemCount = -1;

    private HTML                   countLabel         = new HTML();
    private HTML                   prevButton         = new HTML( "<a href='javascript:;'>&lt; prev</a>",
                                                                  true );
    private HTML                   nextButton         = new HTML( "<a href='javascript:;'>next &gt;</a>",
                                                                  true );
    private HTML                   editButton         = new HTML( "<a href='javascript:;'>edit</a>",
                                                                  true );

    private int                    startIndex, selectedRow = -1;
    private Grid              table              = new Grid();
    private HorizontalPanel        navBar             = new HorizontalPanel();
    
    
    private final RepositoryServiceAsync service;
    private String[][] data;
    protected int numberOfColumns;
    private EditItemEvent editEvent;

    private long timer;

    public RuleListView(EditItemEvent event) {

        service = RepositoryServiceFactory.getService();
        this.editEvent = event;
        

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

        //this is so we can stack controls on top of the table
        VerticalPanel vert = new VerticalPanel();
        vert.add( navBar );
        vert.add( table );
        vert.setStyleName( "rule-List" );
        vert.setWidth( "100%" );
        
        table.setStyleName( "rule-List" );
        table.setWidth( "100%" );
        
        // needed for composite to work
        setWidget( vert );
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
            openEditor();
        }
    }

    /**
     * Open the editor as pertains to the selected row !
     */
    private void openEditor() {
        if (selectedRow < data.length) {
            this.editEvent.open( data[selectedRow] );
        }
    }

    /**
     * Initializes the table. Will load the header config, initialise etc.
     */
    private void initTable() {
        // Create the header row.

        table.resize( 1, 1 );
        table.getRowFormatter().setStyleName( 0, "rule-ListHeader" );  
        table.setText( 0, 0, "Please wait..." );    
        
        service.loadTableConfig( "ruleList", new AsyncCallback() {

            public void onFailure(Throwable caught) {
                //TODO
            }

            public void onSuccess(Object result) {
                TableConfig config = (TableConfig) result;
                
                String[] header = config.headers;
                numberOfColumns = header.length;                
                
                visibleItemCount = config.rowsPerPage;
                table.resize( visibleItemCount + 1, numberOfColumns);
 
                for ( int i = 0; i < numberOfColumns; i++ ) {
                    table.setText( 0, i, header[i]);
                }   
                
                data = new String[1][numberOfColumns];
                update();      
                
            }
            
        });
        
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
    
    
    
    private void startTime() {
        this.timer = System.currentTimeMillis();
    }
    
    private void finish(String message) {
        System.out.println("time taken for " + message + " was: " + (System.currentTimeMillis() - timer));
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

        
        startTime();
        
        System.out.println("i is " + visibleItemCount);
        
        // Clear any remaining slots.
        for ( ; i < visibleItemCount; ++i ) {
            table.setHTML( i + 1, 0, "&nbsp;" );
        }

        finish( "update( )");
        
        // Select the first row if none is selected.
        if ( selectedRow == -1 ) selectRow( 0 );
        
        
    }

    public void loadRulesForCategoryPath(String selectedPath) {
        service.loadRuleListForCategories( selectedPath, "", new AsyncCallback() {

            public void onFailure(Throwable caught) {
                // TODO Auto-generated method stub
            }

            public void onSuccess(Object result) {
                String[][] data = (String[][]) result;
                updateData(data);
            }
            
        });
        
    }
    
    private void updateData(String[][] data) {
        this.data = data;
        update();
    }
}
