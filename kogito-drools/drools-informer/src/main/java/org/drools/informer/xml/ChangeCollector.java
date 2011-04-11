/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package org.drools.informer.xml;

import org.drools.event.rule.ObjectInsertedEvent;
import org.drools.event.rule.ObjectRetractedEvent;
import org.drools.event.rule.ObjectUpdatedEvent;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.informer.*;
import org.drools.runtime.rule.FactHandle;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;


import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * <p>
 * Builds up lists of the Tohu objects created, updated or deleted as a result of a question being answered.
 * </p>
 * <p>
 * These lists are built up as the changes occur because they are read via reflection and we won't know when this happens.
 * </p>
 * <p>
 * There is special handling for:
 * </p>
 * <ul>
 * <li>Inactive objects - From the client's perspective these do not exist. Therefore:
 * <ol>
 * <li> an object becoming inactive is effectively a delete</li>
 * <li> an object becoming active is effectively a create</li>
 * <li> changes to inactive objects are ignored</li>
 * </ol>
 * </li>
 * <li>Question answers - if the client has just answered a question then we care about changes from that value not from the
 * value that was previously on the Question object.</li>
 * </ul>
 * 
 * @author Damon Horrell
 */
public class ChangeCollector implements WorkingMemoryEventListener
{

//	private final static Logger logger = LoggerFactory.getLogger(ChangeCollector.class);
	
	/**
	 * The original values of the objects we have seen. This map will contain nulls to indicate that the "original" of an object
	 * was that it didn't exist. i.e. prior to a create.
	 */
	private transient Map<String, TohuObject> originalObjects;

	/**
	 * Answers provided by the client. This is used to avoid sending back a question to the client if the only change made to the
	 * question was the answer that the client provided.
	 */
	private transient Map<String, String> clientAnswers;

	private Map<Object, FactHandle> create;
	
	private List<Object> update;
	
	// delete list contains ItemId and InvalidAnswer
	private List<Object> delete;
	
	Map<Object, FactHandle> getCreate() {
		return create;
	}

	List<Object> getUpdate() {
		return update;
	}

	List<Object> getDelete() {
		return delete;
	}

	public boolean initialised() {
		return originalObjects != null;
	}

	/**
	 * <p>
	 * Makes copies of the original value all the objects that we wish to track.
	 * </p>
	 * <p>
	 * Shallow copies are sufficient since none of our objects contain children. (All lists are stored as comma-delimited strings
	 * so they can be serialized nicely.)
	 * </p>
	 * 
	 * @param originalObjects
	 */
	public void initialise(Collection<?> originalObjects) {
		this.originalObjects = new HashMap<String, TohuObject>();
		for (Object object : originalObjects) {
			if (object instanceof TohuObject) {
				TohuObject i = (TohuObject) object;
				try {
					this.originalObjects.put(i.getId(), (TohuObject) i.clone());
				} catch (CloneNotSupportedException e) {
					// ignore
				}
			} else if (object instanceof Answer) {
				Answer answer = (Answer) object;
				storeClientAnswer(answer);
			}
		}
	}

	/**
	 * @see org.drools.event.rule.WorkingMemoryEventListener#objectInserted(org.drools.event.rule.ObjectInsertedEvent)
	 */
	public void objectInserted(ObjectInsertedEvent event) {
//        logger.debug("==> [ObjectInserted: handle=" + event.getFactHandle() + "; object=" + event.getObject() + "]");
		if (event.getObject() instanceof TohuObject) {
			TohuObject newObject = (TohuObject) event.getObject();
			String id = newObject.getId();
			TohuObject originalObject = getOriginalObject(id);
//			logger.debug("==>ObjectInserted: Inserting Tohu Fact with ID [" + id + "] into working memry");
			processChange(id, originalObject, newObject, newObject, event.getFactHandle());
		} else if (event.getObject() instanceof Answer) {
			Answer answer = (Answer) event.getObject();
//			logger.debug("==>ObjectInserted: Inserting Answer Fact with value [" + answer.getValue() + "] into working memry");
			storeClientAnswer(answer);
		}
	}

	/**
	 * @see org.drools.event.rule.WorkingMemoryEventListener#objectUpdated(org.drools.event.rule.ObjectUpdatedEvent)
	 */
	public void objectUpdated(ObjectUpdatedEvent event) {
//        logger.debug("==> [ObjectUpdated handle=" + event.getFactHandle() + "; object=" + event.getOldObject() + "]");
		if (event.getObject() instanceof TohuObject) {
			TohuObject newObject = (TohuObject) event.getObject();
			String id = newObject.getId();
//			logger.debug("==> ObjectUpdated: Updating Fact with ID [" + id + "] that exists in Working Memry");
			TohuObject originalObject = getOriginalObject(id);
			processChange(id, originalObject, newObject, newObject, event.getFactHandle());
		}
	}

	/**
	 * @see org.drools.event.rule.WorkingMemoryEventListener#objectRetracted(org.drools.event.rule.ObjectRetractedEvent)
	 */
	public void objectRetracted(ObjectRetractedEvent event) {
//        logger.debug("==> [ObjectRetracted: handle=" + event.getFactHandle() + "; object=" + event.getOldObject() + "]");
		if (event.getOldObject() instanceof TohuObject) {
			TohuObject oldObject = (TohuObject) event.getOldObject();
			String id = oldObject.getId();
//			logger.debug("==> ObjectRemoved: Removing Fact with ID [" + id + "] from Working Memry");
			TohuObject originalObject = getOriginalObject(id);
			processChange(id, originalObject, null, oldObject, event.getFactHandle());
		}
	}

