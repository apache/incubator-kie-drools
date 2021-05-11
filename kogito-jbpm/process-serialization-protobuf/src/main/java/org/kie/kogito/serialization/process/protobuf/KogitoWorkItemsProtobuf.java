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

public final class KogitoWorkItemsProtobuf {
  private KogitoWorkItemsProtobuf() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface HumanTaskWorkItemDataOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.HumanTaskWorkItemData)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>string task_name = 1;</code>
     * @return Whether the taskName field is set.
     */
    boolean hasTaskName();
    /**
     * <code>string task_name = 1;</code>
     * @return The taskName.
     */
    java.lang.String getTaskName();
    /**
     * <code>string task_name = 1;</code>
     * @return The bytes for taskName.
     */
    com.google.protobuf.ByteString
        getTaskNameBytes();

    /**
     * <code>string task_description = 2;</code>
     * @return Whether the taskDescription field is set.
     */
    boolean hasTaskDescription();
    /**
     * <code>string task_description = 2;</code>
     * @return The taskDescription.
     */
    java.lang.String getTaskDescription();
    /**
     * <code>string task_description = 2;</code>
     * @return The bytes for taskDescription.
     */
    com.google.protobuf.ByteString
        getTaskDescriptionBytes();

    /**
     * <code>string task_priority = 3;</code>
     * @return Whether the taskPriority field is set.
     */
    boolean hasTaskPriority();
    /**
     * <code>string task_priority = 3;</code>
     * @return The taskPriority.
     */
    java.lang.String getTaskPriority();
    /**
     * <code>string task_priority = 3;</code>
     * @return The bytes for taskPriority.
     */
    com.google.protobuf.ByteString
        getTaskPriorityBytes();

    /**
     * <code>string actual_owner = 4;</code>
     * @return Whether the actualOwner field is set.
     */
    boolean hasActualOwner();
    /**
     * <code>string actual_owner = 4;</code>
     * @return The actualOwner.
     */
    java.lang.String getActualOwner();
    /**
     * <code>string actual_owner = 4;</code>
     * @return The bytes for actualOwner.
     */
    com.google.protobuf.ByteString
        getActualOwnerBytes();

    /**
     * <code>repeated string pot_users = 5;</code>
     * @return A list containing the potUsers.
     */
    java.util.List<java.lang.String>
        getPotUsersList();
    /**
     * <code>repeated string pot_users = 5;</code>
     * @return The count of potUsers.
     */
    int getPotUsersCount();
    /**
     * <code>repeated string pot_users = 5;</code>
     * @param index The index of the element to return.
     * @return The potUsers at the given index.
     */
    java.lang.String getPotUsers(int index);
    /**
     * <code>repeated string pot_users = 5;</code>
     * @param index The index of the value to return.
     * @return The bytes of the potUsers at the given index.
     */
    com.google.protobuf.ByteString
        getPotUsersBytes(int index);

    /**
     * <code>repeated string pot_groups = 6;</code>
     * @return A list containing the potGroups.
     */
    java.util.List<java.lang.String>
        getPotGroupsList();
    /**
     * <code>repeated string pot_groups = 6;</code>
     * @return The count of potGroups.
     */
    int getPotGroupsCount();
    /**
     * <code>repeated string pot_groups = 6;</code>
     * @param index The index of the element to return.
     * @return The potGroups at the given index.
     */
    java.lang.String getPotGroups(int index);
    /**
     * <code>repeated string pot_groups = 6;</code>
     * @param index The index of the value to return.
     * @return The bytes of the potGroups at the given index.
     */
    com.google.protobuf.ByteString
        getPotGroupsBytes(int index);

    /**
     * <code>repeated string excluded_users = 7;</code>
     * @return A list containing the excludedUsers.
     */
    java.util.List<java.lang.String>
        getExcludedUsersList();
    /**
     * <code>repeated string excluded_users = 7;</code>
     * @return The count of excludedUsers.
     */
    int getExcludedUsersCount();
    /**
     * <code>repeated string excluded_users = 7;</code>
     * @param index The index of the element to return.
     * @return The excludedUsers at the given index.
     */
    java.lang.String getExcludedUsers(int index);
    /**
     * <code>repeated string excluded_users = 7;</code>
     * @param index The index of the value to return.
     * @return The bytes of the excludedUsers at the given index.
     */
    com.google.protobuf.ByteString
        getExcludedUsersBytes(int index);

    /**
     * <code>repeated string admin_users = 8;</code>
     * @return A list containing the adminUsers.
     */
    java.util.List<java.lang.String>
        getAdminUsersList();
    /**
     * <code>repeated string admin_users = 8;</code>
     * @return The count of adminUsers.
     */
    int getAdminUsersCount();
    /**
     * <code>repeated string admin_users = 8;</code>
     * @param index The index of the element to return.
     * @return The adminUsers at the given index.
     */
    java.lang.String getAdminUsers(int index);
    /**
     * <code>repeated string admin_users = 8;</code>
     * @param index The index of the value to return.
     * @return The bytes of the adminUsers at the given index.
     */
    com.google.protobuf.ByteString
        getAdminUsersBytes(int index);

    /**
     * <code>repeated string admin_groups = 9;</code>
     * @return A list containing the adminGroups.
     */
    java.util.List<java.lang.String>
        getAdminGroupsList();
    /**
     * <code>repeated string admin_groups = 9;</code>
     * @return The count of adminGroups.
     */
    int getAdminGroupsCount();
    /**
     * <code>repeated string admin_groups = 9;</code>
     * @param index The index of the element to return.
     * @return The adminGroups at the given index.
     */
    java.lang.String getAdminGroups(int index);
    /**
     * <code>repeated string admin_groups = 9;</code>
     * @param index The index of the value to return.
     * @return The bytes of the adminGroups at the given index.
     */
    com.google.protobuf.ByteString
        getAdminGroupsBytes(int index);

    /**
     * <code>string task_reference_name = 10;</code>
     * @return Whether the taskReferenceName field is set.
     */
    boolean hasTaskReferenceName();
    /**
     * <code>string task_reference_name = 10;</code>
     * @return The taskReferenceName.
     */
    java.lang.String getTaskReferenceName();
    /**
     * <code>string task_reference_name = 10;</code>
     * @return The bytes for taskReferenceName.
     */
    com.google.protobuf.ByteString
        getTaskReferenceNameBytes();

    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
     */
    java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment> 
        getCommentsList();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment getComments(int index);
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
     */
    int getCommentsCount();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
     */
    java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.CommentOrBuilder> 
        getCommentsOrBuilderList();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.CommentOrBuilder getCommentsOrBuilder(
        int index);

    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
     */
    java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment> 
        getAttachmentsList();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment getAttachments(int index);
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
     */
    int getAttachmentsCount();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
     */
    java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.AttachmentOrBuilder> 
        getAttachmentsOrBuilderList();
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
     */
    org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.AttachmentOrBuilder getAttachmentsOrBuilder(
        int index);

    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; start_deadlines = 13;</code>
     */
    int getStartDeadlinesCount();
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; start_deadlines = 13;</code>
     */
    boolean containsStartDeadlines(
        java.lang.String key);
    /**
     * Use {@link #getStartDeadlinesMap()} instead.
     */
    @java.lang.Deprecated
    java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline>
    getStartDeadlines();
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; start_deadlines = 13;</code>
     */
    java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline>
    getStartDeadlinesMap();
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; start_deadlines = 13;</code>
     */

    org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline getStartDeadlinesOrDefault(
        java.lang.String key,
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline defaultValue);
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; start_deadlines = 13;</code>
     */

    org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline getStartDeadlinesOrThrow(
        java.lang.String key);

    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; completed_deadlines = 14;</code>
     */
    int getCompletedDeadlinesCount();
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; completed_deadlines = 14;</code>
     */
    boolean containsCompletedDeadlines(
        java.lang.String key);
    /**
     * Use {@link #getCompletedDeadlinesMap()} instead.
     */
    @java.lang.Deprecated
    java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline>
    getCompletedDeadlines();
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; completed_deadlines = 14;</code>
     */
    java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline>
    getCompletedDeadlinesMap();
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; completed_deadlines = 14;</code>
     */

    org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline getCompletedDeadlinesOrDefault(
        java.lang.String key,
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline defaultValue);
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; completed_deadlines = 14;</code>
     */

    org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline getCompletedDeadlinesOrThrow(
        java.lang.String key);

    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; start_reassigments = 15;</code>
     */
    int getStartReassigmentsCount();
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; start_reassigments = 15;</code>
     */
    boolean containsStartReassigments(
        java.lang.String key);
    /**
     * Use {@link #getStartReassigmentsMap()} instead.
     */
    @java.lang.Deprecated
    java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment>
    getStartReassigments();
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; start_reassigments = 15;</code>
     */
    java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment>
    getStartReassigmentsMap();
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; start_reassigments = 15;</code>
     */

    org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment getStartReassigmentsOrDefault(
        java.lang.String key,
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment defaultValue);
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; start_reassigments = 15;</code>
     */

    org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment getStartReassigmentsOrThrow(
        java.lang.String key);

    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; completed_reassigments = 16;</code>
     */
    int getCompletedReassigmentsCount();
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; completed_reassigments = 16;</code>
     */
    boolean containsCompletedReassigments(
        java.lang.String key);
    /**
     * Use {@link #getCompletedReassigmentsMap()} instead.
     */
    @java.lang.Deprecated
    java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment>
    getCompletedReassigments();
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; completed_reassigments = 16;</code>
     */
    java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment>
    getCompletedReassigmentsMap();
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; completed_reassigments = 16;</code>
     */

    org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment getCompletedReassigmentsOrDefault(
        java.lang.String key,
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment defaultValue);
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; completed_reassigments = 16;</code>
     */

    org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment getCompletedReassigmentsOrThrow(
        java.lang.String key);
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.HumanTaskWorkItemData}
   */
  public static final class HumanTaskWorkItemData extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.HumanTaskWorkItemData)
      HumanTaskWorkItemDataOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use HumanTaskWorkItemData.newBuilder() to construct.
    private HumanTaskWorkItemData(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private HumanTaskWorkItemData() {
      taskName_ = "";
      taskDescription_ = "";
      taskPriority_ = "";
      actualOwner_ = "";
      potUsers_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      potGroups_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      excludedUsers_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      adminUsers_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      adminGroups_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      taskReferenceName_ = "";
      comments_ = java.util.Collections.emptyList();
      attachments_ = java.util.Collections.emptyList();
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new HumanTaskWorkItemData();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private HumanTaskWorkItemData(
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
              taskName_ = s;
              break;
            }
            case 18: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000002;
              taskDescription_ = s;
              break;
            }
            case 26: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000004;
              taskPriority_ = s;
              break;
            }
            case 34: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000008;
              actualOwner_ = s;
              break;
            }
            case 42: {
              java.lang.String s = input.readStringRequireUtf8();
              if (!((mutable_bitField0_ & 0x00000010) != 0)) {
                potUsers_ = new com.google.protobuf.LazyStringArrayList();
                mutable_bitField0_ |= 0x00000010;
              }
              potUsers_.add(s);
              break;
            }
            case 50: {
              java.lang.String s = input.readStringRequireUtf8();
              if (!((mutable_bitField0_ & 0x00000020) != 0)) {
                potGroups_ = new com.google.protobuf.LazyStringArrayList();
                mutable_bitField0_ |= 0x00000020;
              }
              potGroups_.add(s);
              break;
            }
            case 58: {
              java.lang.String s = input.readStringRequireUtf8();
              if (!((mutable_bitField0_ & 0x00000040) != 0)) {
                excludedUsers_ = new com.google.protobuf.LazyStringArrayList();
                mutable_bitField0_ |= 0x00000040;
              }
              excludedUsers_.add(s);
              break;
            }
            case 66: {
              java.lang.String s = input.readStringRequireUtf8();
              if (!((mutable_bitField0_ & 0x00000080) != 0)) {
                adminUsers_ = new com.google.protobuf.LazyStringArrayList();
                mutable_bitField0_ |= 0x00000080;
              }
              adminUsers_.add(s);
              break;
            }
            case 74: {
              java.lang.String s = input.readStringRequireUtf8();
              if (!((mutable_bitField0_ & 0x00000100) != 0)) {
                adminGroups_ = new com.google.protobuf.LazyStringArrayList();
                mutable_bitField0_ |= 0x00000100;
              }
              adminGroups_.add(s);
              break;
            }
            case 82: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000010;
              taskReferenceName_ = s;
              break;
            }
            case 90: {
              if (!((mutable_bitField0_ & 0x00000400) != 0)) {
                comments_ = new java.util.ArrayList<org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment>();
                mutable_bitField0_ |= 0x00000400;
              }
              comments_.add(
                  input.readMessage(org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment.parser(), extensionRegistry));
              break;
            }
            case 98: {
              if (!((mutable_bitField0_ & 0x00000800) != 0)) {
                attachments_ = new java.util.ArrayList<org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment>();
                mutable_bitField0_ |= 0x00000800;
              }
              attachments_.add(
                  input.readMessage(org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment.parser(), extensionRegistry));
              break;
            }
            case 106: {
              if (!((mutable_bitField0_ & 0x00001000) != 0)) {
                startDeadlines_ = com.google.protobuf.MapField.newMapField(
                    StartDeadlinesDefaultEntryHolder.defaultEntry);
                mutable_bitField0_ |= 0x00001000;
              }
              com.google.protobuf.MapEntry<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline>
              startDeadlines__ = input.readMessage(
                  StartDeadlinesDefaultEntryHolder.defaultEntry.getParserForType(), extensionRegistry);
              startDeadlines_.getMutableMap().put(
                  startDeadlines__.getKey(), startDeadlines__.getValue());
              break;
            }
            case 114: {
              if (!((mutable_bitField0_ & 0x00002000) != 0)) {
                completedDeadlines_ = com.google.protobuf.MapField.newMapField(
                    CompletedDeadlinesDefaultEntryHolder.defaultEntry);
                mutable_bitField0_ |= 0x00002000;
              }
              com.google.protobuf.MapEntry<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline>
              completedDeadlines__ = input.readMessage(
                  CompletedDeadlinesDefaultEntryHolder.defaultEntry.getParserForType(), extensionRegistry);
              completedDeadlines_.getMutableMap().put(
                  completedDeadlines__.getKey(), completedDeadlines__.getValue());
              break;
            }
            case 122: {
              if (!((mutable_bitField0_ & 0x00004000) != 0)) {
                startReassigments_ = com.google.protobuf.MapField.newMapField(
                    StartReassigmentsDefaultEntryHolder.defaultEntry);
                mutable_bitField0_ |= 0x00004000;
              }
              com.google.protobuf.MapEntry<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment>
              startReassigments__ = input.readMessage(
                  StartReassigmentsDefaultEntryHolder.defaultEntry.getParserForType(), extensionRegistry);
              startReassigments_.getMutableMap().put(
                  startReassigments__.getKey(), startReassigments__.getValue());
              break;
            }
            case 130: {
              if (!((mutable_bitField0_ & 0x00008000) != 0)) {
                completedReassigments_ = com.google.protobuf.MapField.newMapField(
                    CompletedReassigmentsDefaultEntryHolder.defaultEntry);
                mutable_bitField0_ |= 0x00008000;
              }
              com.google.protobuf.MapEntry<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment>
              completedReassigments__ = input.readMessage(
                  CompletedReassigmentsDefaultEntryHolder.defaultEntry.getParserForType(), extensionRegistry);
              completedReassigments_.getMutableMap().put(
                  completedReassigments__.getKey(), completedReassigments__.getValue());
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
        if (((mutable_bitField0_ & 0x00000010) != 0)) {
          potUsers_ = potUsers_.getUnmodifiableView();
        }
        if (((mutable_bitField0_ & 0x00000020) != 0)) {
          potGroups_ = potGroups_.getUnmodifiableView();
        }
        if (((mutable_bitField0_ & 0x00000040) != 0)) {
          excludedUsers_ = excludedUsers_.getUnmodifiableView();
        }
        if (((mutable_bitField0_ & 0x00000080) != 0)) {
          adminUsers_ = adminUsers_.getUnmodifiableView();
        }
        if (((mutable_bitField0_ & 0x00000100) != 0)) {
          adminGroups_ = adminGroups_.getUnmodifiableView();
        }
        if (((mutable_bitField0_ & 0x00000400) != 0)) {
          comments_ = java.util.Collections.unmodifiableList(comments_);
        }
        if (((mutable_bitField0_ & 0x00000800) != 0)) {
          attachments_ = java.util.Collections.unmodifiableList(attachments_);
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_descriptor;
    }

    @SuppressWarnings({"rawtypes"})
    @java.lang.Override
    protected com.google.protobuf.MapField internalGetMapField(
        int number) {
      switch (number) {
        case 13:
          return internalGetStartDeadlines();
        case 14:
          return internalGetCompletedDeadlines();
        case 15:
          return internalGetStartReassigments();
        case 16:
          return internalGetCompletedReassigments();
        default:
          throw new RuntimeException(
              "Invalid map field number: " + number);
      }
    }
    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData.class, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData.Builder.class);
    }

    private int bitField0_;
    public static final int TASK_NAME_FIELD_NUMBER = 1;
    private volatile java.lang.Object taskName_;
    /**
     * <code>string task_name = 1;</code>
     * @return Whether the taskName field is set.
     */
    @java.lang.Override
    public boolean hasTaskName() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>string task_name = 1;</code>
     * @return The taskName.
     */
    @java.lang.Override
    public java.lang.String getTaskName() {
      java.lang.Object ref = taskName_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        taskName_ = s;
        return s;
      }
    }
    /**
     * <code>string task_name = 1;</code>
     * @return The bytes for taskName.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getTaskNameBytes() {
      java.lang.Object ref = taskName_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        taskName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int TASK_DESCRIPTION_FIELD_NUMBER = 2;
    private volatile java.lang.Object taskDescription_;
    /**
     * <code>string task_description = 2;</code>
     * @return Whether the taskDescription field is set.
     */
    @java.lang.Override
    public boolean hasTaskDescription() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>string task_description = 2;</code>
     * @return The taskDescription.
     */
    @java.lang.Override
    public java.lang.String getTaskDescription() {
      java.lang.Object ref = taskDescription_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        taskDescription_ = s;
        return s;
      }
    }
    /**
     * <code>string task_description = 2;</code>
     * @return The bytes for taskDescription.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getTaskDescriptionBytes() {
      java.lang.Object ref = taskDescription_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        taskDescription_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int TASK_PRIORITY_FIELD_NUMBER = 3;
    private volatile java.lang.Object taskPriority_;
    /**
     * <code>string task_priority = 3;</code>
     * @return Whether the taskPriority field is set.
     */
    @java.lang.Override
    public boolean hasTaskPriority() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     * <code>string task_priority = 3;</code>
     * @return The taskPriority.
     */
    @java.lang.Override
    public java.lang.String getTaskPriority() {
      java.lang.Object ref = taskPriority_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        taskPriority_ = s;
        return s;
      }
    }
    /**
     * <code>string task_priority = 3;</code>
     * @return The bytes for taskPriority.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getTaskPriorityBytes() {
      java.lang.Object ref = taskPriority_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        taskPriority_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int ACTUAL_OWNER_FIELD_NUMBER = 4;
    private volatile java.lang.Object actualOwner_;
    /**
     * <code>string actual_owner = 4;</code>
     * @return Whether the actualOwner field is set.
     */
    @java.lang.Override
    public boolean hasActualOwner() {
      return ((bitField0_ & 0x00000008) != 0);
    }
    /**
     * <code>string actual_owner = 4;</code>
     * @return The actualOwner.
     */
    @java.lang.Override
    public java.lang.String getActualOwner() {
      java.lang.Object ref = actualOwner_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        actualOwner_ = s;
        return s;
      }
    }
    /**
     * <code>string actual_owner = 4;</code>
     * @return The bytes for actualOwner.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getActualOwnerBytes() {
      java.lang.Object ref = actualOwner_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        actualOwner_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int POT_USERS_FIELD_NUMBER = 5;
    private com.google.protobuf.LazyStringList potUsers_;
    /**
     * <code>repeated string pot_users = 5;</code>
     * @return A list containing the potUsers.
     */
    public com.google.protobuf.ProtocolStringList
        getPotUsersList() {
      return potUsers_;
    }
    /**
     * <code>repeated string pot_users = 5;</code>
     * @return The count of potUsers.
     */
    public int getPotUsersCount() {
      return potUsers_.size();
    }
    /**
     * <code>repeated string pot_users = 5;</code>
     * @param index The index of the element to return.
     * @return The potUsers at the given index.
     */
    public java.lang.String getPotUsers(int index) {
      return potUsers_.get(index);
    }
    /**
     * <code>repeated string pot_users = 5;</code>
     * @param index The index of the value to return.
     * @return The bytes of the potUsers at the given index.
     */
    public com.google.protobuf.ByteString
        getPotUsersBytes(int index) {
      return potUsers_.getByteString(index);
    }

    public static final int POT_GROUPS_FIELD_NUMBER = 6;
    private com.google.protobuf.LazyStringList potGroups_;
    /**
     * <code>repeated string pot_groups = 6;</code>
     * @return A list containing the potGroups.
     */
    public com.google.protobuf.ProtocolStringList
        getPotGroupsList() {
      return potGroups_;
    }
    /**
     * <code>repeated string pot_groups = 6;</code>
     * @return The count of potGroups.
     */
    public int getPotGroupsCount() {
      return potGroups_.size();
    }
    /**
     * <code>repeated string pot_groups = 6;</code>
     * @param index The index of the element to return.
     * @return The potGroups at the given index.
     */
    public java.lang.String getPotGroups(int index) {
      return potGroups_.get(index);
    }
    /**
     * <code>repeated string pot_groups = 6;</code>
     * @param index The index of the value to return.
     * @return The bytes of the potGroups at the given index.
     */
    public com.google.protobuf.ByteString
        getPotGroupsBytes(int index) {
      return potGroups_.getByteString(index);
    }

    public static final int EXCLUDED_USERS_FIELD_NUMBER = 7;
    private com.google.protobuf.LazyStringList excludedUsers_;
    /**
     * <code>repeated string excluded_users = 7;</code>
     * @return A list containing the excludedUsers.
     */
    public com.google.protobuf.ProtocolStringList
        getExcludedUsersList() {
      return excludedUsers_;
    }
    /**
     * <code>repeated string excluded_users = 7;</code>
     * @return The count of excludedUsers.
     */
    public int getExcludedUsersCount() {
      return excludedUsers_.size();
    }
    /**
     * <code>repeated string excluded_users = 7;</code>
     * @param index The index of the element to return.
     * @return The excludedUsers at the given index.
     */
    public java.lang.String getExcludedUsers(int index) {
      return excludedUsers_.get(index);
    }
    /**
     * <code>repeated string excluded_users = 7;</code>
     * @param index The index of the value to return.
     * @return The bytes of the excludedUsers at the given index.
     */
    public com.google.protobuf.ByteString
        getExcludedUsersBytes(int index) {
      return excludedUsers_.getByteString(index);
    }

    public static final int ADMIN_USERS_FIELD_NUMBER = 8;
    private com.google.protobuf.LazyStringList adminUsers_;
    /**
     * <code>repeated string admin_users = 8;</code>
     * @return A list containing the adminUsers.
     */
    public com.google.protobuf.ProtocolStringList
        getAdminUsersList() {
      return adminUsers_;
    }
    /**
     * <code>repeated string admin_users = 8;</code>
     * @return The count of adminUsers.
     */
    public int getAdminUsersCount() {
      return adminUsers_.size();
    }
    /**
     * <code>repeated string admin_users = 8;</code>
     * @param index The index of the element to return.
     * @return The adminUsers at the given index.
     */
    public java.lang.String getAdminUsers(int index) {
      return adminUsers_.get(index);
    }
    /**
     * <code>repeated string admin_users = 8;</code>
     * @param index The index of the value to return.
     * @return The bytes of the adminUsers at the given index.
     */
    public com.google.protobuf.ByteString
        getAdminUsersBytes(int index) {
      return adminUsers_.getByteString(index);
    }

    public static final int ADMIN_GROUPS_FIELD_NUMBER = 9;
    private com.google.protobuf.LazyStringList adminGroups_;
    /**
     * <code>repeated string admin_groups = 9;</code>
     * @return A list containing the adminGroups.
     */
    public com.google.protobuf.ProtocolStringList
        getAdminGroupsList() {
      return adminGroups_;
    }
    /**
     * <code>repeated string admin_groups = 9;</code>
     * @return The count of adminGroups.
     */
    public int getAdminGroupsCount() {
      return adminGroups_.size();
    }
    /**
     * <code>repeated string admin_groups = 9;</code>
     * @param index The index of the element to return.
     * @return The adminGroups at the given index.
     */
    public java.lang.String getAdminGroups(int index) {
      return adminGroups_.get(index);
    }
    /**
     * <code>repeated string admin_groups = 9;</code>
     * @param index The index of the value to return.
     * @return The bytes of the adminGroups at the given index.
     */
    public com.google.protobuf.ByteString
        getAdminGroupsBytes(int index) {
      return adminGroups_.getByteString(index);
    }

    public static final int TASK_REFERENCE_NAME_FIELD_NUMBER = 10;
    private volatile java.lang.Object taskReferenceName_;
    /**
     * <code>string task_reference_name = 10;</code>
     * @return Whether the taskReferenceName field is set.
     */
    @java.lang.Override
    public boolean hasTaskReferenceName() {
      return ((bitField0_ & 0x00000010) != 0);
    }
    /**
     * <code>string task_reference_name = 10;</code>
     * @return The taskReferenceName.
     */
    @java.lang.Override
    public java.lang.String getTaskReferenceName() {
      java.lang.Object ref = taskReferenceName_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        taskReferenceName_ = s;
        return s;
      }
    }
    /**
     * <code>string task_reference_name = 10;</code>
     * @return The bytes for taskReferenceName.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getTaskReferenceNameBytes() {
      java.lang.Object ref = taskReferenceName_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        taskReferenceName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int COMMENTS_FIELD_NUMBER = 11;
    private java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment> comments_;
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
     */
    @java.lang.Override
    public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment> getCommentsList() {
      return comments_;
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
     */
    @java.lang.Override
    public java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.CommentOrBuilder> 
        getCommentsOrBuilderList() {
      return comments_;
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
     */
    @java.lang.Override
    public int getCommentsCount() {
      return comments_.size();
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment getComments(int index) {
      return comments_.get(index);
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.CommentOrBuilder getCommentsOrBuilder(
        int index) {
      return comments_.get(index);
    }

    public static final int ATTACHMENTS_FIELD_NUMBER = 12;
    private java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment> attachments_;
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
     */
    @java.lang.Override
    public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment> getAttachmentsList() {
      return attachments_;
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
     */
    @java.lang.Override
    public java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.AttachmentOrBuilder> 
        getAttachmentsOrBuilderList() {
      return attachments_;
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
     */
    @java.lang.Override
    public int getAttachmentsCount() {
      return attachments_.size();
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment getAttachments(int index) {
      return attachments_.get(index);
    }
    /**
     * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
     */
    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.AttachmentOrBuilder getAttachmentsOrBuilder(
        int index) {
      return attachments_.get(index);
    }

    public static final int START_DEADLINES_FIELD_NUMBER = 13;
    private static final class StartDeadlinesDefaultEntryHolder {
      static final com.google.protobuf.MapEntry<
          java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> defaultEntry =
              com.google.protobuf.MapEntry
              .<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline>newDefaultInstance(
                  org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_StartDeadlinesEntry_descriptor, 
                  com.google.protobuf.WireFormat.FieldType.STRING,
                  "",
                  com.google.protobuf.WireFormat.FieldType.MESSAGE,
                  org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline.getDefaultInstance());
    }
    private com.google.protobuf.MapField<
        java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> startDeadlines_;
    private com.google.protobuf.MapField<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline>
    internalGetStartDeadlines() {
      if (startDeadlines_ == null) {
        return com.google.protobuf.MapField.emptyMapField(
            StartDeadlinesDefaultEntryHolder.defaultEntry);
      }
      return startDeadlines_;
    }

    public int getStartDeadlinesCount() {
      return internalGetStartDeadlines().getMap().size();
    }
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; start_deadlines = 13;</code>
     */

    @java.lang.Override
    public boolean containsStartDeadlines(
        java.lang.String key) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      return internalGetStartDeadlines().getMap().containsKey(key);
    }
    /**
     * Use {@link #getStartDeadlinesMap()} instead.
     */
    @java.lang.Override
    @java.lang.Deprecated
    public java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> getStartDeadlines() {
      return getStartDeadlinesMap();
    }
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; start_deadlines = 13;</code>
     */
    @java.lang.Override

    public java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> getStartDeadlinesMap() {
      return internalGetStartDeadlines().getMap();
    }
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; start_deadlines = 13;</code>
     */
    @java.lang.Override

    public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline getStartDeadlinesOrDefault(
        java.lang.String key,
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline defaultValue) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> map =
          internalGetStartDeadlines().getMap();
      return map.containsKey(key) ? map.get(key) : defaultValue;
    }
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; start_deadlines = 13;</code>
     */
    @java.lang.Override

    public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline getStartDeadlinesOrThrow(
        java.lang.String key) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> map =
          internalGetStartDeadlines().getMap();
      if (!map.containsKey(key)) {
        throw new java.lang.IllegalArgumentException();
      }
      return map.get(key);
    }

    public static final int COMPLETED_DEADLINES_FIELD_NUMBER = 14;
    private static final class CompletedDeadlinesDefaultEntryHolder {
      static final com.google.protobuf.MapEntry<
          java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> defaultEntry =
              com.google.protobuf.MapEntry
              .<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline>newDefaultInstance(
                  org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_CompletedDeadlinesEntry_descriptor, 
                  com.google.protobuf.WireFormat.FieldType.STRING,
                  "",
                  com.google.protobuf.WireFormat.FieldType.MESSAGE,
                  org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline.getDefaultInstance());
    }
    private com.google.protobuf.MapField<
        java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> completedDeadlines_;
    private com.google.protobuf.MapField<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline>
    internalGetCompletedDeadlines() {
      if (completedDeadlines_ == null) {
        return com.google.protobuf.MapField.emptyMapField(
            CompletedDeadlinesDefaultEntryHolder.defaultEntry);
      }
      return completedDeadlines_;
    }

    public int getCompletedDeadlinesCount() {
      return internalGetCompletedDeadlines().getMap().size();
    }
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; completed_deadlines = 14;</code>
     */

    @java.lang.Override
    public boolean containsCompletedDeadlines(
        java.lang.String key) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      return internalGetCompletedDeadlines().getMap().containsKey(key);
    }
    /**
     * Use {@link #getCompletedDeadlinesMap()} instead.
     */
    @java.lang.Override
    @java.lang.Deprecated
    public java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> getCompletedDeadlines() {
      return getCompletedDeadlinesMap();
    }
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; completed_deadlines = 14;</code>
     */
    @java.lang.Override

    public java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> getCompletedDeadlinesMap() {
      return internalGetCompletedDeadlines().getMap();
    }
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; completed_deadlines = 14;</code>
     */
    @java.lang.Override

    public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline getCompletedDeadlinesOrDefault(
        java.lang.String key,
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline defaultValue) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> map =
          internalGetCompletedDeadlines().getMap();
      return map.containsKey(key) ? map.get(key) : defaultValue;
    }
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; completed_deadlines = 14;</code>
     */
    @java.lang.Override

    public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline getCompletedDeadlinesOrThrow(
        java.lang.String key) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> map =
          internalGetCompletedDeadlines().getMap();
      if (!map.containsKey(key)) {
        throw new java.lang.IllegalArgumentException();
      }
      return map.get(key);
    }

    public static final int START_REASSIGMENTS_FIELD_NUMBER = 15;
    private static final class StartReassigmentsDefaultEntryHolder {
      static final com.google.protobuf.MapEntry<
          java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> defaultEntry =
              com.google.protobuf.MapEntry
              .<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment>newDefaultInstance(
                  org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_StartReassigmentsEntry_descriptor, 
                  com.google.protobuf.WireFormat.FieldType.STRING,
                  "",
                  com.google.protobuf.WireFormat.FieldType.MESSAGE,
                  org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment.getDefaultInstance());
    }
    private com.google.protobuf.MapField<
        java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> startReassigments_;
    private com.google.protobuf.MapField<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment>
    internalGetStartReassigments() {
      if (startReassigments_ == null) {
        return com.google.protobuf.MapField.emptyMapField(
            StartReassigmentsDefaultEntryHolder.defaultEntry);
      }
      return startReassigments_;
    }

    public int getStartReassigmentsCount() {
      return internalGetStartReassigments().getMap().size();
    }
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; start_reassigments = 15;</code>
     */

    @java.lang.Override
    public boolean containsStartReassigments(
        java.lang.String key) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      return internalGetStartReassigments().getMap().containsKey(key);
    }
    /**
     * Use {@link #getStartReassigmentsMap()} instead.
     */
    @java.lang.Override
    @java.lang.Deprecated
    public java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> getStartReassigments() {
      return getStartReassigmentsMap();
    }
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; start_reassigments = 15;</code>
     */
    @java.lang.Override

    public java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> getStartReassigmentsMap() {
      return internalGetStartReassigments().getMap();
    }
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; start_reassigments = 15;</code>
     */
    @java.lang.Override

    public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment getStartReassigmentsOrDefault(
        java.lang.String key,
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment defaultValue) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> map =
          internalGetStartReassigments().getMap();
      return map.containsKey(key) ? map.get(key) : defaultValue;
    }
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; start_reassigments = 15;</code>
     */
    @java.lang.Override

    public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment getStartReassigmentsOrThrow(
        java.lang.String key) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> map =
          internalGetStartReassigments().getMap();
      if (!map.containsKey(key)) {
        throw new java.lang.IllegalArgumentException();
      }
      return map.get(key);
    }

    public static final int COMPLETED_REASSIGMENTS_FIELD_NUMBER = 16;
    private static final class CompletedReassigmentsDefaultEntryHolder {
      static final com.google.protobuf.MapEntry<
          java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> defaultEntry =
              com.google.protobuf.MapEntry
              .<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment>newDefaultInstance(
                  org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_CompletedReassigmentsEntry_descriptor, 
                  com.google.protobuf.WireFormat.FieldType.STRING,
                  "",
                  com.google.protobuf.WireFormat.FieldType.MESSAGE,
                  org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment.getDefaultInstance());
    }
    private com.google.protobuf.MapField<
        java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> completedReassigments_;
    private com.google.protobuf.MapField<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment>
    internalGetCompletedReassigments() {
      if (completedReassigments_ == null) {
        return com.google.protobuf.MapField.emptyMapField(
            CompletedReassigmentsDefaultEntryHolder.defaultEntry);
      }
      return completedReassigments_;
    }

    public int getCompletedReassigmentsCount() {
      return internalGetCompletedReassigments().getMap().size();
    }
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; completed_reassigments = 16;</code>
     */

    @java.lang.Override
    public boolean containsCompletedReassigments(
        java.lang.String key) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      return internalGetCompletedReassigments().getMap().containsKey(key);
    }
    /**
     * Use {@link #getCompletedReassigmentsMap()} instead.
     */
    @java.lang.Override
    @java.lang.Deprecated
    public java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> getCompletedReassigments() {
      return getCompletedReassigmentsMap();
    }
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; completed_reassigments = 16;</code>
     */
    @java.lang.Override

    public java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> getCompletedReassigmentsMap() {
      return internalGetCompletedReassigments().getMap();
    }
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; completed_reassigments = 16;</code>
     */
    @java.lang.Override

    public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment getCompletedReassigmentsOrDefault(
        java.lang.String key,
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment defaultValue) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> map =
          internalGetCompletedReassigments().getMap();
      return map.containsKey(key) ? map.get(key) : defaultValue;
    }
    /**
     * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; completed_reassigments = 16;</code>
     */
    @java.lang.Override

    public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment getCompletedReassigmentsOrThrow(
        java.lang.String key) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> map =
          internalGetCompletedReassigments().getMap();
      if (!map.containsKey(key)) {
        throw new java.lang.IllegalArgumentException();
      }
      return map.get(key);
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
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, taskName_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, taskDescription_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 3, taskPriority_);
      }
      if (((bitField0_ & 0x00000008) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 4, actualOwner_);
      }
      for (int i = 0; i < potUsers_.size(); i++) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 5, potUsers_.getRaw(i));
      }
      for (int i = 0; i < potGroups_.size(); i++) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 6, potGroups_.getRaw(i));
      }
      for (int i = 0; i < excludedUsers_.size(); i++) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 7, excludedUsers_.getRaw(i));
      }
      for (int i = 0; i < adminUsers_.size(); i++) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 8, adminUsers_.getRaw(i));
      }
      for (int i = 0; i < adminGroups_.size(); i++) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 9, adminGroups_.getRaw(i));
      }
      if (((bitField0_ & 0x00000010) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 10, taskReferenceName_);
      }
      for (int i = 0; i < comments_.size(); i++) {
        output.writeMessage(11, comments_.get(i));
      }
      for (int i = 0; i < attachments_.size(); i++) {
        output.writeMessage(12, attachments_.get(i));
      }
      com.google.protobuf.GeneratedMessageV3
        .serializeStringMapTo(
          output,
          internalGetStartDeadlines(),
          StartDeadlinesDefaultEntryHolder.defaultEntry,
          13);
      com.google.protobuf.GeneratedMessageV3
        .serializeStringMapTo(
          output,
          internalGetCompletedDeadlines(),
          CompletedDeadlinesDefaultEntryHolder.defaultEntry,
          14);
      com.google.protobuf.GeneratedMessageV3
        .serializeStringMapTo(
          output,
          internalGetStartReassigments(),
          StartReassigmentsDefaultEntryHolder.defaultEntry,
          15);
      com.google.protobuf.GeneratedMessageV3
        .serializeStringMapTo(
          output,
          internalGetCompletedReassigments(),
          CompletedReassigmentsDefaultEntryHolder.defaultEntry,
          16);
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, taskName_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, taskDescription_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(3, taskPriority_);
      }
      if (((bitField0_ & 0x00000008) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(4, actualOwner_);
      }
      {
        int dataSize = 0;
        for (int i = 0; i < potUsers_.size(); i++) {
          dataSize += computeStringSizeNoTag(potUsers_.getRaw(i));
        }
        size += dataSize;
        size += 1 * getPotUsersList().size();
      }
      {
        int dataSize = 0;
        for (int i = 0; i < potGroups_.size(); i++) {
          dataSize += computeStringSizeNoTag(potGroups_.getRaw(i));
        }
        size += dataSize;
        size += 1 * getPotGroupsList().size();
      }
      {
        int dataSize = 0;
        for (int i = 0; i < excludedUsers_.size(); i++) {
          dataSize += computeStringSizeNoTag(excludedUsers_.getRaw(i));
        }
        size += dataSize;
        size += 1 * getExcludedUsersList().size();
      }
      {
        int dataSize = 0;
        for (int i = 0; i < adminUsers_.size(); i++) {
          dataSize += computeStringSizeNoTag(adminUsers_.getRaw(i));
        }
        size += dataSize;
        size += 1 * getAdminUsersList().size();
      }
      {
        int dataSize = 0;
        for (int i = 0; i < adminGroups_.size(); i++) {
          dataSize += computeStringSizeNoTag(adminGroups_.getRaw(i));
        }
        size += dataSize;
        size += 1 * getAdminGroupsList().size();
      }
      if (((bitField0_ & 0x00000010) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(10, taskReferenceName_);
      }
      for (int i = 0; i < comments_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(11, comments_.get(i));
      }
      for (int i = 0; i < attachments_.size(); i++) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(12, attachments_.get(i));
      }
      for (java.util.Map.Entry<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> entry
           : internalGetStartDeadlines().getMap().entrySet()) {
        com.google.protobuf.MapEntry<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline>
        startDeadlines__ = StartDeadlinesDefaultEntryHolder.defaultEntry.newBuilderForType()
            .setKey(entry.getKey())
            .setValue(entry.getValue())
            .build();
        size += com.google.protobuf.CodedOutputStream
            .computeMessageSize(13, startDeadlines__);
      }
      for (java.util.Map.Entry<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> entry
           : internalGetCompletedDeadlines().getMap().entrySet()) {
        com.google.protobuf.MapEntry<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline>
        completedDeadlines__ = CompletedDeadlinesDefaultEntryHolder.defaultEntry.newBuilderForType()
            .setKey(entry.getKey())
            .setValue(entry.getValue())
            .build();
        size += com.google.protobuf.CodedOutputStream
            .computeMessageSize(14, completedDeadlines__);
      }
      for (java.util.Map.Entry<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> entry
           : internalGetStartReassigments().getMap().entrySet()) {
        com.google.protobuf.MapEntry<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment>
        startReassigments__ = StartReassigmentsDefaultEntryHolder.defaultEntry.newBuilderForType()
            .setKey(entry.getKey())
            .setValue(entry.getValue())
            .build();
        size += com.google.protobuf.CodedOutputStream
            .computeMessageSize(15, startReassigments__);
      }
      for (java.util.Map.Entry<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> entry
           : internalGetCompletedReassigments().getMap().entrySet()) {
        com.google.protobuf.MapEntry<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment>
        completedReassigments__ = CompletedReassigmentsDefaultEntryHolder.defaultEntry.newBuilderForType()
            .setKey(entry.getKey())
            .setValue(entry.getValue())
            .build();
        size += com.google.protobuf.CodedOutputStream
            .computeMessageSize(16, completedReassigments__);
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
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData other = (org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData) obj;

      if (hasTaskName() != other.hasTaskName()) return false;
      if (hasTaskName()) {
        if (!getTaskName()
            .equals(other.getTaskName())) return false;
      }
      if (hasTaskDescription() != other.hasTaskDescription()) return false;
      if (hasTaskDescription()) {
        if (!getTaskDescription()
            .equals(other.getTaskDescription())) return false;
      }
      if (hasTaskPriority() != other.hasTaskPriority()) return false;
      if (hasTaskPriority()) {
        if (!getTaskPriority()
            .equals(other.getTaskPriority())) return false;
      }
      if (hasActualOwner() != other.hasActualOwner()) return false;
      if (hasActualOwner()) {
        if (!getActualOwner()
            .equals(other.getActualOwner())) return false;
      }
      if (!getPotUsersList()
          .equals(other.getPotUsersList())) return false;
      if (!getPotGroupsList()
          .equals(other.getPotGroupsList())) return false;
      if (!getExcludedUsersList()
          .equals(other.getExcludedUsersList())) return false;
      if (!getAdminUsersList()
          .equals(other.getAdminUsersList())) return false;
      if (!getAdminGroupsList()
          .equals(other.getAdminGroupsList())) return false;
      if (hasTaskReferenceName() != other.hasTaskReferenceName()) return false;
      if (hasTaskReferenceName()) {
        if (!getTaskReferenceName()
            .equals(other.getTaskReferenceName())) return false;
      }
      if (!getCommentsList()
          .equals(other.getCommentsList())) return false;
      if (!getAttachmentsList()
          .equals(other.getAttachmentsList())) return false;
      if (!internalGetStartDeadlines().equals(
          other.internalGetStartDeadlines())) return false;
      if (!internalGetCompletedDeadlines().equals(
          other.internalGetCompletedDeadlines())) return false;
      if (!internalGetStartReassigments().equals(
          other.internalGetStartReassigments())) return false;
      if (!internalGetCompletedReassigments().equals(
          other.internalGetCompletedReassigments())) return false;
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
      if (hasTaskName()) {
        hash = (37 * hash) + TASK_NAME_FIELD_NUMBER;
        hash = (53 * hash) + getTaskName().hashCode();
      }
      if (hasTaskDescription()) {
        hash = (37 * hash) + TASK_DESCRIPTION_FIELD_NUMBER;
        hash = (53 * hash) + getTaskDescription().hashCode();
      }
      if (hasTaskPriority()) {
        hash = (37 * hash) + TASK_PRIORITY_FIELD_NUMBER;
        hash = (53 * hash) + getTaskPriority().hashCode();
      }
      if (hasActualOwner()) {
        hash = (37 * hash) + ACTUAL_OWNER_FIELD_NUMBER;
        hash = (53 * hash) + getActualOwner().hashCode();
      }
      if (getPotUsersCount() > 0) {
        hash = (37 * hash) + POT_USERS_FIELD_NUMBER;
        hash = (53 * hash) + getPotUsersList().hashCode();
      }
      if (getPotGroupsCount() > 0) {
        hash = (37 * hash) + POT_GROUPS_FIELD_NUMBER;
        hash = (53 * hash) + getPotGroupsList().hashCode();
      }
      if (getExcludedUsersCount() > 0) {
        hash = (37 * hash) + EXCLUDED_USERS_FIELD_NUMBER;
        hash = (53 * hash) + getExcludedUsersList().hashCode();
      }
      if (getAdminUsersCount() > 0) {
        hash = (37 * hash) + ADMIN_USERS_FIELD_NUMBER;
        hash = (53 * hash) + getAdminUsersList().hashCode();
      }
      if (getAdminGroupsCount() > 0) {
        hash = (37 * hash) + ADMIN_GROUPS_FIELD_NUMBER;
        hash = (53 * hash) + getAdminGroupsList().hashCode();
      }
      if (hasTaskReferenceName()) {
        hash = (37 * hash) + TASK_REFERENCE_NAME_FIELD_NUMBER;
        hash = (53 * hash) + getTaskReferenceName().hashCode();
      }
      if (getCommentsCount() > 0) {
        hash = (37 * hash) + COMMENTS_FIELD_NUMBER;
        hash = (53 * hash) + getCommentsList().hashCode();
      }
      if (getAttachmentsCount() > 0) {
        hash = (37 * hash) + ATTACHMENTS_FIELD_NUMBER;
        hash = (53 * hash) + getAttachmentsList().hashCode();
      }
      if (!internalGetStartDeadlines().getMap().isEmpty()) {
        hash = (37 * hash) + START_DEADLINES_FIELD_NUMBER;
        hash = (53 * hash) + internalGetStartDeadlines().hashCode();
      }
      if (!internalGetCompletedDeadlines().getMap().isEmpty()) {
        hash = (37 * hash) + COMPLETED_DEADLINES_FIELD_NUMBER;
        hash = (53 * hash) + internalGetCompletedDeadlines().hashCode();
      }
      if (!internalGetStartReassigments().getMap().isEmpty()) {
        hash = (37 * hash) + START_REASSIGMENTS_FIELD_NUMBER;
        hash = (53 * hash) + internalGetStartReassigments().hashCode();
      }
      if (!internalGetCompletedReassigments().getMap().isEmpty()) {
        hash = (37 * hash) + COMPLETED_REASSIGMENTS_FIELD_NUMBER;
        hash = (53 * hash) + internalGetCompletedReassigments().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData parseFrom(
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
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData prototype) {
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
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.HumanTaskWorkItemData}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.HumanTaskWorkItemData)
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemDataOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_descriptor;
      }

      @SuppressWarnings({"rawtypes"})
      protected com.google.protobuf.MapField internalGetMapField(
          int number) {
        switch (number) {
          case 13:
            return internalGetStartDeadlines();
          case 14:
            return internalGetCompletedDeadlines();
          case 15:
            return internalGetStartReassigments();
          case 16:
            return internalGetCompletedReassigments();
          default:
            throw new RuntimeException(
                "Invalid map field number: " + number);
        }
      }
      @SuppressWarnings({"rawtypes"})
      protected com.google.protobuf.MapField internalGetMutableMapField(
          int number) {
        switch (number) {
          case 13:
            return internalGetMutableStartDeadlines();
          case 14:
            return internalGetMutableCompletedDeadlines();
          case 15:
            return internalGetMutableStartReassigments();
          case 16:
            return internalGetMutableCompletedReassigments();
          default:
            throw new RuntimeException(
                "Invalid map field number: " + number);
        }
      }
      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData.class, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData.newBuilder()
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
          getCommentsFieldBuilder();
          getAttachmentsFieldBuilder();
        }
      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        taskName_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        taskDescription_ = "";
        bitField0_ = (bitField0_ & ~0x00000002);
        taskPriority_ = "";
        bitField0_ = (bitField0_ & ~0x00000004);
        actualOwner_ = "";
        bitField0_ = (bitField0_ & ~0x00000008);
        potUsers_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000010);
        potGroups_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000020);
        excludedUsers_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000040);
        adminUsers_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000080);
        adminGroups_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000100);
        taskReferenceName_ = "";
        bitField0_ = (bitField0_ & ~0x00000200);
        if (commentsBuilder_ == null) {
          comments_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000400);
        } else {
          commentsBuilder_.clear();
        }
        if (attachmentsBuilder_ == null) {
          attachments_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000800);
        } else {
          attachmentsBuilder_.clear();
        }
        internalGetMutableStartDeadlines().clear();
        internalGetMutableCompletedDeadlines().clear();
        internalGetMutableStartReassigments().clear();
        internalGetMutableCompletedReassigments().clear();
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData build() {
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData result = new org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          to_bitField0_ |= 0x00000001;
        }
        result.taskName_ = taskName_;
        if (((from_bitField0_ & 0x00000002) != 0)) {
          to_bitField0_ |= 0x00000002;
        }
        result.taskDescription_ = taskDescription_;
        if (((from_bitField0_ & 0x00000004) != 0)) {
          to_bitField0_ |= 0x00000004;
        }
        result.taskPriority_ = taskPriority_;
        if (((from_bitField0_ & 0x00000008) != 0)) {
          to_bitField0_ |= 0x00000008;
        }
        result.actualOwner_ = actualOwner_;
        if (((bitField0_ & 0x00000010) != 0)) {
          potUsers_ = potUsers_.getUnmodifiableView();
          bitField0_ = (bitField0_ & ~0x00000010);
        }
        result.potUsers_ = potUsers_;
        if (((bitField0_ & 0x00000020) != 0)) {
          potGroups_ = potGroups_.getUnmodifiableView();
          bitField0_ = (bitField0_ & ~0x00000020);
        }
        result.potGroups_ = potGroups_;
        if (((bitField0_ & 0x00000040) != 0)) {
          excludedUsers_ = excludedUsers_.getUnmodifiableView();
          bitField0_ = (bitField0_ & ~0x00000040);
        }
        result.excludedUsers_ = excludedUsers_;
        if (((bitField0_ & 0x00000080) != 0)) {
          adminUsers_ = adminUsers_.getUnmodifiableView();
          bitField0_ = (bitField0_ & ~0x00000080);
        }
        result.adminUsers_ = adminUsers_;
        if (((bitField0_ & 0x00000100) != 0)) {
          adminGroups_ = adminGroups_.getUnmodifiableView();
          bitField0_ = (bitField0_ & ~0x00000100);
        }
        result.adminGroups_ = adminGroups_;
        if (((from_bitField0_ & 0x00000200) != 0)) {
          to_bitField0_ |= 0x00000010;
        }
        result.taskReferenceName_ = taskReferenceName_;
        if (commentsBuilder_ == null) {
          if (((bitField0_ & 0x00000400) != 0)) {
            comments_ = java.util.Collections.unmodifiableList(comments_);
            bitField0_ = (bitField0_ & ~0x00000400);
          }
          result.comments_ = comments_;
        } else {
          result.comments_ = commentsBuilder_.build();
        }
        if (attachmentsBuilder_ == null) {
          if (((bitField0_ & 0x00000800) != 0)) {
            attachments_ = java.util.Collections.unmodifiableList(attachments_);
            bitField0_ = (bitField0_ & ~0x00000800);
          }
          result.attachments_ = attachments_;
        } else {
          result.attachments_ = attachmentsBuilder_.build();
        }
        result.startDeadlines_ = internalGetStartDeadlines();
        result.startDeadlines_.makeImmutable();
        result.completedDeadlines_ = internalGetCompletedDeadlines();
        result.completedDeadlines_.makeImmutable();
        result.startReassigments_ = internalGetStartReassigments();
        result.startReassigments_.makeImmutable();
        result.completedReassigments_ = internalGetCompletedReassigments();
        result.completedReassigments_.makeImmutable();
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
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData.getDefaultInstance()) return this;
        if (other.hasTaskName()) {
          bitField0_ |= 0x00000001;
          taskName_ = other.taskName_;
          onChanged();
        }
        if (other.hasTaskDescription()) {
          bitField0_ |= 0x00000002;
          taskDescription_ = other.taskDescription_;
          onChanged();
        }
        if (other.hasTaskPriority()) {
          bitField0_ |= 0x00000004;
          taskPriority_ = other.taskPriority_;
          onChanged();
        }
        if (other.hasActualOwner()) {
          bitField0_ |= 0x00000008;
          actualOwner_ = other.actualOwner_;
          onChanged();
        }
        if (!other.potUsers_.isEmpty()) {
          if (potUsers_.isEmpty()) {
            potUsers_ = other.potUsers_;
            bitField0_ = (bitField0_ & ~0x00000010);
          } else {
            ensurePotUsersIsMutable();
            potUsers_.addAll(other.potUsers_);
          }
          onChanged();
        }
        if (!other.potGroups_.isEmpty()) {
          if (potGroups_.isEmpty()) {
            potGroups_ = other.potGroups_;
            bitField0_ = (bitField0_ & ~0x00000020);
          } else {
            ensurePotGroupsIsMutable();
            potGroups_.addAll(other.potGroups_);
          }
          onChanged();
        }
        if (!other.excludedUsers_.isEmpty()) {
          if (excludedUsers_.isEmpty()) {
            excludedUsers_ = other.excludedUsers_;
            bitField0_ = (bitField0_ & ~0x00000040);
          } else {
            ensureExcludedUsersIsMutable();
            excludedUsers_.addAll(other.excludedUsers_);
          }
          onChanged();
        }
        if (!other.adminUsers_.isEmpty()) {
          if (adminUsers_.isEmpty()) {
            adminUsers_ = other.adminUsers_;
            bitField0_ = (bitField0_ & ~0x00000080);
          } else {
            ensureAdminUsersIsMutable();
            adminUsers_.addAll(other.adminUsers_);
          }
          onChanged();
        }
        if (!other.adminGroups_.isEmpty()) {
          if (adminGroups_.isEmpty()) {
            adminGroups_ = other.adminGroups_;
            bitField0_ = (bitField0_ & ~0x00000100);
          } else {
            ensureAdminGroupsIsMutable();
            adminGroups_.addAll(other.adminGroups_);
          }
          onChanged();
        }
        if (other.hasTaskReferenceName()) {
          bitField0_ |= 0x00000200;
          taskReferenceName_ = other.taskReferenceName_;
          onChanged();
        }
        if (commentsBuilder_ == null) {
          if (!other.comments_.isEmpty()) {
            if (comments_.isEmpty()) {
              comments_ = other.comments_;
              bitField0_ = (bitField0_ & ~0x00000400);
            } else {
              ensureCommentsIsMutable();
              comments_.addAll(other.comments_);
            }
            onChanged();
          }
        } else {
          if (!other.comments_.isEmpty()) {
            if (commentsBuilder_.isEmpty()) {
              commentsBuilder_.dispose();
              commentsBuilder_ = null;
              comments_ = other.comments_;
              bitField0_ = (bitField0_ & ~0x00000400);
              commentsBuilder_ = 
                com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                   getCommentsFieldBuilder() : null;
            } else {
              commentsBuilder_.addAllMessages(other.comments_);
            }
          }
        }
        if (attachmentsBuilder_ == null) {
          if (!other.attachments_.isEmpty()) {
            if (attachments_.isEmpty()) {
              attachments_ = other.attachments_;
              bitField0_ = (bitField0_ & ~0x00000800);
            } else {
              ensureAttachmentsIsMutable();
              attachments_.addAll(other.attachments_);
            }
            onChanged();
          }
        } else {
          if (!other.attachments_.isEmpty()) {
            if (attachmentsBuilder_.isEmpty()) {
              attachmentsBuilder_.dispose();
              attachmentsBuilder_ = null;
              attachments_ = other.attachments_;
              bitField0_ = (bitField0_ & ~0x00000800);
              attachmentsBuilder_ = 
                com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                   getAttachmentsFieldBuilder() : null;
            } else {
              attachmentsBuilder_.addAllMessages(other.attachments_);
            }
          }
        }
        internalGetMutableStartDeadlines().mergeFrom(
            other.internalGetStartDeadlines());
        internalGetMutableCompletedDeadlines().mergeFrom(
            other.internalGetCompletedDeadlines());
        internalGetMutableStartReassigments().mergeFrom(
            other.internalGetStartReassigments());
        internalGetMutableCompletedReassigments().mergeFrom(
            other.internalGetCompletedReassigments());
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
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private java.lang.Object taskName_ = "";
      /**
       * <code>string task_name = 1;</code>
       * @return Whether the taskName field is set.
       */
      public boolean hasTaskName() {
        return ((bitField0_ & 0x00000001) != 0);
      }
      /**
       * <code>string task_name = 1;</code>
       * @return The taskName.
       */
      public java.lang.String getTaskName() {
        java.lang.Object ref = taskName_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          taskName_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string task_name = 1;</code>
       * @return The bytes for taskName.
       */
      public com.google.protobuf.ByteString
          getTaskNameBytes() {
        java.lang.Object ref = taskName_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          taskName_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string task_name = 1;</code>
       * @param value The taskName to set.
       * @return This builder for chaining.
       */
      public Builder setTaskName(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        taskName_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string task_name = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearTaskName() {
        bitField0_ = (bitField0_ & ~0x00000001);
        taskName_ = getDefaultInstance().getTaskName();
        onChanged();
        return this;
      }
      /**
       * <code>string task_name = 1;</code>
       * @param value The bytes for taskName to set.
       * @return This builder for chaining.
       */
      public Builder setTaskNameBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000001;
        taskName_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object taskDescription_ = "";
      /**
       * <code>string task_description = 2;</code>
       * @return Whether the taskDescription field is set.
       */
      public boolean hasTaskDescription() {
        return ((bitField0_ & 0x00000002) != 0);
      }
      /**
       * <code>string task_description = 2;</code>
       * @return The taskDescription.
       */
      public java.lang.String getTaskDescription() {
        java.lang.Object ref = taskDescription_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          taskDescription_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string task_description = 2;</code>
       * @return The bytes for taskDescription.
       */
      public com.google.protobuf.ByteString
          getTaskDescriptionBytes() {
        java.lang.Object ref = taskDescription_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          taskDescription_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string task_description = 2;</code>
       * @param value The taskDescription to set.
       * @return This builder for chaining.
       */
      public Builder setTaskDescription(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000002;
        taskDescription_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string task_description = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearTaskDescription() {
        bitField0_ = (bitField0_ & ~0x00000002);
        taskDescription_ = getDefaultInstance().getTaskDescription();
        onChanged();
        return this;
      }
      /**
       * <code>string task_description = 2;</code>
       * @param value The bytes for taskDescription to set.
       * @return This builder for chaining.
       */
      public Builder setTaskDescriptionBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000002;
        taskDescription_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object taskPriority_ = "";
      /**
       * <code>string task_priority = 3;</code>
       * @return Whether the taskPriority field is set.
       */
      public boolean hasTaskPriority() {
        return ((bitField0_ & 0x00000004) != 0);
      }
      /**
       * <code>string task_priority = 3;</code>
       * @return The taskPriority.
       */
      public java.lang.String getTaskPriority() {
        java.lang.Object ref = taskPriority_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          taskPriority_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string task_priority = 3;</code>
       * @return The bytes for taskPriority.
       */
      public com.google.protobuf.ByteString
          getTaskPriorityBytes() {
        java.lang.Object ref = taskPriority_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          taskPriority_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string task_priority = 3;</code>
       * @param value The taskPriority to set.
       * @return This builder for chaining.
       */
      public Builder setTaskPriority(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        taskPriority_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string task_priority = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearTaskPriority() {
        bitField0_ = (bitField0_ & ~0x00000004);
        taskPriority_ = getDefaultInstance().getTaskPriority();
        onChanged();
        return this;
      }
      /**
       * <code>string task_priority = 3;</code>
       * @param value The bytes for taskPriority to set.
       * @return This builder for chaining.
       */
      public Builder setTaskPriorityBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000004;
        taskPriority_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object actualOwner_ = "";
      /**
       * <code>string actual_owner = 4;</code>
       * @return Whether the actualOwner field is set.
       */
      public boolean hasActualOwner() {
        return ((bitField0_ & 0x00000008) != 0);
      }
      /**
       * <code>string actual_owner = 4;</code>
       * @return The actualOwner.
       */
      public java.lang.String getActualOwner() {
        java.lang.Object ref = actualOwner_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          actualOwner_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string actual_owner = 4;</code>
       * @return The bytes for actualOwner.
       */
      public com.google.protobuf.ByteString
          getActualOwnerBytes() {
        java.lang.Object ref = actualOwner_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          actualOwner_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string actual_owner = 4;</code>
       * @param value The actualOwner to set.
       * @return This builder for chaining.
       */
      public Builder setActualOwner(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000008;
        actualOwner_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string actual_owner = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearActualOwner() {
        bitField0_ = (bitField0_ & ~0x00000008);
        actualOwner_ = getDefaultInstance().getActualOwner();
        onChanged();
        return this;
      }
      /**
       * <code>string actual_owner = 4;</code>
       * @param value The bytes for actualOwner to set.
       * @return This builder for chaining.
       */
      public Builder setActualOwnerBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000008;
        actualOwner_ = value;
        onChanged();
        return this;
      }

      private com.google.protobuf.LazyStringList potUsers_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      private void ensurePotUsersIsMutable() {
        if (!((bitField0_ & 0x00000010) != 0)) {
          potUsers_ = new com.google.protobuf.LazyStringArrayList(potUsers_);
          bitField0_ |= 0x00000010;
         }
      }
      /**
       * <code>repeated string pot_users = 5;</code>
       * @return A list containing the potUsers.
       */
      public com.google.protobuf.ProtocolStringList
          getPotUsersList() {
        return potUsers_.getUnmodifiableView();
      }
      /**
       * <code>repeated string pot_users = 5;</code>
       * @return The count of potUsers.
       */
      public int getPotUsersCount() {
        return potUsers_.size();
      }
      /**
       * <code>repeated string pot_users = 5;</code>
       * @param index The index of the element to return.
       * @return The potUsers at the given index.
       */
      public java.lang.String getPotUsers(int index) {
        return potUsers_.get(index);
      }
      /**
       * <code>repeated string pot_users = 5;</code>
       * @param index The index of the value to return.
       * @return The bytes of the potUsers at the given index.
       */
      public com.google.protobuf.ByteString
          getPotUsersBytes(int index) {
        return potUsers_.getByteString(index);
      }
      /**
       * <code>repeated string pot_users = 5;</code>
       * @param index The index to set the value at.
       * @param value The potUsers to set.
       * @return This builder for chaining.
       */
      public Builder setPotUsers(
          int index, java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensurePotUsersIsMutable();
        potUsers_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string pot_users = 5;</code>
       * @param value The potUsers to add.
       * @return This builder for chaining.
       */
      public Builder addPotUsers(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensurePotUsersIsMutable();
        potUsers_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string pot_users = 5;</code>
       * @param values The potUsers to add.
       * @return This builder for chaining.
       */
      public Builder addAllPotUsers(
          java.lang.Iterable<java.lang.String> values) {
        ensurePotUsersIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, potUsers_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string pot_users = 5;</code>
       * @return This builder for chaining.
       */
      public Builder clearPotUsers() {
        potUsers_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000010);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string pot_users = 5;</code>
       * @param value The bytes of the potUsers to add.
       * @return This builder for chaining.
       */
      public Builder addPotUsersBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        ensurePotUsersIsMutable();
        potUsers_.add(value);
        onChanged();
        return this;
      }

      private com.google.protobuf.LazyStringList potGroups_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      private void ensurePotGroupsIsMutable() {
        if (!((bitField0_ & 0x00000020) != 0)) {
          potGroups_ = new com.google.protobuf.LazyStringArrayList(potGroups_);
          bitField0_ |= 0x00000020;
         }
      }
      /**
       * <code>repeated string pot_groups = 6;</code>
       * @return A list containing the potGroups.
       */
      public com.google.protobuf.ProtocolStringList
          getPotGroupsList() {
        return potGroups_.getUnmodifiableView();
      }
      /**
       * <code>repeated string pot_groups = 6;</code>
       * @return The count of potGroups.
       */
      public int getPotGroupsCount() {
        return potGroups_.size();
      }
      /**
       * <code>repeated string pot_groups = 6;</code>
       * @param index The index of the element to return.
       * @return The potGroups at the given index.
       */
      public java.lang.String getPotGroups(int index) {
        return potGroups_.get(index);
      }
      /**
       * <code>repeated string pot_groups = 6;</code>
       * @param index The index of the value to return.
       * @return The bytes of the potGroups at the given index.
       */
      public com.google.protobuf.ByteString
          getPotGroupsBytes(int index) {
        return potGroups_.getByteString(index);
      }
      /**
       * <code>repeated string pot_groups = 6;</code>
       * @param index The index to set the value at.
       * @param value The potGroups to set.
       * @return This builder for chaining.
       */
      public Builder setPotGroups(
          int index, java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensurePotGroupsIsMutable();
        potGroups_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string pot_groups = 6;</code>
       * @param value The potGroups to add.
       * @return This builder for chaining.
       */
      public Builder addPotGroups(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensurePotGroupsIsMutable();
        potGroups_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string pot_groups = 6;</code>
       * @param values The potGroups to add.
       * @return This builder for chaining.
       */
      public Builder addAllPotGroups(
          java.lang.Iterable<java.lang.String> values) {
        ensurePotGroupsIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, potGroups_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string pot_groups = 6;</code>
       * @return This builder for chaining.
       */
      public Builder clearPotGroups() {
        potGroups_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000020);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string pot_groups = 6;</code>
       * @param value The bytes of the potGroups to add.
       * @return This builder for chaining.
       */
      public Builder addPotGroupsBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        ensurePotGroupsIsMutable();
        potGroups_.add(value);
        onChanged();
        return this;
      }

      private com.google.protobuf.LazyStringList excludedUsers_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      private void ensureExcludedUsersIsMutable() {
        if (!((bitField0_ & 0x00000040) != 0)) {
          excludedUsers_ = new com.google.protobuf.LazyStringArrayList(excludedUsers_);
          bitField0_ |= 0x00000040;
         }
      }
      /**
       * <code>repeated string excluded_users = 7;</code>
       * @return A list containing the excludedUsers.
       */
      public com.google.protobuf.ProtocolStringList
          getExcludedUsersList() {
        return excludedUsers_.getUnmodifiableView();
      }
      /**
       * <code>repeated string excluded_users = 7;</code>
       * @return The count of excludedUsers.
       */
      public int getExcludedUsersCount() {
        return excludedUsers_.size();
      }
      /**
       * <code>repeated string excluded_users = 7;</code>
       * @param index The index of the element to return.
       * @return The excludedUsers at the given index.
       */
      public java.lang.String getExcludedUsers(int index) {
        return excludedUsers_.get(index);
      }
      /**
       * <code>repeated string excluded_users = 7;</code>
       * @param index The index of the value to return.
       * @return The bytes of the excludedUsers at the given index.
       */
      public com.google.protobuf.ByteString
          getExcludedUsersBytes(int index) {
        return excludedUsers_.getByteString(index);
      }
      /**
       * <code>repeated string excluded_users = 7;</code>
       * @param index The index to set the value at.
       * @param value The excludedUsers to set.
       * @return This builder for chaining.
       */
      public Builder setExcludedUsers(
          int index, java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureExcludedUsersIsMutable();
        excludedUsers_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string excluded_users = 7;</code>
       * @param value The excludedUsers to add.
       * @return This builder for chaining.
       */
      public Builder addExcludedUsers(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureExcludedUsersIsMutable();
        excludedUsers_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string excluded_users = 7;</code>
       * @param values The excludedUsers to add.
       * @return This builder for chaining.
       */
      public Builder addAllExcludedUsers(
          java.lang.Iterable<java.lang.String> values) {
        ensureExcludedUsersIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, excludedUsers_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string excluded_users = 7;</code>
       * @return This builder for chaining.
       */
      public Builder clearExcludedUsers() {
        excludedUsers_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000040);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string excluded_users = 7;</code>
       * @param value The bytes of the excludedUsers to add.
       * @return This builder for chaining.
       */
      public Builder addExcludedUsersBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        ensureExcludedUsersIsMutable();
        excludedUsers_.add(value);
        onChanged();
        return this;
      }

      private com.google.protobuf.LazyStringList adminUsers_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      private void ensureAdminUsersIsMutable() {
        if (!((bitField0_ & 0x00000080) != 0)) {
          adminUsers_ = new com.google.protobuf.LazyStringArrayList(adminUsers_);
          bitField0_ |= 0x00000080;
         }
      }
      /**
       * <code>repeated string admin_users = 8;</code>
       * @return A list containing the adminUsers.
       */
      public com.google.protobuf.ProtocolStringList
          getAdminUsersList() {
        return adminUsers_.getUnmodifiableView();
      }
      /**
       * <code>repeated string admin_users = 8;</code>
       * @return The count of adminUsers.
       */
      public int getAdminUsersCount() {
        return adminUsers_.size();
      }
      /**
       * <code>repeated string admin_users = 8;</code>
       * @param index The index of the element to return.
       * @return The adminUsers at the given index.
       */
      public java.lang.String getAdminUsers(int index) {
        return adminUsers_.get(index);
      }
      /**
       * <code>repeated string admin_users = 8;</code>
       * @param index The index of the value to return.
       * @return The bytes of the adminUsers at the given index.
       */
      public com.google.protobuf.ByteString
          getAdminUsersBytes(int index) {
        return adminUsers_.getByteString(index);
      }
      /**
       * <code>repeated string admin_users = 8;</code>
       * @param index The index to set the value at.
       * @param value The adminUsers to set.
       * @return This builder for chaining.
       */
      public Builder setAdminUsers(
          int index, java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureAdminUsersIsMutable();
        adminUsers_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string admin_users = 8;</code>
       * @param value The adminUsers to add.
       * @return This builder for chaining.
       */
      public Builder addAdminUsers(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureAdminUsersIsMutable();
        adminUsers_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string admin_users = 8;</code>
       * @param values The adminUsers to add.
       * @return This builder for chaining.
       */
      public Builder addAllAdminUsers(
          java.lang.Iterable<java.lang.String> values) {
        ensureAdminUsersIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, adminUsers_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string admin_users = 8;</code>
       * @return This builder for chaining.
       */
      public Builder clearAdminUsers() {
        adminUsers_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000080);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string admin_users = 8;</code>
       * @param value The bytes of the adminUsers to add.
       * @return This builder for chaining.
       */
      public Builder addAdminUsersBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        ensureAdminUsersIsMutable();
        adminUsers_.add(value);
        onChanged();
        return this;
      }

      private com.google.protobuf.LazyStringList adminGroups_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      private void ensureAdminGroupsIsMutable() {
        if (!((bitField0_ & 0x00000100) != 0)) {
          adminGroups_ = new com.google.protobuf.LazyStringArrayList(adminGroups_);
          bitField0_ |= 0x00000100;
         }
      }
      /**
       * <code>repeated string admin_groups = 9;</code>
       * @return A list containing the adminGroups.
       */
      public com.google.protobuf.ProtocolStringList
          getAdminGroupsList() {
        return adminGroups_.getUnmodifiableView();
      }
      /**
       * <code>repeated string admin_groups = 9;</code>
       * @return The count of adminGroups.
       */
      public int getAdminGroupsCount() {
        return adminGroups_.size();
      }
      /**
       * <code>repeated string admin_groups = 9;</code>
       * @param index The index of the element to return.
       * @return The adminGroups at the given index.
       */
      public java.lang.String getAdminGroups(int index) {
        return adminGroups_.get(index);
      }
      /**
       * <code>repeated string admin_groups = 9;</code>
       * @param index The index of the value to return.
       * @return The bytes of the adminGroups at the given index.
       */
      public com.google.protobuf.ByteString
          getAdminGroupsBytes(int index) {
        return adminGroups_.getByteString(index);
      }
      /**
       * <code>repeated string admin_groups = 9;</code>
       * @param index The index to set the value at.
       * @param value The adminGroups to set.
       * @return This builder for chaining.
       */
      public Builder setAdminGroups(
          int index, java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureAdminGroupsIsMutable();
        adminGroups_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string admin_groups = 9;</code>
       * @param value The adminGroups to add.
       * @return This builder for chaining.
       */
      public Builder addAdminGroups(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureAdminGroupsIsMutable();
        adminGroups_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string admin_groups = 9;</code>
       * @param values The adminGroups to add.
       * @return This builder for chaining.
       */
      public Builder addAllAdminGroups(
          java.lang.Iterable<java.lang.String> values) {
        ensureAdminGroupsIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, adminGroups_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string admin_groups = 9;</code>
       * @return This builder for chaining.
       */
      public Builder clearAdminGroups() {
        adminGroups_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000100);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string admin_groups = 9;</code>
       * @param value The bytes of the adminGroups to add.
       * @return This builder for chaining.
       */
      public Builder addAdminGroupsBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        ensureAdminGroupsIsMutable();
        adminGroups_.add(value);
        onChanged();
        return this;
      }

      private java.lang.Object taskReferenceName_ = "";
      /**
       * <code>string task_reference_name = 10;</code>
       * @return Whether the taskReferenceName field is set.
       */
      public boolean hasTaskReferenceName() {
        return ((bitField0_ & 0x00000200) != 0);
      }
      /**
       * <code>string task_reference_name = 10;</code>
       * @return The taskReferenceName.
       */
      public java.lang.String getTaskReferenceName() {
        java.lang.Object ref = taskReferenceName_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          taskReferenceName_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string task_reference_name = 10;</code>
       * @return The bytes for taskReferenceName.
       */
      public com.google.protobuf.ByteString
          getTaskReferenceNameBytes() {
        java.lang.Object ref = taskReferenceName_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          taskReferenceName_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string task_reference_name = 10;</code>
       * @param value The taskReferenceName to set.
       * @return This builder for chaining.
       */
      public Builder setTaskReferenceName(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000200;
        taskReferenceName_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string task_reference_name = 10;</code>
       * @return This builder for chaining.
       */
      public Builder clearTaskReferenceName() {
        bitField0_ = (bitField0_ & ~0x00000200);
        taskReferenceName_ = getDefaultInstance().getTaskReferenceName();
        onChanged();
        return this;
      }
      /**
       * <code>string task_reference_name = 10;</code>
       * @param value The bytes for taskReferenceName to set.
       * @return This builder for chaining.
       */
      public Builder setTaskReferenceNameBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000200;
        taskReferenceName_ = value;
        onChanged();
        return this;
      }

      private java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment> comments_ =
        java.util.Collections.emptyList();
      private void ensureCommentsIsMutable() {
        if (!((bitField0_ & 0x00000400) != 0)) {
          comments_ = new java.util.ArrayList<org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment>(comments_);
          bitField0_ |= 0x00000400;
         }
      }

      private com.google.protobuf.RepeatedFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment.Builder, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.CommentOrBuilder> commentsBuilder_;

      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
       */
      public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment> getCommentsList() {
        if (commentsBuilder_ == null) {
          return java.util.Collections.unmodifiableList(comments_);
        } else {
          return commentsBuilder_.getMessageList();
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
       */
      public int getCommentsCount() {
        if (commentsBuilder_ == null) {
          return comments_.size();
        } else {
          return commentsBuilder_.getCount();
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment getComments(int index) {
        if (commentsBuilder_ == null) {
          return comments_.get(index);
        } else {
          return commentsBuilder_.getMessage(index);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
       */
      public Builder setComments(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment value) {
        if (commentsBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureCommentsIsMutable();
          comments_.set(index, value);
          onChanged();
        } else {
          commentsBuilder_.setMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
       */
      public Builder setComments(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment.Builder builderForValue) {
        if (commentsBuilder_ == null) {
          ensureCommentsIsMutable();
          comments_.set(index, builderForValue.build());
          onChanged();
        } else {
          commentsBuilder_.setMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
       */
      public Builder addComments(org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment value) {
        if (commentsBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureCommentsIsMutable();
          comments_.add(value);
          onChanged();
        } else {
          commentsBuilder_.addMessage(value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
       */
      public Builder addComments(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment value) {
        if (commentsBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureCommentsIsMutable();
          comments_.add(index, value);
          onChanged();
        } else {
          commentsBuilder_.addMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
       */
      public Builder addComments(
          org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment.Builder builderForValue) {
        if (commentsBuilder_ == null) {
          ensureCommentsIsMutable();
          comments_.add(builderForValue.build());
          onChanged();
        } else {
          commentsBuilder_.addMessage(builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
       */
      public Builder addComments(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment.Builder builderForValue) {
        if (commentsBuilder_ == null) {
          ensureCommentsIsMutable();
          comments_.add(index, builderForValue.build());
          onChanged();
        } else {
          commentsBuilder_.addMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
       */
      public Builder addAllComments(
          java.lang.Iterable<? extends org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment> values) {
        if (commentsBuilder_ == null) {
          ensureCommentsIsMutable();
          com.google.protobuf.AbstractMessageLite.Builder.addAll(
              values, comments_);
          onChanged();
        } else {
          commentsBuilder_.addAllMessages(values);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
       */
      public Builder clearComments() {
        if (commentsBuilder_ == null) {
          comments_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000400);
          onChanged();
        } else {
          commentsBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
       */
      public Builder removeComments(int index) {
        if (commentsBuilder_ == null) {
          ensureCommentsIsMutable();
          comments_.remove(index);
          onChanged();
        } else {
          commentsBuilder_.remove(index);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment.Builder getCommentsBuilder(
          int index) {
        return getCommentsFieldBuilder().getBuilder(index);
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.CommentOrBuilder getCommentsOrBuilder(
          int index) {
        if (commentsBuilder_ == null) {
          return comments_.get(index);  } else {
          return commentsBuilder_.getMessageOrBuilder(index);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
       */
      public java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.CommentOrBuilder> 
           getCommentsOrBuilderList() {
        if (commentsBuilder_ != null) {
          return commentsBuilder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(comments_);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment.Builder addCommentsBuilder() {
        return getCommentsFieldBuilder().addBuilder(
            org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment.getDefaultInstance());
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment.Builder addCommentsBuilder(
          int index) {
        return getCommentsFieldBuilder().addBuilder(
            index, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment.getDefaultInstance());
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Comment comments = 11;</code>
       */
      public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment.Builder> 
           getCommentsBuilderList() {
        return getCommentsFieldBuilder().getBuilderList();
      }
      private com.google.protobuf.RepeatedFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment.Builder, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.CommentOrBuilder> 
          getCommentsFieldBuilder() {
        if (commentsBuilder_ == null) {
          commentsBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
              org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment.Builder, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.CommentOrBuilder>(
                  comments_,
                  ((bitField0_ & 0x00000400) != 0),
                  getParentForChildren(),
                  isClean());
          comments_ = null;
        }
        return commentsBuilder_;
      }

      private java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment> attachments_ =
        java.util.Collections.emptyList();
      private void ensureAttachmentsIsMutable() {
        if (!((bitField0_ & 0x00000800) != 0)) {
          attachments_ = new java.util.ArrayList<org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment>(attachments_);
          bitField0_ |= 0x00000800;
         }
      }

      private com.google.protobuf.RepeatedFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment.Builder, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.AttachmentOrBuilder> attachmentsBuilder_;

      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
       */
      public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment> getAttachmentsList() {
        if (attachmentsBuilder_ == null) {
          return java.util.Collections.unmodifiableList(attachments_);
        } else {
          return attachmentsBuilder_.getMessageList();
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
       */
      public int getAttachmentsCount() {
        if (attachmentsBuilder_ == null) {
          return attachments_.size();
        } else {
          return attachmentsBuilder_.getCount();
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment getAttachments(int index) {
        if (attachmentsBuilder_ == null) {
          return attachments_.get(index);
        } else {
          return attachmentsBuilder_.getMessage(index);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
       */
      public Builder setAttachments(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment value) {
        if (attachmentsBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureAttachmentsIsMutable();
          attachments_.set(index, value);
          onChanged();
        } else {
          attachmentsBuilder_.setMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
       */
      public Builder setAttachments(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment.Builder builderForValue) {
        if (attachmentsBuilder_ == null) {
          ensureAttachmentsIsMutable();
          attachments_.set(index, builderForValue.build());
          onChanged();
        } else {
          attachmentsBuilder_.setMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
       */
      public Builder addAttachments(org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment value) {
        if (attachmentsBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureAttachmentsIsMutable();
          attachments_.add(value);
          onChanged();
        } else {
          attachmentsBuilder_.addMessage(value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
       */
      public Builder addAttachments(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment value) {
        if (attachmentsBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          ensureAttachmentsIsMutable();
          attachments_.add(index, value);
          onChanged();
        } else {
          attachmentsBuilder_.addMessage(index, value);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
       */
      public Builder addAttachments(
          org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment.Builder builderForValue) {
        if (attachmentsBuilder_ == null) {
          ensureAttachmentsIsMutable();
          attachments_.add(builderForValue.build());
          onChanged();
        } else {
          attachmentsBuilder_.addMessage(builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
       */
      public Builder addAttachments(
          int index, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment.Builder builderForValue) {
        if (attachmentsBuilder_ == null) {
          ensureAttachmentsIsMutable();
          attachments_.add(index, builderForValue.build());
          onChanged();
        } else {
          attachmentsBuilder_.addMessage(index, builderForValue.build());
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
       */
      public Builder addAllAttachments(
          java.lang.Iterable<? extends org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment> values) {
        if (attachmentsBuilder_ == null) {
          ensureAttachmentsIsMutable();
          com.google.protobuf.AbstractMessageLite.Builder.addAll(
              values, attachments_);
          onChanged();
        } else {
          attachmentsBuilder_.addAllMessages(values);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
       */
      public Builder clearAttachments() {
        if (attachmentsBuilder_ == null) {
          attachments_ = java.util.Collections.emptyList();
          bitField0_ = (bitField0_ & ~0x00000800);
          onChanged();
        } else {
          attachmentsBuilder_.clear();
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
       */
      public Builder removeAttachments(int index) {
        if (attachmentsBuilder_ == null) {
          ensureAttachmentsIsMutable();
          attachments_.remove(index);
          onChanged();
        } else {
          attachmentsBuilder_.remove(index);
        }
        return this;
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment.Builder getAttachmentsBuilder(
          int index) {
        return getAttachmentsFieldBuilder().getBuilder(index);
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.AttachmentOrBuilder getAttachmentsOrBuilder(
          int index) {
        if (attachmentsBuilder_ == null) {
          return attachments_.get(index);  } else {
          return attachmentsBuilder_.getMessageOrBuilder(index);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
       */
      public java.util.List<? extends org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.AttachmentOrBuilder> 
           getAttachmentsOrBuilderList() {
        if (attachmentsBuilder_ != null) {
          return attachmentsBuilder_.getMessageOrBuilderList();
        } else {
          return java.util.Collections.unmodifiableList(attachments_);
        }
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment.Builder addAttachmentsBuilder() {
        return getAttachmentsFieldBuilder().addBuilder(
            org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment.getDefaultInstance());
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
       */
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment.Builder addAttachmentsBuilder(
          int index) {
        return getAttachmentsFieldBuilder().addBuilder(
            index, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment.getDefaultInstance());
      }
      /**
       * <code>repeated .org.kie.kogito.serialization.process.protobuf.Attachment attachments = 12;</code>
       */
      public java.util.List<org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment.Builder> 
           getAttachmentsBuilderList() {
        return getAttachmentsFieldBuilder().getBuilderList();
      }
      private com.google.protobuf.RepeatedFieldBuilderV3<
          org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment.Builder, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.AttachmentOrBuilder> 
          getAttachmentsFieldBuilder() {
        if (attachmentsBuilder_ == null) {
          attachmentsBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
              org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment.Builder, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.AttachmentOrBuilder>(
                  attachments_,
                  ((bitField0_ & 0x00000800) != 0),
                  getParentForChildren(),
                  isClean());
          attachments_ = null;
        }
        return attachmentsBuilder_;
      }

      private com.google.protobuf.MapField<
          java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> startDeadlines_;
      private com.google.protobuf.MapField<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline>
      internalGetStartDeadlines() {
        if (startDeadlines_ == null) {
          return com.google.protobuf.MapField.emptyMapField(
              StartDeadlinesDefaultEntryHolder.defaultEntry);
        }
        return startDeadlines_;
      }
      private com.google.protobuf.MapField<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline>
      internalGetMutableStartDeadlines() {
        onChanged();;
        if (startDeadlines_ == null) {
          startDeadlines_ = com.google.protobuf.MapField.newMapField(
              StartDeadlinesDefaultEntryHolder.defaultEntry);
        }
        if (!startDeadlines_.isMutable()) {
          startDeadlines_ = startDeadlines_.copy();
        }
        return startDeadlines_;
      }

      public int getStartDeadlinesCount() {
        return internalGetStartDeadlines().getMap().size();
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; start_deadlines = 13;</code>
       */

      @java.lang.Override
      public boolean containsStartDeadlines(
          java.lang.String key) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        return internalGetStartDeadlines().getMap().containsKey(key);
      }
      /**
       * Use {@link #getStartDeadlinesMap()} instead.
       */
      @java.lang.Override
      @java.lang.Deprecated
      public java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> getStartDeadlines() {
        return getStartDeadlinesMap();
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; start_deadlines = 13;</code>
       */
      @java.lang.Override

      public java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> getStartDeadlinesMap() {
        return internalGetStartDeadlines().getMap();
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; start_deadlines = 13;</code>
       */
      @java.lang.Override

      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline getStartDeadlinesOrDefault(
          java.lang.String key,
          org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline defaultValue) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> map =
            internalGetStartDeadlines().getMap();
        return map.containsKey(key) ? map.get(key) : defaultValue;
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; start_deadlines = 13;</code>
       */
      @java.lang.Override

      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline getStartDeadlinesOrThrow(
          java.lang.String key) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> map =
            internalGetStartDeadlines().getMap();
        if (!map.containsKey(key)) {
          throw new java.lang.IllegalArgumentException();
        }
        return map.get(key);
      }

      public Builder clearStartDeadlines() {
        internalGetMutableStartDeadlines().getMutableMap()
            .clear();
        return this;
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; start_deadlines = 13;</code>
       */

      public Builder removeStartDeadlines(
          java.lang.String key) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        internalGetMutableStartDeadlines().getMutableMap()
            .remove(key);
        return this;
      }
      /**
       * Use alternate mutation accessors instead.
       */
      @java.lang.Deprecated
      public java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline>
      getMutableStartDeadlines() {
        return internalGetMutableStartDeadlines().getMutableMap();
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; start_deadlines = 13;</code>
       */
      public Builder putStartDeadlines(
          java.lang.String key,
          org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline value) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        if (value == null) { throw new java.lang.NullPointerException(); }
        internalGetMutableStartDeadlines().getMutableMap()
            .put(key, value);
        return this;
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; start_deadlines = 13;</code>
       */

      public Builder putAllStartDeadlines(
          java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> values) {
        internalGetMutableStartDeadlines().getMutableMap()
            .putAll(values);
        return this;
      }

      private com.google.protobuf.MapField<
          java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> completedDeadlines_;
      private com.google.protobuf.MapField<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline>
      internalGetCompletedDeadlines() {
        if (completedDeadlines_ == null) {
          return com.google.protobuf.MapField.emptyMapField(
              CompletedDeadlinesDefaultEntryHolder.defaultEntry);
        }
        return completedDeadlines_;
      }
      private com.google.protobuf.MapField<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline>
      internalGetMutableCompletedDeadlines() {
        onChanged();;
        if (completedDeadlines_ == null) {
          completedDeadlines_ = com.google.protobuf.MapField.newMapField(
              CompletedDeadlinesDefaultEntryHolder.defaultEntry);
        }
        if (!completedDeadlines_.isMutable()) {
          completedDeadlines_ = completedDeadlines_.copy();
        }
        return completedDeadlines_;
      }

      public int getCompletedDeadlinesCount() {
        return internalGetCompletedDeadlines().getMap().size();
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; completed_deadlines = 14;</code>
       */

      @java.lang.Override
      public boolean containsCompletedDeadlines(
          java.lang.String key) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        return internalGetCompletedDeadlines().getMap().containsKey(key);
      }
      /**
       * Use {@link #getCompletedDeadlinesMap()} instead.
       */
      @java.lang.Override
      @java.lang.Deprecated
      public java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> getCompletedDeadlines() {
        return getCompletedDeadlinesMap();
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; completed_deadlines = 14;</code>
       */
      @java.lang.Override

      public java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> getCompletedDeadlinesMap() {
        return internalGetCompletedDeadlines().getMap();
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; completed_deadlines = 14;</code>
       */
      @java.lang.Override

      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline getCompletedDeadlinesOrDefault(
          java.lang.String key,
          org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline defaultValue) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> map =
            internalGetCompletedDeadlines().getMap();
        return map.containsKey(key) ? map.get(key) : defaultValue;
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; completed_deadlines = 14;</code>
       */
      @java.lang.Override

      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline getCompletedDeadlinesOrThrow(
          java.lang.String key) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> map =
            internalGetCompletedDeadlines().getMap();
        if (!map.containsKey(key)) {
          throw new java.lang.IllegalArgumentException();
        }
        return map.get(key);
      }

      public Builder clearCompletedDeadlines() {
        internalGetMutableCompletedDeadlines().getMutableMap()
            .clear();
        return this;
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; completed_deadlines = 14;</code>
       */

      public Builder removeCompletedDeadlines(
          java.lang.String key) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        internalGetMutableCompletedDeadlines().getMutableMap()
            .remove(key);
        return this;
      }
      /**
       * Use alternate mutation accessors instead.
       */
      @java.lang.Deprecated
      public java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline>
      getMutableCompletedDeadlines() {
        return internalGetMutableCompletedDeadlines().getMutableMap();
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; completed_deadlines = 14;</code>
       */
      public Builder putCompletedDeadlines(
          java.lang.String key,
          org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline value) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        if (value == null) { throw new java.lang.NullPointerException(); }
        internalGetMutableCompletedDeadlines().getMutableMap()
            .put(key, value);
        return this;
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Deadline&gt; completed_deadlines = 14;</code>
       */

      public Builder putAllCompletedDeadlines(
          java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline> values) {
        internalGetMutableCompletedDeadlines().getMutableMap()
            .putAll(values);
        return this;
      }

      private com.google.protobuf.MapField<
          java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> startReassigments_;
      private com.google.protobuf.MapField<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment>
      internalGetStartReassigments() {
        if (startReassigments_ == null) {
          return com.google.protobuf.MapField.emptyMapField(
              StartReassigmentsDefaultEntryHolder.defaultEntry);
        }
        return startReassigments_;
      }
      private com.google.protobuf.MapField<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment>
      internalGetMutableStartReassigments() {
        onChanged();;
        if (startReassigments_ == null) {
          startReassigments_ = com.google.protobuf.MapField.newMapField(
              StartReassigmentsDefaultEntryHolder.defaultEntry);
        }
        if (!startReassigments_.isMutable()) {
          startReassigments_ = startReassigments_.copy();
        }
        return startReassigments_;
      }

      public int getStartReassigmentsCount() {
        return internalGetStartReassigments().getMap().size();
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; start_reassigments = 15;</code>
       */

      @java.lang.Override
      public boolean containsStartReassigments(
          java.lang.String key) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        return internalGetStartReassigments().getMap().containsKey(key);
      }
      /**
       * Use {@link #getStartReassigmentsMap()} instead.
       */
      @java.lang.Override
      @java.lang.Deprecated
      public java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> getStartReassigments() {
        return getStartReassigmentsMap();
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; start_reassigments = 15;</code>
       */
      @java.lang.Override

      public java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> getStartReassigmentsMap() {
        return internalGetStartReassigments().getMap();
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; start_reassigments = 15;</code>
       */
      @java.lang.Override

      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment getStartReassigmentsOrDefault(
          java.lang.String key,
          org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment defaultValue) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> map =
            internalGetStartReassigments().getMap();
        return map.containsKey(key) ? map.get(key) : defaultValue;
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; start_reassigments = 15;</code>
       */
      @java.lang.Override

      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment getStartReassigmentsOrThrow(
          java.lang.String key) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> map =
            internalGetStartReassigments().getMap();
        if (!map.containsKey(key)) {
          throw new java.lang.IllegalArgumentException();
        }
        return map.get(key);
      }

      public Builder clearStartReassigments() {
        internalGetMutableStartReassigments().getMutableMap()
            .clear();
        return this;
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; start_reassigments = 15;</code>
       */

      public Builder removeStartReassigments(
          java.lang.String key) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        internalGetMutableStartReassigments().getMutableMap()
            .remove(key);
        return this;
      }
      /**
       * Use alternate mutation accessors instead.
       */
      @java.lang.Deprecated
      public java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment>
      getMutableStartReassigments() {
        return internalGetMutableStartReassigments().getMutableMap();
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; start_reassigments = 15;</code>
       */
      public Builder putStartReassigments(
          java.lang.String key,
          org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment value) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        if (value == null) { throw new java.lang.NullPointerException(); }
        internalGetMutableStartReassigments().getMutableMap()
            .put(key, value);
        return this;
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; start_reassigments = 15;</code>
       */

      public Builder putAllStartReassigments(
          java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> values) {
        internalGetMutableStartReassigments().getMutableMap()
            .putAll(values);
        return this;
      }

      private com.google.protobuf.MapField<
          java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> completedReassigments_;
      private com.google.protobuf.MapField<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment>
      internalGetCompletedReassigments() {
        if (completedReassigments_ == null) {
          return com.google.protobuf.MapField.emptyMapField(
              CompletedReassigmentsDefaultEntryHolder.defaultEntry);
        }
        return completedReassigments_;
      }
      private com.google.protobuf.MapField<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment>
      internalGetMutableCompletedReassigments() {
        onChanged();;
        if (completedReassigments_ == null) {
          completedReassigments_ = com.google.protobuf.MapField.newMapField(
              CompletedReassigmentsDefaultEntryHolder.defaultEntry);
        }
        if (!completedReassigments_.isMutable()) {
          completedReassigments_ = completedReassigments_.copy();
        }
        return completedReassigments_;
      }

      public int getCompletedReassigmentsCount() {
        return internalGetCompletedReassigments().getMap().size();
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; completed_reassigments = 16;</code>
       */

      @java.lang.Override
      public boolean containsCompletedReassigments(
          java.lang.String key) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        return internalGetCompletedReassigments().getMap().containsKey(key);
      }
      /**
       * Use {@link #getCompletedReassigmentsMap()} instead.
       */
      @java.lang.Override
      @java.lang.Deprecated
      public java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> getCompletedReassigments() {
        return getCompletedReassigmentsMap();
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; completed_reassigments = 16;</code>
       */
      @java.lang.Override

      public java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> getCompletedReassigmentsMap() {
        return internalGetCompletedReassigments().getMap();
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; completed_reassigments = 16;</code>
       */
      @java.lang.Override

      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment getCompletedReassigmentsOrDefault(
          java.lang.String key,
          org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment defaultValue) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> map =
            internalGetCompletedReassigments().getMap();
        return map.containsKey(key) ? map.get(key) : defaultValue;
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; completed_reassigments = 16;</code>
       */
      @java.lang.Override

      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment getCompletedReassigmentsOrThrow(
          java.lang.String key) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> map =
            internalGetCompletedReassigments().getMap();
        if (!map.containsKey(key)) {
          throw new java.lang.IllegalArgumentException();
        }
        return map.get(key);
      }

      public Builder clearCompletedReassigments() {
        internalGetMutableCompletedReassigments().getMutableMap()
            .clear();
        return this;
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; completed_reassigments = 16;</code>
       */

      public Builder removeCompletedReassigments(
          java.lang.String key) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        internalGetMutableCompletedReassigments().getMutableMap()
            .remove(key);
        return this;
      }
      /**
       * Use alternate mutation accessors instead.
       */
      @java.lang.Deprecated
      public java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment>
      getMutableCompletedReassigments() {
        return internalGetMutableCompletedReassigments().getMutableMap();
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; completed_reassigments = 16;</code>
       */
      public Builder putCompletedReassigments(
          java.lang.String key,
          org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment value) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        if (value == null) { throw new java.lang.NullPointerException(); }
        internalGetMutableCompletedReassigments().getMutableMap()
            .put(key, value);
        return this;
      }
      /**
       * <code>map&lt;string, .org.kie.kogito.serialization.process.protobuf.Reassignment&gt; completed_reassigments = 16;</code>
       */

      public Builder putAllCompletedReassigments(
          java.util.Map<java.lang.String, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment> values) {
        internalGetMutableCompletedReassigments().getMutableMap()
            .putAll(values);
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


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.HumanTaskWorkItemData)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.HumanTaskWorkItemData)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<HumanTaskWorkItemData>
        PARSER = new com.google.protobuf.AbstractParser<HumanTaskWorkItemData>() {
      @java.lang.Override
      public HumanTaskWorkItemData parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new HumanTaskWorkItemData(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<HumanTaskWorkItemData> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<HumanTaskWorkItemData> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.HumanTaskWorkItemData getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface CommentOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.Comment)
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
     * <code>string content = 2;</code>
     * @return Whether the content field is set.
     */
    boolean hasContent();
    /**
     * <code>string content = 2;</code>
     * @return The content.
     */
    java.lang.String getContent();
    /**
     * <code>string content = 2;</code>
     * @return The bytes for content.
     */
    com.google.protobuf.ByteString
        getContentBytes();

    /**
     * <code>int64 updatedAt = 3;</code>
     * @return Whether the updatedAt field is set.
     */
    boolean hasUpdatedAt();
    /**
     * <code>int64 updatedAt = 3;</code>
     * @return The updatedAt.
     */
    long getUpdatedAt();

    /**
     * <code>string updatedBy = 4;</code>
     * @return Whether the updatedBy field is set.
     */
    boolean hasUpdatedBy();
    /**
     * <code>string updatedBy = 4;</code>
     * @return The updatedBy.
     */
    java.lang.String getUpdatedBy();
    /**
     * <code>string updatedBy = 4;</code>
     * @return The bytes for updatedBy.
     */
    com.google.protobuf.ByteString
        getUpdatedByBytes();
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.Comment}
   */
  public static final class Comment extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.Comment)
      CommentOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use Comment.newBuilder() to construct.
    private Comment(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Comment() {
      id_ = "";
      content_ = "";
      updatedBy_ = "";
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new Comment();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Comment(
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
            case 18: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000001;
              content_ = s;
              break;
            }
            case 24: {
              bitField0_ |= 0x00000002;
              updatedAt_ = input.readInt64();
              break;
            }
            case 34: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000004;
              updatedBy_ = s;
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
      return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Comment_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Comment_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment.class, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment.Builder.class);
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

    public static final int CONTENT_FIELD_NUMBER = 2;
    private volatile java.lang.Object content_;
    /**
     * <code>string content = 2;</code>
     * @return Whether the content field is set.
     */
    @java.lang.Override
    public boolean hasContent() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>string content = 2;</code>
     * @return The content.
     */
    @java.lang.Override
    public java.lang.String getContent() {
      java.lang.Object ref = content_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        content_ = s;
        return s;
      }
    }
    /**
     * <code>string content = 2;</code>
     * @return The bytes for content.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getContentBytes() {
      java.lang.Object ref = content_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        content_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int UPDATEDAT_FIELD_NUMBER = 3;
    private long updatedAt_;
    /**
     * <code>int64 updatedAt = 3;</code>
     * @return Whether the updatedAt field is set.
     */
    @java.lang.Override
    public boolean hasUpdatedAt() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>int64 updatedAt = 3;</code>
     * @return The updatedAt.
     */
    @java.lang.Override
    public long getUpdatedAt() {
      return updatedAt_;
    }

    public static final int UPDATEDBY_FIELD_NUMBER = 4;
    private volatile java.lang.Object updatedBy_;
    /**
     * <code>string updatedBy = 4;</code>
     * @return Whether the updatedBy field is set.
     */
    @java.lang.Override
    public boolean hasUpdatedBy() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     * <code>string updatedBy = 4;</code>
     * @return The updatedBy.
     */
    @java.lang.Override
    public java.lang.String getUpdatedBy() {
      java.lang.Object ref = updatedBy_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        updatedBy_ = s;
        return s;
      }
    }
    /**
     * <code>string updatedBy = 4;</code>
     * @return The bytes for updatedBy.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getUpdatedByBytes() {
      java.lang.Object ref = updatedBy_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        updatedBy_ = b;
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
      if (!getIdBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, id_);
      }
      if (((bitField0_ & 0x00000001) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, content_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        output.writeInt64(3, updatedAt_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 4, updatedBy_);
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
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, content_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(3, updatedAt_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(4, updatedBy_);
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
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment other = (org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment) obj;

      if (!getId()
          .equals(other.getId())) return false;
      if (hasContent() != other.hasContent()) return false;
      if (hasContent()) {
        if (!getContent()
            .equals(other.getContent())) return false;
      }
      if (hasUpdatedAt() != other.hasUpdatedAt()) return false;
      if (hasUpdatedAt()) {
        if (getUpdatedAt()
            != other.getUpdatedAt()) return false;
      }
      if (hasUpdatedBy() != other.hasUpdatedBy()) return false;
      if (hasUpdatedBy()) {
        if (!getUpdatedBy()
            .equals(other.getUpdatedBy())) return false;
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
      if (hasContent()) {
        hash = (37 * hash) + CONTENT_FIELD_NUMBER;
        hash = (53 * hash) + getContent().hashCode();
      }
      if (hasUpdatedAt()) {
        hash = (37 * hash) + UPDATEDAT_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
            getUpdatedAt());
      }
      if (hasUpdatedBy()) {
        hash = (37 * hash) + UPDATEDBY_FIELD_NUMBER;
        hash = (53 * hash) + getUpdatedBy().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment parseFrom(
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
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment prototype) {
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
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.Comment}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.Comment)
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.CommentOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Comment_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Comment_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment.class, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment.newBuilder()
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

        content_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        updatedAt_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000002);
        updatedBy_ = "";
        bitField0_ = (bitField0_ & ~0x00000004);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Comment_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment build() {
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment result = new org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        result.id_ = id_;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          to_bitField0_ |= 0x00000001;
        }
        result.content_ = content_;
        if (((from_bitField0_ & 0x00000002) != 0)) {
          result.updatedAt_ = updatedAt_;
          to_bitField0_ |= 0x00000002;
        }
        if (((from_bitField0_ & 0x00000004) != 0)) {
          to_bitField0_ |= 0x00000004;
        }
        result.updatedBy_ = updatedBy_;
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
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment.getDefaultInstance()) return this;
        if (!other.getId().isEmpty()) {
          id_ = other.id_;
          onChanged();
        }
        if (other.hasContent()) {
          bitField0_ |= 0x00000001;
          content_ = other.content_;
          onChanged();
        }
        if (other.hasUpdatedAt()) {
          setUpdatedAt(other.getUpdatedAt());
        }
        if (other.hasUpdatedBy()) {
          bitField0_ |= 0x00000004;
          updatedBy_ = other.updatedBy_;
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
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment) e.getUnfinishedMessage();
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

      private java.lang.Object content_ = "";
      /**
       * <code>string content = 2;</code>
       * @return Whether the content field is set.
       */
      public boolean hasContent() {
        return ((bitField0_ & 0x00000001) != 0);
      }
      /**
       * <code>string content = 2;</code>
       * @return The content.
       */
      public java.lang.String getContent() {
        java.lang.Object ref = content_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          content_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string content = 2;</code>
       * @return The bytes for content.
       */
      public com.google.protobuf.ByteString
          getContentBytes() {
        java.lang.Object ref = content_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          content_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string content = 2;</code>
       * @param value The content to set.
       * @return This builder for chaining.
       */
      public Builder setContent(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        content_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string content = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearContent() {
        bitField0_ = (bitField0_ & ~0x00000001);
        content_ = getDefaultInstance().getContent();
        onChanged();
        return this;
      }
      /**
       * <code>string content = 2;</code>
       * @param value The bytes for content to set.
       * @return This builder for chaining.
       */
      public Builder setContentBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000001;
        content_ = value;
        onChanged();
        return this;
      }

      private long updatedAt_ ;
      /**
       * <code>int64 updatedAt = 3;</code>
       * @return Whether the updatedAt field is set.
       */
      @java.lang.Override
      public boolean hasUpdatedAt() {
        return ((bitField0_ & 0x00000002) != 0);
      }
      /**
       * <code>int64 updatedAt = 3;</code>
       * @return The updatedAt.
       */
      @java.lang.Override
      public long getUpdatedAt() {
        return updatedAt_;
      }
      /**
       * <code>int64 updatedAt = 3;</code>
       * @param value The updatedAt to set.
       * @return This builder for chaining.
       */
      public Builder setUpdatedAt(long value) {
        bitField0_ |= 0x00000002;
        updatedAt_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int64 updatedAt = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearUpdatedAt() {
        bitField0_ = (bitField0_ & ~0x00000002);
        updatedAt_ = 0L;
        onChanged();
        return this;
      }

      private java.lang.Object updatedBy_ = "";
      /**
       * <code>string updatedBy = 4;</code>
       * @return Whether the updatedBy field is set.
       */
      public boolean hasUpdatedBy() {
        return ((bitField0_ & 0x00000004) != 0);
      }
      /**
       * <code>string updatedBy = 4;</code>
       * @return The updatedBy.
       */
      public java.lang.String getUpdatedBy() {
        java.lang.Object ref = updatedBy_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          updatedBy_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string updatedBy = 4;</code>
       * @return The bytes for updatedBy.
       */
      public com.google.protobuf.ByteString
          getUpdatedByBytes() {
        java.lang.Object ref = updatedBy_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          updatedBy_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string updatedBy = 4;</code>
       * @param value The updatedBy to set.
       * @return This builder for chaining.
       */
      public Builder setUpdatedBy(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        updatedBy_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string updatedBy = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearUpdatedBy() {
        bitField0_ = (bitField0_ & ~0x00000004);
        updatedBy_ = getDefaultInstance().getUpdatedBy();
        onChanged();
        return this;
      }
      /**
       * <code>string updatedBy = 4;</code>
       * @param value The bytes for updatedBy to set.
       * @return This builder for chaining.
       */
      public Builder setUpdatedByBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000004;
        updatedBy_ = value;
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


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.Comment)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.Comment)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Comment>
        PARSER = new com.google.protobuf.AbstractParser<Comment>() {
      @java.lang.Override
      public Comment parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new Comment(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Comment> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Comment> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Comment getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface AttachmentOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.Attachment)
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
     * <code>string content = 2;</code>
     * @return Whether the content field is set.
     */
    boolean hasContent();
    /**
     * <code>string content = 2;</code>
     * @return The content.
     */
    java.lang.String getContent();
    /**
     * <code>string content = 2;</code>
     * @return The bytes for content.
     */
    com.google.protobuf.ByteString
        getContentBytes();

    /**
     * <code>int64 updatedAt = 3;</code>
     * @return Whether the updatedAt field is set.
     */
    boolean hasUpdatedAt();
    /**
     * <code>int64 updatedAt = 3;</code>
     * @return The updatedAt.
     */
    long getUpdatedAt();

    /**
     * <code>string updatedBy = 4;</code>
     * @return Whether the updatedBy field is set.
     */
    boolean hasUpdatedBy();
    /**
     * <code>string updatedBy = 4;</code>
     * @return The updatedBy.
     */
    java.lang.String getUpdatedBy();
    /**
     * <code>string updatedBy = 4;</code>
     * @return The bytes for updatedBy.
     */
    com.google.protobuf.ByteString
        getUpdatedByBytes();

    /**
     * <code>string name = 5;</code>
     * @return Whether the name field is set.
     */
    boolean hasName();
    /**
     * <code>string name = 5;</code>
     * @return The name.
     */
    java.lang.String getName();
    /**
     * <code>string name = 5;</code>
     * @return The bytes for name.
     */
    com.google.protobuf.ByteString
        getNameBytes();
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.Attachment}
   */
  public static final class Attachment extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.Attachment)
      AttachmentOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use Attachment.newBuilder() to construct.
    private Attachment(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Attachment() {
      id_ = "";
      content_ = "";
      updatedBy_ = "";
      name_ = "";
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new Attachment();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Attachment(
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
            case 18: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000001;
              content_ = s;
              break;
            }
            case 24: {
              bitField0_ |= 0x00000002;
              updatedAt_ = input.readInt64();
              break;
            }
            case 34: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000004;
              updatedBy_ = s;
              break;
            }
            case 42: {
              java.lang.String s = input.readStringRequireUtf8();
              bitField0_ |= 0x00000008;
              name_ = s;
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
      return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Attachment_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Attachment_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment.class, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment.Builder.class);
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

    public static final int CONTENT_FIELD_NUMBER = 2;
    private volatile java.lang.Object content_;
    /**
     * <code>string content = 2;</code>
     * @return Whether the content field is set.
     */
    @java.lang.Override
    public boolean hasContent() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <code>string content = 2;</code>
     * @return The content.
     */
    @java.lang.Override
    public java.lang.String getContent() {
      java.lang.Object ref = content_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        content_ = s;
        return s;
      }
    }
    /**
     * <code>string content = 2;</code>
     * @return The bytes for content.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getContentBytes() {
      java.lang.Object ref = content_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        content_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int UPDATEDAT_FIELD_NUMBER = 3;
    private long updatedAt_;
    /**
     * <code>int64 updatedAt = 3;</code>
     * @return Whether the updatedAt field is set.
     */
    @java.lang.Override
    public boolean hasUpdatedAt() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <code>int64 updatedAt = 3;</code>
     * @return The updatedAt.
     */
    @java.lang.Override
    public long getUpdatedAt() {
      return updatedAt_;
    }

    public static final int UPDATEDBY_FIELD_NUMBER = 4;
    private volatile java.lang.Object updatedBy_;
    /**
     * <code>string updatedBy = 4;</code>
     * @return Whether the updatedBy field is set.
     */
    @java.lang.Override
    public boolean hasUpdatedBy() {
      return ((bitField0_ & 0x00000004) != 0);
    }
    /**
     * <code>string updatedBy = 4;</code>
     * @return The updatedBy.
     */
    @java.lang.Override
    public java.lang.String getUpdatedBy() {
      java.lang.Object ref = updatedBy_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        updatedBy_ = s;
        return s;
      }
    }
    /**
     * <code>string updatedBy = 4;</code>
     * @return The bytes for updatedBy.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getUpdatedByBytes() {
      java.lang.Object ref = updatedBy_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        updatedBy_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int NAME_FIELD_NUMBER = 5;
    private volatile java.lang.Object name_;
    /**
     * <code>string name = 5;</code>
     * @return Whether the name field is set.
     */
    @java.lang.Override
    public boolean hasName() {
      return ((bitField0_ & 0x00000008) != 0);
    }
    /**
     * <code>string name = 5;</code>
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
     * <code>string name = 5;</code>
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
      if (((bitField0_ & 0x00000001) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, content_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        output.writeInt64(3, updatedAt_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 4, updatedBy_);
      }
      if (((bitField0_ & 0x00000008) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 5, name_);
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
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, content_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(3, updatedAt_);
      }
      if (((bitField0_ & 0x00000004) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(4, updatedBy_);
      }
      if (((bitField0_ & 0x00000008) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(5, name_);
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
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment other = (org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment) obj;

      if (!getId()
          .equals(other.getId())) return false;
      if (hasContent() != other.hasContent()) return false;
      if (hasContent()) {
        if (!getContent()
            .equals(other.getContent())) return false;
      }
      if (hasUpdatedAt() != other.hasUpdatedAt()) return false;
      if (hasUpdatedAt()) {
        if (getUpdatedAt()
            != other.getUpdatedAt()) return false;
      }
      if (hasUpdatedBy() != other.hasUpdatedBy()) return false;
      if (hasUpdatedBy()) {
        if (!getUpdatedBy()
            .equals(other.getUpdatedBy())) return false;
      }
      if (hasName() != other.hasName()) return false;
      if (hasName()) {
        if (!getName()
            .equals(other.getName())) return false;
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
      if (hasContent()) {
        hash = (37 * hash) + CONTENT_FIELD_NUMBER;
        hash = (53 * hash) + getContent().hashCode();
      }
      if (hasUpdatedAt()) {
        hash = (37 * hash) + UPDATEDAT_FIELD_NUMBER;
        hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
            getUpdatedAt());
      }
      if (hasUpdatedBy()) {
        hash = (37 * hash) + UPDATEDBY_FIELD_NUMBER;
        hash = (53 * hash) + getUpdatedBy().hashCode();
      }
      if (hasName()) {
        hash = (37 * hash) + NAME_FIELD_NUMBER;
        hash = (53 * hash) + getName().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment parseFrom(
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
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment prototype) {
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
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.Attachment}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.Attachment)
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.AttachmentOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Attachment_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Attachment_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment.class, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment.newBuilder()
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

        content_ = "";
        bitField0_ = (bitField0_ & ~0x00000001);
        updatedAt_ = 0L;
        bitField0_ = (bitField0_ & ~0x00000002);
        updatedBy_ = "";
        bitField0_ = (bitField0_ & ~0x00000004);
        name_ = "";
        bitField0_ = (bitField0_ & ~0x00000008);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Attachment_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment build() {
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment result = new org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment(this);
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        result.id_ = id_;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          to_bitField0_ |= 0x00000001;
        }
        result.content_ = content_;
        if (((from_bitField0_ & 0x00000002) != 0)) {
          result.updatedAt_ = updatedAt_;
          to_bitField0_ |= 0x00000002;
        }
        if (((from_bitField0_ & 0x00000004) != 0)) {
          to_bitField0_ |= 0x00000004;
        }
        result.updatedBy_ = updatedBy_;
        if (((from_bitField0_ & 0x00000008) != 0)) {
          to_bitField0_ |= 0x00000008;
        }
        result.name_ = name_;
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
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment.getDefaultInstance()) return this;
        if (!other.getId().isEmpty()) {
          id_ = other.id_;
          onChanged();
        }
        if (other.hasContent()) {
          bitField0_ |= 0x00000001;
          content_ = other.content_;
          onChanged();
        }
        if (other.hasUpdatedAt()) {
          setUpdatedAt(other.getUpdatedAt());
        }
        if (other.hasUpdatedBy()) {
          bitField0_ |= 0x00000004;
          updatedBy_ = other.updatedBy_;
          onChanged();
        }
        if (other.hasName()) {
          bitField0_ |= 0x00000008;
          name_ = other.name_;
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
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment) e.getUnfinishedMessage();
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

      private java.lang.Object content_ = "";
      /**
       * <code>string content = 2;</code>
       * @return Whether the content field is set.
       */
      public boolean hasContent() {
        return ((bitField0_ & 0x00000001) != 0);
      }
      /**
       * <code>string content = 2;</code>
       * @return The content.
       */
      public java.lang.String getContent() {
        java.lang.Object ref = content_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          content_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string content = 2;</code>
       * @return The bytes for content.
       */
      public com.google.protobuf.ByteString
          getContentBytes() {
        java.lang.Object ref = content_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          content_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string content = 2;</code>
       * @param value The content to set.
       * @return This builder for chaining.
       */
      public Builder setContent(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000001;
        content_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string content = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearContent() {
        bitField0_ = (bitField0_ & ~0x00000001);
        content_ = getDefaultInstance().getContent();
        onChanged();
        return this;
      }
      /**
       * <code>string content = 2;</code>
       * @param value The bytes for content to set.
       * @return This builder for chaining.
       */
      public Builder setContentBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000001;
        content_ = value;
        onChanged();
        return this;
      }

      private long updatedAt_ ;
      /**
       * <code>int64 updatedAt = 3;</code>
       * @return Whether the updatedAt field is set.
       */
      @java.lang.Override
      public boolean hasUpdatedAt() {
        return ((bitField0_ & 0x00000002) != 0);
      }
      /**
       * <code>int64 updatedAt = 3;</code>
       * @return The updatedAt.
       */
      @java.lang.Override
      public long getUpdatedAt() {
        return updatedAt_;
      }
      /**
       * <code>int64 updatedAt = 3;</code>
       * @param value The updatedAt to set.
       * @return This builder for chaining.
       */
      public Builder setUpdatedAt(long value) {
        bitField0_ |= 0x00000002;
        updatedAt_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>int64 updatedAt = 3;</code>
       * @return This builder for chaining.
       */
      public Builder clearUpdatedAt() {
        bitField0_ = (bitField0_ & ~0x00000002);
        updatedAt_ = 0L;
        onChanged();
        return this;
      }

      private java.lang.Object updatedBy_ = "";
      /**
       * <code>string updatedBy = 4;</code>
       * @return Whether the updatedBy field is set.
       */
      public boolean hasUpdatedBy() {
        return ((bitField0_ & 0x00000004) != 0);
      }
      /**
       * <code>string updatedBy = 4;</code>
       * @return The updatedBy.
       */
      public java.lang.String getUpdatedBy() {
        java.lang.Object ref = updatedBy_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          updatedBy_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <code>string updatedBy = 4;</code>
       * @return The bytes for updatedBy.
       */
      public com.google.protobuf.ByteString
          getUpdatedByBytes() {
        java.lang.Object ref = updatedBy_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          updatedBy_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>string updatedBy = 4;</code>
       * @param value The updatedBy to set.
       * @return This builder for chaining.
       */
      public Builder setUpdatedBy(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000004;
        updatedBy_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string updatedBy = 4;</code>
       * @return This builder for chaining.
       */
      public Builder clearUpdatedBy() {
        bitField0_ = (bitField0_ & ~0x00000004);
        updatedBy_ = getDefaultInstance().getUpdatedBy();
        onChanged();
        return this;
      }
      /**
       * <code>string updatedBy = 4;</code>
       * @param value The bytes for updatedBy to set.
       * @return This builder for chaining.
       */
      public Builder setUpdatedByBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000004;
        updatedBy_ = value;
        onChanged();
        return this;
      }

      private java.lang.Object name_ = "";
      /**
       * <code>string name = 5;</code>
       * @return Whether the name field is set.
       */
      public boolean hasName() {
        return ((bitField0_ & 0x00000008) != 0);
      }
      /**
       * <code>string name = 5;</code>
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
       * <code>string name = 5;</code>
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
       * <code>string name = 5;</code>
       * @param value The name to set.
       * @return This builder for chaining.
       */
      public Builder setName(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  bitField0_ |= 0x00000008;
        name_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>string name = 5;</code>
       * @return This builder for chaining.
       */
      public Builder clearName() {
        bitField0_ = (bitField0_ & ~0x00000008);
        name_ = getDefaultInstance().getName();
        onChanged();
        return this;
      }
      /**
       * <code>string name = 5;</code>
       * @param value The bytes for name to set.
       * @return This builder for chaining.
       */
      public Builder setNameBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        bitField0_ |= 0x00000008;
        name_ = value;
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


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.Attachment)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.Attachment)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Attachment>
        PARSER = new com.google.protobuf.AbstractParser<Attachment>() {
      @java.lang.Override
      public Attachment parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new Attachment(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Attachment> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Attachment> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Attachment getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface DeadlineOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.Deadline)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>map&lt;string, string&gt; content = 1;</code>
     */
    int getContentCount();
    /**
     * <code>map&lt;string, string&gt; content = 1;</code>
     */
    boolean containsContent(
        java.lang.String key);
    /**
     * Use {@link #getContentMap()} instead.
     */
    @java.lang.Deprecated
    java.util.Map<java.lang.String, java.lang.String>
    getContent();
    /**
     * <code>map&lt;string, string&gt; content = 1;</code>
     */
    java.util.Map<java.lang.String, java.lang.String>
    getContentMap();
    /**
     * <code>map&lt;string, string&gt; content = 1;</code>
     */

    java.lang.String getContentOrDefault(
        java.lang.String key,
        java.lang.String defaultValue);
    /**
     * <code>map&lt;string, string&gt; content = 1;</code>
     */

    java.lang.String getContentOrThrow(
        java.lang.String key);
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.Deadline}
   */
  public static final class Deadline extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.Deadline)
      DeadlineOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use Deadline.newBuilder() to construct.
    private Deadline(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Deadline() {
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new Deadline();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Deadline(
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
                content_ = com.google.protobuf.MapField.newMapField(
                    ContentDefaultEntryHolder.defaultEntry);
                mutable_bitField0_ |= 0x00000001;
              }
              com.google.protobuf.MapEntry<java.lang.String, java.lang.String>
              content__ = input.readMessage(
                  ContentDefaultEntryHolder.defaultEntry.getParserForType(), extensionRegistry);
              content_.getMutableMap().put(
                  content__.getKey(), content__.getValue());
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
      return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Deadline_descriptor;
    }

    @SuppressWarnings({"rawtypes"})
    @java.lang.Override
    protected com.google.protobuf.MapField internalGetMapField(
        int number) {
      switch (number) {
        case 1:
          return internalGetContent();
        default:
          throw new RuntimeException(
              "Invalid map field number: " + number);
      }
    }
    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Deadline_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline.class, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline.Builder.class);
    }

    public static final int CONTENT_FIELD_NUMBER = 1;
    private static final class ContentDefaultEntryHolder {
      static final com.google.protobuf.MapEntry<
          java.lang.String, java.lang.String> defaultEntry =
              com.google.protobuf.MapEntry
              .<java.lang.String, java.lang.String>newDefaultInstance(
                  org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Deadline_ContentEntry_descriptor, 
                  com.google.protobuf.WireFormat.FieldType.STRING,
                  "",
                  com.google.protobuf.WireFormat.FieldType.STRING,
                  "");
    }
    private com.google.protobuf.MapField<
        java.lang.String, java.lang.String> content_;
    private com.google.protobuf.MapField<java.lang.String, java.lang.String>
    internalGetContent() {
      if (content_ == null) {
        return com.google.protobuf.MapField.emptyMapField(
            ContentDefaultEntryHolder.defaultEntry);
      }
      return content_;
    }

    public int getContentCount() {
      return internalGetContent().getMap().size();
    }
    /**
     * <code>map&lt;string, string&gt; content = 1;</code>
     */

    @java.lang.Override
    public boolean containsContent(
        java.lang.String key) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      return internalGetContent().getMap().containsKey(key);
    }
    /**
     * Use {@link #getContentMap()} instead.
     */
    @java.lang.Override
    @java.lang.Deprecated
    public java.util.Map<java.lang.String, java.lang.String> getContent() {
      return getContentMap();
    }
    /**
     * <code>map&lt;string, string&gt; content = 1;</code>
     */
    @java.lang.Override

    public java.util.Map<java.lang.String, java.lang.String> getContentMap() {
      return internalGetContent().getMap();
    }
    /**
     * <code>map&lt;string, string&gt; content = 1;</code>
     */
    @java.lang.Override

    public java.lang.String getContentOrDefault(
        java.lang.String key,
        java.lang.String defaultValue) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      java.util.Map<java.lang.String, java.lang.String> map =
          internalGetContent().getMap();
      return map.containsKey(key) ? map.get(key) : defaultValue;
    }
    /**
     * <code>map&lt;string, string&gt; content = 1;</code>
     */
    @java.lang.Override

    public java.lang.String getContentOrThrow(
        java.lang.String key) {
      if (key == null) { throw new java.lang.NullPointerException(); }
      java.util.Map<java.lang.String, java.lang.String> map =
          internalGetContent().getMap();
      if (!map.containsKey(key)) {
        throw new java.lang.IllegalArgumentException();
      }
      return map.get(key);
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
      com.google.protobuf.GeneratedMessageV3
        .serializeStringMapTo(
          output,
          internalGetContent(),
          ContentDefaultEntryHolder.defaultEntry,
          1);
      unknownFields.writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      for (java.util.Map.Entry<java.lang.String, java.lang.String> entry
           : internalGetContent().getMap().entrySet()) {
        com.google.protobuf.MapEntry<java.lang.String, java.lang.String>
        content__ = ContentDefaultEntryHolder.defaultEntry.newBuilderForType()
            .setKey(entry.getKey())
            .setValue(entry.getValue())
            .build();
        size += com.google.protobuf.CodedOutputStream
            .computeMessageSize(1, content__);
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
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline other = (org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline) obj;

      if (!internalGetContent().equals(
          other.internalGetContent())) return false;
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
      if (!internalGetContent().getMap().isEmpty()) {
        hash = (37 * hash) + CONTENT_FIELD_NUMBER;
        hash = (53 * hash) + internalGetContent().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline parseFrom(
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
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline prototype) {
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
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.Deadline}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.Deadline)
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.DeadlineOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Deadline_descriptor;
      }

      @SuppressWarnings({"rawtypes"})
      protected com.google.protobuf.MapField internalGetMapField(
          int number) {
        switch (number) {
          case 1:
            return internalGetContent();
          default:
            throw new RuntimeException(
                "Invalid map field number: " + number);
        }
      }
      @SuppressWarnings({"rawtypes"})
      protected com.google.protobuf.MapField internalGetMutableMapField(
          int number) {
        switch (number) {
          case 1:
            return internalGetMutableContent();
          default:
            throw new RuntimeException(
                "Invalid map field number: " + number);
        }
      }
      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Deadline_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline.class, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline.newBuilder()
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
        internalGetMutableContent().clear();
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Deadline_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline build() {
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline result = new org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline(this);
        int from_bitField0_ = bitField0_;
        result.content_ = internalGetContent();
        result.content_.makeImmutable();
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
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline.getDefaultInstance()) return this;
        internalGetMutableContent().mergeFrom(
            other.internalGetContent());
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
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private com.google.protobuf.MapField<
          java.lang.String, java.lang.String> content_;
      private com.google.protobuf.MapField<java.lang.String, java.lang.String>
      internalGetContent() {
        if (content_ == null) {
          return com.google.protobuf.MapField.emptyMapField(
              ContentDefaultEntryHolder.defaultEntry);
        }
        return content_;
      }
      private com.google.protobuf.MapField<java.lang.String, java.lang.String>
      internalGetMutableContent() {
        onChanged();;
        if (content_ == null) {
          content_ = com.google.protobuf.MapField.newMapField(
              ContentDefaultEntryHolder.defaultEntry);
        }
        if (!content_.isMutable()) {
          content_ = content_.copy();
        }
        return content_;
      }

      public int getContentCount() {
        return internalGetContent().getMap().size();
      }
      /**
       * <code>map&lt;string, string&gt; content = 1;</code>
       */

      @java.lang.Override
      public boolean containsContent(
          java.lang.String key) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        return internalGetContent().getMap().containsKey(key);
      }
      /**
       * Use {@link #getContentMap()} instead.
       */
      @java.lang.Override
      @java.lang.Deprecated
      public java.util.Map<java.lang.String, java.lang.String> getContent() {
        return getContentMap();
      }
      /**
       * <code>map&lt;string, string&gt; content = 1;</code>
       */
      @java.lang.Override

      public java.util.Map<java.lang.String, java.lang.String> getContentMap() {
        return internalGetContent().getMap();
      }
      /**
       * <code>map&lt;string, string&gt; content = 1;</code>
       */
      @java.lang.Override

      public java.lang.String getContentOrDefault(
          java.lang.String key,
          java.lang.String defaultValue) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        java.util.Map<java.lang.String, java.lang.String> map =
            internalGetContent().getMap();
        return map.containsKey(key) ? map.get(key) : defaultValue;
      }
      /**
       * <code>map&lt;string, string&gt; content = 1;</code>
       */
      @java.lang.Override

      public java.lang.String getContentOrThrow(
          java.lang.String key) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        java.util.Map<java.lang.String, java.lang.String> map =
            internalGetContent().getMap();
        if (!map.containsKey(key)) {
          throw new java.lang.IllegalArgumentException();
        }
        return map.get(key);
      }

      public Builder clearContent() {
        internalGetMutableContent().getMutableMap()
            .clear();
        return this;
      }
      /**
       * <code>map&lt;string, string&gt; content = 1;</code>
       */

      public Builder removeContent(
          java.lang.String key) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        internalGetMutableContent().getMutableMap()
            .remove(key);
        return this;
      }
      /**
       * Use alternate mutation accessors instead.
       */
      @java.lang.Deprecated
      public java.util.Map<java.lang.String, java.lang.String>
      getMutableContent() {
        return internalGetMutableContent().getMutableMap();
      }
      /**
       * <code>map&lt;string, string&gt; content = 1;</code>
       */
      public Builder putContent(
          java.lang.String key,
          java.lang.String value) {
        if (key == null) { throw new java.lang.NullPointerException(); }
        if (value == null) { throw new java.lang.NullPointerException(); }
        internalGetMutableContent().getMutableMap()
            .put(key, value);
        return this;
      }
      /**
       * <code>map&lt;string, string&gt; content = 1;</code>
       */

      public Builder putAllContent(
          java.util.Map<java.lang.String, java.lang.String> values) {
        internalGetMutableContent().getMutableMap()
            .putAll(values);
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


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.Deadline)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.Deadline)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Deadline>
        PARSER = new com.google.protobuf.AbstractParser<Deadline>() {
      @java.lang.Override
      public Deadline parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new Deadline(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Deadline> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Deadline> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Deadline getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  public interface ReassignmentOrBuilder extends
      // @@protoc_insertion_point(interface_extends:org.kie.kogito.serialization.process.protobuf.Reassignment)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>repeated string users = 1;</code>
     * @return A list containing the users.
     */
    java.util.List<java.lang.String>
        getUsersList();
    /**
     * <code>repeated string users = 1;</code>
     * @return The count of users.
     */
    int getUsersCount();
    /**
     * <code>repeated string users = 1;</code>
     * @param index The index of the element to return.
     * @return The users at the given index.
     */
    java.lang.String getUsers(int index);
    /**
     * <code>repeated string users = 1;</code>
     * @param index The index of the value to return.
     * @return The bytes of the users at the given index.
     */
    com.google.protobuf.ByteString
        getUsersBytes(int index);

    /**
     * <code>repeated string groups = 2;</code>
     * @return A list containing the groups.
     */
    java.util.List<java.lang.String>
        getGroupsList();
    /**
     * <code>repeated string groups = 2;</code>
     * @return The count of groups.
     */
    int getGroupsCount();
    /**
     * <code>repeated string groups = 2;</code>
     * @param index The index of the element to return.
     * @return The groups at the given index.
     */
    java.lang.String getGroups(int index);
    /**
     * <code>repeated string groups = 2;</code>
     * @param index The index of the value to return.
     * @return The bytes of the groups at the given index.
     */
    com.google.protobuf.ByteString
        getGroupsBytes(int index);
  }
  /**
   * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.Reassignment}
   */
  public static final class Reassignment extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:org.kie.kogito.serialization.process.protobuf.Reassignment)
      ReassignmentOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use Reassignment.newBuilder() to construct.
    private Reassignment(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private Reassignment() {
      users_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      groups_ = com.google.protobuf.LazyStringArrayList.EMPTY;
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new Reassignment();
    }

    @java.lang.Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return this.unknownFields;
    }
    private Reassignment(
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
                users_ = new com.google.protobuf.LazyStringArrayList();
                mutable_bitField0_ |= 0x00000001;
              }
              users_.add(s);
              break;
            }
            case 18: {
              java.lang.String s = input.readStringRequireUtf8();
              if (!((mutable_bitField0_ & 0x00000002) != 0)) {
                groups_ = new com.google.protobuf.LazyStringArrayList();
                mutable_bitField0_ |= 0x00000002;
              }
              groups_.add(s);
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
          users_ = users_.getUnmodifiableView();
        }
        if (((mutable_bitField0_ & 0x00000002) != 0)) {
          groups_ = groups_.getUnmodifiableView();
        }
        this.unknownFields = unknownFields.build();
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Reassignment_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Reassignment_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment.class, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment.Builder.class);
    }

    public static final int USERS_FIELD_NUMBER = 1;
    private com.google.protobuf.LazyStringList users_;
    /**
     * <code>repeated string users = 1;</code>
     * @return A list containing the users.
     */
    public com.google.protobuf.ProtocolStringList
        getUsersList() {
      return users_;
    }
    /**
     * <code>repeated string users = 1;</code>
     * @return The count of users.
     */
    public int getUsersCount() {
      return users_.size();
    }
    /**
     * <code>repeated string users = 1;</code>
     * @param index The index of the element to return.
     * @return The users at the given index.
     */
    public java.lang.String getUsers(int index) {
      return users_.get(index);
    }
    /**
     * <code>repeated string users = 1;</code>
     * @param index The index of the value to return.
     * @return The bytes of the users at the given index.
     */
    public com.google.protobuf.ByteString
        getUsersBytes(int index) {
      return users_.getByteString(index);
    }

    public static final int GROUPS_FIELD_NUMBER = 2;
    private com.google.protobuf.LazyStringList groups_;
    /**
     * <code>repeated string groups = 2;</code>
     * @return A list containing the groups.
     */
    public com.google.protobuf.ProtocolStringList
        getGroupsList() {
      return groups_;
    }
    /**
     * <code>repeated string groups = 2;</code>
     * @return The count of groups.
     */
    public int getGroupsCount() {
      return groups_.size();
    }
    /**
     * <code>repeated string groups = 2;</code>
     * @param index The index of the element to return.
     * @return The groups at the given index.
     */
    public java.lang.String getGroups(int index) {
      return groups_.get(index);
    }
    /**
     * <code>repeated string groups = 2;</code>
     * @param index The index of the value to return.
     * @return The bytes of the groups at the given index.
     */
    public com.google.protobuf.ByteString
        getGroupsBytes(int index) {
      return groups_.getByteString(index);
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
      for (int i = 0; i < users_.size(); i++) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, users_.getRaw(i));
      }
      for (int i = 0; i < groups_.size(); i++) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, groups_.getRaw(i));
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
        for (int i = 0; i < users_.size(); i++) {
          dataSize += computeStringSizeNoTag(users_.getRaw(i));
        }
        size += dataSize;
        size += 1 * getUsersList().size();
      }
      {
        int dataSize = 0;
        for (int i = 0; i < groups_.size(); i++) {
          dataSize += computeStringSizeNoTag(groups_.getRaw(i));
        }
        size += dataSize;
        size += 1 * getGroupsList().size();
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
      if (!(obj instanceof org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment)) {
        return super.equals(obj);
      }
      org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment other = (org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment) obj;

      if (!getUsersList()
          .equals(other.getUsersList())) return false;
      if (!getGroupsList()
          .equals(other.getGroupsList())) return false;
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
      if (getUsersCount() > 0) {
        hash = (37 * hash) + USERS_FIELD_NUMBER;
        hash = (53 * hash) + getUsersList().hashCode();
      }
      if (getGroupsCount() > 0) {
        hash = (37 * hash) + GROUPS_FIELD_NUMBER;
        hash = (53 * hash) + getGroupsList().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment parseFrom(
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
    public static Builder newBuilder(org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment prototype) {
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
     * Protobuf type {@code org.kie.kogito.serialization.process.protobuf.Reassignment}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:org.kie.kogito.serialization.process.protobuf.Reassignment)
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.ReassignmentOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Reassignment_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Reassignment_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment.class, org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment.Builder.class);
      }

      // Construct using org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment.newBuilder()
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
        users_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        groups_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000002);
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.internal_static_org_kie_kogito_serialization_process_protobuf_Reassignment_descriptor;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment getDefaultInstanceForType() {
        return org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment.getDefaultInstance();
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment build() {
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment buildPartial() {
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment result = new org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment(this);
        int from_bitField0_ = bitField0_;
        if (((bitField0_ & 0x00000001) != 0)) {
          users_ = users_.getUnmodifiableView();
          bitField0_ = (bitField0_ & ~0x00000001);
        }
        result.users_ = users_;
        if (((bitField0_ & 0x00000002) != 0)) {
          groups_ = groups_.getUnmodifiableView();
          bitField0_ = (bitField0_ & ~0x00000002);
        }
        result.groups_ = groups_;
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
        if (other instanceof org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment) {
          return mergeFrom((org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment other) {
        if (other == org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment.getDefaultInstance()) return this;
        if (!other.users_.isEmpty()) {
          if (users_.isEmpty()) {
            users_ = other.users_;
            bitField0_ = (bitField0_ & ~0x00000001);
          } else {
            ensureUsersIsMutable();
            users_.addAll(other.users_);
          }
          onChanged();
        }
        if (!other.groups_.isEmpty()) {
          if (groups_.isEmpty()) {
            groups_ = other.groups_;
            bitField0_ = (bitField0_ & ~0x00000002);
          } else {
            ensureGroupsIsMutable();
            groups_.addAll(other.groups_);
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
        org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }
      private int bitField0_;

      private com.google.protobuf.LazyStringList users_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      private void ensureUsersIsMutable() {
        if (!((bitField0_ & 0x00000001) != 0)) {
          users_ = new com.google.protobuf.LazyStringArrayList(users_);
          bitField0_ |= 0x00000001;
         }
      }
      /**
       * <code>repeated string users = 1;</code>
       * @return A list containing the users.
       */
      public com.google.protobuf.ProtocolStringList
          getUsersList() {
        return users_.getUnmodifiableView();
      }
      /**
       * <code>repeated string users = 1;</code>
       * @return The count of users.
       */
      public int getUsersCount() {
        return users_.size();
      }
      /**
       * <code>repeated string users = 1;</code>
       * @param index The index of the element to return.
       * @return The users at the given index.
       */
      public java.lang.String getUsers(int index) {
        return users_.get(index);
      }
      /**
       * <code>repeated string users = 1;</code>
       * @param index The index of the value to return.
       * @return The bytes of the users at the given index.
       */
      public com.google.protobuf.ByteString
          getUsersBytes(int index) {
        return users_.getByteString(index);
      }
      /**
       * <code>repeated string users = 1;</code>
       * @param index The index to set the value at.
       * @param value The users to set.
       * @return This builder for chaining.
       */
      public Builder setUsers(
          int index, java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureUsersIsMutable();
        users_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string users = 1;</code>
       * @param value The users to add.
       * @return This builder for chaining.
       */
      public Builder addUsers(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureUsersIsMutable();
        users_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string users = 1;</code>
       * @param values The users to add.
       * @return This builder for chaining.
       */
      public Builder addAllUsers(
          java.lang.Iterable<java.lang.String> values) {
        ensureUsersIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, users_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string users = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearUsers() {
        users_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000001);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string users = 1;</code>
       * @param value The bytes of the users to add.
       * @return This builder for chaining.
       */
      public Builder addUsersBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        ensureUsersIsMutable();
        users_.add(value);
        onChanged();
        return this;
      }

      private com.google.protobuf.LazyStringList groups_ = com.google.protobuf.LazyStringArrayList.EMPTY;
      private void ensureGroupsIsMutable() {
        if (!((bitField0_ & 0x00000002) != 0)) {
          groups_ = new com.google.protobuf.LazyStringArrayList(groups_);
          bitField0_ |= 0x00000002;
         }
      }
      /**
       * <code>repeated string groups = 2;</code>
       * @return A list containing the groups.
       */
      public com.google.protobuf.ProtocolStringList
          getGroupsList() {
        return groups_.getUnmodifiableView();
      }
      /**
       * <code>repeated string groups = 2;</code>
       * @return The count of groups.
       */
      public int getGroupsCount() {
        return groups_.size();
      }
      /**
       * <code>repeated string groups = 2;</code>
       * @param index The index of the element to return.
       * @return The groups at the given index.
       */
      public java.lang.String getGroups(int index) {
        return groups_.get(index);
      }
      /**
       * <code>repeated string groups = 2;</code>
       * @param index The index of the value to return.
       * @return The bytes of the groups at the given index.
       */
      public com.google.protobuf.ByteString
          getGroupsBytes(int index) {
        return groups_.getByteString(index);
      }
      /**
       * <code>repeated string groups = 2;</code>
       * @param index The index to set the value at.
       * @param value The groups to set.
       * @return This builder for chaining.
       */
      public Builder setGroups(
          int index, java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureGroupsIsMutable();
        groups_.set(index, value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string groups = 2;</code>
       * @param value The groups to add.
       * @return This builder for chaining.
       */
      public Builder addGroups(
          java.lang.String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  ensureGroupsIsMutable();
        groups_.add(value);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string groups = 2;</code>
       * @param values The groups to add.
       * @return This builder for chaining.
       */
      public Builder addAllGroups(
          java.lang.Iterable<java.lang.String> values) {
        ensureGroupsIsMutable();
        com.google.protobuf.AbstractMessageLite.Builder.addAll(
            values, groups_);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string groups = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearGroups() {
        groups_ = com.google.protobuf.LazyStringArrayList.EMPTY;
        bitField0_ = (bitField0_ & ~0x00000002);
        onChanged();
        return this;
      }
      /**
       * <code>repeated string groups = 2;</code>
       * @param value The bytes of the groups to add.
       * @return This builder for chaining.
       */
      public Builder addGroupsBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        ensureGroupsIsMutable();
        groups_.add(value);
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


      // @@protoc_insertion_point(builder_scope:org.kie.kogito.serialization.process.protobuf.Reassignment)
    }

    // @@protoc_insertion_point(class_scope:org.kie.kogito.serialization.process.protobuf.Reassignment)
    private static final org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment();
    }

    public static org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<Reassignment>
        PARSER = new com.google.protobuf.AbstractParser<Reassignment>() {
      @java.lang.Override
      public Reassignment parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        return new Reassignment(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<Reassignment> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<Reassignment> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public org.kie.kogito.serialization.process.protobuf.KogitoWorkItemsProtobuf.Reassignment getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_StartDeadlinesEntry_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_StartDeadlinesEntry_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_CompletedDeadlinesEntry_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_CompletedDeadlinesEntry_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_StartReassigmentsEntry_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_StartReassigmentsEntry_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_CompletedReassigmentsEntry_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_CompletedReassigmentsEntry_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_Comment_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_Comment_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_Attachment_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_Attachment_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_Deadline_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_Deadline_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_Deadline_ContentEntry_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_Deadline_ContentEntry_fieldAccessorTable;
  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_org_kie_kogito_serialization_process_protobuf_Reassignment_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_org_kie_kogito_serialization_process_protobuf_Reassignment_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\nEorg/kie/kogito/serialization/process/p" +
      "rotobuf/kogito_work_items.proto\022-org.kie" +
      ".kogito.serialization.process.protobuf\"\307" +
      "\013\n\025HumanTaskWorkItemData\022\026\n\ttask_name\030\001 " +
      "\001(\tH\000\210\001\001\022\035\n\020task_description\030\002 \001(\tH\001\210\001\001\022" +
      "\032\n\rtask_priority\030\003 \001(\tH\002\210\001\001\022\031\n\014actual_ow" +
      "ner\030\004 \001(\tH\003\210\001\001\022\021\n\tpot_users\030\005 \003(\t\022\022\n\npot" +
      "_groups\030\006 \003(\t\022\026\n\016excluded_users\030\007 \003(\t\022\023\n" +
      "\013admin_users\030\010 \003(\t\022\024\n\014admin_groups\030\t \003(\t" +
      "\022 \n\023task_reference_name\030\n \001(\tH\004\210\001\001\022H\n\010co" +
      "mments\030\013 \003(\01326.org.kie.kogito.serializat" +
      "ion.process.protobuf.Comment\022N\n\013attachme" +
      "nts\030\014 \003(\01329.org.kie.kogito.serialization" +
      ".process.protobuf.Attachment\022q\n\017start_de" +
      "adlines\030\r \003(\0132X.org.kie.kogito.serializa" +
      "tion.process.protobuf.HumanTaskWorkItemD" +
      "ata.StartDeadlinesEntry\022y\n\023completed_dea" +
      "dlines\030\016 \003(\0132\\.org.kie.kogito.serializat" +
      "ion.process.protobuf.HumanTaskWorkItemDa" +
      "ta.CompletedDeadlinesEntry\022w\n\022start_reas" +
      "sigments\030\017 \003(\0132[.org.kie.kogito.serializ" +
      "ation.process.protobuf.HumanTaskWorkItem" +
      "Data.StartReassigmentsEntry\022\177\n\026completed" +
      "_reassigments\030\020 \003(\0132_.org.kie.kogito.ser" +
      "ialization.process.protobuf.HumanTaskWor" +
      "kItemData.CompletedReassigmentsEntry\032n\n\023" +
      "StartDeadlinesEntry\022\013\n\003key\030\001 \001(\t\022F\n\005valu" +
      "e\030\002 \001(\01327.org.kie.kogito.serialization.p" +
      "rocess.protobuf.Deadline:\0028\001\032r\n\027Complete" +
      "dDeadlinesEntry\022\013\n\003key\030\001 \001(\t\022F\n\005value\030\002 " +
      "\001(\01327.org.kie.kogito.serialization.proce" +
      "ss.protobuf.Deadline:\0028\001\032u\n\026StartReassig" +
      "mentsEntry\022\013\n\003key\030\001 \001(\t\022J\n\005value\030\002 \001(\0132;" +
      ".org.kie.kogito.serialization.process.pr" +
      "otobuf.Reassignment:\0028\001\032y\n\032CompletedReas" +
      "sigmentsEntry\022\013\n\003key\030\001 \001(\t\022J\n\005value\030\002 \001(" +
      "\0132;.org.kie.kogito.serialization.process" +
      ".protobuf.Reassignment:\0028\001B\014\n\n_task_name" +
      "B\023\n\021_task_descriptionB\020\n\016_task_priorityB" +
      "\017\n\r_actual_ownerB\026\n\024_task_reference_name" +
      "\"\203\001\n\007Comment\022\n\n\002id\030\001 \001(\t\022\024\n\007content\030\002 \001(" +
      "\tH\000\210\001\001\022\026\n\tupdatedAt\030\003 \001(\003H\001\210\001\001\022\026\n\tupdate" +
      "dBy\030\004 \001(\tH\002\210\001\001B\n\n\010_contentB\014\n\n_updatedAt" +
      "B\014\n\n_updatedBy\"\242\001\n\nAttachment\022\n\n\002id\030\001 \001(" +
      "\t\022\024\n\007content\030\002 \001(\tH\000\210\001\001\022\026\n\tupdatedAt\030\003 \001" +
      "(\003H\001\210\001\001\022\026\n\tupdatedBy\030\004 \001(\tH\002\210\001\001\022\021\n\004name\030" +
      "\005 \001(\tH\003\210\001\001B\n\n\010_contentB\014\n\n_updatedAtB\014\n\n" +
      "_updatedByB\007\n\005_name\"\221\001\n\010Deadline\022U\n\007cont" +
      "ent\030\001 \003(\0132D.org.kie.kogito.serialization" +
      ".process.protobuf.Deadline.ContentEntry\032" +
      ".\n\014ContentEntry\022\013\n\003key\030\001 \001(\t\022\r\n\005value\030\002 " +
      "\001(\t:\0028\001\"-\n\014Reassignment\022\r\n\005users\030\001 \003(\t\022\016" +
      "\n\006groups\030\002 \003(\tB\031B\027KogitoWorkItemsProtobu" +
      "fb\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_descriptor,
        new java.lang.String[] { "TaskName", "TaskDescription", "TaskPriority", "ActualOwner", "PotUsers", "PotGroups", "ExcludedUsers", "AdminUsers", "AdminGroups", "TaskReferenceName", "Comments", "Attachments", "StartDeadlines", "CompletedDeadlines", "StartReassigments", "CompletedReassigments", "TaskName", "TaskDescription", "TaskPriority", "ActualOwner", "TaskReferenceName", });
    internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_StartDeadlinesEntry_descriptor =
      internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_descriptor.getNestedTypes().get(0);
    internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_StartDeadlinesEntry_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_StartDeadlinesEntry_descriptor,
        new java.lang.String[] { "Key", "Value", });
    internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_CompletedDeadlinesEntry_descriptor =
      internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_descriptor.getNestedTypes().get(1);
    internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_CompletedDeadlinesEntry_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_CompletedDeadlinesEntry_descriptor,
        new java.lang.String[] { "Key", "Value", });
    internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_StartReassigmentsEntry_descriptor =
      internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_descriptor.getNestedTypes().get(2);
    internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_StartReassigmentsEntry_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_StartReassigmentsEntry_descriptor,
        new java.lang.String[] { "Key", "Value", });
    internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_CompletedReassigmentsEntry_descriptor =
      internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_descriptor.getNestedTypes().get(3);
    internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_CompletedReassigmentsEntry_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_HumanTaskWorkItemData_CompletedReassigmentsEntry_descriptor,
        new java.lang.String[] { "Key", "Value", });
    internal_static_org_kie_kogito_serialization_process_protobuf_Comment_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_org_kie_kogito_serialization_process_protobuf_Comment_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_Comment_descriptor,
        new java.lang.String[] { "Id", "Content", "UpdatedAt", "UpdatedBy", "Content", "UpdatedAt", "UpdatedBy", });
    internal_static_org_kie_kogito_serialization_process_protobuf_Attachment_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_org_kie_kogito_serialization_process_protobuf_Attachment_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_Attachment_descriptor,
        new java.lang.String[] { "Id", "Content", "UpdatedAt", "UpdatedBy", "Name", "Content", "UpdatedAt", "UpdatedBy", "Name", });
    internal_static_org_kie_kogito_serialization_process_protobuf_Deadline_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_org_kie_kogito_serialization_process_protobuf_Deadline_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_Deadline_descriptor,
        new java.lang.String[] { "Content", });
    internal_static_org_kie_kogito_serialization_process_protobuf_Deadline_ContentEntry_descriptor =
      internal_static_org_kie_kogito_serialization_process_protobuf_Deadline_descriptor.getNestedTypes().get(0);
    internal_static_org_kie_kogito_serialization_process_protobuf_Deadline_ContentEntry_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_Deadline_ContentEntry_descriptor,
        new java.lang.String[] { "Key", "Value", });
    internal_static_org_kie_kogito_serialization_process_protobuf_Reassignment_descriptor =
      getDescriptor().getMessageTypes().get(4);
    internal_static_org_kie_kogito_serialization_process_protobuf_Reassignment_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_org_kie_kogito_serialization_process_protobuf_Reassignment_descriptor,
        new java.lang.String[] { "Users", "Groups", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
