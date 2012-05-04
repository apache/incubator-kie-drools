/**
 * hacked from
 * https://eulergui.svn.sourceforge.net/svnroot/eulergui/trunk/eulergui/src/main/java/n3_project/helpers/Triple.java
 * , removing dependencies
 */
package org.drools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import net.sf.saxon.value.ObjectValue;
//
//import unif.TripleStoreDrools;
//import unif.UnexpectedTripleItemException;
//import deductions.Namespaces;
//import eulergui.inference.drools.impl.DroolsN3EngineRuntime;
//import eulergui.n3model.IValue;


/**
 * simple POJO class for an RDF Statement; the way is it used in
 * {@link eulergui.inference.drools.impl.N3SourcetoDrools} and
 * {@link TripleStoreDrools}, the 3 string fields must be set the N3 way, that
 * is: - all URI are between <> - all string literals are between "" - the rest
 * are other literals and are stored as is: numerics, true, false.
 * 
 * @author J.M. Vanel
 */
public class Triple implements Comparable<Triple>,
	Cloneable {
	private String subject;
	private String predicate;
	private String object;
//	private String objectLangOrDatatype;
	@SuppressWarnings("rawtypes")
	private List/*<String>*/ objectAsList;
	/** URL of the source (aka context) of this triple (quad) */
	private String source;
	@SuppressWarnings("rawtypes")
	private List subjectList;
//	private IValue objectIValue;
//	private List objectList;

	/** for javabeans */
	public Triple() {}
	
	public Triple(String subject, String predicate, String object) {
		this.subject = subject;
		this.predicate = predicate;
		this.object = object;
		// System.out.print(">>> inst. " + toString() );
	}

	public Triple(String subject, Object predicate, Object object) {
		this.subject = subject;
		this.predicate = (String) predicate;
		setObjectFromObject(object);
		// System.out.print(">>> inst. " + toString() );
	}

	public Triple( Object subject, String predicate, String object) {
		setSubject( subject);
		this.predicate = predicate;
		this.object = object;
		// System.out.print(">>> inst. " + toString() );
	}

	public Triple( Object subject, Object predicate, String object) {
		setSubject( subject);
		this.predicate = (String) predicate;
		this.object = object;
		// System.out.print(">>> inst. " + toString() );
	}

	@SuppressWarnings("rawtypes")
	public Triple(String subject, String predicate, List object) {
		this.subject = subject;
		this.predicate = predicate;
		this.objectAsList = object;
		// System.out.print(">>> inst. " + toString() );
	}

	public Triple( Object subject, String predicate, Object object ) {
		setSubject( subject);
		this.predicate = predicate;
		setObjectFromObject(object);
	}

	@SuppressWarnings("rawtypes")
	private void setObjectFromObject(Object object) {
		if ( object instanceof String) {
			this.object = n3Escape( (String) object );
		} else if( object instanceof List ) {
			this.objectAsList = (List)object;
		} else if( object instanceof Number ) {
			this.object = ((Number)object).toString();
		} else {
			System.out.println("Triple.setObjectFromObject(): not implemented: " + object + " (class " + object.getClass() + " )");
		}
	}

	public Object getSubject() {
		if( subjectList != null ) {
			return subjectList;
		}
//		Logger.getLogger("theDefault").info("Triple.getSubject() " +	subject	);
		return subject;
	}

	/* (non-Javadoc)
	 * @see n3_project.helpers.ITriple#getPredicate()
	 */
	public String getPredicate() {
		return predicate;
	}
	/* (non-Javadoc)
	 * @see n3_project.helpers.ITriple#setSubject(java.lang.Object)
	 */
	@SuppressWarnings("rawtypes")
	public void setSubject( Object subject) {
		if (subject instanceof String) {
			this.subject = n3Escape( (String) subject );
		} else if(subject instanceof List ) {
			this.subjectList = (List)subject; }
	}



	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}

	public String getObject() {
		if( object == null) {
			if( objectAsList == null) {
				return "";
			} else
				return toN3String(objectAsList);
		} else {
			return object; }
	}

	public void setObject(String object) {
		setObjectFromObject(object);
	}

	public void setObject( Object object) {
		setObjectFromObject(object);
	}


	public void setObject( boolean object) {
		this.object = Boolean.valueOf(object).toString();
	}
	public void setObject( int object) {
		this.object = Integer.valueOf(object).toString();
	}
	public void setObject( float object) {
		this.object = Float.valueOf(object).toString();
	}
	public void setObject( double object) {
		this.object = Double.valueOf(object).toString();
	}

	@Override
	public String toString() {
		return toEqualityString() +	"\n";
	}

	public String toEqualityString() {
		if( isObjectList() ) {
			final StringBuilder result = new StringBuilder();
			result
			.append(n3Escape(subject)).append(" ")
			.append(predicate).append(" ")
			.append( toN3String(objectAsList));
			return result.toString();

		} else {
//			return n3Escape(subject) + " " + predicate + " "
//				+ n3Escape(object) + " .";
			return (subject) + " " + predicate + " "
				+ (object)
				+ " .";
		}
	}
	/** regexp pattern to detect when it is really necessary to wrap the string with """ ,
	 * that is when it begins with " and there are several lines */
