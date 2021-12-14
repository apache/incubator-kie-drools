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

public final class KogitoNodeInstanceContentsProtobuf {
  private KogitoNodeInstanceContentsProtobuf() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface RuleSetNodeInstanceContentOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.RuleSetNodeInstanceContent)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return A list containing the timerInstanceId.
     */
    java.util.List<java.lang.String>
        getTimerInstanceIdList();
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return The count of timerInstanceId.
     */
    int getTimerInstanceIdCount();
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the element to return.
     * @return The timerInstanceId at the given index.
     */
    java.lang.String getTimerInstanceId(int index);
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the value to return.
     * @return The bytes of the timerInstanceId at the given index.
     */
    com.google.protobuf.ByteString
        getTimerInstanceIdBytes(int index);

    /**
     * <code>optional string rule_flow_group = 2;</code>
     * @return Whether the ruleFlowGroup field is set.
     */
    boolean hasRuleFlowGroup();
    /**
     * <code>optional string rule_flow_group = 2;</code>
     * @return The ruleFlowGroup.
     */
    java.lang.String getRuleFlowGroup();
    /**
     * <code>optional string rule_flow_group = 2;</code>
     * @return The bytes for ruleFlowGroup.
     */
    com.google.protobuf.ByteString
        getRuleFlowGroupBytes();
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.RuleSetNodeInstanceContent}
   */
  public static final class RuleSetNodeInstanceContent extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.RuleSetNodeInstanceContent)
      RuleSetNodeInstanceContentOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use RuleSetNodeInstanceContent.newBuilder() to construct.
    private RuleSetNodeInstanceContent(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private RuleSetNodeInstanceContent() {
      timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      ruleFlowGroup_ = "";
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new RuleSetNodeInstanceContent();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private RuleSetNodeInstanceContent(
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
                timerInstanceId_ = new com.google.protobuf.LazyStringArrayList();
                mutable_bitField0_ |= 0x00000001;
              }
              timerInstanceId_.add(s);
              break;
            }
            case 18: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000001;
              ruleFlowGroup_ = s;
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
          timerInstanceId_ = timerInstanceId_.getUnmodifiableView();
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_RuleSetNodeInstanceContent_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_RuleSetNodeInstanceContent_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent.Builder.class);
    }

    private int bitField0_;
    public static final int TIMER_INSTANCE_ID_FIELD_NUMBER = 1;
    private com.google.protobuf.LazyStringList timerInstanceId_;
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return A list containing the timerInstanceId.
     */
    public com.google.protobuf.ProtocolStringList
        getTimerInstanceIdList() {
      return timerInstanceId_;
    }
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return The count of timerInstanceId.
     */
    public int getTimerInstanceIdCount() {
      return timerInstanceId_.size();
    }
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the element to return.
     * @return The timerInstanceId at the given index.
     */
    public java.lang.String getTimerInstanceId(int index) {
      return timerInstanceId_.get(index);
    }
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the value to return.
     * @return The bytes of the timerInstanceId at the given index.
     */
    public com.google.protobuf.ByteString
        getTimerInstanceIdBytes(int index) {
      return timerInstanceId_.getByteString(index);
    }

    public static final int RULE_FLOW_GROUP_FIELD_NUMBER = 2;
    private volatile java.lang.Object ruleFlowGroup_;
    /**
     * <code>optional string rule_flow_group = 2;</code>
     * @return Whether the ruleFlowGroup field is set.
     */
    @java.lang.Override
    public boolean hasRuleFlowGroup() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>optional string rule_flow_group = 2;</code>
     * @return The ruleFlowGroup.
     */
    @java.lang.Override
    public java.lang.String getRuleFlowGroup() {
      java.lang.Object ref = ruleFlowGroup_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        ruleFlowGroup_ = s;
        return s;
      }
    }
    /**
     * <code>optional string rule_flow_group = 2;</code>
     * @return The bytes for ruleFlowGroup.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getRuleFlowGroupBytes() {
      java.lang.Object ref = ruleFlowGroup_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        ruleFlowGroup_ = b;
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
      for (int i = 0; i < timerInstanceId_.size(); i++) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, timerInstanceId_.getRaw(i));
      }
      if (((bitField0_ & 0x00000001) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, ruleFlowGroup_);
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
        for (int i = 0; i < timerInstanceId_.size(); i++) {
          dataSize += computeStringSizeNoTag(timerInstanceId_.getRaw(i));
        }
        size += dataSize;
        size += 1 * getTimerInstanceIdList().size();
      }
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, ruleFlowGroup_);
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
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent other = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent) obj;

      if (!getTimerInstanceIdList()
          .equals(other.getTimerInstanceIdList())) return false;
      if (hasRuleFlowGroup() != other.hasRuleFlowGroup()) return false;
      if (hasRuleFlowGroup()) {
        if (!getRuleFlowGroup()
            .equals(other.getRuleFlowGroup())) return false;
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
      if (getTimerInstanceIdCount() > 0) {
        hash = (37 * hash) + TIMER_INSTANCE_ID_FIELD_NUMBER;
        hash = (53 * hash) + getTimerInstanceIdList().hashCode();
      }
      if (hasRuleFlowGroup()) {
        hash = (37 * hash) + RULE_FLOW_GROUP_FIELD_NUMBER;
        hash = (53 * hash) + getRuleFlowGroup().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent parseFrom(
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
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent prototype) {
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
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.RuleSetNodeInstanceContent}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.RuleSetNodeInstanceContent)
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContentOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_RuleSetNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_RuleSetNodeInstanceContent_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent.newBuilder()
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
        timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        ruleFlowGroup_ = "";
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_RuleSetNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent build() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent result = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((bitField0_ & 0x00000001) != 0)) {
          timerInstanceId_ = timerInstanceId_.getUnmodifiableView();
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.timerInstanceId_ = timerInstanceId_;
        if (((from_bitField0_ & 0x00000002) != 0)) {
          to_bitField0_ |= 0x00000001;
        }
        result.ruleFlowGroup_ = ruleFlowGroup_;
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
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent.getDefaultInstance()) return this;
        if (!other.timerInstanceId_.isEmpty()) {
          if (timerInstanceId_.isEmpty()) {
            timerInstanceId_ = other.timerInstanceId_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureTimerInstanceIdIsMutable();
            timerInstanceId_.addAll(other.timerInstanceId_);
          }
          onChanged();
        }
        if (other.hasRuleFlowGroup()) {
          bitField0_ |= 0x00000002;
          ruleFlowGroup_ = other.ruleFlowGroup_;
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
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private com.google.protobuf.LazyStringList timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      private void ensureTimerInstanceIdIsMutable() {
        if (!((bitField0_ & 0x00000001) != 0)) {
          timerInstanceId_ = new com.google.protobuf.LazyStringArrayList(timerInstanceId_);
          bitField0_ |= 0x00000001;
         }
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @return A list containing the timerInstanceId.
       */
      public com.google.protobuf.ProtocolStringList
          getTimerInstanceIdList() {
        return timerInstanceId_.getUnmodifiableView();
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @return The count of timerInstanceId.
       */
      public int getTimerInstanceIdCount() {
        return timerInstanceId_.size();
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param index The index of the element to return.
       * @return The timerInstanceId at the given index.
       */
      public java.lang.String getTimerInstanceId(int index) {
        return timerInstanceId_.get(index);
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param index The index of the value to return.
       * @return The bytes of the timerInstanceId at the given index.
       */
      public com.google.protobuf.ByteString
          getTimerInstanceIdBytes(int index) {
        return timerInstanceId_.getByteString(index);
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param index The index to set the value at.
       * @param value The timerInstanceId to set.
       * @return This builder for chaining.
       */
      public Builder setTimerInstanceId(
          int index, java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureTimerInstanceIdIsMutable();
        timerInstanceId_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param value The timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addTimerInstanceId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureTimerInstanceIdIsMutable();
        timerInstanceId_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param values The timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addAllTimerInstanceId(
          java.lang.Iterable<java.lang.String> values) {
        ensureTimerInstanceIdIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, timerInstanceId_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearTimerInstanceId() {
        timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param value The bytes of the timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addTimerInstanceIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        ensureTimerInstanceIdIsMutable();
        timerInstanceId_.add(value);
        onChanged();
        return this;
      }

      private java.lang.Object ruleFlowGroup_ = "";
      /**
       * <code>optional string rule_flow_group = 2;</code>
       * @return Whether the ruleFlowGroup field is set.
       */
      public boolean hasRuleFlowGroup() {
        return ((bitField0_ & 0x00000002) != 0);
      }
      /**
       * <code>optional string rule_flow_group = 2;</code>
       * @return The ruleFlowGroup.
       */
      public java.lang.String getRuleFlowGroup() {
        java.lang.Object ref = ruleFlowGroup_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          ruleFlowGroup_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>optional string rule_flow_group = 2;</code>
       * @return The bytes for ruleFlowGroup.
       */
      public com.google.protobuf.ByteString
          getRuleFlowGroupBytes() {
        java.lang.Object ref = ruleFlowGroup_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          ruleFlowGroup_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string rule_flow_group = 2;</code>
       * @param value The ruleFlowGroup to set.
       * @return This builder for chaining.
       */
      public Builder setRuleFlowGroup(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        ruleFlowGroup_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string rule_flow_group = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearRuleFlowGroup() {
        bitField0_ = (bitField0_ & ~0x00000002);
        ruleFlowGroup_ = getDefaultInstance().getRuleFlowGroup();
        onChanged();
        return this;
      }
      /**
       * <code>optional string rule_flow_group = 2;</code>
       * @param value The bytes for ruleFlowGroup to set.
       * @return This builder for chaining.
       */
      public Builder setRuleFlowGroupBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000002;
        ruleFlowGroup_ = value;
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


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.RuleSetNodeInstanceContent)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.RuleSetNodeInstanceContent)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<RuleSetNodeInstanceContent>
        PARSER = new com.google.protobuf.AbstractParser<RuleSetNodeInstanceContent>() {
      @java.lang.Override
      public RuleSetNodeInstanceContent parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new RuleSetNodeInstanceContent(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<RuleSetNodeInstanceContent> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<RuleSetNodeInstanceContent> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.RuleSetNodeInstanceContent getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface WorkItemNodeInstanceContentOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.WorkItemNodeInstanceContent)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>string work_item_id = 1;</code>
     * @return The workItemId.
     */
    java.lang.String getWorkItemId();
    /**
     * <code>string work_item_id = 1;</code>
     * @return The bytes for workItemId.
     */
    com.google.protobuf.ByteString
        getWorkItemIdBytes();

    /**
     * <code>repeated string timer_instance_id = 2;</code>
     * @return A list containing the timerInstanceId.
     */
    java.util.List<java.lang.String>
        getTimerInstanceIdList();
    /**
     * <code>repeated string timer_instance_id = 2;</code>
     * @return The count of timerInstanceId.
     */
    int getTimerInstanceIdCount();
    /**
     * <code>repeated string timer_instance_id = 2;</code>
     * @param index The index of the element to return.
     * @return The timerInstanceId at the given index.
     */
    java.lang.String getTimerInstanceId(int index);
    /**
     * <code>repeated string timer_instance_id = 2;</code>
     * @param index The index of the value to return.
     * @return The bytes of the timerInstanceId at the given index.
     */
    com.google.protobuf.ByteString
        getTimerInstanceIdBytes(int index);

    /**
     * <code>optional string error_handling_process_instance_id = 3;</code>
     * @return Whether the errorHandlingProcessInstanceId field is set.
     */
    boolean hasErrorHandlingProcessInstanceId();
    /**
     * <code>optional string error_handling_process_instance_id = 3;</code>
     * @return The errorHandlingProcessInstanceId.
     */
    java.lang.String getErrorHandlingProcessInstanceId();
    /**
     * <code>optional string error_handling_process_instance_id = 3;</code>
     * @return The bytes for errorHandlingProcessInstanceId.
     */
    com.google.protobuf.ByteString
        getErrorHandlingProcessInstanceIdBytes();

    /**
     * <pre>
     * work item data
     * </pre>
     *
     * <code>int32 state = 4;</code>
     * @return The state.
     */
    int getState();

    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
     */
    java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable> 
        getVariableList();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable getVariable(int index);
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
     */
    int getVariableCount();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
     */
    java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder> 
        getVariableOrBuilderList();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder getVariableOrBuilder(
        int index);

    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
     */
    java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable> 
        getResultList();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable getResult(int index);
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
     */
    int getResultCount();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
     */
    java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder> 
        getResultOrBuilderList();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder getResultOrBuilder(
        int index);

    /**
     * <code>optional string phase_id = 7;</code>
     * @return Whether the phaseId field is set.
     */
    boolean hasPhaseId();
    /**
     * <code>optional string phase_id = 7;</code>
     * @return The phaseId.
     */
    java.lang.String getPhaseId();
    /**
     * <code>optional string phase_id = 7;</code>
     * @return The bytes for phaseId.
     */
    com.google.protobuf.ByteString
        getPhaseIdBytes();

    /**
     * <code>optional string phase_status = 8;</code>
     * @return Whether the phaseStatus field is set.
     */
    boolean hasPhaseStatus();
    /**
     * <code>optional string phase_status = 8;</code>
     * @return The phaseStatus.
     */
    java.lang.String getPhaseStatus();
    /**
     * <code>optional string phase_status = 8;</code>
     * @return The bytes for phaseStatus.
     */
    com.google.protobuf.ByteString
        getPhaseStatusBytes();

    /**
     * <code>optional string name = 9;</code>
     * @return Whether the name field is set.
     */
    boolean hasName();
    /**
     * <code>optional string name = 9;</code>
     * @return The name.
     */
    java.lang.String getName();
    /**
     * <code>optional string name = 9;</code>
     * @return The bytes for name.
     */
    com.google.protobuf.ByteString
        getNameBytes();

    /**
     * <code>optional int64 start_date = 10;</code>
     * @return Whether the startDate field is set.
     */
    boolean hasStartDate();
    /**
     * <code>optional int64 start_date = 10;</code>
     * @return The startDate.
     */
    long getStartDate();

    /**
     * <code>optional int64 complete_date = 11;</code>
     * @return Whether the completeDate field is set.
     */
    boolean hasCompleteDate();
    /**
     * <code>optional int64 complete_date = 11;</code>
     * @return The completeDate.
     */
    long getCompleteDate();

    /**
     * <code>optional .google.protobuf.Any work_item_data = 12;</code>
     * @return Whether the workItemData field is set.
     */
    boolean hasWorkItemData();
    /**
     * <code>optional .google.protobuf.Any work_item_data = 12;</code>
     * @return The workItemData.
     */
    com.google.protobuf.Any getWorkItemData();
    /**
     * <code>optional .google.protobuf.Any work_item_data = 12;</code>
     */
    com.google.protobuf.AnyOrBuilder getWorkItemDataOrBuilder();
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.WorkItemNodeInstanceContent}
   */
  public static final class WorkItemNodeInstanceContent extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.WorkItemNodeInstanceContent)
      WorkItemNodeInstanceContentOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use WorkItemNodeInstanceContent.newBuilder() to construct.
    private WorkItemNodeInstanceContent(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private WorkItemNodeInstanceContent() {
      workItemId_ = "";
      timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      errorHandlingProcessInstanceId_ = "";
      variable_ = java.util.Collections.emptyList();
      result_ = java.util.Collections.emptyList();
      phaseId_ = "";
      phaseStatus_ = "";
      name_ = "";
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new WorkItemNodeInstanceContent();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private WorkItemNodeInstanceContent(
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

              workItemId_ = s;
              break;
            }
            case 18: {
              java.lang.String s = input.readStringRequireUtf8();
              if (!((mutable_bitField0_ & 0x00000001) != 0)) {
                timerInstanceId_ = new com.google.protobuf.LazyStringArrayList();
                mutable_bitField0_ |= 0x00000001;
              }
              timerInstanceId_.add(s);
              break;
            }
            case 26: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000001;
              errorHandlingProcessInstanceId_ = s;
              break;
            }
            case 32: {

              state_ = input.readInt32();
              break;
            }
            case 42: {
              if (!((mutable_bitField0_ & 0x00000004) != 0)) {
                variable_ = new java.util.ArrayList<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable>();
                mutable_bitField0_ |= 0x00000004;
              }
              variable_.add(
                  input.readMessage(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.parser(), extensionRegistry));
              break;
            }
            case 50: {
              if (!((mutable_bitField0_ & 0x00000008) != 0)) {
                result_ = new java.util.ArrayList<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable>();
                mutable_bitField0_ |= 0x00000008;
              }
              result_.add(
                  input.readMessage(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.parser(), extensionRegistry));
              break;
            }
            case 58: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000002;
              phaseId_ = s;
              break;
            }
            case 66: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000004;
              phaseStatus_ = s;
              break;
            }
            case 74: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000008;
              name_ = s;
              break;
            }
            case 80: {
              bitField0_ |= 0x00000010;
              startDate_ = input.readInt64();
              break;
            }
            case 88: {
              bitField0_ |= 0x00000020;
              completeDate_ = input.readInt64();
              break;
            }
            case 98: {
              com.google.protobuf.Any.Builder subBuilder = null;
              if (((bitField0_ & 0x00000040) != 0)) {
                subBuilder = workItemData_.toBuilder();
              }
              workItemData_ = input.readMessage(com.google.protobuf.Any.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(workItemData_);
                workItemData_ = subBuilder.buildPartial();
              }
              bitField0_ |= 0x00000040;
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
          timerInstanceId_ = timerInstanceId_.getUnmodifiableView();
        }
        if (((mutable_bitField0_ & 0x00000004) != 0)) {
          variable_ = java.util.Collections.unmodifiableList(variable_);
        }
        if (((mutable_bitField0_ & 0x00000008) != 0)) {
          result_ = java.util.Collections.unmodifiableList(result_);
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_WorkItemNodeInstanceContent_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_WorkItemNodeInstanceContent_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent.Builder.class);
    }

    private int bitField0_;
    public static final int WORK_ITEM_ID_FIELD_NUMBER = 1;
    private volatile java.lang.Object workItemId_;
    /**
     * <code>string work_item_id = 1;</code>
     * @return The workItemId.
     */
    @java.lang.Override
    public java.lang.String getWorkItemId() {
      java.lang.Object ref = workItemId_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        workItemId_ = s;
        return s;
      }
    }
    /**
     * <code>string work_item_id = 1;</code>
     * @return The bytes for workItemId.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getWorkItemIdBytes() {
      java.lang.Object ref = workItemId_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        workItemId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int TIMER_INSTANCE_ID_FIELD_NUMBER = 2;
    private com.google.protobuf.LazyStringList timerInstanceId_;
    /**
     * <code>repeated string timer_instance_id = 2;</code>
     * @return A list containing the timerInstanceId.
     */
    public com.google.protobuf.ProtocolStringList
        getTimerInstanceIdList() {
      return timerInstanceId_;
    }
    /**
     * <code>repeated string timer_instance_id = 2;</code>
     * @return The count of timerInstanceId.
     */
    public int getTimerInstanceIdCount() {
      return timerInstanceId_.size();
    }
    /**
     * <code>repeated string timer_instance_id = 2;</code>
     * @param index The index of the element to return.
     * @return The timerInstanceId at the given index.
     */
    public java.lang.String getTimerInstanceId(int index) {
      return timerInstanceId_.get(index);
    }
    /**
     * <code>repeated string timer_instance_id = 2;</code>
     * @param index The index of the value to return.
     * @return The bytes of the timerInstanceId at the given index.
     */
    public com.google.protobuf.ByteString
        getTimerInstanceIdBytes(int index) {
      return timerInstanceId_.getByteString(index);
    }

    public static final int ERROR_HANDLING_PROCESS_INSTANCE_ID_FIELD_NUMBER = 3;
    private volatile java.lang.Object errorHandlingProcessInstanceId_;
    /**
     * <code>optional string error_handling_process_instance_id = 3;</code>
     * @return Whether the errorHandlingProcessInstanceId field is set.
     */
    @java.lang.Override
    public boolean hasErrorHandlingProcessInstanceId() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>optional string error_handling_process_instance_id = 3;</code>
     * @return The errorHandlingProcessInstanceId.
     */
    @java.lang.Override
    public java.lang.String getErrorHandlingProcessInstanceId() {
      java.lang.Object ref = errorHandlingProcessInstanceId_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        errorHandlingProcessInstanceId_ = s;
        return s;
      }
    }
    /**
     * <code>optional string error_handling_process_instance_id = 3;</code>
     * @return The bytes for errorHandlingProcessInstanceId.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getErrorHandlingProcessInstanceIdBytes() {
      java.lang.Object ref = errorHandlingProcessInstanceId_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        errorHandlingProcessInstanceId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int STATE_FIELD_NUMBER = 4;
    private int state_;
    /**
     * <pre>
     * work item data
     * </pre>
     *
     * <code>int32 state = 4;</code>
     * @return The state.
     */
    @java.lang.Override
    public int getState() {
      return state_;
    }

    public static final int VARIABLE_FIELD_NUMBER = 5;
    private java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable> variable_;
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
     */
    @java.lang.Override
    public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable> getVariableList() {
      return variable_;
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
     */
    @java.lang.Override
    public java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder> 
        getVariableOrBuilderList() {
      return variable_;
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
     */
    @java.lang.Override
    public int getVariableCount() {
      return variable_.size();
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable getVariable(int index) {
      return variable_.get(index);
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder getVariableOrBuilder(
        int index) {
      return variable_.get(index);
    }

    public static final int RESULT_FIELD_NUMBER = 6;
    private java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable> result_;
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
     */
    @java.lang.Override
    public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable> getResultList() {
      return result_;
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
     */
    @java.lang.Override
    public java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder> 
        getResultOrBuilderList() {
      return result_;
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
     */
    @java.lang.Override
    public int getResultCount() {
      return result_.size();
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable getResult(int index) {
      return result_.get(index);
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder getResultOrBuilder(
        int index) {
      return result_.get(index);
    }

    public static final int PHASE_ID_FIELD_NUMBER = 7;
    private volatile java.lang.Object phaseId_;
    /**
     * <code>optional string phase_id = 7;</code>
     * @return Whether the phaseId field is set.
     */
    @java.lang.Override
    public boolean hasPhaseId() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>optional string phase_id = 7;</code>
     * @return The phaseId.
     */
    @java.lang.Override
    public java.lang.String getPhaseId() {
      java.lang.Object ref = phaseId_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        phaseId_ = s;
        return s;
      }
    }
    /**
     * <code>optional string phase_id = 7;</code>
     * @return The bytes for phaseId.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getPhaseIdBytes() {
      java.lang.Object ref = phaseId_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        phaseId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int PHASE_STATUS_FIELD_NUMBER = 8;
    private volatile java.lang.Object phaseStatus_;
    /**
     * <code>optional string phase_status = 8;</code>
     * @return Whether the phaseStatus field is set.
     */
    @java.lang.Override
    public boolean hasPhaseStatus() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     * <code>optional string phase_status = 8;</code>
     * @return The phaseStatus.
     */
    @java.lang.Override
    public java.lang.String getPhaseStatus() {
      java.lang.Object ref = phaseStatus_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        phaseStatus_ = s;
        return s;
      }
    }
    /**
     * <code>optional string phase_status = 8;</code>
     * @return The bytes for phaseStatus.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getPhaseStatusBytes() {
      java.lang.Object ref = phaseStatus_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        phaseStatus_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int NAME_FIELD_NUMBER = 9;
    private volatile java.lang.Object name_;
    /**
     * <code>optional string name = 9;</code>
     * @return Whether the name field is set.
     */
    @java.lang.Override
    public boolean hasName() {
      return ((bitField0_ & 0x00000008) != 0);
    }
    /**
     * <code>optional string name = 9;</code>
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
     * <code>optional string name = 9;</code>
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

    public static final int START_DATE_FIELD_NUMBER = 10;
    private long startDate_;
    /**
     * <code>optional int64 start_date = 10;</code>
     * @return Whether the startDate field is set.
     */
    @java.lang.Override
    public boolean hasStartDate() {
      return ((bitField0_ & 0x00000010) != 0);
    }
    /**
     * <code>optional int64 start_date = 10;</code>
     * @return The startDate.
     */
    @java.lang.Override
    public long getStartDate() {
      return startDate_;
    }

    public static final int COMPLETE_DATE_FIELD_NUMBER = 11;
    private long completeDate_;
    /**
     * <code>optional int64 complete_date = 11;</code>
     * @return Whether the completeDate field is set.
     */
    @java.lang.Override
    public boolean hasCompleteDate() {
      return ((bitField0_ & 0x00000020) != 0);
    }
    /**
     * <code>optional int64 complete_date = 11;</code>
     * @return The completeDate.
     */
    @java.lang.Override
    public long getCompleteDate() {
      return completeDate_;
    }

    public static final int WORK_ITEM_DATA_FIELD_NUMBER = 12;
    private com.google.protobuf.Any workItemData_;
    /**
     * <code>optional .google.protobuf.Any work_item_data = 12;</code>
     * @return Whether the workItemData field is set.
     */
    @java.lang.Override
    public boolean hasWorkItemData() {
      return ((bitField0_ & 0x00000040) != 0);
    }
    /**
     * <code>optional .google.protobuf.Any work_item_data = 12;</code>
     * @return The workItemData.
     */
    @java.lang.Override
    public com.google.protobuf.Any getWorkItemData() {
      return workItemData_ == null ? com.google.protobuf.Any.getDefaultInstance() : workItemData_;
    }
    /**
     * <code>optional .google.protobuf.Any work_item_data = 12;</code>
     */
    @java.lang.Override
    public com.google.protobuf.AnyOrBuilder getWorkItemDataOrBuilder() {
      return workItemData_ == null ? com.google.protobuf.Any.getDefaultInstance() : workItemData_;
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
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(workItemId_)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, workItemId_);
      }
      for (int i = 0; i < timerInstanceId_.size(); i++) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, timerInstanceId_.getRaw(i));
      }
      if (((bitField0_ & 0x00000001) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 3, errorHandlingProcessInstanceId_);
      }
      if (state_ != 0) {
        output.writeInt32(4, state_);
      }
      for (int i = 0; i < variable_.size(); i++) {
        output.writeMessage(5, variable_.get(i));
      }
      for (int i = 0; i < result_.size(); i++) {
        output.writeMessage(6, result_.get(i));
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 7, phaseId_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 8, phaseStatus_);
      }
      if (((bitField0_ & 0x00000008) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 9, name_);
      }
      if (((bitField0_ & 0x00000010) != 0)) {
        output.writeInt64(10, startDate_);
      }
      if (((bitField0_ & 0x00000020) != 0)) {
        output.writeInt64(11, completeDate_);
      }
      if (((bitField0_ & 0x00000040) != 0)) {
        output.writeMessage(12, getWorkItemData());
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(workItemId_)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, workItemId_);
      }
      {
        int dataSize = 0;
        for (int i = 0; i < timerInstanceId_.size(); i++) {
          dataSize += computeStringSizeNoTag(timerInstanceId_.getRaw(i));
        }
        size += dataSize;
        size += 1 * getTimerInstanceIdList().size();
      }
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, errorHandlingProcessInstanceId_);
      }
      if (state_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(4, state_);
      }
      for (int i = 0; i < variable_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(5, variable_.get(i));
      }
      for (int i = 0; i < result_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(6, result_.get(i));
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(7, phaseId_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(8, phaseStatus_);
      }
      if (((bitField0_ & 0x00000008) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(9, name_);
      }
      if (((bitField0_ & 0x00000010) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(10, startDate_);
      }
      if (((bitField0_ & 0x00000020) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(11, completeDate_);
      }
      if (((bitField0_ & 0x00000040) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(12, getWorkItemData());
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
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent other = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent) obj;

      if (!getWorkItemId()
          .equals(other.getWorkItemId())) return false;
      if (!getTimerInstanceIdList()
          .equals(other.getTimerInstanceIdList())) return false;
      if (hasErrorHandlingProcessInstanceId() != other.hasErrorHandlingProcessInstanceId()) return false;
      if (hasErrorHandlingProcessInstanceId()) {
        if (!getErrorHandlingProcessInstanceId()
            .equals(other.getErrorHandlingProcessInstanceId())) return false;
      }
      if (getState()
          != other.getState()) return false;
      if (!getVariableList()
          .equals(other.getVariableList())) return false;
      if (!getResultList()
          .equals(other.getResultList())) return false;
      if (hasPhaseId() != other.hasPhaseId()) return false;
      if (hasPhaseId()) {
        if (!getPhaseId()
            .equals(other.getPhaseId())) return false;
      }
      if (hasPhaseStatus() != other.hasPhaseStatus()) return false;
      if (hasPhaseStatus()) {
        if (!getPhaseStatus()
            .equals(other.getPhaseStatus())) return false;
      }
      if (hasName() != other.hasName()) return false;
      if (hasName()) {
        if (!getName()
            .equals(other.getName())) return false;
      }
      if (hasStartDate() != other.hasStartDate()) return false;
      if (hasStartDate()) {
        if (getStartDate()
            != other.getStartDate()) return false;
      }
      if (hasCompleteDate() != other.hasCompleteDate()) return false;
      if (hasCompleteDate()) {
        if (getCompleteDate()
            != other.getCompleteDate()) return false;
      }
      if (hasWorkItemData() != other.hasWorkItemData()) return false;
      if (hasWorkItemData()) {
        if (!getWorkItemData()
            .equals(other.getWorkItemData())) return false;
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
      hash = (37 * hash) + WORK_ITEM_ID_FIELD_NUMBER;
      hash = (53 * hash) + getWorkItemId().hashCode();
      if (getTimerInstanceIdCount() > 0) {
        hash = (37 * hash) + TIMER_INSTANCE_ID_FIELD_NUMBER;
        hash = (53 * hash) + getTimerInstanceIdList().hashCode();
      }
      if (hasErrorHandlingProcessInstanceId()) {
        hash = (37 * hash) + ERROR_HANDLING_PROCESS_INSTANCE_ID_FIELD_NUMBER;
        hash = (53 * hash) + getErrorHandlingProcessInstanceId().hashCode();
      }
      hash = (37 * hash) + STATE_FIELD_NUMBER;
      hash = (53 * hash) + getState();
      if (getVariableCount() > 0) {
        hash = (37 * hash) + VARIABLE_FIELD_NUMBER;
        hash = (53 * hash) + getVariableList().hashCode();
      }
      if (getResultCount() > 0) {
        hash = (37 * hash) + RESULT_FIELD_NUMBER;
        hash = (53 * hash) + getResultList().hashCode();
      }
      if (hasPhaseId()) {
        hash = (37 * hash) + PHASE_ID_FIELD_NUMBER;
        hash = (53 * hash) + getPhaseId().hashCode();
      }
      if (hasPhaseStatus()) {
        hash = (37 * hash) + PHASE_STATUS_FIELD_NUMBER;
        hash = (53 * hash) + getPhaseStatus().hashCode();
      }
      if (hasName()) {
        hash = (37 * hash) + NAME_FIELD_NUMBER;
        hash = (53 * hash) + getName().hashCode();
      }
      if (hasStartDate()) {
        hash = (37 * hash) + START_DATE_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
            getStartDate());
      }
      if (hasCompleteDate()) {
        hash = (37 * hash) + COMPLETE_DATE_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
            getCompleteDate());
      }
      if (hasWorkItemData()) {
        hash = (37 * hash) + WORK_ITEM_DATA_FIELD_NUMBER;
        hash = (53 * hash) + getWorkItemData().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent parseFrom(
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
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent prototype) {
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
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.WorkItemNodeInstanceContent}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.WorkItemNodeInstanceContent)
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContentOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_WorkItemNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_WorkItemNodeInstanceContent_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent.newBuilder()
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
          getResultFieldBuilder();
          getWorkItemDataFieldBuilder();
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        workItemId_ = "";

        timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        errorHandlingProcessInstanceId_ = "";
        bitField0_ = (bitField0_ & ~0x00000002);
        state_ = 0;

        if (variableBuilder_ == null) {
          variable_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000004);
        } else {
          variableBuilder_.clear();
        }
        if (resultBuilder_ == null) {
          result_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000008);
        } else {
          resultBuilder_.clear();
        }
        phaseId_ = "";
        bitField0_ = (bitField0_ & ~0x00000010);
        phaseStatus_ = "";
        bitField0_ = (bitField0_ & ~0x00000020);
        name_ = "";
        bitField0_ = (bitField0_ & ~0x00000040);
        startDate_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000080);
        completeDate_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000100);
        if (workItemDataBuilder_ == null) {
          workItemData_ = null;
        } else {
          workItemDataBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000200);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_WorkItemNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent build() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent result = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        result.workItemId_ = workItemId_;
        if (((bitField0_ & 0x00000001) != 0)) {
          timerInstanceId_ = timerInstanceId_.getUnmodifiableView();
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.timerInstanceId_ = timerInstanceId_;
        if (((from_bitField0_ & 0x00000002) != 0)) {
          to_bitField0_ |= 0x00000001;
        }
        result.errorHandlingProcessInstanceId_ = errorHandlingProcessInstanceId_;
        result.state_ = state_;
        if (variableBuilder_ == null) {
          if (((bitField0_ & 0x00000004) != 0)) {
            variable_ = java.util.Collections.unmodifiableList(variable_);
            bitField0_ = (bitField0_ & ~0x00000004);
          }
          result.variable_ = variable_;
        } else {
          result.variable_ = variableBuilder_.build();
        }
        if (resultBuilder_ == null) {
          if (((bitField0_ & 0x00000008) != 0)) {
            result_ = java.util.Collections.unmodifiableList(result_);
            bitField0_ = (bitField0_ & ~0x00000008);
          }
          result.result_ = result_;
        } else {
          result.result_ = resultBuilder_.build();
        }
        if (((from_bitField0_ & 0x00000010) != 0)) {
          to_bitField0_ |= 0x00000002;
        }
        result.phaseId_ = phaseId_;
        if (((from_bitField0_ & 0x00000020) != 0)) {
          to_bitField0_ |= 0x00000004;
        }
        result.phaseStatus_ = phaseStatus_;
        if (((from_bitField0_ & 0x00000040) != 0)) {
          to_bitField0_ |= 0x00000008;
        }
        result.name_ = name_;
        if (((from_bitField0_ & 0x00000080) != 0)) {
          result.startDate_ = startDate_;
          to_bitField0_ |= 0x00000010;
        }
        if (((from_bitField0_ & 0x00000100) != 0)) {
          result.completeDate_ = completeDate_;
          to_bitField0_ |= 0x00000020;
        }
        if (((from_bitField0_ & 0x00000200) != 0)) {
          if (workItemDataBuilder_ == null) {
            result.workItemData_ = workItemData_;
          } else {
            result.workItemData_ = workItemDataBuilder_.build();
          }
          to_bitField0_ |= 0x00000040;
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
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent.getDefaultInstance()) return this;
        if (!other.getWorkItemId().isEmpty()) {
          workItemId_ = other.workItemId_;
          onChanged();
        }
        if (!other.timerInstanceId_.isEmpty()) {
          if (timerInstanceId_.isEmpty()) {
            timerInstanceId_ = other.timerInstanceId_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureTimerInstanceIdIsMutable();
            timerInstanceId_.addAll(other.timerInstanceId_);
          }
          onChanged();
        }
        if (other.hasErrorHandlingProcessInstanceId()) {
          bitField0_ |= 0x00000002;
          errorHandlingProcessInstanceId_ = other.errorHandlingProcessInstanceId_;
          onChanged();
        }
        if (other.getState() != 0) {
          setState(other.getState());
        }
        if (variableBuilder_ == null) {
          if (!other.variable_.isEmpty()) {
            if (variable_.isEmpty()) {
              variable_ = other.variable_;
              bitField0_ = (bitField0_ & ~0x00000004);
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
              bitField0_ = (bitField0_ & ~0x00000004);
              variableBuilder_ = 
                com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                   getVariableFieldBuilder() : null;
            } else {
              variableBuilder_.addAllMessages(other.variable_);
            }
          }
        }
        if (resultBuilder_ == null) {
          if (!other.result_.isEmpty()) {
            if (result_.isEmpty()) {
              result_ = other.result_;
              bitField0_ = (bitField0_ & ~0x00000008);
            } else {
              ensureResultIsMutable();
              result_.addAll(other.result_);
            }
            onChanged();
          }
        } else {
          if (!other.result_.isEmpty()) {
            if (resultBuilder_.isEmpty()) {
              resultBuilder_.dispose();
              resultBuilder_ = null;
              result_ = other.result_;
              bitField0_ = (bitField0_ & ~0x00000008);
              resultBuilder_ = 
                com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                   getResultFieldBuilder() : null;
            } else {
              resultBuilder_.addAllMessages(other.result_);
            }
          }
        }
        if (other.hasPhaseId()) {
          bitField0_ |= 0x00000010;
          phaseId_ = other.phaseId_;
          onChanged();
        }
        if (other.hasPhaseStatus()) {
          bitField0_ |= 0x00000020;
          phaseStatus_ = other.phaseStatus_;
          onChanged();
        }
        if (other.hasName()) {
          bitField0_ |= 0x00000040;
          name_ = other.name_;
          onChanged();
        }
        if (other.hasStartDate()) {
          setStartDate(other.getStartDate());
        }
        if (other.hasCompleteDate()) {
          setCompleteDate(other.getCompleteDate());
        }
        if (other.hasWorkItemData()) {
          mergeWorkItemData(other.getWorkItemData());
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
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.lang.Object workItemId_ = "";
      /**
       * <code>string work_item_id = 1;</code>
       * @return The workItemId.
       */
      public java.lang.String getWorkItemId() {
        java.lang.Object ref = workItemId_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          workItemId_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string work_item_id = 1;</code>
       * @return The bytes for workItemId.
       */
      public com.google.protobuf.ByteString
          getWorkItemIdBytes() {
        java.lang.Object ref = workItemId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          workItemId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string work_item_id = 1;</code>
       * @param value The workItemId to set.
       * @return This builder for chaining.
       */
      public Builder setWorkItemId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        workItemId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string work_item_id = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearWorkItemId() {
        
        workItemId_ = getDefaultInstance().getWorkItemId();
        onChanged();
        return this;
      }
      /**
       * <code>string work_item_id = 1;</code>
       * @param value The bytes for workItemId to set.
       * @return This builder for chaining.
       */
      public Builder setWorkItemIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        workItemId_ = value;
        onChanged();
        return this;
      }

      private com.google.protobuf.LazyStringList timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      private void ensureTimerInstanceIdIsMutable() {
        if (!((bitField0_ & 0x00000001) != 0)) {
          timerInstanceId_ = new com.google.protobuf.LazyStringArrayList(timerInstanceId_);
          bitField0_ |= 0x00000001;
         }
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @return A list containing the timerInstanceId.
       */
      public com.google.protobuf.ProtocolStringList
          getTimerInstanceIdList() {
        return timerInstanceId_.getUnmodifiableView();
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @return The count of timerInstanceId.
       */
      public int getTimerInstanceIdCount() {
        return timerInstanceId_.size();
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @param index The index of the element to return.
       * @return The timerInstanceId at the given index.
       */
      public java.lang.String getTimerInstanceId(int index) {
        return timerInstanceId_.get(index);
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @param index The index of the value to return.
       * @return The bytes of the timerInstanceId at the given index.
       */
      public com.google.protobuf.ByteString
          getTimerInstanceIdBytes(int index) {
        return timerInstanceId_.getByteString(index);
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @param index The index to set the value at.
       * @param value The timerInstanceId to set.
       * @return This builder for chaining.
       */
      public Builder setTimerInstanceId(
          int index, java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureTimerInstanceIdIsMutable();
        timerInstanceId_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @param value The timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addTimerInstanceId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureTimerInstanceIdIsMutable();
        timerInstanceId_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @param values The timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addAllTimerInstanceId(
          java.lang.Iterable<java.lang.String> values) {
        ensureTimerInstanceIdIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, timerInstanceId_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearTimerInstanceId() {
        timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @param value The bytes of the timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addTimerInstanceIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        ensureTimerInstanceIdIsMutable();
        timerInstanceId_.add(value);
        onChanged();
        return this;
      }

      private java.lang.Object errorHandlingProcessInstanceId_ = "";
      /**
       * <code>optional string error_handling_process_instance_id = 3;</code>
       * @return Whether the errorHandlingProcessInstanceId field is set.
       */
      public boolean hasErrorHandlingProcessInstanceId() {
        return ((bitField0_ & 0x00000002) != 0);
      }
      /**
       * <code>optional string error_handling_process_instance_id = 3;</code>
       * @return The errorHandlingProcessInstanceId.
       */
      public java.lang.String getErrorHandlingProcessInstanceId() {
        java.lang.Object ref = errorHandlingProcessInstanceId_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          errorHandlingProcessInstanceId_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>optional string error_handling_process_instance_id = 3;</code>
       * @return The bytes for errorHandlingProcessInstanceId.
       */
      public com.google.protobuf.ByteString
          getErrorHandlingProcessInstanceIdBytes() {
        java.lang.Object ref = errorHandlingProcessInstanceId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          errorHandlingProcessInstanceId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string error_handling_process_instance_id = 3;</code>
       * @param value The errorHandlingProcessInstanceId to set.
       * @return This builder for chaining.
       */
      public Builder setErrorHandlingProcessInstanceId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        errorHandlingProcessInstanceId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string error_handling_process_instance_id = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearErrorHandlingProcessInstanceId() {
        bitField0_ = (bitField0_ & ~0x00000002);
        errorHandlingProcessInstanceId_ = getDefaultInstance().getErrorHandlingProcessInstanceId();
        onChanged();
        return this;
      }
      /**
       * <code>optional string error_handling_process_instance_id = 3;</code>
       * @param value The bytes for errorHandlingProcessInstanceId to set.
       * @return This builder for chaining.
       */
      public Builder setErrorHandlingProcessInstanceIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000002;
        errorHandlingProcessInstanceId_ = value;
        onChanged();
        return this;
      }

      private int state_ ;
      /**
       * <pre>
       * work item data
       * </pre>
       *
       * <code>int32 state = 4;</code>
       * @return The state.
       */
      @java.lang.Override
      public int getState() {
        return state_;
      }
      /**
       * <pre>
       * work item data
       * </pre>
       *
       * <code>int32 state = 4;</code>
       * @param value The state to set.
       * @return This builder for chaining.
       */
      public Builder setState(int value) {
        
        state_ = value;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * work item data
       * </pre>
       *
       * <code>int32 state = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearState() {
        
        state_ = 0;
        onChanged();
        return this;
      }

      private java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable> variable_ =
        java.util.Collections.emptyList();
      private void ensureVariableIsMutable() {
        if (!((bitField0_ & 0x00000004) != 0)) {
          variable_ = new java.util.ArrayList<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable>(variable_);
          bitField0_ |= 0x00000004;
         }
      }

      private com.google.protobuf.RepeatedFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder> variableBuilder_;

      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
       */
      public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable> getVariableList() {
        if (variableBuilder_ == null) {
          return java.util.Collections.unmodifiableList(variable_);
        } else {
          return variableBuilder_.getMessageList();
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
       */
      public int getVariableCount() {
        if (variableBuilder_ == null) {
          return variable_.size();
        } else {
          return variableBuilder_.getCount();
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable getVariable(int index) {
        if (variableBuilder_ == null) {
          return variable_.get(index);
        } else {
          return variableBuilder_.getMessage(index);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
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
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
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
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
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
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
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
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
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
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
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
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
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
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
       */
      public Builder clearVariable() {
        if (variableBuilder_ == null) {
          variable_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000004);
          onChanged();
        } else {
          variableBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
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
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder getVariableBuilder(
          int index) {
        return getVariableFieldBuilder().getBuilder(index);
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder getVariableOrBuilder(
          int index) {
        if (variableBuilder_ == null) {
          return variable_.get(index);  } else {
          return variableBuilder_.getMessageOrBuilder(index);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
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
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder addVariableBuilder() {
        return getVariableFieldBuilder().addBuilder(
            org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.getDefaultInstance());
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder addVariableBuilder(
          int index) {
        return getVariableFieldBuilder().addBuilder(
            index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.getDefaultInstance());
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable variable = 5;</code>
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
                  ((bitField0_ & 0x00000004) != 0),
                  getParentForChildren(),
                  isClean());
          variable_ = null;
        }
        return variableBuilder_;
      }

      private java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable> result_ =
        java.util.Collections.emptyList();
      private void ensureResultIsMutable() {
        if (!((bitField0_ & 0x00000008) != 0)) {
          result_ = new java.util.ArrayList<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable>(result_);
          bitField0_ |= 0x00000008;
         }
      }

      private com.google.protobuf.RepeatedFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder> resultBuilder_;

      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
       */
      public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable> getResultList() {
        if (resultBuilder_ == null) {
          return java.util.Collections.unmodifiableList(result_);
        } else {
          return resultBuilder_.getMessageList();
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
       */
      public int getResultCount() {
        if (resultBuilder_ == null) {
          return result_.size();
        } else {
          return resultBuilder_.getCount();
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable getResult(int index) {
        if (resultBuilder_ == null) {
          return result_.get(index);
        } else {
          return resultBuilder_.getMessage(index);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
       */
      public Builder setResult(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable value) {
        if (resultBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureResultIsMutable();
          result_.set(index, value);
          onChanged();
        } else {
          resultBuilder_.setMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
       */
      public Builder setResult(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder builderForValue) {
        if (resultBuilder_ == null) {
          ensureResultIsMutable();
          result_.set(index, builderForValue.build());
          onChanged();
        } else {
          resultBuilder_.setMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
       */
      public Builder addResult(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable value) {
        if (resultBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureResultIsMutable();
          result_.add(value);
          onChanged();
        } else {
          resultBuilder_.addMessage(value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
       */
      public Builder addResult(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable value) {
        if (resultBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureResultIsMutable();
          result_.add(index, value);
          onChanged();
        } else {
          resultBuilder_.addMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
       */
      public Builder addResult(
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder builderForValue) {
        if (resultBuilder_ == null) {
          ensureResultIsMutable();
          result_.add(builderForValue.build());
          onChanged();
        } else {
          resultBuilder_.addMessage(builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
       */
      public Builder addResult(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder builderForValue) {
        if (resultBuilder_ == null) {
          ensureResultIsMutable();
          result_.add(index, builderForValue.build());
          onChanged();
        } else {
          resultBuilder_.addMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
       */
      public Builder addAllResult(
          java.lang.Iterable<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable> values) {
        if (resultBuilder_ == null) {
          ensureResultIsMutable();
          com.google.protobuf.AbstractMessageLite.Builder.addAll(
              values, result_);
          onChanged();
        } else {
          resultBuilder_.addAllMessages(values);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
       */
      public Builder clearResult() {
        if (resultBuilder_ == null) {
          result_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000008);
          onChanged();
        } else {
          resultBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
       */
      public Builder removeResult(int index) {
        if (resultBuilder_ == null) {
          ensureResultIsMutable();
          result_.remove(index);
          onChanged();
        } else {
          resultBuilder_.remove(index);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder getResultBuilder(
          int index) {
        return getResultFieldBuilder().getBuilder(index);
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder getResultOrBuilder(
          int index) {
        if (resultBuilder_ == null) {
          return result_.get(index);  } else {
          return resultBuilder_.getMessageOrBuilder(index);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
       */
      public java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder> 
           getResultOrBuilderList() {
        if (resultBuilder_ != null) {
          return resultBuilder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(result_);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder addResultBuilder() {
        return getResultFieldBuilder().addBuilder(
            org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.getDefaultInstance());
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder addResultBuilder(
          int index) {
        return getResultFieldBuilder().addBuilder(
            index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.getDefaultInstance());
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Variable result = 6;</code>
       */
      public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder> 
           getResultBuilderList() {
        return getResultFieldBuilder().getBuilderList();
      }
      private com.google.protobuf.RepeatedFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder> 
          getResultFieldBuilder() {
        if (resultBuilder_ == null) {
          resultBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.Variable.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.VariableOrBuilder>(
                  result_,
                  ((bitField0_ & 0x00000008) != 0),
                  getParentForChildren(),
                  isClean());
          result_ = null;
        }
        return resultBuilder_;
      }

      private java.lang.Object phaseId_ = "";
      /**
       * <code>optional string phase_id = 7;</code>
       * @return Whether the phaseId field is set.
       */
      public boolean hasPhaseId() {
        return ((bitField0_ & 0x00000010) != 0);
      }
      /**
       * <code>optional string phase_id = 7;</code>
       * @return The phaseId.
       */
      public java.lang.String getPhaseId() {
        java.lang.Object ref = phaseId_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          phaseId_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>optional string phase_id = 7;</code>
       * @return The bytes for phaseId.
       */
      public com.google.protobuf.ByteString
          getPhaseIdBytes() {
        java.lang.Object ref = phaseId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          phaseId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string phase_id = 7;</code>
       * @param value The phaseId to set.
       * @return This builder for chaining.
       */
      public Builder setPhaseId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000010;
        phaseId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string phase_id = 7;</code>
       * @return This builder for chaining.
       */
      public Builder clearPhaseId() {
        bitField0_ = (bitField0_ & ~0x00000010);
        phaseId_ = getDefaultInstance().getPhaseId();
        onChanged();
        return this;
      }
      /**
       * <code>optional string phase_id = 7;</code>
       * @param value The bytes for phaseId to set.
       * @return This builder for chaining.
       */
      public Builder setPhaseIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000010;
        phaseId_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object phaseStatus_ = "";
      /**
       * <code>optional string phase_status = 8;</code>
       * @return Whether the phaseStatus field is set.
       */
      public boolean hasPhaseStatus() {
        return ((bitField0_ & 0x00000020) != 0);
      }
      /**
       * <code>optional string phase_status = 8;</code>
       * @return The phaseStatus.
       */
      public java.lang.String getPhaseStatus() {
        java.lang.Object ref = phaseStatus_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          phaseStatus_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>optional string phase_status = 8;</code>
       * @return The bytes for phaseStatus.
       */
      public com.google.protobuf.ByteString
          getPhaseStatusBytes() {
        java.lang.Object ref = phaseStatus_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          phaseStatus_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string phase_status = 8;</code>
       * @param value The phaseStatus to set.
       * @return This builder for chaining.
       */
      public Builder setPhaseStatus(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000020;
        phaseStatus_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string phase_status = 8;</code>
       * @return This builder for chaining.
       */
      public Builder clearPhaseStatus() {
        bitField0_ = (bitField0_ & ~0x00000020);
        phaseStatus_ = getDefaultInstance().getPhaseStatus();
        onChanged();
        return this;
      }
      /**
       * <code>optional string phase_status = 8;</code>
       * @param value The bytes for phaseStatus to set.
       * @return This builder for chaining.
       */
      public Builder setPhaseStatusBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000020;
        phaseStatus_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object name_ = "";
      /**
       * <code>optional string name = 9;</code>
       * @return Whether the name field is set.
       */
      public boolean hasName() {
        return ((bitField0_ & 0x00000040) != 0);
      }
      /**
       * <code>optional string name = 9;</code>
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
       * <code>optional string name = 9;</code>
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
       * <code>optional string name = 9;</code>
       * @param value The name to set.
       * @return This builder for chaining.
       */
      public Builder setName(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000040;
        name_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string name = 9;</code>
       * @return This builder for chaining.
       */
      public Builder clearName() {
        bitField0_ = (bitField0_ & ~0x00000040);
        name_ = getDefaultInstance().getName();
        onChanged();
        return this;
      }
      /**
       * <code>optional string name = 9;</code>
       * @param value The bytes for name to set.
       * @return This builder for chaining.
       */
      public Builder setNameBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000040;
        name_ = value;
        onChanged();
        return this;
      }

      private long startDate_ ;
      /**
       * <code>optional int64 start_date = 10;</code>
       * @return Whether the startDate field is set.
       */
      @java.lang.Override
      public boolean hasStartDate() {
        return ((bitField0_ & 0x00000080) != 0);
      }
      /**
       * <code>optional int64 start_date = 10;</code>
       * @return The startDate.
       */
      @java.lang.Override
      public long getStartDate() {
        return startDate_;
      }
      /**
       * <code>optional int64 start_date = 10;</code>
       * @param value The startDate to set.
       * @return This builder for chaining.
       */
      public Builder setStartDate(long value) {
        bitField0_ |= 0x00000080;
        startDate_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int64 start_date = 10;</code>
       * @return This builder for chaining.
       */
      public Builder clearStartDate() {
        bitField0_ = (bitField0_ & ~0x00000080);
        startDate_ = 0L;
        onChanged();
        return this;
      }

      private long completeDate_ ;
      /**
       * <code>optional int64 complete_date = 11;</code>
       * @return Whether the completeDate field is set.
       */
      @java.lang.Override
      public boolean hasCompleteDate() {
        return ((bitField0_ & 0x00000100) != 0);
      }
      /**
       * <code>optional int64 complete_date = 11;</code>
       * @return The completeDate.
       */
      @java.lang.Override
      public long getCompleteDate() {
        return completeDate_;
      }
      /**
       * <code>optional int64 complete_date = 11;</code>
       * @param value The completeDate to set.
       * @return This builder for chaining.
       */
      public Builder setCompleteDate(long value) {
        bitField0_ |= 0x00000100;
        completeDate_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int64 complete_date = 11;</code>
       * @return This builder for chaining.
       */
      public Builder clearCompleteDate() {
        bitField0_ = (bitField0_ & ~0x00000100);
        completeDate_ = 0L;
        onChanged();
        return this;
      }

      private com.google.protobuf.Any workItemData_;
      private com.google.protobuf.SingleFieldBuilderV3<
          com.google.protobuf.Any, com.google.protobuf.Any.Builder, com.google.protobuf.AnyOrBuilder> workItemDataBuilder_;
      /**
       * <code>optional .google.protobuf.Any work_item_data = 12;</code>
       * @return Whether the workItemData field is set.
       */
      public boolean hasWorkItemData() {
        return ((bitField0_ & 0x00000200) != 0);
      }
      /**
       * <code>optional .google.protobuf.Any work_item_data = 12;</code>
       * @return The workItemData.
       */
      public com.google.protobuf.Any getWorkItemData() {
        if (workItemDataBuilder_ == null) {
          return workItemData_ == null ? com.google.protobuf.Any.getDefaultInstance() : workItemData_;
        } else {
          return workItemDataBuilder_.getMessage();
        }
      }
      /**
       * <code>optional .google.protobuf.Any work_item_data = 12;</code>
       */
      public Builder setWorkItemData(com.google.protobuf.Any value) {
        if (workItemDataBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          workItemData_ = value;
          onChanged();
        } else {
          workItemDataBuilder_.setMessage(value);
        }
        bitField0_ |= 0x00000200;
        return this;
      }
      /**
       * <code>optional .google.protobuf.Any work_item_data = 12;</code>
       */
      public Builder setWorkItemData(
          com.google.protobuf.Any.Builder builderForValue) {
        if (workItemDataBuilder_ == null) {
          workItemData_ = builderForValue.build();
          onChanged();
        } else {
          workItemDataBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00000200;
        return this;
      }
      /**
       * <code>optional .google.protobuf.Any work_item_data = 12;</code>
       */
      public Builder mergeWorkItemData(com.google.protobuf.Any value) {
        if (workItemDataBuilder_ == null) {
          if (((bitField0_ & 0x00000200) != 0) &&
              workItemData_ != null &&
              workItemData_ != com.google.protobuf.Any.getDefaultInstance()) {
            workItemData_ =
              com.google.protobuf.Any.newBuilder(workItemData_).mergeFrom(value).buildPartial();
          } else {
            workItemData_ = value;
          }
          onChanged();
        } else {
          workItemDataBuilder_.mergeFrom(value);
        }
        bitField0_ |= 0x00000200;
        return this;
      }
      /**
       * <code>optional .google.protobuf.Any work_item_data = 12;</code>
       */
      public Builder clearWorkItemData() {
        if (workItemDataBuilder_ == null) {
          workItemData_ = null;
          onChanged();
        } else {
          workItemDataBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000200);
        return this;
      }
      /**
       * <code>optional .google.protobuf.Any work_item_data = 12;</code>
       */
      public com.google.protobuf.Any.Builder getWorkItemDataBuilder() {
        bitField0_ |= 0x00000200;
        onChanged();
        return getWorkItemDataFieldBuilder().getBuilder();
      }
      /**
       * <code>optional .google.protobuf.Any work_item_data = 12;</code>
       */
      public com.google.protobuf.AnyOrBuilder getWorkItemDataOrBuilder() {
        if (workItemDataBuilder_ != null) {
          return workItemDataBuilder_.getMessageOrBuilder();
        } else {
          return workItemData_ == null ?
              com.google.protobuf.Any.getDefaultInstance() : workItemData_;
        }
      }
      /**
       * <code>optional .google.protobuf.Any work_item_data = 12;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          com.google.protobuf.Any, com.google.protobuf.Any.Builder, com.google.protobuf.AnyOrBuilder> 
          getWorkItemDataFieldBuilder() {
        if (workItemDataBuilder_ == null) {
          workItemDataBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              com.google.protobuf.Any, com.google.protobuf.Any.Builder, com.google.protobuf.AnyOrBuilder>(
                  getWorkItemData(),
                  getParentForChildren(),
                  isClean());
          workItemData_ = null;
        }
        return workItemDataBuilder_;
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


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.WorkItemNodeInstanceContent)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.WorkItemNodeInstanceContent)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<WorkItemNodeInstanceContent>
        PARSER = new com.google.protobuf.AbstractParser<WorkItemNodeInstanceContent>() {
      @java.lang.Override
      public WorkItemNodeInstanceContent parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new WorkItemNodeInstanceContent(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<WorkItemNodeInstanceContent> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<WorkItemNodeInstanceContent> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.WorkItemNodeInstanceContent getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface LambdaSubProcessNodeInstanceContentOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.LambdaSubProcessNodeInstanceContent)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>optional string process_instance_id = 1;</code>
     * @return Whether the processInstanceId field is set.
     */
    boolean hasProcessInstanceId();
    /**
     * <code>optional string process_instance_id = 1;</code>
     * @return The processInstanceId.
     */
    java.lang.String getProcessInstanceId();
    /**
     * <code>optional string process_instance_id = 1;</code>
     * @return The bytes for processInstanceId.
     */
    com.google.protobuf.ByteString
        getProcessInstanceIdBytes();

    /**
     * <code>repeated string timer_instance_id = 2;</code>
     * @return A list containing the timerInstanceId.
     */
    java.util.List<java.lang.String>
        getTimerInstanceIdList();
    /**
     * <code>repeated string timer_instance_id = 2;</code>
     * @return The count of timerInstanceId.
     */
    int getTimerInstanceIdCount();
    /**
     * <code>repeated string timer_instance_id = 2;</code>
     * @param index The index of the element to return.
     * @return The timerInstanceId at the given index.
     */
    java.lang.String getTimerInstanceId(int index);
    /**
     * <code>repeated string timer_instance_id = 2;</code>
     * @param index The index of the value to return.
     * @return The bytes of the timerInstanceId at the given index.
     */
    com.google.protobuf.ByteString
        getTimerInstanceIdBytes(int index);
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.LambdaSubProcessNodeInstanceContent}
   */
  public static final class LambdaSubProcessNodeInstanceContent extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.LambdaSubProcessNodeInstanceContent)
      LambdaSubProcessNodeInstanceContentOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use LambdaSubProcessNodeInstanceContent.newBuilder() to construct.
    private LambdaSubProcessNodeInstanceContent(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private LambdaSubProcessNodeInstanceContent() {
      processInstanceId_ = "";
      timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new LambdaSubProcessNodeInstanceContent();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private LambdaSubProcessNodeInstanceContent(
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
              processInstanceId_ = s;
              break;
            }
            case 18: {
              java.lang.String s = input.readStringRequireUtf8();
              if (!((mutable_bitField0_ & 0x00000002) != 0)) {
                timerInstanceId_ = new com.google.protobuf.LazyStringArrayList();
                mutable_bitField0_ |= 0x00000002;
              }
              timerInstanceId_.add(s);
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
        if (((mutable_bitField0_ & 0x00000002) != 0)) {
          timerInstanceId_ = timerInstanceId_.getUnmodifiableView();
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_LambdaSubProcessNodeInstanceContent_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_LambdaSubProcessNodeInstanceContent_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent.Builder.class);
    }

    private int bitField0_;
    public static final int PROCESS_INSTANCE_ID_FIELD_NUMBER = 1;
    private volatile java.lang.Object processInstanceId_;
    /**
     * <code>optional string process_instance_id = 1;</code>
     * @return Whether the processInstanceId field is set.
     */
    @java.lang.Override
    public boolean hasProcessInstanceId() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>optional string process_instance_id = 1;</code>
     * @return The processInstanceId.
     */
    @java.lang.Override
    public java.lang.String getProcessInstanceId() {
      java.lang.Object ref = processInstanceId_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        processInstanceId_ = s;
        return s;
      }
    }
    /**
     * <code>optional string process_instance_id = 1;</code>
     * @return The bytes for processInstanceId.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getProcessInstanceIdBytes() {
      java.lang.Object ref = processInstanceId_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        processInstanceId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int TIMER_INSTANCE_ID_FIELD_NUMBER = 2;
    private com.google.protobuf.LazyStringList timerInstanceId_;
    /**
     * <code>repeated string timer_instance_id = 2;</code>
     * @return A list containing the timerInstanceId.
     */
    public com.google.protobuf.ProtocolStringList
        getTimerInstanceIdList() {
      return timerInstanceId_;
    }
    /**
     * <code>repeated string timer_instance_id = 2;</code>
     * @return The count of timerInstanceId.
     */
    public int getTimerInstanceIdCount() {
      return timerInstanceId_.size();
    }
    /**
     * <code>repeated string timer_instance_id = 2;</code>
     * @param index The index of the element to return.
     * @return The timerInstanceId at the given index.
     */
    public java.lang.String getTimerInstanceId(int index) {
      return timerInstanceId_.get(index);
    }
    /**
     * <code>repeated string timer_instance_id = 2;</code>
     * @param index The index of the value to return.
     * @return The bytes of the timerInstanceId at the given index.
     */
    public com.google.protobuf.ByteString
        getTimerInstanceIdBytes(int index) {
      return timerInstanceId_.getByteString(index);
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
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, processInstanceId_);
      }
      for (int i = 0; i < timerInstanceId_.size(); i++) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, timerInstanceId_.getRaw(i));
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, processInstanceId_);
      }
      {
        int dataSize = 0;
        for (int i = 0; i < timerInstanceId_.size(); i++) {
          dataSize += computeStringSizeNoTag(timerInstanceId_.getRaw(i));
        }
        size += dataSize;
        size += 1 * getTimerInstanceIdList().size();
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
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent other = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent) obj;

      if (hasProcessInstanceId() != other.hasProcessInstanceId()) return false;
      if (hasProcessInstanceId()) {
        if (!getProcessInstanceId()
            .equals(other.getProcessInstanceId())) return false;
      }
      if (!getTimerInstanceIdList()
          .equals(other.getTimerInstanceIdList())) return false;
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
      if (hasProcessInstanceId()) {
        hash = (37 * hash) + PROCESS_INSTANCE_ID_FIELD_NUMBER;
        hash = (53 * hash) + getProcessInstanceId().hashCode();
      }
      if (getTimerInstanceIdCount() > 0) {
        hash = (37 * hash) + TIMER_INSTANCE_ID_FIELD_NUMBER;
        hash = (53 * hash) + getTimerInstanceIdList().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent parseFrom(
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
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent prototype) {
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
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.LambdaSubProcessNodeInstanceContent}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.LambdaSubProcessNodeInstanceContent)
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContentOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_LambdaSubProcessNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_LambdaSubProcessNodeInstanceContent_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent.newBuilder()
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
        processInstanceId_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_LambdaSubProcessNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent build() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent result = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          to_bitField0_ |= 0x00000001;
        }
        result.processInstanceId_ = processInstanceId_;
        if (((bitField0_ & 0x00000002) != 0)) {
          timerInstanceId_ = timerInstanceId_.getUnmodifiableView();
          bitField0_ = (bitField0_ & ~0x00000002);
        }
        result.timerInstanceId_ = timerInstanceId_;
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
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent.getDefaultInstance()) return this;
        if (other.hasProcessInstanceId()) {
          bitField0_ |= 0x00000001;
          processInstanceId_ = other.processInstanceId_;
          onChanged();
        }
        if (!other.timerInstanceId_.isEmpty()) {
          if (timerInstanceId_.isEmpty()) {
            timerInstanceId_ = other.timerInstanceId_;
            bitField0_ = (bitField0_ & ~0x00000002);
          } else {
            ensureTimerInstanceIdIsMutable();
            timerInstanceId_.addAll(other.timerInstanceId_);
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
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.lang.Object processInstanceId_ = "";
      /**
       * <code>optional string process_instance_id = 1;</code>
       * @return Whether the processInstanceId field is set.
       */
      public boolean hasProcessInstanceId() {
        return ((bitField0_ & 0x00000001) != 0);
      }
      /**
       * <code>optional string process_instance_id = 1;</code>
       * @return The processInstanceId.
       */
      public java.lang.String getProcessInstanceId() {
        java.lang.Object ref = processInstanceId_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          processInstanceId_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>optional string process_instance_id = 1;</code>
       * @return The bytes for processInstanceId.
       */
      public com.google.protobuf.ByteString
          getProcessInstanceIdBytes() {
        java.lang.Object ref = processInstanceId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          processInstanceId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string process_instance_id = 1;</code>
       * @param value The processInstanceId to set.
       * @return This builder for chaining.
       */
      public Builder setProcessInstanceId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        processInstanceId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string process_instance_id = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearProcessInstanceId() {
        bitField0_ = (bitField0_ & ~0x00000001);
        processInstanceId_ = getDefaultInstance().getProcessInstanceId();
        onChanged();
        return this;
      }
      /**
       * <code>optional string process_instance_id = 1;</code>
       * @param value The bytes for processInstanceId to set.
       * @return This builder for chaining.
       */
      public Builder setProcessInstanceIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000001;
        processInstanceId_ = value;
        onChanged();
        return this;
      }

      private com.google.protobuf.LazyStringList timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      private void ensureTimerInstanceIdIsMutable() {
        if (!((bitField0_ & 0x00000002) != 0)) {
          timerInstanceId_ = new com.google.protobuf.LazyStringArrayList(timerInstanceId_);
          bitField0_ |= 0x00000002;
         }
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @return A list containing the timerInstanceId.
       */
      public com.google.protobuf.ProtocolStringList
          getTimerInstanceIdList() {
        return timerInstanceId_.getUnmodifiableView();
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @return The count of timerInstanceId.
       */
      public int getTimerInstanceIdCount() {
        return timerInstanceId_.size();
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @param index The index of the element to return.
       * @return The timerInstanceId at the given index.
       */
      public java.lang.String getTimerInstanceId(int index) {
        return timerInstanceId_.get(index);
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @param index The index of the value to return.
       * @return The bytes of the timerInstanceId at the given index.
       */
      public com.google.protobuf.ByteString
          getTimerInstanceIdBytes(int index) {
        return timerInstanceId_.getByteString(index);
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @param index The index to set the value at.
       * @param value The timerInstanceId to set.
       * @return This builder for chaining.
       */
      public Builder setTimerInstanceId(
          int index, java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureTimerInstanceIdIsMutable();
        timerInstanceId_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @param value The timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addTimerInstanceId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureTimerInstanceIdIsMutable();
        timerInstanceId_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @param values The timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addAllTimerInstanceId(
          java.lang.Iterable<java.lang.String> values) {
        ensureTimerInstanceIdIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, timerInstanceId_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearTimerInstanceId() {
        timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000002);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @param value The bytes of the timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addTimerInstanceIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        ensureTimerInstanceIdIsMutable();
        timerInstanceId_.add(value);
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


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.LambdaSubProcessNodeInstanceContent)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.LambdaSubProcessNodeInstanceContent)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<LambdaSubProcessNodeInstanceContent>
        PARSER = new com.google.protobuf.AbstractParser<LambdaSubProcessNodeInstanceContent>() {
      @java.lang.Override
      public LambdaSubProcessNodeInstanceContent parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new LambdaSubProcessNodeInstanceContent(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<LambdaSubProcessNodeInstanceContent> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<LambdaSubProcessNodeInstanceContent> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.LambdaSubProcessNodeInstanceContent getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface SubProcessNodeInstanceContentOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.SubProcessNodeInstanceContent)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>optional string process_instance_id = 1;</code>
     * @return Whether the processInstanceId field is set.
     */
    boolean hasProcessInstanceId();
    /**
     * <code>optional string process_instance_id = 1;</code>
     * @return The processInstanceId.
     */
    java.lang.String getProcessInstanceId();
    /**
     * <code>optional string process_instance_id = 1;</code>
     * @return The bytes for processInstanceId.
     */
    com.google.protobuf.ByteString
        getProcessInstanceIdBytes();

    /**
     * <code>repeated string timer_instance_id = 2;</code>
     * @return A list containing the timerInstanceId.
     */
    java.util.List<java.lang.String>
        getTimerInstanceIdList();
    /**
     * <code>repeated string timer_instance_id = 2;</code>
     * @return The count of timerInstanceId.
     */
    int getTimerInstanceIdCount();
    /**
     * <code>repeated string timer_instance_id = 2;</code>
     * @param index The index of the element to return.
     * @return The timerInstanceId at the given index.
     */
    java.lang.String getTimerInstanceId(int index);
    /**
     * <code>repeated string timer_instance_id = 2;</code>
     * @param index The index of the value to return.
     * @return The bytes of the timerInstanceId at the given index.
     */
    com.google.protobuf.ByteString
        getTimerInstanceIdBytes(int index);
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.SubProcessNodeInstanceContent}
   */
  public static final class SubProcessNodeInstanceContent extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.SubProcessNodeInstanceContent)
      SubProcessNodeInstanceContentOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use SubProcessNodeInstanceContent.newBuilder() to construct.
    private SubProcessNodeInstanceContent(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private SubProcessNodeInstanceContent() {
      processInstanceId_ = "";
      timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new SubProcessNodeInstanceContent();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private SubProcessNodeInstanceContent(
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
              processInstanceId_ = s;
              break;
            }
            case 18: {
              java.lang.String s = input.readStringRequireUtf8();
              if (!((mutable_bitField0_ & 0x00000002) != 0)) {
                timerInstanceId_ = new com.google.protobuf.LazyStringArrayList();
                mutable_bitField0_ |= 0x00000002;
              }
              timerInstanceId_.add(s);
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
        if (((mutable_bitField0_ & 0x00000002) != 0)) {
          timerInstanceId_ = timerInstanceId_.getUnmodifiableView();
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_SubProcessNodeInstanceContent_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_SubProcessNodeInstanceContent_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent.Builder.class);
    }

    private int bitField0_;
    public static final int PROCESS_INSTANCE_ID_FIELD_NUMBER = 1;
    private volatile java.lang.Object processInstanceId_;
    /**
     * <code>optional string process_instance_id = 1;</code>
     * @return Whether the processInstanceId field is set.
     */
    @java.lang.Override
    public boolean hasProcessInstanceId() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>optional string process_instance_id = 1;</code>
     * @return The processInstanceId.
     */
    @java.lang.Override
    public java.lang.String getProcessInstanceId() {
      java.lang.Object ref = processInstanceId_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        processInstanceId_ = s;
        return s;
      }
    }
    /**
     * <code>optional string process_instance_id = 1;</code>
     * @return The bytes for processInstanceId.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getProcessInstanceIdBytes() {
      java.lang.Object ref = processInstanceId_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        processInstanceId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int TIMER_INSTANCE_ID_FIELD_NUMBER = 2;
    private com.google.protobuf.LazyStringList timerInstanceId_;
    /**
     * <code>repeated string timer_instance_id = 2;</code>
     * @return A list containing the timerInstanceId.
     */
    public com.google.protobuf.ProtocolStringList
        getTimerInstanceIdList() {
      return timerInstanceId_;
    }
    /**
     * <code>repeated string timer_instance_id = 2;</code>
     * @return The count of timerInstanceId.
     */
    public int getTimerInstanceIdCount() {
      return timerInstanceId_.size();
    }
    /**
     * <code>repeated string timer_instance_id = 2;</code>
     * @param index The index of the element to return.
     * @return The timerInstanceId at the given index.
     */
    public java.lang.String getTimerInstanceId(int index) {
      return timerInstanceId_.get(index);
    }
    /**
     * <code>repeated string timer_instance_id = 2;</code>
     * @param index The index of the value to return.
     * @return The bytes of the timerInstanceId at the given index.
     */
    public com.google.protobuf.ByteString
        getTimerInstanceIdBytes(int index) {
      return timerInstanceId_.getByteString(index);
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
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, processInstanceId_);
      }
      for (int i = 0; i < timerInstanceId_.size(); i++) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, timerInstanceId_.getRaw(i));
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, processInstanceId_);
      }
      {
        int dataSize = 0;
        for (int i = 0; i < timerInstanceId_.size(); i++) {
          dataSize += computeStringSizeNoTag(timerInstanceId_.getRaw(i));
        }
        size += dataSize;
        size += 1 * getTimerInstanceIdList().size();
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
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent other = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent) obj;

      if (hasProcessInstanceId() != other.hasProcessInstanceId()) return false;
      if (hasProcessInstanceId()) {
        if (!getProcessInstanceId()
            .equals(other.getProcessInstanceId())) return false;
      }
      if (!getTimerInstanceIdList()
          .equals(other.getTimerInstanceIdList())) return false;
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
      if (hasProcessInstanceId()) {
        hash = (37 * hash) + PROCESS_INSTANCE_ID_FIELD_NUMBER;
        hash = (53 * hash) + getProcessInstanceId().hashCode();
      }
      if (getTimerInstanceIdCount() > 0) {
        hash = (37 * hash) + TIMER_INSTANCE_ID_FIELD_NUMBER;
        hash = (53 * hash) + getTimerInstanceIdList().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent parseFrom(
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
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent prototype) {
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
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.SubProcessNodeInstanceContent}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.SubProcessNodeInstanceContent)
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContentOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_SubProcessNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_SubProcessNodeInstanceContent_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent.newBuilder()
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
        processInstanceId_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_SubProcessNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent build() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent result = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          to_bitField0_ |= 0x00000001;
        }
        result.processInstanceId_ = processInstanceId_;
        if (((bitField0_ & 0x00000002) != 0)) {
          timerInstanceId_ = timerInstanceId_.getUnmodifiableView();
          bitField0_ = (bitField0_ & ~0x00000002);
        }
        result.timerInstanceId_ = timerInstanceId_;
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
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent.getDefaultInstance()) return this;
        if (other.hasProcessInstanceId()) {
          bitField0_ |= 0x00000001;
          processInstanceId_ = other.processInstanceId_;
          onChanged();
        }
        if (!other.timerInstanceId_.isEmpty()) {
          if (timerInstanceId_.isEmpty()) {
            timerInstanceId_ = other.timerInstanceId_;
            bitField0_ = (bitField0_ & ~0x00000002);
          } else {
            ensureTimerInstanceIdIsMutable();
            timerInstanceId_.addAll(other.timerInstanceId_);
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
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.lang.Object processInstanceId_ = "";
      /**
       * <code>optional string process_instance_id = 1;</code>
       * @return Whether the processInstanceId field is set.
       */
      public boolean hasProcessInstanceId() {
        return ((bitField0_ & 0x00000001) != 0);
      }
      /**
       * <code>optional string process_instance_id = 1;</code>
       * @return The processInstanceId.
       */
      public java.lang.String getProcessInstanceId() {
        java.lang.Object ref = processInstanceId_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          processInstanceId_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>optional string process_instance_id = 1;</code>
       * @return The bytes for processInstanceId.
       */
      public com.google.protobuf.ByteString
          getProcessInstanceIdBytes() {
        java.lang.Object ref = processInstanceId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          processInstanceId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string process_instance_id = 1;</code>
       * @param value The processInstanceId to set.
       * @return This builder for chaining.
       */
      public Builder setProcessInstanceId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        processInstanceId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string process_instance_id = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearProcessInstanceId() {
        bitField0_ = (bitField0_ & ~0x00000001);
        processInstanceId_ = getDefaultInstance().getProcessInstanceId();
        onChanged();
        return this;
      }
      /**
       * <code>optional string process_instance_id = 1;</code>
       * @param value The bytes for processInstanceId to set.
       * @return This builder for chaining.
       */
      public Builder setProcessInstanceIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000001;
        processInstanceId_ = value;
        onChanged();
        return this;
      }

      private com.google.protobuf.LazyStringList timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      private void ensureTimerInstanceIdIsMutable() {
        if (!((bitField0_ & 0x00000002) != 0)) {
          timerInstanceId_ = new com.google.protobuf.LazyStringArrayList(timerInstanceId_);
          bitField0_ |= 0x00000002;
         }
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @return A list containing the timerInstanceId.
       */
      public com.google.protobuf.ProtocolStringList
          getTimerInstanceIdList() {
        return timerInstanceId_.getUnmodifiableView();
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @return The count of timerInstanceId.
       */
      public int getTimerInstanceIdCount() {
        return timerInstanceId_.size();
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @param index The index of the element to return.
       * @return The timerInstanceId at the given index.
       */
      public java.lang.String getTimerInstanceId(int index) {
        return timerInstanceId_.get(index);
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @param index The index of the value to return.
       * @return The bytes of the timerInstanceId at the given index.
       */
      public com.google.protobuf.ByteString
          getTimerInstanceIdBytes(int index) {
        return timerInstanceId_.getByteString(index);
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @param index The index to set the value at.
       * @param value The timerInstanceId to set.
       * @return This builder for chaining.
       */
      public Builder setTimerInstanceId(
          int index, java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureTimerInstanceIdIsMutable();
        timerInstanceId_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @param value The timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addTimerInstanceId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureTimerInstanceIdIsMutable();
        timerInstanceId_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @param values The timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addAllTimerInstanceId(
          java.lang.Iterable<java.lang.String> values) {
        ensureTimerInstanceIdIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, timerInstanceId_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearTimerInstanceId() {
        timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000002);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 2;</code>
       * @param value The bytes of the timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addTimerInstanceIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        ensureTimerInstanceIdIsMutable();
        timerInstanceId_.add(value);
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


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.SubProcessNodeInstanceContent)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.SubProcessNodeInstanceContent)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<SubProcessNodeInstanceContent>
        PARSER = new com.google.protobuf.AbstractParser<SubProcessNodeInstanceContent>() {
      @java.lang.Override
      public SubProcessNodeInstanceContent parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new SubProcessNodeInstanceContent(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<SubProcessNodeInstanceContent> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<SubProcessNodeInstanceContent> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.SubProcessNodeInstanceContent getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface MilestoneNodeInstanceContentOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.MilestoneNodeInstanceContent)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return A list containing the timerInstanceId.
     */
    java.util.List<java.lang.String>
        getTimerInstanceIdList();
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return The count of timerInstanceId.
     */
    int getTimerInstanceIdCount();
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the element to return.
     * @return The timerInstanceId at the given index.
     */
    java.lang.String getTimerInstanceId(int index);
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the value to return.
     * @return The bytes of the timerInstanceId at the given index.
     */
    com.google.protobuf.ByteString
        getTimerInstanceIdBytes(int index);
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.MilestoneNodeInstanceContent}
   */
  public static final class MilestoneNodeInstanceContent extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.MilestoneNodeInstanceContent)
      MilestoneNodeInstanceContentOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use MilestoneNodeInstanceContent.newBuilder() to construct.
    private MilestoneNodeInstanceContent(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private MilestoneNodeInstanceContent() {
      timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new MilestoneNodeInstanceContent();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private MilestoneNodeInstanceContent(
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
                timerInstanceId_ = new com.google.protobuf.LazyStringArrayList();
                mutable_bitField0_ |= 0x00000001;
              }
              timerInstanceId_.add(s);
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
          timerInstanceId_ = timerInstanceId_.getUnmodifiableView();
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_MilestoneNodeInstanceContent_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_MilestoneNodeInstanceContent_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent.Builder.class);
    }

    public static final int TIMER_INSTANCE_ID_FIELD_NUMBER = 1;
    private com.google.protobuf.LazyStringList timerInstanceId_;
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return A list containing the timerInstanceId.
     */
    public com.google.protobuf.ProtocolStringList
        getTimerInstanceIdList() {
      return timerInstanceId_;
    }
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return The count of timerInstanceId.
     */
    public int getTimerInstanceIdCount() {
      return timerInstanceId_.size();
    }
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the element to return.
     * @return The timerInstanceId at the given index.
     */
    public java.lang.String getTimerInstanceId(int index) {
      return timerInstanceId_.get(index);
    }
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the value to return.
     * @return The bytes of the timerInstanceId at the given index.
     */
    public com.google.protobuf.ByteString
        getTimerInstanceIdBytes(int index) {
      return timerInstanceId_.getByteString(index);
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
      for (int i = 0; i < timerInstanceId_.size(); i++) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, timerInstanceId_.getRaw(i));
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
        for (int i = 0; i < timerInstanceId_.size(); i++) {
          dataSize += computeStringSizeNoTag(timerInstanceId_.getRaw(i));
        }
        size += dataSize;
        size += 1 * getTimerInstanceIdList().size();
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
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent other = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent) obj;

      if (!getTimerInstanceIdList()
          .equals(other.getTimerInstanceIdList())) return false;
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
      if (getTimerInstanceIdCount() > 0) {
        hash = (37 * hash) + TIMER_INSTANCE_ID_FIELD_NUMBER;
        hash = (53 * hash) + getTimerInstanceIdList().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent parseFrom(
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
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent prototype) {
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
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.MilestoneNodeInstanceContent}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.MilestoneNodeInstanceContent)
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContentOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_MilestoneNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_MilestoneNodeInstanceContent_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent.newBuilder()
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
        timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_MilestoneNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent build() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent result = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent(this);
        int from_bitField0_ = bitField0_;
        if (((bitField0_ & 0x00000001) != 0)) {
          timerInstanceId_ = timerInstanceId_.getUnmodifiableView();
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.timerInstanceId_ = timerInstanceId_;
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
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent.getDefaultInstance()) return this;
        if (!other.timerInstanceId_.isEmpty()) {
          if (timerInstanceId_.isEmpty()) {
            timerInstanceId_ = other.timerInstanceId_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureTimerInstanceIdIsMutable();
            timerInstanceId_.addAll(other.timerInstanceId_);
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
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private com.google.protobuf.LazyStringList timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      private void ensureTimerInstanceIdIsMutable() {
        if (!((bitField0_ & 0x00000001) != 0)) {
          timerInstanceId_ = new com.google.protobuf.LazyStringArrayList(timerInstanceId_);
          bitField0_ |= 0x00000001;
         }
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @return A list containing the timerInstanceId.
       */
      public com.google.protobuf.ProtocolStringList
          getTimerInstanceIdList() {
        return timerInstanceId_.getUnmodifiableView();
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @return The count of timerInstanceId.
       */
      public int getTimerInstanceIdCount() {
        return timerInstanceId_.size();
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param index The index of the element to return.
       * @return The timerInstanceId at the given index.
       */
      public java.lang.String getTimerInstanceId(int index) {
        return timerInstanceId_.get(index);
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param index The index of the value to return.
       * @return The bytes of the timerInstanceId at the given index.
       */
      public com.google.protobuf.ByteString
          getTimerInstanceIdBytes(int index) {
        return timerInstanceId_.getByteString(index);
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param index The index to set the value at.
       * @param value The timerInstanceId to set.
       * @return This builder for chaining.
       */
      public Builder setTimerInstanceId(
          int index, java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureTimerInstanceIdIsMutable();
        timerInstanceId_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param value The timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addTimerInstanceId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureTimerInstanceIdIsMutable();
        timerInstanceId_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param values The timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addAllTimerInstanceId(
          java.lang.Iterable<java.lang.String> values) {
        ensureTimerInstanceIdIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, timerInstanceId_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearTimerInstanceId() {
        timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param value The bytes of the timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addTimerInstanceIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        ensureTimerInstanceIdIsMutable();
        timerInstanceId_.add(value);
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


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.MilestoneNodeInstanceContent)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.MilestoneNodeInstanceContent)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<MilestoneNodeInstanceContent>
        PARSER = new com.google.protobuf.AbstractParser<MilestoneNodeInstanceContent>() {
      @java.lang.Override
      public MilestoneNodeInstanceContent parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new MilestoneNodeInstanceContent(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<MilestoneNodeInstanceContent> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<MilestoneNodeInstanceContent> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.MilestoneNodeInstanceContent getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface EventNodeInstanceContentOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.EventNodeInstanceContent)
      com.google.protobuf.MessageOrBuilder {
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.EventNodeInstanceContent}
   */
  public static final class EventNodeInstanceContent extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.EventNodeInstanceContent)
      EventNodeInstanceContentOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use EventNodeInstanceContent.newBuilder() to construct.
    private EventNodeInstanceContent(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private EventNodeInstanceContent() {
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new EventNodeInstanceContent();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private EventNodeInstanceContent(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
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
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_EventNodeInstanceContent_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_EventNodeInstanceContent_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent.Builder.class);
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
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      size += unknownFields.getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent other = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent) obj;

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
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent parseFrom(
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
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent prototype) {
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
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.EventNodeInstanceContent}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.EventNodeInstanceContent)
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContentOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_EventNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_EventNodeInstanceContent_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent.newBuilder()
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
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_EventNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent build() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent result = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent(this);
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
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent.getDefaultInstance()) return this;
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
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
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


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.EventNodeInstanceContent)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.EventNodeInstanceContent)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<EventNodeInstanceContent>
        PARSER = new com.google.protobuf.AbstractParser<EventNodeInstanceContent>() {
      @java.lang.Override
      public EventNodeInstanceContent parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new EventNodeInstanceContent(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<EventNodeInstanceContent> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<EventNodeInstanceContent> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventNodeInstanceContent getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface TimerNodeInstanceContentOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.TimerNodeInstanceContent)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>optional string timer_id = 1;</code>
     * @return Whether the timerId field is set.
     */
    boolean hasTimerId();
    /**
     * <code>optional string timer_id = 1;</code>
     * @return The timerId.
     */
    java.lang.String getTimerId();
    /**
     * <code>optional string timer_id = 1;</code>
     * @return The bytes for timerId.
     */
    com.google.protobuf.ByteString
        getTimerIdBytes();
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.TimerNodeInstanceContent}
   */
  public static final class TimerNodeInstanceContent extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.TimerNodeInstanceContent)
      TimerNodeInstanceContentOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use TimerNodeInstanceContent.newBuilder() to construct.
    private TimerNodeInstanceContent(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private TimerNodeInstanceContent() {
      timerId_ = "";
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new TimerNodeInstanceContent();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private TimerNodeInstanceContent(
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
              timerId_ = s;
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
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_TimerNodeInstanceContent_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_TimerNodeInstanceContent_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent.Builder.class);
    }

    private int bitField0_;
    public static final int TIMER_ID_FIELD_NUMBER = 1;
    private volatile java.lang.Object timerId_;
    /**
     * <code>optional string timer_id = 1;</code>
     * @return Whether the timerId field is set.
     */
    @java.lang.Override
    public boolean hasTimerId() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>optional string timer_id = 1;</code>
     * @return The timerId.
     */
    @java.lang.Override
    public java.lang.String getTimerId() {
      java.lang.Object ref = timerId_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        timerId_ = s;
        return s;
      }
    }
    /**
     * <code>optional string timer_id = 1;</code>
     * @return The bytes for timerId.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getTimerIdBytes() {
      java.lang.Object ref = timerId_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        timerId_ = b;
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
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, timerId_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, timerId_);
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
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent other = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent) obj;

      if (hasTimerId() != other.hasTimerId()) return false;
      if (hasTimerId()) {
        if (!getTimerId()
            .equals(other.getTimerId())) return false;
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
      if (hasTimerId()) {
        hash = (37 * hash) + TIMER_ID_FIELD_NUMBER;
        hash = (53 * hash) + getTimerId().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent parseFrom(
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
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent prototype) {
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
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.TimerNodeInstanceContent}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.TimerNodeInstanceContent)
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContentOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_TimerNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_TimerNodeInstanceContent_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent.newBuilder()
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
        timerId_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_TimerNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent build() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent result = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          to_bitField0_ |= 0x00000001;
        }
        result.timerId_ = timerId_;
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
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent.getDefaultInstance()) return this;
        if (other.hasTimerId()) {
          bitField0_ |= 0x00000001;
          timerId_ = other.timerId_;
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
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.lang.Object timerId_ = "";
      /**
       * <code>optional string timer_id = 1;</code>
       * @return Whether the timerId field is set.
       */
      public boolean hasTimerId() {
        return ((bitField0_ & 0x00000001) != 0);
      }
      /**
       * <code>optional string timer_id = 1;</code>
       * @return The timerId.
       */
      public java.lang.String getTimerId() {
        java.lang.Object ref = timerId_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          timerId_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>optional string timer_id = 1;</code>
       * @return The bytes for timerId.
       */
      public com.google.protobuf.ByteString
          getTimerIdBytes() {
        java.lang.Object ref = timerId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          timerId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string timer_id = 1;</code>
       * @param value The timerId to set.
       * @return This builder for chaining.
       */
      public Builder setTimerId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        timerId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string timer_id = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearTimerId() {
        bitField0_ = (bitField0_ & ~0x00000001);
        timerId_ = getDefaultInstance().getTimerId();
        onChanged();
        return this;
      }
      /**
       * <code>optional string timer_id = 1;</code>
       * @param value The bytes for timerId to set.
       * @return This builder for chaining.
       */
      public Builder setTimerIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000001;
        timerId_ = value;
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


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.TimerNodeInstanceContent)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.TimerNodeInstanceContent)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<TimerNodeInstanceContent>
        PARSER = new com.google.protobuf.AbstractParser<TimerNodeInstanceContent>() {
      @java.lang.Override
      public TimerNodeInstanceContent parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new TimerNodeInstanceContent(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<TimerNodeInstanceContent> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<TimerNodeInstanceContent> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.TimerNodeInstanceContent getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface JoinNodeInstanceContentOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
     */
    java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger> 
        getTriggerList();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger getTrigger(int index);
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
     */
    int getTriggerCount();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
     */
    java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTriggerOrBuilder> 
        getTriggerOrBuilderList();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTriggerOrBuilder getTriggerOrBuilder(
        int index);
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent}
   */
  public static final class JoinNodeInstanceContent extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent)
      JoinNodeInstanceContentOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use JoinNodeInstanceContent.newBuilder() to construct.
    private JoinNodeInstanceContent(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private JoinNodeInstanceContent() {
      trigger_ = java.util.Collections.emptyList();
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new JoinNodeInstanceContent();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private JoinNodeInstanceContent(
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
                trigger_ = new java.util.ArrayList<org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger>();
                mutable_bitField0_ |= 0x00000001;
              }
              trigger_.add(
                  input.readMessage(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger.parser(), extensionRegistry));
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
          trigger_ = java.util.Collections.unmodifiableList(trigger_);
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_JoinNodeInstanceContent_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_JoinNodeInstanceContent_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.Builder.class);
    }

    public interface JoinTriggerOrBuilder extends
        // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger)
        com.google.protobuf.MessageOrBuilder {

      /**
       * <code>optional int64 node_id = 1;</code>
       * @return Whether the nodeId field is set.
       */
      boolean hasNodeId();
      /**
       * <code>optional int64 node_id = 1;</code>
       * @return The nodeId.
       */
      long getNodeId();

      /**
       * <code>optional int32 counter = 2;</code>
       * @return Whether the counter field is set.
       */
      boolean hasCounter();
      /**
       * <code>optional int32 counter = 2;</code>
       * @return The counter.
       */
      int getCounter();
    }
    /**
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger}
     */
    public static final class JoinTrigger extends
        com.google.protobuf.GeneratedMessageV3 implements
        // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger)
        JoinTriggerOrBuilder {
    private static final long serialVersionUID = 0L;
      // Use JoinTrigger.newBuilder() to construct.
      private JoinTrigger(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
        super(builder);
      }
      private JoinTrigger() {
      }

      @java.lang.Override
      @SuppressWarnings({"unused"})
      protected java.lang.Object newInstance(
          UnusedPrivateParameter unused) {
        return new JoinTrigger();
      }

      @java.lang.Override
      public final com.google.protobuf.UnknownFieldSet
      getUnknownFields() {
        return this.unknownFields;
      }
      private JoinTrigger(
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
              case 8: {
                bitField0_ |= 0x00000001;
                nodeId_ = input.readInt64();
                break;
              }
              case 16: {
                bitField0_ |= 0x00000002;
                counter_ = input.readInt32();
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
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_JoinNodeInstanceContent_JoinTrigger_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_JoinNodeInstanceContent_JoinTrigger_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger.Builder.class);
      }

      private int bitField0_;
      public static final int NODE_ID_FIELD_NUMBER = 1;
      private long nodeId_;
      /**
       * <code>optional int64 node_id = 1;</code>
       * @return Whether the nodeId field is set.
       */
      @java.lang.Override
      public boolean hasNodeId() {
        return ((bitField0_ & 0x00000001) != 0);
      }
      /**
       * <code>optional int64 node_id = 1;</code>
       * @return The nodeId.
       */
      @java.lang.Override
      public long getNodeId() {
        return nodeId_;
      }

      public static final int COUNTER_FIELD_NUMBER = 2;
      private int counter_;
      /**
       * <code>optional int32 counter = 2;</code>
       * @return Whether the counter field is set.
       */
      @java.lang.Override
      public boolean hasCounter() {
        return ((bitField0_ & 0x00000002) != 0);
      }
      /**
       * <code>optional int32 counter = 2;</code>
       * @return The counter.
       */
      @java.lang.Override
      public int getCounter() {
        return counter_;
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
          output.writeInt64(1, nodeId_);
        }
        if (((bitField0_ & 0x00000002) != 0)) {
          output.writeInt32(2, counter_);
        }
        unknownFields.writeTo(output);
      }

      @java.lang.Override
      public int getSerializedSize() {
        int size = memoizedSize;
        if (size != -1) return size;

        size = 0;
        if (((bitField0_ & 0x00000001) != 0)) {
          size += com.google.protobuf.CodedOutputStream
            .computeInt64Size(1, nodeId_);
        }
        if (((bitField0_ & 0x00000002) != 0)) {
          size += com.google.protobuf.CodedOutputStream
            .computeInt32Size(2, counter_);
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
        if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger)) {
          return super.equals(obj);
        }
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger other = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger) obj;

        if (hasNodeId() != other.hasNodeId()) return false;
        if (hasNodeId()) {
          if (getNodeId()
              != other.getNodeId()) return false;
        }
        if (hasCounter() != other.hasCounter()) return false;
        if (hasCounter()) {
          if (getCounter()
              != other.getCounter()) return false;
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
        if (hasNodeId()) {
          hash = (37 * hash) + NODE_ID_FIELD_NUMBER;
          hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
              getNodeId());
        }
        if (hasCounter()) {
          hash = (37 * hash) + COUNTER_FIELD_NUMBER;
          hash = (53 * hash) + getCounter();
        }
        hash = (29 * hash) + unknownFields.hashCode();
        memoizedHashCode = hash;
        return hash;
      }

      public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger parseFrom(
          java.nio.ByteBuffer data)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
      }
      public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger parseFrom(
          java.nio.ByteBuffer data,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
      }
      public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger parseFrom(
          com.google.protobuf.ByteString data)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
      }
      public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger parseFrom(
          com.google.protobuf.ByteString data,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
      }
      public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger parseFrom(byte[] data)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
      }
      public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger parseFrom(
          byte[] data,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
      }
      public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger parseFrom(java.io.InputStream input)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input);
      }
      public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger parseFrom(
          java.io.InputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input, extensionRegistry);
      }
      public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger parseDelimitedFrom(java.io.InputStream input)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseDelimitedWithIOException(PARSER, input);
      }
      public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger parseDelimitedFrom(
          java.io.InputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
      }
      public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger parseFrom(
          com.google.protobuf.CodedInputStream input)
          throws java.io.IOException {
        return com.google.protobuf.GeneratedMessageV3
            .parseWithIOException(PARSER, input);
      }
      public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger parseFrom(
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
      public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger prototype) {
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
       * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger}
       */
      public static final class Builder extends
          com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
          // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger)
          org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTriggerOrBuilder {
        public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
          return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_JoinNodeInstanceContent_JoinTrigger_descriptor;
        }

        @java.lang.Override
        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
          return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_JoinNodeInstanceContent_JoinTrigger_fieldAccessorTable
              .ensureFieldAccessorsInitialized(
                  org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger.Builder.class);
        }

        // Construct using org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger.newBuilder()
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
          nodeId_ = 0L;
          bitField0_ = (bitField0_ & ~0x00000001);
          counter_ = 0;
          bitField0_ = (bitField0_ & ~0x00000002);
          return this;
        }

        @java.lang.Override
        public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
          return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_JoinNodeInstanceContent_JoinTrigger_descriptor;
        }

        @java.lang.Override
        public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger getDefaultInstanceForType() {
          return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger.getDefaultInstance();
        }

        @java.lang.Override
        public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger build() {
          org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger result = buildPartial();
          if (!result.isInitialized()) {
            throw newUninitializedMessageException(result);
          }
          return result;
        }

        @java.lang.Override
        public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger buildPartial() {
          org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger result = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger(this);
          int from_bitField0_ = bitField0_;
          int to_bitField0_ = 0;
          if (((from_bitField0_ & 0x00000001) != 0)) {
            result.nodeId_ = nodeId_;
            to_bitField0_ |= 0x00000001;
          }
          if (((from_bitField0_ & 0x00000002) != 0)) {
            result.counter_ = counter_;
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
          if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger) {
            return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger)other);
          } else {
            super.mergeFrom(other);
            return this;
          }
        }

        public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger other) {
          if (other == org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger.getDefaultInstance()) return this;
          if (other.hasNodeId()) {
            setNodeId(other.getNodeId());
          }
          if (other.hasCounter()) {
            setCounter(other.getCounter());
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
          org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger parsedMessage = null;
          try {
            parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
          } catch (com.google.protobuf.InvalidProtocolBufferException e) {
            parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger) e.getUnfinishedMessage();
            throw e.unwrapIOException();
          } finally {
            if (parsedMessage != null) {
              mergeFrom(parsedMessage);
            }
          }
          return this;
        }
        private int bitField0_;

        private long nodeId_ ;
        /**
         * <code>optional int64 node_id = 1;</code>
         * @return Whether the nodeId field is set.
         */
        @java.lang.Override
        public boolean hasNodeId() {
          return ((bitField0_ & 0x00000001) != 0);
        }
        /**
         * <code>optional int64 node_id = 1;</code>
         * @return The nodeId.
         */
        @java.lang.Override
        public long getNodeId() {
          return nodeId_;
        }
        /**
         * <code>optional int64 node_id = 1;</code>
         * @param value The nodeId to set.
         * @return This builder for chaining.
         */
        public Builder setNodeId(long value) {
          bitField0_ |= 0x00000001;
          nodeId_ = value;
          onChanged();
          return this;
        }
        /**
         * <code>optional int64 node_id = 1;</code>
         * @return This builder for chaining.
         */
        public Builder clearNodeId() {
          bitField0_ = (bitField0_ & ~0x00000001);
          nodeId_ = 0L;
          onChanged();
          return this;
        }

        private int counter_ ;
        /**
         * <code>optional int32 counter = 2;</code>
         * @return Whether the counter field is set.
         */
        @java.lang.Override
        public boolean hasCounter() {
          return ((bitField0_ & 0x00000002) != 0);
        }
        /**
         * <code>optional int32 counter = 2;</code>
         * @return The counter.
         */
        @java.lang.Override
        public int getCounter() {
          return counter_;
        }
        /**
         * <code>optional int32 counter = 2;</code>
         * @param value The counter to set.
         * @return This builder for chaining.
         */
        public Builder setCounter(int value) {
          bitField0_ |= 0x00000002;
          counter_ = value;
          onChanged();
          return this;
        }
        /**
         * <code>optional int32 counter = 2;</code>
         * @return This builder for chaining.
         */
        public Builder clearCounter() {
          bitField0_ = (bitField0_ & ~0x00000002);
          counter_ = 0;
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


        // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger)
      }

      // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger)
      private static final org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger DEFAULT_INSTANCE;
      static {
        DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger();
      }

      public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger getDefaultInstance() {
        return DEFAULT_INSTANCE;
      }

      private static final com.google.protobuf.Parser<JoinTrigger>
          PARSER = new com.google.protobuf.AbstractParser<JoinTrigger>() {
        @java.lang.Override
        public JoinTrigger parsePartialFrom(
            com.google.protobuf.CodedInputStream input,
            com.google.protobuf.ExtensionRegistryLite extensionRegistry)
            throws com.google.protobuf.InvalidProtocolBufferException {
          return new JoinTrigger(input, extensionRegistry);
        }
      };

      public static com.google.protobuf.Parser<JoinTrigger> parser() {
        return PARSER;
      }

      @java.lang.Override
      public com.google.protobuf.Parser<JoinTrigger> getParserForType() {
        return PARSER;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
      }

    }

    public static final int TRIGGER_FIELD_NUMBER = 1;
    private java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger> trigger_;
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
     */
    @java.lang.Override
    public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger> getTriggerList() {
      return trigger_;
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
     */
    @java.lang.Override
    public java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTriggerOrBuilder> 
        getTriggerOrBuilderList() {
      return trigger_;
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
     */
    @java.lang.Override
    public int getTriggerCount() {
      return trigger_.size();
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger getTrigger(int index) {
      return trigger_.get(index);
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTriggerOrBuilder getTriggerOrBuilder(
        int index) {
      return trigger_.get(index);
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
      for (int i = 0; i < trigger_.size(); i++) {
        output.writeMessage(1, trigger_.get(i));
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      for (int i = 0; i < trigger_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(1, trigger_.get(i));
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
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent other = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent) obj;

      if (!getTriggerList()
          .equals(other.getTriggerList())) return false;
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
      if (getTriggerCount() > 0) {
        hash = (37 * hash) + TRIGGER_FIELD_NUMBER;
        hash = (53 * hash) + getTriggerList().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent parseFrom(
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
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent prototype) {
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
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent)
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContentOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_JoinNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_JoinNodeInstanceContent_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.newBuilder()
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
          getTriggerFieldBuilder();
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        if (triggerBuilder_ == null) {
          trigger_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000001);
        } else {
          triggerBuilder_.clear();
        }
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_JoinNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent build() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent result = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent(this);
        int from_bitField0_ = bitField0_;
        if (triggerBuilder_ == null) {
          if (((bitField0_ & 0x00000001) != 0)) {
            trigger_ = java.util.Collections.unmodifiableList(trigger_);
            bitField0_ = (bitField0_ & ~0x00000001);
          }
          result.trigger_ = trigger_;
        } else {
          result.trigger_ = triggerBuilder_.build();
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
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.getDefaultInstance()) return this;
        if (triggerBuilder_ == null) {
          if (!other.trigger_.isEmpty()) {
            if (trigger_.isEmpty()) {
              trigger_ = other.trigger_;
              bitField0_ = (bitField0_ & ~0x00000001);
            } else {
              ensureTriggerIsMutable();
              trigger_.addAll(other.trigger_);
            }
            onChanged();
          }
        } else {
          if (!other.trigger_.isEmpty()) {
            if (triggerBuilder_.isEmpty()) {
              triggerBuilder_.dispose();
              triggerBuilder_ = null;
              trigger_ = other.trigger_;
              bitField0_ = (bitField0_ & ~0x00000001);
              triggerBuilder_ = 
                com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                   getTriggerFieldBuilder() : null;
            } else {
              triggerBuilder_.addAllMessages(other.trigger_);
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
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger> trigger_ =
        java.util.Collections.emptyList();
      private void ensureTriggerIsMutable() {
        if (!((bitField0_ & 0x00000001) != 0)) {
          trigger_ = new java.util.ArrayList<org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger>(trigger_);
          bitField0_ |= 0x00000001;
         }
      }

      private com.google.protobuf.RepeatedFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger.Builder, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTriggerOrBuilder> triggerBuilder_;

      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
       */
      public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger> getTriggerList() {
        if (triggerBuilder_ == null) {
          return java.util.Collections.unmodifiableList(trigger_);
        } else {
          return triggerBuilder_.getMessageList();
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
       */
      public int getTriggerCount() {
        if (triggerBuilder_ == null) {
          return trigger_.size();
        } else {
          return triggerBuilder_.getCount();
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger getTrigger(int index) {
        if (triggerBuilder_ == null) {
          return trigger_.get(index);
        } else {
          return triggerBuilder_.getMessage(index);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
       */
      public Builder setTrigger(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger value) {
        if (triggerBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureTriggerIsMutable();
          trigger_.set(index, value);
          onChanged();
        } else {
          triggerBuilder_.setMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
       */
      public Builder setTrigger(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger.Builder builderForValue) {
        if (triggerBuilder_ == null) {
          ensureTriggerIsMutable();
          trigger_.set(index, builderForValue.build());
          onChanged();
        } else {
          triggerBuilder_.setMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
       */
      public Builder addTrigger(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger value) {
        if (triggerBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureTriggerIsMutable();
          trigger_.add(value);
          onChanged();
        } else {
          triggerBuilder_.addMessage(value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
       */
      public Builder addTrigger(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger value) {
        if (triggerBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureTriggerIsMutable();
          trigger_.add(index, value);
          onChanged();
        } else {
          triggerBuilder_.addMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
       */
      public Builder addTrigger(
          org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger.Builder builderForValue) {
        if (triggerBuilder_ == null) {
          ensureTriggerIsMutable();
          trigger_.add(builderForValue.build());
          onChanged();
        } else {
          triggerBuilder_.addMessage(builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
       */
      public Builder addTrigger(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger.Builder builderForValue) {
        if (triggerBuilder_ == null) {
          ensureTriggerIsMutable();
          trigger_.add(index, builderForValue.build());
          onChanged();
        } else {
          triggerBuilder_.addMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
       */
      public Builder addAllTrigger(
          java.lang.Iterable<? extends org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger> values) {
        if (triggerBuilder_ == null) {
          ensureTriggerIsMutable();
          com.google.protobuf.AbstractMessageLite.Builder.addAll(
              values, trigger_);
          onChanged();
        } else {
          triggerBuilder_.addAllMessages(values);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
       */
      public Builder clearTrigger() {
        if (triggerBuilder_ == null) {
          trigger_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000001);
          onChanged();
        } else {
          triggerBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
       */
      public Builder removeTrigger(int index) {
        if (triggerBuilder_ == null) {
          ensureTriggerIsMutable();
          trigger_.remove(index);
          onChanged();
        } else {
          triggerBuilder_.remove(index);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger.Builder getTriggerBuilder(
          int index) {
        return getTriggerFieldBuilder().getBuilder(index);
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTriggerOrBuilder getTriggerOrBuilder(
          int index) {
        if (triggerBuilder_ == null) {
          return trigger_.get(index);  } else {
          return triggerBuilder_.getMessageOrBuilder(index);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
       */
      public java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTriggerOrBuilder> 
           getTriggerOrBuilderList() {
        if (triggerBuilder_ != null) {
          return triggerBuilder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(trigger_);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger.Builder addTriggerBuilder() {
        return getTriggerFieldBuilder().addBuilder(
            org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger.getDefaultInstance());
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger.Builder addTriggerBuilder(
          int index) {
        return getTriggerFieldBuilder().addBuilder(
            index, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger.getDefaultInstance());
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent.JoinTrigger trigger = 1;</code>
       */
      public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger.Builder> 
           getTriggerBuilderList() {
        return getTriggerFieldBuilder().getBuilderList();
      }
      private com.google.protobuf.RepeatedFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger.Builder, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTriggerOrBuilder> 
          getTriggerFieldBuilder() {
        if (triggerBuilder_ == null) {
          triggerBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
              org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTrigger.Builder, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent.JoinTriggerOrBuilder>(
                  trigger_,
                  ((bitField0_ & 0x00000001) != 0),
                  getParentForChildren(),
                  isClean());
          trigger_ = null;
        }
        return triggerBuilder_;
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


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.JoinNodeInstanceContent)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<JoinNodeInstanceContent>
        PARSER = new com.google.protobuf.AbstractParser<JoinNodeInstanceContent>() {
      @java.lang.Override
      public JoinNodeInstanceContent parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new JoinNodeInstanceContent(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<JoinNodeInstanceContent> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<JoinNodeInstanceContent> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.JoinNodeInstanceContent getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface StateNodeInstanceContentOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.StateNodeInstanceContent)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return A list containing the timerInstanceId.
     */
    java.util.List<java.lang.String>
        getTimerInstanceIdList();
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return The count of timerInstanceId.
     */
    int getTimerInstanceIdCount();
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the element to return.
     * @return The timerInstanceId at the given index.
     */
    java.lang.String getTimerInstanceId(int index);
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the value to return.
     * @return The bytes of the timerInstanceId at the given index.
     */
    com.google.protobuf.ByteString
        getTimerInstanceIdBytes(int index);
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.StateNodeInstanceContent}
   */
  public static final class StateNodeInstanceContent extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.StateNodeInstanceContent)
      StateNodeInstanceContentOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use StateNodeInstanceContent.newBuilder() to construct.
    private StateNodeInstanceContent(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private StateNodeInstanceContent() {
      timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new StateNodeInstanceContent();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private StateNodeInstanceContent(
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
                timerInstanceId_ = new com.google.protobuf.LazyStringArrayList();
                mutable_bitField0_ |= 0x00000001;
              }
              timerInstanceId_.add(s);
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
          timerInstanceId_ = timerInstanceId_.getUnmodifiableView();
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_StateNodeInstanceContent_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_StateNodeInstanceContent_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent.Builder.class);
    }

    public static final int TIMER_INSTANCE_ID_FIELD_NUMBER = 1;
    private com.google.protobuf.LazyStringList timerInstanceId_;
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return A list containing the timerInstanceId.
     */
    public com.google.protobuf.ProtocolStringList
        getTimerInstanceIdList() {
      return timerInstanceId_;
    }
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return The count of timerInstanceId.
     */
    public int getTimerInstanceIdCount() {
      return timerInstanceId_.size();
    }
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the element to return.
     * @return The timerInstanceId at the given index.
     */
    public java.lang.String getTimerInstanceId(int index) {
      return timerInstanceId_.get(index);
    }
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the value to return.
     * @return The bytes of the timerInstanceId at the given index.
     */
    public com.google.protobuf.ByteString
        getTimerInstanceIdBytes(int index) {
      return timerInstanceId_.getByteString(index);
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
      for (int i = 0; i < timerInstanceId_.size(); i++) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, timerInstanceId_.getRaw(i));
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
        for (int i = 0; i < timerInstanceId_.size(); i++) {
          dataSize += computeStringSizeNoTag(timerInstanceId_.getRaw(i));
        }
        size += dataSize;
        size += 1 * getTimerInstanceIdList().size();
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
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent other = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent) obj;

      if (!getTimerInstanceIdList()
          .equals(other.getTimerInstanceIdList())) return false;
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
      if (getTimerInstanceIdCount() > 0) {
        hash = (37 * hash) + TIMER_INSTANCE_ID_FIELD_NUMBER;
        hash = (53 * hash) + getTimerInstanceIdList().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent parseFrom(
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
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent prototype) {
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
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.StateNodeInstanceContent}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.StateNodeInstanceContent)
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContentOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_StateNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_StateNodeInstanceContent_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent.newBuilder()
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
        timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_StateNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent build() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent result = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent(this);
        int from_bitField0_ = bitField0_;
        if (((bitField0_ & 0x00000001) != 0)) {
          timerInstanceId_ = timerInstanceId_.getUnmodifiableView();
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.timerInstanceId_ = timerInstanceId_;
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
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent.getDefaultInstance()) return this;
        if (!other.timerInstanceId_.isEmpty()) {
          if (timerInstanceId_.isEmpty()) {
            timerInstanceId_ = other.timerInstanceId_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureTimerInstanceIdIsMutable();
            timerInstanceId_.addAll(other.timerInstanceId_);
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
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private com.google.protobuf.LazyStringList timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      private void ensureTimerInstanceIdIsMutable() {
        if (!((bitField0_ & 0x00000001) != 0)) {
          timerInstanceId_ = new com.google.protobuf.LazyStringArrayList(timerInstanceId_);
          bitField0_ |= 0x00000001;
         }
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @return A list containing the timerInstanceId.
       */
      public com.google.protobuf.ProtocolStringList
          getTimerInstanceIdList() {
        return timerInstanceId_.getUnmodifiableView();
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @return The count of timerInstanceId.
       */
      public int getTimerInstanceIdCount() {
        return timerInstanceId_.size();
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param index The index of the element to return.
       * @return The timerInstanceId at the given index.
       */
      public java.lang.String getTimerInstanceId(int index) {
        return timerInstanceId_.get(index);
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param index The index of the value to return.
       * @return The bytes of the timerInstanceId at the given index.
       */
      public com.google.protobuf.ByteString
          getTimerInstanceIdBytes(int index) {
        return timerInstanceId_.getByteString(index);
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param index The index to set the value at.
       * @param value The timerInstanceId to set.
       * @return This builder for chaining.
       */
      public Builder setTimerInstanceId(
          int index, java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureTimerInstanceIdIsMutable();
        timerInstanceId_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param value The timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addTimerInstanceId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureTimerInstanceIdIsMutable();
        timerInstanceId_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param values The timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addAllTimerInstanceId(
          java.lang.Iterable<java.lang.String> values) {
        ensureTimerInstanceIdIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, timerInstanceId_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearTimerInstanceId() {
        timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param value The bytes of the timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addTimerInstanceIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        ensureTimerInstanceIdIsMutable();
        timerInstanceId_.add(value);
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


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.StateNodeInstanceContent)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.StateNodeInstanceContent)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<StateNodeInstanceContent>
        PARSER = new com.google.protobuf.AbstractParser<StateNodeInstanceContent>() {
      @java.lang.Override
      public StateNodeInstanceContent parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new StateNodeInstanceContent(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<StateNodeInstanceContent> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<StateNodeInstanceContent> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.StateNodeInstanceContent getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface CompositeContextNodeInstanceContentOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.CompositeContextNodeInstanceContent)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return A list containing the timerInstanceId.
     */
    java.util.List<java.lang.String>
        getTimerInstanceIdList();
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return The count of timerInstanceId.
     */
    int getTimerInstanceIdCount();
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the element to return.
     * @return The timerInstanceId at the given index.
     */
    java.lang.String getTimerInstanceId(int index);
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the value to return.
     * @return The bytes of the timerInstanceId at the given index.
     */
    com.google.protobuf.ByteString
        getTimerInstanceIdBytes(int index);

    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
     * @return Whether the context field is set.
     */
    boolean hasContext();
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
     * @return The context.
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext getContext();
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder getContextOrBuilder();
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.CompositeContextNodeInstanceContent}
   */
  public static final class CompositeContextNodeInstanceContent extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.CompositeContextNodeInstanceContent)
      CompositeContextNodeInstanceContentOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use CompositeContextNodeInstanceContent.newBuilder() to construct.
    private CompositeContextNodeInstanceContent(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private CompositeContextNodeInstanceContent() {
      timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new CompositeContextNodeInstanceContent();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private CompositeContextNodeInstanceContent(
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
                timerInstanceId_ = new com.google.protobuf.LazyStringArrayList();
                mutable_bitField0_ |= 0x00000001;
              }
              timerInstanceId_.add(s);
              break;
            }
            case 18: {
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder subBuilder = null;
              if (context_ != null) {
                subBuilder = context_.toBuilder();
              }
              context_ = input.readMessage(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(context_);
                context_ = subBuilder.buildPartial();
              }

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
          timerInstanceId_ = timerInstanceId_.getUnmodifiableView();
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_CompositeContextNodeInstanceContent_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_CompositeContextNodeInstanceContent_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent.Builder.class);
    }

    public static final int TIMER_INSTANCE_ID_FIELD_NUMBER = 1;
    private com.google.protobuf.LazyStringList timerInstanceId_;
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return A list containing the timerInstanceId.
     */
    public com.google.protobuf.ProtocolStringList
        getTimerInstanceIdList() {
      return timerInstanceId_;
    }
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return The count of timerInstanceId.
     */
    public int getTimerInstanceIdCount() {
      return timerInstanceId_.size();
    }
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the element to return.
     * @return The timerInstanceId at the given index.
     */
    public java.lang.String getTimerInstanceId(int index) {
      return timerInstanceId_.get(index);
    }
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the value to return.
     * @return The bytes of the timerInstanceId at the given index.
     */
    public com.google.protobuf.ByteString
        getTimerInstanceIdBytes(int index) {
      return timerInstanceId_.getByteString(index);
    }

    public static final int CONTEXT_FIELD_NUMBER = 2;
    private org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext context_;
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
     * @return Whether the context field is set.
     */
    @java.lang.Override
    public boolean hasContext() {
      return context_ != null;
    }
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
     * @return The context.
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext getContext() {
      return context_ == null ? org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.getDefaultInstance() : context_;
    }
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder getContextOrBuilder() {
      return getContext();
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
      for (int i = 0; i < timerInstanceId_.size(); i++) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, timerInstanceId_.getRaw(i));
      }
      if (context_ != null) {
        output.writeMessage(2, getContext());
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
        for (int i = 0; i < timerInstanceId_.size(); i++) {
          dataSize += computeStringSizeNoTag(timerInstanceId_.getRaw(i));
        }
        size += dataSize;
        size += 1 * getTimerInstanceIdList().size();
      }
      if (context_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, getContext());
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
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent other = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent) obj;

      if (!getTimerInstanceIdList()
          .equals(other.getTimerInstanceIdList())) return false;
      if (hasContext() != other.hasContext()) return false;
      if (hasContext()) {
        if (!getContext()
            .equals(other.getContext())) return false;
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
      if (getTimerInstanceIdCount() > 0) {
        hash = (37 * hash) + TIMER_INSTANCE_ID_FIELD_NUMBER;
        hash = (53 * hash) + getTimerInstanceIdList().hashCode();
      }
      if (hasContext()) {
        hash = (37 * hash) + CONTEXT_FIELD_NUMBER;
        hash = (53 * hash) + getContext().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent parseFrom(
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
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent prototype) {
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
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.CompositeContextNodeInstanceContent}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.CompositeContextNodeInstanceContent)
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContentOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_CompositeContextNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_CompositeContextNodeInstanceContent_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent.newBuilder()
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
        timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        if (contextBuilder_ == null) {
          context_ = null;
        } else {
          context_ = null;
          contextBuilder_ = null;
        }
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_CompositeContextNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent build() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent result = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent(this);
        int from_bitField0_ = bitField0_;
        if (((bitField0_ & 0x00000001) != 0)) {
          timerInstanceId_ = timerInstanceId_.getUnmodifiableView();
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.timerInstanceId_ = timerInstanceId_;
        if (contextBuilder_ == null) {
          result.context_ = context_;
        } else {
          result.context_ = contextBuilder_.build();
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
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent.getDefaultInstance()) return this;
        if (!other.timerInstanceId_.isEmpty()) {
          if (timerInstanceId_.isEmpty()) {
            timerInstanceId_ = other.timerInstanceId_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureTimerInstanceIdIsMutable();
            timerInstanceId_.addAll(other.timerInstanceId_);
          }
          onChanged();
        }
        if (other.hasContext()) {
          mergeContext(other.getContext());
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
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private com.google.protobuf.LazyStringList timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      private void ensureTimerInstanceIdIsMutable() {
        if (!((bitField0_ & 0x00000001) != 0)) {
          timerInstanceId_ = new com.google.protobuf.LazyStringArrayList(timerInstanceId_);
          bitField0_ |= 0x00000001;
         }
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @return A list containing the timerInstanceId.
       */
      public com.google.protobuf.ProtocolStringList
          getTimerInstanceIdList() {
        return timerInstanceId_.getUnmodifiableView();
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @return The count of timerInstanceId.
       */
      public int getTimerInstanceIdCount() {
        return timerInstanceId_.size();
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param index The index of the element to return.
       * @return The timerInstanceId at the given index.
       */
      public java.lang.String getTimerInstanceId(int index) {
        return timerInstanceId_.get(index);
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param index The index of the value to return.
       * @return The bytes of the timerInstanceId at the given index.
       */
      public com.google.protobuf.ByteString
          getTimerInstanceIdBytes(int index) {
        return timerInstanceId_.getByteString(index);
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param index The index to set the value at.
       * @param value The timerInstanceId to set.
       * @return This builder for chaining.
       */
      public Builder setTimerInstanceId(
          int index, java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureTimerInstanceIdIsMutable();
        timerInstanceId_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param value The timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addTimerInstanceId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureTimerInstanceIdIsMutable();
        timerInstanceId_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param values The timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addAllTimerInstanceId(
          java.lang.Iterable<java.lang.String> values) {
        ensureTimerInstanceIdIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, timerInstanceId_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearTimerInstanceId() {
        timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param value The bytes of the timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addTimerInstanceIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        ensureTimerInstanceIdIsMutable();
        timerInstanceId_.add(value);
        onChanged();
        return this;
      }

      private org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext context_;
      private com.google.protobuf.SingleFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder> contextBuilder_;
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       * @return Whether the context field is set.
       */
      public boolean hasContext() {
        return contextBuilder_ != null || context_ != null;
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       * @return The context.
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext getContext() {
        if (contextBuilder_ == null) {
          return context_ == null ? org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.getDefaultInstance() : context_;
        } else {
          return contextBuilder_.getMessage();
        }
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      public Builder setContext(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext value) {
        if (contextBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          context_ = value;
          onChanged();
        } else {
          contextBuilder_.setMessage(value);
        }

        return this;
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      public Builder setContext(
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder builderForValue) {
        if (contextBuilder_ == null) {
          context_ = builderForValue.build();
          onChanged();
        } else {
          contextBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      public Builder mergeContext(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext value) {
        if (contextBuilder_ == null) {
          if (context_ != null) {
            context_ =
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.newBuilder(context_).mergeFrom(value).buildPartial();
          } else {
            context_ = value;
          }
          onChanged();
        } else {
          contextBuilder_.mergeFrom(value);
        }

        return this;
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      public Builder clearContext() {
        if (contextBuilder_ == null) {
          context_ = null;
          onChanged();
        } else {
          context_ = null;
          contextBuilder_ = null;
        }

        return this;
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder getContextBuilder() {
        
        onChanged();
        return getContextFieldBuilder().getBuilder();
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder getContextOrBuilder() {
        if (contextBuilder_ != null) {
          return contextBuilder_.getMessageOrBuilder();
        } else {
          return context_ == null ?
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.getDefaultInstance() : context_;
        }
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder> 
          getContextFieldBuilder() {
        if (contextBuilder_ == null) {
          contextBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder>(
                  getContext(),
                  getParentForChildren(),
                  isClean());
          context_ = null;
        }
        return contextBuilder_;
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


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.CompositeContextNodeInstanceContent)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.CompositeContextNodeInstanceContent)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<CompositeContextNodeInstanceContent>
        PARSER = new com.google.protobuf.AbstractParser<CompositeContextNodeInstanceContent>() {
      @java.lang.Override
      public CompositeContextNodeInstanceContent parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new CompositeContextNodeInstanceContent(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<CompositeContextNodeInstanceContent> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<CompositeContextNodeInstanceContent> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.CompositeContextNodeInstanceContent getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface DynamicNodeInstanceContentOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.DynamicNodeInstanceContent)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return A list containing the timerInstanceId.
     */
    java.util.List<java.lang.String>
        getTimerInstanceIdList();
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return The count of timerInstanceId.
     */
    int getTimerInstanceIdCount();
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the element to return.
     * @return The timerInstanceId at the given index.
     */
    java.lang.String getTimerInstanceId(int index);
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the value to return.
     * @return The bytes of the timerInstanceId at the given index.
     */
    com.google.protobuf.ByteString
        getTimerInstanceIdBytes(int index);

    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
     * @return Whether the context field is set.
     */
    boolean hasContext();
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
     * @return The context.
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext getContext();
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder getContextOrBuilder();
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.DynamicNodeInstanceContent}
   */
  public static final class DynamicNodeInstanceContent extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.DynamicNodeInstanceContent)
      DynamicNodeInstanceContentOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use DynamicNodeInstanceContent.newBuilder() to construct.
    private DynamicNodeInstanceContent(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private DynamicNodeInstanceContent() {
      timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new DynamicNodeInstanceContent();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private DynamicNodeInstanceContent(
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
                timerInstanceId_ = new com.google.protobuf.LazyStringArrayList();
                mutable_bitField0_ |= 0x00000001;
              }
              timerInstanceId_.add(s);
              break;
            }
            case 18: {
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder subBuilder = null;
              if (context_ != null) {
                subBuilder = context_.toBuilder();
              }
              context_ = input.readMessage(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(context_);
                context_ = subBuilder.buildPartial();
              }

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
          timerInstanceId_ = timerInstanceId_.getUnmodifiableView();
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_DynamicNodeInstanceContent_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_DynamicNodeInstanceContent_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent.Builder.class);
    }

    public static final int TIMER_INSTANCE_ID_FIELD_NUMBER = 1;
    private com.google.protobuf.LazyStringList timerInstanceId_;
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return A list containing the timerInstanceId.
     */
    public com.google.protobuf.ProtocolStringList
        getTimerInstanceIdList() {
      return timerInstanceId_;
    }
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return The count of timerInstanceId.
     */
    public int getTimerInstanceIdCount() {
      return timerInstanceId_.size();
    }
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the element to return.
     * @return The timerInstanceId at the given index.
     */
    public java.lang.String getTimerInstanceId(int index) {
      return timerInstanceId_.get(index);
    }
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the value to return.
     * @return The bytes of the timerInstanceId at the given index.
     */
    public com.google.protobuf.ByteString
        getTimerInstanceIdBytes(int index) {
      return timerInstanceId_.getByteString(index);
    }

    public static final int CONTEXT_FIELD_NUMBER = 2;
    private org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext context_;
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
     * @return Whether the context field is set.
     */
    @java.lang.Override
    public boolean hasContext() {
      return context_ != null;
    }
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
     * @return The context.
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext getContext() {
      return context_ == null ? org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.getDefaultInstance() : context_;
    }
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder getContextOrBuilder() {
      return getContext();
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
      for (int i = 0; i < timerInstanceId_.size(); i++) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, timerInstanceId_.getRaw(i));
      }
      if (context_ != null) {
        output.writeMessage(2, getContext());
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
        for (int i = 0; i < timerInstanceId_.size(); i++) {
          dataSize += computeStringSizeNoTag(timerInstanceId_.getRaw(i));
        }
        size += dataSize;
        size += 1 * getTimerInstanceIdList().size();
      }
      if (context_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, getContext());
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
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent other = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent) obj;

      if (!getTimerInstanceIdList()
          .equals(other.getTimerInstanceIdList())) return false;
      if (hasContext() != other.hasContext()) return false;
      if (hasContext()) {
        if (!getContext()
            .equals(other.getContext())) return false;
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
      if (getTimerInstanceIdCount() > 0) {
        hash = (37 * hash) + TIMER_INSTANCE_ID_FIELD_NUMBER;
        hash = (53 * hash) + getTimerInstanceIdList().hashCode();
      }
      if (hasContext()) {
        hash = (37 * hash) + CONTEXT_FIELD_NUMBER;
        hash = (53 * hash) + getContext().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent parseFrom(
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
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent prototype) {
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
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.DynamicNodeInstanceContent}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.DynamicNodeInstanceContent)
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContentOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_DynamicNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_DynamicNodeInstanceContent_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent.newBuilder()
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
        timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        if (contextBuilder_ == null) {
          context_ = null;
        } else {
          context_ = null;
          contextBuilder_ = null;
        }
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_DynamicNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent build() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent result = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent(this);
        int from_bitField0_ = bitField0_;
        if (((bitField0_ & 0x00000001) != 0)) {
          timerInstanceId_ = timerInstanceId_.getUnmodifiableView();
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.timerInstanceId_ = timerInstanceId_;
        if (contextBuilder_ == null) {
          result.context_ = context_;
        } else {
          result.context_ = contextBuilder_.build();
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
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent.getDefaultInstance()) return this;
        if (!other.timerInstanceId_.isEmpty()) {
          if (timerInstanceId_.isEmpty()) {
            timerInstanceId_ = other.timerInstanceId_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureTimerInstanceIdIsMutable();
            timerInstanceId_.addAll(other.timerInstanceId_);
          }
          onChanged();
        }
        if (other.hasContext()) {
          mergeContext(other.getContext());
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
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private com.google.protobuf.LazyStringList timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      private void ensureTimerInstanceIdIsMutable() {
        if (!((bitField0_ & 0x00000001) != 0)) {
          timerInstanceId_ = new com.google.protobuf.LazyStringArrayList(timerInstanceId_);
          bitField0_ |= 0x00000001;
         }
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @return A list containing the timerInstanceId.
       */
      public com.google.protobuf.ProtocolStringList
          getTimerInstanceIdList() {
        return timerInstanceId_.getUnmodifiableView();
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @return The count of timerInstanceId.
       */
      public int getTimerInstanceIdCount() {
        return timerInstanceId_.size();
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param index The index of the element to return.
       * @return The timerInstanceId at the given index.
       */
      public java.lang.String getTimerInstanceId(int index) {
        return timerInstanceId_.get(index);
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param index The index of the value to return.
       * @return The bytes of the timerInstanceId at the given index.
       */
      public com.google.protobuf.ByteString
          getTimerInstanceIdBytes(int index) {
        return timerInstanceId_.getByteString(index);
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param index The index to set the value at.
       * @param value The timerInstanceId to set.
       * @return This builder for chaining.
       */
      public Builder setTimerInstanceId(
          int index, java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureTimerInstanceIdIsMutable();
        timerInstanceId_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param value The timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addTimerInstanceId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureTimerInstanceIdIsMutable();
        timerInstanceId_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param values The timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addAllTimerInstanceId(
          java.lang.Iterable<java.lang.String> values) {
        ensureTimerInstanceIdIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, timerInstanceId_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearTimerInstanceId() {
        timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param value The bytes of the timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addTimerInstanceIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        ensureTimerInstanceIdIsMutable();
        timerInstanceId_.add(value);
        onChanged();
        return this;
      }

      private org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext context_;
      private com.google.protobuf.SingleFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder> contextBuilder_;
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       * @return Whether the context field is set.
       */
      public boolean hasContext() {
        return contextBuilder_ != null || context_ != null;
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       * @return The context.
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext getContext() {
        if (contextBuilder_ == null) {
          return context_ == null ? org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.getDefaultInstance() : context_;
        } else {
          return contextBuilder_.getMessage();
        }
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      public Builder setContext(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext value) {
        if (contextBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          context_ = value;
          onChanged();
        } else {
          contextBuilder_.setMessage(value);
        }

        return this;
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      public Builder setContext(
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder builderForValue) {
        if (contextBuilder_ == null) {
          context_ = builderForValue.build();
          onChanged();
        } else {
          contextBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      public Builder mergeContext(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext value) {
        if (contextBuilder_ == null) {
          if (context_ != null) {
            context_ =
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.newBuilder(context_).mergeFrom(value).buildPartial();
          } else {
            context_ = value;
          }
          onChanged();
        } else {
          contextBuilder_.mergeFrom(value);
        }

        return this;
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      public Builder clearContext() {
        if (contextBuilder_ == null) {
          context_ = null;
          onChanged();
        } else {
          context_ = null;
          contextBuilder_ = null;
        }

        return this;
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder getContextBuilder() {
        
        onChanged();
        return getContextFieldBuilder().getBuilder();
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder getContextOrBuilder() {
        if (contextBuilder_ != null) {
          return contextBuilder_.getMessageOrBuilder();
        } else {
          return context_ == null ?
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.getDefaultInstance() : context_;
        }
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder> 
          getContextFieldBuilder() {
        if (contextBuilder_ == null) {
          contextBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder>(
                  getContext(),
                  getParentForChildren(),
                  isClean());
          context_ = null;
        }
        return contextBuilder_;
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


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.DynamicNodeInstanceContent)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.DynamicNodeInstanceContent)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<DynamicNodeInstanceContent>
        PARSER = new com.google.protobuf.AbstractParser<DynamicNodeInstanceContent>() {
      @java.lang.Override
      public DynamicNodeInstanceContent parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new DynamicNodeInstanceContent(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<DynamicNodeInstanceContent> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<DynamicNodeInstanceContent> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.DynamicNodeInstanceContent getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface EventSubProcessNodeInstanceContentOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.EventSubProcessNodeInstanceContent)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return A list containing the timerInstanceId.
     */
    java.util.List<java.lang.String>
        getTimerInstanceIdList();
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return The count of timerInstanceId.
     */
    int getTimerInstanceIdCount();
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the element to return.
     * @return The timerInstanceId at the given index.
     */
    java.lang.String getTimerInstanceId(int index);
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the value to return.
     * @return The bytes of the timerInstanceId at the given index.
     */
    com.google.protobuf.ByteString
        getTimerInstanceIdBytes(int index);

    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
     * @return Whether the context field is set.
     */
    boolean hasContext();
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
     * @return The context.
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext getContext();
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder getContextOrBuilder();
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.EventSubProcessNodeInstanceContent}
   */
  public static final class EventSubProcessNodeInstanceContent extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.EventSubProcessNodeInstanceContent)
      EventSubProcessNodeInstanceContentOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use EventSubProcessNodeInstanceContent.newBuilder() to construct.
    private EventSubProcessNodeInstanceContent(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private EventSubProcessNodeInstanceContent() {
      timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new EventSubProcessNodeInstanceContent();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private EventSubProcessNodeInstanceContent(
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
                timerInstanceId_ = new com.google.protobuf.LazyStringArrayList();
                mutable_bitField0_ |= 0x00000001;
              }
              timerInstanceId_.add(s);
              break;
            }
            case 18: {
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder subBuilder = null;
              if (context_ != null) {
                subBuilder = context_.toBuilder();
              }
              context_ = input.readMessage(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(context_);
                context_ = subBuilder.buildPartial();
              }

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
          timerInstanceId_ = timerInstanceId_.getUnmodifiableView();
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_EventSubProcessNodeInstanceContent_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_EventSubProcessNodeInstanceContent_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent.Builder.class);
    }

    public static final int TIMER_INSTANCE_ID_FIELD_NUMBER = 1;
    private com.google.protobuf.LazyStringList timerInstanceId_;
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return A list containing the timerInstanceId.
     */
    public com.google.protobuf.ProtocolStringList
        getTimerInstanceIdList() {
      return timerInstanceId_;
    }
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return The count of timerInstanceId.
     */
    public int getTimerInstanceIdCount() {
      return timerInstanceId_.size();
    }
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the element to return.
     * @return The timerInstanceId at the given index.
     */
    public java.lang.String getTimerInstanceId(int index) {
      return timerInstanceId_.get(index);
    }
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the value to return.
     * @return The bytes of the timerInstanceId at the given index.
     */
    public com.google.protobuf.ByteString
        getTimerInstanceIdBytes(int index) {
      return timerInstanceId_.getByteString(index);
    }

    public static final int CONTEXT_FIELD_NUMBER = 2;
    private org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext context_;
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
     * @return Whether the context field is set.
     */
    @java.lang.Override
    public boolean hasContext() {
      return context_ != null;
    }
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
     * @return The context.
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext getContext() {
      return context_ == null ? org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.getDefaultInstance() : context_;
    }
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder getContextOrBuilder() {
      return getContext();
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
      for (int i = 0; i < timerInstanceId_.size(); i++) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, timerInstanceId_.getRaw(i));
      }
      if (context_ != null) {
        output.writeMessage(2, getContext());
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
        for (int i = 0; i < timerInstanceId_.size(); i++) {
          dataSize += computeStringSizeNoTag(timerInstanceId_.getRaw(i));
        }
        size += dataSize;
        size += 1 * getTimerInstanceIdList().size();
      }
      if (context_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, getContext());
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
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent other = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent) obj;

      if (!getTimerInstanceIdList()
          .equals(other.getTimerInstanceIdList())) return false;
      if (hasContext() != other.hasContext()) return false;
      if (hasContext()) {
        if (!getContext()
            .equals(other.getContext())) return false;
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
      if (getTimerInstanceIdCount() > 0) {
        hash = (37 * hash) + TIMER_INSTANCE_ID_FIELD_NUMBER;
        hash = (53 * hash) + getTimerInstanceIdList().hashCode();
      }
      if (hasContext()) {
        hash = (37 * hash) + CONTEXT_FIELD_NUMBER;
        hash = (53 * hash) + getContext().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent parseFrom(
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
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent prototype) {
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
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.EventSubProcessNodeInstanceContent}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.EventSubProcessNodeInstanceContent)
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContentOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_EventSubProcessNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_EventSubProcessNodeInstanceContent_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent.newBuilder()
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
        timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        if (contextBuilder_ == null) {
          context_ = null;
        } else {
          context_ = null;
          contextBuilder_ = null;
        }
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_EventSubProcessNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent build() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent result = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent(this);
        int from_bitField0_ = bitField0_;
        if (((bitField0_ & 0x00000001) != 0)) {
          timerInstanceId_ = timerInstanceId_.getUnmodifiableView();
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.timerInstanceId_ = timerInstanceId_;
        if (contextBuilder_ == null) {
          result.context_ = context_;
        } else {
          result.context_ = contextBuilder_.build();
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
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent.getDefaultInstance()) return this;
        if (!other.timerInstanceId_.isEmpty()) {
          if (timerInstanceId_.isEmpty()) {
            timerInstanceId_ = other.timerInstanceId_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureTimerInstanceIdIsMutable();
            timerInstanceId_.addAll(other.timerInstanceId_);
          }
          onChanged();
        }
        if (other.hasContext()) {
          mergeContext(other.getContext());
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
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private com.google.protobuf.LazyStringList timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      private void ensureTimerInstanceIdIsMutable() {
        if (!((bitField0_ & 0x00000001) != 0)) {
          timerInstanceId_ = new com.google.protobuf.LazyStringArrayList(timerInstanceId_);
          bitField0_ |= 0x00000001;
         }
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @return A list containing the timerInstanceId.
       */
      public com.google.protobuf.ProtocolStringList
          getTimerInstanceIdList() {
        return timerInstanceId_.getUnmodifiableView();
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @return The count of timerInstanceId.
       */
      public int getTimerInstanceIdCount() {
        return timerInstanceId_.size();
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param index The index of the element to return.
       * @return The timerInstanceId at the given index.
       */
      public java.lang.String getTimerInstanceId(int index) {
        return timerInstanceId_.get(index);
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param index The index of the value to return.
       * @return The bytes of the timerInstanceId at the given index.
       */
      public com.google.protobuf.ByteString
          getTimerInstanceIdBytes(int index) {
        return timerInstanceId_.getByteString(index);
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param index The index to set the value at.
       * @param value The timerInstanceId to set.
       * @return This builder for chaining.
       */
      public Builder setTimerInstanceId(
          int index, java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureTimerInstanceIdIsMutable();
        timerInstanceId_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param value The timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addTimerInstanceId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureTimerInstanceIdIsMutable();
        timerInstanceId_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param values The timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addAllTimerInstanceId(
          java.lang.Iterable<java.lang.String> values) {
        ensureTimerInstanceIdIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, timerInstanceId_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearTimerInstanceId() {
        timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param value The bytes of the timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addTimerInstanceIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        ensureTimerInstanceIdIsMutable();
        timerInstanceId_.add(value);
        onChanged();
        return this;
      }

      private org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext context_;
      private com.google.protobuf.SingleFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder> contextBuilder_;
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       * @return Whether the context field is set.
       */
      public boolean hasContext() {
        return contextBuilder_ != null || context_ != null;
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       * @return The context.
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext getContext() {
        if (contextBuilder_ == null) {
          return context_ == null ? org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.getDefaultInstance() : context_;
        } else {
          return contextBuilder_.getMessage();
        }
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      public Builder setContext(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext value) {
        if (contextBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          context_ = value;
          onChanged();
        } else {
          contextBuilder_.setMessage(value);
        }

        return this;
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      public Builder setContext(
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder builderForValue) {
        if (contextBuilder_ == null) {
          context_ = builderForValue.build();
          onChanged();
        } else {
          contextBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      public Builder mergeContext(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext value) {
        if (contextBuilder_ == null) {
          if (context_ != null) {
            context_ =
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.newBuilder(context_).mergeFrom(value).buildPartial();
          } else {
            context_ = value;
          }
          onChanged();
        } else {
          contextBuilder_.mergeFrom(value);
        }

        return this;
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      public Builder clearContext() {
        if (contextBuilder_ == null) {
          context_ = null;
          onChanged();
        } else {
          context_ = null;
          contextBuilder_ = null;
        }

        return this;
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder getContextBuilder() {
        
        onChanged();
        return getContextFieldBuilder().getBuilder();
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder getContextOrBuilder() {
        if (contextBuilder_ != null) {
          return contextBuilder_.getMessageOrBuilder();
        } else {
          return context_ == null ?
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.getDefaultInstance() : context_;
        }
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder> 
          getContextFieldBuilder() {
        if (contextBuilder_ == null) {
          contextBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder>(
                  getContext(),
                  getParentForChildren(),
                  isClean());
          context_ = null;
        }
        return contextBuilder_;
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


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.EventSubProcessNodeInstanceContent)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.EventSubProcessNodeInstanceContent)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<EventSubProcessNodeInstanceContent>
        PARSER = new com.google.protobuf.AbstractParser<EventSubProcessNodeInstanceContent>() {
      @java.lang.Override
      public EventSubProcessNodeInstanceContent parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new EventSubProcessNodeInstanceContent(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<EventSubProcessNodeInstanceContent> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<EventSubProcessNodeInstanceContent> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.EventSubProcessNodeInstanceContent getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface ForEachNodeInstanceContentOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.ForEachNodeInstanceContent)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return A list containing the timerInstanceId.
     */
    java.util.List<java.lang.String>
        getTimerInstanceIdList();
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return The count of timerInstanceId.
     */
    int getTimerInstanceIdCount();
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the element to return.
     * @return The timerInstanceId at the given index.
     */
    java.lang.String getTimerInstanceId(int index);
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the value to return.
     * @return The bytes of the timerInstanceId at the given index.
     */
    com.google.protobuf.ByteString
        getTimerInstanceIdBytes(int index);

    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
     * @return Whether the context field is set.
     */
    boolean hasContext();
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
     * @return The context.
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext getContext();
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder getContextOrBuilder();

    /**
     * <code>int32 totalInstances = 3;</code>
     * @return The totalInstances.
     */
    int getTotalInstances();

    /**
     * <code>int32 executedInstances = 4;</code>
     * @return The executedInstances.
     */
    int getExecutedInstances();

    /**
     * <code>bool hasAsyncInstances = 5;</code>
     * @return The hasAsyncInstances.
     */
    boolean getHasAsyncInstances();
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.ForEachNodeInstanceContent}
   */
  public static final class ForEachNodeInstanceContent extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.ForEachNodeInstanceContent)
      ForEachNodeInstanceContentOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use ForEachNodeInstanceContent.newBuilder() to construct.
    private ForEachNodeInstanceContent(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private ForEachNodeInstanceContent() {
      timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new ForEachNodeInstanceContent();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private ForEachNodeInstanceContent(
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
                timerInstanceId_ = new com.google.protobuf.LazyStringArrayList();
                mutable_bitField0_ |= 0x00000001;
              }
              timerInstanceId_.add(s);
              break;
            }
            case 18: {
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder subBuilder = null;
              if (context_ != null) {
                subBuilder = context_.toBuilder();
              }
              context_ = input.readMessage(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(context_);
                context_ = subBuilder.buildPartial();
              }

              break;
            }
            case 24: {

              totalInstances_ = input.readInt32();
              break;
            }
            case 32: {

              executedInstances_ = input.readInt32();
              break;
            }
            case 40: {

              hasAsyncInstances_ = input.readBool();
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
          timerInstanceId_ = timerInstanceId_.getUnmodifiableView();
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_ForEachNodeInstanceContent_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_ForEachNodeInstanceContent_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent.Builder.class);
    }

    public static final int TIMER_INSTANCE_ID_FIELD_NUMBER = 1;
    private com.google.protobuf.LazyStringList timerInstanceId_;
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return A list containing the timerInstanceId.
     */
    public com.google.protobuf.ProtocolStringList
        getTimerInstanceIdList() {
      return timerInstanceId_;
    }
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @return The count of timerInstanceId.
     */
    public int getTimerInstanceIdCount() {
      return timerInstanceId_.size();
    }
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the element to return.
     * @return The timerInstanceId at the given index.
     */
    public java.lang.String getTimerInstanceId(int index) {
      return timerInstanceId_.get(index);
    }
    /**
     * <code>repeated string timer_instance_id = 1;</code>
     * @param index The index of the value to return.
     * @return The bytes of the timerInstanceId at the given index.
     */
    public com.google.protobuf.ByteString
        getTimerInstanceIdBytes(int index) {
      return timerInstanceId_.getByteString(index);
    }

    public static final int CONTEXT_FIELD_NUMBER = 2;
    private org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext context_;
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
     * @return Whether the context field is set.
     */
    @java.lang.Override
    public boolean hasContext() {
      return context_ != null;
    }
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
     * @return The context.
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext getContext() {
      return context_ == null ? org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.getDefaultInstance() : context_;
    }
    /**
     * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder getContextOrBuilder() {
      return getContext();
    }

    public static final int TOTALINSTANCES_FIELD_NUMBER = 3;
    private int totalInstances_;
    /**
     * <code>int32 totalInstances = 3;</code>
     * @return The totalInstances.
     */
    @java.lang.Override
    public int getTotalInstances() {
      return totalInstances_;
    }

    public static final int EXECUTEDINSTANCES_FIELD_NUMBER = 4;
    private int executedInstances_;
    /**
     * <code>int32 executedInstances = 4;</code>
     * @return The executedInstances.
     */
    @java.lang.Override
    public int getExecutedInstances() {
      return executedInstances_;
    }

    public static final int HASASYNCINSTANCES_FIELD_NUMBER = 5;
    private boolean hasAsyncInstances_;
    /**
     * <code>bool hasAsyncInstances = 5;</code>
     * @return The hasAsyncInstances.
     */
    @java.lang.Override
    public boolean getHasAsyncInstances() {
      return hasAsyncInstances_;
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
      for (int i = 0; i < timerInstanceId_.size(); i++) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, timerInstanceId_.getRaw(i));
      }
      if (context_ != null) {
        output.writeMessage(2, getContext());
      }
      if (totalInstances_ != 0) {
        output.writeInt32(3, totalInstances_);
      }
      if (executedInstances_ != 0) {
        output.writeInt32(4, executedInstances_);
      }
      if (hasAsyncInstances_ != false) {
        output.writeBool(5, hasAsyncInstances_);
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
        for (int i = 0; i < timerInstanceId_.size(); i++) {
          dataSize += computeStringSizeNoTag(timerInstanceId_.getRaw(i));
        }
        size += dataSize;
        size += 1 * getTimerInstanceIdList().size();
      }
      if (context_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, getContext());
      }
      if (totalInstances_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(3, totalInstances_);
      }
      if (executedInstances_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(4, executedInstances_);
      }
      if (hasAsyncInstances_ != false) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(5, hasAsyncInstances_);
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
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent other = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent) obj;

      if (!getTimerInstanceIdList()
          .equals(other.getTimerInstanceIdList())) return false;
      if (hasContext() != other.hasContext()) return false;
      if (hasContext()) {
        if (!getContext()
            .equals(other.getContext())) return false;
      }
      if (getTotalInstances()
          != other.getTotalInstances()) return false;
      if (getExecutedInstances()
          != other.getExecutedInstances()) return false;
      if (getHasAsyncInstances()
          != other.getHasAsyncInstances()) return false;
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
      if (getTimerInstanceIdCount() > 0) {
        hash = (37 * hash) + TIMER_INSTANCE_ID_FIELD_NUMBER;
        hash = (53 * hash) + getTimerInstanceIdList().hashCode();
      }
      if (hasContext()) {
        hash = (37 * hash) + CONTEXT_FIELD_NUMBER;
        hash = (53 * hash) + getContext().hashCode();
      }
      hash = (37 * hash) + TOTALINSTANCES_FIELD_NUMBER;
      hash = (53 * hash) + getTotalInstances();
      hash = (37 * hash) + EXECUTEDINSTANCES_FIELD_NUMBER;
      hash = (53 * hash) + getExecutedInstances();
      hash = (37 * hash) + HASASYNCINSTANCES_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
          getHasAsyncInstances());
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent parseFrom(
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
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent prototype) {
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
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.ForEachNodeInstanceContent}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.ForEachNodeInstanceContent)
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContentOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_ForEachNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_ForEachNodeInstanceContent_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent.newBuilder()
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
        timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        if (contextBuilder_ == null) {
          context_ = null;
        } else {
          context_ = null;
          contextBuilder_ = null;
        }
        totalInstances_ = 0;

        executedInstances_ = 0;

        hasAsyncInstances_ = false;

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_ForEachNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent build() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent result = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent(this);
        int from_bitField0_ = bitField0_;
        if (((bitField0_ & 0x00000001) != 0)) {
          timerInstanceId_ = timerInstanceId_.getUnmodifiableView();
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.timerInstanceId_ = timerInstanceId_;
        if (contextBuilder_ == null) {
          result.context_ = context_;
        } else {
          result.context_ = contextBuilder_.build();
        }
        result.totalInstances_ = totalInstances_;
        result.executedInstances_ = executedInstances_;
        result.hasAsyncInstances_ = hasAsyncInstances_;
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
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent.getDefaultInstance()) return this;
        if (!other.timerInstanceId_.isEmpty()) {
          if (timerInstanceId_.isEmpty()) {
            timerInstanceId_ = other.timerInstanceId_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureTimerInstanceIdIsMutable();
            timerInstanceId_.addAll(other.timerInstanceId_);
          }
          onChanged();
        }
        if (other.hasContext()) {
          mergeContext(other.getContext());
        }
        if (other.getTotalInstances() != 0) {
          setTotalInstances(other.getTotalInstances());
        }
        if (other.getExecutedInstances() != 0) {
          setExecutedInstances(other.getExecutedInstances());
        }
        if (other.getHasAsyncInstances() != false) {
          setHasAsyncInstances(other.getHasAsyncInstances());
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
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private com.google.protobuf.LazyStringList timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      private void ensureTimerInstanceIdIsMutable() {
        if (!((bitField0_ & 0x00000001) != 0)) {
          timerInstanceId_ = new com.google.protobuf.LazyStringArrayList(timerInstanceId_);
          bitField0_ |= 0x00000001;
         }
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @return A list containing the timerInstanceId.
       */
      public com.google.protobuf.ProtocolStringList
          getTimerInstanceIdList() {
        return timerInstanceId_.getUnmodifiableView();
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @return The count of timerInstanceId.
       */
      public int getTimerInstanceIdCount() {
        return timerInstanceId_.size();
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param index The index of the element to return.
       * @return The timerInstanceId at the given index.
       */
      public java.lang.String getTimerInstanceId(int index) {
        return timerInstanceId_.get(index);
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param index The index of the value to return.
       * @return The bytes of the timerInstanceId at the given index.
       */
      public com.google.protobuf.ByteString
          getTimerInstanceIdBytes(int index) {
        return timerInstanceId_.getByteString(index);
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param index The index to set the value at.
       * @param value The timerInstanceId to set.
       * @return This builder for chaining.
       */
      public Builder setTimerInstanceId(
          int index, java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureTimerInstanceIdIsMutable();
        timerInstanceId_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param value The timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addTimerInstanceId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureTimerInstanceIdIsMutable();
        timerInstanceId_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param values The timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addAllTimerInstanceId(
          java.lang.Iterable<java.lang.String> values) {
        ensureTimerInstanceIdIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, timerInstanceId_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearTimerInstanceId() {
        timerInstanceId_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string timer_instance_id = 1;</code>
       * @param value The bytes of the timerInstanceId to add.
       * @return This builder for chaining.
       */
      public Builder addTimerInstanceIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        ensureTimerInstanceIdIsMutable();
        timerInstanceId_.add(value);
        onChanged();
        return this;
      }

      private org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext context_;
      private com.google.protobuf.SingleFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder> contextBuilder_;
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       * @return Whether the context field is set.
       */
      public boolean hasContext() {
        return contextBuilder_ != null || context_ != null;
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       * @return The context.
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext getContext() {
        if (contextBuilder_ == null) {
          return context_ == null ? org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.getDefaultInstance() : context_;
        } else {
          return contextBuilder_.getMessage();
        }
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      public Builder setContext(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext value) {
        if (contextBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          context_ = value;
          onChanged();
        } else {
          contextBuilder_.setMessage(value);
        }

        return this;
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      public Builder setContext(
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder builderForValue) {
        if (contextBuilder_ == null) {
          context_ = builderForValue.build();
          onChanged();
        } else {
          contextBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      public Builder mergeContext(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext value) {
        if (contextBuilder_ == null) {
          if (context_ != null) {
            context_ =
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.newBuilder(context_).mergeFrom(value).buildPartial();
          } else {
            context_ = value;
          }
          onChanged();
        } else {
          contextBuilder_.mergeFrom(value);
        }

        return this;
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      public Builder clearContext() {
        if (contextBuilder_ == null) {
          context_ = null;
          onChanged();
        } else {
          context_ = null;
          contextBuilder_ = null;
        }

        return this;
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder getContextBuilder() {
        
        onChanged();
        return getContextFieldBuilder().getBuilder();
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder getContextOrBuilder() {
        if (contextBuilder_ != null) {
          return contextBuilder_.getMessageOrBuilder();
        } else {
          return context_ == null ?
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.getDefaultInstance() : context_;
        }
      }
      /**
       * <code>.org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 2;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder> 
          getContextFieldBuilder() {
        if (contextBuilder_ == null) {
          contextBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder>(
                  getContext(),
                  getParentForChildren(),
                  isClean());
          context_ = null;
        }
        return contextBuilder_;
      }

      private int totalInstances_ ;
      /**
       * <code>int32 totalInstances = 3;</code>
       * @return The totalInstances.
       */
      @java.lang.Override
      public int getTotalInstances() {
        return totalInstances_;
      }
      /**
       * <code>int32 totalInstances = 3;</code>
       * @param value The totalInstances to set.
       * @return This builder for chaining.
       */
      public Builder setTotalInstances(int value) {
        
        totalInstances_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 totalInstances = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearTotalInstances() {
        
        totalInstances_ = 0;
        onChanged();
        return this;
      }

      private int executedInstances_ ;
      /**
       * <code>int32 executedInstances = 4;</code>
       * @return The executedInstances.
       */
      @java.lang.Override
      public int getExecutedInstances() {
        return executedInstances_;
      }
      /**
       * <code>int32 executedInstances = 4;</code>
       * @param value The executedInstances to set.
       * @return This builder for chaining.
       */
      public Builder setExecutedInstances(int value) {
        
        executedInstances_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 executedInstances = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearExecutedInstances() {
        
        executedInstances_ = 0;
        onChanged();
        return this;
      }

      private boolean hasAsyncInstances_ ;
      /**
       * <code>bool hasAsyncInstances = 5;</code>
       * @return The hasAsyncInstances.
       */
      @java.lang.Override
      public boolean getHasAsyncInstances() {
        return hasAsyncInstances_;
      }
      /**
       * <code>bool hasAsyncInstances = 5;</code>
       * @param value The hasAsyncInstances to set.
       * @return This builder for chaining.
       */
      public Builder setHasAsyncInstances(boolean value) {
        
        hasAsyncInstances_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bool hasAsyncInstances = 5;</code>
       * @return This builder for chaining.
       */
      public Builder clearHasAsyncInstances() {
        
        hasAsyncInstances_ = false;
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


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.ForEachNodeInstanceContent)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.ForEachNodeInstanceContent)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<ForEachNodeInstanceContent>
        PARSER = new com.google.protobuf.AbstractParser<ForEachNodeInstanceContent>() {
      @java.lang.Override
      public ForEachNodeInstanceContent parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new ForEachNodeInstanceContent(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<ForEachNodeInstanceContent> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<ForEachNodeInstanceContent> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.ForEachNodeInstanceContent getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface AsyncEventNodeInstanceContentOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.AsyncEventNodeInstanceContent)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>string job_id = 1;</code>
     * @return The jobId.
     */
    java.lang.String getJobId();
    /**
     * <code>string job_id = 1;</code>
     * @return The bytes for jobId.
     */
    com.google.protobuf.ByteString
        getJobIdBytes();
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.AsyncEventNodeInstanceContent}
   */
  public static final class AsyncEventNodeInstanceContent extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.AsyncEventNodeInstanceContent)
      AsyncEventNodeInstanceContentOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use AsyncEventNodeInstanceContent.newBuilder() to construct.
    private AsyncEventNodeInstanceContent(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private AsyncEventNodeInstanceContent() {
      jobId_ = "";
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new AsyncEventNodeInstanceContent();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private AsyncEventNodeInstanceContent(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      if (extensionRegistry == null) {
        throw new java.lang.NullPointerException();
      }
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

              jobId_ = s;
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
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_AsyncEventNodeInstanceContent_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_AsyncEventNodeInstanceContent_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent.Builder.class);
    }

    public static final int JOB_ID_FIELD_NUMBER = 1;
    private volatile java.lang.Object jobId_;
    /**
     * <code>string job_id = 1;</code>
     * @return The jobId.
     */
    @java.lang.Override
    public java.lang.String getJobId() {
      java.lang.Object ref = jobId_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        jobId_ = s;
        return s;
      }
    }
    /**
     * <code>string job_id = 1;</code>
     * @return The bytes for jobId.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getJobIdBytes() {
      java.lang.Object ref = jobId_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        jobId_ = b;
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
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(jobId_)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, jobId_);
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(jobId_)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, jobId_);
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
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent other = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent) obj;

      if (!getJobId()
          .equals(other.getJobId())) return false;
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
      hash = (37 * hash) + JOB_ID_FIELD_NUMBER;
      hash = (53 * hash) + getJobId().hashCode();
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent parseFrom(
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
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent prototype) {
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
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.AsyncEventNodeInstanceContent}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.AsyncEventNodeInstanceContent)
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContentOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_AsyncEventNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_AsyncEventNodeInstanceContent_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent.class, org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent.newBuilder()
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
        jobId_ = "";

        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_AsyncEventNodeInstanceContent_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent build() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent result = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent(this);
        result.jobId_ = jobId_;
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
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent.getDefaultInstance()) return this;
        if (!other.getJobId().isEmpty()) {
          jobId_ = other.jobId_;
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
        org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private java.lang.Object jobId_ = "";
      /**
       * <code>string job_id = 1;</code>
       * @return The jobId.
       */
      public java.lang.String getJobId() {
        java.lang.Object ref = jobId_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          jobId_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string job_id = 1;</code>
       * @return The bytes for jobId.
       */
      public com.google.protobuf.ByteString
          getJobIdBytes() {
        java.lang.Object ref = jobId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          jobId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string job_id = 1;</code>
       * @param value The jobId to set.
       * @return This builder for chaining.
       */
      public Builder setJobId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        jobId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string job_id = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearJobId() {
        
        jobId_ = getDefaultInstance().getJobId();
        onChanged();
        return this;
      }
      /**
       * <code>string job_id = 1;</code>
       * @param value The bytes for jobId to set.
       * @return This builder for chaining.
       */
      public Builder setJobIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        jobId_ = value;
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


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.AsyncEventNodeInstanceContent)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.AsyncEventNodeInstanceContent)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<AsyncEventNodeInstanceContent>
        PARSER = new com.google.protobuf.AbstractParser<AsyncEventNodeInstanceContent>() {
      @java.lang.Override
      public AsyncEventNodeInstanceContent parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new AsyncEventNodeInstanceContent(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<AsyncEventNodeInstanceContent> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<AsyncEventNodeInstanceContent> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoNodeInstanceContentsProtobuf.AsyncEventNodeInstanceContent getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_RuleSetNodeInstanceContent_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_RuleSetNodeInstanceContent_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_WorkItemNodeInstanceContent_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_WorkItemNodeInstanceContent_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_LambdaSubProcessNodeInstanceContent_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_LambdaSubProcessNodeInstanceContent_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_SubProcessNodeInstanceContent_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_SubProcessNodeInstanceContent_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_MilestoneNodeInstanceContent_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_MilestoneNodeInstanceContent_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_EventNodeInstanceContent_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_EventNodeInstanceContent_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_TimerNodeInstanceContent_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_TimerNodeInstanceContent_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_JoinNodeInstanceContent_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_JoinNodeInstanceContent_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_JoinNodeInstanceContent_JoinTrigger_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_JoinNodeInstanceContent_JoinTrigger_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_StateNodeInstanceContent_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_StateNodeInstanceContent_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_CompositeContextNodeInstanceContent_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_CompositeContextNodeInstanceContent_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_DynamicNodeInstanceContent_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_DynamicNodeInstanceContent_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_EventSubProcessNodeInstanceContent_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_EventSubProcessNodeInstanceContent_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_ForEachNodeInstanceContent_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_ForEachNodeInstanceContent_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_AsyncEventNodeInstanceContent_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_AsyncEventNodeInstanceContent_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\nQorg/kie/kogito/serialization/process/p" +
      "rotobuf/kogito_node_instance_contents.pr" +
      "oto\022-org.kie.kogito.serialization.proces" +
      "s.protobuf\032@org/kie/kogito/serialization" +
      "/process/protobuf/kogito_types.proto\032\031go" +
      "ogle/protobuf/any.proto\"i\n\032RuleSetNodeIn" +
      "stanceContent\022\031\n\021timer_instance_id\030\001 \003(\t" +
      "\022\034\n\017rule_flow_group\030\002 \001(\tH\000\210\001\001B\022\n\020_rule_" +
      "flow_group\"\321\004\n\033WorkItemNodeInstanceConte" +
      "nt\022\024\n\014work_item_id\030\001 \001(\t\022\031\n\021timer_instan" +
      "ce_id\030\002 \003(\t\022/\n\"error_handling_process_in" +
      "stance_id\030\003 \001(\tH\000\210\001\001\022\r\n\005state\030\004 \001(\005\022I\n\010v" +
      "ariable\030\005 \003(\01327.org.kie.kogito.serializa" +
      "tion.process.protobuf.Variable\022G\n\006result" +
      "\030\006 \003(\01327.org.kie.kogito.serialization.pr" +
      "ocess.protobuf.Variable\022\025\n\010phase_id\030\007 \001(" +
      "\tH\001\210\001\001\022\031\n\014phase_status\030\010 \001(\tH\002\210\001\001\022\021\n\004nam" +
      "e\030\t \001(\tH\003\210\001\001\022\027\n\nstart_date\030\n \001(\003H\004\210\001\001\022\032\n" +
      "\rcomplete_date\030\013 \001(\003H\005\210\001\001\0221\n\016work_item_d" +
      "ata\030\014 \001(\0132\024.google.protobuf.AnyH\006\210\001\001B%\n#" +
      "_error_handling_process_instance_idB\013\n\t_" +
      "phase_idB\017\n\r_phase_statusB\007\n\005_nameB\r\n\013_s" +
      "tart_dateB\020\n\016_complete_dateB\021\n\017_work_ite" +
      "m_data\"z\n#LambdaSubProcessNodeInstanceCo" +
      "ntent\022 \n\023process_instance_id\030\001 \001(\tH\000\210\001\001\022" +
      "\031\n\021timer_instance_id\030\002 \003(\tB\026\n\024_process_i" +
      "nstance_id\"t\n\035SubProcessNodeInstanceCont" +
      "ent\022 \n\023process_instance_id\030\001 \001(\tH\000\210\001\001\022\031\n" +
      "\021timer_instance_id\030\002 \003(\tB\026\n\024_process_ins" +
      "tance_id\"9\n\034MilestoneNodeInstanceContent" +
      "\022\031\n\021timer_instance_id\030\001 \003(\t\"\032\n\030EventNode" +
      "InstanceContent\">\n\030TimerNodeInstanceCont" +
      "ent\022\025\n\010timer_id\030\001 \001(\tH\000\210\001\001B\013\n\t_timer_id\"" +
      "\321\001\n\027JoinNodeInstanceContent\022c\n\007trigger\030\001" +
      " \003(\0132R.org.kie.kogito.serialization.proc" +
      "ess.protobuf.JoinNodeInstanceContent.Joi" +
      "nTrigger\032Q\n\013JoinTrigger\022\024\n\007node_id\030\001 \001(\003" +
      "H\000\210\001\001\022\024\n\007counter\030\002 \001(\005H\001\210\001\001B\n\n\010_node_idB" +
      "\n\n\010_counter\"5\n\030StateNodeInstanceContent\022" +
      "\031\n\021timer_instance_id\030\001 \003(\t\"\221\001\n#Composite" +
      "ContextNodeInstanceContent\022\031\n\021timer_inst" +
      "ance_id\030\001 \003(\t\022O\n\007context\030\002 \001(\0132>.org.kie" +
      ".kogito.serialization.process.protobuf.W" +
      "orkflowContext\"\210\001\n\032DynamicNodeInstanceCo" +
      "ntent\022\031\n\021timer_instance_id\030\001 \003(\t\022O\n\007cont" +
      "ext\030\002 \001(\0132>.org.kie.kogito.serialization" +
      ".process.protobuf.WorkflowContext\"\220\001\n\"Ev" +
      "entSubProcessNodeInstanceContent\022\031\n\021time" +
      "r_instance_id\030\001 \003(\t\022O\n\007context\030\002 \001(\0132>.o" +
      "rg.kie.kogito.serialization.process.prot" +
      "obuf.WorkflowContext\"\326\001\n\032ForEachNodeInst" +
      "anceContent\022\031\n\021timer_instance_id\030\001 \003(\t\022O" +
      "\n\007context\030\002 \001(\0132>.org.kie.kogito.seriali" +
      "zation.process.protobuf.WorkflowContext\022" +
      "\026\n\016totalInstances\030\003 \001(\005\022\031\n\021executedInsta" +
      "nces\030\004 \001(\005\022\031\n\021hasAsyncInstances\030\005 \001(\010\"/\n" +
      "\035AsyncEventNodeInstanceContent\022\016\n\006job_id" +
      "\030\001 \001(\tB$B\"KogitoNodeInstanceContentsProt" +
      "obufb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.getDescriptor(),
          com.google.protobuf.AnyProto.getDescriptor(),
        });
    internal_static_org_kie_kogito_serialization_process_protobuf_RuleSetNodeInstanceContent_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_org_kie_kogito_serialization_process_protobuf_RuleSetNodeInstanceContent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_RuleSetNodeInstanceContent_descriptor,
        new java.lang.String[] { "TimerInstanceId", "RuleFlowGroup", "RuleFlowGroup", });
    internal_static_org_kie_kogito_serialization_process_protobuf_WorkItemNodeInstanceContent_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_org_kie_kogito_serialization_process_protobuf_WorkItemNodeInstanceContent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_WorkItemNodeInstanceContent_descriptor,
        new java.lang.String[] { "WorkItemId", "TimerInstanceId", "ErrorHandlingProcessInstanceId", "State", "Variable", "Result", "PhaseId", "PhaseStatus", "Name", "StartDate", "CompleteDate", "WorkItemData", "ErrorHandlingProcessInstanceId", "PhaseId", "PhaseStatus", "Name", "StartDate", "CompleteDate", "WorkItemData", });
    internal_static_org_kie_kogito_serialization_process_protobuf_LambdaSubProcessNodeInstanceContent_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_org_kie_kogito_serialization_process_protobuf_LambdaSubProcessNodeInstanceContent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_LambdaSubProcessNodeInstanceContent_descriptor,
        new java.lang.String[] { "ProcessInstanceId", "TimerInstanceId", "ProcessInstanceId", });
    internal_static_org_kie_kogito_serialization_process_protobuf_SubProcessNodeInstanceContent_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_org_kie_kogito_serialization_process_protobuf_SubProcessNodeInstanceContent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_SubProcessNodeInstanceContent_descriptor,
        new java.lang.String[] { "ProcessInstanceId", "TimerInstanceId", "ProcessInstanceId", });
    internal_static_org_kie_kogito_serialization_process_protobuf_MilestoneNodeInstanceContent_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_org_kie_kogito_serialization_process_protobuf_MilestoneNodeInstanceContent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_MilestoneNodeInstanceContent_descriptor,
        new java.lang.String[] { "TimerInstanceId", });
    internal_static_org_kie_kogito_serialization_process_protobuf_EventNodeInstanceContent_descriptor =
      getDescriptor().getMessageTypes().get(5);
    internal_static_org_kie_kogito_serialization_process_protobuf_EventNodeInstanceContent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_EventNodeInstanceContent_descriptor,
        new java.lang.String[] { });
    internal_static_org_kie_kogito_serialization_process_protobuf_TimerNodeInstanceContent_descriptor =
      getDescriptor().getMessageTypes().get(6);
    internal_static_org_kie_kogito_serialization_process_protobuf_TimerNodeInstanceContent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_TimerNodeInstanceContent_descriptor,
        new java.lang.String[] { "TimerId", "TimerId", });
    internal_static_org_kie_kogito_serialization_process_protobuf_JoinNodeInstanceContent_descriptor =
      getDescriptor().getMessageTypes().get(7);
    internal_static_org_kie_kogito_serialization_process_protobuf_JoinNodeInstanceContent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_JoinNodeInstanceContent_descriptor,
        new java.lang.String[] { "Trigger", });
    internal_static_org_kie_kogito_serialization_process_protobuf_JoinNodeInstanceContent_JoinTrigger_descriptor =
      internal_static_org_kie_kogito_serialization_process_protobuf_JoinNodeInstanceContent_descriptor.getNestedTypes().get(0);
    internal_static_org_kie_kogito_serialization_process_protobuf_JoinNodeInstanceContent_JoinTrigger_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_JoinNodeInstanceContent_JoinTrigger_descriptor,
        new java.lang.String[] { "NodeId", "Counter", "NodeId", "Counter", });
    internal_static_org_kie_kogito_serialization_process_protobuf_StateNodeInstanceContent_descriptor =
      getDescriptor().getMessageTypes().get(8);
    internal_static_org_kie_kogito_serialization_process_protobuf_StateNodeInstanceContent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_StateNodeInstanceContent_descriptor,
        new java.lang.String[] { "TimerInstanceId", });
    internal_static_org_kie_kogito_serialization_process_protobuf_CompositeContextNodeInstanceContent_descriptor =
      getDescriptor().getMessageTypes().get(9);
    internal_static_org_kie_kogito_serialization_process_protobuf_CompositeContextNodeInstanceContent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_CompositeContextNodeInstanceContent_descriptor,
        new java.lang.String[] { "TimerInstanceId", "Context", });
    internal_static_org_kie_kogito_serialization_process_protobuf_DynamicNodeInstanceContent_descriptor =
      getDescriptor().getMessageTypes().get(10);
    internal_static_org_kie_kogito_serialization_process_protobuf_DynamicNodeInstanceContent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_DynamicNodeInstanceContent_descriptor,
        new java.lang.String[] { "TimerInstanceId", "Context", });
    internal_static_org_kie_kogito_serialization_process_protobuf_EventSubProcessNodeInstanceContent_descriptor =
      getDescriptor().getMessageTypes().get(11);
    internal_static_org_kie_kogito_serialization_process_protobuf_EventSubProcessNodeInstanceContent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_EventSubProcessNodeInstanceContent_descriptor,
        new java.lang.String[] { "TimerInstanceId", "Context", });
    internal_static_org_kie_kogito_serialization_process_protobuf_ForEachNodeInstanceContent_descriptor =
      getDescriptor().getMessageTypes().get(12);
    internal_static_org_kie_kogito_serialization_process_protobuf_ForEachNodeInstanceContent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_ForEachNodeInstanceContent_descriptor,
        new java.lang.String[] { "TimerInstanceId", "Context", "TotalInstances", "ExecutedInstances", "HasAsyncInstances", });
    internal_static_org_kie_kogito_serialization_process_protobuf_AsyncEventNodeInstanceContent_descriptor =
      getDescriptor().getMessageTypes().get(13);
    internal_static_org_kie_kogito_serialization_process_protobuf_AsyncEventNodeInstanceContent_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_AsyncEventNodeInstanceContent_descriptor,
        new java.lang.String[] { "JobId", });
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.getDescriptor();
    com.google.protobuf.AnyProto.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
