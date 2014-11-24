package de.bots.command.calc;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class CalculationInterpreter {
	private ScriptEngine engine;
	private List<String> methodNames;
	private List<String> fieldNames;

	public CalculationInterpreter() {
		ScriptEngineManager mgr = new ScriptEngineManager();
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
		return "CmdBot: " + expression + " = " + calculate(expression);
	}

	public String calculate(String expression) {
		String withFunctions = replaceFunctions(expression);
		CalcThread ct = new CalcThread(engine, withFunctions);
		ct.start();
		try {
			synchronized(ct) {
				ct.wait(3000);
			}
		} catch (InterruptedException e) {
			return e.getMessage();
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

	public static void main(String[] args) {
		CalculationInterpreter i = new CalculationInterpreter();
		System.out.println(i.getAnswer("for(;;) {}"));
	}
}