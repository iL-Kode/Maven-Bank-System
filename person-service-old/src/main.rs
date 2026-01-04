use httprouter::{Router, Params};
use hyper::{Request, Response, Body, Error};
use serde_json::Value;

const PERSONS: &str = include_str!("data/persons.json");

async fn list_persons(_: Request<Body>) -> Result<Response<Body>, Error> {
    Ok(Response::new(PERSONS.into()))
}

async fn find_person_by_key(req: Request<Body>) -> Result<Response<Body>, Error> {
    let params = req.extensions().get::<Params>().unwrap();
    let key = params.get("key").unwrap();

    let persons: Value = serde_json::from_str(PERSONS).unwrap();

    if let Some(arr) = persons.as_array() {
        if let Some(person) = arr.iter().find(|p| p["key"] == *key) {
            return Ok(Response::new(
                serde_json::to_string(person).unwrap().into(),
            ));
        }
    }

    Ok(Response::new("null".into()))
}


async fn find_person_by_name(req: Request<Body>) -> Result<Response<Body>, Error> {
    let params = req.extensions().get::<Params>().unwrap();
    let name = params.get("name").unwrap();

    let persons: Value = serde_json::from_str(PERSONS).unwrap();

    if let Some(arr) = persons.as_array() {
        let matches: Vec<&Value> = arr
            .iter()
            .filter(|p| p["name"] == *name)
            .collect();

        return Ok(Response::new(
            serde_json::to_string(&matches).unwrap().into(),
        ));
    }

    Ok(Response::new("[]".into()))
}

#[tokio::main]
async fn main() {
    let router = Router::default()
        .get("/person/", list_persons)
        .get("/person/find.key/:key", find_person_by_key)
        .get("/person/find.name/:name", find_person_by_name);

    hyper::Server::bind(&([127, 0, 0, 1], 8060).into())
        .serve(router.into_service())
        .await;
}