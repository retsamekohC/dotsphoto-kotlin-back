package com.dotsphoto.plugins

import com.dotsphoto.orm.services.*
import com.dotsphoto.orm.services.repositories.*
import com.dotsphoto.orm.tables.*
import io.ktor.server.application.*
import io.ktor.util.*
import org.jetbrains.exposed.sql.Database
import org.koin.core.KoinApplication
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.core.qualifier.named

fun Application.configureDI() {

    val serviceModule = org.koin.dsl.module {
        single<AlbumService> { AlbumService(AlbumRepository()) }
        single<OwnershipService> { OwnershipService(OwnershipRepository()) }
        single<PhotoMetadataService> { PhotoMetadataService(PhotoMetadataRepository()) }
        single<PhotoService> { PhotoService(PhotoRepository()) }
        single<SubscriptionPlanService> { SubscriptionPlanService(SubscriptionPlanRepository()) }
        single<SubscriptionService> { SubscriptionService(SubscriptionRepository()) }
        single<UserService> { UserService(UserRepository()) }
    }
    install(KoinPlugin) {
        modules(
            serviceModule
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
