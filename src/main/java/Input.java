/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.event.*;

/**
 *
 * @author Stephen Garcia
 */
public class Input implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener
{
    private Engine engine;
    
    private boolean[] keys = new boolean[256];
    private boolean[] keyslast = new boolean[256];
    
    private boolean[] mbuttons = new boolean[5];
    private boolean[] mbuttonslast = new boolean[5];
    private int mouseX, mouseY;
    private int scroll;
    
    
    public Input(Engine engine)
    {
        this.engine = engine;
        mouseX = 0;
        mouseY = 0;
        scroll = 0;
        
        engine.getW().getCanvas().addKeyListener(this);
        engine.getW().getCanvas().addMouseListener(this);
        engine.getW().getCanvas().addMouseMotionListener(this);
        engine.getW().getCanvas().addMouseWheelListener(this);
    }
    public void update()
    {
        scroll = 0;
        for (int i = 0; i < keys.length; i++) 
        {
            keyslast[i] = keys[i];
        }
        for (int i = 0; i < mbuttons.length; i++) 
        {
            mbuttonslast[i] = mbuttons[i];
        }
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        keys[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) 
    {
        keys[e.getKeyCode()] = false;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) 
    {
        mbuttons[e.getButton()] = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) 
    {
        mbuttons[e.getButton()] = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) 
    {
        mouseX = (int)(e.getX()/engine.getWindowscale());
        mouseY = (int)(e.getY()/engine.getWindowscale());
    }

    @Override
    public void mouseMoved(MouseEvent e) 
    {
        mouseX = (int)(e.getX()/engine.getWindowscale());
        mouseY = (int)(e.getY()/engine.getWindowscale());
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) 
    {
        scroll = e.getWheelRotation();
    }

    public boolean isKey(int keyCode)
    {
        return keys[keyCode];
    }
    public boolean isKeyup(int keyCode)
    {
        return !keys[keyCode] && keyslast[keyCode];
    }
    public boolean isKeydown(int keyCode)
    {
        return keys[keyCode] && !keyslast[keyCode];
    }
    
    public boolean isMB(int button)
    {
        return mbuttons[button];
    }
    public boolean isMBup(int button)
    {
        return !mbuttons[button] && mbuttonslast[button];
    }
    public boolean isMbdown(int button)
    {
        return mbuttons[button] && !mbuttonslast[button];
    }
    
    public int getMouseX() {
        return mouseX;
    }

    public int getMouseY() {
        return mouseY;
    }

    public int getScroll() {
        return scroll;
    }
    
}
