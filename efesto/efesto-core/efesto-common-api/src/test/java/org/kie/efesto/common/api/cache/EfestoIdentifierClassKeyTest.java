package org.kie.efesto.common.api.cache;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

import static org.assertj.core.api.Assertions.assertThat;

class EfestoIdentifierClassKeyTest {

    @Test
    void sameIdentifierSameClass() {
        String path = "/example/some-id/instances/some-instance-id";
        LocalUri parsed = LocalUri.parse(path);
        ModelLocalUriId modelLocalUriId1 = new ModelLocalUriId(parsed);
        ModelLocalUriId modelLocalUriId2 = new ModelLocalUriId(parsed);
        EfestoClassKey keyListString1 = new EfestoClassKey(List.class, String.class);
        EfestoClassKey keyListString2 = new EfestoClassKey(List.class, String.class);
        EfestoIdentifierClassKey identifierKeyListString1 = new EfestoIdentifierClassKey(modelLocalUriId1,
                                                                                         keyListString1);
        EfestoIdentifierClassKey identifierKeyListString2 = new EfestoIdentifierClassKey(modelLocalUriId2,
                                                                                         keyListString2);
        assertThat(identifierKeyListString1).isEqualTo(identifierKeyListString2);
    }

    @Test
    void sameIdentifierDifferentClass() {
        String path = "/example/some-id/instances/some-instance-id";
        LocalUri parsed = LocalUri.parse(path);
        ModelLocalUriId modelLocalUriId1 = new ModelLocalUriId(parsed);
        ModelLocalUriId modelLocalUriId2 = new ModelLocalUriId(parsed);
        EfestoClassKey keyListString = new EfestoClassKey(List.class, String.class);
        EfestoClassKey keyArrayListString = new EfestoClassKey(ArrayList.class, String.class);
        EfestoIdentifierClassKey identifierKeyListString1 = new EfestoIdentifierClassKey(modelLocalUriId1,
                                                                                         keyListString);
        EfestoIdentifierClassKey identifierKeyListString2 = new EfestoIdentifierClassKey(modelLocalUriId2,
                                                                                         keyArrayListString);
        assertThat(identifierKeyListString1).isNotEqualTo(identifierKeyListString2);
    }

    @Test
    void differentIdentifierSameClass() {
        String path1 = "/example/some-id/instances/some-instance-id";
        LocalUri parsed1 = LocalUri.parse(path1);
        ModelLocalUriId modelLocalUriId1 = new ModelLocalUriId(parsed1);
        String path2 = "/example/some-id/instances/another-instance-id";
        LocalUri parsed2 = LocalUri.parse(path2);
        ModelLocalUriId modelLocalUriId2 = new ModelLocalUriId(parsed2);
        EfestoClassKey keyListString1 = new EfestoClassKey(List.class, String.class);
        EfestoClassKey keyListString2 = new EfestoClassKey(List.class, String.class);
        EfestoIdentifierClassKey identifierKeyListString1 = new EfestoIdentifierClassKey(modelLocalUriId1,
                                                                                         keyListString1);
        EfestoIdentifierClassKey identifierKeyListString2 = new EfestoIdentifierClassKey(modelLocalUriId2,
                                                                                         keyListString2);
        assertThat(identifierKeyListString1).isNotEqualTo(identifierKeyListString2);
    }
}