package com.dotsphoto.plugins

import com.dotsphoto.orm.services.*
import com.dotsphoto.orm.services.repositories.*
import io.ktor.server.application.*
import io.ktor.util.*
import org.koin.core.KoinApplication
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureDI() {
    val dbRepositoriesModule = module {
        singleOf(::AlbumRepository)
        singleOf(::OwnershipRepository)
        singleOf(::PhotoMetadataRepository)
        singleOf(::PhotoRepository)
        singleOf(::SubscriptionPlanRepository)
        singleOf(::SubscriptionRepository)
        singleOf(::UserRepository)
    }
    val dbServiceModule = module {
        singleOf(::AlbumService)
        singleOf(::OwnershipService)
        singleOf(::PhotoMetadataService)
        singleOf(::PhotoService)
        singleOf(::SubscriptionPlanService)
        singleOf(::SubscriptionService)
        singleOf(::UserService)
    }
    install(KoinPlugin) {
        modules(
            dbRepositoriesModule,
            dbServiceModule
        )
    }
}

object KoinPlugin : BaseApplicationPlugin<Application, KoinApplication, Unit> {

    override val key: AttributeKey<Unit>
        get() = AttributeKey("Koin")

    override fun install(
        pipeline: Application,
        configure: KoinApplication.() -> Unit
    ) {
        val monitor = pipeline.environment.monitor
        val koinApplication = startKoin(appDeclaration = configure)
        monitor.raise(EventDefinition(), koinApplication)

        monitor.subscribe(ApplicationStopping) {
            monitor.raise(EventDefinition(), koinApplication)
            stopKoin()
            monitor.raise(EventDefinition(), koinApplication)
        }
    }
}
