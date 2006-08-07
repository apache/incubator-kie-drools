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
public class RuleList extends Composite implements TableListener, ClickListener {

  private static final int EDITOR_TAB = 1;

private static final int VISIBLE_EMAIL_COUNT = 10;

  private HTML countLabel = new HTML();
  private HTML prevButton = new HTML("<a href='javascript:;'>&lt; prev</a>",
    true);
  private HTML nextButton = new HTML("<a href='javascript:;'>next &gt;</a>",
    true);
  
  private HTML editButton = new HTML("<a href='javascript:;'>edit</a>",
		    true);  
  
  private int startIndex, selectedRow = -1;
  private FlexTable table = new FlexTable();
  private HorizontalPanel navBar = new HorizontalPanel();
  private TabPanel	tabPanel;
  
  
  public RuleList(TabPanel tab) {
	  
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
      startIndex += VISIBLE_EMAIL_COUNT;
      if (startIndex >= RuleItems.getMailItemCount())
        startIndex -= VISIBLE_EMAIL_COUNT;
      else {
        styleRow(selectedRow, false);
        selectedRow = -1;
        update();
      }
    } else if (sender == prevButton) {
      // Move back a page.
      startIndex -= VISIBLE_EMAIL_COUNT;
      if (startIndex < 0)
        startIndex = 0;
      else {
        styleRow(selectedRow, false);
        selectedRow = -1;
        update();
      }
    } else if (sender == editButton) {
    	System.out.println("selected row: " + selectedRow);
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
    table.setWidget(0, 3, navBar);
    table.getRowFormatter().setStyleName(0, "rule-ListHeader");

    // Initialize the rest of the rows.
    for (int i = 0; i < VISIBLE_EMAIL_COUNT; ++i) {
      table.setText(i + 1, 0, "");
      table.setText(i + 1, 1, "");
      table.setText(i + 1, 2, "");
      table.getCellFormatter().setWordWrap(i + 1, 0, false);
      table.getCellFormatter().setWordWrap(i + 1, 1, false);
      table.getCellFormatter().setWordWrap(i + 1, 2, false);
      table.getFlexCellFormatter().setColSpan(i + 1, 2, 2);
    }
  }

  /**
   * Selects the given row (relative to the current page).
   * 
   * @param row the row to be selected
   */
  private void selectRow(int row) {
    // When a row (other than the first one, which is used as a header) is
    // selected, display its associated MailItem.
    RuleItem item = RuleItems.getMailItem(startIndex + row);
    if (item == null)
      return;

    styleRow(selectedRow, false);
    styleRow(row, true);

    item.read = true;
    selectedRow = row;
    
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
    int count = RuleItems.getMailItemCount();
    int max = startIndex + VISIBLE_EMAIL_COUNT;
    if (max > count)
      max = count;

    prevButton.setVisible(startIndex != 0);
    nextButton.setVisible(startIndex + VISIBLE_EMAIL_COUNT < count);
    countLabel.setText("" + (startIndex + 1) + " - " + max + " of " + count);

    // Show the selected emails.
    int i = 0;
    for (; i < VISIBLE_EMAIL_COUNT; ++i) {
      // Don't read past the end.
      if (startIndex + i >= RuleItems.getMailItemCount())
        break;

      RuleItem item = RuleItems.getMailItem(startIndex + i);

      // Add a new row to the table, then set each of its columns to the
      // email's sender and subject values.
      table.setText(i + 1, 0, item.name);
      table.setText(i + 1, 1, item.status);
      table.setText(i + 1, 2, item.changedBy);
    }

    // Clear any remaining slots.
    for (; i < VISIBLE_EMAIL_COUNT; ++i) {
      table.setHTML(i + 1, 0, "&nbsp;");
      table.setHTML(i + 1, 1, "&nbsp;");
      table.setHTML(i + 1, 2, "&nbsp;");
    }

    // Select the first row if none is selected.
    if (selectedRow == -1)
      selectRow(0);
  }
}

