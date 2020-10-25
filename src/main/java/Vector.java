/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Random;

/**
 *
 * @author bennett
 */
public class Vector 
{
    private double x;
    private double y;
    
    public Vector(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector() 
    {
         
    }
    public Vector(Vector a) 
    {
         this(a.x,a.y);
    }
    public void add(Vector a)
    {
        this.x += a.getX();
        this.y += a.getY();
    }
    public static Vector add(Vector a, Vector b)
    {
        return new Vector(a.x + b.x, a.y + b.y);
    }
    public static Vector multiple(Vector a, double b)
    {
          return new Vector(a.x * b, a.y * b);
    }
    public static Vector divide(Vector a, double b)
    {
          return new Vector(a.x / b, a.y / b);
    }
    public void subtract(Vector a)
    {
        this.x -= a.getX();
        this.y -= a.getY();
    }
    public static Vector subtract(Vector a, Vector b)
    {
        return new Vector(a.x - b.x, a.y - b.y);
    }
    public void multiply(double a)
    {
        this.x *= a;
        this.y *= a;
    }
    public void divide(double a)
    {
        this.x /= a;
        this.y /= a;
    }
    public double getMagnitude()
    {
        return (Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2)));
    }
    public void Normalize()
    {
         
        if(this.getMagnitude() != 0)
        {
            this.divide(this.getMagnitude());
        }
    }
    public static Vector Normalize(Vector a)
    {
        if(a.getMagnitude() != 0)
        {
            return Vector.divide(a, a.getMagnitude());
        }
        return null;

    }
    public static Vector Random(int a)
    {
        Random R = new Random();
        return new Vector(R.nextInt(a) * (R.nextBoolean() ? -1 : 1), R.nextInt(a) * (R.nextBoolean() ? -1 : 1));
    }
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }
    public void limit(int max)
    {
        if(this.x > max)
            this.x = max;
        else if(this.x < max*-1)
            this.x = max*-1;
        if(this.y > max)
            this.y = max;
        else if(this.y < max*-1)
            this.y = max*-1;
    }
    public static double dot(Vector a, Vector b){
        return a.getX()*b.getX() + a.getY()*b.getY();
    }
    public static double  project(Vector a, Vector b){
        return dot(a,b)/b.getMagnitude();
    }
    public String toString(){
        try {
            return "X: " + Double.toString(this.x).substring(0, 7) + ", Y: " + Double.toString(this.y).substring(0, 7);
        }catch(Exception e){
            return "X: " + this.x + ", Y: " + this.y;
        }
    }
}
