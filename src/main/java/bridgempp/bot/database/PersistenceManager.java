package bridgempp.bot.database;

import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import bridgempp.util.Log;

public class PersistenceManager
{
	private static HashMap<Thread, PersistenceManager> managers = new HashMap<Thread, PersistenceManager>();
	private static EntityManagerFactory entityManagerFactory;
	private EntityManager entityManager;
	
	public PersistenceManager()
	{
		if(entityManagerFactory == null)
		{
			entityManagerFactory = Persistence.createEntityManagerFactory("bridgempp-bots");
		}
		entityManager = entityManagerFactory.createEntityManager();
	}
	
	public <T> T getFromPrimaryKey(Class<T> className, Object primaryKey)
	{
		return entityManager.find(className, primaryKey);
	}

	public <T> Collection<T> getAll(Class<T> className)
	{
		Entity annotation = className.getAnnotation(Entity.class);
		if (annotation == null || annotation.name() == null || annotation.name().length() == 0)
		{
			return null;
		}
		return entityManager.createQuery("SELECT s FROM " + annotation.name() + " s", className).getResultList();
	}
	
	public <T> Collection<T> getFromSelectSQLQuery(Class<T> className, String sqlQuery)
	{
		return entityManager.createQuery(sqlQuery, className).getResultList();
	}
	
	public <T> T getSingleFromSelectSQLQuery(Class<T> className, String sqlQuery)
	{
		return entityManager.createQuery(sqlQuery, className).getSingleResult();
	}
	
	public int executeUpdateQuery(String sqlQuery)
	{
		return entityManager.createQuery(sqlQuery).executeUpdate();
	}

	public void updateState(Object... objects)
	{
		EntityTransaction saveTransaction = entityManager.getTransaction();
		saveTransaction.begin();
		for (Object object : objects)
		{
			try
			{
				if (entityManager.contains(object))
				{
					entityManager.merge(object);
				} else
				{
					entityManager.persist(object);
				}
			} catch (Exception e)
			{
				Log.log(Level.SEVERE, "Error while writing to Database", e);
			}
		}
		saveTransaction.commit();
	}

	public void removeState(Object... objects)
	{
		EntityTransaction transaction = entityManager.getTransaction();
		transaction.begin();
		for (Object object : objects)
		{
			entityManager.remove(object);
		}
		transaction.commit();
	}

	public void shutdown()
	{
		entityManager.close();
		entityManagerFactory.close();
	}
	
	public static PersistenceManager getForCurrentThread()
	{
		PersistenceManager manager = managers.get(Thread.currentThread());
		if(manager == null)
		{
			manager = new PersistenceManager();
			managers.put(Thread.currentThread(), manager);
		}
		return manager;
	}
	
	public void checkAccess()
	{
		if(!managers.get(Thread.currentThread()).equals(this))
		{
			throw new InvalidParameterException("Attempted to access Persistence Manager from another Thread");
		}
	}
}
