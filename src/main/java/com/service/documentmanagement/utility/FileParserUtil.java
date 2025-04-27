package com.service.documentmanagement.utility;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ooxml.POIXMLProperties;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class FileParserUtil {

    public static DocumentData extractMetadataAndText(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null) throw new IOException("Invalid file name");

        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        String title = fileName.substring(0, fileName.lastIndexOf('.'));
        String author = "Unknown";
        String content;

        System.out.println(title);

        DocumentMetadataDTO metadata = new DocumentMetadataDTO();
        metadata.setTitle(title);
        metadata.setFileType(extension);

        if (extension.equalsIgnoreCase("pdf")) {
            try (PDDocument document = PDDocument.load(file.getInputStream())) {
                PDDocumentInformation info = document.getDocumentInformation();
                if (info != null && info.getAuthor() != null && !info.getAuthor().isBlank()) {
                    author = info.getAuthor();
                }
                PDFTextStripper stripper = new PDFTextStripper();
                content = stripper.getText(document);
            }
        } else if (extension.equalsIgnoreCase("docx")) {
            XWPFDocument doc = new XWPFDocument(file.getInputStream());
            POIXMLProperties.CoreProperties props = doc.getProperties().getCoreProperties();
            if (props.getCreator() != null && !props.getCreator().isBlank()) {
                author = props.getCreator();
            }
            XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
            content = extractor.getText();
        } else if (extension.equalsIgnoreCase("txt")) {
            content = new String(file.getBytes(), StandardCharsets.UTF_8);
        } else {
            throw new IOException("Unsupported file format: " + extension);
        }

        metadata.setAuthor(author);
        System.out.println(author);

        return new DocumentData(metadata, content);
    }

}
