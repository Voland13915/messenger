package messenger.ui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import messenger.abstractfactory.*;
import messenger.adapter.*;
import messenger.chain.*;
import messenger.command.*;
import messenger.composite.*;
import messenger.decorator.*;
import messenger.facade.MessengerFacade;
import messenger.observer.MessengerObserver;
import messenger.observer.ObserverEvent;
import messenger.proxy.ImageLoaderProxy;
import messenger.singleton.WebSocketManager;
import messenger.state.*;
import messenger.strategy.*;

import org.json.JSONObject;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MessengerWindow — главное окно приложения.
 *
 * Реализует паттерны поведения:
 *   Observer  — implements MessengerObserver, подписывается на WebSocketManager
 *   Command   — все отправки идут через CommandInvoker
 *   Chain     — validationChain проверяет сообщение перед отправкой
 *   Strategy  — messageSender выбирает алгоритм отправки
 *   State     — stateManager отслеживает состояние каждого сообщения
 */
public class MessengerWindow extends Application implements MessengerObserver {

    // ── Abstract Factory ──────────────────────────────────────────────
    private UIFactory currentFactory = new LightThemeFactory();
    private boolean isDark = false;

    // ── Singleton / ConcreteSubject (Observer) ────────────────────────
    private final WebSocketManager wsManager = WebSocketManager.getInstance();

    // ── Facade ────────────────────────────────────────────────────────
    private final MessengerFacade facade = new MessengerFacade();

    // ── Adapter ───────────────────────────────────────────────────────
    private final Notifier notifier = new EmailNotifierAdapter(
            new ExternalEmailService(), "user@example.com");

    // ── Command: Invoker хранит историю команд ────────────────────────
    private final CommandInvoker invoker = new CommandInvoker();

    // ── Chain of Responsibility: цепочка валидации ────────────────────
    private final MessageValidator validationChain = ValidationChainFactory.build();

    // ── Strategy: Context делегирует алгоритм отправки ────────────────
    private final MessageSender messageSender = new MessageSender();

    // ── State: менеджер состояний исходящих сообщений ─────────────────
    private final MessageStateManager stateManager = new MessageStateManager();

    // ── observerState ─────────────────────────────────────────────────
    private final Map<String, ObservableList<ChatMessage>> chatHistory = new HashMap<>();
    private String selectedChat = null;

    // ── Выделение ─────────────────────────────────────────────────────
    private final Set<ChatMessage>       selectedMessages = new LinkedHashSet<>();
    private final Map<ChatMessage, VBox> selectedBubbles  = new HashMap<>();

    // ── Цитата ────────────────────────────────────────────────────────
    private ChatMessage quotedMessage = null;
    private HBox        quoteBar      = null;

    // ── Decorator ─────────────────────────────────────────────────────
    private boolean isImportant = false;
    private boolean isEncrypted = false;

    // ── Попапы ────────────────────────────────────────────────────────
    private javafx.stage.Popup attachPopup = null;
    private javafx.stage.Popup morePopup   = null;

    // ── UI ────────────────────────────────────────────────────────────
    private VBox       messageArea;
    private ScrollPane messageScroll;
    private TextField  inputField;
    private Label      chatTitleLabel;
    private Label      statusBar;
    private VBox       inputContainer;
    private HBox       contextBar = null;
    private Scene      scene;
    private VBox       chatList;

    // ═════════════════════════════════════════════════════════════════
    @Override
    public void start(Stage stage) {
        showLoginDialog(stage);
    }

    // ── Диалог входа ─────────────────────────────────────────────────
    private void showLoginDialog(Stage stage) {
        Stage loginStage = new Stage();
        loginStage.setTitle("Подключение");
        loginStage.setResizable(false);

        VBox root = new VBox(10);
        root.setPadding(new Insets(24));
        root.setAlignment(Pos.CENTER_LEFT);
        root.setStyle("-fx-background-color: #F7F7F8;");

        Label title = new Label("Messenger");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #7C83FD;");
        title.setPadding(new Insets(0, 0, 12, 0));

        String fs = "-fx-background-radius: 8; -fx-padding: 8; -fx-font-size: 13px; -fx-pref-width: 260;";
        TextField serverField   = new TextField("192.168.1.100"); serverField.setStyle(fs);
        TextField portField     = new TextField("8887");           portField.setStyle(fs);
        TextField usernameField = new TextField();
        usernameField.setPromptText("Например: Alice"); usernameField.setStyle(fs);

        Button connectBtn = new Button("Подключиться");
        connectBtn.setStyle("-fx-background-color: #7C83FD; -fx-text-fill: white; " +
                "-fx-background-radius: 8; -fx-padding: 10 24; -fx-cursor: hand; -fx-font-size: 13px;");
        connectBtn.setMaxWidth(Double.MAX_VALUE);

        Label statusLabel = new Label("");
        statusLabel.setStyle("-fx-text-fill: #E06060; -fx-font-size: 11px;");

        connectBtn.setOnAction(e -> {
            String ip       = serverField.getText().trim();
            String port     = portField.getText().trim();
            String username = usernameField.getText().trim();
            if (ip.isEmpty() || username.isEmpty()) {
                statusLabel.setText("Заполните IP и имя"); return;
            }
            statusLabel.setStyle("-fx-text-fill: #888899; -fx-font-size: 11px;");
            statusLabel.setText("Подключение…");
            connectBtn.setDisable(true);

            // Observer: Attach(this) — регистрируемся до connect()
            wsManager.attach(this);
            wsManager.connect("ws://" + ip + ":" + port, username);

            loginStage.setUserData(new Object[]{ stage, loginStage });
        });

        usernameField.setOnAction(e -> connectBtn.fire());
        root.getChildren().addAll(title,
                new Label("IP сервера:"), serverField,
                new Label("Порт:"),       portField,
                new Label("Ваше имя:"),   usernameField,
                connectBtn, statusLabel);

        loginStage.setScene(new Scene(root, 310, 370));
        loginStage.show();
    }

