
import frame.MainFrame;
import network.Network;

/**
 * 一定時間毎に状態を再送信するクラスです。
 * @author zenjiro
 * 作成日：2004/11/12
 */
public class SendStatusThread extends Thread {
    /**
     * メインフレーム
     */
    private MainFrame frame;

    /**
     * ネットワーク
     */
    private Network network;
    
    /**
     * コンストラクタです。
     * @param frame メインフレーム
     * @param network ネットワーク
     */
    public SendStatusThread(MainFrame frame, Network network){
        this.frame=frame;
        this.network=network;
    }
    
    public void run() {
        while (true) {
            try {
                this.network.sendStatus(this.frame.getStatus(), this.frame.isOpen());
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}