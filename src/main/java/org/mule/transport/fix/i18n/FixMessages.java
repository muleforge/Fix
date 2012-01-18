/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.fix.i18n;

import org.mule.config.i18n.Message;
import org.mule.config.i18n.MessageFactory;
import org.mule.transport.fix.FixConnector;

import quickfix.SessionID;

public class FixMessages extends MessageFactory {
	private static final String BUNDLE_PATH = getBundlePath("fix");

	private static final FixMessages factory = new FixMessages();

	public static Message exceptionWhileConnectingFileNotFound(Exception e) {
		return factory.createMessage(BUNDLE_PATH, 1, e.getMessage());
	}

	public static Message exceptionWhileConnectingConfigError(Exception e) {
		return factory.createMessage(BUNDLE_PATH, 2, e.getMessage());
	}

	public static Message warningNoReceiverListeningOnSession(
			quickfix.Message message, SessionID sessionID,
			FixConnector connector) {
		return factory.createMessage(BUNDLE_PATH, 3, message.toString(),
				sessionID.toString(), connector.getName());
	}

	public static Message messageReceivedInFromApp(quickfix.Message message,
			SessionID sessionID, FixConnector connector) {
		return factory.createMessage(BUNDLE_PATH, 4, message.toString(),
				sessionID.toString(), connector.getName());
	}

	public static Message messageBeingSentToApp(quickfix.Message message,
			SessionID sessionID, FixConnector connector) {
		return factory.createMessage(BUNDLE_PATH, 5, message.toString(),
				sessionID.toString(), connector.getName());
	}

	public static Message sessionCreated(SessionID sessionID,
			FixConnector connector) {
		return factory.createMessage(BUNDLE_PATH, 6, sessionID.toString(),
				connector.getName());
	}

	public static Message sessionLoggedIn(SessionID sessionID,
			FixConnector connector) {
		return factory.createMessage(BUNDLE_PATH, 7, sessionID.toString(),
				connector.getName());
	}

	public static Message sessionLoggedOut(SessionID sessionID,
			FixConnector connector) {
		return factory.createMessage(BUNDLE_PATH, 8, sessionID.toString(),
				connector.getName());
	}

	public static Message messageStoreFactoryClassNotFound(
			String messageStoreFactory, FixConnector connector) {
		return factory.createMessage(BUNDLE_PATH, 9, messageStoreFactory,
				connector.getName());
	}

	public static Message messageStoreFactoryConstructorNotFound(
			String messageStoreFactory, FixConnector connector) {
		return factory.createMessage(BUNDLE_PATH, 10, messageStoreFactory,
				connector.getName());
	}

	public static Message messageStoreFactoryConstructionThrewException(
			String messageStoreFactory, FixConnector connector, Exception e) {
		return factory.createMessage(BUNDLE_PATH, 11, messageStoreFactory,
				connector.getName(), e.getMessage());
	}

	public static Message messageStoreFactoryClassDoesNotInheritMessageStoreFactory(
			String messageStoreFactory, FixConnector connector) {
		return factory.createMessage(BUNDLE_PATH, 12, messageStoreFactory,
				connector.getName());
	}

	public static Message logFactoryClassNotFound(String logFactory,
			FixConnector connector) {
		return factory.createMessage(BUNDLE_PATH, 13, logFactory, connector
				.getName());
	}

	public static Message logFactoryConstructorNotFound(String logFactory,
			FixConnector connector) {
		return factory.createMessage(BUNDLE_PATH, 14, logFactory, connector
				.getName());
	}

	public static Message logFactoryConstructionThrewException(
			String logFactory, FixConnector connector, Exception e) {
		return factory.createMessage(BUNDLE_PATH, 15, logFactory, connector
				.getName(), e.getMessage());
	}

	public static Message logFactoryClassDoesNotInheritLogFactory(
			String logFactory, FixConnector connector) {
		return factory.createMessage(BUNDLE_PATH, 16, logFactory, connector
				.getName());
	}

}