//	private static Pattern pattern = Pattern.compile( "\".*\\n.*", Pattern.DOTALL );
	private static Pattern patternEOLORQuotes = Pattern.compile( "\".*(\"|\\n).*\"", Pattern.DOTALL );
	private static Pattern patternNumber = Pattern.compile( "[+-]?\\d+(\\.\\d+)([edED]\\d+)?" );

	/**
	 * N3 escape strings and numbers: surround with """ to strings if necessary
	 * (if begins with ", not already done, and exists a new line in the string)
	 */
	public static String n3Escape( String n3Item) {
		if( n3Item == null ) {
			return null; }
		final Matcher matcher = patternEOLORQuotes.matcher(n3Item);
		final Matcher matcher2 = patternNumber.matcher(n3Item);

		if( n3Item.startsWith("\"")
				&& ! n3Item.startsWith("\"\"\"") // idempotent !
				&& matcher.matches() ) {
			return "\"\"" + n3Item + "\"\"";
		} else if( matcher2.matches() ) {
			// normalize numbers to Double
			return Double.toString( Double.parseDouble(n3Item) );
		}
		return n3Item;
	}

	@Override
	/* necessary to avoid inserting twice a triple */
	public boolean equals(Object obj) {
		if (obj instanceof Triple) {
			final Triple t = (Triple) obj;
			return t.toEqualityString().equals( this.toEqualityString() );
		}
		return false;
	}

	@Override
	public int hashCode() {
		return toEqualityString().hashCode();
	}

	public int compareTo(Triple t) {// private static int lastCreatedNodeNumber;

		return toString().compareTo(t.toString());
	}

	/** wrap given String In Quotes (mandatory for String values) */
	public static String wrapInQuotes(String oldValue) {
		return "\"" + oldValue + "\"";
	}

	/** wrap given String As an URI,
	 * except if it is a blank node;
	 * idempotent method */
	public static String wrapAsURI(String oldValue) {
		if( oldValue.charAt(0) == '<'
				|| oldValue.startsWith("_:") ) {
			return oldValue; }
		return "<" + oldValue + ">";
	}

//	/** wrap given relative Id As full N3 URI with given N3 prefix;
//	 * uses predefined prefixes in {@link Namespaces} */
//	public static String wrapAsURI(String prefix, String relativeId) {
//		return "<" +
//				Namespaces.prefixToId.get(prefix) +
//				relativeId + ">";
//	}

