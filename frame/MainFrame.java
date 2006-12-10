package frame;

import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * チャットを行うフレームです。
 * @author zenjiro
 * 作成日：2004/11/11
 */
public class MainFrame {
    /**
     * リスナ
     */
    MainFrameListener listener;

    /**
     * シェル
     */
    Shell shell;

    /**
     * ユーザ名
     */
    private String user;

    /**
     * 状態
     */
    String status;

    /**
     * メインフレームが表示されているかどうか
     */
    private boolean isOpen;

    /**
     * 今までの発言
     */
    private StringBuilder log;

    /**
     * 他人の状態
     */
    private Map<String, Status> othersStatus;

    /**
     * 以前の書き込みを表示するテキスト
     */
    Text logText;

    /**
     * 他人の状態を表示するテーブル
     */
    Table othersTable;

    /**
     * 自分の状態を編集するテキスト
     */
    Text statusText;

    /**
     * 追加されたメッセージ
     */
    private String message;

    /**
     * コンストラクタです。
     * @param user ユーザ名
     * @param status 状態
     */
    public MainFrame(final String user, final String status) {
        this.user = user;
        this.status = status;
    }

    /**
     * メインフレームを表示します。
     */
    public void show() {
        if (this.shell == null) {
            this.shell = new Shell(Display.getDefault());
            this.shell.setText("チャットクライアント");
            this.shell.setLayout(new FillLayout());
            final TraverseListener traverseListener = new TraverseListener() {
                public void keyTraversed(TraverseEvent event) {
                    if (event.detail == SWT.TRAVERSE_ESCAPE) {
                        hide();
                    }
                }
            };
            this.shell.addFocusListener(new FocusAdapter() {
                @Override
				public void focusGained(final FocusEvent arg0) {
                    MainFrame.this.shell.traverse(SWT.TRAVERSE_TAB_NEXT);
                }
            });
            final SashForm sash = new SashForm(this.shell, SWT.NONE);
            sash.SASH_WIDTH = 10;
            arrangeLeft(traverseListener, sash);
            arrangeRight(traverseListener, sash);
            sash.setWeights(new int[] { 2, 1 });
            this.shell.setSize(640, 480);
            this.shell.addShellListener(new ShellAdapter() {
                @Override
				public void shellClosed(final ShellEvent arg0) {
                    hide();
                    MainFrame.this.shell.dispose();
                    MainFrame.this.shell = null;
                }

                @Override
				public void shellIconified(final ShellEvent arg0) {
                    if (isOpen()) {
                        hide();
                    }
                }
            });
        }
        this.isOpen = true;
        this.shell.open();
        this.listener.appeared();
    }

    /**
     * 書き込みが表示される部分のコンポーネントを配置します。
     * @param traverseListener Escキーを監視するリスナ
     * @param sash サッシ
     */
    private void arrangeLeft(final TraverseListener traverseListener, final SashForm sash) {
        final Composite composite = new Composite(sash, SWT.NONE);
        composite.setLayout(new FormLayout());
        this.logText = new Text(composite, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER
                | SWT.READ_ONLY);
        this.logText.setTabs(16);
        this.logText.addTraverseListener(traverseListener);
        final Text inputText = new Text(composite, SWT.SINGLE | SWT.BORDER);
        inputText.addTraverseListener(traverseListener);
        inputText.addTraverseListener(new TraverseListener() {
            public void keyTraversed(final TraverseEvent event) {
                if (event.detail == SWT.TRAVERSE_RETURN) {
                    MainFrame.this.listener.messageSent(inputText.getText());
                    inputText.setText("");
                }
            }
        });
        inputText.addFocusListener(new FocusAdapter() {
            @Override
			public void focusLost(final FocusEvent arg0) {
                inputText.setSelection(0);
            }
        });
        inputText.setFocus();
        final FormData formData1 = new FormData();
        formData1.left = new FormAttachment(0, 10);
        formData1.right = new FormAttachment(100, 0);
        formData1.bottom = new FormAttachment(100, -10);
        inputText.setLayoutData(formData1);
        final FormData formData2 = new FormData();
        formData2.left = new FormAttachment(0, 10);
        formData2.right = new FormAttachment(100, 0);
        formData2.top = new FormAttachment(0, 10);
        formData2.bottom = new FormAttachment(inputText, 0);
        this.logText.setLayoutData(formData2);
        if (this.log != null) {
            this.logText.setText(this.log.toString());
            this.logText.append("");
        }
    }

