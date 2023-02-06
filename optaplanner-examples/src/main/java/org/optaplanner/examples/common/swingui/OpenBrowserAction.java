package org.optaplanner.examples.common.swingui;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

public final class OpenBrowserAction extends AbstractAction {

    private final URI uri;

    public OpenBrowserAction(String title, String urlString) {
        super(title);
        try {
            uri = new URI(urlString);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed creating URI for urlString (" + urlString + ").", e);
        }
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop == null || !desktop.isSupported(Desktop.Action.BROWSE)) {
            JOptionPane.showMessageDialog(null, "Cannot open a browser automatically."
                    + "\nPlease open this url manually:\n" + uri.toString(),
                    "Cannot open browser", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            desktop.browse(uri);
        } catch (IOException e) {
            throw new IllegalStateException("Failed showing uri (" + uri + ") in the default browser.", e);
        }
    }

}
