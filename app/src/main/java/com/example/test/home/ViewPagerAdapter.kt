package com.example.test.home

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.*
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.example.test.R
import com.example.test.databinding.ImageListItemBinding
import com.example.test.db.ItemData
import com.example.test.utils.ProgressDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

class ViewPagerAdapter: RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder> {
    private lateinit var mContext: Context
    var fragmentManager: FragmentManager
    var imgList: List<Bitmap>
    var itemList: List<ItemData>
    var stringList: HashMap<String, String>

    constructor(fm: FragmentManager) {
        this.fragmentManager = fm
        this.imgList = ArrayList()
        this.itemList = ArrayList()
        this.stringList = HashMap()
    }

    val outBorder = Paint().apply {
        color = Color.BLACK
        strokeWidth = 3f
        style = Paint.Style.STROKE
    }

    val innerBorder = Paint().apply {
        color = Color.BLACK
        strokeWidth = 1f
        style = Paint.Style.STROKE
    }

    val fill = Paint().apply {
        color = Color.WHITE
        style = Paint.Style.FILL
    }

    val txt = Paint().apply {
        color = Color.BLACK
        textSize = 24f
        textAlign = Paint.Align.CENTER
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        val binding = ImageListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PagerViewHolder(binding)
    }

    fun resizeRect(rect: Rect) {
        rect.left -= 10
        rect.top -= 10
        rect.right += 10
        rect.bottom += 10
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onBindViewHolder(holder: ViewPagerAdapter.PagerViewHolder, position: Int) {
        holder.bind()
    }

    private fun overlayMark(bmp1: Bitmap, bmp2: Bitmap): Bitmap {
        val bmOverlay = Bitmap.createBitmap(bmp1.width, bmp1.height, bmp1.config)
        val canvas = Canvas(bmOverlay)
        canvas.drawBitmap(bmp1, 0f, 0f, null)
        canvas.drawBitmap(bmp2, 0f, 0f, null)
        return bmOverlay
    }

    override fun getItemCount(): Int {
        return imgList.count()
    }

    fun addTwoImages(origin_img: Bitmap): Bitmap {
        if (itemList == null || itemList.count() == 0) {
            return origin_img
        }

        val bitmap = origin_img.copy(Bitmap.Config.ARGB_8888, true)

        val maxTitle = itemList.maxBy { it.itemName.length }

        val maxTRect = Rect()
        txt.getTextBounds(maxTitle.itemName, 0, maxTitle.itemName.length, maxTRect)
        resizeRect(maxTRect)

        val w = 300f
        val h = maxTRect.height() * itemList.count() + 20f

        val maxCRect = Rect(maxTRect)
        maxCRect.left = maxTRect.right
        maxCRect.right = (w - 30).toInt()

        // 테이블 비트맵
        val b = Bitmap.createBitmap(w.toInt(), h.toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(b)

        // 외부 border
        canvas.drawRect(0f, 0f, w, h, fill)
        canvas.drawRect(0f, 0f, w, h, outBorder)
        canvas.translate(20f, 10f - txt.fontMetrics.top)

        for(i in 0 until itemList.count()) {
            val title = itemList[i].itemName
            val content = stringList.getOrDefault(title, "")

            canvas.drawRect(maxTRect, innerBorder)
            canvas.drawText(title, (maxTRect.right + maxTRect.left) / 2.0f, 0f, txt)

            val cBound = Rect()
            txt.getTextBounds(content, 0, content.length, cBound)
            cBound.left += maxCRect.left
            cBound.right += maxCRect.left

            if(cBound.right >= maxCRect.right) {
                val l = content.substring(0, 10)
                canvas.drawText(l, (maxCRect.right + maxCRect.left) / 2.0f, 0f, txt)
                val l2 = content.substring(10, content.length)
                canvas.drawText(l2, (maxCRect.right + maxCRect.left) / 2.0f, 30f, txt)
                val newRct = Rect(maxCRect)
                newRct.bottom += 30
                canvas.drawRect(newRct, innerBorder)
            } else {
                canvas.drawRect(maxCRect, innerBorder)
                canvas.drawText(content, (maxCRect.right + maxCRect.left) / 2.0f, 0f, txt)
            }

            canvas.translate(0f, (maxTRect.bottom.toFloat() - maxTRect.top.toFloat()))
        }

        // 이미지 크기 변경
        val iWidth = origin_img.width.toFloat()
        val iHeight = origin_img.height.toFloat()
        val aspectRatio = iHeight / iWidth
        val targetHeight = (900 * aspectRatio).toInt()

        val bm = Bitmap.createScaledBitmap(bitmap, 900, targetHeight, true)

        // 두 비트맵 합성
        return overlayMark(bm, b)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun save(context: Context) {
        if(imgList == null || imgList.count() == 0) {
            Toast.makeText(context, "이미지를 선택해 주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        val relativeLocation = Environment.DIRECTORY_DCIM + File.separator + "현황판";
        val dlg = ProgressDialogFragment(imgList.count(), "이미지 저장 중입니다...")
        dlg.show(this.fragmentManager, dlg.tag)
        CoroutineScope(Dispatchers.Main).launch {
            val ret = CoroutineScope(Dispatchers.IO).async {
                for(uri in imgList) {
                    val img = addTwoImages(uri)
                    val stream = ByteArrayOutputStream()
                    img.compress(Bitmap.CompressFormat.PNG, 100, stream)
                    saveImageToGallery(relativeLocation, stream.toByteArray(), System.currentTimeMillis())
                    stream.close()
                    dlg.addProgress()
                }

                dlg.dismiss()
            }
            ret.await()
            Toast.makeText(context, relativeLocation +"에 저장되었습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    fun saveImageToGallery(relativeLocation: String, array: ByteArray, currentTime: Long) : Boolean {
        val values = ContentValues()

        val contentResolver: ContentResolver = mContext.contentResolver

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "${currentTime}.jpg")
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
            values.put(MediaStore.Images.Media.IS_PENDING, 1)
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)

            val item = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            try {
                val pdf = contentResolver.openFileDescriptor(item!!, "w", null)
                val fos = FileOutputStream(pdf?.fileDescriptor)
                fos.write(array)
                fos.close()

                values.clear()
                values.put(MediaStore.Images.Media.IS_PENDING, 0)
                contentResolver.update(item, values, null, null)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                return false
            } catch (e: java.lang.Exception) {
                return false
            }
            return true
        } else {
            val rootFolder = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "현황판") //루트 폴더 생성
            if (!rootFolder.exists()) {
                rootFolder.mkdirs()
            }

            val imageRoot = File(
                "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)}/현황판",
                "${currentTime}.jpg"
            )

            try {
                val outputStream = FileOutputStream(imageRoot)
                outputStream.write(array)
                outputStream.close()

                values.put(MediaStore.Images.Media.DISPLAY_NAME, "${currentTime}.jpg")
                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
                values.put(MediaStore.Images.Media.DATA, "${rootFolder.path}/${currentTime}.jpg")
                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)!!
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
            return true
        }
    }

    inner class PagerViewHolder(val binding: ImageListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            val pos = adapterPosition
            binding.imageView.setOnClickListener {
                val largeFragment = LargeImageFragment(imgList[pos])
                largeFragment.show(fragmentManager, largeFragment.tag)
            }

            binding.imageView.setOnLongClickListener {
//                sharedViewModel.removeImg(pos)
                return@setOnLongClickListener true
            }

            binding.imageView.setImageBitmap(addTwoImages(imgList[pos]))
        }
    }
}