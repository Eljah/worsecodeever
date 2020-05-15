package stl;


import eu.mihosoft.vrl.v3d.*;
import javafx.scene.text.Font;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;

public class StlTypographyTest {
    public static void main(String[] args) throws FileNotFoundException {

        String alphabet = "абвгдеёжзийклмнопрстуфхцчшщъыьэюяәүөҗһң";
        for (int i = 0; i < 39; i++) {
            String text = alphabet.substring(i, i + 1).toUpperCase();
            //Font.loadFont(new FileInputStream("captchafont3.ttf"), 12);
            CSG text3d = new Text3d(text, "Arial", 12, 5).toCSG();
            //CSG text3d = new Text3d(text, "Arial", 12, 1).toCSG();

            double boxWidth = text3d.getBounds().getBounds().x + 2;
            double boxHeight = 15; //text3d.getBounds().getBounds().y+2;
            //double boxHeight = text3d.getBounds().getBounds().y+2;
            double boxDepth = text3d.getBounds().getBounds().z;

            System.out.println("H: "+boxHeight);
            System.out.println("D: "+boxDepth);
            System.out.println(text);

            CSG box = new Cube(boxWidth, boxHeight, boxDepth).toCSG();
            CSG box2 = new Cube(boxWidth, boxHeight, 11).toCSG();
            box = box.difference(text3d);

            CSG reversedTex = text3d.transformed(Transform.unity().rotZ(180).rotX(180).translateY(1).translateX(0.5));

            CSG union = reversedTex
                    .union(box2.transformed(Transform.unity().translateZ(8)))
                    .union(box.transformed(Transform.unity().translateZ(13)))
            ;
            //union = union.transformed(Transform.unity().rotZ(180));

// save union as stl
            try {
                FileUtil.write(Paths.get(text + ".stl"),
                        union.toStlString()
                );
            } catch (IOException ex) {
                //Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
