create table jbpm_active_clob ( loid oid );

-- Triggers to protect CLOB from vacuumlo

-- booleanexpression.expression for CLOB

CREATE OR REPLACE FUNCTION booleanexpression_expression_clob_before_insert()
  RETURNS "trigger" AS
$BODY$
declare
begin
    insert into jbpm_active_clob (loid) values (cast(new.expression as oid));
    return new;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER booleanexpression_expression_clob_before_insert_trigger
  BEFORE INSERT
  ON booleanexpression
  FOR EACH ROW
  WHEN (new.expression IS NOT NULL)
  EXECUTE PROCEDURE booleanexpression_expression_clob_before_insert();

CREATE OR REPLACE FUNCTION booleanexpression_expression_clob_before_update()
  RETURNS "trigger" AS
$BODY$
declare
begin
    insert into jbpm_active_clob (loid) values (cast(new.expression as oid));
    return new;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER booleanexpression_expression_clob_before_update_trigger
  BEFORE UPDATE
  ON booleanexpression
  FOR EACH ROW
  WHEN (new.expression IS NOT NULL AND old.expression IS DISTINCT FROM new.expression)
  EXECUTE PROCEDURE booleanexpression_expression_clob_before_update();

CREATE OR REPLACE FUNCTION booleanexpression_expression_clob_after_update()
  RETURNS "trigger" AS
$BODY$
declare
begin
    delete from jbpm_active_clob where loid = cast(old.expression as oid);
    return new;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER booleanexpression_expression_clob_after_update_trigger
  AFTER UPDATE
  ON booleanexpression
  FOR EACH ROW
  WHEN (old.expression IS NOT NULL AND old.expression IS DISTINCT FROM new.expression)
  EXECUTE PROCEDURE booleanexpression_expression_clob_after_update();

CREATE OR REPLACE FUNCTION booleanexpression_expression_clob_after_delete()
  RETURNS "trigger" AS
$BODY$
declare
begin
    delete from jbpm_active_clob where loid = cast(old.expression as oid);
    return old;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER booleanexpression_expression_clob_after_delete_trigger
  AFTER DELETE
  ON booleanexpression
  FOR EACH ROW
  WHEN (old.expression IS NOT NULL)
  EXECUTE PROCEDURE booleanexpression_expression_clob_after_delete();

-- email_header.body for CLOB

CREATE OR REPLACE FUNCTION email_header_body_clob_before_insert()
  RETURNS "trigger" AS
$BODY$
declare
begin
    insert into jbpm_active_clob (loid) values (cast(new.body as oid));
    return new;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER email_header_body_clob_before_insert_trigger
  BEFORE INSERT
  ON email_header
  FOR EACH ROW
  WHEN (new.body IS NOT NULL)
  EXECUTE PROCEDURE email_header_body_clob_before_insert();

CREATE OR REPLACE FUNCTION email_header_body_clob_before_update()
  RETURNS "trigger" AS
$BODY$
declare
begin
    insert into jbpm_active_clob (loid) values (cast(new.body as oid));
    return new;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER email_header_body_clob_before_update_trigger
  BEFORE UPDATE
  ON email_header
  FOR EACH ROW
  WHEN (new.body IS NOT NULL AND old.body IS DISTINCT FROM new.body)
  EXECUTE PROCEDURE email_header_body_clob_before_update();

CREATE OR REPLACE FUNCTION email_header_body_clob_after_update()
  RETURNS "trigger" AS
$BODY$
declare
begin
    delete from jbpm_active_clob where loid = cast(old.body as oid);
    return new;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER email_header_body_clob_after_update_trigger
  AFTER UPDATE
  ON email_header
  FOR EACH ROW
  WHEN (old.body IS NOT NULL AND old.body IS DISTINCT FROM new.body)
  EXECUTE PROCEDURE email_header_body_clob_after_update();

CREATE OR REPLACE FUNCTION email_header_body_clob_after_delete()
  RETURNS "trigger" AS
$BODY$
declare
begin
    delete from jbpm_active_clob where loid = cast(old.body as oid);
    return old;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER email_header_body_clob_after_delete_trigger
  AFTER DELETE
  ON email_header
  FOR EACH ROW
  WHEN (old.body IS NOT NULL)
  EXECUTE PROCEDURE email_header_body_clob_after_delete();

-- i18ntext.text for CLOB

CREATE OR REPLACE FUNCTION i18ntext_text_clob_before_insert()
  RETURNS "trigger" AS
