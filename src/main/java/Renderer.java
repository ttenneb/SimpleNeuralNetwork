/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import org.ejml.simple.SimpleMatrix;

import java.awt.*;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.util.Random;

/**
 *
 * @author Stephen Garcia
 */
public class Renderer 
{
    private int pW, pH;
    private int[] p;
    Random r = new Random();
    public static Font defualt;
    public Renderer(Engine engine) throws IOException
    {
      pW = engine.getWidth();
      pH = engine.getHeight();
      p = ((DataBufferInt)engine.getW().getImage().getRaster().getDataBuffer()).getData();
      defualt = new Font("/font.png");
    }
    
    public void clear()
    {
        for(int i = 0; i < p.length; i++)
        {
            p[i] = 0;
        }
    }
    //edited to restrain rendering to specific cords!!!!!
    public void  setPixel(int x, int y, int color)
    {
        if((x < 0 || x >= pW || y < 0 || y >= pH || color == 0xffff00ff || color == -16777216))
        {
            return;
        }

        p[x+y*pW] = color;

    }
    public int getPixel(int x, int y){
        return p[x+y*pW];
    }
    public void drawText(String text, int offx, int offy, int color)
    {
        text = text.toUpperCase();
        int offset = 0;
        for (int i = 0; i < text.length(); i++) 
        {
            int unicode = text.codePointAt(i) - 32;
            
            for (int y = 0; y < defualt.getFontImage().getH(); y++) 
            {
                for (int x = 0; x < defualt.getWidth()[unicode]; x++) 
                {
                    if(defualt.getFontImage().getP()[x + defualt.getOffset()[unicode] +y*defualt.getFontImage().getW()] == 0xffffffff)
                    {
                        setPixel(x + offx + offset, y + offy, color);
                    }
                }
            }
            offset += defualt.getWidth()[unicode];
        }
    }
    public void drawLine(int x1, int y1, int x2, int y2, int color)
    {

        double slope = (double)(y2-y1)/(double)(x2-x1);
        //System.out.println(slope);
        for (int i = 0; i < x2 - x1; i ++ )
        {
            setPixel(x1+i, (int)Math.round(y1+i*slope), color);
        }
        slope = (double)(x2-x1)/(double)(y2-y1);
        //System.out.println(slope);
        for (int i = 0; i < y2 - y1; i ++ )
        {
            setPixel((int)Math.round(x1+i*slope), y1+i, color);
        }
    }
    public void drawImage(int x, int y, Image image, boolean hud)
    {
        if(x <= -image.getW()){return;}
        if(y <= -image.getH()){return;}

        if(x >= pW){return;}
        if(y >= pH){return;}

        int newx = 0, newy = 0;
        int newW = image.getW(), newH = image.getH();

        if(x < 0){newx -= x;}
        if(y < 0){newy -= y;}

        if(newW+x > pW){newW -= newW + x - pW;}
        if(newH+y > pH){newH -= newH + y - pH;}

        for (int i = newy; i < newH; i++)
        {
            for (int j = newx; j < newW; j++)
            {
                if ((x+j < 320 && y+i < 240) || hud == true) {
                    setPixel(x + j, y + i, image.getP()[j + i * image.getW()]);
                }
            }
        }
    }
    public void drawImage(int x, int y, Image image)
    {
        if(x <= -image.getW()){return;}
        if(y <= -image.getH()){return;}

        if(x >= pW){return;}
        if(y >= pH){return;}

        int newx = 0, newy = 0;
        int newW = image.getW(), newH = image.getH();

        if(x < 0){newx -= x;}
        if(y < 0){newy -= y;}

        if(newW+x > pW){newW -= newW + x - pW;}
        if(newH+y > pH){newH -= newH + y - pH;}

        for (int i = newy; i < newH; i++)
        {
            for (int j = newx; j < newW; j++)
            {
                if (x+j < 320 && y+i < 240){
                    setPixel(x+j, y+i, image.getP()[j + i*image.getW()]);
                }
            }
        }
    }

    public void drawMatrix(int x, int y, SimpleMatrix A){
        int k = 0;
        for(int i = 0; i < 28; i++){
            for(int j =0; j < 28; j++){
                setPixel(x+j, y+i, (int)((A.get(k, 0)+1)*127.5)*0xffffff);
                k++;
            }
        }
    }
}
