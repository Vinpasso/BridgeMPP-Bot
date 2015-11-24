package bridgempp.bot.wrapper.network;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.logging.Level;

import bridgempp.bot.messageformat.MessageFormat;
import bridgempp.bot.wrapper.BotWrapper;
import bridgempp.util.Log;

public class KeepAliveSender extends ChannelDuplexHandler {
	@Override
	public void userEventTriggered(ChannelHandlerContext context, Object event) {
		if (event instanceof IdleStateEvent) {
			IdleStateEvent idleEvent = (IdleStateEvent) event;
			if (idleEvent.state() == IdleState.WRITER_IDLE) {
				ProtoBuf.Message protoMessage = ProtoBuf.Message.newBuilder()
						.setMessageFormat(MessageFormat.PLAIN_TEXT.getName())
						.setMessage("").setSender("").setTarget("")
						.setGroup("").build();
				ChannelFuture future = context.writeAndFlush(protoMessage);
				future.addListener(new ChannelFutureListener() {

					@Override
					public void operationComplete(ChannelFuture future)
							throws Exception {
						if (!future.isSuccess()) {
							Log.log(Level.SEVERE,
									"A Connection has been disconnected after PING: "
											+ future.toString()
											+ ", exiting...");
							BotWrapper.shutdown();
						}
					}
				});
			} else if (idleEvent.state() == IdleState.READER_IDLE) {
				Log.log(Level.SEVERE,
						"A Connection has died due to READER_IDLE");
				BotWrapper.shutdown();
			} else if (idleEvent.state() == IdleState.ALL_IDLE) {
				Log.log(Level.SEVERE,
								"Communications have stalled on a connection due to ALL_IDLE");
				BotWrapper.shutdown();
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		Log.log(Level.SEVERE,
						"Communications have broken down on a Connection due to Exception",
						cause);
		BotWrapper.shutdown();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		Log.log(Level.SEVERE,
						"Communications have broken down on a Connection due to Channel Deactivation");
		BotWrapper.shutdown();
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
		Log.log(Level.SEVERE,
						"Communications have broken down on a Connection due to Channel Derigistration");
		BotWrapper.shutdown();
	}
}