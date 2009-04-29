package org.mule.transport.fix.examples.mock;

import quickfix.Message;

public interface FixDispatcherInterface {
	public void dispatch(Message payload);
}