	/**
	 * Fetches the original version of the object for the specified id. If we don't have one then this must be a new object being
	 * created so we add it (as null to indicate that it didn't orginally exist).
	 * 
	 * @param id
	 * @return
	 */
	private TohuObject getOriginalObject(String id) {
		if (originalObjects == null) {
			originalObjects = new HashMap<String, TohuObject>();
		}
		boolean exists = originalObjects.containsKey(id);
		if (exists) {
			return originalObjects.get(id);
		}
		originalObjects.put(id, null);
		return null;
	}

	/**
	 * <p>
	 * Processes an object change from originalObject to newObject and determines whether this is a create, update or delete.
	 * </p>
	 * <p>
	 * Replaces any previous change for the same object.
	 * </p>
	 * 
	 * @param id
	 * @param originalObject
	 * @param newObject
	 * @param recentObject
	 *            A recent instance of this object which is used only for removing objects from lists. This is required because it
	 *            is possible for both oldObject and newObject to be null if we are processing a delete right after a create.
	 */
	private void processChange(String id, TohuObject originalObject, TohuObject newObject,
			TohuObject recentObject, FactHandle factHandle) {
		// remove previous change
		if (create != null) {
			create.remove(recentObject);
		}
		if (update != null) {
			update.remove(recentObject);
		}
		if (delete != null) {
			if (recentObject instanceof InvalidAnswer) {
				delete.remove(recentObject);
			} else {
				delete.remove(new ItemId((Item) recentObject));
			}
		}
		// determine what we need to do
		boolean isCreate = (originalObject == null || !originalObject.isActive()) && newObject != null && newObject.isActive();
		boolean isUpdate = originalObject != null && originalObject.isActive() && newObject != null && newObject.isActive()
				&& different(originalObject, newObject);
		boolean isDelete = originalObject != null && originalObject.isActive() && (newObject == null || !newObject.isActive());
		// make the change
		if (isCreate) {
			if (create == null) {
				create = new HashMap<Object, FactHandle>();
			}
			create.put(newObject, factHandle);
		}
		if (isUpdate) {
			if (update == null) {
				update = new ArrayList<Object>();
			}
			update.add(newObject);
		}
		if (isDelete) {
			if (delete == null) {
				delete = new ArrayList<Object>();
			}
			if (recentObject instanceof InvalidAnswer) {
				delete.add(recentObject);
			} else {
				delete.add(new ItemId((Item) recentObject));
			}
		}
		// remove empty lists
		if (create != null && create.isEmpty()) {
			create = null;
		}
		if (update != null && update.isEmpty()) {
			update = null;
		}
		if (delete != null && delete.isEmpty()) {
			delete = null;
		}
	}

	/**
	 * <p>
	 * Performs a deep comparison (using reflection) of two objects to determine whether they are different.
	 * </p>
	 * <p>
	 * If the object is a Question then the answer is treated specially because the original value of the answer from our point of
	 * view is the value provided by the client in an Answer fact. If no such fact exists then the value on the question itself is
	 * used. Scenarios are:
	 * </p>
	 * 
	 * <ul>
	 * <li>Question which client has just answered - the new object is different if the answer is not the value provided by the
	 * client. i.e. if the rules have changed it to something else e.g. converting text to upper case.</li>
	 * <li>Another question - the new object is different if the answer is not the value on the original object.</li>
	 * </ul>
	 * 
	 * 
	 * @param originalObject
	 * @param newObject
	 * @return
	 */
	private boolean different(TohuObject originalObject, TohuObject newObject) {
		if (!originalObject.equals(newObject)) {
			return true;
		}
		// special handling for Question answers
		if (originalObject instanceof Question) {
			Question originalQuestion = (Question) originalObject;
			String originalAnswer;
			if (clientAnswers != null && clientAnswers.containsKey(originalQuestion.getId())) {
				// original answer is the one provided by the client
				originalAnswer = clientAnswers.get(originalQuestion.getId());
			} else {
				// original answer not provided by client so is contained in the original question
				originalAnswer = originalQuestion.getAnswer() == null ? null : originalQuestion.getAnswer().toString();
			}
			Question newQuestion = (Question) newObject;
			String newAnswer = newQuestion.getAnswer() == null ? null : newQuestion.getAnswer().toString();
			if (originalAnswer == null ? newAnswer != null : !originalAnswer.equals(newAnswer)) {
				return true;
			}
		}
		Class<?> clazz = originalObject.getClass();
		do {
			// compare all non-static non-transient fields
			for (Field field : clazz.getDeclaredFields()) {
				int modifiers = field.getModifiers();
				if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)) {
					boolean answerField = field.isAnnotationPresent(Question.AnswerField.class);
					// answer fields are skipped because we have checked this already
					if (!answerField) {
						field.setAccessible(true);
						try {
							Object originalValue = field.get(originalObject);
							Object newValue = field.get(newObject);
							if (originalValue == null ? newValue != null : !originalValue.equals(newValue)) {
								return true;
							}
						} catch (IllegalArgumentException e) {
							throw new RuntimeException(e);
						} catch (IllegalAccessException e) {
							throw new RuntimeException(e);
						}
					}
				}
			}
			clazz = clazz.getSuperclass();
		} while (clazz != null);
		return false;

	}

	private void storeClientAnswer(Answer answer) {
		if (clientAnswers == null) {
			clientAnswers = new HashMap<String, String>();
		}
		String answerValue = answer.getValue();
		// TODO should we really be handling "null" - see TOHU-3
		if (answerValue != null && (answerValue.equals("") || answerValue.equals("null"))) {
			answerValue = null;
		}
		clientAnswers.put(answer.getQuestionId(), answerValue);
	}
   
}
