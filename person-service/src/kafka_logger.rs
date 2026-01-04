use rdkafka::config::ClientConfig;
use rdkafka::producer::{FutureProducer, FutureRecord};
use serde::{Deserialize, Serialize};
use std::time::Duration;
use chrono::{DateTime, Utc};

#[derive (Serialize, Deserialize, Clone, Debug)]
pub struct RestLog {
    pub request_id: String,
    pub service: String,
    pub endpoint: String,
    pub method: String,
    pub status_code: u16,
    pub duration_ms: u64,
    pub timestamp: DateTime<Utc>,
    pub query: Option<String>,
}

#[derive(Clone)]
pub struct KafkaLogger {
    producer: FutureProducer,
    topic: String,
}

impl KafkaLogger {
    pub fn new(brokers: &str, topic: &str) -> Result<Self, String> {
        let producer: FutureProducer = ClientConfig::new()
            .set("bootstrap.servers", brokers)
            .set("message.timeout.ms", "5000")
            .set("queue.buffering.max.messages", "100000")
            .create()
            .map_err(|e| format!("Failed to create Kafka pridcuer: {}", e))?;

        println!("Kafka logger initialized: broker={}, topic={}", brokers, topic);

        Ok(KafkaLogger {
            producer,
            topic: topic.to_string(),
        })
    }

    pub async fn log_request(&self, log: RestLog) {
        let payload = match serde_json::to_string(&log) {
            Ok(p) => p,
            Err(e) => {
                eprintln!("Failed to serialize log {}", e);
                return
            }
        };

        let record = FutureRecord::to(&self.topic)
            .key(&log.request_id)
            .payload(&payload);

        if let Err((err, _)) = self.producer.send(record, Duration::from_secs(0)).await {
            eprintln!("Failed to send log to Kafka {}", err);
        }
    }
}
