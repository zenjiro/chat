package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

/**
 * ネットワークに関する処理を行うクラスです。
 * @author zenjiro
 * 作成日：2004/11/11
 */
public class Network implements Runnable {
    /**
     * ポート
     */
    public static final int ECHO_PORT = 10007;

    /**
     * パケットの大きさ
     */
    public static final int PACKET_SIZE = 1024;

    /**
     * マルチキャストを行うIPアドレス
     */
    public static final String MCAST_ADDRESS = "224.0.1.1";

    /**
     * ユーザ名
     */
    private String user;

    /**
     * イベントリスナ
     */
    private NetworkListener listener;

    /**
     * ソケット
     */
    private MulticastSocket socket;

    /**
     * コンストラクタです。
     * @param user ユーザ名
     */
    public Network(String user) {
        this.user = user;
        try {
            this.socket = new MulticastSocket(ECHO_PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread(this).start();
    }

    /**
     * ネットワークに接続します。
     */
    public void connect() {
        sendMessage("ログインしました。");
    }

    /**
     * ネットワークへの接続を終了します。 
     */
    public void disconnect() {
        sendMessage("ログアウトしました。");
    }

    /**
     * メッセージを送信します。
     * @param message メッセージ
     */
    public void sendMessage(String message) {
        try {
            InetAddress mcastAddress = InetAddress.getByName(MCAST_ADDRESS);
            byte[] bytes = (this.user + "\t" + message).getBytes();
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, mcastAddress, ECHO_PORT);
            this.socket.send(packet);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 状態を送信します。
     * @param status 状態
     * @param isOpen メインフレームが表示されているかどうか
     */
    public void sendStatus(String status, boolean isOpen) {
        try {
            InetAddress mcastAddress = InetAddress.getByName(MCAST_ADDRESS);
            byte[] bytes = ("\\status\t" + this.user + "\t" + status + "\t" + isOpen).getBytes();
            DatagramPacket packet = new DatagramPacket(bytes, bytes.length, mcastAddress, ECHO_PORT);
            this.socket.send(packet);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * イベントリスナを設定します。
     * @param listener リスナ
     */
    public void setNetworkListener(NetworkListener listener) {
        this.listener = listener;
    }

    public void run() {
        byte[] buf = new byte[PACKET_SIZE];
        DatagramPacket packet = new DatagramPacket(buf, buf.length);
        try {
            InetAddress mcastAddress = InetAddress.getByName(MCAST_ADDRESS);
            this.socket.joinGroup(mcastAddress);
            while (true) {
                this.socket.receive(packet);
                String message = new String(buf, 0, packet.getLength());
                if (this.listener != null) {
                    if (message.indexOf('\\') == 0) {
                        StringTokenizer tokenizer = new StringTokenizer(message);
                        if (tokenizer.countTokens() > 3) {
                            tokenizer.nextToken();
                            this.listener.statusReceived(tokenizer.nextToken(), tokenizer
                                    .nextToken(), Boolean.parseBoolean(tokenizer.nextToken()));
                        }
                    } else {
                        this.listener.messageReceived(message);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}