package com.me.amps.domain.dto;

public enum MessageType {
	/**
	 * Indicates successful suspend connection attempt
	 */
	SUSPEND_RESPONSE,

	/**
	 * Indicates a successful authentication on the suspend connection
	 */
	SUCCESSFUL_AUTHENTICATION_RESPONSE,

	/**
	 * Indicates a failed authentication on the suspend connection
	 */
	FAILED_AUTHENTICATED_RESPONSE,

	/**
	 * Indicates context related issue on the suspend connection
	 */
	CONTEXT_ERROR_RESPONSE,

	/**
	 * A file or folder has been created in this user's workspace.  contentId field will contain the ID of the content (either File or Folder).
	 */
	CONTENT_CREATED, 
	/**
	 *  A file or folder in this user's workspace has been updated (renamed, etc).  contentId field will contain the ID of the content.
	 */
	CONTENT_UPDATED, 
	/**
	 * A file or folder in this user's workspace has been deleted.
	 */
	CONTENT_DELETED,
	/**
	 *  Even though it reads CONTENT_SHARED, it only applies to a folder that has been shared with this user. NOTE: This is generated at the time someone simply sends a share invitation to the user.  The "sharee" (target of the invitation) will receive a message of this type PRIOR to accepting it.
	 */
	CONTENT_SHARED,
	/**
	 * When a user unshares a folder a message of this type will be pushed to clients.
	 */
	CONTENT_UNSHARED, 
	/**
	 * When a user (sharee) accepts an invitation a message of this type will be sent to both the owner of the share (sharer) and the person who accepted the invitation (sharee).
	 */
	CONTENT_SHARE_ACCEPTED,
	/**
	 * A message type reserved for cases where the back-end service needs to send a communique to the client device
	 */
	SYSTEM 
}
