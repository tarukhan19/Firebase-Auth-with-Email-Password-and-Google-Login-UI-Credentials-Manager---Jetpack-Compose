package com.demo.authentication.core.data.util

import android.content.Context
import com.demo.authentication.core.domain.utils.ResourceProvider
import javax.inject.Inject

class ResourceProviderImpl @Inject constructor(
    private val context: Context
) : ResourceProvider {
    override fun getString(resId: Int): String {
        return context.getString(resId)
    }
}