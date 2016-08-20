create table TaskVariableImpl (
    id numeric(19,0) identity not null,
    modificationDate datetime null,
    name varchar(255) null,
    processId varchar(255) null,
    processInstanceId numeric(19,0) null,
    taskId numeric(19,0) null,
    type int null,
    value varchar(4000) null,
    primary key (id)
) lock datarows
go

create table QueryDefinitionStore (
    id numeric(19,0) identity not null,
    qExpression text null,
    qName varchar(255) null,
    qSource varchar(255) null,
    qTarget varchar(255) null,
    primary key (id)
) lock datarows

go

alter table QueryDefinitionStore 
    add constraint UK_4ry5gt77jvq0orfttsoghta2j unique (qName)

create index IDX_VInstLog_pInstId on VariableInstanceLog(processInstanceId);
go
create index IDX_VInstLog_varId on VariableInstanceLog(variableId);
go
create index IDX_VInstLog_pId on VariableInstanceLog(processId);
go

create index IDX_NInstLog_pInstId on NodeInstanceLog(processInstanceId);
go
create index IDX_NInstLog_nodeType on NodeInstanceLog(nodeType);
go
create index IDX_NInstLog_pId on NodeInstanceLog(processId);
go

