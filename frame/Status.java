package frame;

/**
 * あるユーザの状態とメインフレームが表示されているかどうかをカプセル化するクラスです。
 * @author zenjiro
 * 作成日：2004/11/12
 */
public class Status {
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
     * @param status 状態
     * @param isOpen 表示されているかどうか
     */
    public Status(String status, boolean isOpen) {
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
}