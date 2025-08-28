package com.steadymate.app.data.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import com.steadymate.app.data.proto.OnboardingPrefs
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Serializer for OnboardingPrefs Proto DataStore
 */
@Singleton
class OnboardingPrefsSerializer @Inject constructor() : Serializer<OnboardingPrefs> {
    
    override val defaultValue: OnboardingPrefs = OnboardingPrefs.getDefaultInstance()
    
    override suspend fun readFrom(input: InputStream): OnboardingPrefs {
        try {
            return OnboardingPrefs.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }
    
    override suspend fun writeTo(t: OnboardingPrefs, output: OutputStream) {
        t.writeTo(output)
    }
}
