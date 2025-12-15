package tw.edu.pu.tcyang.selfiegesture

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageProxy
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class HandGestureDetector(
    private val context: Context,
    private val scope: CoroutineScope,
    // 使用 callback 傳回辨識結果
    private val onResults: (String) -> Unit
) {
    // MediaPipe HandLandmarker 實例
    private var handLandmarker: HandLandmarker? = null

    init {  // 初始化 HandLandmarker
        scope.launch {
            // 1. 建立 BaseOptions Builder
            val baseOptionsBuilder =
                BaseOptions.builder().setModelAssetPath("hand_landmarker.task")
            // 2. 建立 HandLandmarkerOptions Builder
            val optionsBuilder =
                HandLandmarker.HandLandmarkerOptions.builder()
                    .setBaseOptions(baseOptionsBuilder.build())
                    .setRunningMode(RunningMode.LIVE_STREAM) // 設置為直播模式
                    .setNumHands(2) // 偵測最多兩隻手
                    .setMinHandDetectionConfidence(0.5f)  // 設定最小手部偵測置信度
                    .setMinTrackingConfidence(0.5f)  // 設定最小手部追蹤置信度
                    .setMinHandPresenceConfidence(0.5f)  // 設定最小手部存在置信度
                    .setResultListener { result, inputImage ->
                        val gesture = processHandLandmarkerResult(result)
                        onResults(gesture)
                    }
                    .setErrorListener { error -> onResults("手勢辨識錯誤: ${error.message}") }
            // 3. 創建 HandLandmarker 實例
            handLandmarker = HandLandmarker.createFromOptions(context, optionsBuilder.build())
        }
    }
    // 處理每一幀圖像，並傳遞給 MediaPipe 進行非同步辨識
    fun detect(imageProxy: ImageProxy) {
        handLandmarker?.let { landmarker ->
            // 將 ImageProxy 轉換為 MediaPipe 圖像格式
            val bitmap = imageProxy.toBitmap()
            val matrix = Matrix()
            // 根據相機旋轉角度調整圖像方向
            matrix.postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
            // 前置鏡頭，需要水平翻轉
            matrix.postScale(
                -1f, 1f,
                bitmap.width / 2f, bitmap.height / 2f
            )
// 創建一個新的、已旋轉的 Bitmap
            val rotatedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width,
                bitmap.height, matrix, true
            )

            // 將 Bitmap 物件轉換成 MediaPipe 可以處理的影像格式
            val mpImage = BitmapImageBuilder(rotatedBitmap).build()

            // 執行手部地標辨識(影像格式與時間戳)
            landmarker.detectAsync(mpImage, imageProxy.imageInfo.timestamp)
        }
    }
    //處理 MediaPipe 手部地標辨識結果，並判斷手勢。
    fun processHandLandmarkerResult(result: HandLandmarkerResult): String {
        if (result.landmarks().isEmpty()) {
            return "無手部"
        }

        // 這裡只是簡單的範例，實際的手勢辨識會更複雜
        val landmarks = result.landmarks()[0] // 假設只考慮第一隻手

        val thumbTip = landmarks.get(4)
        val indexTip = landmarks.get(8)
        val middleTip = landmarks.get(12)
        val ringTip = landmarks.get(16)
        val pinkyTip = landmarks.get(20)
        val indexMcp = landmarks.get(5)
        val middleMcp = landmarks.get(9)
        val ringMcp = landmarks.get(13)
        val pinkyMcp = landmarks.get(17)

        // 簡單判斷剪刀：食指和中指尖端高於其掌指關節，其他手指尖端低於其掌指關節 (非常簡化)
        val isScissor = indexTip.y() < indexMcp.y() && middleTip.y() < middleMcp.y() &&
                ringTip.y() > ringMcp.y() && pinkyTip.y() > pinkyMcp.y()

        // 簡單判斷布：所有手指尖端都高於其掌指關節 (非常簡化)
        val isPaper = indexTip.y() < indexMcp.y() && middleTip.y() < middleMcp.y() &&
                ringTip.y() < ringMcp.y() && pinkyTip.y() < pinkyMcp.y()
        // 簡單判斷石頭：所有手指尖端都低於其掌指關節 (非常簡化)
        val isRock = indexTip.y() > indexMcp.y() && middleTip.y() > middleMcp.y() &&
                ringTip.y() > ringMcp.y() && pinkyTip.y() > pinkyMcp.y()

        return when {
            isScissor -> "剪刀"
            isPaper -> "布"
            isRock -> "石頭"
            else -> "未知手勢"
        }
    }

    // 關閉資源的方法
    fun close() {
        handLandmarker?.close()
    }

}