//	/** the reverse operation of preceding method {@link #wrapAsURI(String, String)} */
//	public static String extractN3RelativeId( String n3URI, String prefix ) {
//		final String uri = unwrapN3URI(n3URI);
//		final String namespaceURI = Namespaces.prefixToId.get(prefix);
//		return DroolsN3EngineRuntime.substringAfter(uri, namespaceURI);
//	}

	/** make full N3 URI with <> , from given base URI and relative Id */
	public static String makeURI(String base, String relativeId) {
		return "<" + base + relativeId + ">";
	}

	private static int lastCreatedNodeNumber;

	/**
	 * new Id , to instantiate a N3 blank node PENDING: no thread safe, no way
	 * to reinitialize the counter lastCreatedNodeNumber
	 * */
	public static String resource(String hint) {
		return "_:sk_" + hint + "_" + lastCreatedNodeNumber++;
	}

	public static String unwrapQuotes(String s ) {
		if( s.startsWith("\"") ) {
			return s.substring(1).replaceFirst("\"$", "" ); }
		return s;
	}

	public static String unwrapN3URI(String s ) {
		if( s.startsWith("<") ) {
			return s.substring(1).replaceFirst(">$", "" ); }
		return s;
	}

//	@Override
//	/** this is on purpose: we want to "clone" the Triple through plain
//	 * shallow copy, but not copy as a derived type like TripleResult */
//	public Object clone() throws CloneNotSupportedException {
//		final ITriple clone = new Triple( (String) getSubject(), getPredicate(), getObject() );
//		//		return super.clone();
//		return clone;
//	}

	public boolean isObjectList() {
		return objectAsList != null;
	}

	@SuppressWarnings("unchecked")
	public List<String> getObjectAsList() {
		return objectAsList;
	}

	public void putObjectAsList(List<String> objectAsList) {
		object = null;
		this.objectAsList = objectAsList;
	}

	@SuppressWarnings({ "unchecked" })
	public void putObjectAsList( String[] objectAsList) {
		object = null;
		this.objectAsList = new ArrayList<String>();
		for (int i = 0; i < objectAsList.length; i++) {
			final String string = objectAsList[i];
			this.objectAsList.add(string);
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static String toN3String( List/*<String>*/ list ) {
		if( list == null ) {
			return null; }
//
		final StringBuilder result = new StringBuilder( "( ");
//		for (String string : list) {
//			result.append( string ).append(" ");
//		}
//		result.append(" )");
//		return result.toString();

	//result.append("( ");
		final List<String> l = list;
		if( l.size() > 0 ) {
			final Object o = l.iterator().next();
			if( o instanceof Triple ) {
				// TODO when do we pass here ?
				final List<Triple> lt = list;
				for (Iterator iterator = lt.iterator(); iterator.hasNext();) {
					Triple triple = (Triple) iterator.next();
					result.append(triple.toEqualityString());
					if (iterator.hasNext()) {
						result.append(" ");
					}
				}
			} else {
				for (Iterator iterator = l.iterator(); iterator.hasNext();) {
					String string = (String) iterator.next();
					result.append(n3Escape(string));
					if (iterator.hasNext()) {
						result.append(" ");
					}
				}
			}
		}

		result.append(" )");
		return result.toString();
	}

//	/** unconditionally remove the surrounding " or """ ;
//	 * used at runtime by Drools N3 engine */
//	public static String n3ValueToNumeric(Object n3Value) {
//		String result = n3Value.toString();
//		if( result.startsWith("\"")) {
////			result = result.replaceFirst("^\"+", "").replaceFirst("\"+$", "");
//			result = result.replaceFirst("^\"+", "").replaceFirst("\".*$", "");
//		}
//		try {
////			Integer.parseInt(result);
//			Double.parseDouble(result);
//		} catch (final NumberFormatException e) {
//			e.printStackTrace();
//			final UnexpectedTripleItemException unexpectedTripleItemException
//			= new UnexpectedTripleItemException(e);
//			throw unexpectedTripleItemException;
//		}
//		return result;
//	}

	/** a hack, as the generated Drools code is currently :
	 * Integer.parseInt( Triple.n3ValueToNumeric( $CHILDREN_SIZE_ )) */
	public static String n3ValueToNumeric( int n3Value) {
//		return Integer.toString( n3Value );
		return Double.toString( n3Value );
	}

	public static String wrapAsBlankNode(String Id) {
		return "_:" + Id;
	}

    public static String wrapAsLong(long time) {
		return '"' + Long.toString(time) + '"' + "^^xsd:long";
    }

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	public boolean isQuad() {
		return source != null;
	}
}
