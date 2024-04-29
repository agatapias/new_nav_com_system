package edu.pwr.navcomsys.ships.app

import android.content.Context
import edu.pwr.navcomsys.ships.screens.conversation.ConversationViewModel
import edu.pwr.navcomsys.ships.screens.main.MainViewModel
import edu.pwr.navcomsys.ships.screens.message.MessageViewModel
import edu.pwr.navcomsys.ships.screens.phone.PhoneViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.context.GlobalContext.startKoin
import org.koin.dsl.module

object AppKoin {

    private val viewModels = module {
        viewModelOf(::MainViewModel)
        viewModelOf(::PhoneViewModel)
        viewModelOf(::MessageViewModel)
        viewModelOf(::ConversationViewModel)
    }

    private val modules by lazy {
        listOf(
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