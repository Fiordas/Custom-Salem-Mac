package haven.res.lib.globfx;

import haven.*;
import java.util.*;

public class GlobEffector {
    private static final Map<Glob, Map<Class<?>, Object>> instances = new WeakHashMap<>();
    
    @SuppressWarnings("unchecked")
    public static <T> T get(Glob glob, Class<T> cl) {
        synchronized(instances) {
            Map<Class<?>, Object> globMap = instances.get(glob);
            if(globMap == null) {
                globMap = new HashMap<>();
                instances.put(glob, globMap);
            }
            
            T inst = (T)globMap.get(cl);
            if(inst == null) {
                try {
                    // Try to create an instance - assumes constructor with (Sprite.Owner, Resource)
                    // For FallingLeaves, we pass null since it's a singleton
                    inst = cl.getConstructor(Sprite.Owner.class, Resource.class).newInstance(null, null);
                    globMap.put(cl, inst);
                } catch(Exception e) {
                    throw new RuntimeException("Cannot create instance of " + cl, e);
                }
            }
            return inst;
        }
    }
}
