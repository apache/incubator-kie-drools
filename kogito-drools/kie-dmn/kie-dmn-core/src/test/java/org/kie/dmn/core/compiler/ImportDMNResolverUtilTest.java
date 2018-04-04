package org.kie.dmn.core.compiler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kie.dmn.feel.util.Either;
import org.kie.dmn.model.v1_1.Import;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ImportDMNResolverUtilTest {

    @Test
    public void testNSonly() {
        Import i = makeImport("ns1", null, null);
        List<QName> available = Arrays.asList(new QName("ns1", "m1"),
                                              new QName("ns2", "m2"),
                                              new QName("ns3", "m3"));
        Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertTrue(result.isRight());
        assertEquals(new QName("ns1", "m1"), result.getOrElse(null));
    }

    @Test
    public void testNSandModelName() {
        Import i = makeImport("ns1", null, "m1");
        List<QName> available = Arrays.asList(new QName("ns1", "m1"),
                                              new QName("ns2", "m2"),
                                              new QName("ns3", "m3"));
        Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertTrue(result.isRight());
        assertEquals(new QName("ns1", "m1"), result.getOrElse(null));
    }

    @Test
    public void testNSandModelNameWithAlias() {
        Import i = makeImport("ns1", "aliased", "m1");
        List<QName> available = Arrays.asList(new QName("ns1", "m1"),
                                              new QName("ns2", "m2"),
                                              new QName("ns3", "m3"));
        Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertTrue(result.isRight());
        assertEquals(new QName("ns1", "m1"), result.getOrElse(null));
    }

    @Test
    public void testNSnoModelNameWithAlias() {
        Import i = makeImport("ns1", "mymodel", null);
        List<QName> available = Arrays.asList(new QName("ns1", "m1"),
                                              new QName("ns2", "m2"),
                                              new QName("ns3", "m3"));
        Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertTrue(result.isRight());
        assertEquals(new QName("ns1", "m1"), result.getOrElse(null));
    }

    @Test
    public void testNSandUnexistentModelName() {
        Import i = makeImport("ns1", null, "boh");
        List<QName> available = Arrays.asList(new QName("ns1", "m1"),
                                              new QName("ns2", "m2"),
                                              new QName("ns3", "m3"));
        Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertTrue(result.isLeft());
    }

    @Test
    public void testNSnoModelNameDefaultWithAlias2() {
        Import i = makeImport("ns1", "boh", null);
        List<QName> available = Arrays.asList(new QName("ns1", "m1"),
                                              new QName("ns2", "m2"),
                                              new QName("ns3", "m3"));
        Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertTrue(result.isRight());
        assertEquals(new QName("ns1", "m1"), result.getOrElse(null));
    }

    @Test
    public void testLocateInNS() {
        Import i = makeImport("nsA", null, "m1");
        List<QName> available = Arrays.asList(new QName("nsA", "m1"),
                                              new QName("nsA", "m2"),
                                              new QName("nsB", "m3"));
        Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertTrue(result.isRight());
        assertEquals(new QName("nsA", "m1"), result.getOrElse(null));
    }

    @Test
    public void testLocateInNSnoModelNameWithAlias() {
        Import i = makeImport("nsA", "m1", null);
        List<QName> available = Arrays.asList(new QName("nsA", "m1"),
                                              new QName("nsA", "m2"),
                                              new QName("nsB", "m3"));
        Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertTrue(result.isLeft());
    }

    @Test
    public void testLocateInNSAliased() {
        Import i = makeImport("nsA", "aliased", "m1");
        List<QName> available = Arrays.asList(new QName("nsA", "m1"),
                                              new QName("nsA", "m2"),
                                              new QName("nsB", "m3"));
        Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertTrue(result.isRight());
        assertEquals(new QName("nsA", "m1"), result.getOrElse(null));
    }

    @Test
    public void testLocateInNSunexistent() {
        Import i = makeImport("nsA", null, "boh");
        List<QName> available = Arrays.asList(new QName("nsA", "m1"),
                                              new QName("nsA", "m2"),
                                              new QName("nsB", "m3"));
        Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertTrue(result.isLeft());
    }

    @Test
    public void testLocateInNSnoModelNameWithAlias2() {
        Import i = makeImport("nsA", "boh", null);
        List<QName> available = Arrays.asList(new QName("nsA", "m1"),
                                              new QName("nsA", "m2"),
                                              new QName("nsB", "m3"));
        Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertTrue(result.isLeft());
    }

    @Test
    public void testLocateInNSAliasedBadScenario() {
        // this is a BAD scenario are in namespace `nsA` there are 2 models with the same name.
        Import i = makeImport("nsA", "aliased", "mA");
        List<QName> available = Arrays.asList(new QName("nsA", "mA"),
                                              new QName("nsA", "mA"),
                                              new QName("nsB", "m3"));
        Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertTrue(result.isLeft());
    }

    private Import makeImport(String namespace, String name, String modelName) {
        Import i = new Import();
        i.setNamespace(namespace);
        Map<QName, String> addAttributes = new HashMap<>();
        if (name != null) {
            addAttributes.put(Import.NAME_QNAME, name);
        }
        if (modelName != null) {
            addAttributes.put(Import.MODELNAME_QNAME, modelName);
        }
        i.setAdditionalAttributes(addAttributes);
        return i;
    }

}
