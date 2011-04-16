/*
 * Copyright 2009 Solnet Solutions Limited (http://www.solnetsolutions.co.nz/)
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
package org.drools.informer;

/**
 * <p>
 * Represents any arbitrary type of note that might need to be displayed as part of a <code>Questionnaire</code>. e.g.
 * </p>
 * 
 * <ul>
 * <li>A prompt such as "Please select one of the following:"</li>
 * <li>An inline note explaining more about a particular question</li>
 * <li>A mouse-over hint</li>
 * <li>A context-sensitive footnote or other advice</li>
 * <li>A help icon that shows some text only when clicked on</li>
 * </ul>
 * 
 * <p>
 * All of these possibilities are controlled through the use of <code>presentationStyles</code>.
 * </p>
 * 
 * @author Damon Horrell
 */
public class Note extends Item {

	private static final long serialVersionUID = 1L;

	private String label;

    private String context;

	public Note() {
	}

	public Note(String id) {
		super(id);
	}

	public Note(String id, String label) {
		super(id);
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}


    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    /**
	 * For debugging purposes.
	 */
	@Override
	public String toString() {
		return super.toString() + " label=" + getLabel() + " context=" +getContext();
	}
}
