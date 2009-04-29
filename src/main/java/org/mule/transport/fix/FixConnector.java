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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.mule.api.MuleException;
import org.mule.api.endpoint.InboundEndpoint;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.service.Service;
import org.mule.transport.AbstractConnector;
import org.mule.transport.ConnectException;
import org.mule.transport.fix.i18n.FixMessages;

import quickfix.Acceptor;
import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.DoNotSend;
import quickfix.FieldConvertError;
import quickfix.FieldNotFound;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Initiator;
import quickfix.LogFactory;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.RejectLogon;
import quickfix.RuntimeError;
import quickfix.Session;
import quickfix.SessionFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;
import quickfix.SocketInitiator;
import quickfix.UnsupportedMessageType;

/**
 * <code>FixConnector</code> This connector is responsible of creating the fix
 * acceptors/initiators and receiving the messages from the fix libraries and
 * passing them on to the correct receivers
 */
public class FixConnector extends AbstractConnector implements Application {
	/* This constant defines the main transport protocol identifier */
	public static final String FIX = "fix";

	private String config = "";
	private Acceptor acceptor = null;
	private Initiator initiator = null;
	private Map<String, Session> sessionMap = new HashMap<String, Session>();
	private String messageStoreFactory = "quickfix.MemoryStoreFactory";
	private String logFactory = "";

	public void doInitialise() throws InitialisationException {
		if (config != "") {

			int initiatorCount = 0;
			int acceptorCount = 0;

			SessionSettings settings;
			try {
				settings = new SessionSettings(new FileInputStream(config));
				MessageStoreFactory storeFactory = getMessageStoreFactory(settings);
				LogFactory logFactory = getFileLogFactory(settings);
				MessageFactory messageFactory = new DefaultMessageFactory();

				// Check if initiators/acceptors are present
				for (Iterator<SessionID> i = settings.sectionIterator(); i
						.hasNext();) {
					SessionID sessionID = i.next();
					if (isInitiatorSession(sessionID, settings)) {
						initiatorCount++;
					} else if (isAcceptorSession(sessionID, settings)) {
						acceptorCount++;
					}
				}

				// TODO check if threaded is better. Probably it is not since
				// once
				// the message is received it is sent off to another thread to
				// be
				// processed.
				if (initiatorCount > 0) {
					initiator = new SocketInitiator(this, storeFactory,
							settings, logFactory, messageFactory);
				}
				if (acceptorCount > 0) {
					acceptor = new SocketAcceptor(this, storeFactory, settings,
							logFactory, messageFactory);
				}
			} catch (FileNotFoundException e) {
				throw new InitialisationException(FixMessages
						.exceptionWhileConnectingFileNotFound(e), e, this);
			} catch (ConfigError e) {
				throw new InitialisationException(FixMessages
						.exceptionWhileConnectingConfigError(e), e, this);
			} catch (FieldConvertError e) {
				throw new InitialisationException(FixMessages
						.exceptionWhileConnectingConfigError(e), e, this);
			}
		} else {
			logger.warn("No Config File set.");
		}
	}

