import android.media.Image;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedImage;
import com.google.ar.core.AugmentedImageDatabase;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions;

import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ImageRecognition extends AppCompatActivity implements Scene.OnUpdateListener {

    private static final String TAG = ImageRecognition.class.getSimpleName();

    private ArFragment arFragment;
    private ModelRenderable itemRenderable;
    private FirebaseVisionLabelDetector labelDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_fragment);

        FirebaseVisionLabelDetectorOptions options =
                new FirebaseVisionLabelDetectorOptions.Builder()
                        .setConfidenceThreshold(0.7f)
                        .build();
        labelDetector = FirebaseVision.getInstance().getVisionLabelDetector(options);

        ModelRenderable.builder()
                .setSource(this, R.raw.item)
                .build()
                .thenAccept(renderable -> itemRenderable = renderable)
                .exceptionally(
                        throwable -> {
                            Log.e(TAG, "Error loading item renderable: ", throwable);
                            return null;
                        });

        arFragment.getArSceneView().getScene().addOnUpdateListener(this);
    }

    @Override
    public void onUpdate(FrameTime frameTime) {
        Frame frame = arFragment.getArSceneView().getArFrame();
        if (frame == null) {
            return;
        }

        Collection<AugmentedImage> updatedAugmentedImages =
                frame.getUpdatedTrackables(AugmentedImage.class);
        for (AugmentedImage augmentedImage : updatedAugmentedImages) {
            switch (augmentedImage.getTrackingState()) {
                case PAUSED:
                    String text = "Detecting image...";
                    Log.d(TAG, text);
                    break;

                case TRACKING:
                    Anchor anchor = augmentedImage.createAnchor(augmentedImage.getCenterPose());
                    placeItem(anchor, augmentedImage);
                    break;

                case STOPPED:
                    break;

                default:
                    break;
            }
        }
    }

    private void placeItem(Anchor anchor, AugmentedImage augmentedImage) {
        if (itemRenderable == null) {
            return;
        }

        TransformableNode itemNode = new TransformableNode(arFragment.getTransformationSystem());
        itemNode.setParent(arFragment.getArSceneView().getScene());
        itemNode.setRenderable(itemRenderable);

        float scaleFactor = 0.2f;
       
