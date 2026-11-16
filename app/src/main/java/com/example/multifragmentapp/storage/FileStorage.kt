package com.example.multifragmentapp.storage

import android.content.Context
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

/**
 * 文件存储工具类。
 *
 * 写文件：context.openFileOutput(fileName, Context.MODE_PRIVATE) + ObjectOutputStream
 * 读文件：context.openFileInput(fileName) + ObjectInputStream
 *
 * 文件保存在应用私有目录 /data/data/<包名>/files/，其它应用无法访问。
 */
object FileStorage {

    /** 序列化对象并写入私有文件 */
    fun <T : java.io.Serializable> save(context: Context, fileName: String, data: T) {
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { fos ->
            ObjectOutputStream(fos).use { oos ->
                oos.writeObject(data)
            }
        }
    }

    /** 从私有文件反序列化对象；文件不存在或已损坏时返回 null */
    fun <T> load(context: Context, fileName: String): T? {
        val file = File(context.filesDir, fileName)
        if (!file.exists()) return null
        return try {
            context.openFileInput(fileName).use { fis ->
                ObjectInputStream(fis).use { ois ->
                    @Suppress("UNCHECKED_CAST")
                    ois.readObject() as? T
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    /** 删除指定私有文件 */
    fun delete(context: Context, fileName: String) {
        File(context.filesDir, fileName).delete()
    }
}
