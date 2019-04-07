--
-- IMPORTANT NOTE :: replace "jBPMClusteredScheduler" with your scheduler's configured name (lines 53 - 80)
-- drop tables that are no longer used
--
drop table qrtz_job_listeners
go

drop table qrtz_trigger_listeners
go

--
-- drop columns that are no longer used
--
alter table qrtz_job_details drop is_volatile
go

alter table qrtz_triggers drop is_volatile
go

alter table qrtz_fired_triggers drop is_volatile
go

--
-- add new columns that replace the 'is_stateful' column
--
alter table qrtz_job_details add is_nonconcurrent bit default 0 not null 
go

alter table qrtz_job_details add is_update_data bit default 0 not null
go
 
update qrtz_job_details set is_nonconcurrent = is_stateful
go

update qrtz_job_details set is_update_data = is_stateful
go

alter table qrtz_job_details drop is_stateful
go

alter table qrtz_fired_triggers add is_nonconcurrent bit default 0 not null 
go

update qrtz_fired_triggers set is_nonconcurrent = is_stateful
go

alter table qrtz_fired_triggers drop is_stateful
go

--
-- add new 'sched_name' column to all tables --- replace "jBPMClusteredScheduler" with your scheduler's configured name
--
alter table qrtz_blob_triggers add sched_name varchar(120) DEFAULT 'jBPMClusteredScheduler' not null
go

alter table qrtz_calendars add sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler' not null
go

alter table qrtz_cron_triggers add sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler' not null
go

alter table qrtz_fired_triggers add sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler' not null
go

alter table qrtz_job_details add sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler' not null
go

alter table qrtz_locks add sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler' not null
go

alter table qrtz_paused_trigger_grps add sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler' not null
go

alter table qrtz_scheduler_state add sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler' not null
go

alter table qrtz_simple_triggers add sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler' not null
go

alter table qrtz_triggers add sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler' not null
go
--
-- add new 'sched_time' column to qrtz_fired_triggers
--
alter table qrtz_fired_triggers add sched_time NUMERIC(13,0)
go
--
-- drop all primary and foreign key constraints, so that we can define new ones
--

alter table QRTZ_CALENDARS 
drop constraint PK_qrtz_calendars
go

alter table QRTZ_CRON_TRIGGERS 
drop constraint PK_qrtz_cron_triggers
go

alter table QRTZ_FIRED_TRIGGERS 
drop constraint PK_qrtz_fired_triggers
go

alter table QRTZ_PAUSED_TRIGGER_GRPS 
drop constraint PK_qrtz_paused_trigger_grps
go

alter table QRTZ_SCHEDULER_STATE 
drop constraint PK_qrtz_scheduler_state
go

alter table QRTZ_LOCKS 
drop constraint PK_qrtz_locks
go

alter table QRTZ_JOB_DETAILS 
drop constraint PK_qrtz_job_details
go

alter table QRTZ_SIMPLE_TRIGGERS 
drop constraint PK_qrtz_simple_triggers
go

alter table QRTZ_TRIGGERS 
drop constraint PK_qrtz_triggers
go

alter table QRTZ_BLOB_TRIGGERS 
drop constraint PK_qrtz_blob_triggers
go


alter table QRTZ_TRIGGERS
drop constraint FK_triggers_job_details
go

alter table QRTZ_CRON_TRIGGERS
drop constraint FK_cron_triggers_triggers
go

alter table QRTZ_SIMPLE_TRIGGERS
drop constraint FK_simple_triggers_triggers
go

alter table QRTZ_BLOB_TRIGGERS
drop constraint FK_blob_triggers_triggers
go

--
-- add new simprop_triggers table
--
CREATE TABLE QRTZ_SIMPROP_TRIGGERS
  (          
    SCHED_NAME VARCHAR(120) NOT NULL,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    STR_PROP_1 VARCHAR(512) NULL,
    STR_PROP_2 VARCHAR(512) NULL,
    STR_PROP_3 VARCHAR(512) NULL,
    INT_PROP_1 INT NULL,
    INT_PROP_2 INT NULL,
    LONG_PROP_1 NUMERIC(13,0) NULL,
    LONG_PROP_2 NUMERIC(13,0) NULL,
    DEC_PROP_1 NUMERIC(13,4) NULL,
    DEC_PROP_2 NUMERIC(13,4) NULL,
    BOOL_PROP_1 bit NOT NULL,
    BOOL_PROP_2 bit NOT NULL
)
go

--
-- add all primary and foreign key constraints, based on new columns
--
alter table QRTZ_CALENDARS
add constraint PK_qrtz_calendars primary key clustered (SCHED_NAME,CALENDAR_NAME)
go

alter table QRTZ_CRON_TRIGGERS
add constraint PK_qrtz_cron_triggers primary key clustered (SCHED_NAME,TRIGGER_NAME, TRIGGER_GROUP)
go

alter table QRTZ_FIRED_TRIGGERS
add constraint PK_qrtz_fired_triggers primary key clustered (SCHED_NAME,ENTRY_ID)
go

alter table QRTZ_PAUSED_TRIGGER_GRPS
add constraint PK_qrtz_paused_trigger_grps primary key clustered (SCHED_NAME,TRIGGER_GROUP)
go

alter table QRTZ_SCHEDULER_STATE
add constraint PK_qrtz_scheduler_state primary key clustered (SCHED_NAME,INSTANCE_NAME)
go

alter table QRTZ_LOCKS
add constraint PK_qrtz_locks primary key clustered (SCHED_NAME,LOCK_NAME)
go

alter table QRTZ_JOB_DETAILS
add constraint PK_qrtz_job_details primary key clustered (SCHED_NAME,JOB_NAME, JOB_GROUP)
go

alter table QRTZ_SIMPLE_TRIGGERS
add constraint PK_qrtz_simple_triggers primary key clustered (SCHED_NAME,TRIGGER_NAME, TRIGGER_GROUP)
go

alter table QRTZ_SIMPROP_TRIGGERS
add constraint PK_qrtz_simprop_triggers primary key clustered (SCHED_NAME,TRIGGER_NAME, TRIGGER_GROUP)
go

alter table QRTZ_TRIGGERS
add constraint PK_qrtz_triggers primary key clustered (SCHED_NAME,TRIGGER_NAME, TRIGGER_GROUP)
go

alter table QRTZ_BLOB_TRIGGERS
add constraint PK_qrtz_blob_triggers primary key clustered (SCHED_NAME,TRIGGER_NAME, TRIGGER_GROUP)
go



alter table QRTZ_CRON_TRIGGERS
add constraint FK_cron_triggers_triggers foreign key (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
references QRTZ_TRIGGERS (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
go

alter table QRTZ_SIMPLE_TRIGGERS
add constraint FK_simple_triggers_triggers foreign key (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
references QRTZ_TRIGGERS (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
go

alter table QRTZ_SIMPROP_TRIGGERS
add constraint FK_simprop_triggers_triggers foreign key (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
references QRTZ_TRIGGERS (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
go

alter table QRTZ_TRIGGERS
add constraint FK_triggers_job_details foreign key (SCHED_NAME,JOB_NAME,JOB_GROUP)
references QRTZ_JOB_DETAILS (SCHED_NAME,JOB_NAME,JOB_GROUP)
go

alter table QRTZ_BLOB_TRIGGERS
add constraint FK_blob_triggers_triggers foreign key (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
references QRTZ_TRIGGERS (SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP)
go
