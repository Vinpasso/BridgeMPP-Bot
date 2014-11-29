package bots.CalcBot.calc;

import bots.CalcBot.logger.ErrorLogger;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class CalculationInterpreter {
	private ScriptEngineManager mgr;
	private ScriptEngine engine;
	private List<String> methodNames;
	private List<String> fieldNames;
	private long lastOOME = 0;

	public static boolean debug = false;

	public void init() {
		mgr = new ScriptEngineManager();
		engine = mgr.getEngineByName("JavaScript");

		Method[] methods = Math.class.getDeclaredMethods();
		methodNames = new ArrayList<>();
		for (Method m : methods) {
			if (!methodNames.contains(m.getName())) {
				methodNames.add(m.getName());
			}
		}

		Field[] fields = Math.class.getDeclaredFields();
		fieldNames = new ArrayList<>();
		for (Field f : fields) {
			String name = f.getName();
			if (name.toUpperCase().equals(name)) {
				fieldNames.add(name);
			}
		}
	}

	public String getAnswer(String expression) {
		if (expression.equals("debug on")) {
			debug = true;
			return null;
		} else if (expression.equals("debug off")) {
			debug = false;
			return null;
		}

		try {
			String result = calculate(expression);
			if (result.split("\n").length > 10 || result.length() > 500) {
				return "Result is too long.";
			}
			return "\"" + expression + "\"   =   " + calculate(expression);
		} catch (OutOfMemoryError e) {
			ErrorLogger.logger.log(Level.SEVERE, "An Error has occured:", e);
			if (debug) {
				e.printStackTrace();
			}
			if (System.currentTimeMillis() - lastOOME < 10000) {
				throw e;
			}
			engine = mgr.getEngineByName("JavaScript");
			lastOOME = System.currentTimeMillis();
			ErrorLogger.logger.log(Level.SEVERE, "The Error could be resolved.");
			return "Scriptengine needed to be restarted, please try again.";
		}
	}

	@SuppressWarnings("deprecation")
	public String calculate(String expression) {
		String withFunctions = replaceFunctions(expression);
		CalcThread ct = new CalcThread(engine, withFunctions);
		ct.start();
		try {
			synchronized(ct) {
				ct.wait(3000);
			}
		} catch (InterruptedException e) {
			ErrorLogger.logger.log(Level.SEVERE, "An Error has occured:", e);
			if (debug) {
				e.printStackTrace();
			}
			return "Waiting Thread has been interrupted.";
		}
		if (ct.getResult() == null) {
			ct.stop();
			return "Calculation needed too much time.";
		}
		return ct.getResult();
	}

	private String replaceFunctions(String expression) {
		for (String name : methodNames) {
			expression = expression.replaceAll(name, "Math.".concat(name));
		}
		for (String name : fieldNames) {
			expression = expression.replaceAll("(?i)[^a-zA-Z]" + name + "[^a-zA-Z]", "Math.".concat(name));
		}
		return expression;
	}
}