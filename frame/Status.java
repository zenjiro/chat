package frame;

/**
 * あるユーザの状態とメインフレームが表示されているかどうかをカプセル化するクラスです。
 * @author zenjiro
 * 作成日：2004/11/12
 */
public class Status {
	
	/**
	 * 名前
	 */
	private String name;
	
    /**
     * 状態
     */
    private String status;

    /**
     * メインフレームが表示されているかどうか
     */
    private boolean isOpen;

    /**
     * 状態とメインフレームが表示されているかどうかを指定して、初期化します。
     * @param name 名前
     * @param status 状態
     * @param isOpen 表示されているかどうか
     */
    public Status(final String name, final String status, final boolean isOpen) {
    	this.name = name;
        this.status = status;
        this.isOpen = isOpen;
    }

    /**
     * メインフレームが表示されているかどうかを取得します。
     * @return isOpen 表示されているかどうか
     */
    public boolean isOpen() {
        return this.isOpen;
    }

    /**
     * 状態を取得します。
     * @return status 状態
     */
    public String getStatus() {
        return this.status;
    }
    
    /**
     * 名前を取得します。
     * @return 名前
     */
    public String getName() {
    	return this.name;
    }
}