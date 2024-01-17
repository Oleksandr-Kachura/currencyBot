package com.example.currencyBot.bot;

import com.example.currencyBot.exception.ServerException;
import com.example.currencyBot.service.ExchangeRatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static java.util.Objects.nonNull;

@Component
public class ExchangeRatesBot extends TelegramLongPollingBot {

    private static final String START_COMMAND = "/start";
    private static final String USD_COMMAND = "/usd";
    private static final String EUR_COMMAND = "/eur";
    private static final String HELP_COMMAND = "/help";


    @Autowired
    public ExchangeRatesService exchangeRatesService;

    public ExchangeRatesBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){
            Message message = update.getMessage();
            String chatId = message.getChatId().toString();
            String text = message.getText().toLowerCase();
            switch (text) {
                case START_COMMAND:
                    startCommand(chatId);
                    break;
                case USD_COMMAND:
                    sendCurrencyInfo(chatId,"USD");
                    break;
                case EUR_COMMAND:
                    sendCurrencyInfo(chatId,"EUR");
                    break;
                case HELP_COMMAND:
                    helpCommand(chatId);
                    break;
                default:
                    unknownCommand(chatId);
            }
        }
    }


    private void startCommand(String chatId) {
        sendMessage(chatId, "Welcome to ExchangeRatesBot! Use " + USD_COMMAND + " or " + EUR_COMMAND + " to get currency rates.");
    }

    private void helpCommand(String chatId) {
        sendMessage(chatId, "Available commands:\n" + USD_COMMAND + " - Get USD exchange rate\n" + EUR_COMMAND + " - Get EUR exchange rate\n" + HELP_COMMAND + " - Show available commands");
    }

    private void unknownCommand(String chatId) {
        sendMessage(chatId, "Unknown command. Use " + HELP_COMMAND + " to see available commands.");
    }

    public void sendCurrencyInfo(String chatId, String currencyCode) {
        try {
           String currencyInfo = exchangeRatesService.getCurrencyInfo(currencyCode);
            if (nonNull(currencyInfo)) {
                String message = String.format("Current %s currency is %s", currencyCode, currencyInfo);
                sendMessage(chatId, message);
            } else {
                sendMessage(chatId, "Unable to fetch " + currencyCode + " exchange rate.");
            }
        } catch (ServerException e) {
            sendMessage(chatId, "Error fetching exchange rate data.");
        }
    }

    private void sendMessage(String chatId, String text) {
        try {
            execute(createMessage(chatId, text));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private SendMessage createMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        return message;
    }

    @Override
    public String getBotUsername() {
        return "alex_ua_currency_bot";
    }

}
