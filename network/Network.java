package network;

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
		ret.add("localhost");
		return ret;
	}
}
