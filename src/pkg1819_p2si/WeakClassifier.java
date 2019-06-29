/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg1819_p2si;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;

/**
 *
 * @author alpasfly
 */
public class WeakClassifier {
    private double error;
    private double alpha;
    
    private double[] position;
    private double[] normal;
    private double[] Y;
    
    public WeakClassifier(int setSize, boolean generate) {
        position = new double[784];
        normal = new double[784];
        Y = new double[setSize];
        for (int i = 0; i < setSize; i++)
            Y[i] = 0;
        
        if (generate) {
            Random r = new Random();
            for (int i = 0; i < 784; i++) {
                normal[i] = Math.random() * 2 - 1;
                position[i] = r.nextDouble() * 255.0;
            }
        }
        
        error = Double.MAX_VALUE;
        alpha = 0;
    }
    
    public WeakClassifier() {
        
    }
    
    public WeakClassifier(WeakClassifier that) {
        position = that.getPosition();
        normal = that.getNormal();
        error = that.getError();
        alpha = that.getAlpha();
        Y = that.getY();
    }
    
    public boolean pointIsAbovePlane(Imagen img) {
        return dotProduct(normal, pointDifference(position, img.getImageData())) > 0;
    }
    
    private int dotProduct(double[] point, double[] vector) {
        int result = 0;
        for (int i = 0; i < point.length; i++) {
            result += point[i] * vector[i];
        }
        return result;
    }
    
    public void updateError(double[] D) {
        error = 0.0;
        for (int i = 0; i < D.length; i++)
            error += Y[i] * D[i];
    }
    
    /**
     * Difference of points of the same number of dimensions
     * @param point1 point to subtract from
     * @param point2 subtractor point
     * @return point1 - point2
     */
    private double[] pointDifference(double[] point1, byte[] point2) {
        double[] result = new double[point1.length];
        for (int i = 0; i < point1.length; i++)
            result[i] = (double)(point1[i] - point2[i]);
        return result;
    }
    
    
    public void applyToData(ArrayList correctSet, ArrayList incorrectSet) {
        
        // Dataset pertaining to expected category
        for (int i = 0; i < correctSet.size() / 2; i++) {
            Imagen img = (Imagen)correctSet.get(i);
            Y[i] = pointIsAbovePlane(img) ? 0.0 : 1.0;
        }
        
        // Dataset not pertaining to expected category
        for (int i = incorrectSet.size() / 2; i < incorrectSet.size(); i++) {
            Imagen img = (Imagen)incorrectSet.get(i);
            Y[i] = pointIsAbovePlane(img) ? 1.0 : 0.0;
        }
    }
    
    public double getError() {
        return error;
    }
    
    public void setError(double e) {
        error = e;
    }
    
    public double getAlpha() {
        return alpha;
    }
    
    
    public void setAlpha(double a) {
        alpha = a;
    }
    
    public void updateAlpha() {
        alpha = 0.5 * ((double)Math.log10((double)(1.0 - error) / error) / (double)Math.log10(2));
    }
    
    public double[] getPosition() {
        return position;
    }
    
    public void setPosition(double[] p) {
        position = p;
    }
    
    public double[] getNormal() {
        return normal;
    }
    
    public void setNormal(double[] n) {
        normal = n;
    }
    
    public double[] getY() {
        return Y;
    }
    
    public void setY(double[] y) {
        Y = y;
    }
}
