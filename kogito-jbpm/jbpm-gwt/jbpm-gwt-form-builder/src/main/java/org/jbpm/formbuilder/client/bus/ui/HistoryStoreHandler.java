package org.jbpm.formbuilder.client.bus.ui;

import com.google.gwt.event.shared.EventHandler;

public interface HistoryStoreHandler extends EventHandler {

    void onEvent(HistoryStoreEvent event);
}
