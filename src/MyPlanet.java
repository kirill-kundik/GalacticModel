import javax.media.j3d.TransformGroup;

public class MyPlanet {

    private TransformGroup parent;
    private TransformGroup translationTransformGroup;
    private int orbitTimeMs;
    private boolean orbitDirection;
    private double distanceFromParent;
    private int rotationTimeMs;
    private boolean rotationDirection;
    private double radius;

    MyPlanet(TransformGroup parent, int orbitTimeMs, boolean orbitDirection, double distanceFromParent, int rotationTimeMs, boolean rotationDirection, double radius) {

        this.parent = parent;
        this.orbitTimeMs = orbitTimeMs;
        this.orbitDirection = orbitDirection;
        this.distanceFromParent = distanceFromParent;
        this.rotationTimeMs = rotationTimeMs;
        this.rotationDirection = rotationDirection;
        this.radius = radius;

    }

    public void setTranslationTransformGroup(TransformGroup group) {
        this.translationTransformGroup = group;
    }

    public TransformGroup getTranslationTransformGroup() {
        return translationTransformGroup;
    }

    public TransformGroup getParent() {
        return parent;
    }

    int getOrbitTimeMs() {
        return orbitTimeMs;
    }

    boolean getOrbitDirection() {
        return orbitDirection;
    }

    double getDistanceFromParent() {
        return distanceFromParent;
    }

    int getRotationTimeMs() {
        return rotationTimeMs;
    }

    boolean getRotationDirection() {
        return rotationDirection;
    }

    double getRadius() {
        return radius;
    }

}
