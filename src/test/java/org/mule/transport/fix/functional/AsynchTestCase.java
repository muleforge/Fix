package org.mule.transport.fix.functional;

import org.junit.Test;
import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;

import quickfix.field.ClOrdID;
import quickfix.field.OrigClOrdID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.Text;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class AsynchTestCase extends org.mule.tck.junit4.FunctionalTestCase {

    @Test
	public void testExecutorConfig() throws Exception {
		MuleClient client = new MuleClient(muleContext);

		quickfix.fix41.OrderCancelRequest message = new quickfix.fix41.OrderCancelRequest(
				new OrigClOrdID("123"), new ClOrdID("321"), new Symbol("LNUX"),
				new Side(Side.BUY));

		message.set(new Text("Cancel My Order!"));

		client.dispatch("banzai", message, null);

		MuleMessage result = client.request("vm://toTest", 10000);
		assertNotNull(result);
		assertNotNull(result.getPayload());
		assertTrue(result.getPayload() instanceof quickfix.Message);
		assertTrue(result.getPayload() instanceof quickfix.fix41.Message);
		assertTrue(result.getPayload() instanceof quickfix.fix41.OrderCancelRequest);
		quickfix.fix41.OrderCancelRequest resultOrderCancel = (quickfix.fix41.OrderCancelRequest) result
				.getPayload();
		assertEquals(message.getOrigClOrdID(), resultOrderCancel
				.getOrigClOrdID());
		assertEquals(message.getClOrdID(), resultOrderCancel.getClOrdID());
		assertEquals(message.getSymbol(), resultOrderCancel.getSymbol());
		assertEquals(message.getSide(), resultOrderCancel.getSide());
	}

	@Override
	protected String getConfigResources() {
		return "asynch-mule-config.xml";
	}

}
