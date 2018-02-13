import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import java.awt.*;

class MyBackground {

    private Background bg;

    MyBackground() {

        bg = new Background();
// set the range of influence of the background
        bg.setApplicationBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 5000));
// create a BranchGroup that will hold
// our Sphere geometry
        BranchGroup bgGeometry = new BranchGroup();
// create an appearance for the Sphere
        Appearance app = new Appearance();
// load a texture image using the Java 3D texture loader
        Texture tex = new TextureLoader("bg.png", new Container()).getTexture();
// apply the texture to the Appearance
        app.setTexture(tex);
// create the Sphere geometry with radius 1.0.
// we tell the Sphere to generate texture coordinates
// to enable the texture image to be rendered
// and because we are *inside* the Sphere we have to generate
// Normal coordinates inwards or the Sphere will not be visible.
        Sphere sphere = new Sphere(1f,
                Primitive.GENERATE_TEXTURE_COORDS |

                        Primitive.GENERATE_NORMALS_INWARD, app);
        // start wiring everything together,
        // add the Sphere to its parent BranchGroup.
        bgGeometry.addChild(sphere);
        // assign the BranchGroup to the Background as geometry.
        bg.setGeometry(bgGeometry);
    }

    Background getBackground() {
        return bg;
    }
}
