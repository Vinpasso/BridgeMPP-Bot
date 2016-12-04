package bots.ChatterBot;

import com.google.code.chatterbotapi.ChatterBotSession;

public class ChatterSessionWrapper {
	
	public boolean active;
	public ChatterBotSession session;
	public String name;
	public ChatterSessionWrapper(boolean active, ChatterBotSession session) {
		super();
		this.active = active;
		this.session = session;
	}
	public ChatterSessionWrapper(boolean active, ChatterBotSession session,String name) {
		super();
		this.active = active;
		this.session = session;
		this.name = name;
	}

}
