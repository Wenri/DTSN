package org.wenri;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImagePanel extends JPanel {

    private BufferedImage currentImage = null;
    private Rectangle posRect = new Rectangle();
    private boolean showRect = false;
    private boolean cropToRect = false;

    public double getRatio() { return getRatio(currentImage); }
    public double getRatio(BufferedImage image) {
        Dimension panelSize = this.getSize();
        double imgHeight = image.getHeight();
        double imgWidth = image.getWidth();
        double ratio = panelSize.width / imgWidth;

        if (ratio * imgHeight > panelSize.height)
            ratio = panelSize.height / imgHeight;

        return ratio;
    }

    public void loadImage(File image) {
        try {
            currentImage = ImageIO.read( image );
            posRect = new Rectangle();
            this.repaint();
        } catch (IOException e) {
            currentImage = null;
            e.printStackTrace();
        }
        //forget the toImage() method, BufferedImage already extends the Image class.
        //And by the way, if you remove components and add new ones, use validate() instead.
    }

    public BufferedImage getImage() { return currentImage; }

    public void resizeImage(int newW, int newH) {
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = dimg.createGraphics();

        AffineTransform tx = new AffineTransform();
        tx.scale((double) newW / currentImage.getWidth(), (double) newH / currentImage.getHeight());
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BICUBIC);

        g2d.drawImage(currentImage, op,0, 0);
        g2d.dispose();

        currentImage = dimg;
    }

    public void setRectPos(Point pos) {
        double ratio = getRatio();
        posRect.x = (int) Math.round(pos.x / ratio - posRect.width / 2);
        posRect.y = (int) Math.round(pos.y / ratio - posRect.height / 2);
        //posRect.width = (int) Math.round(pos.width / ratio);
        //posRect.height = (int) Math.round(pos.height / ratio);
        if (showRect || cropToRect) this.repaint();
    }

    public void setRectSize(Dimension sz) {
        posRect.width = sz.width;
        posRect.height = sz.height;
        if (showRect || cropToRect) this.repaint();
    }

    public void setRect(Rectangle rect) {
        posRect = rect;
        if (showRect || cropToRect) this.repaint();
    }

    public Rectangle getRect() {
        return posRect;
    }

    public void setShowRect(boolean bShow) {
        if (showRect != bShow){
            showRect = bShow;
            this.repaint();
        }
    }

    public boolean getShowRect() {
        return showRect;
    }

    public void setCropToRect(boolean bCrop) {
        if (cropToRect != bCrop){
            cropToRect = bCrop;
            this.repaint();
        }
    }

    public boolean getCropToRect() {
        return cropToRect;
    }

    public void saveImage(File imageFile) {
        saveImage(imageFile, cropToRect);
    }

    public void saveImage(File imageFile, boolean bCrop) {
        BufferedImage image = currentImage;
        if (bCrop && posRect.x > 0 && posRect.y > 0 && posRect.width > 0 && posRect.height > 0 &&
                posRect.x + posRect.width <= image.getWidth() && posRect.y + posRect.height <= image.getHeight())
            image = image.getSubimage(posRect.x, posRect.y, posRect.width, posRect.height);

        try {
            ImageIO.write(image, "png", imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        Stroke oldStroke = g2d.getStroke();
        double ratio = 1;

        if (currentImage != null) {
            BufferedImage image = currentImage;
            if (cropToRect && posRect.x > 0 && posRect.y > 0 && posRect.width > 0 && posRect.height > 0 &&
                    posRect.x + posRect.width <= image.getWidth() && posRect.y + posRect.height <= image.getHeight())
                image = image.getSubimage(posRect.x, posRect.y, posRect.width, posRect.height);

            ratio = getRatio(image);

            AffineTransform tx = new AffineTransform();
            tx.scale(ratio, ratio);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

            g2d.drawImage(image, op,0, 0);

            float thickness = 4;
            g2d.setStroke(new BasicStroke(thickness));
            g2d.setColor(new Color(100, 100, 150));
            g2d.drawRect(0, 0, (int)Math.round(image.getWidth() * ratio), (int)Math.round(image.getHeight() * ratio));
        }

        if (showRect) {
            float thickness = 2;
            g2d.setStroke(new BasicStroke(thickness));
            g2d.setColor(new Color(212, 212, 212));
            g2d.drawRect( (int)Math.round(posRect.x * ratio), (int)Math.round(posRect.y * ratio),
                    (int)Math.round(posRect.width * ratio), (int)Math.round(posRect.height * ratio));
        }

        g2d.setStroke(oldStroke);
    }
}