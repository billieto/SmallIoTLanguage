# Small IoT Language (SIL)

Small IoT Language (SIL) is a minimal scripting language that lets you control a Raspberry Pi via an interpreter. It supports integers, floats, strings, basic if statements, reading values from the Pi, comments, and some necessary declarations.

A sample program demonstrating each feature is available at `input.sil`.

## Features

- **Device Declaration**: save an IP address to a variable.  
- **Variable Declaration**: save any arithmetic value to a variable.  
- **If Statements**: basic conditional statements.  
- **Ask "Function"**: query a device for sensor values. Currently supports temperature, pressure, and humidity.  
- **Turn**: turn a device’s LED on or off on a specified GPIO pin.  
- **Print**: print a message to the device’s standard output.  

The language communicates with the Raspberry Pi via HTTP. The Raspberry Pi runs a small HTTP server implemented in Flask Python, which provides easy access to GPIO control and sensor readings.

## How to Run

### For the interpreter side:
```bash
cd smallIotLanguage/grammar/
antlr4 -no-listener -visitor -o ../src Sil.g4
```
### For the Raspberry Pi side:
```bash
cd raspberryPiSide/
python3 -m venv venv
source venv/bin/activate
pip install -r requirements.txt
sudo python3 server.py
```

## Notes
- You have to see the ip of your http server running on your Raspberry Pi and hardcode it on `smallIotLanguage/ipnut.sil`.
- This project was developed on Debian. ANTLR4 runtime version 4.7.2 is used; compatibility with newer versions is untested.
