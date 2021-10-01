import React, { useState } from 'react';
import {
  Checkbox,
  FormGroup,
  Card,
  CardBody,
  TextInput
} from '@patternfly/react-core';

const Form__hiring_ITInterview: React.FC<any> = (props: any) => {
  const [approve, set__approve] = useState<boolean>();
  const [candidate__email, set__candidate__email] = useState<string>();
  const [candidate__name, set__candidate__name] = useState<string>();
  const [candidate__salary, set__candidate__salary] = useState<string>();
  const [candidate__skills, set__candidate__skills] = useState<string>();

  return (
    <div className={'pf-c-form'}>
      <FormGroup fieldId="uniforms-0001-0001">
        <Checkbox
          isChecked={approve}
          isDisabled={false}
          id={'uniforms-0001-0001'}
          name={'approve'}
          label={'Approve'}
          onChange={set__approve}
        />
      </FormGroup>
      <Card>
        <CardBody className="pf-c-form">
          <label>
            <b>Candidate</b>
          </label>
          <FormGroup
            fieldId={'uniforms-0001-0004'}
            label={'Email'}
            isRequired={false}
          >
            <TextInput
              name={'candidate.email'}
              id={'uniforms-0001-0004'}
              isDisabled={true}
              placeholder={''}
              type={'text'}
              value={candidate__email}
              onChange={set__candidate__email}
            />
          </FormGroup>
          <FormGroup
            fieldId={'uniforms-0001-0005'}
            label={'Name'}
            isRequired={false}
          >
            <TextInput
              name={'candidate.name'}
              id={'uniforms-0001-0005'}
              isDisabled={true}
              placeholder={''}
              type={'text'}
              value={candidate__name}
              onChange={set__candidate__name}
            />
          </FormGroup>
          <FormGroup
            fieldId={'uniforms-0001-0007'}
            label={'Salary'}
            isRequired={false}
          >
            <TextInput
              type={'number'}
              name={'candidate.salary'}
              isDisabled={true}
              id={'uniforms-0001-0007'}
              placeholder={''}
              step={1}
              value={candidate__salary}
              onChange={set__candidate__salary}
            />
          </FormGroup>
          <FormGroup
            fieldId={'uniforms-0001-0008'}
            label={'Skills'}
            isRequired={false}
          >
            <TextInput
              name={'candidate.skills'}
              id={'uniforms-0001-0008'}
              isDisabled={true}
              placeholder={''}
              type={'text'}
              value={candidate__skills}
              onChange={set__candidate__skills}
            />
          </FormGroup>
        </CardBody>
      </Card>
    </div>
  );
};

export default Form__hiring_ITInterview;
