package org.kie.dmn.core.compiler;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.xml.namespace.QName;

import org.junit.Test;
import org.kie.dmn.feel.util.Either;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.Import;
import org.kie.dmn.model.v1_1.TImport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ImportDMNResolverUtilTest {

    @Test
    public void testNSonly() {
        final Import i = makeImport("ns1", null, null);
        final List<QName> available = Arrays.asList(new QName("ns1", "m1"),
                                                    new QName("ns2", "m2"),
                                                    new QName("ns3", "m3"));
        final Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isRight()).isTrue();
        assertThat(result.getOrElse(null)).isEqualTo(new QName("ns1", "m1"));
    }

    @Test
    public void testNSandModelName() {
        final Import i = makeImport("ns1", null, "m1");
        final List<QName> available = Arrays.asList(new QName("ns1", "m1"),
                                                    new QName("ns2", "m2"),
                                                    new QName("ns3", "m3"));
        final Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isRight()).isTrue();
        assertThat(result.getOrElse(null)).isEqualTo(new QName("ns1", "m1"));
    }

    @Test
    public void testNSandModelNameWithAlias() {
        final Import i = makeImport("ns1", "aliased", "m1");
        final List<QName> available = Arrays.asList(new QName("ns1", "m1"),
                                                    new QName("ns2", "m2"),
                                                    new QName("ns3", "m3"));
        final Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isRight()).isTrue();
        assertThat(result.getOrElse(null)).isEqualTo(new QName("ns1", "m1"));
    }

    @Test
    public void testNSnoModelNameWithAlias() {
        final Import i = makeImport("ns1", "mymodel", null);
        final List<QName> available = Arrays.asList(new QName("ns1", "m1"),
                                                    new QName("ns2", "m2"),
                                                    new QName("ns3", "m3"));
        final Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isRight()).isTrue();
        assertThat(result.getOrElse(null)).isEqualTo(new QName("ns1", "m1"));
    }

    @Test
    public void testNSandUnexistentModelName() {
        final Import i = makeImport("ns1", null, "boh");
        final List<QName> available = Arrays.asList(new QName("ns1", "m1"),
                                                    new QName("ns2", "m2"),
                                                    new QName("ns3", "m3"));
        final Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isLeft()).isTrue();
    }

    @Test
    public void testNSnoModelNameDefaultWithAlias2() {
        final Import i = makeImport("ns1", "boh", null);
        final List<QName> available = Arrays.asList(new QName("ns1", "m1"),
                                                    new QName("ns2", "m2"),
                                                    new QName("ns3", "m3"));
        final Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isRight()).isTrue();
        assertThat(result.getOrElse(null)).isEqualTo(new QName("ns1", "m1"));
    }

    @Test
    public void testLocateInNS() {
        final Import i = makeImport("nsA", null, "m1");
        final List<QName> available = Arrays.asList(new QName("nsA", "m1"),
                                                    new QName("nsA", "m2"),
                                                    new QName("nsB", "m3"));
        final Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isRight()).isTrue();
        assertThat(result.getOrElse(null)).isEqualTo(new QName("nsA", "m1"));
    }

    @Test
    public void testLocateInNSnoModelNameWithAlias() {
        final Import i = makeImport("nsA", "m1", null);
        final List<QName> available = Arrays.asList(new QName("nsA", "m1"),
                                                    new QName("nsA", "m2"),
                                                    new QName("nsB", "m3"));
        final Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isLeft()).isTrue();
    }

    @Test
    public void testLocateInNSAliased() {
        final Import i = makeImport("nsA", "aliased", "m1");
        final List<QName> available = Arrays.asList(new QName("nsA", "m1"),
                                                    new QName("nsA", "m2"),
                                                    new QName("nsB", "m3"));
        final Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isRight()).isTrue();
        assertThat(result.getOrElse(null)).isEqualTo(new QName("nsA", "m1"));
    }

    @Test
    public void testLocateInNSunexistent() {
        final Import i = makeImport("nsA", null, "boh");
        final List<QName> available = Arrays.asList(new QName("nsA", "m1"),
                                                    new QName("nsA", "m2"),
                                                    new QName("nsB", "m3"));
        final Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isLeft()).isTrue();
    }

    @Test
    public void testLocateInNSnoModelNameWithAlias2() {
        final Import i = makeImport("nsA", "boh", null);
        final List<QName> available = Arrays.asList(new QName("nsA", "m1"),
                                                    new QName("nsA", "m2"),
                                                    new QName("nsB", "m3"));
        final Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isLeft()).isTrue();
    }

    @Test
    public void testLocateInNSAliasedBadScenario() {
        // this is a BAD scenario are in namespace `nsA` there are 2 models with the same name.
        final Import i = makeImport("nsA", "aliased", "mA");
        final List<QName> available = Arrays.asList(new QName("nsA", "mA"),
                                                    new QName("nsA", "mA"),
                                                    new QName("nsB", "m3"));
        final Either<String, QName> result = ImportDMNResolverUtil.resolveImportDMN(i, available, Function.identity());
        assertThat(result.isLeft()).isTrue();
    }

    private Import makeImport(final String namespace, final String name, final String modelName) {
        final Import i = new TImport();
        i.setNamespace(namespace);
        final Map<QName, String> addAttributes = new HashMap<>();
        if (name != null) {
            addAttributes.put(TImport.NAME_QNAME, name);
        }
        if (modelName != null) {
            addAttributes.put(TImport.MODELNAME_QNAME, modelName);
        }
        i.setAdditionalAttributes(addAttributes);
        final Definitions definitions = mock(Definitions.class);
        definitions.setNamespace("ParentDMNNamespace");
        definitions.setName("ParentDMN");
        i.setParent(definitions);
        return i;
    }

}
