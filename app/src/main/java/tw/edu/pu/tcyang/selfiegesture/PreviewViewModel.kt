package tw.edu.pu.tcyang.selfiegesture

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.view.LifecycleCameraController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class PreviewViewModel : ViewModel() {
    // 保存相機控制器
    var cameraController by
    mutableStateOf<LifecycleCameraController?>(null)
        private set

    // 建立並設定 LifecycleCameraController 的方法
    fun setupCameraController(context: Context) {
        if (cameraController == null) {
            cameraController =
                LifecycleCameraController(context)  // 創建實例

            // 設定前置鏡頭(自拍)
            cameraController?.cameraSelector =
                CameraSelector.DEFAULT_FRONT_CAMERA
        }
    }

    // 釋放相機資源的方法
    fun releaseCameraController() {
        cameraController?.unbind()
        cameraController = null
    }


    // 新增：圖像分析結果的狀態
    var analysisResult by mutableStateOf("分析結果：等待中...")
        private set

    // 用於影像分析的執行緒
    private var analysisExecutor: ExecutorService? = null

    // 新增：停止影像分析
    fun stopImageAnalysis() {
        cameraController?.clearImageAnalysisAnalyzer()
    }

    //onCleared() 是 ViewModel 不再被需要時會被系統自動呼叫。
    override fun onCleared() {
        super.onCleared()
        // ViewModel 被清除時，關閉執行緒
        analysisExecutor?.shutdown()
        analysisExecutor = null
    }

    fun startImageAnalysis() {
        // 如果執行緒未初始化，則創建一個
        if (analysisExecutor == null) {
            analysisExecutor = Executors.newSingleThreadExecutor()
        }
        cameraController?.setImageAnalysisAnalyzer(
            analysisExecutor!!,
            ImageAnalysis.Analyzer { imageProxy ->
                // 在這裡處理每一幀圖像的邏輯
                val width = imageProxy.width
                val height = imageProxy.height
                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                // 更新 ViewModel 的狀態
                analysisResult = "圖像 ($width x $height), 旋轉: $rotationDegrees"
                // 完成處理後，必須關閉 ImageProxy
                imageProxy.close()
            }
        )
    }

}