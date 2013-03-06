/*
 * Copyright 2005 JBoss Inc
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

package org.drools.integrationtests;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import org.drools.CommonTestMethodBase;
import org.drools.FactHandle;
import org.drools.QueryResult;
import org.drools.QueryResults;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseConfiguration.AssertBehaviour;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.Triple;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;
import org.junit.Assert;
import org.junit.Test;

/**
 * Run all the tests with the ReteOO engine implementation
 */
public class MiscTestJMV extends CommonTestMethodBase {

//    private static Logger logger = LoggerFactory.getLogger(MiscTestJMV.class);

    @Test
    /** 2 objects in KB, 3 patterns ==> 4 results !!???
     *  but the KB contains only 2 objects !! */
    public void testJMV2() throws Exception {
    	System.out.println("MiscTestJMV.testJMV2()");
    	String drl = "" +
    			"import org.drools.Triple;\n" +
    			"query \"persistent_audio\"" +
    			"   $Triple_1 : Triple( $X : subject, $Y : predicate, $Z : object )" +
    			"   $Triple_2 : Triple( subject == $X, $P0 : predicate, $V0 : object )" +
    			"   Triple( this == $Triple_2 , $V0.toString().toLowerCase().contains( \"Giant\" .toString().toLowerCase() ) )" +
    			" end";
    	makeRuleBase_StatefulSession_insert2TriplesAndQuery( drl );
    }

    @Test
    /** 3 patterns ==> 4 results !!??? 
     *  but the KB contains only 2 objects !! */
    public void testJMV21() throws Exception {
    	System.out.println("MiscTestJMV.testJMV21()");
    	String drl = "" +
    			"import org.drools.Triple;\n" +
    			"query \"persistent_audio\"" +
    			"   $Triple_1 : Triple( $X : subject, $Y : predicate, $Z : object )" +
    			"   $Triple_2 : Triple( subject == $X, $P0 : predicate, $V0 : object )" +
    			"   Triple( this == $Triple_2 , $V0.contains( \"Giant\" ) )" +
    			" end";
    	makeRuleBase_StatefulSession_insert2TriplesAndQuery( drl );
    }
    
    @Test
    /** 3 patterns ==> 4 results !!??? */
    public void testJMV22() throws Exception {
    	System.out.println("MiscTestJMV.testJMV22()");
    	String drl = "" +
    			"import org.drools.Triple;\n" +
    			"query \"persistent_audio\"" +
    			"   $Triple_1 : Triple( $X : subject, $Y : predicate, $Z : object )" +
    			"   $Triple_2 : Triple( subject == $X, $P0 : predicate, $V0 : object )" +
    			"   eval( $V0.contains( \"Giant\" ) )" +
    			" end";
    	makeRuleBase_StatefulSession_insert2TriplesAndQuery( drl );
    }
    
    @Test
    /** the simplest: one pattern */
    public void testJMV3() throws Exception {
    	System.out.println("MiscTestJMV.testJMV3()");
    	// FIXED: java.lang.InstantiationError: java.lang.CharSequence
    	makeRuleBase_StatefulSession_insert2TriplesAndQuery("package com.sample\n" +
    			"import org.drools.Triple;\n" +
    			"query \"persistent_audio\"" +
				"   $Triple_2 : Triple( $X : subject, $P0 : predicate, $V0 : object , $V0.contains( \"Giant\" ) )\n" +
    			"end");
    }

    @Test
    /** 2 patterns, toLowerCase */
   public void testJMV31() throws Exception {
    	System.out.println("MiscTestJMV.testJMV31()");
    	String drl0 = "package com.sample\n" +
    			"import org.drools.Triple;\n" +
    			"query \"persistent_audio\"" +
    			"   $Triple_2 : Triple( $X : subject, $P0 : predicate, $V0 : object )" +
    			"   Triple( this == $Triple_2 , $V0.toString().toLowerCase().contains( \"Giant\" .toString().toLowerCase() ) )" +
    			" end";
    	makeRuleBase_StatefulSession_insert2TriplesAndQuery( drl0 );
    }
    
    @Test
    /** 2 patterns */
   public void testJMV32() throws Exception {
    	System.out.println("MiscTestJMV.testJMV32()");
    	String drl1 = "package com.sample\n" +
    			"import org.drools.Triple;\n" +
    			"query \"persistent_audio\"" +
				"   $Triple_2 : Triple( $X : subject, $P0 : predicate, $V0 : object )" +
    			"   Triple( this == $Triple_2 , $V0.contains( \"Giant\" ) )" +
    			" end";
    	makeRuleBase_StatefulSession_insert2TriplesAndQuery( drl1 );
    }

