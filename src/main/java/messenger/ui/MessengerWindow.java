package messenger.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import messenger.abstractfactory.*;
import messenger.builder.*;
import messenger.factorymethod.*;
import messenger.prototype.*;
import messenger.singleton.WebSocketManager;

import java.util.*;
import java.util.stream.Collectors;

public class MessengerWindow extends Application {

    // ── Abstract Factory: текущая фабрика темы ───────────────
    private UIFactory currentFactory = new LightThemeFactory();
    private boolean isDark = false;

    // ── Singleton ────────────────────────────────────────────
    private final WebSocketManager wsManager = WebSocketManager.getInstance();

    // ── Данные чатов ─────────────────────────────────────────
    private final Map<String, ObservableList<ChatMessage>> chatHistory = new HashMap<>();
    private String selectedChat = "Alice";

    // ── Мульти-выделение сообщений (Prototype) ────────────────
    private final Set<ChatMessage> selectedMessages = new LinkedHashSet<>();
    private final Map<ChatMessage, VBox> selectedBubbles = new HashMap<>();

    // ── UI компоненты ─────────────────────────────────────────
    private VBox messageArea;
    private ScrollPane messageScroll;
    private TextField inputField;
    private Label chatTitleLabel;
    private Label statusBar;
    private Scene scene;
    private VBox chatList;

    // ── Список чатов ─────────────────────────────────────────
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
        scene = new Scene(root, 900, 620);
        applyTheme();

