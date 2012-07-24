/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.task;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 *
 * @author salaboy
 */
@Entity
public class PresentationElement {

    @Id
    @GeneratedValue
    private long id;
}
