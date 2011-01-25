/*
 * Copyright 2005 JBoss Inc
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

package org.drools.integrationtests.manners;

import java.io.Serializable;

public class Chosen implements Serializable {

	private int id;

	private String guestName;

	private Hobby hobby;
	
	public Chosen() {
		
	}

	public Chosen(int id, String guestName, Hobby hobby) {
		this.id = id;
		this.guestName = guestName;
		this.hobby = hobby;
	}

	public int getId() {
		return this.id;
	}

	public String getGuestName() {
		return this.guestName;
	}

	public Hobby getHobby() {
		return this.hobby;
	}

	public String toString() {
		return "{Chosen id=" + this.id + ", name=" + this.guestName
				+ ", hobbies=" + this.hobby + "}";
	}
}
