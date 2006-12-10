package network;

/**
 * ネットワーク関係のイベントを処理するリスナです。
 * @author zenjiro
 * 作成日：2004/11/11
 */
public interface NetworkListener {
    /**
     * メッセージを受け取ったときに呼び出されます。
     * @param message メッセージ
     */
    public void messageReceived(String message);
    
    /**
     * あるユーザの状態を受け取ったときに呼び出されます。
     * @param user ユーザ名
     * @param status 状態
     * @param isOpen メインフレームが表示されているかどうか
     */
    public void statusReceived(String user, String status, boolean isOpen);
}
