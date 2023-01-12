package com.technokratz.drmsample

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.upstream.DataSourceException
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource.CleartextNotPermittedException
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy.LoadErrorInfo
import com.google.android.exoplayer2.upstream.Loader.UnexpectedLoaderException
import com.google.android.exoplayer2.util.Log
import java.io.FileNotFoundException
import java.net.UnknownHostException

class CustomPolicy : DefaultLoadErrorHandlingPolicy() {

    override fun getRetryDelayMsFor(loadErrorInfo: LoadErrorInfo): Long {
        Log.d("Exo debug", "CustomPolicy getRetryDelayMsFor, ${loadErrorInfo.exception.message}")
        return if (
            (loadErrorInfo.exception is FileNotFoundException ||
            loadErrorInfo.exception.cause is FileNotFoundException ||
            (loadErrorInfo.exception is HttpDataSource.InvalidResponseCodeException &&
                    (loadErrorInfo.exception as? HttpDataSource.InvalidResponseCodeException)?.responseCode == 404) ||
            loadErrorInfo.exception.cause is UnknownHostException ||
            loadErrorInfo.exception is CleartextNotPermittedException ||
            loadErrorInfo.exception is UnexpectedLoaderException ||
            DataSourceException.isCausedByPositionOutOfRange(loadErrorInfo.exception))) {
            C.TIME_UNSET
        } else {
            3000
        }
    }

    override fun getMinimumLoadableRetryCount(dataType: Int): Int {
        return 200
    }
}
