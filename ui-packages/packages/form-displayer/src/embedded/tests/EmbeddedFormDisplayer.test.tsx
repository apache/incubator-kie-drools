/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React from 'react';
import { EmbeddedFormDisplayer } from '../EmbeddedFormDisplayer';
import { mount } from 'enzyme';
import { FormType } from '../../api';

describe('EmbeddedFormDisplayer tests', () => {
  it('Snapshot', () => {
    const props = {
      targetOrigin: 'origin',
      envelopePath: '/resources/form-displayer.html',
      formData: {
        lastModified: new Date('2021-08-23T13:26:02.130Z'),
        name: 'react_hiring_HRInterview',
        type: FormType.TSX
      },
      formContent: {
        name: 'react_hiring_HRInterview',
        formConfiguration: {
          resources: {
            scripts: {},
            styles: {}
          },
          schema:
            '{"$schema":"http://json-schema.org/draft-07/schema#","type":"object","properties":{"candidate":{"type":"object","properties":{"email":{"type":"string"},"name":{"type":"string"},"salary":{"type":"integer"},"skills":{"type":"string"}},"input":true},"approve":{"type":"boolean","output":true}}}'
        },
        source: {
          'source-content':
            "import React, { useState } from 'react';\nimport {\n  Card,\n  CardBody,\n  TextInput,\n  FormGroup,\n  Checkbox\n} from '@patternfly/react-core';\n\nconst Form__hiring_HRInterview: React.FC<any> = (props: any) => {\n  const [candidate__email, set__candidate__email] = useState<string>();\n  const [candidate__name, set__candidate__name] = useState<string>();\n  const [candidate__salary, set__candidate__salary] = useState<string>();\n  const [candidate__skills, set__candidate__skills] = useState<string>();\n  const [approve, set__approve] = useState<boolean>();\n\n  return (\n    <div className={'pf-c-form'}>\n      <Card>\n        <CardBody className=\"pf-c-form\">\n          <label>\n            <b>Candidate</b>\n          </label>\n          <FormGroup\n            fieldId={'uniforms-0000-0002'}\n            label={'Email'}\n            isRequired={false}\n          >\n            <TextInput\n              name={'candidate.email'}\n              id={'uniforms-0000-0002'}\n              isDisabled={true}\n              placeholder={''}\n              type={'text'}\n              value={candidate__email}\n              onChange={set__candidate__email}\n            />\n          </FormGroup>\n          <FormGroup\n            fieldId={'uniforms-0000-0003'}\n            label={'Name'}\n            isRequired={false}\n          >\n            <TextInput\n              name={'candidate.name'}\n              id={'uniforms-0000-0003'}\n              isDisabled={true}\n              placeholder={''}\n              type={'text'}\n              value={candidate__name}\n              onChange={set__candidate__name}\n            />\n          </FormGroup>\n          <FormGroup\n            fieldId={'uniforms-0000-0005'}\n            label={'Salary'}\n            isRequired={false}\n          >\n            <TextInput\n              type={'number'}\n              name={'candidate.salary'}\n              isDisabled={true}\n              id={'uniforms-0000-0005'}\n              placeholder={''}\n              step={1}\n              value={candidate__salary}\n              onChange={set__candidate__salary}\n            />\n          </FormGroup>\n          <FormGroup\n            fieldId={'uniforms-0000-0006'}\n            label={'Skills'}\n            isRequired={false}\n          >\n            <TextInput\n              name={'candidate.skills'}\n              id={'uniforms-0000-0006'}\n              isDisabled={true}\n              placeholder={''}\n              type={'text'}\n              value={candidate__skills}\n              onChange={set__candidate__skills}\n            />\n          </FormGroup>\n        </CardBody>\n      </Card>\n      <FormGroup fieldId=\"uniforms-0000-0008\">\n        <Checkbox\n          isChecked={approve}\n          isDisabled={false}\n          id={'uniforms-0000-0008'}\n          name={'approve'}\n          label={'Approve'}\n          onChange={set__approve}\n        />\n      </FormGroup>\n    </div>\n  );\n};\n\nexport default Form__hiring_HRInterview;\n"
        }
      }
    };

    const wrapper = mount(<EmbeddedFormDisplayer {...props} />);

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.props().targetOrigin).toStrictEqual(props.targetOrigin);
    expect(wrapper.props().formData).toStrictEqual(props.formData);
    const contentIframe = wrapper.find('iframe');

    expect(contentIframe.exists()).toBeTruthy();
  });
});
