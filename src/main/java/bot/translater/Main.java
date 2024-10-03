package bot.translater;

import com.itextpdf.text.DocumentException;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import java.io.IOException;

public class Main {
    private final static TelegramBot telegramBot = new TelegramBot("7474179192:AAFHZjuFrL-eb2mVRO-nImOZXIxwlPRxlXA");
    private final static TelegramHandler telegramHandler = new TelegramHandler(telegramBot, new FileService());

    public static void main(String[] args) {
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                try {
                    telegramHandler.documentHandler(update);
                } catch (IOException | DocumentException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}
