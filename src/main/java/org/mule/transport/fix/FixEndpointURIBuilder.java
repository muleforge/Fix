package org.mule.transport.fix;

import org.mule.api.endpoint.MalformedEndpointException;
import org.mule.endpoint.AbstractEndpointURIBuilder;

import java.net.URI;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: ming
 * Date: 1/18/12
 * Time: 1:16 AM
 */
public class FixEndpointURIBuilder extends AbstractEndpointURIBuilder {
    @Override
    protected void setEndpoint(URI uri, Properties props) throws MalformedEndpointException {
        address = uri.getAuthority();
        if (address.contains("--")) {
            address = address.replace("--", "->");
        }
    }
}
