/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.repository.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;

/**
 * Deals with storing permissions for data and so on.
 * @author Michael Neale
 */
public class PermissionManager {

    private RulesRepository repository;

    public PermissionManager(RulesRepository repo) {
        this.repository = repo;
    }

    /**
     * Save the users permissions.
     * @param userName = obviously, the user name.
     * @param perms - a map of the role type to the targets that it applies to.
     * eg: package.admin => PACKAGE UUID
     * analyst => category path
     * ADMIN => empty (no list needed for admin)
     * @throws RepositoryException
     */
    public void updateUserPermissions(String userName, Map<String, List<String>> perms) {
    	if (!isValideUserName(userName)) {
    		return;
    	}

    	try {
	    	Node permsNode = getUserPermissionNode(userName);
	    	permsNode.remove(); //remove this so we get a fresh set
	    	permsNode = getUserPermissionNode(userName).addNode("jcr:content", "nt:unstructured");
	    	for (Iterator<Map.Entry<String, List<String>>> iterator = perms.entrySet().iterator(); iterator.hasNext();) {
	    		Map.Entry<String, List<String>> en = iterator.next();
				String perm = en.getKey();
				List<String> targets = en.getValue();
				if (targets == null) targets = new ArrayList<String>();
				permsNode.setProperty(perm, targets.toArray(new String[targets.size()]));
			}
	    	this.repository.save();
    	} catch (RepositoryException e) {
    		throw new RulesRepositoryException(e);
    	}
    }

    public void createUser(String userName) {
    	if (!isValideUserName(userName)) {
    		throw new RulesRepositoryException("Invalide user name");
    	}
    	if (containsUser(userName)) {
    		throw new RulesRepositoryException("User name [" + userName + "] already exists");
    	}
    	try {
	    	Node permsNode = getUserPermissionNode(userName);
	    	permsNode.remove(); //remove this so we get a fresh set
	    	permsNode = getUserPermissionNode(userName).addNode("jcr:content", "nt:unstructured");
	    	this.repository.save();
    	} catch (RepositoryException e) {
    		throw new RulesRepositoryException(e);
    	}
    }
    
    private boolean containsUser(String userName) {
		try {
			Node userRoot = getUsersRootNode(getRootNode(repository));
			if (userRoot.hasNode(userName)) {
				return true;
			}
		} catch (RepositoryException e) {
		}

		return false;
	}
    
	private Node getUserPermissionNode(String userName)
			throws RepositoryException {
    	Node permsNode = getNode(getUserInfoNode(userName, repository), "permissions", "nt:file");
		return permsNode;
	}

    /** get the specified user info node (it is an nt:folder type) */
    public static Node getUserInfoNode(String userName, RulesRepository repo) throws RepositoryException {
        Node root = getRootNode(repo);
        return getNode(getUsersRootNode(root), userName, "nt:folder");
    }

    /** The root node of the repository */
    public static Node getRootNode(RulesRepository repo) throws RepositoryException {
        return repo.getSession().getRootNode().getNode(RulesRepository.RULES_REPOSITORY_NAME);
    }

    /**
     * Get the top node for "user_info"
     * @throws RepositoryException
     */
    public static Node getUsersRootNode(Node root) throws RepositoryException {
        return getNode(root, "user_info", "nt:folder");
    }


    /**
     * obtain a mapping of permissions for a given user.
     * @throws RepositoryException
     */
    public Map<String, List<String>> retrieveUserPermissions(String userName) {
    	try {
	    	Map<String, List<String>> result = new HashMap<String, List<String>>(10);
	    	if (!isValideUserName(userName)) {
	    		return result;
	    	}

	    	if (!getUserPermissionNode(userName).hasNode("jcr:content")) {
	    		return result;
	    	}
	    	Node permsNode = getUserPermissionNode(userName).getNode("jcr:content");
	    	PropertyIterator it = permsNode.getProperties();

	    	while (it.hasNext()) {
	    		Property p = (Property) it.next();
	    		String name = p.getName();
	    		if (!name.startsWith("jcr")) {

	    			if (p.getDefinition().isMultiple()) {
			    		Value[] vs = p.getValues();
			    		List<String> perms = new ArrayList<String>();
			    		for (int i = 0; i < vs.length; i++) {
							perms.add(vs[i].getString());
						}
			    		result.put(name, perms);
	    			} else {
	    				Value v = p.getValue();
	    				List<String> perms = new ArrayList<String>(1);
	    				perms.add(v.getString());
	    				result.put(name, perms);
	    			}
	    		}
	    	}
	    	return result;
    	} catch (RepositoryException e) {
    		throw new RulesRepositoryException(e);
    	}
    }

    /**
     * Gets or creates a node.
     */
	public static Node getNode(Node node, String name, String nodeType) throws RepositoryException {
		Node permsNode;
		if (!node.hasNode(name)) {
    		permsNode = node.addNode(name, nodeType);
    	} else {
    		permsNode = node.getNode(name);
    	}
		return permsNode;
	}

	/**
	 * Returns a list of users and their permissions types for display.
	 * The Map maps:
	 *
	 *  userName => [list of permission types, eg admin, package.admin etc... no IDs]
	 *  For display purposes only.
	 * @throws RepositoryException
	 */
	public Map<String, List<String>> listUsers() {
		try {
			Map<String, List<String>> listing = new HashMap<String, List<String>>();
			Node root = getRootNode(this.repository);
	    	Node usersNode = getUsersRootNode(root);
	    	NodeIterator users = usersNode.getNodes();
	    	while (users.hasNext()) {
				Node userNode = (Node) users.next();
				listing.put(userNode.getName(), listOfPermTypes(userNode));
			}
			return listing;
		} catch (RepositoryException e) {
    		throw new RulesRepositoryException(e);
    	}
	}

	private List<String> listOfPermTypes(Node userNode) throws RepositoryException {
		List<String> permTypes = new ArrayList<String>();
		Node permsNode = getNode(userNode, "permissions", "nt:file");
		Node content = getNode(permsNode, "jcr:content", "nt:unstructured");
		PropertyIterator perms = content.getProperties();
		while (perms.hasNext()) {
    		Property p = (Property) perms.next();
    		String name = p.getName();
    		if (!name.startsWith("jcr")) {
	    		permTypes.add(name);
    		}

		}
		return permTypes;
	}

	void deleteAllUsers() throws RepositoryException {
		Node root = getRootNode(this.repository);
		getUsersRootNode(root).remove();
	}

	public void removeUserPermissions(String userName) {
    	if (!isValideUserName(userName)) {
    		return;
    	}

		try {
	    	Node permsNode = getUserPermissionNode(userName);
	    	permsNode.getParent().remove(); //remove this so we get a fresh set
		} catch (RepositoryException e) {
			throw new RulesRepositoryException(e);
		}

	}

	private boolean isValideUserName(String userName) {
		if("".equals(userName.trim()) || userName.trim().length() == 0) {
			return false;
		}
		return true;
	}
}
