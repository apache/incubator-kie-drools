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

package org.drools.repository;

import java.util.Properties;
import java.util.ServiceLoader;

import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.RepositoryFactory;
import javax.jcr.Session;
import javax.jcr.Workspace;

/**
 * This abstract class is required so different JCR implementations can provide their own configuration mechanism.
 * 
 * * This contains code to initialise the repository using the {@link javax.jcr.RepositoryFactory} interface defined by the JCR 2.0
 * specification. This configurator loads the properties from the {@link PROPERTIES_FILE "/drools_repository.properties"}
 * resource, and passes these to the {@link javax.jcr.RepositoryFactory#getRepository(java.util.Map)}.
 * 
 * @author Michael Neale
 */
public abstract class JCRRepositoryConfigurator {

	protected RepositoryFactory factory;

	public static final String JCR_IMPL_CLASS            = "org.drools.repository.jcr.impl";
	public static final String REPOSITORY_ROOT_DIRECTORY = "repository.root.directory";

	/**
	 * @return a new Repository instance. There should only be one instance of this in an application. Generally, one repository
	 *         (which may be binded to JNDI) can spawn multiple sessions for each user as needed. Typically this would be created
	 *         on application startup.
	 * @param repositoryRootDirectory The directory where the data is stored. If empty, the repository will be generated there the
	 *        first time it is used. If it is null, then a default location will be used (it won't fail).
	 */
	public Repository getJCRRepository( Properties properties ) throws RepositoryException {
		try {

			String jcrImplementationClass = properties.getProperty(JCR_IMPL_CLASS);
			//Instantiate real repo.

			if (jcrImplementationClass==null) {
				// Use the JCR 2.0 RepositoryFactory to get a repository using the properties as input ...
				for (RepositoryFactory factory : ServiceLoader.load(RepositoryFactory.class)) {
					Repository repo = factory.getRepository(properties);
					if (repo != null) {
						this.factory = factory;
						return repo;
					}
				}
			}
		} catch (RepositoryException re) {
			throw new RepositoryException(re);
		}
		// If here, then we couldn't find a repository factory ...
		String msg = "Unable to find an appropriate JCR 2.0 RepositoryFactory; check the 'drools_repository.properties' configuration file.";
		throw new RepositoryException(msg);
	}

	public abstract void registerNodeTypesFromCndFile(String cndFileName, Session session, Workspace workspace) throws RepositoryException;

	/**
	 * Method called when the JCR implementation is no longer needed.
	 */
	public abstract void shutdown();
}
