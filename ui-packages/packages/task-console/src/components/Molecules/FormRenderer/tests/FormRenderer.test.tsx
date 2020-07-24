import React from 'react';
import { mount } from 'enzyme';
import _ from 'lodash';
import { AutoForm } from 'uniforms-patternfly';
import { UserTaskInstance } from '../../../../graphql/types';
import FormRenderer from '../FormRenderer';
import ApplyForVisaForm from '../../../../util/tests/mocks/ApplyForVisa';
import { IFormAction } from '../../../../util/uniforms/FormSubmitHandler/FormSubmitHandler';

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
  started: '2020-02-19T11:11:56.282Z',
  excludedUsers: [],
  potentialGroups: [],
  potentialUsers: [],
  inputs:
    '{"Skippable":"true","trip":{"city":"Boston","country":"US","visaRequired":true},"TaskName":"VisaApplication","NodeName":"Apply for visa","traveller":{"firstName":"Rachel","lastName":"White","email":"rwhite@gorle.com","nationality":"Polish","address":{"street":"Cabalone","city":"Zerf","zipCode":"765756","country":"Poland"}},"Priority":"1"}',
  outputs: '{}',
  referenceName: 'VisaApplication',
  lastUpdate: '2020-02-19T11:11:56.282Z'
};

let formData;
let props;
let formActions: IFormAction[];
let submitHandler;

// Clearing unneeded assignments to avoid issues on Uniforms Autoform
delete ApplyForVisaForm.properties.trip.input;
delete ApplyForVisaForm.properties.trip.output;
delete ApplyForVisaForm.properties.traveller.input;
delete ApplyForVisaForm.properties.traveller.output;
delete ApplyForVisaForm.properties.visaApplication.input;
delete ApplyForVisaForm.properties.visaApplication.output;

const MockFormSubmitHandler = jest.fn(() => ({
  doSubmit: jest.fn(),
  getActions: jest.fn(() => formActions)
}));


describe('FormRenderer test', () => {
  beforeEach(() => {
    submitHandler = new MockFormSubmitHandler();
    formData = JSON.parse(userTaskInstance.inputs);
    formActions = [];
    props = {
      formSchema: _.cloneDeep(ApplyForVisaForm),
      model: formData,
      formSubmitHandler: submitHandler
    };
  });

  it('Render form with actions', () => {
    formActions.push({
      name: "complete",
      primary: false,
      execute: jest.fn()
    })

    const wrapper = mount(<FormRenderer {...props} />);
    expect(wrapper).toMatchSnapshot();

    const form = wrapper.findWhere(node => node.type() === AutoForm);

    expect(form.exists()).toBeTruthy()
    expect(form.props().disabled).toBeFalsy();
  });

  it('Render form without actions', () => {
    const wrapper = mount(<FormRenderer {...props} />);
    expect(wrapper).toMatchSnapshot();

    const form = wrapper.findWhere(node => node.type() === AutoForm);
    expect(form.exists()).toBeTruthy()
    expect(form.props().disabled).toBeTruthy();
  });
});
