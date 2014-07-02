/*
 * $Id: MessageReceiverTestCase.vm 11967 2008-06-05 20:32:19Z dfeist $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.fix;

import org.mule.api.endpoint.EndpointBuilder;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.service.Service;
import org.mule.api.transport.MessageReceiver;
import org.mule.endpoint.EndpointURIEndpointBuilder;
import org.mule.transport.AbstractMessageReceiverTestCase;

import com.mockobjects.dynamic.Mock;

public class FixMessageReceiverTestCase extends AbstractMessageReceiverTestCase {

	public MessageReceiver getMessageReceiver() throws Exception {
		Mock mockService = new Mock(Service.class);
		mockService.expectAndReturn("getResponseRouter", null);
		return new FixMessageReceiver(endpoint.getConnector(),
				(Service) mockService.proxy(), endpoint);
	}

	public InboundEndpoint getEndpoint() throws Exception {
		EndpointBuilder endpointBuilder = new EndpointURIEndpointBuilder(
				"fix://:FIX.4.1:BANZAI-EXEC", muleContext);
		endpoint = muleContext.getEndpointFactory().getInboundEndpoint(endpointBuilder);
		return endpoint;
	}

}
