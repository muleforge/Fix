/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.fix;

import org.junit.Test;
import org.mule.api.endpoint.EndpointURI;
import org.mule.endpoint.MuleEndpointURI;
import org.mule.tck.junit4.AbstractMuleContextTestCase;
import org.mule.tck.junit4.AbstractMuleTestCase;

import static junit.framework.Assert.assertEquals;

public class FixEndpointTestCase extends AbstractMuleContextTestCase {

    @Test
	public void testValidEndpointURI() throws Exception {
		EndpointURI url = new MuleEndpointURI("fix://FIX.4.1:BANZAI-EXEC", muleContext);
		assertEquals("fix", url.getScheme());
		assertEquals("FIX.4.1:BANZAI-EXEC", url.getAuthority());
		assertEquals("FIX.4.1:BANZAI-EXEC", url.getAddress());
	}

}
