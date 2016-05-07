package bridgempp.bot.wrapper;

import java.util.AbstractMap;
import java.util.Comparator;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.util.Log;

public class Schedule
{
	private static ThreadFactory threadFactory;
	private static ScheduledExecutorService executorService;
	private static PriorityBlockingQueue<Entry<Bot, Message>> messageQueue;
	private static final int numThreads = 5;
	
	public static void startExecutorService()
	{
		Log.log(Level.INFO, "Starting Bot Scheduler");
		threadFactory = new ThreadFactory() {
			private int threadNumber = 0;
			@Override
			public Thread newThread(Runnable r)
			{
				Thread thread = new Thread(r);
				thread.setName("Bot Thread Executor #" + threadNumber++);
				thread.setPriority(Thread.MIN_PRIORITY);
				thread.setDaemon(false);
				return thread;
			}
		};
		executorService = Executors.newScheduledThreadPool(5, threadFactory);
		messageQueue = new PriorityBlockingQueue<>(10, new Comparator<Entry<Bot, Message>>() {

			@Override
			public int compare(Entry<Bot, Message> o1, Entry<Bot, Message> o2)
			{
				return (int) (o1.getKey().getProcessingTime() - o2.getKey().getProcessingTime());
			}
		});
		int i = 0;
		do
		{
			scheduleRepeatWithDelay(createTask(), 1000, 50, TimeUnit.MILLISECONDS);
			i++;
		} while(i < numThreads);
		Log.log(Level.INFO, "Started Bot Scheduler");
	}
	
	public static void stopExecutorService()
	{
		Log.log(Level.INFO, "Shutting down Schedule Service");
		threadFactory = null;
		if(executorService != null)
		{
			executorService.shutdown();
			Log.log(Level.INFO, "Waiting for running Tasks to be completed...");
			try
			{
				executorService.awaitTermination(60, TimeUnit.SECONDS);
			} catch (InterruptedException e)
			{
				Log.log(Level.WARNING, "Termination of running Tasks was interrupted", e);
			}
		}
		executorService = null;
		Log.log(Level.INFO, "Schedule Service was shut down");
	}
	
	public static Future<?> execute(Runnable runnable)
	{
		return executorService.submit(runnable);
	}
	
	public static <T> Future<T> execute(Callable<T> callable)
	{
		return executorService.submit(callable);
	}
	
	public static Future<?> scheduleOnce(Runnable runnable, long delay, TimeUnit unit)
	{
		return executorService.schedule(runnable, delay, unit);
	}
	
	public static <T> Future<T> scheduleOnce(Callable<T> callable, long delay, TimeUnit unit)
	{
		return executorService.schedule(callable, delay, unit);
	}
	
	public static Future<?> scheduleRepeatWithPeriod(Runnable runnable, long initialDelay, long periodDelay, TimeUnit unit)
	{
		return executorService.scheduleAtFixedRate(runnable, initialDelay, periodDelay, unit);
	}
	
	public static Future<?> scheduleRepeatWithDelay(Runnable runnable, long initialDelay, long periodDelay, TimeUnit unit)
	{
		return executorService.scheduleWithFixedDelay(runnable, initialDelay, periodDelay, unit);
	}
	
	public static void submitMessage(Bot bot, Message message)
	{
		messageQueue.add(new AbstractMap.SimpleEntry<Bot, Message>(bot, message));
	}
	
	protected static Runnable createTask()
	{
		return new Runnable() {
			public void run()
			{
				Entry<Bot, Message> entry;
				try
				{
					entry = messageQueue.take();
				} catch (InterruptedException e1)
				{
					return;
				}
				Bot bot = entry.getKey();
				Message message = entry.getValue();
				long startTime = System.currentTimeMillis();
				try
				{
					bot.synchronizedMessageReceived(message);
				} catch (Exception e)
				{
					BotWrapper.printMessage(new Message(message.getGroup(), "A Bot has crashed!\n" + e.toString() + "\n" + e.getStackTrace()[0].toString(), MessageFormat.PLAIN_TEXT), bot);
				}
				bot.appendProcessingTime(System.currentTimeMillis() - startTime);
			}
		};
	}
	
}
