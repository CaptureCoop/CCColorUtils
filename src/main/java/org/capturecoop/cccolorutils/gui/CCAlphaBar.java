package org.capturecoop.cccolorutils.gui;

import org.capturecoop.cccolorutils.CCColor;
import org.capturecoop.cccolorutils.CCColorUtils;
import org.capturecoop.ccutils.math.CCVector2Float;
import org.capturecoop.ccutils.utils.CCMathUtils;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class CCAlphaBar extends JPanel {
    private CCColor color;
    private float position;
    private CCColorUtils.DIRECTION direction;

    private final static int MARGIN = 10;
    private final static int SEL_MARGIN = 4;
    private final static int SEL_MARGIN_OFF = 2;

    private static BufferedImage gridImage;

    private boolean isDragging = false;


    private BufferedImage buffer;
    private boolean dirty = true;

    public CCAlphaBar(CCColor color, CCColorUtils.DIRECTION direction, boolean alwaysGrab) {
        this.color = color;
        this.direction = direction;

        if(gridImage == null) {
            try {
                gridImage = ImageIO.read(CCAlphaBar.class.getResource("/org/capturecoop/cccolorutils/transparent_grid_4x4.png"));
            } catch (IOException ioException) {
                //Do nothing, that image is not essential.
            }
        }

        updateAlpha();
        color.addChangeListener(e -> updateAlpha());
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                Rectangle rect = getSelectRect();
                if(rect == null)
                    return;

                if(rect.contains(mouseEvent.getPoint()) && !alwaysGrab)
                    isDragging = true;
            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                isDragging = false;
                if(alwaysGrab)
                    execute(mouseEvent.getX(), mouseEvent.getY());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                if(isDragging || alwaysGrab)
                    execute(mouseEvent.getX(), mouseEvent.getY());
            }
        });
    }

    private void execute(int x, int y) {
        dirty = true;
        int pos = y;
        int size = getHeight();
        if(direction == CCColorUtils.DIRECTION.HORIZONTAL) {
            pos = x;
            size = getWidth();
        }
        float percentage = (pos * 100F) / size;
        position = new CCVector2Float(percentage / 100F, 0).limitX(0, 1).getX();
        Color oldColor = color.getPrimaryColor();
        color.setPrimaryColor(new Color(oldColor.getRed(), oldColor.getGreen(), oldColor.getBlue(), CCMathUtils.clampInt((int)(position * 255), 0, 255)));
        repaint();
    }

    private void updateAlpha() {
        if(!isDragging) {
            dirty = true;
            position = ((color.getPrimaryColor().getAlpha() * 100F) / 255F) / 100F;
            repaint();
        }
    }

    private int getSizeX() {
        return getWidth() - MARGIN;
    }

    private int getSizeY() {
        return getHeight() - MARGIN;
    }

    private Rectangle getSelectRect() {
        switch(direction) {
            case VERTICAL:
                int yPos = (int) (getSizeY() / (1 / position)) + MARGIN / 2;
                return new Rectangle(SEL_MARGIN_OFF, yPos - SEL_MARGIN, getWidth() - SEL_MARGIN_OFF * 2, SEL_MARGIN * 2);
            case HORIZONTAL:
                int xPos = (int) (getSizeX() / (1 / position)) + MARGIN / 2;
                return new Rectangle(xPos - SEL_MARGIN, SEL_MARGIN_OFF, SEL_MARGIN * 2, getHeight() - SEL_MARGIN_OFF * 2);
        }
        return null;
    }

    @Override
    public void paint(Graphics g) {
        if(!dirty && buffer != null) {
            g.drawImage(buffer, 0, 0, this);
            return;
        }

        if(buffer == null || !(buffer.getWidth() == getWidth() && buffer.getHeight() == getHeight())) {
            buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        }

        Graphics bufferGraphics = buffer.getGraphics();
        dirty = false;

        int sizeX = getSizeX();
        int sizeY = getSizeY();
        bufferGraphics.setColor(getBackground());
        bufferGraphics.fillRect(0, 0, getWidth(), getHeight());
        int amount = sizeX / sizeY;
        if(gridImage != null) {
            for (int i = 0; i < amount; i++) {
                bufferGraphics.drawImage(gridImage, MARGIN / 2 + i * sizeY, MARGIN / 2, sizeY, sizeY, this);
            }
        }
        bufferGraphics.drawImage(CCColorUtils.createAlphaBar(color.getPrimaryColor(), sizeX, sizeY, direction), MARGIN / 2, MARGIN / 2, sizeX, sizeY, this);
        bufferGraphics.setColor(Color.BLACK);
        bufferGraphics.drawRect(MARGIN / 2 - 1, MARGIN / 2 - 1, sizeX + 1, sizeY + 1);
        bufferGraphics.setColor(Color.GRAY);
        Rectangle rect = getSelectRect();
        bufferGraphics.fillRect(rect.x, rect.y, rect.width, rect.height);

        bufferGraphics.dispose();
        g.drawImage(buffer, 0, 0, this);
    }
}