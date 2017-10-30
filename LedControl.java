
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

import com.pubnub.api.PNConfiguration;
import com.pubnub.api.PubNub;
import com.pubnub.api.callbacks.SubscribeCallback;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.pubsub.PNMessageResult;
import com.pubnub.api.models.consumer.pubsub.PNPresenceEventResult;
import java.util.Arrays;
public class LedControl {

	private static GpioPinDigitalOutput pin1, pin2, pin3, pin4;
	private static GpioPinDigitalOutput[] pins;
	private static String[] pinStates;
	
    public static void main(String[] args) throws InterruptedException {
        initPubnub();
    	final GpioController gpio = GpioFactory.getInstance();
        
	pin1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_24, "PinLED");
	pin2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25, "PinLED");
	pin3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27, "PinLED");
	pin4 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_28, "PinLED");

	pins = new GpioPinDigitalOutput[] {pin1, pin2, pin3, pin4};

	pinStates = new String[] {"off:-1", "fallingBlink:500000000/0", "on:-1", "off:-1"};

       for(GpioPinDigitalOutput pin : pins) {
	pin.low();
       }

       Thread.sleep(1500);
       for(GpioPinDigitalOutput pin : pins) {
	pin.high();
       }
       Thread.sleep(500);
       for(GpioPinDigitalOutput pin : pins) {
	pin.low();
       }
       long lastTime = System.nanoTime();
       long startTime = System.nanoTime();
   	while(true) {
		startTime = System.nanoTime();
		long deltaTime = startTime - lastTime;

		for(int i = 0; i < pinStates.length; i++) {
			String state = pinStates[i].split(":")[0];
			String value = pinStates[i].split(":")[1];
			if(state.equals("on")) {
				pins[i].high();
			} else if(state.equals("off")) {
				pins[i].low();
			} else if(state.equals("risingBlink")) {
				long maxTime = Long.valueOf(value.split("/")[0]);
				long currentTime = Long.valueOf(value.split("/")[1]);
				if(currentTime > maxTime) {
					pins[i].high();
					pinStates[i] = "fallingBlink:" + maxTime + "/0";
				} else {
					pinStates[i] = state + ":" + maxTime + "/" + (currentTime + deltaTime);
				}
			} else if(state.equals("fallingBlink")) {
				long maxTime = Long.valueOf(value.split("/")[0]);
				long currentTime = Long.valueOf(value.split("/")[1]);
				if(currentTime > maxTime) {
					pins[i].low();
					pinStates[i] = "risingBlink:" + maxTime + "/0";
				} else {
					pinStates[i] = state + ":" + maxTime + "/" + (currentTime + deltaTime);
				}
			} else  {
				System.out.println("Invalid State" + state);
			}



		}

		lastTime = startTime;	
	}	
    }
    public static GpioPinDigitalOutput stringToPin(String s) {
	if(s.contains("pin1")) {
		return pin1;
	} else if(s.contains("pin2")) {
		return pin2;
	} else if(s.contains("pin3")) {
		return pin3;
	} else if(s.contains("pin4")) {
		return pin4;
	} else {		
		System.out.println("Error, invald string : [" + s + "], using pin1");
		return pin1;
	}
    }

    public static int stringToInt(String s) {
	if(s.contains("pin1")) {
		return 1;
	} else if(s.contains("pin2")) {
		return 2;
	} else if(s.contains("pin3")) {
		return 3;
	} else if(s.contains("pin4")) {
		return 4;
	} else {
		System.out.println("Error, invald string : [" + s + "], using pin1");
		return 1;
	}
    }


    public static void initPubnub() {
	PNConfiguration pnConfiguration = new PNConfiguration();
	pnConfiguration.setSubscribeKey("sub-c-0f7ca070-bc72-11e7-acda-62870583ed84");
	pnConfiguration.setPublishKey("pub-c-33bdfa69-b688-4d4c-9933-1f4c345d816e");
	pnConfiguration.setSecure(false);	         
	PubNub pubnub = new PubNub(pnConfiguration);
	pubnub.addListener(new SubscribeCallback() {

	@Override
	public void status(PubNub pubnub, PNStatus status) {

	}

	@Override
	public void message(PubNub pubnub, PNMessageResult message) {
		String msg = message.getMessage().toString();
		System.out.println("Received message " + message);
		String[] json = msg.split(",");
		int counter = 0;
		for(String entry:json) {
			String key = entry.split(":")[0];
			String value = entry.split(":")[1];
			int pin = stringToInt(value);
			if(value.contains("on")) {
				System.out.println("turning pin on");
				pinStates[pin] = "on:-1";
			} else if(value.contains("off")) {
				System.out.println("turning pin off");
				pinStates[pin] = "off:-1";
			} else {
				System.out.println("Invalid value: [" + value + "]");
			}
			counter++;
		}
	}

	@Override
	public void presence(PubNub pubnub, PNPresenceEventResult presence) {
			
	}});
	pubnub.subscribe().channels(Arrays.asList("gavino")).execute();
	

    }
}
