/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.games.adventures;

import net.miginfocom.layout.ConstraintParser;
import net.miginfocom.swing.MigLayout;
import org.drools.games.adventures.model.*;
import org.drools.games.adventures.model.Character;
import org.kie.api.runtime.Channel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class AdventureFrame extends JFrame {
    private static final Font        BUTT_FONT          = new Font( "monospaced",
                                                                    Font.PLAIN,
                                                                    12 );

    private JPanel                   contentPane;

    private JTextArea                outputTextArea;
    private JComboBox                characterSelectCombo;
    private JTextArea                localEventsTextArea;
    private JTable                   exitsTable;
    private JTable                   thingsTable;
    private JTable                   inventoryTable;
    private JFormattedTextField      cmdTextField;

    //private JTextArea                globalEventsTextArea;

    private GameEngine               gameEngine;

    private UserSession              session;

    private List                     cmd;
    //private ACLMessage               msg;

    /**
     * Create the frame.
     */
    public AdventureFrame(UserSession session,
                          final GameEngine gameEngine,
                          int onClose) {

        this.session = session;
        this.gameEngine = gameEngine;
        setDefaultCloseOperation( onClose );
        setBounds( 100,
                   100,
                   1100,
                   787 );
        contentPane = new JPanel();
        contentPane.setBorder( new EmptyBorder( 5,
                                                5,
                                                5,
                                                5 ) );
        setContentPane( contentPane );
        contentPane.setLayout( new BoxLayout( contentPane,
                                              BoxLayout.Y_AXIS ) );

        JToolBar toolBar_1 = new JToolBar();
        toolBar_1.setAlignmentX( Component.LEFT_ALIGNMENT );
        contentPane.add( toolBar_1 );

        JToggleButton newFrame = new JToggleButton( "New Window" );
        toolBar_1.add( newFrame );
        newFrame.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                TextAdventure.createFrame( gameEngine,
                                           JFrame.DISPOSE_ON_CLOSE );
            }
        } );

        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight( 0.4 );
        contentPane.add( splitPane );

        Component leftPanel = createEventsAndInvetoryPanel();
        splitPane.setLeftComponent( leftPanel );

        JPanel test = new JPanel();
        splitPane.setRightComponent( test );
        test.setLayout( new MigLayout( "",
                                       "[][][]",
                                       "[][grow, fill][][][][][fill][]" ) );

        createpOutputPanel( test );
        createCharacterPanel( test );
        createBuildActionsPanel( test );
        createSendCommandPanel( test );
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
    }

    public GameEngine getGameEngine() {
        return gameEngine;
    }

    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    public JTextArea getOutputTextArea() {
        return outputTextArea;
    }

    public JComboBox getCharacterSelectCombo() {
        return characterSelectCombo;
    }

    public JTextArea getLocalEventsTextArea() {
        return localEventsTextArea;
    }

    public JTable getExitsTable() {
        return exitsTable;
    }

    public JTable getThingsTable() {
        return thingsTable;
    }

    public JTable getInventoryTable() {
        return inventoryTable;
    }

    private Component createEventsAndInvetoryPanel() {
        JSplitPane leftSplitPanel = new JSplitPane();
        leftSplitPanel.setOrientation( JSplitPane.VERTICAL_SPLIT );
        leftSplitPanel.setDividerLocation( 400 );

        //Component eventsPanel = createEventsPanel();
        Component eventsPanel = createLocalEventsPanel();
        leftSplitPanel.setLeftComponent( eventsPanel );

        Component inventoryPanel = createInventoryPanel();
        leftSplitPanel.setRightComponent( inventoryPanel );

        return leftSplitPanel;
    }

    private Component createEventsPanel() {
        JSplitPane splitPanel = new JSplitPane();
        splitPanel.setResizeWeight( 0.4 );
        splitPanel.setOrientation( JSplitPane.VERTICAL_SPLIT );

        //splitPanel.setRightComponent( createGlobalEventsPanel() );

        splitPanel.setLeftComponent( createLocalEventsPanel() );

        return splitPanel;
    }

    private Component createGlobalEventsPanel() {
        JPanel globalEventsPanel = new JPanel();

        globalEventsPanel.setLayout( new BoxLayout( globalEventsPanel,
                                                    BoxLayout.Y_AXIS ) );

        JLabel globalEventsLabel = new JLabel( "Global Events" );
        globalEventsPanel.add( globalEventsLabel );

        JScrollPane pane1 = createTextAreaScroll( "",
                                                  20,
                                                  50,
                                                  true,
                                                  true );
        //globalEventsTextArea = (JTextArea) ((JViewport) pane1.getComponents()[0]).getComponents()[0];
        globalEventsPanel.add( pane1 );
        return globalEventsPanel;
    }

    private Component createLocalEventsPanel() {
        JPanel localEventsPanel = new JPanel();

        localEventsPanel.setLayout( new BoxLayout( localEventsPanel,
                                                   BoxLayout.Y_AXIS ) );

        JLabel localEventsLabel = new JLabel( "Events" );
        localEventsPanel.add( localEventsLabel );

        //        JScrollPane pane2 = createTextAreaScroll( "",
        //                                                  20,
        //                                                  50,
        //                                                  true,
        //                                                  true );
        JTextArea ta = new JTextArea( "",
                                      20,
                                      50 );
        ta.setFont( UIManager.getFont( "TextField.font" ) );
        ta.setWrapStyleWord( true );
        ta.setLineWrap( true );

        JScrollPane scroll = new JScrollPane( ta,
                                              ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                              ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );

        localEventsTextArea = (JTextArea) ((JViewport) scroll.getComponents()[0]).getComponents()[0];
        localEventsPanel.add( scroll );

        return localEventsPanel;
    }

    private Component createInventoryPanel() {
        inventoryTable = new JTable();
        inventoryTable.setBorder( null );
        inventoryTable.setModel( new NonEditableTableMode(
                                                        new Object[][]{
                                                        },
                                                        new String[]{
                                                                "Inventory"
                                                        }
                ) );
        inventoryTable.addMouseListener( new MouseListener() {

            public void mouseReleased(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
                if ( cmd == null ) {
                    return;
                }
                int row = inventoryTable.rowAtPoint( e.getPoint() );
                int col = inventoryTable.columnAtPoint( e.getPoint() );
                Thing t = (Thing) inventoryTable.getModel().getValueAt( row,
                                                                 col );
                cmdTextField.setText( cmdTextField.getText() + t.getName() + " " );
                cmd.add( t );
            }
        } );

        JScrollPane inventoryPanel = new JScrollPane( inventoryTable );
        return inventoryPanel;
    }

    private void createpOutputPanel(JPanel parent) {
        parent.add( createLabel( "Output" ),
                    "wrap, spanx 3" );
        //        JScrollPane pane = createTextAreaScroll( "",
        //                                                 20,
        //                                                 45,
        //                                                 true,
        //                                                 true );

        JTextArea ta = new JTextArea( "",
                                      20,
                                      50 );
        ta.setFont( UIManager.getFont( "TextField.font" ) );
        ta.setWrapStyleWord( true );
        ta.setLineWrap( true );

        JScrollPane scroll = new JScrollPane( ta,
                                              ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                                              ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );

        outputTextArea = (JTextArea) ((JViewport) scroll.getComponents()[0]).getComponents()[0];
        parent.add( scroll,
                    "wrap, span 3" );
    }

    private void createCharacterPanel(JPanel parent) {
        parent.add( createLabel( "Character" ),
                    "wrap, spanx 3" );
        characterSelectCombo = new JComboBox();
        characterSelectCombo.setModel( new DefaultComboBoxModel( new Object[]{null, null} ) );
        parent.add( characterSelectCombo,
                    "top, left" );

        Map<String, Character> characterMap = ( Map<String, Character> ) gameEngine.getData().get("characters");
        Character[] characters = characterMap.values().toArray( new Character[characterMap.size()] );

        characterSelectCombo.setModel( new DefaultComboBoxModel( characters ) );
        characterSelectCombo.setSelectedItem( characterMap.get("hero"));

        characterSelectCombo.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                Character c = (Character) characterSelectCombo.getSelectedObjects()[0];
                org.kie.api.runtime.rule.FactHandle fh = gameEngine.getKieSession().getFactHandle(session);
                session.setCharacter(c);
                gameEngine.getKieSession().update(fh, session);

                cmd = new ArrayList();
                cmd.add( LookCommand.class );
                cmd.add( characterSelectCombo.getSelectedObjects()[0] );
                gameEngine.receiveMessage( session,
                                           cmd );
                cmd = null;
            }
        } );

        JTable characterPropertiesTable = new JTable();
        characterPropertiesTable.setPreferredScrollableViewportSize( new Dimension( 240,
                                                                                    200 ) );
        characterPropertiesTable.setBorder( null );
        characterPropertiesTable.setModel( new NonEditableTableMode(
                                                                  new Object[][]{
                                                                          {"strength", "100"},
                                                                          {"health", "100"},
                                                                          {"coiins", "100"},
                                                                          {"speed", "100"},
                                                                          {"mana", "100"},
                                                                  },
                                                                  new String[]{
                                                                          "property", "value"
                                                                  }
                ) );
        JScrollPane characterPropertiesPanel = new JScrollPane( characterPropertiesTable );
        parent.add( characterPropertiesPanel,
                    "top, left, wrap, spanx 2" );
    }

    private void createBuildActionsPanel(JPanel parent) {
        parent.add( createLabel( "Actions" ),
                    "wrap, spanx 3" );

        JPanel actionsPanel = new JPanel();
        actionsPanel.setBorder( null );
        actionsPanel.setLayout( new BoxLayout( actionsPanel,
                                               BoxLayout.Y_AXIS ) );

        JButton moveBtn = new JButton( "Move" );
        moveBtn.setToolTipText("Select one Room from the Exits, then press Send");
        actionsPanel.add( moveBtn );
//        msg = new ACLMessage();
//        msg.setPerformative( Performative.REQUEST );
        
        moveBtn.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cmdTextField.setText( "Move " );
                cmd = new ArrayList();
                cmd.add(MoveCommand.class );
                cmd.add( characterSelectCombo.getSelectedObjects()[0] );
            }
        } );

        JButton pickupBtn = new JButton( "Pick Up" );
        pickupBtn.setToolTipText("Select one from the Items list, then press Send");
        actionsPanel.add( pickupBtn );
        pickupBtn.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cmdTextField.setText( "Pickup " );
                cmd = new ArrayList();
                cmd.add(PickupCommand.class);
                cmd.add( characterSelectCombo.getSelectedObjects()[0] );
            }
        } );

        JButton dropBtn = new JButton( "Drop" );
        dropBtn.setToolTipText("Select one from the Inventory, then press Send");
        actionsPanel.add( dropBtn );
        dropBtn.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cmdTextField.setText( "Drop " );
                cmd = new ArrayList();
                cmd.add(DropCommand.class);
                cmd.add( characterSelectCombo.getSelectedObjects()[0] );
            }
        } );

        JButton giveBtn = new JButton( "Give" );
        giveBtn.setToolTipText("Select one from the Inventory, then Select the target Character, then press Send");
        actionsPanel.add( giveBtn );
        giveBtn.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {                
                cmdTextField.setText( "Give " );
                cmd = new ArrayList();
                cmd.add(GiveCommand.class);
                cmd.add( characterSelectCombo.getSelectedObjects()[0] );
            }
        } );

        JButton useBtn = new JButton( "Use" );
        useBtn.setToolTipText("Select one from the Inventory, then Select the target Thing or Room");
        actionsPanel.add( useBtn );
        useBtn.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cmdTextField.setText( "Use " );
                cmd = new ArrayList();
                cmd.add(UseCommand.class);
                cmd.add( characterSelectCombo.getSelectedObjects()[0] );
            }
        } );

        JButton lookBtn = new JButton( "Look" );
        giveBtn.setToolTipText("Just press Send");
        actionsPanel.add( lookBtn );
        lookBtn.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cmdTextField.setText( "Look " );
                cmd = new ArrayList();
                cmd.add( LookCommand.class );
                cmd.add( characterSelectCombo.getSelectedObjects()[0] );
            }
        } );

        JButton searchBtn = new JButton( "Search" );
        giveBtn.setToolTipText("Select the item to search, then press send.");
        actionsPanel.add( searchBtn );
        searchBtn.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cmdTextField.setText( "Search " );
                cmd = new ArrayList();
                cmd.add( SearchCommand.class);
            }
        } );

        parent.add( actionsPanel,
                    "top, left" );

        thingsTable = new JTable();
        thingsTable.setPreferredScrollableViewportSize( new Dimension( 245,
                                                                       250 ) );
        thingsTable.setBorder( null );
        thingsTable.setModel( new NonEditableTableMode(
                                                     new Object[][]{
                                                     },
                                                     new String[]{
                                                             "Things"
                                                     }
                ) );
        thingsTable.addMouseListener( new MouseListener() {

            public void mouseReleased(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
                if ( cmd == null ) {
                    return;
                }
                int row = thingsTable.rowAtPoint( e.getPoint() );
                int col = thingsTable.columnAtPoint( e.getPoint() );
                Thing t = (Thing) thingsTable.getModel().getValueAt( row,
                                                              col );
                cmdTextField.setText( cmdTextField.getText() + t.getName() + " " );
                cmd.add( t );
            }
        } );

        JScrollPane itemsPanel = new JScrollPane( thingsTable );
        parent.add( itemsPanel,
                    "top, left" );

        exitsTable = new JTable();
        exitsTable.setPreferredScrollableViewportSize( new Dimension( 245,
                                                                      250 ) );
        exitsTable.setBorder( null );
        exitsTable.setModel( new NonEditableTableMode(
                                                    new Object[][]{
                                                    },
                                                    new String[]{
                                                            "Exits"
                                                    }
                ) );

        exitsTable.addMouseListener( new MouseListener() {

            public void mouseReleased(MouseEvent e) {
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseClicked(MouseEvent e) {
                if ( cmd == null ) {
                    return;
                }
                int row = exitsTable.rowAtPoint( e.getPoint() );
                int col = exitsTable.columnAtPoint( e.getPoint() );
                Room r = (Room) exitsTable.getModel().getValueAt( row,
                                                             col );
                cmdTextField.setText( cmdTextField.getText() + r.getName() + " " );
                cmd.add( r );
            }
        } );

        JScrollPane exitsPanel = new JScrollPane( exitsTable );
        parent.add( exitsPanel,
                    "top, left, wrap" );

    }

    public void createSendCommandPanel(JPanel parent) {
        cmdTextField = new JFormattedTextField();
        cmdTextField.setText( "" );
        parent.add( cmdTextField,
                    "growx, spanx 3, wrap" );
        JToggleButton sendBtn = new JToggleButton( "send" );
        parent.add( sendBtn );

        sendBtn.addActionListener( new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //msg.setContent( cmd );
                gameEngine.receiveMessage( session,
                                           cmd );
                cmd = null;
                cmdTextField.setText( "" );
            }
        } );
    }

    private JLabel createLabel(String text)
    {
        return createLabel( text,
                            SwingConstants.LEADING );
    }

    private JLabel createLabel(String text,
                               int align)
    {
        final JLabel b = new JLabel( text,
                                     align );
        //configureActiveComponet(b);
        return b;
    }

    private JScrollPane createTextAreaScroll(String text,
                                             int rows,
                                             int cols,
                                             boolean hasVerScroll,
                                             boolean hasHorScroll)
    {
        JTextArea ta = new JTextArea( text,
                                      rows,
                                      cols );
        ta.setFont( UIManager.getFont( "TextField.font" ) );
        ta.setWrapStyleWord( true );
        ta.setLineWrap( true );

        JScrollPane scroll = new JScrollPane( ta,
                                              hasVerScroll ? ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED : ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER,
                                              hasHorScroll ? ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED : ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );

        return scroll;
    }

    private class ConstraintListener extends MouseAdapter
    {
        public void mousePressed(MouseEvent e)
        {
            if ( e.isPopupTrigger() ) react( e );
        }

        public void mouseReleased(MouseEvent e)
        {
            if ( e.isPopupTrigger() ) react( e );
        }

        public void react(MouseEvent e)
        {
            JComponent c = (JComponent) e.getSource();
            LayoutManager lm = c.getParent().getLayout();
            if ( lm instanceof MigLayout == false ) lm = c.getLayout();

            if ( lm instanceof MigLayout ) {
                MigLayout layout = (MigLayout) lm;
                boolean isComp = layout.isManagingComponent( c );

                Object compConstr = isComp ? layout.getComponentConstraints( c ) : null;
                if ( isComp && compConstr == null ) compConstr = "";

                Object rowsConstr = isComp ? null : layout.getRowConstraints();
                Object colsConstr = isComp ? null : layout.getColumnConstraints();
                Object layoutConstr = isComp ? null : layout.getLayoutConstraints();

                ConstraintsDialog cDlg = new ConstraintsDialog( AdventureFrame.this,
                                                                //                       layoutConstr instanceof LC ? IDEUtil.getConstraintString((LC) layoutConstr, false) : (String) layoutConstr,
                                                                //                       rowsConstr instanceof AC ? IDEUtil.getConstraintString((AC) rowsConstr, false, false) : (String) rowsConstr,
                                                                //                       colsConstr instanceof AC ? IDEUtil.getConstraintString((AC) colsConstr, false, false) : (String) colsConstr,
                                                                //                       compConstr instanceof CC ? IDEUtil.getConstraintString((CC) compConstr, false) : (String) compConstr);
                                                                (String) layoutConstr,
                                                                (String) rowsConstr,
                                                                (String) colsConstr,
                                                                (String) compConstr );

                cDlg.pack();
                cDlg.setLocationRelativeTo( c );

                if ( cDlg.showDialog() ) {
                    try {
                        if ( isComp ) {
                            String constrStr = cDlg.componentConstrTF.getText().trim();
                            layout.setComponentConstraints( c,
                                                            constrStr );
                            if ( c instanceof JButton ) {
                                c.setFont( BUTT_FONT );
                                ((JButton) c).setText( constrStr.length() == 0 ? "<Empty>" : constrStr );
                            }
                        } else {
                            layout.setLayoutConstraints( cDlg.layoutConstrTF.getText() );
                            layout.setRowConstraints( cDlg.rowsConstrTF.getText() );
                            layout.setColumnConstraints( cDlg.colsConstrTF.getText() );
                        }
                    } catch ( Exception ex ) {
                        StringWriter sw = new StringWriter();
                        ex.printStackTrace( new PrintWriter( sw ) );
                        JOptionPane.showMessageDialog( SwingUtilities.getWindowAncestor( c ),
                                                       sw.toString(),
                                                       "Error parsing Constraint!",
                                                       JOptionPane.ERROR_MESSAGE );
                        return;
                    }

                    c.invalidate();
                    c.getParent().validate();
                }
            }
        }
    }

    private static class ToolTipListener extends MouseMotionAdapter
    {
        public void mouseMoved(MouseEvent e)
        {
            JComponent c = (JComponent) e.getSource();
            LayoutManager lm = c.getParent().getLayout();
            if ( lm instanceof MigLayout ) {
                Object constr = ((MigLayout) lm).getComponentConstraints( c );
                if ( constr instanceof String ) c.setToolTipText( (constr != null ? ("\"" + constr + "\"") : "null") );
            }
        }
    }

    private static class ConstraintsDialog extends JDialog
        implements
        ActionListener,
        KeyEventDispatcher
    {
        private static final Color ERROR_COLOR = new Color( 255,
                                                            180,
                                                            180 );
        private final JPanel       mainPanel   = new JPanel( new MigLayout( "fillx,flowy,ins dialog",
                                                                            "[fill]",
                                                                            "2[]2" ) );
        final JTextField           layoutConstrTF;
        final JTextField           rowsConstrTF;
        final JTextField           colsConstrTF;
        final JTextField           componentConstrTF;

        private final JButton      okButt      = new JButton( "OK" );
        private final JButton      cancelButt  = new JButton( "Cancel" );

        private boolean            okPressed   = false;

        public ConstraintsDialog(Frame owner,
                                 String layoutConstr,
                                 String rowsConstr,
                                 String colsConstr,
                                 String compConstr)
        {
            super( owner,
                   (compConstr != null ? "Edit Component Constraints" : "Edit Container Constraints"),
                   true );

            layoutConstrTF = createConstraintField( layoutConstr );
            rowsConstrTF = createConstraintField( rowsConstr );
            colsConstrTF = createConstraintField( colsConstr );
            componentConstrTF = createConstraintField( compConstr );

            if ( componentConstrTF != null ) {
                mainPanel.add( new JLabel( "Component Constraints" ) );
                mainPanel.add( componentConstrTF );
            }

            if ( layoutConstrTF != null ) {
                mainPanel.add( new JLabel( "Layout Constraints" ) );
                mainPanel.add( layoutConstrTF );
            }

            if ( colsConstrTF != null ) {
                mainPanel.add( new JLabel( "Column Constraints" ),
                               "gaptop unrel" );
                mainPanel.add( colsConstrTF );
            }

            if ( rowsConstrTF != null ) {
                mainPanel.add( new JLabel( "Row Constraints" ),
                               "gaptop unrel" );
                mainPanel.add( rowsConstrTF );
            }

            mainPanel.add( okButt,
                           "tag ok,split,flowx,gaptop 15" );
            mainPanel.add( cancelButt,
                           "tag cancel,gaptop 15" );

            setContentPane( mainPanel );

            okButt.addActionListener( this );
            cancelButt.addActionListener( this );
        }

        public void addNotify()
        {
            super.addNotify();
            KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher( this );
        }

        public void removeNotify()
        {
            KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher( this );
            super.removeNotify();
        }

        public boolean dispatchKeyEvent(KeyEvent e)
        {
            if ( e.getKeyCode() == KeyEvent.VK_ESCAPE ) dispose();
            return false;
        }

        public void actionPerformed(ActionEvent e)
        {
            if ( e.getSource() == okButt ) okPressed = true;
            dispose();
        }

        private JTextField createConstraintField(String text)
        {
            if ( text == null ) return null;

            final JTextField tf = new JTextField( text,
                                                  50 );
            tf.setFont( new Font( "monospaced",
                                  Font.PLAIN,
                                  12 ) );

            tf.addKeyListener( new KeyAdapter() {
                public void keyPressed(KeyEvent e)
                {
                    if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
                        okButt.doClick();
                        return;
                    }

                    javax.swing.Timer timer = new Timer( 50,
                                                         new ActionListener() {
                                                             public void actionPerformed(ActionEvent e)
                                                             {
                                                                 String constr = tf.getText();
                                                                 try {
                                                                     if ( tf == layoutConstrTF ) {
                                                                         ConstraintParser.parseLayoutConstraint( constr );
                                                                     } else if ( tf == rowsConstrTF ) {
                                                                         ConstraintParser.parseRowConstraints( constr );
                                                                     } else if ( tf == colsConstrTF ) {
                                                                         ConstraintParser.parseColumnConstraints( constr );
                                                                     } else if ( tf == componentConstrTF ) {
                                                                         ConstraintParser.parseComponentConstraint( constr );
                                                                     }

                                                                     tf.setBackground( Color.WHITE );
                                                                     okButt.setEnabled( true );
                                                                 } catch ( Exception ex ) {
                                                                     tf.setBackground( ERROR_COLOR );
                                                                     okButt.setEnabled( false );
                                                                 }
                                                             }
                                                         } );
                    timer.setRepeats( false );
                    timer.start();
                }
            } );

            return tf;
        }

        private boolean showDialog()
        {
            setVisible( true );
            return okPressed;
        }
    }

