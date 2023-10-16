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
/**
 * Function to set OUIA:Page Page Type and Page Object Id attributes in page body.
 * @param ouiaContext OUIAContext of the component wrapped with patternfly's withOuiaContext function.
 * @param type string value to be set as Page Type
 * @param objectId string value to be set as Page Object Id
 */
export const ouiaPageTypeAndObjectId = (
  type: string,
  objectId?: string
): (() => void) => {
  document.body.setAttribute('data-ouia-page-type', type);
  if (objectId) {
    document.body.setAttribute('data-ouia-page-object-id', objectId);
  }
  return () => {
    document.body.removeAttribute('data-ouia-page-type');
    if (objectId) {
      document.body.removeAttribute('data-ouia-page-object-id');
    }
  };
};

type OuiaId = number | string;

export interface OUIAProps {
  // If there is only one instance of the component on the page at once, it is OPTIONAL
  ouiaId?: OuiaId;
  // False if in animation
  ouiaSafe?: boolean;
}

/**
 * Function to set ouia attribute.
 * Usage:
 * <div
 *   {...ouiaAttribute(ouiaContext,'name','value')}
 * />
 *
 * @param name name of the attribute
 * @param value value of the attribute
 */
export const ouiaAttribute = (name: string, value: any) => {
  // if (ouiaContext.isOuia && value) {
  if (value) {
    return { [name]: value };
  }
  return {};
};

/**
 * Function to set OUIA attributes on the component.
 *
 * Typical usage:
 * const MyComponent:React.FC<OUIAProps> = ({
 *   ouiaId,
 *   ouiaSafe
 * }) => {
 * return
 *   <OtherComponent {...componentOuiaProps(ouiaId, 'MyComponent', ouiaSafe)} >
 *   .
 *   .
 *   .
 *   </OtherComponent>
 * }
 *
 * @param ouiaId id of the component - a value passed as part of OUIAProps to the component
 * @param ouiaType type of the component - typically a string explicitly provided to this function call
 * @param isSafe boolean value indicating if the component is safe = is not doing any loading action. Default is true.
 */
export const componentOuiaProps = (
  ouiaId: OuiaId | null,
  ouiaType: string,
  isSafe = true
) => {
  return {
    ...(ouiaId && { 'data-ouia-component-id': ouiaId }),
    'data-ouia-component-type': ouiaType,
    'data-ouia-safe': isSafe
  };
};

/**
 * Function to set an data-ouia-component-id attribute.
 *
 * Typical usage:
 * <MyComponent>
 *   <Button {...attributeOuiaId('button-1')} />
 *   <Button {...attributeOuiaId('button-2')} />
 * </MyComponent>
 *
 * @param ouiaId id string value to be set as an id
 */
export const attributeOuiaId = (ouiaId: OuiaId) => {
  return ouiaAttribute('data-ouia-component-id', ouiaId);
};
