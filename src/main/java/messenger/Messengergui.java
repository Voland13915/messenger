package messenger;

import messenger.factorymethod.*;
import messenger.abstractfactory.*;
import messenger.singleton.*;
import messenger.builder.*;
import messenger.prototype.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Имитация мессенджера. Все 5 порождающих паттернов работают "под капотом".
 * Лог паттернов внизу показывает какой паттерн сработал при каждом действии.
 */
public class MessengerGUI extends JFrame {

    // ── Цвета (тёмная тема) ───────────────────────────────────────────────
    private static final Color C_BG_APP       = new Color(17, 27, 33);
    private static final Color C_BG_SIDEBAR   = new Color(24, 37, 45);
    private static final Color C_BG_CHAT      = new Color(12, 20, 24);
    private static final Color C_BG_INPUT     = new Color(31, 44, 51);
    private static final Color C_BG_MSG_OUT   = new Color(5, 97, 98);
    private static final Color C_BG_MSG_IN    = new Color(31, 44, 51);
    private static final Color C_BG_MSG_BOT   = new Color(40, 30, 55);
    private static final Color C_BG_MSG_FWD   = new Color(25, 50, 45);
    private static final Color C_BG_LOG       = new Color(10, 14, 16);
    private static final Color C_ACCENT       = new Color(0, 168, 132);
    private static final Color C_TEXT_MAIN    = new Color(229, 229, 229);
    private static final Color C_TEXT_DIM     = new Color(130, 150, 160);
    private static final Color C_TEXT_TIME    = new Color(100, 120, 130);
    private static final Color C_LOG_PATTERN  = new Color(100, 200, 100);
    private static final Color C_DIVIDER      = new Color(37, 51, 57);
    private static final Color C_HOVER        = new Color(37, 55, 65);
    private static final Color C_SELECTED     = new Color(44, 65, 77);

    // ── Шрифты ────────────────────────────────────────────────────────────
    private static final Font F_MAIN  = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font F_BOLD  = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font F_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font F_MONO  = new Font("Consolas", Font.PLAIN, 11);
    private static final Font F_TITLE = new Font("Segoe UI", Font.BOLD, 14);

    // ── Данные чатов ──────────────────────────────────────────────────────
    private static final String[][] CHATS = {
            {"Alice",              "👤", "#5b9bd5"},
            {"Bob",                "👤", "#ed7d31"},
            {"ChatBot",            "🤖", "#7030a0"},
            {"NewsBot",            "📰", "#c00000"},
            {"Группа Java Dev",    "👥", "#43a047"},
    };

    private int selectedChat = 0;
    private final List<JPanel>    chatPanels   = new ArrayList<>();
    private final List<JPanel>    chatContents = new ArrayList<>();
    private JPanel                chatArea;
    private JTextArea             patternLog;
    private JTextField            inputField;
    private JLabel                headerName;
    private JLabel                headerStatus;

    // последнее сообщение (для пересылки — Prototype)
    private String lastMessageText   = null;
    private String lastMessageSender = null;