    // ═════════════════════════════════════════════════════════════════
    // Observer: Update() — ConcreteObserver синхронизируется с субъектом
    // observerState = subject->GetState()
    // ═════════════════════════════════════════════════════════════════
    @Override
    public void update(ObserverEvent event, Object data) {
        switch (event) {

            case CONNECTION_CHANGED: {
                boolean connected = (boolean) data;
                if (connected && scene == null) {
                    javafx.stage.Window loginWindow = javafx.stage.Window.getWindows().stream()
                            .filter(w -> w instanceof Stage
                                    && ((Stage) w).getTitle().equals("Подключение"))
                            .findFirst().orElse(null);
                    if (loginWindow != null) {
                        Object[] ud = (Object[]) ((Stage) loginWindow).getUserData();
                        if (ud != null) {
                            ((Stage) ud[1]).close();
                            launchMainWindow((Stage) ud[0]);
                        }
                    }
                } else if (!connected && statusBar != null) {
                    statusBar.setText("● Отключён");
                }
                break;
            }

            case USER_JOINED: {
                if (data instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<String> users = (List<String>) data;
                    for (String user : users)
                        if (!chatHistory.containsKey(user))
                            chatHistory.put(user, FXCollections.observableArrayList());
                    if (selectedChat == null && !chatHistory.isEmpty())
                        selectedChat = chatHistory.keySet().iterator().next();
                } else {
                    String user = (String) data;
                    if (!chatHistory.containsKey(user))
                        chatHistory.put(user, FXCollections.observableArrayList());
                }
                if (chatList != null) updateChatList();
                if (selectedChat != null && messageArea != null) refreshMessages();
                break;
            }

            case USER_LEFT:
                if (chatList != null) updateChatList();
                break;

            case MESSAGE_RECEIVED:
                handleIncomingMessage((JSONObject) data);
                break;
        }
    }

