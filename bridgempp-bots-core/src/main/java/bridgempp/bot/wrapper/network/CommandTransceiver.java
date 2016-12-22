package bridgempp.bot.wrapper.network;

import java.util.logging.Level;

import org.apache.commons.lang3.exception.ExceptionUtils;

import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.BotWrapper;
import bridgempp.message.MessageBuilder;
import bridgempp.services.socket.ProtoBufUtils;
import bridgempp.services.socket.protobuf.Message;
import bridgempp.util.Log;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

public class CommandTransceiver extends
		SimpleChannelInboundHandler<Message> {
	private State state;
	private Channel channel;
	private Bot bot;
	private String key;
	private String[] groups;
	private int groupCount;

	public CommandTransceiver(Channel channel, String alias, String key,
			String[] groups, Bot bot) {
		state = State.CREATING_ALIAS;
		this.channel = channel;
		this.bot = bot;
		this.key = key;
		this.groups = groups;
	}

	public void initializeCommands() {
		setupPipeline();
		sendCreateAlias(bot.name);
	}

	private void setupPipeline() {
		ChannelPipeline pipeline = channel.pipeline();
		pipeline.addLast("idleStateHandler", new IdleStateHandler(120, 60, 120));
		pipeline.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
		pipeline.addLast("protobufDecoder", new ProtobufDecoder(
				Message.getDefaultInstance()));
		pipeline.addLast("frameEncoder",
				new ProtobufVarint32LengthFieldPrepender());
		pipeline.addLast("protobufEncoder", new ProtobufEncoder());
		pipeline.addLast("keepAliveSender", new KeepAliveSender());
		pipeline.addLast("commandTransceiver", this);
	}

	private void sendCreateAlias(String alias) {
		sendCommand("!botcreatealias \"" + alias + "\"", bot);
	}

	private void sendUseKey(String key) {
		sendCommand("!botusekey \"" + key + "\"", bot);
	}

	private void sendSubscribeGroup(String group) {
		sendCommand("!botsubscribegroup \"" + group + "\"", bot);
	}

	private void sendCommand(String command, Bot bot) {
		Log.log(Level.INFO, "State: " + state.toString(), bot);
		ChannelFuture future = BotWrapper.printCommand(command, bot);
		future.addListener(new GenericFutureListener<Future<? super Void>>() {

			@Override
			public void operationComplete(Future<? super Void> future) {
				if (future.isSuccess()) {
					Log.log(Level.INFO, "Sent the command request: " + command,
							bot);
				} else {
					Log.log(Level.SEVERE,
							"Failed to send the command request: " + command,
							bot);
					BotWrapper.shutdown();
				}
			}
		});
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message msg) {
		MessageBuilder messageBuilder = new MessageBuilder(null, null);
		ProtoBufUtils.parseMessage(msg, messageBuilder);
		String confirmation = messageBuilder.build().getPlainTextMessageBody();
		if (confirmation.equalsIgnoreCase("BridgeMPP: command status success")) {
			Log.log(Level.INFO, "Status success: " + state.toString(), bot);
			switch (state) {
			case CREATING_ALIAS:
				state = State.USING_KEY;
				sendUseKey(key);
				break;
			case USING_KEY:
				state = State.JOINING_GROUPS;
				groupCount = 0;
				sendSubscribeGroup(groups[groupCount]);
				break;
			case JOINING_GROUPS:
				groupCount++;
				if (groupCount < groups.length) {
					sendSubscribeGroup(groups[groupCount]);
				} else {
					doneExecutingCommands();
				}
				break;
			default:
				break;

			}
		} else {
			Log.log(Level.SEVERE, "Unexpected Command confirmation: "
					+ confirmation, bot);
			BotWrapper.shutdown();
		}
	}

	private void doneExecutingCommands() {
		try {
			Log.wrapperLog(Level.INFO, "Initializing bot: " + bot.getName());
			bot.initializeBot();
			Log.wrapperLog(Level.INFO, "Initialized bot: " + bot.getName());
		} catch (Exception e) {
			Log.log(Level.WARNING, "Error while initializing Bot: " + bot.getName(), e);
			bot.sendMessage(new MessageBuilder(null, null).addPlainTextBody("Error while initializing Bot: " + bot.getName() + "\n" + e.toString() + "\n" + ExceptionUtils.getStackTrace(e)).build());
		}
		Log.log(Level.INFO,
				"Commands successfully executed. Starting Incomming Message Handler",
				bot);
		ChannelPipeline pipeline = channel.pipeline();
		pipeline.remove(this);
		pipeline.addLast("incommingMessageHandler",
				new IncommingMessageHandler(bot));
		Log.log(Level.INFO, "Started Incomming Message Handler", bot);
	}

	private enum State {
		CREATING_ALIAS, USING_KEY, JOINING_GROUPS
	}

}
