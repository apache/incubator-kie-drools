// tslint:disable: forin
// tslint:disable: no-floating-promises

import { isAuthEnabled } from './KeycloakClient';

declare global {
  interface Window {
    TEST_USER_SYSTEM_ENABLED: boolean;
  }
}

const nestedCheck = (ele, valueObj) => {
  for (const key in ele) {
    const temp = ele[key];
    if (typeof temp[0] === 'object') {
      for (const nestedProp in temp[0]) {
        const nestedObj = {};
        const result = nestedCheck(temp[0], valueObj);
        if (Object.prototype.hasOwnProperty.call(valueObj, nestedProp)) {
          valueObj[nestedProp] = result;
        } else {
          nestedObj[nestedProp] = result;
          valueObj = { ...valueObj, ...nestedObj };
        }
        return valueObj;
      }
    } else {
      const val = ele[key];
      const tempObj = {};
      tempObj[val[0]] = null;
      const firstKey = Object.keys(valueObj)[0];
      valueObj = { ...valueObj[firstKey], ...tempObj };
      return valueObj;
    }
  }
};

const checkFunc = (ele, valueObj) => {
  for (const key in ele) {
    const temp = ele[key];
    if (typeof temp[0] === 'object') {
      for (const nestedProp in temp[0]) {
        const nestedObj = {};
        if (Object.prototype.hasOwnProperty.call(valueObj, nestedProp)) {
          const result = nestedCheck(temp[0], valueObj);
          valueObj[nestedProp] = result;
        } else {
          const result = checkFunc(temp[0], valueObj);
          nestedObj[nestedProp] = result;
          valueObj = { ...valueObj, ...nestedObj };
        }
        return valueObj;
      }
    } else {
      const val = ele[key];
      const tempObj = {};
      tempObj[val[0]] = null;
      valueObj = { ...valueObj, ...tempObj };
      return valueObj;
    }
  }
};
// function that validates null data
export const validateResponse = (obj, paramFields) => {
  let contentObj = {};
  for (const prop in obj) {
    const arr = [];
    if (obj[prop] === null) {
      const parentObj = {};
      paramFields.map((params) => {
        if (Object.prototype.hasOwnProperty.call(params, prop)) {
          arr.push(params);
        }
      });
      let valueObj = {};
      arr.forEach((ele) => {
        valueObj = checkFunc(ele, valueObj);
      });
      parentObj[prop] = valueObj;
      contentObj = { ...contentObj, ...parentObj };
    } else {
      const elseObj = {};
      elseObj[prop] = obj[prop];
      contentObj = { ...contentObj, ...elseObj };
    }
  }
  return contentObj;
};

// function that frames object for query based on the selections
export const filterColumnSelection = (selectionArray, objValue) => {
  let res = {};
  if (selectionArray.length === 0) {
    res = objValue;
    return res;
  }
  for (let i = selectionArray.length - 1; i >= 0; i--) {
    if (i === selectionArray.length - 1) {
      if (selectionArray[i] === '-') {
        res = objValue;
      } else {
        res = { [selectionArray[i]]: [objValue] }; // assign the value
      }
    } else {
      res = { [selectionArray[i]]: [res] }; // put the prev object
    }
  }
  return res;
};

// function that removes single property from object
export const deleteKey = (testObj, pathArray) => {
  const _obj = testObj;
  const keys = pathArray;
  keys.reduce((acc, key, index) => {
    if (index === keys.length - 1) {
      delete acc[key];
      return true;
    }
    return acc[key];
  }, _obj);
  return _obj;
};
export const clearEmpties = (obj) => {
  for (const key in obj) {
    if (!obj[key] || typeof obj[key] !== 'object') {
      continue;
    }
    clearEmpties(obj[key]);
    if (Object.keys(obj[key]).length === 0) {
      delete obj[key];
    }
  }
  return obj;
};

// function adds new property to existing object
export const constructObject = (obj, path, val) => {
  const keys = path.split(',');
  const lastKey = keys.pop();
  // tslint:disable-next-line: no-shadowed-variable
  const lastObj = keys.reduce(
    // tslint:disable-next-line: no-shadowed-variable
    (_obj, key) => (_obj[key] = obj[key] || {}),
    obj
  );
  lastObj[lastKey] = val;
};

// function removes duplicate objects inside array
export const removeDuplicates = (arr, comp) => {
  const unique = arr
    .map((e) => e[comp])
    .map((e, i, final) => final.indexOf(e) === i && i)
    .filter((e) => arr[e])
    .map((e) => arr[e]);

  return unique;
};

export const isTestUserSystemEnabled = () => {
  const testSystemEnabled: boolean =
    window.TEST_USER_SYSTEM_ENABLED ||
    process.env.TEST_USER_SYSTEM_ENABLED === 'true';

  return !isAuthEnabled() && testSystemEnabled;
};
