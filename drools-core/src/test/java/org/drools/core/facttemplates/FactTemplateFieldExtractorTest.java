package org.drools.core.facttemplates;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.facttemplates.Fact;
import org.drools.base.facttemplates.FactTemplate;
import org.drools.base.facttemplates.FactTemplateFieldExtractor;
import org.drools.base.facttemplates.FactTemplateImpl;
import org.drools.base.facttemplates.FactTemplateObjectType;
import org.drools.base.facttemplates.FieldTemplate;
import org.drools.base.facttemplates.FieldTemplateImpl;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.accessor.ReadAccessor;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FactTemplateFieldExtractorTest {
    @Test
    public void testExtractor() {
        InternalKnowledgePackage pkg = CoreComponentFactory.get().createKnowledgePackage( "org.store" );

        final FieldTemplate cheeseName = new FieldTemplateImpl( "name", String.class );
        final FieldTemplate cheesePrice = new FieldTemplateImpl( "price", Integer.class );
        final FieldTemplate[] fields = new FieldTemplate[]{cheeseName, cheesePrice};
        final FactTemplate cheese = new FactTemplateImpl( pkg, "Cheese", fields );

        final ReadAccessor extractName = new FactTemplateFieldExtractor( cheese, "name" );
        final ReadAccessor extractPrice = new FactTemplateFieldExtractor( cheese, "price" );

        final Fact stilton = cheese.createFact();
        stilton.set( "name", "stilton" );
        stilton.set( "price", 200 );

        assertThat(extractName.getValue(null, stilton)).isEqualTo("stilton");

        assertThat(extractPrice.getValue(null, stilton)).isEqualTo(200);

        assertThat(extractName.isNullValue(null, stilton)).isFalse();
        
        stilton.set( "name", null );

        assertThat(extractName.isNullValue(null, stilton)).isTrue();
        assertThat(extractPrice.isNullValue(null, stilton)).isFalse();
        
        final Fact brie = cheese.createFact();
        brie.set( "name", "brie" );
        brie.set( "price", 55 );

        assertThat(extractName.getValue(null, brie)).isEqualTo("brie");

        assertThat(extractPrice.getValue(null, brie)).isEqualTo(55);

        assertThat(extractName.isNullValue(null, brie)).isFalse();
        
        brie.set( "name", null );

        assertThat(extractName.isNullValue(null, brie)).isTrue();
        assertThat(extractPrice.isNullValue(null, stilton)).isFalse();
    }

    @Test
    public void testDeclaration() {
        InternalKnowledgePackage pkg = CoreComponentFactory.get().createKnowledgePackage( "org.store" );

        final FieldTemplate cheeseName = new FieldTemplateImpl( "name", String.class );
        final FieldTemplate cheesePrice = new FieldTemplateImpl( "price", Integer.class );
        final FieldTemplate[] fields = new FieldTemplate[]{cheeseName, cheesePrice};
        final FactTemplate cheese = new FactTemplateImpl( pkg,
                                                          "Cheese",
                                                          fields );

        final ReadAccessor extractName = new FactTemplateFieldExtractor( cheese, "name" );

        final Pattern pattern = new Pattern( 0,
                                          new FactTemplateObjectType( cheese ) );

        final Declaration declaration = new Declaration( "typeOfCheese",
                                                         extractName,
                                                         pattern );

        final Fact brie = cheese.createFact();
        brie.set( "name", "brie" );
        brie.set( "price", 55 );

        // Check we can extract Declarations correctly 
        assertThat(declaration.getValue(null, brie)).isEqualTo("brie");
    }
}
