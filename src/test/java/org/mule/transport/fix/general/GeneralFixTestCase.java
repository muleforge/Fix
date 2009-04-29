package org.mule.transport.fix.general;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;

import junit.framework.TestCase;

import quickfix.Acceptor;
import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.DoNotSend;
import quickfix.FieldConvertError;
import quickfix.FieldNotFound;
import quickfix.FileStoreFactory;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.Initiator;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.RejectLogon;
import quickfix.Session;
import quickfix.SessionFactory;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;
import quickfix.SocketInitiator;
import quickfix.UnsupportedMessageType;

public class GeneralFixTestCase extends TestCase implements
Application{
	public void testInitiatorAcceptorInSameConfig() throws FileNotFoundException, ConfigError, FieldConvertError
	{
		//String curDir = System.getProperty("user.dir");  
		//System.out.println(curDir);

		SessionSettings settings = new SessionSettings(new FileInputStream("./target/test-classes/org/mule/transport/fix/general/initiator-acceptor-in-same-config.cfg"));
		testCheckingSessionTypes(settings,1,1);
		MessageStoreFactory storeFactory = new FileStoreFactory(settings);
		//LogFactory logFactory = new FileLogFactory(settings);
		MessageFactory messageFactory = new DefaultMessageFactory();
		
		Initiator initiator=new SocketInitiator(this, storeFactory, settings,
				messageFactory);
		Acceptor acceptor=new SocketAcceptor(this, storeFactory, settings,
				messageFactory);
		
		ArrayList<SessionID> initiatorSessions= initiator.getSessions();
		ArrayList<SessionID> acceptorSessions= acceptor.getSessions();
		
		assertEquals(1,initiatorSessions.size());
		assertEquals(1,acceptorSessions.size());
	}
	
	public void testOnly1InitatorSession() throws FileNotFoundException, ConfigError, FieldConvertError
	{
		ConfigError configError=null;

		SessionSettings settings = new SessionSettings(new FileInputStream("./target/test-classes/org/mule/transport/fix/general/banzai.cfg"));
		testCheckingSessionTypes(settings,1,0);
		MessageStoreFactory storeFactory = new FileStoreFactory(settings);
		//LogFactory logFactory = new FileLogFactory(settings);
		MessageFactory messageFactory = new DefaultMessageFactory();
		
		Initiator initiator=new SocketInitiator(this, storeFactory, settings,
				messageFactory);
		try
		{
		Acceptor acceptor=new SocketAcceptor(this, storeFactory, settings,
				messageFactory);
		}catch(ConfigError e)
		{
			configError=e;
		}
		
		ArrayList<SessionID> initiatorSessions= initiator.getSessions();
		assertEquals(1,initiatorSessions.size());
		assertNotNull(configError);
		assertEquals("No acceptor sessions found in settings.",configError.getMessage());		
		
	}
	
	public void testOnly1AcceptorSession() throws FileNotFoundException, ConfigError, FieldConvertError
	{
		ConfigError configError=null;

		SessionSettings settings = new SessionSettings(new FileInputStream("./target/test-classes/org/mule/transport/fix/general/executor.cfg"));
		testCheckingSessionTypes(settings,0,1);
		MessageStoreFactory storeFactory = new FileStoreFactory(settings);
		//LogFactory logFactory = new FileLogFactory(settings);
		MessageFactory messageFactory = new DefaultMessageFactory();
		
		Acceptor acceptor=new SocketAcceptor(this, storeFactory, settings,
				messageFactory);
		
		try
		{
			Initiator initiator=new SocketInitiator(this, storeFactory, settings,
					messageFactory);
		}catch(ConfigError e)
		{
			configError=e;
		}
		
		ArrayList<SessionID> acceptorSessions= acceptor.getSessions();
		assertEquals(1,acceptorSessions.size());
		assertNotNull(configError);
		assertEquals("no initiators in settings",configError.getMessage());
		
	}
	
	private void testCheckingSessionTypes(SessionSettings settings,int expectedInitiators,int expectedAcceptors) throws ConfigError, FieldConvertError, FileNotFoundException
	{
		int initiators=0;
		int acceptors=0;
		for (Iterator<SessionID> i = settings.sectionIterator(); i.hasNext();) {
            SessionID sessionID = i.next();
            if (isInitiatorSession(sessionID,settings)) {
                initiators++;
            }else if(isAcceptorSession(sessionID,settings))
            {
            	acceptors++;
            }
        }
		assertEquals(expectedInitiators,initiators);
		assertEquals(expectedAcceptors,acceptors);
	}
	
	private boolean isInitiatorSession(Object sectionKey,SessionSettings settings) throws ConfigError, FieldConvertError{
        return !settings.isSetting((SessionID) sectionKey, SessionFactory.SETTING_CONNECTION_TYPE)
                || settings.getString((SessionID) sectionKey,
                        SessionFactory.SETTING_CONNECTION_TYPE).equals("initiator");
    }
	
	private boolean isAcceptorSession(Object sectionKey,SessionSettings settings) throws ConfigError, FieldConvertError{
        return !settings.isSetting((SessionID) sectionKey, SessionFactory.SETTING_CONNECTION_TYPE)
                || settings.getString((SessionID) sectionKey,
                        SessionFactory.SETTING_CONNECTION_TYPE).equals("acceptor");
    }

	public void fromAdmin(Message arg0, SessionID arg1) throws FieldNotFound,
			IncorrectDataFormat, IncorrectTagValue, RejectLogon {
		// TODO Auto-generated method stub
		
	}

	public void fromApp(Message arg0, SessionID arg1) throws FieldNotFound,
			IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
		// TODO Auto-generated method stub
		
	}

	public void onCreate(SessionID arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onLogon(SessionID arg0) {
		// TODO Auto-generated method stub
		
	}

	public void onLogout(SessionID arg0) {
		// TODO Auto-generated method stub
		
	}

	public void toAdmin(Message arg0, SessionID arg1) {
		// TODO Auto-generated method stub
		
	}

	public void toApp(Message arg0, SessionID arg1) throws DoNotSend {
		// TODO Auto-generated method stub
		
	}
}
