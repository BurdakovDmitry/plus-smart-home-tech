package collector.client;

import org.apache.kafka.clients.producer.Producer;

public interface CollectorClient {
    Producer<String, byte[]> getProducer();

    void stop();
}
