package bridgempp.bot.wrapper.network;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.logging.Level;

import bridgempp.bot.wrapper.BotWrapper;
import bridgempp.message.MessageBuilder;
import bridgempp.services.socket.ProtoBufUtils;
import bridgempp.services.socket.protobuf.Message;
import bridgempp.util.Log;

public class KeepAliveSender extends ChannelDuplexHandler {
	@Override
	public void userEventTriggered(ChannelHandlerContext context, Object event) {
		if (event instanceof IdleStateEvent) {
			IdleStateEvent idleEvent = (IdleStateEvent) event;
			if (idleEvent.state() == IdleState.WRITER_IDLE) {
				sendPing(context);
			} else if (idleEvent.state() == IdleState.READER_IDLE) {
				Log.log(Level.WARNING,
						"A Connection has received READER_IDLE, sending PING");
				sendPing(context);
			} else if (idleEvent.state() == IdleState.ALL_IDLE) {
				Log.log(Level.WARNING,
								"Communications have stalled on a connection due to ALL_IDLE, sending PING");
				sendPing(context);
			}
		}
	}

	private void sendPing(ChannelHandlerContext context) {
		Message protoMessage = ProtoBufUtils.serializeMessage(new MessageBuilder(null, null).build());
		ChannelFuture future = context.writeAndFlush(protoMessage);
		future.addListener(new ChannelFutureListener() {

			@Override
			public void operationComplete(ChannelFuture future) {
				if (!future.isSuccess()) {
					Log.log(Level.SEVERE,
							"A Connection has been disconnected after PING: "
									+ future.toString()
									+ ", exiting...");
					BotWrapper.shutdown();
				}
			}
		});
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		Log.log(Level.SEVERE,
						"Communications have broken down on a Connection due to Exception",
						cause);
		BotWrapper.shutdown();
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) {
		Log.log(Level.SEVERE,
						"Communications have broken down on a Connection due to Channel Deactivation");
		BotWrapper.shutdown();
	}

	@Override
	public void channelUnregistered(ChannelHandlerContext ctx) {
		Log.log(Level.SEVERE,
						"Communications have broken down on a Connection due to Channel Deregistration");
		BotWrapper.shutdown();
	}
}
