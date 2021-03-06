package bridgempp.bot.metawrapper;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.Message;
import bridgempp.util.Log;
import bridgempp.util.Util;

public class MetaWrapper extends Bot
{

	private Class<?> metaClass;
	private Object metaInstance;
	private Method[] metaMethods;
	private Hashtable<String, Method> methods;
	private MetaClass classAnnotation;

	public MetaWrapper(Class<?> metaClass)
	{
		this.metaClass = metaClass;
	}

	public MetaWrapper()
	{
	}

	@Override
	public void initializeBot()
	{
		try
		{
			if (metaClass == null)
			{
				metaClass = Class.forName(properties.getProperty("metaclass"));
			}
			if (metaClass == null)
			{
				throw new Exception("No Meta Class loaded");
			}
			metaInstance = metaClass.newInstance();
			classAnnotation = metaClass.getAnnotation(MetaClass.class);
			metaMethods = metaClass.getDeclaredMethods();
			methods = new Hashtable<>();
			methods.put("?man list", getClass().getMethod("getHelpIndex"));
			methods.put(getManTrigger(), getClass().getMethod("getHelpTopicForClass"));
			for (Method method : metaMethods)
			{
				if (classAnnotation == null)
				{
					methods.put(getDefaultTrigger(method), method);
				} else
				{
					MetaMethod methodAnnotation = method.getAnnotation(MetaMethod.class);
					if (methodAnnotation == null)
					{
						continue;
					}
					methods.put(getTrigger(methodAnnotation, method), method);
				}
			}
			try
			{
				metaClass.getMethod("initializeBot", Bot.class).invoke(metaInstance, this);
			} catch (NoSuchMethodException e)
			{
				Log.log(Level.INFO, "Meta Wrapper: Bot does not have an initialize Method");
			}
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, getName() + ": Failed to initialize Bot", e);
		}
	}

	@Override
	public void deinitializeBot()
	{
		try
		{
			Method method = metaClass.getMethod("deinitializeBot", Bot.class);
			if (method == null)
			{
				return;
			}
			method.invoke(metaInstance, this);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, getName() + ": Failed to deinitialize Bot", e);
		}
	}

	private String getTrigger(MetaMethod methodAnnotation, Method method)
	{
		// <triggerPrefix><trigger>
		return (classAnnotation.triggerPrefix() + methodAnnotation.trigger()).replaceAll("\\$CLASSNAME", metaClass.getName()).replaceAll("\\$METHODNAME", method.getName());
	}

	private String getDefaultTrigger(Method method)
	{
		// ?<classname> <methodname>
		return ("?" + metaClass.getSimpleName() + " " + method.getName() + " ").toLowerCase();
	}

	@Override
	public void messageReceived(Message message)
	{
		if (methods == null)
		{
			return;
		}
		Enumeration<String> keys = methods.keys();
		while (keys.hasMoreElements())
		{
			String key = keys.nextElement();
			if (message.getPlainTextMessage().toLowerCase().startsWith(key.toLowerCase()))
			{
				runMethod(methods.get(key), message, message.getPlainTextMessage().substring(key.length()));
			}
		}
	}

	private void runMethod(Method method, Message message, String parameterString)
	{
		Parameter[] parameters = method.getParameters();
		Object[] arguments = Util.parseParametersCommandLineStyle(parameters, parameterString, message);
		if (arguments == null)
		{
			sendMessage(new Message(message.getGroup(), "Syntax Error: Type " + getManTrigger() + " to print a complete help topic\n" + getHelpTopicForMethod(method), MessageFormat.PLAIN_TEXT));
			return;
		}
		Object instanceObject = method.getDeclaringClass().equals(getClass()) ? this : metaInstance;
		try
		{
			Object returnObject = method.invoke(instanceObject, arguments);
			if (returnObject != null)
			{
				sendMessage(new Message(message.getGroup(), returnObject.toString(), MessageFormat.PLAIN_TEXT));
			}
		} catch (Exception e)
		{
			if (e.getCause() != null && e.getCause() instanceof MetaNotifyException)
			{
				sendMessage(new Message(message.getGroup(), e.getCause().getMessage(), MessageFormat.PLAIN_TEXT));
			} else
			{
				sendMessage(new Message(message.getGroup(), "A Meta Error has ocurred: " + e.toString(), MessageFormat.PLAIN_TEXT));
				Throwable cause = e.getCause();
				while (cause != null)
				{
					sendMessage(new Message(message.getGroup(), "The previous Error was caused by: " + cause.toString(), MessageFormat.PLAIN_TEXT));
					cause = cause.getCause();
				}
				e.printStackTrace();
			}
		}
	}

	private String getManTrigger()
	{
		return "?man " + metaClass.getSimpleName();
	}

	public String getHelpTopicForClass()
	{
		String helpTopic = "Man Page for " + metaClass.getSimpleName() + " at " + metaClass.getName() + "\n";
		if (metaClass.getAnnotation(MetaClass.class) != null)
		{
			helpTopic += metaClass.getAnnotation(MetaClass.class).helpTopic() + "\n";
		}
		Enumeration<Method> elements = methods.elements();
		while (elements.hasMoreElements())
		{
			Method method = elements.nextElement();

			helpTopic += getHelpTopicForMethod(method);
		}
		return helpTopic;
	}

	public String getHelpIndex()
	{
		return "Man Page: " + metaClass.getSimpleName() + ": " + getManTrigger();
	}

	protected String getHelpTopicForMethod(Method method)
	{
		MetaMethod annotation = method.getAnnotation(MetaMethod.class);
		if(annotation == null)
		{
			return "Method: " + method.getName() + " does not have a help topic\n";
		}
		String helpTopic = "Method: " + method.getName() + " Usage: " + getTrigger(annotation, method);
		for (Parameter parameter : method.getParameters())
		{
			helpTopic += "<" + parameter.getType().getSimpleName() + " " + parameter.getName() + "> ";
		}
		helpTopic += "\n";
		if (annotation != null && !annotation.equals(""))
		{
			helpTopic += annotation.helpTopic() + "\n";
		}
		for (Parameter parameter : method.getParameters())
		{
			if (parameter.getAnnotation(MetaParameter.class) != null)
			{
				helpTopic += "<" + parameter.getType().getSimpleName() + " " + parameter.getName() + ">: " + parameter.getAnnotation(MetaParameter.class).helpTopic() + "\n";
			}
		}
		return helpTopic;
	}
}
