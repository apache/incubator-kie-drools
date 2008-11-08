package org.drools.builder.impl;

import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeType;
import org.drools.compiler.PackageBuilder;
import org.drools.definition.KnowledgePackage;
import org.drools.knowledge.definitions.impl.KnowledgePackageImp;
import org.drools.rule.Package;

public class KnowledgeBuilderImpl implements KnowledgeBuilder {
	private PackageBuilder pkgBuilder;
	
	private StringBuilder dslStore;
	
	public KnowledgeBuilderImpl(PackageBuilder pkgBuilder) {
		this.pkgBuilder = pkgBuilder;
	}
	
	public void addResource(URL url, KnowledgeType type) {
        try {
    	    switch ( type ) {
    	        case DRL : {
    	            pkgBuilder.addPackageFromDrl( url );
    	            break;
    	        } 
    	        case DSLR : {
    	            break;
    	        }
    	        case DSL : {
    	            if ( dslStore == null ) {
    	                dslStore = new StringBuilder();
    	            }
    	            break;
    	        }
    	        case XDRL : {
    	            pkgBuilder.addPackageFromXml( url );
    	            break;
    	        }
    	        case DRF : {
    	            pkgBuilder.addProcessFromXml( url );
    	            break;
    	        }
    	        case XLS : {
    	            //pkgBuilder.
    	        }
    	    }
        } catch (Exception e) {
            throw new RuntimeException( e );
        }   
	}
	
	public void processDsl() {
	    
	}
	
	public void X() {
	    try {
            Class compiler = Class.forName( "org.drools.decisiontable.SpreadsheetCompiler" );
        } catch ( ClassNotFoundException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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

    public void addResorce(URL url,
                           KnowledgeType type) {
        // TODO Auto-generated method stub
        
    }

    public void addResource(Reader reader,
                            KnowledgeType type) {
        // TODO Auto-generated method stub
        
    }
	
}