	private MessageStoreFactory getMessageStoreFactory(SessionSettings settings)
			throws InitialisationException {

		Class<MessageStoreFactory> factoryClass;
		try {
			factoryClass = (Class<MessageStoreFactory>) Class
					.forName(messageStoreFactory);
		} catch (ClassNotFoundException e) {
			throw new InitialisationException(
					FixMessages.messageStoreFactoryClassNotFound(
							messageStoreFactory, this), e, this);
		} catch (ClassCastException e) {
			throw new InitialisationException(FixMessages
					.messageStoreFactoryClassDoesNotInheritMessageStoreFactory(
							messageStoreFactory, this), e, this);
		}

		// check if SessionSettings constructor is present
		Constructor[] constructors = factoryClass.getDeclaredConstructors();
		boolean settingsConstructor = false;
		for (Constructor c : constructors) {
			if (c.getParameterTypes().length == 1) {
				if (c.getParameterTypes()[0].equals(SessionSettings.class)) {
					settingsConstructor = true;
				}
			}
		}
		try {
			if (settingsConstructor) {

				return (MessageStoreFactory) factoryClass.getConstructor(
						SessionSettings.class).newInstance(settings);
			} else {
				return (MessageStoreFactory) factoryClass.getConstructor()
						.newInstance();
			}
		} catch (NoSuchMethodException e) {
			throw new InitialisationException(FixMessages
					.messageStoreFactoryConstructorNotFound(
							messageStoreFactory, this), e, this);
		} catch (RuntimeException e) {
			throw new InitialisationException(FixMessages
					.messageStoreFactoryConstructionThrewException(
							messageStoreFactory, this, e), e, this);
		} catch (InstantiationException e) {
			throw new InitialisationException(FixMessages
					.messageStoreFactoryConstructionThrewException(
							messageStoreFactory, this, e), e, this);
		} catch (IllegalAccessException e) {
			throw new InitialisationException(FixMessages
					.messageStoreFactoryConstructionThrewException(
							messageStoreFactory, this, e), e, this);
		} catch (InvocationTargetException e) {
			throw new InitialisationException(FixMessages
					.messageStoreFactoryConstructionThrewException(
							messageStoreFactory, this, e), e, this);
		}

	}

	private LogFactory getFileLogFactory(SessionSettings settings)
			throws InitialisationException {
		// If no log factory is set, one can return null
		if (logFactory.length() == 0) {
			return null;
		}

		Class<LogFactory> factoryClass;
		try {
			factoryClass = (Class<LogFactory>) Class.forName(logFactory);
		} catch (ClassNotFoundException e) {
			throw new InitialisationException(FixMessages
					.logFactoryClassNotFound(logFactory, this), e, this);
		} catch (ClassCastException e) {
			throw new InitialisationException(FixMessages
					.logFactoryClassDoesNotInheritLogFactory(logFactory, this),
					e, this);
		}

		// check if SessionSettings constructor is present
		Constructor[] constructors = factoryClass.getDeclaredConstructors();
		boolean settingsConstructor = false;
		for (Constructor c : constructors) {
			if (c.getParameterTypes().length == 1) {
				if (c.getParameterTypes()[0].equals(SessionSettings.class)) {
					settingsConstructor = true;
				}
			}
		}
		try {
			if (settingsConstructor) {

				return (LogFactory) factoryClass.getConstructor(
						SessionSettings.class).newInstance(settings);
			} else {
				return (LogFactory) factoryClass.getConstructor().newInstance();
			}
		} catch (NoSuchMethodException e) {
			throw new InitialisationException(FixMessages
					.logFactoryConstructorNotFound(logFactory, this), e, this);
		} catch (RuntimeException e) {
			throw new InitialisationException(FixMessages
					.logFactoryConstructionThrewException(logFactory, this, e),
					e, this);
		} catch (InstantiationException e) {
			throw new InitialisationException(FixMessages
					.logFactoryConstructionThrewException(logFactory, this, e),
					e, this);
		} catch (IllegalAccessException e) {
			throw new InitialisationException(FixMessages
					.logFactoryConstructionThrewException(logFactory, this, e),
					e, this);
		} catch (InvocationTargetException e) {
			throw new InitialisationException(FixMessages
					.logFactoryConstructionThrewException(logFactory, this, e),
					e, this);
		}

	}

	public void doConnect() throws Exception {
		// Acceptors/Initiators immediately go into the started mode, and thus
		// code to connect and start is in the doStart() method
	}

	public void doDisconnect() throws Exception {
		// Similar to doConnect, code is in doStop()
	}

	public void doStart() throws MuleException {
		try {
			if (initiator != null) {
				initiator.start();
			}
			if (acceptor != null) {
				acceptor.start();
			}
		} catch (RuntimeError e) {
			throw new ConnectException(e, this);
		} catch (ConfigError e) {
			throw new ConnectException(e, this);
		}
	}

