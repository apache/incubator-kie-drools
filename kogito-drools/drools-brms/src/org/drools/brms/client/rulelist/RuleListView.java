package org.drools.brms.client.rulelist;


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
public class RuleListView extends Composite implements TableListener, ClickListener {

  private static final int EDITOR_TAB = 1;
  private static final int VISIBLE_ITEM_COUNT = 10;

  private HTML countLabel = new HTML();
  private HTML prevButton = new HTML("<a href='javascript:;'>&lt; prev</a>",true);
  private HTML nextButton = new HTML("<a href='javascript:;'>next &gt;</a>",true);  
  private HTML editButton = new HTML("<a href='javascript:;'>edit</a>",true);  
  
  private int startIndex, selectedRow = -1;
  private FlexTable table = new FlexTable();
  private HorizontalPanel navBar = new HorizontalPanel();
  private TabPanel	tabPanel;
  private RuleListData data = new RuleListData();
  
  public RuleListView(TabPanel tab) {
	  
	tabPanel = tab;
	  
    // Setup the table.
    table.setCellSpacing(0);
    table.setCellPadding(0);
    table.setWidth("100%");

    // Hook up events.
    table.addTableListener(this);
    prevButton.addClickListener(this);
    nextButton.addClickListener(this);
    editButton.addClickListener(this);

    // Create the 'navigation' bar at the upper-right.
    HorizontalPanel innerNavBar = new HorizontalPanel();
    innerNavBar.setStyleName("rule-ListNavBar");
    innerNavBar.setSpacing(8);
    innerNavBar.add(prevButton);
    innerNavBar.add(countLabel);
    innerNavBar.add(nextButton);
    innerNavBar.add(editButton);

    navBar.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
    navBar.add(innerNavBar); 
    navBar.setWidth("100%");

    setWidget(table);
    setStyleName("rule-List");

    initTable();
    update();
  }

  public void onCellClicked(SourcesTableEvents sender, int row, int cell) {
    // Select the row that was clicked (-1 to account for header row).
    if (row > 0)
      selectRow(row - 1);
  }

  public void onClick(Widget sender) {
    if (sender == nextButton) {
      // Move forward a page.
      startIndex += VISIBLE_ITEM_COUNT;
      if (startIndex >= data.getMailItemCount())
        startIndex -= VISIBLE_ITEM_COUNT;
      else {
        styleRow(selectedRow, false);
        selectedRow = -1;
        update();
      }
    } else if (sender == prevButton) {
      // Move back a page.
      startIndex -= VISIBLE_ITEM_COUNT;
      if (startIndex < 0)
        startIndex = 0;
      else {
        styleRow(selectedRow, false);
        selectedRow = -1;
        update();
      }
    } else if (sender == editButton) {    	
    	changeTabToEdit();
    }
  }

  private void changeTabToEdit() {
	tabPanel.selectTab(EDITOR_TAB);	
  }

  /**
   * Initializes the table so that it contains enough rows for a full page of
   * emails. Also creates the images that will be used as 'read' flags.
   */
  private void initTable() {
    // Create the header row.
    table.setText(0, 0, "name");
    table.setText(0, 1, "status");
    table.setText(0, 2, "last updated by");
    table.setText(0, 3, "version");
    table.setWidget(0, 4, navBar); //TODO: maybe put this in a seperate bar not the header
    table.getRowFormatter().setStyleName(0, "rule-ListHeader");

    // Initialize the rest of the rows.
    for (int i = 0; i < VISIBLE_ITEM_COUNT; ++i) {
      table.setText(i + 1, 0, "");
      table.setText(i + 1, 1, "");
      table.setText(i + 1, 2, "");
      table.setText(i + 1, 3, "");
      
      table.getCellFormatter().setWordWrap(i + 1, 0, false);
      table.getCellFormatter().setWordWrap(i + 1, 1, false);
      table.getCellFormatter().setWordWrap(i + 1, 2, false);
      table.getCellFormatter().setWordWrap(i + 1, 3, false);
      
      //table.getFlexCellFormatter().setColSpan(i + 1, 2, 2);
    }
  }

  /**
   * Selects the given row (relative to the current page).
   * 
   * @param row the row to be selected
   */
  private void selectRow(int row) {

	//change the style flags
    styleRow(selectedRow, false);
    styleRow(row, true);

    //mark the selected row
    selectedRow = row;
    
    //may also show "preview" view here of rule.
  }

  private void styleRow(int row, boolean selected) {
    if (row != -1) {
      if (selected)
        table.getRowFormatter().addStyleName(row + 1, "rule-SelectedRow");
      else
        table.getRowFormatter().removeStyleName(row + 1, "rule-SelectedRow");
    }
  }

  private void update() {
    // Update the older/newer buttons & label.
    int count = data.getMailItemCount();
    int max = startIndex + VISIBLE_ITEM_COUNT;
    if (max > count)
      max = count;

    prevButton.setVisible(startIndex != 0);
    nextButton.setVisible(startIndex + VISIBLE_ITEM_COUNT < count);
    countLabel.setText("" + (startIndex + 1) + " - " + max + " of " + count);

    // Show the selected emails.
    int i = 0;
    for (; i < VISIBLE_ITEM_COUNT; ++i) {
      // Don't read past the end.
      if (startIndex + i >= data.getMailItemCount())
        break;

      RuleListItem item = data.getMailItem(startIndex + i);

      // Add a new row to the table, then set each of its columns to the
      // email's sender and subject values.
      table.setText(i + 1, 0, item.name);
      table.setText(i + 1, 1, item.status);
      table.setText(i + 1, 2, item.changedBy);
      table.setText(i + 1, 3, item.version);
    }

    // Clear any remaining slots.
    for (; i < VISIBLE_ITEM_COUNT; ++i) {
      table.setHTML(i + 1, 0, "&nbsp;");
      table.setHTML(i + 1, 1, "&nbsp;");
      table.setHTML(i + 1, 2, "&nbsp;");
      table.setHTML(i + 1, 3, "&nbsp;");
    }

    // Select the first row if none is selected.
    if (selectedRow == -1)
      selectRow(0);
  }
}

