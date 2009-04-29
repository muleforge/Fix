package org.mule.transport.fix.examples.mock;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quickfix.FieldNotFound;
import quickfix.IncorrectTagValue;
import quickfix.LogUtil;
import quickfix.Message;
import quickfix.SessionID;
import quickfix.UnsupportedMessageType;
import quickfix.field.AvgPx;
import quickfix.field.CumQty;
import quickfix.field.ExecID;
import quickfix.field.ExecTransType;
import quickfix.field.ExecType;
import quickfix.field.LastPx;
import quickfix.field.LastQty;
import quickfix.field.LastShares;
import quickfix.field.LeavesQty;
import quickfix.field.OrdStatus;
import quickfix.field.OrdType;
import quickfix.field.OrderID;
import quickfix.field.OrderQty;
import quickfix.field.Price;
import quickfix.field.Side;
import quickfix.field.Symbol;

/*
 * This class is basically a port from the Executor QuickFixJ example application.
 * This class shows how the Executor application could be hosted in Mule. One should notice
 * that this class is not transport specific anymore and thus can now receive messages over any
 * tranport.
 */
public class Executor {

	private final Logger log = LoggerFactory.getLogger(getClass());
	private final HashSet<String> validOrderTypes = new HashSet<String>();
	private MarketDataProvider marketDataProvider;
	private int m_orderID = 0;
	private int m_execID = 0;

	// for component binding of outbound
	FixDispatcherInterface dispatcher;

	// configurable properties
	private double defaultMarketPrice = 15.7d;
	private String validOrderTypesDefinition = "1,2,F";
	private boolean alwaysFillLimitOrders = false;

	public Executor() {
		// set market data provider
		marketDataProvider = new MarketDataProvider() {
			public double getAsk(String symbol) {
				return defaultMarketPrice;
			}

			public double getBid(String symbol) {
				return defaultMarketPrice;
			}
		};

		// set valid data
		List<String> orderTypes = Arrays.asList(validOrderTypesDefinition
				.trim().split("\\s*,\\s*"));
		validOrderTypes.addAll(orderTypes);

	}

	public void processMessage(quickfix.fix40.NewOrderSingle order)
			throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

		validateOrder(order);

		OrderQty orderQty = order.getOrderQty();

		Price price = getPrice(order);

		quickfix.fix40.ExecutionReport accept = new quickfix.fix40.ExecutionReport(
				genOrderID(), genExecID(),
				new ExecTransType(ExecTransType.NEW), new OrdStatus(
						OrdStatus.NEW), order.getSymbol(), order.getSide(),
				orderQty, new LastShares(0), new LastPx(0), new CumQty(0),
				new AvgPx(0));

		accept.set(order.getClOrdID());
		dispatcher.dispatch(accept);

