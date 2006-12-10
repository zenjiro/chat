package frame;

/**
 * メインフレームでのイベントを処理するリスナです。
 * @author Kumano Tatsuo
 * 作成日：2004/11/11
 */
public interface MainFrameListener {
    /**
     * メッセージが入力し終わったときに呼び出されます。
     * @param message メッセージ
     */
    public void messageSent(String message);

    /**
     * 状態が変化したときに呼び出されます。
     */
    public void statusChanged();
    
    /**
     * メインフレームが表示されたときに呼び出されます。
     */
    public void appeared();

    /**
     * メインフレームが非表示にされたときに呼び出されます。
     */
    public void disappeared();
}