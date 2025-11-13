package haven;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;

/**
 * Utility class to handle high-DPI scaling issues on macOS Retina displays
 */
public class DPIUtils {
    private static double dpiScaleX = 1.0;
    private static double dpiScaleY = 1.0;
    private static boolean initialized = false;
    
    /**
     * Initialize DPI scaling factors
     */
    public static void initializeDPIScale() {
        if (initialized) return;
        
        try {
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice device = env.getDefaultScreenDevice();
            GraphicsConfiguration config = device.getDefaultConfiguration();
            AffineTransform transform = config.getDefaultTransform();
            
            dpiScaleX = transform.getScaleX();
            dpiScaleY = transform.getScaleY();
            
            // Force disable HiDPI if detected
            if (dpiScaleX > 1.0 || dpiScaleY > 1.0) {
                System.setProperty("sun.java2d.uiScale", "1.0");
                System.setProperty("sun.java2d.dpiaware", "false");
                System.setProperty("prism.allowhidpi", "false");
            }
            
        } catch (Exception e) {
            dpiScaleX = dpiScaleY = 1.0;
        }
        
        initialized = true;
    }
    
    /**
     * Convert screen coordinates to canvas coordinates
     */
    public static Coord screenToCanvas(Coord screenCoord) {
        return new Coord((int)(screenCoord.x / dpiScaleX), (int)(screenCoord.y / dpiScaleY));
    }
    
    /**
     * Convert canvas coordinates to screen coordinates
     */
    public static Coord canvasToScreen(Coord canvasCoord) {
        return new Coord((int)(canvasCoord.x * dpiScaleX), (int)(canvasCoord.y * dpiScaleY));
    }
    
    public static double getScaleX() { return dpiScaleX; }
    public static double getScaleY() { return dpiScaleY; }
    public static boolean isHighDPI() { return dpiScaleX > 1.0 || dpiScaleY > 1.0; }
}