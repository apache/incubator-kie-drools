/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools;

public class FirstClass {
	
	private String item1;
	private String item2;
	private String item3;
	private String item4;
	private String item5;
	
	private String something;
	
	public String getItem1() {
		return item1;
	}
	public void setItem1(String item1) {
		this.item1 = item1;
	}
	public String getItem2() {
		return item2;
	}
	public void setItem2(String item2) {
		this.item2 = item2;
	}
	public String getItem3() {
		return item3;
	}
	public void setItem3(String item3) {
		this.item3 = item3;
	}
	public String getItem4() {
		return item4;
	}
	public void setItem4(String item4) {
		this.item4 = item4;
	}
	public String getItem5() {
		return item5;
	}
	public void setItem5(String item5) {
		this.item5 = item5;
	}
	public String getSomething() {
		return something;
	}
	public void setSomething(String something) {
		this.something = something;
	}
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("FirstClass[");
		buf.append("item1=" + item1 + ", ");
		buf.append("item2=" + item2 + ", ");
		buf.append("item3=" + item3 + ", ");
		buf.append("item4=" + item4 + ", ");
		buf.append("item5=" + item5 + "]");
		return buf.toString();
	}
	
	public static class AlternativeKey {
		
	}

}
