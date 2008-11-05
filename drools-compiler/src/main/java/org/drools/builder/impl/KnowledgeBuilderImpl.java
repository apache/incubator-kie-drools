package org.drools.builder.impl;

import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.builder.KnowledgeBuilder;
import org.drools.compiler.PackageBuilder;
import org.drools.definition.KnowledgePackage;
import org.drools.knowledge.definitions.impl.KnowledgePackageImp;
import org.drools.rule.Package;

public class KnowledgeBuilderImpl implements KnowledgeBuilder {
	private PackageBuilder pkgBuilder;
	
	public KnowledgeBuilderImpl(PackageBuilder pkgBuilder) {
		this.pkgBuilder = pkgBuilder;
	}

	public void addPackageFromDrl(URL url) {
        try {
            pkgBuilder.addPackageFromDrl(url);
        } catch (Exception e) {
            e.printStackTrace();
        }	    
	}
	
	public void addPackageFromDrl(Reader reader) {
		try {
			pkgBuilder.addPackageFromDrl(reader);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addPackageFromXml(URL url) {
        try {
            pkgBuilder.addPackageFromXml( url );
        } catch (Exception e) {
            e.printStackTrace();
        }	    
	}
	
	public void addPackageFromXml(Reader reader) {
		try {
			pkgBuilder.addPackageFromXml(reader);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void addProcessFromXml(URL url) {
	       try {
	            pkgBuilder.addProcessFromXml(url);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	}
	
	public void addProcessFromXml(Reader reader) {
		try {
			pkgBuilder.addProcessFromXml(reader);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Collection<KnowledgePackage> getKnowledgePackages() {
		Package[] pkgs = pkgBuilder.getPackages();
		List<KnowledgePackage> list = new ArrayList<KnowledgePackage>( pkgs.length );
		for ( Package pkg : pkgs ) {
			list.add( new KnowledgePackageImp( pkg ) );
		}
		return list;
	}
	
}
