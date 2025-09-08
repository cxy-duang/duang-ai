package com.duang.ai.utils;

import lombok.experimental.UtilityClass;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.ai.chat.messages.AbstractMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@UtilityClass
public class AiUtils {

    private static final String IMAGE_FORMAT = "png";  // 图片格式，可以是jpg, png等

    private static final float DPI = 300f;

    @Nullable
    public static String getContentFromChatResponse(@Nullable ChatResponse chatResponse) {
        return Optional.ofNullable(chatResponse).map(ChatResponse::getResult).map(Generation::getOutput).map(AbstractMessage::getText).orElse(null);
    }

    public static List<Media> pdfToImages(InputStream inputStream) {
        List<Media> result = new ArrayList<>();
        try (RandomAccessRead readBuffer = new RandomAccessReadBuffer(inputStream)) {
            try (PDDocument document = Loader.loadPDF(readBuffer)) {
                PDFRenderer pdfRenderer = new PDFRenderer(document);
                // 获取PDF总页数
                int pageCount = document.getNumberOfPages();

                for (int i = 0; i < pageCount; i++) {
                    // 渲染PDF页面为BufferedImage
                    BufferedImage image = pdfRenderer.renderImageWithDPI(i, DPI);

                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    ImageIO.write(image, IMAGE_FORMAT, outputStream);
                    byte[] body = outputStream.toByteArray();
                    Media imageMedia = new Media(MediaType.IMAGE_PNG, new ByteArrayResource(body));
                    result.add(imageMedia);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }

}
