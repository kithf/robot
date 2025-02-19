package org.firstinspires.ftc.teamcode.lib.recognition

import android.graphics.Bitmap
import com.google.android.gms.tasks.Task
import com.google.android.odml.image.BitmapMlImageBuilder
import com.google.android.odml.image.MlImage
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.aztec.AztecReader
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode
import com.qualcomm.robotcore.eventloop.opmode.OpMode
import com.qualcomm.robotcore.hardware.HardwareMap
import com.vuforia.Image
import com.vuforia.PIXEL_FORMAT
import com.vuforia.Vuforia
import org.firstinspires.ftc.robotcore.external.ClassFactory
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer
import java.util.concurrent.BlockingQueue
import com.google.mlkit.vision.barcode.BarcodeScannerOptions as BarcodeScannerOptions

class Scanner(
  private val op_mode: OpMode
) {
  private var vuforia: VuforiaLocalizer
  private var webcam: WebcamName = op_mode.hardwareMap.get(WebcamName::class.java, "Webcam 1")
  var image: Image? = null
    get() {
      var frame = try {
        vuforia.frameQueue.take()
      } catch (e: Throwable) {
        null
      }

      if (frame != null) {
        return frame.getImage(0)
      }

      return null
    }

  fun get_bitmap(): Bitmap? {
    var frame = try {
      vuforia.frameQueue.take()
    } catch (e: Throwable) {
      null
    }

    if (frame != null) {
      return vuforia.convertFrameToBitmap(frame)
    }

    return null
  }

  init {
    val params = VuforiaLocalizer.Parameters(
      op_mode.hardwareMap.appContext.resources.getIdentifier(
        "cameraMonitorViewId",
        "id",
        op_mode.hardwareMap.appContext.packageName
      )
    )

    params.vuforiaLicenseKey = "AdeSWBz/////AAABmQM7FzHd2UISqz9MHaOV0yBylrk9bfGQJn/2vdP6LgKJDa2EK1VK2FtAqusmwFbBOscjLE8tPDYhAsZSW19IyXP7HERbWUE/rnGY+F5gIDRk1SPBRAl2EugJn0cUWPG0bVlQ7qmAG503Pkr+BSZKZIhZxTMLFKP5CPgEZC4kpqP7gjUn5KP4dFIb9hsCpFttr2AlxdOFeEvclnpSCCnlWAgXQ5zsX0q3wv0Oqs8DdCAARd2TJ7OMkrgBtYvS06PZykfj2UDJrqcP7JLOOFE6D9B/am2tMXdyqNXebnmmXV8v1yYou8i6sL1TCFi43v3DyrxPho0LrnGqpzy4niWHkPPfjFOXmbwAJU8R79C+L/gr"
    params.cameraName = webcam

    vuforia = ClassFactory.getInstance().createVuforia(params)
    vuforia.frameQueueCapacity = 6
    Vuforia.setFrameFormat(PIXEL_FORMAT.RGB565, true)

  }

  fun scan_aztek(): Task<MutableList<Barcode>>? {
    val options = BarcodeScannerOptions.Builder()
      .build()

    var img = this.get_bitmap()
    if (img != null) {
      val ml_image = BitmapMlImageBuilder(img).build()

      val scanner = BarcodeScanning.getClient(options)
      return scanner.process(ml_image)
    }

    return null
  }
}