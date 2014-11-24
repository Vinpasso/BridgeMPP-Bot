package bots.CommandBot.calc;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class CalcThread extends Thread {
	private ScriptEngine e;
	private String result;
	private String exp;

	CalcThread(ScriptEngine e, String exp) {
		this.e = e;
		this.exp = exp;
	}

	public void run() {
		try {
			Object res = e.eval(exp);
			if (res != null) {
				result = res.toString();
			} else {
				result = "Input has no result.";
			}
		} catch (ScriptException e1) {
			String ex = e1.getMessage();
			int start = ex.lastIndexOf(":")+2;
			int end = ex.indexOf("(")-1;
			if (start >= 0 && end >= 0) {
				result = ex.substring(start, end);
			} else {
				result = ex;
			}
		}
		synchronized (this) {
			this.notify();
		}
	}

	public String getResult() {
		return result;
	}
}