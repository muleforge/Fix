package org.mule.transport.fix.functional;

import org.mule.api.MuleMessage;
import org.mule.module.client.MuleClient;
import org.mule.tck.FunctionalTestCase;

import quickfix.field.ClOrdID;
import quickfix.field.OrigClOrdID;
import quickfix.field.Side;
import quickfix.field.Symbol;
import quickfix.field.Text;

public class AsynchVersionTransformationTestCase extends FunctionalTestCase {

	public void testExecutorConfig() throws Exception {
		MuleClient client = new MuleClient();

		quickfix.fix41.OrderCancelRequest message = new quickfix.fix41.OrderCancelRequest(
				new OrigClOrdID("123"), new ClOrdID("321"), new Symbol("LNUX"),
				new Side(Side.BUY));

		message.set(new Text("Cancel My Order!"));

		client.dispatch("banzai", message, null);

		MuleMessage result = client.request("vm://toTest", 10000);
		assertNotNull(result);
		assertNotNull(result.getPayload());
		assertTrue(result.getPayload() instanceof quickfix.Message);
		assertTrue(result.getPayload() instanceof quickfix.fix40.Message);
		assertTrue(result.getPayload() instanceof quickfix.fix40.OrderCancelRequest);
		quickfix.fix40.OrderCancelRequest resultOrderCancel = (quickfix.fix40.OrderCancelRequest) result
				.getPayload();
		assertEquals(message.getOrigClOrdID(), resultOrderCancel
				.getOrigClOrdID());
		assertEquals(message.getClOrdID(), resultOrderCancel.getClOrdID());
		assertEquals(message.getSymbol(), resultOrderCancel.getSymbol());
		assertEquals(message.getSide(), resultOrderCancel.getSide());
	}

	@Override
	protected String getConfigResources() {
		return "asynch-version-transformation-mule-config.xml";
	}

}
