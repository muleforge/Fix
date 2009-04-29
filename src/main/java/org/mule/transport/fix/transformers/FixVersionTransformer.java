package org.mule.transport.fix.transformers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.mule.api.transformer.TransformerException;
import org.mule.transformer.AbstractTransformer;

import quickfix.Message;

/**
 * <code>FixVersionTransformer</code> will transform between two quickfix
 * message versions. The transformation is quite naive and is simply using
 * reflection to map when property names are identical. If property names have
 * been changed or new properties are present, these will not be set
 * accordingly.
 */
public class FixVersionTransformer extends AbstractTransformer {

	public FixVersionTransformer() {
		registerSourceType(quickfix.Message.class);
		setReturnClass(quickfix.Message.class);
		setName("Fix Version Transformer");
	}

	private String fromVersion;
	private String toVersion;
	private String fromPackageName;
	private String toPackageName;

	@Override
	protected Object doTransform(Object from, String encoding)
			throws TransformerException {

		// check that the correct version has been received
		if (!from.getClass().getCanonicalName().startsWith(fromPackageName)) {
			throw new TransformerException(this, new Throwable(
					"Wrong Class Expected class from Package :"
							+ fromPackageName + " but was "
							+ from.getClass().getCanonicalName()));
		}

		Message to = null;
		// create the result Message
		try {
			to = (Message) Class.forName(
					toPackageName + "." + from.getClass().getSimpleName())
					.newInstance();
		} catch (InstantiationException e) {
			throw new TransformerException(this, e);
		} catch (IllegalAccessException e) {
			throw new TransformerException(this, e);
		} catch (ClassNotFoundException e) {
			throw new TransformerException(this, e);
		}

		logger.debug("Transforming from " + from.getClass().getCanonicalName()
				+ " to " + to.getClass().getCanonicalName());

		Method[] fromMethods = from.getClass().getMethods();
		Method fromIsSetMethod;
		String fromMethodName;
		String actionName;
		Method toMethod;
		for (Method fromMethod : fromMethods) {
			fromMethodName = fromMethod.getName();
			if (fromMethodName.startsWith("get")) {
				actionName = fromMethodName.substring(3);
				if (actionName.length() != 0) {
					toMethod = null;
					fromIsSetMethod = null;
					try {
						fromIsSetMethod = from.getClass().getMethod(
								"isSet" + actionName);
						toMethod = to.getClass().getMethod("set",
								fromMethod.getReturnType());
					} catch (SecurityException e) {
						// Simply Skip the Method
						/*
						 * logger.debug("No Method named " + "set" + actionName
						 * + " that takes a " + fromMethod.getReturnType() +
						 * " as a Parameter Found. Skipping");
						 */
					} catch (NoSuchMethodException e) {
						// Simply Skip the Method
						/*
						 * logger.debug("No Method named " + "set" + actionName
						 * + " that takes a " + fromMethod.getReturnType() +
						 * " as a Parameter Found. Skipping");
						 */
					}
					if (toMethod != null && fromIsSetMethod != null) {
						try {
							if ((Boolean) fromIsSetMethod.invoke(from)) {
								logger.debug("Setting Value:" + actionName);
								toMethod.invoke(to, fromMethod.invoke(from));
							}
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}

		return to;
	}

	public String getFromVersion() {
		return fromVersion;
	}

	public void setFromVersion(String fromVersion) {
		this.fromVersion = fromVersion;
		fromPackageName = "quickfix.fix" + fromVersion.replace(".", "");
	}

	public String getToVersion() {
		return toVersion;
	}

	public void setToVersion(String toVersion) {
		this.toVersion = toVersion;
		toPackageName = "quickfix.fix" + toVersion.replace(".", "");
	}

	public String getFromPackageName() {
		return fromPackageName;
	}

	public String getToPackageName() {
		return toPackageName;
	}

}
