
import icon.ChatIcon;
import icon.ChatIconListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;
import network.Network;
import network.NetworkListener;
import frame.MainFrameListener;
import frame.MainFrame;

/**
 * UDPマルチキャストを用いたチャットクライアントです。
 * 2004/11/11	0.1	初版です。
 * 2004/11/11	0.2	frameパッケージのスタブを作りました。
 * 2004/11/11	0.3	iconパッケージのスタブを作りました。
 * 2004/11/11	0.4	networkパッケージのスタブを作りました。
 * 2004/11/11	0.5	システムトレイアイコンの実装を作りました。
 * 2004/11/11	0.6	システムトレイアイコンのリスナが正しく動作するようにしました。
 * 2004/11/11	0.7	メインフレームの実装を作りました。（途中）
 * 2004/11/12	0.8	メインフレームの実装を作りました。（途中）
 * 2004/11/12	0.9	メインフレームの実装を作りました。
 * 2004/11/12	1.0	ネットワークの実装を作りました。
 * 2004/11/12	1.1	コマンドラインからユーザ名を指定できるようにしました。
 * 2004/11/12	1.2	一定時間毎に状態を送信し続けるようにしました。
 * 2004/11/12	1.3	メッセージの後ろに時間を付加するようにしました。
 * 2004/11/12	1.4	起動時にメッセージを送信するようにしました。
 * 2004/11/12	1.5	終了時にメッセージを送信するようにしました。
 * 2004/11/12	1.6	状態の初期値をホスト名にしました。
 * 2004/11/12	1.7	メッセージを受け取ったときに、アイコンを変更するようにしました。
 * 2004/11/13	1.8	アイコンをクリックしたときの挙動を変更しました。
 * 2004/11/13	1.9	起動直後にテキストボックスにフォーカスを与えるようにしました。
 * 2004/11/13	2.0	アイコンを変更しました。
 * @author Kumano Tatsuo
 * 作成日：2004/11/11
 */
public class Main {
    /**
     * 最後に会話が行われた時刻
     */
    static long lastTalkingTime;

    /**
     * アイコンを誰も話していない状態に戻すまでの待ち時間
     */
    public static final int TIME_TO_NO_ONE_TALKING = 10000;

    /**
     * メインメソッドです。
     * 実行するには、以下のVMオプションをつけて下さい。
     * -Djava.library.path=/usr/local/eclipse3/eclipse/plugins/org.eclipse.swt.gtk_3.1.0/os/linux/x86/
     * @param args コマンドライン引数
     * @throws UnknownHostException
     */
    public static void main(String[] args) throws UnknownHostException {
        String userName = System.getProperty("user.name", "");
        String defaultStatus = InetAddress.getLocalHost().getHostName();
        if (args.length > 0) {
            for (int i = 0; i < args.length; ++i) {
                if (args[i].equals("-u")) {
                    userName = args[++i];
                } else if (args[i].equals("-s")) {
                    defaultStatus = args[++i];
                } else {
                    System.err.println("使い方：java Main [-u username] [-s status]");
                    System.exit(1);
                }
            }
        }
        final String name = userName;
        final Network network = new Network(name);
        final MainFrame frame = new MainFrame(name, defaultStatus);
        final ChatIcon icon = new ChatIcon();
        network.setNetworkListener(new NetworkListener() {
            public void messageReceived(String message) {
                Calendar calender = Calendar.getInstance();
                DateFormat format = new SimpleDateFormat("HH:mm");
                //frame.append(format.format(calender.getTime()) + "\t" + message); 
                frame.append(message);
                StringTokenizer tokeinizer = new StringTokenizer(message);
                if (tokeinizer.countTokens() > 1) {
                    if (!tokeinizer.nextToken().equals(name)) {
                        lastTalkingTime = System.currentTimeMillis();
                        if (message.indexOf(name) > 0) {
                            icon.setIcon(ChatIcon.ICON_CALLED_ME);
                        } else {
                            if (icon.getIcon() == ChatIcon.ICON_NO_ONE_TALKING) {
                                icon.setIcon(ChatIcon.ICON_OTHERS_TALKING);
                            }
                        }
                    }
                }
            }

            public void statusReceived(String user, String status, boolean isOpen) {
                frame.setStatus(user, status, isOpen);
            }
        });
        frame.setFrameListener(new MainFrameListener() {
            public void messageSent(String message) {
                if (message.length() > 0) {
                    network.sendMessage(message);
                }
            }

            public void statusChanged() {
                network.sendStatus(frame.getStatus(), frame.isOpen());
            }

            public void appeared() {
                network.sendStatus(frame.getStatus(), frame.isOpen());
            }

            public void disappeared() {
                network.sendStatus(frame.getStatus(), frame.isOpen());
            }
        });
        icon.setListener(new ChatIconListener() {
            public void clicked() {
                if (frame.isOpen()) {
                    frame.hide();
                } else {
                    frame.show();
                    if (icon.getIcon() == ChatIcon.ICON_CALLED_ME) {
                        if (System.currentTimeMillis() - lastTalkingTime > TIME_TO_NO_ONE_TALKING) {
                            icon.setIcon(ChatIcon.ICON_NO_ONE_TALKING);
                        } else {
                            icon.setIcon(ChatIcon.ICON_OTHERS_TALKING);
                        }
                    }
                }
            }
        });
        new SendStatusThread(frame, network).start();
        new ChangeIconThread(icon).start();
        network.connect();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                network.sendStatus(null, false);
                network.disconnect();
            }
        });
        icon.waitForDisposed();
    }

    /**
     * しばらく会話がないときに、アイコンを戻すクラスです。
     * @author Kumano Tatsuo
     * 作成日：2004/11/12
     */
    static class ChangeIconThread extends Thread {
        /**
         * ネットワーク
         */
        private ChatIcon icon;

        /**
         * コンストラクタです。
         * @param icon アイコン
         */
        public ChangeIconThread(ChatIcon icon) {
            this.icon = icon;
        }

        public void run() {
            while (true) {
                try {
                    Thread.sleep(1000);
                    if (System.currentTimeMillis() - lastTalkingTime > TIME_TO_NO_ONE_TALKING) {
                        if (this.icon.getIcon() == ChatIcon.ICON_OTHERS_TALKING) {
                            this.icon.setIcon(ChatIcon.ICON_NO_ONE_TALKING);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}