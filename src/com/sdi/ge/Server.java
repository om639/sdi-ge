package com.sdi.ge;

import com.sdi.ge.net.ConnectionHandler;
import com.sdi.ge.net.InboundPacketDecoder;
import com.sdi.ge.net.OutboundPacketEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Created by Oliver on 18/02/2016.
 */
public class Server {

    /**
     * The default port to listen on.
     */
    private static final int DEFAULT_PORT = 5577;

    /**
     * The default number of players to use.
     */
    private static final int DEFAULT_NUM_PLAYERS = 2;

    /**
     * Port to run the server on.
     */
    private int port;

    /**
     * The game engine in which the game loop runs.
     */
    private final Engine engine;

    /**
     * Connection handler.
     */
    private final ConnectionHandler connectionHandler;

    /**
     * Netty event loops.
     */
    private EventLoopGroup bossGroup, workerGroup;


    /**
     * Initialises the Server with the specified port.
     *
     * @param port the port to listen for connections on
     */
    public Server(int port, int numPlayers) {
        this.port = port;

        engine = new Engine(this, numPlayers);

        connectionHandler = new ConnectionHandler(engine);
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
    }

    /**
     * Starts listening for connections.
     */
    public void run() {
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();

                            pipeline.addLast(new InboundPacketDecoder());
                            pipeline.addLast(new OutboundPacketEncoder());

                            pipeline.addLast(connectionHandler);
                        }
                    });

            System.out.println("Starting SDI server on port " + port);

            engine.start();
            b.bind(port).sync().channel().closeFuture().sync();
        } catch (Exception ex) {
            System.err.println("Could not start server: " + ex.getMessage());
        } finally {
            shutdown();
        }
    }

    /**
     * Shuts down the server..
     */
    public void shutdown() {
        engine.stop();

        workerGroup.shutdownGracefully();
        bossGroup.shutdownGracefully();
    }

    public static void main(String[] args) throws Exception {
        System.out.println("SDI Multiplayer Alpha Server");
        System.out.println("Created by Oliver McClellan");

        int port = DEFAULT_PORT;
        int numPlayers = DEFAULT_NUM_PLAYERS;

        // Get port from command line parameters.
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);

            // Get number of players from command line parameters.
            if (args.length >= 2) {
                numPlayers = Integer.parseInt(args[1]);
            }
        }

        new Server(port, numPlayers).run();
    }
}
