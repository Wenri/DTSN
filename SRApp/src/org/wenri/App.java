package org.wenri;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class App {
    private JButton btnMsg;
    private JPanel panelMain;
    private JButton btnPrev;
    private JButton btnNext;
    private ImagePanel panelCurrent;
    private JScrollBar scrollBarScale;
    private JComboBox comboBoxImage;
    private ImagePanel panelImg1;
    private ImagePanel panelImg2;
    private ImagePanel panelImg3;
    private ImagePanel panelImg4;
    private ImagePanel panelImg5;
    private ImagePanel panelImg6;
    private ImagePanel panelThb0;
    private ImagePanel panelThb1;
    private ImagePanel panelThb2;
    private ImagePanel panelThb3;
    private ImagePanel panelThb4;
    private ImagePanel panelThb5;
    private ImagePanel panelThb6;
    private JList listCrop;
    private JLabel lblFile;

    private ImageDataset dataset;
    private ImagePanel[] panelImgs;
    private ImagePanel[] panelThbs;
    private int currentIndex = 0;

    public void loadImage() {
        lblFile.setText(dataset.getBaseName(currentIndex));
        panelCurrent.loadImage(dataset.getImagePath(comboBoxImage.getSelectedIndex(), currentIndex).toFile());
        for(int i=1; i<panelImgs.length; i++)
            panelImgs[i].loadImage(dataset.getImagePath(i+2, currentIndex).toFile());
        BufferedImage img = panelImgs[0].getImage();
        panelImg6.resizeImage(img.getWidth(), img.getHeight());

        ArrayList<String>  arrCrop = dataset.getCropRect(currentIndex);
        DefaultListModel modelCrop = new DefaultListModel();
        modelCrop.clear();
        if(arrCrop != null) {
            for (String strCrop : arrCrop)
                modelCrop.addElement(strCrop);
        }
        listCrop.clearSelection();
        listCrop.setModel(modelCrop);

        for(int i=0; i<panelThbs.length; i++) {
            int ind = currentIndex - 3 + i;
            ind = ind % dataset.getTotalImages();
            if(ind < 0) ind += dataset.getTotalImages();
            panelThbs[i].loadImage(dataset.getImagePath(6, ind).toFile());
        }
    }

    public class ThbMouseAdapter extends MouseAdapter {
        public int index;
        public ThbMouseAdapter(int i) {
            index = i;
        }
        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
            super.mouseClicked(mouseEvent);
            ImagePanel panel = (ImagePanel) mouseEvent.getSource();
            currentIndex = currentIndex - 3 + index;
            currentIndex = currentIndex % dataset.getTotalImages();
            if(currentIndex < 0) currentIndex += dataset.getTotalImages();
            loadImage();
        }
    }

    public Rectangle parseRect(String str) {
        Rectangle rect = null;
        Pattern p = Pattern.compile("(\\d+)x(\\d+)\\+(\\d+)\\+(\\d+)");
        Matcher m = p.matcher(str);
        if(m.matches())
            rect = new Rectangle(
                    Integer.parseInt(m.group(3)),
                    Integer.parseInt(m.group(4)),
                    Integer.parseInt(m.group(1)),
                    Integer.parseInt(m.group(2)));
        return rect;
    }

    public String rectToString(Rectangle rect) {
        return rect.width + "x" + rect.width + "+" + rect.x + "+" + rect.y;
    }

    public App() throws Exception {
        dataset = new ImageDataset();

        panelImgs = new ImagePanel[]{panelCurrent, panelImg1, panelImg2, panelImg3, panelImg4, panelImg5, panelImg6};
        panelThbs = new ImagePanel[]{panelThb0, panelThb1, panelThb2, panelThb3, panelThb4, panelThb5, panelThb6};
        loadImage();
        btnMsg.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser j = new JFileChooser();
                j.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int opt = j.showSaveDialog(null);

                if (opt != JFileChooser.APPROVE_OPTION) return;

                String path = j.getSelectedFile().getPath();
                for(int i=0; i<panelImgs.length; i++) {
                    File saveFile = Paths.get(path, dataset.getMethodName(i+2) + "_" +
                            dataset.getBaseName(currentIndex) + "_" + rectToString(panelCurrent.getRect())
                            + ".png").toFile();
                    if(saveFile.exists()) {
                        int rc = JOptionPane.showConfirmDialog(null,
                                "File "  + saveFile.getName() + " already exist. Do you want to overwrite?",
                                "Overwrite file",
                                JOptionPane.YES_NO_OPTION);
                        if (rc != 0) break;
                    }

                    panelImgs[i].saveImage(saveFile, true);
                }
            }
        });

        panelCurrent.setShowRect(true);
        for(int i=1; i<panelImgs.length; i++)
            panelImgs[i].setCropToRect(true);

        panelCurrent.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent mouseEvent) {
                super.mousePressed(mouseEvent);
                int size = scrollBarScale.getValue();
                panelCurrent.setRectSize(new Dimension(size, size));
                panelCurrent.setRectPos(mouseEvent.getPoint());
                for(int i=1; i<panelImgs.length; i++) panelImgs[i].setRect(panelCurrent.getRect());
            }
        });
        panelCurrent.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                super.mouseDragged(mouseEvent);
                int size = scrollBarScale.getValue();
                panelCurrent.setRectSize(new Dimension(size, size));
                panelCurrent.setRectPos(mouseEvent.getPoint());
                for(int i=1; i<panelImgs.length; i++) panelImgs[i].setRect(panelCurrent.getRect());
            }
        });
        scrollBarScale.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent adjustmentEvent) {
                int size = adjustmentEvent.getValue();
                panelCurrent.setRectSize(new Dimension(size, size));
                for(int i=1; i<panelImgs.length; i++) panelImgs[i].setRect(panelCurrent.getRect());
            }
        });
        btnNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                currentIndex = (currentIndex + 1) % dataset.getTotalImages();
                loadImage();
            }
        });
        btnPrev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                currentIndex--;
                if(currentIndex < 0) currentIndex += dataset.getTotalImages();
                loadImage();
            }
        });
        for(int i=0; i<panelThbs.length; i++)
            panelThbs[i].addMouseListener(new ThbMouseAdapter(i));
        listCrop.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                int ind = listCrop.getSelectedIndex();
                if(ind < 0) return;
                String strRect = (String) listCrop.getModel().getElementAt(ind);
                Rectangle rect = parseRect(strRect);
                for(int i=0; i<panelImgs.length; i++) panelImgs[i].setRect(rect);
            }
        });
        comboBoxImage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                panelCurrent.loadImage(dataset.getImagePath(comboBoxImage.getSelectedIndex(), currentIndex).toFile());
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("App");
        try {
            frame.setContentPane(new App().panelMain);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage());
            return;
        }
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(800, 600));
        frame.pack();
        frame.setVisible(true);
    }
}
