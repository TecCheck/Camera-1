package app.grapheneos.camera.ui

import android.app.Dialog
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.camera.core.AspectRatio
import androidx.camera.core.ImageCapture
import androidx.camera.core.TorchState
import app.grapheneos.camera.R
import app.grapheneos.camera.config.CamConfig
import app.grapheneos.camera.ui.activities.MainActivity

class SettingsDialog(mActivity: MainActivity) : Dialog(mActivity) {

    private var locToggle: ToggleButton
    var flashToggle: ImageView
    private var aRToggle: ToggleButton
    private var torchToggle: ToggleButton
    private var gridToggle: ImageView
    private var mActivity: MainActivity

    init {
        setContentView(R.layout.settings)
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setDimAmount(0f)
        window?.attributes?.windowAnimations = R.style.SettingsDialogAnim
        setOnDismissListener {
            mActivity.settingsIcon.visibility = View.VISIBLE
        }

        this.mActivity = mActivity

        locToggle = findViewById(R.id.location_toggle)

        flashToggle = findViewById(R.id.flash_toggle_option)
        flashToggle.setOnClickListener {
            mActivity.config.toggleFlashMode()
        }

        aRToggle = findViewById(R.id.aspect_ratio_toggle)
        aRToggle.setOnClickListener {
            mActivity.config.toggleAspectRatio()
        }

        torchToggle = findViewById(R.id.torch_toggle_option)
        torchToggle.setOnClickListener {
            if(mActivity.config.isFlashAvailable) {
                mActivity.config.camera?.cameraControl?.enableTorch(
                    mActivity.config.camera?.cameraInfo?.torchState?.value ==
                            TorchState.OFF
                )
            } else {
                torchToggle.isChecked = false
                Toast.makeText(mActivity,
                    "Flash/Torch is unavailable for this mode", Toast.LENGTH_LONG)
                    .show()
            }
        }

        gridToggle = findViewById(R.id.grid_toggle_option)
        gridToggle.setOnClickListener {
            mActivity.config.gridType = when(mActivity.config.gridType){
                CamConfig.Grid.NONE -> CamConfig.Grid.THREE_BY_THREE
                CamConfig.Grid.THREE_BY_THREE -> CamConfig.Grid.FOUR_BY_FOUR
                CamConfig.Grid.FOUR_BY_FOUR -> CamConfig.Grid.GOLDEN_RATIO
                CamConfig.Grid.GOLDEN_RATIO -> CamConfig.Grid.NONE
            }
            mActivity.previewGrid.postInvalidate()
            updateGridToggleUI()
        }
    }

    private fun updateGridToggleUI(){
        gridToggle.setImageResource(
            when(mActivity.config.gridType) {
                CamConfig.Grid.NONE -> R.drawable.grid_off_circle
                CamConfig.Grid.THREE_BY_THREE -> R.drawable.grid_3x3_circle
                CamConfig.Grid.FOUR_BY_FOUR -> R.drawable.grid_4x4_circle
                CamConfig.Grid.GOLDEN_RATIO -> R.drawable.grid_goldenratio_circle
            }
        )
    }

    override fun show() {
        flashToggle.setImageResource(
            when(mActivity.config.flashMode) {
                ImageCapture.FLASH_MODE_ON -> R.drawable.flash_on_circle
                ImageCapture.FLASH_MODE_AUTO -> R.drawable.flash_auto_circle
                else -> R.drawable.flash_off_circle
            }
        )

        aRToggle.isChecked = mActivity.config.aspectRatio == AspectRatio.RATIO_16_9
        torchToggle.isChecked =
            mActivity.config.camera?.cameraInfo?.torchState?.value == TorchState.ON

        updateGridToggleUI()

        mActivity.settingsIcon.visibility = View.INVISIBLE
        super.show()
    }
}