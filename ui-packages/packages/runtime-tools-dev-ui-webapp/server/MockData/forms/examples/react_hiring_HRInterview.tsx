/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React, { useCallback, useEffect, useState } from 'react';
import {
  Card,
  CardBody,
  TextInput,
  FormGroup,
  Checkbox
} from '@patternfly/react-core';

const Form__hiring_HRInterview: React.FC<any> = (props: any) => {
  const [formApi, setFormApi] = useState<any>();
  const [candidate__email, set__candidate__email] = useState<string>('');
  const [candidate__name, set__candidate__name] = useState<string>('');
  const [candidate__salary, set__candidate__salary] = useState<number>('');
  const [candidate__skills, set__candidate__skills] = useState<string>('');
  const [approve, set__approve] = useState<boolean>(false);

  /* Utility function that fills the form with the data received from the kogito runtime */
  const setFormData = (data) => {
    if (!data) {
      return;
    }
    set__candidate__email(data?.candidate?.email ?? '');
    set__candidate__name(data?.candidate?.name ?? '');
    set__candidate__salary(data?.candidate?.salary ?? '');
    set__candidate__skills(data?.candidate?.skills ?? '');
    set__approve(data?.approve ?? false);
  };

  /* Utility function to generate the expected form output as a json object */
  const getFormData = useCallback(() => {
    const formData: any = {};
    formData.approve = approve;
    return formData;
  }, [approve]);

  /* Utility function to validate the form on the 'beforeSubmit' Lifecycle Hook */
  const validateForm = useCallback(() => {
    if (candidate__salary > 1000000) {
      throw new Error(`Salary too high ${candidate__salary}`);
    }
  }, [candidate__salary]);

  /* Utility function to perform actions on the on the 'afterSubmit' Lifecycle Hook */
  const afterSubmit = useCallback((result) => {}, []);

  useEffect(() => {
    if (formApi) {
      /*
        Form Lifecycle Hook that will be executed before the form is submitted.
        Throwing an error will stop the form submit. Usually should be used to validate the form.
      */
      formApi.beforeSubmit = () => validateForm();
      /*
        Form Lifecycle Hook that will be executed after the form is submitted.
        It will receive a response object containing the `type` flag indicating if the submit has been successful and `info` with extra information about the submit result.
      */
      formApi.afterSubmit = (result) => afterSubmit(result);
      /* Generates the expected form output object to be posted */
      formApi.getFormData = () => getFormData();
    }
  }, [getFormData, validateForm, afterSubmit]);

  useEffect(() => {
    /*
        Call to the Kogito console form engine. It will establish the connection with the console embeding the form
        and return an instance of FormAPI that will allow hook custom code into the form lifecycle.
        The `window.Form.openForm` call expects an object with the following entries:
            - onOpen: Callback that will be called after the connection with the console is established. The callback
            will receive the following arguments:
                - data: the data to be bound into the form
                - ctx: info about the context where the form is being displayed. This will contain information such as the form JSON Schema, process/task, user...
    */
    const api = window.Form.openForm({
      onOpen: (data, context) => {
        setFormData(data);
      }
    });
    setFormApi(api);
  }, []);
  return (
    <div className={'pf-c-form'}>
      <Card>
        <CardBody className="pf-c-form">
          <label>
            <b>Candidate</b>
          </label>
          <FormGroup
            fieldId={'uniforms-0000-0002'}
            label={'Email'}
            isRequired={false}
          >
            <TextInput
              name={'candidate.email'}
              id={'uniforms-0000-0002'}
              isDisabled={false}
              placeholder={''}
              type={'text'}
              value={candidate__email}
              onChange={set__candidate__email}
            />
          </FormGroup>
          <FormGroup
            fieldId={'uniforms-0000-0003'}
            label={'Name'}
            isRequired={false}
          >
            <TextInput
              name={'candidate.name'}
              id={'uniforms-0000-0003'}
              isDisabled={false}
              placeholder={''}
              type={'text'}
              value={candidate__name}
              onChange={set__candidate__name}
            />
          </FormGroup>
          <FormGroup
            fieldId={'uniforms-0000-0005'}
            label={'Salary'}
            isRequired={false}
          >
            <TextInput
              type={'number'}
              name={'candidate.salary'}
              isDisabled={false}
              id={'uniforms-0000-0005'}
              placeholder={''}
              step={1}
              value={candidate__salary}
              onChange={set__candidate__salary}
            />
          </FormGroup>
          <FormGroup
            fieldId={'uniforms-0000-0006'}
            label={'Skills'}
            isRequired={false}
          >
            <TextInput
              name={'candidate.skills'}
              id={'uniforms-0000-0006'}
              isDisabled={false}
              placeholder={''}
              type={'text'}
              value={candidate__skills}
              onChange={set__candidate__skills}
            />
          </FormGroup>
        </CardBody>
      </Card>
      <FormGroup fieldId="uniforms-0000-0008">
        <Checkbox
          isChecked={approve}
          isDisabled={false}
          id={'uniforms-0000-0008'}
          name={'approve'}
          label={'Approve'}
          onChange={set__approve}
        />
      </FormGroup>
    </div>
  );
};
export default Form__hiring_HRInterview;
