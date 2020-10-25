/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stephen Garcia
 */
public class Engine implements Runnable{

    /**
     * @param args the command line arguments
     */
    private final int height = 240;
    private static final int width = 320;
    private final float windowscale = 2;
    
    private Game game;
    
    private Window w;
    private Input i;
    private Renderer r;
    public Engine(Game game)
    {

        this.game = game;
    }
    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public float getWindowscale() {
        return windowscale;
    }
    
    private boolean running = false;

    public Window getW() {
        return w;
    }
    private boolean render = false;
    public  double UPDATE = 1/60.0;
    private Thread thread;
    public void start() throws IOException
    {
        w = new Window(this);
        i = new Input(this);
        r = new Renderer(this);
        thread = new Thread(this); 
        thread.run();
    }
    public void start(String title) throws IOException
    {
        w = new Window(this);
        w.setTitle(title);
        i = new Input(this);
        r = new Renderer(this);
        thread = new Thread(this);
        thread.run();
    }
    @Override
    public void run() 
    {
        running = true;
        double firsttime = 0;
        double lasttime = System.nanoTime() / 1000000000.0;
        double passedtime = 0;
        double unprocessedtime = 0;
        
        double frametime = 0;
        int frames = 0;
        int fps = 0;
        while(running)
        {   
            render = false;
            firsttime = System.nanoTime() / 1000000000.0;
            passedtime = firsttime - lasttime;
            lasttime = firsttime;
            
            unprocessedtime += passedtime;
            frametime +=passedtime;
            
            while(unprocessedtime >= UPDATE)
            {
                
                unprocessedtime -= UPDATE;
                
                
                render = true;
                i.update();
                game.update(i);
                
                
                if(frametime >= .99)
                {
                    frametime = 0;
                    fps = frames;
                    frames = 0;
                }
            }
            
            if(render ==true)
            {
                game.renderer(r);
                r.drawText("FPS " + fps, 0, 0, 0xff00ffff);
                w.update();
                frames ++;
            }
            else
            {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
}
