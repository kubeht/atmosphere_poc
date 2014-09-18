package com.me.amps.domain.dto;

import java.util.UUID;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Message {

	private String id;
	private MessageType type;
	private long timestamp;
	private UUID resourceUuid;
	private String message;
	private Error error;

	/**
	 * for jason-mapper to decode
	 */
	public Message() { }

	public String getId() {
		return id;
	}

	public MessageType getType() {
		return type;
	}

	public long getTimestamp() {
		return timestamp;
	}

	private void setId(String id) {
		this.id = id;
	}

	private void setType(MessageType type) {
		this.type = type;
	}

	private void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	private void setResourceUuid(UUID resourceUuid) {
		this.resourceUuid = resourceUuid;
	}

	private void setMessage(String message) {
		this.message = message;
	}

	private void setError(Error error) {
		this.error = error;
	}

	public UUID getResourceUuid() {
		return resourceUuid;
	}

	public String getMessage() {
		return message;
	}

	public Error getError() {
		return error;
	}

	
	
	@Override
	public String toString() {
		StringBuilder builder2 = new StringBuilder();
		builder2.append("Message [id=").append(id).append(", type=")
				.append(type).append(", timestamp=").append(timestamp)
				.append(", resourceUuid=").append(resourceUuid)
				.append(", message=").append(message).append(", error=")
				.append(error).append("]");
		return builder2.toString();
	}



	public static class Builder {
		private Message message = new Message();

		public Builder() {
		}

		public Builder id(String id) {
			message.setId(id);
			return this;
		}

		public Builder type(MessageType type) {
			message.setType(type);
			return this;
		}

		public Builder timestmp(long timestamp) {
			message.setTimestamp(timestamp);
			return this;
		}

		public Builder resourceUUId(UUID resourceUuid) {
			message.setResourceUuid(resourceUuid);
			return this;
		}

		public Builder message(String msg) {
			message.setMessage(msg);
			return this;
		}

		public Builder error(Error error) {
			message.setError(error);
			return this;
		}
		
		public Message build() {
			return message;
		}
	}
}
