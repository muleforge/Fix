/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.fix;

import org.mule.api.MessagingException;
import org.mule.api.transport.MessageTypeNotSupportedException;
import org.mule.transport.AbstractMessageAdapter;

import quickfix.Message;

/**
 * <code>FixMessageAdapter</code> The message adaptor is used to wrap the
 * quickfix.Message object
 */
public class FixMessageAdapter extends AbstractMessageAdapter {

	Message message;

	public FixMessageAdapter(Object message) throws MessagingException {
		if (message instanceof Message) {
			this.message = (Message) message;
		} else {
			throw new MessageTypeNotSupportedException(message, getClass());
		}
	}

	public String getPayloadAsString(String encoding) throws Exception {
		return message.toString();
	}

	public byte[] getPayloadAsBytes() throws Exception {
		return message.toString().getBytes();
	}

	public Object getPayload() {
		return message;
	}

}
