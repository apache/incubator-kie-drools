/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.common;

public interface DroolsObjectStreamConstants {
    int STREAM_MAGIC = 0x001500d2;
    short STREAM_VERSION = 400;

    byte RT_CLASS = 11;
    byte RT_SERIALIZABLE = 12;
    byte RT_REFERENCE = 13;
    byte RT_EMPTY_SET = 14;
    byte RT_EMPTY_LIST = 15;
    byte RT_EMPTY_MAP = 16;
    byte RT_MAP = 17;
    byte RT_ARRAY = 18;
    byte RT_STRING = 19;
    byte RT_NULL = 20;
    byte RT_COLLECTION = 21;
    byte RT_EXTERNALIZABLE = 22;
    byte RT_ATOMICREFERENCEARRAY = 30;
}
