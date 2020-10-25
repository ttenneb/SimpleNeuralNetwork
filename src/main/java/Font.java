/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;

/**
 *
 * @author Stephen Garcia
 */
public class Font 
{
    private Image font;
    private int[] w;
    private int[] offset;
    public Font(String p) throws IOException
    {
        font = new Image(p);
        
        offset = new int[59];
        w = new int[59];
        
        int unicode = 0;
        
        for (int i = 0; i < font.getW(); i++) 
        {
            if(font.getP()[i] == 0xff0000ff)
            {
                offset[unicode] = i;
            }
            if(font.getP()[i] == 0xffffff00)
            {
                w[unicode] = i - offset[unicode];
                unicode++;
            }
        }
        
    }

    public int[] getWidth() {
        return w;
    }

    public int[] getOffset() {
        return offset;
    }

    public Image getFontImage() {
        return font;
    }
}
