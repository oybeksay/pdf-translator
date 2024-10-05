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

class TelegramHandler {
    private static final String PDF_MIME_TYPE = "application/pdf";
    private final TelegramBot telegramBot;
    private final FileService fileService;
    private final Translator translator;

    TelegramHandler(TelegramBot telegramBot, FileService fileService, Translator translator) {
        this.telegramBot = telegramBot;
        this.fileService = fileService;
        this.translator = translator;
    }

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
        String PATH = "src/main/resources/static/";
        telegramBot.execute(new SendDocument(update.message().chat().id(), new File(PATH + generatedPdfFileName)));
    }
}