use axum::{routing::get, Router, middleware, extract::Request, response::Response};
//use super::handler::{list_all, find_name, find_key};

use std::sync::Arc;
use std::time::Instant;
use chrono::Utc;
use uuid::Uuid;
use crate::handler::{list_all, find_name, find_key};
use crate::kafka_logger::{KafkaLogger, RestLog};

pub fn router(kafka_logger: Option<Arc<KafkaLogger>>) -> Router {
    let mut router = Router::new()
        .route("/person/list", get(list_all))
        .route("/person/find.name", get(find_name))
        .route("/person/find.key", get(find_key));


    if let Some(logger) = kafka_logger {
        router = router.layer(middleware::from_fn(move |req, next| {
            logging_middleware(req, next, logger.clone())
        }));
    }
    router
}


async fn logging_middleware(
    req: Request,
    next: middleware::Next,
    kafka_logger: Arc<KafkaLogger>,
) -> Response {
    let start = Instant::now();
    let method = req.method().to_string();
    let path = req.uri().path().to_string();
    let query = req.uri().query().map(|q| q.to_string());
    let request_id = Uuid::new_v4().to_string();

    let response = next.run (req).await;
    
    let duration = start.elapsed().as_millis() as u64;

    let status_code = response.status().as_u16();

    let log = RestLog {
        request_id,
        service: "RUST_PEOPLE_API".to_string(),
        endpoint: path,
        method,
        status_code,
        duration_ms: duration,
        timestamp: Utc::now(),
        query,
    };

    tokio::spawn(async move {
        kafka_logger.log_request(log).await;
    });

    response
}
