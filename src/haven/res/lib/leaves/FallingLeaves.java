package haven.res.lib.leaves;

import haven.*;
import haven.res.lib.env.Environ;
import haven.res.lib.globfx.GlobEffector;
import java.nio.FloatBuffer;
import java.util.*;
import com.jogamp.opengl.*;

public class FallingLeaves extends Sprite {
    static final int maxleaves = 10000;
    public final Random rnd = new Random();
    final FloatBuffer posb;
    final FloatBuffer nrmb;
    final FloatBuffer texb;
    final Leaf[] leaves = new Leaf[maxleaves];
    final Map<Material, MSlot> matmap = new HashMap<Material, MSlot>();
    int nl;
    int ckt = 0;
    
    public FallingLeaves(Sprite.Owner owner, Resource res) {
        super(owner, res);
        this.posb = Utils.mkfbuf(maxleaves * 12);
        this.nrmb = Utils.mkfbuf(maxleaves * 12);
        this.texb = Utils.mkfbuf(maxleaves * 8);
        for(int i = 0; i < maxleaves * 8; i += 8) {
            this.texb.put(i + 0, 0.0f);
            this.texb.put(i + 1, 0.0f);
            this.texb.put(i + 2, 0.0f);
            this.texb.put(i + 3, 1.0f);
            this.texb.put(i + 4, 1.0f);
            this.texb.put(i + 5, 1.0f);
            this.texb.put(i + 6, 1.0f);
            this.texb.put(i + 7, 0.0f);
        }
    }
    
    public static FallingLeaves get(Glob glob) {
        return (FallingLeaves)GlobEffector.get(glob, FallingLeaves.class);
    }
    
    public abstract class Leaf {
        float x, y, z;
        float xv, yv, zv;
        float nx, ny, nz;
        float nxv, nyv, nzv;
        float ar;
        MSlot m;
        
        public Leaf(float x, float y, float z) {
            this.ar = (0.5f + FallingLeaves.this.rnd.nextFloat()) / 50.0f;
            this.x = x;
            this.y = y;
            this.z = z;
            this.nx = FallingLeaves.this.rnd.nextFloat();
            this.ny = FallingLeaves.this.rnd.nextFloat();
            this.nz = FallingLeaves.this.rnd.nextFloat();
            if(this.nx < 0.5f)
                this.nx -= 1.0f;
            if(this.ny < 0.5f)
                this.ny -= 1.0f;
            if(this.nz < 0.5f)
                this.nz -= 1.0f;
            float l = 1.0f / (float)Math.sqrt(this.nx * this.nx + this.ny * this.ny + this.nz * this.nz);
            this.nx *= l;
            this.ny *= l;
            this.nz *= l;
        }
        
        public Leaf() {
            this(0.0f, 0.0f, 0.0f);
        }
        
        public Leaf(Coord3f pos) {
            this(pos.x, pos.y, pos.z);
        }
        
        public abstract Material mat();
    }
    
    private class MSlot implements Rendered {
        java.nio.ShortBuffer indb;
        final Material m;
        int nl;
        
        MSlot(Material m) {
            this.m = m;
            this.indb = Utils.mksbuf(400);
        }
        
        public void draw(GOut g) {
            g.apply();
            GL2 gl = g.gl;
            FallingLeaves.this.posb.rewind();
            FallingLeaves.this.nrmb.rewind();
            FallingLeaves.this.texb.rewind();
            this.indb.rewind();
            gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
            gl.glVertexPointer(3, GL.GL_FLOAT, 0, FallingLeaves.this.posb);
            gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
            gl.glNormalPointer(GL.GL_FLOAT, 0, FallingLeaves.this.nrmb);
            gl.glEnableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
            gl.glTexCoordPointer(2, GL.GL_FLOAT, 0, FallingLeaves.this.texb);
            gl.glDrawElements(GL2.GL_QUADS, this.nl * 4, GL.GL_UNSIGNED_SHORT, this.indb);
            gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
            gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
            gl.glDisableClientState(GL2.GL_TEXTURE_COORD_ARRAY);
        }
        