$BODY$
declare
begin
    insert into jbpm_active_clob (loid) values (cast(new.text as oid));
    return new;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER i18ntext_text_clob_before_insert_trigger
  BEFORE INSERT
  ON i18ntext
  FOR EACH ROW
  WHEN (new.text IS NOT NULL)
  EXECUTE PROCEDURE i18ntext_text_clob_before_insert();

CREATE OR REPLACE FUNCTION i18ntext_text_clob_before_update()
  RETURNS "trigger" AS
$BODY$
declare
begin
    insert into jbpm_active_clob (loid) values (cast(new.text as oid));
    return new;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER i18ntext_text_clob_before_update_trigger
  BEFORE UPDATE
  ON i18ntext
  FOR EACH ROW
  WHEN (new.text IS NOT NULL AND old.text IS DISTINCT FROM new.text)
  EXECUTE PROCEDURE i18ntext_text_clob_before_update();

CREATE OR REPLACE FUNCTION i18ntext_text_clob_after_update()
  RETURNS "trigger" AS
$BODY$
declare
begin
    delete from jbpm_active_clob where loid = cast(old.text as oid);
    return new;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER i18ntext_text_clob_after_update_trigger
  AFTER UPDATE
  ON i18ntext
  FOR EACH ROW
  WHEN (old.text IS NOT NULL AND old.text IS DISTINCT FROM new.text)
  EXECUTE PROCEDURE i18ntext_text_clob_after_update();

CREATE OR REPLACE FUNCTION i18ntext_text_clob_after_delete()
  RETURNS "trigger" AS
$BODY$
declare
begin
    delete from jbpm_active_clob where loid = cast(old.text as oid);
    return old;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER i18ntext_text_clob_after_delete_trigger
  AFTER DELETE
  ON i18ntext
  FOR EACH ROW
  WHEN (old.text IS NOT NULL)
  EXECUTE PROCEDURE i18ntext_text_clob_after_delete();

-- task_comment.text for CLOB

CREATE OR REPLACE FUNCTION task_comment_text_clob_before_insert()
  RETURNS "trigger" AS
$BODY$
declare
begin
    insert into jbpm_active_clob (loid) values (cast(new.text as oid));
    return new;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER task_comment_text_clob_before_insert_trigger
  BEFORE INSERT
  ON task_comment
  FOR EACH ROW
  WHEN (new.text IS NOT NULL)
  EXECUTE PROCEDURE task_comment_text_clob_before_insert();

CREATE OR REPLACE FUNCTION task_comment_text_clob_before_update()
  RETURNS "trigger" AS
$BODY$
declare
begin
    insert into jbpm_active_clob (loid) values (cast(new.text as oid));
    return new;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER task_comment_text_clob_before_update_trigger
  BEFORE UPDATE
  ON task_comment
  FOR EACH ROW
  WHEN (new.text IS NOT NULL AND old.text IS DISTINCT FROM new.text)
  EXECUTE PROCEDURE task_comment_text_clob_before_update();

CREATE OR REPLACE FUNCTION task_comment_text_clob_after_update()
  RETURNS "trigger" AS
$BODY$
declare
begin
    delete from jbpm_active_clob where loid = cast(old.text as oid);
    return new;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER task_comment_text_clob_after_update_trigger
  AFTER UPDATE
  ON task_comment
  FOR EACH ROW
  WHEN (old.text IS NOT NULL AND old.text IS DISTINCT FROM new.text)
  EXECUTE PROCEDURE task_comment_text_clob_after_update();

CREATE OR REPLACE FUNCTION task_comment_text_clob_after_delete()
  RETURNS "trigger" AS
$BODY$
declare
begin
    delete from jbpm_active_clob where loid = cast(old.text as oid);
    return old;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER task_comment_text_clob_after_delete_trigger
  AFTER DELETE
  ON task_comment
  FOR EACH ROW
  WHEN (old.text IS NOT NULL)
  EXECUTE PROCEDURE task_comment_text_clob_after_delete();

-- querydefinitionstore.qexpression for CLOB

CREATE OR REPLACE FUNCTION querydefinitionstore_qexpression_clob_before_insert()
  RETURNS "trigger" AS
$BODY$
declare
begin
    insert into jbpm_active_clob (loid) values (cast(new.qexpression as oid));
    return new;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER querydefinitionstore_qexpression_clob_before_insert_trigger
  BEFORE INSERT
  ON querydefinitionstore
  FOR EACH ROW
  WHEN (new.qexpression IS NOT NULL)
  EXECUTE PROCEDURE querydefinitionstore_qexpression_clob_before_insert();

