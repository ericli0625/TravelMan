package com.project2.travelman;

public class Traveler {
	private String _id;
	private String category;
	private String name;
	private String cities;
	private String city;
	private String address;
	private String telephone;

	private String content;
	
	void setId(String id_) {
		_id = id_; 
	}

	void setCategory(String category_) {
		category = new String(category_);
	}

	void setName(String name_) {
		name = new String(name_);
	}

	void setCities(String cities_) {
		cities = new String(cities_);
	}

	void setCity(String city_) {
		city = new String(city_);
	}

	void setAddress(String address_) {
		address = new String(address_);
	}
	
	void setTelephone(String telephone_) {
		telephone = new String(telephone_);
	}

	void setContent(String content_) {
		content = new String(content_);
	}
	

	String getId() {
		return _id; 
	}

	String getCategory() {
		return category ;
	}

	String getName() {
		return name;
	}

	String getCities() {
		return cities;
	}

	String getCity() {
		return city;
	}

	String getAddress() {
		return address;
	}
	
	String getTelephone() {
		return telephone;
	}
	

	String getContent() {
		return content;
	}
	
}
