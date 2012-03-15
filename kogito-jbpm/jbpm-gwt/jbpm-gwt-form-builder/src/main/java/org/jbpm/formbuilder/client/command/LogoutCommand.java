package org.jbpm.formbuilder.client.command;

import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.FormBuilderService;

import com.google.gwt.user.client.ui.MenuItem;
import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class LogoutCommand implements BaseCommand {

    private final FormBuilderService server = FormBuilderGlobals.getInstance().getService();
    
    public LogoutCommand() {
        super();
    }
    
    @Override
    public void execute() {
        server.logout();
    }

    @Override
    public void setItem(MenuItem item) {
        //do nothing
    }

    @Override
    public void setEmbeded(String profile) {
        //do nothing
    }

}
