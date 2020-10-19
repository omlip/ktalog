package io.devolan.ktalog.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.devolan.ktalog.items.ItemService
import org.jetbrains.exposed.sql.Database
import org.kodein.di.*

fun DI.MainBuilder.bindServices(hikariConfigFile: String) {

    bind<HikariConfig>() with eagerSingleton {
        val config = HikariConfig(hikariConfigFile)
        config.validate()
        config
    }
    bind<HikariDataSource>() with singleton { HikariDataSource(instance()) }
    bind<Database>() with eagerSingleton {
        val dataSource: HikariDataSource by di.instance()
        val database = Database.connect(dataSource)
        database
    }
    bind<ItemService>() with singleton { ItemService() }
}
