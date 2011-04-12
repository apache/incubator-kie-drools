package org.drools.pmml_4_0;



import static org.junit.Assert.*;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBaseConfiguration;
import org.drools.builder.*;
import org.drools.common.DefaultFactHandle;
import org.drools.common.EventFactHandle;
import org.drools.conf.EventProcessingOption;
import org.drools.definition.type.FactType;
import org.drools.informer.Questionnaire;
import org.drools.io.ResourceFactory;
import org.drools.runtime.ClassObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;

import java.io.*;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;


public abstract class DroolsAbstractPMMLTest {


    public static final String PMML = "org.drools.pmml_4_0.descr";
    public static final String BASE_PACK = DroolsAbstractPMMLTest.class.getPackage().getName().replace('.','/');




    public static final String RESOURCE_PATH = BASE_PACK;
    public static final String TEMPLATE_PATH = "/" + RESOURCE_PATH + "/org/drools/pmml_4_0/templates/";



    private StatefulKnowledgeSession kSession;
    private KnowledgeBase kbase;





    public DroolsAbstractPMMLTest() {
        super();


    }

    protected StatefulKnowledgeSession getModelSession(String pmmlSource, boolean verbose) {

//        PMML4Compiler compiler = new PMML4Compiler();
//        String theory = compiler.compile(pmmlSource);
//
//        if (verbose)
//            dump(theory,System.out);



//        KnowledgeAgent kAgent = KnowledgeAgentFactory.newKnowledgeAgent("PmmlAgent");
//        kAgent.monitorResourceChangeEvents(true);
//        ChangeSetImpl changeSet = (ChangeSetImpl) ((KnowledgeAgentImpl) kAgent).getChangeSet(ResourceFactory.newClassPathResource("changeset.xml"));
//
//        InternalResource model = (InternalResource) ResourceFactory.newFileResource(pmmlSource);
//                model.setResourceType(ResourceType.PMML);
//        changeSet.getResourcesAdded().add(model);



//        kAgent.applyChangeSet(changeSet);
//
//        StatefulKnowledgeSession ksession = kAgent.getKnowledgeBase().newStatefulKnowledgeSession();

//        KnowledgeBuilder builder = new KnowledgeBui
//        assertNotNull(ksession);
//
//        return ksession;


		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

		    kbuilder.add(ResourceFactory.newClassPathResource("org/drools/pmml_4_0/changeset.xml"), ResourceType.CHANGE_SET);
//            kbuilder.add(ResourceFactory.newClassPathResource("Active.drl", Questionnaire.class),ResourceType.DRL);


        if (! verbose) {
            kbuilder.add(ResourceFactory.newClassPathResource("org/drools/pmml_4_0/"+pmmlSource),ResourceType.PMML);
        } else {
            PMML4Compiler compiler = new PMML4Compiler();
            try {
                String src = compiler.compile(ResourceFactory.newClassPathResource("org/drools/pmml_4_0/" + pmmlSource).getInputStream());
                System.out.println(src);
                kbuilder.add(ResourceFactory.newByteArrayResource(src.getBytes()),ResourceType.DRL);
            } catch (IOException e) {
                fail(e.getMessage());
            }
        }


		KnowledgeBuilderErrors errors = kbuilder.getErrors();
		if (errors.size() > 0) {
			for (KnowledgeBuilderError error: errors) {
				System.err.println(error);
			}
			throw new IllegalArgumentException("Could not parse knowledge.");
		}
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
            conf.setEventProcessingMode(EventProcessingOption.STREAM);
            //conf.setConflictResolver(LifoConflictResolver.getInstance());
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(conf);
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		return kbase.newStatefulKnowledgeSession();

	}






     protected StatefulKnowledgeSession getSession(String theory) {
        KnowledgeBase kbase = readKnowledgeBase(new ByteArrayInputStream(theory.getBytes()));
        return kbase != null ? kbase.newStatefulKnowledgeSession() : null;
     }

