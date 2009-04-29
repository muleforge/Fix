/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.fix.transformers;

import org.mule.api.transformer.TransformerException;

import quickfix.Message;
import quickfix.field.ClOrdID;
import quickfix.field.OrigClOrdID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import junit.framework.TestCase;

public class FixVersionTestCase extends TestCase
{
	public void testSendingWrongObject()
	{
		quickfix.fix41.OrderCancelRequest message = new quickfix.fix41.OrderCancelRequest(
				new OrigClOrdID("123"), new ClOrdID("321"), new Symbol("LNUX"),
				new Side(Side.BUY));
    	FixVersionTransformer transformer=new FixVersionTransformer();
    	transformer.setFromVersion("4.0");
    	transformer.setToVersion("4.1");
    	Exception exception=null;
    	try {
			Message result=(Message) transformer.doTransform(message, null);
		} catch (TransformerException e) {
			exception=e;
		}
		
		assertNotNull(exception);
		assertEquals("Wrong Class Expected class from Package :quickfix.fix40 but was quickfix.fix41.OrderCancelRequest (java.lang.Throwable)",exception.getMessage());
	}

    public void testVersionTransformer() throws Exception
    {
    	quickfix.fix41.OrderCancelRequest message = new quickfix.fix41.OrderCancelRequest(
				new OrigClOrdID("123"), new ClOrdID("321"), new Symbol("LNUX"),
				new Side(Side.BUY));
    	FixVersionTransformer transformer=new FixVersionTransformer();
    	transformer.setFromVersion("4.1");
    	transformer.setToVersion("4.0");
    	
    	Message resultMessage=(Message) transformer.doTransform(message, null);
    	assertTrue(resultMessage instanceof quickfix.fix40.Message);
    	assertTrue(resultMessage instanceof quickfix.fix40.OrderCancelRequest);
    	quickfix.fix40.OrderCancelRequest result=(quickfix.fix40.OrderCancelRequest)resultMessage;
		assertEquals(message.getOrigClOrdID(), result.getOrigClOrdID());
		assertEquals(message.getClOrdID(), result.getClOrdID());
		assertEquals(message.getSymbol(), result.getSymbol());
		assertEquals(message.getSide(), result.getSide());
    }    

}
