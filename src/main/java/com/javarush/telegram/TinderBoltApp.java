package com.javarush.telegram;

import com.javarush.telegram.ChatGPTService;
import com.javarush.telegram.DialogMode;
import com.javarush.telegram.MultiSessionTelegramBot;
import com.javarush.telegram.UserInfo;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;

public class TinderBoltApp extends MultiSessionTelegramBot {
    public static final String TELEGRAM_BOT_NAME = "t.me/test_myAI88_bot"; //TODO: добавь имя бота в кавычках
    public static final String TELEGRAM_BOT_TOKEN = "8145688719:AAG_vtWGuib_SGHZaOqG0pya2nr8Ne8sXLg"; //TODO: добавь токен бота в кавычках
    public static final String OPEN_AI_TOKEN = "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"; //TODO: добавь токен ChatGPT в кавычках

    private ChatGPTService chatGPT = new ChatGPTService(OPEN_AI_TOKEN);

    private DialogMode currentMode = null;

    private ArrayList<String> list = new ArrayList<>();

    public TinderBoltApp() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
    }

    @Override
    public void onUpdateEventReceived(Update update) {
        //TODO: основной функционал бота будем писать здесь
        String message = getMessageText();

        if (message.equals("/start")) {
            currentMode = DialogMode.MAIN;
            sendPhotoMessage("main");
            String text = loadMessage("main");
            sendTextMessage(text);

            showMainMenu("главное меню бота", "/start",
                    "генерация Tinder-профля \uD83D\uDE0E", "/profile",
                    "сообщение для знакомства \uD83E\uDD70", "/opener",
                    "переписка от вашего имени \uD83D\uDE08", "/message",
                    "переписка со звездами \uD83D\uDD25", "/date",
                    "Обшение с ChatGpt", "/gpt");
            return;
        }

        if (message.equals("/gpt")) {
            currentMode = DialogMode.GPT;
            sendPhotoMessage("gpt");
            String text = loadMessage("gpt");
            sendTextMessage(text);
            return;
        }

        if (currentMode == DialogMode.GPT) {
            String prompt = loadPrompt("gpt");
            String answer = chatGPT.sendMessage(prompt, message);
            sendTextMessage(answer);
            return;
        }

        if (message.equals("/date")) {
            currentMode = DialogMode.DATE;
            sendPhotoMessage("date");
            String text = loadMessage("date");
            sendTextButtonsMessage(text,
                    "Ариана Гранде", "date_grande",
                    "Марго Робби", "date_robbie",
                    "Зендея", "date_zendaya",
                    "Раин Гослинг", "date_gosling",
                    "Том Харди", "date_hardy");
            return;
        }
        if (currentMode == DialogMode.DATE) {
            String query = getCallbackQueryButtonKey();
            if (query.startsWith("date_")) {
                sendPhotoMessage(query);
                sendTextMessage("Отличный выбор! \nТвоя задача пригласить девушку/парня на свидание ❤\uFE0F за 5 сообщений.");
                String prompt = loadPrompt(query);
                chatGPT.setPrompt(prompt);
                return;
            }
            Message msg = sendTextMessage("Подождите пару секунд - ChactGPT думает...");
            String answer = chatGPT.addMessage(message);
            updateTextMessage(msg,answer);
            return;
        }
        if (currentMode == DialogMode.DATE) {
            String query = getCallbackQueryButtonKey();
            if (query.startsWith("date_")) {
                sendPhotoMessage(query);
                sendTextMessage("Отличный выбор! \nТвоя задача пригласить девушку/парня на свидание ❤\uFE0F за 5 сообщений.");

                String prompt = loadPrompt(query);
                chatGPT.setPrompt(prompt);
                return;
            }
            Message msg = sendTextMessage("Подождите девушка набирает текст");
            String answer = chatGPT.addMessage(message);
            updateTextMessage(msg,answer);
            return;
        }
        if(message.equals("/message")){
            currentMode = DialogMode.MESSAGE;
            sendPhotoMessage("message");
            sendTextButtonsMessage("Пришлите в чат вашу переписку",
                    "Следуюшее собщение", "message_netx",
                    "Пригласить на свидание", "message_date");
            return;
        }
        if (currentMode == DialogMode.MESSAGE){
            String  query = getCallbackQueryButtonKey();
            if(query.startsWith("message_")) {
                String prompt = loadPrompt(query);
                String userChatHistory = String.join("\n\n", list);

                Message msg = sendTextMessage("Подождите пару секунд - ChactGPT думает...");
                String answer = chatGPT.sendMessage(prompt, userChatHistory);
                updateTextMessage(msg,answer);
                return;
            }

            list.add(message);
            return;
        }

    }
        public static void main (String[]args) throws TelegramApiException {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new TinderBoltApp());
        }
    }