     protected void refreshKSession() {
         if (getKSession() != null)
             getKSession().dispose();
         setKSession(getKbase().newStatefulKnowledgeSession());
     }








    private static KnowledgeBase readKnowledgeBase(InputStream theory) {
        return readKnowledgeBase(Arrays.asList(theory));
    }

    private static KnowledgeBase readKnowledgeBase(List<InputStream> theory) {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
		for (InputStream is : theory)
            kbuilder.add(ResourceFactory.newInputStreamResource(is), ResourceType.DRL);
		KnowledgeBuilderErrors errors = kbuilder.getErrors();
		if (errors.size() > 0) {
			for (KnowledgeBuilderError error: errors) {
				System.err.println(error);
			}
			throw new IllegalArgumentException("Could not parse knowledge.");
		}
        RuleBaseConfiguration conf = new RuleBaseConfiguration();
            conf.setEventProcessingMode(EventProcessingOption.STREAM);
            //conf.setConflictResolver(LifoConflictResolver.getInstance());
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase(conf);
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		return kbase;
	}

    public String reportWMObjects(StatefulKnowledgeSession session) {
        PriorityQueue<String> queue = new PriorityQueue<String>();
        for (FactHandle fh : session.getFactHandles()) {
            Object o;
            if (fh instanceof EventFactHandle) {
                EventFactHandle efh = (EventFactHandle) fh;
                queue.add("\t " + efh.getStartTimestamp() + "\t" + efh.getObject().toString() + "\n");
            } else {
                o = ((DefaultFactHandle) fh).getObject();
                queue.add("\t " + o.toString() + "\n");
            }

        }
        String ans = " ---------------- WM " + session.getObjects().size() + " --------------\n";
            while (! queue.isEmpty())
               ans += queue.poll();
        ans += " ---------------- END WM -----------\n";
        return ans;
    }










	private void dump(String s, OutputStream ostream) {
		// write to outstream
		Writer writer = null;
		try {
			writer = new OutputStreamWriter(ostream, "UTF-8");
			writer.write(s);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
                if (writer != null) {
                    writer.flush();
                }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}










    public StatefulKnowledgeSession getKSession() {
        return kSession;
    }

    public void setKSession(StatefulKnowledgeSession kSession) {
        this.kSession = kSession;
    }

    public KnowledgeBase getKbase() {
        return kbase;
    }

    public void setKbase(KnowledgeBase kbase) {
        this.kbase = kbase;
    }






    protected void checkFirstDataFieldOfTypeStatus(FactType type,boolean valid, boolean missing, String ctx, Object... target) {
        Class<?> klass = type.getFactClass();
        Iterator iter = getKSession().getObjects(new ClassObjectFilter(klass)).iterator();
        Object obj = iter.next();
            if (ctx == null) {
                while (type.get(obj,"context") != null && iter.hasNext())
                    obj = iter.next();
            } else {
                while ( (! ctx.equals(type.get(obj,"context"))) && iter.hasNext())
                    obj = iter.next();
            }
                    assertEquals(target[0], type.get(obj, "value"));
                    assertEquals(valid, type.get(obj, "valid"));
                    assertEquals(missing, type.get(obj, "missing"));

    }


    protected double queryDoubleField(String target, String modelName) {
        QueryResults results = getKSession().getQueryResults(target,modelName);
        assertEquals(1, results.size());

        return (Double) results.iterator().next().get("result");
    }


    protected double queryIntegerField(String target, String modelName) {
        QueryResults results = getKSession().getQueryResults(target,modelName);
        assertEquals(1, results.size());

        return (Integer) results.iterator().next().get("result");
    }


    protected String queryStringField(String target, String modelName) {
        QueryResults results = getKSession().getQueryResults(target,modelName);
        assertEquals(1, results.size());

        return (String) results.iterator().next().get("result");
    }



}
