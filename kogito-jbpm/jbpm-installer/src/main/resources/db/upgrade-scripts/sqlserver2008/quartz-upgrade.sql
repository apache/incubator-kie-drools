--
-- IMPORTANT NOTE :: replace "jBPMClusteredScheduler" with your scheduler's configured name (lines 27 - 36)
-- drop tables that are no longer used
--
drop table qrtz_job_listeners;
drop table qrtz_trigger_listeners;
--
-- drop columns that are no longer used
--
alter table qrtz_job_details drop column is_volatile;
alter table qrtz_triggers drop column is_volatile;
alter table qrtz_fired_triggers drop column is_volatile;
--
-- add new columns that replace the 'is_stateful' column
--
alter table qrtz_job_details add is_nonconcurrent integer;
alter table qrtz_job_details add is_update_data integer;
update qrtz_job_details set is_nonconcurrent = is_stateful;
update qrtz_job_details set is_update_data = is_stateful;
alter table qrtz_job_details drop column is_stateful;
alter table qrtz_fired_triggers add is_nonconcurrent integer;
update qrtz_fired_triggers set is_nonconcurrent = is_stateful;
alter table qrtz_fired_triggers drop column is_stateful;
--
-- add new 'sched_name' column to all tables --- replace "jBPMClusteredScheduler" with your scheduler's configured name
--
alter table qrtz_blob_triggers add sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler';
alter table qrtz_calendars add sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler';
alter table qrtz_cron_triggers add sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler';
alter table qrtz_fired_triggers add sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler';
alter table qrtz_job_details add sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler';
alter table qrtz_locks add sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler';
alter table qrtz_paused_trigger_grps add sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler';
alter table qrtz_scheduler_state add sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler';
alter table qrtz_simple_triggers add sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler';
alter table qrtz_triggers add sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler';
--
-- add new 'sched_time' column to qrtz_fired_triggers
--
alter table qrtz_fired_triggers add sched_time BIGINT;
--
-- drop all primary and foreign key constraints, so that we can define new ones
--
alter table qrtz_triggers drop constraint FK_QRTZ_TRIGGERS_QRTZ_JOB_DETAILS;
alter table qrtz_simple_triggers drop constraint FK_QRTZ_SIMPLE_TRIGGERS_QRTZ_TRIGGERS;
alter table qrtz_simple_triggers drop constraint PK_QRTZ_SIMPLE_TRIGGERS;
alter table qrtz_cron_triggers drop constraint FK_QRTZ_CRON_TRIGGERS_QRTZ_TRIGGERS;
alter table qrtz_cron_triggers drop constraint PK_QRTZ_CRON_TRIGGERS;

alter table qrtz_job_details drop constraint PK_QRTZ_JOB_DETAILS;
alter table qrtz_triggers drop constraint PK_QRTZ_TRIGGERS;
alter table qrtz_fired_triggers drop constraint PK_QRTZ_FIRED_TRIGGERS;
alter table qrtz_calendars drop constraint PK_QRTZ_CALENDARS;
alter table qrtz_locks drop constraint PK_QRTZ_LOCKS;
alter table qrtz_paused_trigger_grps drop constraint PK_QRTZ_PAUSED_TRIGGER_GRPS;
alter table qrtz_scheduler_state drop constraint PK_QRTZ_SCHEDULER_STATE;

--
-- add new simprop_triggers table
--
CREATE TABLE [dbo].[QRTZ_SIMPROP_TRIGGERS] (
  [SCHED_NAME] [VARCHAR] (120)  NOT NULL ,
  [TRIGGER_NAME] [VARCHAR] (200)  NOT NULL ,
  [TRIGGER_GROUP] [VARCHAR] (200)  NOT NULL ,
  [STR_PROP_1] [VARCHAR] (512) NULL,
  [STR_PROP_2] [VARCHAR] (512) NULL,
  [STR_PROP_3] [VARCHAR] (512) NULL,
  [INT_PROP_1] [INT] NULL,
  [INT_PROP_2] [INT] NULL,
  [LONG_PROP_1] [BIGINT] NULL,
  [LONG_PROP_2] [BIGINT] NULL,
  [DEC_PROP_1] [NUMERIC] (13,4) NULL,
  [DEC_PROP_2] [NUMERIC] (13,4) NULL,
  [BOOL_PROP_1] [VARCHAR] (1) NULL,
  [BOOL_PROP_2] [VARCHAR] (1) NULL,
) ON [PRIMARY]
GO

--
-- add all primary and foreign key constraints, based on new columns
--
ALTER TABLE [dbo].[QRTZ_CALENDARS] WITH NOCHECK ADD
  CONSTRAINT [PK_QRTZ_CALENDARS] PRIMARY KEY  CLUSTERED
  (
    [SCHED_NAME],
    [CALENDAR_NAME]
  )  ON [PRIMARY]
GO

