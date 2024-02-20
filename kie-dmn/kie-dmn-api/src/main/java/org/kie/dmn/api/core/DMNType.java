/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.api.core;

import java.util.List;
import java.util.Map;

import org.kie.dmn.model.api.FunctionItem;
import org.kie.dmn.model.api.ItemDefinition;

/**
 * Represent a DMN type; in the vast majority of cases, this representation exists as a result of a DMN model specifying an {@link ItemDefinition}.<br/>
 * <br/>
 * <h1>Design document</h1>
 * A DMN type has a <code>namespace</code>; when a DMN type is a representation of an {@link ItemDefinition} defined in a DMN model, the namespace correspond to the model's namespace as per {@link DMNModel#getNamespace()}.
 * When the namespace is a FEEL reserved namespace such as <code>https://www.omg.org/spec/DMN/20191111/FEEL/</code> then the DMN type stands for an equivalent representation of a built-in FEEL type.<br/>
 * <br/>
 * To be noted FEEL built-in {@link org.kie.dmn.feel.lang.Type}(s) find an equivalent representation as a DMN type when the {@link DMNModel} is compiled.
 * This is by design, since the DMN layer of the Drools DMN open source engine is based <i>on top</i> of the FEEL layer.<br/>
 * <br/>
 * A DMN type has a <code>name</code>; this usually corresponds to the  {@link ItemDefinition}'s name (see: {@link ItemDefinition#getName()}).<br/>
 * <h1>Simple and Composite types</h1>
 * As per the DMN specification, a DMN type representing an <code>ItemDefinition</code> in a model, can be specified based on either:
 * <ul>
 * <li>a reference to a FEEL built-in or another <code>ItemDefinition</code> specified in the model; possibly restricted with {@link #getAllowedValues()}</li>
 * <li>a composition of <code>ItemDefinition</code>(s)</li>
 * <li>as a {@link FunctionItem}</li>
 * </ul>
 * <br/>
 * The difference between Simple and Composite types have large implication on a number of attributes.<br/>
 * <br/>
 * When specified <i>by reference</i>:
 * <ul>
 * <li>the {@link #isComposite()} will be <code>false</code>
 * <li>it is expected this will be an instance of {@link org.kie.dmn.core.impl.SimpleTypeImpl}
 * <li>call to {@link #getBaseType()} will be different than null
 * <li>call to {@link #getFields()} will be an empty collection
 * </ul>
 * <br/>
 * For example the following is a DMN Simple type, defining a type which is like a FEEL <code>string</code> but its values can only be a vowel:<br/>
 * <br/>
<pre>&lt;dmn:itemDefinition name=&quot;tVowel&quot;&gt;
  &lt;dmn:typeRef&gt;string&lt;/dmn:typeRef&gt;
  &lt;dmn:allowedValues&gt;
    &lt;dmn:text&gt;&quot;a&quot;, &quot;e&quot;, &quot;i&quot;, &quot;o&quot;, &quot;u&quot;&lt;/dmn:text&gt;
  &lt;/dmn:allowedValues&gt;
&lt;/dmn:itemDefinition&gt;</pre>
 * Another example of DMN Simple type, modeling a FEEL <code>list&lt;number&gt;</code>:<br/>
 * <br/>
<pre>&lt;dmn:itemDefinition name=&quot;tNumbers&quot; isCollection=&quot;true&quot;&gt;
  &lt;dmn:typeRef&gt;number&lt;/dmn:typeRef&gt;
&lt;/dmn:itemDefinition&gt;
</pre>
 * Another example of DMN Simple type, making reference to another <code>ItemDefinition</code> specified in the model, modeling a list of just vowels:<br/>
 * <br/>
<pre>&lt;dmn:itemDefinition name=&quot;tListOfVowels&quot; isCollection=&quot;true&quot;&gt;
  &lt;dmn:typeRef&gt;tVowel&lt;/dmn:typeRef&gt;
&lt;/dmn:itemDefinition&gt;</pre>
 * When instead specified <i>by composition</i>:
 * <ul>
 * <li>the {@link #isComposite()} will be <code>true</code>
 * <li>it is expected this will be an instance of {@link org.kie.dmn.core.impl.CompositeTypeImpl}
 * <li>calling {@link #getBaseType()} always return null.
 * <li>call to {@link #getFields()} will return the collection of fields representing this composition
 * </ul>
 * <br/>
 * For example the following is a DMN Composite type, consisting of two fields for <i>full name</i> and <i>age</i> respectively, equivalently representing FEEL type for <code>context&lt;full name: string, age: number&gt;</code>:<br/>
 * <br/>
<pre>&lt;dmn:itemDefinition name=&quot;tPerson&quot;&gt;
  &lt;dmn:itemComponent name=&quot;full name&quot;&gt;
    &lt;dmn:typeRef&gt;string&lt;/dmn:typeRef&gt;
  &lt;/dmn:itemComponent&gt;
  &lt;dmn:itemComponent name=&quot;age&quot;&gt;
    &lt;dmn:typeRef&gt;number&lt;/dmn:typeRef&gt;
  &lt;/dmn:itemComponent&gt;
&lt;/dmn:itemDefinition&gt;</pre>
 * <h1>isCollection</h1>
 * As per the DMN specification, setting {@link ItemDefinition#isIsCollection()} to <code>true</code>, indicates that the actual values defined by the type are collections of (allowed) values.<br/>
 * This is reflected in this DMN type {@link #isCollection()}.<br/>
 * <br/>
 * It is important to note that this attribute is orthogonal to the fact of being a Simple or Composite type; most of the time however for Methodology best-practices DMN collection types are simple types, that is, specified by reference.<br/>
 * <h1>allowedValues</h1>
 * As per the DMN specification, the {@link ItemDefinition#getAllowedValues()} attribute lists the possible values or ranges of values in the base type that are allowed in this ItemDefinition.
 * This is reflected in this DMN type {@link #getAllowedValues()}.<br/>
 * <br/>
 * It is important to note that attribute can only be present when the type is specified by reference.
 * <h1>typeConstraints</h1>
 * As per the DMN specification, the {@link ItemDefinition#getTypeConstraint()} ()} attribute lists the possible values or ranges of values in the base type that are allowed in this ItemDefinition.
 * This is reflected in this DMN type {@link #getTypeConstraint()}.<br/>
 * <br/>
 * It is important to note that attribute can only be present when the type is specified by reference.
 * <h1>getFields</h1>
 * Only when a type is specified by composition, {@link #getFields()} will return the collection of the fields which constitutes the composite type.<br/>
 * <br/>
 * For example in the following DMN Composite type, consisting of two fields for <i>full name</i> and <i>age</i> respectively:<br/>
 * <br/>
<pre>&lt;dmn:itemDefinition name=&quot;tPerson&quot;&gt;
  &lt;dmn:itemComponent name=&quot;full name&quot;&gt;
    &lt;dmn:typeRef&gt;string&lt;/dmn:typeRef&gt;
  &lt;/dmn:itemComponent&gt;
  &lt;dmn:itemComponent name=&quot;age&quot;&gt;
    &lt;dmn:typeRef&gt;number&lt;/dmn:typeRef&gt;
  &lt;/dmn:itemComponent&gt;
&lt;/dmn:itemDefinition&gt;</pre>
 * a call to {@link #getFields()} returns a collection of two entries:
 * <ul>
 * <li>field key <i>full name</i>, value the FEEL built-in type <code>string</code>
 * <li>field key <i>age</i>, value the FEEL built-in type <code>number</code>
 * </ul>
 * <h1>Anonymous inner types and TypeRegistry</h1>
 * When the DMN model specifies an {@link ItemDefinition} this gets compiled as a DMN type and it is registered in the model's {@link org.kie.dmn.core.impl.DMNModelImpl#getTypeRegistry()}.<br/>
 * <br/>
 * There is a special case where {@link ItemDefinition} defines an anonymous inner type, which naturally cannot be registered in the type registry since it does not have a globally valid and unique name.<br/>
 * This use-case be thought, in some respects, of being similar to Java's inner classes.<br/>
 * <br/>
 * For example in the following DMN Composite type:<br/>
 * <br/>
<pre>&lt;dmn:itemDefinition name=&quot;tPerson&quot;&gt;
  &lt;dmn:itemComponent name=&quot;full name&quot;&gt;
    &lt;dmn:typeRef&gt;string&lt;/dmn:typeRef&gt;
  &lt;/dmn:itemComponent&gt;
  &lt;dmn:itemComponent name=&quot;address&quot;&gt;
    &lt;dmn:itemComponent name=&quot;country&quot;&gt;
      &lt;dmn:typeRef&gt;string&lt;/dmn:typeRef&gt;
    &lt;/dmn:itemComponent&gt;
    &lt;dmn:itemComponent name=&quot;zip&quot;&gt;
      &lt;dmn:typeRef&gt;string&lt;/dmn:typeRef&gt;
    &lt;/dmn:itemComponent&gt;
  &lt;/dmn:itemComponent&gt;
&lt;/dmn:itemDefinition&gt;
</pre>
 * a call to {@link #getFields()} returns a collection of two entries:
 * <ul>
 * <li>field key <i>full name</i>, value the FEEL built-in type <code>string</code>
 * <li>field key <i>address</i>, value an anonymous inner Composite type.
 * </ul>
 * <br/>
 * This latter inner Composite type is not registered in the model's type registry, as explained.<br/>
 * By convention, it is given the name of the corresponding field: therefore, a call to {@link #getName()} returns <i>address</i>.<br/>
 * In itself, it is a Composite type; therefore everything said about Composite type is applicable, for example a call to {@link #getFields()} in turn would return a collection of two entries:
 * <ul>
 * <li>field key <i>country</i>, value the FEEL built-in type <code>string</code>
 * <li>field key <i>zip</i>, value the FEEL built-in type <code>string</code>
 * </ul>
 * <br/>
 * Another example of a DMN Composite type defining an anonymous inner type:<br/>
 * <br/>
<pre>&lt;dmn:itemDefinition name=&quot;tPart&quot;&gt;
  &lt;dmn:itemComponent name=&quot;name&quot;&gt;
    &lt;dmn:typeRef&gt;string&lt;/dmn:typeRef&gt;
  &lt;/dmn:itemComponent&gt;
  &lt;dmn:itemComponent name=&quot;grade&quot;&gt;
    &lt;dmn:typeRef&gt;string&lt;/dmn:typeRef&gt;
    &lt;dmn:allowedValues&gt;
      &lt;dmn:text&gt;&quot;A&quot;, &quot;B&quot;, &quot;C&quot;&lt;/dmn:text&gt;
    &lt;/dmn:allowedValues&gt;
  &lt;/dmn:itemComponent&gt;
&lt;/dmn:itemDefinition&gt;
</pre>
 * a call to {@link #getFields()} returns a collection of two entries:
 * <ul>
 * <li>field key <i>name</i>, value the FEEL built-in type <code>string</code>
 * <li>field key <i>grade</i>, value an anonymous inner Simple type.
 * </ul>
 * <br/>
 * This latter inner Simple type is not registered in the model's type registry, as explained.<br/>
 * By convention, it is given the name of the corresponding field: therefore, a call to {@link #getName()} returns <i>grade</i>.<br/>
 * In itself, it is a Simple type; therefore everything said about Simple type is applicable.
 * <h1>Implementation notes</h1>
 * To be noted that the convention of representing FEEL built-in types also as {@link DMNType}, is merely for internal mechanisms adopted by the engine
 * and does not correspond to any requirement from the DMN specification;
 * the current valorization of the attributes of this {@link DMNType} follows internal implementation choices and can change any time the convention might need update.
 * It is strongly advised to discern these representations from normal <code>ItemDefinition</code> defined in the DMN model,
 * by making use of the namespace attribute as described above.<br/>
 */
public interface DMNType
        extends Cloneable {

    String getNamespace();

    String getName();

    String getId();

    boolean isCollection();

    boolean isComposite();

    Map<String, DMNType> getFields();

    DMNType getBaseType();

    DMNType clone();

    /**
     * Definition of `instance of` accordingly to FEEL specifications Table 61.
     * @param o
     * @return if o is instance of the type represented by this type. If the parameter is null, returns false. 
     */
    boolean isInstanceOf(Object o);
    
    /**
     * Check if the value passed as parameter can be assigned to this type.
     * It checks
     * 1. type itself
     * 2. allowedValues
     * 3. typeConstraint
     * @param value
     * @return if value can be assigned to the type represented by this type. If the parameter is null, returns true. 
     */
    boolean isAssignableValue(Object value);

    List<DMNUnaryTest> getAllowedValues();

    List<DMNUnaryTest> getTypeConstraint();
}
