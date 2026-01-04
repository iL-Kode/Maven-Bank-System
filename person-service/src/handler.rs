use axum::{Json, extract::Query};
use serde::Deserialize;
use serde_json::{Value, json};

const PERSONS_JSON: &str = include_str!("data/persons.json");

pub async fn list_all() -> Json<Value> {
    let json_value: Value = serde_json::from_str(PERSONS_JSON)
        .expect("persons.json should be valid JSON");
    Json(json_value)
}

#[derive(Deserialize)]
pub struct NamedQuery {
    name: String,
}

pub async fn find_name(Query(params): Query<NamedQuery>) -> Json<Value> {
    let json_value: Value = serde_json::from_str(PERSONS_JSON)
        .expect("persons.json should be valid JSON");

    let mut array: Vec<Value> = Vec::new();

    if let Some(persons_array) = json_value.as_array() {
        for person in persons_array {
            if let Some(person_name) = person.get("name").and_then(|n| n.as_str()) { 
                if person_name == params.name {
                    array.push(person.clone());
                }
            }
        }
    }
    
    if array.is_empty() {
        Json(json!(null))
    } else {
        Json(json!(array))
    }

}


#[derive(Deserialize)]
pub struct KeyQuery {
    key: String,
}

pub async fn find_key(Query(params): Query<KeyQuery>) -> Json<Value> {
    let json_value: Value = serde_json::from_str(PERSONS_JSON)
        .expect("persons.json should be valid JSON");


    if let Some(person_array) = json_value.as_array() {
        for person in person_array {
            if let Some(person_key) = person.get("key").and_then(|n| n.as_str()) { 
                if person_key == params.key{
                    return Json(person.clone()); 
                }
            }
        }
    }
    
    Json(json!(null))
}
