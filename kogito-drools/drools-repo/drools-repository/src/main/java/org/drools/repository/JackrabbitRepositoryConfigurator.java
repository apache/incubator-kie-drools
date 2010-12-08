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

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Calendar;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.Workspace;

import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.core.TransientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * This contains code to initialise the repository for jackrabbit.
 * This is mostly a collection of utilities. 
 * Any jackrabbit specific code needs to go in here.
 */
public class JackrabbitRepositoryConfigurator implements JCRRepositoryConfigurator {

    private static final Logger log = LoggerFactory.getLogger(JackrabbitRepositoryConfigurator.class);        
    
    /* (non-Javadoc)
     * @see org.drools.repository.RepositoryConfigurator#getJCRRepository()
     */
    public Repository getJCRRepository(String repoRootDir) {

            if (repoRootDir == null) {
                return new TransientRepository();
            } else { 
                return new TransientRepository(repoRootDir + "/repository.xml", repoRootDir);
            }

    }
    
  
    
    /* (non-Javadoc)
     * @see org.drools.repository.RepositoryConfigurator#setupRulesRepository(javax.jcr.Session)
     */
    public void setupRulesRepository(Session session) throws RulesRepositoryException {
        System.out.println("Setting up the repository, registering node types etc.");
        try {
            Node root = session.getRootNode();
            Workspace ws = session.getWorkspace();

            //no need to set it up again, skip it if it has.
            boolean registered = RulesRepositoryAdministrator.isNamespaceRegistered( session );

            if (!registered) {
                ws.getNamespaceRegistry().registerNamespace("drools", RulesRepository.DROOLS_URI);
                
                //Note, the order in which they are registered actually does matter !
                this.registerNodeTypesFromCndFile("/node_type_definitions/tag_node_type.cnd", session);
                this.registerNodeTypesFromCndFile("/node_type_definitions/state_node_type.cnd", session);
                this.registerNodeTypesFromCndFile("/node_type_definitions/versionable_node_type.cnd", session);
                this.registerNodeTypesFromCndFile("/node_type_definitions/versionable_asset_folder_node_type.cnd", session);
                
                this.registerNodeTypesFromCndFile("/node_type_definitions/rule_node_type.cnd", session);
                this.registerNodeTypesFromCndFile("/node_type_definitions/rulepackage_node_type.cnd", session);
             
            }
            
            // Setup the rule repository node
            Node repositoryNode = RulesRepository.addNodeIfNew(root, RulesRepository.RULES_REPOSITORY_NAME, "nt:folder");
                    

            
            // Setup the RulePackageItem area        
            Node packageAreaNode = RulesRepository.addNodeIfNew(repositoryNode, RulesRepository.RULE_PACKAGE_AREA, "nt:folder");
            
            // Setup the global area        
            if(!packageAreaNode.hasNode(RulesRepository.RULE_GLOBAL_AREA)){
                Node globalAreaNode = RulesRepository.addNodeIfNew(packageAreaNode, RulesRepository.RULE_GLOBAL_AREA, PackageItem.RULE_PACKAGE_TYPE_NAME);
                globalAreaNode.addNode( PackageItem.ASSET_FOLDER_NAME,  "drools:versionableAssetFolder" );
                globalAreaNode.setProperty( PackageItem.TITLE_PROPERTY_NAME,  RulesRepository.RULE_GLOBAL_AREA);
                globalAreaNode.setProperty( AssetItem.DESCRIPTION_PROPERTY_NAME, "the global area that holds sharable assets");         
                globalAreaNode.setProperty(AssetItem.FORMAT_PROPERTY_NAME,	PackageItem.PACKAGE_FORMAT);
                globalAreaNode.setProperty(PackageItem.CREATOR_PROPERTY_NAME, session.getUserID());
                Calendar lastModified = Calendar.getInstance();
                globalAreaNode.setProperty(PackageItem.LAST_MODIFIED_PROPERTY_NAME,	lastModified);
            }
            
            // Setup the Snapshot area        
            RulesRepository.addNodeIfNew(repositoryNode, RulesRepository.PACKAGE_SNAPSHOT_AREA, "nt:folder");
                        
            //Setup the Category area                
            RulesRepository.addNodeIfNew(repositoryNode, RulesRepository.TAG_AREA, "nt:folder");
            
            //Setup the State area                
            RulesRepository.addNodeIfNew(repositoryNode, RulesRepository.STATE_AREA, "nt:folder");
            
            //and we need the "Draft" state
            RulesRepository.addNodeIfNew( repositoryNode.getNode( RulesRepository.STATE_AREA ), StateItem.DRAFT_STATE_NAME, StateItem.STATE_NODE_TYPE_NAME );
            
            session.save();                        
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            System.err.println(e.getMessage());
            throw new RulesRepositoryException(e);
        }
    }
    
    private void registerNodeTypesFromCndFile(String cndFileName, Session session) throws RulesRepositoryException {
        try {
            //Read in the CND file
            Reader in = new InputStreamReader(this.getClass().getResourceAsStream( cndFileName ));
            CndImporter.registerNodeTypes(in, session);            

        } catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
    }    
    
}
