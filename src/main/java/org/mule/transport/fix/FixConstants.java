/*
 * $Id: JmsConstants.java 14058 2009-02-17 23:34:00Z aperepel $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.fix;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// @ThreadSafe
public class FixConstants
{

    public static final String FIX_RECEIVED_SESSION_ID = "FIXReceivedSessionID";
    public static final String FIX_DISPATCH_SESSION_ID = "FIXDispatchSessionID";
    public static final String FIX_SESSION_ID ="FIXSessionID";
    public static final String BEGIN_STRING = "begin-string";
    public static final String SENDER_COMP_ID = "sender";
    public static final String TARGET_COMP_ID = "target";
    public static final String SESSION_ID = "sessionID";
    
    public static final Set FIX_PROPERTY_NAMES = Collections.unmodifiableSet(new HashSet(
        Arrays.asList(new String[]{FIX_RECEIVED_SESSION_ID,FIX_DISPATCH_SESSION_ID,FIX_SESSION_ID})));

}
