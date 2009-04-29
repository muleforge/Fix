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

import org.mule.tck.FunctionalTestCase;

/**
 * TODO
 */
public class FixNamespaceHandlerTestCase extends FunctionalTestCase
{
    protected String getConfigResources()
    {
        return "fix-namespace-config.xml";
    }

    public void testFixConfig() throws Exception
    {
        FixConnector c = (FixConnector) muleContext.getRegistry().lookupConnector("fixConnector");
        assertNotNull(c);
        
        
        assertTrue(c.isConnected());
        assertTrue(c.isStarted());

        assertEquals("./target/test-classes/org/mule/transport/fix/general/initiator-acceptor-in-same-config.cfg",c.getConfig());
        
    }
}
