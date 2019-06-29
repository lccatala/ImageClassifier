/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg1819_p2si;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fidel
 */
public class Main {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        //Cargador de la BD de SI
        DBLoader ml = new DBLoader();
        ml.loadDBFromPath("./db");
        
        switch (args[0]) {
            case "-train":
                train(args[1], ml);
                break;
            case "-run":
                Imagen img = (Imagen)ml.getImageDatasetForIndex(5).get(0);
                classify(args[1], ml, img);
                break;
            default:
                System.out.println("Error: argumento " + args[0] + " no válido");
        } 
    }
    
    private static void train(String filename, DBLoader dbl) {
        Adaboost[] strongClassifiers = new Adaboost[8];
        for (int i = 0; i < 8; i++) {
            strongClassifiers[i] = new Adaboost(dbl, i);
            System.out.println("Training classifier for category " + i + "...");
            strongClassifiers[i].train();
            System.out.println("Liability with testing set: " + strongClassifiers[i].getTestingLiability(dbl));
        }
        writeData(filename, strongClassifiers);
    }
    
    
    
    private static void classify(String filename, DBLoader dbl, Imagen img) {
        Adaboost classifier[] = readData(filename, dbl);
        double nlh, blh = 0.0;
        byte category = 0;
        
        for (byte i = 0; i < 8; i++) {
            nlh = classifier[i].getLikelyhood(img);
            if (nlh > blh) {
                blh = nlh;
                category = i;
            }
        }
        System.out.println("Image is of category " + category);
    }
    
    private static void writeData(String filename, Adaboost[] strongClassifiers) {
        try {
            Writer writer = new FileWriter(filename);
            
            for (int i = 0; i < 8; i++) {
                // Write one classifier per line
                for (int j = 0; j < strongClassifiers[i].getStrongClassifier().size(); j++) {
                
                    // Point for position
                    for (int k = 0; k < strongClassifiers[i].getStrongClassifier().get(j).getPosition().length; k++)
                        writer.write(strongClassifiers[i].getStrongClassifier().get(j).getPosition()[k] + " ");

                    // Normal vector
                    for (int k = 0; k < strongClassifiers[i].getStrongClassifier().get(j).getNormal().length; k++)
                        writer.write(strongClassifiers[i].getStrongClassifier().get(j).getNormal()[k] + " ");

                    // Alpha
                    writer.write(strongClassifiers[i].getStrongClassifier().get(j).getAlpha() + "");
                    writer.write('\n');
                }
            }
            
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static Adaboost[] readData(String filename, DBLoader dbl) {
        Adaboost[] classifiers = new Adaboost[8];
        Scanner fileScanner = null;
        Scanner lineScanner = null;
        try {
            fileScanner = new Scanner(new File(filename));
            for (int i = 0; i < 8; i++) {
                classifiers[i] = new Adaboost(dbl, i);
                for (int k = 0; k < 100; k++) {
                    lineScanner = new Scanner(fileScanner.nextLine());
                    WeakClassifier wc = new WeakClassifier();
                    double[] pos = new double[784];
                    for (int j = 0; j < 784; j++) 
                        pos[j] = Double.parseDouble(lineScanner.next());
                    wc.setPosition(pos);

                    double[] norm = new double[784];
                    for (int j = 0; j < 784; j++) 
                        norm[j] = Double.parseDouble(lineScanner.next());
                    wc.setNormal(norm);
                    
                    // If we try to read from file with nextDouble(), it throws an InputMismatchException
                    wc.setAlpha(Double.parseDouble(lineScanner.next()));
                    
                    
                    classifiers[i].addClassifier(wc);
            }
            lineScanner.close();   
        }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        fileScanner.close();
        return classifiers;
    }
}