		if (isOrderExecutable(order, price)) {
			quickfix.fix40.ExecutionReport fill = new quickfix.fix40.ExecutionReport(
					genOrderID(), genExecID(), new ExecTransType(
							ExecTransType.NEW),
					new OrdStatus(OrdStatus.FILLED), order.getSymbol(), order
							.getSide(), orderQty, new LastShares(orderQty
							.getValue()), new LastPx(price.getValue()),
					new CumQty(orderQty.getValue()),
					new AvgPx(price.getValue()));

			fill.set(order.getClOrdID());

			dispatcher.dispatch(fill);
		}

	}

	public void processMessage(quickfix.fix41.NewOrderSingle order)
			throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

		validateOrder(order);

		OrderQty orderQty = order.getOrderQty();
		Price price = getPrice(order);

		quickfix.fix41.ExecutionReport accept = new quickfix.fix41.ExecutionReport(
				genOrderID(), genExecID(),
				new ExecTransType(ExecTransType.NEW), new ExecType(
						ExecType.FILL), new OrdStatus(OrdStatus.NEW), order
						.getSymbol(), order.getSide(), orderQty,
				new LastShares(0), new LastPx(0), new LeavesQty(0), new CumQty(
						0), new AvgPx(0));

		accept.set(order.getClOrdID());
		dispatcher.dispatch(accept);

		if (isOrderExecutable(order, price)) {
			quickfix.fix41.ExecutionReport executionReport = new quickfix.fix41.ExecutionReport(
					genOrderID(), genExecID(), new ExecTransType(
							ExecTransType.NEW), new ExecType(ExecType.FILL),
					new OrdStatus(OrdStatus.FILLED), order.getSymbol(), order
							.getSide(), orderQty, new LastShares(orderQty
							.getValue()), new LastPx(price.getValue()),
					new LeavesQty(0), new CumQty(orderQty.getValue()),
					new AvgPx(price.getValue()));

			executionReport.set(order.getClOrdID());

			dispatcher.dispatch(executionReport);
		}

	}

	public void processMessage(quickfix.fix42.NewOrderSingle order)
			throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
		validateOrder(order);

		OrderQty orderQty = order.getOrderQty();
		Price price = getPrice(order);

		quickfix.fix42.ExecutionReport accept = new quickfix.fix42.ExecutionReport(
				genOrderID(), genExecID(),
				new ExecTransType(ExecTransType.NEW), new ExecType(
						ExecType.FILL), new OrdStatus(OrdStatus.NEW), order
						.getSymbol(), order.getSide(), new LeavesQty(0),
				new CumQty(0), new AvgPx(0));

		accept.set(order.getClOrdID());
		dispatcher.dispatch(accept);

		if (isOrderExecutable(order, price)) {
			quickfix.fix42.ExecutionReport executionReport = new quickfix.fix42.ExecutionReport(
					genOrderID(), genExecID(), new ExecTransType(
							ExecTransType.NEW), new ExecType(ExecType.FILL),
					new OrdStatus(OrdStatus.FILLED), order.getSymbol(), order
							.getSide(), new LeavesQty(0), new CumQty(orderQty
							.getValue()), new AvgPx(price.getValue()));

			executionReport.set(order.getClOrdID());
			executionReport.set(orderQty);
			executionReport.set(new LastShares(orderQty.getValue()));
			executionReport.set(new LastPx(price.getValue()));

			dispatcher.dispatch(executionReport);
		}

	}

	public void processMessage(quickfix.fix43.NewOrderSingle order)
			throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

		validateOrder(order);

		OrderQty orderQty = order.getOrderQty();
		Price price = getPrice(order);

		quickfix.fix43.ExecutionReport accept = new quickfix.fix43.ExecutionReport(
				genOrderID(), genExecID(), new ExecType(ExecType.FILL),
				new OrdStatus(OrdStatus.NEW), order.getSide(), new LeavesQty(
						order.getOrderQty().getValue()), new CumQty(0),
				new AvgPx(0));

		accept.set(order.getClOrdID());
		accept.set(order.getSymbol());
		dispatcher.dispatch(accept);

		if (isOrderExecutable(order, price)) {
			quickfix.fix43.ExecutionReport executionReport = new quickfix.fix43.ExecutionReport(
					genOrderID(), genExecID(), new ExecType(ExecType.FILL),
					new OrdStatus(OrdStatus.FILLED), order.getSide(),
					new LeavesQty(0), new CumQty(orderQty.getValue()),
					new AvgPx(price.getValue()));

			executionReport.set(order.getClOrdID());
			executionReport.set(order.getSymbol());
			executionReport.set(orderQty);
			executionReport.set(new LastQty(orderQty.getValue()));
			executionReport.set(new LastPx(price.getValue()));

			dispatcher.dispatch(executionReport);
		}

	}

	public void processMessage(quickfix.fix44.NewOrderSingle order)
			throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {

		validateOrder(order);

		OrderQty orderQty = order.getOrderQty();
		Price price = getPrice(order);

		quickfix.fix44.ExecutionReport accept = new quickfix.fix44.ExecutionReport(
				genOrderID(), genExecID(), new ExecType(ExecType.FILL),
				new OrdStatus(OrdStatus.NEW), order.getSide(), new LeavesQty(
						order.getOrderQty().getValue()), new CumQty(0),
				new AvgPx(0));

		accept.set(order.getClOrdID());
		accept.set(order.getSymbol());
		dispatcher.dispatch(accept);

		if (isOrderExecutable(order, price)) {
			quickfix.fix44.ExecutionReport executionReport = new quickfix.fix44.ExecutionReport(
					genOrderID(), genExecID(), new ExecType(ExecType.FILL),
					new OrdStatus(OrdStatus.FILLED), order.getSide(),
					new LeavesQty(0), new CumQty(orderQty.getValue()),
					new AvgPx(price.getValue()));

			executionReport.set(order.getClOrdID());
			executionReport.set(order.getSymbol());
			executionReport.set(orderQty);
			executionReport.set(new LastQty(orderQty.getValue()));
			executionReport.set(new LastPx(price.getValue()));

			dispatcher.dispatch(executionReport);
		}
	}

	private void validateOrder(Message order) throws IncorrectTagValue,
			FieldNotFound {
		OrdType ordType = new OrdType(order.getChar(OrdType.FIELD));
		if (!validOrderTypes.contains(Character.toString(ordType.getValue()))) {
			log.error("Order type not in ValidOrderTypes setting");
			throw new IncorrectTagValue(ordType.getField());
		}
		if (ordType.getValue() == OrdType.MARKET && marketDataProvider == null) {
			log
					.error("DefaultMarketPrice setting not specified for market order");
			throw new IncorrectTagValue(ordType.getField());
		}
	}

	private Price getPrice(Message message) throws FieldNotFound {
		Price price;
		if (message.getChar(OrdType.FIELD) == OrdType.LIMIT
				&& alwaysFillLimitOrders) {
			price = new Price(message.getDouble(Price.FIELD));
		} else {
			if (marketDataProvider == null) {
				throw new RuntimeException(
						"No market data provider specified for market order");
			}
			char side = message.getChar(Side.FIELD);
			if (side == Side.BUY) {
				price = new Price(marketDataProvider.getAsk(message
						.getString(Symbol.FIELD)));
			} else if (side == Side.SELL || side == Side.SELL_SHORT) {
				price = new Price(marketDataProvider.getBid(message
						.getString(Symbol.FIELD)));
			} else {
				throw new RuntimeException("Invalid order side: " + side);
			}
		}
		return price;
	}

	private boolean isOrderExecutable(Message order, Price price)
			throws FieldNotFound {
		if (order.getChar(OrdType.FIELD) == OrdType.LIMIT) {
			BigDecimal limitPrice = new BigDecimal(order.getString(Price.FIELD));
			char side = order.getChar(Side.FIELD);
			BigDecimal thePrice = new BigDecimal("" + price.getValue());

			return (side == Side.BUY && thePrice.compareTo(limitPrice) <= 0)
					|| ((side == Side.SELL || side == Side.SELL_SHORT) && thePrice
							.compareTo(limitPrice) >= 0);
		}
		return true;
	}

	public OrderID genOrderID() {
		return new OrderID(Integer.valueOf(++m_orderID).toString());
	}

	public ExecID genExecID() {
		return new ExecID(Integer.valueOf(++m_execID).toString());
	}

	public FixDispatcherInterface getDispatcher() {
		return dispatcher;
	}

	public void setDispatcher(FixDispatcherInterface dispatcher) {
		this.dispatcher = dispatcher;
	}

	public double getDefaultMarketPrice() {
		return defaultMarketPrice;
	}

	public void setDefaultMarketPrice(double defaultMarketPrice) {
		this.defaultMarketPrice = defaultMarketPrice;
	}

	public String getValidOrderTypesDefinition() {
		return validOrderTypesDefinition;
	}

	public void setValidOrderTypesDefinition(String validOrderTypesDefinition) {
		this.validOrderTypesDefinition = validOrderTypesDefinition;
	}

	public boolean isAlwaysFillLimitOrders() {
		return alwaysFillLimitOrders;
	}

	public void setAlwaysFillLimitOrders(boolean alwaysFillLimitOrders) {
		this.alwaysFillLimitOrders = alwaysFillLimitOrders;
	}

}
