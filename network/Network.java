package network;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

/**
 * ネットワーク関連のユーティリティクラスです。
 * @author zenjiro
 */
public class Network {

	/**
	 * ポート番号
	 */
	public static final int PORT = 64820;

	/**
	 * @return ホスト名の一覧
	 */
	public static Set<String> getAddresses() {
		final Set<String> ret = new HashSet<String>();
		// test
		ret.add("zenjiro.dyndns.org");
		return ret;
	}

	/**
	 * メッセージを送信します。
	 * @param address ホスト名
	 * @param message メッセージ
	 * @throws IOException
	 */
	public static void sendMessage(final String address, final String message) throws IOException {
		send(address, "message", message);
	}

	/**
	 * 状態を送信します。
	 * @param address ホスト名
	 * @param name 名前
	 * @param status 状態
	 * @throws IOException
	 */
	public static void sendStatus(final String address, final String name, final String status) throws IOException {
		send(address, "status", name + "\t" + status);
	}

	/**
	 * データを送信します。
	 * @param address ホスト名
	 * @param command コマンド
	 * @param data データ
	 * @throws IOException 
	 */
	private static void send(final String address, final String command, final String data) throws IOException {
		final Socket socket = new Socket(address, Network.PORT);
		final PrintWriter out = new PrintWriter(socket.getOutputStream());
		out.println(command + "\t" + data);
		out.close();
		socket.close();
	}
}
