package com.mrsep.musicrecognizer.glue.recognition.di

import com.mrsep.musicrecognizer.core.common.BidirectionalMapper
import com.mrsep.musicrecognizer.core.common.Mapper
import com.mrsep.musicrecognizer.data.audiorecord.AudioRecordingStrategyDo
import com.mrsep.musicrecognizer.data.enqueued.model.EnqueuedRecognitionEntityWithTrack
import com.mrsep.musicrecognizer.data.player.PlayerStatusDo
import com.mrsep.musicrecognizer.data.preferences.ScheduleActionDo
import com.mrsep.musicrecognizer.data.preferences.UserPreferencesDo
import com.mrsep.musicrecognizer.data.remote.RemoteRecognitionResultDo
import com.mrsep.musicrecognizer.data.track.TrackEntity
import com.mrsep.musicrecognizer.feature.recognition.domain.model.AudioRecordingStrategy
import com.mrsep.musicrecognizer.feature.recognition.domain.model.EnqueuedRecognition
import com.mrsep.musicrecognizer.feature.recognition.domain.model.PlayerStatus
import com.mrsep.musicrecognizer.feature.recognition.domain.model.RemoteRecognitionResult
import com.mrsep.musicrecognizer.feature.recognition.domain.model.ScheduleAction
import com.mrsep.musicrecognizer.feature.recognition.domain.model.Track
import com.mrsep.musicrecognizer.feature.recognition.domain.model.UserPreferences
import com.mrsep.musicrecognizer.glue.recognition.mapper.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
interface MapperModule {

    @Binds
    fun bindUserPreferencesMapper(implementation: UserPreferencesMapper):
            Mapper<UserPreferencesDo, UserPreferences>
    @Binds
    fun bindTrackMapper(implementation: TrackMapper): BidirectionalMapper<TrackEntity, Track>

    @Binds
    fun bindRemoteResultMapper(implementation: RemoteResultMapper):
            Mapper<RemoteRecognitionResultDo, RemoteRecognitionResult>

    @Binds
    fun bindRequiredServicesMapper(implementation: RequiredServicesMapper):
            BidirectionalMapper<UserPreferencesDo.RequiredServicesDo, UserPreferences.RequiredServices>

    @Binds
    fun bindAudioRecordingMapper(implementation: AudioRecordingMapper):
            Mapper<AudioRecordingStrategy, AudioRecordingStrategyDo>

    @Binds
    fun bindScheduleActionMapper(implementation: ScheduleActionMapper):
            Mapper<ScheduleActionDo, ScheduleAction>

    @Binds
    fun bindPlayerStatusMapper(implementation: PlayerStatusMapper):
            Mapper<PlayerStatusDo, PlayerStatus>

    @Binds
    fun bindEnqueuedRecognitionMapper(implementation: EnqueuedRecognitionMapper):
    BidirectionalMapper<EnqueuedRecognitionEntityWithTrack, EnqueuedRecognition>

}