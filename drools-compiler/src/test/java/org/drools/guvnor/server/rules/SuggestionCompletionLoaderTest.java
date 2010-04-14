package org.drools.guvnor.server.rules;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import org.drools.guvnor.client.modeldriven.FactTypeFilter;
import org.drools.guvnor.client.modeldriven.ModelField.FIELD_CLASS_TYPE;

import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.lang.descr.ImportDescr;

public class SuggestionCompletionLoaderTest extends TestCase {

    public void testSuggestionCompLoader() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.Person", new ArrayList(), new ArrayList() );
        assertNotNull(eng);

    }

    public void testSuggestionCompLoaderWithExtraImportProviders() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        loader.addExternalImportDescrProvider(new SuggestionCompletionLoader.ExternalImportDescrProvider() {

            public Set<ImportDescr> getImportDescrs() {
                return new HashSet<ImportDescr>(){
                    {
                        add(new ImportDescr("java.util.List"));
                        add(new ImportDescr("java.util.Set"));
                    }
                };
            }
        });
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.Person", new ArrayList(), new ArrayList() );
        assertNotNull(eng);

        assertEquals(3, eng.getFactTypes().length);
        List<String> factTypes = Arrays.asList(eng.getFactTypes());
        assertTrue(factTypes.contains("List"));
        assertTrue(factTypes.contains("Set"));
        assertTrue(factTypes.contains("Person"));

        eng = loader.getSuggestionEngine( "package foo \n import org.drools.Person \n declare GenBean \n   id: int \n name : String \n end \n declare GenBean2 \n list: java.util.List \n gb: GenBean \n end", new ArrayList(), new ArrayList());
        assertEquals(5, eng.getFactTypes().length);
        factTypes = Arrays.asList(eng.getFactTypes());
        assertTrue(factTypes.contains("List"));
        assertTrue(factTypes.contains("Set"));
        assertTrue(factTypes.contains("Person"));
        assertTrue(factTypes.contains("GenBean"));
        assertTrue(factTypes.contains("GenBean2"));

    }

    public void testSuggestionCompLoaderWithExtraImportProvidersAndFilters() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        loader.addExternalImportDescrProvider(new SuggestionCompletionLoader.ExternalImportDescrProvider() {

            public Set<ImportDescr> getImportDescrs() {
                return new HashSet<ImportDescr>(){
                    {
                        add(new ImportDescr("java.util.List"));
                        add(new ImportDescr("java.util.Set"));
                    }
                };
            }
        });
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.Person \n declare GenBean \n   id: int \n name : String \n end \n declare GenBean2 \n list: java.util.List \n gb: GenBean \n end", new ArrayList(), new ArrayList());
        eng.setFactTypeFilter(new FactTypeFilter() {

            public boolean filter(String originalFact) {
                return originalFact.equals("List") || originalFact.equals("GenBean2");
            }
        });
        eng.setFilteringFacts(true);

        assertNotNull(eng);

        assertEquals(3, eng.getFactTypes().length);
        List<String> factTypes = Arrays.asList(eng.getFactTypes());
        assertTrue(factTypes.contains("Set"));
        assertTrue(factTypes.contains("Person"));
        assertTrue(factTypes.contains("GenBean"));

    }

    public void testSuggestionCompLoaderWildcards() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        loader.getSuggestionEngine( "package foo \n import org.drools.*", new ArrayList(), new ArrayList() );
        assertEquals(1, loader.getErrors().size());
        String err = loader.getErrors().get(0);
        assertTrue(err.startsWith("Unable"));
    }

    public void testLoadDifferentFieldTypes() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.guvnor.server.rules.SomeFact", new ArrayList(), new ArrayList() );
        assertNotNull(eng);

        assertEquals(SuggestionCompletionEngine.TYPE_NUMERIC, eng.getFieldType( "SomeFact", "age" ));
        assertEquals(SuggestionCompletionEngine.TYPE_STRING, eng.getFieldType( "SomeFact", "likes"));
        assertEquals(SuggestionCompletionEngine.TYPE_STRING, eng.getFieldType( "SomeFact","name"));
        assertEquals(SuggestionCompletionEngine.TYPE_NUMERIC, eng.getFieldType("SomeFact", "bigDecimal"));
        assertEquals(SuggestionCompletionEngine.TYPE_BOOLEAN, eng.getFieldType("SomeFact", "alive"));