    // ── Обработка входящего сообщения ─────────────────────────────────
    private void handleIncomingMessage(JSONObject json) {
        try {
            String from     = json.getString("from");
            String text     = json.optString("text", "");
            String msgType  = json.optString("msgType", "TEXT");
            String filePath = json.has("filePath") ? json.getString("filePath") : null;
            String location = json.has("location") ? json.getString("location") : null;
            String to       = json.optString("to", "all");

            boolean isOwn  = from.equals(wsManager.getMyUsername());
            String chatKey = isOwn ? to : from;

            if (!chatHistory.containsKey(chatKey))
                chatHistory.put(chatKey, FXCollections.observableArrayList());

            chatHistory.get(chatKey).add(new ChatMessage(
                    isOwn ? "Вы" : from, text, isOwn,
                    msgType, filePath, null, location, null));

            if (chatKey.equals(selectedChat)) refreshMessages();
            updateChatList();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ── Запуск главного окна ──────────────────────────────────────────
    private void launchMainWindow(Stage stage) {
        BorderPane root = buildRoot();
        scene = new Scene(root, 900, 640);
        applyTheme();
        stage.setTitle("Messenger — " + wsManager.getMyUsername());
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> {
            wsManager.detach(this);   // Observer: Detach при закрытии
            wsManager.disconnect();
        });
        stage.show();
    }

    // ═════════════════════════════════════════════════════════════════
    // ── Построение интерфейса ─────────────────────────────────────────
    // ═════════════════════════════════════════════════════════════════

    private BorderPane buildRoot() {
        BorderPane bp = new BorderPane();
        VBox left = buildLeftPanel(); left.setPrefWidth(240);
        bp.setLeft(left); bp.setCenter(buildRightPanel());
        return bp;
    }

    private VBox buildLeftPanel() {
        VBox panel = new VBox(0); panel.getStyleClass().add("left-panel");

        HBox header = new HBox(); header.getStyleClass().add("left-header");
        header.setAlignment(Pos.CENTER_LEFT); header.setPadding(new Insets(16, 12, 16, 16));
        Label title = new Label("Чаты"); title.getStyleClass().add("left-title");
        HBox.setHgrow(title, Priority.ALWAYS);
        Button themeBtn = new Button("◑"); themeBtn.getStyleClass().add("theme-btn");
        themeBtn.setOnAction(e -> toggleTheme());
        header.getChildren().addAll(title, themeBtn);

        statusBar = new Label("● Подключение…");
        statusBar.getStyleClass().add("status-bar");
        statusBar.setPadding(new Insets(4, 16, 8, 16));

        chatList = new VBox(0);
        ScrollPane scroll = new ScrollPane(chatList);
        scroll.setFitToWidth(true); scroll.getStyleClass().add("chat-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        panel.getChildren().addAll(header, statusBar, scroll);
        return panel;
    }

    private HBox buildChatItem(String name) {
        HBox item = new HBox(10); item.getStyleClass().add("chat-item");
        item.setPadding(new Insets(10, 16, 10, 16)); item.setAlignment(Pos.CENTER_LEFT);
        if (name.equals(selectedChat)) item.getStyleClass().add("chat-item-selected");

        StackPane avatar = new StackPane();
        Circle c = new Circle(20); c.getStyleClass().add("avatar-circle");
        Label ini = new Label(name.substring(0, 1).toUpperCase());
        ini.getStyleClass().add("avatar-initials");
        avatar.getChildren().addAll(c, ini);

        VBox info = new VBox(2);
        Label nameLabel = new Label(name); nameLabel.getStyleClass().add("chat-name");
        ObservableList<ChatMessage> hist = chatHistory.get(name);
        String last = (hist == null || hist.isEmpty()) ? ""
                : hist.get(hist.size() - 1).text;
        if (last.length() > 28) last = last.substring(0, 28) + "…";
        Label lastLabel = new Label(last); lastLabel.getStyleClass().add("chat-last");
        info.getChildren().addAll(nameLabel, lastLabel);
        HBox.setHgrow(info, Priority.ALWAYS);

        item.getChildren().addAll(avatar, info);
        item.setOnMouseClicked(e -> selectChat(name));
        return item;
    }

    private VBox buildRightPanel() {
        VBox panel = new VBox(0); panel.getStyleClass().add("right-panel");

        HBox chatHeader = new HBox(12); chatHeader.getStyleClass().add("chat-header");
        chatHeader.setAlignment(Pos.CENTER_LEFT); chatHeader.setPadding(new Insets(14, 20, 14, 20));
        chatTitleLabel = new Label(selectedChat != null ? selectedChat : "Выберите чат");
        chatTitleLabel.getStyleClass().add("chat-title");
        chatHeader.getChildren().add(chatTitleLabel);

        messageArea = new VBox(8); messageArea.setPadding(new Insets(16));
        messageArea.getStyleClass().add("message-area");

        messageScroll = new ScrollPane(messageArea);
        messageScroll.setFitToWidth(true); messageScroll.getStyleClass().add("message-scroll");
        VBox.setVgrow(messageScroll, Priority.ALWAYS);

        inputContainer = new VBox(0);
        inputContainer.getChildren().add(buildInputPanel());

        panel.getChildren().addAll(chatHeader, messageScroll, inputContainer);
        return panel;
    }

    private HBox buildInputPanel() {
        HBox panel = new HBox(8); panel.getStyleClass().add("input-panel");
        panel.setPadding(new Insets(10, 14, 10, 14)); panel.setAlignment(Pos.CENTER);

        Button attachBtn = new Button("⊕"); attachBtn.getStyleClass().add("attach-btn");
        attachBtn.setOnAction(e -> showAttachMenu(attachBtn));

        inputField = new TextField();
        inputField.setPromptText("Сообщение…"); inputField.getStyleClass().add("input-field");
        HBox.setHgrow(inputField, Priority.ALWAYS);
        inputField.setOnAction(e -> sendMessage());

        Button moreBtn = new Button("⋯"); moreBtn.getStyleClass().add("more-btn");
        moreBtn.setOnAction(e -> showMoreMenu(moreBtn));

        Button sendBtn = new Button("➤"); sendBtn.getStyleClass().add("send-btn");
        sendBtn.setOnAction(e -> sendMessage());

        panel.getChildren().addAll(attachBtn, inputField, moreBtn, sendBtn);
        return panel;
    }

    // ── Попап «⊕» ────────────────────────────────────────────────────
    private void showAttachMenu(javafx.scene.Node anchor) {
        if (attachPopup != null && attachPopup.isShowing()) {
            attachPopup.hide(); attachPopup = null; return;
        }
        attachPopup = new javafx.stage.Popup();
        attachPopup.setAutoHide(true); attachPopup.setHideOnEscape(true);
        VBox menu = new VBox(0); menu.getStyleClass().add("popup-menu");
        menu.getChildren().addAll(
                makePopupItem("🖼", "Фото",             () -> { attachPopup.hide(); sendFileMessage("IMAGE"); }),
                makePopupItem("▶",  "Видео",             () -> { attachPopup.hide(); sendFileMessage("VIDEO"); }),
                makePopupItem("📍", "Геолокация",        () -> { attachPopup.hide(); sendLocationMessage(); }),
                new javafx.scene.layout.Region() {{
                    getStyleClass().add("popup-separator"); setMinHeight(1); setMaxHeight(1);
                }},
                makePopupItem("👥", "Отправить группе",  () -> { attachPopup.hide(); sendToGroup(); }),
                makePopupItem("✉",  "Email-уведомление", () -> { attachPopup.hide(); sendEmailNotification(); })
        );
        attachPopup.getContent().add(menu);
        javafx.geometry.Bounds b = anchor.localToScreen(anchor.getBoundsInLocal());
        attachPopup.show(anchor, b.getMinX(), b.getMinY() - 8);
        menu.layout(); attachPopup.setY(b.getMinY() - menu.getHeight() - 8);
    }

    // ── Попап «⋯» ────────────────────────────────────────────────────
    private void showMoreMenu(javafx.scene.Node anchor) {
        if (morePopup != null && morePopup.isShowing()) {
            morePopup.hide(); morePopup = null; return;
        }
        morePopup = new javafx.stage.Popup();
        morePopup.setAutoHide(true); morePopup.setHideOnEscape(true);
        VBox menu = new VBox(0); menu.getStyleClass().add("popup-menu");
        menu.getChildren().addAll(
                makePopupToggleItem("❗", "Важное",       isImportant, v -> isImportant = v),
                makePopupToggleItem("🔒", "Зашифрованное", isEncrypted, v -> isEncrypted = v)
        );
        morePopup.getContent().add(menu);
        javafx.geometry.Bounds b = anchor.localToScreen(anchor.getBoundsInLocal());
        morePopup.show(anchor, b.getMinX(), b.getMinY() - 8);
        menu.layout(); morePopup.setY(b.getMinY() - menu.getHeight() - 8);
    }

    private HBox makePopupItem(String icon, String label, Runnable action) {
        HBox row = new HBox(10); row.getStyleClass().add("popup-item");
        row.setAlignment(Pos.CENTER_LEFT); row.setPadding(new Insets(7, 16, 7, 14));
        Label ic = new Label(icon); ic.getStyleClass().add("popup-item-icon"); ic.setMinWidth(20);
        Label tx = new Label(label); tx.getStyleClass().add("popup-item-text");
        row.getChildren().addAll(ic, tx);
        row.setOnMouseClicked(e -> action.run());
        row.setOnMouseEntered(e -> row.getStyleClass().add("popup-item-hover"));
        row.setOnMouseExited(e  -> row.getStyleClass().remove("popup-item-hover"));
        return row;
    }

    private HBox makePopupToggleItem(String icon, String label, boolean init,
                                     java.util.function.Consumer<Boolean> onChange) {
        HBox row = new HBox(10); row.getStyleClass().add("popup-item");
        row.setAlignment(Pos.CENTER_LEFT); row.setPadding(new Insets(7, 16, 7, 14));
        Label ic  = new Label(icon);  ic.getStyleClass().add("popup-item-icon"); ic.setMinWidth(20);
        Label tx  = new Label(label); tx.getStyleClass().add("popup-item-text"); HBox.setHgrow(tx, Priority.ALWAYS);
        Label chk = new Label("✓");   chk.getStyleClass().add("popup-item-check"); chk.setVisible(init);
        boolean[] state = {init};
        row.getChildren().addAll(ic, tx, chk);
        row.setOnMouseClicked(e -> {
            state[0] = !state[0]; chk.setVisible(state[0]); onChange.accept(state[0]);
            if (state[0]) row.getStyleClass().add("popup-item-active");
            else          row.getStyleClass().remove("popup-item-active");
        });
        row.setOnMouseEntered(e -> row.getStyleClass().add("popup-item-hover"));
        row.setOnMouseExited(e  -> row.getStyleClass().remove("popup-item-hover"));
        if (init) row.getStyleClass().add("popup-item-active");
        return row;
    }

    // ── Контекстная панель ────────────────────────────────────────────
    private void showContextBar() {
        if (contextBar != null) {
            ((Label) contextBar.getChildren().get(0))
                    .setText("Выбрано: " + selectedMessages.size()); return;
        }
        contextBar = new HBox(8); contextBar.getStyleClass().add("context-bar");
        contextBar.setPadding(new Insets(6, 14, 6, 14)); contextBar.setAlignment(Pos.CENTER_LEFT);
        Label hint = new Label("Выбрано: " + selectedMessages.size());
        hint.getStyleClass().add("context-bar-hint"); HBox.setHgrow(hint, Priority.ALWAYS);
        Button replyBtn   = new Button("↩  Ответить");
        Button forwardBtn = new Button("⤷  Переслать");
        Button closeBtn   = new Button("✕");
        replyBtn.getStyleClass().add("context-action-btn");
        forwardBtn.getStyleClass().add("context-action-btn");
        closeBtn.getStyleClass().add("theme-btn");
        replyBtn.setOnAction(e   -> { replyToSelected(); hideContextBar(); });
        forwardBtn.setOnAction(e -> { forwardMessage();  hideContextBar(); });
        closeBtn.setOnAction(e   -> { clearSelection();  hideContextBar(); });
        contextBar.getChildren().addAll(hint, replyBtn, forwardBtn, closeBtn);
        inputContainer.getChildren().add(0, contextBar);
    }

    private void hideContextBar() {
        if (contextBar != null) {
            inputContainer.getChildren().remove(contextBar); contextBar = null;
        }
    }

    // ═════════════════════════════════════════════════════════════════
    // ── Отправка — Chain → Strategy → Command → State ─────────────────
    // ═════════════════════════════════════════════════════════════════

    // 1. Текст
    private void sendMessage() {
        String text = inputField.getText().trim();
        String quote = quotedMessage != null
                ? quotedMessage.sender + ": " + quotedMessage.text : null;
        String decorated = applyDecorators(text, "Вы");

        // Chain of Responsibility: валидация перед отправкой
        ValidationRequest req = new ValidationRequest(
                decorated, selectedChat, wsManager.isConnected());
        ValidationResult chainResult = validationChain.handleRequest(req);
        if (!chainResult.isValid()) { showAlert(chainResult.getReason()); return; }

        // Strategy: Client конфигурирует Context нужным алгоритмом
        messageSender.setStrategy(new TextSendStrategy(facade, wsManager));
        messageSender.send(SendContext.forText(selectedChat, decorated, quote));

        // Command: Invoker сохраняет команду в историю
        invoker.invoke(new SendTextCommand(facade, wsManager, selectedChat, decorated, quote));

        // State: создаём контекст состояния для нового сообщения
        MessageContext msgCtx = stateManager.createContext(decorated, selectedChat);

        // Добавляем в историю чата с идентификатором состояния
        chatHistory.get(selectedChat).add(new ChatMessage(
                "Вы", decorated, true, "TEXT", null, quote, null, msgCtx.getMessageId()));

        inputField.clear(); clearQuote(); resetDecorators();
        refreshMessages(); updateChatList();
    }

    // 2. Файл
    private void sendFileMessage(String type) {
        if (selectedChat == null) { showAlert("Выберите чат"); return; }
        FileChooser ch = new FileChooser();
        if (type.equals("IMAGE"))
            ch.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Изображения","*.png","*.jpg","*.jpeg","*.gif"));
        else
            ch.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Видео","*.mp4","*.avi","*.mkv","*.mov"));
        File file = ch.showOpenDialog(scene.getWindow());
        if (file == null) return;

        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Подпись"); dlg.setHeaderText(null); dlg.setContentText("Подпись:");
        String caption = dlg.showAndWait().orElse("").trim();
        String quote   = quotedMessage != null
                ? quotedMessage.sender + ": " + quotedMessage.text : null;
        String text    = applyDecorators(caption.isEmpty() ? file.getName() : caption, "Вы");

        // Chain: валидация подписи
        ValidationResult chainResult = validationChain.handleRequest(
                new ValidationRequest(text, selectedChat, wsManager.isConnected()));
        if (!chainResult.isValid()) { showAlert(chainResult.getReason()); return; }

        // Strategy: алгоритм отправки файла
        messageSender.setStrategy(new FileSendStrategy(facade, wsManager));
        messageSender.send(SendContext.forFile(
                selectedChat, text, file.getAbsolutePath(), type, quote));

        // Command
        invoker.invoke(new SendFileCommand(
                facade, wsManager, selectedChat, text, file.getAbsolutePath(), type, quote));

        // State
        MessageContext msgCtx = stateManager.createContext(text, selectedChat);

        chatHistory.get(selectedChat).add(new ChatMessage(
                "Вы", text, true, type, file.getAbsolutePath(),
                quote, null, msgCtx.getMessageId()));

        clearQuote(); resetDecorators(); refreshMessages(); updateChatList();
    }

    // 3. Геолокация
    private void sendLocationMessage() {
        if (selectedChat == null) { showAlert("Выберите чат"); return; }
        TextInputDialog dlg = new TextInputDialog("53.9045,27.5615");
        dlg.setTitle("Геолокация"); dlg.setHeaderText(null); dlg.setContentText("Координаты:");
        dlg.showAndWait().ifPresent(coords -> {
            if (coords.trim().isEmpty()) return;
            String quote = quotedMessage != null
                    ? quotedMessage.sender + ": " + quotedMessage.text : null;

            // Chain
            ValidationResult chainResult = validationChain.handleRequest(
                    new ValidationRequest(coords, selectedChat, wsManager.isConnected()));
            if (!chainResult.isValid()) { showAlert(chainResult.getReason()); return; }

            // Strategy
            messageSender.setStrategy(new LocationSendStrategy(facade, wsManager));
            messageSender.send(SendContext.forLocation(selectedChat, coords, quote));

            // Command
            invoker.invoke(new SendLocationCommand(
                    facade, wsManager, selectedChat, coords, quote));

            // State
            MessageContext msgCtx = stateManager.createContext("📍 " + coords, selectedChat);

            chatHistory.get(selectedChat).add(new ChatMessage(
                    "Вы", "📍 " + coords, true,
                    "TEXT", null, quote, coords, msgCtx.getMessageId()));

            clearQuote(); resetDecorators(); refreshMessages(); updateChatList();
        });
    }

    // ── Adapter — email ───────────────────────────────────────────────
    private void sendEmailNotification() {
        ObservableList<ChatMessage> hist = selectedChat != null
                ? chatHistory.get(selectedChat) : null;
        List<ChatMessage> toNotify = selectedMessages.isEmpty()
                ? (hist == null || hist.isEmpty() ? Collections.emptyList()
                : List.of(hist.get(hist.size() - 1)))
                : new ArrayList<>(selectedMessages);
        if (toNotify.isEmpty()) { showAlert("Нет сообщений"); return; }

        TextInputDialog dlg = new TextInputDialog("user@example.com");
        dlg.setTitle("Email"); dlg.setHeaderText(null); dlg.setContentText("Email:");
        dlg.showAndWait().ifPresent(email -> {
            if (email.trim().isEmpty()) return;
            Notifier n = new EmailNotifierAdapter(new ExternalEmailService(), email.trim());
            for (ChatMessage msg : toNotify)
                n.notify("[" + selectedChat + "] " + msg.sender + ": " + msg.text);
            clearSelection(); showAlert("Отправлено на: " + email);
        });
    }

    // ── Composite — группа ───────────────────────────────────────────
    private void sendToGroup() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) { showAlert("Введите текст"); return; }

        List<String> available = chatHistory.keySet().stream()
                .filter(c -> !c.equals(selectedChat)).collect(Collectors.toList());
        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Отправить группе"); dialog.setHeaderText("Выберите получателей:");
        VBox boxes = new VBox(8); boxes.setPadding(new Insets(12));
        List<CheckBox> cbs = new ArrayList<>();
        for (String c : available) {
            CheckBox cb = new CheckBox(c); cbs.add(cb); boxes.getChildren().add(cb);
        }
        dialog.getDialogPane().setContent(boxes);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setResultConverter(btn -> btn == ButtonType.OK
                ? cbs.stream().filter(CheckBox::isSelected).map(CheckBox::getText)
                .collect(Collectors.toList()) : null);

        dialog.showAndWait().ifPresent(selected -> {
            if (selected == null || selected.isEmpty()) return;
            String decorated = applyDecorators(text, "Вы");
            String quote     = quotedMessage != null
                    ? quotedMessage.sender + ": " + quotedMessage.text : null;

            // Composite: рассылаем через дерево получателей
            RecipientGroup group = new RecipientGroup("Рассылка");
            for (String name : selected) group.add(new SingleRecipient(name));
            group.send(decorated);

            // Strategy + Command для каждого получателя
            messageSender.setStrategy(new TextSendStrategy(facade, wsManager));
            for (Recipient member : group.getMembers()) {
                messageSender.send(SendContext.forText(member.getName(), decorated, quote));
                invoker.invoke(new SendTextCommand(
                        facade, wsManager, member.getName(), decorated, quote));

                if (!chatHistory.containsKey(member.getName()))
                    chatHistory.put(member.getName(), FXCollections.observableArrayList());
                chatHistory.get(member.getName()).add(new ChatMessage(
                        "Вы", decorated, true, "TEXT", null, quote, null, null));
            }
            inputField.clear(); clearQuote(); resetDecorators();
            refreshMessages(); updateChatList();
            showAlert("Отправлено в: " + String.join(", ", selected));
        });
    }

