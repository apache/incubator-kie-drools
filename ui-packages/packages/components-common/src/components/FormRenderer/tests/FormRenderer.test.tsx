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
import { mount } from 'enzyme';
import cloneDeep from 'lodash/cloneDeep';
import { AutoForm } from 'uniforms-patternfly/dist/es6';
import { UserTaskInstance } from '@kogito-apps/task-console-shared';

import FormRenderer from '../FormRenderer';
import { FormAction } from '../../utils';
import { ApplyForVisaForm } from '../../utils/tests/mocks/ApplyForVisa';
import FormFooter from '../../FormFooter/FormFooter';

const userTaskInstance: UserTaskInstance = {
  id: '45a73767-5da3-49bf-9c40-d533c3e77ef3',
  description: null,
  name: 'Apply for visa',
  priority: '1',
  processInstanceId: '9ae7ce3b-d49c-4f35-b843-8ac3d22fa427',
  processId: 'travels',
  rootProcessInstanceId: null,
  rootProcessId: null,
  state: 'Ready',
  actualOwner: null,
  adminGroups: [],
  adminUsers: [],
  completed: null,
  started: new Date('2020-02-19T11:11:56.282Z'),
  excludedUsers: [],
  potentialGroups: [],
  potentialUsers: [],
  inputs:
    '{"Skippable":"true","trip":{"city":"Boston","country":"US","visaRequired":true},"TaskName":"VisaApplication","NodeName":"Apply for visa","traveller":{"firstName":"Rachel","lastName":"White","email":"rwhite@gorle.com","nationality":"Polish","address":{"street":"Cabalone","city":"Zerf","zipCode":"765756","country":"Poland"}},"Priority":"1"}',
  outputs: '{}',
  referenceName: 'VisaApplication',
  lastUpdate: new Date('2020-02-19T11:11:56.282Z')
};

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('../../FormFooter/FormFooter');
jest.mock('uniforms-patternfly/dist/es6', () =>
  Object.assign({}, jest.requireActual('uniforms-patternfly/dist/es6'), {
    AutoForm: () => {
      return <MockedComponent />;
    },
    AutoFields: () => {
      return <MockedComponent />;
    },
    ErrorsField: () => {
      return <MockedComponent />;
    }
  })
);

let model;
let props;
let formActions: FormAction[];

// Clearing unneeded assignments to avoid issues on Uniforms Autoform
delete ApplyForVisaForm.properties.trip.input;
delete ApplyForVisaForm.properties.traveller.input;
delete ApplyForVisaForm.properties.traveller.output;
delete ApplyForVisaForm.properties.visaApplication.input;

describe('FormRenderer test', () => {
  beforeEach(() => {
    model = JSON.parse(userTaskInstance.inputs);
    formActions = [];
    props = {
      formSchema: cloneDeep(ApplyForVisaForm),
      model,
      formActions,
      readOnly: false,
      onSubmit: jest.fn()
    };
  });

  it('Render form with actions', () => {
    formActions.push({
      name: 'complete',
      execute: jest.fn()
    });

    const wrapper = mount(<FormRenderer {...props} />);
    expect(wrapper).toMatchSnapshot();

    const form = wrapper.findWhere((node) => node.type() === AutoForm);

    expect(form.exists()).toBeTruthy();
    expect(form.props().disabled).toBeFalsy();

    const footer = wrapper.find(FormFooter);
    expect(footer.exists()).toBeTruthy();
    expect(footer.props()['actions']).toHaveLength(1);
    expect(footer.props()['enabled']).toBeTruthy();
  });

  it('Render readonly form with actions', () => {
    formActions.push({
      name: 'complete',
      execute: jest.fn()
    });

    props.readOnly = true;

    const wrapper = mount(<FormRenderer {...props} />);

    const form = wrapper.findWhere((node) => node.type() === AutoForm);

    expect(form.exists()).toBeTruthy();
    expect(form.props().disabled).toBeTruthy();

    const footer = wrapper.find(FormFooter);
    expect(footer.exists()).toBeTruthy();
    expect(footer.props()['actions']).toHaveLength(1);
    expect(footer.props()['enabled']).toBeFalsy();
  });

  it('Render form without actions', () => {
    const wrapper = mount(<FormRenderer {...props} />);
    expect(wrapper).toMatchSnapshot();

    const form = wrapper.findWhere((node) => node.type() === AutoForm);
    expect(form.exists()).toBeTruthy();
    expect(form.props()['disabled']).toBeFalsy();
    const footer = wrapper.find(FormFooter);
    expect(footer.exists()).toBeTruthy();
    expect(footer.props()['actions']).toHaveLength(0);
    expect(footer.props()['enabled']).toStrictEqual(true);
  });
});
