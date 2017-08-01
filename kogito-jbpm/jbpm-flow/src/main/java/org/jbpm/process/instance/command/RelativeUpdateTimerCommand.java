/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.instance.command;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.jbpm.process.instance.timer.TimerInstance;

@XmlRootElement(name = "relative-update-timer-command")
@XmlAccessorType(XmlAccessType.NONE)
public class RelativeUpdateTimerCommand extends UpdateTimerCommand {

    private static final long serialVersionUID = -8252686458877022330L;

    public RelativeUpdateTimerCommand(long processInstanceId, String timerName, long delay) {
        this(processInstanceId, timerName, delay, 0, 0);
    }

    public RelativeUpdateTimerCommand(long processInstanceId, String timerName, long period, int repeatLimit) {
        this(processInstanceId, timerName, 0, period, repeatLimit);
    }

    public RelativeUpdateTimerCommand(long processInstanceId, String timerName, long delay, long period, int repeatLimit) {
        super(processInstanceId, timerName, delay, period, repeatLimit);
    }

    public RelativeUpdateTimerCommand(long processInstanceId, long timerId, long period, int repeatLimit) {
        super(processInstanceId, timerId, period, repeatLimit);
    }

    public RelativeUpdateTimerCommand(long processInstanceId, long timerId, long delay, long period, int repeatLimit) {
        super(processInstanceId, timerId, delay, period, repeatLimit);
    }

    public RelativeUpdateTimerCommand(long processInstanceId, long timerId, long delay) {
        super(processInstanceId, timerId, delay);
    }

    protected long calculateDelay(long delay, TimerInstance timer) {      
        return delay * 1000;
    }
}