        public boolean setup(RenderList rl) {
            rl.prepo(this.m);
            return true;
        }
        
        void rewind() {
            this.indb.rewind();
            this.nl = 0;
        }
        
        void add(int n) {
            if(n >= 65536)
                throw new RuntimeException("More leaf vertices than should be possible.");
            if(this.indb.position() + 4 > this.indb.capacity()) {
                java.nio.ShortBuffer n2 = Utils.mksbuf(this.indb.capacity() * 2);
                n2.rewind();
                this.indb.limit(this.indb.position());
                this.indb.rewind();
                n2.put(this.indb);
                this.indb = n2;
            }
            this.indb.put((short)(n + 0));
            this.indb.put((short)(n + 1));
            this.indb.put((short)(n + 2));
            this.indb.put((short)(n + 3));
            this.nl++;
        }
    }
    
    public boolean setup(RenderList rl) {
        for(MSlot ms : this.matmap.values())
            rl.add(ms, null);
        return false;
    }
    
    void updvert() {
        for(MSlot ms : this.matmap.values())
            ms.rewind();
        int vo = 0, io = 0, vp = 0;
        for(int i = 0; i < this.leaves.length; i++) {
            Leaf lf = this.leaves[i];
            if(lf != null) {
                this.posb.put(vp + 0, lf.x + 2.0f * lf.nz);
                this.posb.put(vp + 1, lf.y - 2.0f * lf.nz);
                this.posb.put(vp + 2, lf.z + 2.0f * (lf.ny - lf.nx));
                this.posb.put(vp + 3, lf.x + 2.0f * lf.nz);
                this.posb.put(vp + 4, lf.y + 2.0f * lf.nz);
                this.posb.put(vp + 5, lf.z - 2.0f * (lf.nx - lf.ny));
                this.posb.put(vp + 6, lf.x - 2.0f * lf.nz);
                this.posb.put(vp + 7, lf.y + 2.0f * lf.nz);
                this.posb.put(vp + 8, lf.z + 2.0f * (lf.nx - lf.ny));
                this.posb.put(vp + 9, lf.x - 2.0f * lf.nz);
                this.posb.put(vp + 10, lf.y - 2.0f * lf.ny);
                this.posb.put(vp + 11, lf.z + 2.0f * (lf.nx + lf.ny));
                this.nrmb.put(vp + 0, lf.nx);
                this.nrmb.put(vp + 1, lf.ny);
                this.nrmb.put(vp + 2, lf.nz);
                this.nrmb.put(vp + 3, lf.nx);
                this.nrmb.put(vp + 4, lf.ny);
                this.nrmb.put(vp + 5, lf.nz);
                this.nrmb.put(vp + 6, lf.nx);
                this.nrmb.put(vp + 7, lf.ny);
                this.nrmb.put(vp + 8, lf.nz);
                this.nrmb.put(vp + 9, lf.nx);
                this.nrmb.put(vp + 10, lf.ny);
                this.nrmb.put(vp + 11, lf.nz);
                lf.m.add(io);
                vo += 4;
            }
            io += 4;
            vp += 12;
        }
        if(vo / 4 != this.nl)
            throw new RuntimeException();
    }
    
