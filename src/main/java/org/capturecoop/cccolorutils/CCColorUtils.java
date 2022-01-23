package org.capturecoop.cccolorutils;

import java.awt.*;
import java.awt.image.BufferedImage;

public class CCColorUtils {
    public enum DIRECTION {VERTICAL, HORIZONTAL}

    public static String rgb2hex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

    public static Color hex2rgb(String colorStr) {
        return new Color(Integer.valueOf(colorStr.substring(1, 3), 16), Integer.valueOf( colorStr.substring(3, 5), 16), Integer.valueOf(colorStr.substring(5, 7), 16));
    }

    public static BufferedImage createAlphaBar(Color color, int width, int height, DIRECTION direction) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = (Graphics2D) image.getGraphics();

        int amount = width;
        float step = 255F / width;
        float alpha = 0;

        if(direction == DIRECTION.VERTICAL) {
            amount = height;
            step = 255F / height;
        }

        for(int pos = 0; pos < amount; pos++) {
            g.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), (int)alpha));
            switch(direction) {
                case VERTICAL: g.drawLine(0, pos, width, pos); break;
                case HORIZONTAL: g.drawLine(pos, 0, pos, height); break;
            }
            alpha += step;
        }

        g.dispose();
        return image;
    }

    public static BufferedImage createHSVHueBar(int width, int height, DIRECTION direction) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) image.getGraphics();

        float hue = 0F;
        int amount = width;
        float step = 1F / width;

        if(direction == DIRECTION.VERTICAL) {
            amount = height;
            step = 1F / height;
        }

        for(int pos = 0; pos < amount; pos++) {
            g.setColor(new CCHSB(hue, 1F, 1F).toRGB());
            switch(direction) {
                case VERTICAL: g.drawLine(0, pos, width, pos); break;
                case HORIZONTAL: g.drawLine(pos, 0, pos, height); break;
            }
            hue += step;
        }

        g.dispose();
        return image;
    }

    public static BufferedImage createHSVBox(int width, int height, float hue) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = (Graphics2D) image.getGraphics();

        float brightness = 1F;
        float saturation;
        float stepHeight = 1F / height;
        float stepWidth = 1F / width;

        for(int y = 0; y < height; y++) {
            saturation = 0;
            for(int x = 0; x < width; x++) {
                saturation += stepWidth;
                g.setColor(new CCHSB(hue, saturation, brightness).toRGB());
                g.drawLine(x, y, x, y);
            }
            brightness -= stepHeight;
        }

        g.dispose();
        return image;
    }
}
