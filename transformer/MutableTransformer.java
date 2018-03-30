package com.eng.cber.na.transformer;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import com.eng.cber.na.event.util.PropertyChangeSupport;

import edu.uci.ics.jung.visualization.transform.shape.ShapeTransformer;

/**
 * Interface for the mutation of a transformer and for 
 * adding listeners for changes on the transformer. The 
 * interfaces allows mapping from one coordinate system
 * to another coordinate system, through basic linear
 * algebraic operations (e.g., shear, rotate, scale, 
 * translate).
 * 
 * This is a PANACEA (OpenGL-reimplementation-friendly) version
 * of the JUNG class MutableTransformer.
 *
 */
public interface MutableTransformer extends ShapeTransformer, PropertyChangeSupport {
    
    void translate(double dx, double dy);
    
    void setTranslate(double dx, double dy);
    
    void scale(double sx, double sy, Point2D point);
    
    void setScale(double sx, double sy, Point2D point);
    
    void rotate(double radians, Point2D point);
    
    void rotate(double radians, double x, double y);
    
    void shear(double shx, double shy, Point2D from);
    
    void concatenate(AffineTransform transform);
    
    void preConcatenate(AffineTransform transform);
    
    double getScaleX();
    
    double getScaleY();
    
    double getScale();
    
    double getTranslateX();
    
    double getTranslateY();
    
    double getShearX();
    
    double getShearY();

    AffineTransform getTransform();
    
    void setToIdentity();
    
    double getRotation();
    
}

