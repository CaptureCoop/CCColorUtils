package org.capturecoop.cccolorutils;

import org.capturecoop.ccutils.math.CCVector2Float;
import org.capturecoop.ccutils.math.CCVector2Int;
import org.capturecoop.ccutils.utils.CCStringUtils;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;

public class CCColor {
    private Color primaryColor;
    private Color secondaryColor;
    private CCVector2Float point1;
    private CCVector2Float point2;
    private boolean isGradient = false;

    private final ArrayList<ChangeListener> listeners = new ArrayList<>();

    public CCColor() {
        primaryColor = Color.WHITE;
    }

    public CCColor(Color color) {
        this.primaryColor = color;
    }

    public CCColor(CCColor color) {
        primaryColor = color.primaryColor;
        secondaryColor = color.secondaryColor;
        isGradient = color.isGradient;
        if(color.point1 != null) point1 = new CCVector2Float(color.point1);
        if(color.point2 != null) point2 = new CCVector2Float(color.point2);
    }

    public CCColor(CCColor color, int alpha) {
        primaryColor = new Color(color.primaryColor.getRed(), color.primaryColor.getGreen(), color.primaryColor.getBlue(), alpha);
        if(color.secondaryColor != null) secondaryColor = new Color(color.secondaryColor.getRed(), color.secondaryColor.getGreen(), color.secondaryColor.getBlue(), alpha);
        isGradient = color.isGradient;
        if(color.point1 != null) point1 = new CCVector2Float(color.point1);
        if(color.point2 != null) point2 = new CCVector2Float(color.point2);
    }

    public CCColor(Color primaryColor, Color secondaryColor, boolean isGradient) {
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        this.isGradient = isGradient;
    }

    public CCColor(Color c, int alpha) {
        primaryColor = new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
    }

    public CCColor(int r, int g, int b, int a) {
        primaryColor = new Color(r, g, b, a);
    }

    public Color getPrimaryColor() {
        return primaryColor;
    }

    public Color getSecondaryColor() {
        return secondaryColor;
    }

    public void setPrimaryColor(Color color, int alpha) {
        if(color == null) {
            setPrimaryColor(null);
            return;
        }
        setPrimaryColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
    }

    public void setPrimaryColor(Color color) {
        primaryColor = color;
        alertChangeListeners();
    }

    public void setSecondaryColor(Color color, int alpha) {
        if(color == null) {
            setSecondaryColor(null);
            return;
        }
        setSecondaryColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha));
    }

    public void setSecondaryColor(Color color) {
        secondaryColor = color;
        alertChangeListeners();
    }

    public void setPoint1(CCVector2Float point) {
        if(point != null) {
            point1 = new CCVector2Float(point);
            point1.limit(0f, 1f);
        } else {
            point1 = null;
        }
        alertChangeListeners();
    }

    public void setPoint2(CCVector2Float point) {
        if(point != null) {
            point2 = new CCVector2Float(point);
            point2.limit(0f, 1f);
        } else {
            point2 = null;
        }
        alertChangeListeners();
    }

    public CCVector2Float getPoint1() {
        return point1;
    }

    public CCVector2Float getPoint2() {
        return point2;
    }

    public Paint getGradientPaint(int width, int height, int posX, int posY) {
        if(!isGradient) {
            return primaryColor;
        }

        if(secondaryColor == null)
            secondaryColor = primaryColor;

        if(point1 == null)
            point1 = new CCVector2Float(0f, 0f);
        if(point2 == null)
            point2 = new CCVector2Float(1f, 1f);

        CCVector2Int point1int = new CCVector2Int(point1.getX() * width, point1.getY() * height);
        CCVector2Int point2int = new CCVector2Int(point2.getX() * width, point2.getY() * height);
        return new GradientPaint(point1int.getX() + posX, point1int.getY() + posY, primaryColor, point2int.getX() + posX, point2int.getY() + posY, secondaryColor);
    }

    public Paint getGradientPaint(int width, int height) {
        return getGradientPaint(width, height, 0, 0);
    }

    private void alertChangeListeners() {
        for(ChangeListener listener : listeners) {
            listener.stateChanged(new ChangeEvent(this));
        }
    }

    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }

    public String toSaveString() {
        String string = CCColorUtils.rgb2hex(primaryColor);
        if(primaryColor.getAlpha() != 255)
            string += "_a" + primaryColor.getAlpha();

        if(point1 != null)
            string += "_x" + point1.getX() + "_y" + point1.getY();

        string += "_G" + isGradient();

        if(secondaryColor != null) {
            string += "___" + CCColorUtils.rgb2hex(secondaryColor);
            if(secondaryColor.getAlpha() != 255)
                string += "_a" + secondaryColor.getAlpha();
            if (point2 != null)
                string += "_x" + point2.getX() + "_y" + point2.getY();
        }
        return string;
    }

    public static CCColor fromSaveString(String string) {
        CCColor newColor = new CCColor();
        int index = 0;
        for(String part : string.split("___")) {
            int alpha = -1;
            Color color = null;

            float defaultPos = 0;
            if(index != 0) defaultPos = 1;
            CCVector2Float pos = new CCVector2Float(defaultPos, defaultPos);
            for(String str : part.split("_")) {
                switch(str.charAt(0)) {
                    case '#': color = CCColorUtils.hex2rgb(str); break;
                    case 'a': alpha = Integer.parseInt(str.substring(1)); break;
                    case 'x': pos.setX(Float.parseFloat(str.substring(1))); break;
                    case 'y': pos.setY(Float.parseFloat(str.substring(1))); break;
                    case 'G': newColor.isGradient = Boolean.parseBoolean(str.substring(1)); break;
                }

                if(alpha == -1 && color != null)
                    alpha = color.getAlpha();

                if(color != null) {
                    if(index == 0)
                        newColor.primaryColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
                    else if(index == 1)
                        newColor.secondaryColor = new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
                }


                if(index == 0) newColor.point1 = pos;
                else if(index == 1) newColor.point2 = pos;
            }
            index++;
        }
        return newColor;
    }

    public void loadFromCCColor(CCColor otherColor) {
        primaryColor = otherColor.primaryColor;
        secondaryColor = otherColor.secondaryColor;
        point1 = otherColor.point1;
        point2 = otherColor.point2;
        isGradient = otherColor.isGradient;
        alertChangeListeners();
    }

    public void setIsGradient(boolean bool) {
        isGradient = bool;
    }

    public boolean isGradient() {
        return isGradient;
    }

    public boolean isValidGradient() {
        return secondaryColor != null;
    }

    public String toString() {
        return CCStringUtils.format("CCColor primaryColor: %c secondaryColor: %c point1: %c point2: %c isGradient: %c", primaryColor, secondaryColor, point1, point2, isGradient);
    }
}