package bridgempp.bot.metawrapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.Message;

public class MetaWrapper extends Bot {

	private Class<MetaBot> metaClass;
	private MetaBot metaInstance;
	private Method[] metaMethods;
	private Hashtable<String, Method> methods;
	private MetaClass classAnnotation;

	@Override
	public void initializeBot()  {
		try {
			metaInstance = metaClass.newInstance();
			classAnnotation = metaClass.getAnnotation(MetaClass.class);
			metaMethods = metaClass.getDeclaredMethods();
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
		return ("?" + metaClass.getName() + " " + method.getName() + " ").toLowerCase();
	}

	@Override
	public void messageReceived(Message message) {
		Enumeration<String> keys = methods.keys();
		while(keys.hasMoreElements())
		{
			String key = keys.nextElement();
			if(message.getPlainTextMessage().startsWith(key))
			{
				runMethod(methods.get(key), message.getPlainTextMessage());
			}
		}
	}

	private void runMethod(Method method, String message) {
		Parameter[] parameters = method.getParameters();
		Object[] arguments = new Object[parameters.length];
		parseParametersCommandLineStyle(parameters);
		try {
			method.invoke(metaInstance, arguments);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
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
	

}