//        assertEquals(SuggestionCompletionEngine.TYPE_COMPARABLE, eng.getFieldType( "SomeFact", "date"));
        assertEquals(SuggestionCompletionEngine.TYPE_DATE, eng.getFieldType( "SomeFact", "date"));
        assertEquals("Cheese", eng.getFieldType( "SomeFact", "cheese"));
        assertEquals(SuggestionCompletionEngine.TYPE_BOOLEAN, eng.getFieldType( "SomeFact", "dead"));
        assertEquals(SuggestionCompletionEngine.TYPE_BOOLEAN, eng.getFieldType( "SomeFact", "alive"));
        assertEquals(SuggestionCompletionEngine.TYPE_COLLECTION,eng.getFieldType( "SomeFact", "factList"));
        assertEquals("SomeFact",eng.getParametricFieldType("SomeFact", "factList"));
        assertEquals(SuggestionCompletionEngine.TYPE_COLLECTION,eng.getFieldType( "SomeFact", "factListString"));
        assertEquals("String",eng.getParametricFieldType("SomeFact", "factListString"));
    }

    public void testLoadDifferentMethodTypes() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.guvnor.server.rules.SomeFact", new ArrayList(), new ArrayList() );
        assertNotNull(eng);

        assertEquals(List.class.getName(), eng.getMethodClassType("SomeFact", "aMethod(int)"));
        assertEquals("SomeFact", eng.getParametricFieldType("SomeFact", "aMethod(int)"));
    }

    public void testGeneratedBeans() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n declare GenBean \n   id: int \n name : String \n end \n declare GenBean2 \n list: java.util.List \n gb: GenBean \n end", new ArrayList(), new ArrayList() );
        assertFalse(loader.hasErrors());
        assertNotNull(eng);

        assertEquals(2, eng.getFactTypes().length);
        assertEquals("GenBean", eng.getFactTypes()[0]);
        assertEquals("GenBean2", eng.getFactTypes()[1]);

        assertEquals(SuggestionCompletionEngine.TYPE_NUMERIC, eng.getFieldType( "GenBean", "id" ));
        assertEquals(SuggestionCompletionEngine.TYPE_STRING, eng.getFieldType( "GenBean", "name"));

        assertEquals("GenBean", eng.getFieldType("GenBean2", "gb"));
    }

    public void testGlobal() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n global org.drools.Person p", new ArrayList(), new ArrayList() );
        assertNotNull(eng);
        assertFalse(loader.hasErrors());

        assertEquals(1, eng.getGlobalVariables().length);
        assertEquals("p", eng.getGlobalVariables()[0]);
        assertEquals("Person", eng.getGlobalVariable("p"));
        String[] flds = (String[]) eng.getModelFields("Person");
        assertNotNull(flds);

        assertEquals(0, eng.getGlobalCollections().length);
    }

    public void testGlobalCollections() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n global java.util.List ls", new ArrayList(), new ArrayList() );
        assertNotNull(eng);
        assertFalse(loader.hasErrors());

        assertEquals(1, eng.getGlobalVariables().length);
        assertEquals("ls", eng.getGlobalVariables()[0]);
        assertEquals("List", eng.getGlobalVariable("ls"));

        assertNotNull(eng.getGlobalCollections());
        assertEquals(1, eng.getGlobalCollections().length);
        assertEquals("ls", eng.getGlobalCollections()[0]);
    }

    public void testSortOrderOfFields() throws Exception {

	    SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
	    SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.guvnor.server.rules.SomeFact", new ArrayList(), new ArrayList() );
	    assertNotNull(eng);

	    String[] fields = eng.getFieldCompletions("SomeFact");

	    assertEquals("age", fields[0]);
	    assertEquals("alive", fields[1]);
        assertEquals("anEnum", fields[2]);
	    assertEquals("bigDecimal", fields[3]);
    }

    public void testEnumFields() throws Exception {
	    SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
	    SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.guvnor.server.rules.SomeFact", new ArrayList(), new ArrayList() );
	    assertNotNull(eng);
        assertTrue(eng.hasDataEnumLists());
        assertEquals(eng.getDataEnumList("SomeFact.anEnum").length,3);
        String a[] = eng.getDataEnumList("SomeFact.anEnum") ;
        assertEquals(a[0],"0="+EnumClass.v1);
        assertEquals(a[1],"1="+EnumClass.v2);
        assertEquals(a[2],"2="+EnumClass.v3);
    }

    public void testSortOrderOfFacts() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.drools.guvnor.server.rules.SomeFact\n import org.drools.Person", new ArrayList(), new ArrayList() );
        assertNotNull(eng);
        String[] facts  = eng.getFactTypes();
        assertEquals(2, facts.length);

        assertEquals("Person", facts[0]);
        assertEquals("SomeFact", facts[1]);
    }

    public void testTypeDeclarations() throws Exception {
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader();

        String header ="";
        header += "package foo\n";

        header += "declare Applicant\n";
	header += "     creditRating: String\n";
	header += "     approved: Boolean\n";
	header += "     applicationDate: java.util.Date\n";
	header += "     age: Integer\n";
	header += "     name: String\n";
        header += "end\n";

        header += "declare LoanApplication\n";
	header += "     amount: Integer\n";
	header += "     approved: Boolean\n";
	header += "     deposit: Integer\n";
	header += "     approvedRate: Integer\n";
	header += "     lengthYears: Integer\n";
	header += "     explanation: String\n";
	header += "     insuranceCost: Integer\n";
	header += "     applicant: Applicant\n";
        header += "end\n";

        SuggestionCompletionEngine eng = loader.getSuggestionEngine( header, new ArrayList(), new ArrayList() );
        assertNotNull(eng);

        assertEquals(SuggestionCompletionEngine.TYPE_STRING, eng.getFieldType( "Applicant", "creditRating" ));
        assertEquals("java.lang.String", eng.getFieldClassName( "Applicant", "creditRating" ));
        assertEquals(FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS, eng.getFieldClassType( "Applicant", "creditRating" ));

        assertEquals(SuggestionCompletionEngine.TYPE_NUMERIC, eng.getFieldType( "LoanApplication", "deposit" ));
        assertEquals("java.lang.Integer", eng.getFieldClassName( "LoanApplication", "deposit" ));
        assertEquals(FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS, eng.getFieldClassType( "LoanApplication", "deposit" ));

        assertEquals("Applicant", eng.getFieldType( "LoanApplication", "applicant" ));
        assertNull(eng.getFieldClassName( "LoanApplication", "applicant" ));
        assertEquals(FIELD_CLASS_TYPE.TYPE_DECLARATION_CLASS, eng.getFieldClassType( "LoanApplication", "applicant" ));
    }

    public void testLoaderWithExistingClassloader() throws Exception {
        MockClassLoader mcl = new MockClassLoader();
        SuggestionCompletionLoader loader = new SuggestionCompletionLoader(mcl);
        SuggestionCompletionEngine eng = loader.getSuggestionEngine( "package foo \n import org.foo.Bar", new ArrayList(), new ArrayList() );
        assertNotNull(eng);
        //assertNotNull(eng.dataEnumLists);
        assertTrue(mcl.called);
    }

    static class MockClassLoader extends ClassLoader {

        public boolean called = false;

        public Class<?> loadClass(String name) {
            called = true;
            return Object.class;
        }

    }

}
