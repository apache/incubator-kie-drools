package org.jbpm.marshalling.impl;

import java.util.Date;

import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.ProtobufInputMarshaller;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.drools.core.marshalling.impl.ProtobufMessages.Timers.Timer;
import org.drools.core.marshalling.impl.ProtobufOutputMarshaller;
import org.drools.core.marshalling.impl.TimersInputMarshaller;
import org.drools.core.marshalling.impl.TimersOutputMarshaller;
import org.kie.services.time.JobContext;
import org.kie.services.time.JobHandle;
import org.kie.services.time.TimerService;
import org.kie.services.time.Trigger;
import org.jbpm.process.instance.InternalProcessRuntime;
import org.kie.services.time.manager.TimerInstance;
import org.kie.services.time.manager.TimerManager;
import org.jbpm.process.instance.timer.TimerManagerRuntimeAdaptor;

public class TimerManagerMarshallers {

    public static class ProcessTimerOutputMarshaller implements TimersOutputMarshaller {

        public Timer serialize(JobContext jobCtx, MarshallerWriteContext outputCtx) {
            // do not store StartProcess timers as they are registered whenever session starts
            if (jobCtx instanceof TimerManager.StartProcessJobContext) {
                return null;
            }
            TimerManager.ProcessJobContext pctx = (TimerManager.ProcessJobContext) jobCtx;

            return ProtobufMessages.Timers.Timer
                    .newBuilder()
                    .setType(ProtobufMessages.Timers.TimerType.PROCESS)
                    .setExtension(
                            JBPMMessages.procTimer,
                            JBPMMessages.ProcessTimer.newBuilder()
                                    .setTimer(ProtobufProcessMarshaller.writeTimer(outputCtx, pctx.getTimer()))
                                    .setTrigger(ProtobufOutputMarshaller.writeTrigger(pctx.getTrigger(), outputCtx)).build())
                    .build();
        }
    }

    public static class ProcessTimerInputMarshaller implements TimersInputMarshaller {

        public void deserialize(MarshallerReaderContext inCtx, Timer timer) throws ClassNotFoundException {
            JBPMMessages.ProcessTimer ptimer = timer.getExtension(JBPMMessages.procTimer);

            TimerService ts = inCtx.wm.getTimerService();

            String processInstanceId = ptimer.getTimer().getProcessInstanceId();

            Trigger trigger = ProtobufInputMarshaller.readTrigger(inCtx, ptimer.getTrigger());

            TimerInstance timerInstance = ProtobufProcessMarshaller.readTimer(inCtx, ptimer.getTimer());

            TimerManager tm = ((InternalProcessRuntime) inCtx.wm.getProcessRuntime()).getTimerManager();

            // check if the timer instance is not already registered to avoid duplicated timers
            if (!tm.getTimerMap().containsKey(timerInstance.getId())) {
                TimerManagerRuntimeAdaptor rt = new TimerManagerRuntimeAdaptor(inCtx.wm.getKnowledgeRuntime());
                TimerManager.ProcessJobContext pctx = new TimerManager.ProcessJobContext(timerInstance, trigger, processInstanceId,
                                                                                         rt, false);
                Date date = trigger.hasNextFireTime();

                if (date != null) {
                    long then = date.getTime();
                    long now = pctx.getKnowledgeRuntime().getSessionClock().getCurrentTime();
                    // overdue timer
                    if (then < now) {
                        trigger = new TimerManager.OverdueTrigger(trigger, pctx.getKnowledgeRuntime());
                    }
                }
                JobHandle jobHandle = ts.scheduleJob(TimerManager.processJob, pctx, trigger);
                timerInstance.setJobHandle(jobHandle);
                pctx.setJobHandle(jobHandle);

                tm.getTimerMap().put(timerInstance.getId(), timerInstance);
            }
        }
    }
}