    private String applyDecorators(String content, String sender) {
        messenger.decorator.Message msg = new SimpleMessage(content, sender);
        if (isEncrypted) msg = new EncryptedMessageDecorator(msg);
        if (isImportant) msg = new ImportantMessageDecorator(msg);
        return msg.getContent();
    }

    private void resetDecorators() { isImportant = false; isEncrypted = false; }

    // ── Ответить ──────────────────────────────────────────────────────
    private void replyToSelected() {
        if (selectedMessages.isEmpty()) return;
        quotedMessage = selectedMessages.iterator().next();
        clearSelection(); showQuoteBar(quotedMessage); inputField.requestFocus();
    }

    private void showQuoteBar(ChatMessage msg) {
        clearQuote();
        quoteBar = new HBox(8); quoteBar.getStyleClass().add("quote-bar");
        quoteBar.setPadding(new Insets(6, 12, 6, 12)); quoteBar.setAlignment(Pos.CENTER_LEFT);
        Label ql = new Label("↩  " + msg.sender + ": " + msg.text);
        ql.getStyleClass().add("quote-bar-text"); ql.setMaxWidth(500);
        HBox.setHgrow(ql, Priority.ALWAYS);
        Button cancel = new Button("✕"); cancel.getStyleClass().add("theme-btn");
        cancel.setOnAction(e -> clearQuote());
        quoteBar.getChildren().addAll(ql, cancel);
        inputContainer.getChildren().add(0, quoteBar);
    }

