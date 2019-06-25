/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg1819_p2si;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author fidel
 */
public class DBLoader {

    private ArrayList[] imageDB;
    
    private ArrayList[] correctTrainingImages;
    private ArrayList[] incorrectTrainingImages;
    private int imageCount = 0;
    
    public DBLoader() {
        
    }
    
    void loadDBFromPath(String path){
        
        //Una arrayList por clase almacenará las imágenes
        imageDB = new ArrayList[8];
        
        //Creo un array list de imagenes para cada clase y cargo cada una
        //de las imágenes disponibles por clase
        for (int i=0;i<8; i++){
            imageDB[i] = new ArrayList();
            System.out.println("Loaded class "+i);
            
            File[] files = new File(path,"" + i).listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathname) {
                    return pathname.getName().toLowerCase().endsWith(".png") 
                         || pathname.isDirectory();
                    }
                });
            
            //File[] files = new File(path,"" + i).listFiles();
            for (File file : files) {
                if (file.isFile()) {
                   imageDB[i].add(new Imagen(file.getAbsoluteFile()));
                   imageCount++;
                }
            }
        }
        
        loadSets();
        System.out.println("Loaded "+ imageCount + " images...");
    }
    
    void loadSets() {
        correctTrainingImages = new ArrayList[8];
        incorrectTrainingImages = new ArrayList[8];
        
        for (int i = 0; i < 8; i++) {
            int s = imageDB[i].size();
            incorrectTrainingImages[i] = new ArrayList();
            correctTrainingImages[i] = new ArrayList();
            
            // Add 80% of all correct images as the first half
            for (int j = 0; j < s * 0.8; j++)
                correctTrainingImages[i].add(imageDB[i].get(j));
            
            // Add the same ammount of incorrect images divided amongst the rest of datasets
            for (int j = 0; j < 8; j++) {
                int ni = (i + j + 1) % 8;
                for (int k = 0; k < s * 0.1; k++)
                    incorrectTrainingImages[i].add(imageDB[ni].get(k));
            }
        }
    }
    
    public ArrayList getImageDatasetForIndex(int i){
        return imageDB[i];
    }
    
    public ArrayList getCorrectTrainingDatasetForIndex(int i) {
        return correctTrainingImages[i];
    }
    
    public ArrayList getIncorrectTrainingDatasetForIndex(int i) {
        return incorrectTrainingImages[i];
    }
    
    public double getImageCount() {
        return imageCount;
    }
}
