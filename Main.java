import icon.ChatIcon;
import icon.ChatIconListener;

import java.net.InetAddress;
import java.net.UnknownHostException;

import network.NetworkListener;
import network.Server;
import frame.MainFrame;
import frame.MainFrameListener;

/**
 * チャットクライアントです。
 * @author zenjiro
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
        final MainFrame frame = new MainFrame(name, defaultStatus);
        final ChatIcon icon = new ChatIcon();
        frame.setFrameListener(new MainFrameListener() {
            public void messageSent(String message) {
                if (message.length() > 0) {
                }
            }

            public void statusChanged() {
            }

            public void appeared() {
            }

            public void disappeared() {
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
        new ChangeIconThread(icon).start();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
            }
        });
        Server.getServer().addNetworkListener(new NetworkListener() {
			public void gotMessage(final String message) {
				frame.append(message);
			}
		});
        icon.waitForDisposed();
    }

    /**
     * しばらく会話がないときに、アイコンを戻すクラスです。
     * @author zenjiro
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