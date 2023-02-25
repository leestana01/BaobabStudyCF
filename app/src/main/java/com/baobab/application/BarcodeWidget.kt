package com.baobab.application

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.widget.RemoteViews
import java.io.File
import java.io.FileInputStream


class BarcodeWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context,appWidgetManager: AppWidgetManager?,appWidgetIds: IntArray ) {
        // 위젯 메타 데이터를 구성 할 때 updatePeriodMillis 라는 업데이트 주기 값을 설정하게 되며, 이 주기에 따라 호출
        //  앱 위젯이 추가 될 떄에도 호출 되므로 Service 와의 상호작용 등의 초기 설정이 필요 할 경우에도 이 메소드를 통해 구현
//        super.onUpdate(context, appWidgetManager, appWidgetIds)
        val file = File(context.filesDir, "barcode.png")
        if (file.exists()) {
            for (appWidgetId in appWidgetIds) {
                val remoteViews = RemoteViews(context.packageName, R.layout.barcode_widget)
//                val target : AppWidgetTarget = object : AppWidgetTarget(context, R.id.widget_barcode_image, remoteViews, appWidgetId) {
//                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                        super.onResourceReady(resource, transition)
//                    }
//                }
//                Glide.with(context.applicationContext).asBitmap().load(file).into(target)
//                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                val bitmapOrg = pathToBitmap(file.absolutePath)
                val bitmapEdit = scaleBitmap(bitmapOrg, 1800F, 600F)
                remoteViews.setImageViewBitmap(R.id.widget_barcode_image, bitmapEdit)
                appWidgetManager?.updateAppWidget(appWidgetId, remoteViews)
            }
        }
    }

//    override fun onAppWidgetOptionsChanged(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int, newOptions: Bundle?) {
////        updateAppWidget(context, appWidgetManager, appWidgetId)
//        }
//
//    override fun onEnabled(context: Context) {
//        // 앱 위젯은 여러개가 등록 될 수 있는데, 최초의 앱 위젯이 등록 될 때 호출
//    }
//
//    override fun onDisabled(context: Context) {
//        // onEnabled() 와는 반대로 마지막의 최종 앱 위젯 인스턴스가 삭제 될 때 호출
//    }
//
//    override fun onRestored(context: Context?, oldWidgetIds: IntArray?, newWidgetIds: IntArray?) {
//        // 앱이 구글에서 백업되었을 때 호출. 거의 호출되지 않음
//    }
//
//    override fun onDeleted(context: Context?, appWidgetIds: IntArray?) {
//        // 해당 앱 위젯이 삭제 될 때 호출
//    }
}

//internal fun updateAppWidget(
//    context: Context,
//    appWidgetManager: AppWidgetManager,
//    appWidgetId: Int
//) {
//    val views = RemoteViews(context.packageName, R.layout.barcode_widget)
////    views.setImageViewBitmap(R.id.widget_barcode_image, bitmap)
////    views.setImageViewUri(R.id.widget_barcode_image, Uri.parse(file.absolutePath))
////    views.setImageViewResource(R.id.widget_barcode_image, R.drawable.teest)
//
//    appWidgetManager.updateAppWidget(appWidgetId, views)
//}

fun pathToBitmap(path: String): Bitmap? {
    return try {
        val f = File(path)
        val options = BitmapFactory.Options()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888
        BitmapFactory.decodeStream(FileInputStream(f), null, options)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun scaleBitmap(bitmapToScale: Bitmap?, newWidth: Float, newHeight: Float): Bitmap? {
    if (bitmapToScale == null) return null
    //get the original width and height
    val width = bitmapToScale.width
    val height = bitmapToScale.height
    // create a matrix for the manipulation
    val matrix = Matrix()

// resize the bit map
    matrix.postScale(newWidth / width, newHeight / height)

// recreate the new Bitmap and set it back
    return Bitmap.createBitmap(
        bitmapToScale,
        0,
        0,
        bitmapToScale.width,
        bitmapToScale.height,
        matrix,
        true
    )
}
