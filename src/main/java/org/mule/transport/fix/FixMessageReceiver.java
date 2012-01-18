/*
 * $Id: MessageReceiver.vm 11079 2008-02-27 15:52:01Z tcarlson $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.fix;

import java.util.ArrayList;
import java.util.List;

import org.mule.api.construct.FlowConstruct;
import org.mule.transport.AbstractReceiverWorker;
import org.mule.transport.ConnectException;
import org.mule.transport.AbstractMessageReceiver;
import org.mule.api.MessagingException;
import org.mule.api.service.Service;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.lifecycle.CreateException;
import org.mule.api.transaction.Transaction;
import org.mule.api.transaction.TransactionException;
import org.mule.api.transport.Connector;

import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;

/**
 * <code>FixMessageReceiver</code> Responsible of creating a Fix worker to carry
 * out the rest of the inbound flow. If synchronous no worker is used.
 */
public class FixMessageReceiver extends AbstractMessageReceiver {

    public FixMessageReceiver(Connector connector, FlowConstruct flowConstruct, InboundEndpoint endpoint)
            throws CreateException {
		super(connector, flowConstruct, endpoint);
	}

	public void doConnect() throws ConnectException {
		// Connection is managed by Connector
	}

	public void doDisconnect() throws ConnectException {
		// Connection is managed by Connector
	}

	public void doStart() {
		//Connection is managed by Connector
	}

	public void doStop() {
		//Connection is managed by Connector
	}

	public void doDispose() {
		//Connection is managed by Connector
	}

	public void fromApp(Message message, SessionID sessionId)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
			UnsupportedMessageType {
		//this method is called when an application message has been received
		logger.debug("Received Message from App on Session " + sessionId + ":"
				+ message);
		try {
			getWorkManager().scheduleWork(
					new FixWorker(message, sessionId, this));
		} catch (Exception e) {
//			handleException(e);
		}

	}

	protected class FixWorker extends AbstractReceiverWorker {
		SessionID sessionId;

		public FixWorker(Message message, SessionID sessionId,
				AbstractMessageReceiver receiver) throws MessagingException {
			super(new ArrayList(1), receiver);

			this.sessionId = sessionId;
			messages.add(message);
		}

		public FixWorker(List messages, SessionID sessionId,
				AbstractMessageReceiver receiver) throws MessagingException {
			super(new ArrayList(messages), receiver);
			this.sessionId = sessionId;
		}

		@Override
		protected void bindTransaction(Transaction tx)
				throws TransactionException {
			//no transactions
		}

/*
		@Override
		protected Object preProcessMessage(Object message) throws Exception {
			MessageAdapter adapter;
			adapter = endpoint.getConnector().getMessageAdapter(message);
			// TODO this session ID is being shared by more than 1 message,
			// this could be a problem if the property is changed...
			adapter
					.setProperty(FixConstants.FIX_RECEIVED_SESSION_ID,
							sessionId);
			return adapter;
		}
*/
	}

}
