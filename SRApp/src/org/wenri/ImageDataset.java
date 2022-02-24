package org.wenri;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class ImageDataset {
    private final String imagePath = "DIV2K_valid";
    private final String[] imageMethods = {
            "DIV2K_valid_HR", "DIV2K_val_HR_sketch", "DIV2K_val_HR_sketch_img",
            "GuidedSyn", "SRGAN_x4", "RDN_x4", "EDSR_x4", "LapSRN_x4",
            "DIV2K_valid_LR_bicubic_x4"
    };
    private final int totalImages = 100;
    private String[] imageNames = new String[totalImages];
    HashMap<String, ArrayList<String>> cropMap;

    private static String getFileNameWithoutExtensiotn(File file) {
        String fileName = file.getName();
        if (fileName.indexOf(".") > 0)
            fileName = fileName.substring(0, fileName.lastIndexOf("."));
        return fileName;
    }

    public void loadCropInfo() throws FileNotFoundException {
        cropMap = new HashMap<>();

        Scanner sc = new Scanner(Paths.get(imagePath, "CropInfo.txt").toFile());
        while (sc.hasNext()) {
            sc.next("#readonly");
            sc.next("NUM=(\\d+)");
            String num = "0" + sc.match().group(1);

            sc.next("#readonly");
            sc.next("SIZE=(\\d+x\\d+\\+\\d+\\+\\d+)");

            ArrayList<String> arrRect = cropMap.get(num);
            if(arrRect == null){
                arrRect = new ArrayList<>();
                cropMap.put(num, arrRect);
            }
            arrRect.add(sc.match().group(1));
        }
        sc.close();

    }

    public ImageDataset() throws Exception {
        loadCropInfo();
        Path path = Paths.get(imagePath, imageMethods[0]);
        File dir = path.toFile();
        File[] directoryListing = dir.listFiles();
        if (directoryListing == null || directoryListing.length != totalImages) throw new Exception("Dataset inconsistant");
        for(int i=0; i<totalImages; i++) imageNames[i] = getFileNameWithoutExtensiotn(directoryListing[i]);
        Arrays.sort(imageNames);
    }

    public Path getImagePath(int i, int i0) {
        String baseName = getBaseName(i0);
        if(i > 0 && ! imageMethods[i].equals("LapSRN_x4")) baseName += "x4";
        baseName += ".png";
        return Paths.get(imagePath, imageMethods[i], baseName);
    }

    public String getBaseName(int i0) {
        return imageNames[i0];
    }

    public String getMethodName(int i) {
        return imageMethods[i];
    }

    public ArrayList<String> getCropRect(int i0) {
        String baseName = imageNames[i0];
        return cropMap.get(baseName);
    }

    public int getTotalImages() { return totalImages; }
}
