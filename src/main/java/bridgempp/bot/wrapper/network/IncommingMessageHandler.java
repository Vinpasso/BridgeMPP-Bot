package bridgempp.bot.wrapper.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.time.Duration;
import java.util.logging.Level;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.BotWrapper;
import bridgempp.bot.wrapper.Message;
import bridgempp.bot.wrapper.Schedule;
import bridgempp.util.Log;
import bridgempp.util.Util;

public class IncommingMessageHandler extends SimpleChannelInboundHandler<ProtoBuf.Message>
{
	private Bot bot;

	public IncommingMessageHandler(Bot bot)
	{
		this.bot = bot;
	}

	protected void channelRead0(ChannelHandlerContext channelHandlerContext, ProtoBuf.Message protoMessage)
	{
		Message message = new Message(protoMessage.getGroup(), protoMessage.getSender(), protoMessage.getTarget(), protoMessage.getMessage(),
				MessageFormat.parseMessageFormat(protoMessage.getMessageFormat()));
		String lowerCaseMessage = message.getMessage().toLowerCase();
		if (lowerCaseMessage.length() == 0)
		{
			return;
		}
		Log.log(Level.INFO, "Inbound: " + message.toComplexString());
		if (lowerCaseMessage.startsWith("?botwrapper reload"))
		{
			bot.sendMessage(new Message(message.getGroup(), "Bot Wrapper reloading. Respawn Throttle 60 seconds", MessageFormat.PLAIN_TEXT));
			BotWrapper.shutdown();
		}
		if (lowerCaseMessage.startsWith("?botwrapper ping"))
		{
			bot.sendMessage(new Message(message.getGroup(), "This is " + bot.name + " at your service", MessageFormat.PLAIN_TEXT));
		}
		if (lowerCaseMessage.startsWith("?botwrapper version"))
		{
			bot.sendMessage(new Message(message.getGroup(), "This is " + bot.name + " running on BridgeMPP-Bot-Wrapper Build: #" + BotWrapper.build, MessageFormat.PLAIN_TEXT));
		}
		if (lowerCaseMessage.startsWith("?botwrapper status"))
		{
			bot.sendMessage(new Message(message.getGroup(), "This is Status Check triggered by " + bot.name + "\nResult:\n" + BotWrapper.statusCheck(), MessageFormat.PLAIN_TEXT));
		}
		if (lowerCaseMessage.startsWith("?botwrapper load"))
		{
			bot.sendMessage(new Message(message.getGroup(), "Load Check: " + bot.name + " has used " + Duration.ofMillis(bot.getProcessingTime()).toString().substring(2) + " of processing time",
					MessageFormat.PLAIN_TEXT));
		}
		if (lowerCaseMessage.startsWith("?botwrapper setproperty "))
		{
			String[] parameters = Util.parseStringCommandLineStyle(message.getMessage());
			if(parameters.length < 5)
			{
				bot.sendMessage(message.replyTo("Not enough Arguments! Requires <Bot Name> <Property Name> <Property Value>", MessageFormat.PLAIN_TEXT));
			}
			else if(parameters[2].equalsIgnoreCase(bot.getName()))
			{
				bot.properties.setProperty(parameters[3], parameters[4]);
				bot.sendMessage(message.replyTo("Set " + bot.name + " property " + parameters[3] + " to " + parameters[4], MessageFormat.PLAIN_TEXT));
			}
		}
		if (lowerCaseMessage.startsWith("?botwrapper getproperty "))
		{
			String[] parameters = Util.parseStringCommandLineStyle(message.getMessage());
			if(parameters.length < 4)
			{
				bot.sendMessage(message.replyTo("Not enough Arguments! Requires <Bot Name> <Property Name>", MessageFormat.PLAIN_TEXT));
			}
			else if(parameters[2].equalsIgnoreCase(bot.getName()))
			{
				bot.sendMessage(message.replyTo("Set " + bot.name + " property " + parameters[3] + " is " + bot.properties.getProperty(parameters[3]), MessageFormat.PLAIN_TEXT));
			}
		}
		Schedule.submitMessage(bot, message);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
	{
		Log.log(Level.SEVERE, "A Connection has been disconnected, exiting...", cause);
		BotWrapper.shutdown();
	}

}
