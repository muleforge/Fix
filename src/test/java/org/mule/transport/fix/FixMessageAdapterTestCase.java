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

import org.mule.transport.AbstractMessageAdapterTestCase;
import org.mule.api.transport.MessageAdapter;
import org.mule.api.MessagingException;

import quickfix.field.ClOrdID;
import quickfix.field.OrigClOrdID;
import quickfix.field.Side;
import quickfix.field.Symbol;

public class FixMessageAdapterTestCase extends AbstractMessageAdapterTestCase {

	public Object getValidMessage() throws Exception {
		return new quickfix.fix41.OrderCancelRequest(new OrigClOrdID("123"),
				new ClOrdID("321"), new Symbol("LNUX"), new Side(Side.BUY));
	}

	public MessageAdapter createAdapter(Object payload)
			throws MessagingException {
		return new FixMessageAdapter(payload);
	}

}