    /**
     * ユーザの一覧が表示される部分のコンポーネントを配置します。
     * @param traverseListener Escキーを監視するリスナ
     * @param sash サッシ
     */
    private void arrangeRight(final TraverseListener traverseListener, final SashForm sash) {
        final Composite composite = new Composite(sash, SWT.NONE);
        composite.setLayout(new FormLayout());
        this.othersTable = new Table(composite, SWT.MULTI);
        this.othersTable.setBackground(Display.getDefault().getSystemColor(
                SWT.COLOR_WIDGET_BACKGROUND));
        updateStatus();
        final Label label = new Label(composite, SWT.NONE);
        label.setText("@");
        label.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        this.statusText = new Text(composite, SWT.NONE);
        this.statusText.setText(this.status);
        this.statusText.setForeground(Display.getDefault().getSystemColor(
                SWT.COLOR_WIDGET_BACKGROUND));
        this.statusText.setBackground(Display.getDefault().getSystemColor(
                SWT.COLOR_WIDGET_BACKGROUND));
        this.statusText.addTraverseListener(traverseListener);
        this.statusText.addTraverseListener(new TraverseListener() {
            public void keyTraversed(final TraverseEvent event) {
                if (event.detail == SWT.TRAVERSE_RETURN) {
                    MainFrame.this.status = MainFrame.this.statusText.getText();
                    MainFrame.this.listener.statusChanged();
                }
            }
        });
        this.statusText.addFocusListener(new FocusListener() {
            public void focusLost(final FocusEvent arg0) {
                MainFrame.this.statusText.setSelection(0);
                label.setForeground(Display.getDefault()
                        .getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
                MainFrame.this.statusText.setForeground(Display.getDefault().getSystemColor(
                        SWT.COLOR_WIDGET_BACKGROUND));
                MainFrame.this.statusText.setBackground(Display.getDefault().getSystemColor(
                        SWT.COLOR_WIDGET_BACKGROUND));
            }

            public void focusGained(final FocusEvent arg0) {
                label.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_LIST_FOREGROUND));
                MainFrame.this.statusText.setForeground(Display.getDefault().getSystemColor(
                        SWT.COLOR_LIST_FOREGROUND));
                MainFrame.this.statusText.setBackground(Display.getDefault().getSystemColor(
                        SWT.COLOR_LIST_BACKGROUND));
            }
        });
        final FormData labelForm = new FormData();
        labelForm.left = new FormAttachment(0, 0);
        labelForm.top = new FormAttachment(this.statusText, 0, SWT.CENTER);
        label.setLayoutData(labelForm);
        final FormData statusForm = new FormData();
        statusForm.left = new FormAttachment(label, 0);
        statusForm.right = new FormAttachment(100, -10);
        statusForm.bottom = new FormAttachment(100, -10);
        this.statusText.setLayoutData(statusForm);
        final FormData othersForm = new FormData();
        othersForm.left = new FormAttachment(0, 0);
        othersForm.right = new FormAttachment(100, -10);
        othersForm.top = new FormAttachment(0, 10);
        othersForm.bottom = new FormAttachment(this.statusText, -10);
        this.othersTable.setLayoutData(othersForm);
    }

    /**
     * メインフレームを非表示にします。 
     */
    public void hide() {
        this.isOpen = false;
        this.shell.setVisible(false);
        this.listener.disappeared();
    }

    /**
     * メインフレームが表示されているかどうかを取得します。
     * @return 表示されているかどうか
     */
    public boolean isOpen() {
        if (this.shell == null) {
            return false;
        } else {
            return this.isOpen;
        }
    }

    /**
     * メッセージを追加します。
     * @param newMessage メッセージ
     */
    public void append(final String newMessage) {
        while (this.message != null) {
            try {
                Thread.sleep(100);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.message = newMessage;
        final Display display = Display.getDefault();
        display.asyncExec(new UpdateThread());
    }

    /**
     * 追加されたメッセージがあるかどうかを調べ、あればログに追加します。
     */
    void updateMessage() {
        if (this.message != null) {
            if (this.log == null) {
                this.log = new StringBuilder(this.message);
                if (this.shell != null) {
                    if (this.logText != null) {
                        this.logText.setText(this.message);
                    }
                }
            } else {
                this.log.append("\n" + this.message);
                if (this.shell != null) {
                    if (this.logText != null) {
                        this.logText.append("\n" + this.message);
                    }
                }
            }
        }
        this.message = null;
    }

    /**
     * あるユーザの状態を変更します。
     * @param address ホスト名
     * @param user ユーザ
     * @param status 状態
     * @param isOpen メインフレームを開いているかどうか
     */
    public void setStatus(final String address, final String user, final String status, final boolean isOpen) {
        if (this.othersStatus == null) {
            this.othersStatus = new TreeMap<String, Status>();
        }
        if (!status.equals("null")) {
            this.othersStatus.put(address, new Status(user, status, isOpen));
        } else {
            this.othersStatus.remove(address);
        }
        final Display display = Display.getDefault();
        display.asyncExec(new UpdateThread());
    }

    /**
     * 他人の状態を更新します。
     */
    void updateStatus() {
        if (this.shell != null) {
            if (this.othersTable != null) {
                this.othersTable.removeAll();
                if (this.othersStatus != null) {
                    for (final Map.Entry<String, Status> entry : this.othersStatus.entrySet()) {
                        final TableItem tableItem = new TableItem(this.othersTable, SWT.NONE);
                        tableItem.setText(entry.getValue().getName() + "@" + entry.getValue().getStatus());
                        if (entry.getValue().isOpen()) {
                            tableItem.setImage(new Image(Display.getDefault(), "img/bubble_yellow.gif"));
                        } else {
                            tableItem.setImage(new Image(Display.getDefault(),
                                    "img/bubble_gray.gif"));
                        }
                    }
                }
            }
        }
    }

    /**
     * 状態を取得します。
     * @return 状態
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * イベントを監視するリスナを追加します。
     * @param listener リスナ
     */
    public void setFrameListener(final MainFrameListener listener) {
        this.listener = listener;
    }

    /**
     * 追加されたメッセージがあるかどうかを非同期に呼び出すためのクラスです。
     * @author zenjiro
     * 作成日：2004/11/12
     */
    class UpdateThread implements Runnable {
        public void run() {
            updateMessage();
            updateStatus();
        }
    }
}