    // ── Точка входа ───────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MessengerGUI::new);
    }

    public MessengerGUI() {
        setTitle("Messenger — Порождающие паттерны");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1000, 680);
        setMinimumSize(new Dimension(860, 560));
        setLocationRelativeTo(null);

        for (int i = 0; i < CHATS.length; i++) chatContents.add(null);
        buildUI();
        selectChat(0);

        // Singleton: запуск WebSocket при старте
        WebSocketManager.getInstance().start();
        logPattern("Singleton", "WebSocketManager.getInstance() — сервер запущен");

        setVisible(true);
    }

    // ════════════════════════════════════════════════════════════════════════
    // ПОСТРОЕНИЕ UI
    // ════════════════════════════════════════════════════════════════════════

    private void buildUI() {
        setLayout(new BorderLayout());
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildSidebar(), buildChatZone());
        mainSplit.setDividerLocation(260);
        mainSplit.setDividerSize(1);
        mainSplit.setBorder(null);
        add(mainSplit, BorderLayout.CENTER);
    }

    // ── Левая панель ──────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(C_BG_SIDEBAR);
        root.setPreferredSize(new Dimension(260, 0));

        // Шапка
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(C_BG_INPUT);
        header.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel title = new JLabel("Мессенджер");
        title.setFont(F_TITLE);
        title.setForeground(C_TEXT_MAIN);
        JLabel conn = new JLabel("● онлайн");
        conn.setFont(F_SMALL);
        conn.setForeground(C_ACCENT);
        header.add(title, BorderLayout.WEST);
        header.add(conn,  BorderLayout.EAST);
        root.add(header, BorderLayout.NORTH);

        // Список чатов
        JPanel list = new JPanel();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBackground(C_BG_SIDEBAR);
        for (int i = 0; i < CHATS.length; i++) {
            JPanel row = buildChatRow(i);
            chatPanels.add(row);
            list.add(row);
            list.add(makeDivider());
        }
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(C_BG_SIDEBAR);
        root.add(scroll, BorderLayout.CENTER);

        // Панель ботов внизу
        root.add(buildBotPanel(), BorderLayout.SOUTH);
        return root;
    }

    private JPanel buildChatRow(int idx) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setBackground(C_BG_SIDEBAR);
        row.setBorder(new EmptyBorder(10, 14, 10, 14));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel avatar = new JLabel(CHATS[idx][1], SwingConstants.CENTER);
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setOpaque(true);
        avatar.setBackground(new Color(Integer.parseInt(CHATS[idx][2].substring(1), 16)));

        JLabel name = new JLabel(CHATS[idx][0]);
        name.setFont(F_BOLD);
        name.setForeground(C_TEXT_MAIN);
        JLabel sub = new JLabel(idx < 2 ? "нажмите для чата" : "бот");
        sub.setFont(F_SMALL);
        sub.setForeground(C_TEXT_DIM);

        JPanel text = new JPanel(new GridLayout(2, 1, 0, 1));
        text.setOpaque(false);
        text.add(name);
        text.add(sub);

        row.add(avatar, BorderLayout.WEST);
        row.add(text,   BorderLayout.CENTER);

        final int i = idx;
        row.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e)  { selectChat(i); }
            public void mouseEntered(MouseEvent e)  { if (selectedChat != i) row.setBackground(C_HOVER); }
            public void mouseExited(MouseEvent e)   { if (selectedChat != i) row.setBackground(C_BG_SIDEBAR); }
        });
        return row;
    }

    private JPanel buildBotPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 2, 2));
        panel.setBackground(C_BG_SIDEBAR);
        panel.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, C_DIVIDER),
                new EmptyBorder(6, 10, 6, 10)));

        JLabel lbl = new JLabel("  Написать боту (Abstract Factory):");
        lbl.setFont(F_SMALL);
        lbl.setForeground(C_TEXT_DIM);
        panel.add(lbl);

        String[] names = {"💬 ChatBot", "📰 NewsBot", "🤖 AssistantBot"};
        BotFactory[] factories = {
                new ChatBotFactory(),
                new NewsBotFactory(),
                new AssistantBotFactory()
        };
        String[] replies = {
                "Здравствуйте! Чем могу помочь?",
                "Последние новости: Java 23 вышла!",
                "Задача добавлена в календарь ✓"
        };

        for (int i = 0; i < 3; i++) {
            final int idx = i;
            JButton btn = makeBtn(names[i]);
            btn.addActionListener(e -> {
                // Abstract Factory создаёт бота и профиль
                Bot bot = factories[idx].createBot();
                factories[idx].createBotProfile();
                logPattern("Abstract Factory",
                        factories[idx].getClass().getSimpleName() +
                                " → createBot() + createBotProfile()");

                selectChat(idx + 2);
                addMessage(idx + 2, "Вы", "Привет!", "out");
                addMessage(idx + 2, bot.getName(), replies[idx], "bot");
            });
            panel.add(btn);
        }
        return panel;
    }

    // ── Правая зона: чат + лог ────────────────────────────────────────────
    private JPanel buildChatZone() {
        JPanel zone = new JPanel(new BorderLayout());
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                buildChatPanel(), buildLogPanel());
        split.setDividerLocation(420);
        split.setDividerSize(3);
        split.setBorder(null);
        zone.add(split, BorderLayout.CENTER);
        return zone;
    }

    private JPanel buildChatPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(C_BG_CHAT);

        // Шапка чата
        JPanel header = new JPanel(new BorderLayout(10, 0));
        header.setBackground(C_BG_INPUT);
        header.setBorder(new EmptyBorder(10, 16, 10, 16));
        headerName   = new JLabel("Alice");
        headerName.setFont(F_TITLE);
        headerName.setForeground(C_TEXT_MAIN);
        headerStatus = new JLabel("в сети");
        headerStatus.setFont(F_SMALL);
        headerStatus.setForeground(C_ACCENT);
        JPanel ht = new JPanel(new GridLayout(2, 1));
        ht.setOpaque(false);
        ht.add(headerName);
        ht.add(headerStatus);
        header.add(ht, BorderLayout.CENTER);
        panel.add(header, BorderLayout.NORTH);

        // CardLayout — по одному скроллу на каждый чат
        chatArea = new JPanel(new CardLayout());
        chatArea.setBackground(C_BG_CHAT);
        for (int i = 0; i < CHATS.length; i++) {
            JPanel content = new JPanel();
            content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
            content.setBackground(C_BG_CHAT);
            content.setBorder(new EmptyBorder(8, 10, 8, 10));
            chatContents.set(i, content);

            JScrollPane scroll = new JScrollPane(content);
            scroll.setBorder(null);
            scroll.getViewport().setBackground(C_BG_CHAT);
            scroll.getVerticalScrollBar().setUnitIncrement(16);
            chatArea.add(scroll, String.valueOf(i));
        }
        panel.add(chatArea, BorderLayout.CENTER);
        panel.add(buildInputBar(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildInputBar() {
        JPanel bar = new JPanel(new BorderLayout(8, 0));
        bar.setBackground(C_BG_INPUT);
        bar.setBorder(new EmptyBorder(8, 12, 8, 12));

        inputField = new JTextField();
        inputField.setBackground(C_BG_APP);
        inputField.setForeground(C_TEXT_MAIN);
        inputField.setCaretColor(C_TEXT_MAIN);
        inputField.setFont(F_MAIN);
        inputField.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(C_DIVIDER),
                new EmptyBorder(6, 10, 6, 10)));

        JButton sendBtn  = makeBtn("Отправить ➤");
        JButton fwdBtn   = makeBtn("↩ Переслать");
        JButton photoBtn = makeBtn("🖼 Фото");
        JButton videoBtn = makeBtn("🎬 Видео");

        sendBtn.setBackground(C_ACCENT);
        sendBtn.setForeground(Color.WHITE);

        sendBtn.addActionListener(e  -> sendTextMessage());
        inputField.addActionListener(e -> sendTextMessage());
        fwdBtn.addActionListener(e   -> forwardMessage());
        photoBtn.addActionListener(e -> sendImageMessage());
        videoBtn.addActionListener(e -> sendVideoMessage());

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        btns.setOpaque(false);
        btns.add(photoBtn);
        btns.add(videoBtn);
        btns.add(fwdBtn);
        btns.add(sendBtn);

        bar.add(inputField, BorderLayout.CENTER);
        bar.add(btns,       BorderLayout.EAST);
        return bar;
    }

    private JPanel buildLogPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(C_BG_LOG);

        JLabel title = new JLabel("  🔍 Лог паттернов — что происходит под капотом");
        title.setFont(F_BOLD);
        title.setForeground(C_LOG_PATTERN);
        title.setBorder(new EmptyBorder(5, 8, 5, 8));
        title.setBackground(new Color(15, 20, 22));
        title.setOpaque(true);
        panel.add(title, BorderLayout.NORTH);

        patternLog = new JTextArea();
        patternLog.setEditable(false);
        patternLog.setBackground(C_BG_LOG);
        patternLog.setForeground(C_LOG_PATTERN);
        patternLog.setFont(F_MONO);
        patternLog.setBorder(new EmptyBorder(4, 10, 4, 10));

        JScrollPane scroll = new JScrollPane(patternLog);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ════════════════════════════════════════════════════════════════════════
    // ЛОГИКА ДЕЙСТВИЙ
    // ════════════════════════════════════════════════════════════════════════

    private void selectChat(int idx) {
        if (selectedChat >= 0 && selectedChat < chatPanels.size())
            chatPanels.get(selectedChat).setBackground(C_BG_SIDEBAR);

        selectedChat = idx;
        chatPanels.get(idx).setBackground(C_SELECTED);
        headerName.setText(CHATS[idx][0]);
        headerStatus.setText(idx >= 2 ? "бот" : "в сети");
        ((CardLayout) chatArea.getLayout()).show(chatArea, String.valueOf(idx));

        // Singleton — один менеджер для всех подключений
        WebSocketManager.getInstance().connect(CHATS[idx][0]);
        logPattern("Singleton",
                "WebSocketManager.getInstance().connect(\"" + CHATS[idx][0] + "\")");
    }

    private void sendTextMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        // Factory Method — подкласс создаёт нужный тип
        MessageCreator creator = new TextMessageCreator();
        Message msg = creator.createMessage("Вы", text);
        logPattern("Factory Method",
                "TextMessageCreator.createMessage() → " + msg.getClass().getSimpleName());

        // Builder — собираем RichMessage пошагово
        RichMessage rich = new RichMessageBuilder("Вы")
                .setText(text).build();
        logPattern("Builder", "RichMessageBuilder.setText().build() → RichMessage");

        addBubble(selectedChat, "Вы", text, "out", null);
        lastMessageText   = text;
        lastMessageSender = "Вы";
        inputField.setText("");

        // Автоответ
        if (selectedChat < 2) {
            String reply = autoReply(text, CHATS[selectedChat][0]);
            RichMessage replyRich = new RichMessageBuilder(CHATS[selectedChat][0])
                    .setText(reply).build();
            logPattern("Factory Method", "TextMessageCreator → TextMessage (автоответ)");
            logPattern("Builder", "RichMessageBuilder.build() → RichMessage (автоответ)");
            addBubble(selectedChat, CHATS[selectedChat][0], reply, "in", null);
        }
    }

    private void sendImageMessage() {
        MessageCreator creator = new ImageMessageCreator();
        Message msg = creator.createMessage("Вы", "photo.jpg");
        logPattern("Factory Method",
                "ImageMessageCreator.createMessage() → " + msg.getClass().getSimpleName());

        RichMessage rich = new RichMessageBuilder("Вы")
                .setText("📷 Фотография").setImage("photo.jpg").build();
        logPattern("Builder", "RichMessageBuilder.setImage().build() → RichMessage");

        addBubble(selectedChat, "Вы", "📷 Фотография", "out", null);
        lastMessageText   = "📷 Фотография";
        lastMessageSender = "Вы";
    }

    private void sendVideoMessage() {
        MessageCreator creator = new VideoMessageCreator(30);
        Message msg = creator.createMessage("Вы", "video.mp4");
        logPattern("Factory Method",
                "VideoMessageCreator.createMessage() → " + msg.getClass().getSimpleName());

        RichMessage rich = new RichMessageBuilder("Вы")
                .setText("🎬 Видео (30 сек)").build();
        logPattern("Builder", "RichMessageBuilder.setText().build() → RichMessage");

        addBubble(selectedChat, "Вы", "🎬 Видео (30 сек)", "out", null);
        lastMessageText   = "🎬 Видео (30 сек)";
        lastMessageSender = "Вы";
    }

    private void forwardMessage() {
        if (lastMessageText == null) {
            addBubble(selectedChat, "Система",
                    "⚠ Нет сообщения для пересылки. Сначала отправьте что-нибудь.", "sys", null);
            return;
        }
        // Prototype — клонируем существующее сообщение
        ChatMessage original  = new ChatMessage(lastMessageSender, lastMessageText, "сейчас");
        ChatMessage forwarded = original.clone();
        forwarded.setForwardedBy("Вы");
        logPattern("Prototype",
                "ChatMessage.cloneMessage() → копия [" + lastMessageSender + ": \"" + lastMessageText + "\"]");

        addBubble(selectedChat, "Вы", lastMessageText, "fwd",
                "↩ Переслано от " + lastMessageSender);
    }

    private void addMessage(int chatIdx, String sender, String text, String type) {
        RichMessage rich = new RichMessageBuilder(sender)
                .setText(text).build();
        logPattern("Builder", "RichMessageBuilder.build() → RichMessage [" + sender + "]");
        addBubble(chatIdx, sender, text, type, null);
    }

    // ════════════════════════════════════════════════════════════════════════
    // РЕНДЕР ПУЗЫРЕЙ СООБЩЕНИЙ
    // ════════════════════════════════════════════════════════════════════════

    private void addBubble(int chatIdx, String sender, String text, String type, String fwdTag) {
        boolean isOut = type.equals("out");

        JPanel wrapper = new JPanel(new FlowLayout(isOut ? FlowLayout.RIGHT : FlowLayout.LEFT, 4, 2));
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));

        JPanel bubble = new JPanel();
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));

        Color bg = switch (type) {
            case "out" -> C_BG_MSG_OUT;
            case "bot" -> C_BG_MSG_BOT;
            case "fwd" -> C_BG_MSG_FWD;
            case "sys" -> new Color(50, 40, 20);
            default    -> C_BG_MSG_IN;
        };
        bubble.setBackground(bg);
        bubble.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(bg.brighter(), 1),
                new EmptyBorder(6, 10, 6, 10)));

        if (!isOut && !type.equals("sys")) {
            JLabel senderLbl = new JLabel(sender);
            senderLbl.setFont(F_BOLD);
            senderLbl.setForeground(C_ACCENT);
            senderLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            bubble.add(senderLbl);
        }

        if (fwdTag != null) {
            JLabel fwdLbl = new JLabel(fwdTag);
            fwdLbl.setFont(F_SMALL);
            fwdLbl.setForeground(C_TEXT_DIM);
            fwdLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            bubble.add(fwdLbl);
            bubble.add(Box.createRigidArea(new Dimension(0, 2)));
        }

        JLabel textLbl = new JLabel("<html><body style='width:260px'>" + text + "</body></html>");
        textLbl.setFont(F_MAIN);
        textLbl.setForeground(C_TEXT_MAIN);
        textLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        bubble.add(textLbl);

        String time = new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date());
        JLabel timeLbl = new JLabel(time);
        timeLbl.setFont(F_SMALL);
        timeLbl.setForeground(C_TEXT_TIME);
        timeLbl.setAlignmentX(isOut ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);
        bubble.add(timeLbl);

        wrapper.add(bubble);
        JPanel content = chatContents.get(chatIdx);
        content.add(wrapper);
        content.add(Box.createRigidArea(new Dimension(0, 4)));
        content.revalidate();
        content.repaint();

        SwingUtilities.invokeLater(() -> {
            Container c = content.getParent();
            while (c != null && !(c instanceof JScrollPane)) c = c.getParent();
            if (c instanceof JScrollPane sp) {
                JScrollBar bar = sp.getVerticalScrollBar();
                bar.setValue(bar.getMaximum());
            }
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    // ЛОГ ПАТТЕРНОВ
    // ════════════════════════════════════════════════════════════════════════

    private void logPattern(String pattern, String detail) {
        String time = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date());
        String line = "[" + time + "] [" + pattern + "]  " + detail + "\n";
        SwingUtilities.invokeLater(() -> {
            patternLog.append(line);
            patternLog.setCaretPosition(patternLog.getDocument().getLength());
        });
    }

    // ════════════════════════════════════════════════════════════════════════
    // ВСПОМОГАТЕЛЬНЫЕ
    // ════════════════════════════════════════════════════════════════════════

    private String autoReply(String input, String name) {
        String low = input.toLowerCase();
        if (low.contains("привет") || low.contains("здравствуй")) return "Привет! 👋";
        if (low.contains("как дела"))  return "Всё отлично, спасибо! А у тебя?";
        if (low.contains("встреч"))    return "Да, встреча в пятницу в 15:00!";
        if (low.contains("спасибо"))   return "Пожалуйста! 😊";
        return name + ": понял, отвечу позже 👍";
    }

    private JButton makeBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(F_SMALL);
        btn.setForeground(C_TEXT_MAIN);
        btn.setBackground(C_BG_INPUT);
        btn.setBorder(new CompoundBorder(
                BorderFactory.createLineBorder(C_DIVIDER),
                new EmptyBorder(4, 8, 4, 8)));
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(C_HOVER); }
            public void mouseExited(MouseEvent e)  { btn.setBackground(C_BG_INPUT); }
        });
        return btn;
    }

    private JSeparator makeDivider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(C_DIVIDER);
        sep.setBackground(C_DIVIDER);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }
}