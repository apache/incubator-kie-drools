package org.drools.core.metadata;

public interface MetadataHolder<T extends MetadataHolder> extends Metadatable<T> {

	public MetadataContainer<T> get_();
	
}