        stage.setTitle("Messenger");
        stage.setScene(scene);
        stage.show();
    }

    // ── Начальные сообщения ───────────────────────────────────
    private void seedInitialMessages() {
        chatHistory.get("Alice").add(new ChatMessage("Alice", "Привет! Как дела?", false, "TEXT"));
        chatHistory.get("Alice").add(new ChatMessage("Вы", "Всё отлично, спасибо!", true, "TEXT"));
        chatHistory.get("Alice").add(new ChatMessage("Alice", "Встретимся завтра?", false, "TEXT"));

        chatHistory.get("Bob").add(new ChatMessage("Bob", "Смотрел новый фильм?", false, "TEXT"));
        chatHistory.get("Bob").add(new ChatMessage("Вы", "Ещё нет!", true, "TEXT"));

        chatHistory.get("Charlie").add(new ChatMessage("Charlie", "Держи фото 📷", false, "IMAGE"));

        chatHistory.get("Группа: Проект").add(new ChatMessage("Alice", "Дедлайн завтра!", false, "TEXT"));
        chatHistory.get("Группа: Проект").add(new ChatMessage("Bob", "Уже делаю", false, "TEXT"));
    }

    // ── Построение интерфейса ─────────────────────────────────
    private BorderPane buildRoot() {
        BorderPane bp = new BorderPane();

        VBox leftPanel = buildLeftPanel();
        leftPanel.setPrefWidth(240);
        bp.setLeft(leftPanel);

        VBox rightPanel = buildRightPanel();
        bp.setCenter(rightPanel);

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

        statusBar = new Label("● " + wsManager.getConnectionCount() + " онлайн");
        statusBar.getStyleClass().add("status-bar");
        statusBar.setPadding(new Insets(4, 16, 8, 16));

        chatList = new VBox(0);
        for (String chat : chats) {
            chatList.getChildren().add(buildChatItem(chat));
        }

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

        if (name.equals(selectedChat)) {
            item.getStyleClass().add("chat-item-selected");
        }

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

        HBox inputPanel = buildInputPanel();

        panel.getChildren().addAll(chatHeader, messageScroll, inputPanel);
        refreshMessages();
        return panel;
    }

    private HBox buildInputPanel() {
        HBox panel = new HBox(10);
        panel.getStyleClass().add("input-panel");
        panel.setPadding(new Insets(12, 16, 12, 16));
        panel.setAlignment(Pos.CENTER);

        Button textBtn  = new Button("T");
        Button imageBtn = new Button("🖼");
        Button videoBtn = new Button("▶");
        textBtn.getStyleClass().addAll("type-btn", "type-btn-active");
        imageBtn.getStyleClass().add("type-btn");
        videoBtn.getStyleClass().add("type-btn");

        final String[] msgType = {"TEXT"};
        textBtn.setOnAction(e -> {
            msgType[0] = "TEXT";
            textBtn.getStyleClass().add("type-btn-active");
            imageBtn.getStyleClass().remove("type-btn-active");
            videoBtn.getStyleClass().remove("type-btn-active");
            inputField.setPromptText("Напишите сообщение...");
        });
        imageBtn.setOnAction(e -> {
            msgType[0] = "IMAGE";
            imageBtn.getStyleClass().add("type-btn-active");
            textBtn.getStyleClass().remove("type-btn-active");
            videoBtn.getStyleClass().remove("type-btn-active");
            inputField.setPromptText("URL изображения...");
        });
        videoBtn.setOnAction(e -> {
            msgType[0] = "VIDEO";
            videoBtn.getStyleClass().add("type-btn-active");
            textBtn.getStyleClass().remove("type-btn-active");
            imageBtn.getStyleClass().remove("type-btn-active");
            inputField.setPromptText("URL видео...");
        });

        inputField = new TextField();
        inputField.setPromptText("Напишите сообщение...");
        inputField.getStyleClass().add("input-field");
        HBox.setHgrow(inputField, Priority.ALWAYS);

        Button sendBtn = new Button("➤");
        sendBtn.getStyleClass().add("send-btn");
        sendBtn.setOnAction(e -> sendMessage(msgType[0]));
        inputField.setOnAction(e -> sendMessage(msgType[0]));

        Button forwardBtn = new Button("⤷");
        forwardBtn.getStyleClass().add("forward-btn");
        forwardBtn.setTooltip(new Tooltip("Переслать выбранные сообщения"));
        forwardBtn.setOnAction(e -> forwardMessage());

        panel.getChildren().addAll(textBtn, imageBtn, videoBtn, inputField, forwardBtn, sendBtn);
        return panel;
    }

    // ── Отправка сообщения (Factory Method + Builder) ─────────
    private void sendMessage(String type) {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        // Factory Method — создаём нужный тип сообщения
        MessageCreator creator = switch (type) {
            case "IMAGE" -> new ImageMessageCreator();
            case "VIDEO" -> new VideoMessageCreator();
            default      -> new TextMessageCreator();
        };
        creator.factoryMethod(text);

        // Builder — собираем ComplexMessage с метаданными
        ConcreteMessageBuilder builder = new ConcreteMessageBuilder();
        new MessageDirector(builder).construct(
                "Вы", text, null,
                type.equals("TEXT") ? null : text,
                null
        );

        chatHistory.get(selectedChat).add(new ChatMessage("Вы", text, true, type));
        inputField.clear();
        refreshMessages();
        updateChatList();
    }

    // ── Пересылка (Prototype) ─────────────────────────────────
    private void forwardMessage() {
        // Берём выделенные или последнее если ничего не выбрано
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
            // Prototype — клонируем каждое сообщение отдельно
            for (ChatMessage msg : toForward) {
                messenger.prototype.TextMessage proto =
                        new messenger.prototype.TextMessage(msg.text, msg.sender, selectedChat);
                MessageForwarder forwarder = new MessageForwarder(proto);
                messenger.prototype.Message forwarded = forwarder.forward(targetChat);

                chatHistory.get(targetChat).add(
                        new ChatMessage("⤷ " + msg.sender, forwarded.getContent(), false, "TEXT")
                );
            }

            // Снимаем все выделения
            selectedBubbles.values().forEach(b -> b.getStyleClass().remove("bubble-selected"));
            selectedMessages.clear();
            selectedBubbles.clear();

            showAlert("Переслано " + toForward.size() + " сообщ. в чат: " + targetChat);
            updateChatList();
        });
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
        // Сбрасываем выделение при смене чата
        selectedBubbles.values().forEach(b -> b.getStyleClass().remove("bubble-selected"));
        selectedMessages.clear();
        selectedBubbles.clear();

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

        VBox bubble = new VBox(3);
        bubble.getStyleClass().add(msg.isOwn ? "bubble-own" : "bubble-other");
        bubble.setPadding(new Insets(8, 12, 8, 12));
        bubble.setMaxWidth(400);

        if (!msg.isOwn) {
            Label sender = new Label(msg.sender);
            sender.getStyleClass().add("bubble-sender");
            bubble.getChildren().add(sender);
        }

        String prefix = switch (msg.type) {
            case "IMAGE" -> "🖼 ";
            case "VIDEO" -> "▶ ";
            default -> "";
        };

        Label text = new Label(prefix + msg.text);
        text.getStyleClass().add("bubble-text");
        text.setWrapText(true);
        bubble.getChildren().add(text);

        // Клик — добавляем или убираем из выделения
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
        for (String chat : chats) {
            chatList.getChildren().add(buildChatItem(chat));
        }
        statusBar.setText("● " + wsManager.getConnectionCount() + " онлайн");
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    // ── Внутренний класс сообщения ────────────────────────────
    static class ChatMessage {
        final String sender, text, type;
        final boolean isOwn;

        ChatMessage(String sender, String text, boolean isOwn, String type) {
            this.sender = sender;
            this.text   = text;
            this.isOwn  = isOwn;
            this.type   = type;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}