CREATE OR REPLACE FUNCTION querydefinitionstore_qexpression_clob_before_update()
  RETURNS "trigger" AS
$BODY$
declare
begin
    insert into jbpm_active_clob (loid) values (cast(new.qexpression as oid));
    return new;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER querydefinitionstore_qexpression_clob_before_update_trigger
  BEFORE UPDATE
  ON querydefinitionstore
  FOR EACH ROW
  WHEN (new.qexpression IS NOT NULL AND old.qexpression IS DISTINCT FROM new.qexpression)
  EXECUTE PROCEDURE querydefinitionstore_qexpression_clob_before_update();

CREATE OR REPLACE FUNCTION querydefinitionstore_qexpression_clob_after_update()
  RETURNS "trigger" AS
$BODY$
declare
begin
    delete from jbpm_active_clob where loid = cast(old.qexpression as oid);
    return new;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER querydefinitionstore_qexpression_clob_after_update_trigger
  AFTER UPDATE
  ON querydefinitionstore
  FOR EACH ROW
  WHEN (old.qexpression IS NOT NULL AND old.qexpression IS DISTINCT FROM new.qexpression)
  EXECUTE PROCEDURE querydefinitionstore_qexpression_clob_after_update();

CREATE OR REPLACE FUNCTION querydefinitionstore_qexpression_clob_after_delete()
  RETURNS "trigger" AS
$BODY$
declare
begin
    delete from jbpm_active_clob where loid = cast(old.qexpression as oid);
    return old;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER querydefinitionstore_qexpression_clob_after_delete_trigger
  AFTER DELETE
  ON querydefinitionstore
  FOR EACH ROW
  WHEN (old.qexpression IS NOT NULL)
  EXECUTE PROCEDURE querydefinitionstore_qexpression_clob_after_delete();

-- deploymentstore.deploymentunit for CLOB

CREATE OR REPLACE FUNCTION deploymentstore_deploymentunit_clob_before_insert()
  RETURNS "trigger" AS
$BODY$
declare
begin
    insert into jbpm_active_clob (loid) values (cast(new.deploymentunit as oid));
    return new;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER deploymentstore_deploymentunit_clob_before_insert_trigger
  BEFORE INSERT
  ON deploymentstore
  FOR EACH ROW
  WHEN (new.deploymentunit IS NOT NULL)
  EXECUTE PROCEDURE deploymentstore_deploymentunit_clob_before_insert();

CREATE OR REPLACE FUNCTION deploymentstore_deploymentunit_clob_before_update()
  RETURNS "trigger" AS
$BODY$
declare
begin
    insert into jbpm_active_clob (loid) values (cast(new.deploymentunit as oid));
    return new;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER deploymentstore_deploymentunit_clob_before_update_trigger
  BEFORE UPDATE
  ON deploymentstore
  FOR EACH ROW
  WHEN (new.deploymentunit IS NOT NULL AND old.deploymentunit IS DISTINCT FROM new.deploymentunit)
  EXECUTE PROCEDURE deploymentstore_deploymentunit_clob_before_update();

CREATE OR REPLACE FUNCTION deploymentstore_deploymentunit_clob_after_update()
  RETURNS "trigger" AS
$BODY$
declare
begin
    delete from jbpm_active_clob where loid = cast(old.deploymentunit as oid);
    return new;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER deploymentstore_deploymentunit_clob_after_update_trigger
  AFTER UPDATE
  ON deploymentstore
  FOR EACH ROW
  WHEN (old.deploymentunit IS NOT NULL AND old.deploymentunit IS DISTINCT FROM new.deploymentunit)
  EXECUTE PROCEDURE deploymentstore_deploymentunit_clob_after_update();

CREATE OR REPLACE FUNCTION deploymentstore_deploymentunit_clob_after_delete()
  RETURNS "trigger" AS
$BODY$
declare
begin
    delete from jbpm_active_clob where loid = cast(old.deploymentunit as oid);
    return old;
end;
$BODY$
  LANGUAGE plpgsql VOLATILE;

CREATE TRIGGER deploymentstore_deploymentunit_clob_after_delete_trigger
  AFTER DELETE
  ON deploymentstore
  FOR EACH ROW
  WHEN (old.deploymentunit IS NOT NULL)
  EXECUTE PROCEDURE deploymentstore_deploymentunit_clob_after_delete();

