/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jbpm.services.task.impl.model;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="CompletionBehavior")
public class CompletionBehaviorImpl implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    
    
}
