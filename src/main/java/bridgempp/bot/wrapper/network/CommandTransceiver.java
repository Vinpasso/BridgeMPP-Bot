package bridgempp.bot.wrapper.network;

import java.util.logging.Level;

import bridgempp.bot.wrapper.Bot;
import bridgempp.bot.wrapper.BotWrapper;
import bridgempp.bot.wrapper.network.ProtoBuf.Message;
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

public class CommandTransceiver extends SimpleChannelInboundHandler<ProtoBuf.Message>
{
	private State state;
	private Channel channel;
	private Bot bot;
	private String key;
	private String[] groups;
	private int groupCount;
	
	public CommandTransceiver(Channel channel, String alias, String key, String[] groups, Bot bot)
	{
		state = State.CREATING_ALIAS;
		this.channel = channel;
		this.bot = bot;
		this.key = key;
		this.groups = groups;
	}
	
	public void initializeCommands()
	{
		setupPipeline();
		sendCreateAlias(bot.name);
	}
	
	private void setupPipeline()
	{
		ChannelPipeline pipeline = channel.pipeline();
		pipeline.addLast("idleStateHandler", new IdleStateHandler(120, 60,
				120));
		pipeline.addLast("frameDecoder", new ProtobufVarint32FrameDecoder());
		pipeline.addLast("protobufDecoder", new ProtobufDecoder(
				ProtoBuf.Message.getDefaultInstance()));
		pipeline.addLast("frameEncoder",
				new ProtobufVarint32LengthFieldPrepender());
		pipeline.addLast("protobufEncoder", new ProtobufEncoder());
		pipeline.addLast("keepAliveSender", new KeepAliveSender());
		pipeline.addLast("commandTransceiver", this);
	}

	private void sendCreateAlias(String alias)
	{
		sendCommand("!botcreatealias \"" + alias + "\"", bot);
	}
	
	private void sendUseKey(String key)
	{
		sendCommand("!botusekey \"" + key + "\"", bot);
	}
	
	private void sendSubscribeGroup(String group)
	{
		sendCommand("!botsubscribegroup \"" + group + "\"", bot);
	}

	
	private void sendCommand(String command, Bot bot)
	{
		Log.log(Level.INFO, "Bot: " + bot.name + " is in State: " + state.toString());
		ChannelFuture future = BotWrapper.printCommand(command, bot);
		future.addListener(new GenericFutureListener<Future<? super Void>>() {

			@Override
			public void operationComplete(Future<? super Void> future) throws Exception
			{
				if(future.isSuccess())
				{
					Log.log(Level.INFO, "Bot: " + bot.name + " sent the command request: " + command);
				}
				else
				{
					Log.log(Level.SEVERE, "Bot: " + bot.name + " failed to send the command request: " + command);
					BotWrapper.shutdown();
				}
			}
		});
	}
	
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception
	{
		String confirmation = msg.getMessage();
		if(confirmation.equalsIgnoreCase("BridgeMPP: command status success"))
		{
			Log.log(Level.INFO, "Status success for Bot: " + bot.name + " for status: " + state.toString());
			switch(state)
			{
				case CREATING_ALIAS:
					state = State.USING_KEY;
					sendUseKey(key);
					break;
				case USING_KEY:
					state=State.JOINING_GROUPS;
					groupCount = 0;
					sendSubscribeGroup(groups[groupCount]);
					break;
				case JOINING_GROUPS:
					groupCount++;
					if(groupCount < groups.length)
					{
						sendSubscribeGroup(groups[groupCount]);
					}
					else
					{
						doneExecutingCommands();
					}
					break;
				default:
					break;
				
			}
		}
		else
		{
			Log.log(Level.SEVERE, "Unexpected Command confirmation: " + confirmation);
			BotWrapper.shutdown();
		}
	}
	
	private void doneExecutingCommands()
	{
		Log.log(Level.INFO, "Commands successfully executed. Starting Incomming Message Handler");
		ChannelPipeline pipeline = channel.pipeline();
		pipeline.remove(this);
		pipeline.addLast("incommingMessageHandler", new IncommingMessageHandler(bot));		
		Log.log(Level.INFO, "Started Incomming Message Handler");
	}

	private enum State
	{
		CREATING_ALIAS,
		USING_KEY,
		JOINING_GROUPS
	}

}
