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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.UnsupportedRepositoryOperationException;

import org.drools.repository.AssetItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;
import org.drools.repository.VersionableItem;
import org.junit.Ignore;
import org.junit.Test;

import junit.framework.TestCase;


/**
 * This is a bit of a hacked scalability test.
 * It will add 5000 odd rule nodes, and then do some basic operations.
 * It will take a LONG time to add these nodes, and does it in batches.
 *
 */
public class ScalabilityTest extends RepositoryTestCase {

    private static final int NUM = 5000;
    private RulesRepository repo;

    @Test
    public void testDummy() {

    }

    @Test @Ignore
    public void xxtestRun() throws Exception {
    	Properties properties = new Properties();
    	properties.put(JCRRepositoryConfigurator.REPOSITORY_ROOT_DIRECTORY, "./scalabilityTestRepo");
        RulesRepositoryConfigurator config = RulesRepositoryConfigurator.getInstance(properties);
        Session session = config.getJCRRepository().login(
                                           new SimpleCredentials("alan_parsons", "password".toCharArray()));
        config.setupRepository(session);
        repo = new RulesRepository(session);

        long start = System.currentTimeMillis();
        setupData( repo );
        System.out.println("time to add, version and tag 5000: " + (System.currentTimeMillis() - start));
        List list = listACat(repo);
        System.out.println("list size is: " + list.size());

        start = System.currentTimeMillis();
        AssetItem item = (AssetItem) list.get( 0 );
        item.updateContent( "this is a description" );
        item.checkin( "newer" );
        System.out.println("time to update and version: " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        item = (AssetItem) list.get( 42 );
        item.updateContent( "this is a description" );
        item.updateContent( "wooooooooooooooooooooooooooooooooooot" );
        item.checkin( "latest" );
        System.out.println("time to update and version: " + (System.currentTimeMillis() - start));

    }

//    /** This tests it "bare" just setting properties on node types directly. */
//    public void xxxtestBare() throws Exception {
//        hackit();
//    }

    private List listACat(RulesRepository repo) {
        long start = System.currentTimeMillis();
        List results = repo.findAssetsByCategory( "HR/CAT_1", 0, -1  ).assets;
        System.out.println("Time for listing a cat: " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        List results2 = repo.findAssetsByCategory( "HR/CAT_1", 0, -1  ).assets;
        System.out.println("Time for listing a cat: " + (System.currentTimeMillis() - start));


        start = System.currentTimeMillis();
        results2 = repo.findAssetsByCategory( "HR/CAT_100", 0, -1  ).assets;
        System.out.println("Time for listing a cat: " + (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        results2 = repo.findAssetsByCategory( "HR/CAT_100", 0, -1  ).assets;
        System.out.println("Time for listing a cat: " + (System.currentTimeMillis() - start));


        return results;
    }

    /** To run this, need to hack the addRule method to not save a session */
    private void setupData(RulesRepository repo) throws Exception {


        int count = 1;

        List list = new ArrayList();

        String prefix = "HR/";
        String cat = prefix + "CAT_1";
        for (int i=1; i <= NUM; i++ ) {

            if (i % 500 == 0) {
                repo.getSession().save();
                for ( Iterator iter = list.iterator(); iter.hasNext(); ) {
                    AssetItem element = (AssetItem) iter.next();
                    element.getNode().checkin();
                }
                list.clear();
            }


            if (i > 2500) {
                prefix = "FINANCE/";
            }

            if (count == 100) {
                count = 1;
                cat = prefix + "CAT_" + i;
                System.err.println("changing CAT");
                System.gc();

            } else {
                count++;
            }

            String ruleName = "rule_" + i + "_" + System.currentTimeMillis();
            System.out.println("ADDING rule: " + ruleName);


            AssetItem item = repo.loadDefaultPackage().addAsset( ruleName, "Foo(bar == " + i + ")panic(" + i + ");" );
            item.addCategory( cat );
            list.add( item );

        }




    }


//    private void hackit() throws Exception {
//
//
//        RulesRepository repo = new RulesRepository(true);
//        Session session = repo.getSession();
//
//
//        Node folderNode = session.getRootNode().getNode("drools:repository/drools:rule_area");
//
//        for (int i=1 ; i <= 50; i++) {
//
//            System.out.println("doing: Rule " + i);
//
//            //create the node - see section 6.7.22.6 of the spec
//            Node ruleNode = folderNode.addNode("Rule_" + i, RuleItem.RULE_NODE_TYPE_NAME );
//
//            ruleNode.setProperty(RuleItem.TITLE_PROPERTY_NAME, "Rule_" + i);
//
//            //TODO: set this property correctly once we've figured out logging in / JAAS
//            ruleNode.setProperty(RuleItem.CONTRIBUTOR_PROPERTY_NAME, "not yet implemented");
//
//            ruleNode.setProperty(RuleItem.DESCRIPTION_PROPERTY_NAME, "");
//            ruleNode.setProperty(RuleItem.FORMAT_PROPERTY_NAME, RuleItem.RULE_FORMAT);
//            ruleNode.setProperty(RuleItem.LHS_PROPERTY_NAME, "LHS_" + i);
//            ruleNode.setProperty(RuleItem.RHS_PROPERTY_NAME, "RHS_" + i);
//            ruleNode.setProperty( VersionableItem.CHECKIN_COMMENT, "Initial" );
//
//
//            Calendar lastModified = Calendar.getInstance();
//            ruleNode.setProperty(RuleItem.LAST_MODIFIED_PROPERTY_NAME, lastModified);
//            if (i % 500 == 0) {
//                System.out.println("saving......");
//                session.save();
//                System.out.println("finished.");
//            }
//        }
//
//    }

}