    @Test
    /** like {@link #testJMV2()} , but larger KB */
    public void testJMV4() throws Exception {
    	String drl = "" +
    			"import org.drools.Triple;\n" +
    			"query \"persistent_audio\"" +
    			"   $Triple_1 : Triple( $X : subject, $Y : predicate, $Z : object )" +
    			"   $Triple_2 : Triple( subject == $X, $P0 : predicate, $V0 : object )" +
    			"   Triple( this == $Triple_2 , $V0.toString().toLowerCase().contains( \"My\" .toString().toLowerCase() ) )" +
    			" end";
		StatefulSession ss = makeRuleBase_StatefulSession(drl);
		populateStatefulSession(ss);
    	testOneQuery( ss, 11 ); // 5, 7, 10 sometimes !!!!!!!!!!!!!!!!!!!!!
    }
    
    private void populateStatefulSession( StatefulSession ss ) {
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2002%20-%20Cousin%20Mary.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#album>", "\"Giant Steps\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2002%20-%20Cousin%20Mary.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#artist>", "\"John Coltrane\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2002%20-%20Cousin%20Mary.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#disc_no>", "\"0\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2002%20-%20Cousin%20Mary.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#genre>", "\"Jazz\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2002%20-%20Cousin%20Mary.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#title>", "\"Cousin Mary\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2002%20-%20Cousin%20Mary.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track>", "\"2\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2002%20-%20Cousin%20Mary.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track_length>", "\"350\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2001%20-%20Giant%20Steps.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#album>", "\"Giant Steps\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2001%20-%20Giant%20Steps.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#artist>", "\"John Coltrane\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2001%20-%20Giant%20Steps.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#disc_no>", "\"0\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2001%20-%20Giant%20Steps.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#genre>", "\"Jazz\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2001%20-%20Giant%20Steps.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#title>", "\"Giant Steps\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2001%20-%20Giant%20Steps.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track>", "\"1\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2001%20-%20Giant%20Steps.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track_length>", "\"287\"" ));
		ss.insert( new Triple( "<http://java.sun.com/class#n3_project-ProjectGUI>", "<http://deductions.sf.net/ontology/software_applications.owl.n3#hasFeature>", "\"<http://java.sun.com/class#eulergui-gui-actions-ResultEditorAction>\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2011%20-%20Body%20and%20Soul.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#artist>", "\"John Coltrane\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2011%20-%20Body%20and%20Soul.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#genre>", "\"\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2011%20-%20Body%20and%20Soul.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#title>", "\"Body And Soul\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2011%20-%20Body%20and%20Soul.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track>", "\"11\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2011%20-%20Body%20and%20Soul.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track_length>", "\"340\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2009%20-%20Mr.%20Syms.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#album>", "\"Plays the Blues\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2009%20-%20Mr.%20Syms.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#artist>", "\"John Coltrane\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2009%20-%20Mr.%20Syms.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#disc_no>", "\"0\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2009%20-%20Mr.%20Syms.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#genre>", "\"Jazz\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2009%20-%20Mr.%20Syms.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#title>", "\"Mr. Syms\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2009%20-%20Mr.%20Syms.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track>", "\"9\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2009%20-%20Mr.%20Syms.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track_length>", "\"323\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2013%20-%20Spiral.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#album>", "\"Giant Steps\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2013%20-%20Spiral.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#artist>", "\"John Coltrane\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2013%20-%20Spiral.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#disc_no>", "\"0\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2013%20-%20Spiral.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#genre>", "\"Jazz\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2013%20-%20Spiral.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#title>", "\"Spiral\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2013%20-%20Spiral.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track>", "\"13\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2013%20-%20Spiral.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track_length>", "\"363\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2005%20-%20My%20Shining%20Hour.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#artist>", "\"John Coltrane\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2005%20-%20My%20Shining%20Hour.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#genre>", "\"\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2005%20-%20My%20Shining%20Hour.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#title>", "\"My Shining\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2005%20-%20My%20Shining%20Hour.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track>", "\"5\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2005%20-%20My%20Shining%20Hour.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track_length>", "\"293\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2014%20-%20Syeeda's%20Song%20Flute.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#album>", "\"Giant Steps\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2014%20-%20Syeeda's%20Song%20Flute.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#artist>", "\"John Coltrane\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2014%20-%20Syeeda's%20Song%20Flute.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#disc_no>", "\"0\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2014%20-%20Syeeda's%20Song%20Flute.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#genre>", "\"Jazz\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2014%20-%20Syeeda's%20Song%20Flute.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#media>", "\"ANA\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2014%20-%20Syeeda's%20Song%20Flute.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#musicmatch_preference>", "\"None\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2014%20-%20Syeeda's%20Song%20Flute.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#title>", "\"Syeeda's Song Flute\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2014%20-%20Syeeda's%20Song%20Flute.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track>", "\"14\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2014%20-%20Syeeda's%20Song%20Flute.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track_length>", "\"425\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2012%20-%20Countdown.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#album>", "\"Giant Steps\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2012%20-%20Countdown.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#artist>", "\"John Coltrane\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2012%20-%20Countdown.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#disc_no>", "\"0\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2012%20-%20Countdown.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#genre>", "\"Jazz\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2012%20-%20Countdown.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#title>", "\"Countdown\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2012%20-%20Countdown.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track>", "\"12\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2012%20-%20Countdown.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track_length>", "\"145\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2008%20-%20Summertime.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#artist>", "\"John Coltrane\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2008%20-%20Summertime.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#genre>", "\"\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2008%20-%20Summertime.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#title>", "\"Summertime\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2008%20-%20Summertime.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track>", "\"8\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2008%20-%20Summertime.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track_length>", "\"695\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2007%20-%20Central%20Park%20West.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#artist>", "\"John Coltrane\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2007%20-%20Central%20Park%20West.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#genre>", "\"\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2007%20-%20Central%20Park%20West.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#title>", "\"Central Park West\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2007%20-%20Central%20Park%20West.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track>", "\"7\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2007%20-%20Central%20Park%20West.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track_length>", "\"250\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2010%20-%20Equinox.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#artist>", "\"John Coltrane\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2010%20-%20Equinox.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#genre>", "\"\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2010%20-%20Equinox.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#title>", "\"Equinox\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2010%20-%20Equinox.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track>", "\"10\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2010%20-%20Equinox.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track_length>", "\"519\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2003%20-%20Naima.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#album>", "\"Giant Steps\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2003%20-%20Naima.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#artist>", "\"John Coltrane\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2003%20-%20Naima.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#disc_no>", "\"0\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2003%20-%20Naima.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#genre>", "\"Jazz\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2003%20-%20Naima.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#title>", "\"Naima\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2003%20-%20Naima.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track>", "\"3\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2003%20-%20Naima.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track_length>", "\"264\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2004%20-%20Like%20Sonny.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#artist>", "\"John Coltrane\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2004%20-%20Like%20Sonny.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#genre>", "\"\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2004%20-%20Like%20Sonny.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#title>", "\"Like Sonny\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2004%20-%20Like%20Sonny.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track>", "\"4\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2004%20-%20Like%20Sonny.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track_length>", "\"355\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2006%20-%20My%20Favorite%20Things.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#artist>", "\"John Coltrane\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2006%20-%20My%20Favorite%20Things.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#genre>", "\"\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2006%20-%20My%20Favorite%20Things.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#media>", "\"DIG\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2006%20-%20My%20Favorite%20Things.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#title>", "\"My Favorite Things\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2006%20-%20My%20Favorite%20Things.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track>", "\"6\"" ));
		ss.insert( new Triple( "<file:/home/jmv/Musique/John%20Coltrane-Very%20Best%20of-2000/Very%20Best%20of%20John%20Coltrane%20-%2006%20-%20My%20Favorite%20Things.mp3>", "<http://www.taxochronos.com/ontologies/2012/03/mp3TagsOntology.owl#track_length>", "\"826\"" ));
    }
    
