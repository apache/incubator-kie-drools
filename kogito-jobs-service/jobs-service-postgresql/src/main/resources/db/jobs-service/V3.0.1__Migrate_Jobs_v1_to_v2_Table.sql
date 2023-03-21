INSERT INTO job_details (id, correlation_id, status, last_update, fire_time, retries, execution_counter, scheduled_id, priority, trigger, recipient)
    SELECT job.id AS id,
           job.correlation_id AS correlation_id,
           job.status AS status,
           job.last_update AS last_update,
           job.fire_time AS fire_time,
           job.retries AS retries,
           job.execution_counter AS execution_counter,
           job.scheduled_id AS scheduled_id,
           job.priority AS priority,
           job.trigger AS trigger,
           json_build_object('url', job.recipient ->> 'endpoint',
                             'type', 'http',
                             'method', 'POST',
                             'classType', 'org.kie.kogito.jobs.service.api.recipient.http.HttpRecipient',
                             'queryParams', '{}'::jsonb,
                             'headers','{}'::jsonb,
                             'payload', null
               ) AS recipient
    FROM job_details_v1 job WHERE job.id IS NOT NULL;