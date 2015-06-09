package bridgempp.bot.metawrapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.Message;

public class MetaWrapper extends Bot {

	private Class<?> metaClass;
	private Object metaInstance;
	private Method[] metaMethods;
	private Hashtable<String, Method> methods;
	private MetaClass classAnnotation;

	public MetaWrapper(Class<?> metaClass)
	{
		this.metaClass = metaClass;
	}
	
	@Override
	public void initializeBot()  {
		try {
			if(metaClass == null && properties.containsKey("metaClass"))
			{
				metaClass = Class.forName(properties.getProperty("metaClass"));
			}
			if(metaClass == null)
			{
				return;
			}
			metaInstance = metaClass.newInstance();
			classAnnotation = metaClass.getAnnotation(MetaClass.class);
			metaMethods = metaClass.getDeclaredMethods();
			methods = new Hashtable<>();
			for(Method method : metaMethods)
			{
				if(classAnnotation == null)
				{
					methods.put(getDefaultTrigger(method), method);
				}
				else
				{
					MetaMethod methodAnnotation = method.getAnnotation(MetaMethod.class);
					if(methodAnnotation == null)
					{
						return;
					}
					methods.put(getTrigger(methodAnnotation, method), method);
				}
			}
			if(metaClass.getMethod("initializeBot", Bot.class) != null)
			{
				metaClass.getMethod("initializeBot", Bot.class).invoke(metaInstance, this);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getTrigger(MetaMethod methodAnnotation, Method method) {
		//<triggerPrefix><trigger>
		return (classAnnotation.triggerPrefix() + methodAnnotation.trigger()).replaceAll("\\$CLASSNAME", metaClass.getName()).replaceAll("\\$METHODNAME", method.getName());
	}

	private String getDefaultTrigger(Method method) {
		//?<classname> <methodname> 
		return ("?" + metaClass.getSimpleName() + " " + method.getName() + " ").toLowerCase();
	}

	@Override
	public void messageReceived(Message message) {
		Enumeration<String> keys = methods.keys();
		while(keys.hasMoreElements())
		{
			String key = keys.nextElement();
			if(message.getPlainTextMessage().toLowerCase().startsWith(key.toLowerCase()))
			{
				runMethod(methods.get(key), message, message.getPlainTextMessage().substring(key.length()));
			}
		}
	}

	private void runMethod(Method method, Message message, String parameterString) {
		Parameter[] parameters = method.getParameters();
		Object[] arguments = parseParametersSpaceDelimited(parameters, parameterString);
		try {
			Object returnObject = method.invoke(metaInstance, arguments);
			if(returnObject != null)
			{
				sendMessage(new Message(message.getGroup(), returnObject.toString(), MessageFormat.PLAIN_TEXT));
			}
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private Object[] parseParametersCommandLineStyle(Parameter[] parameters) {
		Options options = new Options();
		for(Parameter parameter : parameters)
		{
			switch(parameter.getType().getName())
			{
			case "boolean":
				options.addOption(parameter.getName(), false, "Boolean: " + parameter.getName());
				break;
			case "String":
				options.addOption(parameter.getName(), true, "String: " + parameter.getName());
				break;
			case "int":
				options.addOption(parameter.getName(), true, "Integer: " + parameter.getName());
				break;
			default:
				return null;
			}
		}
		DefaultParser parser = new DefaultParser();
		throw new UnsupportedOperationException("Not Implemented");
	}

	private Object[] parseParametersSpaceDelimited(Parameter[] parameters, String message)
	{
		if(parameters.length == 1 && parameters[0].getType().getName().equals("java.lang.String"))
		{
			return new Object[] { message };
		}
		String[] splittedString = message.split("\\s");
		Object[] parameterObjects = new Object[parameters.length];
		for(int i = 0; i < parameters.length; i++)
		{
			switch(parameters[i].getType().getName())
			{
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

}
