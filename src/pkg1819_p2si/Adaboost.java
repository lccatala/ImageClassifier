/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg1819_p2si;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author lccatala
 */
public class Adaboost {
    private ArrayList<WeakClassifier> strongClassifier;
    private ArrayList correctTrainingSet;
    private ArrayList incorrectTrainingSet;
    
    private double[] D;
    
    private int T = 100;
    private int A = 100;
    private double Z;
    int N;
    private int category;
    
    public Adaboost(DBLoader dbl, int category) {
        correctTrainingSet = dbl.getCorrectTrainingDatasetForIndex(category);
        incorrectTrainingSet = dbl.getIncorrectTrainingDatasetForIndex(category);
        
        N = correctTrainingSet.size() + incorrectTrainingSet.size();
        D = new double[N];
        
        for (int i = 0; i < N; i++)
            D[i] = 1.0 / N;
        
        this.category = category;
        
        strongClassifier = new ArrayList();
    }
    
    public double getLikelyhood(Imagen img) {
        double result = 0.0;
        
        for (byte i = 0; i < strongClassifier.size(); i++)
            result += strongClassifier.get(i).getAlpha() * (strongClassifier.get(i).pointIsAbovePlane(img) ? 1.0 : -1.0);
        return result;
    }
    
    public void train() {
        for (int i = 0; i < T; i++) {
            WeakClassifier gwc = new WeakClassifier(N, false);
            
            // Get weak classifier with minimum error (gwc)
            for (int j = 0; j < A; j++) {
                WeakClassifier wc = new WeakClassifier(N, true);
                wc.applyToData(correctTrainingSet, incorrectTrainingSet);
                wc.updateError(D);
                
                if (wc.getError() < gwc.getError()) 
                    gwc = wc;
            }
            
            gwc.updateAlpha();
            strongClassifier.add(gwc);
            
            updateD(gwc);
        }
    }
    
    private void normaliseD() {
        for (int i = 0; i < D.length; i++)
            D[i] /= Z;
    }
    
    private void updateD(WeakClassifier gwc) {
        for (int i = 0; i < D.length; i++)
            D[i] *= (Math.pow(Math.E, gwc.getAlpha() * (gwc.getY()[i] == 0.0 ? -1.0 : 1.0)));
        Z = sum(D);
        normaliseD();
    }
    
    private double sum(double[] D) {
        double result = 0;
        for (int i = 0; i < D.length; i++)
            result+= D[i];
        return result;
    }
    
    public ArrayList<WeakClassifier> getStrongClassifier() {
        return strongClassifier;
    }
    
    public void addClassifier(WeakClassifier wc) {
        strongClassifier.add(wc);
    }
    
    public double getLiability(DBLoader dbl) {
        double liability = 0.0;
        for (int i = 0; i < 8; i++) {
            ArrayList set = dbl.getImageDatasetForIndex(i);
            for (int j = 0; j < set.size(); j++) {
                Imagen ci = (Imagen)set.get(j);
                for (int k = 0; k < strongClassifier.size(); k++) {
                    if (i == category)
                        liability += (strongClassifier.get(k).pointIsAbovePlane(ci) ? 1.0 : 0.0);
                    else
                        liability += (strongClassifier.get(k).pointIsAbovePlane(ci) ? 0.0 : 1.0);
                }
            }
        }
        return liability / (dbl.getImageCount() * strongClassifier.size());
    }
}