//    public void receiveMessage(List cmd) {
//        Performative p = null;
//
//        switch ( p ) {
//            case REQUEST :
//                //Performatives r = ( Performatives ) p ;
//        }
//    }

    public void handleRequestGive() {
        final JOptionPane pane = new JOptionPane( "xxx msg" );
        pane.setWantsInput( true );

        pane.setInputValue( "" );
        pane.setOptions( new String[]{"Yes", "No"} );

        JInternalFrame internalFrame = pane.createInternalFrame( contentPane,
                                                                 "xxx title" );
        internalFrame.setVisible( true );
        pane.show();
        internalFrame.addInternalFrameListener( new InternalFrameListener() {

            public void internalFrameOpened(InternalFrameEvent e) {
            }

            public void internalFrameIconified(InternalFrameEvent e) {
            }

            public void internalFrameDeiconified(InternalFrameEvent e) {
            }

            public void internalFrameDeactivated(InternalFrameEvent e) {
            }

            public void internalFrameClosing(InternalFrameEvent e) {

            }

            public void internalFrameClosed(InternalFrameEvent e) {
                System.out.println( pane.getInputValue() + ":" + pane.getValue() );
            }

            public void internalFrameActivated(InternalFrameEvent e) {
            }
        } );
    }

    public static class JTextAreaChannel
        implements
        Channel {
        private JTextArea textArea;

        public JTextAreaChannel(JTextArea textArea) {
            this.textArea = textArea;
        }

        public void send(Object object) {
            //textArea.insert( object.toString() + "\n", 0 );
            textArea.append( object.toString() + "\n" );
            JScrollPane scrollPane = (JScrollPane) ((JViewport) textArea.getParent()).getParent();

            // Can't get this to work :(
            //            JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
            //            JScrollBar horizontalScrollBar = scrollPane.getHorizontalScrollBar();
            //            verticalScrollBar.setValue(verticalScrollBar.getMinimum());
            //            horizontalScrollBar.setValue(horizontalScrollBar.getMinimum());            
        }
    }

