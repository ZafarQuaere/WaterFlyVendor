package com.waterfly.vendor.bgtask

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import java.lang.Exception

class MyWorker(context: Context, workerParameters: WorkerParameters): Worker(context,workerParameters) {

    override fun doWork(): Result {
        for (i in 0..10000){
            try {
            Thread.sleep(1000)
                println("doWork >>>.. $i")
                if (i == 10){
                    break
                }
            } catch (e: Exception){
                Result.failure()
            }
        }
        return Result.success()
    }
}