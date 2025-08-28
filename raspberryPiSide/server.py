from flask import Flask, request
from gpiozero import LED
from sense_emu import SenseHat

app = Flask(__name__)
sense = SenseHat()

@app.route("/turn")
def turn():
	action = request.args.get("action")
	pin = int(request.args.get("pin"))
	
	led = LED(pin)
	
	if action == "on":
		print("Turning device ON")
		led.on()
		
	elif action == "off":
		print("Turning device OFF")
		led.off()

	return "OK", 200

@app.route("/print")
def message():
	msg = request.args.get("msg")
	
	if msg:
		print(f"Got a message!\nPrinting: {msg}")
		
	return "OK", 200

@app.route("/ask")
def ask():
	value = request.args.get("value")
	
	if value == "temperature":
		num = sense.temp
	elif value == "pressure":
		num = sense.pressure
	elif value == "humidity":
		num = sense.humidity
	
	return str(num)

if __name__ == "__main__":
	app.run(host="0.0.0.0", port=80)
