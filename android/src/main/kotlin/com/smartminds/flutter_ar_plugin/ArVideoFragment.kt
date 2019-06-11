package com.shliama.augmentedvideotutorial

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.ar.core.*
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ExternalTexture
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.smartminds.flutter_ar_plugin.Constants
import java.io.IOException


open class ArVideoFragment : ArFragment() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var externalTexture: ExternalTexture
    private lateinit var videoRenderable: ModelRenderable
    private lateinit var facebookRenderable: ModelRenderable
    private lateinit var callRenderable: ModelRenderable
    private lateinit var borderRenderable: ModelRenderable
    private lateinit var messageRenderable: ModelRenderable
    private lateinit var videoAnchorNode: AnchorNode
    private lateinit var border: AnchorNode
    private lateinit var facebook: Node
    private lateinit var call: Node
    private lateinit var message: Node



    private var activeAugmentedImage: AugmentedImage? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mediaPlayer = MediaPlayer()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)

        planeDiscoveryController.hide()
        planeDiscoveryController.setInstructionView(null)
        arSceneView.planeRenderer.isEnabled = false
        arSceneView.isLightEstimationEnabled = false

        initializeSession()
        createArScene()

        return view
    }

    override fun getSessionConfiguration(session: Session): Config {

        fun loadAugmentedImageBitmap(imageName: String): Bitmap =
                requireContext().assets.open(imageName).use { return BitmapFactory.decodeStream(it) }

        fun setupAugmentedImageDatabase(config: Config, session: Session): Boolean {
            try {
                config.augmentedImageDatabase = AugmentedImageDatabase(session).also { db ->
                    for((key ,value) in Constants.data_map){
                        db.addImage(value, loadAugmentedImageBitmap(key))
                    }
                }
                return true
            } catch (e: IllegalArgumentException) {
                Log.e(TAG, "Could not add bitmap to augmented image database", e)
            } catch (e: IOException) {
                Log.e(TAG, "IO exception loading augmented image bitmap.", e)
            }
            return false
        }

        return super.getSessionConfiguration(session).also {
            it.lightEstimationMode = Config.LightEstimationMode.DISABLED
            it.focusMode = Config.FocusMode.AUTO

            if (!setupAugmentedImageDatabase(it, session)) {
                Toast.makeText(requireContext(), "Could not setup augmented image database", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun createArScene() {
        // Create an ExternalTexture for displaying the contents of the video.
        externalTexture = ExternalTexture().also {
            mediaPlayer.setSurface(it.surface)
        }

        // Create a renderable with a material that has a parameter of type 'samplerExternal' so that
        // it can display an ExternalTexture.
        ModelRenderable.builder()
                .setSource(requireContext(), Uri.parse("models/augmented_video_model.sfb"))
                .build()
                .thenAccept { renderable ->
                    videoRenderable = renderable
                    renderable.isShadowCaster = false
                    renderable.isShadowReceiver = false
                    renderable.material.setExternalTexture("videoTexture", externalTexture)
                }
                .exceptionally { throwable ->
                    Log.e(TAG, "Could not create ModelRenderable", throwable)
                    return@exceptionally null
                }

        ModelRenderable.builder()
                .setSource(requireContext(), Uri.parse("models/border.sfb"))
                .build()
                .thenAccept { renderable ->
                    borderRenderable = renderable

                }
                .exceptionally { throwable ->
                    Log.e(TAG, "Could not create ModelRenderable", throwable)
                    return@exceptionally null
                }

        ModelRenderable.builder()
                .setSource(requireContext(), Uri.parse("models/facebook_button.sfb"))
                .build()
                .thenAccept { renderable ->
                    facebookRenderable = renderable

                }
                .exceptionally { throwable ->
                    Log.e(TAG, "Could not create ModelRenderable", throwable)
                    return@exceptionally null
                }

        ModelRenderable.builder()
                .setSource(requireContext(), Uri.parse("models/call_button.sfb"))
                .build()
                .thenAccept { renderable ->
                    callRenderable = renderable

                }
                .exceptionally { throwable ->
                    Log.e(TAG, "Could not create ModelRenderable", throwable)
                    return@exceptionally null
                }

        ModelRenderable.builder()
                .setSource(requireContext(), Uri.parse("models/message_button.sfb"))
                .build()
                .thenAccept { renderable ->
                    messageRenderable = renderable

                }
                .exceptionally { throwable ->
                    Log.e(TAG, "Could not create ModelRenderable", throwable)
                    return@exceptionally null
                }

        videoAnchorNode = AnchorNode().apply {
            setParent(arSceneView.scene)
        }
        border = AnchorNode().apply {
            setParent(arSceneView.scene)
        }

        facebook = Node().apply {
            setParent(border)
        }

        message = Node().apply {
            setParent(border)
        }

        call = Node().apply {
            setParent(border)
        }
    }

    override fun onUpdate(frameTime: FrameTime) {
        val frame = arSceneView.arFrame
        if (frame == null || frame.camera.trackingState != TrackingState.TRACKING) {
            return
        }

        val updatedAugmentedImages = frame.getUpdatedTrackables(AugmentedImage::class.java)
        for (augmentedImage in updatedAugmentedImages) {
            if (activeAugmentedImage != augmentedImage && augmentedImage.trackingState == TrackingState.TRACKING && augmentedImage.trackingMethod == AugmentedImage.TrackingMethod.FULL_TRACKING) {
                try {
                    Log.d("Jatin", "In For loop")
                    dismissArVideo()
                    playbackArVideo(augmentedImage)
                    break
                } catch (e: Exception) {
                    Log.e(TAG, "Could not play video [${augmentedImage.name}]", e)
                }
            }
            if (activeAugmentedImage == augmentedImage && augmentedImage.trackingState == TrackingState.TRACKING && augmentedImage.trackingMethod != AugmentedImage.TrackingMethod.FULL_TRACKING) {
                try {
                    Log.d("Jatin", "In For loop: Not fully Tacking" + augmentedImage.trackingMethod)
                    dismissArVideo()
                    break
                } catch (e: Exception) {
                    Log.e(TAG, "Could not play video [${augmentedImage.name}]", e)
                }
            }
        }
    }

    private fun dismissArVideo() {
        Log.d("Jatin", "In DismissAR Function")
        videoAnchorNode.anchor?.detach()
        videoAnchorNode.renderable = null
        border.anchor?.detach()
        border.renderable = null
        activeAugmentedImage = null
        mediaPlayer.reset()
    }

    private fun playbackArVideo(augmentedImage: AugmentedImage) {
        Log.d("Jatin", "In CreateAR Function")
        Log.d("Jatin", "Plying = ${augmentedImage.name}")

        requireContext().assets.openFd(augmentedImage.name)
                .use { descriptor ->
                    mediaPlayer.setDataSource(descriptor)
                }.also {
                    mediaPlayer.isLooping = true
                    mediaPlayer.prepare()
                    mediaPlayer.start()
                }


        videoAnchorNode.anchor = augmentedImage.createAnchor(augmentedImage.centerPose)
        border.anchor = augmentedImage.createAnchor(augmentedImage.centerPose)

        videoAnchorNode.localScale = Vector3(
                1.0f*augmentedImage.extentX, // width
                1.0f,
                1.0f*augmentedImage.extentZ
        ) // height
        videoAnchorNode.localPosition = Vector3(
                0.0f*augmentedImage.extentX, // width
                0.0f,
                0.0f*augmentedImage.extentZ
        )

        border.localScale = Vector3(
                1.95f*augmentedImage.extentX, // width
                1.0f,
                1.3f*augmentedImage.extentZ
        )

        border.localPosition = Vector3(
                0.0f*augmentedImage.extentX, // width
                0.0f,
                0.0f*augmentedImage.extentZ
        )
        facebook.localScale = Vector3(
                0.6f,0.1f,0.6f
        )

        facebook.localPosition = Vector3(
                0.325f, 0.0f, -0.2f
        )

        facebook.setOnTapListener { hitTestResult, motionEvent ->
            val uri = Uri.parse("https://mail.google.com/mail")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context?.startActivity(intent)
        }




        message.localScale = Vector3(
                0.6f,0.1f,0.6f
        )

        message.localPosition = Vector3(
                0.325f, 0.0f, 0.0f
        )

        message.setOnTapListener { hitTestResult, motionEvent ->
            val uri = Uri.parse("https://www.instagram.com/dream_ar_/")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context?.startActivity(intent)
        }
        call.localScale = Vector3(
                0.6f,0.1f,0.6f
        )

        call.localPosition = Vector3(
                0.325f , 0.0f, 0.2f
        )

        call.setOnTapListener { hitTestResult, motionEvent ->
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:8800211079")
            context?.startActivity(intent)
        }


        activeAugmentedImage = augmentedImage

        externalTexture.surfaceTexture.setOnFrameAvailableListener {
            it.setOnFrameAvailableListener(null)
            videoAnchorNode.renderable = videoRenderable
            border.renderable = borderRenderable
            facebook.renderable = facebookRenderable
            call.renderable = callRenderable
            message.renderable = messageRenderable
        }
    }

    override fun onPause() {
        super.onPause()
        dismissArVideo()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    companion object {
        private const val TAG = "ArVideoFragment"
    }
}