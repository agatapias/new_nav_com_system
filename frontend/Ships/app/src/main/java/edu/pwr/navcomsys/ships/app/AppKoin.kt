package edu.pwr.navcomsys.ships.app

import android.content.Context
import edu.pwr.navcomsys.ships.model.datasource.remote.SignalRemoteDataSource
import edu.pwr.navcomsys.ships.model.datasource.remote.UserInfoRemoteDataSource
import edu.pwr.navcomsys.ships.model.datasource.remote.impl.SignalRemoteDataSourceImpl
import edu.pwr.navcomsys.ships.model.datasource.remote.impl.UserInfoRemoteDataSourceImpl
import edu.pwr.navcomsys.ships.model.repository.SignalRepository
import edu.pwr.navcomsys.ships.model.repository.UserInfoRepository
import edu.pwr.navcomsys.ships.screens.conversation.ConversationViewModel
import edu.pwr.navcomsys.ships.screens.main.MainViewModel
import edu.pwr.navcomsys.ships.screens.message.MessageViewModel
import edu.pwr.navcomsys.ships.screens.phone.PhoneViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

object AppKoin {

    private val environments = module {
        single { AppRetrofit.create() }
    }

    private val dataSources = module {
        singleOf(::UserInfoRemoteDataSourceImpl) bind UserInfoRemoteDataSource::class
        singleOf(::SignalRemoteDataSourceImpl) bind SignalRemoteDataSource::class
    }

    private val repositories = module {
        single { UserInfoRepository(get()) }
        single { SignalRepository(get()) }
    }

    private val viewModels = module {
        viewModelOf(::MainViewModel)
        viewModelOf(::PhoneViewModel)
        viewModelOf(::MessageViewModel)
        viewModelOf(::ConversationViewModel)
    }

    private val modules by lazy {
        listOf(
            environments,
            dataSources,
            repositories,
            viewModels,
        )
    }

    fun init(context: Context) {
        startKoin {
            androidLogger()
            androidContext(context)
            modules(modules)
        }
    }
}