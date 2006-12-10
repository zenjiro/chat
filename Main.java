
import icon.ChatIcon;
import icon.ChatIconListener;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * UDPマルチキャストを用いたチャットクライアントです。
 * @author zenjiro
 * 作成日：2004/11/11
 */
public class Main {
    /**
     * メインメソッドです。
     * 実行するには、以下のコマンドを実行してください。
     * java -classpath swt-M20060921-0945-gtk-linux-x86/swt.jar:. -Djava.library.path=swt-M20060921-0945-gtk-linux-x86/ Main
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
        final ChatIcon icon = new ChatIcon();
        icon.setListener(new ChatIconListener() {
            public void clicked() {
            	System.out.println("clicked");
            }
        });
        icon.waitForDisposed();
    }
}