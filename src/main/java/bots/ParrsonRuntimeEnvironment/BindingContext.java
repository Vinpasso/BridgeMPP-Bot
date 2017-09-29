package bots.ParrsonRuntimeEnvironment;

import java.util.HashMap;
import java.util.logging.Level;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.script.Bindings;
import javax.script.SimpleBindings;

import com.thoughtworks.xstream.XStream;

import bridgempp.bot.database.PersistenceManager;
import bridgempp.util.Log;

@Entity(name = "SCRIPTBINDINGCONTEXT")
public class BindingContext
{
	@Id()
	@GeneratedValue()
	int id;
	
	@Column(name = "PERSISTENT", nullable = false)
	boolean persistent;
	
	@Column(name = "BINDINGDATA", nullable = false, length = 50000000)
	@Lob
	String data = "";
	
	
	private transient Bindings bindings = new SimpleBindings();
	private transient HashMap<String, Object> bindingValues = new HashMap<String, Object>();
	
	private static XStream xStream = new XStream();

	@SuppressWarnings("unchecked")
	public BindingContext(boolean persistent)
	{
		this.persistent = persistent || this.persistent;
		if(data.length() == 0 || !persistent)
		{
			bindings = new SimpleBindings(bindingValues);
			return;
		}
		try
		{
			bindingValues = (HashMap<String, Object>) xStream.fromXML(data);
			bindings = new SimpleBindings(bindingValues);
		} catch (Exception e)
		{
			Log.log(Level.SEVERE, "Could not load Bindings: " + id, e);
		}
	}
	
	protected BindingContext()
	{
		this(false);
	}
	
	public void saveBindings()
	{
		if(!persistent)
		{
			return;
		}
		data = xStream.toXML(bindingValues);
		PersistenceManager.getForCurrentThread().updateState(this);
	}
	
	
	public Bindings asBindingContext()
	{
		return bindings;
	}

	
}
