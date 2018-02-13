import java.awt.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.media.j3d.*;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.geometry.Sphere;
import com.sun.j3d.utils.image.TextureLoader;
import com.sun.j3d.utils.universe.SimpleUniverse;

public class MySmallUniverse {
    public static void main(String[] args) throws Exception {
        System.setProperty("sun.awt.noerasebackground", "true");

        Clip clip = AudioSystem.getClip();
        // getAudioInputStream() also accepts a File or InputStream
        InputStream audioSrc = new FileInputStream("sound.wav");
//add buffer for mark/reset support
        InputStream bufferedIn = new BufferedInputStream(audioSrc);
        AudioInputStream ais = AudioSystem.
                getAudioInputStream(bufferedIn);
        clip.open(ais);
        clip.loop(Clip.LOOP_CONTINUOUSLY);

        SwingUtilities.invokeLater(MySmallUniverse::createAndShowGUI);
    }


    private static void createAndShowGUI() {
        JFrame frame = new JFrame();
        MySmallUniverse u = new MySmallUniverse();

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        GraphicsConfiguration config =
                SimpleUniverse.getPreferredConfiguration();
        Canvas3D canvas = new Canvas3D(config);
        frame.getContentPane().add(canvas);
        SimpleUniverse simpleUniverse = new SimpleUniverse(canvas);

        BranchGroup rootBranchGroup = new BranchGroup();
        createContents(rootBranchGroup);
        rootBranchGroup.compile();
//        simpleUniverse.addBranchGraph(u.createBackground());
        simpleUniverse.addBranchGraph(rootBranchGroup);
        Transform3D viewPlatformTransform = new Transform3D();
        Transform3D t0 = new Transform3D();
        t0.setTranslation(new Vector3d(0, 0, 10));
        Transform3D t1 = new Transform3D();
        t1.rotX(Math.toRadians(-30));
        viewPlatformTransform.mul(t1, t0);
        simpleUniverse.getViewingPlatform().
                getViewPlatformTransform().setTransform(viewPlatformTransform);

        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setLocationRelativeTo(null);
        frame.setUndecorated(true);
        frame.setVisible(true);

    }

    private BranchGroup createBackground() {
        BranchGroup backgroundGroup = new BranchGroup();

        Background back = new Background();
        back.setApplicationBounds(BOUNDS);

        BranchGroup bgGeometry = new BranchGroup();

        bgGeometry.addChild(new SkyBox());

        back.setGeometry(bgGeometry);

        backgroundGroup.addChild(back);

        return backgroundGroup;
    }

    private static BoundingSphere boundingSphere =
            new BoundingSphere(new Point3d(0.0, 0.0, 0.0), Double.MAX_VALUE);
    private static final Bounds BOUNDS = new BoundingSphere(new Point3d(0.0, 0.0,
            0.0), Double.POSITIVE_INFINITY);

    // Build the transform group that does a rotation about the
    // y-axis, rotating once in the given time
    private static TransformGroup createRotationTransformGroup(
            int rotationTimeMs, boolean forward) {
        TransformGroup rotationTransformGroup = new TransformGroup();
        rotationTransformGroup.setCapability(
                TransformGroup.ALLOW_TRANSFORM_WRITE);
        Alpha rotationAlpha = new Alpha(-1, rotationTimeMs);
        float angle = forward ? (float) (2 * Math.PI) : (float) (-2 * Math.PI);
        RotationInterpolator rotationInterpolator =
                new RotationInterpolator(rotationAlpha, rotationTransformGroup,
                        new Transform3D(), 0.0f, angle);
        rotationInterpolator.setSchedulingBounds(boundingSphere);
        rotationTransformGroup.addChild(rotationInterpolator);
        return rotationTransformGroup;
    }

    // Build the transform group that performs the specified translation
    private static TransformGroup createTranslatingTransformGroup(
            double dx, double dy, double dz) {
        TransformGroup translationTransformGroup = new TransformGroup();
        Transform3D translationTransform = new Transform3D();
        translationTransform.setTranslation(
                new Vector3d(dx, dy, dz));
        translationTransformGroup.setTransform(translationTransform);
        return translationTransformGroup;
    }

