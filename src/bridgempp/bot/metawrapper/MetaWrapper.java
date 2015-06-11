package bridgempp.bot.metawrapper;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedList;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.Message;

public class MetaWrapper extends Bot {

	private Class<?> metaClass;
	private Object metaInstance;
	private Method[] metaMethods;
	private Hashtable<String, Method> methods;
	private MetaClass classAnnotation;

	public MetaWrapper(Class<?> metaClass) {
		this.metaClass = metaClass;
	}

	public MetaWrapper() {
	}

	@Override
	public void initializeBot() {
		try {
			if (metaClass == null) {
				metaClass = Class.forName(properties.getProperty("metaclass"));
			}
			if (metaClass == null) {
				throw new Exception("No Meta Class loaded");
			}
			metaInstance = metaClass.newInstance();
			classAnnotation = metaClass.getAnnotation(MetaClass.class);
			metaMethods = metaClass.getDeclaredMethods();
			methods = new Hashtable<>();
			for (Method method : metaMethods) {
				if (classAnnotation == null) {
					methods.put(getDefaultTrigger(method), method);
				} else {
					MetaMethod methodAnnotation = method
							.getAnnotation(MetaMethod.class);
					if (methodAnnotation == null) {
						continue;
					}
					methods.put(getTrigger(methodAnnotation, method), method);
				}
			}
			if (metaClass.getMethod("initializeBot", Bot.class) != null) {
				metaClass.getMethod("initializeBot", Bot.class).invoke(
						metaInstance, this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getTrigger(MetaMethod methodAnnotation, Method method) {
		// <triggerPrefix><trigger>
		return (classAnnotation.triggerPrefix() + methodAnnotation.trigger())
				.replaceAll("\\$CLASSNAME", metaClass.getName()).replaceAll(
						"\\$METHODNAME", method.getName());
	}

	private String getDefaultTrigger(Method method) {
		// ?<classname> <methodname>
		return ("?" + metaClass.getSimpleName() + " " + method.getName() + " ")
				.toLowerCase();
	}

	@Override
	public void messageReceived(Message message) {
		if (methods == null) {
			return;
		}
		Enumeration<String> keys = methods.keys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			if (message.getPlainTextMessage().toLowerCase()
					.startsWith(key.toLowerCase())) {
				runMethod(methods.get(key), message, message
						.getPlainTextMessage().substring(key.length()));
			}
		}
	}

	private void runMethod(Method method, Message message,
			String parameterString) {
		Parameter[] parameters = method.getParameters();
		Object[] arguments = parseParametersCommandLineStyle(parameters,
				parameterString, message);
		if (arguments == null) {
			sendMessage(new Message(message.getGroup(), getHelpTopic(),
					MessageFormat.PLAIN_TEXT));
			return;
		}
		try {
			Object returnObject = method.invoke(metaInstance, arguments);
			if (returnObject != null) {
				sendMessage(new Message(message.getGroup(),
						returnObject.toString(), MessageFormat.PLAIN_TEXT));
			}
		} catch (Exception e) {
			sendMessage(new Message(message.getGroup(),
					"A Meta Error has ocurred: " + e.toString(),
					MessageFormat.PLAIN_TEXT));
			e.printStackTrace();
		}
	}

	private String getHelpTopic() {
		String helpTopic = "Man Page for " + metaClass.getSimpleName() + " at "
				+ metaClass.getName() + "\n";
		if (metaClass.getAnnotation(MetaClass.class) != null) {
			helpTopic += metaClass.getAnnotation(MetaClass.class).helpTopic()
					+ "\n";
		}
		Enumeration<Method> elements = methods.elements();
		while (elements.hasMoreElements()) {
			Method method = elements.nextElement();

			helpTopic += "Method: "
					+ method.getName()
					+ " Usage: "
					+ getTrigger(method.getAnnotation(MetaMethod.class), method);
			for (Parameter parameter : method.getParameters()) {
				helpTopic += "<" + parameter.getType() + " "
						+ parameter.getName() + "> ";
			}
			helpTopic += "\n";
			if (method.getAnnotation(MetaMethod.class) != null
					&& !method.getAnnotation(MetaMethod.class).equals("")) {
				helpTopic += method.getAnnotation(MetaMethod.class).helpTopic()
						+ "\n";
			}
			for (Parameter parameter : method.getParameters()) {
				if (parameter.getAnnotation(MetaParameter.class) != null) {
					helpTopic += "<" + parameter.getType() + " "
							+ parameter.getName() + ">: " + parameter.getAnnotation(MetaParameter.class)
							.helpTopic() + "\n";
				}
			}
		}
		return helpTopic;
	}

	private Object[] parseParametersCommandLineStyle(Parameter[] parameters,
			String message, Message bridgemppMessage) {
		if(parameters.length == 0)
		{
			return new Object[0];
		}
		if(parameters.length == 1 && parameters[0].getType().equals(Message.class))
		{
			return new Object[] { bridgemppMessage };
		}
		String[] splittedString = splitCommandLine(message);
		Object[] parameterObjects = new Object[parameters.length];
		if (splittedString.length != parameters.length) {
			return null;
		}
		for (int i = 0; i < splittedString.length; i++) {
			switch (parameters[i].getType().getName()) {
			case "java.lang.String":
				parameterObjects[i] = splittedString[i];
				break;
			case "boolean":
				parameterObjects[i] = Boolean.parseBoolean(splittedString[i]);
				break;
			case "int":
				parameterObjects[i] = Integer.parseInt(splittedString[i]);
				break;
			case "double":
				parameterObjects[i] = Double.parseDouble(splittedString[i]);
				break;
			default:
				parameterObjects[i] = splittedString[i];
				break;
			}
		}
		return parameterObjects;
	}

	public String[] splitCommandLine(String message) {
		LinkedList<String> list = new LinkedList<>();
		char[] characters = message.toCharArray();
		char delimiter = 0;
		int startSequence = 0;
		for (int i = 0; i < characters.length; i++) {
			if (delimiter == 0) {
				if (Character.isWhitespace(characters[i])) {
					continue;
				}
				if (characters[i] == '\'' || characters[i] == '\"') {
					startSequence = i;
					delimiter = characters[i];
				} else {
					startSequence = i - 1;
					delimiter = ' ';
				}
			} else {
				if (characters[i] == delimiter && characters[i - 1] != '\\') {
					if (startSequence + 1 > i - 1) {
						list.add("");
					} else {
						list.add(message.substring(startSequence + 1, i)
								.replace("\\" + delimiter, "" + delimiter));
					}
					delimiter = 0;
				}
			}
		}
		return list.toArray(new String[list.size()]);
	}

}
