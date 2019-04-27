package com.monday8am.cycleradar


import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


// Scheduler provider abstraction:
// based on: https://medium.com/@peter.tackage/an-alternative-to-rxandroidplugins-and-rxjavaplugins-scheduler-injection-9831bbc3dfaf

interface SchedulerProvider {
    fun ui(): Scheduler
    fun computation(): Scheduler
    fun io(): Scheduler
}

internal class AppSchedulerProvider : SchedulerProvider {
    override fun ui(): Scheduler {
        return AndroidSchedulers.mainThread()
    }

    override fun computation(): Scheduler {
        return Schedulers.computation()
    }

    override fun io(): Scheduler {
        return Schedulers.io()
    }
}
