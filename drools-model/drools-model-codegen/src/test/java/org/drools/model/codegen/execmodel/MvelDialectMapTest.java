package org.drools.model.codegen.execmodel;

import java.util.ArrayList;
import java.util.List;

import org.drools.model.codegen.execmodel.domain.Person;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class MvelDialectMapTest extends BaseModelTest {

    public MvelDialectMapTest(RUN_TYPE testRunType ) {
        super( testRunType );
    }


    @Test
    public void testMapAccessorWithBind() {
        final String drl = "" +
                "import java.util.*;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "\n" +
                "dialect \"mvel\"\n" +
                "\n" +
                "rule \"rule1\"\n" +
                "  when\n" +
                "    m: Person($i : itemsString[\"key1\"])" +
                "\n" +
                "  then\n" +
                "   results.add($i.length());" +
                "end";

        KieSession ksession = getKieSession(drl);

        List<Integer> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Person p = new Person("Luca");
        p.getItemsString().put("key1", "item1");
        p.getItemsString().put("key2", "item2");

        ksession.insert(p);

        assertThat(ksession.fireAllRules()).isEqualTo(1);

        assertThat(results).containsExactly(5); // item1.length()
    }

    @Test
    public void testMapAccessorWithBindFieldAccessor() {
        final String drl = "" +
                "import java.util.*;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "\n" +
                "dialect \"mvel\"\n" +
                "\n" +
                "rule \"rule1\"\n" +
                "  when\n" +
                "    m: Person($childName : childrenMap[\"Leonardo\"].name)" +
                "\n" +
                "  then\n" +
                "   results.add($childName.length());" +
                "end";

        KieSession ksession = getKieSession(drl);

        List<Integer> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Person luca = new Person("Luca");
        Person leonardo = new Person("Leonardo").setAge(3);
        Person andrea = new Person("Andrea").setAge(0);
        luca.getChildrenMap().put(leonardo.getName(), leonardo);
        luca.getChildrenMap().put(andrea.getName(), andrea);

        ksession.insert(luca);

        assertThat(ksession.fireAllRules()).isEqualTo(1);

        assertThat(results).containsExactly(8); // Leonardo.length()
    }

  }
