import com.sun.j3d.utils.geometry.Box;
import com.sun.j3d.utils.geometry.Primitive;
import com.sun.j3d.utils.image.TextureLoader;

import javax.media.j3d.Appearance;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Texture;

class SkyBox extends Box {

    SkyBox() {
        super(0.75f, 0.75f, 0.75f, Primitive.GENERATE_TEXTURE_COORDS |
                Primitive.GENERATE_NORMALS_INWARD, new Appearance());

        setFaceTexture(Box.FRONT, new TextureLoader("skybox/front.bmp", null).getTexture());
        setFaceTexture(Box.LEFT, new TextureLoader("skybox/left.bmp", null).getTexture());
        setFaceTexture(Box.RIGHT, new TextureLoader("skybox/right.bmp", null).getTexture());
        setFaceTexture(Box.BACK, new TextureLoader("skybox/back.bmp", null).getTexture());
        setFaceTexture(Box.TOP, new TextureLoader("skybox/up.bmp", null).getTexture());
        setFaceTexture(Box.BOTTOM, new TextureLoader("skybox/down.bmp", null).getTexture());
    }

    private void setFaceTexture(int faceID, Texture tex) {
        Appearance appearance = new Appearance();

        tex.setBoundaryModeS(Texture.CLAMP_TO_EDGE);
        tex.setBoundaryModeT(Texture.CLAMP_TO_EDGE);

        PolygonAttributes pa = new PolygonAttributes();
        pa.setCullFace(PolygonAttributes.CULL_FRONT);
        appearance.setPolygonAttributes(pa);

        appearance.setTexture(tex);

        getShape(faceID).setAppearance(appearance);
    }

}
