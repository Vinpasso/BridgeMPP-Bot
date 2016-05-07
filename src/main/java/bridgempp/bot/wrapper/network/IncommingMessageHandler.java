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
		if (message.getMessage().length() == 0)
		{
			return;
		}
		Log.log(Level.INFO, "Inbound: " + message.toComplexString());
		if (message.getMessage().startsWith("?botwrapper reload"))
		{
			bot.sendMessage(new Message(message.getGroup(), "Bot Wrapper reloading. Respawn Throttle 60 seconds", MessageFormat.PLAIN_TEXT));
			BotWrapper.shutdown();
		}
		if (message.getMessage().startsWith("?botwrapper ping"))
		{
			bot.sendMessage(new Message(message.getGroup(), "This is " + bot.name + " at your service", MessageFormat.PLAIN_TEXT));
		}
		if (message.getMessage().startsWith("?botwrapper version"))
		{
			bot.sendMessage(new Message(message.getGroup(), "This is " + bot.name + " running on BridgeMPP-Bot-Wrapper Build: #" + BotWrapper.build, MessageFormat.PLAIN_TEXT));
		}
		if (message.getMessage().startsWith("?botwrapper status"))
		{
			bot.sendMessage(new Message(message.getGroup(), "This is Status Check triggered by " + bot.name + "\nResult:\n" + BotWrapper.statusCheck(), MessageFormat.PLAIN_TEXT));
		}
		if (message.getMessage().startsWith("?botwrapper load"))
		{
			bot.sendMessage(new Message(message.getGroup(), "Load Check: " + bot.name + " has used " + Duration.ofMillis(bot.getProcessingTime()).toString().substring(2) + " of processing time", MessageFormat.PLAIN_TEXT));
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
