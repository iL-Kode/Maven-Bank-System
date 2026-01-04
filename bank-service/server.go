package main

import (
	"encoding/json"
	"fmt"
	"net/http"
	"os"
	"strconv"

	//kafka
	"context"
	"time"
	"github.com/segmentio/kafka-go"
)

type Bank struct {
	Key  int    `json:"key"`
	Name string `json:"name"`
}

type RestLog struct {
	RequestID string `json:"requestId"`
	Service string `json:"service"`
	Endpoint string `json:"endpoint"`
	Method string `json:"method"`
	Duration int64 `json:"duration"`
	StatusCode int `json:"statusCode"`
	Timestamp time.Time `json:"timestamp"`
	Query string `json:"query,omitempty"`
}

type LogProducer struct {
	writer *kafka.Writer
}

var banks []Bank
var logProducer *LogProducer


func NewLogProducer(brokers []string) *LogProducer {
	return &LogProducer{
		writer: &kafka.Writer{
			Addr:		kafka.TCP(brokers...),
			Topic:		"rest-requests-log",
			Balancer:	&kafka.LeastBytes{},
		},
	}
}

func (p *LogProducer) LogRequest(log RestLog) {
	logBytes, err := json.Marshal(log)

	if err!=nil {
		fmt.Printf("Error marshal\n", err)
		return
	}

	err = p.writer.WriteMessages(context.Background(),
		kafka.Message{
			Key: []byte(log.RequestID),
			Value: logBytes,
		},
	)	

	if err!=nil {
		fmt.Printf("Error writing to kafka", err)
	}
}

func (p *LogProducer) Close() error {
	return p.writer.Close()
}




func loggingMiddleware(next http.HandlerFunc) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		start := time.Now()

		requestID := fmt.Sprintf("req-%d", time.Now().UnixNano())

		rw := &responseWriter{ResponseWriter: w, statusCode: http.StatusOK}

		next(rw, r)

		log := RestLog{
			RequestID: requestID,
			Service: "GO_Bank_API",
			Endpoint: r.URL.Path,
			Method: r.Method,
			StatusCode: rw.statusCode,
			Duration: time.Since(start).Milliseconds(),
			Timestamp: time.Now(),
			Query: r.URL.RawQuery,
		}

		go logProducer.LogRequest(log)
	}
}

type responseWriter struct {
	http.ResponseWriter
	statusCode int
}


func (rw *responseWriter) WriteHeader(code int) {
	rw.statusCode = code
	rw.ResponseWriter.WriteHeader(code)
}




func listBanks(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(banks)

}

func getBankByName(w http.ResponseWriter, r *http.Request) {
	name := r.URL.Query().Get("name")

	for _, bank := range banks {
		if bank.Name == name {
			w.Header().Set("Content-Type", "application/json")
			json.NewEncoder(w).Encode(bank)
			return
		}
	}
	
	w.WriteHeader(http.StatusNotFound)
	json.NewEncoder(w).Encode(nil)
}

func getBankByKey(w http.ResponseWriter, r *http.Request) {
	key, err := strconv.Atoi(r.URL.Query().Get("key"))

	if err != nil {
		w.WriteHeader(http.StatusBadRequest)
		json.NewEncoder(w).Encode(map[string]string{"error": "invalid key"})
		return
		//panic(err)
	}

	for _, bank := range banks {
		if bank.Key == key {
			w.Header().Set("Content-Type", "application/json")
			json.NewEncoder(w).Encode(bank)
			return
		}
	}
	
	w.WriteHeader(http.StatusNotFound)
	json.NewEncoder(w).Encode(nil)
}

func main() {
	load_bank_data()


	//kafka
	kafkaBrokers := []string{"localhost:8090"}
	logProducer = NewLogProducer(kafkaBrokers)
	defer logProducer.Close()





	mux := http.NewServeMux()

	mux.HandleFunc("/bank/", loggingMiddleware(listBanks))
	mux.HandleFunc("/bank/find.name", loggingMiddleware(getBankByName))
	mux.HandleFunc("/bank/find.key", loggingMiddleware(getBankByKey))

	fmt.Println("Server running on :8070")
	http.ListenAndServe(":8070", mux)
}

func load_bank_data() {
	buff, err := os.ReadFile("data/banks.json")
	if err != nil {
		fmt.Errorf("could not read banks.json")
		return
	}

	err = json.Unmarshal(buff, &banks)
	// json.NewEncoder(os.Stdout).Encode(banks)
}
