package bot.translater;

import com.itextpdf.text.DocumentException;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;

import java.io.IOException;

public class Main {
    private final static TelegramBot telegramBot = new TelegramBot("YOUR_BOT_TOKEN");
    private final static TelegramHandler telegramHandler = new TelegramHandler(telegramBot, new FileService(),new Translator());

    public static void main(String[] args) {
        telegramBot.setUpdatesListener(updates -> {
            for (Update update : updates) {
                try {
                    telegramHandler.handleDocument(update);
                } catch (IOException | DocumentException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}
