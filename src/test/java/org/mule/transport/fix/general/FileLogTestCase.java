package org.mule.transport.fix.general;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import quickfix.Acceptor;
import quickfix.Application;
import quickfix.ConfigError;
import quickfix.DefaultMessageFactory;
import quickfix.DoNotSend;
import quickfix.FieldNotFound;
import quickfix.FileLogFactory;
import quickfix.FileStoreFactory;
import quickfix.IncorrectDataFormat;
import quickfix.IncorrectTagValue;
import quickfix.LogFactory;
import quickfix.MemoryStoreFactory;
import quickfix.Message;
import quickfix.MessageFactory;
import quickfix.MessageStoreFactory;
import quickfix.RejectLogon;
import quickfix.SessionID;
import quickfix.SessionSettings;
import quickfix.SocketAcceptor;
import quickfix.UnsupportedMessageType;
import junit.framework.TestCase;

public class FileLogTestCase extends TestCase{
	public void testWithFileStore() throws ConfigError, IOException
	{
		SessionSettings settings = new SessionSettings(new FileInputStream("src/test/resources/org/mule/transport/fix/general/file-store.cfg"));
	    MessageStoreFactory storeFactory = new FileStoreFactory(settings);
	    //LogFactory logFactory = new FileLogFactory(settings);
	    MessageFactory messageFactory = new DefaultMessageFactory();
	    Acceptor acceptor = new SocketAcceptor
	      (new MyApp(), storeFactory, settings, null, messageFactory);
	}
	
	public void testWithoutFileStore() throws ConfigError, IOException
	{
		SessionSettings settings = new SessionSettings(new FileInputStream("src/test/resources/org/mule/transport/fix/general/no-file-store.cfg"));
	    //MessageStoreFactory storeFactory = new FileStoreFactory(settings);
		MessageStoreFactory storeFactory = new MemoryStoreFactory();
	    //LogFactory logFactory = new FileLogFactory(settings);
	    MessageFactory messageFactory = new DefaultMessageFactory();
	    Acceptor acceptor = new SocketAcceptor
	      (new MyApp(), storeFactory, settings, null, messageFactory);
	}
	
	public void testWithAndWithoutFileStore() throws ConfigError, IOException
	{
		SessionSettings settings = new SessionSettings(new FileInputStream("src/test/resources/org/mule/transport/fix/general/2-sessions-withwithout-file-store.cfg"));
	    MessageStoreFactory storeFactory = new FileStoreFactory(settings);
	    //LogFactory logFactory = new FileLogFactory(settings);
	    MessageFactory messageFactory = new DefaultMessageFactory();
	    //Acceptor acceptor = new SocketAcceptor
	    //  (new MyApp(), storeFactory, settings, null, messageFactory);
	}
	

	public class MyApp implements Application{

		public void fromAdmin(Message arg0, SessionID arg1)
				throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue,
				RejectLogon {
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
}