//    public static class JComboBoxChannel
//        implements
//        Channel {
//        private JComboBox jcomboBox;
//
//        public JComboBoxChannel(JComboBox jcomboBox) {
//            this.jcomboBox = jcomboBox;
//        }
//
//        public void send(Object object) {
//            List list = (List) object;
//            jcomboBox.setModel( new DefaultComboBoxModel( list.toArray() ) );
//            //jcomboBox.setModel( new DefaultComboBoxModel( new Object[] { "xxxxx", "yyyyyy" } ) ) ;
//        }
//    }

    public static class JTableChannel
        implements
        Channel {
        private JTable jTable;

        public JTableChannel(JTable jTable) {
            this.jTable = jTable;
            String name = jTable.getColumnName(0);

            jTable.getColumn( name ).setCellRenderer(new DefaultTableCellRenderer() {
                public void setValue(Object value) {
                    if ( value == null ) {
                        return;
                    } else if ( value instanceof Thing ) {
                        setText(((Thing) value).getName());
                    } else {
                        setText( value.toString() );
                    }
                }
            });

        }

        public void send(Object object) {
            List<Thing> things = (List<Thing>) object;
            addRows( things );
        }

        public void addRows(List list) {
            DefaultTableModel model = (DefaultTableModel) jTable.getModel();


            if ( model.getRowCount() < list.size() ) {
                Object[][] exits = new Object[list.size()][];
                for ( int i = 0, length = model.getRowCount(); i < length; i++ ) {
                    model.setValueAt( list.get( i ),
                                      i,
                                      0 );
                }
                for ( int i = model.getRowCount(), length = exits.length; i < length; i++ ) {
                    if ( list.get( i ) == null ) {
                        continue;
                    }
                    model.addRow( new Object[]{list.get( i )} );
                }
            } else {
                Object[][] exits = new Object[list.size()][];
                for ( int i = 0; i < exits.length; i++ ) {
                    if ( list.get( i ) == null ) {
                        continue;
                    }
                    model.setValueAt( list.get( i ),
                                      i,
                                      0 );
                }
                int i = exits.length;
                while ( model.getRowCount() > exits.length ) {
                    model.removeRow( i );
                }
            }
        }
    }

    public static class NonEditableTableMode extends DefaultTableModel {
        public NonEditableTableMode() {
        }

        public NonEditableTableMode(int rowCount, int columnCount) {
            super(rowCount, columnCount);
        }

        public NonEditableTableMode(Vector columnNames, int rowCount) {
            super(columnNames, rowCount);
        }

        public NonEditableTableMode(Object[] columnNames, int rowCount) {
            super(columnNames, rowCount);
        }

        public NonEditableTableMode(Vector data, Vector columnNames) {
            super(data, columnNames);
        }

        public NonEditableTableMode(Object[][] data, Object[] columnNames) {
            super(data, columnNames);
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            //all cells false
            return false;
        }
    }

}
