use std::net::{Ipv4Addr, SocketAddrV4};
use tokio::net::TcpListener;
use crate::router::router;

use crate::kafka_logger::KafkaLogger;
use std::sync::Arc;

pub struct BasicServer;

impl BasicServer {
    pub async fn run() -> Result<(), std::io::Error> {

        let kafka_logger = Self::init_kafka_logger();


        let ip = Ipv4Addr::new(0, 0, 0, 0);
        let port = 8060;
        let addr = SocketAddrV4::new(ip, port);

        let app = router(kafka_logger);
        let listener = TcpListener::bind(addr).await?;
        
        println!("Server running on http://{}:{}", ip, port);


        axum::serve(listener, app).await

    }

    fn init_kafka_logger() -> Option<Arc<KafkaLogger>> {
        match std::env::var("KAFKA_BROKER") {
            Ok(broker) => {
                match KafkaLogger::new(&broker, "rest-requests-log") {
                    Ok(logger) => Some(Arc::new(logger)),
                    Err(e) => {


                        eprintln!("Failed to init Kafka logger {}", e);
                        None
                    }
                }
            }
            Err(_) => {
                println!("Kafka broker not set");
                None
            }
        }
    }
}
