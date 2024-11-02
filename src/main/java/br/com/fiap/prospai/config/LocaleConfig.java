package br.com.fiap.prospai.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.util.Locale;

@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    // Configura o idioma padrão e a resolução de idioma para a sessão do usuário
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(new Locale("pt", "BR")); // Define o português do Brasil como padrão
        return slr;
    }

    // Configura o interceptador para troca de idioma via parâmetro "lang" na URL
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor lci = new LocaleChangeInterceptor();
        lci.setParamName("lang"); // Define o nome do parâmetro de idioma como "lang"
        return lci;
    }

    // Configura o recurso de mensagens, que permite acessar os arquivos de propriedades de mensagens
    @Bean
    public MessageSource messageSource() {
        Utf8ResourceBundleMessageSource messageSource = new Utf8ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        return messageSource;
    }

    // Adiciona o interceptador ao registro para permitir troca de idioma durante as requisições
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
}
