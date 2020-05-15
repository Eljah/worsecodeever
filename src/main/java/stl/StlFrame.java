package stl;

import eu.mihosoft.vrl.v3d.CSG;
import eu.mihosoft.vrl.v3d.Cube;
import eu.mihosoft.vrl.v3d.FileUtil;
import eu.mihosoft.vrl.v3d.Transform;

import java.io.IOException;
import java.nio.file.Paths;

public class StlFrame {
    public static void main(String[] args) {
        CSG frame = new Cube(160, 160, 20).toCSG();
        CSG hole = new Cube(156, 15, 20).toCSG();
        frame = frame.difference(hole);
        frame = frame.difference(hole.transformed(Transform.unity().translateY(17)));
        frame = frame.difference(hole.transformed(Transform.unity().translateY(34)));
        frame = frame.difference(hole.transformed(Transform.unity().translateY(51)));
        frame = frame.difference(hole.transformed(Transform.unity().translateY(68)));
        frame = frame.difference(hole.transformed(Transform.unity().translateY(-17)));
        frame = frame.difference(hole.transformed(Transform.unity().translateY(-34)));
        frame = frame.difference(hole.transformed(Transform.unity().translateY(-51)));
        frame = frame.difference(hole.transformed(Transform.unity().translateY(-68)));
        try {
            FileUtil.write(Paths.get( "frame.stl"),
                    frame.toStlString()
            );
        } catch (IOException ex) {
            //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
