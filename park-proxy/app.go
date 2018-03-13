package main

import (
	"github.com/gorilla/mux"
	"github.com/urfave/negroni"
	"github.com/wunderlist/moxy"
	"os"
)

func main() {

	var filters []moxy.FilterFunc

	parkToolHosts := []string{os.Getenv("PARK_TOOL_HOST")}
	toolProxy := moxy.NewReverseProxy(parkToolHosts, filters)

	parkCalculatorHosts := []string{os.Getenv("PARK_CALCULATOR_HOST")}
	calcProxy := moxy.NewReverseProxy(parkCalculatorHosts, filters)

	router := mux.NewRouter()

	//Parking Meter routes
	router.HandleFunc("/parking-meters/ping", toolProxy.ServeHTTP).Methods("GET")
	router.HandleFunc("/parking-meters/start", toolProxy.ServeHTTP).Methods("POST")
	router.HandleFunc("/parking-meters/stop", toolProxy.ServeHTTP).Methods("POST")

	//Parking Calculator routes
	router.HandleFunc("/parking-fees/{vehicleId}", calcProxy.ServeHTTP).Methods("GET")

	app := negroni.New()
	app.UseHandler(router)
	app.Run(":3000")
}
