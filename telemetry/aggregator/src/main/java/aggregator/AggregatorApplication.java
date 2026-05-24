package aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
/**
 * Главный класс сервиса AggregatorApplication.
 */
@SpringBootApplication(excludeName = {
        "net.devh.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration",
        "net.devh.boot.grpc.server.autoconfigure.GrpcServerFactoryAutoConfiguration"
})
@ConfigurationPropertiesScan
public class AggregatorApplication {

    public static void main(String[] args) {
        // Запуск Spring Boot приложения при помощи вспомогательного класса SpringApplication
        // метод run возвращает назад настроенный контекст, который мы можем использовать для
        // получения настроенных бинов
        ConfigurableApplicationContext context = SpringApplication.run(AggregatorApplication.class, args);

        // Получаем бин AggregationStarter из контекста и запускаем основную логику сервиса
        AggregationStarter aggregator = context.getBean(AggregationStarter.class);
        aggregator.start();
    }
}
