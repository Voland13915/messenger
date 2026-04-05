package messenger.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import messenger.abstractfactory.*;
import messenger.adapter.*;
import messenger.builder.ComplexMessage;
import messenger.composite.*;
import messenger.decorator.*;
import messenger.facade.MessengerFacade;
import messenger.proxy.ImageLoaderProxy;
import messenger.singleton.WebSocketManager;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class MessengerWindow extends Application {

    // ── Abstract Factory ──────────────────────────────────────
    private UIFactory currentFactory = new LightThemeFactory();
    private boolean isDark = false;

    // ── Singleton ─────────────────────────────────────────────
    private final WebSocketManager wsManager = WebSocketManager.getInstance();

    // ── Facade ────────────────────────────────────────────────
    private final MessengerFacade facade = new MessengerFacade();

    // ── Adapter — уведомления через внешний сервис ────────────
    private final Notifier notifier = new EmailNotifierAdapter(
            new ExternalEmailService(), "user@example.com"
    );

    // ── Данные чатов ──────────────────────────────────────────
    private final Map<String, ObservableList<ChatMessage>> chatHistory = new HashMap<>();
    private String selectedChat = "Alice";

    // ── Мульти-выделение (Prototype) ──────────────────────────
    private final Set<ChatMessage> selectedMessages = new LinkedHashSet<>();
    private final Map<ChatMessage, VBox> selectedBubbles = new HashMap<>();

    // ── Цитата (Builder) ──────────────────────────────────────
    private ChatMessage quotedMessage = null;
    private HBox quoteBar = null;

    // ── Decorator ─────────────────────────────────────────────
    private boolean isImportant = false;
    private boolean isEncrypted = false;
    private Button importantBtn;
    private Button encryptedBtn;

    // ── UI компоненты ─────────────────────────────────────────
    private VBox messageArea;
    private ScrollPane messageScroll;
    private TextField inputField;
    private Label chatTitleLabel;
    private Label statusBar;
    private VBox inputContainer;
    private Scene scene;
    private VBox chatList;

    private final String[] chats = {"Alice", "Bob", "Charlie", "Группа: Проект"};

    @Override
    public void start(Stage stage) {
        wsManager.start();
        wsManager.connect("alice");
        wsManager.connect("bob");
        wsManager.connect("charlie");

        for (String chat : chats) {
            chatHistory.put(chat, FXCollections.observableArrayList());
        }

        seedInitialMessages();

        BorderPane root = buildRoot();
        scene = new Scene(root, 900, 640);
        applyTheme();

        stage.setTitle("Messenger");
        stage.setScene(scene);
        stage.show();
    }

    // ── Начальные сообщения ───────────────────────────────────
    private void seedInitialMessages() {
        chatHistory.get("Alice").add(new ChatMessage("Alice", "Привет! Как дела?", false, "TEXT", null, null, null));
        chatHistory.get("Alice").add(new ChatMessage("Вы", "Всё отлично, спасибо!", true, "TEXT", null, null, null));
        chatHistory.get("Alice").add(new ChatMessage("Alice", "Встретимся завтра?", false, "TEXT", null, null, null));

        chatHistory.get("Bob").add(new ChatMessage("Bob", "Смотрел новый фильм?", false, "TEXT", null, null, null));
        chatHistory.get("Bob").add(new ChatMessage("Вы", "Ещё нет!", true, "TEXT", null, null, null));

        chatHistory.get("Charlie").add(new ChatMessage("Charlie", "Держи фото 📷", false, "TEXT", null, null, null));

        chatHistory.get("Группа: Проект").add(new ChatMessage("Alice", "Дедлайн завтра!", false, "TEXT", null, null, null));
        chatHistory.get("Группа: Проект").add(new ChatMessage("Bob", "Уже делаю", false, "TEXT", null, null, null));
    }

    // ── Построение интерфейса ─────────────────────────────────
    private BorderPane buildRoot() {
        BorderPane bp = new BorderPane();
        VBox leftPanel = buildLeftPanel();
        leftPanel.setPrefWidth(240);
        bp.setLeft(leftPanel);
        bp.setCenter(buildRightPanel());
        return bp;
    }

    // ── Левая панель ──────────────────────────────────────────
    private VBox buildLeftPanel() {
        VBox panel = new VBox(0);
        panel.getStyleClass().add("left-panel");

        HBox header = new HBox();
        header.getStyleClass().add("left-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(16, 12, 16, 16));

        Label title = new Label("Чаты");
        title.getStyleClass().add("left-title");
        HBox.setHgrow(title, Priority.ALWAYS);

        Button themeBtn = new Button("◑");
        themeBtn.getStyleClass().add("theme-btn");
        themeBtn.setOnAction(e -> toggleTheme());
        themeBtn.setTooltip(new Tooltip("Переключить тему"));
        header.getChildren().addAll(title, themeBtn);

        statusBar = new Label("● " + facade.getOnlineCount() + " онлайн");
        statusBar.getStyleClass().add("status-bar");
        statusBar.setPadding(new Insets(4, 16, 8, 16));

        chatList = new VBox(0);
        for (String chat : chats) chatList.getChildren().add(buildChatItem(chat));

        ScrollPane chatScroll = new ScrollPane(chatList);
        chatScroll.setFitToWidth(true);
        chatScroll.getStyleClass().add("chat-scroll");
        VBox.setVgrow(chatScroll, Priority.ALWAYS);

        panel.getChildren().addAll(header, statusBar, chatScroll);
        return panel;
    }

    private HBox buildChatItem(String name) {
        HBox item = new HBox(10);
        item.getStyleClass().add("chat-item");
        item.setPadding(new Insets(10, 16, 10, 16));
        item.setAlignment(Pos.CENTER_LEFT);

        if (name.equals(selectedChat)) item.getStyleClass().add("chat-item-selected");

        StackPane avatar = new StackPane();
        Circle circle = new Circle(20);
        circle.getStyleClass().add("avatar-circle");
        Label initials = new Label(name.substring(0, 1).toUpperCase());
        initials.getStyleClass().add("avatar-initials");
        avatar.getChildren().addAll(circle, initials);

        VBox info = new VBox(2);
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("chat-name");
        ObservableList<ChatMessage> history = chatHistory.get(name);
        String lastMsg = history.isEmpty() ? "" : history.get(history.size() - 1).text;
        if (lastMsg.length() > 28) lastMsg = lastMsg.substring(0, 28) + "…";
        Label lastLabel = new Label(lastMsg);
        lastLabel.getStyleClass().add("chat-last");
        info.getChildren().addAll(nameLabel, lastLabel);
        HBox.setHgrow(info, Priority.ALWAYS);

        item.getChildren().addAll(avatar, info);
        item.setOnMouseClicked(e -> selectChat(name));
        return item;
    }

    // ── Правая панель ─────────────────────────────────────────
    private VBox buildRightPanel() {
        VBox panel = new VBox(0);
        panel.getStyleClass().add("right-panel");

        HBox chatHeader = new HBox(12);
        chatHeader.getStyleClass().add("chat-header");
        chatHeader.setAlignment(Pos.CENTER_LEFT);
        chatHeader.setPadding(new Insets(14, 20, 14, 20));

        chatTitleLabel = new Label(selectedChat);
        chatTitleLabel.getStyleClass().add("chat-title");
        chatHeader.getChildren().add(chatTitleLabel);

        messageArea = new VBox(8);
        messageArea.setPadding(new Insets(16));
        messageArea.getStyleClass().add("message-area");

        messageScroll = new ScrollPane(messageArea);
        messageScroll.setFitToWidth(true);
        messageScroll.getStyleClass().add("message-scroll");
        VBox.setVgrow(messageScroll, Priority.ALWAYS);

        inputContainer = new VBox(0);
        inputContainer.getChildren().add(buildInputPanel());

        panel.getChildren().addAll(chatHeader, messageScroll, inputContainer);
        refreshMessages();
        return panel;
    }

    private HBox buildInputPanel() {
        HBox panel = new HBox(8);
        panel.getStyleClass().add("input-panel");
        panel.setPadding(new Insets(12, 16, 12, 16));
        panel.setAlignment(Pos.CENTER);

        Button textBtn  = new Button("T");
        Button imageBtn = new Button("🖼");
        Button videoBtn = new Button("▶");
        Button geoBtn   = new Button("📍");
        textBtn.getStyleClass().addAll("type-btn", "type-btn-active");
        imageBtn.getStyleClass().add("type-btn");
        videoBtn.getStyleClass().add("type-btn");
        geoBtn.getStyleClass().add("type-btn");

        textBtn.setOnAction(e -> {
            textBtn.getStyleClass().add("type-btn-active");
            imageBtn.getStyleClass().remove("type-btn-active");
            videoBtn.getStyleClass().remove("type-btn-active");
            geoBtn.getStyleClass().remove("type-btn-active");
            inputField.setPromptText("Напишите сообщение...");
        });
        imageBtn.setOnAction(e -> {
            imageBtn.getStyleClass().add("type-btn-active");
            textBtn.getStyleClass().remove("type-btn-active");
            videoBtn.getStyleClass().remove("type-btn-active");
            geoBtn.getStyleClass().remove("type-btn-active");
            sendFileMessage("IMAGE");
        });
        videoBtn.setOnAction(e -> {
            videoBtn.getStyleClass().add("type-btn-active");
            textBtn.getStyleClass().remove("type-btn-active");
            imageBtn.getStyleClass().remove("type-btn-active");
            geoBtn.getStyleClass().remove("type-btn-active");
            sendFileMessage("VIDEO");
        });
        geoBtn.setOnAction(e -> {
            geoBtn.getStyleClass().add("type-btn-active");
            textBtn.getStyleClass().remove("type-btn-active");
            imageBtn.getStyleClass().remove("type-btn-active");
            videoBtn.getStyleClass().remove("type-btn-active");
            sendLocationMessage();
        });

        inputField = new TextField();
        inputField.setPromptText("Напишите сообщение...");
        inputField.getStyleClass().add("input-field");
        HBox.setHgrow(inputField, Priority.ALWAYS);

        // Decorator
        importantBtn = new Button("❗");
        importantBtn.getStyleClass().add("type-btn");
        importantBtn.setTooltip(new Tooltip("Важное сообщение"));
        importantBtn.setOnAction(e -> {
            isImportant = !isImportant;
            if (isImportant) importantBtn.getStyleClass().add("type-btn-active");
            else importantBtn.getStyleClass().remove("type-btn-active");
        });

        encryptedBtn = new Button("🔒");
        encryptedBtn.getStyleClass().add("type-btn");
        encryptedBtn.setTooltip(new Tooltip("Зашифрованное сообщение"));
        encryptedBtn.setOnAction(e -> {
            isEncrypted = !isEncrypted;
            if (isEncrypted) encryptedBtn.getStyleClass().add("type-btn-active");
            else encryptedBtn.getStyleClass().remove("type-btn-active");
        });

        // Composite
        Button groupBtn = new Button("👥");
        groupBtn.getStyleClass().add("type-btn");
        groupBtn.setTooltip(new Tooltip("Отправить нескольким"));
        groupBtn.setOnAction(e -> sendToGroup());

        // Adapter
        Button emailBtn = new Button("✉");
        emailBtn.getStyleClass().add("type-btn");
        emailBtn.setTooltip(new Tooltip("Отправить уведомление на email"));
        emailBtn.setOnAction(e -> sendEmailNotification());

        Button sendBtn = new Button("➤");
        sendBtn.getStyleClass().add("send-btn");
        sendBtn.setOnAction(e -> sendMessage());
        inputField.setOnAction(e -> sendMessage());

        Button forwardBtn = new Button("⤷");
        forwardBtn.getStyleClass().add("forward-btn");
        forwardBtn.setTooltip(new Tooltip("Переслать выбранные сообщения"));
        forwardBtn.setOnAction(e -> forwardMessage());

        Button replyBtn = new Button("↩");
        replyBtn.getStyleClass().add("forward-btn");
        replyBtn.setTooltip(new Tooltip("Ответить на выбранное сообщение"));
        replyBtn.setOnAction(e -> replyToSelected());

        panel.getChildren().addAll(
                textBtn, imageBtn, videoBtn, geoBtn,
                inputField,
                importantBtn, encryptedBtn,
                groupBtn, emailBtn,
                replyBtn, forwardBtn, sendBtn
        );
        return panel;
    }

    // ── Adapter — уведомление на email ────────────────────────
    private void sendEmailNotification() {
        ObservableList<ChatMessage> history = chatHistory.get(selectedChat);

        // Берём выделенные или последнее сообщение
        List<ChatMessage> toNotify = selectedMessages.isEmpty()
                ? history.isEmpty()
                ? Collections.emptyList()
                : List.of(history.get(history.size() - 1))
                : new ArrayList<>(selectedMessages);

        if (toNotify.isEmpty()) {
            showAlert("Нет сообщений для уведомления.");
            return;
        }

        // Запрашиваем email
        TextInputDialog emailDialog = new TextInputDialog("user@example.com");
        emailDialog.setTitle("Email уведомление");
        emailDialog.setHeaderText("Adapter: notify() → sendEmail()");
        emailDialog.setContentText("Email получателя:");

        emailDialog.showAndWait().ifPresent(email -> {
            if (email.trim().isEmpty()) return;

            // Создаём адаптер с нужным email
            Notifier customNotifier = new EmailNotifierAdapter(
                    new ExternalEmailService(), email.trim()
            );

            // Client вызывает notify() — не знает про sendEmail() внутри
            for (ChatMessage msg : toNotify) {
                customNotifier.notify(
                        "[" + selectedChat + "] " + msg.sender + ": " + msg.text
                );
            }

            // Снимаем выделение
            selectedBubbles.values().forEach(b -> b.getStyleClass().remove("bubble-selected"));
            selectedMessages.clear();
            selectedBubbles.clear();

            showAlert("Уведомление отправлено на: " + email + "\n(см. консоль)");
        });
    }

    // ── Composite — отправка группе ───────────────────────────
    private void sendToGroup() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) {
            showAlert("Введите текст сообщения перед выбором группы.");
            return;
        }

        List<String> available = Arrays.stream(chats)
                .filter(c -> !c.equals(selectedChat))
                .collect(Collectors.toList());

        Dialog<List<String>> dialog = new Dialog<>();
        dialog.setTitle("Отправить группе");
        dialog.setHeaderText("Выберите получателей:");

        VBox checkBoxes = new VBox(8);
        checkBoxes.setPadding(new Insets(12));
        List<CheckBox> boxes = new ArrayList<>();
        for (String chat : available) {
            CheckBox cb = new CheckBox(chat);
            boxes.add(cb);
            checkBoxes.getChildren().add(cb);
        }

        dialog.getDialogPane().setContent(checkBoxes);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                return boxes.stream()
                        .filter(CheckBox::isSelected)
                        .map(CheckBox::getText)
                        .collect(Collectors.toList());
            }
            return null;
        });

        dialog.showAndWait().ifPresent(selected -> {
            if (selected == null || selected.isEmpty()) return;

            String quote = quotedMessage != null ? quotedMessage.sender + ": " + quotedMessage.text : null;
            String decoratedText = applyDecorators(text, "Вы");

            // Composite — строим дерево получателей
            RecipientGroup group = new RecipientGroup("Рассылка");
            for (String name : selected) {
                group.add(new SingleRecipient(name));
            }

            // Operation() — рассылаем всем
            group.send(decoratedText);

            for (Recipient member : group.getMembers()) {
                ComplexMessage msg = facade.sendText("Вы", decoratedText, quote);
                chatHistory.get(member.getName()).add(
                        new ChatMessage("Вы", msg.getText(), true, "TEXT", null, quote, null)
                );
            }

            ComplexMessage msg = facade.sendText("Вы", decoratedText, quote);
            chatHistory.get(selectedChat).add(
                    new ChatMessage("Вы", msg.getText(), true, "TEXT", null, quote, null)
            );

            inputField.clear();
            clearQuote();
            resetDecorators();
            refreshMessages();
            updateChatList();

            showAlert("Отправлено в " + selected.size() + " чат(а): " + String.join(", ", selected));
        });
    }

    // ── Применяем декораторы ──────────────────────────────────
    private String applyDecorators(String content, String sender) {
        messenger.decorator.Message msg = new SimpleMessage(content, sender);
        if (isEncrypted) msg = new EncryptedMessageDecorator(msg);
        if (isImportant) msg = new ImportantMessageDecorator(msg);
        return msg.getContent();
    }

    // ── 1. Текст — через Facade + Decorator ───────────────────
    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        String quote = quotedMessage != null ? quotedMessage.sender + ": " + quotedMessage.text : null;
        String decoratedText = applyDecorators(text, "Вы");
        ComplexMessage msg = facade.sendText("Вы", decoratedText, quote);

        chatHistory.get(selectedChat).add(
                new ChatMessage("Вы", msg.getText(), true, "TEXT", null, quote, null)
        );

        inputField.clear();
        clearQuote();
        resetDecorators();
        refreshMessages();
        updateChatList();
    }

    // ── 2. Файл — через Facade + Decorator ───────────────────
    private void sendFileMessage(String type) {
        FileChooser chooser = new FileChooser();
        if (type.equals("IMAGE")) {
            chooser.setTitle("Выберите изображение");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Изображения", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
            );
        } else {
            chooser.setTitle("Выберите видео");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Видео", "*.mp4", "*.avi", "*.mkv", "*.mov")
            );
        }

        File file = chooser.showOpenDialog(scene.getWindow());
        if (file == null) return;

        TextInputDialog captionDialog = new TextInputDialog();
        captionDialog.setTitle("Подпись");
        captionDialog.setHeaderText(null);
        captionDialog.setContentText("Добавить подпись (необязательно):");
        String caption = captionDialog.showAndWait().orElse("").trim();

        String quote = quotedMessage != null ? quotedMessage.sender + ": " + quotedMessage.text : null;
        String rawCaption = caption.isEmpty() ? file.getName() : caption;
        String decoratedCaption = applyDecorators(rawCaption, "Вы");

        ComplexMessage complex = facade.sendFile("Вы", decoratedCaption, file.getAbsolutePath(), type, quote);

        chatHistory.get(selectedChat).add(
                new ChatMessage("Вы", complex.getText(), true, type, file.getAbsolutePath(), quote, null)
        );

        clearQuote();
        resetDecorators();
        refreshMessages();
        updateChatList();
    }

    // ── 3. Геолокация ─────────────────────────────────────────
    private void sendLocationMessage() {
        TextInputDialog dialog = new TextInputDialog("53.9045,27.5615");
        dialog.setTitle("Геолокация");
        dialog.setHeaderText(null);
        dialog.setContentText("Введите координаты (широта,долгота):");

        dialog.showAndWait().ifPresent(coords -> {
            if (coords.trim().isEmpty()) return;

            String quote = quotedMessage != null ? quotedMessage.sender + ": " + quotedMessage.text : null;
            ComplexMessage msg = facade.sendLocation("Вы", coords, quote);

            chatHistory.get(selectedChat).add(
                    new ChatMessage("Вы", msg.getText(), true, "TEXT", null, quote, coords)
            );

            clearQuote();
            resetDecorators();
            refreshMessages();
            updateChatList();
        });
    }

    // ── Сбрасываем декораторы ─────────────────────────────────
    private void resetDecorators() {
        isImportant = false;
        isEncrypted = false;
        importantBtn.getStyleClass().remove("type-btn-active");
        encryptedBtn.getStyleClass().remove("type-btn-active");
    }

    // ── 4. Ответить с цитатой ─────────────────────────────────
    private void replyToSelected() {
        if (selectedMessages.isEmpty()) return;

        ChatMessage toQuote = selectedMessages.iterator().next();
        quotedMessage = toQuote;

        selectedBubbles.values().forEach(b -> b.getStyleClass().remove("bubble-selected"));
        selectedMessages.clear();
        selectedBubbles.clear();

        showQuoteBar(toQuote);
        inputField.requestFocus();
    }

    private void showQuoteBar(ChatMessage msg) {
        clearQuote();

        quoteBar = new HBox(8);
        quoteBar.getStyleClass().add("quote-bar");
        quoteBar.setPadding(new Insets(6, 12, 6, 12));
        quoteBar.setAlignment(Pos.CENTER_LEFT);

        Label quoteText = new Label("↩  " + msg.sender + ": " + msg.text);
        quoteText.getStyleClass().add("quote-bar-text");
        quoteText.setMaxWidth(500);
        HBox.setHgrow(quoteText, Priority.ALWAYS);

        Button cancelBtn = new Button("✕");
        cancelBtn.getStyleClass().add("theme-btn");
        cancelBtn.setOnAction(e -> clearQuote());

        quoteBar.getChildren().addAll(quoteText, cancelBtn);
        inputContainer.getChildren().add(0, quoteBar);
    }

    private void clearQuote() {
        quotedMessage = null;
        if (quoteBar != null) {
            inputContainer.getChildren().remove(quoteBar);
            quoteBar = null;
        }
    }

    // ── 5. Пересылка — Prototype через Facade ─────────────────
    private void forwardMessage() {
        List<ChatMessage> toForward = selectedMessages.isEmpty()
                ? chatHistory.get(selectedChat).isEmpty()
                ? Collections.emptyList()
                : List.of(chatHistory.get(selectedChat).get(chatHistory.get(selectedChat).size() - 1))
                : new ArrayList<>(selectedMessages);

        if (toForward.isEmpty()) return;

        List<String> targets = Arrays.stream(chats)
                .filter(c -> !c.equals(selectedChat))
                .collect(Collectors.toList());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(targets.get(0), targets);
        dialog.setTitle("Переслать сообщения");
        dialog.setHeaderText("Выбрано сообщений: " + toForward.size());
        dialog.setContentText("Выберите чат:");

        dialog.showAndWait().ifPresent(targetChat -> {
            for (ChatMessage msg : toForward) {
                messenger.prototype.Message forwarded =
                        facade.forwardMessage(msg.text, msg.sender, selectedChat, targetChat);

                chatHistory.get(targetChat).add(
                        new ChatMessage("⤷ " + msg.sender, forwarded.getContent(), false, msg.type, msg.filePath, null, msg.location)
                );
            }

            selectedBubbles.values().forEach(b -> b.getStyleClass().remove("bubble-selected"));
            selectedMessages.clear();
            selectedBubbles.clear();

            showAlert("Переслано " + toForward.size() + " сообщ. в чат: " + targetChat);
            updateChatList();
        });
    }

    // ── Видеоплеер ────────────────────────────────────────────
    private void openVideoPlayer(String filePath, String title) {
        Stage playerStage = new Stage();
        playerStage.setTitle(title);

        Media media = new Media(new File(filePath).toURI().toString());
        MediaPlayer player = new MediaPlayer(media);
        MediaView mediaView = new MediaView(player);
        mediaView.setFitWidth(640);
        mediaView.setPreserveRatio(true);

        Button playBtn  = new Button("▶ Играть");
        Button pauseBtn = new Button("⏸ Пауза");
        Button stopBtn  = new Button("⏹ Стоп");

        playBtn.setOnAction(e  -> player.play());
        pauseBtn.setOnAction(e -> player.pause());
        stopBtn.setOnAction(e  -> { player.stop(); playerStage.close(); });

        HBox controls = new HBox(10, playBtn, pauseBtn, stopBtn);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(8));

        VBox layout = new VBox(8, mediaView, controls);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(10));
        layout.setStyle("-fx-background-color: #000;");

        playerStage.setScene(new Scene(layout));
        playerStage.setOnCloseRequest(e -> player.stop());
        playerStage.show();
        player.play();
    }

    // ── Переключение темы (Abstract Factory) ─────────────────
    private void toggleTheme() {
        isDark = !isDark;
        currentFactory = isDark ? new DarkThemeFactory() : new LightThemeFactory();
        applyTheme();
    }

    private void applyTheme() {
        scene.getStylesheets().clear();
        if (isDark) {
            scene.getStylesheets().add(getClass().getResource("/dark-theme.css").toExternalForm());
        } else {
            scene.getStylesheets().add(getClass().getResource("/light-theme.css").toExternalForm());
        }
    }

    // ── Обновление UI ─────────────────────────────────────────
    private void selectChat(String name) {
        selectedBubbles.values().forEach(b -> b.getStyleClass().remove("bubble-selected"));
        selectedMessages.clear();
        selectedBubbles.clear();
        clearQuote();

        selectedChat = name;
        chatTitleLabel.setText(name);
        refreshMessages();
        updateChatList();
    }

    private void refreshMessages() {
        messageArea.getChildren().clear();
        for (ChatMessage msg : chatHistory.get(selectedChat)) {
            messageArea.getChildren().add(buildMessageBubble(msg));
        }
        messageScroll.layout();
        messageScroll.setVvalue(1.0);
    }

    private HBox buildMessageBubble(ChatMessage msg) {
        HBox row = new HBox();
        row.setMaxWidth(Double.MAX_VALUE);

        VBox bubble = new VBox(4);
        bubble.getStyleClass().add(msg.isOwn ? "bubble-own" : "bubble-other");
        bubble.setPadding(new Insets(8, 12, 8, 12));
        bubble.setMaxWidth(320);

        if (!msg.isOwn) {
            Label sender = new Label(msg.sender);
            sender.getStyleClass().add("bubble-sender");
            bubble.getChildren().add(sender);
        }

        if (msg.quote != null) {
            HBox quoteBox = new HBox();
            quoteBox.setStyle("-fx-background-color: rgba(0,0,0,0.1); -fx-background-radius: 6; -fx-padding: 4 8 4 8;");
            Label quoteLabel = new Label("↩ " + msg.quote);
            quoteLabel.setStyle("-fx-font-size: 11px; -fx-opacity: 0.8;");
            quoteLabel.getStyleClass().add("bubble-text");
            quoteLabel.setWrapText(true);
            quoteLabel.setMaxWidth(260);
            quoteBox.getChildren().add(quoteLabel);
            bubble.getChildren().add(quoteBox);
        }

        if (msg.type.equals("IMAGE") && msg.filePath != null) {
            try {
                // Proxy
                ImageLoaderProxy proxy = new ImageLoaderProxy(msg.filePath);
                Image image = proxy.getImage();
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(280);
                imageView.setPreserveRatio(true);
                bubble.getChildren().add(imageView);
            } catch (Exception ex) {
                bubble.getChildren().add(new Label("🖼 " + msg.text));
            }
            if (!msg.text.equals(new File(msg.filePath).getName())) {
                Label caption = new Label(msg.text);
                caption.getStyleClass().add("bubble-text");
                caption.setWrapText(true);
                bubble.getChildren().add(caption);
            }

        } else if (msg.type.equals("VIDEO") && msg.filePath != null) {
            HBox videoRow = new HBox(8);
            videoRow.setAlignment(Pos.CENTER_LEFT);
            videoRow.setStyle("-fx-cursor: hand;");
            Label icon = new Label("▶");
            icon.setStyle("-fx-font-size: 20px;");
            VBox videoInfo = new VBox(2);
            Label name = new Label(msg.text);
            name.getStyleClass().add("bubble-text");
            name.setStyle("-fx-font-weight: bold;");
            Label hint = new Label("Нажмите для воспроизведения");
            hint.setStyle("-fx-font-size: 11px; -fx-opacity: 0.6;");
            hint.getStyleClass().add("bubble-text");
            videoInfo.getChildren().addAll(name, hint);
            videoRow.getChildren().addAll(icon, videoInfo);
            bubble.getChildren().add(videoRow);
            videoRow.setOnMouseClicked(e -> { e.consume(); openVideoPlayer(msg.filePath, msg.text); });

        } else if (msg.location != null) {
            VBox geoBox = new VBox(2);
            Label geoLabel = new Label("📍 " + msg.location);
            geoLabel.getStyleClass().add("bubble-text");
            geoLabel.setStyle("-fx-font-weight: bold;");
            Label geoHint = new Label("Координаты");
            geoHint.setStyle("-fx-font-size: 11px; -fx-opacity: 0.6;");
            geoHint.getStyleClass().add("bubble-text");
            geoBox.getChildren().addAll(geoLabel, geoHint);
            bubble.getChildren().add(geoBox);

        } else {
            Label text = new Label(msg.text);
            text.getStyleClass().add("bubble-text");
            text.setWrapText(true);
            bubble.getChildren().add(text);
        }

        bubble.setOnMouseClicked(e -> {
            if (selectedMessages.contains(msg)) {
                selectedMessages.remove(msg);
                selectedBubbles.remove(msg);
                bubble.getStyleClass().remove("bubble-selected");
            } else {
                selectedMessages.add(msg);
                selectedBubbles.put(msg, bubble);
                bubble.getStyleClass().add("bubble-selected");
            }
        });

        row.setAlignment(msg.isOwn ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        row.getChildren().add(bubble);
        return row;
    }

    private void updateChatList() {
        chatList.getChildren().clear();
        for (String chat : chats) chatList.getChildren().add(buildChatItem(chat));
        statusBar.setText("● " + facade.getOnlineCount() + " онлайн");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    static class ChatMessage {
        final String sender, text, type, filePath, quote, location;
        final boolean isOwn;

        ChatMessage(String sender, String text, boolean isOwn, String type,
                    String filePath, String quote, String location) {
            this.sender   = sender;
            this.text     = text;
            this.isOwn    = isOwn;
            this.type     = type;
            this.filePath = filePath;
            this.quote    = quote;
            this.location = location;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}