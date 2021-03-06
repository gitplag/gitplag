package io.gitplag.core.testconfig

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary

import javax.sql.DataSource

/**
 * Embedded postgres database config
 */
@Configuration
class EmbeddedPgConfig {

    /**
     * Embedded postgres datasource bean
     */
    @Bean("embeddedPg")
    @Primary
    fun dataSource(): DataSource {
        val postgres = EmbeddedPostgres.start()
        postgres.templateDatabase.connection
            .createStatement().execute("CREATE DATABASE PLAGAN")
        return postgres.getDatabase("postgres", "plagan")
    }

}