ALTER TABLE [dbo].[QRTZ_CRON_TRIGGERS] WITH NOCHECK ADD
  CONSTRAINT [PK_QRTZ_CRON_TRIGGERS] PRIMARY KEY  CLUSTERED
  (
    [SCHED_NAME],
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  )  ON [PRIMARY]
GO

ALTER TABLE [dbo].[QRTZ_FIRED_TRIGGERS] WITH NOCHECK ADD
  CONSTRAINT [PK_QRTZ_FIRED_TRIGGERS] PRIMARY KEY  CLUSTERED
  (
    [SCHED_NAME],
    [ENTRY_ID]
  )  ON [PRIMARY]
GO

ALTER TABLE [dbo].[QRTZ_PAUSED_TRIGGER_GRPS] WITH NOCHECK ADD
  CONSTRAINT [PK_QRTZ_PAUSED_TRIGGER_GRPS] PRIMARY KEY  CLUSTERED
  (
    [SCHED_NAME],
    [TRIGGER_GROUP]
  )  ON [PRIMARY]
GO

ALTER TABLE [dbo].[QRTZ_SCHEDULER_STATE] WITH NOCHECK ADD
  CONSTRAINT [PK_QRTZ_SCHEDULER_STATE] PRIMARY KEY  CLUSTERED
  (
    [SCHED_NAME],
    [INSTANCE_NAME]
  )  ON [PRIMARY]
GO

ALTER TABLE [dbo].[QRTZ_LOCKS] WITH NOCHECK ADD
  CONSTRAINT [PK_QRTZ_LOCKS] PRIMARY KEY  CLUSTERED
  (
    [SCHED_NAME],
    [LOCK_NAME]
  )  ON [PRIMARY]
GO

ALTER TABLE [dbo].[QRTZ_JOB_DETAILS] WITH NOCHECK ADD
  CONSTRAINT [PK_QRTZ_JOB_DETAILS] PRIMARY KEY  CLUSTERED
  (
    [SCHED_NAME],
    [JOB_NAME],
    [JOB_GROUP]
  )  ON [PRIMARY]
GO

ALTER TABLE [dbo].[QRTZ_SIMPLE_TRIGGERS] WITH NOCHECK ADD
  CONSTRAINT [PK_QRTZ_SIMPLE_TRIGGERS] PRIMARY KEY  CLUSTERED
  (
    [SCHED_NAME],
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  )  ON [PRIMARY]
GO

ALTER TABLE [dbo].[QRTZ_SIMPROP_TRIGGERS] WITH NOCHECK ADD
  CONSTRAINT [PK_QRTZ_SIMPROP_TRIGGERS] PRIMARY KEY  CLUSTERED
  (
    [SCHED_NAME],
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  )  ON [PRIMARY]
GO

ALTER TABLE [dbo].[QRTZ_TRIGGERS] WITH NOCHECK ADD
  CONSTRAINT [PK_QRTZ_TRIGGERS] PRIMARY KEY  CLUSTERED
  (
    [SCHED_NAME],
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  )  ON [PRIMARY]
GO

ALTER TABLE [dbo].[QRTZ_CRON_TRIGGERS] ADD
  CONSTRAINT [FK_QRTZ_CRON_TRIGGERS_QRTZ_TRIGGERS] FOREIGN KEY
  (
    [SCHED_NAME],
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  ) REFERENCES [dbo].[QRTZ_TRIGGERS] (
    [SCHED_NAME],
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  ) ON DELETE CASCADE
GO

ALTER TABLE [dbo].[QRTZ_SIMPLE_TRIGGERS] ADD
  CONSTRAINT [FK_QRTZ_SIMPLE_TRIGGERS_QRTZ_TRIGGERS] FOREIGN KEY
  (
    [SCHED_NAME],
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  ) REFERENCES [dbo].[QRTZ_TRIGGERS] (
    [SCHED_NAME],
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  ) ON DELETE CASCADE
GO

ALTER TABLE [dbo].[QRTZ_SIMPROP_TRIGGERS] ADD
  CONSTRAINT [FK_QRTZ_SIMPROP_TRIGGERS_QRTZ_TRIGGERS] FOREIGN KEY
  (
    [SCHED_NAME],
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  ) REFERENCES [dbo].[QRTZ_TRIGGERS] (
    [SCHED_NAME],
    [TRIGGER_NAME],
    [TRIGGER_GROUP]
  ) ON DELETE CASCADE
GO

ALTER TABLE [dbo].[QRTZ_TRIGGERS] ADD
  CONSTRAINT [FK_QRTZ_TRIGGERS_QRTZ_JOB_DETAILS] FOREIGN KEY
  (
    [SCHED_NAME],
    [JOB_NAME],
    [JOB_GROUP]
  ) REFERENCES [dbo].[QRTZ_JOB_DETAILS] (
    [SCHED_NAME],
    [JOB_NAME],
    [JOB_GROUP]
  )
GO