    private Appearance getTexture(String s) {

        s = "planets/" + s;

        TextureLoader loader = new TextureLoader(s, new Container());
        Texture texture = loader.getTexture();

        // Set up the texture attributes
        //could be REPLACE, BLEND or DECAL instead of MODULATE
        TextureAttributes texAttr = new TextureAttributes();
        texAttr.setTextureMode(TextureAttributes.MODULATE);
        Appearance ap = new Appearance();
        ap.setTexture(texture);
        ap.setTextureAttributes(texAttr);

        return ap;
    }

    private static void createContents(BranchGroup root) {

        TransformGroup top = new TransformGroup();
        root.addChild(top);

        //creating background
        MySmallUniverse u = new MySmallUniverse();

        double sun_coeff = 0.175; //because sun is 109 times bigger than earth
        double planets_size_coeff = 0.075; //0.075 = 1 earth's radius if ==0.075
        double distance_coeff = 0.8; //0.8 = 1 a.o.e if ==0.8
        double time_coeff = 10; //1s = 100 days if ==10

        int primflags = Primitive.GENERATE_NORMALS
                + Primitive.GENERATE_TEXTURE_COORDS;

        //creating sun
        top.addChild(u.createBackground());

        MyPlanet sun = new MyPlanet(null, 0, true, 0, (int) (25 * time_coeff), true, 1 * sun_coeff);

        TransformGroup sunTG = createRotationTransformGroup(sun.getOrbitTimeMs(), sun.getOrbitDirection());
        top.addChild(sunTG);

        TransformGroup sunTTG = createTranslatingTransformGroup(sun.getDistanceFromParent(), 0, 0);
        sunTG.addChild(sunTTG);

        Appearance sun_app = u.getTexture("sun.jpg");

        Node sun_planet = new Sphere((float) sun.getRadius(), primflags, sun_app);

        TransformGroup sunRTG = createRotationTransformGroup(sun.getRotationTimeMs(), sun.getRotationDirection());
        sunRTG.addChild(sun_planet);
        sunTTG.addChild(sunRTG);

        //creating mercury

        MyPlanet mercury = new MyPlanet(createTranslatingTransformGroup(1, 0, 0), (int) (88 * time_coeff), true, 0.4 * distance_coeff, (int) (59 * time_coeff), true, 0.38 * planets_size_coeff);
        TransformGroup mercuryTG = createRotationTransformGroup(mercury.getOrbitTimeMs(), mercury.getOrbitDirection());
        sunTTG.addChild(mercuryTG);

        TransformGroup mercuryTTG = createTranslatingTransformGroup(mercury.getDistanceFromParent(), 0, 0);
        mercuryTG.addChild(mercuryTTG);

        Appearance mercury_app = u.getTexture("mercury.jpg");
        Node mercury_planet = new Sphere((float) mercury.getRadius(), primflags, mercury_app);
        TransformGroup mercuryRTG = createRotationTransformGroup(mercury.getRotationTimeMs(), mercury.getRotationDirection());
        mercuryRTG.addChild(mercury_planet);
        mercuryTTG.addChild(mercuryRTG);

        //creating venus

        MyPlanet venus = new MyPlanet(createTranslatingTransformGroup(1, 0, 0), (int) (224 * time_coeff), true, 0.72 * distance_coeff, (int) (243 * time_coeff), false, 0.94 * planets_size_coeff);
        TransformGroup venusTG = createRotationTransformGroup(venus.getOrbitTimeMs(), venus.getOrbitDirection());
        sunTTG.addChild(venusTG);

        TransformGroup venusTTG = createTranslatingTransformGroup(venus.getDistanceFromParent(), 0, 0);
        venusTG.addChild(venusTTG);

        Appearance venus_app = u.getTexture("venus.jpg");
        Node venus_planet = new Sphere((float) venus.getRadius(), primflags, venus_app);
        TransformGroup venusRTG = createRotationTransformGroup(venus.getRotationTimeMs(), venus.getRotationDirection());
        venusRTG.addChild(venus_planet);
        venusTTG.addChild(venusRTG);

        //creating earth

        MyPlanet earth = new MyPlanet(null, (int) (365 * time_coeff), true, 1 * distance_coeff, (int) (1 * time_coeff), true, 1 * planets_size_coeff);

        TransformGroup earthTG = createRotationTransformGroup(earth.getOrbitTimeMs(), earth.getOrbitDirection());
        sunTTG.addChild(earthTG);

        TransformGroup earthTTG = createTranslatingTransformGroup(earth.getDistanceFromParent(), 0, 0);
        earthTG.addChild(earthTTG);

        Appearance earth_app = u.getTexture("earth.jpg");

        Node earth_planet = new Sphere((float) earth.getRadius(), primflags, earth_app);
        TransformGroup earthRTG = createRotationTransformGroup(earth.getRotationTimeMs(), earth.getRotationDirection());
        earthRTG.addChild(earth_planet);
        earthTTG.addChild(earthRTG);

        //creating mars

        MyPlanet mars = new MyPlanet(createTranslatingTransformGroup(1, 0, 0), (int) (687 * time_coeff), true, 1.5 * distance_coeff, (int) (1.03 * time_coeff), true, 0.53 * planets_size_coeff);
        TransformGroup marsTG = createRotationTransformGroup(mars.getOrbitTimeMs(), mars.getOrbitDirection());
        sunTTG.addChild(marsTG);

        TransformGroup marsTTG = createTranslatingTransformGroup(mars.getDistanceFromParent(), 0, 0);
        marsTG.addChild(marsTTG);

        Appearance mars_app = u.getTexture("mars.jpg");

        Node mars_planet = new Sphere((float) mars.getRadius(), primflags, mars_app);

        TransformGroup marsRTG = createRotationTransformGroup(mars.getRotationTimeMs(), mars.getRotationDirection());
        marsRTG.addChild(mars_planet);
        marsTTG.addChild(marsRTG);

        //creating jupiter

        MyPlanet jupiter = new MyPlanet(createTranslatingTransformGroup(1, 0, 0), (int) (4333 * time_coeff), true, 5.2 * distance_coeff * 0.5, (int) (0.42 * time_coeff), true, 19 * planets_size_coeff * 0.1);
        TransformGroup jupiterTG = createRotationTransformGroup(jupiter.getOrbitTimeMs(), jupiter.getOrbitDirection());
        sunTTG.addChild(jupiterTG);

        TransformGroup jupiterTTG = createTranslatingTransformGroup(jupiter.getDistanceFromParent(), 0, 0);
        jupiterTG.addChild(jupiterTTG);
        Appearance jupiter_app = u.getTexture("jupiter.jpg");

        Node jupiter_planet = new Sphere((float) jupiter.getRadius(), primflags, jupiter_app);
        TransformGroup jupiterRTG = createRotationTransformGroup(jupiter.getRotationTimeMs(), jupiter.getRotationDirection());
        jupiterRTG.addChild(jupiter_planet);
        jupiterTTG.addChild(jupiterRTG);

        //creating saturn

        MyPlanet saturn = new MyPlanet(createTranslatingTransformGroup(1, 0, 0), (int) (10759 * time_coeff), true, 9.6 * distance_coeff * 0.4, (int) (0.44 * time_coeff), true, 9 * planets_size_coeff * 0.15);
        TransformGroup saturnTG = createRotationTransformGroup(saturn.getOrbitTimeMs(), saturn.getOrbitDirection());
        sunTTG.addChild(saturnTG);

        TransformGroup saturnTTG = createTranslatingTransformGroup(saturn.getDistanceFromParent(), 0, 0);
        saturnTG.addChild(saturnTTG);

        Appearance saturn_app = u.getTexture("saturn.jpg");

        Node saturn_planet = new Sphere((float) saturn.getRadius(), primflags, saturn_app);
        TransformGroup saturnRTG = createRotationTransformGroup(saturn.getRotationTimeMs(), saturn.getRotationDirection());
        saturnRTG.addChild(saturn_planet);
        saturnTTG.addChild(saturnRTG);

        //creating uranus

        MyPlanet uranus = new MyPlanet(createTranslatingTransformGroup(1, 0, 0), (int) (30685 * time_coeff), true, 19.2 * distance_coeff * 0.25, (int) (0.71 * time_coeff), false, 3.97 * planets_size_coeff * 0.1);
        TransformGroup uranusTG = createRotationTransformGroup(uranus.getOrbitTimeMs(), uranus.getOrbitDirection());
        sunTTG.addChild(uranusTG);

        TransformGroup uranusTTG = createTranslatingTransformGroup(uranus.getDistanceFromParent(), 0, 0);
        uranusTG.addChild(uranusTTG);

        Appearance uranus_app = u.getTexture("uranus.jpg");

        Node uranus_planet = new Sphere((float) uranus.getRadius(), primflags, uranus_app);
        TransformGroup uranusRTG = createRotationTransformGroup(uranus.getRotationTimeMs(), uranus.getRotationDirection());
        uranusRTG.addChild(uranus_planet);
        uranusTTG.addChild(uranusRTG);

        //creating neptune

        MyPlanet neptune = new MyPlanet(createTranslatingTransformGroup(1, 0, 0), (int) (60190 * time_coeff), true, 30 * distance_coeff * 0.18, (int) (0.67 * time_coeff), true, 3.85 * planets_size_coeff * 0.1);
        TransformGroup neptuneTG = createRotationTransformGroup(neptune.getOrbitTimeMs(), neptune.getOrbitDirection());
        sunTTG.addChild(neptuneTG);

        TransformGroup neptuneTTG = createTranslatingTransformGroup(neptune.getDistanceFromParent(), 0, 0);
        neptuneTG.addChild(neptuneTTG);

        Appearance neptune_app = u.getTexture("neptune.jpg");

        Node neptune_planet = new Sphere((float) neptune.getRadius(), primflags, neptune_app);
        TransformGroup neptuneRTG = createRotationTransformGroup(neptune.getRotationTimeMs(), neptune.getRotationDirection());
        neptuneRTG.addChild(neptune_planet);
        neptuneTTG.addChild(neptuneRTG);

        top.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        top.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);


