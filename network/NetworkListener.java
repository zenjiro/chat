package network;

/**
 * メッセージを受け取ったときに通知するためのリスナです。
 * @author zenjiro
 */
public interface NetworkListener {
	/**
	 * メッセージを受け取ったときに呼び出されます。
	 * @param message メッセージ
	 */
	public void gotMessage(final String message);
}
