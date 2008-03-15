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
