package bridgempp.bot.wrapper;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.apache.commons.lang3.exception.ExceptionUtils;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.util.Log;

public class Schedule
{
	private static ThreadFactory threadFactory;
	private static ScheduledExecutorService executorService;
	private static PriorityBlockingQueue<MessageEntry> messageQueue;
	private static final int numThreads = 5;
	private static final MessageProcessor MESSAGE_PROCESSOR = new MessageProcessor();
	protected static volatile int numMessageProcessors = 0;

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
		messageQueue = new PriorityBlockingQueue<>();
		Log.log(Level.INFO, "Started Bot Scheduler");
	}

	public static void stopExecutorService()
	{
		Log.log(Level.INFO, "Shutting down Schedule Service");
		threadFactory = null;
		if (executorService != null)
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
		messageQueue.add(new MessageEntry(message, bot));
		allocateMessageProcessor();
	}
	
	private static synchronized void allocateMessageProcessor()
	{
		if(numMessageProcessors < numThreads)
		{
			numMessageProcessors++;
			execute(MESSAGE_PROCESSOR);
		}
	}
	
	private static synchronized void deallocateMessageProcessor()
	{
		numMessageProcessors--;
	}


	protected static class MessageProcessor implements Runnable
	{
			public void run()
			{
				while (!messageQueue.isEmpty())
				{
					MessageEntry entry;
					try
					{
						entry = messageQueue.poll(1, TimeUnit.SECONDS);
					} catch (InterruptedException e1)
					{
						continue;
					}
					if(entry == null)
					{
						continue;
					}
					Bot bot = entry.getBot();
					Message message = entry.getMessage();
					long startTime = System.currentTimeMillis();
					try
					{
						bot.synchronizedMessageReceived(message);
					} catch (Exception e)
					{
						BotWrapper.printMessage(new Message(message.getGroup(), "A Bot has crashed!\n" + e.toString() + "\n" + ExceptionUtils.getStackTrace(e), MessageFormat.PLAIN_TEXT), bot);
					}
					bot.appendProcessingTime(System.currentTimeMillis() - startTime);
				}
				deallocateMessageProcessor();
			}
	}

}
