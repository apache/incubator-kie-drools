--
-- IMPORTANT NOTE :: replace "jBPMClusteredScheduler" with your scheduler's configured name (lines 27 - 36)
-- IMPORTANT NOTE :: replace SQLXXXXXX with actual foreign key names for that tables (lines 45, 46, 48, 50)
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
alter table qrtz_job_details add column is_nonconcurrent integer;
alter table qrtz_job_details add column is_update_data integer;
update qrtz_job_details set is_nonconcurrent = is_stateful;
update qrtz_job_details set is_update_data = is_stateful;
alter table qrtz_job_details drop column is_stateful;
alter table qrtz_fired_triggers add column is_nonconcurrent integer;
update qrtz_fired_triggers set is_nonconcurrent = is_stateful;
alter table qrtz_fired_triggers drop column is_stateful;
--
-- add new 'sched_name' column to all tables --- replace "jBPMClusteredScheduler" with your scheduler's configured name
--
alter table qrtz_blob_triggers add column sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler';
alter table qrtz_calendars add column sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler';
alter table qrtz_cron_triggers add column sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler';
alter table qrtz_fired_triggers add column sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler';
alter table qrtz_job_details add column sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler';
alter table qrtz_locks add column sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler';
alter table qrtz_paused_trigger_grps add column sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler';
alter table qrtz_scheduler_state add column sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler';
alter table qrtz_simple_triggers add column sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler';
alter table qrtz_triggers add column sched_name varchar(120) not null DEFAULT 'jBPMClusteredScheduler';
--
-- add new 'sched_time' column to qrtz_fired_triggers
--
alter table qrtz_fired_triggers add column sched_time BIGINT;
--
-- drop all primary and foreign key constraints, so that we can define new ones
--
alter table qrtz_triggers drop foreign key SQL171214124431150;
alter table qrtz_blob_triggers drop foreign key SQL171214124513540;
alter table qrtz_blob_triggers drop primary key;
alter table qrtz_simple_triggers drop foreign key SQL171214124431430;
alter table qrtz_simple_triggers drop primary key;
alter table qrtz_cron_triggers drop foreign key SQL171214124513290;
alter table qrtz_cron_triggers drop primary key;

alter table qrtz_job_details drop primary key;
alter table qrtz_triggers drop primary key;

