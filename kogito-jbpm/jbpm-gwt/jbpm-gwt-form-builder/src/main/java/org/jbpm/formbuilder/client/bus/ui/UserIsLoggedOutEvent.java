package org.jbpm.formbuilder.client.bus.ui;

import com.google.gwt.event.shared.GwtEvent;

public class UserIsLoggedOutEvent extends GwtEvent<UserIsLoggedOutHandler> {

	public static final Type<UserIsLoggedOutHandler> TYPE = new Type<UserIsLoggedOutHandler>();
	
	public UserIsLoggedOutEvent() {
	}
	
	@Override
	public Type<UserIsLoggedOutHandler> getAssociatedType() {
		return TYPE;
	}
	
	@Override
	protected void dispatch(UserIsLoggedOutHandler handler) {
		handler.onEvent(this);
	}
}
