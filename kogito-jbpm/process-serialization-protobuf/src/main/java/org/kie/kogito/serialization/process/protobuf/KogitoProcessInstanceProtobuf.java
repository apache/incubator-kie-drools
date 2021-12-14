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

public final class KogitoProcessInstanceProtobuf {
  private KogitoProcessInstanceProtobuf() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface ProcessInstanceOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.ProcessInstance)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>string process_type = 1;</code>
     * @return The processType.
     */
    java.lang.String getProcessType();
    /**
     * <code>string process_type = 1;</code>
     * @return The bytes for processType.
     */
    com.google.protobuf.ByteString
        getProcessTypeBytes();

    /**
     * <code>string process_id = 2;</code>
     * @return The processId.
     */
    java.lang.String getProcessId();
    /**
     * <code>string process_id = 2;</code>
     * @return The bytes for processId.
     */
    com.google.protobuf.ByteString
        getProcessIdBytes();

    /**
     * <code>string id = 3;</code>
     * @return The id.
     */
    java.lang.String getId();
    /**
     * <code>string id = 3;</code>
     * @return The bytes for id.
     */
    com.google.protobuf.ByteString
        getIdBytes();

    /**
     * <code>optional string parent_process_instance_id = 4;</code>
     * @return Whether the parentProcessInstanceId field is set.
     */
    boolean hasParentProcessInstanceId();
    /**
     * <code>optional string parent_process_instance_id = 4;</code>
     * @return The parentProcessInstanceId.
     */
    java.lang.String getParentProcessInstanceId();
    /**
     * <code>optional string parent_process_instance_id = 4;</code>
     * @return The bytes for parentProcessInstanceId.
     */
    com.google.protobuf.ByteString
        getParentProcessInstanceIdBytes();

    /**
     * <code>optional string business_key = 5;</code>
     * @return Whether the businessKey field is set.
     */
    boolean hasBusinessKey();
    /**
     * <code>optional string business_key = 5;</code>
     * @return The businessKey.
     */
    java.lang.String getBusinessKey();
    /**
     * <code>optional string business_key = 5;</code>
     * @return The bytes for businessKey.
     */
    com.google.protobuf.ByteString
        getBusinessKeyBytes();

    /**
     * <code>optional string deploymentId = 6;</code>
     * @return Whether the deploymentId field is set.
     */
    boolean hasDeploymentId();
    /**
     * <code>optional string deploymentId = 6;</code>
     * @return The deploymentId.
     */
    java.lang.String getDeploymentId();
    /**
     * <code>optional string deploymentId = 6;</code>
     * @return The bytes for deploymentId.
     */
    com.google.protobuf.ByteString
        getDeploymentIdBytes();

    /**
     * <code>optional string description = 7;</code>
     * @return Whether the description field is set.
     */
    boolean hasDescription();
    /**
     * <code>optional string description = 7;</code>
     * @return The description.
     */
    java.lang.String getDescription();
    /**
     * <code>optional string description = 7;</code>
     * @return The bytes for description.
     */
    com.google.protobuf.ByteString
        getDescriptionBytes();

    /**
     * <code>int32 state = 8;</code>
     * @return The state.
     */
    int getState();

    /**
     * <code>optional int64 start_date = 9;</code>
     * @return Whether the startDate field is set.
     */
    boolean hasStartDate();
    /**
     * <code>optional int64 start_date = 9;</code>
     * @return The startDate.
     */
    long getStartDate();

    /**
     * <code>optional int64 node_instance_counter = 10;</code>
     * @return Whether the nodeInstanceCounter field is set.
     */
    boolean hasNodeInstanceCounter();
    /**
     * <code>optional int64 node_instance_counter = 10;</code>
     * @return The nodeInstanceCounter.
     */
    long getNodeInstanceCounter();

    /**
     * <code>bool signal_completion = 11;</code>
     * @return The signalCompletion.
     */
    boolean getSignalCompletion();

    /**
     * <code>optional string root_process_instance_id = 12;</code>
     * @return Whether the rootProcessInstanceId field is set.
     */
    boolean hasRootProcessInstanceId();
    /**
     * <code>optional string root_process_instance_id = 12;</code>
     * @return The rootProcessInstanceId.
     */
    java.lang.String getRootProcessInstanceId();
    /**
     * <code>optional string root_process_instance_id = 12;</code>
     * @return The bytes for rootProcessInstanceId.
     */
    com.google.protobuf.ByteString
        getRootProcessInstanceIdBytes();

    /**
     * <code>optional string root_process_id = 13;</code>
     * @return Whether the rootProcessId field is set.
     */
    boolean hasRootProcessId();
    /**
     * <code>optional string root_process_id = 13;</code>
     * @return The rootProcessId.
     */
    java.lang.String getRootProcessId();
    /**
     * <code>optional string root_process_id = 13;</code>
     * @return The bytes for rootProcessId.
     */
    com.google.protobuf.ByteString
        getRootProcessIdBytes();

    /**
     * <code>optional string error_node_id = 14;</code>
     * @return Whether the errorNodeId field is set.
     */
    boolean hasErrorNodeId();
    /**
     * <code>optional string error_node_id = 14;</code>
     * @return The errorNodeId.
     */
    java.lang.String getErrorNodeId();
    /**
     * <code>optional string error_node_id = 14;</code>
     * @return The bytes for errorNodeId.
     */
    com.google.protobuf.ByteString
        getErrorNodeIdBytes();

    /**
     * <code>optional string error_message = 15;</code>
     * @return Whether the errorMessage field is set.
     */
    boolean hasErrorMessage();
    /**
     * <code>optional string error_message = 15;</code>
     * @return The errorMessage.
     */
    java.lang.String getErrorMessage();
    /**
     * <code>optional string error_message = 15;</code>
     * @return The bytes for errorMessage.
     */
    com.google.protobuf.ByteString
        getErrorMessageBytes();

    /**
     * <code>optional string reference_id = 16;</code>
     * @return Whether the referenceId field is set.
     */
    boolean hasReferenceId();
    /**
     * <code>optional string reference_id = 16;</code>
     * @return The referenceId.
     */
    java.lang.String getReferenceId();
    /**
     * <code>optional string reference_id = 16;</code>
     * @return The bytes for referenceId.
     */
    com.google.protobuf.ByteString
        getReferenceIdBytes();

    /**
     * <code>optional .org.kie.kogito.serialization.process.protobuf.SLAContext sla = 17;</code>
     * @return Whether the sla field is set.
     */
    boolean hasSla();
    /**
     * <code>optional .org.kie.kogito.serialization.process.protobuf.SLAContext sla = 17;</code>
     * @return The sla.
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext getSla();
    /**
     * <code>optional .org.kie.kogito.serialization.process.protobuf.SLAContext sla = 17;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContextOrBuilder getSlaOrBuilder();

    /**
     * <code>optional .org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 18;</code>
     * @return Whether the context field is set.
     */
    boolean hasContext();
    /**
     * <code>optional .org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 18;</code>
     * @return The context.
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext getContext();
    /**
     * <code>optional .org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 18;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder getContextOrBuilder();

    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
     */
    java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext> 
        getSwimlaneContextList();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext getSwimlaneContext(int index);
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
     */
    int getSwimlaneContextCount();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
     */
    java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContextOrBuilder> 
        getSwimlaneContextOrBuilderList();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContextOrBuilder getSwimlaneContextOrBuilder(
        int index);

    /**
     * <code>repeated string completedNodeIds = 20;</code>
     * @return A list containing the completedNodeIds.
     */
    java.util.List<java.lang.String>
        getCompletedNodeIdsList();
    /**
     * <code>repeated string completedNodeIds = 20;</code>
     * @return The count of completedNodeIds.
     */
    int getCompletedNodeIdsCount();
    /**
     * <code>repeated string completedNodeIds = 20;</code>
     * @param index The index of the element to return.
     * @return The completedNodeIds at the given index.
     */
    java.lang.String getCompletedNodeIds(int index);
    /**
     * <code>repeated string completedNodeIds = 20;</code>
     * @param index The index of the value to return.
     * @return The bytes of the completedNodeIds at the given index.
     */
    com.google.protobuf.ByteString
        getCompletedNodeIdsBytes(int index);
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.ProcessInstance}
   */
  public static final class ProcessInstance extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.ProcessInstance)
      ProcessInstanceOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use ProcessInstance.newBuilder() to construct.
    private ProcessInstance(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private ProcessInstance() {
      processType_ = "";
      processId_ = "";
      id_ = "";
      parentProcessInstanceId_ = "";
      businessKey_ = "";
      deploymentId_ = "";
      description_ = "";
      rootProcessInstanceId_ = "";
      rootProcessId_ = "";
      errorNodeId_ = "";
      errorMessage_ = "";
      referenceId_ = "";
      swimlaneContext_ = java.util.Collections.emptyList();
      completedNodeIds_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new ProcessInstance();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private ProcessInstance(
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

              processType_ = s;
              break;
            }
            case 18: {
              java.lang.String s = input.readStringRequireUtf8();

              processId_ = s;
              break;
            }
            case 26: {
              java.lang.String s = input.readStringRequireUtf8();

              id_ = s;
              break;
            }
            case 34: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000001;
              parentProcessInstanceId_ = s;
              break;
            }
            case 42: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000002;
              businessKey_ = s;
              break;
            }
            case 50: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000004;
              deploymentId_ = s;
              break;
            }
            case 58: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000008;
              description_ = s;
              break;
            }
            case 64: {

              state_ = input.readInt32();
              break;
            }
            case 72: {
              bitField0_ |= 0x00000010;
              startDate_ = input.readInt64();
              break;
            }
            case 80: {
              bitField0_ |= 0x00000020;
              nodeInstanceCounter_ = input.readInt64();
              break;
            }
            case 88: {

              signalCompletion_ = input.readBool();
              break;
            }
            case 98: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000040;
              rootProcessInstanceId_ = s;
              break;
            }
            case 106: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000080;
              rootProcessId_ = s;
              break;
            }
            case 114: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000100;
              errorNodeId_ = s;
              break;
            }
            case 122: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000200;
              errorMessage_ = s;
              break;
            }
            case 130: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000400;
              referenceId_ = s;
              break;
            }
            case 138: {
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.Builder subBuilder = null;
              if (((bitField0_ & 0x00000800) != 0)) {
                subBuilder = sla_.toBuilder();
              }
              sla_ = input.readMessage(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(sla_);
                sla_ = subBuilder.buildPartial();
              }
              bitField0_ |= 0x00000800;
              break;
            }
            case 146: {
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder subBuilder = null;
              if (((bitField0_ & 0x00001000) != 0)) {
                subBuilder = context_.toBuilder();
              }
              context_ = input.readMessage(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(context_);
                context_ = subBuilder.buildPartial();
              }
              bitField0_ |= 0x00001000;
              break;
            }
            case 154: {
              if (!((mutable_bitField0_ & 0x00002000) != 0)) {
                swimlaneContext_ = new java.util.ArrayList<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext>();
                mutable_bitField0_ |= 0x00002000;
              }
              swimlaneContext_.add(
                  input.readMessage(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext.parser(), extensionRegistry));
              break;
            }
            case 162: {
              java.lang.String s = input.readStringRequireUtf8();
              if (!((mutable_bitField0_ & 0x00004000) != 0)) {
                completedNodeIds_ = new com.google.protobuf.LazyStringArrayList();
                mutable_bitField0_ |= 0x00004000;
              }
              completedNodeIds_.add(s);
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
        if (((mutable_bitField0_ & 0x00002000) != 0)) {
          swimlaneContext_ = java.util.Collections.unmodifiableList(swimlaneContext_);
        }
        if (((mutable_bitField0_ & 0x00004000) != 0)) {
          completedNodeIds_ = completedNodeIds_.getUnmodifiableView();
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_ProcessInstance_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_ProcessInstance_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance.class, org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance.Builder.class);
    }

    private int bitField0_;
    public static final int PROCESS_TYPE_FIELD_NUMBER = 1;
    private volatile java.lang.Object processType_;
    /**
     * <code>string process_type = 1;</code>
     * @return The processType.
     */
    @java.lang.Override
    public java.lang.String getProcessType() {
      java.lang.Object ref = processType_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        processType_ = s;
        return s;
      }
    }
    /**
     * <code>string process_type = 1;</code>
     * @return The bytes for processType.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getProcessTypeBytes() {
      java.lang.Object ref = processType_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        processType_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int PROCESS_ID_FIELD_NUMBER = 2;
    private volatile java.lang.Object processId_;
    /**
     * <code>string process_id = 2;</code>
     * @return The processId.
     */
    @java.lang.Override
    public java.lang.String getProcessId() {
      java.lang.Object ref = processId_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        processId_ = s;
        return s;
      }
    }
    /**
     * <code>string process_id = 2;</code>
     * @return The bytes for processId.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getProcessIdBytes() {
      java.lang.Object ref = processId_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        processId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int ID_FIELD_NUMBER = 3;
    private volatile java.lang.Object id_;
    /**
     * <code>string id = 3;</code>
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
     * <code>string id = 3;</code>
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

    public static final int PARENT_PROCESS_INSTANCE_ID_FIELD_NUMBER = 4;
    private volatile java.lang.Object parentProcessInstanceId_;
    /**
     * <code>optional string parent_process_instance_id = 4;</code>
     * @return Whether the parentProcessInstanceId field is set.
     */
    @java.lang.Override
    public boolean hasParentProcessInstanceId() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>optional string parent_process_instance_id = 4;</code>
     * @return The parentProcessInstanceId.
     */
    @java.lang.Override
    public java.lang.String getParentProcessInstanceId() {
      java.lang.Object ref = parentProcessInstanceId_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        parentProcessInstanceId_ = s;
        return s;
      }
    }
    /**
     * <code>optional string parent_process_instance_id = 4;</code>
     * @return The bytes for parentProcessInstanceId.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getParentProcessInstanceIdBytes() {
      java.lang.Object ref = parentProcessInstanceId_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        parentProcessInstanceId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int BUSINESS_KEY_FIELD_NUMBER = 5;
    private volatile java.lang.Object businessKey_;
    /**
     * <code>optional string business_key = 5;</code>
     * @return Whether the businessKey field is set.
     */
    @java.lang.Override
    public boolean hasBusinessKey() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>optional string business_key = 5;</code>
     * @return The businessKey.
     */
    @java.lang.Override
    public java.lang.String getBusinessKey() {
      java.lang.Object ref = businessKey_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        businessKey_ = s;
        return s;
      }
    }
    /**
     * <code>optional string business_key = 5;</code>
     * @return The bytes for businessKey.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getBusinessKeyBytes() {
      java.lang.Object ref = businessKey_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        businessKey_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int DEPLOYMENTID_FIELD_NUMBER = 6;
    private volatile java.lang.Object deploymentId_;
    /**
     * <code>optional string deploymentId = 6;</code>
     * @return Whether the deploymentId field is set.
     */
    @java.lang.Override
    public boolean hasDeploymentId() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     * <code>optional string deploymentId = 6;</code>
     * @return The deploymentId.
     */
    @java.lang.Override
    public java.lang.String getDeploymentId() {
      java.lang.Object ref = deploymentId_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        deploymentId_ = s;
        return s;
      }
    }
    /**
     * <code>optional string deploymentId = 6;</code>
     * @return The bytes for deploymentId.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getDeploymentIdBytes() {
      java.lang.Object ref = deploymentId_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        deploymentId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int DESCRIPTION_FIELD_NUMBER = 7;
    private volatile java.lang.Object description_;
    /**
     * <code>optional string description = 7;</code>
     * @return Whether the description field is set.
     */
    @java.lang.Override
    public boolean hasDescription() {
      return ((bitField0_ & 0x00000008) != 0);
    }
    /**
     * <code>optional string description = 7;</code>
     * @return The description.
     */
    @java.lang.Override
    public java.lang.String getDescription() {
      java.lang.Object ref = description_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        description_ = s;
        return s;
      }
    }
    /**
     * <code>optional string description = 7;</code>
     * @return The bytes for description.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getDescriptionBytes() {
      java.lang.Object ref = description_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        description_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int STATE_FIELD_NUMBER = 8;
    private int state_;
    /**
     * <code>int32 state = 8;</code>
     * @return The state.
     */
    @java.lang.Override
    public int getState() {
      return state_;
    }

    public static final int START_DATE_FIELD_NUMBER = 9;
    private long startDate_;
    /**
     * <code>optional int64 start_date = 9;</code>
     * @return Whether the startDate field is set.
     */
    @java.lang.Override
    public boolean hasStartDate() {
      return ((bitField0_ & 0x00000010) != 0);
    }
    /**
     * <code>optional int64 start_date = 9;</code>
     * @return The startDate.
     */
    @java.lang.Override
    public long getStartDate() {
      return startDate_;
    }

    public static final int NODE_INSTANCE_COUNTER_FIELD_NUMBER = 10;
    private long nodeInstanceCounter_;
    /**
     * <code>optional int64 node_instance_counter = 10;</code>
     * @return Whether the nodeInstanceCounter field is set.
     */
    @java.lang.Override
    public boolean hasNodeInstanceCounter() {
      return ((bitField0_ & 0x00000020) != 0);
    }
    /**
     * <code>optional int64 node_instance_counter = 10;</code>
     * @return The nodeInstanceCounter.
     */
    @java.lang.Override
    public long getNodeInstanceCounter() {
      return nodeInstanceCounter_;
    }

    public static final int SIGNAL_COMPLETION_FIELD_NUMBER = 11;
    private boolean signalCompletion_;
    /**
     * <code>bool signal_completion = 11;</code>
     * @return The signalCompletion.
     */
    @java.lang.Override
    public boolean getSignalCompletion() {
      return signalCompletion_;
    }

    public static final int ROOT_PROCESS_INSTANCE_ID_FIELD_NUMBER = 12;
    private volatile java.lang.Object rootProcessInstanceId_;
    /**
     * <code>optional string root_process_instance_id = 12;</code>
     * @return Whether the rootProcessInstanceId field is set.
     */
    @java.lang.Override
    public boolean hasRootProcessInstanceId() {
      return ((bitField0_ & 0x00000040) != 0);
    }
    /**
     * <code>optional string root_process_instance_id = 12;</code>
     * @return The rootProcessInstanceId.
     */
    @java.lang.Override
    public java.lang.String getRootProcessInstanceId() {
      java.lang.Object ref = rootProcessInstanceId_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        rootProcessInstanceId_ = s;
        return s;
      }
    }
    /**
     * <code>optional string root_process_instance_id = 12;</code>
     * @return The bytes for rootProcessInstanceId.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getRootProcessInstanceIdBytes() {
      java.lang.Object ref = rootProcessInstanceId_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        rootProcessInstanceId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int ROOT_PROCESS_ID_FIELD_NUMBER = 13;
    private volatile java.lang.Object rootProcessId_;
    /**
     * <code>optional string root_process_id = 13;</code>
     * @return Whether the rootProcessId field is set.
     */
    @java.lang.Override
    public boolean hasRootProcessId() {
      return ((bitField0_ & 0x00000080) != 0);
    }
    /**
     * <code>optional string root_process_id = 13;</code>
     * @return The rootProcessId.
     */
    @java.lang.Override
    public java.lang.String getRootProcessId() {
      java.lang.Object ref = rootProcessId_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        rootProcessId_ = s;
        return s;
      }
    }
    /**
     * <code>optional string root_process_id = 13;</code>
     * @return The bytes for rootProcessId.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getRootProcessIdBytes() {
      java.lang.Object ref = rootProcessId_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        rootProcessId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int ERROR_NODE_ID_FIELD_NUMBER = 14;
    private volatile java.lang.Object errorNodeId_;
    /**
     * <code>optional string error_node_id = 14;</code>
     * @return Whether the errorNodeId field is set.
     */
    @java.lang.Override
    public boolean hasErrorNodeId() {
      return ((bitField0_ & 0x00000100) != 0);
    }
    /**
     * <code>optional string error_node_id = 14;</code>
     * @return The errorNodeId.
     */
    @java.lang.Override
    public java.lang.String getErrorNodeId() {
      java.lang.Object ref = errorNodeId_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        errorNodeId_ = s;
        return s;
      }
    }
    /**
     * <code>optional string error_node_id = 14;</code>
     * @return The bytes for errorNodeId.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getErrorNodeIdBytes() {
      java.lang.Object ref = errorNodeId_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        errorNodeId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int ERROR_MESSAGE_FIELD_NUMBER = 15;
    private volatile java.lang.Object errorMessage_;
    /**
     * <code>optional string error_message = 15;</code>
     * @return Whether the errorMessage field is set.
     */
    @java.lang.Override
    public boolean hasErrorMessage() {
      return ((bitField0_ & 0x00000200) != 0);
    }
    /**
     * <code>optional string error_message = 15;</code>
     * @return The errorMessage.
     */
    @java.lang.Override
    public java.lang.String getErrorMessage() {
      java.lang.Object ref = errorMessage_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        errorMessage_ = s;
        return s;
      }
    }
    /**
     * <code>optional string error_message = 15;</code>
     * @return The bytes for errorMessage.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getErrorMessageBytes() {
      java.lang.Object ref = errorMessage_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        errorMessage_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int REFERENCE_ID_FIELD_NUMBER = 16;
    private volatile java.lang.Object referenceId_;
    /**
     * <code>optional string reference_id = 16;</code>
     * @return Whether the referenceId field is set.
     */
    @java.lang.Override
    public boolean hasReferenceId() {
      return ((bitField0_ & 0x00000400) != 0);
    }
    /**
     * <code>optional string reference_id = 16;</code>
     * @return The referenceId.
     */
    @java.lang.Override
    public java.lang.String getReferenceId() {
      java.lang.Object ref = referenceId_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        referenceId_ = s;
        return s;
      }
    }
    /**
     * <code>optional string reference_id = 16;</code>
     * @return The bytes for referenceId.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getReferenceIdBytes() {
      java.lang.Object ref = referenceId_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        referenceId_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int SLA_FIELD_NUMBER = 17;
    private org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext sla_;
    /**
     * <code>optional .org.kie.kogito.serialization.process.protobuf.SLAContext sla = 17;</code>
     * @return Whether the sla field is set.
     */
    @java.lang.Override
    public boolean hasSla() {
      return ((bitField0_ & 0x00000800) != 0);
    }
    /**
     * <code>optional .org.kie.kogito.serialization.process.protobuf.SLAContext sla = 17;</code>
     * @return The sla.
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext getSla() {
      return sla_ == null ? org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.getDefaultInstance() : sla_;
    }
    /**
     * <code>optional .org.kie.kogito.serialization.process.protobuf.SLAContext sla = 17;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContextOrBuilder getSlaOrBuilder() {
      return sla_ == null ? org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.getDefaultInstance() : sla_;
    }

    public static final int CONTEXT_FIELD_NUMBER = 18;
    private org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext context_;
    /**
     * <code>optional .org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 18;</code>
     * @return Whether the context field is set.
     */
    @java.lang.Override
    public boolean hasContext() {
      return ((bitField0_ & 0x00001000) != 0);
    }
    /**
     * <code>optional .org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 18;</code>
     * @return The context.
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext getContext() {
      return context_ == null ? org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.getDefaultInstance() : context_;
    }
    /**
     * <code>optional .org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 18;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder getContextOrBuilder() {
      return context_ == null ? org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.getDefaultInstance() : context_;
    }

    public static final int SWIMLANE_CONTEXT_FIELD_NUMBER = 19;
    private java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext> swimlaneContext_;
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
     */
    @java.lang.Override
    public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext> getSwimlaneContextList() {
      return swimlaneContext_;
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
     */
    @java.lang.Override
    public java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContextOrBuilder> 
        getSwimlaneContextOrBuilderList() {
      return swimlaneContext_;
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
     */
    @java.lang.Override
    public int getSwimlaneContextCount() {
      return swimlaneContext_.size();
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext getSwimlaneContext(int index) {
      return swimlaneContext_.get(index);
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContextOrBuilder getSwimlaneContextOrBuilder(
        int index) {
      return swimlaneContext_.get(index);
    }

    public static final int COMPLETEDNODEIDS_FIELD_NUMBER = 20;
    private com.google.protobuf.LazyStringList completedNodeIds_;
    /**
     * <code>repeated string completedNodeIds = 20;</code>
     * @return A list containing the completedNodeIds.
     */
    public com.google.protobuf.ProtocolStringList
        getCompletedNodeIdsList() {
      return completedNodeIds_;
    }
    /**
     * <code>repeated string completedNodeIds = 20;</code>
     * @return The count of completedNodeIds.
     */
    public int getCompletedNodeIdsCount() {
      return completedNodeIds_.size();
    }
    /**
     * <code>repeated string completedNodeIds = 20;</code>
     * @param index The index of the element to return.
     * @return The completedNodeIds at the given index.
     */
    public java.lang.String getCompletedNodeIds(int index) {
      return completedNodeIds_.get(index);
    }
    /**
     * <code>repeated string completedNodeIds = 20;</code>
     * @param index The index of the value to return.
     * @return The bytes of the completedNodeIds at the given index.
     */
    public com.google.protobuf.ByteString
        getCompletedNodeIdsBytes(int index) {
      return completedNodeIds_.getByteString(index);
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
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(processType_)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, processType_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(processId_)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, processId_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(id_)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 3, id_);
      }
      if (((bitField0_ & 0x00000001) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 4, parentProcessInstanceId_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 5, businessKey_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 6, deploymentId_);
      }
      if (((bitField0_ & 0x00000008) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 7, description_);
      }
      if (state_ != 0) {
        output.writeInt32(8, state_);
      }
      if (((bitField0_ & 0x00000010) != 0)) {
        output.writeInt64(9, startDate_);
      }
      if (((bitField0_ & 0x00000020) != 0)) {
        output.writeInt64(10, nodeInstanceCounter_);
      }
      if (signalCompletion_ != false) {
        output.writeBool(11, signalCompletion_);
      }
      if (((bitField0_ & 0x00000040) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 12, rootProcessInstanceId_);
      }
      if (((bitField0_ & 0x00000080) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 13, rootProcessId_);
      }
      if (((bitField0_ & 0x00000100) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 14, errorNodeId_);
      }
      if (((bitField0_ & 0x00000200) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 15, errorMessage_);
      }
      if (((bitField0_ & 0x00000400) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 16, referenceId_);
      }
      if (((bitField0_ & 0x00000800) != 0)) {
        output.writeMessage(17, getSla());
      }
      if (((bitField0_ & 0x00001000) != 0)) {
        output.writeMessage(18, getContext());
      }
      for (int i = 0; i < swimlaneContext_.size(); i++) {
        output.writeMessage(19, swimlaneContext_.get(i));
      }
      for (int i = 0; i < completedNodeIds_.size(); i++) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 20, completedNodeIds_.getRaw(i));
      }
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(processType_)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, processType_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(processId_)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, processId_);
      }
      if (!com.google.protobuf.GeneratedMessageV3.isStringEmpty(id_)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, id_);
      }
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(4, parentProcessInstanceId_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(5, businessKey_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(6, deploymentId_);
      }
      if (((bitField0_ & 0x00000008) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(7, description_);
      }
      if (state_ != 0) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(8, state_);
      }
      if (((bitField0_ & 0x00000010) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(9, startDate_);
      }
      if (((bitField0_ & 0x00000020) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(10, nodeInstanceCounter_);
      }
      if (signalCompletion_ != false) {
        size += com.google.protobuf.CodedOutputStream
          .computeBoolSize(11, signalCompletion_);
      }
      if (((bitField0_ & 0x00000040) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(12, rootProcessInstanceId_);
      }
      if (((bitField0_ & 0x00000080) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(13, rootProcessId_);
      }
      if (((bitField0_ & 0x00000100) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(14, errorNodeId_);
      }
      if (((bitField0_ & 0x00000200) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(15, errorMessage_);
      }
      if (((bitField0_ & 0x00000400) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(16, referenceId_);
      }
      if (((bitField0_ & 0x00000800) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(17, getSla());
      }
      if (((bitField0_ & 0x00001000) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(18, getContext());
      }
      for (int i = 0; i < swimlaneContext_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(19, swimlaneContext_.get(i));
      }
      {
        int dataSize = 0;
        for (int i = 0; i < completedNodeIds_.size(); i++) {
          dataSize += computeStringSizeNoTag(completedNodeIds_.getRaw(i));
        }
        size += dataSize;
        size += 2 * getCompletedNodeIdsList().size();
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
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance other = (org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance) obj;

      if (!getProcessType()
          .equals(other.getProcessType())) return false;
      if (!getProcessId()
          .equals(other.getProcessId())) return false;
      if (!getId()
          .equals(other.getId())) return false;
      if (hasParentProcessInstanceId() != other.hasParentProcessInstanceId()) return false;
      if (hasParentProcessInstanceId()) {
        if (!getParentProcessInstanceId()
            .equals(other.getParentProcessInstanceId())) return false;
      }
      if (hasBusinessKey() != other.hasBusinessKey()) return false;
      if (hasBusinessKey()) {
        if (!getBusinessKey()
            .equals(other.getBusinessKey())) return false;
      }
      if (hasDeploymentId() != other.hasDeploymentId()) return false;
      if (hasDeploymentId()) {
        if (!getDeploymentId()
            .equals(other.getDeploymentId())) return false;
      }
      if (hasDescription() != other.hasDescription()) return false;
      if (hasDescription()) {
        if (!getDescription()
            .equals(other.getDescription())) return false;
      }
      if (getState()
          != other.getState()) return false;
      if (hasStartDate() != other.hasStartDate()) return false;
      if (hasStartDate()) {
        if (getStartDate()
            != other.getStartDate()) return false;
      }
      if (hasNodeInstanceCounter() != other.hasNodeInstanceCounter()) return false;
      if (hasNodeInstanceCounter()) {
        if (getNodeInstanceCounter()
            != other.getNodeInstanceCounter()) return false;
      }
      if (getSignalCompletion()
          != other.getSignalCompletion()) return false;
      if (hasRootProcessInstanceId() != other.hasRootProcessInstanceId()) return false;
      if (hasRootProcessInstanceId()) {
        if (!getRootProcessInstanceId()
            .equals(other.getRootProcessInstanceId())) return false;
      }
      if (hasRootProcessId() != other.hasRootProcessId()) return false;
      if (hasRootProcessId()) {
        if (!getRootProcessId()
            .equals(other.getRootProcessId())) return false;
      }
      if (hasErrorNodeId() != other.hasErrorNodeId()) return false;
      if (hasErrorNodeId()) {
        if (!getErrorNodeId()
            .equals(other.getErrorNodeId())) return false;
      }
      if (hasErrorMessage() != other.hasErrorMessage()) return false;
      if (hasErrorMessage()) {
        if (!getErrorMessage()
            .equals(other.getErrorMessage())) return false;
      }
      if (hasReferenceId() != other.hasReferenceId()) return false;
      if (hasReferenceId()) {
        if (!getReferenceId()
            .equals(other.getReferenceId())) return false;
      }
      if (hasSla() != other.hasSla()) return false;
      if (hasSla()) {
        if (!getSla()
            .equals(other.getSla())) return false;
      }
      if (hasContext() != other.hasContext()) return false;
      if (hasContext()) {
        if (!getContext()
            .equals(other.getContext())) return false;
      }
      if (!getSwimlaneContextList()
          .equals(other.getSwimlaneContextList())) return false;
      if (!getCompletedNodeIdsList()
          .equals(other.getCompletedNodeIdsList())) return false;
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
      hash = (37 * hash) + PROCESS_TYPE_FIELD_NUMBER;
      hash = (53 * hash) + getProcessType().hashCode();
      hash = (37 * hash) + PROCESS_ID_FIELD_NUMBER;
      hash = (53 * hash) + getProcessId().hashCode();
      hash = (37 * hash) + ID_FIELD_NUMBER;
      hash = (53 * hash) + getId().hashCode();
      if (hasParentProcessInstanceId()) {
        hash = (37 * hash) + PARENT_PROCESS_INSTANCE_ID_FIELD_NUMBER;
        hash = (53 * hash) + getParentProcessInstanceId().hashCode();
      }
      if (hasBusinessKey()) {
        hash = (37 * hash) + BUSINESS_KEY_FIELD_NUMBER;
        hash = (53 * hash) + getBusinessKey().hashCode();
      }
      if (hasDeploymentId()) {
        hash = (37 * hash) + DEPLOYMENTID_FIELD_NUMBER;
        hash = (53 * hash) + getDeploymentId().hashCode();
      }
      if (hasDescription()) {
        hash = (37 * hash) + DESCRIPTION_FIELD_NUMBER;
        hash = (53 * hash) + getDescription().hashCode();
      }
      hash = (37 * hash) + STATE_FIELD_NUMBER;
      hash = (53 * hash) + getState();
      if (hasStartDate()) {
        hash = (37 * hash) + START_DATE_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
            getStartDate());
      }
      if (hasNodeInstanceCounter()) {
        hash = (37 * hash) + NODE_INSTANCE_COUNTER_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
            getNodeInstanceCounter());
      }
      hash = (37 * hash) + SIGNAL_COMPLETION_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashBoolean(
          getSignalCompletion());
      if (hasRootProcessInstanceId()) {
        hash = (37 * hash) + ROOT_PROCESS_INSTANCE_ID_FIELD_NUMBER;
        hash = (53 * hash) + getRootProcessInstanceId().hashCode();
      }
      if (hasRootProcessId()) {
        hash = (37 * hash) + ROOT_PROCESS_ID_FIELD_NUMBER;
        hash = (53 * hash) + getRootProcessId().hashCode();
      }
      if (hasErrorNodeId()) {
        hash = (37 * hash) + ERROR_NODE_ID_FIELD_NUMBER;
        hash = (53 * hash) + getErrorNodeId().hashCode();
      }
      if (hasErrorMessage()) {
        hash = (37 * hash) + ERROR_MESSAGE_FIELD_NUMBER;
        hash = (53 * hash) + getErrorMessage().hashCode();
      }
      if (hasReferenceId()) {
        hash = (37 * hash) + REFERENCE_ID_FIELD_NUMBER;
        hash = (53 * hash) + getReferenceId().hashCode();
      }
      if (hasSla()) {
        hash = (37 * hash) + SLA_FIELD_NUMBER;
        hash = (53 * hash) + getSla().hashCode();
      }
      if (hasContext()) {
        hash = (37 * hash) + CONTEXT_FIELD_NUMBER;
        hash = (53 * hash) + getContext().hashCode();
      }
      if (getSwimlaneContextCount() > 0) {
        hash = (37 * hash) + SWIMLANE_CONTEXT_FIELD_NUMBER;
        hash = (53 * hash) + getSwimlaneContextList().hashCode();
      }
      if (getCompletedNodeIdsCount() > 0) {
        hash = (37 * hash) + COMPLETEDNODEIDS_FIELD_NUMBER;
        hash = (53 * hash) + getCompletedNodeIdsList().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance parseFrom(
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
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance prototype) {
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
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.ProcessInstance}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.ProcessInstance)
        org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstanceOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_ProcessInstance_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_ProcessInstance_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance.class, org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance.newBuilder()
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
          getContextFieldBuilder();
          getSwimlaneContextFieldBuilder();
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        processType_ = "";

        processId_ = "";

        id_ = "";

        parentProcessInstanceId_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        businessKey_ = "";
        bitField0_ = (bitField0_ & ~0x00000002);
        deploymentId_ = "";
        bitField0_ = (bitField0_ & ~0x00000004);
        description_ = "";
        bitField0_ = (bitField0_ & ~0x00000008);
        state_ = 0;

        startDate_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000010);
        nodeInstanceCounter_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000020);
        signalCompletion_ = false;

        rootProcessInstanceId_ = "";
        bitField0_ = (bitField0_ & ~0x00000040);
        rootProcessId_ = "";
        bitField0_ = (bitField0_ & ~0x00000080);
        errorNodeId_ = "";
        bitField0_ = (bitField0_ & ~0x00000100);
        errorMessage_ = "";
        bitField0_ = (bitField0_ & ~0x00000200);
        referenceId_ = "";
        bitField0_ = (bitField0_ & ~0x00000400);
        if (slaBuilder_ == null) {
          sla_ = null;
        } else {
          slaBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000800);
        if (contextBuilder_ == null) {
          context_ = null;
        } else {
          contextBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00001000);
        if (swimlaneContextBuilder_ == null) {
          swimlaneContext_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00002000);
        } else {
          swimlaneContextBuilder_.clear();
        }
        completedNodeIds_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00004000);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_ProcessInstance_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance build() {
        org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance result = new org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        result.processType_ = processType_;
        result.processId_ = processId_;
        result.id_ = id_;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          to_bitField0_ |= 0x00000001;
        }
        result.parentProcessInstanceId_ = parentProcessInstanceId_;
        if (((from_bitField0_ & 0x00000002) != 0)) {
          to_bitField0_ |= 0x00000002;
        }
        result.businessKey_ = businessKey_;
        if (((from_bitField0_ & 0x00000004) != 0)) {
          to_bitField0_ |= 0x00000004;
        }
        result.deploymentId_ = deploymentId_;
        if (((from_bitField0_ & 0x00000008) != 0)) {
          to_bitField0_ |= 0x00000008;
        }
        result.description_ = description_;
        result.state_ = state_;
        if (((from_bitField0_ & 0x00000010) != 0)) {
          result.startDate_ = startDate_;
          to_bitField0_ |= 0x00000010;
        }
        if (((from_bitField0_ & 0x00000020) != 0)) {
          result.nodeInstanceCounter_ = nodeInstanceCounter_;
          to_bitField0_ |= 0x00000020;
        }
        result.signalCompletion_ = signalCompletion_;
        if (((from_bitField0_ & 0x00000040) != 0)) {
          to_bitField0_ |= 0x00000040;
        }
        result.rootProcessInstanceId_ = rootProcessInstanceId_;
        if (((from_bitField0_ & 0x00000080) != 0)) {
          to_bitField0_ |= 0x00000080;
        }
        result.rootProcessId_ = rootProcessId_;
        if (((from_bitField0_ & 0x00000100) != 0)) {
          to_bitField0_ |= 0x00000100;
        }
        result.errorNodeId_ = errorNodeId_;
        if (((from_bitField0_ & 0x00000200) != 0)) {
          to_bitField0_ |= 0x00000200;
        }
        result.errorMessage_ = errorMessage_;
        if (((from_bitField0_ & 0x00000400) != 0)) {
          to_bitField0_ |= 0x00000400;
        }
        result.referenceId_ = referenceId_;
        if (((from_bitField0_ & 0x00000800) != 0)) {
          if (slaBuilder_ == null) {
            result.sla_ = sla_;
          } else {
            result.sla_ = slaBuilder_.build();
          }
          to_bitField0_ |= 0x00000800;
        }
        if (((from_bitField0_ & 0x00001000) != 0)) {
          if (contextBuilder_ == null) {
            result.context_ = context_;
          } else {
            result.context_ = contextBuilder_.build();
          }
          to_bitField0_ |= 0x00001000;
        }
        if (swimlaneContextBuilder_ == null) {
          if (((bitField0_ & 0x00002000) != 0)) {
            swimlaneContext_ = java.util.Collections.unmodifiableList(swimlaneContext_);
            bitField0_ = (bitField0_ & ~0x00002000);
          }
          result.swimlaneContext_ = swimlaneContext_;
        } else {
          result.swimlaneContext_ = swimlaneContextBuilder_.build();
        }
        if (((bitField0_ & 0x00004000) != 0)) {
          completedNodeIds_ = completedNodeIds_.getUnmodifiableView();
          bitField0_ = (bitField0_ & ~0x00004000);
        }
        result.completedNodeIds_ = completedNodeIds_;
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
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance.getDefaultInstance()) return this;
        if (!other.getProcessType().isEmpty()) {
          processType_ = other.processType_;
          onChanged();
        }
        if (!other.getProcessId().isEmpty()) {
          processId_ = other.processId_;
          onChanged();
        }
        if (!other.getId().isEmpty()) {
          id_ = other.id_;
          onChanged();
        }
        if (other.hasParentProcessInstanceId()) {
          bitField0_ |= 0x00000001;
          parentProcessInstanceId_ = other.parentProcessInstanceId_;
          onChanged();
        }
        if (other.hasBusinessKey()) {
          bitField0_ |= 0x00000002;
          businessKey_ = other.businessKey_;
          onChanged();
        }
        if (other.hasDeploymentId()) {
          bitField0_ |= 0x00000004;
          deploymentId_ = other.deploymentId_;
          onChanged();
        }
        if (other.hasDescription()) {
          bitField0_ |= 0x00000008;
          description_ = other.description_;
          onChanged();
        }
        if (other.getState() != 0) {
          setState(other.getState());
        }
        if (other.hasStartDate()) {
          setStartDate(other.getStartDate());
        }
        if (other.hasNodeInstanceCounter()) {
          setNodeInstanceCounter(other.getNodeInstanceCounter());
        }
        if (other.getSignalCompletion() != false) {
          setSignalCompletion(other.getSignalCompletion());
        }
        if (other.hasRootProcessInstanceId()) {
          bitField0_ |= 0x00000040;
          rootProcessInstanceId_ = other.rootProcessInstanceId_;
          onChanged();
        }
        if (other.hasRootProcessId()) {
          bitField0_ |= 0x00000080;
          rootProcessId_ = other.rootProcessId_;
          onChanged();
        }
        if (other.hasErrorNodeId()) {
          bitField0_ |= 0x00000100;
          errorNodeId_ = other.errorNodeId_;
          onChanged();
        }
        if (other.hasErrorMessage()) {
          bitField0_ |= 0x00000200;
          errorMessage_ = other.errorMessage_;
          onChanged();
        }
        if (other.hasReferenceId()) {
          bitField0_ |= 0x00000400;
          referenceId_ = other.referenceId_;
          onChanged();
        }
        if (other.hasSla()) {
          mergeSla(other.getSla());
        }
        if (other.hasContext()) {
          mergeContext(other.getContext());
        }
        if (swimlaneContextBuilder_ == null) {
          if (!other.swimlaneContext_.isEmpty()) {
            if (swimlaneContext_.isEmpty()) {
              swimlaneContext_ = other.swimlaneContext_;
              bitField0_ = (bitField0_ & ~0x00002000);
            } else {
              ensureSwimlaneContextIsMutable();
              swimlaneContext_.addAll(other.swimlaneContext_);
            }
            onChanged();
          }
        } else {
          if (!other.swimlaneContext_.isEmpty()) {
            if (swimlaneContextBuilder_.isEmpty()) {
              swimlaneContextBuilder_.dispose();
              swimlaneContextBuilder_ = null;
              swimlaneContext_ = other.swimlaneContext_;
              bitField0_ = (bitField0_ & ~0x00002000);
              swimlaneContextBuilder_ = 
                com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                   getSwimlaneContextFieldBuilder() : null;
            } else {
              swimlaneContextBuilder_.addAllMessages(other.swimlaneContext_);
            }
          }
        }
        if (!other.completedNodeIds_.isEmpty()) {
          if (completedNodeIds_.isEmpty()) {
            completedNodeIds_ = other.completedNodeIds_;
            bitField0_ = (bitField0_ & ~0x00004000);
          } else {
            ensureCompletedNodeIdsIsMutable();
            completedNodeIds_.addAll(other.completedNodeIds_);
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
        org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.lang.Object processType_ = "";
      /**
       * <code>string process_type = 1;</code>
       * @return The processType.
       */
      public java.lang.String getProcessType() {
        java.lang.Object ref = processType_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          processType_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string process_type = 1;</code>
       * @return The bytes for processType.
       */
      public com.google.protobuf.ByteString
          getProcessTypeBytes() {
        java.lang.Object ref = processType_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          processType_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string process_type = 1;</code>
       * @param value The processType to set.
       * @return This builder for chaining.
       */
      public Builder setProcessType(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        processType_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string process_type = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearProcessType() {
        
        processType_ = getDefaultInstance().getProcessType();
        onChanged();
        return this;
      }
      /**
       * <code>string process_type = 1;</code>
       * @param value The bytes for processType to set.
       * @return This builder for chaining.
       */
      public Builder setProcessTypeBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        processType_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object processId_ = "";
      /**
       * <code>string process_id = 2;</code>
       * @return The processId.
       */
      public java.lang.String getProcessId() {
        java.lang.Object ref = processId_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          processId_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string process_id = 2;</code>
       * @return The bytes for processId.
       */
      public com.google.protobuf.ByteString
          getProcessIdBytes() {
        java.lang.Object ref = processId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          processId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string process_id = 2;</code>
       * @param value The processId to set.
       * @return This builder for chaining.
       */
      public Builder setProcessId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        processId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string process_id = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearProcessId() {
        
        processId_ = getDefaultInstance().getProcessId();
        onChanged();
        return this;
      }
      /**
       * <code>string process_id = 2;</code>
       * @param value The bytes for processId to set.
       * @return This builder for chaining.
       */
      public Builder setProcessIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        processId_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object id_ = "";
      /**
       * <code>string id = 3;</code>
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
       * <code>string id = 3;</code>
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
       * <code>string id = 3;</code>
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
       * <code>string id = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearId() {
        
        id_ = getDefaultInstance().getId();
        onChanged();
        return this;
      }
      /**
       * <code>string id = 3;</code>
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

      private java.lang.Object parentProcessInstanceId_ = "";
      /**
       * <code>optional string parent_process_instance_id = 4;</code>
       * @return Whether the parentProcessInstanceId field is set.
       */
      public boolean hasParentProcessInstanceId() {
        return ((bitField0_ & 0x00000001) != 0);
      }
      /**
       * <code>optional string parent_process_instance_id = 4;</code>
       * @return The parentProcessInstanceId.
       */
      public java.lang.String getParentProcessInstanceId() {
        java.lang.Object ref = parentProcessInstanceId_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          parentProcessInstanceId_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>optional string parent_process_instance_id = 4;</code>
       * @return The bytes for parentProcessInstanceId.
       */
      public com.google.protobuf.ByteString
          getParentProcessInstanceIdBytes() {
        java.lang.Object ref = parentProcessInstanceId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          parentProcessInstanceId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string parent_process_instance_id = 4;</code>
       * @param value The parentProcessInstanceId to set.
       * @return This builder for chaining.
       */
      public Builder setParentProcessInstanceId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        parentProcessInstanceId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string parent_process_instance_id = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearParentProcessInstanceId() {
        bitField0_ = (bitField0_ & ~0x00000001);
        parentProcessInstanceId_ = getDefaultInstance().getParentProcessInstanceId();
        onChanged();
        return this;
      }
      /**
       * <code>optional string parent_process_instance_id = 4;</code>
       * @param value The bytes for parentProcessInstanceId to set.
       * @return This builder for chaining.
       */
      public Builder setParentProcessInstanceIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000001;
        parentProcessInstanceId_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object businessKey_ = "";
      /**
       * <code>optional string business_key = 5;</code>
       * @return Whether the businessKey field is set.
       */
      public boolean hasBusinessKey() {
        return ((bitField0_ & 0x00000002) != 0);
      }
      /**
       * <code>optional string business_key = 5;</code>
       * @return The businessKey.
       */
      public java.lang.String getBusinessKey() {
        java.lang.Object ref = businessKey_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          businessKey_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>optional string business_key = 5;</code>
       * @return The bytes for businessKey.
       */
      public com.google.protobuf.ByteString
          getBusinessKeyBytes() {
        java.lang.Object ref = businessKey_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          businessKey_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string business_key = 5;</code>
       * @param value The businessKey to set.
       * @return This builder for chaining.
       */
      public Builder setBusinessKey(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        businessKey_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string business_key = 5;</code>
       * @return This builder for chaining.
       */
      public Builder clearBusinessKey() {
        bitField0_ = (bitField0_ & ~0x00000002);
        businessKey_ = getDefaultInstance().getBusinessKey();
        onChanged();
        return this;
      }
      /**
       * <code>optional string business_key = 5;</code>
       * @param value The bytes for businessKey to set.
       * @return This builder for chaining.
       */
      public Builder setBusinessKeyBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000002;
        businessKey_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object deploymentId_ = "";
      /**
       * <code>optional string deploymentId = 6;</code>
       * @return Whether the deploymentId field is set.
       */
      public boolean hasDeploymentId() {
        return ((bitField0_ & 0x00000004) != 0);
      }
      /**
       * <code>optional string deploymentId = 6;</code>
       * @return The deploymentId.
       */
      public java.lang.String getDeploymentId() {
        java.lang.Object ref = deploymentId_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          deploymentId_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>optional string deploymentId = 6;</code>
       * @return The bytes for deploymentId.
       */
      public com.google.protobuf.ByteString
          getDeploymentIdBytes() {
        java.lang.Object ref = deploymentId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          deploymentId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string deploymentId = 6;</code>
       * @param value The deploymentId to set.
       * @return This builder for chaining.
       */
      public Builder setDeploymentId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        deploymentId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string deploymentId = 6;</code>
       * @return This builder for chaining.
       */
      public Builder clearDeploymentId() {
        bitField0_ = (bitField0_ & ~0x00000004);
        deploymentId_ = getDefaultInstance().getDeploymentId();
        onChanged();
        return this;
      }
      /**
       * <code>optional string deploymentId = 6;</code>
       * @param value The bytes for deploymentId to set.
       * @return This builder for chaining.
       */
      public Builder setDeploymentIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000004;
        deploymentId_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object description_ = "";
      /**
       * <code>optional string description = 7;</code>
       * @return Whether the description field is set.
       */
      public boolean hasDescription() {
        return ((bitField0_ & 0x00000008) != 0);
      }
      /**
       * <code>optional string description = 7;</code>
       * @return The description.
       */
      public java.lang.String getDescription() {
        java.lang.Object ref = description_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          description_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>optional string description = 7;</code>
       * @return The bytes for description.
       */
      public com.google.protobuf.ByteString
          getDescriptionBytes() {
        java.lang.Object ref = description_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          description_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string description = 7;</code>
       * @param value The description to set.
       * @return This builder for chaining.
       */
      public Builder setDescription(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000008;
        description_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string description = 7;</code>
       * @return This builder for chaining.
       */
      public Builder clearDescription() {
        bitField0_ = (bitField0_ & ~0x00000008);
        description_ = getDefaultInstance().getDescription();
        onChanged();
        return this;
      }
      /**
       * <code>optional string description = 7;</code>
       * @param value The bytes for description to set.
       * @return This builder for chaining.
       */
      public Builder setDescriptionBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000008;
        description_ = value;
        onChanged();
        return this;
      }

      private int state_ ;
      /**
       * <code>int32 state = 8;</code>
       * @return The state.
       */
      @java.lang.Override
      public int getState() {
        return state_;
      }
      /**
       * <code>int32 state = 8;</code>
       * @param value The state to set.
       * @return This builder for chaining.
       */
      public Builder setState(int value) {
        
        state_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int32 state = 8;</code>
       * @return This builder for chaining.
       */
      public Builder clearState() {
        
        state_ = 0;
        onChanged();
        return this;
      }

      private long startDate_ ;
      /**
       * <code>optional int64 start_date = 9;</code>
       * @return Whether the startDate field is set.
       */
      @java.lang.Override
      public boolean hasStartDate() {
        return ((bitField0_ & 0x00000010) != 0);
      }
      /**
       * <code>optional int64 start_date = 9;</code>
       * @return The startDate.
       */
      @java.lang.Override
      public long getStartDate() {
        return startDate_;
      }
      /**
       * <code>optional int64 start_date = 9;</code>
       * @param value The startDate to set.
       * @return This builder for chaining.
       */
      public Builder setStartDate(long value) {
        bitField0_ |= 0x00000010;
        startDate_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int64 start_date = 9;</code>
       * @return This builder for chaining.
       */
      public Builder clearStartDate() {
        bitField0_ = (bitField0_ & ~0x00000010);
        startDate_ = 0L;
        onChanged();
        return this;
      }

      private long nodeInstanceCounter_ ;
      /**
       * <code>optional int64 node_instance_counter = 10;</code>
       * @return Whether the nodeInstanceCounter field is set.
       */
      @java.lang.Override
      public boolean hasNodeInstanceCounter() {
        return ((bitField0_ & 0x00000020) != 0);
      }
      /**
       * <code>optional int64 node_instance_counter = 10;</code>
       * @return The nodeInstanceCounter.
       */
      @java.lang.Override
      public long getNodeInstanceCounter() {
        return nodeInstanceCounter_;
      }
      /**
       * <code>optional int64 node_instance_counter = 10;</code>
       * @param value The nodeInstanceCounter to set.
       * @return This builder for chaining.
       */
      public Builder setNodeInstanceCounter(long value) {
        bitField0_ |= 0x00000020;
        nodeInstanceCounter_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int64 node_instance_counter = 10;</code>
       * @return This builder for chaining.
       */
      public Builder clearNodeInstanceCounter() {
        bitField0_ = (bitField0_ & ~0x00000020);
        nodeInstanceCounter_ = 0L;
        onChanged();
        return this;
      }

      private boolean signalCompletion_ ;
      /**
       * <code>bool signal_completion = 11;</code>
       * @return The signalCompletion.
       */
      @java.lang.Override
      public boolean getSignalCompletion() {
        return signalCompletion_;
      }
      /**
       * <code>bool signal_completion = 11;</code>
       * @param value The signalCompletion to set.
       * @return This builder for chaining.
       */
      public Builder setSignalCompletion(boolean value) {
        
        signalCompletion_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>bool signal_completion = 11;</code>
       * @return This builder for chaining.
       */
      public Builder clearSignalCompletion() {
        
        signalCompletion_ = false;
        onChanged();
        return this;
      }

      private java.lang.Object rootProcessInstanceId_ = "";
      /**
       * <code>optional string root_process_instance_id = 12;</code>
       * @return Whether the rootProcessInstanceId field is set.
       */
      public boolean hasRootProcessInstanceId() {
        return ((bitField0_ & 0x00000040) != 0);
      }
      /**
       * <code>optional string root_process_instance_id = 12;</code>
       * @return The rootProcessInstanceId.
       */
      public java.lang.String getRootProcessInstanceId() {
        java.lang.Object ref = rootProcessInstanceId_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          rootProcessInstanceId_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>optional string root_process_instance_id = 12;</code>
       * @return The bytes for rootProcessInstanceId.
       */
      public com.google.protobuf.ByteString
          getRootProcessInstanceIdBytes() {
        java.lang.Object ref = rootProcessInstanceId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          rootProcessInstanceId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string root_process_instance_id = 12;</code>
       * @param value The rootProcessInstanceId to set.
       * @return This builder for chaining.
       */
      public Builder setRootProcessInstanceId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000040;
        rootProcessInstanceId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string root_process_instance_id = 12;</code>
       * @return This builder for chaining.
       */
      public Builder clearRootProcessInstanceId() {
        bitField0_ = (bitField0_ & ~0x00000040);
        rootProcessInstanceId_ = getDefaultInstance().getRootProcessInstanceId();
        onChanged();
        return this;
      }
      /**
       * <code>optional string root_process_instance_id = 12;</code>
       * @param value The bytes for rootProcessInstanceId to set.
       * @return This builder for chaining.
       */
      public Builder setRootProcessInstanceIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000040;
        rootProcessInstanceId_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object rootProcessId_ = "";
      /**
       * <code>optional string root_process_id = 13;</code>
       * @return Whether the rootProcessId field is set.
       */
      public boolean hasRootProcessId() {
        return ((bitField0_ & 0x00000080) != 0);
      }
      /**
       * <code>optional string root_process_id = 13;</code>
       * @return The rootProcessId.
       */
      public java.lang.String getRootProcessId() {
        java.lang.Object ref = rootProcessId_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          rootProcessId_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>optional string root_process_id = 13;</code>
       * @return The bytes for rootProcessId.
       */
      public com.google.protobuf.ByteString
          getRootProcessIdBytes() {
        java.lang.Object ref = rootProcessId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          rootProcessId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string root_process_id = 13;</code>
       * @param value The rootProcessId to set.
       * @return This builder for chaining.
       */
      public Builder setRootProcessId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000080;
        rootProcessId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string root_process_id = 13;</code>
       * @return This builder for chaining.
       */
      public Builder clearRootProcessId() {
        bitField0_ = (bitField0_ & ~0x00000080);
        rootProcessId_ = getDefaultInstance().getRootProcessId();
        onChanged();
        return this;
      }
      /**
       * <code>optional string root_process_id = 13;</code>
       * @param value The bytes for rootProcessId to set.
       * @return This builder for chaining.
       */
      public Builder setRootProcessIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000080;
        rootProcessId_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object errorNodeId_ = "";
      /**
       * <code>optional string error_node_id = 14;</code>
       * @return Whether the errorNodeId field is set.
       */
      public boolean hasErrorNodeId() {
        return ((bitField0_ & 0x00000100) != 0);
      }
      /**
       * <code>optional string error_node_id = 14;</code>
       * @return The errorNodeId.
       */
      public java.lang.String getErrorNodeId() {
        java.lang.Object ref = errorNodeId_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          errorNodeId_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>optional string error_node_id = 14;</code>
       * @return The bytes for errorNodeId.
       */
      public com.google.protobuf.ByteString
          getErrorNodeIdBytes() {
        java.lang.Object ref = errorNodeId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          errorNodeId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string error_node_id = 14;</code>
       * @param value The errorNodeId to set.
       * @return This builder for chaining.
       */
      public Builder setErrorNodeId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000100;
        errorNodeId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string error_node_id = 14;</code>
       * @return This builder for chaining.
       */
      public Builder clearErrorNodeId() {
        bitField0_ = (bitField0_ & ~0x00000100);
        errorNodeId_ = getDefaultInstance().getErrorNodeId();
        onChanged();
        return this;
      }
      /**
       * <code>optional string error_node_id = 14;</code>
       * @param value The bytes for errorNodeId to set.
       * @return This builder for chaining.
       */
      public Builder setErrorNodeIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000100;
        errorNodeId_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object errorMessage_ = "";
      /**
       * <code>optional string error_message = 15;</code>
       * @return Whether the errorMessage field is set.
       */
      public boolean hasErrorMessage() {
        return ((bitField0_ & 0x00000200) != 0);
      }
      /**
       * <code>optional string error_message = 15;</code>
       * @return The errorMessage.
       */
      public java.lang.String getErrorMessage() {
        java.lang.Object ref = errorMessage_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          errorMessage_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>optional string error_message = 15;</code>
       * @return The bytes for errorMessage.
       */
      public com.google.protobuf.ByteString
          getErrorMessageBytes() {
        java.lang.Object ref = errorMessage_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          errorMessage_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string error_message = 15;</code>
       * @param value The errorMessage to set.
       * @return This builder for chaining.
       */
      public Builder setErrorMessage(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000200;
        errorMessage_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string error_message = 15;</code>
       * @return This builder for chaining.
       */
      public Builder clearErrorMessage() {
        bitField0_ = (bitField0_ & ~0x00000200);
        errorMessage_ = getDefaultInstance().getErrorMessage();
        onChanged();
        return this;
      }
      /**
       * <code>optional string error_message = 15;</code>
       * @param value The bytes for errorMessage to set.
       * @return This builder for chaining.
       */
      public Builder setErrorMessageBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000200;
        errorMessage_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object referenceId_ = "";
      /**
       * <code>optional string reference_id = 16;</code>
       * @return Whether the referenceId field is set.
       */
      public boolean hasReferenceId() {
        return ((bitField0_ & 0x00000400) != 0);
      }
      /**
       * <code>optional string reference_id = 16;</code>
       * @return The referenceId.
       */
      public java.lang.String getReferenceId() {
        java.lang.Object ref = referenceId_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          referenceId_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>optional string reference_id = 16;</code>
       * @return The bytes for referenceId.
       */
      public com.google.protobuf.ByteString
          getReferenceIdBytes() {
        java.lang.Object ref = referenceId_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          referenceId_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string reference_id = 16;</code>
       * @param value The referenceId to set.
       * @return This builder for chaining.
       */
      public Builder setReferenceId(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000400;
        referenceId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string reference_id = 16;</code>
       * @return This builder for chaining.
       */
      public Builder clearReferenceId() {
        bitField0_ = (bitField0_ & ~0x00000400);
        referenceId_ = getDefaultInstance().getReferenceId();
        onChanged();
        return this;
      }
      /**
       * <code>optional string reference_id = 16;</code>
       * @param value The bytes for referenceId to set.
       * @return This builder for chaining.
       */
      public Builder setReferenceIdBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000400;
        referenceId_ = value;
        onChanged();
        return this;
      }

      private org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext sla_;
      private com.google.protobuf.SingleFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContextOrBuilder> slaBuilder_;
      /**
       * <code>optional .org.kie.kogito.serialization.process.protobuf.SLAContext sla = 17;</code>
       * @return Whether the sla field is set.
       */
      public boolean hasSla() {
        return ((bitField0_ & 0x00000800) != 0);
      }
      /**
       * <code>optional .org.kie.kogito.serialization.process.protobuf.SLAContext sla = 17;</code>
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
       * <code>optional .org.kie.kogito.serialization.process.protobuf.SLAContext sla = 17;</code>
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
        bitField0_ |= 0x00000800;
        return this;
      }
      /**
       * <code>optional .org.kie.kogito.serialization.process.protobuf.SLAContext sla = 17;</code>
       */
      public Builder setSla(
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.Builder builderForValue) {
        if (slaBuilder_ == null) {
          sla_ = builderForValue.build();
          onChanged();
        } else {
          slaBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00000800;
        return this;
      }
      /**
       * <code>optional .org.kie.kogito.serialization.process.protobuf.SLAContext sla = 17;</code>
       */
      public Builder mergeSla(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext value) {
        if (slaBuilder_ == null) {
          if (((bitField0_ & 0x00000800) != 0) &&
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
        bitField0_ |= 0x00000800;
        return this;
      }
      /**
       * <code>optional .org.kie.kogito.serialization.process.protobuf.SLAContext sla = 17;</code>
       */
      public Builder clearSla() {
        if (slaBuilder_ == null) {
          sla_ = null;
          onChanged();
        } else {
          slaBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00000800);
        return this;
      }
      /**
       * <code>optional .org.kie.kogito.serialization.process.protobuf.SLAContext sla = 17;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SLAContext.Builder getSlaBuilder() {
        bitField0_ |= 0x00000800;
        onChanged();
        return getSlaFieldBuilder().getBuilder();
      }
      /**
       * <code>optional .org.kie.kogito.serialization.process.protobuf.SLAContext sla = 17;</code>
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
       * <code>optional .org.kie.kogito.serialization.process.protobuf.SLAContext sla = 17;</code>
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

      private org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext context_;
      private com.google.protobuf.SingleFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContextOrBuilder> contextBuilder_;
      /**
       * <code>optional .org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 18;</code>
       * @return Whether the context field is set.
       */
      public boolean hasContext() {
        return ((bitField0_ & 0x00001000) != 0);
      }
      /**
       * <code>optional .org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 18;</code>
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
       * <code>optional .org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 18;</code>
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
        bitField0_ |= 0x00001000;
        return this;
      }
      /**
       * <code>optional .org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 18;</code>
       */
      public Builder setContext(
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder builderForValue) {
        if (contextBuilder_ == null) {
          context_ = builderForValue.build();
          onChanged();
        } else {
          contextBuilder_.setMessage(builderForValue.build());
        }
        bitField0_ |= 0x00001000;
        return this;
      }
      /**
       * <code>optional .org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 18;</code>
       */
      public Builder mergeContext(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext value) {
        if (contextBuilder_ == null) {
          if (((bitField0_ & 0x00001000) != 0) &&
              context_ != null &&
              context_ != org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.getDefaultInstance()) {
            context_ =
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.newBuilder(context_).mergeFrom(value).buildPartial();
          } else {
            context_ = value;
          }
          onChanged();
        } else {
          contextBuilder_.mergeFrom(value);
        }
        bitField0_ |= 0x00001000;
        return this;
      }
      /**
       * <code>optional .org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 18;</code>
       */
      public Builder clearContext() {
        if (contextBuilder_ == null) {
          context_ = null;
          onChanged();
        } else {
          contextBuilder_.clear();
        }
        bitField0_ = (bitField0_ & ~0x00001000);
        return this;
      }
      /**
       * <code>optional .org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 18;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.WorkflowContext.Builder getContextBuilder() {
        bitField0_ |= 0x00001000;
        onChanged();
        return getContextFieldBuilder().getBuilder();
      }
      /**
       * <code>optional .org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 18;</code>
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
       * <code>optional .org.kie.kogito.serialization.process.protobuf.WorkflowContext context = 18;</code>
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

      private java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext> swimlaneContext_ =
        java.util.Collections.emptyList();
      private void ensureSwimlaneContextIsMutable() {
        if (!((bitField0_ & 0x00002000) != 0)) {
          swimlaneContext_ = new java.util.ArrayList<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext>(swimlaneContext_);
          bitField0_ |= 0x00002000;
         }
      }

      private com.google.protobuf.RepeatedFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContextOrBuilder> swimlaneContextBuilder_;

      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
       */
      public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext> getSwimlaneContextList() {
        if (swimlaneContextBuilder_ == null) {
          return java.util.Collections.unmodifiableList(swimlaneContext_);
        } else {
          return swimlaneContextBuilder_.getMessageList();
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
       */
      public int getSwimlaneContextCount() {
        if (swimlaneContextBuilder_ == null) {
          return swimlaneContext_.size();
        } else {
          return swimlaneContextBuilder_.getCount();
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext getSwimlaneContext(int index) {
        if (swimlaneContextBuilder_ == null) {
          return swimlaneContext_.get(index);
        } else {
          return swimlaneContextBuilder_.getMessage(index);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
       */
      public Builder setSwimlaneContext(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext value) {
        if (swimlaneContextBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureSwimlaneContextIsMutable();
          swimlaneContext_.set(index, value);
          onChanged();
        } else {
          swimlaneContextBuilder_.setMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
       */
      public Builder setSwimlaneContext(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext.Builder builderForValue) {
        if (swimlaneContextBuilder_ == null) {
          ensureSwimlaneContextIsMutable();
          swimlaneContext_.set(index, builderForValue.build());
          onChanged();
        } else {
          swimlaneContextBuilder_.setMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
       */
      public Builder addSwimlaneContext(org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext value) {
        if (swimlaneContextBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureSwimlaneContextIsMutable();
          swimlaneContext_.add(value);
          onChanged();
        } else {
          swimlaneContextBuilder_.addMessage(value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
       */
      public Builder addSwimlaneContext(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext value) {
        if (swimlaneContextBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureSwimlaneContextIsMutable();
          swimlaneContext_.add(index, value);
          onChanged();
        } else {
          swimlaneContextBuilder_.addMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
       */
      public Builder addSwimlaneContext(
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext.Builder builderForValue) {
        if (swimlaneContextBuilder_ == null) {
          ensureSwimlaneContextIsMutable();
          swimlaneContext_.add(builderForValue.build());
          onChanged();
        } else {
          swimlaneContextBuilder_.addMessage(builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
       */
      public Builder addSwimlaneContext(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext.Builder builderForValue) {
        if (swimlaneContextBuilder_ == null) {
          ensureSwimlaneContextIsMutable();
          swimlaneContext_.add(index, builderForValue.build());
          onChanged();
        } else {
          swimlaneContextBuilder_.addMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
       */
      public Builder addAllSwimlaneContext(
          java.lang.Iterable<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext> values) {
        if (swimlaneContextBuilder_ == null) {
          ensureSwimlaneContextIsMutable();
          com.google.protobuf.AbstractMessageLite.Builder.addAll(
              values, swimlaneContext_);
          onChanged();
        } else {
          swimlaneContextBuilder_.addAllMessages(values);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
       */
      public Builder clearSwimlaneContext() {
        if (swimlaneContextBuilder_ == null) {
          swimlaneContext_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00002000);
          onChanged();
        } else {
          swimlaneContextBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
       */
      public Builder removeSwimlaneContext(int index) {
        if (swimlaneContextBuilder_ == null) {
          ensureSwimlaneContextIsMutable();
          swimlaneContext_.remove(index);
          onChanged();
        } else {
          swimlaneContextBuilder_.remove(index);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext.Builder getSwimlaneContextBuilder(
          int index) {
        return getSwimlaneContextFieldBuilder().getBuilder(index);
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContextOrBuilder getSwimlaneContextOrBuilder(
          int index) {
        if (swimlaneContextBuilder_ == null) {
          return swimlaneContext_.get(index);  } else {
          return swimlaneContextBuilder_.getMessageOrBuilder(index);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
       */
      public java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContextOrBuilder> 
           getSwimlaneContextOrBuilderList() {
        if (swimlaneContextBuilder_ != null) {
          return swimlaneContextBuilder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(swimlaneContext_);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext.Builder addSwimlaneContextBuilder() {
        return getSwimlaneContextFieldBuilder().addBuilder(
            org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext.getDefaultInstance());
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext.Builder addSwimlaneContextBuilder(
          int index) {
        return getSwimlaneContextFieldBuilder().addBuilder(
            index, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext.getDefaultInstance());
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.SwimlaneContext swimlane_context = 19;</code>
       */
      public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext.Builder> 
           getSwimlaneContextBuilderList() {
        return getSwimlaneContextFieldBuilder().getBuilderList();
      }
      private com.google.protobuf.RepeatedFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContextOrBuilder> 
          getSwimlaneContextFieldBuilder() {
        if (swimlaneContextBuilder_ == null) {
          swimlaneContextBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
              org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContext.Builder, org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.SwimlaneContextOrBuilder>(
                  swimlaneContext_,
                  ((bitField0_ & 0x00002000) != 0),
                  getParentForChildren(),
                  isClean());
          swimlaneContext_ = null;
        }
        return swimlaneContextBuilder_;
      }

      private com.google.protobuf.LazyStringList completedNodeIds_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      private void ensureCompletedNodeIdsIsMutable() {
        if (!((bitField0_ & 0x00004000) != 0)) {
          completedNodeIds_ = new com.google.protobuf.LazyStringArrayList(completedNodeIds_);
          bitField0_ |= 0x00004000;
         }
      }
      /**
       * <code>repeated string completedNodeIds = 20;</code>
       * @return A list containing the completedNodeIds.
       */
      public com.google.protobuf.ProtocolStringList
          getCompletedNodeIdsList() {
        return completedNodeIds_.getUnmodifiableView();
      }
      /**
       * <code>repeated string completedNodeIds = 20;</code>
       * @return The count of completedNodeIds.
       */
      public int getCompletedNodeIdsCount() {
        return completedNodeIds_.size();
      }
      /**
       * <code>repeated string completedNodeIds = 20;</code>
       * @param index The index of the element to return.
       * @return The completedNodeIds at the given index.
       */
      public java.lang.String getCompletedNodeIds(int index) {
        return completedNodeIds_.get(index);
      }
      /**
       * <code>repeated string completedNodeIds = 20;</code>
       * @param index The index of the value to return.
       * @return The bytes of the completedNodeIds at the given index.
       */
      public com.google.protobuf.ByteString
          getCompletedNodeIdsBytes(int index) {
        return completedNodeIds_.getByteString(index);
      }
      /**
       * <code>repeated string completedNodeIds = 20;</code>
       * @param index The index to set the value at.
       * @param value The completedNodeIds to set.
       * @return This builder for chaining.
       */
      public Builder setCompletedNodeIds(
          int index, java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureCompletedNodeIdsIsMutable();
        completedNodeIds_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string completedNodeIds = 20;</code>
       * @param value The completedNodeIds to add.
       * @return This builder for chaining.
       */
      public Builder addCompletedNodeIds(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureCompletedNodeIdsIsMutable();
        completedNodeIds_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string completedNodeIds = 20;</code>
       * @param values The completedNodeIds to add.
       * @return This builder for chaining.
       */
      public Builder addAllCompletedNodeIds(
          java.lang.Iterable<java.lang.String> values) {
        ensureCompletedNodeIdsIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, completedNodeIds_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string completedNodeIds = 20;</code>
       * @return This builder for chaining.
       */
      public Builder clearCompletedNodeIds() {
        completedNodeIds_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00004000);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string completedNodeIds = 20;</code>
       * @param value The bytes of the completedNodeIds to add.
       * @return This builder for chaining.
       */
      public Builder addCompletedNodeIdsBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        ensureCompletedNodeIdsIsMutable();
        completedNodeIds_.add(value);
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


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.ProcessInstance)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.ProcessInstance)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<ProcessInstance>
        PARSER = new com.google.protobuf.AbstractParser<ProcessInstance>() {
      @java.lang.Override
      public ProcessInstance parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new ProcessInstance(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<ProcessInstance> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<ProcessInstance> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoProcessInstanceProtobuf.ProcessInstance getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_ProcessInstance_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_ProcessInstance_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\nKorg/kie/kogito/serialization/process/p" +
      "rotobuf/kogito_process_instance.proto\022-o" +
      "rg.kie.kogito.serialization.process.prot" +
      "obuf\032@org/kie/kogito/serialization/proce" +
      "ss/protobuf/kogito_types.proto\"\312\007\n\017Proce" +
      "ssInstance\022\024\n\014process_type\030\001 \001(\t\022\022\n\nproc" +
      "ess_id\030\002 \001(\t\022\n\n\002id\030\003 \001(\t\022\'\n\032parent_proce" +
      "ss_instance_id\030\004 \001(\tH\000\210\001\001\022\031\n\014business_ke" +
      "y\030\005 \001(\tH\001\210\001\001\022\031\n\014deploymentId\030\006 \001(\tH\002\210\001\001\022" +
      "\030\n\013description\030\007 \001(\tH\003\210\001\001\022\r\n\005state\030\010 \001(\005" +
      "\022\027\n\nstart_date\030\t \001(\003H\004\210\001\001\022\"\n\025node_instan" +
      "ce_counter\030\n \001(\003H\005\210\001\001\022\031\n\021signal_completi" +
      "on\030\013 \001(\010\022%\n\030root_process_instance_id\030\014 \001" +
      "(\tH\006\210\001\001\022\034\n\017root_process_id\030\r \001(\tH\007\210\001\001\022\032\n" +
      "\rerror_node_id\030\016 \001(\tH\010\210\001\001\022\032\n\rerror_messa" +
      "ge\030\017 \001(\tH\t\210\001\001\022\031\n\014reference_id\030\020 \001(\tH\n\210\001\001" +
      "\022K\n\003sla\030\021 \001(\01329.org.kie.kogito.serializa" +
      "tion.process.protobuf.SLAContextH\013\210\001\001\022T\n" +
      "\007context\030\022 \001(\0132>.org.kie.kogito.serializ" +
      "ation.process.protobuf.WorkflowContextH\014" +
      "\210\001\001\022X\n\020swimlane_context\030\023 \003(\0132>.org.kie." +
      "kogito.serialization.process.protobuf.Sw" +
      "imlaneContext\022\030\n\020completedNodeIds\030\024 \003(\tB" +
      "\035\n\033_parent_process_instance_idB\017\n\r_busin" +
      "ess_keyB\017\n\r_deploymentIdB\016\n\014_description" +
      "B\r\n\013_start_dateB\030\n\026_node_instance_counte" +
      "rB\033\n\031_root_process_instance_idB\022\n\020_root_" +
      "process_idB\020\n\016_error_node_idB\020\n\016_error_m" +
      "essageB\017\n\r_reference_idB\006\n\004_slaB\n\n\010_cont" +
      "extB\037B\035KogitoProcessInstanceProtobufb\006pr" +
      "oto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
          org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.getDescriptor(),
        });
    internal_static_org_kie_kogito_serialization_process_protobuf_ProcessInstance_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_org_kie_kogito_serialization_process_protobuf_ProcessInstance_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_ProcessInstance_descriptor,
        new java.lang.String[] { "ProcessType", "ProcessId", "Id", "ParentProcessInstanceId", "BusinessKey", "DeploymentId", "Description", "State", "StartDate", "NodeInstanceCounter", "SignalCompletion", "RootProcessInstanceId", "RootProcessId", "ErrorNodeId", "ErrorMessage", "ReferenceId", "Sla", "Context", "SwimlaneContext", "CompletedNodeIds", "ParentProcessInstanceId", "BusinessKey", "DeploymentId", "Description", "StartDate", "NodeInstanceCounter", "RootProcessInstanceId", "RootProcessId", "ErrorNodeId", "ErrorMessage", "ReferenceId", "Sla", "Context", });
    org.kie.kogito.serialization.process.protobuf.KogitoTypesProtobuf.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
