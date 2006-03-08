package org.drools.decisiontable.model;


/*
 * Copyright 2005 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a registered trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company.
 * (http://drools.werken.com/).
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */



import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale </a>
 * 
 * Represents a rule.
 */
public class Rule extends DRLElement
    implements
    DRLJavaEmitter
{

    private static final int MAX_ROWS = 65535; 
    private Integer          _salience; //Integer as it may be null
    private String           _name;
    private Duration		 _duration; // RIK: New variable to the Rule class (Defines a Duration tag for the rule)
    private String           _description; // RIK: New variable to the Rule class (Set the description parameter of the rule tag)
    private String           _noLoop; // RIK: New variable to the Rule class (Set the no-loop parameter of the rule tag)
    private String           _xorGroup; // RIK: New variable to the Rule class (Set the xor-group parameter of the rule tag)
    private List             _lhs;
    private List             _rhs;
	private int 			 _spreadsheetRow;

    public Rule(String name,
                Integer salience, int spreadsheetRow)
    {
        _name = name;
        _salience = salience;
        _description = "";

        _lhs = new LinkedList( );
        _rhs = new LinkedList( );
        _spreadsheetRow = spreadsheetRow;
    }


    public void addCondition(Condition con)
    {
        _lhs.add( con );
    }

    public void addConsequence(Consequence con)
    {
        _rhs.add( con );
    }

    /*
     * (non-Javadoc)
     * 
     * @see mdneale.drools.xls.model.DRLJavaEmitter#toXML()
     */
    public String toXML()
    {
        String xml = "<!--" + getComment( ) + "-->\n";
        xml = xml + "<rule name=\"" + _name + "\"";
        if (_description != null) {
            xml = xml + " description=\"" + _description + "\"";
        }
        if (_salience != null) {
            xml = xml + " salience=\"" + _salience.toString() + "\"";
        }
        if (_xorGroup != null) {
            xml = xml + " xor-group=\"" + _xorGroup + "\"";
        }
        if (_noLoop != null) {
            xml = xml + " no-loop=\"" + _noLoop + "\"";
        }        
        xml = xml + ">\n";
        
        xml = xml + generateXml( _lhs );
        if (_duration != null) // Add the XML of the duration variable to the Rule's XML  
        {
        	xml = xml + _duration.toXML();
        }
        xml = xml + "\t<java:consequence><![CDATA[ \n";
        xml = xml + generateXml( _rhs );
        xml = xml + "\t ]]></java:consequence>";
        xml = xml + "\n" + "</rule>\n\n";
        return xml;
    }

    /**
     * Uses the JavaEmitter interface to generate the xml.
     */
    private String generateXml(List list)
    {
        StringBuffer buf = new StringBuffer( );
        for ( Iterator it = list.iterator( ); it.hasNext( ); )
        {
            DRLJavaEmitter emitter = (DRLJavaEmitter) it.next( );
            buf.append( emitter.toXML( ) );
        }
        return buf.toString( );
    }

    public static int calcSalience(int rowNumber)
    {
        if ( rowNumber > MAX_ROWS )
        {
            throw new IllegalArgumentException( "That row number is above the max: " + MAX_ROWS );
        }
        return MAX_ROWS - rowNumber;
    }

    /**
     * @param col -
     *            the column number. Start with zero.
     * @return The spreadsheet name for this col number, such as "AA" or "AB" or
     *         "A" and such and such.
     */
    public static String convertColNumToColName(int i)
    {

        String result;
        int div = i / 26;
        int mod = i % 26;

        if ( div == 0 )
        {
            byte[] c = new byte[1];
            c[0] = (byte) (mod + 65);
            result = byteToString( c );
        }
        else
        {
            byte[] firstChar = new byte[1];
            firstChar[0] = (byte) ((div - 1) + 65);

            byte[] secondChar = new byte[1];
            secondChar[0] = (byte) (mod + 65);
            String first = byteToString( firstChar );
            String second = byteToString( secondChar );
            result = first + second;
        }
        return result;

    }

    private static String byteToString(byte[] secondChar)
    {
        try
        {
            return new String( secondChar,
                               "UTF-8" );
        }
        catch ( UnsupportedEncodingException e )
        {
            throw new RuntimeException( "Unable to convert char to string.",
                                        e );
        }
    }

    public List getConditions()
    {
        return _lhs;
    }

    public List getConsequences()
    {
        return _rhs;
    }

    public void setSalience(Integer value) // Set the salience of the rule
    {
        _salience = value;
    }

    public Integer getSalience()
    {
        return _salience;
    }

    public void setName(String value) // Set the name of the rule
    {
        _name = value;
    }

    public String getName()
    {
        return _name;
    }
    
    public void setDescription(String value) // Set the description of the rule
    {
        _description = value;
    }

    public void appendDescription(String value) // Set the description of the rule
    {
        _description += value;
    }

    public String getDescription()
    {
        return _description;
    }

    public void setDuration(Duration value) // Set the duration of the rule
    {
    	_duration=value;
    }

    public String getDuration()
    {
    	return _duration.getSnippet();
    }
    
    public void setXorGroup(String value) // Set the duration of the rule
    {
    	_xorGroup=value;
    }

    public String getXorGroup()
    {
    	return _xorGroup;
    }

    public void setNoLoop(String value) // Set the no-loop attribute of the rule
    {
    	_noLoop=value;
    }
    
    public boolean getNoLoop()
    {
    	String value="false";
    	if (_noLoop.compareTo("true")!=0)
    		value=_noLoop;
    	Boolean b=new Boolean(value);
    	return b.booleanValue();
    }
}

