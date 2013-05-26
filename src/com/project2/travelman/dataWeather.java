package com.project2.travelman;

import java.util.ArrayList;

public class dataWeather implements Cloneable {

	String name = new String();
	String a1 = new String();
	String a2 = new String();
	String a3 = new String();
	String a4 = new String();
	String a5 = new String();
	String a6 = new String();
	String a7 = new String();

	String image1 = new String();
	String image2 = new String();
	String image3 = new String();
	String image4 = new String();
	String image5 = new String();
	String image6 = new String();
	String image7 = new String();

	dataWeather() {

	}

	public void nameSet(String value) {
		this.name = value;
	}

	public String nameGet() {
		return this.name;
	}

	public void a1Set(String value) {
		this.a1 = value;
	}

	public void a2Set(String value) {
		this.a2 = value;
	}

	public void a3Set(String value) {
		this.a3 = value;
	}

	public void a4Set(String value) {
		this.a4 = value;
	}

	public void a5Set(String value) {
		this.a5 = value;
	}

	public void a6Set(String value) {
		this.a6 = value;
	}

	public void a7Set(String value) {
		this.a7 = value;
	}

	public String a1Get() {
		return this.a1;
	}

	public String a2Get() {
		return this.a2;
	}

	public String a3Get() {
		return this.a3;
	}

	public String a4Get() {
		return this.a4;
	}

	public String a5Get() {
		return this.a5;
	}

	public String a6Get() {
		return this.a6;
	}

	public String a7Get() {
		return this.a7;
	}

	public void v1Set(String value) {
		this.image1 = value;
	}

	public void v2Set(String value) {
		this.image2 = value;
	}

	public void v3Set(String value) {
		this.image3 = value;
	}

	public void v4Set(String value) {
		this.image4 = value;
	}

	public void v5Set(String value) {
		this.image5 = value;
	}

	public void v6Set(String value) {
		this.image6 = value;
	}

	public void v7Set(String value) {
		this.image7 = value;
	}

	public String v1Get() {
		return this.image1;
	}

	public String v2Get() {
		return this.image2;
	}

	public String v3Get() {
		return this.image3;
	}

	public String v4Get() {
		return this.image4;
	}

	public String v5Get() {
		return this.image5;
	}

	public String v6Get() {
		return this.image6;
	}

	public String v7Get() {
		return this.image7;
	}

	public void splitDay(String temp, dataWeather lists) {
		int a = 7;

		String temp1 = temp.substring(0, a);
		String temp2 = temp.substring(a + 1, a * 2 + 1);
		String temp3 = temp.substring(a * 2 + 2, a * 3 + 2);
		String temp4 = temp.substring(a * 3 + 3, a * 4 + 3);
		String temp5 = temp.substring(a * 4 + 4, a * 5 + 4);
		String temp6 = temp.substring(a * 5 + 5, a * 6 + 5);
		String temp7 = temp.substring(a * 6 + 6, a * 7 + 6);

		// System.out.println(temp1);
		// System.out.println(temp2);
		// System.out.println(temp3);
		// System.out.println(temp4);
		// System.out.println(temp5);
		// System.out.println(temp6);
		// System.out.println(temp7);

		this.a1Set(temp1);
		this.a2Set(temp2);
		this.a3Set(temp3);
		this.a4Set(temp4);
		this.a5Set(temp5);
		this.a6Set(temp6);
		this.a7Set(temp7);

	}

	public void weatherDayNight(ArrayList<String> temp) {

		this.v1Set(temp.get(0));
		this.v2Set(temp.get(1));
		this.v3Set(temp.get(2));
		this.v4Set(temp.get(3));
		this.v5Set(temp.get(4));
		this.v6Set(temp.get(5));
		this.v7Set(temp.get(6));

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
