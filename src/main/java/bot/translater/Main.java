package bot.translater;

import com.itextpdf.text.DocumentException;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;

import java.io.IOException;

final class Main {

    private static final TelegramBot TELEGRAM_BOT = new TelegramBot("7474179192:AAFHZjuFrL-eb2mVRO-nImOZXIxwlPRxlXA");
    private static final TelegramHandler TELEGRAM_HANDLER = new TelegramHandler(TELEGRAM_BOT, new FileService(), new Translator());

    private Main() {
    }

    public static void main(final String[] args) {
        TELEGRAM_BOT.setUpdatesListener(updates -> {
            for (final Update update : updates) {
                try {
                    if (update.message() != null && update.message().text() != null) {
                        TELEGRAM_BOT.execute(new SendMessage(update.message().chat().id(), "Please upload a PDF file."));
                        continue;
                    }
                    SendMessage message = new SendMessage(update.message().chat().id(), "Please wait ⏳...");
                    TELEGRAM_BOT.execute(message);
                    TELEGRAM_HANDLER.handleDocument(update);
                } catch (final IOException | DocumentException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }
}