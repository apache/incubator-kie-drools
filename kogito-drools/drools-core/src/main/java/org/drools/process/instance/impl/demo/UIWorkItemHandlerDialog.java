package org.drools.process.instance.impl.demo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.drools.process.instance.WorkItem;

/**
 * 
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class UIWorkItemHandlerDialog extends JDialog {
    
    private static final long serialVersionUID = 400L;
    
    private UIWorkItemHandler handler;
    private WorkItem workItem;
    private JButton completeButton;
    private JButton abortButton;
    
    public UIWorkItemHandlerDialog(UIWorkItemHandler handler, WorkItem workItem) {
        super(handler, "Execute Work Item", true);
        this.handler = handler;
        this.workItem = workItem;
        setSize(new Dimension(400, 300));
        initializeComponent();
    }

    private void initializeComponent() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        getRootPane().setLayout(new BorderLayout());
        getRootPane().add(panel, BorderLayout.CENTER);
        
        JTextArea params = new JTextArea();
        params.setText(getParameters());
        params.setEditable(false);
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1;
        c.weighty = 1;
        c.gridwidth = 2;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(params, c);
        
        completeButton = new JButton("Complete");
        completeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                complete();
            }
        });
        c = new GridBagConstraints();
        c.gridy = 1;
        c.weightx = 1;
        c.anchor = GridBagConstraints.EAST;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(completeButton, c);

        abortButton = new JButton("Abort");
        abortButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                abort();
            }
        });
        c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 1;
        c.insets = new Insets(5, 5, 5, 5);
        panel.add(abortButton, c);
    }
    
    private String getParameters() {
        String result = "";
        if (workItem.getParameters() != null) {
            for (Iterator<Map.Entry<String, Object>> iterator = workItem.getParameters().entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, Object> entry = iterator.next();
                result += entry.getKey() + " = " + entry.getValue() + "\n";
            }
        }
        return result;
    }
    
    private void complete() {
        handler.complete(workItem, null);
        dispose();
    }
    
    private void abort() {
        handler.abort(workItem);
        dispose();
    }
}
