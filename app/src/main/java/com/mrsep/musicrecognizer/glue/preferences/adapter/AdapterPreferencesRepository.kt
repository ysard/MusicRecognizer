package com.mrsep.musicrecognizer.glue.preferences.adapter

import com.mrsep.musicrecognizer.UserPreferencesProto
import com.mrsep.musicrecognizer.UserPreferencesProto.*
import com.mrsep.musicrecognizer.core.common.BidirectionalMapper
import com.mrsep.musicrecognizer.core.common.Mapper
import com.mrsep.musicrecognizer.data.preferences.PreferencesDataRepository
import com.mrsep.musicrecognizer.feature.preferences.domain.PreferencesRepository
import com.mrsep.musicrecognizer.feature.preferences.domain.UserPreferences
import com.mrsep.musicrecognizer.feature.preferences.domain.UserPreferences.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AdapterPreferencesRepository @Inject constructor(
    private val preferencesDataRepository: PreferencesDataRepository,
    private val preferencesMapper: Mapper<UserPreferencesProto, UserPreferences>,
    private val requiredServicesMapper: BidirectionalMapper<RequiredServicesProto, RequiredServices>,
    private val schedulePolicyMapper: BidirectionalMapper<SchedulePolicyProto, SchedulePolicy>
) : PreferencesRepository {

    override val userPreferencesFlow: Flow<UserPreferences>
        get() = preferencesDataRepository.userPreferencesFlow
            .map { proto -> preferencesMapper.map(proto) }

    override suspend fun saveApiToken(newToken: String) {
        preferencesDataRepository.saveApiToken(newToken)
    }

    override suspend fun setOnboardingCompleted(value: Boolean) {
        preferencesDataRepository.setOnboardingCompleted(value)
    }

    override suspend fun setNotificationServiceEnabled(value: Boolean) {
        preferencesDataRepository.setNotificationServiceEnabled(value)
    }

    override suspend fun setDynamicColorsEnabled(value: Boolean) {
        preferencesDataRepository.setDynamicColorsEnabled(value)
    }

    override suspend fun setDeveloperModeEnabled(value: Boolean) {
        preferencesDataRepository.setDeveloperModeEnabled(value)
    }

    override suspend fun setRequiredServices(requiredServices: RequiredServices) {
        preferencesDataRepository.setRequiredServices(
            requiredServicesMapper.reverseMap(requiredServices)
        )
    }

    override suspend fun setSchedulePolicy(schedulePolicy: SchedulePolicy) {
        preferencesDataRepository.setSchedulePolicy(
            schedulePolicyMapper.reverseMap(schedulePolicy)
        )
    }

}