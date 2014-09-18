package com.me.amps.wasync.common;

import org.testng.Assert;

import com.me.amps.domain.dto.Message;
import com.me.amps.domain.dto.MessageType;

public class ConnectionAssert {
	
	public static void assertSuccessfulAuthentication(MessageHandler handler, long timeout) throws InterruptedException {
		Message suspend = handler.poll(timeout);
		Assert.assertNotNull(suspend,
				"Did not receive suspend response in time");
		Assert.assertEquals(suspend.getType(), MessageType.SUSPEND_RESPONSE);
				
		Message authenticate = handler.poll(timeout);
		Assert.assertNotNull(authenticate,
				"Did not receive authenticate response in time");
		Assert.assertEquals(authenticate.getType(), MessageType.SUCCESSFUL_AUTHENTICATION_RESPONSE);
	} 


	public static void assertContextError(MessageHandler handler, long timeout) throws InterruptedException {
		Message suspend = handler.poll(timeout);
		Assert.assertNotNull(suspend,
				"Did not receive suspend response in time");
		Assert.assertEquals(suspend.getType(), MessageType.SUSPEND_RESPONSE);
				
		Message authenticate = handler.poll(timeout);
		Assert.assertNotNull(authenticate,
				"Did not receive authenticate response in time");
		Assert.assertEquals(authenticate.getType(), MessageType.CONTEXT_ERROR_RESPONSE);
	}
	
	public static void assertFailedAuthentication(MessageHandler handler, long timeout) throws InterruptedException {
		Message suspend = handler.poll(timeout);
		Assert.assertNotNull(suspend,
				"Did not receive suspend response in time");
		Assert.assertEquals(suspend.getType(), MessageType.SUSPEND_RESPONSE);
				
		Message authenticate = handler.poll(timeout);
		Assert.assertNotNull(authenticate,
				"Did not receive authenticate response in time");
		Assert.assertEquals(authenticate.getType(), MessageType.FAILED_AUTHENTICATED_RESPONSE);
	} 

}
