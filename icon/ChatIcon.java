package icon;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

/**
 * システムトレイに表示されるアイコンに関する処理をするクラスです。
 * @author zenjiro
 * 作成日：2004/11/11
 */
public class ChatIcon {
    /**
     * 誰も話をしていないことを表すアイコンです。
     */
    public static final int ICON_NO_ONE_TALKING = 0;

    /**
     * 他人が話をしていることを表すアイコンです。
     */
    public static final int ICON_OTHERS_TALKING = 1;

    /**
     * 自分が誰かに話しかけられたことを表すアイコンです。
     */
    public static final int ICON_CALLED_ME = 2;

    /**
     * ディスプレイ
     */
    Display display;

    /**
     * システムトレイ
     */
    private Tray tray;

    /**
     * システムトレイアイテム
     */
    TrayItem trayItem;

    /**
     * リスナ
     */
    ChatIconListener listener;

    /**
     * 現在のアイコン
     */
    int icon;
    
    /**
     * コンストラクタです。
     */
    public ChatIcon() {
        this.display = Display.getDefault();
        this.tray = this.display.getSystemTray();
        this.trayItem = new TrayItem(this.tray, SWT.NONE);
        this.trayItem.setToolTipText("チャットクライアント");
        this.trayItem.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                if (ChatIcon.this.listener != null) {
                    ChatIcon.this.listener.clicked();
                }
            }
        });
        setIcon(ICON_NO_ONE_TALKING);
    }

    /**
     * 破壊されるのを待ちます。
     */
    public void waitForDisposed() {
        while (!this.tray.isDisposed()) {
            if (!this.display.readAndDispatch()) {
                this.display.sleep();
            }
        }
    }
    
    /**
     * アイコンを設定します。
     * @param icon アイコンを表す定数
     */
    public void setIcon(int icon) {
        this.icon = icon;
        Display.getDefault().asyncExec(new UpdateThread());
    }

    /**
     * 現在設定されているアイコンを表す定数を取得します。
     * @return アイコンを表す定数
     */
    public int getIcon(){
        return this.icon;
    }
    
    /**
     * イベントリスナを設定します。
     * @param listener リスナ
     */
    public void setListener(ChatIconListener listener){
        this.listener = listener;
    }
    
    /**
     * アイコンを非同期で更新するクラスです。
     * @author zenjiro
     * 作成日：2004/11/12
     */
    class UpdateThread implements Runnable {
        public void run() {
            switch (ChatIcon.this.icon) {
            case ICON_NO_ONE_TALKING:
                ChatIcon.this.trayItem.setImage(new Image(ChatIcon.this.display, "img/bubble_gray.gif"));
                break;
            case ICON_OTHERS_TALKING:
                ChatIcon.this.trayItem.setImage(new Image(ChatIcon.this.display, "img/bubble_yellow.gif"));
                break;
            case ICON_CALLED_ME:
                ChatIcon.this.trayItem.setImage(new Image(ChatIcon.this.display, "img/bubble_red.gif"));
                break;
            default:
                throw new IllegalArgumentException("そのようなアイコンは指定できません。");
            }
        }
    }

}