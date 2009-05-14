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

import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.transport.AbstractMessageDispatcher;

import quickfix.Message;
import quickfix.Session;
import quickfix.SessionID;

/**
 * <code>FixMessageDispatcher</code> The fix message dispatcher is responsible
 * to send the fix message over a session
 */
public class FixMessageDispatcher extends AbstractMessageDispatcher {
	SessionID sessionID;

	public FixMessageDispatcher(OutboundEndpoint endpoint) {
		super(endpoint);
		// in order to stick to the default sessionids internally we replace the
		// "-" with a "->" since the ">" is not accepted to be part of a URI.

		// TODO currently this is a bit brittle, for example if a name with a -
		// is used it will mess things up. Possibly should prohibit the use of -
		sessionID = new SessionID(endpoint.getEndpointURI().getAuthority()
				.replace("-", "->"));

	}

	public void doConnect() throws Exception {
		// Connection is handled by connector
	}

	public void doDisconnect() throws Exception {
		// Connection is handled by connector
	}

	public void doDispatch(MuleEvent event) throws Exception {
		logger.debug("Sending Message to Session:" + sessionID + ":"
				+ event.getMessage().getPayload());

		Message message = (Message) event.getMessage().getPayload();
		// getting session from the connector
		((FixConnector) connector).lookupSession(sessionID).send(message);

	}

	public MuleMessage doSend(MuleEvent event) throws Exception {
		// Quick fix is mainly asynchronous and so the dispatch will be called
		// and null returned
		doDispatch(event);
		return null;
	}

	public void doDispose() {
		// nothing to dispose
	}

}
