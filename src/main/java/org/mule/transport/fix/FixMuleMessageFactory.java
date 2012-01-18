package org.mule.transport.fix;

import org.mule.api.MuleContext;
import org.mule.transport.AbstractMuleMessageFactory;
import quickfix.Message;

/**
 * Created by IntelliJ IDEA.
 * User: ming
 * Date: 1/16/12
 * Time: 10:56 PM
 */
public class FixMuleMessageFactory extends AbstractMuleMessageFactory{
    public FixMuleMessageFactory(MuleContext context) {
        super(context);
    }

    @Override
    protected Class<?>[] getSupportedTransportMessageTypes() {
        return new Class<?>[]{Message.class};
    }

    @Override
    protected Object extractPayload(Object transportMessage, String encoding) throws Exception {
        return transportMessage;
    }
}