call SYSPROC.ADMIN_CMD('reorg table qrtz_job_details');
call SYSPROC.ADMIN_CMD('reorg table qrtz_triggers');
call SYSPROC.ADMIN_CMD('reorg table qrtz_blob_triggers');
call SYSPROC.ADMIN_CMD('reorg table qrtz_simple_triggers');
call SYSPROC.ADMIN_CMD('reorg table qrtz_cron_triggers');
call SYSPROC.ADMIN_CMD('reorg table qrtz_fired_triggers');
call SYSPROC.ADMIN_CMD('reorg table qrtz_calendars');
call SYSPROC.ADMIN_CMD('reorg table qrtz_locks');
call SYSPROC.ADMIN_CMD('reorg table qrtz_paused_trigger_grps');
call SYSPROC.ADMIN_CMD('reorg table qrtz_scheduler_state');
--
-- add all primary and foreign key constraints, based on new columns
--
alter table qrtz_job_details add primary key (sched_name, job_name, job_group);
alter table qrtz_triggers add primary key (sched_name, trigger_name, trigger_group);
alter table qrtz_triggers add foreign key (sched_name, job_name, job_group) references qrtz_job_details(sched_name, job_name, job_group);
alter table qrtz_blob_triggers add primary key (sched_name, trigger_name, trigger_group);
alter table qrtz_blob_triggers add foreign key (sched_name, trigger_name, trigger_group) references qrtz_triggers(sched_name, trigger_name, trigger_group);
alter table qrtz_cron_triggers add primary key (sched_name, trigger_name, trigger_group);
alter table qrtz_cron_triggers add foreign key (sched_name, trigger_name, trigger_group) references qrtz_triggers(sched_name, trigger_name, trigger_group);
alter table qrtz_simple_triggers add primary key (sched_name, trigger_name, trigger_group);
alter table qrtz_simple_triggers add foreign key (sched_name, trigger_name, trigger_group) references qrtz_triggers(sched_name, trigger_name, trigger_group);
alter table qrtz_fired_triggers drop primary key;
alter table qrtz_fired_triggers add primary key (sched_name, entry_id);
alter table qrtz_calendars drop primary key;
alter table qrtz_calendars add primary key (sched_name, calendar_name);
alter table qrtz_locks drop primary key;
alter table qrtz_locks add primary key (sched_name, lock_name);
alter table qrtz_paused_trigger_grps drop primary key;
alter table qrtz_paused_trigger_grps add primary key (sched_name, trigger_group);
alter table qrtz_scheduler_state drop primary key;
alter table qrtz_scheduler_state add primary key (sched_name, instance_name);
--
-- add new simprop_triggers table
--
CREATE TABLE qrtz_simprop_triggers
  (          
    sched_name varchar(120) not null,
    TRIGGER_NAME VARCHAR(200) NOT NULL,
    TRIGGER_GROUP VARCHAR(200) NOT NULL,
    STR_PROP_1 VARCHAR(512),
    STR_PROP_2 VARCHAR(512),
    STR_PROP_3 VARCHAR(512),
    INT_PROP_1 INT,
    INT_PROP_2 INT,
    LONG_PROP_1 BIGINT,
    LONG_PROP_2 BIGINT,
    DEC_PROP_1 NUMERIC(13,4),
    DEC_PROP_2 NUMERIC(13,4),
    BOOL_PROP_1 VARCHAR(1),
    BOOL_PROP_2 VARCHAR(1),
    PRIMARY KEY (sched_name,TRIGGER_NAME,TRIGGER_GROUP),
    FOREIGN KEY (sched_name,TRIGGER_NAME,TRIGGER_GROUP) 
    REFERENCES QRTZ_TRIGGERS(sched_name,TRIGGER_NAME,TRIGGER_GROUP)
);
--
-- create indexes for faster queries
--
create index idx_qrtz_j_req_recovery on qrtz_job_details(SCHED_NAME,REQUESTS_RECOVERY);
create index idx_qrtz_j_grp on qrtz_job_details(SCHED_NAME,JOB_GROUP);
create index idx_qrtz_t_j on qrtz_triggers(SCHED_NAME,JOB_NAME,JOB_GROUP);
create index idx_qrtz_t_jg on qrtz_triggers(SCHED_NAME,JOB_GROUP);
create index idx_qrtz_t_c on qrtz_triggers(SCHED_NAME,CALENDAR_NAME);
create index idx_qrtz_t_g on qrtz_triggers(SCHED_NAME,TRIGGER_GROUP);
create index idx_qrtz_t_state on qrtz_triggers(SCHED_NAME,TRIGGER_STATE);
create index idx_qrtz_t_n_state on qrtz_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP,TRIGGER_STATE);
create index idx_qrtz_t_n_g_state on qrtz_triggers(SCHED_NAME,TRIGGER_GROUP,TRIGGER_STATE);
create index idx_qrtz_t_next_fire_time on qrtz_triggers(SCHED_NAME,NEXT_FIRE_TIME);
create index idx_qrtz_t_nft_st on qrtz_triggers(SCHED_NAME,TRIGGER_STATE,NEXT_FIRE_TIME);
create index idx_qrtz_t_nft_misfire on qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME);
create index idx_qrtz_t_nft_st_misfire on qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_STATE);
create index idx_qrtz_t_nft_st_misfire_grp on qrtz_triggers(SCHED_NAME,MISFIRE_INSTR,NEXT_FIRE_TIME,TRIGGER_GROUP,TRIGGER_STATE);
create index idx_qrtz_ft_trig_inst_name on qrtz_fired_triggers(SCHED_NAME,INSTANCE_NAME);
create index idx_qrtz_ft_inst_job_req_rcvry on qrtz_fired_triggers(SCHED_NAME,INSTANCE_NAME,REQUESTS_RECOVERY);
create index idx_qrtz_ft_j_g on qrtz_fired_triggers(SCHED_NAME,JOB_NAME,JOB_GROUP);
create index idx_qrtz_ft_jg on qrtz_fired_triggers(SCHED_NAME,JOB_GROUP);
create index idx_qrtz_ft_t_g on qrtz_fired_triggers(SCHED_NAME,TRIGGER_NAME,TRIGGER_GROUP);
create index idx_qrtz_ft_tg on qrtz_fired_triggers(SCHED_NAME,TRIGGER_GROUP);
