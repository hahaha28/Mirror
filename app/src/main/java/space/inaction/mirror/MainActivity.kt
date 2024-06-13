package space.inaction.mirror

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture
import com.permissionx.guolindev.PermissionX
import space.inaction.mirror.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private lateinit var camera: Camera
    private var zoom: Float = 0.4f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PermissionX.init(this)
            .permissions(Manifest.permission.CAMERA)
            .request { allGranted, _, _ ->
                if (!allGranted) {
                    finish()
                    return@request
                }
                startPreview()
                binding.progressBar.setOnRateChangeListener(object : ProgressBar.OnRateChangeListener{
                    override fun onRateChange(rate: Float) {
                        zoom = rate
                        camera.cameraControl.setLinearZoom(zoom)
                    }
                })
            }

    }

    private fun startPreview() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindPreview(cameraProvider : ProcessCameraProvider) {
        val preview : Preview = Preview.Builder()
            .build()

        val cameraSelector : CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()

        preview.setSurfaceProvider(binding.cameraPreview.getSurfaceProvider())

        camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)
        camera.cameraControl.setLinearZoom(zoom)
    }

}

