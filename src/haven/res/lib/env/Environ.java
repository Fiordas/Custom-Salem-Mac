package haven.res.lib.env;

import haven.*;

public class Environ {
    private final Glob glob;
    
    public Environ(Glob glob) {
        this.glob = glob;
    }
    
    public static Environ get(Glob glob) {
        // Simple stub - return a new instance
        return new Environ(glob);
    }
    
    public Coord3f wind() {
        // Return a default wind vector
        return new Coord3f(0.5f, 0.5f, 0.0f);
    }
}
