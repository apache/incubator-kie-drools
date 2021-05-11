/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.serialization.process.protobuf;

public final class KogitoTypesProtobuf {
  private KogitoTypesProtobuf() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface VariableOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.Variable)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>string name = 1;</code>
     * @return The name.
     */
    java.lang.String getName();
    /**
     * <code>string name = 1;</code>
     * @return The bytes for name.
     */
    com.google.protobuf.ByteString
        getNameBytes();

    /**
     * <code>string data_type = 2;</code>
     * @return The dataType.
     */
    java.lang.String getDataType();
    /**
     * <code>string data_type = 2;</code>
     * @return The bytes for dataType.
     */
    com.google.protobuf.ByteString
        getDataTypeBytes();

    /**
     * <code>.google.protobuf.Any value = 3;</code>
     * @return Whether the value field is set.
     */
    boolean hasValue();
    /**
     * <code>.google.protobuf.Any value = 3;</code>
     * @return The value.
     */
    com.google.protobuf.Any getValue();
    /**
     * <code>.google.protobuf.Any value = 3;</code>
     */
    com.google.protobuf.AnyOrBuilder getValueOrBuilder();
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.Variable}
   */
  public static final class Variable extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.Variable)
      VariableOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use Variable.newBuilder() to construct.
    private Variable(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Variable() {
      name_ = "";
      dataType_ = "";
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new Variable();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Variable(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 10: {
              java.lang.String s = input.readStringRequireUtf8();

              name_ = s;
              break;
            }
            case 18: {
              java.lang.String s = input.readStringRequireUtf8();

              dataType_ = s;
              break;
            }
            case 26: {
              com.google.protobuf.Any.Builder subBuilder = null;
              if (((bitField0_ & 0x00000001) != 0)) {
                subBuilder = value_.toBuilder();
              }
              value_ = input.readMessage(com.google.protobuf.Any.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(value_);
                value_ = subBuilder.buildPartial();
              }
              bitField0_ |= 0x00000001;
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Variable_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Variable_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.class, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder.class);
    }

    private int bitField0_;
    public static final int NAME_FIELD_NUMBER = 1;
    private volatile java.lang.Object name_;
    /**
     * <code>string name = 1;</code>
     * @return The name.
     */
    @java.lang.Override
    public java.lang.String getName() {
      java.lang.Object ref = name_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        name_ = s;
        return s;
      }
    }
    /**
     * <code>string name = 1;</code>
     * @return The bytes for name.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getNameBytes() {
      java.lang.Object ref = name_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        name_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int DATA_TYPE_FIELD_NUMBER = 2;
    private volatile java.lang.Object dataType_;
    /**
     * <code>string data_type = 2;</code>
     * @return The dataType.
     */
    @java.lang.Override
    public java.lang.String getDataType() {
      java.lang.Object ref = dataType_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        dataType_ = s;
        return s;
      }
    }
    /**
     * <code>string data_type = 2;</code>
     * @return The bytes for dataType.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getDataTypeBytes() {
      java.lang.Object ref = dataType_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        dataType_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int VALUE_FIELD_NUMBER = 3;
    private com.google.protobuf.Any value_;
    /**
     * <code>.google.protobuf.Any value = 3;</code>
     * @return Whether the value field is set.
     */
    @java.lang.Override
    public boolean hasValue() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>.google.protobuf.Any value = 3;</code>
     * @return The value.
     */
    @java.lang.Override
    public com.google.protobuf.Any getValue() {
      return value_ == null ? com.google.protobuf.Any.getDefaultInstance() : value_;
    }
    /**
     * <code>.google.protobuf.Any value = 3;</code>
     */
    @java.lang.Override
    public com.google.protobuf.AnyOrBuilder getValueOrBuilder() {
      return value_ == null ? com.google.protobuf.Any.getDefaultInstance() : value_;
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (!getNameBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, name_);
      }
      if (!getDataTypeBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, dataType_);
      }
      if (((bitField0_ & 0x00000001) != 0)) {
        output.writeMessage(3, getValue());
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!getNameBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, name_);
      }
      if (!getDataTypeBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, dataType_);
      }
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(3, getValue());
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable other = (org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable) obj;

      if (!getName()
          .equals(other.getName())) return false;
      if (!getDataType()
          .equals(other.getDataType())) return false;
      if (hasValue() != other.hasValue()) return false;
      if (hasValue()) {
        if (!getValue()
            .equals(other.getValue())) return false;
      }
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + NAME_FIELD_NUMBER;
      hash = (53 * hash) + getName().hashCode();
      hash = (37 * hash) + DATA_TYPE_FIELD_NUMBER;
      hash = (53 * hash) + getDataType().hashCode();
      if (hasValue()) {
        hash = (37 * hash) + VALUE_FIELD_NUMBER;
        hash = (53 * hash) + getValue().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.Variable}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.Variable)
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Variable_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Variable_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.class, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
          getValueFieldBuilder();
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        name_ = "";

        dataType_ = "";

        if (valueBuilder_ == null) {
          value_ = null;
        } else {
          valueBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Variable_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable build() {
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable result = new org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        result.name_ = name_;
        result.dataType_ = dataType_;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          if (valueBuilder_ == null) {
            result.value_ = value_;
          } else {
            result.value_ = valueBuilder_.build();
          }
          to_bitField0_ |= 0x00000001;
        }
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.getDefaultInstance()) return this;
        if (!other.getName().isEmpty()) {
          name_ = other.name_;
          onChanged();
        }
        if (!other.getDataType().isEmpty()) {
          dataType_ = other.dataType_;
          onChanged();
        }
        if (other.hasValue()) {
          mergeValue(other.getValue());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.lang.Object name_ = "";
      /**
       * <code>string name = 1;</code>
       * @return The name.
       */
      public java.lang.String getName() {
        java.lang.Object ref = name_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          name_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string name = 1;</code>
       * @return The bytes for name.
       */
      public com.google.protobuf.ByteString
          getNameBytes() {
        java.lang.Object ref = name_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          name_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string name = 1;</code>
       * @param value The name to set.
       * @return This builder for chaining.
       */
      public Builder setName(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        name_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string name = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearName() {
        
        name_ = getDefaultInstance().getName();
        onChanged();
        return this;
      }
      /**
       * <code>string name = 1;</code>
       * @param value The bytes for name to set.
       * @return This builder for chaining.
       */
      public Builder setNameBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        name_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object dataType_ = "";
      /**
       * <code>string data_type = 2;</code>
       * @return The dataType.
       */
      public java.lang.String getDataType() {
        java.lang.Object ref = dataType_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          dataType_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string data_type = 2;</code>
       * @return The bytes for dataType.
       */
      public com.google.protobuf.ByteString
          getDataTypeBytes() {
        java.lang.Object ref = dataType_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          dataType_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string data_type = 2;</code>
       * @param value The dataType to set.
       * @return This builder for chaining.
       */
      public Builder setDataType(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        dataType_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string data_type = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearDataType() {
        
        dataType_ = getDefaultInstance().getDataType();
        onChanged();
        return this;
      }
      /**
       * <code>string data_type = 2;</code>
       * @param value The bytes for dataType to set.
       * @return This builder for chaining.
       */
      public Builder setDataTypeBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        dataType_ = value;
        onChanged();
        return this;
      }

      private com.google.protobuf.Any value_;
      private com.google.protobuf.SingleFieldBuilderV3<
          com.google.protobuf.Any, com.google.protobuf.Any.Builder, com.google.protobuf.AnyOrBuilder> valueBuilder_;
      /**
       * <code>.google.protobuf.Any value = 3;</code>
       * @return Whether the value field is set.
       */
      public boolean hasValue() {
        return ((bitField0_ & 0x00000001) != 0);
      }
      /**
       * <code>.google.protobuf.Any value = 3;</code>
       * @return The value.
       */
      public com.google.protobuf.Any getValue() {
        if (valueBuilder_ == null) {
          return value_ == null ? com.google.protobuf.Any.getDefaultInstance() : value_;
        } else {
          return valueBuilder_.getMessage();
        }
      }
      /**
       * <code>.google.protobuf.Any value = 3;</code>
       */
      public Builder setValue(com.google.protobuf.Any value) {
        if (valueBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          value_ = value;
          onChanged();
        } else {
          valueBuilder_.setMessage(value);
        }
        bitField0_ |= 0x00000001;
        return this;
      }
      /**
       * <code>.google.protobuf.Any value = 3;</code>
       */
      public Builder setValue(
          com.google.protobuf.Any.Builder builderForValue) {
        if (valueBuilder_ == null) {
          value_ = builderForValue.build();
          onChanged();
        } else {
          valueBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00000001;
        return this;
      }
      /**
       * <code>.google.protobuf.Any value = 3;</code>
       */
      public Builder mergeValue(com.google.protobuf.Any value) {
        if (valueBuilder_ == null) {
          if (((bitField0_ & 0x00000001) != 0) &&
              value_ != null &&
              value_ != com.google.protobuf.Any.getDefaultInstance()) {
            value_ =
              com.google.protobuf.Any.newBuilder(value_).mergeFrom(value).buildPartial();
          } else {
            value_ = value;
          }
          onChanged();
        } else {
          valueBuilder_.mergeFrom(value);
        }
        bitField0_ |= 0x00000001;
        return this;
      }
      /**
       * <code>.google.protobuf.Any value = 3;</code>
       */
      public Builder clearValue() {
        if (valueBuilder_ == null) {
          value_ = null;
          onChanged();
        } else {
          valueBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }
      /**
       * <code>.google.protobuf.Any value = 3;</code>
       */
      public com.google.protobuf.Any.Builder getValueBuilder() {
        bitField0_ |= 0x00000001;
        onChanged();
        return getValueFieldBuilder().getBuilder();
      }
      /**
       * <code>.google.protobuf.Any value = 3;</code>
       */
      public com.google.protobuf.AnyOrBuilder getValueOrBuilder() {
        if (valueBuilder_ != null) {
          return valueBuilder_.getMessageOrBuilder();
        } else {
          return value_ == null ?
              com.google.protobuf.Any.getDefaultInstance() : value_;
        }
      }
      /**
       * <code>.google.protobuf.Any value = 3;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          com.google.protobuf.Any, com.google.protobuf.Any.Builder, com.google.protobuf.AnyOrBuilder> 
          getValueFieldBuilder() {
        if (valueBuilder_ == null) {
          valueBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              com.google.protobuf.Any, com.google.protobuf.Any.Builder, com.google.protobuf.AnyOrBuilder>(
                  getValue(),
                  getParentForChildren(),
                  isClean());
          value_ = null;
        }
        return valueBuilder_;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.Variable)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.Variable)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Variable>
        PARSER = new com.google.protobuf.AbstractParser<Variable>() {
      @java.lang.Override
      public Variable parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new Variable(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Variable> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Variable> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface NodeInstanceOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.NodeInstance)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>string id = 1;</code>
     * @return The id.
     */
    java.lang.String getId();
    /**
     * <code>string id = 1;</code>
     * @return The bytes for id.
     */
    com.google.protobuf.ByteString
        getIdBytes();

    /**
     * <code>int64 node_id = 2;</code>
     * @return The nodeId.
     */
    long getNodeId();

    /**
     * <code>.google.protobuf.Any content = 3;</code>
     * @return Whether the content field is set.
     */
    boolean hasContent();
    /**
     * <code>.google.protobuf.Any content = 3;</code>
     * @return The content.
     */
    com.google.protobuf.Any getContent();
    /**
     * <code>.google.protobuf.Any content = 3;</code>
     */
    com.google.protobuf.AnyOrBuilder getContentOrBuilder();

    /**
     * <code>int32 level = 4;</code>
     * @return Whether the level field is set.
     */
    boolean hasLevel();
    /**
     * <code>int32 level = 4;</code>
     * @return The level.
     */
    int getLevel();

    /**
     * <code>int64 trigger_date = 5;</code>
     * @return Whether the triggerDate field is set.
     */
    boolean hasTriggerDate();
    /**
     * <code>int64 trigger_date = 5;</code>
     * @return The triggerDate.
     */
    long getTriggerDate();

    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.SLAContext sla = 6;</code>
     * @return Whether the sla field is set.
     */
    boolean hasSla();
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.SLAContext sla = 6;</code>
     * @return The sla.
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext getSla();
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.SLAContext sla = 6;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContextOrBuilder getSlaOrBuilder();
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.NodeInstance}
   */
  public static final class NodeInstance extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.NodeInstance)
      NodeInstanceOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use NodeInstance.newBuilder() to construct.
    private NodeInstance(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private NodeInstance() {
      id_ = "";
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new NodeInstance();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private NodeInstance(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 10: {
              java.lang.String s = input.readStringRequireUtf8();

              id_ = s;
              break;
            }
            case 16: {

              nodeId_ = input.readInt64();
              break;
            }
            case 26: {
              com.google.protobuf.Any.Builder subBuilder = null;
              if (content_ != null) {
                subBuilder = content_.toBuilder();
              }
              content_ = input.readMessage(com.google.protobuf.Any.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(content_);
                content_ = subBuilder.buildPartial();
              }

              break;
            }
            case 32: {
              bitField0_ |= 0x00000001;
              level_ = input.readInt32();
              break;
            }
            case 40: {
              bitField0_ |= 0x00000002;
              triggerDate_ = input.readInt64();
              break;
            }
            case 50: {
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.Builder subBuilder = null;
              if (((bitField0_ & 0x00000004) != 0)) {
                subBuilder = sla_.toBuilder();
              }
              sla_ = input.readMessage(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(sla_);
                sla_ = subBuilder.buildPartial();
              }
              bitField0_ |= 0x00000004;
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_NodeInstance_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_NodeInstance_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance.class, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance.Builder.class);
    }

    private int bitField0_;
    public static final int ID_FIELD_NUMBER = 1;
    private volatile java.lang.Object id_;
    /**
     * <code>string id = 1;</code>
     * @return The id.
     */
    @java.lang.Override
    public java.lang.String getId() {
      java.lang.Object ref = id_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        id_ = s;
        return s;
      }
    }
    /**
     * <code>string id = 1;</code>
     * @return The bytes for id.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getIdBytes() {
      java.lang.Object ref = id_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        id_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int NODE_ID_FIELD_NUMBER = 2;
    private long nodeId_;
    /**
     * <code>int64 node_id = 2;</code>
     * @return The nodeId.
     */
    @java.lang.Override
    public long getNodeId() {
      return nodeId_;
    }

    public static final int CONTENT_FIELD_NUMBER = 3;
    private com.google.protobuf.Any content_;
    /**
     * <code>.google.protobuf.Any content = 3;</code>
     * @return Whether the content field is set.
     */
    @java.lang.Override
    public boolean hasContent() {
      return content_ != null;
    }
    /**
     * <code>.google.protobuf.Any content = 3;</code>
     * @return The content.
     */
    @java.lang.Override
    public com.google.protobuf.Any getContent() {
      return content_ == null ? com.google.protobuf.Any.getDefaultInstance() : content_;
    }
    /**
     * <code>.google.protobuf.Any content = 3;</code>
     */
    @java.lang.Override
    public com.google.protobuf.AnyOrBuilder getContentOrBuilder() {
      return getContent();
    }

    public static final int LEVEL_FIELD_NUMBER = 4;
    private int level_;
    /**
     * <code>int32 level = 4;</code>
     * @return Whether the level field is set.
     */
    @java.lang.Override
    public boolean hasLevel() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>int32 level = 4;</code>
     * @return The level.
     */
    @java.lang.Override
    public int getLevel() {
      return level_;
    }

    public static final int TRIGGER_DATE_FIELD_NUMBER = 5;
    private long triggerDate_;
    /**
     * <code>int64 trigger_date = 5;</code>
     * @return Whether the triggerDate field is set.
     */
    @java.lang.Override
    public boolean hasTriggerDate() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>int64 trigger_date = 5;</code>
     * @return The triggerDate.
     */
    @java.lang.Override
    public long getTriggerDate() {
      return triggerDate_;
    }

    public static final int SLA_FIELD_NUMBER = 6;
    private org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext sla_;
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.SLAContext sla = 6;</code>
     * @return Whether the sla field is set.
     */
    @java.lang.Override
    public boolean hasSla() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.SLAContext sla = 6;</code>
     * @return The sla.
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext getSla() {
      return sla_ == null ? org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.getDefaultInstance() : sla_;
    }
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.SLAContext sla = 6;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContextOrBuilder getSlaOrBuilder() {
      return sla_ == null ? org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.getDefaultInstance() : sla_;
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (!getIdBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, id_);
      }
      if (nodeId_ != 0L) {
        output.writeInt64(2, nodeId_);
      }
      if (content_ != null) {
        output.writeMessage(3, getContent());
      }
      if (((bitField0_ & 0x00000001) != 0)) {
        output.writeInt32(4, level_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        output.writeInt64(5, triggerDate_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        output.writeMessage(6, getSla());
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!getIdBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, id_);
      }
      if (nodeId_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(2, nodeId_);
      }
      if (content_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(3, getContent());
      }
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(4, level_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(5, triggerDate_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(6, getSla());
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance other = (org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance) obj;

      if (!getId()
          .equals(other.getId())) return false;
      if (getNodeId()
          != other.getNodeId()) return false;
      if (hasContent() != other.hasContent()) return false;
      if (hasContent()) {
        if (!getContent()
            .equals(other.getContent())) return false;
      }
      if (hasLevel() != other.hasLevel()) return false;
      if (hasLevel()) {
        if (getLevel()
            != other.getLevel()) return false;
      }
      if (hasTriggerDate() != other.hasTriggerDate()) return false;
      if (hasTriggerDate()) {
        if (getTriggerDate()
            != other.getTriggerDate()) return false;
      }
      if (hasSla() != other.hasSla()) return false;
      if (hasSla()) {
        if (!getSla()
            .equals(other.getSla())) return false;
      }
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      hash = (37 * hash) + ID_FIELD_NUMBER;
      hash = (53 * hash) + getId().hashCode();
      hash = (37 * hash) + NODE_ID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getNodeId());
      if (hasContent()) {
        hash = (37 * hash) + CONTENT_FIELD_NUMBER;
        hash = (53 * hash) + getContent().hashCode();
      }
      if (hasLevel()) {
        hash = (37 * hash) + LEVEL_FIELD_NUMBER;
        hash = (53 * hash) + getLevel();
      }
      if (hasTriggerDate()) {
        hash = (37 * hash) + TRIGGER_DATE_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
            getTriggerDate());
      }
      if (hasSla()) {
        hash = (37 * hash) + SLA_FIELD_NUMBER;
        hash = (53 * hash) + getSla().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.NodeInstance}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.NodeInstance)
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_NodeInstance_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_NodeInstance_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance.class, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
          getSlaFieldBuilder();
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        id_ = "";

        nodeId_ = 0L;

        if (contentBuilder_ == null) {
          content_ = null;
        } else {
          content_ = null;
          contentBuilder_ = null;
        }
        level_ = 0;
        bitField0_ = (bitField0_ & ~0x00000001);
        triggerDate_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000002);
        if (slaBuilder_ == null) {
          sla_ = null;
        } else {
          slaBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000004);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_NodeInstance_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance build() {
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance result = new org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        result.id_ = id_;
        result.nodeId_ = nodeId_;
        if (contentBuilder_ == null) {
          result.content_ = content_;
        } else {
          result.content_ = contentBuilder_.build();
        }
        if (((from_bitField0_ & 0x00000001) != 0)) {
          result.level_ = level_;
          to_bitField0_ |= 0x00000001;
        }
        if (((from_bitField0_ & 0x00000002) != 0)) {
          result.triggerDate_ = triggerDate_;
          to_bitField0_ |= 0x00000002;
        }
        if (((from_bitField0_ & 0x00000004) != 0)) {
          if (slaBuilder_ == null) {
            result.sla_ = sla_;
          } else {
            result.sla_ = slaBuilder_.build();
          }
          to_bitField0_ |= 0x00000004;
        }
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance.getDefaultInstance()) return this;
        if (!other.getId().isEmpty()) {
          id_ = other.id_;
          onChanged();
        }
        if (other.getNodeId() != 0L) {
          setNodeId(other.getNodeId());
        }
        if (other.hasContent()) {
          mergeContent(other.getContent());
        }
        if (other.hasLevel()) {
          setLevel(other.getLevel());
        }
        if (other.hasTriggerDate()) {
          setTriggerDate(other.getTriggerDate());
        }
        if (other.hasSla()) {
          mergeSla(other.getSla());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.lang.Object id_ = "";
      /**
       * <code>string id = 1;</code>
       * @return The id.
       */
      public java.lang.String getId() {
        java.lang.Object ref = id_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          id_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string id = 1;</code>
       * @return The bytes for id.
       */
      public com.google.protobuf.ByteString
          getIdBytes() {
        java.lang.Object ref = id_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          id_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string id = 1;</code>
       * @param value The id to set.
       * @return This builder for chaining.
       */
      public Builder setId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        id_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string id = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearId() {
        
        id_ = getDefaultInstance().getId();
        onChanged();
        return this;
      }
      /**
       * <code>string id = 1;</code>
       * @param value The bytes for id to set.
       * @return This builder for chaining.
       */
      public Builder setIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        id_ = value;
        onChanged();
        return this;
      }

      private long nodeId_ ;
      /**
       * <code>int64 node_id = 2;</code>
       * @return The nodeId.
       */
      @java.lang.Override
      public long getNodeId() {
        return nodeId_;
      }
      /**
       * <code>int64 node_id = 2;</code>
       * @param value The nodeId to set.
       * @return This builder for chaining.
       */
      public Builder setNodeId(long value) {
        
        nodeId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int64 node_id = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearNodeId() {
        
        nodeId_ = 0L;
        onChanged();
        return this;
      }

      private com.google.protobuf.Any content_;
      private com.google.protobuf.SingleFieldBuilderV3<
          com.google.protobuf.Any, com.google.protobuf.Any.Builder, com.google.protobuf.AnyOrBuilder> contentBuilder_;
      /**
       * <code>.google.protobuf.Any content = 3;</code>
       * @return Whether the content field is set.
       */
      public boolean hasContent() {
        return contentBuilder_ != null || content_ != null;
      }
      /**
       * <code>.google.protobuf.Any content = 3;</code>
       * @return The content.
       */
      public com.google.protobuf.Any getContent() {
        if (contentBuilder_ == null) {
          return content_ == null ? com.google.protobuf.Any.getDefaultInstance() : content_;
        } else {
          return contentBuilder_.getMessage();
        }
      }
      /**
       * <code>.google.protobuf.Any content = 3;</code>
       */
      public Builder setContent(com.google.protobuf.Any value) {
        if (contentBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          content_ = value;
          onChanged();
        } else {
          contentBuilder_.setMessage(value);
        }

        return this;
      }
      /**
       * <code>.google.protobuf.Any content = 3;</code>
       */
      public Builder setContent(
          com.google.protobuf.Any.Builder builderForValue) {
        if (contentBuilder_ == null) {
          content_ = builderForValue.build();
          onChanged();
        } else {
          contentBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }
      /**
       * <code>.google.protobuf.Any content = 3;</code>
       */
      public Builder mergeContent(com.google.protobuf.Any value) {
        if (contentBuilder_ == null) {
          if (content_ != null) {
            content_ =
              com.google.protobuf.Any.newBuilder(content_).mergeFrom(value).buildPartial();
          } else {
            content_ = value;
          }
          onChanged();
        } else {
          contentBuilder_.mergeFrom(value);
        }

        return this;
      }
      /**
       * <code>.google.protobuf.Any content = 3;</code>
       */
      public Builder clearContent() {
        if (contentBuilder_ == null) {
          content_ = null;
          onChanged();
        } else {
          content_ = null;
          contentBuilder_ = null;
        }

        return this;
      }
      /**
       * <code>.google.protobuf.Any content = 3;</code>
       */
      public com.google.protobuf.Any.Builder getContentBuilder() {
        
        onChanged();
        return getContentFieldBuilder().getBuilder();
      }
      /**
       * <code>.google.protobuf.Any content = 3;</code>
       */
      public com.google.protobuf.AnyOrBuilder getContentOrBuilder() {
        if (contentBuilder_ != null) {
          return contentBuilder_.getMessageOrBuilder();
        } else {
          return content_ == null ?
              com.google.protobuf.Any.getDefaultInstance() : content_;
        }
      }
      /**
       * <code>.google.protobuf.Any content = 3;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          com.google.protobuf.Any, com.google.protobuf.Any.Builder, com.google.protobuf.AnyOrBuilder> 
          getContentFieldBuilder() {
        if (contentBuilder_ == null) {
          contentBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              com.google.protobuf.Any, com.google.protobuf.Any.Builder, com.google.protobuf.AnyOrBuilder>(
                  getContent(),
                  getParentForChildren(),
                  isClean());
          content_ = null;
        }
        return contentBuilder_;
      }

      private int level_ ;
      /**
       * <code>int32 level = 4;</code>
       * @return Whether the level field is set.
       */
      @java.lang.Override
      public boolean hasLevel() {
        return ((bitField0_ & 0x00000001) != 0);
      }
      /**
       * <code>int32 level = 4;</code>
       * @return The level.
       */
      @java.lang.Override
      public int getLevel() {
        return level_;
      }
      /**
       * <code>int32 level = 4;</code>
       * @param value The level to set.
       * @return This builder for chaining.
       */
      public Builder setLevel(int value) {
        bitField0_ |= 0x00000001;
        level_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 level = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearLevel() {
        bitField0_ = (bitField0_ & ~0x00000001);
        level_ = 0;
        onChanged();
        return this;
      }

      private long triggerDate_ ;
      /**
       * <code>int64 trigger_date = 5;</code>
       * @return Whether the triggerDate field is set.
       */
      @java.lang.Override
      public boolean hasTriggerDate() {
        return ((bitField0_ & 0x00000002) != 0);
      }
      /**
       * <code>int64 trigger_date = 5;</code>
       * @return The triggerDate.
       */
      @java.lang.Override
      public long getTriggerDate() {
        return triggerDate_;
      }
      /**
       * <code>int64 trigger_date = 5;</code>
       * @param value The triggerDate to set.
       * @return This builder for chaining.
       */
      public Builder setTriggerDate(long value) {
        bitField0_ |= 0x00000002;
        triggerDate_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int64 trigger_date = 5;</code>
       * @return This builder for chaining.
       */
      public Builder clearTriggerDate() {
        bitField0_ = (bitField0_ & ~0x00000002);
        triggerDate_ = 0L;
        onChanged();
        return this;
      }

      private org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext sla_;
      private com.google.protobuf.SingleFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContextOrBuilder> slaBuilder_;
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.SLAContext sla = 6;</code>
       * @return Whether the sla field is set.
       */
      public boolean hasSla() {
        return ((bitField0_ & 0x00000004) != 0);
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.SLAContext sla = 6;</code>
       * @return The sla.
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext getSla() {
        if (slaBuilder_ == null) {
          return sla_ == null ? org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.getDefaultInstance() : sla_;
        } else {
          return slaBuilder_.getMessage();
        }
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.SLAContext sla = 6;</code>
       */
      public Builder setSla(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext value) {
        if (slaBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          sla_ = value;
          onChanged();
        } else {
          slaBuilder_.setMessage(value);
        }
        bitField0_ |= 0x00000004;
        return this;
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.SLAContext sla = 6;</code>
       */
      public Builder setSla(
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.Builder builderForValue) {
        if (slaBuilder_ == null) {
          sla_ = builderForValue.build();
          onChanged();
        } else {
          slaBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00000004;
        return this;
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.SLAContext sla = 6;</code>
       */
      public Builder mergeSla(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext value) {
        if (slaBuilder_ == null) {
          if (((bitField0_ & 0x00000004) != 0) &&
              sla_ != null &&
              sla_ != org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.getDefaultInstance()) {
            sla_ =
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.newBuilder(sla_).mergeFrom(value).buildPartial();
          } else {
            sla_ = value;
          }
          onChanged();
        } else {
          slaBuilder_.mergeFrom(value);
        }
        bitField0_ |= 0x00000004;
        return this;
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.SLAContext sla = 6;</code>
       */
      public Builder clearSla() {
        if (slaBuilder_ == null) {
          sla_ = null;
          onChanged();
        } else {
          slaBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000004);
        return this;
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.SLAContext sla = 6;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.Builder getSlaBuilder() {
        bitField0_ |= 0x00000004;
        onChanged();
        return getSlaFieldBuilder().getBuilder();
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.SLAContext sla = 6;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContextOrBuilder getSlaOrBuilder() {
        if (slaBuilder_ != null) {
          return slaBuilder_.getMessageOrBuilder();
        } else {
          return sla_ == null ?
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.getDefaultInstance() : sla_;
        }
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.SLAContext sla = 6;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContextOrBuilder> 
          getSlaFieldBuilder() {
        if (slaBuilder_ == null) {
          slaBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContextOrBuilder>(
                  getSla(),
                  getParentForChildren(),
                  isClean());
          sla_ = null;
        }
        return slaBuilder_;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.NodeInstance)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.NodeInstance)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<NodeInstance>
        PARSER = new com.google.protobuf.AbstractParser<NodeInstance>() {
      @java.lang.Override
      public NodeInstance parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new NodeInstance(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<NodeInstance> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<NodeInstance> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface WorkflowContextOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.WorkflowContext)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
     */
    java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable> 
        getVariableList();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable getVariable(int index);
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
     */
    int getVariableCount();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
     */
    java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder> 
        getVariableOrBuilderList();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder getVariableOrBuilder(
        int index);

    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
     */
    java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance> 
        getNodeInstanceList();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance getNodeInstance(int index);
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
     */
    int getNodeInstanceCount();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
     */
    java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceOrBuilder> 
        getNodeInstanceOrBuilderList();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceOrBuilder getNodeInstanceOrBuilder(
        int index);

    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
     */
    java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup> 
        getExclusiveGroupList();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup getExclusiveGroup(int index);
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
     */
    int getExclusiveGroupCount();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
     */
    java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroupOrBuilder> 
        getExclusiveGroupOrBuilderList();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroupOrBuilder getExclusiveGroupOrBuilder(
        int index);

    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
     */
    java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel> 
        getIterationLevelsList();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel getIterationLevels(int index);
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
     */
    int getIterationLevelsCount();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
     */
    java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevelOrBuilder> 
        getIterationLevelsOrBuilderList();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevelOrBuilder getIterationLevelsOrBuilder(
        int index);
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.WorkflowContext}
   */
  public static final class WorkflowContext extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.WorkflowContext)
      WorkflowContextOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use WorkflowContext.newBuilder() to construct.
    private WorkflowContext(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private WorkflowContext() {
      variable_ = java.util.Collections.emptyList();
      nodeInstance_ = java.util.Collections.emptyList();
      exclusiveGroup_ = java.util.Collections.emptyList();
      iterationLevels_ = java.util.Collections.emptyList();
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new WorkflowContext();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private WorkflowContext(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 10: {
              if (!((mutable_bitField0_ & 0x00000001) != 0)) {
                variable_ = new java.util.ArrayList<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable>();
                mutable_bitField0_ |= 0x00000001;
              }
              variable_.add(
                  input.readMessage(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.parser(), extensionRegistry));
              break;
            }
            case 18: {
              if (!((mutable_bitField0_ & 0x00000002) != 0)) {
                nodeInstance_ = new java.util.ArrayList<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance>();
                mutable_bitField0_ |= 0x00000002;
              }
              nodeInstance_.add(
                  input.readMessage(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance.parser(), extensionRegistry));
              break;
            }
            case 26: {
              if (!((mutable_bitField0_ & 0x00000004) != 0)) {
                exclusiveGroup_ = new java.util.ArrayList<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup>();
                mutable_bitField0_ |= 0x00000004;
              }
              exclusiveGroup_.add(
                  input.readMessage(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup.parser(), extensionRegistry));
              break;
            }
            case 34: {
              if (!((mutable_bitField0_ & 0x00000008) != 0)) {
                iterationLevels_ = new java.util.ArrayList<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel>();
                mutable_bitField0_ |= 0x00000008;
              }
              iterationLevels_.add(
                  input.readMessage(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel.parser(), extensionRegistry));
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        if (((mutable_bitField0_ & 0x00000001) != 0)) {
          variable_ = java.util.Collections.unmodifiableList(variable_);
        }
        if (((mutable_bitField0_ & 0x00000002) != 0)) {
          nodeInstance_ = java.util.Collections.unmodifiableList(nodeInstance_);
        }
        if (((mutable_bitField0_ & 0x00000004) != 0)) {
          exclusiveGroup_ = java.util.Collections.unmodifiableList(exclusiveGroup_);
        }
        if (((mutable_bitField0_ & 0x00000008) != 0)) {
          iterationLevels_ = java.util.Collections.unmodifiableList(iterationLevels_);
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_WorkflowContext_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_WorkflowContext_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.class, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder.class);
    }

    public static final int VARIABLE_FIELD_NUMBER = 1;
    private java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable> variable_;
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
     */
    @java.lang.Override
    public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable> getVariableList() {
      return variable_;
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
     */
    @java.lang.Override
    public java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder> 
        getVariableOrBuilderList() {
      return variable_;
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
     */
    @java.lang.Override
    public int getVariableCount() {
      return variable_.size();
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable getVariable(int index) {
      return variable_.get(index);
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder getVariableOrBuilder(
        int index) {
      return variable_.get(index);
    }

    public static final int NODE_INSTANCE_FIELD_NUMBER = 2;
    private java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance> nodeInstance_;
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
     */
    @java.lang.Override
    public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance> getNodeInstanceList() {
      return nodeInstance_;
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
     */
    @java.lang.Override
    public java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceOrBuilder> 
        getNodeInstanceOrBuilderList() {
      return nodeInstance_;
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
     */
    @java.lang.Override
    public int getNodeInstanceCount() {
      return nodeInstance_.size();
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance getNodeInstance(int index) {
      return nodeInstance_.get(index);
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceOrBuilder getNodeInstanceOrBuilder(
        int index) {
      return nodeInstance_.get(index);
    }

    public static final int EXCLUSIVE_GROUP_FIELD_NUMBER = 3;
    private java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup> exclusiveGroup_;
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
     */
    @java.lang.Override
    public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup> getExclusiveGroupList() {
      return exclusiveGroup_;
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
     */
    @java.lang.Override
    public java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroupOrBuilder> 
        getExclusiveGroupOrBuilderList() {
      return exclusiveGroup_;
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
     */
    @java.lang.Override
    public int getExclusiveGroupCount() {
      return exclusiveGroup_.size();
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup getExclusiveGroup(int index) {
      return exclusiveGroup_.get(index);
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroupOrBuilder getExclusiveGroupOrBuilder(
        int index) {
      return exclusiveGroup_.get(index);
    }

    public static final int ITERATIONLEVELS_FIELD_NUMBER = 4;
    private java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel> iterationLevels_;
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
     */
    @java.lang.Override
    public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel> getIterationLevelsList() {
      return iterationLevels_;
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
     */
    @java.lang.Override
    public java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevelOrBuilder> 
        getIterationLevelsOrBuilderList() {
      return iterationLevels_;
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
     */
    @java.lang.Override
    public int getIterationLevelsCount() {
      return iterationLevels_.size();
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel getIterationLevels(int index) {
      return iterationLevels_.get(index);
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevelOrBuilder getIterationLevelsOrBuilder(
        int index) {
      return iterationLevels_.get(index);
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      for (int i = 0; i < variable_.size(); i++) {
        output.writeMessage(1, variable_.get(i));
      }
      for (int i = 0; i < nodeInstance_.size(); i++) {
        output.writeMessage(2, nodeInstance_.get(i));
      }
      for (int i = 0; i < exclusiveGroup_.size(); i++) {
        output.writeMessage(3, exclusiveGroup_.get(i));
      }
      for (int i = 0; i < iterationLevels_.size(); i++) {
        output.writeMessage(4, iterationLevels_.get(i));
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      for (int i = 0; i < variable_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(1, variable_.get(i));
      }
      for (int i = 0; i < nodeInstance_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, nodeInstance_.get(i));
      }
      for (int i = 0; i < exclusiveGroup_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(3, exclusiveGroup_.get(i));
      }
      for (int i = 0; i < iterationLevels_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(4, iterationLevels_.get(i));
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext other = (org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext) obj;

      if (!getVariableList()
          .equals(other.getVariableList())) return false;
      if (!getNodeInstanceList()
          .equals(other.getNodeInstanceList())) return false;
      if (!getExclusiveGroupList()
          .equals(other.getExclusiveGroupList())) return false;
      if (!getIterationLevelsList()
          .equals(other.getIterationLevelsList())) return false;
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (getVariableCount() > 0) {
        hash = (37 * hash) + VARIABLE_FIELD_NUMBER;
        hash = (53 * hash) + getVariableList().hashCode();
      }
      if (getNodeInstanceCount() > 0) {
        hash = (37 * hash) + NODE_INSTANCE_FIELD_NUMBER;
        hash = (53 * hash) + getNodeInstanceList().hashCode();
      }
      if (getExclusiveGroupCount() > 0) {
        hash = (37 * hash) + EXCLUSIVE_GROUP_FIELD_NUMBER;
        hash = (53 * hash) + getExclusiveGroupList().hashCode();
      }
      if (getIterationLevelsCount() > 0) {
        hash = (37 * hash) + ITERATIONLEVELS_FIELD_NUMBER;
        hash = (53 * hash) + getIterationLevelsList().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.WorkflowContext}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.WorkflowContext)
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_WorkflowContext_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_WorkflowContext_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.class, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
          getVariableFieldBuilder();
          getNodeInstanceFieldBuilder();
          getExclusiveGroupFieldBuilder();
          getIterationLevelsFieldBuilder();
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        if (variableBuilder_ == null) {
          variable_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000001);
        } else {
          variableBuilder_.clear();
        }
        if (nodeInstanceBuilder_ == null) {
          nodeInstance_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000002);
        } else {
          nodeInstanceBuilder_.clear();
        }
        if (exclusiveGroupBuilder_ == null) {
          exclusiveGroup_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000004);
        } else {
          exclusiveGroupBuilder_.clear();
        }
        if (iterationLevelsBuilder_ == null) {
          iterationLevels_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000008);
        } else {
          iterationLevelsBuilder_.clear();
        }
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_WorkflowContext_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext build() {
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext result = new org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext(this);
        int from_bitField0_ = bitField0_;
        if (variableBuilder_ == null) {
          if (((bitField0_ & 0x00000001) != 0)) {
            variable_ = java.util.Collections.unmodifiableList(variable_);
            bitField0_ = (bitField0_ & ~0x00000001);
          }
          result.variable_ = variable_;
        } else {
          result.variable_ = variableBuilder_.build();
        }
        if (nodeInstanceBuilder_ == null) {
          if (((bitField0_ & 0x00000002) != 0)) {
            nodeInstance_ = java.util.Collections.unmodifiableList(nodeInstance_);
            bitField0_ = (bitField0_ & ~0x00000002);
          }
          result.nodeInstance_ = nodeInstance_;
        } else {
          result.nodeInstance_ = nodeInstanceBuilder_.build();
        }
        if (exclusiveGroupBuilder_ == null) {
          if (((bitField0_ & 0x00000004) != 0)) {
            exclusiveGroup_ = java.util.Collections.unmodifiableList(exclusiveGroup_);
            bitField0_ = (bitField0_ & ~0x00000004);
          }
          result.exclusiveGroup_ = exclusiveGroup_;
        } else {
          result.exclusiveGroup_ = exclusiveGroupBuilder_.build();
        }
        if (iterationLevelsBuilder_ == null) {
          if (((bitField0_ & 0x00000008) != 0)) {
            iterationLevels_ = java.util.Collections.unmodifiableList(iterationLevels_);
            bitField0_ = (bitField0_ & ~0x00000008);
          }
          result.iterationLevels_ = iterationLevels_;
        } else {
          result.iterationLevels_ = iterationLevelsBuilder_.build();
        }
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.getDefaultInstance()) return this;
        if (variableBuilder_ == null) {
          if (!other.variable_.isEmpty()) {
            if (variable_.isEmpty()) {
              variable_ = other.variable_;
              bitField0_ = (bitField0_ & ~0x00000001);
            } else {
              ensureVariableIsMutable();
              variable_.addAll(other.variable_);
            }
            onChanged();
          }
        } else {
          if (!other.variable_.isEmpty()) {
            if (variableBuilder_.isEmpty()) {
              variableBuilder_.dispose();
              variableBuilder_ = null;
              variable_ = other.variable_;
              bitField0_ = (bitField0_ & ~0x00000001);
              variableBuilder_ = 
                com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                   getVariableFieldBuilder() : null;
            } else {
              variableBuilder_.addAllMessages(other.variable_);
            }
          }
        }
        if (nodeInstanceBuilder_ == null) {
          if (!other.nodeInstance_.isEmpty()) {
            if (nodeInstance_.isEmpty()) {
              nodeInstance_ = other.nodeInstance_;
              bitField0_ = (bitField0_ & ~0x00000002);
            } else {
              ensureNodeInstanceIsMutable();
              nodeInstance_.addAll(other.nodeInstance_);
            }
            onChanged();
          }
        } else {
          if (!other.nodeInstance_.isEmpty()) {
            if (nodeInstanceBuilder_.isEmpty()) {
              nodeInstanceBuilder_.dispose();
              nodeInstanceBuilder_ = null;
              nodeInstance_ = other.nodeInstance_;
              bitField0_ = (bitField0_ & ~0x00000002);
              nodeInstanceBuilder_ = 
                com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                   getNodeInstanceFieldBuilder() : null;
            } else {
              nodeInstanceBuilder_.addAllMessages(other.nodeInstance_);
            }
          }
        }
        if (exclusiveGroupBuilder_ == null) {
          if (!other.exclusiveGroup_.isEmpty()) {
            if (exclusiveGroup_.isEmpty()) {
              exclusiveGroup_ = other.exclusiveGroup_;
              bitField0_ = (bitField0_ & ~0x00000004);
            } else {
              ensureExclusiveGroupIsMutable();
              exclusiveGroup_.addAll(other.exclusiveGroup_);
            }
            onChanged();
          }
        } else {
          if (!other.exclusiveGroup_.isEmpty()) {
            if (exclusiveGroupBuilder_.isEmpty()) {
              exclusiveGroupBuilder_.dispose();
              exclusiveGroupBuilder_ = null;
              exclusiveGroup_ = other.exclusiveGroup_;
              bitField0_ = (bitField0_ & ~0x00000004);
              exclusiveGroupBuilder_ = 
                com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                   getExclusiveGroupFieldBuilder() : null;
            } else {
              exclusiveGroupBuilder_.addAllMessages(other.exclusiveGroup_);
            }
          }
        }
        if (iterationLevelsBuilder_ == null) {
          if (!other.iterationLevels_.isEmpty()) {
            if (iterationLevels_.isEmpty()) {
              iterationLevels_ = other.iterationLevels_;
              bitField0_ = (bitField0_ & ~0x00000008);
            } else {
              ensureIterationLevelsIsMutable();
              iterationLevels_.addAll(other.iterationLevels_);
            }
            onChanged();
          }
        } else {
          if (!other.iterationLevels_.isEmpty()) {
            if (iterationLevelsBuilder_.isEmpty()) {
              iterationLevelsBuilder_.dispose();
              iterationLevelsBuilder_ = null;
              iterationLevels_ = other.iterationLevels_;
              bitField0_ = (bitField0_ & ~0x00000008);
              iterationLevelsBuilder_ = 
                com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                   getIterationLevelsFieldBuilder() : null;
            } else {
              iterationLevelsBuilder_.addAllMessages(other.iterationLevels_);
            }
          }
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable> variable_ =
        java.util.Collections.emptyList();
      private void ensureVariableIsMutable() {
        if (!((bitField0_ & 0x00000001) != 0)) {
          variable_ = new java.util.ArrayList<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable>(variable_);
          bitField0_ |= 0x00000001;
         }
      }

      private com.google.protobuf.RepeatedFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder> variableBuilder_;

      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
       */
      public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable> getVariableList() {
        if (variableBuilder_ == null) {
          return java.util.Collections.unmodifiableList(variable_);
        } else {
          return variableBuilder_.getMessageList();
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
       */
      public int getVariableCount() {
        if (variableBuilder_ == null) {
          return variable_.size();
        } else {
          return variableBuilder_.getCount();
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable getVariable(int index) {
        if (variableBuilder_ == null) {
          return variable_.get(index);
        } else {
          return variableBuilder_.getMessage(index);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
       */
      public Builder setVariable(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable value) {
        if (variableBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureVariableIsMutable();
          variable_.set(index, value);
          onChanged();
        } else {
          variableBuilder_.setMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
       */
      public Builder setVariable(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder builderForValue) {
        if (variableBuilder_ == null) {
          ensureVariableIsMutable();
          variable_.set(index, builderForValue.build());
          onChanged();
        } else {
          variableBuilder_.setMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
       */
      public Builder addVariable(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable value) {
        if (variableBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureVariableIsMutable();
          variable_.add(value);
          onChanged();
        } else {
          variableBuilder_.addMessage(value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
       */
      public Builder addVariable(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable value) {
        if (variableBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureVariableIsMutable();
          variable_.add(index, value);
          onChanged();
        } else {
          variableBuilder_.addMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
       */
      public Builder addVariable(
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder builderForValue) {
        if (variableBuilder_ == null) {
          ensureVariableIsMutable();
          variable_.add(builderForValue.build());
          onChanged();
        } else {
          variableBuilder_.addMessage(builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
       */
      public Builder addVariable(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder builderForValue) {
        if (variableBuilder_ == null) {
          ensureVariableIsMutable();
          variable_.add(index, builderForValue.build());
          onChanged();
        } else {
          variableBuilder_.addMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
       */
      public Builder addAllVariable(
          java.lang.Iterable<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable> values) {
        if (variableBuilder_ == null) {
          ensureVariableIsMutable();
          com.google.protobuf.AbstractMessageLite.Builder.addAll(
              values, variable_);
          onChanged();
        } else {
          variableBuilder_.addAllMessages(values);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
       */
      public Builder clearVariable() {
        if (variableBuilder_ == null) {
          variable_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000001);
          onChanged();
        } else {
          variableBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
       */
      public Builder removeVariable(int index) {
        if (variableBuilder_ == null) {
          ensureVariableIsMutable();
          variable_.remove(index);
          onChanged();
        } else {
          variableBuilder_.remove(index);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder getVariableBuilder(
          int index) {
        return getVariableFieldBuilder().getBuilder(index);
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder getVariableOrBuilder(
          int index) {
        if (variableBuilder_ == null) {
          return variable_.get(index);  } else {
          return variableBuilder_.getMessageOrBuilder(index);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
       */
      public java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder> 
           getVariableOrBuilderList() {
        if (variableBuilder_ != null) {
          return variableBuilder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(variable_);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder addVariableBuilder() {
        return getVariableFieldBuilder().addBuilder(
            org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.getDefaultInstance());
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder addVariableBuilder(
          int index) {
        return getVariableFieldBuilder().addBuilder(
            index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.getDefaultInstance());
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 1;</code>
       */
      public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder> 
           getVariableBuilderList() {
        return getVariableFieldBuilder().getBuilderList();
      }
      private com.google.protobuf.RepeatedFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder> 
          getVariableFieldBuilder() {
        if (variableBuilder_ == null) {
          variableBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder>(
                  variable_,
                  ((bitField0_ & 0x00000001) != 0),
                  getParentForChildren(),
                  isClean());
          variable_ = null;
        }
        return variableBuilder_;
      }

      private java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance> nodeInstance_ =
        java.util.Collections.emptyList();
      private void ensureNodeInstanceIsMutable() {
        if (!((bitField0_ & 0x00000002) != 0)) {
          nodeInstance_ = new java.util.ArrayList<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance>(nodeInstance_);
          bitField0_ |= 0x00000002;
         }
      }

      private com.google.protobuf.RepeatedFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceOrBuilder> nodeInstanceBuilder_;

      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
       */
      public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance> getNodeInstanceList() {
        if (nodeInstanceBuilder_ == null) {
          return java.util.Collections.unmodifiableList(nodeInstance_);
        } else {
          return nodeInstanceBuilder_.getMessageList();
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
       */
      public int getNodeInstanceCount() {
        if (nodeInstanceBuilder_ == null) {
          return nodeInstance_.size();
        } else {
          return nodeInstanceBuilder_.getCount();
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance getNodeInstance(int index) {
        if (nodeInstanceBuilder_ == null) {
          return nodeInstance_.get(index);
        } else {
          return nodeInstanceBuilder_.getMessage(index);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
       */
      public Builder setNodeInstance(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance value) {
        if (nodeInstanceBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureNodeInstanceIsMutable();
          nodeInstance_.set(index, value);
          onChanged();
        } else {
          nodeInstanceBuilder_.setMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
       */
      public Builder setNodeInstance(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance.Builder builderForValue) {
        if (nodeInstanceBuilder_ == null) {
          ensureNodeInstanceIsMutable();
          nodeInstance_.set(index, builderForValue.build());
          onChanged();
        } else {
          nodeInstanceBuilder_.setMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
       */
      public Builder addNodeInstance(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance value) {
        if (nodeInstanceBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureNodeInstanceIsMutable();
          nodeInstance_.add(value);
          onChanged();
        } else {
          nodeInstanceBuilder_.addMessage(value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
       */
      public Builder addNodeInstance(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance value) {
        if (nodeInstanceBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureNodeInstanceIsMutable();
          nodeInstance_.add(index, value);
          onChanged();
        } else {
          nodeInstanceBuilder_.addMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
       */
      public Builder addNodeInstance(
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance.Builder builderForValue) {
        if (nodeInstanceBuilder_ == null) {
          ensureNodeInstanceIsMutable();
          nodeInstance_.add(builderForValue.build());
          onChanged();
        } else {
          nodeInstanceBuilder_.addMessage(builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
       */
      public Builder addNodeInstance(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance.Builder builderForValue) {
        if (nodeInstanceBuilder_ == null) {
          ensureNodeInstanceIsMutable();
          nodeInstance_.add(index, builderForValue.build());
          onChanged();
        } else {
          nodeInstanceBuilder_.addMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
       */
      public Builder addAllNodeInstance(
          java.lang.Iterable<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance> values) {
        if (nodeInstanceBuilder_ == null) {
          ensureNodeInstanceIsMutable();
          com.google.protobuf.AbstractMessageLite.Builder.addAll(
              values, nodeInstance_);
          onChanged();
        } else {
          nodeInstanceBuilder_.addAllMessages(values);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
       */
      public Builder clearNodeInstance() {
        if (nodeInstanceBuilder_ == null) {
          nodeInstance_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000002);
          onChanged();
        } else {
          nodeInstanceBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
       */
      public Builder removeNodeInstance(int index) {
        if (nodeInstanceBuilder_ == null) {
          ensureNodeInstanceIsMutable();
          nodeInstance_.remove(index);
          onChanged();
        } else {
          nodeInstanceBuilder_.remove(index);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance.Builder getNodeInstanceBuilder(
          int index) {
        return getNodeInstanceFieldBuilder().getBuilder(index);
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceOrBuilder getNodeInstanceOrBuilder(
          int index) {
        if (nodeInstanceBuilder_ == null) {
          return nodeInstance_.get(index);  } else {
          return nodeInstanceBuilder_.getMessageOrBuilder(index);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
       */
      public java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceOrBuilder> 
           getNodeInstanceOrBuilderList() {
        if (nodeInstanceBuilder_ != null) {
          return nodeInstanceBuilder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(nodeInstance_);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance.Builder addNodeInstanceBuilder() {
        return getNodeInstanceFieldBuilder().addBuilder(
            org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance.getDefaultInstance());
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance.Builder addNodeInstanceBuilder(
          int index) {
        return getNodeInstanceFieldBuilder().addBuilder(
            index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance.getDefaultInstance());
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstance node_instance = 2;</code>
       */
      public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance.Builder> 
           getNodeInstanceBuilderList() {
        return getNodeInstanceFieldBuilder().getBuilderList();
      }
      private com.google.protobuf.RepeatedFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceOrBuilder> 
          getNodeInstanceFieldBuilder() {
        if (nodeInstanceBuilder_ == null) {
          nodeInstanceBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstance.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceOrBuilder>(
                  nodeInstance_,
                  ((bitField0_ & 0x00000002) != 0),
                  getParentForChildren(),
                  isClean());
          nodeInstance_ = null;
        }
        return nodeInstanceBuilder_;
      }

      private java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup> exclusiveGroup_ =
        java.util.Collections.emptyList();
      private void ensureExclusiveGroupIsMutable() {
        if (!((bitField0_ & 0x00000004) != 0)) {
          exclusiveGroup_ = new java.util.ArrayList<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup>(exclusiveGroup_);
          bitField0_ |= 0x00000004;
         }
      }

      private com.google.protobuf.RepeatedFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroupOrBuilder> exclusiveGroupBuilder_;

      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
       */
      public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup> getExclusiveGroupList() {
        if (exclusiveGroupBuilder_ == null) {
          return java.util.Collections.unmodifiableList(exclusiveGroup_);
        } else {
          return exclusiveGroupBuilder_.getMessageList();
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
       */
      public int getExclusiveGroupCount() {
        if (exclusiveGroupBuilder_ == null) {
          return exclusiveGroup_.size();
        } else {
          return exclusiveGroupBuilder_.getCount();
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup getExclusiveGroup(int index) {
        if (exclusiveGroupBuilder_ == null) {
          return exclusiveGroup_.get(index);
        } else {
          return exclusiveGroupBuilder_.getMessage(index);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
       */
      public Builder setExclusiveGroup(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup value) {
        if (exclusiveGroupBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureExclusiveGroupIsMutable();
          exclusiveGroup_.set(index, value);
          onChanged();
        } else {
          exclusiveGroupBuilder_.setMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
       */
      public Builder setExclusiveGroup(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup.Builder builderForValue) {
        if (exclusiveGroupBuilder_ == null) {
          ensureExclusiveGroupIsMutable();
          exclusiveGroup_.set(index, builderForValue.build());
          onChanged();
        } else {
          exclusiveGroupBuilder_.setMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
       */
      public Builder addExclusiveGroup(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup value) {
        if (exclusiveGroupBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureExclusiveGroupIsMutable();
          exclusiveGroup_.add(value);
          onChanged();
        } else {
          exclusiveGroupBuilder_.addMessage(value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
       */
      public Builder addExclusiveGroup(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup value) {
        if (exclusiveGroupBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureExclusiveGroupIsMutable();
          exclusiveGroup_.add(index, value);
          onChanged();
        } else {
          exclusiveGroupBuilder_.addMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
       */
      public Builder addExclusiveGroup(
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup.Builder builderForValue) {
        if (exclusiveGroupBuilder_ == null) {
          ensureExclusiveGroupIsMutable();
          exclusiveGroup_.add(builderForValue.build());
          onChanged();
        } else {
          exclusiveGroupBuilder_.addMessage(builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
       */
      public Builder addExclusiveGroup(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup.Builder builderForValue) {
        if (exclusiveGroupBuilder_ == null) {
          ensureExclusiveGroupIsMutable();
          exclusiveGroup_.add(index, builderForValue.build());
          onChanged();
        } else {
          exclusiveGroupBuilder_.addMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
       */
      public Builder addAllExclusiveGroup(
          java.lang.Iterable<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup> values) {
        if (exclusiveGroupBuilder_ == null) {
          ensureExclusiveGroupIsMutable();
          com.google.protobuf.AbstractMessageLite.Builder.addAll(
              values, exclusiveGroup_);
          onChanged();
        } else {
          exclusiveGroupBuilder_.addAllMessages(values);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
       */
      public Builder clearExclusiveGroup() {
        if (exclusiveGroupBuilder_ == null) {
          exclusiveGroup_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000004);
          onChanged();
        } else {
          exclusiveGroupBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
       */
      public Builder removeExclusiveGroup(int index) {
        if (exclusiveGroupBuilder_ == null) {
          ensureExclusiveGroupIsMutable();
          exclusiveGroup_.remove(index);
          onChanged();
        } else {
          exclusiveGroupBuilder_.remove(index);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup.Builder getExclusiveGroupBuilder(
          int index) {
        return getExclusiveGroupFieldBuilder().getBuilder(index);
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroupOrBuilder getExclusiveGroupOrBuilder(
          int index) {
        if (exclusiveGroupBuilder_ == null) {
          return exclusiveGroup_.get(index);  } else {
          return exclusiveGroupBuilder_.getMessageOrBuilder(index);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
       */
      public java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroupOrBuilder> 
           getExclusiveGroupOrBuilderList() {
        if (exclusiveGroupBuilder_ != null) {
          return exclusiveGroupBuilder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(exclusiveGroup_);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup.Builder addExclusiveGroupBuilder() {
        return getExclusiveGroupFieldBuilder().addBuilder(
            org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup.getDefaultInstance());
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup.Builder addExclusiveGroupBuilder(
          int index) {
        return getExclusiveGroupFieldBuilder().addBuilder(
            index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup.getDefaultInstance());
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup exclusive_group = 3;</code>
       */
      public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup.Builder> 
           getExclusiveGroupBuilderList() {
        return getExclusiveGroupFieldBuilder().getBuilderList();
      }
      private com.google.protobuf.RepeatedFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroupOrBuilder> 
          getExclusiveGroupFieldBuilder() {
        if (exclusiveGroupBuilder_ == null) {
          exclusiveGroupBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroupOrBuilder>(
                  exclusiveGroup_,
                  ((bitField0_ & 0x00000004) != 0),
                  getParentForChildren(),
                  isClean());
          exclusiveGroup_ = null;
        }
        return exclusiveGroupBuilder_;
      }

      private java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel> iterationLevels_ =
        java.util.Collections.emptyList();
      private void ensureIterationLevelsIsMutable() {
        if (!((bitField0_ & 0x00000008) != 0)) {
          iterationLevels_ = new java.util.ArrayList<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel>(iterationLevels_);
          bitField0_ |= 0x00000008;
         }
      }

      private com.google.protobuf.RepeatedFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevelOrBuilder> iterationLevelsBuilder_;

      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
       */
      public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel> getIterationLevelsList() {
        if (iterationLevelsBuilder_ == null) {
          return java.util.Collections.unmodifiableList(iterationLevels_);
        } else {
          return iterationLevelsBuilder_.getMessageList();
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
       */
      public int getIterationLevelsCount() {
        if (iterationLevelsBuilder_ == null) {
          return iterationLevels_.size();
        } else {
          return iterationLevelsBuilder_.getCount();
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel getIterationLevels(int index) {
        if (iterationLevelsBuilder_ == null) {
          return iterationLevels_.get(index);
        } else {
          return iterationLevelsBuilder_.getMessage(index);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
       */
      public Builder setIterationLevels(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel value) {
        if (iterationLevelsBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureIterationLevelsIsMutable();
          iterationLevels_.set(index, value);
          onChanged();
        } else {
          iterationLevelsBuilder_.setMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
       */
      public Builder setIterationLevels(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel.Builder builderForValue) {
        if (iterationLevelsBuilder_ == null) {
          ensureIterationLevelsIsMutable();
          iterationLevels_.set(index, builderForValue.build());
          onChanged();
        } else {
          iterationLevelsBuilder_.setMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
       */
      public Builder addIterationLevels(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel value) {
        if (iterationLevelsBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureIterationLevelsIsMutable();
          iterationLevels_.add(value);
          onChanged();
        } else {
          iterationLevelsBuilder_.addMessage(value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
       */
      public Builder addIterationLevels(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel value) {
        if (iterationLevelsBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureIterationLevelsIsMutable();
          iterationLevels_.add(index, value);
          onChanged();
        } else {
          iterationLevelsBuilder_.addMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
       */
      public Builder addIterationLevels(
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel.Builder builderForValue) {
        if (iterationLevelsBuilder_ == null) {
          ensureIterationLevelsIsMutable();
          iterationLevels_.add(builderForValue.build());
          onChanged();
        } else {
          iterationLevelsBuilder_.addMessage(builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
       */
      public Builder addIterationLevels(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel.Builder builderForValue) {
        if (iterationLevelsBuilder_ == null) {
          ensureIterationLevelsIsMutable();
          iterationLevels_.add(index, builderForValue.build());
          onChanged();
        } else {
          iterationLevelsBuilder_.addMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
       */
      public Builder addAllIterationLevels(
          java.lang.Iterable<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel> values) {
        if (iterationLevelsBuilder_ == null) {
          ensureIterationLevelsIsMutable();
          com.google.protobuf.AbstractMessageLite.Builder.addAll(
              values, iterationLevels_);
          onChanged();
        } else {
          iterationLevelsBuilder_.addAllMessages(values);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
       */
      public Builder clearIterationLevels() {
        if (iterationLevelsBuilder_ == null) {
          iterationLevels_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000008);
          onChanged();
        } else {
          iterationLevelsBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
       */
      public Builder removeIterationLevels(int index) {
        if (iterationLevelsBuilder_ == null) {
          ensureIterationLevelsIsMutable();
          iterationLevels_.remove(index);
          onChanged();
        } else {
          iterationLevelsBuilder_.remove(index);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel.Builder getIterationLevelsBuilder(
          int index) {
        return getIterationLevelsFieldBuilder().getBuilder(index);
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevelOrBuilder getIterationLevelsOrBuilder(
          int index) {
        if (iterationLevelsBuilder_ == null) {
          return iterationLevels_.get(index);  } else {
          return iterationLevelsBuilder_.getMessageOrBuilder(index);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
       */
      public java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevelOrBuilder> 
           getIterationLevelsOrBuilderList() {
        if (iterationLevelsBuilder_ != null) {
          return iterationLevelsBuilder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(iterationLevels_);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel.Builder addIterationLevelsBuilder() {
        return getIterationLevelsFieldBuilder().addBuilder(
            org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel.getDefaultInstance());
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel.Builder addIterationLevelsBuilder(
          int index) {
        return getIterationLevelsFieldBuilder().addBuilder(
            index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel.getDefaultInstance());
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.IterationLevel iterationLevels = 4;</code>
       */
      public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel.Builder> 
           getIterationLevelsBuilderList() {
        return getIterationLevelsFieldBuilder().getBuilderList();
      }
      private com.google.protobuf.RepeatedFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevelOrBuilder> 
          getIterationLevelsFieldBuilder() {
        if (iterationLevelsBuilder_ == null) {
          iterationLevelsBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevelOrBuilder>(
                  iterationLevels_,
                  ((bitField0_ & 0x00000008) != 0),
                  getParentForChildren(),
                  isClean());
          iterationLevels_ = null;
        }
        return iterationLevelsBuilder_;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.WorkflowContext)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.WorkflowContext)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<WorkflowContext>
        PARSER = new com.google.protobuf.AbstractParser<WorkflowContext>() {
      @java.lang.Override
      public WorkflowContext parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new WorkflowContext(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<WorkflowContext> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<WorkflowContext> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface SwimlaneContextOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.SwimlaneContext)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>string swimlane = 1;</code>
     * @return Whether the swimlane field is set.
     */
    boolean hasSwimlane();
    /**
     * <code>string swimlane = 1;</code>
     * @return The swimlane.
     */
    java.lang.String getSwimlane();
    /**
     * <code>string swimlane = 1;</code>
     * @return The bytes for swimlane.
     */
    com.google.protobuf.ByteString
        getSwimlaneBytes();

    /**
     * <code>string actor_id = 2;</code>
     * @return Whether the actorId field is set.
     */
    boolean hasActorId();
    /**
     * <code>string actor_id = 2;</code>
     * @return The actorId.
     */
    java.lang.String getActorId();
    /**
     * <code>string actor_id = 2;</code>
     * @return The bytes for actorId.
     */
    com.google.protobuf.ByteString
        getActorIdBytes();
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.SwimlaneContext}
   */
  public static final class SwimlaneContext extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.SwimlaneContext)
      SwimlaneContextOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use SwimlaneContext.newBuilder() to construct.
    private SwimlaneContext(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private SwimlaneContext() {
      swimlane_ = "";
      actorId_ = "";
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new SwimlaneContext();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private SwimlaneContext(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 10: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000001;
              swimlane_ = s;
              break;
            }
            case 18: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000002;
              actorId_ = s;
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_SwimlaneContext_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_SwimlaneContext_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext.class, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext.Builder.class);
    }

    private int bitField0_;
    public static final int SWIMLANE_FIELD_NUMBER = 1;
    private volatile java.lang.Object swimlane_;
    /**
     * <code>string swimlane = 1;</code>
     * @return Whether the swimlane field is set.
     */
    @java.lang.Override
    public boolean hasSwimlane() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>string swimlane = 1;</code>
     * @return The swimlane.
     */
    @java.lang.Override
    public java.lang.String getSwimlane() {
      java.lang.Object ref = swimlane_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        swimlane_ = s;
        return s;
      }
    }
    /**
     * <code>string swimlane = 1;</code>
     * @return The bytes for swimlane.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getSwimlaneBytes() {
      java.lang.Object ref = swimlane_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        swimlane_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int ACTOR_ID_FIELD_NUMBER = 2;
    private volatile java.lang.Object actorId_;
    /**
     * <code>string actor_id = 2;</code>
     * @return Whether the actorId field is set.
     */
    @java.lang.Override
    public boolean hasActorId() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>string actor_id = 2;</code>
     * @return The actorId.
     */
    @java.lang.Override
    public java.lang.String getActorId() {
      java.lang.Object ref = actorId_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        actorId_ = s;
        return s;
      }
    }
    /**
     * <code>string actor_id = 2;</code>
     * @return The bytes for actorId.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getActorIdBytes() {
      java.lang.Object ref = actorId_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        actorId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (((bitField0_ & 0x00000001) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, swimlane_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, actorId_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, swimlane_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, actorId_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext other = (org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext) obj;

      if (hasSwimlane() != other.hasSwimlane()) return false;
      if (hasSwimlane()) {
        if (!getSwimlane()
            .equals(other.getSwimlane())) return false;
      }
      if (hasActorId() != other.hasActorId()) return false;
      if (hasActorId()) {
        if (!getActorId()
            .equals(other.getActorId())) return false;
      }
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (hasSwimlane()) {
        hash = (37 * hash) + SWIMLANE_FIELD_NUMBER;
        hash = (53 * hash) + getSwimlane().hashCode();
      }
      if (hasActorId()) {
        hash = (37 * hash) + ACTOR_ID_FIELD_NUMBER;
        hash = (53 * hash) + getActorId().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.SwimlaneContext}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.SwimlaneContext)
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContextOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_SwimlaneContext_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_SwimlaneContext_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext.class, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        swimlane_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        actorId_ = "";
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_SwimlaneContext_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext build() {
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext result = new org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          to_bitField0_ |= 0x00000001;
        }
        result.swimlane_ = swimlane_;
        if (((from_bitField0_ & 0x00000002) != 0)) {
          to_bitField0_ |= 0x00000002;
        }
        result.actorId_ = actorId_;
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext.getDefaultInstance()) return this;
        if (other.hasSwimlane()) {
          bitField0_ |= 0x00000001;
          swimlane_ = other.swimlane_;
          onChanged();
        }
        if (other.hasActorId()) {
          bitField0_ |= 0x00000002;
          actorId_ = other.actorId_;
          onChanged();
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.lang.Object swimlane_ = "";
      /**
       * <code>string swimlane = 1;</code>
       * @return Whether the swimlane field is set.
       */
      public boolean hasSwimlane() {
        return ((bitField0_ & 0x00000001) != 0);
      }
      /**
       * <code>string swimlane = 1;</code>
       * @return The swimlane.
       */
      public java.lang.String getSwimlane() {
        java.lang.Object ref = swimlane_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          swimlane_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string swimlane = 1;</code>
       * @return The bytes for swimlane.
       */
      public com.google.protobuf.ByteString
          getSwimlaneBytes() {
        java.lang.Object ref = swimlane_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          swimlane_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string swimlane = 1;</code>
       * @param value The swimlane to set.
       * @return This builder for chaining.
       */
      public Builder setSwimlane(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        swimlane_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string swimlane = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearSwimlane() {
        bitField0_ = (bitField0_ & ~0x00000001);
        swimlane_ = getDefaultInstance().getSwimlane();
        onChanged();
        return this;
      }
      /**
       * <code>string swimlane = 1;</code>
       * @param value The bytes for swimlane to set.
       * @return This builder for chaining.
       */
      public Builder setSwimlaneBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000001;
        swimlane_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object actorId_ = "";
      /**
       * <code>string actor_id = 2;</code>
       * @return Whether the actorId field is set.
       */
      public boolean hasActorId() {
        return ((bitField0_ & 0x00000002) != 0);
      }
      /**
       * <code>string actor_id = 2;</code>
       * @return The actorId.
       */
      public java.lang.String getActorId() {
        java.lang.Object ref = actorId_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          actorId_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string actor_id = 2;</code>
       * @return The bytes for actorId.
       */
      public com.google.protobuf.ByteString
          getActorIdBytes() {
        java.lang.Object ref = actorId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          actorId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string actor_id = 2;</code>
       * @param value The actorId to set.
       * @return This builder for chaining.
       */
      public Builder setActorId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        actorId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string actor_id = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearActorId() {
        bitField0_ = (bitField0_ & ~0x00000002);
        actorId_ = getDefaultInstance().getActorId();
        onChanged();
        return this;
      }
      /**
       * <code>string actor_id = 2;</code>
       * @param value The bytes for actorId to set.
       * @return This builder for chaining.
       */
      public Builder setActorIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000002;
        actorId_ = value;
        onChanged();
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.SwimlaneContext)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.SwimlaneContext)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<SwimlaneContext>
        PARSER = new com.google.protobuf.AbstractParser<SwimlaneContext>() {
      @java.lang.Override
      public SwimlaneContext parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new SwimlaneContext(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<SwimlaneContext> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<SwimlaneContext> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface SLAContextOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.SLAContext)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>string sla_timer_id = 1;</code>
     * @return Whether the slaTimerId field is set.
     */
    boolean hasSlaTimerId();
    /**
     * <code>string sla_timer_id = 1;</code>
     * @return The slaTimerId.
     */
    java.lang.String getSlaTimerId();
    /**
     * <code>string sla_timer_id = 1;</code>
     * @return The bytes for slaTimerId.
     */
    com.google.protobuf.ByteString
        getSlaTimerIdBytes();

    /**
     * <code>int64 sla_due_date = 2;</code>
     * @return Whether the slaDueDate field is set.
     */
    boolean hasSlaDueDate();
    /**
     * <code>int64 sla_due_date = 2;</code>
     * @return The slaDueDate.
     */
    long getSlaDueDate();

    /**
     * <code>int32 sla_compliance = 3;</code>
     * @return Whether the slaCompliance field is set.
     */
    boolean hasSlaCompliance();
    /**
     * <code>int32 sla_compliance = 3;</code>
     * @return The slaCompliance.
     */
    int getSlaCompliance();
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.SLAContext}
   */
  public static final class SLAContext extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.SLAContext)
      SLAContextOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use SLAContext.newBuilder() to construct.
    private SLAContext(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private SLAContext() {
      slaTimerId_ = "";
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new SLAContext();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private SLAContext(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 10: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000001;
              slaTimerId_ = s;
              break;
            }
            case 16: {
              bitField0_ |= 0x00000002;
              slaDueDate_ = input.readInt64();
              break;
            }
            case 24: {
              bitField0_ |= 0x00000004;
              slaCompliance_ = input.readInt32();
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_SLAContext_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_SLAContext_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.class, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.Builder.class);
    }

    private int bitField0_;
    public static final int SLA_TIMER_ID_FIELD_NUMBER = 1;
    private volatile java.lang.Object slaTimerId_;
    /**
     * <code>string sla_timer_id = 1;</code>
     * @return Whether the slaTimerId field is set.
     */
    @java.lang.Override
    public boolean hasSlaTimerId() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>string sla_timer_id = 1;</code>
     * @return The slaTimerId.
     */
    @java.lang.Override
    public java.lang.String getSlaTimerId() {
      java.lang.Object ref = slaTimerId_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        slaTimerId_ = s;
        return s;
      }
    }
    /**
     * <code>string sla_timer_id = 1;</code>
     * @return The bytes for slaTimerId.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getSlaTimerIdBytes() {
      java.lang.Object ref = slaTimerId_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        slaTimerId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int SLA_DUE_DATE_FIELD_NUMBER = 2;
    private long slaDueDate_;
    /**
     * <code>int64 sla_due_date = 2;</code>
     * @return Whether the slaDueDate field is set.
     */
    @java.lang.Override
    public boolean hasSlaDueDate() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>int64 sla_due_date = 2;</code>
     * @return The slaDueDate.
     */
    @java.lang.Override
    public long getSlaDueDate() {
      return slaDueDate_;
    }

    public static final int SLA_COMPLIANCE_FIELD_NUMBER = 3;
    private int slaCompliance_;
    /**
     * <code>int32 sla_compliance = 3;</code>
     * @return Whether the slaCompliance field is set.
     */
    @java.lang.Override
    public boolean hasSlaCompliance() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     * <code>int32 sla_compliance = 3;</code>
     * @return The slaCompliance.
     */
    @java.lang.Override
    public int getSlaCompliance() {
      return slaCompliance_;
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (((bitField0_ & 0x00000001) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, slaTimerId_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        output.writeInt64(2, slaDueDate_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        output.writeInt32(3, slaCompliance_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, slaTimerId_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(2, slaDueDate_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(3, slaCompliance_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext other = (org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext) obj;

      if (hasSlaTimerId() != other.hasSlaTimerId()) return false;
      if (hasSlaTimerId()) {
        if (!getSlaTimerId()
            .equals(other.getSlaTimerId())) return false;
      }
      if (hasSlaDueDate() != other.hasSlaDueDate()) return false;
      if (hasSlaDueDate()) {
        if (getSlaDueDate()
            != other.getSlaDueDate()) return false;
      }
      if (hasSlaCompliance() != other.hasSlaCompliance()) return false;
      if (hasSlaCompliance()) {
        if (getSlaCompliance()
            != other.getSlaCompliance()) return false;
      }
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (hasSlaTimerId()) {
        hash = (37 * hash) + SLA_TIMER_ID_FIELD_NUMBER;
        hash = (53 * hash) + getSlaTimerId().hashCode();
      }
      if (hasSlaDueDate()) {
        hash = (37 * hash) + SLA_DUE_DATE_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
            getSlaDueDate());
      }
      if (hasSlaCompliance()) {
        hash = (37 * hash) + SLA_COMPLIANCE_FIELD_NUMBER;
        hash = (53 * hash) + getSlaCompliance();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.SLAContext}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.SLAContext)
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContextOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_SLAContext_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_SLAContext_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.class, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        slaTimerId_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        slaDueDate_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000002);
        slaCompliance_ = 0;
        bitField0_ = (bitField0_ & ~0x00000004);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_SLAContext_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext build() {
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext result = new org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          to_bitField0_ |= 0x00000001;
        }
        result.slaTimerId_ = slaTimerId_;
        if (((from_bitField0_ & 0x00000002) != 0)) {
          result.slaDueDate_ = slaDueDate_;
          to_bitField0_ |= 0x00000002;
        }
        if (((from_bitField0_ & 0x00000004) != 0)) {
          result.slaCompliance_ = slaCompliance_;
          to_bitField0_ |= 0x00000004;
        }
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.getDefaultInstance()) return this;
        if (other.hasSlaTimerId()) {
          bitField0_ |= 0x00000001;
          slaTimerId_ = other.slaTimerId_;
          onChanged();
        }
        if (other.hasSlaDueDate()) {
          setSlaDueDate(other.getSlaDueDate());
        }
        if (other.hasSlaCompliance()) {
          setSlaCompliance(other.getSlaCompliance());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.lang.Object slaTimerId_ = "";
      /**
       * <code>string sla_timer_id = 1;</code>
       * @return Whether the slaTimerId field is set.
       */
      public boolean hasSlaTimerId() {
        return ((bitField0_ & 0x00000001) != 0);
      }
      /**
       * <code>string sla_timer_id = 1;</code>
       * @return The slaTimerId.
       */
      public java.lang.String getSlaTimerId() {
        java.lang.Object ref = slaTimerId_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          slaTimerId_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string sla_timer_id = 1;</code>
       * @return The bytes for slaTimerId.
       */
      public com.google.protobuf.ByteString
          getSlaTimerIdBytes() {
        java.lang.Object ref = slaTimerId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          slaTimerId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string sla_timer_id = 1;</code>
       * @param value The slaTimerId to set.
       * @return This builder for chaining.
       */
      public Builder setSlaTimerId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        slaTimerId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string sla_timer_id = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearSlaTimerId() {
        bitField0_ = (bitField0_ & ~0x00000001);
        slaTimerId_ = getDefaultInstance().getSlaTimerId();
        onChanged();
        return this;
      }
      /**
       * <code>string sla_timer_id = 1;</code>
       * @param value The bytes for slaTimerId to set.
       * @return This builder for chaining.
       */
      public Builder setSlaTimerIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000001;
        slaTimerId_ = value;
        onChanged();
        return this;
      }

      private long slaDueDate_ ;
      /**
       * <code>int64 sla_due_date = 2;</code>
       * @return Whether the slaDueDate field is set.
       */
      @java.lang.Override
      public boolean hasSlaDueDate() {
        return ((bitField0_ & 0x00000002) != 0);
      }
      /**
       * <code>int64 sla_due_date = 2;</code>
       * @return The slaDueDate.
       */
      @java.lang.Override
      public long getSlaDueDate() {
        return slaDueDate_;
      }
      /**
       * <code>int64 sla_due_date = 2;</code>
       * @param value The slaDueDate to set.
       * @return This builder for chaining.
       */
      public Builder setSlaDueDate(long value) {
        bitField0_ |= 0x00000002;
        slaDueDate_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int64 sla_due_date = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearSlaDueDate() {
        bitField0_ = (bitField0_ & ~0x00000002);
        slaDueDate_ = 0L;
        onChanged();
        return this;
      }

      private int slaCompliance_ ;
      /**
       * <code>int32 sla_compliance = 3;</code>
       * @return Whether the slaCompliance field is set.
       */
      @java.lang.Override
      public boolean hasSlaCompliance() {
        return ((bitField0_ & 0x00000004) != 0);
      }
      /**
       * <code>int32 sla_compliance = 3;</code>
       * @return The slaCompliance.
       */
      @java.lang.Override
      public int getSlaCompliance() {
        return slaCompliance_;
      }
      /**
       * <code>int32 sla_compliance = 3;</code>
       * @param value The slaCompliance to set.
       * @return This builder for chaining.
       */
      public Builder setSlaCompliance(int value) {
        bitField0_ |= 0x00000004;
        slaCompliance_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 sla_compliance = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearSlaCompliance() {
        bitField0_ = (bitField0_ & ~0x00000004);
        slaCompliance_ = 0;
        onChanged();
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.SLAContext)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.SLAContext)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<SLAContext>
        PARSER = new com.google.protobuf.AbstractParser<SLAContext>() {
      @java.lang.Override
      public SLAContext parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new SLAContext(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<SLAContext> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<SLAContext> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface IterationLevelOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.IterationLevel)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>string id = 1;</code>
     * @return Whether the id field is set.
     */
    boolean hasId();
    /**
     * <code>string id = 1;</code>
     * @return The id.
     */
    java.lang.String getId();
    /**
     * <code>string id = 1;</code>
     * @return The bytes for id.
     */
    com.google.protobuf.ByteString
        getIdBytes();

    /**
     * <code>int32 level = 2;</code>
     * @return Whether the level field is set.
     */
    boolean hasLevel();
    /**
     * <code>int32 level = 2;</code>
     * @return The level.
     */
    int getLevel();
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.IterationLevel}
   */
  public static final class IterationLevel extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.IterationLevel)
      IterationLevelOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use IterationLevel.newBuilder() to construct.
    private IterationLevel(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private IterationLevel() {
      id_ = "";
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new IterationLevel();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private IterationLevel(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 10: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000001;
              id_ = s;
              break;
            }
            case 16: {
              bitField0_ |= 0x00000002;
              level_ = input.readInt32();
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_IterationLevel_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_IterationLevel_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel.class, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel.Builder.class);
    }

    private int bitField0_;
    public static final int ID_FIELD_NUMBER = 1;
    private volatile java.lang.Object id_;
    /**
     * <code>string id = 1;</code>
     * @return Whether the id field is set.
     */
    @java.lang.Override
    public boolean hasId() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>string id = 1;</code>
     * @return The id.
     */
    @java.lang.Override
    public java.lang.String getId() {
      java.lang.Object ref = id_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        id_ = s;
        return s;
      }
    }
    /**
     * <code>string id = 1;</code>
     * @return The bytes for id.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getIdBytes() {
      java.lang.Object ref = id_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        id_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int LEVEL_FIELD_NUMBER = 2;
    private int level_;
    /**
     * <code>int32 level = 2;</code>
     * @return Whether the level field is set.
     */
    @java.lang.Override
    public boolean hasLevel() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>int32 level = 2;</code>
     * @return The level.
     */
    @java.lang.Override
    public int getLevel() {
      return level_;
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (((bitField0_ & 0x00000001) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, id_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        output.writeInt32(2, level_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, id_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(2, level_);
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel other = (org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel) obj;

      if (hasId() != other.hasId()) return false;
      if (hasId()) {
        if (!getId()
            .equals(other.getId())) return false;
      }
      if (hasLevel() != other.hasLevel()) return false;
      if (hasLevel()) {
        if (getLevel()
            != other.getLevel()) return false;
      }
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (hasId()) {
        hash = (37 * hash) + ID_FIELD_NUMBER;
        hash = (53 * hash) + getId().hashCode();
      }
      if (hasLevel()) {
        hash = (37 * hash) + LEVEL_FIELD_NUMBER;
        hash = (53 * hash) + getLevel();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.IterationLevel}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.IterationLevel)
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevelOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_IterationLevel_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_IterationLevel_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel.class, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        id_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        level_ = 0;
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_IterationLevel_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel build() {
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel result = new org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          to_bitField0_ |= 0x00000001;
        }
        result.id_ = id_;
        if (((from_bitField0_ & 0x00000002) != 0)) {
          result.level_ = level_;
          to_bitField0_ |= 0x00000002;
        }
        result.bitField0_ = to_bitField0_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel.getDefaultInstance()) return this;
        if (other.hasId()) {
          bitField0_ |= 0x00000001;
          id_ = other.id_;
          onChanged();
        }
        if (other.hasLevel()) {
          setLevel(other.getLevel());
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.lang.Object id_ = "";
      /**
       * <code>string id = 1;</code>
       * @return Whether the id field is set.
       */
      public boolean hasId() {
        return ((bitField0_ & 0x00000001) != 0);
      }
      /**
       * <code>string id = 1;</code>
       * @return The id.
       */
      public java.lang.String getId() {
        java.lang.Object ref = id_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          id_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string id = 1;</code>
       * @return The bytes for id.
       */
      public com.google.protobuf.ByteString
          getIdBytes() {
        java.lang.Object ref = id_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          id_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string id = 1;</code>
       * @param value The id to set.
       * @return This builder for chaining.
       */
      public Builder setId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        id_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string id = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearId() {
        bitField0_ = (bitField0_ & ~0x00000001);
        id_ = getDefaultInstance().getId();
        onChanged();
        return this;
      }
      /**
       * <code>string id = 1;</code>
       * @param value The bytes for id to set.
       * @return This builder for chaining.
       */
      public Builder setIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000001;
        id_ = value;
        onChanged();
        return this;
      }

      private int level_ ;
      /**
       * <code>int32 level = 2;</code>
       * @return Whether the level field is set.
       */
      @java.lang.Override
      public boolean hasLevel() {
        return ((bitField0_ & 0x00000002) != 0);
      }
      /**
       * <code>int32 level = 2;</code>
       * @return The level.
       */
      @java.lang.Override
      public int getLevel() {
        return level_;
      }
      /**
       * <code>int32 level = 2;</code>
       * @param value The level to set.
       * @return This builder for chaining.
       */
      public Builder setLevel(int value) {
        bitField0_ |= 0x00000002;
        level_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 level = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearLevel() {
        bitField0_ = (bitField0_ & ~0x00000002);
        level_ = 0;
        onChanged();
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.IterationLevel)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.IterationLevel)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<IterationLevel>
        PARSER = new com.google.protobuf.AbstractParser<IterationLevel>() {
      @java.lang.Override
      public IterationLevel parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new IterationLevel(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<IterationLevel> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<IterationLevel> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.IterationLevel getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface NodeInstanceGroupOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>repeated string group_node_instance_id = 1;</code>
     * @return A list containing the groupNodeInstanceId.
     */
    java.util.List<java.lang.String>
        getGroupNodeInstanceIdList();
    /**
     * <code>repeated string group_node_instance_id = 1;</code>
     * @return The count of groupNodeInstanceId.
     */
    int getGroupNodeInstanceIdCount();
    /**
     * <code>repeated string group_node_instance_id = 1;</code>
     * @param index The index of the element to return.
     * @return The groupNodeInstanceId at the given index.
     */
    java.lang.String getGroupNodeInstanceId(int index);
    /**
     * <code>repeated string group_node_instance_id = 1;</code>
     * @param index The index of the value to return.
     * @return The bytes of the groupNodeInstanceId at the given index.
     */
    com.google.protobuf.ByteString
        getGroupNodeInstanceIdBytes(int index);
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup}
   */
  public static final class NodeInstanceGroup extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup)
      NodeInstanceGroupOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use NodeInstanceGroup.newBuilder() to construct.
    private NodeInstanceGroup(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private NodeInstanceGroup() {
      groupNodeInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new NodeInstanceGroup();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private NodeInstanceGroup(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
      int mutable_bitField0_ = 0;
      com.google.protobuf.UnknownFieldSet.Builder unknownFields =
          com.google.protobuf.UnknownFieldSet.newBuilder();
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            case 10: {
              java.lang.String s = input.readStringRequireUtf8();
              if (!((mutable_bitField0_ & 0x00000001) != 0)) {
                groupNodeInstanceId_ = new com.google.protobuf.LazyStringArrayList();
                mutable_bitField0_ |= 0x00000001;
              }
              groupNodeInstanceId_.add(s);
              break;
            }
            default: {
              if (!parseUnknownField(
                  input, unknownFields, extensionRegistry, tag)) {
                done = true;
              }
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        if (((mutable_bitField0_ & 0x00000001) != 0)) {
          groupNodeInstanceId_ = groupNodeInstanceId_.getUnmodifiableView();
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_NodeInstanceGroup_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_NodeInstanceGroup_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup.class, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup.Builder.class);
    }

    public static final int GROUP_NODE_INSTANCE_ID_FIELD_NUMBER = 1;
    private com.google.protobuf.LazyStringList groupNodeInstanceId_;
    /**
     * <code>repeated string group_node_instance_id = 1;</code>
     * @return A list containing the groupNodeInstanceId.
     */
    public com.google.protobuf.ProtocolStringList
        getGroupNodeInstanceIdList() {
      return groupNodeInstanceId_;
    }
    /**
     * <code>repeated string group_node_instance_id = 1;</code>
     * @return The count of groupNodeInstanceId.
     */
    public int getGroupNodeInstanceIdCount() {
      return groupNodeInstanceId_.size();
    }
    /**
     * <code>repeated string group_node_instance_id = 1;</code>
     * @param index The index of the element to return.
     * @return The groupNodeInstanceId at the given index.
     */
    public java.lang.String getGroupNodeInstanceId(int index) {
      return groupNodeInstanceId_.get(index);
    }
    /**
     * <code>repeated string group_node_instance_id = 1;</code>
     * @param index The index of the value to return.
     * @return The bytes of the groupNodeInstanceId at the given index.
     */
    public com.google.protobuf.ByteString
        getGroupNodeInstanceIdBytes(int index) {
      return groupNodeInstanceId_.getByteString(index);
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      for (int i = 0; i < groupNodeInstanceId_.size(); i++) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, groupNodeInstanceId_.getRaw(i));
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      {
        int dataSize = 0;
        for (int i = 0; i < groupNodeInstanceId_.size(); i++) {
          dataSize += computeStringSizeNoTag(groupNodeInstanceId_.getRaw(i));
        }
        size += dataSize;
        size += 1 * getGroupNodeInstanceIdList().size();
      }
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup other = (org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup) obj;

      if (!getGroupNodeInstanceIdList()
          .equals(other.getGroupNodeInstanceIdList())) return false;
      if (!unknownFields.equals(other.unknownFields)) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (getGroupNodeInstanceIdCount() > 0) {
        hash = (37 * hash) + GROUP_NODE_INSTANCE_ID_FIELD_NUMBER;
        hash = (53 * hash) + getGroupNodeInstanceIdList().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup)
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroupOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_NodeInstanceGroup_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_NodeInstanceGroup_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup.class, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        groupNodeInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_NodeInstanceGroup_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup build() {
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup result = new org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup(this);
        int from_bitField0_ = bitField0_;
        if (((bitField0_ & 0x00000001) != 0)) {
          groupNodeInstanceId_ = groupNodeInstanceId_.getUnmodifiableView();
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.groupNodeInstanceId_ = groupNodeInstanceId_;
        onBuilt();
        return result;
      }

      @java.lang.Override
      public Builder clone() {
        return super.clone();
      }
      @java.lang.Override
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.setField(field, value);
      }
      @java.lang.Override
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return super.clearField(field);
      }
      @java.lang.Override
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return super.clearOneof(oneof);
      }
      @java.lang.Override
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, java.lang.Object value) {
        return super.setRepeatedField(field, index, value);
      }
      @java.lang.Override
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          java.lang.Object value) {
        return super.addRepeatedField(field, value);
      }
      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup.getDefaultInstance()) return this;
        if (!other.groupNodeInstanceId_.isEmpty()) {
          if (groupNodeInstanceId_.isEmpty()) {
            groupNodeInstanceId_ = other.groupNodeInstanceId_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureGroupNodeInstanceIdIsMutable();
            groupNodeInstanceId_.addAll(other.groupNodeInstanceId_);
          }
          onChanged();
        }
        this.mergeUnknownFields(other.unknownFields);
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private com.google.protobuf.LazyStringList groupNodeInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      private void ensureGroupNodeInstanceIdIsMutable() {
        if (!((bitField0_ & 0x00000001) != 0)) {
          groupNodeInstanceId_ = new com.google.protobuf.LazyStringArrayList(groupNodeInstanceId_);
          bitField0_ |= 0x00000001;
         }
      }
      /**
       * <code>repeated string group_node_instance_id = 1;</code>
       * @return A list containing the groupNodeInstanceId.
       */
      public com.google.protobuf.ProtocolStringList
          getGroupNodeInstanceIdList() {
        return groupNodeInstanceId_.getUnmodifiableView();
      }
      /**
       * <code>repeated string group_node_instance_id = 1;</code>
       * @return The count of groupNodeInstanceId.
       */
      public int getGroupNodeInstanceIdCount() {
        return groupNodeInstanceId_.size();
      }
      /**
       * <code>repeated string group_node_instance_id = 1;</code>
       * @param index The index of the element to return.
       * @return The groupNodeInstanceId at the given index.
       */
      public java.lang.String getGroupNodeInstanceId(int index) {
        return groupNodeInstanceId_.get(index);
      }
      /**
       * <code>repeated string group_node_instance_id = 1;</code>
       * @param index The index of the value to return.
       * @return The bytes of the groupNodeInstanceId at the given index.
       */
      public com.google.protobuf.ByteString
          getGroupNodeInstanceIdBytes(int index) {
        return groupNodeInstanceId_.getByteString(index);
      }
      /**
       * <code>repeated string group_node_instance_id = 1;</code>
       * @param index The index to set the value at.
       * @param value The groupNodeInstanceId to set.
       * @return This builder for chaining.
       */
      public Builder setGroupNodeInstanceId(
          int index, java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureGroupNodeInstanceIdIsMutable();
        groupNodeInstanceId_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string group_node_instance_id = 1;</code>
       * @param value The groupNodeInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addGroupNodeInstanceId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureGroupNodeInstanceIdIsMutable();
        groupNodeInstanceId_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string group_node_instance_id = 1;</code>
       * @param values The groupNodeInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addAllGroupNodeInstanceId(
          java.lang.Iterable<java.lang.String> values) {
        ensureGroupNodeInstanceIdIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, groupNodeInstanceId_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string group_node_instance_id = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearGroupNodeInstanceId() {
        groupNodeInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string group_node_instance_id = 1;</code>
       * @param value The bytes of the groupNodeInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addGroupNodeInstanceIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        ensureGroupNodeInstanceIdIsMutable();
        groupNodeInstanceId_.add(value);
        onChanged();
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.NodeInstanceGroup)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<NodeInstanceGroup>
        PARSER = new com.google.protobuf.AbstractParser<NodeInstanceGroup>() {
      @java.lang.Override
      public NodeInstanceGroup parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new NodeInstanceGroup(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<NodeInstanceGroup> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<NodeInstanceGroup> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.NodeInstanceGroup getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_Variable_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_Variable_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_NodeInstance_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_NodeInstance_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_WorkflowContext_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_WorkflowContext_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_SwimlaneContext_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_SwimlaneContext_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_SLAContext_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_SLAContext_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_IterationLevel_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_IterationLevel_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_NodeInstanceGroup_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_NodeInstanceGroup_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n@org/kie/kogito/serialization/process/p" +
      "rotobuf/kogito_types.proto\022-org.kie.kogi" +
      "to.serialization.process.protobuf\032\031googl" +
      "e/protobuf/any.proto\"_\n\010Variable\022\014\n\004name" +
      "\030\001 \001(\t\022\021\n\tdata_type\030\002 \001(\t\022(\n\005value\030\003 \001(\013" +
      "2\024.google.protobuf.AnyH\000\210\001\001B\010\n\006_value\"\361\001" +
      "\n\014NodeInstance\022\n\n\002id\030\001 \001(\t\022\017\n\007node_id\030\002 " +
      "\001(\003\022%\n\007content\030\003 \001(\0132\024.google.protobuf.A" +
      "ny\022\022\n\005level\030\004 \001(\005H\000\210\001\001\022\031\n\014trigger_date\030\005" +
      " \001(\003H\001\210\001\001\022K\n\003sla\030\006 \001(\01329.org.kie.kogito." +
      "serialization.process.protobuf.SLAContex" +
      "tH\002\210\001\001B\010\n\006_levelB\017\n\r_trigger_dateB\006\n\004_sl" +
      "a\"\343\002\n\017WorkflowContext\022I\n\010variable\030\001 \003(\0132" +
      "7.org.kie.kogito.serialization.process.p" +
      "rotobuf.Variable\022R\n\rnode_instance\030\002 \003(\0132" +
      ";.org.kie.kogito.serialization.process.p" +
      "rotobuf.NodeInstance\022Y\n\017exclusive_group\030" +
      "\003 \003(\0132@.org.kie.kogito.serialization.pro" +
      "cess.protobuf.NodeInstanceGroup\022V\n\017itera" +
      "tionLevels\030\004 \003(\0132=.org.kie.kogito.serial" +
      "ization.process.protobuf.IterationLevel\"" +
      "Y\n\017SwimlaneContext\022\025\n\010swimlane\030\001 \001(\tH\000\210\001" +
      "\001\022\025\n\010actor_id\030\002 \001(\tH\001\210\001\001B\013\n\t_swimlaneB\013\n" +
      "\t_actor_id\"\224\001\n\nSLAContext\022\031\n\014sla_timer_i" +
      "d\030\001 \001(\tH\000\210\001\001\022\031\n\014sla_due_date\030\002 \001(\003H\001\210\001\001\022" +
      "\033\n\016sla_compliance\030\003 \001(\005H\002\210\001\001B\017\n\r_sla_tim" +
      "er_idB\017\n\r_sla_due_dateB\021\n\017_sla_complianc" +
      "e\"F\n\016IterationLevel\022\017\n\002id\030\001 \001(\tH\000\210\001\001\022\022\n\005" +
      "level\030\002 \001(\005H\001\210\001\001B\005\n\003_idB\010\n\006_level\"3\n\021Nod" +
      "eInstanceGroup\022\036\n\026group_node_instance_id" +
      "\030\001 \003(\tB\025B\023KogitoTypesProtobufb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          com.google.protobuf.AnyProto.getDescriptor(),
        });
    internal_static_org_kie_kogito_serialization_process_protobuf_Variable_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_org_kie_kogito_serialization_process_protobuf_Variable_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_Variable_descriptor,
        new java.lang.String[] { "Name", "DataType", "Value", "Value", });
    internal_static_org_kie_kogito_serialization_process_protobuf_NodeInstance_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_org_kie_kogito_serialization_process_protobuf_NodeInstance_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_NodeInstance_descriptor,
        new java.lang.String[] { "Id", "NodeId", "Content", "Level", "TriggerDate", "Sla", "Level", "TriggerDate", "Sla", });
    internal_static_org_kie_kogito_serialization_process_protobuf_WorkflowContext_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_org_kie_kogito_serialization_process_protobuf_WorkflowContext_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_WorkflowContext_descriptor,
        new java.lang.String[] { "Variable", "NodeInstance", "ExclusiveGroup", "IterationLevels", });
    internal_static_org_kie_kogito_serialization_process_protobuf_SwimlaneContext_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_org_kie_kogito_serialization_process_protobuf_SwimlaneContext_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_SwimlaneContext_descriptor,
        new java.lang.String[] { "Swimlane", "ActorId", "Swimlane", "ActorId", });
    internal_static_org_kie_kogito_serialization_process_protobuf_SLAContext_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_org_kie_kogito_serialization_process_protobuf_SLAContext_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_SLAContext_descriptor,
        new java.lang.String[] { "SlaTimerId", "SlaDueDate", "SlaCompliance", "SlaTimerId", "SlaDueDate", "SlaCompliance", });
    internal_static_org_kie_kogito_serialization_process_protobuf_IterationLevel_descriptor =
      getDescriptor().getMessageTypes().get(5);
    internal_static_org_kie_kogito_serialization_process_protobuf_IterationLevel_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_IterationLevel_descriptor,
        new java.lang.String[] { "Id", "Level", "Id", "Level", });
    internal_static_org_kie_kogito_serialization_process_protobuf_NodeInstanceGroup_descriptor =
      getDescriptor().getMessageTypes().get(6);
    internal_static_org_kie_kogito_serialization_process_protobuf_NodeInstanceGroup_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_NodeInstanceGroup_descriptor,
        new java.lang.String[] { "GroupNodeInstanceId", });
    com.google.protobuf.AnyProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
