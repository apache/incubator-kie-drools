// tslint:disable: forin
// tslint:disable: no-floating-promises

const nestedCheck = (ele, valueObj) => {
  for (const key in ele) {
    const temp = ele[key];
    if (typeof temp[0] === 'object') {
      for (const nestedProp in temp[0]) {
        const nestedObj = {};
        const result = nestedCheck(temp[0], valueObj);
        if (valueObj.hasOwnProperty(nestedProp)) {
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
        if (valueObj.hasOwnProperty(nestedProp)) {
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

export const validateResponse = (obj, paramFields) => {
  let contentObj = {};
  for (const prop in obj) {
    const arr = [];
    if (obj[prop] === null) {
      const parentObj = {};
      paramFields.map(params => {
        if (params.hasOwnProperty(prop)) {
          arr.push(params);
        }
      });
      let valueObj = {};
      arr.forEach(ele => {
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
