package bridgempp.bot.wrapper;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.logging.Level;
import java.util.logging.Logger;


public class IncommingMessageHandler extends SimpleChannelInboundHandler<ProtoBuf.Message> {
	private Bot bot;

	public IncommingMessageHandler(Bot bot) {
		this.bot = bot;
	}

	protected void channelRead0(ChannelHandlerContext channelHandlerContext, ProtoBuf.Message protoMessage) {
		Message message = new Message(protoMessage.getGroup(), protoMessage.getSender(), protoMessage.getTarget(),
				protoMessage.getMessage(), protoMessage.getMessageFormat());
		Logger.getLogger(BotWrapper.class.getName()).log(Level.INFO, "Inbound: " + message.toComplexString());
		if (message.getMessage().startsWith("?botwrapper reload")) {
			bot.sendMessage(new Message(message.getGroup(), "Bot Wrapper reloading. Respawn Throttle 10 seconds",
					"Plain Text"));
			System.exit(0);
		}
		if (message.getMessage().startsWith("?botwrapper ping")) {
			bot.sendMessage(new Message(message.getGroup(), "This is " + bot.name + " at your service",
					"Plain Text"));
		}
		if (message.getMessage().startsWith("?botwrapper version")) {
			bot.sendMessage(new Message(message.getGroup(), "This is " + bot.name
					+ " running on BridgeMPP-Bot-Wrapper Build: #" + BotWrapper.build, "Plain Text"));
		}
		if (message.getMessage().length() == 0) {
			return;
		}
		try {
			bot.messageReceived(message);
		} catch (Exception e) {
			BotWrapper.printMessage(
					new Message(message.getGroup(), "A Bot has crashed!\n" + e.toString() + "\n"
							+ e.getStackTrace()[0].toString(), "Plain Text"), bot);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		Logger.getLogger(BotWrapper.class.getName()).log(Level.SEVERE,
				"A Connection has been disconnected, exiting...", cause);
		System.exit(0);
	}

}