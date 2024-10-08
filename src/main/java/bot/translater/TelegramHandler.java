package bot.translater;

import com.itextpdf.text.DocumentException;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Document;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendDocument;
import com.pengrad.telegrambot.request.SendMessage;

import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Class for handling document updates from Telegram.
 */
class TelegramHandler {
    private static final String PDF_MIME_TYPE = "application/pdf";
    private final TelegramBot telegramBot;
    private final FileService fileService;
    private final Translator translator;
    private final String PATH = "src/main/resources/static/";

    /**
     * Constructor for TelegramHandler.
     * @param telegramBot Telegram bot instance.
     * @param fileService File service instance.
     * @param translator Translator instance.
     */
    TelegramHandler(TelegramBot telegramBot, FileService fileService, Translator translator) {
        this.telegramBot = telegramBot;
        this.fileService = fileService;
        this.translator = translator;
    }

    /**
     * Handles document updates from Telegram.
     * @param update Update object containing document information.
     * @throws IOException If an I/O error occurs.
     * @throws InterruptedException If the thread is interrupted.
     * @throws DocumentException If a document exception occurs.
     */
    void handleDocument(Update update) throws IOException, InterruptedException, DocumentException {
        if (update.message() == null) {
            System.out.println("Update message is null, no document found.");
            return;
        }

        Document document = update.message().document();
        if (document == null || !PDF_MIME_TYPE.equalsIgnoreCase(document.mimeType())) {
            SendMessage message = new SendMessage(update.message().chat().id(), "Please upload a PDF file.");
            telegramBot.execute(message);
            return;
        }

        String savePath = fileService.getSavePath(telegramBot, update);
        String generatedName = UUID.randomUUID() + ".pdf";
        fileService.fileDownload(savePath, generatedName);

        String content = fileService.fileReadPdf(generatedName);
        String translatedText = translator.translate(content, "en", "uz");

        String generatedPdfFileName = fileService.pdfFileWriter(translatedText, generatedName);
        CompletableFuture.runAsync(() -> {
            telegramBot.execute(new SendDocument(update.message().chat().id(), new File(PATH + generatedPdfFileName)));
            deleteFileSafely(new File(PATH + generatedPdfFileName));
        });
    }

    private void deleteFileSafely(File file) {
        if (file.exists() && file.isFile()) {
            boolean deleted = file.delete();
            if (!deleted) {
                System.out.println("Failed to delete file: " + file.getName());
            }
        }
    }

}