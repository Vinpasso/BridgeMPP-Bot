package bridgempp.bot.wrapper.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.time.Duration;
import java.util.logging.Level;

import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.BotWrapper;
import bridgempp.bot.wrapper.Schedule;
import bridgempp.message.Message;
import bridgempp.message.MessageBuilder;
import bridgempp.services.socket.ProtoBufUtils;
import bridgempp.util.Log;
import bridgempp.util.Util;

public class IncommingMessageHandler extends SimpleChannelInboundHandler<bridgempp.services.socket.protobuf.Message>
{
	private Bot bot;

	public IncommingMessageHandler(Bot bot)
	{
		this.bot = bot;
	}

	protected void channelRead0(ChannelHandlerContext channelHandlerContext, bridgempp.services.socket.protobuf.Message protoMessage)
	{
		Message message = ProtoBufUtils.parseMessage(protoMessage, new MessageBuilder(null, null)).build();
		String lowerCaseMessage = message.getPlainTextMessageBody().toLowerCase();
		if (lowerCaseMessage.length() == 0)
		{
			return;
		}
		Log.log(Level.INFO, "Inbound: " + message.toString());
		if (lowerCaseMessage.startsWith("?botwrapper reload"))
		{
			bot.sendMessage(message.directConstructReply(null, null, "Bot Wrapper reloading. Respawn Throttle 60 seconds"));
			BotWrapper.shutdown();
		}
		if (lowerCaseMessage.startsWith("?botwrapper ping"))
		{
			bot.sendMessage(message.directConstructReply(null, null, "This is " + bot.name + " at your service"));
		}
		if (lowerCaseMessage.startsWith("?botwrapper version"))
		{
			bot.sendMessage(message.directConstructReply(null, null, "This is " + bot.name + " running on BridgeMPP-Bot-Wrapper Build: #" + BotWrapper.build));
		}
		if (lowerCaseMessage.startsWith("?botwrapper status"))
		{
			bot.sendMessage(message.directConstructReply(null, null, "This is Status Check triggered by " + bot.name + "\nResult:\n" + BotWrapper.statusCheck()));
		}
		if (lowerCaseMessage.startsWith("?botwrapper load"))
		{
			bot.sendMessage(message.directConstructReply(null, null, "Load Check: " + bot.name + " has used " + Duration.ofMillis(bot.getProcessingTime()).toString().substring(2) + " of processing time"));
		}
		if (lowerCaseMessage.startsWith("?botwrapper setproperty "))
		{
			String[] parameters = Util.parseStringCommandLineStyle(message.getPlainTextMessageBody());
			if (parameters.length < 5)
			{
				bot.sendMessage(message.directConstructReply(null, null, "Not enough Arguments! Requires <Bot Name> <Property Name> <Property Value>"));
			} else if (parameters[2].equalsIgnoreCase(bot.getName()))
			{
				bot.properties.setProperty(parameters[3], parameters[4]);
				bot.sendMessage(message.directConstructReply(null, null, "Set " + bot.name + " property " + parameters[3] + " to " + parameters[4]));
			}
		}
		if (lowerCaseMessage.startsWith("?botwrapper getproperty "))
		{
			String[] parameters = Util.parseStringCommandLineStyle(message.getPlainTextMessageBody());
			if (parameters.length < 4)
			{
				bot.sendMessage(message.directConstructReply(null, null, "Not enough Arguments! Requires <Bot Name> <Property Name>"));
			} else if (parameters[2].equalsIgnoreCase(bot.getName()))
			{
				bot.sendMessage(message.directConstructReply(null, null, "The " + bot.name + " property " + parameters[3] + " is " + bot.properties.getProperty(parameters[3])));
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
