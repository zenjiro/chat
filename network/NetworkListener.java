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

	/**
	 * 名前を問い合わせられたときに呼び出されます。
	 * @return 名前
	 */
	public String queryName();
	
	/**
	 * 状態を受け取ったときに呼び出されます。
	 * @param address ホスト名
	 * @param name 名前
	 * @param status 状態
	 */
	public void gotStatus(final String address, final String name, final String status);
}
