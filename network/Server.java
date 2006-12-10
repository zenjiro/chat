package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * サーバです。
 * @author zenjiro
 */
public class Server {
	/**
	 * シングルトンのコンストラクタです。
	 */
	private Server() {
		this.listeners = new HashSet<NetworkListener>();
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					try {
						final ServerSocket serverSocket = new ServerSocket(Network.PORT);
						final Socket socket = serverSocket.accept();
						final Scanner scanner = new Scanner(socket.getInputStream());
						while (scanner.hasNextLine()) {
							final String line = scanner.nextLine();
							for (final NetworkListener listener : Server.this.listeners) {
								listener.gotMessage(line);
							}
						}
						scanner.close();
						socket.close();
						serverSocket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	/**
	 * インスタンス
	 */
	private static Server server;

	/**
	 * @return インスタンス
	 */
	public static Server getServer() {
		if (server == null) {
			server = new Server();
		}
		return server;
	}

	/**
	 * リスナの一覧
	 */
	private Set<NetworkListener> listeners;

	/**
	 * リスナを追加します。
	 * @param listener リスナ
	 */
	public void addNetworkListener(final NetworkListener listener) {
		this.listeners.add(listener);
	}

}
