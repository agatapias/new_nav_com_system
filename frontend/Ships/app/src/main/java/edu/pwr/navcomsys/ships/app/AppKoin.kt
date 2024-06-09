package edu.pwr.navcomsys.ships.app

import android.content.Context
import com.google.android.gms.location.LocationServices
import edu.pwr.navcomsys.ships.model.datasource.remote.SignalRemoteDataSource
import edu.pwr.navcomsys.ships.model.datasource.remote.UserInfoRemoteDataSource
import edu.pwr.navcomsys.ships.model.datasource.remote.impl.SignalRemoteDataSourceImpl
import edu.pwr.navcomsys.ships.model.datasource.remote.impl.UserInfoRemoteDataSourceImpl
import edu.pwr.navcomsys.ships.model.repository.ChatMessageRepository
import edu.pwr.navcomsys.ships.model.repository.PeerRepository
import edu.pwr.navcomsys.ships.model.repository.SignalRepository
import edu.pwr.navcomsys.ships.model.repository.UserInfoRepository
import edu.pwr.navcomsys.ships.screens.account.AccountViewModel
import edu.pwr.navcomsys.ships.screens.conversation.ConversationViewModel
import edu.pwr.navcomsys.ships.screens.main.MainViewModel
import edu.pwr.navcomsys.ships.screens.message.MessageViewModel
import edu.pwr.navcomsys.ships.screens.phone.PhoneViewModel
import org.koin.android.ext.koin.androidApplication
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
        single { AppDatabase.create(androidApplication()) }
    }

    private val dataSources = module {
        singleOf(::UserInfoRemoteDataSourceImpl) bind UserInfoRemoteDataSource::class
        singleOf(::SignalRemoteDataSourceImpl) bind SignalRemoteDataSource::class
        single { get<AppDatabase>().userLocalDataSource() }
        single { get<AppDatabase>().chatMessageLocalDataSource() }
        single { LocationServices.getFusedLocationProviderClient(androidApplication()) }
    }

    private val repositories = module {
        single { PeerRepository() }
        single { UserInfoRepository(get(), get()) }
        single { SignalRepository(get()) }
        single { ChatMessageRepository(get(), get()) }
    }

    private val viewModels = module {
        viewModelOf(::MainViewModel)
        viewModelOf(::PhoneViewModel)
        viewModelOf(::MessageViewModel)
        viewModelOf(::ConversationViewModel)
        viewModelOf(::AccountViewModel)
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