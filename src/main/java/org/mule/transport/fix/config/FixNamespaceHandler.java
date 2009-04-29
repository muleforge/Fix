/*
 * $Id: NamespaceHandler.vm 10621 2008-01-30 12:15:16Z dirk.olmes $
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.transport.fix.config;

import org.mule.config.spring.factories.InboundEndpointFactoryBean;
import org.mule.config.spring.factories.OutboundEndpointFactoryBean;
import org.mule.config.spring.handlers.AbstractMuleNamespaceHandler;
import org.mule.config.spring.parsers.MuleDefinitionParser;
import org.mule.config.spring.parsers.specific.TransformerDefinitionParser;
import org.mule.config.spring.parsers.specific.endpoint.TransportEndpointDefinitionParser;
import org.mule.config.spring.parsers.specific.endpoint.TransportGlobalEndpointDefinitionParser;
import org.mule.transport.fix.FixConnector;
import org.mule.transport.fix.FixConstants;
import org.mule.transport.fix.transformers.FixVersionTransformer;
import org.mule.endpoint.URIBuilder;


/**
 * Registers a Bean Definition Parser for handling <code><fix:connector></code> elements
 * and supporting endpoint elements.
 */
public class FixNamespaceHandler extends AbstractMuleNamespaceHandler
{
	public static final String[][] FIX_ATTRIBUTES = new String[][]{new String[]{FixConstants.SESSION_ID}};
	
    public void init()
    {
        /* This creates handlers for 'endpoint', 'outbound-endpoint' and 'inbound-endpoint' elements.
           The defaults are sufficient unless you have endpoint styles different from the Mule standard ones
           The URIBuilder as constants for common required attributes, but you can also pass in a user-defined String[].
         */
        registerFixTransportEndpoints();

        /* This will create the handler for your custom 'connector' element.  You will need to add handlers for any other
           xml elements you define.  For more information see:
           http://www.mulesource.org/display/MULE2USER/Creating+a+Custom+XML+Namespace
        */
        registerConnectorDefinitionParser(FixConnector.class);
        
        registerBeanDefinitionParser("fix-version-transformer", new TransformerDefinitionParser(FixVersionTransformer.class));
    }
    
    /**
     * Need to use the most complex constructors as have mutually exclusive address aattributes
     */
    protected void registerFixTransportEndpoints()
    {
        registerFixEndpointDefinitionParser("endpoint", new TransportGlobalEndpointDefinitionParser(FixConnector.FIX, TransportGlobalEndpointDefinitionParser.PROTOCOL, TransportGlobalEndpointDefinitionParser.RESTRICTED_ENDPOINT_ATTRIBUTES, FIX_ATTRIBUTES, new String[][]{}));
        registerFixEndpointDefinitionParser("inbound-endpoint", new TransportEndpointDefinitionParser(FixConnector.FIX, TransportEndpointDefinitionParser.PROTOCOL, InboundEndpointFactoryBean.class, TransportEndpointDefinitionParser.RESTRICTED_ENDPOINT_ATTRIBUTES, FIX_ATTRIBUTES, new String[][]{}));
        registerFixEndpointDefinitionParser("outbound-endpoint", new TransportEndpointDefinitionParser(FixConnector.FIX, TransportEndpointDefinitionParser.PROTOCOL, OutboundEndpointFactoryBean.class, TransportEndpointDefinitionParser.RESTRICTED_ENDPOINT_ATTRIBUTES, FIX_ATTRIBUTES, new String[][]{}));
    }

    protected void registerFixEndpointDefinitionParser(String element, MuleDefinitionParser parser)
    {
        parser.addAlias(FixConstants.SESSION_ID, URIBuilder.PATH);
        registerBeanDefinitionParser(element, parser);
    }
}
