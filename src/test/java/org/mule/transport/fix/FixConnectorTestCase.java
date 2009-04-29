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

import org.mule.api.transport.Connector;
import org.mule.transport.AbstractConnectorTestCase;

import quickfix.field.ClOrdID;
import quickfix.field.OrigClOrdID;
import quickfix.field.Side;
import quickfix.field.Symbol;

public class FixConnectorTestCase extends AbstractConnectorTestCase
{
    public Connector createConnector() throws Exception
    {
        FixConnector c = new FixConnector();
        c.setName("Test");
        c.setConfig("./src/test/resources/org/mule/transport/fix/general/banzai.cfg");
        return c;
    }

    public String getTestEndpointURI()
    {
    	return "fix://FIX.4.1:BANZAI-EXEC";
    }

    public Object getValidMessage() throws Exception
    {
    	return new quickfix.fix41.OrderCancelRequest(
				new OrigClOrdID("123"), new ClOrdID("321"), new Symbol("LNUX"),
				new Side(Side.BUY));
    }


    public void testProperties() throws Exception
    {
        // TODO test setting and retrieving any custom properties on the
        // Connector as necessary
    }
    
    public void testConnectorMessageRequesterFactory() throws Exception
    {
    	//NO REQUESTOR and thus should not test for it...
    }

}
