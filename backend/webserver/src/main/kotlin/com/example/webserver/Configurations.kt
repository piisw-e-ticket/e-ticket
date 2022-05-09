package com.example.webserver

import org.springframework.boot.web.server.ErrorPage
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import kotlin.io.path.Path

@Configuration
@EnableWebMvc
class MvcConfig : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        val dist = Path(System.getProperty("user.dir")).resolve("../frontend/dist/frontend/")
            .normalize().toAbsolutePath()

        registry
            .addResourceHandler("/**")
            .addResourceLocations(getSystemDependentUri(dist.toString()))
    }
}

@Configuration
class WebApplicationConfig : WebMvcConfigurer {
    override fun addViewControllers(registry: ViewControllerRegistry) {
        registry.addViewController("/").apply {
            setViewName("forward:/index.html")
            setStatusCode(HttpStatus.OK)
        }
    }

    @Bean
    fun containerCustomizer(): WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {
        return WebServerFactoryCustomizer { container: ConfigurableServletWebServerFactory ->
            container.addErrorPages(ErrorPage(HttpStatus.NOT_FOUND, "/"))
        }
    }
}

fun getSystemDependentUri(path: String): String {
    val systemName = System.getProperty("os.name").lowercase()
    val unixPath = path.replace("\\", "/")
    return when(true) {
        systemName.contains("win") -> "file:///$unixPath/"
        else -> "file:$unixPath/"
    }
}
