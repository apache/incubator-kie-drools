package org.jbpm.kie.services.cdi.producer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;

import org.jbpm.services.task.identity.LDAPUserGroupCallbackImpl;
import org.jbpm.services.task.identity.LDAPUserInfoImpl;
import org.jbpm.shared.services.cdi.Selectable;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.task.api.UserInfo;

@ApplicationScoped
@Alternative
@Selectable
public class LDAPUserGroupInfoProducer implements UserGroupInfoProducer {

	private UserGroupCallback callback = new LDAPUserGroupCallbackImpl(true);
	private UserInfo userInfo = new LDAPUserInfoImpl(true);
	
	@Override
	@ApplicationScoped
	@Produces
	public UserGroupCallback produceCallback() {
		return callback;
	}

	@Override
	@ApplicationScoped
	@Produces
	public UserInfo produceUserInfo() {
		return userInfo;
	}

}
