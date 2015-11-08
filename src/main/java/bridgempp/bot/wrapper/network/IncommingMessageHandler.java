package bridgempp.bot.wrapper.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Level;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.BotWrapper;
import bridgempp.bot.wrapper.Message;
import bridgempp.util.Log;

public class IncommingMessageHandler extends
		SimpleChannelInboundHandler<ProtoBuf.Message> {
	private Bot bot;

	public IncommingMessageHandler(Bot bot) {
		this.bot = bot;
	}

	protected void channelRead0(ChannelHandlerContext channelHandlerContext,
			ProtoBuf.Message protoMessage) {
		Message message = new Message(protoMessage.getGroup(),
				protoMessage.getSender(), protoMessage.getTarget(),
				protoMessage.getMessage(),
				MessageFormat.parseMessageFormat(protoMessage
						.getMessageFormat()));
		if (message.getMessage().length() == 0) {
			return;
		}
		Log.log(Level.INFO, "Inbound: " + message.toComplexString());
		if (message.getMessage().startsWith("?botwrapper reload")) {
			bot.sendMessage(new Message(message.getGroup(),
					"Bot Wrapper reloading. Respawn Throttle 60 seconds",
					MessageFormat.PLAIN_TEXT));
			BotWrapper.shutdown();
		}
		if (message.getMessage().startsWith("?botwrapper ping")) {
			bot.sendMessage(new Message(message.getGroup(), "This is "
					+ bot.name + " at your service", MessageFormat.PLAIN_TEXT));
		}
		if (message.getMessage().startsWith("?botwrapper version")) {
			bot.sendMessage(new Message(message.getGroup(), "This is "
					+ bot.name + " running on BridgeMPP-Bot-Wrapper Build: #"
					+ BotWrapper.build, MessageFormat.PLAIN_TEXT));
		}
		if(message.getMessage().startsWith("?botwrapper status"))
		{
			bot.sendMessage(new Message(message.getGroup(), "This is Status Check triggered by "
					+ bot.name + "\nResult:\n"
					+ BotWrapper.statusCheck(), MessageFormat.PLAIN_TEXT));
		}
		try {
			bot.messageReceived(message);
		} catch (Exception e) {
			BotWrapper.printMessage(
					new Message(message.getGroup(), "A Bot has crashed!\n"
							+ e.toString() + "\n"
							+ e.getStackTrace()[0].toString(),
							MessageFormat.PLAIN_TEXT), bot);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		Log.log(Level.SEVERE, "A Connection has been disconnected, exiting...",
				cause);
		BotWrapper.shutdown();
	}

}