    private void clearQuote() {
        quotedMessage = null;
        if (quoteBar != null) {
            inputContainer.getChildren().remove(quoteBar); quoteBar = null;
        }
    }

    // ── Переслать — Command + Prototype ──────────────────────────────
    private void forwardMessage() {
        List<ChatMessage> toForward = selectedMessages.isEmpty()
                ? (chatHistory.get(selectedChat) == null
                || chatHistory.get(selectedChat).isEmpty()
                ? Collections.emptyList()
                : List.of(chatHistory.get(selectedChat)
                .get(chatHistory.get(selectedChat).size() - 1)))
                : new ArrayList<>(selectedMessages);
        if (toForward.isEmpty()) return;

        List<String> targets = chatHistory.keySet().stream()
                .filter(c -> !c.equals(selectedChat)).collect(Collectors.toList());
        if (targets.isEmpty()) { showAlert("Нет других чатов"); return; }

        ChoiceDialog<String> dlg = new ChoiceDialog<>(targets.get(0), targets);
        dlg.setTitle("Переслать"); dlg.setHeaderText(null); dlg.setContentText("Чат:");
        dlg.showAndWait().ifPresent(target -> {
            for (ChatMessage msg : toForward) {
                // Command: каждое сообщение — отдельная команда
                invoker.invoke(new ForwardCommand(
                        facade, wsManager, msg.text, msg.sender, selectedChat, target));
            }
            clearSelection(); showAlert("Переслано " + toForward.size() + " сообщ. в: " + target);
            updateChatList();
        });
    }