        // Add behavior

        MouseRotate myMouseRotate = new MouseRotate();
        myMouseRotate.setTransformGroup(top);
        myMouseRotate.setSchedulingBounds(new BoundingSphere());
        root.addChild(myMouseRotate);


        MouseTranslate myMouseTranslate = new MouseTranslate();
        myMouseTranslate.setTransformGroup(top);
        myMouseTranslate.setSchedulingBounds(new BoundingSphere());
        root.addChild(myMouseTranslate);


        MouseZoom myMouseZoom = new MouseZoom();
        myMouseZoom.setTransformGroup(top);
        myMouseZoom.setSchedulingBounds(new BoundingSphere());
        root.addChild(myMouseZoom);

//        //creating moon
//
//        MyPlanet moon = new MyPlanet(createTranslatingTransformGroup(1, 0, 0), 1500, true, 1, 2500, true, 0.1);
//        TransformGroup monthTG = createRotationTransformGroup(moon.getOrbitTimeMs(), moon.getOrbitDirection());
//        earthTTG.addChild(monthTG);
//
//        TransformGroup monthTTG = createTranslatingTransformGroup(moon.getDistanceFromParent(), 0, 0);
//        monthTG.addChild(monthTTG);
//
//        Node month_planet = new ColorCube(moon.getRadius());
//        TransformGroup monthRTG = createRotationTransformGroup(moon.getRotationTimeMs(), moon.getRotationDirection());
//        monthRTG.addChild(month_planet);
//        monthTTG.addChild(monthRTG);
//
//        //testing one more planet
//
//        MyPlanet mercury = new MyPlanet(createTranslatingTransformGroup(1, 0, 0), 500, false, 0.25, 4500, false, 0.025);
//        TransformGroup mercuryTG = createRotationTransformGroup(mercury.getOrbitTimeMs(), mercury.getOrbitDirection());
//        monthTTG.addChild(mercuryTG);
//
//        TransformGroup myPlanetTTG = createTranslatingTransformGroup(mercury.getDistanceFromParent(), 0, 0);
//        mercuryTG.addChild(myPlanetTTG);
//
//        Node myPlanet_planet = new ColorCube(mercury.getRadius());
//        TransformGroup myPlanetRTG = createRotationTransformGroup(mercury.getRotationTimeMs(), mercury.getRotationDirection());
//        myPlanetRTG.addChild(myPlanet_planet);
//        myPlanetTTG.addChild(myPlanetRTG);

    }

}