    void move(float dt) {
        Coord3f wind = Environ.get(((Gob)this.owner).glob).wind();
        for(Leaf lf : this.leaves) {
            if(lf == null)
                continue;
            float dx = lf.xv - wind.x;
            float dy = lf.yv - wind.y;
            float dz = lf.zv - wind.z;
            float dd = (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
            float rl = (float)Math.sqrt(lf.nxv * lf.nxv + lf.nyv * lf.nyv + lf.nzv * lf.nzv);
            if(rl > 0.0f) {
                float rs = (float)Math.sin(rl * dt);
                float rc = (float)Math.cos(rl * dt);
                rl = 1.0f / rl;
                float rax = lf.nxv * rl;
                float ray = lf.nyv * rl;
                float raz = lf.nzv * rl;
                float onx = lf.nx, ony = lf.ny, onz = lf.nz;
                lf.nx = onx * (rax * rax * (1.0f - rc) + rc) + ony * (rax * ray * (1.0f - rc) - raz * rs) + onz * (rax * raz * (1.0f - rc) + ray * rs);
                lf.ny = onx * (ray * rax * (1.0f - rc) + raz * rs) + ony * (ray * ray * (1.0f - rc) + rc) + onz * (ray * raz * (1.0f - rc) - rax * rs);
                lf.nz = onx * (raz * rax * (1.0f - rc) - ray * rs) + ony * (raz * ray * (1.0f - rc) + rax * rs) + onz * (raz * raz * (1.0f - rc) + rc);
                float rd = (float)Math.pow(0.7, dt);
                lf.nxv *= rd;
                lf.nyv *= rd;
                lf.nzv *= rd;
            }
            float dr = dd * dd / 5.0f;
            float ts = 0.5f;
            float tx = dx + (this.rnd.nextFloat() - 0.5f) * dr;
            float ty = dy + (this.rnd.nextFloat() - 0.5f) * dr;
            float tz = dz + (this.rnd.nextFloat() - 0.5f) * dr;
            float onxv = lf.nxv, onyv = lf.nyv, onzv = lf.nzv;
            lf.nxv += (lf.ny * tz - lf.nz * ty) * dt * ts;
            lf.nyv += (lf.nz * tx - lf.nx * tz) * dt * ts;
            lf.nzv += (lf.nx * ty - lf.ny * tx) * dt * ts;
            float dp = Math.abs(lf.nx * dx + lf.ny * dy + lf.nz * dz);
            float fpx = lf.nx * dp - dx;
            float fpy = lf.ny * dp - dy;
            float fpz = lf.nz * dp - dz;
            lf.xv += fpx * Math.abs(fpx) * lf.ar * dt;
            lf.yv += fpy * Math.abs(fpy) * lf.ar * dt;
            lf.zv += fpz * Math.abs(fpz) * lf.ar * dt;
            lf.x += lf.xv * dt;
            lf.y += lf.yv * dt;
            lf.z += lf.zv * dt;
            lf.zv -= 9.81f * dt;
        }
    }
    
    void ckstop(Glob glob) {
        for(int i = 0; i < this.leaves.length; i++) {
            if(this.leaves[i] == null)
                continue;
            boolean stop = false;
            try {
                stop = this.leaves[i].z < glob.map.getcz(this.leaves[i].x, -this.leaves[i].y) - 1.0f;
            } catch(Loading e) {
                stop = true;
            }
            if(stop) {
                this.leaves[i] = null;
                this.nl--;
            }
        }
    }
    
    public boolean tick(int dt) {
        Glob glob = ((Gob)this.owner).glob;
        float fdt = (float)dt / 1000.0f;
        if(this.ckt++ > 1000) {
            this.ckstop(glob);
            this.ckt = 0;
        }
        if(this.nl == 0)
            return true;
        this.move(fdt);
        this.updvert();
        return false;
    }
    
    public Coord3f onevertex(Location.Chain loc, FastMesh mesh) {
        short s = mesh.indb.get(this.rnd.nextInt(mesh.num));
        VertexBuf.VertexArray va = (VertexBuf.VertexArray)mesh.vert.buf(VertexBuf.VertexArray.class);
        Coord3f v = new Coord3f(va.data.get(s * 3), va.data.get(s * 3 + 1), va.data.get(s * 3 + 2));
        return loc.fin(Matrix4f.id).mul4(v);
    }
    
    public void addleaf(Leaf lf) {
        for(int i = 0; i < this.leaves.length; i++) {
            if(this.leaves[i] != null)
                continue;
            this.leaves[i] = lf;
            Material mat = lf.mat();
            lf.m = this.matmap.get(mat);
            if(lf.m == null) {
                lf.m = new MSlot(mat);
                this.matmap.put(mat, lf.m);
            }
            this.nl++;
            return;
        }
    }
}