    private void clearSelection() {
        selectedBubbles.values().forEach(b -> b.getStyleClass().remove("bubble-selected"));
        selectedMessages.clear(); selectedBubbles.clear(); hideContextBar();
    }

    // ── Видеоплеер ────────────────────────────────────────────────────
    private void openVideoPlayer(String filePath, String title) {
        Stage ps = new Stage(); ps.setTitle(title);
        javafx.scene.media.Media media =
                new javafx.scene.media.Media(new File(filePath).toURI().toString());
        javafx.scene.media.MediaPlayer player = new javafx.scene.media.MediaPlayer(media);
        javafx.scene.media.MediaView mv = new javafx.scene.media.MediaView(player);
        mv.setFitWidth(640); mv.setPreserveRatio(true);
        Button play = new Button("▶"); Button pause = new Button("⏸");
        Button stop = new Button("⏹ Стоп");
        play.setOnAction(e -> player.play()); pause.setOnAction(e -> player.pause());
        stop.setOnAction(e -> { player.stop(); ps.close(); });
        HBox ctrl = new HBox(10, play, pause, stop);
        ctrl.setAlignment(Pos.CENTER); ctrl.setPadding(new Insets(8));
        VBox layout = new VBox(8, mv, ctrl); layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10)); layout.setStyle("-fx-background-color:#000;");
        ps.setScene(new Scene(layout));
        ps.setOnCloseRequest(e -> player.stop()); ps.show(); player.play();
    }

    // ── Тема (Abstract Factory) ───────────────────────────────────────
    private void toggleTheme() {
        isDark = !isDark;
        currentFactory = isDark ? new DarkThemeFactory() : new LightThemeFactory();
        applyTheme();
    }

    private void applyTheme() {
        scene.getStylesheets().clear();
        scene.getStylesheets().add(getClass().getResource(
                isDark ? "/dark-theme.css" : "/light-theme.css").toExternalForm());
    }

    // ── Обновление UI ─────────────────────────────────────────────────
    private void selectChat(String name) {
        clearSelection(); clearQuote();
        selectedChat = name; chatTitleLabel.setText(name);
        refreshMessages(); updateChatList();
    }

    private void refreshMessages() {
        messageArea.getChildren().clear();
        ObservableList<ChatMessage> msgs = chatHistory.get(selectedChat);
        if (msgs != null)
            for (ChatMessage msg : msgs)
                messageArea.getChildren().add(buildMessageBubble(msg));
        messageScroll.layout(); messageScroll.setVvalue(1.0);
    }

    private HBox buildMessageBubble(ChatMessage msg) {
        HBox row = new HBox(); row.setMaxWidth(Double.MAX_VALUE);
        VBox bubble = new VBox(4);
        bubble.getStyleClass().add(msg.isOwn ? "bubble-own" : "bubble-other");
        bubble.setPadding(new Insets(8, 12, 8, 12)); bubble.setMaxWidth(320);

        if (!msg.isOwn) {
            Label s = new Label(msg.sender); s.getStyleClass().add("bubble-sender");
            bubble.getChildren().add(s);
        }

        if (msg.quote != null) {
            HBox qb = new HBox();
            qb.setStyle("-fx-background-color:rgba(0,0,0,0.1);" +
                    "-fx-background-radius:6;-fx-padding:4 8 4 8;");
            Label ql = new Label("↩ " + msg.quote);
            ql.setStyle("-fx-font-size:11px;-fx-opacity:0.8;");
            ql.getStyleClass().add("bubble-text"); ql.setWrapText(true); ql.setMaxWidth(260);
            qb.getChildren().add(ql); bubble.getChildren().add(qb);
        }

        if (msg.type.equals("IMAGE") && msg.filePath != null) {
            try {
                javafx.scene.image.ImageView iv = new javafx.scene.image.ImageView(
                        new ImageLoaderProxy(msg.filePath).getImage());
                iv.setFitWidth(280); iv.setPreserveRatio(true); bubble.getChildren().add(iv);
            } catch (Exception ex) { bubble.getChildren().add(new Label("🖼 " + msg.text)); }
            if (!msg.text.equals(new File(msg.filePath).getName())) {
                Label cap = new Label(msg.text); cap.getStyleClass().add("bubble-text");
                cap.setWrapText(true); bubble.getChildren().add(cap);
            }
        } else if (msg.type.equals("VIDEO") && msg.filePath != null) {
            HBox vr = new HBox(8); vr.setAlignment(Pos.CENTER_LEFT);
            vr.setStyle("-fx-cursor:hand;");
            Label ic = new Label("▶"); ic.setStyle("-fx-font-size:20px;");
            VBox vi = new VBox(2);
            Label vn = new Label(msg.text); vn.getStyleClass().add("bubble-text");
            vn.setStyle("-fx-font-weight:bold;");
            Label vh = new Label("Нажмите для воспроизведения");
            vh.setStyle("-fx-font-size:11px;-fx-opacity:0.6;");
            vh.getStyleClass().add("bubble-text");
            vi.getChildren().addAll(vn, vh); vr.getChildren().addAll(ic, vi);
            bubble.getChildren().add(vr);
            vr.setOnMouseClicked(e -> { e.consume(); openVideoPlayer(msg.filePath, msg.text); });
        } else if (msg.location != null) {
            Label gl = new Label("📍 " + msg.location); gl.getStyleClass().add("bubble-text");
            gl.setStyle("-fx-font-weight:bold;");
            Label gh = new Label("Координаты");
            gh.setStyle("-fx-font-size:11px;-fx-opacity:0.6;");
            gh.getStyleClass().add("bubble-text");
            bubble.getChildren().addAll(gl, gh);
        } else {
            Label t = new Label(msg.text); t.getStyleClass().add("bubble-text");
            t.setWrapText(true); bubble.getChildren().add(t);
        }

        // State: иконка статуса для исходящих сообщений
        if (msg.isOwn && msg.messageId != null) {
            String icon = stateManager.getStatusIcon(msg.messageId);
            if (!icon.isEmpty()) {
                Label statusIcon = new Label(icon);
                statusIcon.setStyle("-fx-font-size: 10px; -fx-opacity: 0.7;");
                statusIcon.getStyleClass().add("bubble-text");
                HBox statusRow = new HBox(); statusRow.setAlignment(Pos.CENTER_RIGHT);
                statusRow.getChildren().add(statusIcon);
                bubble.getChildren().add(statusRow);
            }
        }

        bubble.setOnMouseClicked(e -> {
            if (selectedMessages.contains(msg)) {
                selectedMessages.remove(msg); selectedBubbles.remove(msg);
                bubble.getStyleClass().remove("bubble-selected");
            } else {
                selectedMessages.add(msg); selectedBubbles.put(msg, bubble);
                bubble.getStyleClass().add("bubble-selected");
            }
            if (selectedMessages.isEmpty()) hideContextBar(); else showContextBar();
        });

        row.setAlignment(msg.isOwn ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        row.getChildren().add(bubble);
        return row;
    }

    private void updateChatList() {
        if (chatList == null) return;
        chatList.getChildren().clear();
        for (String chat : chatHistory.keySet())
            chatList.getChildren().add(buildChatItem(chat));
        if (statusBar != null)
            statusBar.setText("● " + facade.getOnlineCount() + " онлайн");
    }

    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null); a.showAndWait();
    }

    // ── Модель сообщения (добавлено поле messageId для State) ─────────
    static class ChatMessage {
        final String  sender, text, type, filePath, quote, location, messageId;
        final boolean isOwn;

        ChatMessage(String sender, String text, boolean isOwn, String type,
                    String filePath, String quote, String location, String messageId) {
            this.sender    = sender;    this.text     = text;
            this.isOwn     = isOwn;     this.type     = type;
            this.filePath  = filePath;  this.quote    = quote;
            this.location  = location;  this.messageId = messageId;
        }
    }

    public static void main(String[] args) { launch(args); }
}