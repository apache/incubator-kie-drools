package org.jbpm.services.task.commands;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.kie.internal.command.Context;

@XmlRootElement(name="task-composite-command")
@XmlAccessorType(XmlAccessType.NONE)
public class CompositeCommand<T> extends TaskCommand<T> {
	
	private static final long serialVersionUID = -5591247478243819049L;
	private TaskCommand<T> mainCommand;
	private TaskCommand<?>[] commands;
	
	public CompositeCommand() {
		
	}
	
	public CompositeCommand(TaskCommand<T> mainCommand, TaskCommand<?>...commands) {
		this.mainCommand = mainCommand;
		this.commands = commands;
	}

	@Override
	public T execute(Context context) {
		if (commands != null) {
			for (TaskCommand<?> cmd : commands) {
				cmd.execute(context);
			}
		}
		return mainCommand.execute(context);
	}

}
