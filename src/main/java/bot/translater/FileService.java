package bot.translater;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.GetFile;
import com.pengrad.telegrambot.response.GetFileResponse;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Class for working with files:
 * downloading from telegram and saving to local machine
 * reading data from local machine
 * writing data to local machine
 */
public class FileService {

    private static final String SAVE_PATH = "src/main/resources/static/";
    private static final Logger LOGGER = LoggerFactory.getLogger(FileService.class);

    /**
     * Download file from telegram and save to local machine
     * @param downloadFilePath link to download file from telegram
     * @param fileName file name that will be saved on local machine
     * @return true if file downloaded successfully
     */
    public void fileDownload(String downloadFilePath,String fileName) {
        try(BufferedInputStream in = new BufferedInputStream(new java.net.URL(downloadFilePath).openStream());
            FileOutputStream fileOutputStream = new FileOutputStream(SAVE_PATH + fileName)) {
            byte[] data = new byte[1024];
            int count;
            while ((count = in.read(data, 0, 1024)) != -1) {
                fileOutputStream.write(data, 0, count);
            }
            LOGGER.info("File downloaded successfully:" + fileName);
        }catch (IOException e){
            LOGGER.error("Error occurred while downloading file:" + fileName + " " + e);
        }
    }

    /**
     * Read data from local machine
     * @param fileName name of file that will be read
     * @return String data from file
     * @throws IOException if file not found
     */
    public String fileReadPdf(String fileName) throws IOException {
        File file = new File(SAVE_PATH + fileName);
        PdfReader pdfReader = new PdfReader(file.getCanonicalPath());
        int pages = pdfReader.getNumberOfPages();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 1; i <= pages; i++) {
            stringBuilder.append(PdfTextExtractor.getTextFromPage(pdfReader, i));
        }
        LOGGER.info("File read successfully:" + fileName);
        return stringBuilder.toString();
    }

    /**
     * Write data to local machine
     * @param document data that will be written to file
     * @return name of file that was written
     * @throws IOException if file not found
     * @throws DocumentException if something went wrong with writing to file
     */
    public String pdfFileWriter(String document,String fileName) throws IOException, DocumentException {
        String fullPath = SAVE_PATH + fileName;
        FileOutputStream fos = new FileOutputStream(fullPath);
        Document doc = new Document();
        PdfWriter writer = PdfWriter.getInstance(doc, fos);
        doc.open();
        doc.add(new com.itextpdf.text.Paragraph(document));
        doc.close();
        writer.close();
        LOGGER.info("File written successfully:" + fileName);
        return fileName;
    }

    /**
     * Get download file path
     * @param telegramBot telegram bot that will be used to get file
     * @param update update that contains file
     * @return download file path
     */
    public String getSavePath(TelegramBot telegramBot, Update update) {
        // download file from telegram and save to local machine
        com.pengrad.telegrambot.model.Document document = update.message().document();
        //get download file path ex -> https://api.telegram.org/file/<BOT_TOKEN>/<FILE_PATH>
        GetFile getFile = new GetFile(document.fileId());
        GetFileResponse response = telegramBot.execute(getFile);
        com.pengrad.telegrambot.model.File file = response.file();
        // download file path
        return telegramBot.getFullFilePath(file);
    }

}
