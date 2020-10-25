import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Bennett Garcia
 */
public class Image {

     private int w = 0, h = 0;
     private int rw, rh;
     private int[] p;
     public BufferedImage base = null;
     public BufferedImage image = null;
     private Color color;
     private Color hitboxcolor = new Color(0xffff00ff);
     private int rotation = 0;
     private int biggerSide;
     int offset = 0;

     public int getOffset() {
          return offset;
     }

     
     public int getRotation() {
          return rotation;
     }

     public void setRotation(int rotation) {
          this.rotation = rotation;
          rotate(0);
     }
     public Image(String file) throws IOException {

          image = ImageIO.read(Image.class.getResourceAsStream(file));

          w = image.getWidth();
          h = image.getHeight();
          p = image.getRGB(0, 0, w, h, null, 0, w);
          base = image;
          
          image.flush();
     }

     public Image(BufferedImage img) {

          image = img;

          w = image.getWidth();
          h = image.getHeight();
          p = image.getRGB(0, 0, w, h, null, 0, w);
          base = image;
          image.flush();
     }

     public Image(int x, int y, int color) {
          if(x >= y)
               biggerSide = x;
          else
               biggerSide = y;
          //TODO fix rectangle offsets
          offset = (int)((((Math.sqrt(2*Math.pow(biggerSide, 2))) - biggerSide)) +.5);
          rw = x;
          rh = y;
          w = x + offset;
          h = y + offset;
          this.color = new Color(color);
         image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
          for (int i = 0; i < w; i++){
              for (int j = 0; j < h; j++){
                  image.setRGB(i,j,0xffff00ff);
              }
          }
          for (int i = offset/2; i < x + offset/2; i++) {
               for (int j = offset/2; j < y + offset/2; j++) {
                    image.setRGB(i, j, color);
               }
          }
          p = image.getRGB(0, 0, w, h, null, 0, w);
          base = image;
          image.flush();

     }

     public void setW(int w) {
          this.w = w;
     }

     public int getW() {
          return w;
     }

     public int getH() {
          return h;
     }

     public int[] getP() {
          return p;
     }

     public void rotate(double deg) {
          rotation += deg;
          rotation = rotation % 90;
          AffineTransform transform = new AffineTransform();
          transform.rotate(Math.toRadians(rotation), this.getW() / 2, this.getH() / 2);
          AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
          Image copy = new Image(base);
          
          copy.image = op.filter(copy.image, null);
          copy.image.flush();
          
           this.image =copy.image;
           p = this.image.getRGB(0, 0, w, h, null, 0, w);
          this.image.flush();
          
     }
     public int getRw() {
          return rw;
     }

     public void setRw(int rw) {
          this.rw = rw;
     }

     public int getRh() {
          return rh;
     }

     public void setRh(int rh) {
          this.rh = rh;
     }


}
