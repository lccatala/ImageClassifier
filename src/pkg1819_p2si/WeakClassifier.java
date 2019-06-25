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
    
    private byte[] position;
    private byte[] normal;
    private double[] Y;
    
    public WeakClassifier(int setSize, boolean generate) {
        position = new byte[784];
        normal = new byte[784];
        Y = new double[setSize];
        for (int i = 0; i < setSize; i++)
            Y[i] = 0;
        
        if (generate) {
            new Random().nextBytes(position);
            new Random().nextBytes(normal);
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
        return dotProduct(normal, pointDifference(img.getImageData(), position)) > 0;
    }
    
    private int dotProduct(byte[] point, byte[] vector) {
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
    private byte[] pointDifference(byte[] point1, byte[] point2) {
        byte[] result = new byte[point1.length];
        for (int i = 0; i < point1.length; i++)
            result[i] = (byte)(point1[i] - point2[i]);
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
    
    public byte[] getPosition() {
        return position;
    }
    
    public void setPosition(byte[] p) {
        position = p;
    }
    
    public byte[] getNormal() {
        return normal;
    }
    
    public void setNormal(byte[] n) {
        normal = n;
    }
    
    public double[] getY() {
        return Y;
    }
    
    public void setY(double[] y) {
        Y = y;
    }
}
