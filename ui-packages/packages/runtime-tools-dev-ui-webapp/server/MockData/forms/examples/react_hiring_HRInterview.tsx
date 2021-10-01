import React, { useState } from 'react';
import {
  Card,
  CardBody,
  TextInput,
  FormGroup,
  Checkbox
} from '@patternfly/react-core';

const Form__hiring_HRInterview: React.FC<any> = (props: any) => {
  const [candidate__email, set__candidate__email] = useState<string>();
  const [candidate__name, set__candidate__name] = useState<string>();
  const [candidate__salary, set__candidate__salary] = useState<string>();
  const [candidate__skills, set__candidate__skills] = useState<string>();
  const [approve, set__approve] = useState<boolean>();

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
              isDisabled={true}
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
              isDisabled={true}
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
              isDisabled={true}
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
              isDisabled={true}
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
