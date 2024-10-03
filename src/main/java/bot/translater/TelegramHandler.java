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

public class TelegramHandler {
    private final TelegramBot telegramBot;
    private final FileService fileService;
    private final Translator translator = new Translator();

    public TelegramHandler(TelegramBot telegramBot, FileService fileService) {
        this.telegramBot = telegramBot;
        this.fileService = fileService;
    }

    public void documentHandler(Update update) throws IOException, InterruptedException, DocumentException {

        if (update.message() != null) {
            Document document = update.message().document();
            if (document != null && document.mimeType().equalsIgnoreCase("application/pdf")) {
                // get download file path
                String savePath = fileService.getSavePath(telegramBot, update);
                // generated name for input file
                String generatedName = UUID.randomUUID() + ".pdf";
                // download file from telegram and save to local machine
                fileService.fileDownload(savePath, generatedName);
                // get data from local machine
                String content = fileService.fileReadPdf(generatedName);

                String translatedText = translator.translate(content, "en", "uz");

                String generatedPdfFileName = fileService.pdfFileWriter(translatedText, generatedName);
                telegramBot.execute(new SendDocument(update.message().chat().id(), new File(generatedPdfFileName)));
            } else {
                SendMessage message = new SendMessage(update.message().chat().id(), "Please upload a PDF file.");
                telegramBot.execute(message);
            }
        } else {
            System.out.println("Update message is null, no document found.");
        }
    }



}