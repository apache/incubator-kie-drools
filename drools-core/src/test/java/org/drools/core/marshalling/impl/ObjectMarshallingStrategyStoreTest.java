package org.drools.core.marshalling.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.drools.core.SessionConfiguration;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyAcceptor;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class ObjectMarshallingStrategyStoreTest {

	private class Thing{
		int id;
		String value;
		Thing( int id, String value ){
			this.id = id;
			this.value = value;
		}
		
		public boolean equals( Object thing )
		{
			return thing!= null && thing instanceof Thing && ((Thing)thing).id == this.id;
		}
		
		public String toString(){
			return "Thing:"+id+","+value;
		}
	}
	
	@Test 
	public void avoidMixingOfObjectMarshallingStrategiesOfTheSameClass() throws IOException, ClassNotFoundException {

		Environment env = EnvironmentFactory.newEnvironment();
		final Thing entityOne = new Thing( 1, "Object 1" );
		final Thing entityTwo = new Thing( 2, "Object 2" );
		
		Collection srcItems = new ArrayList();
		srcItems.add( entityOne ); 
		srcItems.add( entityTwo );
		
		ObjectMarshallingStrategy[] strats = new ObjectMarshallingStrategy[] {
				new IdentityPlaceholderResolverStrategy(new ObjectMarshallingStrategyAcceptor() {

					@Override
					public boolean accept(Object object) {
						return entityOne.equals(object);
					}
				}, Collections.singletonMap(entityOne.id, (Object) entityOne)),
				new IdentityPlaceholderResolverStrategy(new ObjectMarshallingStrategyAcceptor() {

					@Override
					public boolean accept(Object object) {
						return entityTwo.equals(object);
					}
				}, Collections.singletonMap(entityTwo.id, (Object) entityTwo)) };

		env.set(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, strats);

		KieSessionConfiguration ksc = SessionConfiguration.newInstance();

		final KieBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();

		kbconf.setOption(EventProcessingOption.STREAM);
		
		InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kbconf );

		KieSession ks = kbase.newKieSession( ksc, env);
		
		
		ks.insert( entityOne ); 
		ks.insert( entityTwo );
		
		ProtobufMarshaller marshaller = (ProtobufMarshaller) MarshallerFactory.newMarshaller(kbase, strats);
		
		// Serialize object
		final byte[] b1;
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			marshaller.marshall(bos, ks, System.currentTimeMillis());
			b1 = bos.toByteArray();
			bos.close();
		}

		// Deserialize object
		StatefulKnowledgeSession ksession2;
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(b1);
			try{
				ksession2 = marshaller.unmarshall(bais, ks.getSessionConfiguration(), ks.getEnvironment());
				Collection items = ksession2.getFactHandles();
				Assert.assertTrue( items.size() == 2 );
				for( Object item : items ){
					FactHandle factHandle = (FactHandle)item;
					Assert.assertTrue( srcItems.contains( ((DefaultFactHandle)factHandle).getObject() ) );
				}
			}catch( RuntimeException npe ){
				// Here ocurrs the bug that shows that NamedObjectMarshallingStrategies are required.
				Assert.fail( "This error only happens if identity ObjectMarshallingStrategy use old name" );
			}finally{
				bais.close();
			}
			
			
		}
	}

	@Test
	public void allowMixingOfNamedObjectMarshallingStrategiesWithTheSameClass()
			throws IOException, ClassNotFoundException {

		Environment env = EnvironmentFactory.newEnvironment();
		final Thing entityOne = new Thing( 1, "Object 1" );
		final Thing entityTwo = new Thing( 2, "Object 2" );
		final Thing entityThree = new Thing( 3, "Object 3" );
		
		Collection srcItems = new ArrayList();
		srcItems.add( entityOne ); 
		srcItems.add( entityTwo );
		srcItems.add( entityThree );
		
		
		ObjectMarshallingStrategy[] strats = new ObjectMarshallingStrategy[] {
				new NamedCustomMarshallingStrategy("ofObject1", new ObjectMarshallingStrategyAcceptor() {

					@Override
					public boolean accept(Object object) {
						return entityOne.equals(object);
					}
				}, Collections.singletonMap(entityOne.id, (Object) entityOne)),
				new NamedCustomMarshallingStrategy("ofObject2", new ObjectMarshallingStrategyAcceptor() {

					@Override
					public boolean accept(Object object) {
						return entityTwo.equals(object);
					}
				}, Collections.singletonMap(entityTwo.id, (Object) entityTwo)),
				
				new IdentityPlaceholderResolverStrategy(new ObjectMarshallingStrategyAcceptor() {

					@Override
					public boolean accept(Object object) {
						return entityThree.equals(object);
					}
				}, Collections.singletonMap(3, (Object) entityThree))  };

		env.set(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES, strats);

		KieSessionConfiguration ksc = SessionConfiguration.newInstance();

		final KieBaseConfiguration kbconf = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();

		kbconf.setOption(EventProcessingOption.STREAM);

		InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kbconf );

		KieSession ks = kbase.newKieSession( ksc, env);
		
		ks.insert( entityOne ); 
		ks.insert( entityTwo );
		ks.insert( entityThree );
		
		ProtobufMarshaller marshaller = null;
		marshaller = (ProtobufMarshaller) MarshallerFactory.newMarshaller(kbase, strats);

		// Serialize object
		final byte[] b1;
		{
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			marshaller.marshall(bos, ks, System.currentTimeMillis());
			b1 = bos.toByteArray();
			bos.close();
		}

		// Deserialize object
		StatefulKnowledgeSession ksession2;
		{
			ByteArrayInputStream bais = new ByteArrayInputStream(b1);
			ksession2 = marshaller.unmarshall(bais, ks.getSessionConfiguration(), ks.getEnvironment());
			bais.close();
			
			Collection items = ksession2.getFactHandles();
			for( Object item : items ){
				FactHandle factHandle = (FactHandle)item;
				//Here we can validate that using named ObjectMarshallingStrategies can read properly the serialized objects
				Assert.assertTrue( srcItems.contains( ((DefaultFactHandle)factHandle).getObject() ) );
			}
		}
	}
}
