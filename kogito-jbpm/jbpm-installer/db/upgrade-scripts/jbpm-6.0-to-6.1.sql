-- update context mapping info table with owner id (deployment id) for per process instance strategies
update contextmappinginfo set owner_id = (select externalId from processinstancelog where processinstanceid||'' = context_id);


-- update all tasks with its name, subject and description
update task t set name = (select shorttext from I18NText where task_names_id = t.id);
update task t set subject = (select shorttext from I18NText where task_subjects_id = t.id);
update task t set description = (select shorttext from I18NText where task_descriptions_id = t.id);