    /** makeRuleBase_StatefulSession_insert2TriplesAndQuery */
	private void makeRuleBase_StatefulSession_insert2TriplesAndQuery(String drl) throws DroolsParserException,
			IOException {
		StatefulSession ss = makeRuleBase_StatefulSession(drl);
    	
    	ss.insert( new Triple( "<http://deductions.sf.net/samples/n3#CN1>", "<http://deductions.sf.net/n3#number>", "Giant" ) );
    	ss.insert( new Triple( "<http://deductions.sf.net/samples/n3#CN1>", "<http://deductions.sf.net/n3#title>", "Giant Steps" ) );

    	testOneQuery(ss);
	}

	private void testOneQuery(StatefulSession ss) {
		testOneQuery( ss, 2 );
	}
	
	private void testOneQuery(StatefulSession ss, int expected) {
		QueryResults res = ss.getQueryResults( "persistent_audio");
    	System.out.println("MiscTest.testJMV() " + res.size() );
    	if( ! (expected == res.size()) ) {
    		Iterator<QueryResult> it = res.iterator();
    		while (it.hasNext()) {
    			QueryResult r = (QueryResult) it.next();
    			System.out.println( r.getFactHandles().length );
    			for (int i = 0; i < r.getFactHandles().length; i++) {
					FactHandle element = r.getFactHandles()[i];
					System.out.println( "\t" + element );
				}
    		}
    		System.out.println( "KB");
    		for ( Object element : ss.getFactHandles() ) {
    			System.out.println( element );			
    		}
    	}
		ss.dispose();
		Assert.assertEquals( expected, res.size() );
	}

	private StatefulSession makeRuleBase_StatefulSession(String drl)
			throws DroolsParserException, IOException {
		System.setProperty("drools.dialect.mvel.strict", "false");
    	final RuleBaseConfiguration conf = new RuleBaseConfiguration();
    	// removing AssertBehaviour.EQUALITY changes things when setRemoveIdentities(true);
    	conf.setAssertBehaviour( AssertBehaviour.EQUALITY );
    	
    	// was recommanded by conan at one time :
//    	conf.setRemoveIdentities(true);
    	
    	RuleBase ruleBase = RuleBaseFactory.newRuleBase(conf);

    	final PackageBuilder builder = new PackageBuilder();
    	builder.addPackageFromDrl( new StringReader( drl ) );
    	final Package pkg = builder.getPackage();
    	ruleBase.addPackage( pkg );
    	StatefulSession ss = ruleBase.newStatefulSession();
		return ss;
	}
}