	public void doStop() throws MuleException {
		try {
			if (initiator != null) {
				initiator.stop();
			}
			if (acceptor != null) {
				acceptor.stop();
			}
		} catch (RuntimeError e) {
			throw new ConnectException(null, e, this);
		}
	}

	public void doDispose() {
		// No need to dispose anything as far as i know...
	}

	public String getProtocol() {
		return FIX;
	}

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	private boolean isInitiatorSession(Object sectionKey,
			SessionSettings settings) throws ConfigError, FieldConvertError {
		return !settings.isSetting((SessionID) sectionKey,
				SessionFactory.SETTING_CONNECTION_TYPE)
				|| settings.getString((SessionID) sectionKey,
						SessionFactory.SETTING_CONNECTION_TYPE).equals(
						"initiator");
	}

	private boolean isAcceptorSession(Object sectionKey,
			SessionSettings settings) throws ConfigError, FieldConvertError {
		return !settings.isSetting((SessionID) sectionKey,
				SessionFactory.SETTING_CONNECTION_TYPE)
				|| settings.getString((SessionID) sectionKey,
						SessionFactory.SETTING_CONNECTION_TYPE).equals(
						"acceptor");
	}

	protected Object getReceiverKey(Service service, InboundEndpoint endpoint) {
		return endpoint.getEndpointURI().getAuthority().replace("-", "->");
	}

	public void fromAdmin(Message arg0, SessionID arg1) throws FieldNotFound,
			IncorrectDataFormat, IncorrectTagValue, RejectLogon {
		// at this point, admin messages are left to pass through
		// TODO possibly create a filtering capability at this point
	}

	public void fromApp(Message message, SessionID sessionID)
			throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
			UnsupportedMessageType {
		logger.debug(FixMessages.messageReceivedInFromApp(message, sessionID,
				this));
		// receiver for appropriate Session is obtained from the receivers map
		// and the message is passed by calling the fromApp method. The fromApp
		// method was used instead of something like "onMessage" in order to
		// possibly have different behaviours depending on messages that have
		// been received since we might want to also capture admin messages and
		// passed through the ESB
		FixMessageReceiver receiver = (FixMessageReceiver) receivers
				.get(sessionID.toString());
		if (receiver == null) {
			logger.warn(FixMessages.warningNoReceiverListeningOnSession(
					message, sessionID, this));
		} else {
			receiver.fromApp(message, sessionID);
		}
	}

	public void onCreate(SessionID sessionID) {
		logger.debug(FixMessages.sessionCreated(sessionID, this));
		// once session is created, it is added to the sessionMap so that we
		// have a collection of SessionObject-SessionID mapping at the connector
		// level rather than at the static Session class level
		sessionMap.put(sessionID.toString(), Session.lookupSession(sessionID));
	}

	public Session lookupSession(SessionID sessionID) {
		return sessionMap.get(sessionID.toString());
	}

	public void onLogon(SessionID sessionID) {
		logger.debug(FixMessages.sessionLoggedIn(sessionID, this));
	}

	public void onLogout(SessionID sessionID) {
		logger.debug(FixMessages.sessionLoggedOut(sessionID, this));
	}

	public void toAdmin(Message arg0, SessionID arg1) {
		// at this point, administration messages are not being monitored
		// TODO possibly have a filter here
	}

	public void toApp(Message message, SessionID sessionID) throws DoNotSend {
		logger.debug(FixMessages
				.messageBeingSentToApp(message, sessionID, this));
		// TODO possibly have a filter here
	}

	public String getMessageStoreFactory() {
		return messageStoreFactory;
	}

	public void setMessageStoreFactory(String messageStoreFactory) {
		this.messageStoreFactory = messageStoreFactory;
	}

	public String getLogFactory() {
		return logFactory;
	}

	public void setLogFactory(String logFactory) {
		this.logFactory = logFactory;
	}

}
