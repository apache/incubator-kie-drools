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

package org.drools.benchmark.manners;

public class Path {
	private int id;

	private String guestName;

	private int seat;

	public Path() {
		
	}
	
	public Path(int id, int seat, String guestName) {
		this.id = id;
		this.seat = seat;
		this.guestName = guestName;
	}

	public int getSeat() {
		return this.seat;
	}

	public String getGuestName() {
		return this.guestName;
	}

	public int getId() {
		return this.id;
	}

	public String toString() {
		return "[Path id=" + this.id + ", seat=" + this.seat + ", guest="
				+ this.guestName + "]";
	}

}
