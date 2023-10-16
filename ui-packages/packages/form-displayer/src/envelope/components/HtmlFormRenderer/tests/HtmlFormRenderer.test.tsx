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
import React from 'react';
import { mount } from 'enzyme';
import HtmlFormRenderer from '../HtmlFormRenderer';
import ResourcesContainer from '../../ResourcesContainer/ResourcesContainer';
import InnerHTML from 'dangerously-set-html-content';

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('dangerously-set-html-content', () => ({
  __esModule: true,
  default: 'InnerHTML',
  InnerHTML: () => <MockedComponent />
}));

jest.mock('../../ResourcesContainer/ResourcesContainer');

describe('HtmlFormRenderer test cases', () => {
  beforeAll(() => {
    const div = document.createElement('div');
    div.setAttribute('id', 'formContainer');
    document.body.appendChild(div);
  });
  it('Snapshot test with default props', () => {
    const props = {
      source: '<div></div>',
      resources: {
        scripts: {
          'bootstrap.min.js':
            'https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js',
          'jquery.js': 'https://code.jquery.com/jquery-3.2.1.slim.min.js',
          'popper.js':
            'https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js'
        },
        styles: {
          'bootstrap.min.css':
            'https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css'
        }
      }
    };
    const wrapper = mount(<HtmlFormRenderer {...props} />);
    expect(wrapper).toMatchSnapshot();
    expect(wrapper.find('div')).toBeTruthy();
  });
  it('Test source with script tags', () => {
    const props = {
      source:
        '<script src=\'https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js\'></script><div>  <fieldset disabled>    <legend>Candidate</legend>    <div>      <div class="form-group">        <label for="uniforms-0000-0002">Email</label>        <input type="text" id="uniforms-0000-0002" name="candidate.email" class="form-control" disabled value="" />      </div>      <div class="form-group">        <label for="uniforms-0000-0003">Name</label>        <input type="text" id="uniforms-0000-0003" name="candidate.name" class="form-control" disabled value="" />      </div>      <div class="form-group">        <label for="uniforms-0000-0005">Salary</label>        <input type="number" class="form-control" id="uniforms-0000-0005" name="candidate.salary" disabled step="1" value="" />      </div>      <div class="form-group">        <label for="uniforms-0000-0006">Skills</label>        <input type="text" id="uniforms-0000-0006" name="candidate.skills" class="form-control" disabled value="" />      </div>    </div>  </fieldset>  <div class="form-check">    <input type="checkbox" id="uniforms-0000-0008" name="approve" class="form-check-input" />    <label class="form-check-label" for="uniforms-0000-0008">Approve</label>  </div></div>',
      resources: {
        scripts: {
          'bootstrap.min.js':
            'https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js',
          'jquery.js': 'https://code.jquery.com/jquery-3.2.1.slim.min.js',
          'popper.js':
            'https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js'
        },
        styles: {
          'bootstrap.min.css':
            'https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css'
        }
      }
    };
    const wrapper = mount(<HtmlFormRenderer {...props} />);
    expect(wrapper.find('div')).toBeTruthy();

    const resources = wrapper.find(ResourcesContainer);
    expect(resources.exists()).toBeTruthy();

    const innerHTML = wrapper.find(InnerHTML);
    expect(innerHTML.exists()).toBeTruthy();
  });
});
