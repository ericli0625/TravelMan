package com.project2.travelman;

public class doubleWeather implements Cloneable {

	dataWeather data1 = new dataWeather();
	dataWeather data2 = new dataWeather();

	dataWeather weather1 = new dataWeather();
	dataWeather weather2 = new dataWeather();
	
	doubleWeather() {

	}

	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			System.out.println(e);
			return null;
		}
	}

}
