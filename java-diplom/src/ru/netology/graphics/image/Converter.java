package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Converter implements TextGraphicsConverter {
    private int maxWidth;
    private int maxHeight;
    private double maxRatio;
    TextColorSchema schema = new Schema();

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {
        BufferedImage img = ImageIO.read(new URL(url));
        double imgWidth = img.getWidth();
        double imgHeight = img.getHeight();
        double ratio;
        if (imgHeight > imgWidth) {
            ratio = imgHeight / imgWidth;
        } else {
            ratio = imgWidth / imgHeight;
        }
        if (ratio > maxRatio) {
            throw new BadImageSizeException(ratio, maxRatio);
        }
        int newWidth;
        int newHeight;
        double kWidth = imgWidth / maxWidth;
        double kHeight = imgHeight / maxHeight;
        double k = Math.max(kWidth, kHeight);
        if (kWidth > 1 || kHeight > 1) {
            newWidth = (int) (imgWidth / k);
            newHeight = (int) (imgHeight / k);
        } else {
            newWidth = (int) imgWidth;
            newHeight = (int) imgHeight;
        }
        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH);
        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = bwImg.createGraphics();
        graphics.drawImage(scaledImage, 0, 0, null);
        ImageIO.write(bwImg, "png", new File("out.png"));   // Сохранили
        WritableRaster bwRaster = bwImg.getRaster();
        String[][] textImg = new String[newHeight][newWidth];
        StringBuilder convertImage = new StringBuilder();
        for (int h = 0; h < (newHeight); h++) {
            if (!convertImage.toString().equals("")) {
                convertImage.append("\n");
            }
            for (int w = 0; w < (newWidth); w++) {
                int color = bwRaster.getPixel(w, h, new int[3])[0];
                char c = schema.convert(color);
                textImg[h][w] = String.valueOf(c);
                convertImage.append(textImg[h][w]).append(textImg[h][w]);
            }
        }
        return convertImage.toString();
    }

    @Override
    public void setMaxWidth(int width) {
        this.maxWidth = width;
    }

    @Override
    public void setMaxHeight(int height) {
        this.maxHeight = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) {
        this.maxRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema schema) {
        this.schema = schema;
    }
}
