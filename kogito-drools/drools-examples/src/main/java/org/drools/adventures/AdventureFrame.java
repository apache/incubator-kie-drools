package org.drools.adventures;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.JSplitPane;
import java.awt.FlowLayout;
import javax.swing.JTextArea;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.JButton;
import java.awt.Component;
import javax.swing.JTabbedPane;
import javax.swing.JScrollPane;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JToggleButton;
import javax.swing.JLabel;

public class AdventureFrame extends JFrame {

    private JPanel contentPane;
    private JTable characterPropertiesTable;
    private JTable table;
    private JTable exitsTable;
    private JTable itemsTable;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater( new Runnable() {
            public void run() {
                try {
                    AdventureFrame frame = new AdventureFrame();
                    frame.setVisible( true );
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        } );
    }

    /**
     * Create the frame.
     */
    public AdventureFrame() {
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        setBounds( 100,
                   100,
                   833,
                   714 );
        contentPane = new JPanel();
        contentPane.setBorder( new EmptyBorder( 5,
                                                5,
                                                5,
                                                5 ) );
        setContentPane( contentPane );
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        
        JToolBar toolBar_1 = new JToolBar();
        toolBar_1.setAlignmentX(0.0f);
        contentPane.add(toolBar_1);
        
        JToggleButton Exit = new JToggleButton("Exit");
        toolBar_1.add(Exit);
        
        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.3);
        contentPane.add(splitPane);
        
        JSplitPane splitPane_1 = new JSplitPane();
        splitPane_1.setResizeWeight(0.8);
        
        splitPane_1.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setRightComponent(splitPane_1);
        
        JPanel outputPanel = new JPanel();
        splitPane_1.setLeftComponent(outputPanel);
        outputPanel.setLayout(new BoxLayout(outputPanel, BoxLayout.Y_AXIS));
        
        JLabel outputLabel = new JLabel("Output");
        outputLabel.setAlignmentY(Component.TOP_ALIGNMENT);
        outputPanel.add(outputLabel);
        
        JTextArea outputTextArea = new JTextArea();
        outputTextArea.setAlignmentY(Component.TOP_ALIGNMENT);
        outputTextArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        outputTextArea.setText("qerqwerqwer");
        outputPanel.add(outputTextArea);
        
        JPanel userPanel = new JPanel();
        splitPane_1.setRightComponent(userPanel);
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        
        JPanel characterPanel = new JPanel();
        characterPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        characterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userPanel.add(characterPanel);
        characterPanel.setLayout(new BoxLayout(characterPanel, BoxLayout.X_AXIS));
        
        JPanel characterSelectionPanel = new JPanel();
        characterSelectionPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        characterSelectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        FlowLayout fl_characterSelectionPanel = (FlowLayout) characterSelectionPanel.getLayout();
        fl_characterSelectionPanel.setVgap(0);
        fl_characterSelectionPanel.setHgap(0);
        characterPanel.add(characterSelectionPanel);
        
        JComboBox characterSelectCombo = new JComboBox();
        characterSelectionPanel.add(characterSelectCombo);
        characterSelectCombo.setModel(new DefaultComboBoxModel(new String[] {"Hero", "Monster"}));        
        
        characterPropertiesTable = new JTable();
        characterPropertiesTable.setAlignmentY(Component.TOP_ALIGNMENT);
        characterPropertiesTable.setAlignmentX(Component.LEFT_ALIGNMENT);
        characterPropertiesTable.setModel(new DefaultTableModel(
            new Object[][] {
                {"strength", "100"},
                {"health", "100"},
                {"coiins", "100"},
                {"speed", "100"},
                {"mana", "100"},
            },
            new String[] {
                "property", "value"
            }
        ));
        
        JScrollPane characterPropertiesPanel = new JScrollPane(characterPropertiesTable);
        characterPropertiesPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        characterPropertiesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        characterPanel.add(characterPropertiesPanel);       
        
        JPanel actionsPanel = new JPanel();
        actionsPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        actionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        userPanel.add(actionsPanel);
        actionsPanel.setLayout(new BoxLayout(actionsPanel, BoxLayout.X_AXIS));
        
        JPanel commandsPanel = new JPanel();
        commandsPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        commandsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionsPanel.add(commandsPanel);
        commandsPanel.setLayout(new BoxLayout(commandsPanel, BoxLayout.Y_AXIS));
        
        JButton moveBtn = new JButton("Move");
        commandsPanel.add(moveBtn);
        
        JButton pickupBtn = new JButton("Pick Up");
        commandsPanel.add(pickupBtn);
        
        JButton DropBtn = new JButton("Drop");
        commandsPanel.add(DropBtn);
        
        JButton lookBtn = new JButton("Look");
        commandsPanel.add(lookBtn);
        

        
        itemsTable = new JTable();
        itemsTable.setModel(new DefaultTableModel(
            new Object[][] {
                {"Sword"},
                {null},
                {null},
                {null},
                {null},
            },
            new String[] {
                "Items"
            }
        ));
        JScrollPane itemsPanel = new JScrollPane(itemsTable);
        itemsPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        itemsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionsPanel.add(itemsPanel);
        
        exitsTable = new JTable();
        exitsTable.setModel(new DefaultTableModel(
            new Object[][] {
                {"North"},
                {null},
                {null},
                {null},
                {null},
            },
            new String[] {
                "Exits"
            }
        ));
        JScrollPane exitsPanel = new JScrollPane(exitsTable);
        exitsPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        exitsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionsPanel.add(exitsPanel);        
        
        JSplitPane eventsSplitPanel = new JSplitPane();
        eventsSplitPanel.setResizeWeight(0.7);
        eventsSplitPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setLeftComponent(eventsSplitPanel);
        
        JPanel localEventsPanel = new JPanel();
        eventsSplitPanel.setLeftComponent(localEventsPanel);
        localEventsPanel.setLayout(new BoxLayout(localEventsPanel, BoxLayout.Y_AXIS));
        
        JLabel localEventsLabel = new JLabel("Local Events");
        localEventsPanel.add(localEventsLabel);
        
        JTextArea textArea = new JTextArea();
        localEventsPanel.add(textArea);
        
        JPanel globalEventsPanel = new JPanel();
        eventsSplitPanel.setRightComponent(globalEventsPanel);
        globalEventsPanel.setLayout(new BoxLayout(globalEventsPanel, BoxLayout.Y_AXIS));
        
        JLabel globalEventsLabel = new JLabel("Global Events");
        globalEventsPanel.add(globalEventsLabel);
        
        JTextArea textArea_1 = new JTextArea();
        globalEventsPanel.add(textArea_1);
    }

}
